package com.volvo.gloria.materialrequest.d.status.materialrequest;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.repositories.b.UnitOfMeasureRepository;
import com.volvo.gloria.materialRequestProxy.b.MaterialRequestCancelSender;
import com.volvo.gloria.materialRequestProxy.b.MaterialRequestSender;
import com.volvo.gloria.materialrequest.c.dto.MaterialRequestLineDTO;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequest;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequestLine;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequestVersion;
import com.volvo.gloria.materialrequest.repositories.b.MaterialRequestRepository;
import com.volvo.gloria.util.GloriaApplicationException;

/**
 * MaterialRequestHeader possible statuses supporting operations in behaviour classes..
 */
public enum MaterialRequestStatus implements MaterialRequestOperations {

    CREATED(new Created()), 
    SENT_WAIT(new SentWait()), 
    SENT_REJECTED(new SentRejected()), 
    SENT_ACCEPTED(new SentAccepted()), 
    UPDATED(new Updated()), 
    CANCEL_WAIT(new CancelWait()), 
    CANCEL_REJECTED(new CancelRejected()), 
    CANCELLED(new Cancelled());

    private final MaterialRequestOperations materialRequestOperations;

    MaterialRequestStatus(MaterialRequestOperations materialRequestOperations) {
        this.materialRequestOperations = materialRequestOperations;
    }

    @Override
    public MaterialRequest send(MaterialRequest materialRequest, MaterialRequestRepository materialRequestRepository,
            MaterialRequestSender materialRequestSender, CommonServices commonServices) throws GloriaApplicationException {
        return materialRequestOperations.send(materialRequest, materialRequestRepository, materialRequestSender, commonServices);
    }

    @Override
    public void delete(MaterialRequest materialRequest, MaterialRequestRepository materialRequestRepository) throws GloriaApplicationException {
        materialRequestOperations.delete(materialRequest, materialRequestRepository);
    }

    @Override
    public MaterialRequestStatus cancel(MaterialRequest materialRequest, MaterialRequestCancelSender materialRequestCancelSender,
            MaterialRequestRepository materialRequestRepository) throws GloriaApplicationException {
        return materialRequestOperations.cancel(materialRequest, materialRequestCancelSender, materialRequestRepository);
    }

    @Override
    public MaterialRequestStatus mcAccept(MaterialRequest materialRequest) throws GloriaApplicationException {
        return materialRequestOperations.mcAccept(materialRequest);
    }

    @Override
    public MaterialRequestStatus mcReject(MaterialRequest materialRequest) throws GloriaApplicationException {
        return materialRequestOperations.mcReject(materialRequest);
    }

    @Override
    public MaterialRequestLine updateMaterialRequestLine(MaterialRequestLineDTO materialRequestLineDTO, MaterialRequestRepository materialRequestRepository,
            MaterialRequest materialRequest, UnitOfMeasureRepository unitOfMeasureRepo) throws GloriaApplicationException {
        return materialRequestOperations.updateMaterialRequestLine(materialRequestLineDTO, materialRequestRepository, materialRequest, unitOfMeasureRepo);
    }

    @Override
    public MaterialRequestLine createMaterialRequestLine(MaterialRequestLineDTO materialRequestLineDTO, MaterialRequestRepository materialRequestRepository,
            MaterialRequest materialRequest, UnitOfMeasureRepository unitOfMeasureRepo) throws GloriaApplicationException {
        return materialRequestOperations.createMaterialRequestLine(materialRequestLineDTO, materialRequestRepository, materialRequest, unitOfMeasureRepo);
    }

    @Override
    public MaterialRequest newVersion(MaterialRequest materialRequest, MaterialRequestRepository materialRequestRepository) throws GloriaApplicationException {
        return materialRequestOperations.newVersion(materialRequest, materialRequestRepository);
    }

    @Override
    public void deleteMaterialRequestLine(MaterialRequestLine materialRequestLine, MaterialRequestRepository materialRequestRepository,
            MaterialRequestVersion materialRequestVersion) throws GloriaApplicationException {
        materialRequestOperations.deleteMaterialRequestLine(materialRequestLine, materialRequestRepository, materialRequestVersion);
    }

    public MaterialRequest revert(MaterialRequest materialRequest, MaterialRequestRepository materialRequestRepository) throws GloriaApplicationException {
        return materialRequestOperations.revert(materialRequest, materialRequestRepository);
    }

    public boolean isSentAccepted() {
        return materialRequestOperations.isSentAccepted();
    }

    @Override
    public void changeState(MaterialRequest materialRequest, MaterialRequestRepository materialRequestRepository, String response)
            throws GloriaApplicationException {
        materialRequestOperations.changeState(materialRequest, materialRequestRepository, response);
    }

    public MaterialRequestLine undoRemoveMaterialRequestLine(MaterialRequestLine materialRequestLine, MaterialRequestRepository materialRequestRepository,
            MaterialRequestVersion current) throws GloriaApplicationException {
        return materialRequestOperations.undoRemoveMaterialRequestLine( materialRequestLine,  materialRequestRepository,
                                                                        current);
    }
}
