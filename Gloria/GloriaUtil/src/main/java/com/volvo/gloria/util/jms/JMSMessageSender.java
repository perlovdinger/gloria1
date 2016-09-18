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
package com.volvo.gloria.util.jms;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;
/**
 * Sender of a JMSMessage.
 */
public class JMSMessageSender {
    private static final String CONNECTION_FACTORY = "connectionFactory";
    
    protected JMSMessageSender() {

    }

    public static void send(final String message, String configurationURI) {
        ConnectionFactory connectionFactory = ServiceLocator.getService(CONNECTION_FACTORY, ConnectionFactory.class);
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setSessionTransacted(true);
        Destination destination = ServiceLocator.getService(configurationURI, Destination.class);
        try {
            jmsTemplate.send(destination, new MessageCreator() {
                public Message createMessage(Session session) throws JMSException {
                    return session.createTextMessage(message);
                }
            });
        } catch (JmsException e) {
            throw new GloriaSystemException(e, "Unable to send message.");

        }
    }
}
