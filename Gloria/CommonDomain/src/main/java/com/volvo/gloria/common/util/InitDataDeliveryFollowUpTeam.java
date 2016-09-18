package com.volvo.gloria.common.util;

import java.io.IOException;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.FollowUpType;
import com.volvo.gloria.common.d.entities.SupplierCounterPart;
import com.volvo.gloria.util.IOUtil;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * Init Data for DeliveryFollowUpTeam.
 */
public class InitDataDeliveryFollowUpTeam extends InitDataBase {
    private static final String FILE_NAME = "DeliveryFollowUpTeam.xml";
    private static final String FILE_TYPE = "common";
    private static final String DELIVERY_FOLLOW_UP_TEAM_DATA_PROPERTY_KEY = "deliveryFollowUpTeam.data";
    private String message;

    public InitDataDeliveryFollowUpTeam(String xmlFilePath) throws IOException {
        super(XmlConstants.SchemaFullPath.INIT_DELIVERY_FOLLOW_UP_TEAM, XmlConstants.PackageName.INIT_DELIVERY_FOLLOW_UP_TEAM);
        message = IOUtil.getStringFromClasspath(xmlFilePath);
    }

    public InitDataDeliveryFollowUpTeam(Properties testDataProperties, String env,String fileType) throws IOException {
        super(XmlConstants.SchemaFullPath.INIT_DELIVERY_FOLLOW_UP_TEAM, XmlConstants.PackageName.INIT_DELIVERY_FOLLOW_UP_TEAM);
        if (fileType == null) {
            fileType = FILE_TYPE;
        }
        if (testDataProperties.containsKey(DELIVERY_FOLLOW_UP_TEAM_DATA_PROPERTY_KEY)) {
            message = IOUtil.getStringFromFileSystem(testDataProperties.getProperty(DELIVERY_FOLLOW_UP_TEAM_DATA_PROPERTY_KEY));
        } else {
            message = getXml(env, fileType, FILE_NAME);
        }
    }

    public void initDeliveryFollowUpTeam() throws JAXBException, IOException {
        Object object = transformXmlToJaxb(message);
        CommonServices service = ServiceLocator.getService(CommonServices.class);
        com.volvo.group.init.deliveryFollowUpTeam._1_0.DeliveryFollowUpTeams deliveryFollowUpTeams;
        deliveryFollowUpTeams = (com.volvo.group.init.deliveryFollowUpTeam._1_0.DeliveryFollowUpTeams) object;

        for (com.volvo.group.init.deliveryFollowUpTeam._1_0.DeliveryFollowUpTeams.DeliveryFollowUpTeam deliveryFollowUpTeam : 
                                                                                            deliveryFollowUpTeams.getDeliveryFollowUpTeam()) {
            com.volvo.gloria.common.d.entities.DeliveryFollowUpTeam deliveryFollowUpTeamEntity = new com.volvo.gloria.common.d.entities.DeliveryFollowUpTeam();
            deliveryFollowUpTeamEntity.setName(deliveryFollowUpTeam.getName());
            deliveryFollowUpTeamEntity.setFollowUpType(FollowUpType.valueOf(deliveryFollowUpTeam.getFollowUpType()));
            com.volvo.gloria.common.d.entities.DeliveryFollowUpTeam deliveryFollowUpteam = service.addDeliveryFollowUpTeam(deliveryFollowUpTeamEntity);
            for (com.volvo.group.init.deliveryFollowUpTeam._1_0.DeliveryFollowUpTeams.DeliveryFollowUpTeam.SupplierCounterPart supplierCounterPart : 
                                                                                                deliveryFollowUpTeam.getSupplierCounterPart()) {
                SupplierCounterPart supplierPartEntity = new SupplierCounterPart();
                supplierPartEntity.setShipToId(supplierCounterPart.getShipToId());
                supplierPartEntity.setPpSuffix(supplierCounterPart.getPpSuffix());
                supplierPartEntity.setTransitAddressId(supplierCounterPart.getTransitAddressId());
                supplierPartEntity.setDomesticInvoiceId(supplierCounterPart.getDomesticInvoiceId());
                supplierPartEntity.setInternationalInvoiceId(supplierCounterPart.getInternationalInvoiceId());
                supplierPartEntity.setCompanyCode(supplierCounterPart.getCompanyCode());
                supplierPartEntity.setComment(supplierCounterPart.getComment());
                supplierPartEntity.setMaterialUserId(supplierCounterPart.getMaterialUserId());
                if (supplierCounterPart.isDisabledProcure() != null) {
                    supplierPartEntity.setDisabledProcure(supplierCounterPart.isDisabledProcure());
                }
                supplierPartEntity.setDeliveryFollowUpTeam(service.findDeliveryFollowUpTeam(deliveryFollowUpteam.getDeliveryFollowUpTeamOid()));
                service.addSupplierCounterPart(supplierPartEntity);
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
