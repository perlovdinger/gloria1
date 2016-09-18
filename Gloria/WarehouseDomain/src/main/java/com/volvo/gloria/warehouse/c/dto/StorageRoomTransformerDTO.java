package com.volvo.gloria.warehouse.c.dto;

import java.util.List;

/**
 * Transformer method for StorageRoom.
 */
public class StorageRoomTransformerDTO {

    private String code;
    private String name;
    private String description;
    private List<ZoneTransformerDTO> zoneDTOs;

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

    public List<ZoneTransformerDTO> getZoneDTOs() {
        return zoneDTOs;
    }

    public void setZoneDTOs(List<ZoneTransformerDTO> zoneDTOs) {
        this.zoneDTOs = zoneDTOs;
    }

}
