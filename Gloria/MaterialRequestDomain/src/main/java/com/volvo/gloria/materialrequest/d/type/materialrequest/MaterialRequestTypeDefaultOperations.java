package com.volvo.gloria.materialrequest.d.type.materialrequest;

import com.volvo.gloria.materialrequest.c.dto.MaterialRequestLineDTO;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequestLine;
import com.volvo.gloria.util.GloriaApplicationException;

/**
 * Possible operations for MaterialRequestType.
 */
public class MaterialRequestTypeDefaultOperations implements MaterialRequestTypeOperations {

    @Override
    public void setAttributesOnCreate(MaterialRequestLineDTO materialRequestLineDTO, MaterialRequestLine materialRequestLine) 
            throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state" + this.getClass().getName());
    }

    @Override
    public void setAttributesOnUpdate(MaterialRequestLineDTO materialRequestLineDTO, MaterialRequestLine materialRequestLine) 
            throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state" + this.getClass().getName());
    }
}
