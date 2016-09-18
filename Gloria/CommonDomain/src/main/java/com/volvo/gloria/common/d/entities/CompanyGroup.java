package com.volvo.gloria.common.d.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "COMPANY_GROUP")
public class CompanyGroup implements Serializable {

    private static final long serialVersionUID = 5700426017789168836L;
   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMPANY_GROUP_OID")
    private long companyGroupOid;

    @Version
    private long version;
    private String code;
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "companyGroup")
    private List<CompanyCode> companyCodes = new ArrayList<CompanyCode>();
    
    public long getCompanyGroupOid() {
        return companyGroupOid;
    }
    public void setCompanyGroupOid(long companyGroupOid) {
        this.companyGroupOid = companyGroupOid;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public long getVersion() {
        return version;
    }
    public List<CompanyCode> getCompanyCodes() {
        return companyCodes;
    }
    public void setCompanyCodes(List<CompanyCode> companyCodes) {
        this.companyCodes = companyCodes;
    }
}
