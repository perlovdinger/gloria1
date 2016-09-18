package com.volvo.gloria.procurematerial.d.entities.action.requestlist;

import java.util.Date;
import java.util.List;

import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.c.dto.MaterialLineDTO;
import com.volvo.gloria.procurematerial.d.entities.RequestList;
import com.volvo.gloria.util.GloriaApplicationException;

/**
 * Possible operations for all action types.
 */
public interface RequestListActionOperations {

    RequestList performAction(RequestList requestList, UserDTO userDTO, List<MaterialLineDTO> materialLineDTOs, Date requiredDeliveryDate, Long priority,
            String deliveryAddressType, String deliveryAddessId, String deliveryAddressName, MaterialServices materialServices)
            throws GloriaApplicationException;
}
