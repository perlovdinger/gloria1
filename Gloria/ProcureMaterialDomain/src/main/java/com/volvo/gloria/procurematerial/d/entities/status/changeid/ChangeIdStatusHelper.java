package com.volvo.gloria.procurematerial.d.entities.status.changeid;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringUtils;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.materialRequestProxy.b.MaterialProcureResponse;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.c.dto.MaterialDTO;
import com.volvo.gloria.procurematerial.d.entities.ChangeId;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeaderVersion;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.status.requestline.MaterialStatus;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.procurematerial.d.type.request.RequestType;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.GloriaSystemException;

/**
 * This class is used for method implementations that are common for several statuses.
 */
public final class ChangeIdStatusHelper {

    private ChangeIdStatusHelper() {
    }
    
    public static void inactivateHeader(ChangeId changeId) {
        List<MaterialHeaderVersion> materialHeaderVersions = changeId.getMaterialHeaderVersions();
        if (materialHeaderVersions != null && !materialHeaderVersions.isEmpty()) {
            for (MaterialHeaderVersion materialHeaderVersion : materialHeaderVersions) {
                MaterialHeader materialHeader = materialHeaderVersion.getMaterialHeader();
                if (hasAllMaterialsRemoved(materialHeader.getMaterials())) {
                    materialHeader.setActive(false);
                }
            }
        }
    }

    private static boolean hasAllMaterialsRemoved(List<Material> materials) {
        for (Material material : materials) {
            if (material.getStatus() != MaterialStatus.REMOVED) {
                return false;
            }
        }
        return true;
    }
    
    public static void sendResponse(String response, MaterialProcureResponse materialProcureResponse, ChangeId changeId, String userId) {
        try {
            materialProcureResponse.sendMaterialProcureResponse(changeId.getMtrlRequestVersion(), userId, response, changeId.getChangeTechId(),
                                                                changeId.getMaterialHeaderVersions().get(0).getMaterialHeader().getMtrlRequestId());
        } catch (JAXBException e) {
            throw new GloriaSystemException(e, "Material Procure Response couldn't be sent.");
        }
    }

    public static void cancel(ChangeId changeId, MaterialHeaderRepository requestHeaderRepository) throws GloriaApplicationException {
        changeId.setStatus(ChangeIdStatus.CANCELLED);
        requestHeaderRepository.save(changeId);
    }

    public static void removeAllNeed(ChangeId changeId, MaterialHeaderRepository requestHeaderRepository, 
            TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        List<MaterialHeaderVersion> requestHeaderVersions = changeId.getMaterialHeaderVersions();
        for (MaterialHeaderVersion aMaterialHeaderVersion : requestHeaderVersions) {
            MaterialHeader parentMaterialHeader = aMaterialHeaderVersion.getMaterialHeader();
            List<MaterialHeader> materialHeaders = null;
            if (parentMaterialHeader.getRequestType().isProtomRequest()) {
                materialHeaders = requestHeaderRepository.findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId(null,
                                                                                                                         parentMaterialHeader.getReferenceId(),
                                                                                                                         null, "", null);
            } else if (parentMaterialHeader.getRequestType() == RequestType.SINGLE) {
                materialHeaders = requestHeaderRepository.findMaterialHeaderByMtrlRequestId(parentMaterialHeader.getMtrlRequestId(), null);
            } else {
                materialHeaders = requestHeaderRepository.findMaterialHeaderByMtrlRequestId(parentMaterialHeader.getMtrlRequestId(),
                                                                                            parentMaterialHeader.getReferenceId());
            }
            if (materialHeaders != null) {
                for (MaterialHeader materialHeader : materialHeaders) {
                    for (Material aMaterial : materialHeader.getMaterials()) {
                        if (aMaterial.getAdd().getChangeTechId() == changeId.getChangeTechId()) {
                            aMaterial.getStatus().needIsRemoved(changeId, aMaterial, requestHeaderRepository, traceabilityRepository);
                        }
                    }                    
                }
            }
            
            if (hasAllMaterialsRemoved(parentMaterialHeader.getMaterials())) {
                parentMaterialHeader.setActive(false);
            }
        }
    }

    public static void setChangeIdStatus(ChangeId changeId, MaterialHeaderRepository requestHeaderRepo, MaterialProcureResponse materialProcureResponse,
            String userId, ChangeIdStatus changeIdStatus) {
        if (changeId.getRequestType() != null && (changeId.getRequestType() == RequestType.SINGLE 
                || changeId.getRequestType() == RequestType.MULTIPLE || changeId.getRequestType() == RequestType.FOR_STOCK)) {
            
            sendResponse(changeIdStatus.name(), materialProcureResponse, changeId, userId);
        }
        changeId.setStatus(changeIdStatus);
        requestHeaderRepo.save(changeId);
    }

    public static void acceptMaterials(MaterialHeaderRepository requestHeaderRepo, ChangeId changeId, ProcurementServices procurementServices,
            ProcureLineRepository procureLineRepository, List<MaterialDTO> materialDTOs, 
            OrderRepository orderRepository, UserServices userServices,
            String userId, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        for (Material addMaterial : changeId.getAddMaterials()) {
            addMaterial.getStatus().accept(requestHeaderRepo, addMaterial, changeId, procurementServices, procureLineRepository, null, orderRepository,
                                           userServices, userId, traceabilityRepository);
        }
        
        List<Material> removeMaterials = changeId.getRemoveMaterials();
        sortMaterialsByMaterialType(removeMaterials);
        // @Comment: sorting in ascending order of material type, the usage materials will be removed and later on all the usageReplaced materials has to be
        // removed at once
        for (Material removeMaterial : removeMaterials) {
            MaterialHeader materialHeader = removeMaterial.getMaterialHeader();
            if (materialHeader != null) {
                MaterialHeaderVersion materialHeaderVersion = materialHeader.getAccepted();
                if (materialHeaderVersion != null) {
                    ChangeId previousChangeId = materialHeaderVersion.getPreviousChangeId();
                    if (previousChangeId != null) {
                        materialHeaderVersion.setChangeId(previousChangeId);
                    }
                }
            }
            boolean usageReplacedMaterial = removeMaterial.getMaterialType().equals(MaterialType.USAGE_REPLACED);
            removeMaterial.getStatus().accept(requestHeaderRepo, removeMaterial, changeId, procurementServices, procureLineRepository, materialDTOs,
                                              orderRepository, userServices, userId, traceabilityRepository);
            if (usageReplacedMaterial) {
                break;
            }
        }
    }
    
    private static void sortMaterialsByMaterialType(List<Material> materialList) {
        Collections.sort(materialList, new Comparator<Material>() {
            public int compare(Material m1, Material m2) {
                if (m1.getMaterialType().ordinal() < (m2.getMaterialType().ordinal())) {
                    return -1;
                }
                return 1;
            }
        });
    }

    public static void rejectMaterials(ChangeId changeId, MaterialHeaderRepository requestHeaderRepo, TraceabilityRepository traceabilityRepository,
            UserDTO userDTO) throws GloriaApplicationException {
        for (Material addMaterial : changeId.getAddMaterials()) {
            addMaterial.getStatus().reject(addMaterial, requestHeaderRepo, traceabilityRepository, userDTO);
        }

        for (Material removeMaterial : changeId.getRemoveMaterials()) {
            removeMaterial.getStatus().reject(removeMaterial, requestHeaderRepo, traceabilityRepository, userDTO);
        }
    }

    public static void cancelMaterials(ChangeId changeId, MaterialHeaderRepository requestHeaderRepo, ProcureLineRepository procureLineRepository, TraceabilityRepository traceabilityRepository)
            throws GloriaApplicationException {
        for (Material addMaterial : changeId.getAddMaterials()) {
            addMaterial.getStatus().remove(changeId, StringUtils.isNotEmpty(addMaterial.getMaterialHeader().getMaterialControllerUserId()), addMaterial,
                                           procureLineRepository, traceabilityRepository);
        }
    }
    
    public static boolean hasChangeIdMaterialHeadersAssignedToMaterialController(ChangeId changeId) {
        for (MaterialHeaderVersion materialHeaderVersion : changeId.getMaterialHeaderVersions()) {
            if (StringUtils.isNotEmpty(materialHeaderVersion.getMaterialHeader().getMaterialControllerUserId())) {
                return true;
            }
        }
        return false;
    }
    
    public static void updateNeedIsChanged(ChangeId changeId, ProcureLineRepository procureLineRepository, boolean needIsChanged) {
        List<Material> addMaterials = changeId.getAddMaterials();
        List<Material> removeMaterials = changeId.getRemoveMaterials();
        if (addMaterials != null && !addMaterials.isEmpty()) {
            for (Material addMaterial : addMaterials) {
                ProcureLine procureLine = addMaterial.getProcureLine();
                if (procureLine != null) {
                    procureLine.setNeedIsChanged(needIsChanged);
                    procureLineRepository.save(procureLine);
                }
            }
        }

        if (removeMaterials != null && !removeMaterials.isEmpty()) {
            for (Material removeMaterial : removeMaterials) {
                ProcureLine procureLine = removeMaterial.getProcureLine();
                if (procureLine != null) {
                    procureLine.setNeedIsChanged(needIsChanged);
                    procureLineRepository.save(procureLine);
                }
            }
        }
    }
    
    public static void updateChangeRequestIds(ChangeId changeId, List<Material> materials) {
        MaterialHeader materialHeader = changeId.getMaterialHeaderVersions().get(0).getMaterialHeader();
        ChangeId previousChangeId = null;
        if (materialHeader.getAccepted() != null) {
            previousChangeId = materialHeader.getAccepted().getChangeId();
        }
        for (Material material : materials) {
            ProcureLine procureLine = material.getProcureLine();
            String changeRequestIds = procureLine.getChangeRequestIds();
            if (StringUtils.isNotBlank(changeRequestIds)) {
                procureLine.setChangeRequestIds(StringUtils.replace(changeRequestIds, previousChangeId.getMtrlRequestVersion(),
                                                                    changeId.getMtrlRequestVersion()));
            } else {
                procureLine.setChangeRequestIds(changeId.getMtrlRequestVersion());
            }
        }
    }
    
    public static void updateVersionAsAccepted(ChangeId changeId, MaterialHeaderRepository requestHeaderRepo) {
        List<MaterialHeaderVersion> materialHeaderVersions = changeId.getMaterialHeaderVersions();
        if (materialHeaderVersions != null && !materialHeaderVersions.isEmpty()) {
            for (MaterialHeaderVersion materialHeaderVersion : materialHeaderVersions) {
                MaterialHeader materialHeader = materialHeaderVersion.getMaterialHeader();
                MaterialHeaderVersion previousAcceptedVersion = materialHeader.getAccepted();
                if (previousAcceptedVersion != null) {
                    materialHeaderVersion.setPreviousAcceptedVersion(previousAcceptedVersion);
                }
                materialHeader.setAccepted(materialHeaderVersion);
                requestHeaderRepo.save(materialHeader);
            }
        }
    }

    public static void updateWithMtrlAcceptedVersion(ChangeId changeId) {
        for (Material addMaterial : changeId.getAddMaterials()) {
            addMaterial.setMtrlRequestVersionAccepted(changeId.getMtrlRequestVersion());
        }

        for (Material removeMaterial : changeId.getRemoveMaterials()) {
            removeMaterial.setMtrlRequestVersionAccepted(changeId.getMtrlRequestVersion());
        }
    }
}
