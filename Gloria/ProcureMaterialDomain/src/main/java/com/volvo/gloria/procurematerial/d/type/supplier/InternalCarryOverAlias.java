package com.volvo.gloria.procurematerial.d.type.supplier;

import java.util.ArrayList;
import java.util.List;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.d.entities.CarryOver;
import com.volvo.gloria.common.repositories.b.CarryOverRepository;
import com.volvo.gloria.procurematerial.d.entities.Supplier;

public class InternalCarryOverAlias extends SupplierTypeDefaultOperations implements SupplierTypeOperations {
    @Override
    public List<Supplier> getSuppliers(String partNumber, String partVersion, String partAffiliation, String customerId,
            CarryOverRepository carryOverRepository, CommonServices commonServices) {
        List<Supplier> sparePartSites = new ArrayList<Supplier>();
        List<CarryOver> carryOvers = carryOverRepository.findCarryOverAlias(partNumber, partAffiliation, customerId);
        for (CarryOver carryOver : SupplierTypeHelper.removeDuplicateCarryOvers(carryOvers)) {
            Supplier supplier = SupplierTypeHelper.createInternaCarryOverSupplier(carryOver);
            supplier.setSupplierType(SupplierType.INTERNAL_CARRY_OVER_ALIAS);
            sparePartSites.add(supplier);
        }
        return sparePartSites;
    }
}
