package com.volvo.gloria.procurematerial.d.type.internalexternal;

import com.volvo.gloria.util.GloriaApplicationException;

/**
 * posiible operations on Internal/External.
 * 
 */
public interface InternalExternalOperations {

    String[] evaluateOrderNo(String orderNo, String buyerId) throws GloriaApplicationException;

}
