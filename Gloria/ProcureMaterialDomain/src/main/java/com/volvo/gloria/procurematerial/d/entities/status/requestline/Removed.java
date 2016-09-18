package com.volvo.gloria.procurematerial.d.entities.status.requestline;

import java.util.List;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.c.dto.MaterialDTO;
import com.volvo.gloria.procurematerial.d.entities.ChangeId;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;

/**
 * Operations for REMOVE status.
 */
public class Removed extends MaterialStatusDefaultOperations {
    @Override
    public void needIsRemoved(ChangeId changeId, Material material, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
    }

    @Override
    public void accept(MaterialHeaderRepository requestHeaderRepo, Material material, ChangeId changeId, ProcurementServices procurementServices,
            ProcureLineRepository procureLineRepository, List<MaterialDTO> materialDTOs, OrderRepository orderRepository, UserServices userServices,
            String userId, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
    }

    @Override
    public void remove(ChangeId changeId, boolean procureLineExists, Material material, ProcureLineRepository procureLineRepository,
            TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
    }

}
