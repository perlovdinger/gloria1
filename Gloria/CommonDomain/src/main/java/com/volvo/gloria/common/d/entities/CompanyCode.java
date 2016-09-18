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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.volvo.gloria.util.persistence.GenericEntity;

/**
 * Entity class for CompanyCode.
 * 
 */
@Entity
@Table(name = "COMPANY_CODE")
public class CompanyCode implements GenericEntity<Long>, Serializable {
    private static final long serialVersionUID = -4816534497828711273L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMPANY_CODE_OID")
    private long companyCodeOid;

    @Version
    private long version;
    private String code;
    private String name;
    private boolean receivingGoods;
    private boolean sendPOtoSAP;
    private boolean sendGRtoSAP;
    private String sapPurchaseOrg;
    private String sapQuantityBlockReceiverId;
    private String defaultCurrency;
    private boolean jointVenture;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "companyCode")
    private List<CostCenter> costCenters = new ArrayList<CostCenter>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "companyCode")
    private List<GlAccount> glAccounts = new ArrayList<GlAccount>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "companyCode")
    private List<WbsElement> wbsElements = new ArrayList<WbsElement>();
    
    @ManyToMany(mappedBy = "companyCodes")
    private Set<Currency> currencies = new HashSet<Currency>();
    
    @ManyToOne
    @JoinColumn(name = "COMPANY_GROUP_OID")
    private CompanyGroup companyGroup;

    public long getCompanyCodeOid() {
        return companyCodeOid;
    }

    public void setCompanyCodeOid(long companyCodeOid) {
        this.companyCodeOid = companyCodeOid;
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
 
    public boolean isReceivingGoods() {
        return receivingGoods;
    }
    
    public void setReceivingGoods(boolean receivingGoods) {
        this.receivingGoods = receivingGoods;
    }
    
    public boolean isSendPOtoSAP() {
        return sendPOtoSAP;
    }

    public void setSendPOtoSAP(boolean sendPOtoSAP) {
        this.sendPOtoSAP = sendPOtoSAP;
    }

    public boolean isSendGRtoSAP() {
        return sendGRtoSAP;
    }

    public void setSendGRtoSAP(boolean sendGRtoSAP) {
        this.sendGRtoSAP = sendGRtoSAP;
    }

    public String getSapPurchaseOrg() {
        return sapPurchaseOrg;
    }

    public void setSapPurchaseOrg(String sapPurchaseOrg) {
        this.sapPurchaseOrg = sapPurchaseOrg;
    }

    public String getSapQuantityBlockReceiverId() {
        return sapQuantityBlockReceiverId;
    }

    public void setSapQuantityBlockReceiverId(String sapQuantityBlockReceiverId) {
        this.sapQuantityBlockReceiverId = sapQuantityBlockReceiverId;
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }
    
    public boolean isJointVenture() {
        return jointVenture;
    }
    
    public void setJointVenture(boolean jointVenture) {
        this.jointVenture = jointVenture;
    }

    @Override
    public Long getId() {
        return companyCodeOid;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public List<CostCenter> getCostCenters() {
        return costCenters;
    }

    public void setCostCenters(List<CostCenter> costCenters) {
        this.costCenters = costCenters;
    }

    public List<GlAccount> getGlAccounts() {
        return glAccounts;
    }

    public void setGlAccounts(List<GlAccount> glAccounts) {
        this.glAccounts = glAccounts;
    }

    public List<WbsElement> getWbsElements() {
        return wbsElements;
    }

    public void setWbsElements(List<WbsElement> wbsElements) {
        this.wbsElements = wbsElements;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public CompanyGroup getCompanyGroup() {
        return companyGroup;
    }

    public void setCompanyGroup(CompanyGroup companyGroup) {
        this.companyGroup = companyGroup;
    }

    public Set<Currency> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(Set<Currency> currencies) {
        this.currencies = currencies;
    }
}
