package com.volvo.gloria.procurematerial.b.beans;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.procure.c.dto.ProcureDTO;
import com.volvo.gloria.procurematerial.b.ProcureTransformer;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.gloria.util.xml.XmlTransformer;
import com.volvo.group.init.procure._1_0.Procure;
import com.volvo.group.init.procure._1_0.ProcureDetail;
import com.volvo.group.init.procure._1_0.ProcureOrder;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * Service Implmentations for ProcureTransformer.
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ProcureTransformerBean extends XmlTransformer implements ProcureTransformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseOrderTransformerBean.class);

    public ProcureTransformerBean() {
        super(XmlConstants.SchemaFullPath.INIT_PROCURE, XmlConstants.PackageName.INIT_PROCURE);
    }

    @Override
    public ProcureDTO transformProcure(String receivedProcureMessage) {
        try {
            return (ProcureDTO) transform(receivedProcureMessage);
        } catch (JAXBException e) {
            // Throw an exception to roll-back the transaction.
            LOGGER.error("Unable to unmarshall the received message to a ProcureDTO object, message will be discarded. Exception is:", e);
            throw new GloriaSystemException(e, "Unable to unmarshall the received message to a ProcureDTO object, message will be discarded");
        }
    }

    @Override
    protected Object transformFromJAXBToDTO(Object jaxbOject) {
        Procure procure = (Procure) jaxbOject;
        ProcureDTO procureDTO = new ProcureDTO();

        ProcureDetail procureDetail = procure.getProcureDetail();
        procureDTO.setAssignedMaterialController(procureDetail.getAssignedMaterialController());
        procureDTO.setShipToId(procureDetail.getShipToId());
        procureDTO.setFinalWarehouseId(procureDetail.getFinalWarehouseId());
        procureDTO.setRequiredSTADate(procureDetail.getRequiredStaDate().toGregorianCalendar().getTime());
        procureDTO.setUnitPrice(procureDetail.getUnitPrice());
        procureDTO.setCurrency(procureDetail.getCurrency());
        procureDTO.setAdditionalQuantity(procureDetail.getAdditionalQuantity());
        
        ProcureOrder procureOrder = procure.getProcureOrder();
        procureDTO.setSupplierId(procureOrder.getSupplierId());
        procureDTO.setBuyerCode(procureOrder.getBuyerCode());
        procureDTO.setMaxPrice(procureOrder.getMaxPrice());
        procureDTO.setMaterialUserCategory(procureOrder.getMaterialUserCategory());
        procureDTO.setMaterialUserName(procureOrder.getMaterialUserName());
        procureDTO.setUnitOfMeasure(procureOrder.getUnitOfMeasure());

        return procureDTO;
    }

    @Override
    protected Object transformFromDTOToJAXB(Object dto) {
        // TODO Auto-generated method stub
        return null;
    }

}
