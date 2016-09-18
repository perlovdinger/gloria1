package com.volvo.gloria.materialrequest.d.status.materialrequest;

import com.volvo.gloria.materialrequest.d.entities.MaterialRequest;
import com.volvo.gloria.materialrequest.repositories.b.MaterialRequestRepository;

/**
 * Operations for SENT_WAIT status.
 */
public class SentWait extends MaterialRequestDefaultOperations {
    private static final String REJECT = "Rejected";
    private static final String ACCEPT = "Accepted";

    @Override
    public void changeState(MaterialRequest materialRequest, MaterialRequestRepository materialRequestRepository, String response) {

        if (response.equalsIgnoreCase(ACCEPT)) {
            materialRequest.getCurrent().setStatus(MaterialRequestStatus.SENT_ACCEPTED);
        } else if (response.equalsIgnoreCase(REJECT)) {
            materialRequest.getCurrent().setStatus(MaterialRequestStatus.SENT_REJECTED);
        }
        materialRequestRepository.save(materialRequest);
    }
}
