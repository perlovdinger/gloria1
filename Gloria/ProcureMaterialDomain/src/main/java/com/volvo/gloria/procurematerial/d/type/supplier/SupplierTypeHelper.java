package com.volvo.gloria.procurematerial.d.type.supplier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.volvo.gloria.common.d.entities.CarryOver;
import com.volvo.gloria.procurematerial.d.entities.Supplier;

public class SupplierTypeHelper extends SupplierTypeDefaultOperations implements SupplierTypeOperations {
   
   
    public static Supplier createInternaCarryOverSupplier(CarryOver carryOver) {
        Supplier supplier = new Supplier();
        supplier.setCurrency(carryOver.getCurrency());
        supplier.setSupplierId(carryOver.getCustomerId());
        supplier.setSupplierName(carryOver.getCustomerId() + " (" + carryOver.getCustomerName() + ")");
        supplier.setUnitPrice(carryOver.getAmount());
        supplier.setPriceUnit(carryOver.getPriceUnit());
        supplier.setPartNumber(carryOver.getPartNumber());
        supplier.setPartAffiliation(carryOver.getPartAffiliation());
        supplier.setPartVersion(carryOver.getPartVersion());
        return supplier;
    }
    
    public static List<CarryOver> removeDuplicateCarryOvers(List<CarryOver> carryOvers) {
        Map<String, CarryOver> filteredCarryOvers = new HashMap<String, CarryOver>();
        for (CarryOver carryOver : carryOvers) {
            String carryOverKey = handleEmpty(carryOver.getPartNumber()) + handleEmpty(carryOver.getPartAffiliation()) + handleEmpty(carryOver.getCustomerId());
            if (!filteredCarryOvers.containsKey(carryOverKey)) {
                filteredCarryOvers.put(carryOverKey, carryOver);
            }
        }
        return new ArrayList<CarryOver>(filteredCarryOvers.values());
    }
    
    private static String handleEmpty(Object value) {
        if (value == null || String.valueOf(value).isEmpty()) {
            return "";
        }
        return String.valueOf(value).trim();
    }
}
