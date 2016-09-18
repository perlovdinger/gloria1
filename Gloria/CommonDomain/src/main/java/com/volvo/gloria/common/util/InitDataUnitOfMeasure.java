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
import com.volvo.gloria.common.d.entities.UnitOfMeasure;
import com.volvo.gloria.util.IOUtil;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.group.init.unitOfMeasure._1_0.UnitOfMeasures;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * Initialises data for User Domain, by loading an XML file on classpath or using a file URI defined by <code>domain.user</code> property.
 * 
 */
public class InitDataUnitOfMeasure extends InitDataBase {

    private static final String FILE_NAME = "UnitOfMeasure.xml";
    private static final String FILE_TYPE = "common";

    private static final String USER_DATA_PROPERTY_KEY = "unitOfMeasure.data";
    private String message;

    public InitDataUnitOfMeasure(String xmlFilePath) throws IOException {
        super(XmlConstants.SchemaFullPath.INIT_UNIT_OF_MEASURE, XmlConstants.PackageName.INIT_UNIT_OF_MEASURE);
        message = IOUtil.getStringFromClasspath(xmlFilePath);
    }

    public InitDataUnitOfMeasure(Properties testDataProperties, String env) throws IOException {
        super(XmlConstants.SchemaFullPath.INIT_UNIT_OF_MEASURE, XmlConstants.PackageName.INIT_UNIT_OF_MEASURE);
        if (testDataProperties.containsKey(USER_DATA_PROPERTY_KEY)) {
            message = IOUtil.getStringFromFileSystem(testDataProperties.getProperty(USER_DATA_PROPERTY_KEY));
        } else {
            message = getXml(env, FILE_TYPE, FILE_NAME);
        }
    }

    public void initUnitOfMeasure() throws JAXBException, IOException {
        Object object = transformXmlToJaxb(message);

        UnitOfMeasures unitOfMeasures = (UnitOfMeasures) object;

        CommonServices service = ServiceLocator.getService(CommonServices.class);

        for (com.volvo.group.init.unitOfMeasure._1_0.UnitOfMeasure unitOfMeasure : unitOfMeasures.getUnitOfMeasure()) {
            UnitOfMeasure unitOfMeasureEty = new UnitOfMeasure();
            unitOfMeasureEty.setCode(unitOfMeasure.getCode());
            unitOfMeasureEty.setName(unitOfMeasure.getName());
            unitOfMeasureEty.setGloriaCode(unitOfMeasure.isGloriaCode());
            unitOfMeasureEty.setDisplaySeq(unitOfMeasure.getDisplaySeq());
            service.addUnitOfMeasure(unitOfMeasureEty);
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
