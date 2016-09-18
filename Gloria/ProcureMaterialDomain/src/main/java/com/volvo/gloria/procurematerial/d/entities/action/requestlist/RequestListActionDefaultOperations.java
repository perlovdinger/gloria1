package com.volvo.gloria.procurematerial.d.entities.action.requestlist;

import java.util.Date;
import java.util.List;

import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.c.dto.MaterialLineDTO;
import com.volvo.gloria.procurematerial.d.entities.RequestList;
import com.volvo.gloria.util.GloriaApplicationException;

public class RequestListActionDefaultOperations implements RequestListActionOperations {

    private static final String EXCEPTION_MESSAGE = "This operation is not supported in current state.";

    @Override
    public RequestList performAction(RequestList requestList, UserDTO userDTO, List<MaterialLineDTO> materialLineDTOs, Date requiredDeliveryDate,
            Long priority, String deliveryAddressType, String deliveryAddessId, String deliveryAddressName, MaterialServices materialServices)
            throws GloriaApplicationException {
        throw new GloriaApplicationException(null, EXCEPTION_MESSAGE + this.getClass().getName());
    }
}
