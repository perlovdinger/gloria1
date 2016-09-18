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
 * Possible operations for MaterialRequest.
 */
public interface MaterialRequestOperations {

    void delete(MaterialRequest materialRequest, MaterialRequestRepository materialRequestRepository) throws GloriaApplicationException;

    MaterialRequestStatus cancel(MaterialRequest materialRequest, MaterialRequestCancelSender materialRequestCancelSender,
            MaterialRequestRepository materialRequestRepository) throws GloriaApplicationException;

    public MaterialRequest newVersion(MaterialRequest materialRequest, MaterialRequestRepository materialRequestRepository) throws GloriaApplicationException;

    MaterialRequestStatus mcAccept(MaterialRequest materialRequest) throws GloriaApplicationException;

    MaterialRequestStatus mcReject(MaterialRequest materialRequest) throws GloriaApplicationException;

    MaterialRequestLine createMaterialRequestLine(MaterialRequestLineDTO materialRequestLineDTO, MaterialRequestRepository materialRequestRepository,
            MaterialRequest materialRequest, UnitOfMeasureRepository unitOfMeasureRepo) throws GloriaApplicationException;

    MaterialRequestLine updateMaterialRequestLine(MaterialRequestLineDTO materialRequestLineDTO, MaterialRequestRepository materialRequestRepository,
            MaterialRequest materialRequest, UnitOfMeasureRepository unitOfMeasureRepo) throws GloriaApplicationException;

    void deleteMaterialRequestLine(MaterialRequestLine materialRequestLine, MaterialRequestRepository materialRequestRepository,
            MaterialRequestVersion materialRequestVersion) throws GloriaApplicationException;

    MaterialRequest send(MaterialRequest materialRequest, MaterialRequestRepository materialRequestRepository, MaterialRequestSender materialRequestSender,
            CommonServices commonServices) throws GloriaApplicationException;

    MaterialRequest revert(MaterialRequest materialRequest, MaterialRequestRepository materialRequestRepository) throws GloriaApplicationException;

    boolean isSentAccepted();

    void changeState(MaterialRequest materialRequest, MaterialRequestRepository materialRequestRepository, String response) throws GloriaApplicationException;

    MaterialRequestLine undoRemoveMaterialRequestLine(MaterialRequestLine materialRequestLine, MaterialRequestRepository materialRequestRepository,
            MaterialRequestVersion current) throws GloriaApplicationException;

}
