package com.volvo.gloria.common.costcenter.c.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO class for Sync Purchase Order.
 */
public class SyncCostCenterDTO {

    private List<CostCenterItemDTO> costCenterItems = new ArrayList<CostCenterItemDTO>();

    private String creationDateTime;

    public List<CostCenterItemDTO> getCostCenterItems() {
        return costCenterItems;
    }

    public void setCostCenterItems(List<CostCenterItemDTO> costCenterItems) {
        this.costCenterItems = costCenterItems;
    }

    public String getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(String creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    /**
     * Base equals on releaseID. {@inheritDoc}
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
        SyncCostCenterDTO other = (SyncCostCenterDTO) obj;
        if (!this.getCreationDateTime().equals(other.getCreationDateTime())) {
            return false;
        }
        return true;
    }

    /**
     * Base hashCode on releaseID. {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.getCreationDateTime().hashCode();
    }

}
