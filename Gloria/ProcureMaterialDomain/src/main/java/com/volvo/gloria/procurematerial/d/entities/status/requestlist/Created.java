package com.volvo.gloria.procurematerial.d.entities.status.requestlist;

import java.util.Date;
import java.util.List;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.c.dto.MaterialLineDTO;
import com.volvo.gloria.procurematerial.d.entities.RequestList;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;

public class Created extends RequestListDefaultOperations {

    @Override
    public RequestList addToRequestList(RequestList requestList, List<MaterialLineDTO> materialLineDTOs, MaterialHeaderRepository materialHeaderRepository)
            throws GloriaApplicationException {
        return RequestListHelper.addToRequestList(requestList, materialLineDTOs, materialHeaderRepository);
    }

    @Override
    public RequestList removeFromRequestList(RequestList requestList, List<MaterialLineDTO> materialLineDTOs, MaterialHeaderRepository materialHeaderRepository)
            throws GloriaApplicationException {
        return RequestListHelper.removeFromRequestList(requestList, materialLineDTOs, materialHeaderRepository);
    }

    @Override
    public void cancel(RequestList requestList, UserDTO userDTO, MaterialHeaderRepository materialHeaderRepository,
            TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        RequestListHelper.cancel(requestList, userDTO, materialHeaderRepository, traceabilityRepository);
    }
    
    @Override
    public RequestList updateRequestList(RequestList requestList, Date requiredDeliveryDate, Long priority, String deliveryAddressType,
            String deliveryAddessId, String deliveryAddressName, UserDTO user, List<MaterialLineDTO> materialLineDTOs, MaterialServices materialServices,
            CommonServices commonServices, MaterialHeaderRepository materialHeaderRepository) throws GloriaApplicationException {
        return RequestListHelper.updateRequestList(requestList, materialLineDTOs, requiredDeliveryDate, priority, deliveryAddressType, deliveryAddessId,
                                                   deliveryAddressName, commonServices, materialHeaderRepository);
    }

    @Override
    public void sendRequestList(RequestList requestList, Date requiredDeliveryDate, Long priority, String deliveryAddressType, String deliveryAddessId,
            String deliveryAddressName, UserDTO user, List<MaterialLineDTO> materialLineDTOs, MaterialServices materialServices, CommonServices commonServices,
            MaterialHeaderRepository materialHeaderRepository) throws GloriaApplicationException {
        RequestListHelper.sendRequestList(requestList, requiredDeliveryDate, priority, deliveryAddressType, deliveryAddessId, deliveryAddressName, user,
                                          materialLineDTOs, materialServices, commonServices, materialHeaderRepository);
    }
}
