package com.volvo.gloria.procurematerial.d.entities.status.requestlist;

import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.d.entities.RequestList;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;

public class Sent extends RequestListDefaultOperations {
    @Override
    public void cancel(RequestList requestList, UserDTO userDTO, MaterialHeaderRepository materialHeaderRepository,
            TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        RequestListHelper.cancel(requestList, userDTO, materialHeaderRepository, traceabilityRepository);
    }
}
