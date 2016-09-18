package com.volvo.gloria.common.repositories.b;

import java.util.List;

import com.volvo.gloria.common.d.entities.CarryOver;
import com.volvo.gloria.util.persistence.GenericRepository;

/**
 * repository for root CarryOver.
 * 
 */
public interface CarryOverRepository extends GenericRepository<CarryOver, Long> {
    List<CarryOver> findCarryOverByPartNumberPartversionAndCustomerId(String partNumber, String partVersion, String partAffiliation, String customerId,
            String supplierId);
    List<CarryOver> findCarryOverAlias(String partNumber, String partAffiliation, String customerId);

    void deleteAllCarryOver();

    CarryOver findUniqueCarryOver(String customerId, String supplierId, String partAffiliation, String partNumber, String partVersion);
}
