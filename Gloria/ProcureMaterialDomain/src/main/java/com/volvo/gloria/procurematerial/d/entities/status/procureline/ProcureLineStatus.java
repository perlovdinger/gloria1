package com.volvo.gloria.procurematerial.d.entities.status.procureline;

import java.util.List;

import org.apache.commons.lang.StringUtils;

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


/**
 * Possible ProcureLine status values.
 */
public enum ProcureLineStatus implements ProcureLineOperations {
    START(new Start()), 
    WAIT_TO_PROCURE(new WaitToProcure()), 
    PROCURED(new Procured()), 
    PLACED(new Placed()), 
    CANCELLED(new Cancelled()),
    RECEIVED_PARTLY(new ReceivedPartly()),
    RECEIVED(new Received());
    
    
    private final ProcureLineOperations procureLineOperations;

    ProcureLineStatus(ProcureLineOperations procureLineOperations) {
        this.procureLineOperations = procureLineOperations;
    }
    
    @Override
    public void init(ProcureLine procureLine) throws GloriaApplicationException {
        procureLineOperations.init(procureLine);
    }
    
    @Override
    public void procured(ProcureLine procureLine) throws GloriaApplicationException {
        procureLineOperations.procured(procureLine);
    }
    
    @Override
    public void revertToWait(ProcureLine procureLine, ProcureLineRepository procureLineRepository) throws GloriaApplicationException {
            procureLineOperations.revertToWait(procureLine, procureLineRepository);
    }
    
    public static ProcureLineStatus getValue(String string) {
        if (StringUtils.isEmpty(string)) {
            return null;
        } else {
            return ProcureLineStatus.valueOf(string);
        }
    }

    @Override
    public void remove(ProcureLine procureLine, ProcureLineRepository procureLineRepository) throws GloriaApplicationException {
        procureLineOperations.remove(procureLine, procureLineRepository);
    }

    @Override
    public void validateReassign() throws GloriaApplicationException {
        procureLineOperations.validateReassign();        
    }

    @Override
    public void acceptRemoveMaterial(ProcureLine procureLine, ProcureLineRepository procureLineRepository, List<MaterialDTO> materialDTOs,
            Material removeMaterial, MaterialHeaderRepository requestHeaderRepo, ProcurementServices procurementServices, OrderRepository orderRepository,
            UserDTO userDto, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        procureLineOperations.acceptRemoveMaterial(procureLine, procureLineRepository, materialDTOs, removeMaterial, requestHeaderRepo, procurementServices,
                                                   orderRepository, userDto, traceabilityRepository);
    }

    @Override
    public void cancelModification(List<Material> materials, MaterialHeaderRepository requestHeaderRepository, ProcurementServices procurementServices,
            ProcureLineRepository procureLineRepository, ProcureLine procureLine, UserDTO userDTO) throws GloriaApplicationException {
        procureLineOperations.cancelModification(materials, requestHeaderRepository, procurementServices, procureLineRepository, procureLine, userDTO);
    }

    @Override
    public void updateProcureLine(ProcureLineDTO procureLineDTO, ProcureLine procureLine, String action, String teamId, UserDTO userDto,
            ProcureLineRepository procureLineRepository, CommonServices commonServices, ProcurementServices procurementServices,
            PurchaseOrganisationRepository purchaseOrganisationRepo, String finalWarehouseId) throws GloriaApplicationException {
        procureLineOperations.updateProcureLine(procureLineDTO, procureLine, action, teamId, userDto, procureLineRepository, commonServices,
                                                procurementServices, purchaseOrganisationRepo, finalWarehouseId);
    }
    
    @Override
    public boolean isRevertProcurementAllowed(ProcureLine procureLine) {
        return procureLineOperations.isRevertProcurementAllowed(procureLine);
    }

    @Override
    public String getUsageChangeAction(Material material) {
        return procureLineOperations.getUsageChangeAction(material);
    }

    @Override
    public String getUsageReplacedChangeAction(Material material) {
        return procureLineOperations.getUsageReplacedChangeAction(material);
    }

    @Override
    public void acceptRemoveUsageReplacedMaterial(ProcureLine procureLine, ProcureLineRepository procureLineRepository, List<MaterialDTO> materialDTOs,
            Material removeMaterial, MaterialHeaderRepository requestHeaderRepo, ProcurementServices procurementServices, 
            OrderRepository orderRepository, UserServices userServices, ChangeId changeId, String userID, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        procureLineOperations.acceptRemoveUsageReplacedMaterial(procureLine, procureLineRepository, materialDTOs, removeMaterial, requestHeaderRepo,
                                                                procurementServices, orderRepository, userServices, changeId, userID, traceabilityRepository);
    }

    @Override
    public boolean isOrderPlaced() {
        return procureLineOperations.isOrderPlaced();
    }

    @Override
    public boolean isProcurementStarted() {
        return procureLineOperations.isProcurementStarted();
    }
}
