package com.volvo.gloria.authorization.d.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.volvo.gloria.util.persistence.GenericEntity;

/**
 * ReportFilter entity attributes.
 * 
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "REPORT_FILTER")
public abstract class ReportFilter implements GenericEntity<Long>, Serializable {

    private static final long serialVersionUID = 6009402175943349984L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REPORT_FILTER_OID")
    private long reportFIlterOID;
    
    @Version
    private long version;

    private String type;

    private String name;

    @ManyToOne
    @JoinColumn(name = "GLORIA_USER_OID")
    private GloriaUser gloriaUser;

    public long getReportFIlterOID() {
        return reportFIlterOID;
    }

    public void setReportFIlterOID(long reportFIlterOID) {
        this.reportFIlterOID = reportFIlterOID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GloriaUser getGloriaUser() {
        return gloriaUser;
    }

    public void setGloriaUser(GloriaUser gloriaUser) {
        this.gloriaUser = gloriaUser;
    }
    
    @Override
    public Long getId() {
        return reportFIlterOID;
    }

    public void setVersion(long version) {
        this.version = version;
    }
    
    @Override
    public long getVersion() {
        return version;
    }
}
