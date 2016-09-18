package com.volvo.gloria.common.b.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.common.b.DeliveryFollowUpTeamTransformer;
import com.volvo.gloria.common.c.FollowUpType;
import com.volvo.gloria.common.c.dto.DeliveryFollowUpTeamTransformerDTO;
import com.volvo.gloria.common.c.dto.SupplierCounterPartDTO;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.gloria.util.xml.XmlTransformer;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * DeliveryFollowUpTeam message transformer service implementation.
 * 
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class DeliveryFollowUpTeamTransformerBean extends XmlTransformer implements DeliveryFollowUpTeamTransformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryFollowUpTeamTransformerBean.class);

    public DeliveryFollowUpTeamTransformerBean() throws IOException {
        super(XmlConstants.SchemaFullPath.INIT_DELIVERY_FOLLOW_UP_TEAM, XmlConstants.PackageName.INIT_DELIVERY_FOLLOW_UP_TEAM);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<DeliveryFollowUpTeamTransformerDTO> transformDeliveryFollowUpTeam(String xmlContent) {

        try {
            return (List<DeliveryFollowUpTeamTransformerDTO>) transform(xmlContent);
        } catch (JAXBException e) {
            // Throw an exception to roll-back the transaction.
            LOGGER.error("Unable to unmarshall the received message to a DeliveryFollowUpTeamTransformerDTOs object,"
                    + " message will be discarded. Exception is:", e);
            throw new GloriaSystemException(e, "Unable to unmarshall the received message to a DeliveryFollowUpTeamTransformerDTOs object,"
                    + " message will be discarded");
        }

    }

    @Override
    protected Object transformFromJAXBToDTO(Object jaxbOject) {
        com.volvo.group.init.deliveryFollowUpTeam._1_0.DeliveryFollowUpTeams deliveryFollowUpTeams = 
                (com.volvo.group.init.deliveryFollowUpTeam._1_0.DeliveryFollowUpTeams) jaxbOject;
        List<DeliveryFollowUpTeamTransformerDTO> deliveryFollowUpTeamTransformerDTOs = new ArrayList<DeliveryFollowUpTeamTransformerDTO>();
        for (com.volvo.group.init.deliveryFollowUpTeam._1_0.DeliveryFollowUpTeams.DeliveryFollowUpTeam deliveryFollowUpTeam 
                : deliveryFollowUpTeams.getDeliveryFollowUpTeam()) {
           DeliveryFollowUpTeamTransformerDTO deliveryFollowUpTeamDTO = new DeliveryFollowUpTeamTransformerDTO();
            deliveryFollowUpTeamDTO.setName(deliveryFollowUpTeam.getName());
            deliveryFollowUpTeamDTO.setFollowUpType(FollowUpType.valueOf(deliveryFollowUpTeam.getFollowUpType()));
            List<SupplierCounterPartDTO> supplierCounterPartDTOs = new ArrayList<SupplierCounterPartDTO>();
            for (com.volvo.group.init.deliveryFollowUpTeam._1_0.DeliveryFollowUpTeams.DeliveryFollowUpTeam.SupplierCounterPart supplierCounterPart 
                    : deliveryFollowUpTeam.getSupplierCounterPart()) {
                SupplierCounterPartDTO supplierPartDTO = new SupplierCounterPartDTO();
                supplierPartDTO.setShipToId(supplierCounterPart.getShipToId());
                supplierPartDTO.setPpSuffix(supplierCounterPart.getPpSuffix());
                supplierPartDTO.setTransitAddressId(supplierCounterPart.getTransitAddressId());
                supplierPartDTO.setDomesticInvoiceId(supplierCounterPart.getDomesticInvoiceId());
                supplierPartDTO.setInternationalInvoiceId(supplierCounterPart.getInternationalInvoiceId());
                supplierPartDTO.setComment(supplierCounterPart.getComment());
                supplierPartDTO.setMaterialUserId(supplierCounterPart.getMaterialUserId());
                supplierPartDTO.setCompanyCode(supplierCounterPart.getCompanyCode());
                supplierCounterPartDTOs.add(supplierPartDTO);
            }
            deliveryFollowUpTeamDTO.setSupplierCounterPartDTOs(supplierCounterPartDTOs);
            deliveryFollowUpTeamTransformerDTOs.add(deliveryFollowUpTeamDTO);
        }
        return deliveryFollowUpTeamTransformerDTOs;
    }

    @Override
    protected Object transformFromDTOToJAXB(Object dto) {
        return null;
    }

}
