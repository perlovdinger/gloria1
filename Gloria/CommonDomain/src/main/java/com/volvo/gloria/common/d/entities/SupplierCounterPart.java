package com.volvo.gloria.common.d.entities;

import java.io.Serializable;

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
 * Entity class for SupplierCounterPart.
 */
@Entity
@Table(name = "SUPPLIER_COUNTER_PART")
public class SupplierCounterPart implements GenericEntity<Long>, Serializable {

    private static final long serialVersionUID = -2905094609220470262L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SUPPLIER_COUNTER_PART_OID")
    private long supplierCounterPartOid;

    @Version
    private long version;

    private String shipToId;
    private String ppSuffix;
    private String transitAddressId;
    private String domesticInvoiceId;
    private String internationalInvoiceId;
    private String comment;
    private String materialUserId;
    private String companyCode;
    private boolean disabledProcure;

    @ManyToOne
    @JoinColumn(name = "DELIVERY_FOLLOW_UP_TEAM_OID")
    private DeliveryFollowUpTeam deliveryFollowUpTeam;

    public long getSupplierCounterPartOid() {
        return supplierCounterPartOid;
    }

    public void setSupplierCounterPartOid(long supplierCounterPartOid) {
        this.supplierCounterPartOid = supplierCounterPartOid;
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

    public DeliveryFollowUpTeam getDeliveryFollowUpTeam() {
        return deliveryFollowUpTeam;
    }

    public void setDeliveryFollowUpTeam(DeliveryFollowUpTeam deliveryFollowUpTeam) {
        this.deliveryFollowUpTeam = deliveryFollowUpTeam;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    @Override
    public Long getId() {
        return supplierCounterPartOid;
    }

    public boolean isDisabledProcure() {
        return disabledProcure;
    }

    public void setDisabledProcure(boolean disabledProcure) {
        this.disabledProcure = disabledProcure;
    }
    

}
