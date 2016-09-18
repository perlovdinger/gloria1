package com.volvo.gloria.procurematerial.d.entities.action.requestlist;

import java.util.Date;

import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.c.dto.MaterialLineDTO;
import com.volvo.gloria.procurematerial.d.entities.RequestList;
import com.volvo.gloria.util.GloriaApplicationException;

public class Save extends RequestListActionDefaultOperations {

    public RequestList performAction(RequestList requestList, UserDTO userDTO, java.util.List<MaterialLineDTO> materialLineDTOs,
            Date requiredDeliveryDate, Long priority, String deliveryAddressType, String deliveryAddessId, String deliveryAddressName,
            MaterialServices materialServices) throws GloriaApplicationException {

        return materialServices.createOrUpdateRequestList(requestList, requiredDeliveryDate, priority, deliveryAddressType, deliveryAddessId,
                                                          deliveryAddressName, userDTO, materialLineDTOs);
    }
}
