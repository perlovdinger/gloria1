package com.volvo.gloria.procurematerial.util;

import java.io.IOException;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import com.volvo.gloria.common.util.InitDataBase;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.d.entities.Buyer;
import com.volvo.gloria.procurematerial.d.entities.PurchaseOrganisation;
import com.volvo.gloria.util.IOUtil;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.group.init.purchaseOrganisation._1_0.PurchaseOrganisations;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * Util class for InitDataBuyerCode.
 */
public class InitDataPurchaseOrganisation extends InitDataBase {
    private static final String FILE_NAME = "PurchaseOrganisation.xml";
    private static final String FILE_TYPE = "procurement";

    private static final String PURCHASE_ORGANISATION_DATA_PROPERTY_KEY = "purchaseOrganisation.data";
    private String message;

    public InitDataPurchaseOrganisation(String xmlFilePath) throws IOException {
        super(XmlConstants.SchemaFullPath.INIT_PURCHASE_ORGANISATION, XmlConstants.PackageName.INIT_PURCHASE_ORGANISATION);
        message = IOUtil.getStringFromClasspath(xmlFilePath);
    }

    /**
     * @param testDataProperties
     * @throws IOException
     */
    public InitDataPurchaseOrganisation(Properties testDataProperties, String env, String fileName) throws IOException {
        super(XmlConstants.SchemaFullPath.INIT_PURCHASE_ORGANISATION, XmlConstants.PackageName.INIT_PURCHASE_ORGANISATION);
        if (fileName == null) {
            fileName = FILE_NAME;
        }
        if (testDataProperties.containsKey(PURCHASE_ORGANISATION_DATA_PROPERTY_KEY)) {
            message = IOUtil.getStringFromFileSystem(testDataProperties.getProperty(PURCHASE_ORGANISATION_DATA_PROPERTY_KEY));
        } else {
            message = getXml(env, FILE_TYPE, fileName);
        }
    }

    public void initPurchaseOrganisation() throws JAXBException, IOException {
        Object object = transformXmlToJaxb(message);

        PurchaseOrganisations purchaseOrganisations = (PurchaseOrganisations) object;
        ProcurementServices procurementService = ServiceLocator.getService(ProcurementServices.class);
        for (com.volvo.group.init.purchaseOrganisation._1_0.PurchaseOrganisation purchaseOrganisation : purchaseOrganisations.getPurchaseOrganisation()) {
            PurchaseOrganisation purchaseOrganisationEntity = new PurchaseOrganisation();
            purchaseOrganisationEntity.setOrganisationCode(purchaseOrganisation.getOrganisationCode());
            purchaseOrganisationEntity.setOrganisationName(purchaseOrganisation.getOrganisationName());
            for (com.volvo.group.init.purchaseOrganisation._1_0.Buyer buyer : purchaseOrganisation.getBuyer()) {
                Buyer buyerEntity = new Buyer();
                buyerEntity.setCode(buyer.getCode());
                buyerEntity.setName(buyer.getName());
                buyerEntity.setPurchaseOrganisation(purchaseOrganisationEntity);
                purchaseOrganisationEntity.getBuyers().add(buyerEntity);
            }
            procurementService.addPurchaseOrganisation(purchaseOrganisationEntity);
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
