package com.volvo.gloria.util.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.persistence.PersistenceException;
import javax.xml.bind.JAXBException;

/**
 * services for JMSExceptionHandler.
 */
public interface JMSExceptionHandler {
    
    void handleGeneralException(Throwable t, Message message);
    
    void handleMessageFormatException(JAXBException e, Message message);

    void handleMessagingException(JMSException e, Message message);
    
    void handlePersistenceException(PersistenceException e, Message message);
}
