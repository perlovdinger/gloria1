package com.volvo.gloria.materialRequestProxy.b.beans;

import static com.volvo.gloria.util.c.GloriaParams.APPLICATION_ID;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.materialRequestProxy.b.MaterialRequestTransformer;
import com.volvo.gloria.materialRequestProxy.c.FinanceMaterialDTO;
import com.volvo.gloria.materialRequestProxy.c.MaterialRequestLineTransformerDTO;
import com.volvo.gloria.materialRequestProxy.c.MaterialRequestToSendDTO;
import com.volvo.gloria.materialRequestProxy.c.MaterialRequestTransformerDTO;
import com.volvo.gloria.materialRequestProxy.c.MaterialRequestVersionTransformerDTO;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.Utils;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.gloria.util.xml.XmlTransformer;
import com.volvo.group.common._1_0.ApplicationAreaType;
import com.volvo.group.common._1_0.ProcessType;
import com.volvo.group.common._1_0.ReceiverType;
import com.volvo.group.common._1_0.SenderType;
import com.volvo.group.materialProcureRequest._1_0.ApprovalInfoType;
import com.volvo.group.materialProcureRequest._1_0.BomLinkType;
import com.volvo.group.materialProcureRequest._1_0.BuildSiteType;
import com.volvo.group.materialProcureRequest._1_0.ComponentType;
import com.volvo.group.materialProcureRequest._1_0.ContactPersonType;
import com.volvo.group.materialProcureRequest._1_0.FinancialInfoType;
import com.volvo.group.materialProcureRequest._1_0.HeaderType;
import com.volvo.group.materialProcureRequest._1_0.LineAddType;
import com.volvo.group.materialProcureRequest._1_0.LineRemoveType;
import com.volvo.group.materialProcureRequest._1_0.LineType;
import com.volvo.group.materialProcureRequest._1_0.MaterialControllerType;
import com.volvo.group.materialProcureRequest._1_0.MaterialRequestChangeType;
import com.volvo.group.materialProcureRequest._1_0.MaterialRequestChangeTypeAdd;
import com.volvo.group.materialProcureRequest._1_0.MaterialRequestChangeTypeRemove;
import com.volvo.group.materialProcureRequest._1_0.MaterialRequestHeaderType;
import com.volvo.group.materialProcureRequest._1_0.MaterialRequestMessageType;
import com.volvo.group.materialProcureRequest._1_0.PartType;
import com.volvo.group.materialProcureRequest._1_0.ProcessMaterialProcureRequestType;
import com.volvo.group.materialProcureRequest._1_0.ProcessMaterialProcureRequestType.DataArea;
import com.volvo.group.materialProcureRequest._1_0.RequesterType;
import com.volvo.group.materialProcureRequest._1_0.UnitOfMeasureType;
import com.volvo.jvs.runtime.platform.ContainerManaged;



/**
 * Transformer bean to convert the MaterialRequestSendDTO to xml message.
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class MaterialRequestTransformerBean extends XmlTransformer implements MaterialRequestTransformer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MaterialRequestTransformerBean.class);

    private static final String COMPONENT_ID = "SubSystem";

    public MaterialRequestTransformerBean() {
        super(XmlConstants.SchemaFullPath.PROCURE_TO_REQUEST_OBJECT, XmlConstants.PackageName.PROCURE_TO_REQUEST_OBJECT);
    }

    @Override
    protected Object transformFromJAXBToDTO(Object jaxbOject) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected ProcessMaterialProcureRequestType transformFromDTOToJAXB(Object dto) {
        MaterialRequestToSendDTO materialRequestToSendDTO = (MaterialRequestToSendDTO) dto;
        ProcessMaterialProcureRequestType processMaterialProcureRequestType = new ProcessMaterialProcureRequestType();
        
        ApplicationAreaType applicationAreaType = createApplicationAreaType(materialRequestToSendDTO.getMaterialRequestTransformerDtos());
        DataArea dataArea = createDataAreaType(materialRequestToSendDTO);
        
        processMaterialProcureRequestType.setApplicationArea(applicationAreaType);
        processMaterialProcureRequestType.setDataArea(dataArea);
        
        return processMaterialProcureRequestType;
    }

    private DataArea createDataAreaType(MaterialRequestToSendDTO materialRequestToSendDTO) {
        DataArea dataArea = new DataArea();
        ProcessType processType = new ProcessType();
        processType.setActionCriteria("ProcessRequest");
        dataArea.setProcess(processType);

        if (materialRequestToSendDTO.isCancelRequest()) {
            List<MaterialRequestChangeType> changeTypes = new ArrayList<MaterialRequestChangeType>();
            List<String> changeTechIds = new ArrayList<String>();
            for (String changeTechId : materialRequestToSendDTO.getChangeTechIds()) {
                if (!changeTechIds.contains(changeTechId)) {
                    MaterialRequestChangeType materialRequestChangeType = new MaterialRequestChangeType();
                    materialRequestChangeType.setRequestType(materialRequestToSendDTO.getRequestType());
                    MaterialRequestChangeTypeRemove materialRequestChangeTypeRemove = new MaterialRequestChangeTypeRemove();
                    materialRequestChangeTypeRemove.setChangeTechId(changeTechId);
                    materialRequestChangeTypeRemove.setMtrlRequestVersion(materialRequestToSendDTO.getMtrlRequestVersion());
                    materialRequestChangeType.setMaterialRequestChangeRemove(materialRequestChangeTypeRemove);
                    changeTypes.add(materialRequestChangeType);
                    dataArea.getMaterialRequestChange().add(materialRequestChangeType);
                    changeTechIds.add(changeTechId);
                }
            }
        } else {

            MaterialRequestChangeType materialRequestChangeType = new MaterialRequestChangeType();
            materialRequestChangeType.setRequestType(materialRequestToSendDTO.getRequestType());

            MaterialRequestChangeTypeAdd materialRequestChangeTypeAdd = new MaterialRequestChangeTypeAdd();
            materialRequestChangeTypeAdd.setId(materialRequestToSendDTO.getMaterialRequestId());
            materialRequestChangeTypeAdd.setChangeTechId(materialRequestToSendDTO.getChangeTechId());
            materialRequestChangeTypeAdd.setMtrlRequestVersion(materialRequestToSendDTO.getMtrlRequestVersion());
            materialRequestChangeTypeAdd.setType(materialRequestToSendDTO.getRequestType());

            materialRequestChangeType.setMaterialRequestChangeAdd(materialRequestChangeTypeAdd);

            for (MaterialRequestTransformerDTO materialRequestTransformerDTO : materialRequestToSendDTO.getMaterialRequestTransformerDtos()) {
                if (materialRequestTransformerDTO != null) {
                    MaterialRequestMessageType materialRequestMessageType = new MaterialRequestMessageType();

                    HeaderType headerType = createHeaderType(materialRequestTransformerDTO, materialRequestChangeTypeAdd);

                    ComponentType componentType = createComponentType(materialRequestTransformerDTO);

                    materialRequestMessageType.setHeader(headerType);
                    materialRequestMessageType.getComponent().add(componentType);
                    materialRequestChangeTypeAdd.getMaterialRequestMessage().add(materialRequestMessageType);
                }
            }
            dataArea.getMaterialRequestChange().add(materialRequestChangeType);
        }
        return dataArea;
    }

    private ComponentType createComponentType(MaterialRequestTransformerDTO materialRequestTransformerDTO) {
        ComponentType componentType = new ComponentType();
        
        BuildSiteType buildSiteType = new BuildSiteType();
        //outboundlocation Id??
        buildSiteType.setId(materialRequestTransformerDTO.getMtrlRequestVersionDTO().getOutboundLocationId());
        componentType.setBuildSiteType(buildSiteType);
        
        FinanceMaterialDTO financeMaterialDTO = materialRequestTransformerDTO.getFinanceMaterialDTO();
        for (MaterialRequestLineTransformerDTO mrLineTransformerDTO : materialRequestTransformerDTO.getMtrlRequestLineTransformerDTOs()) {
            LineType lineType = new LineType();
            if (mrLineTransformerDTO != null) {
                if (mrLineTransformerDTO.isRemoveType()) {
                    LineRemoveType lineRemoveType = new LineRemoveType();
                    lineRemoveType.setProcureLinkId(mrLineTransformerDTO.getProcureLinkId());
                    lineType.setLineRemoveType(lineRemoveType);
                } else {
                    LineAddType lineAddType = new LineAddType();
                    BomLinkType bomLinkType = new BomLinkType();
                    bomLinkType.setProcureLinkId(mrLineTransformerDTO.getProcureLinkId());
                    bomLinkType.setPartAffiliation(mrLineTransformerDTO.getPartAffiliation());
                    bomLinkType.setPartNumber(mrLineTransformerDTO.getPartNumber());
                    bomLinkType.setPartVersion(mrLineTransformerDTO.getPartVersion());
                    bomLinkType.setPartModification(mrLineTransformerDTO.getPartModification());
                    if (mrLineTransformerDTO.getQuantity() > 0) {
                        Long qty = (Long) mrLineTransformerDTO.getQuantity();
                        bomLinkType.setQuantity(qty.intValue());
                    }
                    bomLinkType.setProductClassId(mrLineTransformerDTO.getProductClass());
                    bomLinkType.setFunctionGroup(mrLineTransformerDTO.getFunctionGroup());
                    lineAddType.setBomLink(bomLinkType);

                    PartType partType = new PartType();
                    // partName??
                    partType.setName(mrLineTransformerDTO.getPartName());
                  
                    UnitOfMeasureType uomType =   UnitOfMeasureType.fromValue(mrLineTransformerDTO.getUnitOfMeasure().toUpperCase());
                                      
                    partType.setUnitOfMeasure(uomType);
                    lineAddType.setPart(partType);

                    FinancialInfoType financialInfoType = new FinancialInfoType();
                    financialInfoType.setProjectId(financeMaterialDTO.getProjectId());
                    financialInfoType.setCompanyCode(financeMaterialDTO.getCompanyCode());
                    financialInfoType.setCostCenter(financeMaterialDTO.getCostCenter());
                    financialInfoType.setGlAccount(financeMaterialDTO.getGlAccount());
                    financialInfoType.setInternalOrderNoSAP(financeMaterialDTO.getInternalOrderNoSAP());
                    financialInfoType.setWbsCode(financeMaterialDTO.getWbsCode());

                    MaterialRequestVersionTransformerDTO materialRequestVersionDTO = materialRequestTransformerDTO.getMtrlRequestVersionDTO();
                    if (materialRequestVersionDTO.getRequiredStaDate() != null) {
                        try {
                            lineAddType.setRequiredShipToArrive(DateUtil.getXMLGreorianCalendar(materialRequestVersionDTO.getRequiredStaDate()));
                        } catch (DatatypeConfigurationException e) {
                            LOGGER.error(e.getMessage(), e);
                        }
                    }
                    lineAddType.setFinancialInfo(financialInfoType);
                    lineType.setLineAddType(lineAddType);
                }
            }
            componentType.getLine().add(lineType);
        }
        return componentType;
    }

    private HeaderType createHeaderType(MaterialRequestTransformerDTO materialRequestTransformerDTO,
                                        MaterialRequestChangeTypeAdd materialRequestChangeTypeAdd) {
        HeaderType headerType = new HeaderType();
        MaterialRequestHeaderType materialRequestHeaderType = new MaterialRequestHeaderType();
        if (materialRequestTransformerDTO.getReferenceId() != null) {
            materialRequestHeaderType.setReferenceId(materialRequestTransformerDTO.getReferenceId());            
        }
        materialRequestHeaderType.setReferenceGroup(materialRequestTransformerDTO.getReferenceGroup());
        MaterialRequestVersionTransformerDTO materialRequestVersionDTO = materialRequestTransformerDTO.getMtrlRequestVersionDTO();
        materialRequestHeaderType.setOutboundLocationId(materialRequestVersionDTO.getOutboundLocationId());

        Date outboundStartDate = materialRequestVersionDTO.getOutboundStartDate();
        if (outboundStartDate != null) {
            try {
                XMLGregorianCalendar xmlGreorianCalendar = DateUtil.getXMLGreorianCalendar(outboundStartDate);
                materialRequestHeaderType.setOutboundStartDate(xmlGreorianCalendar);
            } catch (DatatypeConfigurationException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        Date requiredStaDate = materialRequestVersionDTO.getRequiredStaDate();
        if (requiredStaDate != null) {
            try {
                materialRequestHeaderType.setRequiredShipToArrive(DateUtil.getXMLGreorianCalendar(requiredStaDate));
            } catch (DatatypeConfigurationException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        ApprovalInfoType approvalInfoType = new ApprovalInfoType();
        approvalInfoType.setMailformId(materialRequestVersionDTO.getMailFormId());
        approvalInfoType.setApprovalAmount(materialRequestVersionDTO.getApprovalAmount());
        approvalInfoType.setApprovalCurrency(materialRequestVersionDTO.getApprovalCurrency());
        materialRequestChangeTypeAdd.setApprovalInfo(approvalInfoType);
        
        ContactPersonType contactPersonType = new ContactPersonType();
        contactPersonType.setUserId(materialRequestTransformerDTO.getContactPersonUserId());
        contactPersonType.setName(materialRequestTransformerDTO.getContactPersonName());
        
        RequesterType requesterType = new RequesterType();
        requesterType.setUserId(materialRequestTransformerDTO.getRequesterId());
        requesterType.setName(materialRequestTransformerDTO.getRequesterName());
        
        MaterialControllerType materialControllerType = new MaterialControllerType();
        materialControllerType.setUserId(materialRequestTransformerDTO.getMaterialControllerUserId());
        materialControllerType.setName(materialRequestTransformerDTO.getMaterialControllerName());
        
        headerType.setMaterialController(materialControllerType);
        headerType.setMaterialRequestHeader(materialRequestHeaderType);
        headerType.setContactPerson(contactPersonType);
        headerType.setRequester(requesterType);
        return headerType;
    }
    
    private ApplicationAreaType createApplicationAreaType(List<MaterialRequestTransformerDTO> materialRequestTransformerDTOs) {
        ApplicationAreaType applicationAreaType = new ApplicationAreaType();

        SenderType senderType = new SenderType();
        senderType.setLogicalID(APPLICATION_ID);
        senderType.setComponentID(COMPONENT_ID);

        if (materialRequestTransformerDTOs != null && !materialRequestTransformerDTOs.isEmpty()) {
            senderType.setUserID(materialRequestTransformerDTOs.get(0).getRequesterId());
        }

        ReceiverType receiverType = new ReceiverType();
        receiverType.setLogicalID(APPLICATION_ID);
        receiverType.setComponentID(COMPONENT_ID);

        applicationAreaType.setSender(senderType);
        applicationAreaType.setReceiver(receiverType);

        try {
            applicationAreaType.setCreationDateTime(DateUtil.getXMLGreorianCalendarWithTimeStamp(DateUtil.getCurrentUTCDateTime()));
        } catch (DatatypeConfigurationException e) {
            LOGGER.error(e.getMessage(), e);
        }
        // random String
        applicationAreaType.setBODID(Utils.createRandomString());
        return applicationAreaType;
    }
}
