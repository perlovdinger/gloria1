package com.volvo.gloria.common.repositories.b;

import java.util.List;

import com.volvo.gloria.common.d.entities.UnitOfMeasure;
import com.volvo.gloria.util.persistence.GenericRepository;

/**
 * repository for root UnitOfMeasure.
 * 
 */
public interface UnitOfMeasureRepository extends GenericRepository<UnitOfMeasure, Long> {

    UnitOfMeasure findUnitOfMeasureByCode(String code);

    List<UnitOfMeasure> findAllUnitOfMeasuresSupportedForGloria();
}
