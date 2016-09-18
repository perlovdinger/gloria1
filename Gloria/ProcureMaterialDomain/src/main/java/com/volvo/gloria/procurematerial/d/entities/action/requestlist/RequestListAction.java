package com.volvo.gloria.procurematerial.d.entities.action.requestlist;

import java.util.Date;
import java.util.List;

import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.c.dto.MaterialLineDTO;
import com.volvo.gloria.procurematerial.d.entities.RequestList;
import com.volvo.gloria.util.GloriaApplicationException;

/**
 * possible actions on request list.
 * 
 */
public enum RequestListAction implements RequestListActionOperations {

    ADD(new Add()), 
    REMOVE(new Remove()), 
    SAVE(new Save()), 
    SEND(new Send()), 
    CANCEL(new Cancel());

    private final RequestListActionOperations requestListActionOperations;

    RequestListAction(RequestListActionOperations requestListActionOperations) {
        this.requestListActionOperations = requestListActionOperations;
    }

    @Override
    public RequestList performAction(RequestList requestList, UserDTO userDTO, List<MaterialLineDTO> materialLineDTOs, Date requiredDeliveryDate,
            Long priority, String deliveryAddressType, String deliveryAddessId, String deliveryAddressName, MaterialServices materialServices)
            throws GloriaApplicationException {
        return requestListActionOperations.performAction(requestList, userDTO, materialLineDTOs, requiredDeliveryDate, priority, deliveryAddressType,
                                                         deliveryAddessId, deliveryAddressName, materialServices);
    }
}
