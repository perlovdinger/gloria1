package com.volvo.gloria.materialrequest.b.beans;

import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXBException;

import com.volvo.gloria.materialRequestProxy.c.MaterialProcureResponseDTO;
import com.volvo.gloria.materialrequest.b.MaterialProcureGatewayTranformer;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.gloria.util.xml.XmlTransformer;
import com.volvo.jvs.runtime.platform.ContainerManaged;
import com.volvo.materialprocureresponse._1_0.MaterialProcureResponseType;
import com.volvo.materialprocureresponse._1_0.ProcessMaterialProcureResponseType;
import com.volvo.materialprocureresponse._1_0.ProcessMaterialProcureResponseType.DataArea;

@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class MaterialProcureGatewayTransformerBean extends XmlTransformer implements MaterialProcureGatewayTranformer {

    public MaterialProcureGatewayTransformerBean() {
        super(XmlConstants.SchemaFullPath.MATERIAL_PROCESS_RESPONSE, XmlConstants.PackageName.MATERIAL_PROCESS_RESPONSE);
    }

    @Override
    public MaterialProcureResponseDTO transformMaterialProcureResponse(String receivedMaterialProcureResponse) {
        try {
            return (MaterialProcureResponseDTO) transform(receivedMaterialProcureResponse);
        } catch (JAXBException e) {
            throw new GloriaSystemException(e, "Unable to unmarshall the received message to a MaterialProcureResponseDTO object, message will be discarded.");
        }
    }

    @Override
    protected Object transformFromJAXBToDTO(Object jaxbOject) {
        MaterialProcureResponseDTO materialProcureResponseDTO = new MaterialProcureResponseDTO();
        ProcessMaterialProcureResponseType processMaterialProcureResponseType = (ProcessMaterialProcureResponseType) jaxbOject;
        
        DataArea dataArea = processMaterialProcureResponseType.getDataArea();
        List<MaterialProcureResponseType> materialProcureResponseTypes = dataArea.getMaterialProcureResponse();
        MaterialProcureResponseType materialProcureResponseType = materialProcureResponseTypes.get(0);
        materialProcureResponseDTO.setChangeId(materialProcureResponseType.getMtrlRequestVersion());
        materialProcureResponseDTO.setMaterialRequestId(materialProcureResponseType.getMtrlRequestId());
        materialProcureResponseDTO.setResponse(materialProcureResponseType.getChangeIdStatus());
        return materialProcureResponseDTO;
    }

    @Override
    protected Object transformFromDTOToJAXB(Object dto) {
        return null;
    }

}
