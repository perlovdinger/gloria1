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

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * XmlSchemaValidator class.
 */
public class XmlSchemaValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlSchemaValidator.class);
    private List<String> errors = new ArrayList<String>();

    /**
     * Validate the schema.
     * 
     * @param xmlString
     *            String
     * @param schemaFullPath
     *            String
     * @return boolean
     * @throws JAXBException
     */
    public boolean validate(String xmlString, String schemaFullPath) throws JAXBException {

        JaxbManager schemaMgr = JaxbManager.getInstance();
        Schema schema = schemaMgr.getSchema(schemaFullPath);

        if (schema == null) {
            return false;
        } else {
            return validate(schema, xmlString);
        }
    }

    private boolean validate(Schema schema, String xmlString) {
        boolean status = false;

        Validator validator = schema.newValidator();
        validator.setErrorHandler(new MyValidationErrorHandler());
        StreamSource streamSource = new StreamSource(new StringReader(xmlString));
        try {
            validator.validate(streamSource);
        } catch (SAXException e) {
            LOGGER.error("SAXException " + e);
        } catch (IOException e) {
            LOGGER.error("IOException " + e);
        }
        if (errors.isEmpty()) {
            status = true;
        }
        return status;
    }

    class MyValidationErrorHandler extends DefaultHandler {
        public void warning(SAXParseException exception) {
            errors.add(constructExceptionMessage(exception));
        }

        public void error(SAXParseException exception) {
            errors.add(constructExceptionMessage(exception));
        }

        public void fatalError(SAXParseException exception) {
            errors.add(constructExceptionMessage(exception));
        }

        private String constructExceptionMessage(SAXParseException exception) {
            return " line " + exception.getLineNumber() + " column " + exception.getColumnNumber() + ": " + exception.getMessage();
        }
    }

    public List<String> getErrors() {
        return this.errors;
    }

}
