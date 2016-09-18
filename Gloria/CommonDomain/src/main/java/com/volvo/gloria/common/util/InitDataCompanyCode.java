package com.volvo.gloria.common.util;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.d.entities.CompanyCode;
import com.volvo.gloria.common.d.entities.CompanyGroup;
import com.volvo.gloria.common.d.entities.Currency;
import com.volvo.gloria.config.b.beans.ApplicationUtils;
import com.volvo.gloria.util.IOUtil;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.group.init.companyCode._1_0.CompanyCodes;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * Util class for InitDataforCompanyCode.
 */
public class InitDataCompanyCode extends InitDataBase {
    private static final String COMPANYCODE_DATA_PROPERTY_KEY = "companyCode.data";
    private static final String FILE_NAME = "CompanyCode.xml";
    private static final String FILE_TYPE = "common";
    private static final Logger LOGGER = LoggerFactory.getLogger(InitDataCompanyCode.class);
    private static CommonServices commonService = ServiceLocator.getService(CommonServices.class);
    private String message;

    public InitDataCompanyCode(String xmlFilePath) throws IOException {
        super(XmlConstants.SchemaFullPath.INIT_COMPANYCODE, XmlConstants.PackageName.INIT_COMPANYCODE);
        message = IOUtil.getStringFromClasspath(xmlFilePath);
    }

    public InitDataCompanyCode(Properties testDataProperties, String env, String fileName) throws IOException {
        super(XmlConstants.SchemaFullPath.INIT_COMPANYCODE, XmlConstants.PackageName.INIT_COMPANYCODE);
        if (fileName == null) {
            fileName = FILE_NAME;
        }
        if (testDataProperties.containsKey(COMPANYCODE_DATA_PROPERTY_KEY)) {
            message = IOUtil.getStringFromFileSystem(testDataProperties.getProperty(COMPANYCODE_DATA_PROPERTY_KEY));
        } else {

            message = getXml(env, FILE_TYPE, fileName);
        }
    }

    public void initCompanyCodes(String env, boolean rollout) throws JAXBException, IOException {
        Object object = transformXmlToJaxb(message);
        CompanyCodes companyCodes = (CompanyCodes) object;
        for (com.volvo.group.init.companyCode._1_0.CompanyCode companyCode : companyCodes.getCompanyCode()) {

            List<String> companyCodesEnv = ApplicationUtils.getCompanyCodesForEnv(env);
            // if rollout companyCodes does not need to match - just use rollout xml
            if (rollout || companyCodesEnv.contains(companyCode.getCode())) {
                LOGGER.info("Add companyCode=" + companyCode.getCode());
                CompanyCode companyCodeEntity = new CompanyCode();
                companyCodeEntity.setCode(companyCode.getCode());
                companyCodeEntity.setName(companyCode.getName());
                companyCodeEntity.setDefaultCurrency(companyCode.getDefaultCurrency());
                companyCodeEntity.setSapPurchaseOrg(companyCode.getSapPurchaseOrg());
                companyCodeEntity.setSapQuantityBlockReceiverId(companyCode.getSapQuantityBlockReceiverId());
                companyCodeEntity.setReceivingGoods(companyCode.isReceivingGoods());
                companyCodeEntity.setSendGRtoSAP(companyCode.isSendGRtoSAP());
                companyCodeEntity.setSendPOtoSAP(companyCode.isSendPOtoSAP());
                CompanyGroup companyGroup = commonService.getCompanyGroupByCode(companyCode.getCompanyCodeGroup());
                if (companyGroup != null) {
                    companyCodeEntity.setCompanyGroup(companyGroup);
                    companyGroup.getCompanyCodes().add(companyCodeEntity);
                } else {
                    companyGroup = new CompanyGroup();
                    companyGroup.setCode(companyCode.getCompanyCodeGroup());
                    commonService.addCompanyGroup(companyGroup);
                    companyCodeEntity.setCompanyGroup(companyGroup);
                    companyGroup.getCompanyCodes().add(companyCodeEntity);
                }

                commonService.addCompanyCode(companyCodeEntity);

                List<com.volvo.group.init.companyCode._1_0.Currency> currencies = companyCode.getCurrency();
                for (com.volvo.group.init.companyCode._1_0.Currency currency : currencies) {
                    Currency currencyEntity = commonService.getCurrencyByCode(currency.getCode());
                    if (currencyEntity != null) {
                        currencyEntity.getCompanyCodes().add(companyCodeEntity);
                        companyCodeEntity.getCurrencies().add(currencyEntity);
                        commonService.addCurrency(currencyEntity);
                    }
                }
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
