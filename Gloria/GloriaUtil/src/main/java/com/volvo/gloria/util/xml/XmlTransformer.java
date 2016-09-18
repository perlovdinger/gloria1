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
package com.volvo.gloria.util.xml;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Responsibility of this class is to manage transformations between xml and other objects.
 */
public abstract class XmlTransformer {

    private static final int MAX_NUMBER_OF_LOGGED_CHARACTERS = 100;
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlTransformer.class);
    private String xmlSchemaFullPath;
    private String packageName;

    public XmlTransformer() {        
    }
    
    public XmlTransformer(String xmlSchemaFullPath, String packageName) {
        this.xmlSchemaFullPath = xmlSchemaFullPath;
        this.packageName = packageName;
    }

    /**
     * Transform a xml to DTO or other class. The transformation from JAXB to DTO is done in extending class.
     * 
     * @param xml
     * @return
     * @throws JAXBException
     */
    public Object transform(String xml) throws JAXBException {
        Object jaxbOject = transformXmlToJaxb(xml);

        // Transform from JAXB to whatever
        return transformFromJAXBToDTO(jaxbOject);
    }

    public Object transformXmlToJaxb(String xml) throws JAXBException {
        // Validate xml against schema
        XmlSchemaValidator validator = new XmlSchemaValidator();
        if (!validator.validate(xml, xmlSchemaFullPath)) {
            LOGGER.error("validation failed errors:" + validator.getErrors() + " xml="
                    + xml.substring(0, Math.min(MAX_NUMBER_OF_LOGGED_CHARACTERS, xml.length())));
            throw new JAXBException("validation failed errors:" + validator.getErrors());
        }

        // Transform to JAXB-generated classes
        return unmarshallXMLMessage(xml, xmlSchemaFullPath, packageName);
    }

    /**
     * Transform a DTO to xml. The transformation from DTO to JAXB is done in extending class.
     * 
     * @param dto
     * @return
     * @throws JAXBException 
     */
    public String transform(Object dto) throws JAXBException {
        Object jaxbElement = transformFromDTOToJAXB(dto);
        JaxbHandler handler = new JaxbHandler(xmlSchemaFullPath, packageName);
        return handler.marshal(jaxbElement);
    }

    protected abstract Object transformFromJAXBToDTO(Object jaxbOject);

    protected abstract Object transformFromDTOToJAXB(Object dto);

    /**
     * Deserialize the received xml into JAXB-generated classes (representing the information model).
     * 
     * @param xmlMessage
     *            the XML message
     * @return an Object instance
     * @throws JAXBException
     */
    protected Object unmarshallXMLMessage(String xml, String xmlSchemaFullPath, String packageName) throws JAXBException {
        JaxbHandler jaxbHandler = new JaxbHandler(xmlSchemaFullPath, packageName);
        JaxbHandlerResult result = jaxbHandler.unmarshalAndValidateXml(xml);
        if (result  == null) {
            return null;
        } else {
            return result.getJaxbObject();
        }     
    }

}
