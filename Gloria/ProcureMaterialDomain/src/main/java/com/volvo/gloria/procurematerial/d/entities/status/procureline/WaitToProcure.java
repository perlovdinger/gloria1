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
import com.volvo.gloria.procurematerial.d.entities.status.requestline.MaterialStatusHelper;
import com.volvo.gloria.procurematerial.d.type.material.MaterialTypeHelper;
import com.volvo.gloria.procurematerial.d.type.procure.ProcureTypeHelper;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.repositories.b.PurchaseOrganisationRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.util.ProcureGroupHelper;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.c.GloriaParams;


public class WaitToProcure extends ProcureLineDefaultOperations {

    @Override
    public void procured(ProcureLine procureLine) throws GloriaApplicationException {
        procureLine.getProcureType().setProcureLineStatus(procureLine);
    }
    
    @Override
    public void remove(ProcureLine procureLine, ProcureLineRepository procureLineRepository) throws GloriaApplicationException {
        procureLineRepository.delete(procureLine);
    }
    
    @Override
    public void validateReassign() throws GloriaApplicationException {
    }
    
    @Override
    public void acceptRemoveMaterial(ProcureLine procureLine, ProcureLineRepository procureLineRepository, List<MaterialDTO> materialDTOs,
            Material removeMaterial, MaterialHeaderRepository requestHeaderRepo, ProcurementServices procurementServices, OrderRepository orderRepository,
            UserDTO userDto, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        ProcureTypeHelper.disconnectMaterialFromProcureline(removeMaterial);
        if (procureLine.getMaterials() == null || procureLine.getMaterials().isEmpty()) {
            procureLineRepository.delete(procureLine);
        } else {
            procureLine.setNeedIsChanged(false);
            ProcureGroupHelper.updateIdSetsOnProcureLine(procureLine, procureLine.getMaterials());
        }
    }
    
    @Override
    public void cancelModification(List<Material> materials, MaterialHeaderRepository requestHeaderRepository, ProcurementServices procurementServices,
            ProcureLineRepository procureLineRepository, ProcureLine procureLine, UserDTO userDTO) throws GloriaApplicationException {
        ProcureLineHelper.cancelModification(materials, requestHeaderRepository, procurementServices, procureLineRepository, procureLine, userDTO);
    }
    
    @Override
    public void updateProcureLine(ProcureLineDTO procureLineDTO, ProcureLine procureLine, String action, String teamId, UserDTO userDto,
            ProcureLineRepository procureLineRepository, CommonServices commonServices, ProcurementServices procurementServices,
            PurchaseOrganisationRepository purchaseOrganisationRepo, 
            String finalWarehouseId) throws GloriaApplicationException {
        ProcureLineHelper.updateProcureLine(procureLineDTO, procureLine, action, procureLineRepository, 
                                            commonServices, procurementServices, purchaseOrganisationRepo, userDto, finalWarehouseId);
        procureLineRepository.save(procureLine);
    }
    
    @Override
    public String getUsageChangeAction(Material material) {
        return GloriaParams.REMOVE;
    }
    
    @Override
    public String getUsageReplacedChangeAction(Material material) {
        return GloriaParams.CANCEL_MOD_REMOVE;
    }
    
    @Override
    public void acceptRemoveUsageReplacedMaterial(ProcureLine procureLine, ProcureLineRepository procureLineRepository, List<MaterialDTO> materialDTOs,
            Material removeMaterial, MaterialHeaderRepository requestHeaderRepo, ProcurementServices procurementServices, 
            OrderRepository orderRepository, UserServices userServices, ChangeId changeId, String userID, TraceabilityRepository traceabilityRepository)
            throws GloriaApplicationException {
        UserDTO userDTO = userServices.getUser(userID);
        MaterialStatusHelper.removeMaterial(changeId.getMtrlRequestVersion(), removeMaterial, userDTO, traceabilityRepository);
        UserDTO userDTOMC = userServices.getUser(procureLine.getMaterialControllerId());
        MaterialTypeHelper.resetModifiedAttributes(removeMaterial);
        ProcureTypeHelper.disconnectMaterialFromProcureline(removeMaterial);
       
        ProcureLineHelper.performRemoveOnAllRemoveMarked(procureLine, changeId, requestHeaderRepo, userDTO, traceabilityRepository);
        
        ProcureLineHelper.cancelModification(procureLine.getMaterials(), requestHeaderRepo, procurementServices, procureLineRepository, procureLine, userDTOMC);
    }

}
