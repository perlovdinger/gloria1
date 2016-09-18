package com.volvo.gloria.common.b.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.common.b.PartAffiliationTransformer;
import com.volvo.gloria.common.c.dto.PartAffiliationDTO;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.gloria.util.xml.XmlTransformer;
import com.volvo.group.init.partAffiliation._1_0.PartAffiliations;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * Part affiliation message transformer service implementation.
 * 
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class PartAffiliationTransformerBean extends XmlTransformer implements PartAffiliationTransformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(PartAffiliationTransformerBean.class);

    public PartAffiliationTransformerBean() throws IOException {
        super(XmlConstants.SchemaFullPath.INIT_PART_AFFILIATION, XmlConstants.PackageName.INIT_PART_AFFILIATION);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PartAffiliationDTO> transformPartAffiliation(String xmlContent) {
        try {
            return (List<PartAffiliationDTO>) transform(xmlContent);
        } catch (JAXBException e) {
            // Throw an exception to roll-back the transaction.
            LOGGER.error("Unable to unmarshall the received message to a PartAffiliationDTOs object, message will be discarded. Exception is:", e);
            throw new GloriaSystemException(e, "Unable to unmarshall the received message to a PartAffiliationDTOs object, message will be discarded");
        }
    }

    @Override
    protected Object transformFromJAXBToDTO(Object jaxbOject) {
        PartAffiliations partAffiliations = (PartAffiliations) jaxbOject;
        List<PartAffiliationDTO> partAffiliationDTOs = new ArrayList<PartAffiliationDTO>();
        for (com.volvo.group.init.partAffiliation._1_0.PartAffiliation partAffiliation : partAffiliations.getPartAffiliation()) {
            PartAffiliationDTO partAffiliationDto = new PartAffiliationDTO();
            partAffiliationDto.setCode(partAffiliation.getCode());
            partAffiliationDto.setName(partAffiliation.getName());
            Integer displaySeq = partAffiliation.getDisplaySeq();
            partAffiliationDto.setRequestable(partAffiliation.isRequestable());
            if (displaySeq != null) {
                partAffiliationDto.setDisplaySeq(displaySeq.toString());
            } else {
                partAffiliationDto.setDisplaySeq(null);
            }
            partAffiliationDTOs.add(partAffiliationDto);
        }

        return partAffiliationDTOs;
    }

    @Override
    protected Object transformFromDTOToJAXB(Object dto) {
        return null;
    }

}
