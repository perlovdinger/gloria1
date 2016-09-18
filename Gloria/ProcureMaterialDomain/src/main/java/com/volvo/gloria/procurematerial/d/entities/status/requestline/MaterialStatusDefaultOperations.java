package com.volvo.gloria.procurematerial.d.entities.status.requestline;

import java.util.List;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.common.c.dto.UserDTO;
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
 * Default operations.
 */
public class MaterialStatusDefaultOperations implements MaterialStatusOperations {

    private static final String EXCEPTION_MESSAGE = "This operation is not supported in current state.";

    @Override
    public void init(boolean procureLineExists, Material material) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, EXCEPTION_MESSAGE + this.getClass().getName());      
    }

    @Override
    public void accept(MaterialHeaderRepository requestHeaderRepo, Material material, ChangeId changeId, ProcurementServices procurementServices,
            ProcureLineRepository procureLineRepository, List<MaterialDTO> materialDTOs, OrderRepository orderRepository, UserServices userServices,
            String userId, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, EXCEPTION_MESSAGE + this.getClass().getName());
    }

    @Override
    public void reject(Material material, MaterialHeaderRepository requestHeaderRepo, TraceabilityRepository traceabilityRepository, UserDTO userDTO)
            throws GloriaApplicationException {
        throwException(material);
    }

    @Override
    public void remove(ChangeId changeId, boolean procureLineExists, Material material, ProcureLineRepository procureLineRepository,
            TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        throwException(material);
    }

    @Override
    public boolean isAvailableForGrouping(Material material) {
        return false;
    }

    @Override
    public void needIsRemoved(ChangeId changeId, Material material, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        throwException(material);
    }

    private void throwException(Material material) throws GloriaApplicationException {
        if (this.getClass() != MaterialStatusDefaultOperations.class) {
            throw new GloriaApplicationException(null, EXCEPTION_MESSAGE + material.getStatus());    
        }        
    }

    @Override
    public String getChangeAction(Material material) {
        return null;
    }

    @Override
    public void acceptAfterCancelOrder(Material material, TraceabilityRepository traceabilityRepository) {
        //do nothing
    }
 }
