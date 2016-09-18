package com.volvo.gloria.procurematerial.d.entities.status.procureline;

import java.util.List;

import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.c.dto.MaterialDTO;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.c.GloriaParams;

public class Received extends ProcureLineDefaultOperations {

    @Override
    public void acceptRemoveMaterial(ProcureLine procureLine, ProcureLineRepository procureLineRepository, List<MaterialDTO> materialDTOs,
            Material removeMaterial, MaterialHeaderRepository requestHeaderRepo, ProcurementServices procurementServices, OrderRepository orderRepository,
            UserDTO userDto, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        ProcureLineHelper.handleReceivedProcureLines(procureLine, procureLineRepository, removeMaterial, requestHeaderRepo, userDto, traceabilityRepository);
    }

    @Override
    public boolean isRevertProcurementAllowed(ProcureLine procureLine) {
        return false;
    }
    
    @Override
    public String getUsageChangeAction(Material material) {
        return GloriaParams.ADDITIONAL;
    }
    
    @Override
    public boolean isOrderPlaced() {
        return true;
    }
    
    @Override
    public boolean isProcurementStarted() {
        return true;
    }
}

