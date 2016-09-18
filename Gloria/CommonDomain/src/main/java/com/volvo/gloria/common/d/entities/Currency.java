package com.volvo.gloria.common.d.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.volvo.gloria.util.persistence.GenericEntity;

/**
 * Entity class for Currency.
 * 
 */
@Entity
@Table(name = "CURRENCY")
public class Currency implements GenericEntity<Long>, Serializable {

    private static final long serialVersionUID = -2710272267471489712L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CURRENCY_OID")
    private long currencyOID;

    @Version
    private long version;
    private String code;
    private String name;
    private boolean suppressDecimal;
    private int displaySeq;

    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH }, mappedBy = "currency")
    private List<CurrencyRate> currencyRates = new ArrayList<CurrencyRate>();
    
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH }, fetch = FetchType.EAGER)
    @JoinTable(name = "COMPANY_CODE_CURRENCY", joinColumns = { @JoinColumn(name = "CURRENCY_OID") }, 
        inverseJoinColumns = { @JoinColumn(name = "COMPANY_CODE_OID") })
    private Set<CompanyCode> companyCodes = new HashSet<CompanyCode>();

    public long getCurrencyOID() {
        return currencyOID;
    }

    public void setCurrencyOID(long currencyOID) {
        this.currencyOID = currencyOID;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDisplaySeq() {
        return displaySeq;
    }

    public void setDisplaySeq(int displaySeq) {
        this.displaySeq = displaySeq;
    }

    public List<CurrencyRate> getCurrencyRates() {
        return currencyRates;
    }

    public void setCurrencyRates(List<CurrencyRate> currencyRates) {
        this.currencyRates = currencyRates;
    }

    @Override
    public Long getId() {
        return currencyOID;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public boolean isSuppressDecimal() {
        return suppressDecimal;
    }

    public void setSuppressDecimal(boolean suppressDecimal) {
        this.suppressDecimal = suppressDecimal;
    }

    public Set<CompanyCode> getCompanyCodes() {
        return companyCodes;
    }

    public void setCompanyCodes(Set<CompanyCode> companyCodes) {
        this.companyCodes = companyCodes;
    }
}
