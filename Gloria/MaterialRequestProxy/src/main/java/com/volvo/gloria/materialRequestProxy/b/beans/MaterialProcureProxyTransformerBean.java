package com.volvo.gloria.materialRequestProxy.b.beans;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.materialRequestProxy.b.MaterialProcureProxyTransformer;
import com.volvo.gloria.materialRequestProxy.c.MaterialProcureResponseDTO;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.gloria.util.xml.XmlTransformer;
import com.volvo.group.common._1_0.ApplicationAreaType;
import com.volvo.group.common._1_0.ReceiverType;
import com.volvo.group.common._1_0.SenderType;
import com.volvo.jvs.runtime.platform.ContainerManaged;
import com.volvo.materialprocureresponse._1_0.MaterialProcureResponseType;
import com.volvo.materialprocureresponse._1_0.ProcessMaterialProcureResponseType;
import com.volvo.materialprocureresponse._1_0.ProcessMaterialProcureResponseType.DataArea;

@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class MaterialProcureProxyTransformerBean extends XmlTransformer implements MaterialProcureProxyTransformer {
  
    private static final Logger LOGGER = LoggerFactory.getLogger(MaterialProcureProxyTransformerBean.class);
    
    public MaterialProcureProxyTransformerBean() {
        super(XmlConstants.SchemaFullPath.MATERIAL_PROCESS_RESPONSE, XmlConstants.PackageName.MATERIAL_PROCESS_RESPONSE);
    }

    @Override
    protected Object transformFromJAXBToDTO(Object jaxbOject) {
        return null;
    }

    @Override
    protected ProcessMaterialProcureResponseType transformFromDTOToJAXB(Object dto) {
        MaterialProcureResponseDTO materialProcureResponsesDTO = (MaterialProcureResponseDTO) dto;

        ProcessMaterialProcureResponseType processMaterialProcureResponseType = new ProcessMaterialProcureResponseType();

        ApplicationAreaType applicationAreaType = new ApplicationAreaType();

        SenderType senderType = new SenderType();

        senderType.setLogicalID("Gloria");
        senderType.setUserID(materialProcureResponsesDTO.getUserId());

        applicationAreaType.setSender(senderType);

        ReceiverType receiverType = new ReceiverType();
        receiverType.setLogicalID("Gloria");
        receiverType.setComponentID("MaterialRequest");

        try {
            XMLGregorianCalendar creationDateTime = DateUtil.getXMLGreorianCalendarWithTimeStamp(DateUtil.getCurrentUTCDateTime());
            applicationAreaType.setCreationDateTime(creationDateTime);
        } catch (DatatypeConfigurationException e) {
            throw new GloriaSystemException(e, "Material Procure Response creation date is having a problem.");
        }
        applicationAreaType.setBODID("");
        applicationAreaType.setReceiver(receiverType);
        processMaterialProcureResponseType.setApplicationArea(applicationAreaType);

        DataArea dataArea = new DataArea();
   

        MaterialProcureResponseType materialProcureResponseType = new MaterialProcureResponseType();
        materialProcureResponseType.setChangeTechId(materialProcureResponsesDTO.getChangeTechId());
        materialProcureResponseType.setMtrlRequestId(materialProcureResponsesDTO.getMaterialRequestId());
        materialProcureResponseType.setMtrlRequestVersion(materialProcureResponsesDTO.getChangeId());
        //TODO Set correct ChangeIdStatus
        materialProcureResponseType.setChangeIdStatus(materialProcureResponsesDTO.getResponse());
        dataArea.getMaterialProcureResponse().add(materialProcureResponseType);

        processMaterialProcureResponseType.setDataArea(dataArea);

        return processMaterialProcureResponseType;
    }

}
