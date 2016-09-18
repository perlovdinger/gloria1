package com.volvo.gloria.procurematerial.c.dto;

import java.io.Serializable;
import java.util.Date;

import com.volvo.gloria.util.paging.c.PageResults;

/**
 * RequestHeader DTO.
 */
public class MaterialHeaderDTO implements Serializable, PageResults {

    private static final long serialVersionUID = -4635990243386273835L;
    private Long id;
    private long version;
    private String projectId;
    private String requesterName;
    private String assignedMaterialControllerId;
    private String companyCode;
    private String buildId;
    private String buildName;
    private String mtrlRequestVersion;
    private Date receivedDateTime;
    private String requesterUserId;
    private Date outboundStartDate;
    private String outboundLocationId;
    private String referenceGroup;
    private String referenceId;
    private String assignedMaterialControllerName;
    private String assignedMaterialControllerTeam;
    private String requestType;
    private boolean unAssignable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getAssignedMaterialControllerId() {
        return assignedMaterialControllerId;
    }

    public void setAssignedMaterialControllerId(String assignedMaterialControllerId) {
        this.assignedMaterialControllerId = assignedMaterialControllerId;
    }
    
    public String getCompanyCode() {
        return companyCode;
    }
    
    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getBuildId() {
        return buildId;
    }

    public void setBuildId(String buildId) {
        this.buildId = buildId;
    }
    
    public String getBuildName() {
        return buildName;
    }
    
    public void setBuildName(String buildName) {
        this.buildName = buildName;
    }

    public Date getReceivedDateTime() {
        return receivedDateTime;
    }

    public void setReceivedDateTime(Date receivedDateTime) {
        this.receivedDateTime = receivedDateTime;
    }

    public String getRequesterUserId() {
        return requesterUserId;
    }

    public void setRequesterUserId(String requesterUserId) {
        this.requesterUserId = requesterUserId;
    }

    public Date getOutboundStartDate() {
        return outboundStartDate;
    }

    public void setOutboundStartDate(Date outboundStartDate) {
        this.outboundStartDate = outboundStartDate;
    }

    public String getOutboundLocationId() {
        return outboundLocationId;
    }

    public void setOutboundLocationId(String outboundLocationId) {
        this.outboundLocationId = outboundLocationId;
    }

    public String getReferenceGroup() {
        return referenceGroup;
    }

    public void setReferenceGroup(String referenceGroup) {
        this.referenceGroup = referenceGroup;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getMtrlRequestVersion() {
        return mtrlRequestVersion;
    }

    public void setMtrlRequestVersion(String mtrlRequestVersion) {
        this.mtrlRequestVersion = mtrlRequestVersion;
    }

    public String getAssignedMaterialControllerName() {
        return assignedMaterialControllerName;
    }

    public void setAssignedMaterialControllerName(String assignedMaterialControllerName) {
        this.assignedMaterialControllerName = assignedMaterialControllerName;
    }
    
    public String getAssignedMaterialControllerTeam() {
        return assignedMaterialControllerTeam;
    }
    
    public void setAssignedMaterialControllerTeam(String assignedMaterialControllerTeam) {
        this.assignedMaterialControllerTeam = assignedMaterialControllerTeam;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }
    
    public boolean isUnAssignable() {
        return unAssignable;
    }
    
    public void setUnAssignable(boolean unAssignable) {
        this.unAssignable = unAssignable;
    }
}
