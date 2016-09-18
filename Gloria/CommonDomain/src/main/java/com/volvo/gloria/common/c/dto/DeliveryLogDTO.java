package com.volvo.gloria.common.c.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO class for maintaning Delivery log information.
 */
public class DeliveryLogDTO implements Serializable {

    private static final long serialVersionUID = 3755301535564502266L;

    private long id;
    private Date eventTime;
    private String eventValue;
    private String eventOriginatorId;
    private String eventOriginatorName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventValue() {
        return eventValue;
    }

    public void setEventValue(String eventValue) {
        this.eventValue = eventValue;
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

    @Override
    public String toString() {
        return "EventLogDTO [id=" + id + ", eventTime=" + eventTime + ", eventValue=" + eventValue + ", eventOriginatorId=" + eventOriginatorId
                + ", eventOriginatorName=" + eventOriginatorName + "]";
    }
}
