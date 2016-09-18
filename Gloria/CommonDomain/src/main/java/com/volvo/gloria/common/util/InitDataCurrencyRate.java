/**
 * 
 */
package com.volvo.gloria.common.util;

import java.io.IOException;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.d.entities.Currency;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.group.init.currencyrate._1_0.CurrencyRate;
import com.volvo.group.init.currencyrate._1_0.CurrencyRates;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

public class InitDataCurrencyRate extends InitDataBase {

    private static final String FILE_NAME = "CurrencyRate.xml";
    private static final String FILE_TYPE = "common";
    private String message;

    public InitDataCurrencyRate(Properties properties, String env) throws IOException {
        super(XmlConstants.SchemaFullPath.INIT_CURRENCY_RATE, XmlConstants.PackageName.INIT_CURRENCY_RATE);
        message = getXml(env, FILE_TYPE, FILE_NAME);
    }

    public void initCurrencyRate() throws JAXBException, IOException {
        Object object = transformXmlToJaxb(message);
        CurrencyRates currencyRates = (CurrencyRates) object;
        CommonServices commonService = ServiceLocator.getService(CommonServices.class);
        for (CurrencyRate currencyRate : currencyRates.getCurrencyRate()) {
            Currency currency = commonService.findCurrencyByCode(currencyRate.getCurrencyCode());
            if (currency != null) {
                com.volvo.gloria.common.d.entities.CurrencyRate currencyRateEntity = new com.volvo.gloria.common.d.entities.CurrencyRate();
                currencyRateEntity.setBaseCurrencyCode(currencyRate.getBaseCurrencyCode());
                currencyRateEntity.setCurrency(currency);
                currencyRateEntity.setDisplaySeq(currencyRate.getDisplaySeq());
                currencyRateEntity.setEndDate(currencyRate.getEndDate().toGregorianCalendar().getTime());
                currencyRateEntity.setRate(currencyRate.getRate());
                currencyRateEntity.setStartDate(currencyRate.getStartDate().toGregorianCalendar().getTime());
                commonService.addCurrencyRate(currencyRateEntity);
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
