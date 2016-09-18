package com.volvo.gloria.procurematerial.d.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * Entity implementation class for Entity: FinanceHeaderX.
 * 
 */
@Entity
@Table(name = "FINANCE_HEADER")
public class FinanceHeader implements Serializable {

    private static final long serialVersionUID = -5155947472953181062L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FINANCE_HEADER_OID")
    private long financeHeaderXOid;

    @Version
    private long version;

    private String projectId;
    private String glAccount;
    private String costCenter;
    private String wbsCode;
    private String internalOrderNoSAP;
    private String companyCode;
    
    public long getFinanceHeaderXOid() {
        return financeHeaderXOid;
    }

    public void setFinanceHeaderXOid(long financeHeaderXOid) {
        this.financeHeaderXOid = financeHeaderXOid;
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
