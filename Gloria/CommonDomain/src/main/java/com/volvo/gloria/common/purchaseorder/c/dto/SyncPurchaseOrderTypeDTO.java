package com.volvo.gloria.common.purchaseorder.c.dto;

import java.util.List;

/**
 * DTO class for purchase order incomming message from GPS.
 */
public class SyncPurchaseOrderTypeDTO {
    private String releaseID;
    private String creationDateTime;
    private String action;
    private List<PurchaseOrderHeaderDTO> purchaseOrderHeaderDTO;
    private List<StandAloneOrderHeaderDTO> standAloneOrderHeaderDTO;
    
    public String getReleaseID() {
        return releaseID;
    }

    public void setReleaseID(String releaseID) {
        this.releaseID = releaseID;
    }

    public String getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(String creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public List<PurchaseOrderHeaderDTO> getPurchaseOrderHeaderDTO() {
        return purchaseOrderHeaderDTO;
    }

    public void setPurchaseOrderHeaderDTO(List<PurchaseOrderHeaderDTO> purchaseOrderHeaderDTO) {
        this.purchaseOrderHeaderDTO = purchaseOrderHeaderDTO;
    }
    
    public List<StandAloneOrderHeaderDTO> getStandAloneOrderHeaderDTO() {
        return standAloneOrderHeaderDTO;
    }

    public void setStandAloneOrderHeaderDTO(List<StandAloneOrderHeaderDTO> standAloneOrderHeaderDTO) {
        this.standAloneOrderHeaderDTO = standAloneOrderHeaderDTO;
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
        SyncPurchaseOrderTypeDTO other = (SyncPurchaseOrderTypeDTO) obj;
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    
}
