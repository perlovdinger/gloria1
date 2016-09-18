package com.volvo.gloria.procurematerial.repositories.b;

import java.util.List;

import com.volvo.gloria.procurematerial.d.entities.Buyer;
import com.volvo.gloria.procurematerial.d.entities.PurchaseOrganisation;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.persistence.GenericRepository;

/**
 * repositiory for root BuyerCode.
 * 
 */
public interface PurchaseOrganisationRepository extends GenericRepository<PurchaseOrganisation, Long> {

    Buyer findBuyerByCode(String buyerId);
    
    Buyer findBuyerByCodeAndOrganisationCode(String code, String organisationCode);

    PageObject findAllBuyers(PageObject pageObject, String organisationCode);

    Buyer save(Buyer buyer);

    PurchaseOrganisation findPurchaseOrgByCode(String purchaseOrganisationCode);
    
    List<Buyer> findAllBuyers();

    PageObject findAllBuyers(PageObject pageObject);
}
