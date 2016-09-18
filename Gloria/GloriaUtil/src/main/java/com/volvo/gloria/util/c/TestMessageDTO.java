package com.volvo.gloria.util.c;

import java.io.Serializable;

/**
 * DTO class for JMS test messages used by the test tool.
 */
public class TestMessageDTO implements Serializable {
    
    private static final long serialVersionUID = 3654609264764435477L;
   
    private String format;
    private String jmsQueueId;
    private String messageContent;

    public String getFormat() {
        return format;
    }
    
    public void setFormat(String format) {
        this.format = format;
    }
    
    public String getJmsQueueId() {
        return jmsQueueId;
    }

    public void setJmsQueueId(String jmsQueueId) {
        this.jmsQueueId = jmsQueueId;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

}
