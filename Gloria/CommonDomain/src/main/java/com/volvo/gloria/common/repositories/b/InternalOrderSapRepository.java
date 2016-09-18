package com.volvo.gloria.common.repositories.b;

import java.util.List;

import com.volvo.gloria.common.d.entities.InternalOrderSap;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.persistence.GenericRepository;

/**
 * repository for root InternalOrderSap.
 * 
 */
public interface InternalOrderSapRepository extends GenericRepository<InternalOrderSap, Long> {

    List<InternalOrderSap> findInternalOrderSap(PageObject pageObject, String code);

}
