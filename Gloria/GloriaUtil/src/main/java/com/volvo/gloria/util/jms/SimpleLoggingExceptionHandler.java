package com.volvo.gloria.util.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.persistence.PersistenceException;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;

/**
 * Services for SimpleLoggingExceptionHandler.
 */
public class SimpleLoggingExceptionHandler implements JMSExceptionHandler {
    
    private Logger logger;
    
  
    public SimpleLoggingExceptionHandler(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void handleGeneralException(Throwable t, Message message) {
        log("A general exception ocurred when recieving message", message, t);
    }

    @Override
    public void handleMessageFormatException(JAXBException e, Message message) {
        log("A MessageFormat exception ocurred when recieving message", message, e);
    }

    @Override
    public void handleMessagingException(JMSException e, Message message) {
        log("A JMS exception ocurred when recieving message", message, e);
    }

    @Override
    public void handlePersistenceException(PersistenceException e, Message message) {
        log("A Database related exception ocurred when recieving message", message, e);
    }
    
    private void log(String logText, Message jmsMessage, Throwable t) {
        logger.error("{} : {}", logText, jmsMessage);
        logger.error("Exception:", t);
    }

}
