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
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.repositories.b.PurchaseOrganisationRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;

public class Procured extends ProcureLineDefaultOperations {

    @Override
    public void revertToWait(ProcureLine procureLine, ProcureLineRepository procureLineRepository) throws GloriaApplicationException {
        procureLine.setStatus(ProcureLineStatus.WAIT_TO_PROCURE);
        procureLineRepository.save(procureLine);
    }

    
     @Override
     public void acceptRemoveMaterial(ProcureLine procureLine, ProcureLineRepository procureLineRepository, List<MaterialDTO> materialDTOs,
     Material removeMaterial, MaterialHeaderRepository requestHeaderRepo, ProcurementServices procurementServices, OrderRepository orderRepository,
     UserDTO userDto, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
     boolean isPlaced = false;
     ProcureLineHelper.updateChangeToProcureLine(procureLine, procureLineRepository, materialDTOs, removeMaterial, requestHeaderRepo, procurementServices,
     orderRepository, isPlaced, userDto, traceabilityRepository);
     }

    @Override
    public void updateProcureLine(ProcureLineDTO procureLineDTO, ProcureLine procureLine, String action, String teamId, UserDTO userDto,
            ProcureLineRepository procureLineRepository, CommonServices commonServices, ProcurementServices procurementServices,
            PurchaseOrganisationRepository purchaseOrganisationRepo, String finalWarehouseId) throws GloriaApplicationException {
        ProcureLineHelper.updateProcureLine(procureLineDTO, procureLine, action, procureLineRepository, commonServices, procurementServices,
                                            purchaseOrganisationRepo, userDto, finalWarehouseId);
        ProcureLineHelper.updateFinancialInfo(procureLine.getMaterials(), procureLineDTO);
        procureLineRepository.save(procureLine);
    }
    
    @Override
    public String getUsageChangeAction(Material material) {
        return material.getProcureLine().getProcureType().getProcuredChangeAction(material);
    }
    
    @Override
    public String getUsageReplacedChangeAction(Material material) {
        return material.getProcureLine().getProcureType().getUsageReplacedProcuredChangeAction();
    }
    
    @Override
    public void acceptRemoveUsageReplacedMaterial(ProcureLine procureLine, ProcureLineRepository procureLineRepository, List<MaterialDTO> materialDTOs,
            Material removeMaterial, MaterialHeaderRepository requestHeaderRepo, ProcurementServices procurementServices, OrderRepository orderRepository,
            UserServices userServices, ChangeId changeId, String userID, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        ProcureLineHelper.acceptUsageReplaced(procureLine, procureLineRepository, removeMaterial, requestHeaderRepo, procurementServices, orderRepository,
                                              userServices, changeId, userID, traceabilityRepository);
    }
    
    @Override
    public boolean isProcurementStarted() {
        return true;
    }
}
