package com.volvo.gloria.procurematerial.c.dto;

import java.io.Serializable;
/**
 * Data class for finance header details.
 */
public class FinanceHeaderDTO implements Serializable {
    
    private static final long serialVersionUID = 7744122566180481141L;
    private long id;
    private long version;
    private String glAccount;
    private String costCenter;
    private String internalOrdereNoSAP;
    private String mailFormId;
    private String wbsCode;
    
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
    public String getInternalOrdereNoSAP() {
        return internalOrdereNoSAP;
    }
    public void setInternalOrdereNoSAP(String internalOrdereNoSAP) {
        this.internalOrdereNoSAP = internalOrdereNoSAP;
    }
    public String getMailFormId() {
        return mailFormId;
    }
    public void setMailFormId(String mailFormId) {
        this.mailFormId = mailFormId;
    }
    public String getWbsCode() {
        return wbsCode;
    }
    public void setWbsCode(String wbsCode) {
        this.wbsCode = wbsCode;
    }
  
    
}
