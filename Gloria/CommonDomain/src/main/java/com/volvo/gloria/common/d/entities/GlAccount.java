package com.volvo.gloria.common.d.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.volvo.gloria.util.persistence.GenericEntity;

/**
 * Entinty class for General Ledger Account information.
 */
@Entity
@Table(name = "GLACCOUNT")
public class GlAccount implements GenericEntity<Long>{
    private static final long serialVersionUID = 5597771026742783673L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GLACCOUNT_OID")
    private long glAccountOid;
    @Version
    private long version;
    private String accountNumber;
    private String accountName;
    private String accountDescription;
    
    @ManyToOne
    @JoinColumn(name = "COMPANY_CODE_OID")
    private CompanyCode companyCode;

    public long getglAccountOid() {
        return glAccountOid;
    }

    public void setgIAccountOid(long glAccountOid) {
        this.glAccountOid = glAccountOid;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountDescription() {
        return accountDescription;
    }

    public void setAccountDescription(String accountDescription) {
        this.accountDescription = accountDescription;
    }

    @Override
    public Long getId() {
        return glAccountOid;
    }

    @Override
    public long getVersion() {
        return version;
    }
    
    public CompanyCode getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(CompanyCode companyCode) {
        this.companyCode = companyCode;
    }
}
