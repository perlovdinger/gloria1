package com.volvo.gloria.procurematerial.d.entities.status.changeid;

import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.materialRequestProxy.b.MaterialProcureResponse;
import com.volvo.gloria.procurematerial.d.entities.ChangeId;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;

/**
 * Operations for ACCEPTED status.
 */
public class Accepted extends ChangeIdDefaultOperations {

    @Override
    public void cancel(ChangeId changeId, MaterialHeaderRepository requestHeaderRepository, MaterialProcureResponse materialProcureResponse, String userId,
            ProcureLineRepository procureLineRepository, TraceabilityRepository traceabilityRepository)
            throws GloriaApplicationException {
        if (ChangeIdStatusHelper.hasChangeIdMaterialHeadersAssignedToMaterialController(changeId)) {
            ChangeIdStatusHelper.setChangeIdStatus(changeId, requestHeaderRepository, materialProcureResponse, userId, ChangeIdStatus.CANCEL_WAIT);
            ChangeIdStatusHelper.cancelMaterials(changeId, requestHeaderRepository, procureLineRepository, traceabilityRepository);
        } else {
            ChangeIdStatusHelper.setChangeIdStatus(changeId, requestHeaderRepository, materialProcureResponse, userId, ChangeIdStatus.CANCELLED);
            ChangeIdStatusHelper.removeAllNeed(changeId, requestHeaderRepository, traceabilityRepository);
            ChangeIdStatusHelper.updateVersionAsAccepted(changeId, requestHeaderRepository);
        }
    }
    
    @Override
    public void setAcceptedRelationToMaterialHeader(ChangeId changeId, MaterialHeaderRepository requestHeaderRepo) {
        ChangeIdStatusHelper.updateVersionAsAccepted(changeId, requestHeaderRepo);
    }
}
