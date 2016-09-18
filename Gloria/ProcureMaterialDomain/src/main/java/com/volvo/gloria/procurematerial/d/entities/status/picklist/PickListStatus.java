package com.volvo.gloria.procurematerial.d.entities.status.picklist;

import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.d.entities.PickList;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;

/**
 * Enum for PickListStatus.
 */
public enum PickListStatus implements PickListOperations {
    CREATED(new Created()), 
    IN_WORK(new InWork()), 
    PICKED(new Picked()), 
    CANCELLED(new Cancelled());

    private final PickListOperations pickListOperations;

    PickListStatus(PickListOperations pickListOperations) {
        this.pickListOperations = pickListOperations;
    }
    
    @Override
    public void cancel(PickList pickList, UserDTO userDTO, MaterialHeaderRepository materialHeaderRepository, TraceabilityRepository traceabilityRepository)
            throws GloriaApplicationException {
        this.pickListOperations.cancel(pickList, userDTO, materialHeaderRepository, traceabilityRepository);
    }
}
