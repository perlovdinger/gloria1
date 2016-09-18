package com.volvo.gloria.procurematerial.d.type.procure;

import java.util.List;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.CompanyCodeRepository;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.financeProxy.b.GoodsReceiptSender;
import com.volvo.gloria.financeProxy.b.ProcessPurchaseOrderSender;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.c.dto.ProcureLineDTO;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.GoodsReceiptHeader;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.Order;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderSapRepository;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.repositories.b.PurchaseOrganisationRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.warehouse.b.WarehouseServices;

/**
 * Default operations.
 */
public class ProcureTypeDefaultOperations implements ProcureTypeOperations {
    @Override
    public ProcureType setProcureType(long usageQuantity, long additionalQuantity, long selectedFromStockQuan) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }
    
    @Override
    public boolean procure(MaterialHeaderRepository requestHeaderRepository, ProcurementServices procurementServices, ProcureLine procureLine,
            List<MaterialLine> fromStockMaterialLines, TraceabilityRepository traceabilityRepository, MaterialServices materialServices,
            WarehouseServices warehouseServices, UserDTO user, boolean multipleProcure, ProcureLineDTO procureLineDTO, List<Material> materials)
            throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }

    @Override
    public ProcureType suggestProcureTypeForStock(boolean existsInStock) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }
    
    @Override
    public void revert(ProcureLine procureLine, MaterialHeaderRepository requestHeaderRepository, ProcureLineRepository procureLineRepository,
            OrderRepository orderRepository, TraceabilityRepository traceabilityRepository, ProcurementServices procurementServices, UserDTO userDTO) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }
    
    @Override
    public boolean update(Material removeMaterial, String changeAction, MaterialHeaderRepository requestHeaderRepo, ProcureLine procureLine,
            ProcureLineRepository procureLineRepository, ProcurementServices procurementServices, OrderRepository orderRepository, boolean isPlaced,
            UserDTO userDto, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }
    
    @Override
    public void forward(ProcureLine procureLine, String forwardedTeam, String userId, String userName, ProcureLineRepository procureLineRepository,
            ProcurementServices procurementServices, UserDTO userDTO, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }

    @Override
    public void returnProcureLine(ProcureLine procureLine, ProcureLineRepository procureLineRepository, ProcurementServices procurementServices)
            throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }
    
    @Override
    public void assignInternal(ProcureLine procureLine, String userTeam, String userId, String userName, ProcureLineRepository procureLineRepository)
            throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());        
    }
    
    public MaterialLineStatus getOrderPlacedStatus() throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());                
    }

    @Override
    public void updateProcureLine(ProcureLineDTO procureLineDTO, ProcureLine procureLine, 
            PurchaseOrganisationRepository purchaseOrganisationRepo, UserDTO userDto) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }
    
    @Override
    public void unAssignInternal(ProcureLine procureLine, ProcureLineRepository procureLineRepository) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }
    
    @Override
    public MaterialLineStatus getOrderProcuredStatus() throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());                
    }
    
    @Override
    public void validate(ProcureLineDTO procureLineDTO, ProcurementServices procurementServices) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }
    
    @Override
    public boolean isPartiallyProcuredFromStock() {
        return false;
    }
    
    @Override
    public void setProcureLineStatus(ProcureLine procureLine) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());    
    }

    @Override
    public ProcureType getMaterialLineProcureType() throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName()); 
    }

    @Override
    public void acceptChangeForMaterialLines(Material removeMaterial, MaterialHeaderRepository requestHeaderRepo, ProcureLine procureLine,
            ProcureLineRepository procureLineRepository, ProcurementServices procurementServices, OrderRepository orderRepository, Long quantity,
            MaterialLine fromStockMaterialLine, UserDTO userDto, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }

    @Override
    public boolean isFromStock() {
        return false;
    }

    @Override
    public void revertMaterialLine(MaterialLine materialLine, MaterialHeaderRepository requestHeaderRepository, UserDTO userDto,
            TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }

    @Override
    public String translateUOMToGPSUnits(String unitOfMeasure, CommonServices commonServices) {
        return unitOfMeasure;
    }
    
    @Override
    public long convertQuantityToGPSUnits(long quantity, String gloriaUnitOfMeasure, CommonServices commonServices) {
        return quantity;
    }
    
    @Override
    public void createSendGoodsReceipt(String companyCode, DeliveryNoteLine deliveryNoteLine, Long approvedQuantity, GoodsReceiptSender goodsReceiptSender,
            DeliveryNoteRepository deliveryNoteRepository, CommonServices commonServices) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }

    @Override
    public void cancelGoodsReceipt(long quantityCancelled, GoodsReceiptHeader goodsReceiptHeader, GoodsReceiptSender goodsReceiptSender,
            CommonServices commonServices) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }

    @Override
    public void createSendProcessPurchaseOrder(ProcureLine procureLine, Order order, String action, CommonServices commonServices,
            CompanyCodeRepository companyCodeRepository, OrderSapRepository orderSapRepository, ProcessPurchaseOrderSender processPurchaseOrderSender)
            throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }
    
    @Override
    public String getPlacedChangeAction(Material material) {
        return null;
    }

    @Override
    public String getProcuredChangeAction(Material material) {
        return null;
    }
    
    @Override
    public String getUsageReplacedProcuredChangeAction() {
        return null;
    }
    
    @Override
    public String getUsageReplacedPlacedChangeAction() {
        return null;
    }

    @Override
    public void updateForUsageReplaced(ProcureLine procureLine, ProcureLineRepository procureLineRepository, Material removeMaterial,
            MaterialHeaderRepository requestHeaderRepo, ProcurementServices procurementServices, OrderRepository orderRepository, boolean isPlaced,
            UserDTO userDto, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        throw new GloriaApplicationException(null, "This operation is not supported in current state." + this.getClass().getName());
    }
}