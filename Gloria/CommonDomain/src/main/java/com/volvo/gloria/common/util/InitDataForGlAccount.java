package com.volvo.gloria.common.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.d.entities.CompanyCode;
import com.volvo.gloria.common.d.entities.GlAccount;
import com.volvo.gloria.util.IOUtil;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.group.init.glAccount._1_0.GlAccounts;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * Util class for InitDataforGlAccount.
 */
public class InitDataForGlAccount extends InitDataBase {

    private static final String FILE_NAME = "GlAccount.xml";
    private static final String FILE_TYPE = "common";
    private static final String GLACCOUNT_DATA_PROPERTY_KEY = "glaccount.data";
    private static CommonServices commonService = ServiceLocator.getService(CommonServices.class);
    private String message;

    public InitDataForGlAccount(String xmlFilePath) throws IOException {
        super(XmlConstants.SchemaFullPath.INIT_GLACCOUNT, XmlConstants.PackageName.INIT_GLACCOUNT);
        message = IOUtil.getStringFromClasspath(xmlFilePath);
    }

    public InitDataForGlAccount(Properties testDataProperties, String env, String fileName) throws IOException {
        super(XmlConstants.SchemaFullPath.INIT_GLACCOUNT, XmlConstants.PackageName.INIT_GLACCOUNT);
        if (fileName == null) {
            fileName = FILE_NAME;
        }
        if (testDataProperties.containsKey(GLACCOUNT_DATA_PROPERTY_KEY)) {
            message = IOUtil.getStringFromFileSystem(testDataProperties.getProperty(GLACCOUNT_DATA_PROPERTY_KEY));
        } else {
            message = getXml(env, FILE_TYPE, fileName);
        }
    }

    public void initGlAccounts() throws JAXBException, IOException {
        Object object = transformXmlToJaxb(message);

        GlAccounts glAccs = (GlAccounts) object;

        List<GlAccount> glAccountEntitys = new ArrayList<GlAccount>();
        List<com.volvo.group.init.glAccount._1_0.GlAccount> glAccounts = glAccs.getGlAccount();
        for (com.volvo.group.init.glAccount._1_0.GlAccount glAccount : glAccounts) {
            CompanyCode companyCode = commonService.findCompanyCodeByCode(glAccount.getCompanyCode());
            if (companyCode != null) {
                GlAccount glAccountEntity = new GlAccount();
                glAccountEntity.setAccountNumber(glAccount.getAccountNumber());
                glAccountEntity.setCompanyCode(companyCode);
                glAccountEntity.setAccountName(glAccount.getAccountName());
                glAccountEntity.setAccountDescription(glAccount.getAcountDescription());
                glAccountEntitys.add(glAccountEntity);
            }
        }

        commonService.addGlAccounts(glAccountEntitys);
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
