package com.volvo.gloria.procurematerial.d.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.springframework.util.StringUtils;

import com.volvo.gloria.procurematerial.c.EventType;

/**
 * Entity class for event logs.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "EVENT_LOG")
public class EventLog implements Serializable {

    private static final long serialVersionUID = -7528769050679615098L;

    private static final int _2048 = 2048;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EVENT_LOG_OID")
    private long eventLogOid;

    private EventType eventType;

    private Date eventTime;

    @Column(length = _2048)
    private String eventValue;

    private String eventOriginatorId;

    private String eventOriginatorName;

    public long getEventLogOid() {
        return eventLogOid;
    }

    public void setEventLogOid(long eventLogOid) {
        this.eventLogOid = eventLogOid;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
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
    
    public String getEventLog() {
        if (!StringUtils.isEmpty(eventValue)) {
            if (!StringUtils.isEmpty(this.eventOriginatorId)) {
                return this.eventTime + "  " + this.eventValue + "  " + this.eventOriginatorId + "(" + this.eventOriginatorName + ")";
            } else {
                return this.eventTime + "  " + this.eventValue;
            }
        }
        return "";
    }
}
