package com.volvo.gloria.common.d.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Version;

import com.volvo.gloria.util.persistence.GenericEntity;

/**
 * Entity class to log Gateway and Proxy message processing metadata.
 */
@Entity
@Table(name = "XML_MESSAGE_LOG")
public class XmlMessageLog implements GenericEntity<Long>, Serializable {

    private static final long serialVersionUID = -3455657090510828872L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "XMLMESSAGELOG_OID")
    private Long id;
    
    @Version
    private long version;
    private String application;
    private String sentReceived;
    private String integration;
    private Timestamp eventTime;
    private String jmsMessageId;
    
    @Lob
    private byte[] xmlMessage;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public long getVersion() {
        return version;
    }
}
