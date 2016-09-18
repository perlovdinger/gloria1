package com.volvo.gloria.procurematerial.d.entities.status.materialLine;

import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;


public class RemovedDb extends MaterialLineDefaultOperation {


    @Override
    public boolean isRemovedDb() {
        return true;
    }
    
    @Override
    public void cancel(MaterialLine materialLine, MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository,
            UserDTO userDTO) throws GloriaApplicationException {
        materialLine.setOrderCancelled(true);
    }
}
