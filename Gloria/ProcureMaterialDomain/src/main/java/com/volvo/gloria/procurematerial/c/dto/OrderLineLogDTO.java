package com.volvo.gloria.procurematerial.c.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO class to manage OrderLine log.
 * 
 */
public class OrderLineLogDTO implements Serializable {

    private static final long serialVersionUID = -7546116244642450526L;

    private long id;

    private long version;

    private String eventValue;

    private Date eventTime;

    private String eventOriginatorId;

    private String eventOriginatorName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getEventValue() {
        return eventValue;
    }

    public void setEventValue(String eventValue) {
        this.eventValue = eventValue;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventOriginatorId() {
        return eventOriginatorId;
    }

    public void setEventOriginatorId(String eventOriginatorId) {
        this.eventOriginatorId = eventOriginatorId;
    }

    public String getEventOriginatorName() {
        return eventOriginatorName;
    }

    public void setEventOriginatorName(String eventOriginatorName) {
        this.eventOriginatorName = eventOriginatorName;
    }
}
