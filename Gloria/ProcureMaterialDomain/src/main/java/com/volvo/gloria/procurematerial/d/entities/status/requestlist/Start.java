package com.volvo.gloria.procurematerial.d.entities.status.requestlist;

import java.util.Date;
import java.util.List;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.c.dto.MaterialLineDTO;
import com.volvo.gloria.procurematerial.d.entities.RequestList;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;

public class Start extends RequestListDefaultOperations {
    
    @Override
    public List<RequestList> createRequestList(Date requiredDeliveryDate, Long priority, String deliveryAddressType, String deliveryAddessId, String deliveryAddressName,
            UserDTO user, List<MaterialLineDTO> materialLineDTOs, MaterialServices materialServices, CommonServices commonServices,
            MaterialHeaderRepository materialHeaderRepository) throws GloriaApplicationException {
        return RequestListHelper.createRequestList(requiredDeliveryDate, priority, deliveryAddressType, deliveryAddessId, deliveryAddressName, user, materialLineDTOs,
                           materialServices, commonServices, materialHeaderRepository);
    }
}
