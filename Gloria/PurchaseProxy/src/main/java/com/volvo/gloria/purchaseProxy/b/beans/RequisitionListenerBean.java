package com.volvo.gloria.purchaseProxy.b.beans;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;

import com.volvo.gloria.util.jms.TextMessageListener;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * Service Implementations for RequistionListener.
 */
@ContainerManaged
public class RequisitionListenerBean extends TextMessageListener {
    @Override
    public void processTextMessage(TextMessage message, String messageText) throws JMSException, JAXBException {
    }
}

