package com.volvo.gloria.procurematerial.util.migration.b;

import java.util.Properties;

import com.volvo.gloria.util.GloriaApplicationException;

/**
 * Split Service for open order data. 
 */
public interface SplitService {

    void initiateSplitOrderData(Properties openOrderProperties) throws GloriaApplicationException;

    void initiateSplitWarehouseData(Properties openOrderProperties) throws GloriaApplicationException;

}
