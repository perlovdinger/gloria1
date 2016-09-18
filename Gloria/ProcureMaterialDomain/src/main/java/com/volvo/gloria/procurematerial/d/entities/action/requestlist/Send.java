package com.volvo.gloria.procurematerial.d.entities.action.requestlist;

import java.util.Date;
import java.util.List;

import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.c.dto.MaterialLineDTO;
import com.volvo.gloria.procurematerial.d.entities.RequestList;
import com.volvo.gloria.util.GloriaApplicationException;

public class Send extends RequestListActionDefaultOperations {

    @Override
    public RequestList performAction(RequestList requestList, UserDTO userDTO, List<MaterialLineDTO> materialLineDTOs, Date requiredDeliveryDate,
            Long priority, String deliveryAddressType, String deliveryAddessId, String deliveryAddressName, MaterialServices materialServices)
            throws GloriaApplicationException {
        if (requestList == null) {
            requestList = RequestListAction.SAVE.performAction(requestList, userDTO, materialLineDTOs, requiredDeliveryDate, priority, deliveryAddressType,
                                                               deliveryAddessId, deliveryAddressName, materialServices);
        }
        return materialServices.sendRequestList(requestList, materialLineDTOs, requiredDeliveryDate, priority, deliveryAddressType, deliveryAddessId,
                                                deliveryAddressName, userDTO);
    }
}
