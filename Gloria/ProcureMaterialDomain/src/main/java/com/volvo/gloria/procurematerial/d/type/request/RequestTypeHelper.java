package com.volvo.gloria.procurematerial.d.type.request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.TransformerUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.authorization.c.TeamType;
import com.volvo.gloria.authorization.d.entities.Team;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.CompanyCodeRepository;
import com.volvo.gloria.common.repositories.b.TeamRepository;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.b.beans.ProcurementServicesHelper;
import com.volvo.gloria.procurematerial.c.BuildType;
import com.volvo.gloria.procurematerial.d.entities.ChangeId;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeaderVersion;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.status.requestline.MaterialStatus;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurerequest.c.dto.RequestHeaderTransformerDTO;
import com.volvo.gloria.procurerequest.c.dto.RequestHeaderVersionTransformerDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.GloriaFormateUtil;
import com.volvo.gloria.util.c.UniqueItems;

/**
 * Helper class for RequestType.
 * 
 */
public final class RequestTypeHelper {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestTypeHelper.class);
    
    private RequestTypeHelper() {
    }

    public static void moveMaterialsToCurrentHeader(MaterialHeader materialHeader, String crId, MaterialHeaderRepository requestHeaderRepository,
            RequestHeaderTransformerDTO requestHeaderTransformerDTO, ProcurementServices procurementServices) throws GloriaApplicationException {
        List<ChangeId> changeIds = requestHeaderRepository.findChangeIdByCrId(crId);
        String materialControllerUserId = materialHeader.getMaterialControllerUserId();
        List<Material> unassignedSourceMaterials = new ArrayList<Material>();
        Set<Long> unassignedSourceMaterialHeaderOids = new HashSet<Long>();
        if (changeIds != null && !changeIds.isEmpty()) {
            for (ChangeId aChangeId : changeIds) {
                moveMaterials(materialHeader, aChangeId.getAddMaterials(), requestHeaderTransformerDTO, crId, unassignedSourceMaterials,
                              unassignedSourceMaterialHeaderOids);
                moveMaterials(materialHeader, aChangeId.getRemoveMaterials(), requestHeaderTransformerDTO, crId, unassignedSourceMaterials,
                              unassignedSourceMaterialHeaderOids);
            }
        }
        
        if (!StringUtils.isEmpty(materialControllerUserId)) {
            procurementServices.createProcureLine(materialControllerUserId, materialControllerUserId, "", unassignedSourceMaterials);
        }
    }

    private static List<Material> moveMaterials(MaterialHeader materialHeader, List<Material> materials,
            RequestHeaderTransformerDTO requestHeaderTransformerDTO, String crId, List<Material> unassignedSourceMaterials,
            Set<Long> unassignedSourceMaterialOids) {
        String buildRemoveId = requestHeaderTransformerDTO.getBuildRemoveId();
        String removeReferenceId = requestHeaderTransformerDTO.getRemoveReferenceId();
        String replacementReference = requestHeaderTransformerDTO.getReferenceId();
        List<RequestHeaderVersionTransformerDTO> requestHeaderVersionTransformerDtos = requestHeaderTransformerDTO.getRequestHeaderVersionTransformerDtos();
        for (Material material : materials) {
            MaterialHeader sourceMaterialHeader = material.getMaterialHeader();
            String sourceBuildId = sourceMaterialHeader.getBuildId();
            String sourceReferenceID = sourceMaterialHeader.getReferenceId();
            String referenceID = materialHeader.getReferenceId();
            for (MaterialHeaderVersion sourceMaterialHeaderVersion : sourceMaterialHeader.getMaterialHeaderVersions()) {
                if (requestHeaderVersionTransformerDtos != null && !requestHeaderVersionTransformerDtos.isEmpty()) {
                    Material movedMaterial = verifyAndMoveMaterial(materialHeader, material, buildRemoveId, removeReferenceId, sourceMaterialHeader,
                                                                   sourceBuildId, sourceReferenceID, referenceID, requestHeaderVersionTransformerDtos,
                                                                   replacementReference, sourceMaterialHeaderVersion, crId);
                    if (movedMaterial != null) {
                        long sourceMaterialOid = movedMaterial.getMaterialOID();
                        if (StringUtils.isEmpty(sourceMaterialHeader.getMaterialControllerUserId()) && sourceMaterialOid > 0l) {
                            if (!unassignedSourceMaterialOids.contains(sourceMaterialOid)) {
                                unassignedSourceMaterialOids.add(sourceMaterialOid);
                                unassignedSourceMaterials.add(movedMaterial);
                            }
                        }
                    }
                }
            }
        }
        return new ArrayList<Material>(unassignedSourceMaterials);
    }

    private static void setAcceptedVersion(String removeReferenceId, MaterialHeader sourceMaterialHeader, MaterialHeaderVersion sourceMaterialHeaderVersion,
            String outBoundLocationId, String crId) {
        if (isSwap(removeReferenceId) && sourceMaterialHeaderVersion != null) {
            String sourceCrId = sourceMaterialHeaderVersion.getChangeId().getCrId();

            String outboundLocationId = sourceMaterialHeaderVersion.getOutboundLocationId();
            if (outBoundLocationId.equalsIgnoreCase(outboundLocationId) && sourceCrId.equalsIgnoreCase(crId)) {
                MaterialHeaderVersion previousAcceptedVersion = sourceMaterialHeaderVersion.getPreviousAcceptedVersion();
                if (previousAcceptedVersion != null) {
                    sourceMaterialHeader.setAccepted(previousAcceptedVersion);
                    sourceMaterialHeaderVersion.setSwapped(true);
                }
            }
        }
    }

    private static Material verifyAndMoveMaterial(MaterialHeader materialHeader, Material material, String buildRemoveId, String removeReferenceId,
            MaterialHeader sourceMaterialHeader, String sourceBuildId, String sourceReferenceID, String referenceID,
            List<RequestHeaderVersionTransformerDTO> requestHeaderVersionTransformerDtos, String replacementReference,
            MaterialHeaderVersion sourceMaterialHeaderVersion, String crId) {
        String sourceCrId = sourceMaterialHeaderVersion.getChangeId().getCrId();
        String sourceOutboundLocationId = sourceMaterialHeaderVersion.getOutboundLocationId();
                
        for (RequestHeaderVersionTransformerDTO requestHeaderVersionTransformerDto : requestHeaderVersionTransformerDtos) {
            String outboundLocationId = requestHeaderVersionTransformerDto.getOutboundLocationId();
            if (!isSwap(removeReferenceId)) {
                if (notMatchesReferenceId(sourceReferenceID, referenceID)) {
                    continue;
                }
            }
            
            if (isSwap(removeReferenceId)) {
                if (notMatchesReferenceId(sourceReferenceID, removeReferenceId)) {
                    continue;
                }
            }
            if (notMatchesBuildId(buildRemoveId, sourceBuildId)) {
                continue;
            }
            if (notMatchesOutboundLocationId(sourceOutboundLocationId, outboundLocationId)) {
                continue;
            }
            if (notMatchesCrId(sourceCrId, crId)) {
                continue;
            }
            if (!isSwap(removeReferenceId)) {
                if (buildRemoveId == null && sourceBuildId != null) {
                    continue;
                }
            }

            ProcureLine procureLine = material.getProcureLine();
            if (procureLine != null) {
                updateReferenceIds(procureLine);
            }
            ProcurementServicesHelper.replaceDeliveryNoteLineReferenceIds(replacementReference, material, removeReferenceId);

            if (sourceMaterialHeader != null && sourceMaterialHeader.getMaterialHeaderOid() != materialHeader.getMaterialHeaderOid()) {
                material.setMaterialHeader(materialHeader);
                materialHeader.getMaterials().add(material);
            } else {
                continue;
            }
            if (procureLine != null) {
                updateReferenceIds(procureLine);
            }
 
            String sourceMaterialControllerUserId = sourceMaterialHeader.getMaterialControllerUserId();
            if (StringUtils.isNotEmpty(sourceMaterialControllerUserId)) {
                materialHeader.setMaterialControllerUserId(sourceMaterialControllerUserId);
                materialHeader.setMaterialControllerName(sourceMaterialHeader.getMaterialControllerName());
                materialHeader.setMaterialControllerTeam(sourceMaterialHeader.getMaterialControllerTeam());
                materialHeader.setCompanyCode(sourceMaterialHeader.getCompanyCode());
            }
            setAcceptedVersion(removeReferenceId, sourceMaterialHeader, sourceMaterialHeaderVersion, outboundLocationId, crId);
            return material;
        }
        return null;
    }

    private static boolean isSwap(String removeReferenceId) {
        return removeReferenceId != null && !StringUtils.isEmpty(removeReferenceId);
    }

    private static boolean notMatchesBuildId(String buildRemoveId, String sourceBuildId) {
        return buildRemoveId != null && sourceBuildId != null && !buildRemoveId.equalsIgnoreCase(sourceBuildId);
    }

    private static boolean notMatchesReferenceId(String sourceReferenceID, String referenceID) {
        return sourceReferenceID != null && referenceID != null && !referenceID.equalsIgnoreCase(sourceReferenceID);
    }

    private static boolean notMatchesCrId(String sourceCrId, String crId) {
        return sourceCrId != null && crId != null && !crId.equalsIgnoreCase(sourceCrId);
    }

    public static boolean isContainingLines(RequestHeaderTransformerDTO requestHeaderTransformerDTO) {
        return requestHeaderTransformerDTO.getRequestHeaderVersionTransformerDtos().get(0).isContainingLines();

    }

    private static boolean notMatchesOutboundLocationId(String sourceOutboundLocationId, String outboundLocationId) {
        return sourceOutboundLocationId != null && outboundLocationId != null && !sourceOutboundLocationId.equalsIgnoreCase(outboundLocationId);
    }

    public static void setBuild(RequestHeaderTransformerDTO requestHeaderTransformerDTO, MaterialHeader materialHeader) {
        String buildId = requestHeaderTransformerDTO.getBuildId();
        String buildName = requestHeaderTransformerDTO.getBuildName();
        String buildType = requestHeaderTransformerDTO.getBuildType();
        materialHeader.setBuildId(buildId);
        materialHeader.setBuildName(buildName);
        materialHeader.setBuildType(BuildType.FIRSTASSEMBLY);
        if (!StringUtils.isEmpty(buildType) && buildType.equalsIgnoreCase(BuildType.REBUILD.name())) {
            materialHeader.setBuildType(BuildType.REBUILD);
        }
    }
    
    public static ProcureLine updateReferenceIds(ProcureLine procureLine) {
        List<Material> materialList = procureLine.getMaterials();
        UniqueItems referenceIds = new UniqueItems();

        for (Material material : materialList) {
            if (material.getStatus() == MaterialStatus.ADDED || material.getStatus() == MaterialStatus.REMOVE_MARKED) {
                MaterialHeader header = material.getMaterialHeader();
                if (header != null) {
                    if (!material.getMaterialType().isAdditional()) {
                        referenceIds.add(header.getReferenceId());
                    }
                }
            }
        }
        procureLine.setReferenceIds(referenceIds.createCommaSeparatedKey());
        return procureLine;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void autoAssignToMC(MaterialHeader materialHeader, ProcurementServices procurementServices, UserServices userServices,
            TeamRepository teamRepository, CompanyCodeRepository companyCodeRepository) throws GloriaApplicationException {
        UserDTO userDTO = userServices.getUser(materialHeader.getMcIdToBeAssigned());
        List<String> companyGroups = Arrays.asList(materialHeader.getCompanyCode());
        Collection companyGroupsByCompanyCodes = CollectionUtils.collect(companyCodeRepository.getCompanyGroupsByCompanyCodes(companyGroups),
                                                                         TransformerUtils.invokerTransformer("getCode"));

        Team mcTeamForCompanyCode = teamRepository.findTeamByCompanyCodeAndType(new ArrayList(companyGroupsByCompanyCodes), TeamType.MATERIAL_CONTROL);
        if (mcTeamForCompanyCode != null && GloriaFormateUtil.getValuesAsString(userDTO.getProcureTeam()).contains(mcTeamForCompanyCode.getName())) {
            materialHeader.setMaterialControllerName(userDTO.getUserName());
            materialHeader.setMaterialControllerUserId(materialHeader.getMcIdToBeAssigned());
            materialHeader.setMaterialControllerTeam(mcTeamForCompanyCode.getName());

            procurementServices.assignMaterialController(materialHeader.getMcIdToBeAssigned(), materialHeader.getAccepted().getRequesterUserId(),
                                                         userDTO.getProcureTeam(), Arrays.asList(materialHeader));
        } else {
            LOGGER.error("The Material Controller doesnt belong to team/companycode " + mcTeamForCompanyCode.getName() + "/" + materialHeader.getCompanyCode());
        }
    }
}
