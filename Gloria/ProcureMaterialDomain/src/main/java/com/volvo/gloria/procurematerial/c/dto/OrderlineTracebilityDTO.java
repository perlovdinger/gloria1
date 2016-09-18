package com.volvo.gloria.procurematerial.c.dto;

import java.util.Date;

import com.volvo.gloria.util.tracing.c.TraceabilityDTO;

public class OrderlineTracebilityDTO extends TraceabilityDTO {

    private static final long serialVersionUID = 7601513122291545434L;

    private String olStatus;
    private Date staAcceptedDate;
    private Date staAgreedDate;
    private long allowedQuantity;

    public String getOlStatus() {
        return olStatus;
    }

    public void setOlStatus(String olStatus) {
        this.olStatus = olStatus;
    }

    public Date getStaAcceptedDate() {
        return staAcceptedDate;
    }

    public void setStaAcceptedDate(Date staAcceptedDate) {
        this.staAcceptedDate = staAcceptedDate;
    }

    public Date getStaAgreedDate() {
        return staAgreedDate;
    }

    public void setStaAgreedDate(Date staAgreedDate) {
        this.staAgreedDate = staAgreedDate;
    }

    public long getAllowedQuantity() {
        return allowedQuantity;
    }

    public void setAllowedQuantity(long allowedQuantity) {
        this.allowedQuantity = allowedQuantity;
    }
}
