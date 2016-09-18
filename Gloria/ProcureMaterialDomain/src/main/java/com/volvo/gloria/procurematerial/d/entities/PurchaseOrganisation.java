package com.volvo.gloria.procurematerial.d.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.volvo.gloria.util.persistence.GenericEntity;

@Entity
@Table(name = "PURCHASE_ORGANISATION")
public class PurchaseOrganisation implements GenericEntity<Long>, Serializable {

    private static final long serialVersionUID = 8680280707063575044L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PURCHASE_ORGANISATION_OID")
    private long purchaseOrganisationOid;

    @Version
    private long version;
    private String organisationCode;
    private String organisationName;

    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST, 
               CascadeType.REFRESH, CascadeType.DETACH }, mappedBy = "purchaseOrganisation")
    private List<Buyer> buyers = new ArrayList<Buyer>();

    @Override
    public Long getId() {
        return purchaseOrganisationOid;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public long getPurchaseOrganisationOid() {
        return purchaseOrganisationOid;
    }

    public void setPurchaseOrganisationOid(long purchaseOrganisationOid) {
        this.purchaseOrganisationOid = purchaseOrganisationOid;
    }

    public String getOrganisationCode() {
        return organisationCode;
    }

    public void setOrganisationCode(String organisationCode) {
        this.organisationCode = organisationCode;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public List<Buyer> getBuyers() {
        return buyers;
    }

    public void setBuyers(List<Buyer> buyers) {
        this.buyers = buyers;
    }

}
