package com.volvo.gloria.warehouse.c.dto;

import java.io.Serializable;
import java.util.List;

import com.volvo.gloria.warehouse.c.Allocation;

/**
 * Bin Location Transformer.
 */
public class BinLocationTransformerDTO implements Serializable {

    private static final long serialVersionUID = 4779503782841914735L;
    private long rackRow;
    private String aisleRackRowCode;
    private String bay;
    private long level;
    private long position;
    private String code;
    private Allocation allocation;
    private List<PlacementDTO> placementDTOs;
    
    public long getRackRow() {
        return rackRow;
    }
    public void setRackRow(long rackRow) {
        this.rackRow = rackRow;
    }
    public String getBay() {
        return bay;
    }
    public void setBay(String bay) {
        this.bay = bay;
    }
    public long getLevel() {
        return level;
    }
    public void setLevel(long level) {
        this.level = level;
    }
    public long getPosition() {
        return position;
    }
    public void setPosition(long position) {
        this.position = position;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public Allocation getAllocation() {
        return allocation;
    }
    public void setAllocation(Allocation allocation) {
        this.allocation = allocation;
    }
    public List<PlacementDTO> getPlacementDTOs() {
        return placementDTOs;
    }
    public void setPlacementDTOs(List<PlacementDTO> placementDTOs) {
        this.placementDTOs = placementDTOs;
    }
    public String getAisleRackRowCode() {
        return aisleRackRowCode;
    }
    public void setAisleRackRowCode(String aisleRackRowCode) {
        this.aisleRackRowCode = aisleRackRowCode;
    }

}
