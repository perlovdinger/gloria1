package com.volvo.gloria.procurematerial.c.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Data Transfer object for the Delivery Schedule.
 */
public class DeliveryScheduleDTO implements Serializable {

    private static final long serialVersionUID = -6225076618166322650L;

    private long id;

    private long version;

    private long expectedQuantity;

    private Date expectedDate;

    private String statusFlag;

    private Date plannedDispatchDate;

    private boolean markPassedDate;

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

    public long getExpectedQuantity() {
        return expectedQuantity;
    }

    public void setExpectedQuantity(long expectedQuantity) {
        this.expectedQuantity = expectedQuantity;
    }

    public Date getExpectedDate() {
        return expectedDate;
    }

    public void setExpectedDate(Date expectedDate) {
        this.expectedDate = expectedDate;
    }

    public String getStatusFlag() {
        return statusFlag;
    }

    public void setStatusFlag(String statusFlag) {
        this.statusFlag = statusFlag;
    }

    public Date getPlannedDispatchDate() {
        return plannedDispatchDate;
    }

    public void setPlannedDispatchDate(Date plannedDispatchDate) {
        this.plannedDispatchDate = plannedDispatchDate;
    }

    public boolean isMarkPassedDate() {
        return markPassedDate;
    }

    public void setMarkPassedDate(boolean markPassedDate) {
        this.markPassedDate = markPassedDate;
    }
}
