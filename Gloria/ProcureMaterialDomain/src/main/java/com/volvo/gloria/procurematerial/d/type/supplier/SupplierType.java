package com.volvo.gloria.procurematerial.d.type.supplier;

import java.util.List;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.repositories.b.CarryOverRepository;
import com.volvo.gloria.procurematerial.d.entities.Supplier;

/**
 * Possible supplier types.
 */
public enum SupplierType implements SupplierTypeOperations {
    EXTERNAL(new External()),    
    INTERNAL_CARRY_OVER(new InternalCarryOver()), 
    INTERNAL_CARRY_OVER_ALIAS(new InternalCarryOverAlias()), 
    INTERNAL_EDITED(new InternalEdited());
    
    private final SupplierTypeOperations supplierTypeOperations;

    SupplierType(SupplierTypeOperations supplierTypeOperations) {
        this.supplierTypeOperations = supplierTypeOperations;
    }

    @Override
    public List<Supplier> getSuppliers(String partNumber, String partVersion, String partAffiliation, String customerId,
            CarryOverRepository carryOverRepository, CommonServices commonServices) {
        return supplierTypeOperations.getSuppliers(partNumber, partVersion, partAffiliation, customerId, carryOverRepository, commonServices);
    }

}
