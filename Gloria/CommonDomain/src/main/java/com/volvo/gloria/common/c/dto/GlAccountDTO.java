package com.volvo.gloria.common.c.dto;

import java.io.Serializable;

import com.volvo.gloria.util.paging.c.PageResults;

/**
 * Data class for GlAccount details. 
 */
public class GlAccountDTO implements Serializable, PageResults {
   
    private static final long serialVersionUID = 572175018965097794L;

    private long id;
    private String accountNumber;
    private String companyCode;
    private String accountName;
    private String accountDescription;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    public String getCompanyCode() {
        return companyCode;
    }
    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
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
    
}
