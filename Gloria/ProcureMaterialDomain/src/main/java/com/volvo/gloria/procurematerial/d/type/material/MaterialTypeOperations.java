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
 * Possible operations for material types.
 */
public interface MaterialTypeOperations {
   
    ProcureType toRemove(boolean existsInStock);

    void revert(Material material, ProcureLine procureLine, MaterialHeaderRepository requestHeaderRepository, ProcureLineRepository procureLineRepository,
            List<Material> materialsToBeRemoved, UserDTO userDto, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException;

    boolean isBorrowable() throws GloriaApplicationException;

    void release(Material material, ProcureLine procureLine) throws GloriaApplicationException;
    
    boolean isModifiable(); 
    
    void cancelModification(Material material, MaterialHeaderRepository requestHeaderRepository,
            ProcureLine procureLine) throws GloriaApplicationException;

    boolean isAdditional();

    boolean isReceiveble();

    boolean isAllowedForRequisitionQty(ProcureLine procureLine);
    
    boolean isScrappable();

    void setSupplierCounterPart(List<MaterialLine> materialLine, SupplierCounterPart supplierCounterPart);

    List<Material> cancelMaterial(Material material, MaterialHeaderRepository requestHeaderRepository
                                  , TraceabilityRepository traceabilityRepository, OrderLine orderLine, UserDTO userDTO) throws GloriaApplicationException;

    String getChangeAction(Material material);

    void acceptRemove(ProcureLine procureLine, ProcureLineRepository procureLineRepository, List<MaterialDTO> materialDTOs, Material removeMaterial,
            MaterialHeaderRepository requestHeaderRepo, ProcurementServices procurementServices, 
            OrderRepository orderRepository, UserServices userServices, ChangeId changeId, String userId, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException;

    boolean isReleased();
}
