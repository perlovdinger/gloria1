package com.volvo.gloria.common.d.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import com.volvo.gloria.util.c.GloriaParams;
import com.volvo.gloria.util.persistence.GenericEntity;

/**
 * Entity class for PART_AFFILIATION.
 */
@Entity
@Table(name = "PART_AFFILIATION")
public class PartAffiliation implements GenericEntity<Long>, Serializable {

    private static final long serialVersionUID = -5077141319546228341L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PART_AFFILIATION_OID")
    private long partAffiliationOID;

    @Version
    private long version;

    @Column(length = GloriaParams.PARTAFFILIATION_CODE_LENGTH)
    private String code;
    @Column(length = GloriaParams.PARTAFFILIATION_NAME_LENGTH)
    private String name;
    private int displaySeq;
    
    private boolean requestable;

    public boolean isRequestable() {
        return requestable;
    }

    public void setRequestable(boolean requestable) {
        this.requestable = requestable;
    }

    public long getPartAffiliationOID() {
        return partAffiliationOID;
    }

    public void setPartAffiliationOID(long partAffiliationOID) {
        this.partAffiliationOID = partAffiliationOID;
    }

    @Override
    public long getVersion() {
        return version;
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

    @Override
    public Long getId() {
        return partAffiliationOID;
    }
}
