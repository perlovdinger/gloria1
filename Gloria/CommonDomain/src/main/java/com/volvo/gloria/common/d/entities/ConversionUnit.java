package com.volvo.gloria.common.d.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import com.volvo.gloria.util.persistence.GenericEntity;

/**
 * Entity class for CONVERSION_UNIT.
 */
@Entity
@Table(name = "CONVERSION_UNIT")
public class ConversionUnit implements GenericEntity<Long>, Serializable {

    private static final long serialVersionUID = -5045196617283292199L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONVERSION_UNIT_OID")
    private long conversionUnitOID;

    @Version
    private long version;
    
    private String applFrom;
    private String codeFrom;
    private String applTo;
    private String codeTo;
    private long dividedBy;

    public long getConversionUnitOID() {
        return conversionUnitOID;
    }

    public void setConversionUnitOID(long conversionUnitOID) {
        this.conversionUnitOID = conversionUnitOID;
    }

    public String getApplFrom() {
        return applFrom;
    }

    public void setApplFrom(String applFrom) {
        this.applFrom = applFrom;
    }

    public String getCodeFrom() {
        return codeFrom;
    }

    public void setCodeFrom(String codeFrom) {
        this.codeFrom = codeFrom;
    }

    public String getApplTo() {
        return applTo;
    }

    public void setApplTo(String applTo) {
        this.applTo = applTo;
    }

    public String getCodeTo() {
        return codeTo;
    }

    public void setCodeTo(String codeTo) {
        this.codeTo = codeTo;
    }

    public long getDividedBy() {
        return dividedBy;
    }

    public void setDividedBy(long dividedBy) {
        this.dividedBy = dividedBy;
    }
    
    @Override
    public Long getId() {
        return conversionUnitOID;
    }

    @Override
    public long getVersion() {
        return version;
    }
}
