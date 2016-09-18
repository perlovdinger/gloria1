package com.volvo.gloria.warehouse.b;

import java.util.List;

import com.volvo.gloria.warehouse.c.dto.WarehouseTransformerDTO;

/**
 * Interface for WarehouseTransformer.
 */
public interface WarehouseTransformer {

    List<WarehouseTransformerDTO> transformWarehouse(String xmlContent);

}
