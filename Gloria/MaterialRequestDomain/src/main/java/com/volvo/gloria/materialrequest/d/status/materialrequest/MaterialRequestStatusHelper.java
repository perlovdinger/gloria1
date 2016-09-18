package com.volvo.gloria.materialrequest.d.status.materialrequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.common.d.entities.UnitOfMeasure;
import com.volvo.gloria.common.repositories.b.UnitOfMeasureRepository;
import com.volvo.gloria.materialRequestProxy.b.MaterialRequestCancelSender;
import com.volvo.gloria.materialRequestProxy.b.MaterialRequestSender;
import com.volvo.gloria.materialRequestProxy.c.FinanceMaterialDTO;
import com.volvo.gloria.materialRequestProxy.c.MaterialRequestLineTransformerDTO;
import com.volvo.gloria.materialRequestProxy.c.MaterialRequestToSendDTO;
import com.volvo.gloria.materialRequestProxy.c.MaterialRequestTransformerDTO;
import com.volvo.gloria.materialRequestProxy.c.MaterialRequestVersionTransformerDTO;
import com.volvo.gloria.materialrequest.c.dto.MaterialRequestLineDTO;
import com.volvo.gloria.materialrequest.d.entities.FinanceMaterial;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequest;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequestLine;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequestObject;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequestVersion;
import com.volvo.gloria.materialrequest.d.type.materialrequest.MaterialRequestType;
import com.volvo.gloria.materialrequest.repositories.b.MaterialRequestRepository;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.Utils;
import com.volvo.gloria.util.c.GloriaExceptionConstants;

/**
 * This class is used for method implementations that are common for several statuses.
 */
public final class MaterialRequestStatusHelper {
    
    private static final String INVALID_LENGTH = "Invalid Length";

    private static final Logger LOGGER = LoggerFactory.getLogger(MaterialRequestStatusHelper.class);
    
    private static final int PART_NUMBER_LENGTH = 20;
    private static final int PART_NUMBER_LENGTH_FOR_VOLVO_PARTS = 8;
    private static final int PART_VERSION_LENGTH = 10;
    private static final int PART_NAME_LENGTH = 254;
    private static final int PART_MODIFICATION_LENGTH = 60;
    private static final int FUNCTION_GROUP_LENGTH = 8;

    private MaterialRequestStatusHelper() {

    }

    public static MaterialRequestLine doCreateMaterialRequestLine(MaterialRequestLineDTO materialRequestLineDTO,
            MaterialRequestRepository materialRequestHeaderRepo, MaterialRequest materialRequestHeader, UnitOfMeasureRepository unitOfMeasureRepo)
            throws GloriaApplicationException {
        validatePartInfo(materialRequestLineDTO, unitOfMeasureRepo.findAllUnitOfMeasuresSupportedForGloria());

        MaterialRequestLine materialRequestLine = new MaterialRequestLine();
        MaterialRequestVersion current = materialRequestHeader.getCurrent();
        if (current != null) {
            materialRequestLine.setChangeTechId(current.getChangeTechId());
            materialRequestLine.setMaterialRequestHeaderVersion(current);
            handleMaterialRequestObject(materialRequestLineDTO, materialRequestHeaderRepo, materialRequestLine, current);
        }
        materialRequestHeader.getType().setAttributesOnCreate(materialRequestLineDTO, materialRequestLine);
        return materialRequestHeaderRepo.saveOrUpdateMaterialRequestLine(materialRequestLine);
    }

    public static void validatePartInfo(MaterialRequestLineDTO materialRequestLineDTO, List<UnitOfMeasure> unitOfMeasures) throws GloriaApplicationException {
        if ("V".equalsIgnoreCase(materialRequestLineDTO.getPartAffiliation())) {
            if (StringUtils.isEmpty(materialRequestLineDTO.getPartNumber())
                    || materialRequestLineDTO.getPartNumber().length() > PART_NUMBER_LENGTH_FOR_VOLVO_PARTS) {
                Map<String, Object> msgParam = new HashMap<String, Object>();
                msgParam.put("min", 1);
                msgParam.put("max", PART_NUMBER_LENGTH_FOR_VOLVO_PARTS);
                throw new GloriaApplicationException("partNumber", GloriaExceptionConstants.INVALID_STRING_LENGTH, INVALID_LENGTH, msgParam);
            }
            if (!Utils.isNumeric(materialRequestLineDTO.getPartNumber())) {
                throw new GloriaApplicationException("partNumber", GloriaExceptionConstants.NOT_NUMBER,
                                                     "This operation cannot be performed since the PartNumber is not a number.", null);
            }
            String partVersion = materialRequestLineDTO.getPartVersion().toUpperCase();
            boolean prefixWithAlphabet = partVersion.startsWith("A") || partVersion.startsWith("B") || partVersion.startsWith("C")
                    || partVersion.startsWith("P");

            boolean specificVersions = "B00".equalsIgnoreCase(partVersion) || "C00".equalsIgnoreCase(partVersion);
            if (!prefixWithAlphabet || !partVersion.substring(1).matches("^[0-9]{2}$") || specificVersions) {
                throw new GloriaApplicationException("partVersion", GloriaExceptionConstants.INVALID_PARTVERSION,
                                                     "This operation cannot be performed since the partVersion does not match the pattern", null);
            }
        } else if ("X".equalsIgnoreCase(materialRequestLineDTO.getPartAffiliation())) {
            if (StringUtils.isEmpty(materialRequestLineDTO.getPartNumber()) || materialRequestLineDTO.getPartNumber().length() > PART_NUMBER_LENGTH) {
                throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_STRING_LENGTH,
                                                     "This operation cannot be performed since the length of the Part Number is exceeds the max length "
                                                             + PART_NUMBER_LENGTH);
            }
        } else {
            // Validate if Part Affiliation is not V / X
            throw new GloriaApplicationException(null, GloriaExceptionConstants.INVALID_PART_AFFILIATION,
                                                 "This operation cannot be performed as the Part Affiliation is not valid", null);
        }

        // Part Version is mandatory and should be less than or equal to 10 chars
        if (StringUtils.isEmpty(materialRequestLineDTO.getPartVersion()) || materialRequestLineDTO.getPartVersion().length() > PART_VERSION_LENGTH) {
            Map<String, Object> msgParam = new HashMap<String, Object>();
            msgParam.put("min", 1);
            msgParam.put("max", PART_VERSION_LENGTH);
            throw new GloriaApplicationException("partVersion", GloriaExceptionConstants.INVALID_STRING_LENGTH, INVALID_LENGTH, msgParam);
        }

        // Unit Of Measure is mandatory and should be one of the available Unit of measure list
        if (StringUtils.isEmpty(materialRequestLineDTO.getUnitOfMeasure()) || !checkIfUomExists(unitOfMeasures, materialRequestLineDTO.getUnitOfMeasure())) {
            throw new GloriaApplicationException("unitOfMeasure", GloriaExceptionConstants.INVALID_UNIT_OF_MEASURE, "Invalid Unit Of Measure.", null);
        }

        // Part Name is mandatory and should be less than or equal to 254 chars
        if (StringUtils.isEmpty(materialRequestLineDTO.getPartName()) || materialRequestLineDTO.getPartName().length() > PART_NAME_LENGTH) {
            Map<String, Object> msgParam = new HashMap<String, Object>();
            msgParam.put("min", 1);
            msgParam.put("max", PART_NAME_LENGTH);
            throw new GloriaApplicationException("partName", GloriaExceptionConstants.INVALID_STRING_LENGTH, INVALID_LENGTH, msgParam);
        }

        // Part Modification is not mandatory but should be less than or equal to 60 chars if there is any
        if (!StringUtils.isEmpty(materialRequestLineDTO.getPartModification())
                && materialRequestLineDTO.getPartModification().length() > PART_MODIFICATION_LENGTH) {
            Map<String, Object> msgParam = new HashMap<String, Object>();
            msgParam.put("min", 1);
            msgParam.put("max", PART_MODIFICATION_LENGTH);
            throw new GloriaApplicationException("partModification", GloriaExceptionConstants.INVALID_STRING_LENGTH, INVALID_LENGTH, null);
        }

        // Function Group is not mandatory but should be less than or equal to 8 chars if there is any
        if (!StringUtils.isEmpty(materialRequestLineDTO.getFunctionGroup()) && materialRequestLineDTO.getFunctionGroup().length() > FUNCTION_GROUP_LENGTH) {
            Map<String, Object> msgParam = new HashMap<String, Object>();
            msgParam.put("min", 1);
            msgParam.put("max", FUNCTION_GROUP_LENGTH);
            throw new GloriaApplicationException("functionGroup", GloriaExceptionConstants.INVALID_STRING_LENGTH, INVALID_LENGTH, msgParam);
        }
    }

    private static boolean checkIfUomExists(List<UnitOfMeasure> unitOfMeasures, String valueToBeVerified) {
        for (UnitOfMeasure uom : unitOfMeasures) {
            if (valueToBeVerified.equalsIgnoreCase(uom.getCode())) {
                return true;
            }
        }
        return false;
    }

    public static MaterialRequestLine doUpdateMaterialRequestLine(MaterialRequestLineDTO materialRequestLineDTO,
            MaterialRequestRepository materialRequestHeaderRepo, MaterialRequestVersion current, MaterialRequestLine materialRequestLine)
            throws GloriaApplicationException {
        handleMaterialRequestObject(materialRequestLineDTO, materialRequestHeaderRepo, materialRequestLine, current);
        current.getMaterialRequest().getType().setAttributesOnUpdate(materialRequestLineDTO, materialRequestLine);
        return materialRequestHeaderRepo.saveOrUpdateMaterialRequestLine(materialRequestLine);
    }

    public static void handleMaterialRequestObject(MaterialRequestLineDTO materialRequestLineDTO, MaterialRequestRepository materialRequestHeaderRepo,
            MaterialRequestLine materialRequestLine, MaterialRequestVersion current) {
        if (current.getMaterialRequest().getType() == MaterialRequestType.MULTIPLE) {
            MaterialRequestObject materialRequestObject = null;
            if (materialRequestLine.getMaterialRequestLineOid() > 0) {
                materialRequestObject = materialRequestLine.getMaterialRequestObject();
            } else {
                materialRequestObject = new MaterialRequestObject();
                materialRequestHeaderRepo.save(materialRequestObject);
            }
            materialRequestObject.setName(materialRequestLineDTO.getName());
            materialRequestLine.setMaterialRequestObject(materialRequestObject);
        }
    }

    public static void doDeleteMaterialRequestLine(MaterialRequestLine materialRequestLine, MaterialRequestRepository materialRequestHeaderRepo,
            MaterialRequestVersion currentMaterialRequestVersion) throws GloriaApplicationException {
        if (currentMaterialRequestVersion.getChangeTechId().equals(materialRequestLine.getChangeTechId())) {
            materialRequestHeaderRepo.deleteMaterialRequestLine(materialRequestLine.getMaterialRequestLineOid());
        } else {
            materialRequestLine.setRemoveMarked(true);
            materialRequestLine.setOperation("REMOVED");
            materialRequestLine.setChangeTechId(currentMaterialRequestVersion.getChangeTechId());
            materialRequestHeaderRepo.saveOrUpdateMaterialRequestLine(materialRequestLine);
        }
    }

    public static MaterialRequest newVersion(MaterialRequest materialRequestHeader, MaterialRequestRepository materialRequestHeaderRepo, long changeVersion)
            throws GloriaApplicationException {
        MaterialRequestVersion current = materialRequestHeader.getCurrent();

        MaterialRequestVersion newMaterialRequestHeaderVersion = cloneMaterialRequestVersion(materialRequestHeader, materialRequestHeaderRepo, changeVersion,
                                                                                             current);
        newMaterialRequestHeaderVersion.setStatus(MaterialRequestStatus.UPDATED);
        newMaterialRequestHeaderVersion.setStatusDate(DateUtil.getCurrentUTCDateTime());

        MaterialRequestObject materialRequestObject = current.getMaterialRequestObject();
        if (materialRequestHeader.getType() == MaterialRequestType.SINGLE && materialRequestObject != null) {
            MaterialRequestObject newMaterialRequestObject = new MaterialRequestObject();
            newMaterialRequestObject.setName(materialRequestObject.getName());
            newMaterialRequestHeaderVersion.setMaterialRequestObject(newMaterialRequestObject);
        }

        materialRequestHeader.setCurrent(newMaterialRequestHeaderVersion);
        return materialRequestHeaderRepo.save(materialRequestHeader);
    }

    static MaterialRequestVersion cloneMaterialRequestVersion(MaterialRequest materialRequestHeader, MaterialRequestRepository materialRequestHeaderRepo,
            long changeVersion, MaterialRequestVersion current) throws GloriaApplicationException {
        MaterialRequestVersion newMaterialRequestHeaderVersion = new MaterialRequestVersion();
        newMaterialRequestHeaderVersion.setMaterialRequest(materialRequestHeader);
        newMaterialRequestHeaderVersion.setMtrlRequestVersion(current.getMtrlRequestVersion());
        newMaterialRequestHeaderVersion.setRequiredStaDate(current.getRequiredStaDate());
        newMaterialRequestHeaderVersion.setTitle(current.getTitle());
        newMaterialRequestHeaderVersion.setChangeVersion(changeVersion);
        newMaterialRequestHeaderVersion.setChangeTechId(Utils.createRandomString());
        newMaterialRequestHeaderVersion.setOutboundLocationId(current.getOutboundLocationId());
        newMaterialRequestHeaderVersion.setOutboundStartDate(current.getOutboundStartDate());
        newMaterialRequestHeaderVersion.setMailFormId(current.getMailFormId());
        newMaterialRequestHeaderVersion.setApprovalAmount(current.getApprovalAmount());
        newMaterialRequestHeaderVersion.setApprovalCurrency(current.getApprovalCurrency());
        newMaterialRequestHeaderVersion.setMaterialRequestLines(cloneMaterialRequestLines(current.getMaterialRequestLines(), newMaterialRequestHeaderVersion,
                                                                                          materialRequestHeaderRepo));
        return newMaterialRequestHeaderVersion;
    }

    private static List<MaterialRequestLine> cloneMaterialRequestLines(List<MaterialRequestLine> materialRequestLines, MaterialRequestVersion current,
            MaterialRequestRepository materialRequestHeaderRepo) throws GloriaApplicationException {
        List<MaterialRequestLine> listOfMaterialRequests = new ArrayList<MaterialRequestLine>();

        for (MaterialRequestLine materialRequestLine : materialRequestLines) {
            if (!materialRequestLine.isRemoveMarked()) {
                MaterialRequestLine clonedMaterialRequestLine = new MaterialRequestLine();
                clonedMaterialRequestLine.setChangeTechId(materialRequestLine.getChangeTechId());
                clonedMaterialRequestLine.setMaterialRequestHeaderVersion(current);
                clonedMaterialRequestLine.setFunctionGroup(materialRequestLine.getFunctionGroup());
                clonedMaterialRequestLine.setPartAffiliation(materialRequestLine.getPartAffiliation());
                clonedMaterialRequestLine.setPartModification(materialRequestLine.getPartModification());
                clonedMaterialRequestLine.setPartName(materialRequestLine.getPartName());
                clonedMaterialRequestLine.setPartNumber(materialRequestLine.getPartNumber());
                clonedMaterialRequestLine.setPartVersion(materialRequestLine.getPartVersion());
                clonedMaterialRequestLine.setProductClass(materialRequestLine.getProductClass());
                clonedMaterialRequestLine.setQuantity(materialRequestLine.getQuantity());
                clonedMaterialRequestLine.setRemoveMarked(materialRequestLine.isRemoveMarked());
                clonedMaterialRequestLine.setUnitOfMeasure(materialRequestLine.getUnitOfMeasure());
                clonedMaterialRequestLine.setRequestLinkId(materialRequestLine.getRequestLinkId());
                if (current.getMaterialRequest().getType() == MaterialRequestType.MULTIPLE) {
                    MaterialRequestObject materialRequestObject = materialRequestLine.getMaterialRequestObject();
                    if (materialRequestObject != null) {
                        MaterialRequestObject newMaterialRequestObject = new MaterialRequestObject();
                        newMaterialRequestObject.setName(materialRequestObject.getName());
                        materialRequestHeaderRepo.save(newMaterialRequestObject);
                        clonedMaterialRequestLine.setMaterialRequestObject(newMaterialRequestObject);
                        newMaterialRequestObject.getMaterialRequestLines().add(clonedMaterialRequestLine);
                    }
                }
                listOfMaterialRequests.add(clonedMaterialRequestLine);
            }
        }
        return listOfMaterialRequests;
    }

    public static List<MaterialRequestLine> findPhysicallyDeletedMaterialRequestLineOids(MaterialRequest materialRequestHeader,
            List<MaterialRequestLineDTO> materialRequestLineDTOs) {
        List<MaterialRequestLine> deletedMaterialRequestLines = new ArrayList<MaterialRequestLine>();
        MaterialRequestVersion current = materialRequestHeader.getCurrent();
        for (MaterialRequestLine someMaterialRequestLine : current.getMaterialRequestLines()) {
            boolean isForDelete = true;
            for (MaterialRequestLineDTO someMaterialRequestLineDTO : materialRequestLineDTOs) {
                if (someMaterialRequestLineDTO.getId() == someMaterialRequestLine.getMaterialRequestLineOid()) {
                    isForDelete = false;
                    break;
                }
            }
            if (isForDelete) {
                deletedMaterialRequestLines.add(someMaterialRequestLine);
            }
        }
        return deletedMaterialRequestLines;
    }

    public static MaterialRequestVersion backOneVerion(long previousChangeVersion, MaterialRequest materialRequestHeader,
            MaterialRequestRepository materialRequestHeaderRepo) {
        MaterialRequestVersion previousMaterialRequestHeaderVersion = null;
        for (MaterialRequestVersion headerVersion : materialRequestHeader.getMaterialRequestVersions()) {
            if (headerVersion.getChangeVersion() == previousChangeVersion) {
                previousMaterialRequestHeaderVersion = headerVersion;
                break;
            }
        }
        materialRequestHeader.setCurrent(previousMaterialRequestHeaderVersion);
        materialRequestHeaderRepo.save(materialRequestHeader);
        return previousMaterialRequestHeaderVersion;
    }

    public static void sortMaterialRequestHeaderVersionsByDescOrder(List<MaterialRequestVersion> materialRequestHeaderVersions) {
        Collections.sort(materialRequestHeaderVersions, new Comparator<MaterialRequestVersion>() {
            public int compare(MaterialRequestVersion materialRequestHeaderVersionOne, MaterialRequestVersion materialRequestHeaderVersionTwo) {
                if (materialRequestHeaderVersionOne.getChangeVersion() > (materialRequestHeaderVersionTwo.getChangeVersion())) {
                    return -1;
                }
                return 1;
            }
        });
    }

    public static MaterialRequest prepareAndSendMaterialRequest(MaterialRequest materialRequest, MaterialRequestRepository materialRequestRepository,
            MaterialRequestSender materialRequestSender) throws GloriaApplicationException {
        MaterialRequestToSendDTO materialRequestToSendDTO = new MaterialRequestToSendDTO();

        validateMaterialRequest(materialRequestRepository, materialRequest);

        materialRequestToSendDTO.setMaterialRequestId(materialRequest.getMaterialRequestId());
        materialRequestToSendDTO.setRequestType(materialRequest.getType().toString());
        MaterialRequestVersion currentRequestVersion = materialRequest.getCurrent();
        materialRequestToSendDTO.setMtrlRequestVersion(currentRequestVersion.getMtrlRequestVersion());
        materialRequestToSendDTO.setChangeTechId(currentRequestVersion.getChangeTechId());

        List<MaterialRequestTransformerDTO> materialRequestTransformerDTOs = new ArrayList<MaterialRequestTransformerDTO>();

        if (materialRequest.getType().equals(MaterialRequestType.MULTIPLE)) {
            List<MaterialRequestLine> materialRequestLines = currentRequestVersion.getMaterialRequestLines();
            Map<String, MaterialRequestTransformerDTO> requestLineMap = new HashMap<String, MaterialRequestTransformerDTO>();
            for (MaterialRequestLine materialRequestLine : materialRequestLines) {
                String materialRequestObjectName = materialRequestLine.getMaterialRequestObject().getName();
                if (requestLineMap.containsKey(materialRequestObjectName)) {
                    if (currentRequestVersion.getChangeTechId().equalsIgnoreCase(materialRequestLine.getChangeTechId())) {
                        MaterialRequestLineTransformerDTO materialRequestLineTransformerDTO = prepareMtrlRequestLines(currentRequestVersion,
                                                                                                                      materialRequestLine);
                        if (materialRequestLineTransformerDTO != null) {
                            MaterialRequestTransformerDTO materialRequestTransformerDTO = requestLineMap.get(materialRequestObjectName);
                            materialRequestTransformerDTO.getMtrlRequestLineTransformerDTOs().add(materialRequestLineTransformerDTO);
                        }
                    }
                } else {
                    MaterialRequestTransformerDTO materialRequestTransformerDTO = prepareMaterialRequestHeader(materialRequest, currentRequestVersion,
                                                                                                               materialRequestLine.getMaterialRequestObject());
                    if (currentRequestVersion.getChangeTechId().equalsIgnoreCase(materialRequestLine.getChangeTechId())) {
                        MaterialRequestLineTransformerDTO materialRequestLineTransformerDTO = prepareMtrlRequestLines(currentRequestVersion,
                                                                                                                      materialRequestLine);
                        if (materialRequestLineTransformerDTO != null) {
                            materialRequestTransformerDTO.getMtrlRequestLineTransformerDTOs().add(materialRequestLineTransformerDTO);
                        }
                    }
                    materialRequestTransformerDTOs.add(materialRequestTransformerDTO);
                    requestLineMap.put(materialRequestObjectName, materialRequestTransformerDTO);
                }
            }
        } else {
            MaterialRequestTransformerDTO materialRequestTransformerDTO = prepareMaterialRequestHeader(materialRequest, currentRequestVersion,
                                                                                                       currentRequestVersion.getMaterialRequestObject());

            materialRequestTransformerDTO.setMtrlRequestLineTransformerDTOs(createMaterialRequestLines(currentRequestVersion));

            materialRequestTransformerDTOs.add(materialRequestTransformerDTO);

        }

        materialRequestToSendDTO.setMaterialRequestTransformerDtos(materialRequestTransformerDTOs);

        try {
            materialRequestSender.sendMaterialRequestSender(materialRequestToSendDTO);
        } catch (JAXBException e) {
            throw new GloriaSystemException(e, "Unable to send procure message.");
        } catch (GloriaApplicationException e) {
            throw new GloriaApplicationException(GloriaExceptionConstants.JMS_MSG_SENDING_FAILED, "Failed to send message. Queue might not be up and running.");
        }

        return materialRequest;
    }

    private static MaterialRequestLineTransformerDTO prepareMtrlRequestLines(MaterialRequestVersion currentRequestVersion,
            MaterialRequestLine materialRequestLine) {
        if (currentRequestVersion.getChangeTechId().equalsIgnoreCase(materialRequestLine.getChangeTechId())) {
            MaterialRequestLineTransformerDTO materialRequestLineTransformerDTO = new MaterialRequestLineTransformerDTO();
            materialRequestLineTransformerDTO.setProcureLinkId(materialRequestLine.getRequestLinkId());
            if (materialRequestLine.isRemoveMarked()) {
                materialRequestLineTransformerDTO.setRemoveType(true);
            } else {
                materialRequestLineTransformerDTO.setRemoveType(false);
                materialRequestLineTransformerDTO.setFunctionGroup(materialRequestLine.getFunctionGroup());
                materialRequestLineTransformerDTO.setPartAffiliation(materialRequestLine.getPartAffiliation());
                materialRequestLineTransformerDTO.setPartModification(materialRequestLine.getPartModification());
                materialRequestLineTransformerDTO.setPartName(materialRequestLine.getPartName());
                materialRequestLineTransformerDTO.setPartNumber(materialRequestLine.getPartNumber());
                materialRequestLineTransformerDTO.setPartVersion(materialRequestLine.getPartVersion());
                materialRequestLineTransformerDTO.setProductClass(materialRequestLine.getProductClass());
                materialRequestLineTransformerDTO.setQuantity(materialRequestLine.getQuantity());
                materialRequestLineTransformerDTO.setUnitOfMeasure(materialRequestLine.getUnitOfMeasure());
            }
            return materialRequestLineTransformerDTO;
        }
        return null;
    }

    private static MaterialRequestTransformerDTO prepareMaterialRequestHeader(MaterialRequest materialRequest, MaterialRequestVersion currentRequestVersion,
            MaterialRequestObject materialRequestObject) {
        MaterialRequestTransformerDTO materialRequestTransformerDTO = new MaterialRequestTransformerDTO();
        materialRequestTransformerDTO.setMtrlRequestId(materialRequest.getMaterialRequestId());       
        materialRequestTransformerDTO.setContactPersonUserId(materialRequest.getContactPersonUserId());
        materialRequestTransformerDTO.setContactPersonName(materialRequest.getContactPersonName());
        materialRequestTransformerDTO.setRequesterId(materialRequest.getRequesterId());
        materialRequestTransformerDTO.setRequesterName(materialRequest.getRequesterName());
        materialRequestTransformerDTO.setMaterialControllerUserId(materialRequest.getMaterialControllerUserId());
        materialRequestTransformerDTO.setMaterialControllerName(materialRequest.getMaterialControllerName());
        if (materialRequestObject != null) {
            materialRequestTransformerDTO.setReferenceId(materialRequestObject.getName());
        }
        materialRequestTransformerDTO.setFinanceMaterialDTO(createFinanceMaterial(materialRequest));

        materialRequestTransformerDTO.setMtrlRequestVersionDTO(createMtlrRequestVersion(currentRequestVersion));
        return materialRequestTransformerDTO;
    }

    private static List<MaterialRequestLineTransformerDTO> createMaterialRequestLines(MaterialRequestVersion currentRequestVersion) {
        List<MaterialRequestLine> materialRequestLines = currentRequestVersion.getMaterialRequestLines();
        List<MaterialRequestLineTransformerDTO> materialRequestLineTransformerDTOs = new ArrayList<MaterialRequestLineTransformerDTO>();
        if (materialRequestLines != null && !materialRequestLines.isEmpty()) {
            for (MaterialRequestLine materialRequestLine : materialRequestLines) {
                materialRequestLineTransformerDTOs.add(prepareMtrlRequestLines(currentRequestVersion, materialRequestLine));
            }
        }
        return materialRequestLineTransformerDTOs;
    }

    private static FinanceMaterialDTO createFinanceMaterial(MaterialRequest materialRequest) {
        FinanceMaterialDTO financeMaterialDTO = new FinanceMaterialDTO();
        FinanceMaterial financeMaterial = materialRequest.getFinanceMaterial();
        financeMaterialDTO.setCompanyCode(financeMaterial.getCompanyCode());
        financeMaterialDTO.setCostCenter(financeMaterial.getCostCenter());
        financeMaterialDTO.setGlAccount(financeMaterial.getGlAccount());
        financeMaterialDTO.setInternalOrderNoSAP(financeMaterial.getInternalOrderNoSAP());
        financeMaterialDTO.setProjectId(financeMaterial.getProjectId());
        financeMaterialDTO.setWbsCode(financeMaterial.getWbsCode());
        return financeMaterialDTO;
    }

    private static MaterialRequestVersionTransformerDTO createMtlrRequestVersion(MaterialRequestVersion currentRequestVersion) {
        MaterialRequestVersionTransformerDTO mtrlRequestVersionDTO = new MaterialRequestVersionTransformerDTO();
        mtrlRequestVersionDTO.setApprovalAmount(currentRequestVersion.getApprovalAmount());
        mtrlRequestVersionDTO.setApprovalCurrency(currentRequestVersion.getApprovalCurrency());
        mtrlRequestVersionDTO.setMailFormId(currentRequestVersion.getMailFormId());
        mtrlRequestVersionDTO.setOutboundLocationId(currentRequestVersion.getOutboundLocationId());
        mtrlRequestVersionDTO.setOutboundStartDate(currentRequestVersion.getOutboundStartDate());
        mtrlRequestVersionDTO.setRequiredStaDate(currentRequestVersion.getRequiredStaDate());
        return mtrlRequestVersionDTO;
    }

    public static MaterialRequestStatus cancelMaterialRequest(MaterialRequest materialRequest, MaterialRequestCancelSender materialRequestCancelSender,
            MaterialRequestRepository materialRequestRepository) throws GloriaApplicationException {
        // new version for cancel
        long changeVersion = 0L;
        List<MaterialRequestVersion> materialRequestVersions = materialRequest.getMaterialRequestVersions();
        MaterialRequestStatusHelper.sortMaterialRequestHeaderVersionsByDescOrder(materialRequestVersions);
        changeVersion = materialRequest.getCurrent().getChangeVersion();
        for (MaterialRequestVersion materialRequestVersion : materialRequestVersions) {
            if (materialRequestVersion.getStatus().isSentAccepted()) {
                materialRequest.setCurrent(materialRequestVersion);
                break;
            }
        }

        MaterialRequest cancelRequest = newVersion(materialRequest, materialRequestRepository, changeVersion);

        MaterialRequestVersion cancelMaterialRequestVersion = cancelRequest.getCurrent();
        cancelMaterialRequestVersion.setStatus(MaterialRequestStatus.CANCEL_WAIT);

        List<MaterialRequestLine> materialRequestLines = cancelMaterialRequestVersion.getMaterialRequestLines();
        if (materialRequestLines != null && !materialRequestLines.isEmpty()) {
            for (MaterialRequestLine materialRequestLine : materialRequestLines) {
                materialRequestLine.setRemoveMarked(true);
            }
        }

        List<MaterialRequestTransformerDTO> materialRequestTransformerDtos = new ArrayList<MaterialRequestTransformerDTO>();
        MaterialRequestToSendDTO materialRequestToSendDTO = new MaterialRequestToSendDTO();
        materialRequestToSendDTO.setMtrlRequestVersion(cancelRequest.getMaterialRequestId() + "V" + (changeVersion + 1));

        MaterialRequestTransformerDTO materialRequestTransformerDTO = new MaterialRequestTransformerDTO();
        materialRequestTransformerDTO.setRequesterId(cancelRequest.getRequesterId());
        materialRequestTransformerDTO.setRequesterName(cancelRequest.getRequesterName());
        materialRequestTransformerDtos.add(materialRequestTransformerDTO);
        materialRequestToSendDTO.setMaterialRequestTransformerDtos(materialRequestTransformerDtos);
        materialRequestToSendDTO.setCancelRequest(true);
        materialRequestToSendDTO.setRequestType(cancelRequest.getType().toString());
        for (MaterialRequestLine materialRequestLine : cancelMaterialRequestVersion.getMaterialRequestLines()) {
            materialRequestToSendDTO.getChangeTechIds().add(materialRequestLine.getChangeTechId());
        }
        try {
            materialRequestCancelSender.sendCancelMaterialRequest(materialRequestToSendDTO);
        } catch (JAXBException e) {
            LOGGER.error("Error when sending cancel material request message:" + e.getMessage());
        }
        return cancelMaterialRequestVersion.getStatus();
    }

    public static String evaluateTypePrefix(MaterialRequestType materialRequestType) {
        String typePrefix = "S";
        if (materialRequestType == MaterialRequestType.MULTIPLE) {
            typePrefix = "M";
        } else if (materialRequestType == MaterialRequestType.FOR_STOCK) {
            typePrefix = "FS";
        }
        return typePrefix;
    }

    public static void validateMaterialRequest(MaterialRequestRepository materialRequestRepository, MaterialRequest materialRequest)
            throws GloriaApplicationException {
        if (!materialRequestRepository.validateMaterialRequest(materialRequest.getMaterialRequestOid(), materialRequest.getType().toString())) {
            throw new GloriaApplicationException(GloriaExceptionConstants.VALIDATING_MATERIAL_REQUEST_LINE,
                                                 "Failed to send message. It may be any of the material request line is not filled with mandatory fields.");
        }
    }

    public static MaterialRequestVersion doRevert(MaterialRequest materialRequest, MaterialRequestRepository materialRequestRepository) {
        MaterialRequestVersion toRemoveCurrentMaterialRequestHeaderVersion = materialRequest.getCurrent();
        long previousChangeVersion = toRemoveCurrentMaterialRequestHeaderVersion.getChangeVersion() - 1;

        MaterialRequestVersion materialRequestVersion = MaterialRequestStatusHelper.backOneVerion(previousChangeVersion, materialRequest,
                                                                                                  materialRequestRepository);

        materialRequestRepository.deleteMaterialRequestVersion(toRemoveCurrentMaterialRequestHeaderVersion);
        materialRequest.getMaterialRequestVersions().remove(toRemoveCurrentMaterialRequestHeaderVersion);
        return materialRequestVersion;
    }

    public static MaterialRequestLine doUndoRemoveMaterialRequestLine(MaterialRequestLine materialRequestLine,
            MaterialRequestRepository materialRequestRepository, MaterialRequestVersion currentMaterialRequestVersion) {
        materialRequestLine.setRemoveMarked(false);
        MaterialRequestVersion previousMaterialRequestVersion = null;
        for (MaterialRequestVersion headerVersion : currentMaterialRequestVersion.getMaterialRequest().getMaterialRequestVersions()) {
            if (headerVersion.getChangeVersion() == currentMaterialRequestVersion.getChangeVersion() - 1) {
                previousMaterialRequestVersion = headerVersion;
                break;
            }
        }

        if (previousMaterialRequestVersion != null) {
            materialRequestLine.setChangeTechId(previousMaterialRequestVersion.getChangeTechId());
        }
        materialRequestRepository.saveOrUpdateMaterialRequestLine(materialRequestLine);
        materialRequestLine.setOperation(null);
        return materialRequestLine;
    }
}
