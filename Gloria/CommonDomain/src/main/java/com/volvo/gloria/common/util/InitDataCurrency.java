package com.volvo.gloria.common.util;

import java.io.IOException;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.d.entities.Currency;
import com.volvo.gloria.util.IOUtil;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.group.init.currency._1_0.Currencys;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * Initialises data for User Domain, by loading an XML file on classpath or using a file URI defined by <code>domain.user</code> property.
 * 
 */
public class InitDataCurrency extends InitDataBase {
    private static final String CURRENCY_DATA_PROPERTY_KEY = "currencyData.data";
    private static final String FILE_NAME = "Currency.xml";
    private static final String FILE_TYPE = "common";
    private String message;

    public InitDataCurrency(String xmlFilePath) throws IOException {
        super(XmlConstants.SchemaFullPath.INIT_CURRENCY, XmlConstants.PackageName.INIT_CURRENCY);
        message = IOUtil.getStringFromClasspath(xmlFilePath);
    }

    public InitDataCurrency(Properties testDataProperties, String env) throws IOException {
        super(XmlConstants.SchemaFullPath.INIT_CURRENCY, XmlConstants.PackageName.INIT_CURRENCY);
        if (testDataProperties.containsKey(CURRENCY_DATA_PROPERTY_KEY)) {
            message = IOUtil.getStringFromFileSystem(testDataProperties.getProperty(CURRENCY_DATA_PROPERTY_KEY));
        } else {
            message = getXml(env, FILE_TYPE, FILE_NAME);
        }
    }

    public void initCurrencys() throws JAXBException, IOException {
        Object object = transformXmlToJaxb(message);

        Currencys currencys = (Currencys) object;

        CommonServices service = ServiceLocator.getService(CommonServices.class);
        for (com.volvo.group.init.currency._1_0.Currency currency : currencys.getCurrency()) {
            Currency currencyEntity = new Currency();
            currencyEntity.setCode(currency.getCode());
            currencyEntity.setName(currency.getName());
            currencyEntity.setSuppressDecimal(currency.isSuppressDecimal());
            currencyEntity.setDisplaySeq(currency.getDisplaySeq());
            service.addCurrency(currencyEntity);
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
