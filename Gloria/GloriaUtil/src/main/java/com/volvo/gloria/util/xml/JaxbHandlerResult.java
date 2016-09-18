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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.ValidationEvent;

/**
 * This class incapsulate the JAXB result and state after unmarshal.
 */
public class JaxbHandlerResult {

    private boolean validatedOk = true;

    private Object jaxbObject;

    private List<ValidationEvent> eventList = new ArrayList<ValidationEvent>();

    protected void setValidatedOk(boolean validatedOk) {
        this.validatedOk = validatedOk;
    }

    protected void setJaxbObject(Object jaxbObject) {
        this.jaxbObject = jaxbObject;
    }

    public Object getJaxbObject() {
        return jaxbObject;
    }

    public boolean validatedOk() {
        return validatedOk;
    }

    protected void setEvents(List<ValidationEvent> eventList) {
        this.eventList = eventList;
    }

    public List<ValidationEvent> getEventList() {
        return eventList;
    }

    public boolean handleValidationError(ValidationEventHandlerImpl validationEventHandler) {
        if (validationEventHandler != null && validationEventHandler.hasEvents()) {
            eventList = validationEventHandler.getEvents();
            this.setValidatedOk(false);
        }
        return this.validatedOk;
    }
}
