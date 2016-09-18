package com.volvo.gloria.procurematerial.d.entities;

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

/**
 * 
 * Entity class for Buyer.
 */
@Entity
@Table(name = "BUYER")
public class Buyer implements Serializable {

    
    private static final long serialVersionUID = -3082287242930624631L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BUYER_OID")
    private long buyerOid;
    
    @Version
    private long version;
    private String code;
    private String name;

 
    @ManyToOne
    @JoinColumn(name = "PURCHASE_ORGANISATION_OID")
    private PurchaseOrganisation purchaseOrganisation;
    
    public long getBuyerOid() {
        return buyerOid;
    }

    public void setBuyerOid(long buyerCodeOid) {
        this.buyerOid = buyerCodeOid;
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

    public PurchaseOrganisation getPurchaseOrganisation() {
        return purchaseOrganisation;
    }

    public void setPurchaseOrganisation(PurchaseOrganisation purchaseOrganisation) {
        this.purchaseOrganisation = purchaseOrganisation;
    }

    public long getVersion() {
        return version;
    }
}
