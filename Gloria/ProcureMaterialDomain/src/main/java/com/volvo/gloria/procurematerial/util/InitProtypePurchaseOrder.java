package com.volvo.gloria.procurematerial.util;

import java.io.IOException;
import java.util.Properties;

import com.volvo.gloria.common.purchaseorder.c.dto.SyncPurchaseOrderTypeDTO;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.b.PurchaseOrderTransformer;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.IOUtil;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.gloria.util.xml.XmlTransformer;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * Loads initial data for Prototype Purchase Order entities from XML file.
 */
public class InitProtypePurchaseOrder extends XmlTransformer {
    private static final String PROTOTYPE_PURCHASE_ORDER_DATA_PROPERTY_KEY = "prototype.purchaseorder.data";
    private static final String PROTOTYPE_PURCHASE_ORDER_DATA_CP = "testdata/procure/SyncStandAloneOrder_Prototype 1.00.xml";
    private String message;

    public InitProtypePurchaseOrder(String xmlFilePath) throws IOException {
        super(XmlConstants.SchemaFullPath.PROTOTYPE_PURCHASE_ORDER, XmlConstants.PackageName.PROTOTYPE_PURCHASE_ORDER);
        message = IOUtil.getStringFromClasspath(xmlFilePath);
    }

    public InitProtypePurchaseOrder(Properties testDataProperties) throws IOException {
        if (testDataProperties.containsKey(PROTOTYPE_PURCHASE_ORDER_DATA_PROPERTY_KEY)) {
            message = IOUtil.getStringFromFileSystem(testDataProperties.getProperty(PROTOTYPE_PURCHASE_ORDER_DATA_PROPERTY_KEY));
        } else {
            message = IOUtil.getStringFromClasspath(PROTOTYPE_PURCHASE_ORDER_DATA_CP);
        }
    }

    public void initPrototypePurchaseOrder() throws GloriaApplicationException {
        PurchaseOrderTransformer purchaseOrderTransformer = ServiceLocator.getService(PurchaseOrderTransformer.class);
        ProcurementServices procurementServices = ServiceLocator.getService(ProcurementServices.class);
        SyncPurchaseOrderTypeDTO syncPurchaseOrderTypeDTO = purchaseOrderTransformer.transformStoredPurchaseOrder(message);
        procurementServices.createOrUpdatePurchaseOrder(syncPurchaseOrderTypeDTO);
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
