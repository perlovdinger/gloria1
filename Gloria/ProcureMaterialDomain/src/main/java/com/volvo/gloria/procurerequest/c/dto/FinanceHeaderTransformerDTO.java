package com.volvo.gloria.procurerequest.c.dto;

import java.io.Serializable;

/**
 * 
 */
public class FinanceHeaderTransformerDTO implements Serializable {

    private static final long serialVersionUID = -4191059710786007665L;
    
    private long id;
    private String projectId;
    private String glAccount;
    private String costCenter;
    private String wbsCode;
    private String internalorderNoSAP;
    private String mailFormId;
    private String companyCode;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getProjectId() {
        return projectId;
    }
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
    public String getGlAccount() {
        return glAccount;
    }
    public void setGlAccount(String glAccount) {
        this.glAccount = glAccount;
    }
    public String getCostCenter() {
        return costCenter;
    }
    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }
    public String getWbsCode() {
        return wbsCode;
    }
    public void setWbsCode(String wbsCode) {
        this.wbsCode = wbsCode;
    }
    public String getInternalorderNoSAP() {
        return internalorderNoSAP;
    }
    public void setInternalorderNoSAP(String internalorderNoSAP) {
        this.internalorderNoSAP = internalorderNoSAP;
    }
    public String getMailFormId() {
        return mailFormId;
    }
    public void setMailFormId(String mailFormId) {
        this.mailFormId = mailFormId;
    }
    public String getCompanyCode() {
        return companyCode;
    }
    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }
    
}
