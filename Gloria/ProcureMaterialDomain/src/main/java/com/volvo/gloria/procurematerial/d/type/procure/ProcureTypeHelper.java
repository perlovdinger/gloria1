package com.volvo.gloria.procurematerial.d.type.procure;

import static com.volvo.gloria.util.c.SAPParam.ACCOUNT_ASSIGNMENT_CATEGORY;
import static com.volvo.gloria.util.c.SAPParam.ACTION_CHANGE;
import static com.volvo.gloria.util.c.SAPParam.CATEGORY_OF_DELIVERY_DATE;
import static com.volvo.gloria.util.c.SAPParam.GOODS_RECEIPT_ASSIGN_CODE_GM;
import static com.volvo.gloria.util.c.SAPParam.GOODS_RECEIPT_HEADER_TEXT;
import static com.volvo.gloria.util.c.SAPParam.GOODS_RECEIPT_MOVEMENT_TYPE;
import static com.volvo.gloria.util.c.SAPParam.GR_INDICATOR;
import static com.volvo.gloria.util.c.SAPParam.IR_INDICATOR;
import static com.volvo.gloria.util.c.SAPParam.MATERIAL_GROUP;
import static com.volvo.gloria.util.c.SAPParam.MOVEMENT_INDICATOR;
import static com.volvo.gloria.util.c.SAPParam.NON_VALUED_GR_INDICATOR;
import static com.volvo.gloria.util.c.SAPParam.ORDER_TYPE;
import static com.volvo.gloria.util.c.SAPParam.PURCHASE_GROUP;
import static com.volvo.gloria.util.c.SAPParam.PURCHASE_TYPE;
import static com.volvo.gloria.util.c.SAPParam.TAX_CODE;
import static com.volvo.gloria.util.c.SAPParam.UNLIMITED_DELIVERY_INDICATOR;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.d.entities.CompanyCode;
import com.volvo.gloria.common.d.entities.Currency;
import com.volvo.gloria.common.repositories.b.CompanyCodeRepository;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.common.util.UnitConverter;
import com.volvo.gloria.financeProxy.b.GoodsReceiptSender;
import com.volvo.gloria.financeProxy.b.ProcessPurchaseOrderSender;
import com.volvo.gloria.financeProxy.c.GoodsReceiptHeaderTransformerDTO;
import com.volvo.gloria.financeProxy.c.ProcessPurchaseOrderDTO;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.b.beans.MaterialServicesHelper;
import com.volvo.gloria.procurematerial.c.CompleteType;
import com.volvo.gloria.procurematerial.c.PriceType;
import com.volvo.gloria.procurematerial.c.ProcureResponsibility;
import com.volvo.gloria.procurematerial.c.dto.ProcureLineDTO;
import com.volvo.gloria.procurematerial.d.entities.Buyer;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.FinanceHeader;
import com.volvo.gloria.procurematerial.d.entities.GoodsReceiptHeader;
import com.volvo.gloria.procurematerial.d.entities.GoodsReceiptLine;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.MessageStatus;
import com.volvo.gloria.procurematerial.d.entities.Order;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLineVersion;
import com.volvo.gloria.procurematerial.d.entities.OrderSap;
import com.volvo.gloria.procurematerial.d.entities.OrderSapAccounts;
import com.volvo.gloria.procurematerial.d.entities.OrderSapLine;
import com.volvo.gloria.procurematerial.d.entities.OrderSapSchedule;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.PurchaseOrganisation;
import com.volvo.gloria.procurematerial.d.entities.Requisition;
import com.volvo.gloria.procurematerial.d.entities.status.goodsReceiptLine.GoodsReceiptLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatusHelper;
import com.volvo.gloria.procurematerial.d.entities.status.orderLine.OrderLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineHelper;
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.requestline.MaterialStatus;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderSapRepository;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.repositories.b.PurchaseOrganisationRepository;
import com.volvo.gloria.procurematerial.util.MaterialTransformHelper;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.Utils;
import com.volvo.gloria.util.c.GloriaExceptionConstants;
import com.volvo.gloria.util.c.GloriaParams;
import com.volvo.gloria.util.tracing.c.GloriaTraceabilityConstants;
import com.volvo.gloria.warehouse.b.WarehouseServices;
import com.volvo.gloria.warehouse.d.entities.BinLocation;
import com.volvo.gloria.warehouse.d.entities.Placement;

/**
 * Helper class for ProcureType.
 * 
 */
public final class ProcureTypeHelper {
    
    private static final int MAX_REFERENCE_LENGTH = 20;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcureTypeHelper.class);
    
    private static final int LEFT_PAD_CURRENTBUYER = 4;
    private static final int LEFT_PAD_GENERALLEDGER = 10;
    private static final int LEFT_PAD_COSTCENTER = 10;
    private static final int LEFT_PAD_VENDOR = 10;
    
    private static final int UOP_PCE = 1;
 

    private ProcureTypeHelper() {
    }

    /**
     * 
     * quantity will be always usage + additional >= fromStock.
     * 
     * if from stock is > usage+additional then, from stock = usage+additional
     */
    public static void procureFromStock(MaterialHeaderRepository requestHeaderRepository, ProcureLine procureLine, List<MaterialLine> fromStockMaterialLines,
            TraceabilityRepository traceabilityRepository, MaterialServices materialServices, WarehouseServices warehouseServices, UserDTO user)
            throws GloriaApplicationException {
        if (fromStockMaterialLines != null && !fromStockMaterialLines.isEmpty()) {
            // stock
            List<MaterialLine> availableMaterialLinesFromStock = new ArrayList<MaterialLine>();
            availableMaterialLinesFromStock.addAll(fromStockMaterialLines);

            // need
            List<Material> materialsToProcure = procureLine.getMaterials();

            if (materialsToProcure != null && !materialsToProcure.isEmpty()) {
                for (Material materialToProcure : materialsToProcure) {
                    if (!materialToProcure.getMaterialType().equals(MaterialType.USAGE_REPLACED)) {
                        MaterialLine materialLineToProcure = materialToProcure.getMaterialLine().get(0);
                        for (int idx = availableMaterialLinesFromStock.size() - 1; idx >= 0; idx--) {
                            MaterialLine materialLineFromStock = availableMaterialLinesFromStock.get(idx);
                            if (materialLineFromStock.getQuantity() >= materialLineToProcure.getQuantity()) {
                                procureFullNeedFromStock(materialToProcure, materialLineToProcure, materialLineFromStock, requestHeaderRepository,
                                                         traceabilityRepository, materialServices, warehouseServices, user);
                                break;
                            } else {
                                availableMaterialLinesFromStock.remove(procurePartialNeedFromStock(materialToProcure, materialLineToProcure,
                                                                                                   materialLineFromStock, traceabilityRepository, user));
                            }
                          
                        }
                    }
                }
            }
        }
    }

    private static MaterialLine procureFullNeedFromStock(Material materialToProcure, MaterialLine materialLineToProcure,
            MaterialLine availableMaterialLineFromStock, MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository,
            MaterialServices materialServices, WarehouseServices warehouseServices, UserDTO user) throws GloriaApplicationException {

        Long placementOID = availableMaterialLineFromStock.getPlacementOID();
        BinLocation extistingBinlocationForStockMaterial = null;
        if (placementOID != null) {
            Placement placement = warehouseServices.getPlacement(placementOID);
            if (placement != null) {
                extistingBinlocationForStockMaterial = placement.getBinLocation();
                MaterialServicesHelper.removePlacement(availableMaterialLineFromStock, warehouseServices);
            }
        }

        List<MaterialLineStatus> avoidTraceForMLStatus = new ArrayList<MaterialLineStatus>();
        avoidTraceForMLStatus.add(availableMaterialLineFromStock.getStatus());
        MaterialLine splittedMaterialLineFromStock = MaterialLineStatusHelper.split(availableMaterialLineFromStock, materialLineToProcure.getQuantity(),
                                                                                    requestHeaderRepository, traceabilityRepository, materialServices, user,
                                                                                    avoidTraceForMLStatus);

        materialServices.createPlacement(extistingBinlocationForStockMaterial, splittedMaterialLineFromStock);
        if (availableMaterialLineFromStock.getQuantity() > 0) {
            materialServices.createPlacement(extistingBinlocationForStockMaterial, availableMaterialLineFromStock);
        }

        // remove current materialNeed
        materialToProcure.getMaterialLine().remove(materialLineToProcure);
        requestHeaderRepository.deleteMaterialLine(materialLineToProcure);

        swapMaterialLineFromStock(materialToProcure, splittedMaterialLineFromStock, traceabilityRepository, user);
        return availableMaterialLineFromStock;
    }

    private static void swapMaterialLineFromStock(Material materialToProcure, MaterialLine availableMaterialLineFromStock,
            TraceabilityRepository traceabilityRepository, UserDTO userDTO) throws GloriaApplicationException {
        Material material = availableMaterialLineFromStock.getMaterial();
        String action = getFromStockTraceAction(material, availableMaterialLineFromStock);
        String actionDetail = getFromStockTraceActionDetail(material, availableMaterialLineFromStock);

        availableMaterialLineFromStock.setProcureType(ProcureType.FROM_STOCK);
        MaterialLineStatusHelper.assignMaterialLineToMaterial(materialToProcure, availableMaterialLineFromStock);

        if (userDTO != null && !availableMaterialLineFromStock.getStatus().equals(MaterialLineStatus.REMOVED_DB)) {
            MaterialLineStatusHelper.createTraceabilityLog(availableMaterialLineFromStock, traceabilityRepository, action, actionDetail, userDTO.getId(),
                                                           userDTO.getUserName(), GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE);
        }

    }

    private static String getFromStockTraceActionDetail(Material material, MaterialLine availableMaterialLineFromStock) {
        String materialId = material.getMtrlRequestVersionAccepted();
        String orderNo = availableMaterialLineFromStock.getOrderNo();
        String testObj = null;
        String phase = null;
        String actionDetail = "";
        if (materialId != null || orderNo != null) {
            MaterialHeader materialHeader = material.getMaterialHeader();
            if (materialHeader != null) {
                testObj = materialHeader.getReferenceId();
                phase = materialHeader.getBuildName();
            }

            actionDetail = "Mtr id: " + materialId + ", Ord No: " + orderNo;
            if (!StringUtils.isEmpty(testObj)) {
                actionDetail = actionDetail + ", TO: " + testObj;
            }
            if (!StringUtils.isEmpty(phase)) {
                actionDetail = actionDetail + ", Phase: " + phase;
            }
        }
        return actionDetail;
    }

    private static String getFromStockTraceAction(Material material, MaterialLine availableMaterialLineFromStock) {
        MaterialType materialType = material.getMaterialType();
        String materialId = material.getMtrlRequestVersionAccepted();
        String orderNo = availableMaterialLineFromStock.getOrderNo();
        if (materialId != null || orderNo != null) {
            if (materialType.equals(MaterialType.ADDITIONAL_USAGE)) {
                return "From Stock - " + StringUtils.capitalize(MaterialType.ADDITIONAL.name().toLowerCase());
            } else {
                return "From Stock - " + StringUtils.capitalize(materialType.name().toLowerCase());
            }
        }
        return "From Stock - Released/Qty Adj";
    }

    private static MaterialLine procurePartialNeedFromStock(Material materialToProcure, MaterialLine materialLineToProcure,
            MaterialLine availableMaterialLineFromStock, TraceabilityRepository traceabilityRepository, UserDTO userDTO)
            throws GloriaApplicationException {
       
        swapMaterialLineFromStock(materialToProcure, availableMaterialLineFromStock, traceabilityRepository, userDTO);

        // update the remaining need
        materialLineToProcure.setQuantity(materialLineToProcure.getQuantity() - availableMaterialLineFromStock.getQuantity());

        return availableMaterialLineFromStock;
    }
    
    public static void procureExternal(ProcurementServices procurementServices, ProcureLine procureLine) throws GloriaApplicationException {
        procurementServices.createRequisition(procureLine);
        procurementServices.sendProcureLineRequsitions(procureLine);
    }

    public static void procureInternal(ProcurementServices procurementServices, ProcureLine procureLine) throws GloriaApplicationException {
        procurementServices.createRequisition(procureLine);
        procurementServices.createInternalOrder(procureLine, true);
    }

    public static boolean validateMissingBuyerId(ProcureLine procureLine, boolean multipleProcure, boolean procuredParam) throws GloriaApplicationException {
        boolean procured = procuredParam;
        if (StringUtils.isEmpty(procureLine.getBuyerCode())
                && procureLine.getResponsibility().equals(ProcureResponsibility.PROCURER)) {
            if (multipleProcure) {
                procured = false;
            } else {
                throw new GloriaApplicationException("buyerCode", GloriaExceptionConstants.BUYER_ID_REQUIRED, "Buyer ID is missing. ", null);
            }
        }
        return procured;
    }

    public static boolean validatePriceAndCurrency(Double price, String currency, boolean multipleProcure, boolean procuredParam)
            throws GloriaApplicationException {
        boolean procured = procuredParam;
        if (price == null) {
            return procured;
        }
        
        if (!Utils.validateMaxPrice(price)) {
            if (multipleProcure) {
                procured = false;
            } else {
                throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_MAX_PRICE, "This operation cannot be performed. Price is not valid.");
            }
        }
        // If price is entered, currency must be selected
        if (price > 0 && StringUtils.isEmpty(currency)) {
            if (multipleProcure) {
                procured = false;
            } else {
                throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_CURRENCY,
                        "This operation cannot be performed. Currency should not be empty.");
            }
        }
        return procured;
    }

    public static void revertMaterial(ProcureLine procureLine, MaterialHeaderRepository requestHeaderRepository, ProcureLineRepository procureLineRepository,
            ProcurementServices procurementServices, UserDTO userDTO, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        if (procureLine != null) {
            List<Material> materials = procureLine.getMaterials();
            List<Material> materialsToBeRemoved = new ArrayList<Material>();

            for (Material material : materials) {
                material.getMaterialType().revert(material, procureLine, requestHeaderRepository, procureLineRepository, materialsToBeRemoved, userDTO,
                                                  traceabilityRepository);
            }

            procureLine.getMaterials().removeAll(materialsToBeRemoved);
            for (Material material : materialsToBeRemoved) {
                requestHeaderRepository.deleteMaterial(material);
            }

            revertAndGroupMaterialForProcureLine(procureLine, procureLineRepository, procurementServices, userDTO);
        }
    }

    public static void revertAndGroupMaterialForProcureLine(ProcureLine procureLine, ProcureLineRepository procureLineRepository,
            ProcurementServices procurementServices, UserDTO userDTO) throws GloriaApplicationException {
        String forwardedUserId = procureLine.getForwardedUserId();
        String forwardedUserName = procureLine.getForwardedUserName();
        String forwardedTeam = procureLine.getForwardedTeam();
        ProcureType procureType = procureLine.getProcureType();

        List<Material> revertedMaterialsToUsage = procureLine.getMaterials();
        procureLineRepository.delete(procureLine);
        List<Material> materialsNotFound = new ArrayList<Material>();
        for (int idx = revertedMaterialsToUsage.size() - 1; idx >= 0; idx--) {
            Material material = revertedMaterialsToUsage.get(idx);
            materialsNotFound.addAll(procurementServices.groupIfMaterialsExist(userDTO.getId(), material));
        }
        procurementServices.groupMaterials(materialsNotFound);
        for (Material material : revertedMaterialsToUsage) {
            ProcureLine revertedProcureLine = material.getProcureLine();
            if (revertedProcureLine != null) {
                revertedProcureLine.setProcureType(procureType);
                revertedProcureLine.setForwardedUserId(forwardedUserId);
                revertedProcureLine.setForwardedUserName(forwardedUserName);
                revertedProcureLine.setForwardedTeam(forwardedTeam);
            }
        }
    }

    public static void revertOrder(ProcureLine procureLine, OrderRepository orderRepository) {
        if (procureLine != null) {
            List<Material> materials = procureLine.getMaterials();
            if (materials != null && !materials.isEmpty()) {
                boolean deleteOrder = true;
                for (Material material : materials) {
                    OrderLine orderLine = material.getOrderLine();
                    if (orderLine != null && deleteOrder) {
                        Order order = orderLine.getOrder();
                        if (order.getOrderLines().size() > 1) {
                            order.getOrderLines().remove(orderLine);
                            orderRepository.delete(orderLine);
                        } else {
                            orderRepository.delete(order);
                        }
                        deleteOrder = false;
                    }
                    material.setOrderLine(null);
                    material.setOrderNo(null);
                }
            }
        }
    }

    public static void revertRequisition(ProcureLine procureLine) {
        if (procureLine != null) {
            Requisition requisition = procureLine.getRequisition();
            if (requisition != null) {
                procureLine.getRequisition().setCancelled(true);
                procureLine.setRequisition(null);
            }
        }
    }

    public static void recreateRequisition(ProcureLine procureLine, ProcurementServices procurementServices) throws GloriaApplicationException {
        Double price = null;
        if (ProcureType.INTERNAL.equals(procureLine.getProcureType())) {
            price = procureLine.getUnitPrice();
        } else {
            price = procureLine.getMaxPrice();
        }
        validatePriceAndCurrency(price, procureLine.getCurrency(), false, false);
        revertRequisition(procureLine);
        procurementServices.createRequisition(procureLine);
    }

    public static void recreateOrder(ProcureLine procureLine, ProcurementServices procurementServices, OrderRepository orderRepository)
            throws GloriaApplicationException {
        revertOrder(procureLine, orderRepository);
        procurementServices.createInternalOrder(procureLine, false);
    }

    public static void forwardProcureLine(ProcureLine procureLine, String forwardedTeam, String forwardedUserId, String forwardedUserName,
            ProcureLineRepository procureLineRepository, ProcurementServices procurementServices, UserDTO userDTO, TraceabilityRepository traceabilityRepository)
            throws GloriaApplicationException {
        List<Material> materials = procureLine.getMaterials();
        int size = materials.size();
        int numberOfGroupedMaterials = 0;
        for (int i = 0; i < size; i++) {
            Material material = materials.get(i);
            List<Material> materialsToBeGrouped = procurementServices.groupIfMaterialsExist(userDTO.getId(), material, forwardedUserId, forwardedTeam);
            numberOfGroupedMaterials = numberOfGroupedMaterials + materialsToBeGrouped.size();
        }
        if (numberOfGroupedMaterials == 0) {
            // remove a procureline if it has no materials attached to it
            procureLine.getStatus().remove(procureLine, procureLineRepository);
        } else {
            procureLine.setForwardedTeam(forwardedTeam);
            procureLine.setForwardedUserId(forwardedUserId);
            procureLine.setForwardedUserName(forwardedUserName);
            procureLineRepository.save(procureLine);
            
            List<Material> materialList = procureLine.getMaterials();
            for (Material material : materialList) {
                List<MaterialLine> materialLines = material.getMaterialLine();
                if (materialLines != null) {
                    for (MaterialLine materialLine : materialLines) {
                        if (materialLine.getStatus().equals(MaterialLineStatus.WAIT_TO_PROCURE)) {
                            MaterialLineStatusHelper.createTraceabilityLog(materialLine, traceabilityRepository, "Forwarded to IP", "IP Team : "
                                    + forwardedTeam, userDTO.getId(), userDTO.getUserName(), null);
                            if (!StringUtils.isEmpty(forwardedUserId)) {
                                String actionDetail = forwardedUserId + " - " + forwardedUserName + " (" + forwardedTeam + ")";
                                MaterialLineStatusHelper.createTraceabilityLog(materialLine, traceabilityRepository, "Assigned IP", actionDetail, userDTO.getId(),
                                                                               userDTO.getUserName(), null);
                            }
                        }
                    }
                }
            }
        }

    }
    
    public static void returnProcureLine(ProcureLine procureLine, ProcureLineRepository procureLineRepository, ProcurementServices procurementServices)
            throws GloriaApplicationException {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(procureLine.getMaterialControllerId());
        List<Material> materials = procureLine.getMaterials();
        int size = materials.size();
        int numberOfGroupedMaterials = 0;
        
        //temporary procureline - to generate md5GroupingKey without forward procurement informations
        ProcureLine temporaryProcureLine = new ProcureLine();
        temporaryProcureLine.setResponsibility(procureLine.getResponsibility());        
        
        for (int i = 0; i < size; i++) {
            Material material = materials.get(i);
            material.setTemporaryProcureLine(temporaryProcureLine);
            List<Material> materialsToBeGrouped = procurementServices.groupIfMaterialsExist(userDTO.getId(), material);
            numberOfGroupedMaterials = numberOfGroupedMaterials + materialsToBeGrouped.size();
        }
        if (numberOfGroupedMaterials == 0) {
            // remove a procureline if it has no materials attached to it
            procureLine.getStatus().remove(procureLine, procureLineRepository);
        } else {
            procureLine.setForwardedTeam(null);
            procureLine.setForwardedUserId(null);
            procureLine.setForwardedUserName(null);
            procureLineRepository.save(procureLine);
        }
    }
    
    public static void assign(ProcureLine procureLine, String userTeam, String userId, String userName,
            ProcureLineRepository procureLineRepository) {
        procureLine.setForwardedTeam(userTeam);
        procureLine.setForwardedUserId(userId);
        procureLine.setForwardedUserName(userName);
        procureLineRepository.save(procureLine);
    }
    
    public static void unAssign(ProcureLine procureLine, ProcureLineRepository procureLineRepository) {
        procureLine.setForwardedUserId(null);
        procureLine.setForwardedUserName(null);
        procureLineRepository.save(procureLine);
    }

    public static void updateProcureLineForInternal(ProcureLineDTO procureLineDTO, ProcureLine procureLine,
            PurchaseOrganisationRepository purchaseOrganisationRepo, UserDTO userDto) throws GloriaApplicationException {
        validateReferenceGps(procureLineDTO);
        procureLine.setOrderNo(procureLineDTO.getOrderNo());
        procureLine.setUnitPrice(procureLineDTO.getUnitPrice());
        procureLine.setReferenceGps(procureLineDTO.getReferenceGps());
        procureLine.setProcureInfo(null);
        procureLine.setMaxPrice(null);

        procureLine.setQualityDocumentOID(null);    
        Buyer buyer = purchaseOrganisationRepo.findBuyerByCodeAndOrganisationCode(procureLineDTO.getBuyerCode(), procureLineDTO.getPurchaseOrganisationCode());
        if (buyer == null) {
            buyer = createBuyer(userDto.getId(), userDto.getUserName(), purchaseOrganisationRepo);
        }
        procureLine.setBuyerCode(buyer.getCode());
        procureLine.setBuyerName(buyer.getName());
        PurchaseOrganisation purchaseOrganisation = purchaseOrganisationRepo.findPurchaseOrgByCode(procureLineDTO.getPurchaseOrganisationCode());
        buyer.setPurchaseOrganisation(purchaseOrganisation);
        if (purchaseOrganisation != null) {
            procureLine.setPurchaseOrgCode(purchaseOrganisation.getOrganisationCode());
            procureLine.setPurchaseOrgName(purchaseOrganisation.getOrganisationName());
        }
        long perQuantity = procureLineDTO.getUnitOfPrice();
        if (perQuantity <= 0) {
            perQuantity = UOP_PCE;
        }
        procureLine.setPerQuantity(perQuantity);
    }

    public static void updateProcureLineForExternal(ProcureLineDTO procureLineDTO, ProcureLine procureLine,
            PurchaseOrganisationRepository purchaseOrganisationRepo) throws GloriaApplicationException {
        validateReferenceGps(procureLineDTO);

        if (!procureLine.getStatus().isOrderPlaced()) {
            procureLine.setOrderNo(null);
            procureLine.setSupplier(null);
            procureLine.setUnitPrice(0);
            procureLine.setPartAlias(null);
        }

        procureLine.setReferenceGps(procureLineDTO.getReferenceGps());
        procureLine.setProcureInfo(procureLineDTO.getProcureInfo());
        procureLine.setMaxPrice(procureLineDTO.getMaxPrice());
        
        PurchaseOrganisation purchaseOrganisation = purchaseOrganisationRepo.findPurchaseOrgByCode(procureLineDTO.getPurchaseOrganisationCode());
        if (purchaseOrganisation == null) {
            throw new GloriaApplicationException(GloriaExceptionConstants.PURCHASE_ORGANISATION_MISSING,
                                                 "Cannot send requisition, missing purchase organisation.");
        }
        
        if (!StringUtils.isEmpty(procureLineDTO.getBuyerCode())) {
            Buyer buyer = purchaseOrganisationRepo.findBuyerByCodeAndOrganisationCode(procureLineDTO.getBuyerCode(),
                                                                                      procureLineDTO.getPurchaseOrganisationCode());
            if (buyer == null) {
                buyer = createBuyer(procureLineDTO.getBuyerCode(), procureLineDTO.getPurchaseOrganisationCode(), purchaseOrganisationRepo);
            }
            procureLine.setBuyerCode(buyer.getCode());
            procureLine.setBuyerName(buyer.getName());
            buyer.setPurchaseOrganisation(purchaseOrganisation);
        }
        
        procureLine.setPurchaseOrgCode(purchaseOrganisation.getOrganisationCode());
        procureLine.setPurchaseOrgName(purchaseOrganisation.getOrganisationName());
        procureLine.setQualityDocumentOID(procureLineDTO.getQualityDocumentId());
    }

    private static void validateReferenceGps(ProcureLineDTO procureLineDTO) throws GloriaApplicationException {
        String referenceGps = procureLineDTO.getReferenceGps();
        if (referenceGps != null && referenceGps.length() > MAX_REFERENCE_LENGTH) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_STRING_LENGTH, "The max reference length is  " + MAX_REFERENCE_LENGTH);
        }
    }
    
    public static Buyer createBuyer(String buyerCode, String purchaseOrganisationCode, PurchaseOrganisationRepository purchaseOrganisationRepo) {
        Buyer buyer = new Buyer();
        buyer.setCode(buyerCode);
        PurchaseOrganisation purchaseOrganisation = purchaseOrganisationRepo.findPurchaseOrgByCode(purchaseOrganisationCode);
        if (purchaseOrganisation != null) {
            buyer.setName(purchaseOrganisation.getOrganisationName());
        } else {
            buyer.setName(purchaseOrganisationCode); 
        }
        buyer.setPurchaseOrganisation(purchaseOrganisation);
        return purchaseOrganisationRepo.save(buyer);
    }
    
    public static boolean updateChangeToExternal(Material removeMaterial, boolean updateOrder, MaterialHeaderRepository requestHeaderRepo,
            ProcureLine procureLine, ProcureLineRepository procureLineRepository, ProcurementServices procurementServices, UserDTO userDto,
            TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        boolean removeProcureLine = false;
        if (updateOrder) {
            if (hasMoreUsageMaterials(procureLine, removeMaterial)) {
                disconnectMaterialFromProcureline(removeMaterial);
                recreateRequisition(procureLine, procurementServices);
                procurementServices.sendProcureLineRequsitions(procureLine);
            } else {
                revertRequisition(procureLine);
                disconnectMaterialFromProcureline(removeMaterial);
                removeProcureLine = true;
            }
        } else {
            if (!removeMaterial.getMaterialLine().isEmpty()) {
                createNewReleasedOrAdditionalMaterial(removeMaterial, removeMaterial.getMaterialLine(), requestHeaderRepo, procureLine, null,
                                                      MaterialType.ADDITIONAL, userDto, traceabilityRepository);
            } 
            ProcureLineHelper.deAssociateMaterialFromProcureLine(removeMaterial);
        }
        return removeProcureLine;
    }

    public static boolean hasMoreUsageMaterials(ProcureLine procureLine, Material removeMaterial) {
        boolean hasMaterials = false;
        List<Material> materials = procureLine.getMaterials();
        if (materials != null && materials.size() > 1) {
            for (Material material : materials) {
                if (material.getMaterialOID() != removeMaterial.getMaterialOID() && material.getMaterialType().equals(MaterialType.USAGE)) {
                    hasMaterials = true;
                    break;
                }
            }
        }
        return hasMaterials;
    }

    public static void disconnectMaterialFromProcureline(Material removeMaterial) {
        for (MaterialLine materialLine : removeMaterial.getMaterialLine()) {
            if (!materialLine.getStatus().isRemovedDb()) {
                materialLine.setStatus(MaterialLineStatus.REMOVED);
                materialLine.setStatusDate(DateUtil.getCurrentUTCDateTime());
            }
        }
        ProcureLineHelper.deAssociateMaterialFromProcureLine(removeMaterial);
    }
    
    public static boolean updateChangeToInternal(Material removeMaterial, boolean updateOrder, MaterialHeaderRepository requestHeaderRepo,
            ProcureLine procureLine, ProcureLineRepository procureLineRepository, ProcurementServices procurementServices, 
            OrderRepository orderRepository, UserDTO userDto, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        boolean removeProcureLine = false;
        if (updateOrder) {
            if (hasMoreUsageMaterials(procureLine, removeMaterial)) {
                disconnectMaterialFromProcureline(removeMaterial);
                recreateRequisition(procureLine, procurementServices);
                recreateOrder(procureLine, procurementServices, orderRepository);
            } else {
                OrderLine orderLine = procureLine.getMaterials().get(0).getOrderLine();
                orderLine.setPossibleToReceiveQuantity(orderLine.getReceivedQuantity());
                procureLine.getRequisition().setCancelled(true);
                procureLine.setStatus(ProcureLineStatus.CANCELLED);
                orderLine.setCompleteType(CompleteType.CANCELLED);
                orderLine.setStatus(OrderLineStatus.COMPLETED);
                disconnectMaterialFromProcureline(removeMaterial);
                removeProcureLine = false;
            }
        } else {
            createNewReleasedOrAdditionalMaterial(removeMaterial, removeMaterial.getMaterialLine(), requestHeaderRepo, procureLine, null,
                                                      MaterialType.ADDITIONAL, userDto, traceabilityRepository);
            
            ProcureLineHelper.deAssociateMaterialFromProcureLine(removeMaterial);
           
        }
        return removeProcureLine;
    }

    public static void createNewReleasedOrAdditionalMaterial(Material removeMaterial, List<MaterialLine> materialLinesToRemove,
            MaterialHeaderRepository requestHeaderRepo, ProcureLine procureLine, MaterialLine fromStockmaterialLine, MaterialType materialType, UserDTO userDto,
            TraceabilityRepository traceabilityRepository)
            throws GloriaApplicationException {
        Material newMaterial = cloneMaterial(removeMaterial);
        newMaterial.setStatus(MaterialStatus.ADDED);
        newMaterial.setMaterialType(materialType);
        newMaterial.setMaterialHeader(null);
        if (fromStockmaterialLine == null) {
            long removedQty = 0;
            for (MaterialLine materialLine : materialLinesToRemove) {
                if (!materialLine.getStatus().isRemovedDb()) {
                    removedQty += materialLine.getQuantity();
                    MaterialLineStatus previousStatus = materialLine.getPreviousStatus();
                    if (previousStatus != null) {
                        materialLine.setStatus(previousStatus);
                    }
                    materialLine.setMaterial(newMaterial);
                    materialLine.setMaterialOwner(newMaterial);
                }
            }
            MaterialLine aMaterialLine = materialLinesToRemove.get(0);
            MaterialLine removeMaterialLine = MaterialLineStatusHelper.cloneMaterialline(aMaterialLine);
            removeMaterialLine.setQuantity(removedQty);
            removeMaterialLine.setStatus(MaterialLineStatus.REMOVED);
            MaterialLineStatusHelper.assignMaterialLineToMaterial(removeMaterial, removeMaterialLine);

            ProcureLineHelper.associateMaterialWithProcureLine(newMaterial, procureLine);
            newMaterial.setMaterialLine(materialLinesToRemove);
        } else {
            MaterialLine clonedMaterialline = MaterialLineStatusHelper.cloneMateriallineAttributes(fromStockmaterialLine);
            MaterialLineStatusHelper.assignMaterialLineToMaterial(removeMaterial, clonedMaterialline);
            MaterialLineStatusHelper.assignMaterialLineToMaterial(newMaterial, fromStockmaterialLine);
            newMaterial.setOrderLine(null);
            newMaterial.setOrderNo(null);
            ProcureLineHelper.deAssociateMaterialFromProcureLine(newMaterial);
        }
        if (materialType.equals(MaterialType.RELEASED) && newMaterial.getFinanceHeader() != null) {
            newMaterial.getFinanceHeader().setProjectId(null);
        }        

        requestHeaderRepo.addMaterial(newMaterial);
        
        if (newMaterial.getStatus() != null && userDto != null) {
            for (MaterialLine materialLine : removeMaterial.getMaterialLine()) {
                if (!materialLine.getStatus().isRemovedDb() && materialLine.getStatus() != MaterialLineStatus.REMOVED) {
                    String actionDetail = "Change Accepted as " + materialType + " : " + removeMaterial.getMtrlRequestVersionAccepted();
                    MaterialLineStatusHelper.createTraceabilityLog(materialLine, traceabilityRepository,
                                                                   MaterialLineStatusHelper.getAdjustedActionForMaterialType(materialType.name()),
                                                                   actionDetail, userDto.getId(), userDto.getUserName(),
                                                                   GloriaTraceabilityConstants.ORDER_STAACCEPTED_AGREEDSTA);
                }
            }
        }
    }

    public static boolean doPlacedOrProcuredExternal(Material removeMaterial, boolean updateOrder, MaterialHeaderRepository requestHeaderRepo,
            ProcureLine procureLine, ProcureLineRepository procureLineRepository, ProcurementServices procurementServices, OrderRepository orderRepository,
            boolean isPlaced, long fromStockQty, UserDTO userDto, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        if (isPlaced) {
            ProcureTypeHelper.createNewReleasedOrAdditionalMaterial(removeMaterial, removeMaterial.getMaterialLine(), requestHeaderRepo, procureLine, null,
                                                                    MaterialType.ADDITIONAL, userDto, traceabilityRepository);
            ProcureLineHelper.deAssociateMaterialFromProcureLine(removeMaterial);
            return false;
        } else {
            return ProcureTypeHelper.updateChangeToExternal(removeMaterial, updateOrder, requestHeaderRepo, procureLine, procureLineRepository,
                                                            procurementServices, userDto, traceabilityRepository);
        }
    }
   
    public static void updateBuyerCodeInProcureLine(Order order, PurchaseOrganisationRepository purchaseOrganisationRepo,
            ProcureLineRepository procureLineRepository) {
        for (OrderLine anOrderLine : order.getOrderLines()) {
            OrderLineVersion currentOrderLineVersion = anOrderLine.getCurrent();
            Buyer buyer = purchaseOrganisationRepo.findBuyerByCodeAndOrganisationCode(currentOrderLineVersion.getBuyerId(), currentOrderLineVersion.getBuyerName());
            ProcureLine procureLine = anOrderLine.getProcureLine();
            if (procureLine != null) {
                if (buyer == null) {
                    buyer = createBuyer(currentOrderLineVersion.getBuyerId(), currentOrderLineVersion.getBuyerName(), purchaseOrganisationRepo);
                }

                procureLine.setBuyerCode(buyer.getCode());
                procureLine.setBuyerName(buyer.getName());
                PurchaseOrganisation purchaseOrganisation = buyer.getPurchaseOrganisation();
                if (purchaseOrganisation != null) {
                    procureLine.setPurchaseOrgCode(purchaseOrganisation.getOrganisationCode());
                    procureLine.setPurchaseOrgName(purchaseOrganisation.getOrganisationName());
                }
                procureLineRepository.save(procureLine);
            }
        }
    }

    public static String translateUOMToGPSUnits(String gloriaUnitOfMeasure, CommonServices commonServices) {
       return UnitConverter.convert(GloriaParams.APPLICATION_GLORIA, GloriaParams.APPLICATION_GPS, gloriaUnitOfMeasure, commonServices);
    }

    public static long convertQuantityToGPSUnits(long quantity, String gloriaUnitOfMeasure, CommonServices commonServices) {
        return UnitConverter.convert(GloriaParams.APPLICATION_GLORIA, GloriaParams.APPLICATION_GPS, gloriaUnitOfMeasure, quantity, commonServices);
    }

    public static boolean validateOrderStaDate(ProcureLine procureLine, boolean multipleProcure, boolean procuredParam) throws GloriaApplicationException {
        boolean procured = procuredParam;
        if (procureLine.getOrderStaDate() == null) {
            if (multipleProcure) {
                procured = false;
            } else {
                throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_ORDERSTADATE, "OrderStaDate is mandatory.");
            }
        }
        return procured;
    }

    public static boolean validateShipToAndPPSuffix(ProcureLine procureLine, boolean multipleProcure, boolean procuredParam) throws GloriaApplicationException {
        boolean procured = procuredParam;
        if (StringUtils.isEmpty(procureLine.getShipToId())) {
            if (multipleProcure) {
                procured = false;
            } else {
                throw new GloriaApplicationException("supplierCounterPartID", GloriaExceptionConstants.SHIP_TO_AND_PP_SUFFIX_REQUIRED, 
                                                     "Ship to and ppSuffix is missing. ", null);
            }
        }
        return procured;
    }

    public static boolean validateMissingReqdSTADate(ProcureLine procureLine, boolean multipleProcure, boolean procuredParam) 
            throws GloriaApplicationException {
        boolean procured = procuredParam;
        if (procureLine.getRequiredStaDate() == null) {
            if (multipleProcure) {
                procured = false;
            } else {
                throw new GloriaApplicationException("requiredStaDate", GloriaExceptionConstants.REQD_STA_DATE_MISSING, "Required STA Date is missing. ", null);
            }
        }
        return procured;
    }

    public static boolean validateMissingFinalWhSite(ProcureLine procureLine, boolean multipleProcure, boolean procuredParam) 
            throws GloriaApplicationException {
        boolean procured = procuredParam;
        List<Material> materials = procureLine.getMaterials();
        for (Material material : materials) {
            if (material.getMaterialType() != MaterialType.USAGE_REPLACED) {
                for (MaterialLine materialLine : material.getMaterialLine()) {
                    if (StringUtils.isEmpty(materialLine.getFinalWhSiteId())) {
                        if (multipleProcure) {
                            procured = false;
                        } else {
                            throw new GloriaApplicationException("finalWhSiteId", GloriaExceptionConstants.FINAL_WHSITE_REQUIRED,
                                                                 "Final Warehouse is missing. ", null);
                        }
                    }
                }
            }
        }
        return procured;
    }

    public static boolean validateMissingInternalSupplier(ProcureLine procureLine,
                            boolean multipleProcure, boolean procuredParam) throws GloriaApplicationException {
        boolean procured = procuredParam;
        if (procureLine.getSupplier() == null) {
            if (multipleProcure) {
                procured = false;
            } else {
                throw new GloriaApplicationException("supplier", GloriaExceptionConstants.INTERNAL_SUPPLIER_MISSING, "Internal Supplier is missing. ", null);
            }
        }
        return procured;
    }

    public static boolean validateMissingOrderNo(ProcureLine procureLine, boolean multipleProcure, boolean procuredParam) throws GloriaApplicationException {
        boolean procured = procuredParam;
        if (StringUtils.isEmpty(procureLine.getOrderNo())) {
            if (multipleProcure) {
                procured = false;
            } else {
                throw new GloriaApplicationException("orderNo", GloriaExceptionConstants.ORDER_NO_MISSING, "Order No is missing. ", null);
            }
        }
        return procured;           
    }
    
    public static GoodsReceiptHeader createGoodsReceipt(long deliveryNoteLineOId, long quantity, DeliveryNoteRepository deliveryNoteRepository,
            CommonServices commonServices) throws GloriaApplicationException {

        DeliveryNoteLine deliveryNoteLine = deliveryNoteRepository.findDeliveryNoteLineById(deliveryNoteLineOId);

        String materialUserId = deliveryNoteLine.getOrderLine().getOrder().getMaterialUserId();

        GoodsReceiptHeader goodsReceiptHeader = new GoodsReceiptHeader();
        if (materialUserId != null) {
            goodsReceiptHeader.setCompanyCode(commonServices.getSiteBySiteId(materialUserId).getCompanyCode());
        }
        goodsReceiptHeader.setPostedDateTime(DateUtil.getCurrentUTCDate());
        goodsReceiptHeader.setDocumentDate(deliveryNoteLine.getDeliveryNote().getDeliveryNoteDate());
        goodsReceiptHeader.setReferenceDocument(deliveryNoteLine.getDeliveryNote().getDeliveryNoteNo());
        goodsReceiptHeader.setHeaderText(GOODS_RECEIPT_HEADER_TEXT);
        goodsReceiptHeader.setAssignCodeGM(GOODS_RECEIPT_ASSIGN_CODE_GM);
        goodsReceiptHeader.setDeliveryNote(deliveryNoteLine.getDeliveryNote());

        GoodsReceiptLine goodsReceiptLine = new GoodsReceiptLine();

        goodsReceiptLine.setPlant(materialUserId);
        goodsReceiptLine.setMovementType(GOODS_RECEIPT_MOVEMENT_TYPE);
        goodsReceiptLine.setVendor(StringUtils.leftPad(deliveryNoteLine.getDeliveryNote().getSupplierId(), LEFT_PAD_VENDOR, '0'));
        goodsReceiptLine.setVendorMaterialNumber(deliveryNoteLine.getOrderLine().getPartNumber());
        goodsReceiptLine.setOrderReference(deliveryNoteLine.getDeliveryNote().getOrderNo());

        goodsReceiptLine.setQuantity(quantity);
        goodsReceiptLine.setStatus(GoodsReceiptLineStatus.RECEIVED);
        goodsReceiptLine.setIsoUnitOfMeasure(deliveryNoteLine.getOrderLine().getUnitOfMeasure());
        goodsReceiptLine.setDeliveryNoteLine(deliveryNoteLine);
        goodsReceiptLine.setGoodsReceiptHeader(goodsReceiptHeader);
        goodsReceiptLine.setMovementIndicator(MOVEMENT_INDICATOR);
        goodsReceiptHeader.getGoodsReceiptLines().add(deliveryNoteRepository.save(goodsReceiptLine));
        goodsReceiptHeader.setMessageStatus(MessageStatus.SENT);
        return deliveryNoteRepository.save(goodsReceiptHeader);
    }
    
    private static boolean isSAPSupportsGoodsMovementForCompany(String companyCode, CommonServices commonServices) {
        CompanyCode codeByCode = commonServices.findCompanyCodeByCode(companyCode);
        if (codeByCode != null) {
            return codeByCode.isSendGRtoSAP();
        } else {
            return false;
        }
    }
    
    public static void createSendGoodsReceiptExternal(String companyCode, DeliveryNoteLine deliveryNoteLine, Long approvedQuantity,
            GoodsReceiptSender goodsReceiptSender, DeliveryNoteRepository deliveryNoteRepository, CommonServices commonServices)
            throws GloriaApplicationException {
        if (companyCode != null) {
            if (approvedQuantity != null && approvedQuantity > 0) {
                GoodsReceiptHeader goodsReceiptHeader = ProcureTypeHelper.createGoodsReceipt(deliveryNoteLine.getDeliveryNoteLineOID(), approvedQuantity,
                                                                                             deliveryNoteRepository, commonServices);
                if (isSAPSupportsGoodsMovementForCompany(companyCode, commonServices)) {
                    GoodsReceiptHeaderTransformerDTO goodsReceiptHeaderDTO = MaterialTransformHelper.transformAsDTOForProxy(goodsReceiptHeader);
                    try {
                        goodsReceiptSender.sendGoodsReceipt(goodsReceiptHeaderDTO);
                    } catch (Exception e) {
                        throw new GloriaSystemException(e, "Goods Receipt Movement couldn't be sent.");
                    }
                } else {
                    LOGGER.info("GR for" + deliveryNoteLine.getOrderLine().getOrder().getOrderNo() + " is not sent to SAP MIV as company code " + companyCode
                            + " not supported!");
                }
            }
        }
    }
    
    public static void createSendGoodsReceiptInternal(DeliveryNoteLine deliveryNoteLine, Long approvedQuantity, DeliveryNoteRepository deliveryNoteRepository,
            CommonServices commonServices)
            throws GloriaApplicationException {
        if (approvedQuantity != null && approvedQuantity > 0) {
            ProcureTypeHelper.createGoodsReceipt(deliveryNoteLine.getDeliveryNoteLineOID(), approvedQuantity, deliveryNoteRepository, commonServices);
        }
    }
    
    public static void sendCancelGoodsReceipt(Long cancelledQuantity, GoodsReceiptHeader goodsReceiptHeader, GoodsReceiptSender goodsReceiptSender,
            CommonServices commonServices) throws GloriaApplicationException {
        if (cancelledQuantity != null && cancelledQuantity > 0) {
            String companyCode = goodsReceiptHeader.getCompanyCode();
            if (isSAPSupportsGoodsMovementForCompany(companyCode, commonServices)) {
                GoodsReceiptHeaderTransformerDTO goodsReceiptHeaderDTO = MaterialTransformHelper.transformAsDTOForProxy(goodsReceiptHeader, cancelledQuantity);
                try {
                    goodsReceiptSender.sendGoodsReceipt(goodsReceiptHeaderDTO);
                } catch (Exception e) {
                    throw new GloriaSystemException(e, "Cancel Goods Receipt Movement couldn't be sent.");
                }
            } else {
                LOGGER.info("Cancel-GR for" + goodsReceiptHeader.getDeliveryNote().getOrderNo() + " is not sent to SAP MIV as company code " + companyCode
                        + " not supported!");
            }
        }
    }
    
    public static boolean validateMandatoryFieldsForExternal(ProcureLine procureLine, boolean multipleProcure, boolean procuredParam)
            throws GloriaApplicationException {
        boolean procured = procuredParam;
        procured = ProcureTypeHelper.validateShipToAndPPSuffix(procureLine, multipleProcure, procured);
        procured = ProcureTypeHelper.validateMissingReqdSTADate(procureLine, multipleProcure, procured);
        procured = ProcureTypeHelper.validateMissingFinalWhSite(procureLine, multipleProcure, procured);
        procured = ProcureTypeHelper.validateMissingBuyerId(procureLine, multipleProcure, procured);
        procured = ProcureTypeHelper.validatePriceAndCurrency(procureLine.getMaxPrice(), procureLine.getCurrency(), multipleProcure, procured);
        return procured;
    }

    public static boolean validateMandatoryFieldsForInternal(ProcureLine procureLine, boolean multipleProcure, boolean procuredParam)
            throws GloriaApplicationException {
        boolean procured = procuredParam;
        procured = ProcureTypeHelper.validateShipToAndPPSuffix(procureLine, multipleProcure, procured);
        procured = ProcureTypeHelper.validateMissingReqdSTADate(procureLine, multipleProcure, procured);
        procured = ProcureTypeHelper.validateMissingFinalWhSite(procureLine, multipleProcure, procured);
        procured = ProcureTypeHelper.validateOrderStaDate(procureLine, multipleProcure, procured);
        procured = ProcureTypeHelper.validateMissingInternalSupplier(procureLine, multipleProcure, procured);
        procured = ProcureTypeHelper.validateMissingOrderNo(procureLine, multipleProcure, procured);
        procured = ProcureTypeHelper.validatePriceAndCurrency(procureLine.getUnitPrice(), procureLine.getCurrency(), multipleProcure, procured);
        return procured;
    }
    
    public static void createSendProcessPurchaseOrder(ProcureLine procureLine, Order order, String action, CommonServices commonServices,
            OrderSapRepository orderSapRepository, CompanyCodeRepository companyCodeRepository, ProcessPurchaseOrderSender processPurchaseOrderSender)
            throws GloriaApplicationException {
        String companyCode = procureLine.getFinanceHeader().getCompanyCode();
        if (isSAPSupportsProcessPurchaseOrderForCompany(companyCode, companyCodeRepository)) {
            OrderSap orderSap = orderSapRepository.findOrderSapByUniqueExtOrder(order.getOrderIdGps());
            ProcessPurchaseOrderDTO processPurchaseOrderDTO = createTransformProcessPurchaseOrderEntities(order, orderSap, action, commonServices,
                                                                                                          companyCodeRepository, orderSapRepository);
            try {
                processPurchaseOrderSender.sendProcessPurchaseOrder(processPurchaseOrderDTO);
            } catch (JAXBException e1) {
                throw new GloriaSystemException(e1, "process purchase order couldn't be sent.");
            }
        } else {
            LOGGER.info(order.getOrderNo() + " is not sent to SAP MIV as company code " + companyCode + " not supported!");
        }
    }
    
    private static boolean isSAPSupportsProcessPurchaseOrderForCompany(String companyCode, CompanyCodeRepository companyCodeRepository) {
        return companyCodeRepository.findCompanyCodeByCode(companyCode).isSendPOtoSAP();
    }
    
    public static ProcessPurchaseOrderDTO createTransformProcessPurchaseOrderEntities(Order order, OrderSap orderSap, String action,
            CommonServices commonServices, CompanyCodeRepository companyCodeRepository, OrderSapRepository orderSapRepository)
            throws GloriaApplicationException {
        if (orderSap == null) {
            orderSap = new OrderSap();
        }
        String companyCode = null;
        List<OrderLine> orderLines = order.getOrderLines();
        if (orderLines != null && !orderLines.isEmpty()) {
            OrderLine firstOrderLine = orderLines.get(0);
            ProcureLine procureLine = firstOrderLine.getProcureLine();
            if (procureLine != null) {
                FinanceHeader financeHeader = procureLine.getFinanceHeader();
                companyCode = financeHeader.getCompanyCode();
                orderSap.setCompanyCode(companyCode);
                String currencyCode = firstOrderLine.getCurrency();
                orderSap.setCurrency(currencyCode);
                Currency currency = commonServices.findCurrencyByCode(currencyCode);
                if (currency != null) {
                    orderSap.setSuppressDecimal(currency.isSuppressDecimal());
                }
            }
        }
        orderSap.setOrderType(ORDER_TYPE);
        orderSap.setVendor(StringUtils.leftPad(order.getSupplierId(), LEFT_PAD_VENDOR, '0'));
        orderSap.setPurchaseGroup(PURCHASE_GROUP);
        orderSap.setDocumentDate(order.getOrderDateTime());
        orderSap.setPurchaseType(PURCHASE_TYPE);
        orderSap.setUniqueExtOrder(order.getOrderIdGps());
        orderSap.setPurchaseOrganization(companyCodeRepository.findCompanyCodeByCode(companyCode).getSapPurchaseOrg());        
        orderSap.setMessageStatus(MessageStatus.SENT);

        List<OrderSapLine> orderSapLines = createOrderSapLines(orderLines, order, orderSap, companyCode, action, companyCodeRepository);

        orderSap.getOrderSapLines().addAll(orderSapLines);

        orderSapRepository.save(orderSap);

        return MaterialTransformHelper.prepareProcessPurchaseOrderDTO(orderSapLines, action);
    }

    private static List<OrderSapLine> createOrderSapLines(List<OrderLine> orderLines, Order order, OrderSap orderSap, String companyCode, String action,
            CompanyCodeRepository companyCodeRepository) {
        List<OrderSapLine> orderSapLines = new ArrayList<OrderSapLine>();

        // reorder orderlines "asc" to maintain proper re-creating of "sequence[purchaseOrderItem]", during GPS ammendment.
        Collections.sort(orderLines, new Comparator<OrderLine>() {
            @Override
            public int compare(OrderLine o1, OrderLine o2) {
                return Utils.compare(o1.getOrderLineOID(), o2.getOrderLineOID());
            }
        });

        long sequence = 1;
        for (OrderLine orderLine : orderLines) {
            if (!isOrderLinePaid(orderLine)) {
                OrderSapLine orderSapLine = new OrderSapLine();
                orderSapLine.setOrderSap(orderSap);
                orderSapLine.setPurchaseOrderitem(sequence);
                if (orderLine.getStatus().isCancelled(orderLine)) {
                    orderSapLine.setAction(ACTION_CHANGE);
                    orderSapLine.setCancelDate(order.getOrderDateTime());
                } else {
                    orderSapLine.setAction(action);
                }
                orderSapLine.setOrderReference(order.getOrderNo());
                orderSapLine.setPartNumber(orderLine.getPartNumber());
                orderSapLine.setShortText(orderLine.getPartName());
                orderSapLine.setPlant(order.getMaterialUserId());
                orderSapLine.setMaterialGroup(MATERIAL_GROUP);

                OrderLineVersion orderLineVersion = orderLine.getCurrent();
                orderSapLine.setOrderLineVersion(orderLineVersion);
                orderSapLine.setQuantity(orderLine.getPossibleToReceiveQuantity() - orderLine.getPaidQuantity());
                if (orderLineVersion.getUnitPrice() > 0) {
                    orderSapLine.setNetPrice(orderLineVersion.getUnitPrice());
                }
                if (orderLineVersion.getPriceType() != null) {
                    orderSapLine.setEstimatedPriceIndicator(PriceType.Estimated == orderLineVersion.getPriceType());
                }
                orderSapLine.setPriceUnit(String.valueOf(orderLineVersion.getPerQuantity()));
                orderSapLine.setCurrentBuyer(StringUtils.leftPad(orderLineVersion.getBuyerId(), LEFT_PAD_CURRENTBUYER, '0'));

                orderSapLine.setIsoPurchaseOrderUnit(orderLine.getUnitOfMeasure());
                orderSapLine.setIsoOrderPriceUnit(orderLine.getUnitOfMeasure());
                orderSapLine.setTaxCode(TAX_CODE);
                orderSapLine.setAccountAssignmentCategory(ACCOUNT_ASSIGNMENT_CATEGORY);
                orderSapLine.setUnlimitedDeliveryIndicator(UNLIMITED_DELIVERY_INDICATOR);
                orderSapLine.setGrIndicator(GR_INDICATOR);
                orderSapLine.setNonValuedGrIndicator(NON_VALUED_GR_INDICATOR);
                orderSapLine.setIrIndicator(IR_INDICATOR);
                orderSapLine.setAcknowledgementNumber(order.getOrderNo());
                orderSapLine.setPurchaseRequisitionNumber(companyCodeRepository.findCompanyCodeByCode(companyCode).getSapQuantityBlockReceiverId());

                orderSapLine.setOrderSapAccounts(createOrderSapAccounts(orderLine.getProcureLine(), orderSapLine));
                orderSapLine.setOrderSapSchedules(createOrderSapSchedules(order, orderSapLine));
                orderSapLines.add(orderSapLine);
                sequence++;
            }
        }
        return orderSapLines;
    }
    
    private static boolean isOrderLinePaid(OrderLine orderLine) {
        if (orderLine.isOrderlinePaidOnMigration()) {
            return true;
        }
        return false;
    }

    private static List<OrderSapSchedule> createOrderSapSchedules(Order order, OrderSapLine orderSapLine) {
        List<OrderSapSchedule> orderSapSchedules = new ArrayList<OrderSapSchedule>();
        OrderSapSchedule orderSapSchedule = new OrderSapSchedule();
        orderSapSchedule.setOrderSapLine(orderSapLine);
        orderSapSchedule.setCategoryOfDeliveryDate(CATEGORY_OF_DELIVERY_DATE);
        orderSapSchedule.setDeliveryDate(order.getOrderDateTime());
        orderSapSchedules.add(orderSapSchedule);
        return orderSapSchedules;
    }

    private static List<OrderSapAccounts> createOrderSapAccounts(ProcureLine procureLine, OrderSapLine orderSapLine) {
        List<OrderSapAccounts> orderSapAccounts = new ArrayList<OrderSapAccounts>();
        OrderSapAccounts ordSapAccounts = new OrderSapAccounts();
        // set sequenceGenerator
        ordSapAccounts.setSequence(1);
        if (procureLine != null) {
            FinanceHeader financeHeader = procureLine.getFinanceHeader();
            ordSapAccounts.setGeneralLedgerAccount(StringUtils.leftPad(financeHeader.getGlAccount(), LEFT_PAD_GENERALLEDGER, '0'));
            String costCenter = financeHeader.getCostCenter();
            if (StringUtils.isNumeric(costCenter)) {
                ordSapAccounts.setCostCenter(StringUtils.leftPad(costCenter, LEFT_PAD_COSTCENTER, '0'));
            } else {
                ordSapAccounts.setCostCenter(costCenter);
            }
            ordSapAccounts.setWbsElement(financeHeader.getWbsCode());
        }
        ordSapAccounts.setOrderSapLine(orderSapLine);
        orderSapAccounts.add(ordSapAccounts);
        return orderSapAccounts;
    }
    
    public static Material cloneMaterial(Material material) {
        Material clonedMaterial = new Material();
        MaterialHeader materialHeader = material.getMaterialHeader();
        if (materialHeader != null) {
            clonedMaterial.setAdd(materialHeader.getAccepted().getChangeId());
        }
        clonedMaterial.setCharacteristics(material.getCharacteristics());
        clonedMaterial.setCreatedDate(DateUtil.getCurrentUTCDateTime());
        clonedMaterial.setDemarcation(material.getDemarcation());
        clonedMaterial.setDesignResponsible(material.getDesignResponsible());
        
        FinanceHeader financeHeader = material.getFinanceHeader();
        if (financeHeader != null) {
            clonedMaterial.setFinanceHeader(cloneFinanceHeader(financeHeader));
        }
        
        clonedMaterial.setFunctionGroup(material.getFunctionGroup());
        clonedMaterial.setItemToVariantLinkId(material.getItemToVariantLinkId());
        clonedMaterial.setLinkFunctionGroup(material.getLinkFunctionGroup());
        clonedMaterial.setLinkFUnctionGroupSuffix(material.getLinkFUnctionGroupSuffix());
        clonedMaterial.setMailFormId(material.getMailFormId());
        clonedMaterial.setMaterialHeader(materialHeader);
        clonedMaterial.setMtrlRequestVersionAccepted(material.getMtrlRequestVersionAccepted());
        clonedMaterial.setReceiver(material.getReceiver());
        clonedMaterial.setMaterialType(material.getMaterialType());
        clonedMaterial.setMigrated(material.isMigrated());
        clonedMaterial.setModularHarness(material.getModularHarness());
        clonedMaterial.setObjectNumber(material.getObjectNumber());
        clonedMaterial.setOrderLine(material.getOrderLine());
        clonedMaterial.setOrderNo(material.getOrderNo());
        clonedMaterial.setPartAffiliation(material.getPartAffiliation());
        clonedMaterial.setPartModification(material.getPartModification());
        clonedMaterial.setPartName(material.getPartName());
        clonedMaterial.setPartNumber(material.getPartNumber());
        clonedMaterial.setPartVersion(material.getPartVersion());
        clonedMaterial.setPartVersionOriginal(material.getPartVersionOriginal());        
        clonedMaterial.setProcureLinkId(material.getProcureLinkId());
        clonedMaterial.setProductClass(material.getProductClass());
        clonedMaterial.setRefAssemblyPartNo(material.getRefAssemblyPartNo());
        clonedMaterial.setRefAssemblyPartVersion(material.getRefAssemblyPartVersion());
        clonedMaterial.setRequiredStaDate(material.getRequiredStaDate());
        clonedMaterial.setStatus(material.getStatus());
        clonedMaterial.setUnitOfMeasure(material.getUnitOfMeasure());        
        return clonedMaterial;
    }

    public static FinanceHeader cloneFinanceHeader(FinanceHeader financeHeader) {
        FinanceHeader clonedFinanceHeader = new FinanceHeader();
        clonedFinanceHeader.setCompanyCode(financeHeader.getCompanyCode());
        clonedFinanceHeader.setCostCenter(financeHeader.getCostCenter());
        clonedFinanceHeader.setGlAccount(financeHeader.getGlAccount());
        clonedFinanceHeader.setInternalOrderNoSAP(financeHeader.getInternalOrderNoSAP());
        clonedFinanceHeader.setProjectId(financeHeader.getProjectId());
        clonedFinanceHeader.setWbsCode(financeHeader.getWbsCode());
        return clonedFinanceHeader;
    }
    
    public static boolean checkHasOnlyFromStockLine(Material material) {
        boolean hasOnlyFromStockLine = true;
        if (material.getMaterialLine() != null && !material.getMaterialLine().isEmpty()) {
            for (MaterialLine materialLine : material.getMaterialLine()) {
                if (!materialLine.getProcureType().isFromStock()) {
                    hasOnlyFromStockLine = false;
                }
            }
        }
        return hasOnlyFromStockLine;
    }

    public static String getActionForPartialFromStock(Material material) {
        boolean hasOnlyFromStockLine = ProcureTypeHelper.checkHasOnlyFromStockLine(material);
        if (hasOnlyFromStockLine) {
            return GloriaParams.ADDITIONAL;
        } else {
            return null;
        }
    }
    
    public static void trace(String action, String actionDetail, ProcureLine procureLine, UserDTO userDTO, TraceabilityRepository traceabilityRepository, String traceConstant) {
        List<Material> materialList = procureLine.getMaterials();
        if (materialList != null) {
            for (Material material : materialList) {
                List<MaterialLine> materialLineList = material.getMaterialLine();
                if (materialLineList != null && userDTO != null) {
                    MaterialLine materialLine = materialLineList.get(0);
                    MaterialLineStatusHelper.createTraceabilityLog(materialLine, traceabilityRepository, action, actionDetail, userDTO.getId(),
                                                                   userDTO.getUserName(), traceConstant);
                }
            }
        }
    }
}
