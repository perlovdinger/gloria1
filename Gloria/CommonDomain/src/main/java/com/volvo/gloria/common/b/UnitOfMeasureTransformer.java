package com.volvo.gloria.common.b;

import java.util.List;

import com.volvo.gloria.common.c.dto.UnitOfMeasureDTO;

/**
 * Service interface for UOM message transformer.
 * 
 */
public interface UnitOfMeasureTransformer {

    List<UnitOfMeasureDTO> transformUnitOfMeasure(String xmlContent);

}
