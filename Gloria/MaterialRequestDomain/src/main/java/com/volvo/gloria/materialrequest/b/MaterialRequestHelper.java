package com.volvo.gloria.materialrequest.b;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.authorization.c.TeamType;
import com.volvo.gloria.authorization.d.entities.Team;
import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.materialRequestProxy.b.MaterialRequestCancelSender;
import com.volvo.gloria.materialRequestProxy.b.MaterialRequestSender;
import com.volvo.gloria.materialrequest.c.dto.MaterialRequestDTO;
import com.volvo.gloria.materialrequest.c.dto.MaterialRequestLineDTO;
import com.volvo.gloria.materialrequest.d.entities.FinanceMaterial;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequest;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequestLine;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequestObject;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequestVersion;
import com.volvo.gloria.materialrequest.d.status.materialrequest.MaterialRequestStatus;
import com.volvo.gloria.materialrequest.d.status.materialrequest.MaterialRequestStatusHelper;
import com.volvo.gloria.materialrequest.d.type.materialrequest.MaterialRequestType;
import com.volvo.gloria.materialrequest.repositories.b.MaterialRequestRepository;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.Utils;
import com.volvo.gloria.util.c.GloriaExceptionConstants;
import com.volvo.gloria.util.c.UniqueItems;

/**
 * Helper class for Procurement related entities.
 * 
 */
public class MaterialRequestHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaterialRequestHelper.class);
    
    protected MaterialRequestHelper() {

    }

    public static List<MaterialRequestDTO> transformListOfMaterialRequestDTOs(List<MaterialRequest> materialRequests) {
        List<MaterialRequestDTO> listOfMaterialRequestHeaderDTO = new ArrayList<MaterialRequestDTO>();

        if (materialRequests != null && !materialRequests.isEmpty()) {
            for (MaterialRequest materialRequest : materialRequests) {
                listOfMaterialRequestHeaderDTO.add(transformAsDTO(materialRequest));
            }
        }

        return listOfMaterialRequestHeaderDTO;
    }

    public static MaterialRequestDTO transformAsDTO(MaterialRequest materialRequest) {
        if (materialRequest != null) {
            MaterialRequestDTO materialRequestDTO = new MaterialRequestDTO();

            materialRequestDTO.setId(materialRequest.getMaterialRequestOid());
            materialRequestDTO.setVersion(materialRequest.getVersion());
            FinanceMaterial financeMaterial = materialRequest.getFinanceMaterial();
            if (financeMaterial != null) {
                materialRequestDTO.setFinanceMaterialID(financeMaterial.getFinanceMaterialOid());
                materialRequestDTO.setFinanceMaterialVersion(financeMaterial.getVersion());
                materialRequestDTO.setProjectId(financeMaterial.getProjectId());
                materialRequestDTO.setCompanyCode(financeMaterial.getCompanyCode());
                materialRequestDTO.setInternalOrderNoSAP(financeMaterial.getInternalOrderNoSAP());
                materialRequestDTO.setGlAccount(financeMaterial.getGlAccount());
                materialRequestDTO.setWbsCode(financeMaterial.getWbsCode());
                materialRequestDTO.setCostCenter(financeMaterial.getCostCenter());
            }           

            MaterialRequestVersion current = materialRequest.getCurrent();
            if (current != null) {
                if (materialRequest.getType() == MaterialRequestType.SINGLE) {
                    MaterialRequestObject materialRequestObject = current.getMaterialRequestObject();
                    materialRequestDTO.setMaterialRequestObjectID(materialRequestObject.getMaterialRequestObjectOid());
                    materialRequestDTO.setMaterialRequestObjectVersion(materialRequestObject.getVersion());
                    materialRequestDTO.setName(materialRequestObject.getName());
                } else if (materialRequest.getType() == MaterialRequestType.MULTIPLE) {
                    List<MaterialRequestLine> materialRequestLines = current.getMaterialRequestLines();
                    if (materialRequestLines != null && !materialRequestLines.isEmpty()) {
                        UniqueItems testObjectName = new UniqueItems();
                        for (MaterialRequestLine materialRequestLine : materialRequestLines) {
                            testObjectName.add(materialRequestLine.getMaterialRequestObject().getName());
                        }
                        materialRequestDTO.setName(testObjectName.createCommaSeparatedKey());
                    }
                }

                materialRequestDTO.setMaterialRequestVersionId(current.getMaterialRequestVersionOid());
                materialRequestDTO.setMaterialRequestVersionVersion(current.getVersion());
                materialRequestDTO.setTitle(current.getTitle());

                materialRequestDTO.setMtrlRequestVersion(current.getMtrlRequestVersion());
                materialRequestDTO.setMaterialRequestVersionStatus(current.getStatus().name());
                materialRequestDTO.setMaterialRequestVersionStatusDate(current.getStatusDate());

                materialRequestDTO.setRequiredStaDate(current.getRequiredStaDate());
                materialRequestDTO.setMailFormId(current.getMailFormId());
                materialRequestDTO.setOutboundStartDate(current.getOutboundStartDate());
                materialRequestDTO.setOutboundLocationId(current.getOutboundLocationId());
                materialRequestDTO.setApprovalAmount(current.getApprovalAmount());
                materialRequestDTO.setApprovalCurrency(current.getApprovalCurrency());
            }
            materialRequestDTO.setContactPersonUserId(materialRequest.getContactPersonUserId());
            materialRequestDTO.setContactPersonName(materialRequest.getContactPersonName());
            materialRequestDTO.setRequesterId(materialRequest.getRequesterId());
            materialRequestDTO.setRequesterName(materialRequest.getRequesterName());
            if (materialRequest.getType() != null) {
                materialRequestDTO.setType(materialRequest.getType().name());
            }
            materialRequestDTO.setMaterialControllerUserId(materialRequest.getMaterialControllerUserId());
            materialRequestDTO.setMaterialControllerName(materialRequest.getMaterialControllerName());

            return materialRequestDTO;
        }
        return null;
    }

    public static List<MaterialRequestLineDTO> transformListOfMaterialRequestLineDTOs(List<MaterialRequestLine> materialRequestLines) {
        List<MaterialRequestLineDTO> listOfMaterialRequestLineDTO = new ArrayList<MaterialRequestLineDTO>();

        if (materialRequestLines != null && !materialRequestLines.isEmpty()) {
            for (MaterialRequestLine materialRequestLine : materialRequestLines) {
                listOfMaterialRequestLineDTO.add(transformAsDTO(materialRequestLine));
            }
        }
        return listOfMaterialRequestLineDTO;
    }

    public static MaterialRequestLineDTO transformAsDTO(MaterialRequestLine materialRequestLine) {
        if (materialRequestLine != null) {
            MaterialRequestLineDTO materialRequestLineDTO = new MaterialRequestLineDTO();
            materialRequestLineDTO.setId(materialRequestLine.getMaterialRequestLineOid());
            materialRequestLineDTO.setVersion(materialRequestLine.getVersion());
            materialRequestLineDTO.setFunctionGroup(materialRequestLine.getFunctionGroup());
            materialRequestLineDTO.setPartAffiliation(materialRequestLine.getPartAffiliation());
            materialRequestLineDTO.setPartModification(materialRequestLine.getPartModification());
            materialRequestLineDTO.setPartName(materialRequestLine.getPartName());
            materialRequestLineDTO.setPartNumber(materialRequestLine.getPartNumber());
            materialRequestLineDTO.setPartVersion(materialRequestLine.getPartVersion().toUpperCase());
            materialRequestLineDTO.setQuantity(materialRequestLine.getQuantity());
            materialRequestLineDTO.setUnitOfMeasure(materialRequestLine.getUnitOfMeasure());
            materialRequestLineDTO.setRemoveMarked(materialRequestLine.isRemoveMarked());
            materialRequestLineDTO.setChangeTechId(materialRequestLine.getChangeTechId());
            materialRequestLineDTO.setIsNew(materialRequestLine.isNewMarked());
            if (materialRequestLine.getMaterialRequestObject() != null) {
                materialRequestLineDTO.setName(materialRequestLine.getMaterialRequestObject().getName());
            }
            return materialRequestLineDTO;
        }
        return null;
    }

    public static MaterialRequest sendMaterialRequest(MaterialRequest materialRequest, String userId, String userName,
            MaterialRequestRepository materialRequestRepository, MaterialRequestSender materialRequestSender, CommonServices commonServices)
            throws GloriaApplicationException {
        materialRequest.setRequesterId(userId);
        materialRequest.setRequesterName(userName);
        return materialRequest.getCurrent().getStatus().send(materialRequest, materialRequestRepository, materialRequestSender, commonServices);
    }

    public static MaterialRequest revertMaterialRequestVersion(MaterialRequest materialRequest, MaterialRequestRepository materialRequestRepository)
            throws GloriaApplicationException {
        return materialRequest.getCurrent().getStatus().revert(materialRequest, materialRequestRepository);
    }

    public static MaterialRequest copyCreateMaterialRequest(long materialRequestOid, String userId, MaterialRequestRepository materialRequestRepository,
            UserServices userServices) throws GloriaApplicationException {
        MaterialRequest materialRequest = materialRequestRepository.findById(materialRequestOid);
        if (materialRequest != null) {
            UserDTO userDTO = userServices.getUser(userId);
            MaterialRequest newRequest = new MaterialRequest();
            newRequest.setContactPersonUserId(materialRequest.getContactPersonUserId());
            newRequest.setContactPersonName(materialRequest.getContactPersonName());          
            newRequest.setType(materialRequest.getType());
            newRequest.setMaterialRequestId(MaterialRequestStatusHelper.evaluateTypePrefix(materialRequest.getType())
                    + (materialRequestRepository.getMaxMaterialRequestIdSequence() + 1));
            if (userDTO != null) {
                newRequest.setRequesterId(userDTO.getId());
                newRequest.setRequesterName(userDTO.getUserName());
            }

            FinanceMaterial newfinanceMaterial = new FinanceMaterial();
            newRequest.setFinanceMaterial(newfinanceMaterial);
            FinanceMaterial financeMaterial = materialRequest.getFinanceMaterial();

            newfinanceMaterial.setProjectId(financeMaterial.getProjectId());
            newfinanceMaterial.setCompanyCode(financeMaterial.getCompanyCode());
            newfinanceMaterial.setWbsCode(financeMaterial.getWbsCode());
            newfinanceMaterial.setGlAccount(financeMaterial.getGlAccount());
            newfinanceMaterial.setCostCenter(financeMaterial.getCostCenter());
            newfinanceMaterial.setInternalOrderNoSAP(financeMaterial.getInternalOrderNoSAP());

            MaterialRequestVersion newRequestVersion = new MaterialRequestVersion();
            MaterialRequestVersion materialRequestVersion = materialRequest.getCurrent();

            newRequestVersion.setMaterialRequest(newRequest);
            newRequestVersion.setChangeTechId(Utils.createRandomString());
            newRequestVersion.setStatus(MaterialRequestStatus.CREATED);
            newRequestVersion.setStatusDate(DateUtil.getCurrentUTCDateTime());
            newRequestVersion.setRequiredStaDate(materialRequestVersion.getRequiredStaDate());
            newRequestVersion.setOutboundLocationId(materialRequestVersion.getOutboundLocationId());
            newRequestVersion.setOutboundStartDate(materialRequestVersion.getOutboundStartDate());
            newRequestVersion.setApprovalAmount(materialRequestVersion.getApprovalAmount());
            newRequestVersion.setApprovalCurrency(materialRequestVersion.getApprovalCurrency());
            newRequestVersion.setTitle(materialRequestVersion.getTitle());
            newRequestVersion.setMailFormId(materialRequestVersion.getMailFormId());

            if (materialRequest.getType() == MaterialRequestType.SINGLE) {
                MaterialRequestObject newMaterialRequestObject = new MaterialRequestObject();
                MaterialRequestObject materialRequestObject = materialRequestVersion.getMaterialRequestObject();
                newMaterialRequestObject.setName(materialRequestObject.getName());
                newRequestVersion.setMaterialRequestObject(newMaterialRequestObject);
            }

            newRequest.getMaterialRequestVersions().add(newRequestVersion);
            newRequest.setCurrent(newRequestVersion);

            newRequestVersion.getMaterialRequestLines().addAll(copyCreateMaterialRequestLines(materialRequestVersion.getMaterialRequestLines(),
                                                                                              newRequestVersion, materialRequestRepository));
            return materialRequestRepository.save(newRequest);
        }
        return null;
    }

    private static List<MaterialRequestLine> copyCreateMaterialRequestLines(List<MaterialRequestLine> materialRequestLines,
            MaterialRequestVersion newRequestVersion, MaterialRequestRepository materialRequestRepository) throws GloriaApplicationException {
        List<MaterialRequestLine> newLines = new ArrayList<MaterialRequestLine>();
        if (materialRequestLines != null && !materialRequestLines.isEmpty()) {
            for (MaterialRequestLine materialRequestLine : materialRequestLines) {
                MaterialRequestLine newMaterialRequestLine = new MaterialRequestLine();
                if (newRequestVersion != null) {
                    newMaterialRequestLine.setChangeTechId(newRequestVersion.getChangeTechId());
                    newMaterialRequestLine.setMaterialRequestHeaderVersion(newRequestVersion);
                    handleMaterialRequestObject(materialRequestLine, newRequestVersion, newMaterialRequestLine, materialRequestRepository);
                }
                newMaterialRequestLine.setPartAffiliation(materialRequestLine.getPartAffiliation());
                newMaterialRequestLine.setPartNumber(materialRequestLine.getPartNumber());
                newMaterialRequestLine.setPartVersion(materialRequestLine.getPartVersion());
                newMaterialRequestLine.setPartName(materialRequestLine.getPartName());
                newMaterialRequestLine.setPartModification(materialRequestLine.getPartModification());
                newMaterialRequestLine.setQuantity(materialRequestLine.getQuantity());
                newMaterialRequestLine.setUnitOfMeasure(materialRequestLine.getUnitOfMeasure());
                newMaterialRequestLine.setFunctionGroup(materialRequestLine.getFunctionGroup());
                newMaterialRequestLine.setRequestLinkId(Utils.createRandomNumber());
                newMaterialRequestLine.setOperation("ADDED");
                newLines.add(newMaterialRequestLine);
            }
        }
        return newLines;
    }

    private static void handleMaterialRequestObject(MaterialRequestLine materialRequestLine, MaterialRequestVersion newRequestVersion,
            MaterialRequestLine newMaterialRequestLine, MaterialRequestRepository materialRequestRepository) {
        MaterialRequestObject materialRequestObject = materialRequestLine.getMaterialRequestObject();
        if (newRequestVersion.getMaterialRequest().getType() == MaterialRequestType.MULTIPLE && materialRequestObject != null) {
            MaterialRequestObject newMaterialRequestObject = new MaterialRequestObject();
            materialRequestRepository.save(newMaterialRequestObject);
            newMaterialRequestObject.setName(materialRequestObject.getName());
            newMaterialRequestLine.setMaterialRequestObject(newMaterialRequestObject);
        }
    }

    public static void writeRowCells(List<Object> cellContents, XSSFRow row) {
        if (cellContents != null && !cellContents.isEmpty()) {
            for (int columnIndex = 0; columnIndex < cellContents.size(); columnIndex++) {
                XSSFCell newCell = row.createCell(columnIndex);
                if (evalCellType(cellContents.get(columnIndex)) == XSSFCell.CELL_TYPE_NUMERIC) {
                    newCell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
                    newCell.setCellValue(Integer.valueOf(String.valueOf(cellContents.get(columnIndex))));
                } else {
                    newCell.setCellValue(String.valueOf(cellContents.get(columnIndex)));
                    newCell.setCellType(XSSFCell.CELL_TYPE_STRING);
                }                
            }
        }
    }

    private static int evalCellType(Object object) {
        if (object instanceof Long || object instanceof Double) {
            return XSSFCell.CELL_TYPE_NUMERIC;
        }
        return XSSFCell.CELL_TYPE_STRING;
    }

    public static List<Object> evalRow(MaterialRequestType type, MaterialRequestLine materialRequestLine) {
        List<Object> xclRows = new ArrayList<Object>();
        if (type == MaterialRequestType.SINGLE || type == MaterialRequestType.FOR_STOCK) {
            xclRows.add(materialRequestLine.getPartAffiliation());
            xclRows.add(handleEmpty(materialRequestLine.getPartNumber()));
            xclRows.add(handleEmpty(materialRequestLine.getPartVersion()));
            xclRows.add(handleEmpty(materialRequestLine.getPartName()));
            xclRows.add(handleEmpty(materialRequestLine.getPartModification()));
            xclRows.add(materialRequestLine.getQuantity());
            xclRows.add(handleEmpty(materialRequestLine.getUnitOfMeasure()));
            xclRows.add(handleEmpty(materialRequestLine.getFunctionGroup()));
        } else {
            String tObject = "";
            if (materialRequestLine.getMaterialRequestObject() != null) {
                tObject = materialRequestLine.getMaterialRequestObject().getName();
            }
            xclRows.add(handleEmpty(tObject));
            xclRows.add(handleEmpty(materialRequestLine.getPartAffiliation()));
            xclRows.add(handleEmpty(materialRequestLine.getPartNumber()));
            xclRows.add(handleEmpty(materialRequestLine.getPartVersion()));
            xclRows.add(handleEmpty(materialRequestLine.getPartName()));
            xclRows.add(materialRequestLine.getQuantity());
            xclRows.add(handleEmpty(materialRequestLine.getUnitOfMeasure()));
        }
        return xclRows;
    }

    public static List<Object> evalHeader(MaterialRequestType type) {
        List<Object> xclHeaders = new ArrayList<Object>();
        if (type == MaterialRequestType.SINGLE || type == MaterialRequestType.FOR_STOCK) {
            xclHeaders.add("Part Affiliation");
            xclHeaders.add("Part No.");
            xclHeaders.add("Version");
            xclHeaders.add("Part Name");
            xclHeaders.add("Part Modification");
            xclHeaders.add("Qty");
            xclHeaders.add("Unit of Measure");
            xclHeaders.add("Function Group");
        } else {
            xclHeaders.add("Test Object");
            xclHeaders.add("Part Affiliation");
            xclHeaders.add("Part No.");
            xclHeaders.add("Version");
            xclHeaders.add("Part Name");
            xclHeaders.add("Qty");
            xclHeaders.add("Unit of Measure");
        }
        return xclHeaders;
    }

    public static byte[] getExcelBytes(XSSFWorkbook workbook) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
        } catch (IOException e) {
            throw new GloriaSystemException(e, "Failed to export data to excel.");
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                LOGGER.error("Could not close excel stream", e);
            }
        }
        return bos.toByteArray();
    }

    private static String handleEmpty(String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        return value;
    }

    public static MaterialRequest cancelMaterialRequest(MaterialRequest materialRequest, String userId, String userName,
            MaterialRequestCancelSender materialRequestCancelSender, MaterialRequestRepository materialRequestRepository) throws GloriaApplicationException {
        materialRequest.setRequesterId(userId);
        materialRequest.setRequesterName(userName);
        materialRequest.getCurrent().getStatus().cancel(materialRequest, materialRequestCancelSender, materialRequestRepository);
        return materialRequest;
    }

    public static void validateWBSProjectAndCompanyCodes(String companyCode, String projectId, String wbsCode, CommonServices commonServices) {
        if (commonServices.findWBSElementByProjectIDandCompanyCode(companyCode, projectId, wbsCode) == null) {
            throw new GloriaSystemException("This operation cannot be performed. No WBS Element object exists for the combination of companyCode : "
                    + companyCode + " : projectId ::" + projectId + " :: wbsCode ::", GloriaExceptionConstants.INVALID_DATASET_OID);
        }
    }

    public static MaterialRequest createNewVersion(long materialRequestId, MaterialRequestRepository materialRequestRepository)
            throws GloriaApplicationException {
        MaterialRequest materialRequest = materialRequestRepository.findById(materialRequestId);
        return materialRequest.getCurrent().getStatus().newVersion(materialRequest, materialRequestRepository);
    }

    public static boolean validateMCAndCompanyCodes(String materialControllerUserId, String companyCode, UserServices userServices)
            throws GloriaApplicationException {
        List<Team> matchingAssignedMCTeams = null;
        if (!StringUtils.isEmpty(materialControllerUserId)) {
            matchingAssignedMCTeams = userServices.getUserTeams(materialControllerUserId.toUpperCase(), TeamType.MATERIAL_CONTROL.name(), companyCode);
        }
        return (matchingAssignedMCTeams != null && !matchingAssignedMCTeams.isEmpty());
    }
}
