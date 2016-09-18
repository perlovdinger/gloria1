package com.volvo.gloria.procurematerial.d.type.internalexternal;

import com.volvo.gloria.util.GloriaApplicationException;

/**
 * Keeps procurement types.
 */
public enum InternalExternal implements InternalExternalOperations {
    INTERNAL(new Internal()),
    EXTERNAL(new External());

    private InternalExternalOperations internalExternalOperations;

    private InternalExternal(InternalExternalOperations internalExternalOperations) {
        this.internalExternalOperations = internalExternalOperations;
    }

    public String[] evaluateOrderNo(String orderNo, String buyerId) throws GloriaApplicationException {
        return internalExternalOperations.evaluateOrderNo(orderNo, buyerId);
    }
}
