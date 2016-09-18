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

/**
 * Possible operations for all status.
 */
public interface RequestListOperations {

    RequestList addToRequestList(RequestList requestList, List<MaterialLineDTO> materialLineDTOs, MaterialHeaderRepository materialHeaderRepository)
            throws GloriaApplicationException;

    RequestList removeFromRequestList(RequestList requestList, List<MaterialLineDTO> materialLineDTOs, MaterialHeaderRepository materialHeaderRepository)
            throws GloriaApplicationException;

    void cancel(RequestList requestList, UserDTO userDTO, MaterialHeaderRepository materialHeaderRepository, TraceabilityRepository traceabilityRepository)
            throws GloriaApplicationException;

    List<RequestList> createRequestList(Date requiredDeliveryDate, Long priority, String deliveryAddressType, String deliveryAddessId, String deliveryAddressName,
            UserDTO user, List<MaterialLineDTO> materialLineDTOs, MaterialServices materialServices, CommonServices commonServices,
            MaterialHeaderRepository materialHeaderRepository) throws GloriaApplicationException;

    RequestList updateRequestList(RequestList requestList, Date requiredDeliveryDate, Long priority, String deliveryAddressType, String deliveryAddessId,
            String deliveryAddressName, UserDTO user, List<MaterialLineDTO> materialLineDTOs, MaterialServices materialServices, CommonServices commonServices,
            MaterialHeaderRepository materialHeaderRepository) throws GloriaApplicationException;

    void sendRequestList(RequestList requestList, Date requiredDeliveryDate, Long priority, String deliveryAddressType, String deliveryAddessId,
            String deliveryAddressName, UserDTO user, List<MaterialLineDTO> materialLineDTOs, MaterialServices materialServices, CommonServices commonServices,
            MaterialHeaderRepository materialHeaderRepository) throws GloriaApplicationException;
}
