package com.volvo.gloria.common.b.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.common.b.UnitOfMeasureTransformer;
import com.volvo.gloria.common.c.dto.UnitOfMeasureDTO;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.gloria.util.xml.XmlTransformer;
import com.volvo.group.init.unitOfMeasure._1_0.UnitOfMeasures;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * UOM message transformer service implementation.
 * 
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class UnitOfMeasureTransformerBean extends XmlTransformer implements UnitOfMeasureTransformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(UnitOfMeasureTransformerBean.class);

    public UnitOfMeasureTransformerBean() throws IOException {
        super(XmlConstants.SchemaFullPath.INIT_UNIT_OF_MEASURE, XmlConstants.PackageName.INIT_UNIT_OF_MEASURE);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<UnitOfMeasureDTO> transformUnitOfMeasure(String xmlContent) {
        try {
            return (List<UnitOfMeasureDTO>) transform(xmlContent);
        } catch (JAXBException e) {
            // Throw an exception to roll-back the transaction.
            LOGGER.error("Unable to unmarshall the received message to a UnitOfMeasureDTOs object, message will be discarded. Exception is:", e);
            throw new GloriaSystemException(e, "Unable to unmarshall the received message to a UnitOfMeasureDTOs object, message will be discarded");
        }
    }

    @Override
    protected Object transformFromJAXBToDTO(Object jaxbOject) {
        List<UnitOfMeasureDTO> unitOfMeasureDTOs = new ArrayList<UnitOfMeasureDTO>();
        UnitOfMeasures unitOfMeasures = (UnitOfMeasures) jaxbOject;

        for (com.volvo.group.init.unitOfMeasure._1_0.UnitOfMeasure unitOfMeasure : unitOfMeasures.getUnitOfMeasure()) {
            UnitOfMeasureDTO unitOfMeasureDTO = new UnitOfMeasureDTO();
            unitOfMeasureDTO.setCode(unitOfMeasure.getCode());
            unitOfMeasureDTO.setName(unitOfMeasure.getName());
            unitOfMeasureDTO.setGloriaCode(unitOfMeasure.isGloriaCode());
            Integer displaySeq = unitOfMeasure.getDisplaySeq();
            if (displaySeq != null) {
                unitOfMeasureDTO.setDisplaySeq(displaySeq.toString());
            } else {
                unitOfMeasureDTO.setDisplaySeq(null);
            }
            unitOfMeasureDTOs.add(unitOfMeasureDTO);
        }
        return unitOfMeasureDTOs;
    }

    @Override
    protected Object transformFromDTOToJAXB(Object dto) {
        return null;
    }

}
