package com.volvo.gloria.procurematerial.d.entities.status.picklist;

import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.d.entities.PickList;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;

/**
 * Possible operations for all status.
 */
public interface PickListOperations {

    void cancel(PickList pickList, UserDTO userDTO, MaterialHeaderRepository materialHeaderRepository, TraceabilityRepository traceabilityRepository)
            throws GloriaApplicationException;
}
