package com.volvo.gloria.materialrequest.d.type.materialrequest;

import com.volvo.gloria.materialrequest.c.dto.MaterialRequestLineDTO;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequestLine;
import com.volvo.gloria.util.GloriaApplicationException;

public class ForStock extends MaterialRequestTypeDefaultOperations {

    @Override
    public void setAttributesOnCreate(MaterialRequestLineDTO materialRequestLineDTO, MaterialRequestLine materialRequestLine) 
            throws GloriaApplicationException {
        MaterialRequestTypeHelper.setAttributesOnCreateTypeForStock(materialRequestLineDTO, materialRequestLine);
    }

    @Override
    public void setAttributesOnUpdate(MaterialRequestLineDTO materialRequestLineDTO, MaterialRequestLine materialRequestLine) 
            throws GloriaApplicationException {
        MaterialRequestTypeHelper.setAttributesOnUpdateTypeForStock(materialRequestLineDTO, materialRequestLine);

    }
}