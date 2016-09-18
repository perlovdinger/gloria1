package com.volvo.gloria.common.util;

import java.io.IOException;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.d.entities.ConversionUnit;
import com.volvo.gloria.util.IOUtil;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.group.init.conversionUnit._1_0.ConversionUnits;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * Initialises data for User Domain, by loading an XML file on classpath.
 * 
 */
public class InitDataConversionUnits extends InitDataBase {
    private static final String FILE_NAME = "ConversionUnit.xml";
    private static final String FILE_TYPE = "common";
    private static final String USER_DATA_PROPERTY_KEY = "conversionUnit.data";
    private String message;

    public InitDataConversionUnits(String xmlFilePath) throws IOException {
        super(XmlConstants.SchemaFullPath.INIT_UNIT_OF_MEASURE, XmlConstants.PackageName.INIT_UNIT_OF_MEASURE);
        message = IOUtil.getStringFromClasspath(xmlFilePath);
    }

    public InitDataConversionUnits(Properties testDataProperties, String env) throws IOException {
        super(XmlConstants.SchemaFullPath.INIT_UNIT_FOR_CONVERSION, XmlConstants.PackageName.INIT_UNIT_FOR_CONVERSION);
        if (testDataProperties.containsKey(USER_DATA_PROPERTY_KEY)) {
            message = IOUtil.getStringFromFileSystem(testDataProperties.getProperty(USER_DATA_PROPERTY_KEY));
        } else {
            message = getXml(env, FILE_TYPE, FILE_NAME);
        }
    }

    public void initConversionUnits() throws JAXBException, IOException {
        Object object = transformXmlToJaxb(message);

        ConversionUnits conversionUnits = (ConversionUnits) object;

        CommonServices service = ServiceLocator.getService(CommonServices.class);

        for (com.volvo.group.init.conversionUnit._1_0.ConversionUnit conversionUnit : conversionUnits.getConversionUnit()) {
            ConversionUnit conversionUnitEty = new ConversionUnit();
            conversionUnitEty.setApplFrom(conversionUnit.getApplFrom());
            conversionUnitEty.setCodeFrom(conversionUnit.getCodeFrom());
            conversionUnitEty.setApplTo(conversionUnit.getApplTo());
            conversionUnitEty.setCodeTo(conversionUnit.getCodeTo());
            conversionUnitEty.setDividedBy(conversionUnit.getDividedBy());
            service.addConversionUnit(conversionUnitEty);
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
