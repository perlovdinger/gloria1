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
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineStatus;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderSapRepository;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.repositories.b.PurchaseOrganisationRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.c.GloriaParams;
import com.volvo.gloria.util.tracing.c.GloriaTraceabilityConstants;
import com.volvo.gloria.warehouse.b.WarehouseServices;

/**
 * Operations for EXTERNAL procure type.
 */
public class External extends ProcureTypeDefaultOperations {
    @Override
    public ProcureType suggestProcureTypeForStock(boolean existsInStock) {
        return ProcureType.EXTERNAL;
    }

    @Override
    public ProcureType setProcureType(long usageQuantity, long additionalQuantity, long selectedFromStockQuantity) throws GloriaApplicationException {
        if (selectedFromStockQuantity >= usageQuantity + additionalQuantity) {
            return ProcureType.FROM_STOCK;
        } else if (selectedFromStockQuantity > 0) {
            return ProcureType.EXTERNAL_FROM_STOCK;
        } else {
            return ProcureType.EXTERNAL;
        }
    }
    
    @Override
    public void revert(ProcureLine procureLine, MaterialHeaderRepository requestHeaderRepository, ProcureLineRepository procureLineRepository,
            OrderRepository orderRepository, TraceabilityRepository traceabilityRepository, ProcurementServices procurementServices, UserDTO userDTO)
            throws GloriaApplicationException {
        String requistionId = procureLine.getRequisitionId();
        String actionDetail = "";
        String action = "Revert Procurement";
        if(requistionId != null){
            actionDetail = "Requisition cancelled : "+ requistionId;
        }
        ProcureTypeHelper.revertRequisition(procureLine);
        procureLine.getStatus().revertToWait(procureLine, procureLineRepository);
        ProcureTypeHelper.revertMaterial(procureLine, requestHeaderRepository, procureLineRepository, procurementServices, userDTO, traceabilityRepository);
        ProcureTypeHelper.trace(action, actionDetail, procureLine, userDTO, traceabilityRepository, GloriaTraceabilityConstants.ORDER_STAACCEPTED_AGREEDSTA);        
    }
    
    @Override
    public boolean procure(MaterialHeaderRepository requestHeaderRepository, ProcurementServices procurementServices, ProcureLine procureLine,
            List<MaterialLine> fromStockMaterialLines, TraceabilityRepository traceabilityRepository,
            MaterialServices materialServices, WarehouseServices warehouseServices, UserDTO user, 
            boolean multipleProcure, ProcureLineDTO procureLineDTO, List<Material> materials) throws GloriaApplicationException {
        boolean procured = true;
        procured = ProcureTypeHelper.validateMandatoryFieldsForExternal(procureLine, multipleProcure, procured);
        if (procured) {
            procurementServices.updateSupplierCounterPartInfo(procureLine);
            procurementServices.updateStatusAndProcureTeamInfo(procureLineDTO, user, procureLine, materials);
            procurementServices.createAdditionalMaterial(procureLine);
            ProcureTypeHelper.procureExternal(procurementServices, procureLine);
        }
        return procured;
    }
    
    @Override
    public boolean update(Material removeMaterial, String changeAction, MaterialHeaderRepository requestHeaderRepo, ProcureLine procureLine,
            ProcureLineRepository procureLineRepository, ProcurementServices procurementServices, OrderRepository orderRepository, boolean isPlaced,
            UserDTO userDto, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        boolean updateOrder = false;
        if (changeAction != null && changeAction.equalsIgnoreCase(GloriaParams.CANCEL_PP_REQ)) {
            updateOrder = true;
        }
        return ProcureTypeHelper.doPlacedOrProcuredExternal(removeMaterial, updateOrder, requestHeaderRepo, procureLine, procureLineRepository,
                                                            procurementServices, orderRepository, isPlaced, 0, userDto, traceabilityRepository);

    }

    public MaterialLineStatus getOrderPlacedStatus() {
        return MaterialLineStatus.ORDER_PLACED_EXTERNAL;
    }

    @Override
    public void updateProcureLine(ProcureLineDTO procureLineDTO, ProcureLine procureLine, 
            PurchaseOrganisationRepository purchaseOrganisationRepo, UserDTO userDto)
            throws GloriaApplicationException {
        ProcureTypeHelper.updateProcureLineForExternal(procureLineDTO, procureLine, purchaseOrganisationRepo);
    }
    
    @Override
    public MaterialLineStatus getOrderProcuredStatus() throws GloriaApplicationException {
        return MaterialLineStatus.REQUISITION_SENT;
    }
    
    @Override
    public void validate(ProcureLineDTO procureLineDTO, ProcurementServices procurementServices) throws GloriaApplicationException {
       
    }
    
    @Override
    public void setProcureLineStatus(ProcureLine procureLine) {
        procureLine.setStatus(ProcureLineStatus.PROCURED);
    }
    
    @Override
    public ProcureType getMaterialLineProcureType() throws GloriaApplicationException {
        return ProcureType.EXTERNAL;
    }

    @Override
    public String translateUOMToGPSUnits(String unitOfMeasure, CommonServices commonServices) {
        return ProcureTypeHelper.translateUOMToGPSUnits(unitOfMeasure, commonServices);
    }
    
    @Override
    public long convertQuantityToGPSUnits(long quantity, String gloriaUnitOfMeasure, CommonServices commonServices) {
        return ProcureTypeHelper.convertQuantityToGPSUnits(quantity, gloriaUnitOfMeasure, commonServices);
    }
    
    @Override
    public void createSendGoodsReceipt(String companyCode, DeliveryNoteLine deliveryNoteLine, Long approvedQuantity, GoodsReceiptSender goodsReceiptSender,
            DeliveryNoteRepository deliveryNoteRepository, CommonServices commonServices) throws GloriaApplicationException {
        ProcureTypeHelper.createSendGoodsReceiptExternal(companyCode, deliveryNoteLine, approvedQuantity, goodsReceiptSender, deliveryNoteRepository,
                                                         commonServices);
    }
    
    @Override
    public void cancelGoodsReceipt(long quantityCancelled, GoodsReceiptHeader goodsReceiptHeader, GoodsReceiptSender goodsReceiptSender,
            CommonServices commonServices) throws GloriaApplicationException {
        ProcureTypeHelper.sendCancelGoodsReceipt(quantityCancelled, goodsReceiptHeader, goodsReceiptSender, commonServices);
    }
    
    @Override
    public void createSendProcessPurchaseOrder(ProcureLine procureLine, Order order, String action, CommonServices commonServices,
            CompanyCodeRepository companyCodeRepository, OrderSapRepository orderSapRepository, ProcessPurchaseOrderSender processPurchaseOrderSender)
            throws GloriaApplicationException {
        ProcureTypeHelper.createSendProcessPurchaseOrder(procureLine, order, action, commonServices, orderSapRepository, companyCodeRepository,
                                                         processPurchaseOrderSender);
    }
    
    @Override
    public String getPlacedChangeAction(Material material) {
        return GloriaParams.ADDITIONAL;
    }
    
    @Override
    public String getUsageReplacedProcuredChangeAction() {
        return GloriaParams.CANCEL_MOD_PP_REQ;
    }
    
    @Override
    public String getUsageReplacedPlacedChangeAction() {
        return GloriaParams.CANCEL_MOD_WAITING;
    }
    
    @Override
    public void updateForUsageReplaced(ProcureLine procureLine, ProcureLineRepository procureLineRepository, Material removeMaterial,
            MaterialHeaderRepository requestHeaderRepo, ProcurementServices procurementServices, OrderRepository orderRepository, boolean isPlaced,
            UserDTO userDto, TraceabilityRepository traceabilityRepository) {
        if (!isPlaced) {
            ProcureTypeHelper.revertRequisition(procureLine);
            ProcureTypeHelper.disconnectMaterialFromProcureline(removeMaterial);
        }
    }
}
