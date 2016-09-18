package com.volvo.gloria.common.repositories.b;

import java.util.List;

import com.volvo.gloria.common.d.entities.SupplierCounterPart;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.persistence.GenericRepository;

/**
 * repository for root SupplierCounterPart.
 * 
 */
public interface SupplierCounterPartRepository extends GenericRepository<SupplierCounterPart, Long> {
    
    List<SupplierCounterPart> getAllSupplierCounterParts(String deliveryFollowUpTeamId);

    SupplierCounterPart findSupplierCounterPartByPPSuffix(String ppSuffix);

    SupplierCounterPart findSupplierCounterPartByMaterialUserId(String materialUserId);

    List<SupplierCounterPart> getSupplierCounterPartsByCompanyCode(String companyCode);
    
    PageObject getSupplierCounterParts(PageObject pageObject);

    List<SupplierCounterPart> getSupplierCounterPartsBySuffix(List<String> valuesAsString);
}
