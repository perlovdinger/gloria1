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

/**
 * Possible operations for all status.
 */
public interface ProcureLineOperations {

    void init(ProcureLine procureLine) throws GloriaApplicationException;

    void procured(ProcureLine procureLine) throws GloriaApplicationException;

    void revertToWait(ProcureLine procureLine, ProcureLineRepository procureLineRepository) throws GloriaApplicationException;

    void remove(ProcureLine procureLine, ProcureLineRepository procureLineRepository) throws GloriaApplicationException;
    
    void validateReassign() throws GloriaApplicationException;
    
    void acceptRemoveMaterial(ProcureLine procureLine, ProcureLineRepository procureLineRepository, List<MaterialDTO> materialDTOs, Material removeMaterial,
            MaterialHeaderRepository requestHeaderRepo, ProcurementServices procurementServices, OrderRepository orderRepository, UserDTO userDto,
            TraceabilityRepository traceabilityRepository) throws GloriaApplicationException;

    void cancelModification(List<Material> materials, MaterialHeaderRepository requestHeaderRepository, ProcurementServices procurementServices,
            ProcureLineRepository procureLineRepository, ProcureLine procureLine, UserDTO userDTO) throws GloriaApplicationException;

    void updateProcureLine(ProcureLineDTO procureLineDTO, ProcureLine procureLine, String action, String teamId, UserDTO userDto,
            ProcureLineRepository procureLineRepository, CommonServices commonServices, ProcurementServices procurementServices,
            PurchaseOrganisationRepository purchaseOrganisationRepo, String finalWarehouseId) throws GloriaApplicationException;

    boolean isRevertProcurementAllowed(ProcureLine procureLine);

    String getUsageChangeAction(Material material);

    String getUsageReplacedChangeAction(Material material);

    void acceptRemoveUsageReplacedMaterial(ProcureLine procureLine, ProcureLineRepository procureLineRepository, List<MaterialDTO> materialDTOs,
            Material removeMaterial, MaterialHeaderRepository requestHeaderRepo, ProcurementServices procurementServices, OrderRepository orderRepository,
            UserServices userServices, ChangeId changeId, String userID, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException;

    boolean isOrderPlaced();

    boolean isProcurementStarted();
}
