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
import com.volvo.gloria.procurematerial.d.entities.status.orderLine.OrderLineStatusHelper;
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineStatus;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderSapRepository;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.repositories.b.PurchaseOrganisationRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.c.GloriaParams;
import com.volvo.gloria.warehouse.b.WarehouseServices;

/**
 * Operations for INTERNAL procure type.
 */
public class Internal extends ProcureTypeDefaultOperations {
    @Override
    public ProcureType suggestProcureTypeForStock(boolean existsInStock) {
        return ProcureType.INTERNAL;
    }

    @Override
    public ProcureType setProcureType(long usageQuantity, long additionalQuantity, long selectedFromStockQuantity) throws GloriaApplicationException {
        if (selectedFromStockQuantity >= usageQuantity + additionalQuantity) {
            return ProcureType.FROM_STOCK;
        } else if (selectedFromStockQuantity > 0) {
            return ProcureType.INTERNAL_FROM_STOCK;
        } else {
            return ProcureType.INTERNAL;
        }
    }

    @Override
    public boolean procure(MaterialHeaderRepository requestHeaderRepository, ProcurementServices procurementServices, ProcureLine procureLine,
            List<MaterialLine> fromStockMaterialLines, TraceabilityRepository traceabilityRepository, MaterialServices materialServices,
            WarehouseServices warehouseServices, UserDTO user, boolean multipleProcure, ProcureLineDTO procureLineDTO, List<Material> materials)
            throws GloriaApplicationException {
        boolean procured = true;
        procured = ProcureTypeHelper.validateMandatoryFieldsForInternal(procureLine, multipleProcure, procured);
        if (procured) {
            procurementServices.updateSupplierCounterPartInfo(procureLine);
            procurementServices.updateStatusAndProcureTeamInfo(procureLineDTO, user, procureLine, materials);
            procurementServices.createAdditionalMaterial(procureLine);
            ProcureTypeHelper.procureInternal(procurementServices, procureLine);
        }
        return procured;
    }
    
    @Override
    public void revert(ProcureLine procureLine, MaterialHeaderRepository requestHeaderRepository, ProcureLineRepository procureLineRepository,
            OrderRepository orderRepository, TraceabilityRepository traceabilityRepository, ProcurementServices procurementServices, UserDTO userDTO)
            throws GloriaApplicationException {
        OrderLineStatusHelper.cancel(procureLine.getMaterials().get(0).getOrderLine(), procurementServices, requestHeaderRepository, traceabilityRepository,
                                     userDTO);
    }
    
    @Override
    public boolean update(Material removeMaterial, String changeAction, MaterialHeaderRepository requestHeaderRepo, ProcureLine procureLine,
            ProcureLineRepository procureLineRepository, ProcurementServices procurementServices, OrderRepository orderRepository, boolean isPlaced,
            UserDTO userDto, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        boolean updateOrder = false;
        if (changeAction != null && (changeAction.equalsIgnoreCase(GloriaParams.CANCEL_IO) || changeAction.equalsIgnoreCase(GloriaParams.REMOVE))) {
            updateOrder = true;
        } 
        return ProcureTypeHelper.updateChangeToInternal(removeMaterial, updateOrder, requestHeaderRepo, procureLine, 
                                                        procureLineRepository, procurementServices, orderRepository, userDto, traceabilityRepository);
    }

    @Override
    public void forward(ProcureLine procureLine, String forwardedTeam, String userId, String userName, ProcureLineRepository procureLineRepository,
            ProcurementServices procurementServices, UserDTO userDTO, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        ProcureTypeHelper.forwardProcureLine(procureLine, forwardedTeam, userId, userName, procureLineRepository, procurementServices, userDTO,
                                             traceabilityRepository);
    }
    
    @Override
    public void returnProcureLine(ProcureLine procureLine, ProcureLineRepository procureLineRepository, ProcurementServices procurementServices) 
            throws GloriaApplicationException {
        ProcureTypeHelper.returnProcureLine(procureLine, procureLineRepository, procurementServices);
    }
    
    @Override
    public void assignInternal(ProcureLine procureLine, String userTeam, String userId, String userName, ProcureLineRepository procureLineRepository)
            throws GloriaApplicationException {
        ProcureTypeHelper.assign(procureLine, userTeam, userId, userName, procureLineRepository);
    }
    
    public MaterialLineStatus getOrderPlacedStatus() {
        return MaterialLineStatus.ORDER_PLACED_INTERNAL;
    }
    
    @Override
    public void updateProcureLine(ProcureLineDTO procureLineDTO, ProcureLine procureLine, 
            PurchaseOrganisationRepository purchaseOrganisationRepo, UserDTO userDto)
            throws GloriaApplicationException {
        ProcureTypeHelper.updateProcureLineForInternal(procureLineDTO, procureLine, purchaseOrganisationRepo, userDto);
    }

    @Override
    public void unAssignInternal(ProcureLine procureLine, ProcureLineRepository procureLineRepository) throws GloriaApplicationException {
        ProcureTypeHelper.unAssign(procureLine, procureLineRepository);
    }
    
    @Override
    public MaterialLineStatus getOrderProcuredStatus() throws GloriaApplicationException {
        return MaterialLineStatus.ORDER_PLACED_INTERNAL;
    }
    
    @Override
    public void validate(ProcureLineDTO procureLineDTO, ProcurementServices procurementServices) throws GloriaApplicationException {        
        procurementServices.validateOrderNoLength(procureLineDTO);
        procurementServices.validateOrderInfo(procureLineDTO);
    }
    
    @Override
    public void setProcureLineStatus(ProcureLine procureLine) {
        procureLine.setStatus(ProcureLineStatus.PLACED);
    }
    
    @Override
    public ProcureType getMaterialLineProcureType() throws GloriaApplicationException {
        return ProcureType.INTERNAL;
    }

    @Override
    public void createSendGoodsReceipt(String companyCode, DeliveryNoteLine deliveryNoteLine, Long approvedQuantity, GoodsReceiptSender goodsReceiptSender,
            DeliveryNoteRepository deliveryNoteRepository, CommonServices commonServices) throws GloriaApplicationException {
        ProcureTypeHelper.createSendGoodsReceiptInternal(deliveryNoteLine, approvedQuantity, deliveryNoteRepository, commonServices);
    }
    
    @Override
    public void cancelGoodsReceipt(long quantityCancelled, GoodsReceiptHeader goodsReceiptHeader, GoodsReceiptSender goodsReceiptSender,
            CommonServices commonServices) throws GloriaApplicationException {
        // do nothing
    }
    
    @Override
    public void createSendProcessPurchaseOrder(ProcureLine procureLine, Order order, String action, CommonServices commonServices,
            CompanyCodeRepository companyCodeRepository, OrderSapRepository orderSapRepository, ProcessPurchaseOrderSender processPurchaseOrderSender)
            throws GloriaApplicationException {
     // do nothing
    }
    
    @Override
    public String getUsageReplacedPlacedChangeAction() {
        return GloriaParams.CANCEL_MOD_IO;
    }
    
    @Override
    public void updateForUsageReplaced(ProcureLine procureLine, ProcureLineRepository procureLineRepository, Material removeMaterial,
            MaterialHeaderRepository requestHeaderRepo, ProcurementServices procurementServices, OrderRepository orderRepository, boolean isPlaced,
            UserDTO userDto, TraceabilityRepository traceabilityRepository)
            throws GloriaApplicationException {
        ProcureTypeHelper.revertOrder(procureLine, orderRepository);
        ProcureTypeHelper.revertRequisition(procureLine);
        ProcureTypeHelper.disconnectMaterialFromProcureline(removeMaterial);
    }
    
    @Override
    public String getProcuredChangeAction(Material material) {
        return GloriaParams.REMOVE;
    }
}
