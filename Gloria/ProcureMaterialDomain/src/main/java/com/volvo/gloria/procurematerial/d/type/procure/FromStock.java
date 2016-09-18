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
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineHelper;
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineStatus;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderSapRepository;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.repositories.b.PurchaseOrganisationRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.c.GloriaExceptionConstants;
import com.volvo.gloria.util.c.GloriaParams;
import com.volvo.gloria.warehouse.b.WarehouseServices;

/**
 * Operations for FROM_STOCK procure type.
 */
public class FromStock extends ProcureTypeDefaultOperations {

    @Override
    public boolean procure(MaterialHeaderRepository requestHeaderRepository, ProcurementServices procurementServices, ProcureLine procureLine,
            List<MaterialLine> fromStockMaterialLines, TraceabilityRepository traceabilityRepository, MaterialServices materialServices,
            WarehouseServices warehouseServices, UserDTO user, boolean multipleProcure, ProcureLineDTO procureLineDTO, List<Material> materials)
            throws GloriaApplicationException {
        procurementServices.updateSupplierCounterPartInfo(procureLine);
        procurementServices.updateStatusAndProcureTeamInfo(procureLineDTO, user, procureLine, materials);
        procurementServices.createAdditionalMaterial(procureLine);
        ProcureTypeHelper.procureFromStock(requestHeaderRepository, procureLine, fromStockMaterialLines, traceabilityRepository, materialServices,
                                           warehouseServices, user);
        return true;
    }
    
    @Override
    public boolean update(Material removeMaterial, String changeAction, MaterialHeaderRepository requestHeaderRepo, ProcureLine procureLine,
            ProcureLineRepository procureLineRepository, ProcurementServices procurementServices, OrderRepository orderRepository, boolean isplaced,
            UserDTO userDto, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        ProcureTypeHelper.createNewReleasedOrAdditionalMaterial(removeMaterial, removeMaterial.getMaterialLine(), requestHeaderRepo, procureLine,
                                                    null, MaterialType.ADDITIONAL, userDto, traceabilityRepository);
        ProcureLineHelper.deAssociateMaterialFromProcureLine(removeMaterial);
        return false;
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
        return MaterialLineStatus.STORED;
    }

    @Override
    public void updateProcureLine(ProcureLineDTO procureLineDTO, ProcureLine procureLine, 
            PurchaseOrganisationRepository purchaseOrganisationRepo, UserDTO userDto)
            throws GloriaApplicationException {
        procureLine.setProcureInfo(null);
        procureLine.setMaxPrice(null);
        procureLine.setQualityDocumentOID(null);
        procureLine.setSupplier(null);
        procureLine.setUnitPrice(0);
        procureLine.setSupplierCounterPartOID(null);
        procureLine.setShipToId(null);
        procureLine.setCurrency(null);
        procureLine.setRequiredStaDate(null);
        procureLine.setPartAlias(null);
        procureLine.setBuyerCode(null);
        procureLine.setBuyerName(null);
        procureLine.setPurchaseOrgCode(null);
        procureLine.setPurchaseOrgName(null);
        procureLine.setReferenceGps(procureLineDTO.getReferenceGps());
    }
    
    @Override
    public void unAssignInternal(ProcureLine procureLine, ProcureLineRepository procureLineRepository) throws GloriaApplicationException {
        ProcureTypeHelper.unAssign(procureLine, procureLineRepository);
    }
    
    @Override
    public ProcureType setProcureType(long usageQuantity, long additionalQuantity, long selectedFromStockQuantity) throws GloriaApplicationException {
        if (selectedFromStockQuantity >= usageQuantity + additionalQuantity) {
            return ProcureType.FROM_STOCK;
        } else {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_PROCURE_TYPE, "Procure Type cannot be from stock. Invalid procure type.");
        }
            
    }
    
    @Override
    public MaterialLineStatus getOrderProcuredStatus() throws GloriaApplicationException {
        return MaterialLineStatus.STORED;
    }
    
    @Override
    public void validate(ProcureLineDTO procureLineDTO, ProcurementServices procurementServices) throws GloriaApplicationException {
     
    }
    
    @Override
    public void setProcureLineStatus(ProcureLine procureLine) {
        procureLine.setStatus(ProcureLineStatus.RECEIVED);
    }
    
    @Override
    public ProcureType getMaterialLineProcureType() throws GloriaApplicationException {
        return ProcureType.FROM_STOCK;
    }
    
    @Override
    public void acceptChangeForMaterialLines(Material removeMaterial, MaterialHeaderRepository requestHeaderRepo, ProcureLine procureLine,
            ProcureLineRepository procureLineRepository, ProcurementServices procurementServices, OrderRepository orderRepository, Long quantity,
            MaterialLine fromStockMaterialLine, UserDTO userDto, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        ProcureTypeHelper.createNewReleasedOrAdditionalMaterial(removeMaterial, removeMaterial.getMaterialLine(), requestHeaderRepo, procureLine,
                                                                fromStockMaterialLine, MaterialType.ADDITIONAL, userDto, traceabilityRepository);
    }

    @Override
    public boolean isFromStock() {
        return true;
    }

    @Override
    public void revertMaterialLine(MaterialLine materialLine, MaterialHeaderRepository requestHeaderRepository, UserDTO userDto,
            TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        Material oldMaterial = materialLine.getMaterial();
        ProcureTypeHelper.createNewReleasedOrAdditionalMaterial(oldMaterial, oldMaterial.getMaterialLine(), requestHeaderRepository, null, materialLine,
                                                                MaterialType.RELEASED, userDto, traceabilityRepository);
        oldMaterial.getMaterialLine().remove(materialLine);
        materialLine.setProcureType(null);
        materialLine.setOrderNo(null);
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
    public void createSendGoodsReceipt(String companyCode, DeliveryNoteLine deliveryNoteLine, Long approvedQuantity, GoodsReceiptSender goodsReceiptSender,
            DeliveryNoteRepository deliveryNoteRepository, CommonServices commonServices) throws GloriaApplicationException {
        ProcureTypeHelper.createSendGoodsReceiptInternal(deliveryNoteLine, approvedQuantity, deliveryNoteRepository, commonServices);
    }
    
    
    @Override
    public String getPlacedChangeAction(Material material) {
        return GloriaParams.ADDITIONAL;
    }
    
    @Override
    public String getProcuredChangeAction(Material material) {
        return GloriaParams.ADDITIONAL;
    }
    
    @Override
    public void updateForUsageReplaced(ProcureLine procureLine, ProcureLineRepository procureLineRepository, Material removeMaterial,
            MaterialHeaderRepository requestHeaderRepo, ProcurementServices procurementServices, OrderRepository orderRepository, boolean isPlaced,
            UserDTO userDto, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        Material procuredMaterial = null;
        for (Material material : procureLine.getMaterials()) {
            if (material.getMaterialType().equals(MaterialType.MODIFIED)) {
                procuredMaterial = material;
                break;
            }
        }
        ProcureTypeHelper.createNewReleasedOrAdditionalMaterial(procuredMaterial, procuredMaterial.getMaterialLine(), requestHeaderRepo, procureLine, null,
                                                                MaterialType.ADDITIONAL, userDto, traceabilityRepository);
        ProcureLineHelper.deAssociateMaterialFromProcureLine(removeMaterial);
    }
}
