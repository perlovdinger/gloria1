package com.volvo.gloria.materialrequest.d.status.materialrequest;

import com.volvo.gloria.materialrequest.d.entities.MaterialRequest;
import com.volvo.gloria.materialrequest.repositories.b.MaterialRequestRepository;

/**
 * Operations for CANCEL_WAIT status.
 */
public class CancelWait extends MaterialRequestDefaultOperations {
    private static final String CANCELLED = "CANCELLED";
    private static final String CANCEL_REJECTED = "CANCEL_REJECTED";

    @Override
    public void changeState(MaterialRequest materialRequest, MaterialRequestRepository materialRequestRepository, String response) {

        if (response.equalsIgnoreCase(CANCELLED)) {
            materialRequest.getCurrent().setStatus(MaterialRequestStatus.CANCELLED);
        } else if (response.equalsIgnoreCase(CANCEL_REJECTED)) {
            materialRequest.getCurrent().setStatus(MaterialRequestStatus.CANCEL_REJECTED);
        }
        materialRequestRepository.save(materialRequest);
    }
}
