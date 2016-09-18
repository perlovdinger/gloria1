package com.volvo.gloria.common.repositories.b;

import com.volvo.gloria.common.d.entities.ConversionUnit;
import com.volvo.gloria.util.persistence.GenericRepository;

/**
 * repository for root Conversion Unit.
 * 
 */
public interface ConversionUnitRepository extends GenericRepository<ConversionUnit, Long> {

    ConversionUnit findConversionUnit(String conversionFromApp, String conversionToApp, String codeToConvert);

}
