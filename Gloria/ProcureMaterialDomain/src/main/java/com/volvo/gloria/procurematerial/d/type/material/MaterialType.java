package com.volvo.gloria.procurematerial.d.type.material;

import java.util.List;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.d.entities.SupplierCounterPart;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.c.dto.MaterialDTO;
import com.volvo.gloria.procurematerial.d.entities.ChangeId;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.type.procure.ProcureType;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;

/**
 * MaterialType possible types, supporting operations in behaviour classes.
 */
public enum MaterialType implements MaterialTypeOperations {

    USAGE(new Usage()), 
    MODIFIED(new Modified()), 
    USAGE_REPLACED(new UsageReplaced()), 
    ADDITIONAL(new Additional()), 
    RELEASED(new Released()),
    ADDITIONAL_USAGE(new AdditionalUsage());

    private final MaterialTypeOperations materialTypeOperations;

    MaterialType(MaterialTypeOperations materialTypeOperations) {
        this.materialTypeOperations = materialTypeOperations;
    }

    @Override
    public ProcureType toRemove(boolean existsInStock) {
        return materialTypeOperations.toRemove(existsInStock);
    }

    @Override
    public void revert(Material material, ProcureLine procureLine, MaterialHeaderRepository requestHeaderRepository,
            ProcureLineRepository procureLineRepository, List<Material> materialsToBeRemoved, UserDTO userDto, TraceabilityRepository traceabilityRepository)
            throws GloriaApplicationException {
        materialTypeOperations.revert(material, procureLine, requestHeaderRepository, procureLineRepository, materialsToBeRemoved, userDto,
                                      traceabilityRepository);
    }
    
    @Override
    public boolean isBorrowable() throws GloriaApplicationException {
        return materialTypeOperations.isBorrowable();
    }

    @Override
    public void release(Material material, ProcureLine procureLine) throws GloriaApplicationException {
        materialTypeOperations.release(material, procureLine);
    }

    @Override
    public boolean isModifiable() {
        return materialTypeOperations.isModifiable();
    }

    @Override
    public void cancelModification(Material material, MaterialHeaderRepository requestHeaderRepository, 
            ProcureLine procureLine) throws GloriaApplicationException {
        materialTypeOperations.cancelModification(material, requestHeaderRepository, procureLine);
    }

    public boolean isAdditional() {
        return materialTypeOperations.isAdditional();
    }
    
    @Override
    public boolean isReceiveble() {
        return materialTypeOperations.isReceiveble();
    }

    public boolean isAllowedForRequisitionQty(ProcureLine procureLine) {
        return materialTypeOperations.isAllowedForRequisitionQty(procureLine);
    }

    @Override
    public boolean isScrappable() {
        return materialTypeOperations.isScrappable();
    }
    
    @Override
    public void setSupplierCounterPart(List<MaterialLine> materialLines, SupplierCounterPart supplierCounterPart) {
        materialTypeOperations.setSupplierCounterPart(materialLines, supplierCounterPart);        
    }

    @Override
    public List<Material> cancelMaterial(Material material, MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository,
            OrderLine orderLine, UserDTO userDTO) throws GloriaApplicationException {
        return materialTypeOperations.cancelMaterial(material, requestHeaderRepository, traceabilityRepository, orderLine, userDTO);
    }

    @Override
    public String getChangeAction(Material material) {
        return materialTypeOperations.getChangeAction(material);
    }

    @Override
    public void acceptRemove(ProcureLine procureLine, ProcureLineRepository procureLineRepository, List<MaterialDTO> materialDTOs, Material removeMaterial,
            MaterialHeaderRepository requestHeaderRepo, ProcurementServices procurementServices, 
            OrderRepository orderRepository, UserServices userServices, ChangeId changeId, String userId, TraceabilityRepository traceabilityRepository)
            throws GloriaApplicationException {
        materialTypeOperations.acceptRemove(procureLine, procureLineRepository, materialDTOs, removeMaterial, requestHeaderRepo, procurementServices,
                                            orderRepository, userServices, changeId, userId, traceabilityRepository);
    }

    public boolean isReleased() {
        return materialTypeOperations.isReleased();
    }
}
