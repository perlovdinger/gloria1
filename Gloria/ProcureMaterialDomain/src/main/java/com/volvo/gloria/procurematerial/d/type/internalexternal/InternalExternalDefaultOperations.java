package com.volvo.gloria.procurematerial.d.type.internalexternal;

import com.volvo.gloria.util.GloriaApplicationException;

/**
 * default behaviour for internal/external.
 * 
 */
public class InternalExternalDefaultOperations  implements InternalExternalOperations{
    @Override
    public String[] evaluateOrderNo(String orderNo, String buyerId) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }
}
