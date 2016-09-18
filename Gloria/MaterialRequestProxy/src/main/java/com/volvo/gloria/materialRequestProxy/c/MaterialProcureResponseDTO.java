package com.volvo.gloria.materialRequestProxy.c;

import java.io.Serializable;
import java.util.Date;

public class MaterialProcureResponseDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -563296093256659282L;

    private String logicalId;
    private Date createdDate;
    private String userId;
    private String response;
    private String materialRequestId;
    private String materialRequest;
    private String component;
    private String changeId;
    private String changeTechId;

    public String getChangeTechId() {
        return changeTechId;
    }

    public void setChangeTechId(String changeTechId) {
        this.changeTechId = changeTechId;
    }

    public String getLogicalId() {
        return logicalId;
    }

    public void setLogicalId(String logicalId) {
        this.logicalId = logicalId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getMaterialRequestId() {
        return materialRequestId;
    }

    public void setMaterialRequestId(String materialRequestId) {
        this.materialRequestId = materialRequestId;
    }

    public String getMaterialRequest() {
        return materialRequest;
    }

    public void setMaterialRequest(String materialRequest) {
        this.materialRequest = materialRequest;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getChangeId() {
        return changeId;
    }

    public void setChangeId(String changeId) {
        this.changeId = changeId;
    }

}
