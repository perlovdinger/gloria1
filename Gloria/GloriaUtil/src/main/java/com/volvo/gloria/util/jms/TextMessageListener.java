package com.volvo.gloria.util.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;

/**
 * Convenience class to help us stay DRY when recieving JMS TextMessages.
 * @see http://en.wikipedia.org/wiki/Don't_repeat_yourself
 */
public abstract class TextMessageListener extends AbstractMessageListener {
    
    @Override
    public void processMessage(Message message) throws JMSException, JAXBException {
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            processTextMessage(textMessage, textMessage.getText());
        } else {
            getExceptionHandler().handleMessageFormatException(null, message);
        }
    }
    
    public abstract void processTextMessage(TextMessage message, String messageText) throws  JMSException, JAXBException;

}
