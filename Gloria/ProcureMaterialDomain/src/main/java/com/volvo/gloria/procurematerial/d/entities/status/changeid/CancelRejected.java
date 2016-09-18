package com.volvo.gloria.procurematerial.d.entities.status.changeid;

import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.materialRequestProxy.b.MaterialProcureResponse;
import com.volvo.gloria.procurematerial.d.entities.ChangeId;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;

public class CancelRejected extends ChangeIdDefaultOperations {
    @Override
    public void cancel(ChangeId changeId, MaterialHeaderRepository requestHeaderRepository, MaterialProcureResponse materialProcureResponse, String userId,
            ProcureLineRepository procureLineRepository, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        ChangeIdStatusHelper.setChangeIdStatus(changeId, requestHeaderRepository, materialProcureResponse, userId, ChangeIdStatus.CANCEL_WAIT);
        ChangeIdStatusHelper.updateNeedIsChanged(changeId, procureLineRepository, true);
    }
    
    @Override
    public boolean isCancelRejected() {
        return true;
    }
}
