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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * 
 * Singleton class that cache xml schemas.
 */
public final class JaxbManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(JaxbHandler.class);
    private static JaxbManager instance;
    private Map<String, Schema> schemas = new HashMap<String, Schema>();
    private Map<String, JAXBContext> contexts = new HashMap<String, JAXBContext>();

    private JaxbManager() {
        // To prevent init
    }

    /**
     * 
     * @return XmlSchemaManager
     */
    public static synchronized JaxbManager getInstance() {
        if (instance == null) {
            instance = new JaxbManager();
        }
        return instance;
    }

    /**
     * Get Schema.
     * 
     * @param schemaName
     *            String
     * @return Schema
     * @throws JAXBException
     */
    public synchronized Schema getSchema(String schemaName) throws JAXBException {
        // Schema is threadsafe
        Schema schema = null; 

        // If schema is not loaded - load it
        if (!schemas.containsKey(schemaName)) {
            URL schemaURL = getClass().getClassLoader().getResource(schemaName);
            try {
                SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                schema = schemaFactory.newSchema(schemaURL);
            } catch (SAXException e) {
                LOGGER.error("Couldn't parse XML schema: " + e.getMessage());
            }
            schemas.put(schemaName, schema);
        }
        schema = (Schema) schemas.get(schemaName);
        return schema;
    }

    public synchronized JAXBContext getJAXBContext(String packageName) throws JAXBException {
        JAXBContext jaxbContext = null;
        if (!contexts.containsKey(packageName)) {
            jaxbContext = JAXBContext.newInstance(packageName);
            contexts.put(packageName, jaxbContext);
        }
        jaxbContext = (JAXBContext) contexts.get(packageName);
        return jaxbContext;
    }

}
