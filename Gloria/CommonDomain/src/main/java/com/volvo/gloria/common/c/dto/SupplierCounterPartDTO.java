package com.volvo.gloria.common.c.dto;

import java.io.Serializable;

/**
 * Data transfer class for SupplierCounterPart Details.
 */
public class SupplierCounterPartDTO implements Serializable {

    private static final long serialVersionUID = -6855815381462963645L;

    private long id;
    private long version;
    private String shipToId;
    private String ppSuffix;
    private String transitAddressId;
    private String domesticInvoiceId;
    private String internationalInvoiceId;
    private String comment;
    private String materialUserId;
    private String companyCode;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getShipToId() {
        return shipToId;
    }

    public void setShipToId(String shipToId) {
        this.shipToId = shipToId;
    }

    public String getPpSuffix() {
        return ppSuffix;
    }

    public void setPpSuffix(String ppSuffix) {
        this.ppSuffix = ppSuffix;
    }

    public String getTransitAddressId() {
        return transitAddressId;
    }

    public void setTransitAddressId(String transitAddressId) {
        this.transitAddressId = transitAddressId;
    }

    public String getDomesticInvoiceId() {
        return domesticInvoiceId;
    }

    public void setDomesticInvoiceId(String domesticInvoiceId) {
        this.domesticInvoiceId = domesticInvoiceId;
    }

    public String getInternationalInvoiceId() {
        return internationalInvoiceId;
    }

    public void setInternationalInvoiceId(String internationalInvoiceId) {
        this.internationalInvoiceId = internationalInvoiceId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getMaterialUserId() {
        return materialUserId;
    }

    public void setMaterialUserId(String materialUserId) {
        this.materialUserId = materialUserId;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }
}
