package com.volvo.gloria.procurematerial.util.migration.b.beans;

import static com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper.getRelatedOrderMigrationDTO;
import static com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper.isSendGRtoSAP;
import static com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper.isSendPOtoSAP;
import static com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper.transformToDeliveryNote;
import static com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper.transformToFinanceHeader;
import static com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper.transformToGoodsReceiptHeaderEntity;
import static com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper.transformToMaterialEntity;
import static com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper.transformToMaterialHeaderEntity;
import static com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper.transformToOrderEntity;
import static com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper.transformToOrderLineLog;
import static com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper.transformToOrderLogs;
import static com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper.transformToOrderSapEntity;
import static com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper.transformToProcureLine;
import static com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper.transformToRequisition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.b.beans.MaterialServicesHelper;
import com.volvo.gloria.procurematerial.c.ChangeType;
import com.volvo.gloria.procurematerial.d.entities.ChangeId;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNote;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.FinanceHeader;
import com.volvo.gloria.procurematerial.d.entities.GoodsReceiptHeader;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeaderVersion;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.Order;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLineLog;
import com.volvo.gloria.procurematerial.d.entities.OrderLog;
import com.volvo.gloria.procurematerial.d.entities.OrderSap;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.Requisition;
import com.volvo.gloria.procurematerial.d.entities.status.changeid.ChangeIdStatus;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.orderLine.OrderLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineStatus;
import com.volvo.gloria.procurematerial.d.type.internalexternal.InternalExternal;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderSapRepository;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.util.migration.c.dto.OrderMigrationDTO;
import com.volvo.gloria.procurematerial.util.migration.c.validation.migrationsite.MigrationSiteCode;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.warehouse.b.WarehouseServices;
import com.volvo.gloria.warehouse.c.ZoneType;
import com.volvo.gloria.warehouse.d.entities.BinLocation;
import com.volvo.gloria.warehouse.d.entities.Placement;
import com.volvo.gloria.warehouse.repositories.b.WarehouseRepository;
import com.volvo.jvs.runtime.platform.ContainerManaged;

@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class PerformOrderMigrationServiceBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(PerformOrderMigrationServiceBean.class);

    @Inject
    private MaterialHeaderRepository requestHeaderRepository;
    @Inject
    private OrderRepository orderRepository;
    @Inject
    private ProcureLineRepository procureLineRepository;
    @Inject
    private OrderSapRepository orderSapRepo;
    @Inject
    private DeliveryNoteRepository deliveryNoteRepo;
    @Inject
    private WarehouseRepository warehouseRepository;
    @Inject
    private MaterialServices materialServices;
    @Inject
    private WarehouseServices warehouseServices;
    @Inject
    private CommonServices commonServices;

    private StringBuilder statusText = new StringBuilder();
    private int totalOrderLinesInput = 0, totalOrderLinesCreated = 0;
    private int totalOrderLinesPlacedCreated = 0, totalOrderSapLinesPlaced = 0, totalPlacedOrderLinesForGoodsReceipt = 0;
    private int totalOrderLinesRPCreated = 0, totalOrderLinesRPSapLines = 0, totalOrderLinesRPGoodsReceipt = 0;
    private int totalOrderLinesReceivedCreated = 0, totalOrderLinesReceivedSapLines = 0, totalOrderLinesReceivedGoodsReceipt = 0;
    private static final String NOT_PAID_ORDER = "NO";

    public void performOpenOrderMigration(List<OrderMigrationDTO> sameOrderNoDtos, boolean isLastItem) throws GloriaApplicationException {
        String companyCode = sameOrderNoDtos.get(0).getCompanyCode();
        Order order = transformToOrderEntity(sameOrderNoDtos, commonServices);
        List<OrderLine> orderLines = order.getOrderLines();
        order.setOrderLines(null);
        order.setMigrated(true);
        boolean firstUpdate = true;
        totalOrderLinesInput += orderLines.size();
        for (OrderLine orderLine : orderLines) {
            orderLine.setOrder(null);
            OrderMigrationDTO dto = getRelatedOrderMigrationDTO(sameOrderNoDtos, orderLine);

            if (firstUpdate) {
                orderRepository.save(order);
                orderRepository.flush();
                firstUpdate = false;
            }
            MaterialType materialType = MaterialType.USAGE;
            materialType = overrideMaterialType(dto, materialType);
            if (OrderLineStatus.PLACED == orderLine.getStatus()) {
                MaterialLineStatus materialLineStatus = getPlacedStatus(order);

                createProcureMaterialInfoForPlaced(order, orderLine, dto, materialType, materialLineStatus);

                totalOrderLinesPlacedCreated++;
                totalOrderLinesCreated++;

            } else {
                try {
                    Material whMaterial = dto.getMaterial();
                    long shippingQty = 0;

                    ProcureLineStatus procureLineStatus = null;
                    if (orderLine.getStatus().equals(OrderLineStatus.COMPLETED)) {
                        procureLineStatus = ProcureLineStatus.RECEIVED;
                    } else {
                        procureLineStatus = ProcureLineStatus.RECEIVED_PARTLY;
                    }
                    if (whMaterial != null) {
                        // When there is matched material in warehouse data
                        handleMatchedMaterial(order, orderLine, dto, materialType, whMaterial, shippingQty, procureLineStatus);
                    } else {
                        // if no matching material - create shipped materials
                        shippingQty = dto.getReceivedQuantity();
                        createShippedMaterials(order, orderLine, dto, materialType, shippingQty, 0, procureLineStatus);
                    }

                    if (!StringUtils.isEmpty(dto.getOrderInformation())) {
                        OrderLog orderLog = transformToOrderLogs(dto, order);
                        order.getOrderLog().add(orderLog);
                        orderLog.setOrders(order);
                        orderRepository.save(orderLog);
                    }

                    if (!StringUtils.isEmpty(dto.getObjectInformation())) {
                        OrderLineLog orderLineLog = transformToOrderLineLog(dto, orderLine);
                        orderLine.getOrderLineLog().add(orderLineLog);
                        orderLineLog.setOrderLine(orderLine);
                        orderRepository.save(orderLineLog);
                    }

                    ProcureLine procureLine = orderLine.getProcureLine();
                    Requisition requisition = transformToRequisition(dto, procureLine, commonServices);
                    procureLineRepository.addRequisition(requisition);
                    procureLineRepository.flush();
                    requisition.setRequisitionId(orderLine.getRequisitionId());
                    procureLineRepository.updateRequisition(requisition);
                    procureLine.setRequisition(requisition);

                    orderRepository.flush();
                    totalOrderLinesCreated++;
                    dto.setMigrated(true);

                    // Populate GM to SAP - unless OrderLine is Received
                    if (!StringUtils.isEmpty(dto.getOrderLinePaid()) && dto.getOrderLinePaid().trim().equals(NOT_PAID_ORDER)) {
                        populateGoodsReceipt(companyCode, order, orderLine, dto);
                    }
                } catch (Exception e) {
                    log(dto.getOrderNumber() + ": failed during migration | Reason: " + e.getMessage());
                }
            }
        }

        // Populate Process purchase order to SAP.
        if (InternalExternal.EXTERNAL == order.getInternalExternal() && isSendPOtoSAP(companyCode)) {
            OrderSap orderSap = transformToOrderSapEntity(sameOrderNoDtos, order, orderLines, companyCode, totalOrderSapLinesPlaced, totalOrderLinesRPSapLines,
                                                          totalOrderLinesReceivedSapLines);
            if (orderSap.getOrderSapLines() != null && !orderSap.getOrderSapLines().isEmpty()) {
                orderSapRepo.save(orderSap);
            }
        }
        logCounters(isLastItem);
    }

    private void populateGoodsReceipt(String companyCode, Order order, OrderLine orderLine, OrderMigrationDTO dto) {
        long goodsMovementQty = dto.getReceivedQuantity() - dto.getPaidQuantity();
        if (goodsMovementQty > 0) {
            GoodsReceiptHeader goodsReceiptHeader = transformToGoodsReceiptHeaderEntity(dto, companyCode, goodsMovementQty, order, isSendGRtoSAP(companyCode));
            // set relation to deliveryNote
            DeliveryNoteLine deliveryNoteLine = orderLine.getDeliveryNoteLines().get(0);
            if (deliveryNoteLine != null) {
                goodsReceiptHeader.setDeliveryNote(deliveryNoteLine.getDeliveryNote());
                goodsReceiptHeader.getGoodsReceiptLines().get(0).setDeliveryNoteLine(deliveryNoteLine);
            }
            deliveryNoteRepo.save(goodsReceiptHeader);
            if (orderLine.getStatus() == OrderLineStatus.RECEIVED_PARTLY && InternalExternal.EXTERNAL == order.getInternalExternal()) {
                totalOrderLinesRPGoodsReceipt++;
            } else if (orderLine.getStatus() == OrderLineStatus.COMPLETED && InternalExternal.EXTERNAL == order.getInternalExternal()) {
                totalOrderLinesReceivedGoodsReceipt++;
            }
            dto.setSendGM(true);
        }
    }

    private void handleMatchedMaterial(Order order, OrderLine orderLine, OrderMigrationDTO dto, MaterialType materialType, Material whMaterial,
            long shippingQty, ProcureLineStatus procureLineStatus) throws GloriaApplicationException {
        long storedQty;
        FinanceHeader financeHeader = whMaterial.getFinanceHeader();
        ProcureLine procureLine = whMaterial.getProcureLine();
        procureLine.setProcureDate(dto.getProcureDate());
        // lazy load
        procureLine.getMaterials();
        MaterialLine materialLine = whMaterial.getMaterialLine().get(0);
        DeliveryNoteLine deliveryNoteLine = materialLine.getDeliveryNoteLine();

        BinLocation binlocation = warehouseRepository.findBinLocationByCode(materialLine.getBinLocationCode(), materialLine.getWhSiteId());
        if (whMaterial.getQuantity() > dto.getReceivedQuantity()) {
            // If warehouse stored qty is more than Received, split the material to have the extra qty as Additional/Released
            storedQty = dto.getReceivedQuantity();
            materialLine.setQuantity(whMaterial.getQuantity() - storedQty);
            orderLine.setProcureLine(procureLine);
            procureLine.setStatus(procureLineStatus);
            orderLine.setOrder(order);
            orderRepository.saveOrderLine(orderLine);

            deliveryNoteLine.setReferenceIds(dto.getReferenceIds());
            deliveryNoteLine.setOrderLine(orderLine);
            orderLine.getDeliveryNoteLines().add(deliveryNoteLine);
            updateMaterial(orderLine, dto, whMaterial);
            // updatePlacement
            Placement placement = warehouseRepository.findPlacementByID(materialLine.getPlacementOID());
            if (placement != null) {
                MaterialServicesHelper.removePlacement(materialLine, warehouseServices);
                if (binlocation != null) {
                    materialServices.createPlacement(binlocation, materialLine);
                }
            }
        } else {
            // If warehouse stored qty is less than Received, delete the exisisting material and create new materials according to TestObjects
            storedQty = whMaterial.getQuantity();
            shippingQty = dto.getReceivedQuantity() - whMaterial.getQuantity();
            Material existingMaterial = requestHeaderRepository.findMaterialById(whMaterial.getMaterialOID());
            if (existingMaterial != null) {
                Placement placement = warehouseRepository.findPlacementByID(materialLine.getPlacementOID());
                requestHeaderRepository.deleteMaterial(existingMaterial);
                if (placement != null) {
                    MaterialServicesHelper.removePlacement(materialLine, warehouseServices);
                }
            }
            financeHeader = transformToFinanceHeader(dto, materialType);
            procureLine = transformToProcureLine(dto, financeHeader, procureLineStatus);
            procureLine.setRequisitionId(orderLine.getRequisitionId());

            procureLineRepository.save(procureLine);
            orderLine.setProcureLine(procureLine);
            orderLine.setOrder(order);
            orderRepository.saveOrderLine(orderLine);

            DeliveryNote deliveryNote = transformToDeliveryNote(dto, binlocation, procureLine);
            deliveryNoteLine = deliveryNote.getDeliveryNoteLine().get(0);
            deliveryNoteLine.setOrderLine(orderLine);
            orderLine.setFirst(deliveryNoteLine);
            orderLine.getDeliveryNoteLines().add(deliveryNoteLine);
            deliveryNoteRepo.save(deliveryNote);

        }
        // create material headers and materials based on test object for MaterialType = Usage
        if (OrderLineStatus.RECEIVED_PARTLY == orderLine.getStatus()) {
            saveTestObjectsAndMaterials(orderLine, dto, materialType, financeHeader, procureLine, order, MaterialLineStatus.STORED, true, binlocation,
                                        storedQty, shippingQty, deliveryNoteLine);
            totalOrderLinesRPCreated++;
        } else {
            saveTestObjectsAndMaterials(orderLine, dto, materialType, financeHeader, procureLine, order, MaterialLineStatus.STORED, false, binlocation,
                                        storedQty, shippingQty, deliveryNoteLine);

            totalOrderLinesReceivedCreated++;
        }
    }

    public void logCounters(boolean isLastItem) {
        if (isLastItem) {
            log("Total  OrderLines input -" + totalOrderLinesInput);
            log("Total  OrderLines created -" + totalOrderLinesCreated);

            log("Total  OrderLines (placed) created -" + totalOrderLinesPlacedCreated);
            log("Total  OrderLines (placed) order_sap_lines -" + totalOrderSapLinesPlaced);
            log("Total  OrderLines (placed) goods_receipt -" + totalPlacedOrderLinesForGoodsReceipt);

            log("Total  OrderLines (received_partly) created -" + totalOrderLinesRPCreated);
            log("Total  OrderLines (received_partly) order_sap_lines -" + totalOrderLinesRPSapLines);
            log("Total  OrderLines (received_partly) goods_receipt -" + totalOrderLinesRPGoodsReceipt);

            log("Total  OrderLines (received) created -" + totalOrderLinesReceivedCreated);
            log("Total  OrderLines (received) order_sap_lines -" + totalOrderLinesReceivedSapLines);
            log("Total  OrderLines (received) goods_receipt -" + totalOrderLinesReceivedGoodsReceipt);

            reInitializeCounters();

        }
    }

    public void createShippedMaterials(Order order, OrderLine orderLine, OrderMigrationDTO dto, MaterialType materialType, long shippingQty, long usageQty,
            ProcureLineStatus procureLineStatus) throws GloriaApplicationException {
        FinanceHeader financeHeader = transformToFinanceHeader(dto, materialType);
        ProcureLine procureLine = transformToProcureLine(dto, financeHeader, procureLineStatus);
        procureLine.setRequisitionId(orderLine.getRequisitionId());
        procureLineRepository.save(procureLine);

        orderLine.setProcureLine(procureLine);
        orderLine.setOrder(order);
        orderRepository.saveOrderLine(orderLine);

        DeliveryNote deliveryNote = transformToDeliveryNote(dto, null, procureLine);
        DeliveryNoteLine deliveryNoteLine = deliveryNote.getDeliveryNoteLine().get(0);
        deliveryNoteLine.setOrderLine(orderLine);
        orderLine.setFirst(deliveryNoteLine);
        orderLine.getDeliveryNoteLines().add(deliveryNoteLine);
        deliveryNoteRepo.save(deliveryNote);
        saveTestObjectsAndMaterials(orderLine, dto, materialType, financeHeader, procureLine, order, MaterialLineStatus.SHIPPED, true, null, shippingQty,
                                    usageQty, deliveryNoteLine);
    }

    public MaterialType overrideMaterialType(OrderMigrationDTO dto, MaterialType materialType) {
        if (MigrationSiteCode.valueOf(dto.getCompanyCode()).getProjectsToBeReleased().contains(dto.getProject())) {
            return MaterialType.RELEASED;
        }
        return materialType;
    }

    public void updateMaterial(OrderLine orderLine, OrderMigrationDTO dto, Material whMaterial) throws GloriaApplicationException {
        FinanceHeader financeHeader = whMaterial.getFinanceHeader();
        financeHeader.setGlAccount(dto.getGlAccount());
        financeHeader.setWbsCode(dto.getWbsElement());
        financeHeader.setCostCenter(dto.getCostCenter());
        whMaterial.setPartVersionOriginal(dto.getPartVersion());
        whMaterial.setUnitOfMeasure(dto.getUnitOfMeasure());
        whMaterial.setObjectNumber(dto.getProject());
        whMaterial.setOrderLine(orderLine);
        if (whMaterial.getMaterialType() != null && whMaterial.getMaterialType() == MaterialType.USAGE) {
            whMaterial.setMtrlRequestVersionAccepted("Migrated");
        }

        ProcureLine procureLine = whMaterial.getProcureLine();
        if (procureLine.getMaterials() == null || procureLine.getMaterials().isEmpty()) {
            List<Material> materials = new ArrayList<Material>();
            materials.add(whMaterial);
            procureLine.setMaterials(materials);
        }
        procureLine.setRequisitionId(orderLine.getRequisitionId());
        procureLine.setMaterialControllerId(dto.getIssuerId());
        procureLine.setMaterialControllerName(dto.getIssuerName());
        procureLine.setUnitPrice(dto.getPrice());

        MaterialLine materialLine = whMaterial.getMaterialLine().get(0);
        DeliveryNoteLine deliveryNoteLine = materialLine.getDeliveryNoteLine();
        DeliveryNote deliveryNote = deliveryNoteLine.getDeliveryNote();

        Date firstReceivalDate = dto.getMinReceivedDate();
        deliveryNote.setDeliveryNoteDate(firstReceivalDate);
        deliveryNote.setSupplierId(dto.getSupplierCode());
        deliveryNote.setSupplierName(dto.getSupplierName());
        deliveryNote.setMaterialUserId(dto.getMaterialUserid());
        deliveryNoteLine.setPossibleToReceiveQty(dto.getPossibleToReceiveQuantity());
        deliveryNoteLine.setReceivedDateTime(firstReceivalDate);

        deliveryNoteRepo.save(deliveryNote);

        requestHeaderRepository.updateMaterial(whMaterial);
    }

    public void createProcureMaterialInfoForPlaced(Order order, OrderLine orderLine, OrderMigrationDTO dto, MaterialType materialType,
            MaterialLineStatus materialLineStatus) throws GloriaApplicationException {
        // financeHeader and ProcureLine
        FinanceHeader financeHeader = transformToFinanceHeader(dto, materialType);

        ProcureLine procureLine = transformToProcureLine(dto, financeHeader, ProcureLineStatus.PLACED);
        procureLine.setRequisitionId(orderLine.getRequisitionId());
        procureLineRepository.save(procureLine);

        orderLine.setProcureLine(procureLine);
        orderLine.setOrder(order);
        orderRepository.saveOrderLine(orderLine);

        if (!StringUtils.isEmpty(dto.getOrderInformation())) {
            OrderLog orderLog = transformToOrderLogs(dto, order);
            order.getOrderLog().add(orderLog);
            orderLog.setOrders(order);
            orderRepository.save(orderLog);
        }

        if (dto.getObjectInformation() != null) {
            OrderLineLog orderLineLog = transformToOrderLineLog(dto, orderLine);
            orderLine.getOrderLineLog().add(orderLineLog);
            orderLineLog.setOrderLine(orderLine);
            orderRepository.save(orderLineLog);
        }

        // Materials
        saveTestObjectsAndMaterials(orderLine, dto, materialType, financeHeader, procureLine, order, materialLineStatus, false, null, 0, 0, null);

        Requisition requisition = transformToRequisition(dto, orderLine.getProcureLine(), commonServices);
        procureLineRepository.addRequisition(requisition);

        requestHeaderRepository.flush();
        procureLineRepository.flush();
        orderRepository.flush();
        dto.setMigrated(true);
        requisition.setRequisitionId(orderLine.getRequisitionId());
        procureLineRepository.updateRequisition(requisition);
        procureLine.setRequisition(requisition);
    }

    // TODO : REFACTORING PENDING
    public void saveTestObjectsAndMaterials(OrderLine orderLine, OrderMigrationDTO dto, MaterialType materialType, FinanceHeader financeHeader,
            ProcureLine procureLine, Order order, MaterialLineStatus materialLineStatus, boolean checkToReceive, BinLocation binlocation,
            long storedQtyParameter, long shippingQty, DeliveryNoteLine deliveryNoteLine) throws GloriaApplicationException {
        long storedQty = storedQtyParameter;
        long toReceiveQty = 0;
        if (checkToReceive) {
            toReceiveQty = dto.getPossibleToReceiveQuantity() - dto.getReceivedQuantity();

        }
        List<String> testObjects = dto.getTestObjectsQty();
        if (testObjects != null && !testObjects.isEmpty()) {
            for (String testObject : testObjects) {
                long testObjectQuantity = new Long(StringUtils.substringAfter(testObject, "@")).longValue();

                if (toReceiveQty > 0) {
                    // RECEIVED_PARTLY CASE
                    MaterialLineStatus materialLineStatusForReceive = getPlacedStatus(order);
                    if (testObjectQuantity <= toReceiveQty) {
                        toReceiveQty = toReceiveQty - testObjectQuantity;
                        createMaterials(orderLine, dto, financeHeader, procureLine, testObject, testObjectQuantity, materialType, materialLineStatusForReceive,
                                        binlocation, deliveryNoteLine);
                        testObjectQuantity = 0;
                    } else {
                        testObjectQuantity = testObjectQuantity - toReceiveQty;
                        createMaterials(orderLine, dto, financeHeader, procureLine, testObject, toReceiveQty, materialType, materialLineStatusForReceive,
                                        binlocation, deliveryNoteLine);

                        if (testObjectQuantity <= storedQty) {
                            createMaterials(orderLine, dto, financeHeader, procureLine, testObject, testObjectQuantity, materialType, materialLineStatus,
                                            binlocation, deliveryNoteLine);
                            storedQty = storedQty - testObjectQuantity;
                            testObjectQuantity = 0;
                        } else {
                            testObjectQuantity = testObjectQuantity - storedQty;
                            createMaterials(orderLine, dto, financeHeader, procureLine, testObject, storedQty, materialType, materialLineStatus, binlocation,
                                            deliveryNoteLine);
                            storedQty = 0;
                        }

                        toReceiveQty = 0;
                    }
                } else if (storedQty > 0) {
                    // For RECEIVED status , Material found in warehouse data with storage qty

                    if (testObjectQuantity <= storedQty) {
                        createMaterials(orderLine, dto, financeHeader, procureLine, testObject, testObjectQuantity, materialType, materialLineStatus,
                                        binlocation, deliveryNoteLine);
                        storedQty = storedQty - testObjectQuantity;
                        testObjectQuantity = 0;
                    } else {
                        testObjectQuantity = testObjectQuantity - storedQty;
                        createMaterials(orderLine, dto, financeHeader, procureLine, testObject, storedQty, materialType, materialLineStatus, binlocation,
                                        deliveryNoteLine);
                        storedQty = 0;
                    }

                } else if (materialLineStatus.equals(MaterialLineStatus.ORDER_PLACED_EXTERNAL)
                        || materialLineStatus.equals(MaterialLineStatus.ORDER_PLACED_INTERNAL)) {
                    // FOR ORDER_PLACED status
                    createMaterials(orderLine, dto, financeHeader, procureLine, testObject, testObjectQuantity, materialType, materialLineStatus, binlocation,
                                    deliveryNoteLine);
                    testObjectQuantity = 0;
                }

                // If the testObjectQuantity is split during above conditions then the below loop gets executed for remaining testObjectQuantity.
                // And if Received order has no material found in the warehouse data or partial of material qty is missing in warehouse data.
                if (testObjectQuantity > 0 && shippingQty > 0 && storedQty == 0) {
                    materialLineStatus = MaterialLineStatus.SHIPPED;
                    shippingQty = shippingQty - testObjectQuantity;
                    createMaterials(orderLine, dto, financeHeader, procureLine, testObject, testObjectQuantity, materialType, materialLineStatus, binlocation,
                                    deliveryNoteLine);
                }

            }
        }

        // manage over deliveries and shipped deliveries.
        manageOverDelivery(orderLine, dto, materialType, financeHeader, procureLine, materialLineStatus, binlocation, shippingQty, deliveryNoteLine,
                           toReceiveQty, order);
    }

    private MaterialLineStatus getPlacedStatus(Order order) {
        MaterialLineStatus materialLineStatusForReceive = MaterialLineStatus.ORDER_PLACED_INTERNAL;
        if (order.getInternalExternal() == InternalExternal.EXTERNAL) {
            materialLineStatusForReceive = MaterialLineStatus.ORDER_PLACED_EXTERNAL;
        }
        return materialLineStatusForReceive;
    }

    private void manageOverDelivery(OrderLine orderLine, OrderMigrationDTO dto, MaterialType materialType, FinanceHeader financeHeader,
            ProcureLine procureLine, MaterialLineStatus materialLineStatus, BinLocation binlocation, long shippingQty, DeliveryNoteLine deliveryNoteLine,
            long toReceiveQty, Order order) throws GloriaApplicationException {
        long additionalQty = orderLine.getStatus().getAdditionalQty(dto);

        materialType = MaterialType.ADDITIONAL_USAGE;
        materialType = overrideMaterialType(dto, materialType);

        additionalQty = additionalQty - shippingQty;
        if (toReceiveQty > 0) {
            MaterialLineStatus materialLineStatusForReceive = getPlacedStatus(order);
            createMaterials(orderLine, dto, financeHeader, procureLine, null, toReceiveQty, materialType, materialLineStatusForReceive, binlocation,
                            deliveryNoteLine);
            additionalQty = additionalQty - toReceiveQty;
        }
        if (additionalQty > 0) {
            createMaterials(orderLine, dto, financeHeader, procureLine, null, additionalQty, materialType, materialLineStatus, binlocation, deliveryNoteLine);
        }
        if (shippingQty > 0) {
            materialLineStatus = MaterialLineStatus.SHIPPED;
            createMaterials(orderLine, dto, financeHeader, procureLine, null, shippingQty, materialType, materialLineStatus, binlocation, deliveryNoteLine);
        }
    }

    public void reInitializeCounters() {
        totalOrderLinesInput = 0;
        totalOrderLinesCreated = 0;
        totalOrderLinesPlacedCreated = 0;
        totalOrderSapLinesPlaced = 0;
        totalPlacedOrderLinesForGoodsReceipt = 0;
        totalOrderLinesRPCreated = 0;
        totalOrderLinesRPSapLines = 0;
        totalOrderLinesRPGoodsReceipt = 0;
        totalOrderLinesReceivedCreated = 0;
        totalOrderLinesReceivedSapLines = 0;
        totalOrderLinesReceivedGoodsReceipt = 0;
    }

    public MaterialType overrideMaterialTypeBasedOnTestObjects(String companyCode, String testObject, MaterialType materialType) {
        if (!StringUtils.isEmpty(testObject)) {
            testObject = StringUtils.substringBefore(testObject.replace("[", ""), "@").toUpperCase();
            List<String> testObjectsAsAdditionalList = MigrationSiteCode.valueOf(companyCode).getTestObjectsToBeConsideredAsAdditional();
            if (testObjectsAsAdditionalList != null && !testObjectsAsAdditionalList.isEmpty()) {
                for (String testObjectAsAdditional : testObjectsAsAdditionalList) {
                    if (testObject.toUpperCase().contains(testObjectAsAdditional.toUpperCase())) {
                        return MaterialType.ADDITIONAL_USAGE;
                    }
                }
            }
        }
        return materialType;
    }

    public void createMaterials(OrderLine orderLine, OrderMigrationDTO dto, FinanceHeader financeHeader, ProcureLine procureLine, String testObject,
            Long quantity, MaterialType materialType, MaterialLineStatus materialLineStatus, BinLocation binlocation, DeliveryNoteLine deliveryNoteLine)
            throws GloriaApplicationException {
        MaterialHeader materialHeader = null;

        materialType = overrideMaterialTypeBasedOnTestObjects(dto.getCompanyCode(), testObject, materialType);

        if (!materialType.equals(MaterialType.ADDITIONAL) && !materialType.equals(MaterialType.RELEASED)) {
            materialHeader = transformToMaterialHeaderEntity(testObject, materialType);
            materialHeader.setMaterialControllerUserId(dto.getIssuerId());
            materialHeader.setMaterialControllerName(dto.getIssuerName());
            materialHeader.setMaterialControllerTeam(dto.getMaterialControllerTeam());
            MaterialHeaderVersion materialHeaderVersion = materialHeader.getAccepted();
            materialHeaderVersion.setContactPersonId(dto.getOrdererId());
            materialHeaderVersion.setContactPersonName(dto.getContactPersonName());
            materialHeaderVersion.setContactPersonEmail(dto.getContactPersonEmail());
            materialHeaderVersion.setRequesterUserId(dto.getOrdererId());
            materialHeaderVersion.setRequesterName(dto.getContactPersonName());
            requestHeaderRepository.save(materialHeader);
        }

        // changeId -- null chk instead
        ChangeId changeId = new ChangeId();
        changeId.setType(ChangeType.SINGLE);
        changeId.setStatus(ChangeIdStatus.ACCEPTED);
        changeId.setMtrlRequestVersion("Migrated");
        if (materialHeader != null) {
            materialHeader.getAccepted().setChangeId(changeId);
        }
        requestHeaderRepository.save(changeId);

        Material material = transformToMaterialEntity(dto, quantity, materialType, materialLineStatus, materialHeader, financeHeader);
        MaterialLine materialLine = material.getMaterialLine().get(0);
        materialLine.setDeliveryNoteLine(deliveryNoteLine);
        material.setProcureLine(procureLine);

        if (procureLine.getMaterials() == null || procureLine.getMaterials().isEmpty()) {
            List<Material> materials = new ArrayList<Material>();
            materials.add(material);
            procureLine.setMaterials(materials);
        } else {
            procureLine.getMaterials().add(material);
        }
        material.setOrderLine(orderLine);
        material.setAdd(changeId);
        requestHeaderRepository.addMaterial(material);

        materialLine.setMaterialOwner(material);

        // Add Placement
        if (materialLineStatus == MaterialLineStatus.STORED && binlocation != null) {
            materialServices.createPlacement(binlocation, materialLine);
            requestHeaderRepository.updateMaterialLine(materialLine);
        } else if (materialLineStatus == MaterialLineStatus.SHIPPED) {
            materialServices.placeIntoZone(materialLine, ZoneType.SHIPPING);
        }
        requestHeaderRepository.updateMaterialLine(materialLine);
    }

    private void log(String text) {
        statusText.append(text + "\n");
        LOGGER.info(text);
    }
}
