package com.volvo.gloria.materialrequest.d.type.materialrequest;

import com.volvo.gloria.materialrequest.c.dto.MaterialRequestLineDTO;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequestLine;
import com.volvo.gloria.util.GloriaApplicationException;

/**
 * Possible operations for MaterialRequestType.
 */
public interface MaterialRequestTypeOperations {

    void setAttributesOnCreate(MaterialRequestLineDTO materialRequestLineDTO, MaterialRequestLine materialRequestLine) throws GloriaApplicationException;

    void setAttributesOnUpdate(MaterialRequestLineDTO materialRequestLineDTO, MaterialRequestLine materialRequestLine) throws GloriaApplicationException;
}
