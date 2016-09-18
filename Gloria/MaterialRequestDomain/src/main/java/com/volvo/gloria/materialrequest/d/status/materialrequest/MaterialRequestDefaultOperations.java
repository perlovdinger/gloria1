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
 * Default operations.
 */
public class MaterialRequestDefaultOperations implements MaterialRequestOperations {

    @Override
    public void delete(MaterialRequest materialRequest, MaterialRequestRepository materialRequestRepository) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state" + this.getClass().getName());
    }

    @Override
    public MaterialRequestStatus cancel(MaterialRequest materialRequest, MaterialRequestCancelSender materialRequestCancelSender,
            MaterialRequestRepository materialRequestRepository) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }

    @Override
    public MaterialRequestStatus mcAccept(MaterialRequest materialRequest) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }

    @Override
    public MaterialRequestStatus mcReject(MaterialRequest materialRequest) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }

    @Override
    public MaterialRequestLine updateMaterialRequestLine(MaterialRequestLineDTO materialRequestLineDTO, MaterialRequestRepository materialRequestRepository,
            MaterialRequest materialRequest, UnitOfMeasureRepository unitOfMeasureRepo) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }

    @Override
    public MaterialRequest newVersion(MaterialRequest materialRequest, MaterialRequestRepository materialRequestRepository) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }

    @Override
    public MaterialRequestLine createMaterialRequestLine(MaterialRequestLineDTO materialRequestLineDTO, MaterialRequestRepository materialRequestRepository,
            MaterialRequest materialRequest, UnitOfMeasureRepository unitOfMeasureRepo) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }

    @Override
    public void deleteMaterialRequestLine(MaterialRequestLine materialRequestLine, MaterialRequestRepository materialRequestRepository,
            MaterialRequestVersion materialRequestVersion) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }

    @Override
    public MaterialRequest send(MaterialRequest materialRequest, MaterialRequestRepository materialRequestRepository,
            MaterialRequestSender materialRequestSender, CommonServices commonServices) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state. " + this.getClass().getName());
    }

    @Override
    public MaterialRequest revert(MaterialRequest materialRequest, MaterialRequestRepository materialRequestRepository) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }

    @Override
    public boolean isSentAccepted() {
        return false;
    }

    @Override
    public void changeState(MaterialRequest materialRequest, MaterialRequestRepository materialRequestRepository, String response)
            throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }

    @Override
    public MaterialRequestLine undoRemoveMaterialRequestLine(MaterialRequestLine materialRequestLine, MaterialRequestRepository materialRequestRepository,
            MaterialRequestVersion current) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }
}
