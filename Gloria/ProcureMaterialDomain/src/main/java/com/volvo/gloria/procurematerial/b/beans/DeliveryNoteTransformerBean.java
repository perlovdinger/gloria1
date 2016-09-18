package com.volvo.gloria.procurematerial.b.beans;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.procurematerial.b.DeliveryNoteTransformer;
import com.volvo.gloria.procurematerial.c.dto.DeliveryNoteDTO;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.gloria.util.xml.XmlTransformer;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * Implementation of the Delivery Note transformer.
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class DeliveryNoteTransformerBean extends XmlTransformer implements DeliveryNoteTransformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryNoteTransformerBean.class);

    public DeliveryNoteTransformerBean() {
        super(XmlConstants.SchemaFullPath.INIT_DELIVERY_NOTE, XmlConstants.PackageName.INIT_DELIVERYNOTE);
    }
    
    @Override
    protected Object transformFromDTOToJAXB(Object dto) {
        return null;
    }

    @Override
    protected Object transformFromJAXBToDTO(Object jaxbOject) {
        com.volvo.group.init.deliveryNote._1_0.DeliveryNotes deliveryNotes = (com.volvo.group.init.deliveryNote._1_0.DeliveryNotes) jaxbOject;
        List<DeliveryNoteDTO> deliveryNoteDTOs = new ArrayList<DeliveryNoteDTO>();
        for (com.volvo.group.init.deliveryNote._1_0.DeliveryNote deliveryNote : deliveryNotes.getDeliveryNote()) {
            DeliveryNoteDTO deliveryNoteDTO = new DeliveryNoteDTO();
            deliveryNoteDTO.setOrderNo(deliveryNote.getOrderNo());
            deliveryNoteDTO.setDeliveryNoteNo(deliveryNote.getDeliveryNoteNo());
            deliveryNoteDTO.setSupplierId(deliveryNote.getSupplierId());
            deliveryNoteDTO.setSupplierName(deliveryNote.getSupplierName());
            deliveryNoteDTO.setCarrier(deliveryNote.getCarrier());
            deliveryNoteDTO.setReceiveType(deliveryNote.getReceiveType());
            deliveryNoteDTO.setTransportationNo(deliveryNote.getTransportationNo());
            deliveryNoteDTO.setDeliveryNoteDate(deliveryNoteDTO.getDeliveryNoteDate());
            deliveryNoteDTOs.add(deliveryNoteDTO);
        }
        return deliveryNoteDTOs;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DeliveryNoteDTO> transformDeliveryNote(String xmlContent) {
        try {
            return (List<DeliveryNoteDTO>) transform(xmlContent);
        } catch (JAXBException e) {
            // Throw an exception to roll-back the transaction.
            LOGGER.error("Unable to unmarshall the received message to a DeliveryNote object, message will be discarded. Exception is:", e);
            throw new GloriaSystemException(e, "Unable to unmarshall the received message to a DeliveryNoteDTOs object, message will be discarded");
        }
    }
}
