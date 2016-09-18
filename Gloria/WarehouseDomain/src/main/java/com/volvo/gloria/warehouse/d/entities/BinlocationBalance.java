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

import com.volvo.gloria.warehouse.c.Deviation;

/**
 * Entity class for BinlocationBalance.
 */
@Entity
@Table(name = "BINLOCATION_BALANCE")
public class BinlocationBalance implements Serializable {

    private static final long serialVersionUID = 2073201021762957170L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BINLOCATION_BALANCE_OID")
    private long binlocationBalanceOid;

    @Version
    private long version;

    private String partAffiliation;

    private String partNumber;

    private String partVersion;

    private String partModification;

    private String partName;

    private long quantity;

    @Enumerated(EnumType.STRING)
    private Deviation deviation;

    @ManyToOne
    @JoinColumn(name = "WAREHOUSE_OID")
    private Warehouse warehouse;

    @ManyToOne
    @JoinColumn(name = "BIN_LOCATION_OID")
    private BinLocation binLocation;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "binlocationBalance")
    private List<Placement> placements = new ArrayList<Placement>();

    public long getBinlocationBalanceOid() {
        return binlocationBalanceOid;
    }

    public void setBinlocationBalanceOid(long binlocationBalanceOid) {
        this.binlocationBalanceOid = binlocationBalanceOid;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
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

    public String getPartModification() {
        return partModification;
    }

    public void setPartModification(String partModification) {
        this.partModification = partModification;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public Deviation getDeviation() {
        return deviation;
    }

    public void setDeviation(Deviation deviation) {
        this.deviation = deviation;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public BinLocation getBinLocation() {
        return binLocation;
    }

    public void setBinLocation(BinLocation binLocation) {
        this.binLocation = binLocation;
    }

    public List<Placement> getPlacements() {
        return placements;
    }

    public void setPlacements(List<Placement> placements) {
        this.placements = placements;
    }
}
