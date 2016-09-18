package com.volvo.gloria.common.util;

import com.volvo.gloria.common.carryover.c.dto.CarryOverItemDTO;
import com.volvo.gloria.common.d.entities.CarryOver;

/**
 * Helper class for Cost center.
 */
public final class CarryOverHelper {

    private CarryOverHelper() {
    }

    public static CarryOver transformCarryOverItemDTOTpEty(CarryOverItemDTO carryOverItemDTO, CarryOver carryOver) {
        if (carryOverItemDTO != null) {
            if (carryOver == null) {
                carryOver = new CarryOver();
            }
            carryOver.setCustomerId(carryOverItemDTO.getCustomerId());
            carryOver.setCustomerName(carryOverItemDTO.getCustomerName());
            carryOver.setSupplierId(carryOverItemDTO.getSupplierId());
            carryOver.setSupplierName(carryOverItemDTO.getSupplierName());
            carryOver.setSupplierCountryCode(carryOverItemDTO.getSupplierCountryCode());
            carryOver.setPartAffiliation(carryOverItemDTO.getPartAffliation());
            carryOver.setPartNumber(carryOverItemDTO.getPartNumber());
            carryOver.setPartVersion(carryOverItemDTO.getPartVersion());
            carryOver.setSupplierPartNumber(carryOverItemDTO.getSupplierPartNumber());
            carryOver.setSupplierPartVersion(carryOverItemDTO.getSupplierPartVersion());
            carryOver.setStartDate(carryOverItemDTO.getStartDate());
            carryOver.setAmount(carryOverItemDTO.getAmount());
            carryOver.setCurrency(carryOverItemDTO.getCurrency());
            carryOver.setPriceUnit(carryOverItemDTO.getPriceUnit());
            carryOver.setUnitCode(carryOverItemDTO.getUnitCode());
            return carryOver;
        }
        return null;
    }
}
