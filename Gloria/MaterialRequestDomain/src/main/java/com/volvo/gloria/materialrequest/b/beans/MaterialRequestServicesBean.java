package com.volvo.gloria.materialrequest.b.beans;

import static com.volvo.gloria.util.c.GloriaParams.PROJECT_ID_LENGTH;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.authorization.d.entities.GloriaUser;
import com.volvo.gloria.authorization.d.userrole.UserRoleHelper;
import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TeamRepository;
import com.volvo.gloria.common.repositories.b.UnitOfMeasureRepository;
import com.volvo.gloria.materialRequestProxy.b.MaterialRequestCancelSender;
import com.volvo.gloria.materialRequestProxy.b.MaterialRequestSender;
import com.volvo.gloria.materialRequestProxy.c.MaterialProcureResponseDTO;
import com.volvo.gloria.materialrequest.b.MaterialRequestHelper;
import com.volvo.gloria.materialrequest.b.MaterialRequestServices;
import com.volvo.gloria.materialrequest.c.MaterialImportHandler;
import com.volvo.gloria.materialrequest.c.MultipleMaterialImportHandler;
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
import com.volvo.gloria.util.DocumentDTO;
import com.volvo.gloria.util.FileToExportDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.GloriaFormateUtil;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.Utils;
import com.volvo.gloria.util.c.GloriaExceptionConstants;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * MaterialRequest services.
 */
@ContainerManaged(name = "MaterialRequestServices")
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class MaterialRequestServicesBean implements MaterialRequestServices {
    private static final String CANCEL = "cancel";
    private static final String REVERT = "revert";
    private static final String SEND = "send";
    private static final String NEW_VERSION = "newVersion";
    private static final Logger LOGGER = LoggerFactory.getLogger(MaterialRequestServicesBean.class);

    @Inject
    private UserServices userServices;

    @Inject
    private MaterialRequestRepository materialRequestRepository;

    @Inject
    private UnitOfMeasureRepository unitOfMeasureRepo;

    @Inject
    private MaterialRequestSender materialRequestSender;

    @Inject
    private CommonServices commonServices;

    @Inject
    private MaterialRequestCancelSender materialRequestCancelSender;
    
    @Inject
    private TeamRepository teamRepository;

    @Override
    @PreAuthorize("hasAnyRole('PROCURE')")
    public MaterialRequest createMaterialRequest(MaterialRequestDTO materialRequestDTO, String userId) throws GloriaApplicationException {

        UserDTO userDTO = userServices.getUser(userId);

        MaterialRequest materialRequest = new MaterialRequest();

        if (!StringUtils.isEmpty(materialRequestDTO.getType())) {
            MaterialRequestType materialRequestType = MaterialRequestType.valueOf(materialRequestDTO.getType());
            materialRequest.setType(materialRequestType);
            materialRequest.setMaterialRequestId(MaterialRequestStatusHelper.evaluateTypePrefix(materialRequestType)
                    + (materialRequestRepository.getMaxMaterialRequestIdSequence() + 1));
        }       
        materialRequest.setContactPersonUserId(materialRequestDTO.getContactPersonUserId());
        materialRequest.setContactPersonName(materialRequestDTO.getContactPersonName());
        materialRequest.setMaterialControllerName(materialRequestDTO.getMaterialControllerName());
        materialRequest.setMaterialControllerUserId(materialRequestDTO.getMaterialControllerUserId());
        

        if (userDTO != null) {
            materialRequest.setRequesterId(userDTO.getId());
            materialRequest.setRequesterName(userDTO.getUserName());
        }

        FinanceMaterial financeMaterial = new FinanceMaterial();
        materialRequest.setFinanceMaterial(financeMaterial);
        if (!StringUtils.isEmpty(materialRequestDTO.getProjectId()) && materialRequestDTO.getProjectId().length() > PROJECT_ID_LENGTH) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_STRING_LENGTH,
                                                 "This operation cannot be performed since the length of the Project is exceeds the length "
                                                         + PROJECT_ID_LENGTH);
        }

        MaterialRequestHelper.validateWBSProjectAndCompanyCodes(materialRequestDTO.getCompanyCode(), materialRequestDTO.getProjectId(),
                                                                materialRequestDTO.getWbsCode(), commonServices);

        financeMaterial.setProjectId(materialRequestDTO.getProjectId());
        financeMaterial.setCompanyCode(materialRequestDTO.getCompanyCode());
        financeMaterial.setWbsCode(materialRequestDTO.getWbsCode());
        financeMaterial.setGlAccount(materialRequestDTO.getGlAccount());
        financeMaterial.setCostCenter(materialRequestDTO.getCostCenter());
        financeMaterial.setInternalOrderNoSAP(materialRequestDTO.getInternalOrderNoSAP());

        MaterialRequestVersion materialRequestVersion = new MaterialRequestVersion();
        materialRequestVersion.setMaterialRequest(materialRequest);
        materialRequestVersion.setChangeTechId(Utils.createRandomString());
        materialRequestVersion.setStatus(MaterialRequestStatus.CREATED);
        materialRequestVersion.setStatusDate(DateUtil.getCurrentUTCDateTime());
        materialRequestVersion.setRequiredStaDate(materialRequestDTO.getRequiredStaDate());
        materialRequestVersion.setOutboundLocationId(materialRequestDTO.getOutboundLocationId());
        materialRequestVersion.setOutboundStartDate(materialRequestDTO.getOutboundStartDate());
        materialRequestVersion.setApprovalAmount(materialRequestDTO.getApprovalAmount());
        materialRequestVersion.setApprovalCurrency(materialRequestDTO.getApprovalCurrency());
        materialRequestVersion.setTitle(materialRequestDTO.getTitle());
        materialRequestVersion.setMailFormId(materialRequestDTO.getMailFormId());

        if (materialRequest.getType() == MaterialRequestType.SINGLE) {
            MaterialRequestObject materialRequestObject = new MaterialRequestObject();
            materialRequestObject.setName(materialRequestDTO.getName());
            materialRequestVersion.setMaterialRequestObject(materialRequestObject);
        }

        materialRequest.getMaterialRequestVersions().add(materialRequestVersion);
        materialRequest.setCurrent(materialRequestVersion);

        return materialRequestRepository.save(materialRequest);
    }

    @Override
    @PreAuthorize("hasAnyRole('PROCURE')")
    public MaterialRequest updateMaterialRequest(MaterialRequestDTO materialRequestDTO) throws GloriaApplicationException {
        MaterialRequest materialRequest = materialRequestRepository.findById(materialRequestDTO.getId());

        if (materialRequest == null) {
            LOGGER.error("No material request objects exists for id : " + materialRequest);
            throw new GloriaSystemException("This operation cannot be performed. No material request objects exists for id : " + materialRequestDTO.getId(),
                                            GloriaExceptionConstants.INVALID_DATASET_OID);
        }

        if (materialRequestDTO.getVersion() != materialRequest.getVersion()) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_UPDATION_OF_STALE_DATASET,
                                                 "This operation cannot be performed since the information seen in the page has already been updated.");
        }

        if (!StringUtils.isEmpty(materialRequestDTO.getType())) {
            materialRequest.setType(MaterialRequestType.valueOf(materialRequestDTO.getType()));
        }
       
        materialRequest.setContactPersonUserId(materialRequestDTO.getContactPersonUserId());
        materialRequest.setContactPersonName(materialRequestDTO.getContactPersonName());
        materialRequest.setMaterialControllerUserId(materialRequestDTO.getMaterialControllerUserId());
        materialRequest.setMaterialControllerName(materialRequestDTO.getMaterialControllerName());
        materialRequest.setRequesterId(materialRequestDTO.getRequesterId());
        materialRequest.setRequesterName(materialRequestDTO.getRequesterName());

        MaterialRequestVersion current = materialRequest.getCurrent();
        current.setOutboundStartDate(materialRequestDTO.getOutboundStartDate());
        current.setRequiredStaDate(materialRequestDTO.getRequiredStaDate());
        current.setOutboundLocationId(materialRequestDTO.getOutboundLocationId());
        current.setApprovalAmount(materialRequestDTO.getApprovalAmount());
        current.setApprovalCurrency(materialRequestDTO.getApprovalCurrency());
        current.setTitle(materialRequestDTO.getTitle());
        current.setMailFormId(materialRequestDTO.getMailFormId());

        if (materialRequest.getType() == MaterialRequestType.SINGLE) {
            MaterialRequestObject materialRequestObject = current.getMaterialRequestObject();
            if (materialRequestObject != null) {
                materialRequestObject.setName(materialRequestDTO.getName());
            }
        }

        FinanceMaterial financeMaterial = materialRequest.getFinanceMaterial();
        if (financeMaterial != null) {
            if (materialRequestDTO.getProjectId().length() > PROJECT_ID_LENGTH) {
                throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_STRING_LENGTH,
                                                     "This operation cannot be performed since the length of the Project is exceeds the length "
                                                             + PROJECT_ID_LENGTH);
            }
            financeMaterial.setProjectId(materialRequestDTO.getProjectId());
            financeMaterial.setInternalOrderNoSAP(materialRequestDTO.getInternalOrderNoSAP());
            financeMaterial.setGlAccount(materialRequestDTO.getGlAccount());
            financeMaterial.setWbsCode(materialRequestDTO.getWbsCode());
            financeMaterial.setCostCenter(materialRequestDTO.getCostCenter());
            financeMaterial.setCompanyCode(materialRequestDTO.getCompanyCode());
        }

        return materialRequestRepository.save(materialRequest);
    }

    @Override
    @PreAuthorize("hasAnyRole('PROCURE')")
    public void deleteMaterialRequest(long materialRequestId) throws GloriaApplicationException {
        MaterialRequest materialRequest = materialRequestRepository.findById(materialRequestId);
        if (materialRequest != null) {
            materialRequest.getCurrent().getStatus().delete(materialRequest, materialRequestRepository);
        }
    }

    @Override
    @PreAuthorize("hasAnyRole('PROCURE','IT_SUPPORT')")
    public PageObject getMaterialRequestlines(long materialRequestId, PageObject pageObject) {
        return materialRequestRepository.findMaterialRequestLinesByHeaderId(pageObject, materialRequestId);
    }

    @Override
    @PreAuthorize("hasAnyRole('PROCURE')")
    public MaterialRequest updateMaterialRequest(MaterialRequestDTO materialRequestDTO, String action, String userId) throws GloriaApplicationException {
        MaterialRequest materialRequest = materialRequestRepository.findById(materialRequestDTO.getId());

        if (materialRequest == null) {
            LOGGER.error("No material request objects exists for id : " + materialRequest);
            throw new GloriaSystemException("This operation cannot be performed. No material request objects exists for id : " + materialRequestDTO.getId(),
                                            GloriaExceptionConstants.INVALID_DATASET_OID);
        }

        if (materialRequestDTO.getVersion() != materialRequest.getVersion()) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_UPDATION_OF_STALE_DATASET,
                                                 "This operation cannot be performed since the information seen in the page has already been updated.");
        }

        if (SEND.equals(action) || CANCEL.equals(action)) {
            UserDTO user = userServices.getUser(userId);
            String userName = user.getUserName();

            if (SEND.equals(action)) {
                MaterialRequestHelper.validateWBSProjectAndCompanyCodes(materialRequestDTO.getCompanyCode(), materialRequestDTO.getProjectId(),
                                                                        materialRequestDTO.getWbsCode(), commonServices);
                return MaterialRequestHelper.sendMaterialRequest(materialRequest, userId, userName, materialRequestRepository, materialRequestSender,
                                                                 commonServices);
            } else if (CANCEL.equals(action)) {
                return MaterialRequestHelper.cancelMaterialRequest(materialRequest, userId, userName, materialRequestCancelSender, materialRequestRepository);
            }
        } else if (REVERT.equals(action)) {
            return MaterialRequestHelper.revertMaterialRequestVersion(materialRequest, materialRequestRepository);
        } else if (NEW_VERSION.equals(action)) {
            return MaterialRequestHelper.createNewVersion(materialRequestDTO.getId(), materialRequestRepository);
        }

        return materialRequest;
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE','IT_SUPPORT')")
    public MaterialRequest findMaterialRequestById(long materialRequestOid, String action, String userId) throws GloriaApplicationException {
        if (!StringUtils.isEmpty(action) && "copy".equals(action)) {
            return MaterialRequestHelper.copyCreateMaterialRequest(materialRequestOid, userId, materialRequestRepository, userServices);
        }
        return materialRequestRepository.findById(materialRequestOid);
    }

    @Override
    @PreAuthorize("hasAnyRole('PROCURE','IT_SUPPORT')")
    public PageObject getMaterialRequests(PageObject pageObject, String userId) throws GloriaApplicationException {
        GloriaUser gloriaUser = teamRepository.findUserByUserId(pageObject.getPredicate("userId"));
        Set<String> allUserCompanyCodes = UserRoleHelper.getAllUserCompanyCodeCodes(gloriaUser, commonServices);
        return materialRequestRepository.getMaterialRequests(pageObject, new ArrayList<String>(allUserCompanyCodes));
    }

    @Override
    @Deprecated
    @PreAuthorize("hasAnyRole('PROCURE')")
    public List<MaterialRequestLine> updateMaterialRequestLines(List<MaterialRequestLineDTO> materialRequestLineDTOs, long materialRequestOid)
            throws GloriaApplicationException {

        MaterialRequest materialRequest = materialRequestRepository.findById(materialRequestOid);

        MaterialRequestStatus status = materialRequest.getCurrent().getStatus();

        for (MaterialRequestLineDTO materialRequestLineDTO : materialRequestLineDTOs) {
            if (materialRequestLineDTO.getId() == 0) {
                createMaterialRequestLine(materialRequestLineDTO, materialRequest.getMaterialRequestOid());
            } else {
                status.updateMaterialRequestLine(materialRequestLineDTO, materialRequestRepository, materialRequest, unitOfMeasureRepo);
            }
        }

        List<MaterialRequestLine> physicallyDeletedMaterialRequestLines = 
                MaterialRequestStatusHelper.findPhysicallyDeletedMaterialRequestLineOids(materialRequest, materialRequestLineDTOs);
        for (MaterialRequestLine physicallyDeletedMaterialRequestLine : physicallyDeletedMaterialRequestLines) {
            status.deleteMaterialRequestLine(physicallyDeletedMaterialRequestLine, materialRequestRepository, materialRequest.getCurrent());
        }

        return materialRequest.getCurrent().getMaterialRequestLines();
    }

    @Override
    @PreAuthorize("hasAnyRole('PROCURE','IT_SUPPORT')")
    public FileToExportDTO exportMaterialRequestParts(long materialRequestId) {
        MaterialRequest materialRequest = materialRequestRepository.findById(materialRequestId);
        FileToExportDTO exportDTO = new FileToExportDTO();
        if (materialRequest != null) {
            MaterialRequestVersion current = materialRequest.getCurrent();
            List<MaterialRequestLine> materialRequestLines = current.getMaterialRequestLines();
            if (materialRequestLines != null && !materialRequestLines.isEmpty()) {
                XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet sheet = workbook.createSheet();
                MaterialRequestHelper.writeRowCells(MaterialRequestHelper.evalHeader(materialRequest.getType()), sheet.createRow(0));
                for (int idx = 0; idx < materialRequestLines.size(); idx++) {
                    XSSFRow row = sheet.createRow(idx + 1);
                    MaterialRequestHelper.writeRowCells(MaterialRequestHelper.evalRow(materialRequest.getType(), materialRequestLines.get(idx)), row);
                }
                exportDTO.setContent(MaterialRequestHelper.getExcelBytes(workbook));
            }
            exportDTO.setName(current.getMtrlRequestVersion());
        }
        return exportDTO;
    }

    @Override
    @PreAuthorize("hasAnyRole('PROCURE')")
    public void uploadMaterialRequestDocuments(Long materialRequestId, DocumentDTO document) throws GloriaApplicationException {
        MaterialImportHandler materialImportHandler = null;
        if (document != null && document.getContent() != null) {
            MaterialRequest materialRequest = materialRequestRepository.findById(materialRequestId);
            InputStream is = new ByteArrayInputStream(document.getContent());
            if (materialRequest.getType() == MaterialRequestType.MULTIPLE) {
                materialImportHandler = new MultipleMaterialImportHandler(is);
            } else {
                materialImportHandler = new MaterialImportHandler(is);
            }

            List<MaterialRequestLineDTO> lineDTOs = new ArrayList<MaterialRequestLineDTO>();
            List<MaterialRequestLineDTO> materialLineDtos = MaterialRequestHelper.transformListOfMaterialRequestLineDTOs(
                                                                                      materialRequest.getCurrent().getMaterialRequestLines());
            lineDTOs.addAll(materialLineDtos);
            lineDTOs.addAll(materialImportHandler.manageExcel());
            updateMaterialRequestLines(lineDTOs, materialRequestId);

        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void changeMaterialRequestState(MaterialProcureResponseDTO materialProcureResponse) throws GloriaApplicationException {
        MaterialRequest materialRequest = materialRequestRepository.findMaterialRequestByMtrlRequestId(materialProcureResponse.getMaterialRequestId());
        if (materialRequest != null) {
            materialRequest.getCurrent().getStatus().changeState(materialRequest, materialRequestRepository, materialProcureResponse.getResponse());
        }
    }

    @Override
    @PreAuthorize("hasAnyRole('PROCURE')")
    public MaterialRequestLine createMaterialRequestLine(MaterialRequestLineDTO materialRequestLineDTO, long materialRequestOid)
            throws GloriaApplicationException {
        MaterialRequest materialRequest = materialRequestRepository.findById(materialRequestOid);
        return materialRequest.getCurrent().getStatus()
                              .createMaterialRequestLine(materialRequestLineDTO, materialRequestRepository, materialRequest, unitOfMeasureRepo);
    }

    @Override
    @PreAuthorize("hasAnyRole('PROCURE')")
    public MaterialRequestLine updateMaterialRequestLine(MaterialRequestLineDTO materialRequestLineDTO, long materialRequestOid)
            throws GloriaApplicationException {
        MaterialRequest materialRequest = materialRequestRepository.findById(materialRequestOid);
        return materialRequest.getCurrent().getStatus()
                              .updateMaterialRequestLine(materialRequestLineDTO, materialRequestRepository, materialRequest, unitOfMeasureRepo);
    }

    @Override
    @PreAuthorize("hasAnyRole('PROCURE')")
    public void deleteMaterialRequestLine(long materialRequestOid, String materialRequestLineIds) throws GloriaApplicationException {
        List<Long> materialRequestLineOids = GloriaFormateUtil.getValuesAsLong(materialRequestLineIds);
        MaterialRequest materialRequest = materialRequestRepository.findById(materialRequestOid);
        for (Long materialRequestLineOid : materialRequestLineOids) {
            MaterialRequestLine materialRequestLine = materialRequestRepository.findMaterialRequestLineById(materialRequestLineOid);
            materialRequest.getCurrent().getStatus().deleteMaterialRequestLine(materialRequestLine, materialRequestRepository, materialRequest.getCurrent());
        }
    }

    @Override
    public MaterialRequestLine undoRemoveMaterialRequestLine(MaterialRequestLineDTO materialRequestLineDTO, long materialRequestOid, long materialRequestLineOid) throws GloriaApplicationException {
        MaterialRequestLine materialRequestLine = materialRequestRepository.findMaterialRequestLineById(materialRequestLineOid);
        MaterialRequest materialRequest = materialRequestRepository.findById(materialRequestOid);
        return materialRequest.getCurrent().getStatus().undoRemoveMaterialRequestLine(materialRequestLine, materialRequestRepository, materialRequest.getCurrent());
    }
}
