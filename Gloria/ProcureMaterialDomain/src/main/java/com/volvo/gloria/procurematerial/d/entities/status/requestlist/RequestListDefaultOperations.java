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

public class RequestListDefaultOperations implements RequestListOperations {

    private static final String EXCEPTION_MESSAGE = "This operation is not supported in current state.";

    @Override
    public RequestList addToRequestList(RequestList requestList, List<MaterialLineDTO> materialLineDTOs, MaterialHeaderRepository materialHeaderRepository)
            throws GloriaApplicationException {
        throw new GloriaApplicationException(null, EXCEPTION_MESSAGE + this.getClass().getName());
    }

    @Override
    public RequestList removeFromRequestList(RequestList requestList, List<MaterialLineDTO> materialLineDTOs, MaterialHeaderRepository materialHeaderRepository)
            throws GloriaApplicationException {
        throw new GloriaApplicationException(null, EXCEPTION_MESSAGE + this.getClass().getName());
    }

    @Override
    public void cancel(RequestList requestList, UserDTO userDTO, MaterialHeaderRepository materialHeaderRepository,
            TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, EXCEPTION_MESSAGE + this.getClass().getName());
    }

    @Override
    public List<RequestList> createRequestList(Date requiredDeliveryDate, Long priority, String deliveryAddressType, String deliveryAddessId, String deliveryAddressName,
            UserDTO user, List<MaterialLineDTO> materialLineDTOs, MaterialServices materialServices, CommonServices commonServices,
            MaterialHeaderRepository materialHeaderRepository) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, EXCEPTION_MESSAGE + this.getClass().getName());
    }

    @Override
    public RequestList updateRequestList(RequestList requestList, Date requiredDeliveryDate, Long priority, String deliveryAddressType, String deliveryAddessId,
            String deliveryAddressName, UserDTO user, List<MaterialLineDTO> materialLineDTOs, MaterialServices materialServices, CommonServices commonServices,
            MaterialHeaderRepository materialHeaderRepository) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, EXCEPTION_MESSAGE + this.getClass().getName());
    }

    @Override
    public void sendRequestList(RequestList requestList, Date requiredDeliveryDate, Long priority, String deliveryAddressType, String deliveryAddessId,
            String deliveryAddressName, UserDTO user, List<MaterialLineDTO> materialLineDTOs, MaterialServices materialServices, CommonServices commonServices,
            MaterialHeaderRepository materialHeaderRepository) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, EXCEPTION_MESSAGE + this.getClass().getName());
    }
}
