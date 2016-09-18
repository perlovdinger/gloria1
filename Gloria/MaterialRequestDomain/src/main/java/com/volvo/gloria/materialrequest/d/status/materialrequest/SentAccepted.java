package com.volvo.gloria.materialrequest.d.status.materialrequest;

import java.util.List;

import com.volvo.gloria.materialRequestProxy.b.MaterialRequestCancelSender;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequest;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequestVersion;
import com.volvo.gloria.materialrequest.repositories.b.MaterialRequestRepository;
import com.volvo.gloria.util.GloriaApplicationException;

/**
 * Operations for SENT status.
 */
public class SentAccepted extends MaterialRequestDefaultOperations {

    @Override
    public MaterialRequest newVersion(MaterialRequest materialRequest, MaterialRequestRepository materialRequestRepository) throws GloriaApplicationException {
        long changeVersion = 0L;
        List<MaterialRequestVersion> materialRequestVersions = materialRequest.getMaterialRequestVersions();
        MaterialRequestStatusHelper.sortMaterialRequestHeaderVersionsByDescOrder(materialRequestVersions);
        changeVersion = materialRequestVersions.get(0).getChangeVersion();
        return MaterialRequestStatusHelper.newVersion(materialRequest, materialRequestRepository, changeVersion);
    }

    @Override
    public MaterialRequest revert(MaterialRequest materialRequest, MaterialRequestRepository materialRequestRepository) throws GloriaApplicationException {
        return materialRequest;
    }
    
    @Override
    public MaterialRequestStatus cancel(MaterialRequest materialRequest, MaterialRequestCancelSender materialRequestCancelSender,
            MaterialRequestRepository materialRequestRepository) throws GloriaApplicationException {
        return MaterialRequestStatusHelper.cancelMaterialRequest(materialRequest, materialRequestCancelSender, materialRequestRepository);
    }

    @Override
    public boolean isSentAccepted() {
        return true;
    }

    @Override
    public void changeState(MaterialRequest materialRequest, MaterialRequestRepository materialRequestRepository, String response) {
    }

}
