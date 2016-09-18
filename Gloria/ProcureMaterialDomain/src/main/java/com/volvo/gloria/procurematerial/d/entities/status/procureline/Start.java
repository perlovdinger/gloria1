package com.volvo.gloria.procurematerial.d.entities.status.procureline;

import com.volvo.gloria.procurematerial.d.entities.ProcureLine;

public class Start extends ProcureLineDefaultOperations {

    @Override
    public void init(ProcureLine procureLine) {
        procureLine.setStatus(ProcureLineStatus.WAIT_TO_PROCURE);
    }
}
