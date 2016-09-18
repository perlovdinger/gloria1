package com.volvo.gloria.common.c.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;

/**
 * Data class for XmlMessageLog.
 * 
 */
public class XmlLogEventDTO implements Serializable {
    private static final long serialVersionUID = 8283634388178317731L;
    private String application;
    private String sentReceived;
    private String integration;
    private Timestamp eventTime;
    private String jmsMessageId;
    private byte[] xmlMessage;

    public XmlLogEventDTO(String application, String sentReceived, String integration, String jmsMessageId, byte[] message) {
        this.application = application;
        this.sentReceived = sentReceived;
        this.integration = integration;
        this.jmsMessageId = jmsMessageId;
        this.eventTime = new Timestamp(new Date().getTime());
        if (message != null) {
            this.xmlMessage = Arrays.copyOf(message, message.length);
        }
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getSentReceived() {
        return sentReceived;
    }

    public void setSentReceived(String sentReceived) {
        this.sentReceived = sentReceived;
    }

    public String getIntegration() {
        return integration;
    }

    public void setIntegration(String integration) {
        this.integration = integration;
    }

    public Timestamp getEventTime() {
        return eventTime;
    }

    public void setEventTime(Timestamp eventTime) {
        this.eventTime = eventTime;
    }

    public String getJmsMessageId() {
        return jmsMessageId;
    }

    public void setJmsMessageId(String jmsMessageId) {
        this.jmsMessageId = jmsMessageId;
    }

    public byte[] getXmlMessage() {
        return xmlMessage;
    }

    public void setXmlMessage(byte[] message) {
        if (message == null) {
            this.xmlMessage = new byte[0];
        } else {
            this.xmlMessage = Arrays.copyOf(message, message.length);
        }
    }
}
