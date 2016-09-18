package com.volvo.gloria.procurematerial.d.type.supplier;

import java.util.List;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.repositories.b.CarryOverRepository;
import com.volvo.gloria.procurematerial.d.entities.Supplier;

/**
 * SupplierType operations.
 */
public interface SupplierTypeOperations {
    List<Supplier> getSuppliers(String partNumber, String partVersion, String partAffiliation, String customerId, CarryOverRepository carryOverRepository,
            CommonServices commonServices);
}
