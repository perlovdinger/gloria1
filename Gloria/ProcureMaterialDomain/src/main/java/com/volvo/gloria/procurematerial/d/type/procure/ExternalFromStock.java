package com.volvo.gloria.procurematerial.d.type.procure;

import java.util.ArrayList;
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
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatusHelper;
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineHelper;
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineStatus;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
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
 * Operations for EXTERNAL_FROM_STOCK procure type.
 */
public class ExternalFromStock extends ProcureTypeDefaultOperations {

    @Override
    public boolean procure(MaterialHeaderRepository requestHeaderRepository, ProcurementServices procurementServices, ProcureLine procureLine,
            List<MaterialLine> fromStockMaterialLines, TraceabilityRepository traceabilityRepository, MaterialServices materialServices,
            WarehouseServices warehouseServices, UserDTO user, boolean multipleProcure, ProcureLineDTO procureLineDTO, List<Material> materials)
            throws GloriaApplicationException {
        boolean procured = true;
        procured = ProcureTypeHelper.validateMandatoryFieldsForExternal(procureLine, multipleProcure, procured);
        if (procured) {
            procurementServices.updateSupplierCounterPartInfo(procureLine);
            procurementServices.updateStatusAndProcureTeamInfo(procureLineDTO, user, procureLine, materials);
            procurementServices.createAdditionalMaterial(procureLine);
            ProcureTypeHelper.procureFromStock(requestHeaderRepository, procureLine, fromStockMaterialLines, traceabilityRepository, materialServices,
                                               warehouseServices, user);
            ProcureTypeHelper.procureExternal(procurementServices, procureLine);
        }
        return procured;
    }
    
    @Override
    public boolean update(Material removeMaterial, String changeAction, MaterialHeaderRepository requestHeaderRepo, ProcureLine procureLine,
            ProcureLineRepository procureLineRepository, ProcurementServices procurementServices, OrderRepository orderRepository, boolean isPlaced,
            UserDTO userDto, TraceabilityRepository traceabilityRepository)
            throws GloriaApplicationException {
        List<MaterialLine> fromStockLines = new ArrayList<MaterialLine>();
        long fromStockQty = 0;
        boolean updateOrder = false;
        if (changeAction != null && changeAction.equalsIgnoreCase(GloriaParams.CANCEL_PP_REQ)) {
            updateOrder = true;
        }
        if (removeMaterial != null) {
            List<MaterialLine> removeMaterialLines = removeMaterial.getMaterialLine();
            for (int idx = removeMaterialLines.size() - 1; idx >= 0; idx--) {
                MaterialLine materialLine = removeMaterialLines.get(idx);
                if (materialLine.getProcureType().isFromStock() && !materialLine.getStatus().isRemovedDb()) {
                    fromStockLines.add(materialLine);
                    fromStockQty += materialLine.getQuantity();
                    materialLine.getProcureType().acceptChangeForMaterialLines(removeMaterial, requestHeaderRepo, procureLine, procureLineRepository,
                                                                               procurementServices, orderRepository, materialLine.getQuantity(), materialLine,
                                                                               userDto, traceabilityRepository);
                }
            }
            removeMaterialLines.removeAll(fromStockLines);
            if (!removeMaterialLines.isEmpty()) {
                return ProcureTypeHelper.doPlacedOrProcuredExternal(removeMaterial, updateOrder, requestHeaderRepo, procureLine, procureLineRepository,
                                                                    procurementServices, orderRepository, isPlaced, fromStockQty, userDto,
                                                                    traceabilityRepository);
            } else {
                MaterialLine removeMaterialLine = MaterialLineStatusHelper.cloneMaterialline(fromStockLines.get(0));
                removeMaterialLine.setQuantity(fromStockQty);
                MaterialLineStatusHelper.updateMaterialLineStatus(removeMaterialLine, MaterialLineStatus.REMOVED, "Removed",
                                                                  GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE, userDto, traceabilityRepository, true);
                removeMaterialLine.setMaterial(removeMaterial);
                ProcureLineHelper.deAssociateMaterialFromProcureLine(removeMaterial);
                return false;
            }
        } else {
            return false;
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
    public boolean isPartiallyProcuredFromStock() {
        return true;
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
    public void createSendGoodsReceipt(String companyCode, DeliveryNoteLine deliveryNoteLine, Long approvedQuantity, GoodsReceiptSender goodsReceiptSender,
            DeliveryNoteRepository deliveryNoteRepository, CommonServices commonServices) throws GloriaApplicationException {
        ProcureTypeHelper.createSendGoodsReceiptExternal(companyCode, deliveryNoteLine, approvedQuantity, goodsReceiptSender, deliveryNoteRepository,
                                                         commonServices);
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
    public void updateForUsageReplaced(ProcureLine procureLine, ProcureLineRepository procureLineRepository, Material removeMaterial,
            MaterialHeaderRepository requestHeaderRepo, ProcurementServices procurementServices, OrderRepository orderRepository, boolean isPlaced,
            UserDTO userDto, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        if (!isPlaced) {
            List<MaterialLine> fromStockLines = new ArrayList<MaterialLine>();
            Material procuredMaterial = null;
            for (Material material : procureLine.getMaterials()) {
                if (material.getMaterialType().equals(MaterialType.MODIFIED)) {
                    procuredMaterial = material;
                    break;
                }
            }
            for (MaterialLine materialLine : procuredMaterial.getMaterialLine()) {
                if (materialLine.getProcureType().isFromStock() && !materialLine.getStatus().isRemovedDb()) {
                    fromStockLines.add(materialLine);
                    materialLine.getProcureType().acceptChangeForMaterialLines(removeMaterial, requestHeaderRepo, procureLine, procureLineRepository,
                                                                               procurementServices, orderRepository, materialLine.getQuantity(), materialLine,
                                                                               userDto, traceabilityRepository);
                }
            }
            procuredMaterial.getMaterialLine().removeAll(fromStockLines);
            ProcureTypeHelper.revertRequisition(procureLine);
            ProcureTypeHelper.disconnectMaterialFromProcureline(removeMaterial);
        }
    }
    
    @Override
    public String getProcuredChangeAction(Material material) {
        return ProcureTypeHelper.getActionForPartialFromStock(material);
    }
}
