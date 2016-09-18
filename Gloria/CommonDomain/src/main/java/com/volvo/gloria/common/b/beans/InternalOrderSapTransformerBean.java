package com.volvo.gloria.common.b.beans;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.common.b.InternalOrderSapTransformer;
import com.volvo.gloria.common.c.dto.InternalOrderSapDTO;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.gloria.util.xml.XmlTransformer;
import com.volvo.group.init.internalOrderSap._1_0.InternalOrderSaps;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * Service Implementation for InternalOrderSapTransfomer.
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class InternalOrderSapTransformerBean extends XmlTransformer implements InternalOrderSapTransformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(InternalOrderSapTransformerBean.class);

    public InternalOrderSapTransformerBean() {
        super(XmlConstants.SchemaFullPath.INIT_INTERNALORDERSAP, XmlConstants.PackageName.INIT_INTERNALORDERSAP);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<InternalOrderSapDTO> transformInterOrderSap(String receivedInternalOrderSapMessage) {
        try {
            return (List<InternalOrderSapDTO>) transform(receivedInternalOrderSapMessage);
        } catch (JAXBException e) {
            // Throw an exception to roll-back the transaction.
            LOGGER.error("Unable to unmarshall the received message to a InternalOrderSapDTOs object, message will be discarded. Exception is:", e);
            throw new GloriaSystemException(e, "Unable to unmarshall the received message to a InternalOrderSapDTOs object, message will be discarded");
        }
    }

    @Override
    protected Object transformFromJAXBToDTO(Object jaxbOject) {
        InternalOrderSaps internalOrderSaps = (InternalOrderSaps) jaxbOject;

        List<InternalOrderSapDTO> internalOrderSapDTOs = new ArrayList<InternalOrderSapDTO>();
        for (com.volvo.group.init.internalOrderSap._1_0.InternalOrderSap internalOrderSap : internalOrderSaps.getInternalOrderSap()) {
            InternalOrderSapDTO internalOrderSapDTO = new InternalOrderSapDTO();
            internalOrderSapDTO.setCode(internalOrderSap.getCode());
            internalOrderSapDTOs.add(internalOrderSapDTO);
        }
        return internalOrderSapDTOs;
    }

    @Override
    protected Object transformFromDTOToJAXB(Object dto) {
        // TODO Auto-generated method stub
        return null;
    }
}
