package com.volvo.gloria.materialRequestProxy.c;

import java.io.Serializable;

public class FinanceMaterialDTO implements Serializable {

    private static final long serialVersionUID = -1277142184879039316L;

    private String projectId;
    private String glAccount;
    private String costCenter;
    private String wbsCode;
    private String internalOrderNoSAP;
    private String companyCode;
    
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
    public String getInternalOrderNoSAP() {
        return internalOrderNoSAP;
    }
    public void setInternalOrderNoSAP(String internalOrderNoSAP) {
        this.internalOrderNoSAP = internalOrderNoSAP;
    }
    public String getCompanyCode() {
        return companyCode;
    }
    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }
    
}
