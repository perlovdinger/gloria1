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
 * Default operations.
 */
public class MaterialTypeDefaultOperations implements MaterialTypeOperations {

    @Override
    public ProcureType toRemove(boolean existsInStock) {
        return ProcureType.INTERNAL;
    }

    @Override
    public void revert(Material material, ProcureLine procureLine, MaterialHeaderRepository requestHeaderRepository,
            ProcureLineRepository procureLineRepository, List<Material> materialsToBeRemoved, UserDTO userDto, TraceabilityRepository traceabilityRepository)
            throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }

    @Override
    public boolean isBorrowable() throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }

    @Override
    public void release(Material material, ProcureLine procureLine) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }

    @Override
    public boolean isModifiable() {
        return false;
    }

    @Override
    public void cancelModification(Material material, MaterialHeaderRepository requestHeaderRepository,
            ProcureLine procureLine) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }

    @Override
    public boolean isAdditional() {
        return false;
    }
    
    @Override
    public boolean isReceiveble() {
        return false;
    }

    @Override
    public boolean isAllowedForRequisitionQty(ProcureLine procureLine) {
        return true;
    }

    @Override
    public boolean isScrappable() {
        return true;
    }
    
    @Override
    public void setSupplierCounterPart(List<MaterialLine> materialLines, SupplierCounterPart supplierCounterPart) {
        MaterialTypeHelper.setSupplierCounterPart(materialLines, supplierCounterPart);
    }
    
    @Override
    public List<Material> cancelMaterial(Material material, MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository,
            OrderLine orderLine, UserDTO userDTO) throws GloriaApplicationException {
        return null;
    }

    @Override
    public String getChangeAction(Material material) {
        return null;
    }

    @Override
    public void acceptRemove(ProcureLine procureLine, ProcureLineRepository procureLineRepository, List<MaterialDTO> materialDTOs, Material removeMaterial,
            MaterialHeaderRepository requestHeaderRepo, ProcurementServices procurementServices, OrderRepository orderRepository, UserServices userServices,
            ChangeId changeId, String userId, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }

    @Override
    public boolean isReleased() {
        return false;
    }
}
