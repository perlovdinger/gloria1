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
 * Entity class for Quality Document.
 */
@Entity
@Table(name = "QUALITY_DOCUMENT")
public class QualityDocument implements GenericEntity<Long>, Serializable {

    private static final long serialVersionUID = 2881594848098913110L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QUALITY_DOCUMENT_OID")
    private long qualityDocumentOID;

    @Version
    private long version;

    private String code;

    private String name;

    private long displaySeq;

    public long getQualityDocumentOID() {
        return qualityDocumentOID;
    }

    public void setQualityDocumentOID(long qualityDocumentOID) {
        this.qualityDocumentOID = qualityDocumentOID;
    }

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

    public long getDisplaySeq() {
        return displaySeq;
    }

    public void setDisplaySeq(long displaySeq) {
        this.displaySeq = displaySeq;
    }

    @Override
    public Long getId() {
        return qualityDocumentOID;
    }
}
