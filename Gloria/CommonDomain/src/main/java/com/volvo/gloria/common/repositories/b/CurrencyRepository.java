package com.volvo.gloria.common.repositories.b;

import com.volvo.gloria.common.d.entities.Currency;
import com.volvo.gloria.util.persistence.GenericRepository;

/**
 * repository for root Currency.
 * 
 */
public interface CurrencyRepository extends GenericRepository<Currency, Long> {

    Currency findByCode(String code);
}
