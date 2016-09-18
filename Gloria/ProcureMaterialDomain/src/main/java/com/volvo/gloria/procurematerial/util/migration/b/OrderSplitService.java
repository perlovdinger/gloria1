package com.volvo.gloria.procurematerial.util.migration.b;

import java.util.Properties;

import com.volvo.gloria.util.GloriaApplicationException;

/**
 * services for Order Split.
 */
public interface OrderSplitService {

    void initiateOrderSplitData(Properties openOrderProperties) throws GloriaApplicationException;

}
