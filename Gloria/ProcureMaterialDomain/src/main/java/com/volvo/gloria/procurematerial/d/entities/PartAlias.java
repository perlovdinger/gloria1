package com.volvo.gloria.procurematerial.d.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.volvo.gloria.common.c.Domain;
import com.volvo.gloria.util.persistence.GenericEntity;

/**
 * 
 */
@Entity
@Table(name = "PART_ALIAS")
public class PartAlias implements GenericEntity<Long>, Serializable {

    private static final long serialVersionUID = 863595594055807675L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PART_ALIAS_OID")
    private long partAliasOid;

    @Version
    private long version;

    @Enumerated(EnumType.STRING)
    private Domain domain;

    private String aliasPartNumber;

    @ManyToOne
    @JoinColumn(name = "PART_NUMBER_OID")
    private PartNumber partNumber;

    public long getPartAliasOid() {
        return partAliasOid;
    }

    public void setPartAliasOid(long partAliasOid) {
        this.partAliasOid = partAliasOid;
    }

    public Domain getDomain() {
        return domain;
    }

    public String getAliasPartNumber() {
        return aliasPartNumber;
    }

    public void setAliasPartNumber(String aliasPartNumber) {
        this.aliasPartNumber = aliasPartNumber;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public PartNumber getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(PartNumber partNumber) {
        this.partNumber = partNumber;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public Long getId() {
        return partAliasOid;
    }

    @Override
    public long getVersion() {
        return version;
    }
}
