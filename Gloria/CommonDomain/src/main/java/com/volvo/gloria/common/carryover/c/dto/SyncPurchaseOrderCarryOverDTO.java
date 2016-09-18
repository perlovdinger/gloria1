package com.volvo.gloria.common.carryover.c.dto;

import java.util.List;

/**
 * DTO for incoming CarryOver message from GPS.
 */
public class SyncPurchaseOrderCarryOverDTO {
    private String action;
    private String creationDateTime;
    private List<CarryOverItemDTO> carryOverItemDTOs;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(String creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public List<CarryOverItemDTO> getCarryOverItemDTOs() {
        return carryOverItemDTOs;
    }

    public void setCarryOverItemDTOs(List<CarryOverItemDTO> carryOverItemDTOs) {
        this.carryOverItemDTOs = carryOverItemDTOs;
    }

    /**
     * Base equals on creationDateTime. {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SyncPurchaseOrderCarryOverDTO other = (SyncPurchaseOrderCarryOverDTO) obj;
        if (!this.getCreationDateTime().equals(other.getCreationDateTime())) {
            return false;
        }
        return true;
    }

    /**
     * Base hashCode on creationDateTime. {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.getCreationDateTime().hashCode();
    }
}
