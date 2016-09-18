package com.volvo.gloria.materialrequest.d.type.materialrequest;

import com.volvo.gloria.materialrequest.c.dto.MaterialRequestLineDTO;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequestLine;
import com.volvo.gloria.util.GloriaApplicationException;

/**
 * MaterialRequestType possible types supporting operations in behaviour classes..
 */
public enum MaterialRequestType implements MaterialRequestTypeOperations {
    SINGLE(new Single()), 
    MULTIPLE(new Multiple()), 
    FOR_STOCK(new ForStock());

    private final MaterialRequestTypeOperations materialRequestTypeOperations;

    MaterialRequestType(MaterialRequestTypeOperations materialRequestTypeOperations) {
        this.materialRequestTypeOperations = materialRequestTypeOperations;
    }

    @Override
    public void setAttributesOnCreate(MaterialRequestLineDTO materialRequestLineDTO, MaterialRequestLine materialRequestLine)
            throws GloriaApplicationException {
        materialRequestTypeOperations.setAttributesOnCreate(materialRequestLineDTO, materialRequestLine);
    }

    @Override
    public void setAttributesOnUpdate(MaterialRequestLineDTO materialRequestLineDTO, MaterialRequestLine materialRequestLine) 
            throws GloriaApplicationException {
        materialRequestTypeOperations.setAttributesOnUpdate(materialRequestLineDTO, materialRequestLine);
    }
}
