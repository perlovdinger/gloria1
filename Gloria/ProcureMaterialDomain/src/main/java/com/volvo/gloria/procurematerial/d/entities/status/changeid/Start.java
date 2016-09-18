package com.volvo.gloria.procurematerial.d.entities.status.changeid;

import com.volvo.gloria.materialRequestProxy.b.MaterialProcureResponse;
import com.volvo.gloria.procurematerial.d.entities.ChangeId;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;

/**
 * Operations for START.
 */
public class Start extends ChangeIdDefaultOperations {
    @Override
    public void init(boolean procureLineExists, MaterialHeaderRepository requestHeaderRepo, MaterialProcureResponse materialProcureResponse, ChangeId changeId,
            String userId) {
        if (procureLineExists) {
            changeId.setVisibleUi(true);
            ChangeIdStatusHelper.setChangeIdStatus(changeId, requestHeaderRepo, materialProcureResponse, userId, ChangeIdStatus.WAIT_CONFIRM);
        } else {
            changeId.setVisibleUi(false);
            ChangeIdStatusHelper.setChangeIdStatus(changeId, requestHeaderRepo, materialProcureResponse, userId, ChangeIdStatus.ACCEPTED);
            ChangeIdStatusHelper.updateWithMtrlAcceptedVersion(changeId);
        }
    }

}
