package com.volvo.gloria.procurerequest.c.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Request Header Version DTO.
 */
public class RequestHeaderVersionTransformerDTO implements Serializable {

    private static final long serialVersionUID = 4501234754785761753L;

    private long headerVersion;
    private Date receivedDateTime;
    private String referenceGroup;
    private String outboundLocationId;
    private Date outboundStartDate;
    private Date buildStartDate;
    private String requesterUserId;
    private String requesterName;
    private String requesterNotes;
    private String contactPersonId;
    private String contactPersonName;
    private String materialControllerUserId;
    private String materialControllerName;
    private String contactPersonDepartment;
    private Date requiredSTADate;
    private boolean containingLines;

    public long getHeaderVersion() {
        return headerVersion;
    }

    public void setHeaderVersion(long headerVersion) {
        this.headerVersion = headerVersion;
    }

    public Date getReceivedDateTime() {
        return receivedDateTime;
    }

    public void setReceivedDateTime(Date receivedDateTime) {
        this.receivedDateTime = receivedDateTime;
    }

    public String getReferenceGroup() {
        return referenceGroup;
    }

    public void setReferenceGroup(String referenceGroup) {
        this.referenceGroup = referenceGroup;
    }
    
    public String getOutboundLocationId() {
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

    public Date getBuildStartDate() {
        return buildStartDate;
    }

    public void setBuildStartDate(Date buildStartDate) {
        this.buildStartDate = buildStartDate;
    }

    public String getRequesterUserId() {
        return requesterUserId;
    }

    public void setRequesterUserId(String requesterUserId) {
        this.requesterUserId = requesterUserId;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getRequesterNotes() {
        return requesterNotes;
    }

    public void setRequesterNotes(String requesterNotes) {
        this.requesterNotes = requesterNotes;
    }

    public String getContactPersonId() {
        return contactPersonId;
    }

    public void setContactPersonId(String contactPersonId) {
        this.contactPersonId = contactPersonId;
    }

    public String getContactPersonName() {
        return contactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }
    
    public String getMaterialControllerName() {
        return materialControllerName;
    }
    
    public void setMaterialControllerName(String materialControllerName) {
        this.materialControllerName = materialControllerName;
    }
    
    public String getMaterialControllerUserId() {
        return materialControllerUserId;
    }
    
    public void setMaterialControllerUserId(String materialControllerUserId) {
        this.materialControllerUserId = materialControllerUserId;
    }

    public String getContactPersonDepartment() {
        return contactPersonDepartment;
    }

    public void setContactPersonDepartment(String contactPersonDepartment) {
        this.contactPersonDepartment = contactPersonDepartment;
    }

    public Date getRequiredSTADate() {
        return requiredSTADate;
    }

    public void setRequiredSTADate(Date requiredSTADate) {
        this.requiredSTADate = requiredSTADate;
    }

    public boolean isContainingLines() {
        return containingLines;
    }

    public void setContainingLines(boolean containingLines) {
        this.containingLines = containingLines;
    }

}
