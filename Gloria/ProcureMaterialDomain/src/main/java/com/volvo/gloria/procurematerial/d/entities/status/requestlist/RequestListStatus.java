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
 * 
 */
public enum RequestListStatus implements RequestListOperations {
    START(new Start()),
    CREATED(new Created()), 
    SENT(new Sent()), 
    PICK_COMPLETED(new PickCompleted()), 
    READY_TO_SHIP(new ReadyToShip()), 
    SHIPPED(new Shipped());
    
    private final RequestListOperations requestListOperations;
    
    RequestListStatus(RequestListOperations requestListOperations) {
        this.requestListOperations = requestListOperations;
    }
    
    public RequestList create() {
        return null;
    }

    public RequestList update() {
        return null;
    }

    @Override
    public void cancel(RequestList requestList, UserDTO userDTO, MaterialHeaderRepository materialHeaderRepository,
            TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        this.requestListOperations.cancel(requestList, userDTO, materialHeaderRepository, traceabilityRepository);
    }

    @Override
    public RequestList addToRequestList(RequestList requestList, List<MaterialLineDTO> materialLineDTOs, MaterialHeaderRepository materialHeaderRepository)
            throws GloriaApplicationException {
        return this.requestListOperations.addToRequestList(requestList, materialLineDTOs, materialHeaderRepository);
    }

    @Override
    public RequestList removeFromRequestList(RequestList requestList, List<MaterialLineDTO> materialLineDTOs, MaterialHeaderRepository materialHeaderRepository)
            throws GloriaApplicationException {
        return this.requestListOperations.removeFromRequestList(requestList, materialLineDTOs, materialHeaderRepository);
    }

    @Override
    public List<RequestList> createRequestList(Date requiredDeliveryDate, Long priority, String deliveryAddressType, String deliveryAddessId,
            String deliveryAddressName, UserDTO user, List<MaterialLineDTO> materialLineDTOs, MaterialServices materialServices, CommonServices commonServices,
            MaterialHeaderRepository materialHeaderRepository) throws GloriaApplicationException {
        return this.requestListOperations.createRequestList(requiredDeliveryDate, priority, deliveryAddressType, deliveryAddessId, deliveryAddressName, user,
                                                            materialLineDTOs, materialServices, commonServices, materialHeaderRepository);
    }

    @Override
    public RequestList updateRequestList(RequestList requestList, Date requiredDeliveryDate, Long priority, String deliveryAddressType,
            String deliveryAddessId, String deliveryAddressName, UserDTO user, List<MaterialLineDTO> materialLineDTOs, MaterialServices materialServices,
            CommonServices commonServices, MaterialHeaderRepository materialHeaderRepository) throws GloriaApplicationException {
        return this.requestListOperations.updateRequestList(requestList, requiredDeliveryDate, priority, deliveryAddressType, deliveryAddessId,
                                                            deliveryAddressName, user, materialLineDTOs, materialServices, commonServices,
                                                            materialHeaderRepository);
    }

    @Override
    public void sendRequestList(RequestList requestList, Date requiredDeliveryDate, Long priority, String deliveryAddressType, String deliveryAddessId,
            String deliveryAddressName, UserDTO user, List<MaterialLineDTO> materialLineDTOs, MaterialServices materialServices, CommonServices commonServices,
            MaterialHeaderRepository materialHeaderRepository) throws GloriaApplicationException {
        this.requestListOperations.sendRequestList(requestList, requiredDeliveryDate, priority, deliveryAddressType, deliveryAddessId, deliveryAddressName,
                                                   user, materialLineDTOs, materialServices, commonServices, materialHeaderRepository);
    }
}
