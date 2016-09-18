package com.volvo.gloria.procurematerial.util;

import java.io.IOException;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.procurematerial.b.DeliveryServices;
import com.volvo.gloria.procurematerial.c.dto.DeliveryNoteDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.IOUtil;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.gloria.util.xml.XmlTransformer;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * Init data for DeliveryNote.
 */
public class InitDataForDeliveryNote extends XmlTransformer {

    private static final String DELIVERYNOTE_PROPERTY_KEY = "deliveryNote.data";
    private static final String DELIVERYNOTE_DATA_CP = "testdata/deliveryNote/DeliveryNote.xml";
    private static DeliveryServices deliveryServices = ServiceLocator.getService(DeliveryServices.class);

    private static final Logger LOGGER = LoggerFactory.getLogger(InitDataForDeliveryNote.class);

    private String message;

    public InitDataForDeliveryNote(String xmlFilePath) throws IOException {
        super(XmlConstants.SchemaFullPath.INIT_DELIVERY_NOTE, XmlConstants.PackageName.INIT_DELIVERYNOTE);
        message = IOUtil.getStringFromClasspath(xmlFilePath);
    }

    public InitDataForDeliveryNote(Properties testDataProperties) throws IOException {
        super(XmlConstants.SchemaFullPath.INIT_DELIVERY_NOTE, XmlConstants.PackageName.INIT_DELIVERYNOTE);
        if (testDataProperties.containsKey(DELIVERYNOTE_PROPERTY_KEY)) {
            message = IOUtil.getStringFromFileSystem(testDataProperties.getProperty(DELIVERYNOTE_PROPERTY_KEY));
        } else {
            message = IOUtil.getStringFromClasspath(DELIVERYNOTE_DATA_CP);
        }
    }

    public void initDeliveryNotes() throws JAXBException, IOException {
        Object object = transformXmlToJaxb(message);

        com.volvo.group.init.deliveryNote._1_0.DeliveryNotes deliveryNotes = (com.volvo.group.init.deliveryNote._1_0.DeliveryNotes) object;

        for (com.volvo.group.init.deliveryNote._1_0.DeliveryNote deliveryNote : deliveryNotes.getDeliveryNote()) {
            DeliveryNoteDTO deliveryNoteDTO = new DeliveryNoteDTO();
            deliveryNoteDTO.setOrderNo(deliveryNote.getOrderNo());
            deliveryNoteDTO.setDeliveryNoteNo(deliveryNote.getDeliveryNoteNo());
            deliveryNoteDTO.setSupplierId(deliveryNote.getSupplierId());
            deliveryNoteDTO.setSupplierName(deliveryNote.getSupplierName());
            deliveryNoteDTO.setCarrier(deliveryNote.getCarrier());
            deliveryNoteDTO.setTransportationNo(deliveryNote.getTransportationNo());
            deliveryNoteDTO.setDeliveryNoteDate(deliveryNoteDTO.getDeliveryNoteDate());
            try {
                deliveryServices.createOrUpdateDeliveryNote(deliveryNoteDTO);
            } catch (GloriaApplicationException gae) {
                LOGGER.error("InitDataDeliveryNote failed to process xml : " + gae.getErrorMessage());
            }
        }
    }

    @Override
    protected Object transformFromJAXBToDTO(Object jaxbOject) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Object transformFromDTOToJAXB(Object dto) {
        // TODO Auto-generated method stub
        return null;
    }

}
