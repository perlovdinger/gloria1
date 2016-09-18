package com.volvo.gloria.common.util;

import com.volvo.gloria.common.costcenter.c.dto.CostCenterItemDTO;
import com.volvo.gloria.common.d.entities.CostCenter;

/**
 * Helper class for Cost center.
 */
public final class CostCenterHelper {

    private CostCenterHelper() {
    }

    public static CostCenter transformCostCenterItemDTOTpEty(CostCenterItemDTO costCenterItemDTO) {
        if (costCenterItemDTO != null) {
            CostCenter costCenter = new CostCenter();
            costCenter.setCostCenter(costCenterItemDTO.getCostCenter());
            costCenter.setEffectiveStartDate(costCenterItemDTO.getEffectiveStartDate());
            costCenter.setDescriptionLong(costCenterItemDTO.getDescriptionLong());
            costCenter.setDescriptionShort(costCenterItemDTO.getDescriptionShort());
            costCenter.setEffectiveEndDate(costCenterItemDTO.getEffectiveEndDate());
            costCenter.setPersonResponisbleId(costCenterItemDTO.getPersonResponsibleUserId());
            costCenter.setPersonalResponsibleName(costCenterItemDTO.getPersonResponsibleName());

            return costCenter;
        }
        return null;
    }

    public static CostCenter copyCostCenterEty(CostCenter costCenterExisting, CostCenterItemDTO costCenterIncoming) {
        if (costCenterIncoming != null) {
            costCenterExisting.setDescriptionLong(costCenterIncoming.getDescriptionLong());
            costCenterExisting.setDescriptionShort(costCenterIncoming.getDescriptionShort());
            costCenterExisting.setEffectiveEndDate(costCenterIncoming.getEffectiveEndDate());
            costCenterExisting.setPersonResponisbleId(costCenterIncoming.getPersonResponsibleUserId());
            costCenterExisting.setPersonalResponsibleName(costCenterIncoming.getPersonResponsibleName());

            return costCenterExisting;
        }
        return null;
    }
}
