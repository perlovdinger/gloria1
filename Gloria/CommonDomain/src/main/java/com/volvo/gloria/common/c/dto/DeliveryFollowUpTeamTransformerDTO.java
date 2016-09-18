package com.volvo.gloria.common.c.dto;

import java.util.List;

import com.volvo.gloria.common.c.FollowUpType;

/**
 * Data Transfer Object for the DeliveryFollowUpTeamTranformer.
 */
public class DeliveryFollowUpTeamTransformerDTO {

    private String name;
    private FollowUpType followUpType;
    private List<SupplierCounterPartDTO> supplierCounterPartDTOs;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public FollowUpType getFollowUpType() {
        return followUpType;
    }
    public void setFollowUpType(FollowUpType followUpType) {
        this.followUpType = followUpType;
    }
    public List<SupplierCounterPartDTO> getSupplierCounterPartDTOs() {
        return supplierCounterPartDTOs;
    }
    public void setSupplierCounterPartDTOs(List<SupplierCounterPartDTO> supplierCounterPartDTOs) {
        this.supplierCounterPartDTOs = supplierCounterPartDTOs;
    }
}
