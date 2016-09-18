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
import com.volvo.gloria.procurematerial.d.type.supplier.SupplierType;

/**
 * Entity class Supplier.
 * 
 */
@Entity
@Table(name = "SUPPLIER")
public class Supplier implements Serializable {
    private static final long serialVersionUID = 8584561383862473476L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SUPPLIER_OID")
    private long supplierOid;

    @Version
    private long version;

    @Enumerated(EnumType.STRING)
    private SupplierType supplierType;

    private String supplierId;
    private String supplierName;
    private boolean alias;

    @Enumerated(EnumType.STRING)
    private Domain domain;

    private String partAffiliation;
    private String partNumber;
    private String partVersion;
    private String currency;
    private String priceUnit;
    private double unitPrice;

    @ManyToOne
    @JoinColumn(name = "PROCURE_LINE_OID")
    private ProcureLine procureLine;

    public long getSupplierOid() {
        return supplierOid;
    }

    public void setSupplierOid(long supplierOid) {
        this.supplierOid = supplierOid;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public SupplierType getSupplierType() {
        return supplierType;
    }

    public void setSupplierType(SupplierType supplierType) {
        this.supplierType = supplierType;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public boolean isAlias() {
        return alias;
    }

    public void setAlias(boolean alias) {
        this.alias = alias;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public String getPartAffiliation() {
        return partAffiliation;
    }

    public void setPartAffiliation(String partAffiliation) {
        this.partAffiliation = partAffiliation;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getPartVersion() {
        return partVersion;
    }

    public void setPartVersion(String partVersion) {
        this.partVersion = partVersion;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPriceUnit() {
        return priceUnit;
    }

    public void setPriceUnit(String priceUnit) {
        this.priceUnit = priceUnit;
    }

    public ProcureLine getProcureLine() {
        return procureLine;
    }

    public void setProcureLine(ProcureLine procureLine) {
        this.procureLine = procureLine;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
}
