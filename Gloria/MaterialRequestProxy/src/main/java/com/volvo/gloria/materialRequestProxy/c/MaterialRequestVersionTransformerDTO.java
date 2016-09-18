package com.volvo.gloria.materialRequestProxy.c;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

/**
 * MaterialRequestVersionDTO.
 */
public class MaterialRequestVersionTransformerDTO implements Serializable {

    private static final long serialVersionUID = 3823389359834479390L;

    private static final String ASSIGN_BLANK_SPACE_IF_MANDATORY = " ";

    private Date receivedDateTime;
    private String outboundLocationId;
    private Date outboundStartDate;
    private String mailFormId;
    private double approvalAmount;
    private String approvalCurrency;
    private Date requiredStaDate;
    
    public Date getReceivedDateTime() {
        return receivedDateTime;
    }
    public void setReceivedDateTime(Date receivedDateTime) {
        this.receivedDateTime = receivedDateTime;
    }
    public String getOutboundLocationId() {
        if (StringUtils.isEmpty(this.outboundLocationId)) {
            return ASSIGN_BLANK_SPACE_IF_MANDATORY;
        }
        return outboundLocationId;
    }
    public void setOutboundLocationId(String outboundLocationId) {
        this.outboundLocationId = outboundLocationId;
    }
    public Date getOutboundStartDate() {
        return outboundStartDate;
    }
    public void setOutboundStartDate(Date outboundStartDate) {
        this.outboundStartDate = outboundStartDate;
    }
    public String getMailFormId() {
        if (StringUtils.isEmpty(this.mailFormId)) {
            return ASSIGN_BLANK_SPACE_IF_MANDATORY;
        }
        return mailFormId;
    }
    public void setMailFormId(String mailFormId) {
        this.mailFormId = mailFormId;
    }
    public double getApprovalAmount() {
        return approvalAmount;
    }
    public void setApprovalAmount(double approvalAmount) {
        this.approvalAmount = approvalAmount;
    }
    public String getApprovalCurrency() {
        return approvalCurrency;
    }
    public void setApprovalCurrency(String approvalCurrency) {
        this.approvalCurrency = approvalCurrency;
    }
    public Date getRequiredStaDate() {
        return requiredStaDate;
    }
    public void setRequiredStaDate(Date requiredStaDate) {
        this.requiredStaDate = requiredStaDate;
    }
}
