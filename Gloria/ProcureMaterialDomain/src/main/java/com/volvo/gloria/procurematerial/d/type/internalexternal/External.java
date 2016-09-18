package com.volvo.gloria.procurematerial.d.type.internalexternal;

import static com.volvo.gloria.util.c.GloriaParams.GPS_ORDER_NO_SPLITTER;

import com.volvo.gloria.util.GloriaApplicationException;

/**
 * behaviour for type External.
 * 
 */
public class External extends InternalExternalDefaultOperations{

    @Override
    public String[] evaluateOrderNo(String orderNo, String buyerId) throws GloriaApplicationException {
        String[] evalTokens = new String[2];
       
        String[] tokens = orderNo.split(GPS_ORDER_NO_SPLITTER);
        if (tokens.length > 2) {
            // First part of Order No
            evalTokens[0] = tokens[0]; 
            // Last part of Order No
            evalTokens[1] = tokens[tokens.length - 1]; 
        } else {            
            // Buyer Id
            evalTokens[0] = buyerId; 
            // Last part of Order No
            evalTokens[1] = tokens[tokens.length - 1]; 
        }
        return evalTokens;
    }
}
