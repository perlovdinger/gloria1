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

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.validation.Schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles marshal,unmarshal and Validation of XML and JAXB object graphs.
 */
public class JaxbHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(JaxbHandler.class);
    private Schema schema;
    private JAXBContext jaxbContext;

    public JaxbHandler(String schemaName, String packageName) throws JAXBException {
        JaxbManager jaxbManager = JaxbManager.getInstance();
        this.schema = jaxbManager.getSchema(schemaName);
        jaxbContext = jaxbManager.getJAXBContext(packageName);
    }
    
    public String marshal(Object jaxbElement) throws JAXBException {
        StringWriter sw = null;
        Marshaller marshaller = jaxbContext.createMarshaller();
        sw = new StringWriter();
        marshaller.marshal(jaxbElement, sw);
        return sw.toString();
    }

    public JaxbHandlerResult unmarshalAndValidateXml(String xml) throws JAXBException {
        JaxbHandlerResult result = unmarshal(xml);
        if (!result.validatedOk()) {
            LOGGER.error("validationEvent:XML validation error");
            List<ValidationEvent> events = result.getEventList();
            for (ValidationEvent validationEvent : events) {
                LOGGER.error("validationEvent:" + validationEvent.getMessage());
            }
        }

        return result;
    }

    private JaxbHandlerResult unmarshal(String xml) {
        JaxbHandlerResult result = new JaxbHandlerResult();
        ValidationEventHandlerImpl validationEventHandler = null;
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            unmarshaller.setSchema(schema);
            validationEventHandler = new ValidationEventHandlerImpl();
            unmarshaller.setEventHandler(validationEventHandler);

            Reader reader = new StringReader(xml);
            Object jaxbObject = unmarshaller.unmarshal(reader);

            if (jaxbObject instanceof JAXBElement) {
                jaxbObject = ((JAXBElement<?>) jaxbObject).getValue();
            }

            if (validationEventHandler.hasEvents()) {
                result.handleValidationError(validationEventHandler);

            } else {
                result.setJaxbObject(jaxbObject);
                result.setValidatedOk(true);
            }
        } catch (JAXBException e) {
            handleValidationError(validationEventHandler, result);
        }
        return result;
    }

    private void handleValidationError(ValidationEventHandlerImpl validationEventHandler, JaxbHandlerResult result) {
        result.setValidatedOk(false);
        List<ValidationEvent> valEvents = null;
        if (validationEventHandler != null) {
            valEvents = validationEventHandler.getEvents();
        }
        result.setEvents(valEvents);
    }
}
