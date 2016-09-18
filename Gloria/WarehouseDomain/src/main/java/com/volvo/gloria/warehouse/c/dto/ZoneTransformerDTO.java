package com.volvo.gloria.warehouse.c.dto;

import java.util.List;

import com.volvo.gloria.warehouse.c.ZoneType;

/**
 * Data class for Zone. Used in JAXB transformation(init load).
 * 
 */
public class ZoneTransformerDTO {

    private String code;
    private String name;
    private String description;
    private ZoneType type;
    private List<AisleRackRowTransformerDTO> aisleRackRowTransformerDTOs;
    private List<BinLocationTransformerDTO> binLocationTransformerDTOs;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ZoneType getType() {
        return type;
    }

    public void setType(ZoneType type) {
        this.type = type;
    }

    public List<AisleRackRowTransformerDTO> getAisleTransformerDTOs() {
        return aisleRackRowTransformerDTOs;
    }

    public void setAisleTransformerDTOs(List<AisleRackRowTransformerDTO> aisleRackRowTransformerDTOs) {
        this.aisleRackRowTransformerDTOs = aisleRackRowTransformerDTOs;
    }

    public List<BinLocationTransformerDTO> getBinLocationTransformerDTOs() {
        return binLocationTransformerDTOs;
    }

    public void setBinLocationTransformerDTOs(List<BinLocationTransformerDTO> binLocationTransformerDTOs) {
        this.binLocationTransformerDTOs = binLocationTransformerDTOs;
    }
}
