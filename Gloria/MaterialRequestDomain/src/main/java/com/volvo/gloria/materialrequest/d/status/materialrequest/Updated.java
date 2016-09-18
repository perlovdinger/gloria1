package com.volvo.gloria.materialrequest.d.status.materialrequest;

import java.util.ArrayList;
import java.util.List;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.repositories.b.UnitOfMeasureRepository;
import com.volvo.gloria.materialRequestProxy.b.MaterialRequestSender;
import com.volvo.gloria.materialrequest.c.dto.MaterialRequestLineDTO;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequest;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequestLine;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequestVersion;
import com.volvo.gloria.materialrequest.repositories.b.MaterialRequestRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.Utils;
import com.volvo.gloria.util.c.GloriaExceptionConstants;

/**
 * Operations for UPDATED status.
 */
public class Updated extends MaterialRequestDefaultOperations {

    @Override
    public MaterialRequestLine createMaterialRequestLine(MaterialRequestLineDTO materialRequestLineDTO, MaterialRequestRepository materialRequestRepository,
            MaterialRequest materialRequest, UnitOfMeasureRepository unitOfMeasureRepo) throws GloriaApplicationException {
        return MaterialRequestStatusHelper.doCreateMaterialRequestLine(materialRequestLineDTO, materialRequestRepository, materialRequest, unitOfMeasureRepo);
    }

    @Override
    public MaterialRequestLine updateMaterialRequestLine(MaterialRequestLineDTO materialRequestLineDTO, MaterialRequestRepository materialRequestRepository,
            MaterialRequest materialRequest, UnitOfMeasureRepository unitOfMeasureRepo) throws GloriaApplicationException {
        MaterialRequestVersion current = materialRequest.getCurrent();
        MaterialRequestLine originalMRLine = materialRequestRepository.findMaterialRequestLineById(materialRequestLineDTO.getId());
        if (materialRequestLineDTO.getVersion() != originalMRLine.getVersion()) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_UPDATION_OF_STALE_DATASET,
                                                 "This operation cannot be performed since the information seen in the page has already been updated.");
        }
        MaterialRequestStatusHelper.validatePartInfo(materialRequestLineDTO, unitOfMeasureRepo.findAllUnitOfMeasuresSupportedForGloria());

        MaterialRequestLine updatedMRLine = null;
        if (current.getChangeTechId().equalsIgnoreCase(originalMRLine.getChangeTechId())) {
            updatedMRLine = MaterialRequestStatusHelper.doUpdateMaterialRequestLine(materialRequestLineDTO, materialRequestRepository, current, originalMRLine);
        } else {
            List<MaterialRequestLine> materialRequestLinesToUpdate = new ArrayList<MaterialRequestLine>();
            originalMRLine.setRemoveMarked(true);
            originalMRLine.setChangeTechId(current.getChangeTechId());
            originalMRLine.setOperation("REMOVED");
            materialRequestLinesToUpdate.add(originalMRLine);

            updatedMRLine = cloneMaterialRequestLine(materialRequestLineDTO, originalMRLine, current, materialRequestRepository);
            materialRequestLinesToUpdate.add(updatedMRLine);
            materialRequestRepository.saveOrUpdateMaterialRequestLines(materialRequestLinesToUpdate);
        }

        return updatedMRLine;
    }

    private MaterialRequestLine cloneMaterialRequestLine(MaterialRequestLineDTO materialRequestLineDTO, MaterialRequestLine originalMRLine,
            MaterialRequestVersion current, MaterialRequestRepository materialRequestRepository) throws GloriaApplicationException {
        MaterialRequestLine updatedMRLine = new MaterialRequestLine();
        updatedMRLine.setChangeTechId(current.getChangeTechId());
        updatedMRLine.setFunctionGroup(materialRequestLineDTO.getFunctionGroup());
        updatedMRLine.setMaterialRequestHeaderVersion(current);
        updatedMRLine.setOperation("ADDED");
        updatedMRLine.setPartAffiliation(materialRequestLineDTO.getPartAffiliation());
        updatedMRLine.setPartModification(materialRequestLineDTO.getPartModification());
        updatedMRLine.setPartName(materialRequestLineDTO.getPartName());
        updatedMRLine.setPartNumber(materialRequestLineDTO.getPartNumber());
        updatedMRLine.setPartVersion(materialRequestLineDTO.getPartVersion());
        updatedMRLine.setQuantity(materialRequestLineDTO.getQuantity());
        updatedMRLine.setRemoveMarked(false);
        updatedMRLine.setUnitOfMeasure(materialRequestLineDTO.getUnitOfMeasure());
        updatedMRLine.setMaterialRequestObject(originalMRLine.getMaterialRequestObject());
        MaterialRequestStatusHelper.handleMaterialRequestObject(materialRequestLineDTO, materialRequestRepository, updatedMRLine, current);
        updatedMRLine.setRequestLinkId(Utils.createRandomNumber());
        updatedMRLine.setUpdatedMaterialRequestlineOid(originalMRLine.getMaterialRequestLineOid());
        return updatedMRLine;
    }

    @Override
    public void deleteMaterialRequestLine(MaterialRequestLine materialRequestLine, MaterialRequestRepository materialRequestRepository,
            MaterialRequestVersion currentMaterialRequestVersion) throws GloriaApplicationException {
        MaterialRequestStatusHelper.doDeleteMaterialRequestLine(materialRequestLine, materialRequestRepository, currentMaterialRequestVersion);
    }

    @Override
    public MaterialRequest send(MaterialRequest materialRequest, MaterialRequestRepository materialRequestRepository,
            MaterialRequestSender materialRequestSender, CommonServices commonServices) throws GloriaApplicationException {
        MaterialRequest updatedMaterialRequest = MaterialRequestStatusHelper.prepareAndSendMaterialRequest(materialRequest, materialRequestRepository,
                                                                                                           materialRequestSender);
        updatedMaterialRequest.getCurrent().setStatus(MaterialRequestStatus.SENT_WAIT);
        return materialRequestRepository.save(updatedMaterialRequest);
    }

    @Override
    public MaterialRequest revert(MaterialRequest materialRequest, MaterialRequestRepository materialRequestRepository) throws GloriaApplicationException {
        MaterialRequestVersion materialRequestVersion = MaterialRequestStatusHelper.doRevert(materialRequest, materialRequestRepository);
        return materialRequestVersion.getStatus().revert(materialRequest, materialRequestRepository);
    }
    
    @Override
    public MaterialRequestLine undoRemoveMaterialRequestLine(MaterialRequestLine materialRequestLine, MaterialRequestRepository materialRequestRepository,
            MaterialRequestVersion current) throws GloriaApplicationException {
        return MaterialRequestStatusHelper.doUndoRemoveMaterialRequestLine(materialRequestLine, materialRequestRepository, current);
    }
}
