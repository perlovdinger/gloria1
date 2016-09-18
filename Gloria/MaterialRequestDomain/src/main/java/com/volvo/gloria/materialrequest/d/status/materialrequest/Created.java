package com.volvo.gloria.materialrequest.d.status.materialrequest;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.repositories.b.UnitOfMeasureRepository;
import com.volvo.gloria.materialRequestProxy.b.MaterialRequestSender;
import com.volvo.gloria.materialrequest.c.dto.MaterialRequestLineDTO;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequest;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequestLine;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequestVersion;
import com.volvo.gloria.materialrequest.repositories.b.MaterialRequestRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.c.GloriaExceptionConstants;

/**
 * Operations for CREATED status.
 */
public class Created extends MaterialRequestDefaultOperations {

    private static final String ACCEPTED = "ACCEPTED";

    @Override
    public MaterialRequestLine createMaterialRequestLine(MaterialRequestLineDTO materialRequestLineDTO, MaterialRequestRepository materialRequestRepository,
            MaterialRequest materialRequest, UnitOfMeasureRepository unitOfMeasureRepo) throws GloriaApplicationException {
        return MaterialRequestStatusHelper.doCreateMaterialRequestLine(materialRequestLineDTO, materialRequestRepository, materialRequest, unitOfMeasureRepo);
    }

    @Override
    public void deleteMaterialRequestLine(MaterialRequestLine materialRequestLine, MaterialRequestRepository materialRequestRepository,
            MaterialRequestVersion materialRequestVersion) throws GloriaApplicationException {
        MaterialRequestStatusHelper.doDeleteMaterialRequestLine(materialRequestLine, materialRequestRepository, materialRequestVersion);
    }

    @Override
    public MaterialRequestLine updateMaterialRequestLine(MaterialRequestLineDTO materialRequestLineDTO, MaterialRequestRepository materialRequestRepository,
            MaterialRequest materialRequest, UnitOfMeasureRepository unitOfMeasureRepo) throws GloriaApplicationException {
        MaterialRequestVersion current = materialRequest.getCurrent();
        MaterialRequestLine materialRequestLine = materialRequestRepository.findMaterialRequestLineById(materialRequestLineDTO.getId());
        if (materialRequestLineDTO.getVersion() != materialRequestLine.getVersion()) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_UPDATION_OF_STALE_DATASET,
                                                 "This operation cannot be performed since the information seen in the page has already been updated.");
        }
        MaterialRequestStatusHelper.validatePartInfo(materialRequestLineDTO, unitOfMeasureRepo.findAllUnitOfMeasuresSupportedForGloria());
        return MaterialRequestStatusHelper.doUpdateMaterialRequestLine(materialRequestLineDTO, materialRequestRepository, current, materialRequestLine);
    }

    @Override
    public void delete(MaterialRequest materialRequest, MaterialRequestRepository materialRequestRepository) {
        materialRequestRepository.delete(materialRequest.getMaterialRequestOid());
    }

    @Override
    public MaterialRequest send(MaterialRequest materialRequest, MaterialRequestRepository materialRequestRepository,
            MaterialRequestSender materialRequestSender, CommonServices commonServices) throws GloriaApplicationException {
        MaterialRequest updatedMaterialRequest = MaterialRequestStatusHelper.prepareAndSendMaterialRequest(materialRequest, materialRequestRepository,
                                                                                                           materialRequestSender);
        updatedMaterialRequest.getCurrent().setStatus(MaterialRequestStatus.SENT_ACCEPTED);
        return materialRequestRepository.save(updatedMaterialRequest);
    }

    @Override
    public void changeState(MaterialRequest materialRequest, MaterialRequestRepository materialRequestRepository, String response) {
        if (response.equalsIgnoreCase(ACCEPTED)) {
            materialRequest.getCurrent().setStatus(MaterialRequestStatus.SENT_ACCEPTED);
        }
    }

    @Override
    public MaterialRequestLine undoRemoveMaterialRequestLine(MaterialRequestLine materialRequestLine, MaterialRequestRepository materialRequestRepository,
            MaterialRequestVersion current) throws GloriaApplicationException {
        return MaterialRequestStatusHelper.doUndoRemoveMaterialRequestLine(materialRequestLine, materialRequestRepository, current);
    }
}
