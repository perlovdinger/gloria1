package com.volvo.gloria.procurematerial.util.migration.b;

import java.util.Properties;

import com.volvo.gloria.util.GloriaApplicationException;
/**
 * Warehouse split.
 */
public interface WarehouseSplitService {

    void initiateWarehouseSplitData(Properties openOrderProperties, boolean mergeSplittedParts) throws GloriaApplicationException;

}
