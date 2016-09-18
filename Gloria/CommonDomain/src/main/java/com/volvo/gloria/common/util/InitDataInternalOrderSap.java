package com.volvo.gloria.common.util;

import java.io.IOException;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.d.entities.InternalOrderSap;
import com.volvo.gloria.util.IOUtil;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.group.init.internalOrderSap._1_0.InternalOrderSaps;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * Util class for InitDataforInternalOrderSAP.
 */
public class InitDataInternalOrderSap extends InitDataBase {

    private static final String FILE_NAME = "InternalOrderSap.xml";
    private static final String FILE_TYPE = "common";
    private static final String INTERNALORDERSAP_DATA_PROPERTY_KEY = "internalOrderSap.data";
    private static CommonServices commonService = ServiceLocator.getService(CommonServices.class);
    private String message;

    public InitDataInternalOrderSap(String xmlFilePath) throws IOException {
        super(XmlConstants.SchemaFullPath.INIT_INTERNALORDERSAP, XmlConstants.PackageName.INIT_INTERNALORDERSAP);
        message = IOUtil.getStringFromClasspath(xmlFilePath);
    }

    public InitDataInternalOrderSap(Properties testDataProperties, String env) throws IOException {
        super(XmlConstants.SchemaFullPath.INIT_INTERNALORDERSAP, XmlConstants.PackageName.INIT_INTERNALORDERSAP);
        if (testDataProperties.containsKey(INTERNALORDERSAP_DATA_PROPERTY_KEY)) {
            message = IOUtil.getStringFromFileSystem(testDataProperties.getProperty(INTERNALORDERSAP_DATA_PROPERTY_KEY));
        } else {
            message = getXml(env, FILE_TYPE, FILE_NAME);
        }
    }

    public void initInternalOrderSap() throws JAXBException, IOException {
        Object object = transformXmlToJaxb(message);

        InternalOrderSaps internalOrderSaps = (InternalOrderSaps) object;

        for (com.volvo.group.init.internalOrderSap._1_0.InternalOrderSap internalOrderSap : internalOrderSaps.getInternalOrderSap()) {
            InternalOrderSap internalOrderSapEntity = new InternalOrderSap();
            internalOrderSapEntity.setCode(internalOrderSap.getCode());
            commonService.addInternalOrderSap(internalOrderSapEntity);
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
