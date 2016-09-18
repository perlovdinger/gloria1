package com.volvo.gloria.warehouse.d.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.volvo.gloria.util.persistence.GenericEntity;
import com.volvo.gloria.warehouse.c.BaySides;
import com.volvo.gloria.warehouse.c.Setup;

/**
 * Entity implementation class for Entity: Aisle.
 *
 */
@Entity
@Table(name = "AISLERACKROW")
public class AisleRackRow implements GenericEntity<Long>, Serializable {
    private static final long serialVersionUID = 3746488378078649865L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AISLE_RACK_ROW_OID")
    private Long aisleRackRowOid;

    @Version
    private long version;
    
    private Setup setUp;

    private String code;
    @Enumerated(EnumType.STRING)
    private BaySides baySides;
    private long numberOfBay;

    @ManyToOne
    @JoinColumn(name = "ZONE_OID")
    private Zone zone;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "aisleRackRow")
    private List<BaySetting> baySettings = new ArrayList<BaySetting>();

    public long getVersion() {
        return version;
    }

    public Long getAisleRackRowOid() {
        return aisleRackRowOid;
    }

    public void setAisleRackRowOid(Long aisleRackRowOid) {
        this.aisleRackRowOid = aisleRackRowOid;
    }

    public Setup getSetUp() {
        return setUp;
    }

    public void setSetUp(Setup setUp) {
        this.setUp = setUp;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BaySides getBaySides() {
        return baySides;
    }

    public void setBaySides(BaySides baySides) {
        this.baySides = baySides;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }


    public long getNumberOfBay() {
        return numberOfBay;
    }

    public void setNumberOfBay(long numberOfBay) {
        this.numberOfBay = numberOfBay;
    }
    
    public List<BaySetting> getBaySettings() {
        return baySettings;
    }

    public void setBaySettings(List<BaySetting> baySettings) {
        this.baySettings = baySettings;
    }


    @Override
    public Long getId() {
        return aisleRackRowOid;
    }

}
