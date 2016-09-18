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
 * Entity class for UNIT_OF_MEASURE.
 */
@Entity
@Table(name = "UNIT_OF_MEASURE")
public class UnitOfMeasure implements GenericEntity<Long>, Serializable {

    private static final long serialVersionUID = 5801862610676145053L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UNIT_OF_MEASURE_OID")
    private long unitOfMeasureOID;

    @Version
    private long version;
    private String code;   
    private String name;
    private boolean gloriaCode;
    private int displaySeq;

    public long getUnitOfMeasureOID() {
        return unitOfMeasureOID;
    }

    public void setUnitOfMeasureOID(long unitOfMeasureOID) {
        this.unitOfMeasureOID = unitOfMeasureOID;
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
        return unitOfMeasureOID;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public boolean isGloriaCode() {
        return gloriaCode;
    }
    
    public void setGloriaCode(boolean gloriaCode) {
        this.gloriaCode = gloriaCode;
    }
}
