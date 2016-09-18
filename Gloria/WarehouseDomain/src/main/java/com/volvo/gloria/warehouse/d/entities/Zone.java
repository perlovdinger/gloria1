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
import com.volvo.gloria.warehouse.c.ZoneType;

/**
 * Entity implementation class for entity Zon.
 * 
 */
@Entity
@Table(name = "Zone")
public class Zone implements GenericEntity<Long>, Serializable {
    private static final long serialVersionUID = 428501871610775677L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ZONE_OID")
    private Long zoneOid;

    @Version
    private long version;

    private String name;
    private String description;
    private String code;
    @Enumerated(EnumType.STRING)
    private ZoneType type;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "zone")
    private List<AisleRackRow> aisleRackRows = new ArrayList<AisleRackRow>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "zone")
    private List<BinLocation> binLocations = new ArrayList<BinLocation>();

    @ManyToOne
    @JoinColumn(name = "STORAGE_ROOM_OID")
    private StorageRoom storageRoom;

    public StorageRoom getStorageRoom() {
        return storageRoom;
    }

    public void setStorageRoom(StorageRoom storageRoom) {
        this.storageRoom = storageRoom;
    }

    public Long getZoneOid() {
        return zoneOid;
    }

    public void setZoneOid(Long zoneOid) {
        this.zoneOid = zoneOid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ZoneType getType() {
        return type;
    }

    public void setType(ZoneType type) {
        this.type = type;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public List<AisleRackRow> getAisleRackRows() {
        return aisleRackRows;
    }

    public void setAisleRackRows(List<AisleRackRow> aisleRackRows) {
        this.aisleRackRows = aisleRackRows;
    }

    public List<BinLocation> getBinLocations() {
        return binLocations;
    }

    public void setBinLocations(List<BinLocation> binLocations) {
        this.binLocations = binLocations;
    }

    @Override
    public Long getId() {
        return zoneOid;
    }

}
