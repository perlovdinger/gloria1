/*
 * Copyright 2009 Volvo Information Technology AB 
 * 
 * Licensed under the Volvo IT Corporate Source License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *      http://java.volvo.com/licenses/CORPORATE-SOURCE-LICENSE 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.volvo.gloria.common.util;

import java.io.IOException;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.d.entities.Site;
import com.volvo.gloria.util.IOUtil;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.group.init.site._1_0.Sites;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * Initialises data for sites, by loading an XML file on classpath or using a file URI defined by <code>domain.user</code> property.
 * 
 */
public class InitDataSites extends InitDataBase {

    private static final String FILE_NAME = "Site.xml";
    private static final String FILE_TYPE = "common";

    private static final String USER_DATA_PROPERTY_KEY = "site.data";
    private String message;

    public InitDataSites(String xmlFilePath) throws IOException {
        super(XmlConstants.SchemaFullPath.INIT_SITE, XmlConstants.PackageName.INIT_SITE);
        message = IOUtil.getStringFromClasspath(xmlFilePath);
    }

    public InitDataSites(Properties testDataProperties, String env) throws IOException {
        super(XmlConstants.SchemaFullPath.INIT_SITE, XmlConstants.PackageName.INIT_SITE);
        if (testDataProperties.containsKey(USER_DATA_PROPERTY_KEY)) {
            message = IOUtil.getStringFromFileSystem(testDataProperties.getProperty(USER_DATA_PROPERTY_KEY));
        } else {
            message = getXml(env, FILE_TYPE, FILE_NAME);
        }
    }

    public void initSites(String env) throws JAXBException, IOException {
        Object object = transformXmlToJaxb(message);
        Sites sites = (Sites) object;

        CommonServices commonServices = ServiceLocator.getService(CommonServices.class);

      
        for (com.volvo.group.init.site._1_0.Sites.Site siteFromJaxb : sites.getSite()) {
            Site site = new Site();
            site.setSiteId(siteFromJaxb.getSiteId());
            site.setSiteCode(siteFromJaxb.getSiteCode());
            site.setSiteName(siteFromJaxb.getSiteName());
            site.setAddress(siteFromJaxb.getAddress());
            site.setPhone(siteFromJaxb.getPhone());
            site.setCountryCode(siteFromJaxb.getCountryCode());
            site.setCompanyCode(siteFromJaxb.getCompanyCode());
            site.setJointVenture(siteFromJaxb.isJoinVenture());
            site.setShipToSite(siteFromJaxb.isShipToSite());
            site.setShipToType(siteFromJaxb.getShipToType());           
            site.setBuildSite(siteFromJaxb.isBuildSite());
            site.setBuildSiteType(siteFromJaxb.getBuildSiteType());
            commonServices.addSite(site);
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
