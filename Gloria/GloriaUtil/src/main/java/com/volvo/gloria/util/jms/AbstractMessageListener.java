package com.volvo.gloria.util.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.persistence.PersistenceException;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.jvs.integration.utils.JmsUtils;

/**
 * Base class for message reception with injectable error handling.
 */
public abstract class AbstractMessageListener implements MessageListener {
    
    private static final int DELIVERY_THRESHOLD = 3;
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMessageListener.class);
    private JMSExceptionHandler exceptionHandler = new SimpleLoggingExceptionHandler(LOGGER);
    
  
    @Override
    public void onMessage(Message message) {
        JmsUtils.logMessage(message);
        try {
            int deliveryCount = message.getIntProperty("JMSXDeliveryCount");
            LOGGER.debug("JMSXDeliveryCount is {}", deliveryCount);
            if (deliveryCount < DELIVERY_THRESHOLD) {
                processMessage(message);
            } else {
                LOGGER.error("A message with ID {} has not been consumed after three attempts. Ignoring it.", message.getJMSMessageID());
                // TODO send mail to support?
            }
        } catch (final JMSException jmsException) {
            // catching JMSException as we might want to handle this in specific way
            handleMessagingException(jmsException, message);
        } catch (final JAXBException jaxbException) {
            // catching JAXBException as we might want to handle this in specific way
            handleWrongMessageFormat(jaxbException, message);
        } catch (final PersistenceException e) {
            handleDatabaseException(e, message);
        } catch (final Exception e) {
            handleGeneralException(e, message);
        }
    }
    
    public abstract void processMessage(Message message) throws JAXBException, JMSException;
    
    public void handleDatabaseException(Exception e, Message message) {
        getExceptionHandler().handleGeneralException(e, message);
    }
    
    public void handleGeneralException(Exception e, Message message) {
        getExceptionHandler().handleGeneralException(e, message);
    }
    
    public void handleMessagingException(JMSException e, Message message) {
        getExceptionHandler().handleMessagingException(e, message);
    }
    
    public void handleWrongMessageFormat(JAXBException e, Message message) {
        getExceptionHandler().handleMessageFormatException(e, message);
    }

    public JMSExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public void setExceptionHandler(JMSExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

}
