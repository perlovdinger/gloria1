package com.volvo.gloria.warehouse.d.entities;

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
 * Entity class for Baysetting.
 */
@Entity
@Table(name = "BAYSETTING")
public class BaySetting implements Serializable {

    private static final long serialVersionUID = 487401540642329834L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BAY_SETTING_OID")
    private Long baySettingOid;
    
    @Version
    private long version;
    
    private String bayCode;
    
    private long numberOfLevels;
    
    private long numberOfPositions;
    
    @ManyToOne
    @JoinColumn(name = "AISLE_RACK_ROW_OID")
    private AisleRackRow aisleRackRow;
    
    public Long getBaySettingOid() {
        return baySettingOid;
    }

    public void setBaySettingOid(Long baySettingOid) {
        this.baySettingOid = baySettingOid;
    }

    public long getVersion() {
        return version;
    }

    public String getBayCode() {
        return bayCode;
    }
   
    public void setBayCode(String bayCode) {
        this.bayCode = bayCode;
    }
   
    public long getNumberOfLevels() {
        return numberOfLevels;
    }
   
    public void setNumberOfLevels(long numberOfLevels) {
        this.numberOfLevels = numberOfLevels;
    }
   
    public long getNumberOfPositions() {
        return numberOfPositions;
    }
    
    public void setNumberOfPositions(long numberOfPositions) {
        this.numberOfPositions = numberOfPositions;
    }
    
    public AisleRackRow getAisleRackRow() {
        return aisleRackRow;
    }

    public void setAisleRackRow(AisleRackRow aisleRackRow) {
        this.aisleRackRow = aisleRackRow;
    }

}
