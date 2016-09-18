package com.volvo.gloria.authorization.d.entities;

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
 * UserCompanyCode entity for user.
 */
@Entity
@Table(name = "USER_COMPANY_CODE")
public class UserCompanyCode implements GenericEntity<Long> {

    private static final long serialVersionUID = -7021111963571067901L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_COMPANY_CODE_OID")
    private long userCompanyCodeOid;

    @ManyToOne
    @JoinColumn(name = "GLORIA_USER_OID")
    private GloriaUser gloriaUser;
    
    @Version
    private long version;
    private String code;

    @Override
    public Long getId() {
        return version;
    }

    @Override
    public long getVersion() {
        return userCompanyCodeOid;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public GloriaUser getGloriaUser() {
        return gloriaUser;
    }

    public void setGloriaUser(GloriaUser gloriaUser) {
        this.gloriaUser = gloriaUser;
    }
}
