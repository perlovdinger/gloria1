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
import com.volvo.gloria.common.d.entities.PartAffiliation;
import com.volvo.gloria.util.IOUtil;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.group.init.partAffiliation._1_0.PartAffiliations;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * Initialises data for User Domain, by loading an XML file on classpath or using a file URI defined by <code>domain.user</code> property.
 * 
 */
public class InitDataPartAffiliation extends InitDataBase {

    private static final String FILE_NAME = "PartAffiliation.xml";
    private static final String FILE_TYPE = "common";
    private static final String USER_DATA_PROPERTY_KEY = "partAffiliation.data";
    private String message;

    public InitDataPartAffiliation(String xmlFilePath) throws IOException {
        super(XmlConstants.SchemaFullPath.INIT_PART_AFFILIATION, XmlConstants.PackageName.INIT_PART_AFFILIATION);
        message = IOUtil.getStringFromClasspath(xmlFilePath);
    }

    public InitDataPartAffiliation(Properties testDataProperties, String env) throws IOException {
        super(XmlConstants.SchemaFullPath.INIT_PART_AFFILIATION, XmlConstants.PackageName.INIT_PART_AFFILIATION);
        if (testDataProperties.containsKey(USER_DATA_PROPERTY_KEY)) {
            message = IOUtil.getStringFromFileSystem(testDataProperties.getProperty(USER_DATA_PROPERTY_KEY));
        } else {
            message = getXml(env, FILE_TYPE, FILE_NAME);
        }
    }

    public void initPartAffiliations() throws JAXBException, IOException {
        Object object = transformXmlToJaxb(message);

        PartAffiliations partAffiliations = (PartAffiliations) object;

        CommonServices service = ServiceLocator.getService(CommonServices.class);
        for (com.volvo.group.init.partAffiliation._1_0.PartAffiliation partAffiliation : partAffiliations.getPartAffiliation()) {
            PartAffiliation partAffiliationEntity = new PartAffiliation();
            partAffiliationEntity.setCode(partAffiliation.getCode());
            partAffiliationEntity.setName(partAffiliation.getName());
            partAffiliationEntity.setDisplaySeq(partAffiliation.getDisplaySeq());
            partAffiliationEntity.setRequestable(partAffiliation.isRequestable());           
            service.addPartAffiliation(partAffiliationEntity);
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
