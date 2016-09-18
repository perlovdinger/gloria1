/**
 * 
 */
package com.volvo.gloria.common.repositories.b;

import com.volvo.gloria.common.d.entities.CurrencyRate;
import com.volvo.gloria.util.persistence.GenericRepository;

/**
 * Repository for CurrencyRate.
 */
public interface CurrencyRateRepository extends GenericRepository<CurrencyRate, Long> {

    CurrencyRate findValidCurrencyRate(String currencyCode);

}
