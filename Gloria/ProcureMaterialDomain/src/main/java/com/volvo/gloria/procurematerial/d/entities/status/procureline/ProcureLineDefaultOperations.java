package com.volvo.gloria.procurematerial.d.entities.status.procureline;

import java.util.List;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.c.dto.MaterialDTO;
import com.volvo.gloria.procurematerial.c.dto.ProcureLineDTO;
import com.volvo.gloria.procurematerial.d.entities.ChangeId;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.repositories.b.PurchaseOrganisationRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;

public class ProcureLineDefaultOperations implements ProcureLineOperations {

    private static final String EXCEPTION_MESSAGE = "This operation is not supported in current state.";

    @Override
    public void init(ProcureLine procureLine) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, EXCEPTION_MESSAGE + this.getClass().getName());
    }

    @Override
    public void procured(ProcureLine procureLine) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, EXCEPTION_MESSAGE + this.getClass().getName());        
    }

    @Override
    public void revertToWait(ProcureLine procureLine, ProcureLineRepository procureLineRepository) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, EXCEPTION_MESSAGE + this.getClass().getName());
    }

    @Override
    public void remove(ProcureLine procureLine, ProcureLineRepository procureLineRepository) throws GloriaApplicationException {
    }

    @Override
    public void validateReassign() throws GloriaApplicationException {
        throw new GloriaApplicationException(null, EXCEPTION_MESSAGE + this.getClass().getName());
    }
    
    @Override
    public void acceptRemoveMaterial(ProcureLine procureLine, ProcureLineRepository procureLineRepository, List<MaterialDTO> materialDTOs,
            Material removeMaterial, MaterialHeaderRepository requestHeaderRepo, ProcurementServices procurementServices, OrderRepository orderRepository,
            UserDTO userDto, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, EXCEPTION_MESSAGE + this.getClass().getName());
    }

    @Override
    public void cancelModification(List<Material> materials, MaterialHeaderRepository requestHeaderRepository, ProcurementServices procurementServices,
            ProcureLineRepository procureLineRepository, ProcureLine procureLine, UserDTO userDTO) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, EXCEPTION_MESSAGE + this.getClass().getName());
    }

    @Override
    public void updateProcureLine(ProcureLineDTO procureLineDTO, ProcureLine procureLine, String action, String teamId, UserDTO userDto,
            ProcureLineRepository procureLineRepository, CommonServices commonServices,
            ProcurementServices procurementServices, PurchaseOrganisationRepository purchaseOrganisationRepo, 
            String finalWarehouseId) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, EXCEPTION_MESSAGE + this.getClass().getName());
    }
    
    @Override
    public boolean isRevertProcurementAllowed(ProcureLine procureLine) {
        return true;
    }

    @Override
    public String getUsageChangeAction(Material material) {
        return null;
    }

    @Override
    public String getUsageReplacedChangeAction(Material material) {
        return null;
    }

    @Override
    public void acceptRemoveUsageReplacedMaterial(ProcureLine procureLine, ProcureLineRepository procureLineRepository, List<MaterialDTO> materialDTOs,
            Material removeMaterial, MaterialHeaderRepository requestHeaderRepo, ProcurementServices procurementServices, OrderRepository orderRepository,
            UserServices userServices, ChangeId changeId, String userID, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, EXCEPTION_MESSAGE + this.getClass().getName());
    }

    @Override
    public boolean isOrderPlaced() {
        return false;
    }
    
    @Override
    public boolean isProcurementStarted() {
        return false;
    }
}
