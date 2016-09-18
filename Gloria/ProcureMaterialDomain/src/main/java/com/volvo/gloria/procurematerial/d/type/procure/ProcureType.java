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
 * ProcureType possible types, supporting operations in behaviour classes.
 */
public enum ProcureType implements ProcureTypeOperations {

    FROM_STOCK(new FromStock()), 
    INTERNAL(new Internal()), 
    EXTERNAL(new External()), 
    EXTERNAL_FROM_STOCK(new ExternalFromStock()), 
    INTERNAL_FROM_STOCK(new InternalFromStock());

    private final ProcureTypeOperations procureTypeOperations;

    ProcureType(ProcureTypeOperations materialTypeOperations) {
        this.procureTypeOperations = materialTypeOperations;
    }

    public ProcureType suggestProcureTypeForStock(boolean existsInStock) throws GloriaApplicationException {
        return procureTypeOperations.suggestProcureTypeForStock(existsInStock);
    }

    @Override
    public ProcureType setProcureType(long usageQuantity, long additionalQuantity, long selectedFromStockQuan) throws GloriaApplicationException {
        return procureTypeOperations.setProcureType(usageQuantity, additionalQuantity, selectedFromStockQuan);
    }

    @Override
    public boolean procure(MaterialHeaderRepository requestHeaderRepository, ProcurementServices procurementServices, ProcureLine procureLine,
             List<MaterialLine> fromStockMaterialLines, TraceabilityRepository traceabilityRepository,
            MaterialServices materialServices, WarehouseServices warehouseServices, UserDTO user, 
            boolean multipleProcure, ProcureLineDTO procureLineDTO, List<Material> materials) throws GloriaApplicationException {
       return procureTypeOperations.procure(requestHeaderRepository, procurementServices, procureLine, fromStockMaterialLines,
                                      traceabilityRepository, materialServices, warehouseServices, user, multipleProcure, procureLineDTO, materials);
    }

    @Override
    public void revert(ProcureLine procureLine, MaterialHeaderRepository requestHeaderRepository, ProcureLineRepository procureLineRepository,
            OrderRepository orderRepository, TraceabilityRepository traceabilityRepository, ProcurementServices procurementServices, UserDTO userDTO)
            throws GloriaApplicationException {
        procureTypeOperations.revert(procureLine, requestHeaderRepository, procureLineRepository, orderRepository, traceabilityRepository, procurementServices,
                                     userDTO);
    }

    @Override
    public boolean update(Material removeMaterial, String changeAction, MaterialHeaderRepository requestHeaderRepo, ProcureLine procureLine,
            ProcureLineRepository procureLineRepository, ProcurementServices procurementServices, OrderRepository orderRepository, boolean isPlaced, 
            UserDTO userDto, TraceabilityRepository traceabilityRepository)
            throws GloriaApplicationException {
        return procureTypeOperations.update(removeMaterial, changeAction, requestHeaderRepo, procureLine, procureLineRepository, procurementServices,
                                            orderRepository, isPlaced, userDto, traceabilityRepository);
    }
    
    @Override
    public void forward(ProcureLine procureLine, String forwardedTeam, String userId, String userName, ProcureLineRepository procureLineRepository,
            ProcurementServices procurementServices, UserDTO userDTO, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        procureTypeOperations.forward(procureLine, forwardedTeam, userId, userName, procureLineRepository, procurementServices, userDTO, traceabilityRepository);
    }

    @Override
    public void returnProcureLine(ProcureLine procureLine, ProcureLineRepository procureLineRepository, ProcurementServices procurementServices)
            throws GloriaApplicationException {
        procureTypeOperations.returnProcureLine(procureLine, procureLineRepository, procurementServices);
    }
    
    @Override
    public void assignInternal(ProcureLine procureLine, String userTeam, String userId, String userName, ProcureLineRepository procureLineRepository)
            throws GloriaApplicationException {
        procureTypeOperations.assignInternal(procureLine, userTeam, userId, userName, procureLineRepository);
    }

    @Override
    public MaterialLineStatus getOrderPlacedStatus() throws GloriaApplicationException {
        return procureTypeOperations.getOrderPlacedStatus();
    }

    @Override
    public void updateProcureLine(ProcureLineDTO procureLineDTO, ProcureLine procureLine, 
            PurchaseOrganisationRepository purchaseOrganisationRepo, UserDTO userDto) throws GloriaApplicationException {
        procureTypeOperations.updateProcureLine(procureLineDTO, procureLine, purchaseOrganisationRepo, userDto);
    }

    @Override
    public void unAssignInternal(ProcureLine procureLine, ProcureLineRepository procureLineRepository) throws GloriaApplicationException {
        procureTypeOperations.unAssignInternal(procureLine, procureLineRepository);
    }

    @Override
    public MaterialLineStatus getOrderProcuredStatus() throws GloriaApplicationException {
        return procureTypeOperations.getOrderProcuredStatus();
    }

    @Override
    public void validate(ProcureLineDTO procureLineDTO, ProcurementServices procurementServices) throws GloriaApplicationException {
        procureTypeOperations.validate(procureLineDTO, procurementServices);
    }

    @Override
    public boolean isPartiallyProcuredFromStock() {
        return procureTypeOperations.isPartiallyProcuredFromStock();
    }

    @Override
    public void setProcureLineStatus(ProcureLine procureLine) throws GloriaApplicationException {
        procureTypeOperations.setProcureLineStatus(procureLine);
    }

    @Override
    public ProcureType getMaterialLineProcureType() throws GloriaApplicationException {
        return procureTypeOperations.getMaterialLineProcureType();
    }

    @Override
    public void acceptChangeForMaterialLines(Material removeMaterial, MaterialHeaderRepository requestHeaderRepo, ProcureLine procureLine,
            ProcureLineRepository procureLineRepository, ProcurementServices procurementServices, OrderRepository orderRepository, Long quantity,
            MaterialLine fromStockMaterialLine, UserDTO userDto, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        procureTypeOperations.acceptChangeForMaterialLines(removeMaterial, requestHeaderRepo, procureLine, procureLineRepository, procurementServices,
                                                           orderRepository, quantity, fromStockMaterialLine, userDto, traceabilityRepository);
    }

    @Override
    public boolean isFromStock() {
        return procureTypeOperations.isFromStock();
    }

    @Override
    public void revertMaterialLine(MaterialLine materialLine, MaterialHeaderRepository requestHeaderRepository, UserDTO userDto,
            TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        procureTypeOperations.revertMaterialLine(materialLine, requestHeaderRepository, userDto, traceabilityRepository);
    }

    @Override
    public String translateUOMToGPSUnits(String unitOfMeasure, CommonServices commonServices) {       
        return procureTypeOperations.translateUOMToGPSUnits(unitOfMeasure, commonServices);
    }

    @Override
    public long convertQuantityToGPSUnits(long quantity, String gloriaUnitOfMeasure, CommonServices commonServices) {
        return procureTypeOperations.convertQuantityToGPSUnits(quantity, gloriaUnitOfMeasure, commonServices);
    }

    @Override
    public void createSendGoodsReceipt(String companyCode, DeliveryNoteLine deliveryNoteLine, Long approvedQuantity, GoodsReceiptSender goodsReceiptSender,
            DeliveryNoteRepository deliveryNoteRepository, CommonServices commonServices) throws GloriaApplicationException {
        procureTypeOperations.createSendGoodsReceipt(companyCode, deliveryNoteLine, approvedQuantity, goodsReceiptSender, deliveryNoteRepository,
                                                     commonServices);
    }
    
    @Override
    public void cancelGoodsReceipt(long quantityCancelled, GoodsReceiptHeader goodsReceiptHeader, GoodsReceiptSender goodsReceiptSender,
            CommonServices commonServices) throws GloriaApplicationException {
        procureTypeOperations.cancelGoodsReceipt(quantityCancelled, goodsReceiptHeader, goodsReceiptSender, commonServices);
    }

    @Override
    public void createSendProcessPurchaseOrder(ProcureLine procureLine, Order order, String action, CommonServices commonServices,
            CompanyCodeRepository companyCodeRepository, OrderSapRepository orderSapRepository, ProcessPurchaseOrderSender processPurchaseOrderSender)
            throws GloriaApplicationException {
        procureTypeOperations.createSendProcessPurchaseOrder(procureLine, order, action, commonServices, companyCodeRepository, orderSapRepository,
                                                             processPurchaseOrderSender);
    }

    @Override
    public String getPlacedChangeAction(Material material) {
        return procureTypeOperations.getPlacedChangeAction(material);
    }

    @Override
    public String getProcuredChangeAction(Material material) {
        return procureTypeOperations.getProcuredChangeAction(material);
    }

    @Override
    public String getUsageReplacedProcuredChangeAction() {
        return procureTypeOperations.getUsageReplacedProcuredChangeAction();
    }

    @Override
    public String getUsageReplacedPlacedChangeAction() {
        return procureTypeOperations.getUsageReplacedPlacedChangeAction();
    }

    @Override
    public void updateForUsageReplaced(ProcureLine procureLine, ProcureLineRepository procureLineRepository, Material removeMaterial,
            MaterialHeaderRepository requestHeaderRepo, ProcurementServices procurementServices, OrderRepository orderRepository, boolean isPlaced,
            UserDTO userDto, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        procureTypeOperations.updateForUsageReplaced(procureLine, procureLineRepository, removeMaterial, requestHeaderRepo, procurementServices,
                                                     orderRepository, isPlaced, userDto, traceabilityRepository);
    }
}
