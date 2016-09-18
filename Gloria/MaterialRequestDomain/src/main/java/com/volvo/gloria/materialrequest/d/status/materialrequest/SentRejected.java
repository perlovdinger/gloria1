package com.volvo.gloria.materialrequest.d.status.materialrequest;

import java.util.List;

import com.volvo.gloria.materialRequestProxy.b.MaterialRequestCancelSender;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequest;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequestVersion;
import com.volvo.gloria.materialrequest.repositories.b.MaterialRequestRepository;
import com.volvo.gloria.util.GloriaApplicationException;

/**
 * Operations for SENT_REJECTED status.
 */
public class SentRejected extends MaterialRequestDefaultOperations {

    @Override
    public MaterialRequest newVersion(MaterialRequest materialRequest, MaterialRequestRepository materialRequestRepository) throws GloriaApplicationException {
        List<MaterialRequestVersion> materialRequestVersions = materialRequest.getMaterialRequestVersions();
        MaterialRequestStatusHelper.sortMaterialRequestHeaderVersionsByDescOrder(materialRequestVersions);
        long changeVersion = materialRequest.getCurrent().getChangeVersion();
        for (MaterialRequestVersion materialRequestVersion : materialRequestVersions) {
            if (materialRequestVersion.getStatus().isSentAccepted()) {
                materialRequest.setCurrent(materialRequestVersion);
                break;
            }
        }
        return MaterialRequestStatusHelper.newVersion(materialRequest, materialRequestRepository, changeVersion);
    }

    @Override
    public MaterialRequest revert(MaterialRequest materialRequest, MaterialRequestRepository materialRequestRepository) throws GloriaApplicationException {
        long previousChangeVersion = materialRequest.getCurrent().getChangeVersion() - 1;

        MaterialRequestVersion materialRequestVersion = MaterialRequestStatusHelper.backOneVerion(previousChangeVersion, materialRequest,
                                                                                                  materialRequestRepository);
        return materialRequestVersion.getStatus().revert(materialRequest, materialRequestRepository);
    }

    @Override
    public MaterialRequestStatus cancel(MaterialRequest materialRequest, MaterialRequestCancelSender materialRequestCancelSender,
            MaterialRequestRepository materialRequestRepository) throws GloriaApplicationException {
        long previousChangeVersion = materialRequest.getCurrent().getChangeVersion() - 1;

        MaterialRequestVersion previousMaterialRequestVersion = MaterialRequestStatusHelper.backOneVerion(previousChangeVersion, materialRequest,
                                                                                                          materialRequestRepository);
        materialRequest = previousMaterialRequestVersion.getStatus().revert(materialRequest, materialRequestRepository);
        return MaterialRequestStatusHelper.cancelMaterialRequest(materialRequest, materialRequestCancelSender, materialRequestRepository);
    }
}
