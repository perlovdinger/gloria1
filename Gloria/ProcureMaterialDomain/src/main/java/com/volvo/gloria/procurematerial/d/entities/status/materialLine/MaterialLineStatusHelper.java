package com.volvo.gloria.procurematerial.d.entities.status.materialLine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.d.entities.Traceability;
import com.volvo.gloria.common.d.entities.TraceabilityType;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.b.beans.MaterialServicesHelper;
import com.volvo.gloria.procurematerial.b.beans.ProcurementServicesHelper;
import com.volvo.gloria.procurematerial.c.DeliveryNoteLineStatus;
import com.volvo.gloria.procurematerial.c.QiMarking;
import com.volvo.gloria.procurematerial.d.entities.ChangeId;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteSubLine;
import com.volvo.gloria.procurematerial.d.entities.DeliverySchedule;
import com.volvo.gloria.procurematerial.d.entities.GoodsReceiptLine;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeaderVersion;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.MaterialLineStatusTime;
import com.volvo.gloria.procurematerial.d.entities.Order;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLineVersion;
import com.volvo.gloria.procurematerial.d.entities.PickList;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.RequestGroup;
import com.volvo.gloria.procurematerial.d.entities.status.orderLine.OrderLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.requestline.MaterialStatus;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.procurematerial.d.type.procure.ProcureType;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.ObjectJSON;
import com.volvo.gloria.util.tracing.c.GloriaTraceabilityConstants;
import com.volvo.gloria.warehouse.b.WarehouseServices;
import com.volvo.gloria.warehouse.c.ZoneType;
import com.volvo.gloria.warehouse.d.entities.BinLocation;
import com.volvo.gloria.warehouse.d.entities.Placement;

/**
 * helper class for MaterialLineStatus.
 * 
 */
public final class MaterialLineStatusHelper {

    private static final int ACTION_DETAIL_MAX_LENGTH = 255;

    private MaterialLineStatusHelper() {

    }

    public static MaterialLine receiveRegular(DeliveryNoteLine deliveryNoteLine, DeliveryNoteSubLine deliveryNoteSubLine, MaterialLine materialLine,
            MaterialHeaderRepository requestHeaderRepository, UserDTO user, TraceabilityRepository traceabilityRepository, MaterialServices materialServices)
            throws GloriaApplicationException {
        MaterialLine receivedMaterialLine = null;
        long totalToReceiveQuantityForDirectSendType = calculateTotalToReceiveQuantity(deliveryNoteLine, deliveryNoteSubLine);

        long damagedQuantityForDirectSendType = Math.max(deliveryNoteLine.getDamagedQuantity() - deliveryNoteLine.getPreviouslyBlockedQty(), 0);

        long toReceiveQuantity = Math.max(Math.max(totalToReceiveQuantityForDirectSendType - damagedQuantityForDirectSendType, 0)
                                                  - deliveryNoteLine.getAlreadyReceivedQty(), 0);

        if (toReceiveQuantity > 0) {
            receivedMaterialLine = receive(deliveryNoteLine, materialLine, toReceiveQuantity, requestHeaderRepository, user, traceabilityRepository,
                                           materialServices);
        }
        return receivedMaterialLine;
    }
    

    private static long calculateTotalToReceiveQuantity(DeliveryNoteLine deliveryNoteLine, DeliveryNoteSubLine deliveryNoteSubLine) {
        long totalToReceiveQuantity = 0L;

        for (DeliveryNoteSubLine aDeliveryNoteSubLine : deliveryNoteLine.getDeliveryNoteSubLines()) {
            if (deliveryNoteSubLine.isDirectSend() == aDeliveryNoteSubLine.isDirectSend()) {
                totalToReceiveQuantity = totalToReceiveQuantity + aDeliveryNoteSubLine.getToReceiveQty();
            }

        }
        return totalToReceiveQuantity;
    }

    private static MaterialLine receive(DeliveryNoteLine deliveryNoteLine, MaterialLine materialLine, long toReceivedQuantity,
            MaterialHeaderRepository requestHeaderRepository, UserDTO user, TraceabilityRepository traceabilityRepository, MaterialServices materialServices)
            throws GloriaApplicationException {
        toReceivedQuantity = Math.min(materialLine.getQuantity(), toReceivedQuantity);
        MaterialLine receivedMaterialLine = null;
        if (toReceivedQuantity > 0) {
            materialLine.setConfirmationText(deliveryNoteLine.getDeliveryNote().getReceiveType().name());
            List<MaterialLineStatus> avoidTraceForMLStatus = new ArrayList<MaterialLineStatus>();
            avoidTraceForMLStatus.add(materialLine.getStatus());
            avoidTraceForMLStatus.add(MaterialLineStatus.RECEIVED);
            receivedMaterialLine = split(materialLine, MaterialLineStatus.RECEIVED, toReceivedQuantity, requestHeaderRepository, traceabilityRepository,
                                         materialServices, user, avoidTraceForMLStatus);
            deliveryNoteLine.setAlreadyReceivedQty(deliveryNoteLine.getAlreadyReceivedQty() + receivedMaterialLine.getQuantity());
            assignMaterialLineToDeliveryNoteLine(deliveryNoteLine, receivedMaterialLine, user, materialServices, traceabilityRepository);

            receivedMaterialLine.setReceivedDate(DateUtil.getCurrentUTCDateTime());
        }
        return receivedMaterialLine;
    }

    private static void block(DeliveryNoteLine deliveryNoteLine, MaterialLine materialLine, MaterialHeaderRepository requestHeaderRepository, UserDTO user,
            TraceabilityRepository traceabilityRepository, MaterialServices materialServices, long toBlockQuantity) throws GloriaApplicationException {
        toBlockQuantity = Math.min(materialLine.getQuantity(), toBlockQuantity);
        if (toBlockQuantity > 0) {
            MaterialLine blockedMaterialLine = split(materialLine, MaterialLineStatus.BLOCKED, toBlockQuantity, requestHeaderRepository,
                                                     traceabilityRepository, materialServices, user, null);
            blockedMaterialLine.getStatus().setInspectionStatus(blockedMaterialLine);
            materialServices.placeIntoZone(blockedMaterialLine, ZoneType.QUARANTINE);
            deliveryNoteLine.setAlreadyBlockedQty(deliveryNoteLine.getAlreadyBlockedQty() + blockedMaterialLine.getQuantity());
            assignMaterialLineToDeliveryNoteLine(deliveryNoteLine, blockedMaterialLine, user, materialServices, traceabilityRepository);
        }
    }
    
    
    public static void blockMaterialLines(DeliveryNoteLine deliveryNoteLine,  DeliveryNoteSubLine deliveryNoteSubLine, List<MaterialLine> materialLines,
            MaterialHeaderRepository requestHeaderRepository, UserDTO user, TraceabilityRepository traceabilityRepository, MaterialServices materialServices)
            throws GloriaApplicationException {
        List<List<MaterialType>> priotizedBlockList = getBlockMaterialTypeInOrder();
        for (List<MaterialType> blockList : priotizedBlockList) {
            long toBlockQuantity = caluclateToBlockQuantity(deliveryNoteLine, deliveryNoteSubLine);
            if (toBlockQuantity > 0) {
                List<MaterialLine> tempMaterialLineList = new LinkedList<MaterialLine>();
                for (MaterialLine materialLine : materialLines) {
                    if (blockList.contains(materialLine.getMaterial().getMaterialType())) {
                        tempMaterialLineList.add(materialLine);
                    }
                }
                if (tempMaterialLineList.size() > 0) {
                    List<MaterialLine> sortedMaterialLineList = sortMaterialLinesByOutboundStartdate(tempMaterialLineList.
                                                                                                     toArray(new MaterialLine[tempMaterialLineList.size()]));
                    for (MaterialLine materialLine : sortedMaterialLineList) {
                        toBlockQuantity = caluclateToBlockQuantity(deliveryNoteLine, deliveryNoteSubLine);
                        if (toBlockQuantity > 0) {
                            block(deliveryNoteLine, materialLine, requestHeaderRepository, user, traceabilityRepository, materialServices, toBlockQuantity);
                        }
                    }
                }
            }
        } 
    }

    private static long caluclateToBlockQuantity(DeliveryNoteLine deliveryNoteLine, DeliveryNoteSubLine deliveryNoteSubLine) {
        long totalToReceiveQuantityForDirectSendType = calculateTotalToReceiveQuantity(deliveryNoteLine, deliveryNoteSubLine);

        long damagedQuantityForDirectSendType = Math.max(deliveryNoteLine.getDamagedQuantity() - deliveryNoteLine.getPreviouslyBlockedQty(), 0);

        long toBlockQuantity = Math.max(deliveryNoteLine.getDamagedQuantity() - deliveryNoteLine.getAlreadyBlockedQty(), 0);

        if (!deliveryNoteSubLine.isDirectSend()) {
            toBlockQuantity = Math.max(damagedQuantityForDirectSendType - deliveryNoteLine.getAlreadyBlockedQty(), 0);
        }

        if (toBlockQuantity > 0) {
            toBlockQuantity = Math.min(totalToReceiveQuantityForDirectSendType - deliveryNoteLine.getAlreadyBlockedQty(), toBlockQuantity);           
        }
        return toBlockQuantity;
    }

    private static List<MaterialLine> sortMaterialLinesByOutboundStartdate(MaterialLine[] materialLines) {
        int materialLinesLength = materialLines.length;
        int j;
        for (int m = materialLinesLength; m >= 0; m--) {
            for (int i = 0; i < materialLinesLength - 1; i++) {
                j = i + 1;
                MaterialHeader materialHeaderI = materialLines[i].getMaterial().getMaterialHeader();
                MaterialHeader materialHeaderJ = materialLines[j].getMaterial().getMaterialHeader();

                if (materialHeaderJ != null && materialHeaderJ.getAccepted().getOutboundStartDate() == null) {
                    swapMaterialLines(i, j, materialLines);
                } else if ((materialHeaderI != null && materialHeaderJ != null && materialHeaderI.getAccepted().getOutboundStartDate() != null 
                        && materialHeaderJ.getAccepted().getOutboundStartDate() != null)
                        && materialHeaderJ.getAccepted().getOutboundStartDate().after(materialHeaderI.getAccepted().getOutboundStartDate())) {
                    swapMaterialLines(i, j, materialLines);
                }

            }
        }
        return Arrays.asList(materialLines);
    }

    private static void swapMaterialLines(int i, int j, MaterialLine[] materialLines) {
        MaterialLine temp;
        temp = materialLines[i];
        materialLines[i] = materialLines[j];
        materialLines[j] = temp;
    }

    private static List<List<MaterialType>> getBlockMaterialTypeInOrder() {
        List<List<MaterialType>> priotizedMaterialTypesForBlock = new LinkedList<List<MaterialType>>();
        priotizedMaterialTypesForBlock.add(Arrays.asList(MaterialType.RELEASED));
        priotizedMaterialTypesForBlock.add(Arrays.asList(MaterialType.ADDITIONAL, MaterialType.ADDITIONAL_USAGE));
        priotizedMaterialTypesForBlock.add(Arrays.asList(MaterialType.USAGE, MaterialType.MODIFIED));
        return priotizedMaterialTypesForBlock;
    }

    private static DeliveryNoteLine assignMaterialLineToDeliveryNoteLine(DeliveryNoteLine deliveryNoteLine, MaterialLine materialLine, UserDTO user,
            MaterialServices materialServices, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        deliveryNoteLine.getMaterialLine().add(materialLine);
        materialLine.setDeliveryNoteLine(deliveryNoteLine);
        OrderLine orderLine = deliveryNoteLine.getOrderLine();
        if (orderLine != null) {
            Order order = orderLine.getOrder();
            if (order != null) {
                materialLine.setOrderNo(order.getOrderNo());
            }
        }
        updateWithTransportLabelCode(deliveryNoteLine, materialLine);
        updateAndAlertPartversionChangeForDelyveryNoteLine(deliveryNoteLine, materialLine, user, materialServices, traceabilityRepository);
        return deliveryNoteLine;
    }

    public static void updateWithTransportLabelCode(DeliveryNoteLine deliveryNoteLine, MaterialLine materialLine) {
        if (materialLine.getDirectSend() != null) {
            DeliveryNoteSubLine subLine = deliveryNoteLine.getSubLine(materialLine.getDirectSend().isDirectSend());
            if (subLine != null && subLine.getTransportLabel() != null) {
                materialLine.setTransportLabelCode(subLine.getTransportLabel().getCode());
            }
        }
    }

    public static void updateAndAlertPartversionChangeForDelyveryNoteLine(DeliveryNoteLine deliveryNoteLine, MaterialLine materialLine, UserDTO user,
            MaterialServices materialServices, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        Material material = materialLine.getMaterial();
        if (material != null) {
            String currentPartVersion = material.getPartVersion();
            String newPartVersion = deliveryNoteLine.getPartVersion();
            updateAndAlertPartversionChange(material, currentPartVersion, newPartVersion, user, materialServices, traceabilityRepository);
        }
    }

    public static void updateAndAlertPartversionChange(Material material, String currentPartVersion, String newPartVersion, UserDTO user,
            MaterialServices materialServices, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        if (!currentPartVersion.equalsIgnoreCase(newPartVersion)) {
            material.setPartVersionOriginal(currentPartVersion);
            material.setPartVersion(newPartVersion);
            material.getMaterialLastModified().setAlertPartVersion(true);
            for (MaterialLine materialLine : material.getMaterialLine()) {
                createTraceabilityLog(materialLine, traceabilityRepository, "Part version changed", null, user.getId(), user.getUserName(),
                                      GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE);
            }

            materialServices.reCreatePlacements(material);
        }
    }

    public static MaterialLine borrow(MaterialLine borrowToMaterialLine, List<MaterialLine> borrowFromMaterialLines,
            MaterialHeaderRepository requestHeaderRepository, boolean noReturn, MaterialServices materialServices,
            TraceabilityRepository traceabilityRepository, WarehouseServices warehouseServices, OrderRepository orderRepository, UserDTO userDTO)
            throws GloriaApplicationException {
        MaterialLine splittedMaterialLineAfterBorrow = null;
        Material borrowToMaterial = borrowToMaterialLine.getMaterial();
        if (borrowFromMaterialLines != null && !borrowFromMaterialLines.isEmpty()) {
            if (borrowToMaterial.getMaterialType().isBorrowable()) {
                splittedMaterialLineAfterBorrow = doBorrow(borrowToMaterialLine, borrowFromMaterialLines, requestHeaderRepository, noReturn, materialServices,
                                                           traceabilityRepository, warehouseServices, userDTO, splittedMaterialLineAfterBorrow,
                                                           borrowToMaterial);
            }
        }
        return splittedMaterialLineAfterBorrow;
    }

    private static MaterialLine doBorrow(MaterialLine borrowToMaterialLine, List<MaterialLine> borrowFromMaterialLines,
            MaterialHeaderRepository requestHeaderRepository, boolean noReturn, MaterialServices materialServices,
            TraceabilityRepository traceabilityRepository, WarehouseServices warehouseServices, UserDTO userDTO, MaterialLine splittedMaterialLineAfterBorrow,
            Material borrowToMaterial) throws GloriaApplicationException {
        long toBorrowQuantity = borrowToMaterialLine.getQuantity();
        createTraceabilityLog(borrowToMaterialLine, traceabilityRepository, "Borrowed to", null, userDTO.getId(), userDTO.getUserName(),
                              GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE);
        for (MaterialLine borrowFromMaterialLine : borrowFromMaterialLines) {
            if (borrowFromMaterialLine.getStatus().isBorrowable() && toBorrowQuantity > 0) {
                long borrowedQuantity = Math.min(borrowFromMaterialLine.getQuantity(), toBorrowQuantity);
                toBorrowQuantity -= borrowedQuantity;
                BinLocation borrowFromMaterialLineBinLocation = removePlacement(warehouseServices, borrowFromMaterialLine);

                MaterialLine borrowedToMaterialLine = split(borrowToMaterialLine, borrowedQuantity, requestHeaderRepository, traceabilityRepository,
                                                            materialServices, userDTO, null);

                MaterialLine borrowedFromMaterialLine = split(borrowFromMaterialLine, borrowedQuantity, requestHeaderRepository, traceabilityRepository,
                                                              materialServices, userDTO, null);

                createTraceabilityLog(borrowedFromMaterialLine, traceabilityRepository, "Borrowed from", null, userDTO.getId(), userDTO.getUserName(),
                                      GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE);

                materialServices.createPlacement(borrowFromMaterialLineBinLocation, borrowFromMaterialLine);
                materialServices.createPlacement(borrowFromMaterialLineBinLocation, borrowedFromMaterialLine);

                Material borrowFromMaterial = borrowFromMaterialLine.getMaterial();
                if (noReturn && borrowedFromMaterialLine != null) {
                    borrowToMaterialLine.getStatus().noReturn(requestHeaderRepository, traceabilityRepository, borrowedFromMaterialLine, borrowFromMaterial,
                                                              userDTO);
                }

                if (borrowedFromMaterialLine != null) {
                    borrowedFromMaterialLine.setBorrowed(true);
                    defineMaterialLineOwner(borrowToMaterial, borrowedFromMaterialLine);
                }

                if (borrowedToMaterialLine != null) {
                    borrowedToMaterialLine.setBorrowed(true);
                    defineMaterialLineOwner(borrowFromMaterial, borrowedToMaterialLine);
                }
                if (borrowedQuantity <= 0L) {
                    splittedMaterialLineAfterBorrow = borrowFromMaterialLine;
                }
            }
        }
        return splittedMaterialLineAfterBorrow;
    }

    private static void defineMaterialLineOwner(Material materialOwner, MaterialLine materialLine) {
        if (materialLine != null) {
            materialLine.setMaterialOwner(materialOwner);
        }
    }

    public static BinLocation removePlacement(WarehouseServices warehouseServices, MaterialLine materiaLine) throws GloriaApplicationException {
        Placement placement = null;
        if (materiaLine.getPlacementOID() != null) {
            placement = warehouseServices.getPlacement(materiaLine.getPlacementOID());
        }
        BinLocation materialLineBinLocation = null;
        if (placement != null) {
            materialLineBinLocation = placement.getBinLocation();
            MaterialServicesHelper.removePlacement(materiaLine, warehouseServices);
        }
        return materialLineBinLocation;
    }

    public static void qualityInspectMaterial(MaterialLine materialLine, MaterialServices materialServices, UserDTO user,
            TraceabilityRepository traceabilityRepository, DeliveryNoteSubLine deliveryNoteSubLine) throws GloriaApplicationException {
        QiMarking qiMarking = getQiMarking(deliveryNoteSubLine);
        if (qiMarking != null) {
            if (qiMarking.equals(QiMarking.MANDATORY)) {
                materialServices.placeIntoZone(materialLine, ZoneType.QI);
                updateMaterialLineStatus(materialLine, MaterialLineStatus.QI_READY, "QI Ready", GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE, user,
                                         traceabilityRepository, false);
            } else {
                if (qiMarking.equals(QiMarking.VISUAL)) {
                    updateMaterialLineStatus(materialLine, MaterialLineStatus.QI_OK, "QI Ok", GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE, user,
                                             traceabilityRepository, false);
                }
                if (!deliveryNoteSubLine.isStore() && materialLine.getDirectSend().isDirectSend()) {
                    materialServices.placeIntoZone(materialLine, ZoneType.SHIPPING);
                    updateMaterialLineStatus(materialLine, MaterialLineStatus.READY_TO_SHIP, "Direct Send",
                                             GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE, user, traceabilityRepository, false);
                    materialServices.getMaterialLineStatusCounterRepository().createAndSave(materialLine, MaterialLineStatusCounterType.PICKS);
                } else {
                    updateMaterialLineStatus(materialLine, MaterialLineStatus.READY_TO_STORE, "Ready To Store",
                                             GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE, user, traceabilityRepository, false);
                    materialServices.placeIntoZone(materialLine, ZoneType.TO_STORE);
                }
            }
        }
    }

    private static QiMarking getQiMarking(DeliveryNoteSubLine deliveryNoteSubLine) {
        if (deliveryNoteSubLine.isStore()) {
            return QiMarking.VISUAL;
        } else {
            return deliveryNoteSubLine.getDeliveryNoteLine().getOrderLine().getQiMarking();
        }
    }

    public static MaterialLine receiveTransferReturn(DeliveryNoteLine deliveryNoteLine, DeliveryNoteSubLine deliveryNoteSubLine, MaterialLine materialLine,
            MaterialHeaderRepository requestHeaderRepository, UserDTO user, TraceabilityRepository traceabilityRepository, MaterialServices materialServices,
            Material transferMaterial, WarehouseServices warehouseServices) throws GloriaApplicationException {
        MaterialLine receivedMaterialLine = null;
        long receivedQuantity = Math.max(deliveryNoteSubLine.getToReceiveQty() - deliveryNoteLine.getAlreadyReceivedQty(), 0);
        if (receivedQuantity > 0) {
            receivedMaterialLine = receive(deliveryNoteLine, materialLine, receivedQuantity, requestHeaderRepository, user, traceabilityRepository,
                                           materialServices);
        }
        deliveryNoteLine.setStatus(DeliveryNoteLineStatus.RECEIVED);
        deliveryNoteLine.setReceivedDateTime(DateUtil.getCurrentUTCDateTime());
        deliveryNoteLine.setPossibleToReceiveQty(receivedQuantity);
        if (receivedMaterialLine != null) {
            RequestGroup requestGroup = receivedMaterialLine.getRequestGroup();
            if (requestGroup != null) {
                deliveryNoteLine.getDeliveryNote().setReturnDeliveryAddressId(requestGroup.getRequestList().getDeliveryAddressId());
                deliveryNoteLine.getDeliveryNote().setReturnDeliveryAddressName(requestGroup.getRequestList().getDeliveryAddressName());
            }
        }
        return receivedMaterialLine;
    }

    public static void createTraceabilityLog(MaterialLine materialLine, TraceabilityRepository traceabilityRepository, String action, String actionDetail,
            String userId, String userName, String il18MessageCode) {
        if (materialLine != null) {
            if (shouldBeTraced(action) && shouldBeTraced(materialLine.getStatus())) {
                Traceability traceability = new Traceability(TraceabilityType.MATERIAL_LINE);
                traceability.setLoggedTime(DateUtil.getUTCTimeStamp());
                traceability.setUserId(userId);
                traceability.setUserName(userName);
                traceability.setAction(adjustAction(action));
                if (actionDetail != null) {
                    traceability.setActionDetail(trimString(actionDetail));
                } else {
                    traceability.setActionDetail(trimString(fetchActionDetail(materialLine, action)));
                }
                traceability.setUserText("");

                Material material = materialLine.getMaterial();
                if (material != null) {
                    traceability.setMaterialOID(material.getMaterialOID());
                    traceability.setMaterialLineOId(materialLine.getMaterialLineOID());
                    traceability.setMlStatus(materialLine.getStatus().name());
                    traceability.setMlQuantity(materialLine.getQuantity());
                    traceability.setI18MessageCode(il18MessageCode);

                    setOrderDetails(traceability, material, null);
                    traceability.setWhSiteId(materialLine.getWhSiteId());
                    traceability.setBinLocationCode(materialLine.getBinLocationCode());
                }

                traceabilityRepository.save(traceability);
            }
        }
    }

    private static String trimString(String actionDetail) {
        if (!StringUtils.isEmpty(actionDetail)) {
            if (actionDetail.length() > ACTION_DETAIL_MAX_LENGTH) {
                return actionDetail.substring(0, ACTION_DETAIL_MAX_LENGTH);
            }
            return actionDetail;
        }
        return "";
    }

    public static String fetchActionDetail(MaterialLine materialLine, String action) {
        String actionDetail = "";
        if (materialLine != null) {
            Material material = materialLine.getMaterial();
            if (action != null && (action.equals(MaterialLineStatus.RECEIVED.toString()) || action.equals("Received"))) {
                actionDetail = "Receive Type : " + materialLine.getConfirmationText()+", WhSite : "+materialLine.getWhSiteId();
            }

            if (action != null && action.equals(MaterialLineStatus.REQUESTED.toString())) {
                actionDetail = materialLine.getConfirmationText();
            }

            if (action != null && action.equals(MaterialLineStatus.DEVIATED.toString())) {
                actionDetail = "Deviated from picklist";
            }

            if (action != null && action.equalsIgnoreCase("Released")) {
                actionDetail = materialLine.getConfirmationText();
            }

            if (action != null && action.equals(MaterialLineStatus.SCRAPPED.toString())) {
                actionDetail = materialLine.getConfirmationText();
            }

            if (action != null && action.equalsIgnoreCase("Part version changed")) {
                actionDetail = "From Version: " + material.getPartVersionOriginal();
            }

            if (action != null && action.equalsIgnoreCase("Order Cancelled")) {
                actionDetail = "Order No: " + materialLine.getOrderNo() + " Cancelled";
            }

            if (action != null && action.equals(MaterialLineStatus.MISSING.toString())) {
                actionDetail = materialLine.getConfirmationText();
            }

            if (action != null && action.equals("Removed")) {
                MaterialStatus rejectStatus = material.getRejectChangeStatus();
                String acceptOrRejected = "Accepted";
                if (rejectStatus != null) {
                    if (rejectStatus == MaterialStatus.ADD_NOT_ACCEPTED) {
                        acceptOrRejected = "Rejected";
                    }
                }
                actionDetail = "Change " + acceptOrRejected + " : " + material.getMtrlRequestVersionAccepted() + ", No change for removed quantity";
            }

            if (action != null && action.equals("Modified")) {
                actionDetail = buildModificationActionDetail(material);
            }
            
            if (action != null && action.equalsIgnoreCase("Stored")) {
                actionDetail = "Bin Location : " + materialLine.getBinLocationCode() + ", WhSite : " + materialLine.getWhSiteId();
            }
        }

        return actionDetail;
    }

    private static String buildModificationActionDetail(Material material) {
        ObjectJSON objectJSON = new ObjectJSON();
        objectJSON.addItem("procureLineId", String.valueOf(material.getProcureLine().getProcureLineOid()));
        objectJSON.addItem("modificationType", String.valueOf(material.getModificationType()));
        objectJSON.addItem("modificationId", String.valueOf(material.getModificationId()));
        return objectJSON.jsonValue();
    }

    public static void createTraceabilityLogWithDeliverySchedule(MaterialLine materialLine, TraceabilityRepository traceabilityRepository, String action,
            String userId, String userName, DeliverySchedule deliverySchedule, String il18MessageCode) {
        if (materialLine != null) {
            if (shouldBeTraced(action) && shouldBeTraced(materialLine.getStatus())) {
                Traceability traceability = new Traceability(TraceabilityType.MATERIAL_LINE);
                traceability.setLoggedTime(DateUtil.getUTCTimeStamp());
                traceability.setUserId(userId);
                traceability.setUserName(userName);
                traceability.setAction(adjustAction(action));
                traceability.setActionDetail("");
                traceability.setUserText("");
                Material material = materialLine.getMaterial();
                traceability.setMaterialOID(material.getMaterialOID());
                traceability.setMaterialLineOId(materialLine.getMaterialLineOID());
                traceability.setMlStatus(materialLine.getStatus().name());
                traceability.setMlQuantity(materialLine.getQuantity());
                traceability.setI18MessageCode(il18MessageCode);

                setOrderDetails(traceability, material, deliverySchedule);

                traceability.setWhSiteId(materialLine.getWhSiteId());
                traceability.setBinLocationCode(materialLine.getBinLocationCode());
                traceabilityRepository.save(traceability);
            }
        }
    }

    private static String adjustAction(String action) {
        String adjustedAction = action;
        if (action != null && action.equals(MaterialLineStatus.ORDER_PLACED_INTERNAL.toString())) {
            adjustedAction = "Placed Internal";
        }
        if (action != null && action.equals(MaterialLineStatus.ORDER_PLACED_EXTERNAL.toString())) {
            adjustedAction = "Placed External";
        }
        if (action != null && "REQUISITION_SENT".equals(action)) {
            adjustedAction = "Requisition Sent";
        }

        if (action != null && action.equals(MaterialLineStatus.RECEIVED.toString())) {
            adjustedAction = "Received";
        }
        if (action != null && action.equals(MaterialLineStatus.READY_TO_STORE.toString())) {
            adjustedAction = "Ready to Store";
        }
        if (action != null && action.equals(MaterialLineStatus.STORED.toString())) {
            adjustedAction = "Stored";
        }
        if (action != null && action.equals(MaterialLineStatus.SHIPPED.toString())) {
            adjustedAction = "Shipped";
        }
        if (action != null && action.equals(MaterialLineStatus.QI_READY.toString())) {
            adjustedAction = "QI Ready";
        }
        if (action != null && action.equals(MaterialLineStatus.QI_OK.toString())) {
            adjustedAction = "QI Ok";
        }
        if (action != null && action.equals(MaterialLineStatus.REQUESTED.toString())) {
            adjustedAction = "Requested";
        }
        if (action != null && action.equals(MaterialLineStatus.IN_TRANSFER.toString())) {
            adjustedAction = "Transfered";
        }
        if (action != null && action.equals(MaterialLineStatus.BLOCKED.toString())) {
            adjustedAction = "Blocked";
        }
        if (action != null && action.equals(MaterialLineStatus.DEVIATED.toString())) {
            adjustedAction = "Deviated";
        }
        if (action != null && action.equals(MaterialLineStatus.SCRAPPED.toString())) {
            adjustedAction = "Scrapped";
        }

        if (action != null && action.equals(MaterialLineStatus.MARKED_INSPECTION.toString())) {
            adjustedAction = "Marked For Inspection";
        }

        if (action != null && action.equals(MaterialLineStatus.MISSING.toString())) {
            adjustedAction = "Qty Adjusted";
        }

        if (action != null && action.equals(MaterialLineStatus.QTY_DECREASED.toString())) {
            adjustedAction = "Qty Decreased";
        }

        return adjustedAction;
    }

    public static String getAdjustedActionForMaterialType(String materialTypeAction) {
        String adjustedAction = materialTypeAction;
        if (materialTypeAction != null && materialTypeAction.equals(MaterialType.ADDITIONAL.name())) {
            adjustedAction = "Added";
        } else if (materialTypeAction != null && materialTypeAction.equals(MaterialType.ADDITIONAL_USAGE.name())) {
            adjustedAction = "Added Usage";
        }
        if (materialTypeAction != null && materialTypeAction.equals(MaterialType.MODIFIED.name())) {
            adjustedAction = "Modified";
        } else if (materialTypeAction != null && materialTypeAction.equals(MaterialType.RELEASED.name())) {
            adjustedAction = "Released";
        } else if (materialTypeAction != null && materialTypeAction.equals(MaterialType.USAGE.name())) {
            adjustedAction = "Create Usage";
        } else if (materialTypeAction != null && materialTypeAction.equals(MaterialType.USAGE_REPLACED.name())) {
            adjustedAction = "Usage Replaced";
        }
        return adjustedAction;
    }

    private static boolean shouldBeTraced(String action) {
        boolean shouldBeTraced = true;
        if (action != null
                && (action.equals(MaterialLineStatus.ORDER_PLACED_INTERNAL.toString()) || action.equals(MaterialLineStatus.REMOVED_DB.toString()) || action.equals(MaterialLineStatus.READY_TO_STORE.toString()))) {
            shouldBeTraced = false;
        }
        return shouldBeTraced;
    }

    private static boolean shouldBeTraced(MaterialLineStatus status) {
        boolean shouldBeTraced = true;
        if (status.equals(MaterialLineStatus.REMOVED_DB)) {
            shouldBeTraced = false;
        }
        return shouldBeTraced;
    }

    private static void setOrderDetails(Traceability traceability, Material material, DeliverySchedule deliverySchedule) {
        OrderLine orderLine = material.getOrderLine();
        if (orderLine != null) {
            Order order = orderLine.getOrder();
            traceability.setOrderLineOID(orderLine.getOrderLineOID());
            if (order != null) {
                traceability.setOrderNo(order.getOrderNo());
                traceability.setOrderDate(order.getOrderDateTime());
                if (order.getInternalExternal() != null) {
                    traceability.setInternalExternal(order.getInternalExternal().name());
                }
            }
            if (orderLine != null) {
                OrderLineStatus status = orderLine.getStatus();
                if (status != null) {
                    traceability.setOlStatus(status.name());
                }
                OrderLineVersion currentOrderLineVersion = orderLine.getCurrent();
                if (currentOrderLineVersion != null) {
                    traceability.setOlQuantity(currentOrderLineVersion.getQuantity());
                    traceability.setStaAgreedDate(currentOrderLineVersion.getStaAgreedDate());
                    traceability.setStaAcceptedDate(currentOrderLineVersion.getStaAcceptedDate());
                }
                traceability.setAllowedQuantity(orderLine.getPossibleToReceiveQuantity());
                if (deliverySchedule != null) {
                    traceability.setDeliveryScheduleOID(deliverySchedule.getDeliveryScheduleOID());
                    traceability.setExpectedDate(deliverySchedule.getExpectedDate());
                    traceability.setPlannedDispatchDate(deliverySchedule.getPlannedDispatchDate());
                    traceability.setExpectedQty(deliverySchedule.getExpectedQuantity());
                }
            }
        }
    }

    public static MaterialLineStatus getPlacedState(MaterialLine materialLine) throws GloriaApplicationException {
        Material material = materialLine.getMaterial();
        ProcureLine procureLine = material.getProcureLine();
        if (procureLine != null) {
            ProcureType procureType = procureLine.getProcureType();
            return procureType.getOrderPlacedStatus();
        }
        return null;
    }

    public static MaterialLineStatus getProcureState(MaterialLine materialLine) throws GloriaApplicationException {
        Material material = materialLine.getMaterial();
        ProcureLine procureLine = material.getProcureLine();
        if (procureLine != null) {
            ProcureType procureType = procureLine.getProcureType();
            return procureType.getOrderProcuredStatus();
        }
        return null;
    }

    public static MaterialLine doShipOrStoreMaterial(UserDTO user, MaterialLine materialLine, MaterialServices materialServices,
            TraceabilityRepository traceabilityRepository, MaterialHeaderRepository requestHeaderRepository, CommonServices commonServices, boolean isStore,
            WarehouseServices warehouseServices) throws GloriaApplicationException {
        if (materialLine.getPlacementOID() != null) {
            MaterialServicesHelper.removePlacement(materialLine, warehouseServices);
        }
        if (!isStore && materialLine.getDirectSend().isDirectSend()) {
            materialServices.placeIntoZone(materialLine, ZoneType.SHIPPING);
            updateMaterialLineStatus(materialLine, MaterialLineStatus.READY_TO_SHIP, "Direct Send", GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE,
                                     user, traceabilityRepository, false);
            materialServices.getMaterialLineStatusCounterRepository().createAndSave(materialLine, MaterialLineStatusCounterType.PICKS);
        } else {
            materialServices.placeIntoZone(materialLine, ZoneType.TO_STORE);
            updateMaterialLineStatus(materialLine, MaterialLineStatus.READY_TO_STORE, "Ready To Store", GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE,
                                     user, traceabilityRepository, false);
        }
        return materialLine;
    }

    public static void quarantine(long blockedQuantity, UserDTO user, MaterialLine materialLine, MaterialServices materialServices,
            TraceabilityRepository traceabilityRepository, WarehouseServices warehouseServices) throws GloriaApplicationException {
        materialLine.setQuantity(blockedQuantity);
        updateMaterialLineStatus(materialLine, MaterialLineStatus.BLOCKED, "Blocked", GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE, user,
                                 traceabilityRepository, true);
        MaterialServicesHelper.removePlacement(materialLine, warehouseServices);
        materialServices.placeIntoZone(materialLine, ZoneType.QUARANTINE);
    }

    public static void unBlock(long unBlockQty, UserDTO user, MaterialLine materialLine, TraceabilityRepository traceabilityRepository)
            throws GloriaApplicationException {
        materialLine.setQuantity(unBlockQty);
        updateMaterialLineStatus(materialLine, MaterialLineStatus.QI_OK, "QI Ok", GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE, user,
                                 traceabilityRepository, false);
    }

    public static void updateMaterialLineStatus(MaterialLine materialLine, MaterialLineStatus status, String action, String il18MessageCode, UserDTO user,
            TraceabilityRepository traceabilityRepository, boolean isTraceRequired) {
        materialLine.setStatus(status);
        materialLine.getStatus().setInspectionStatus(materialLine);
        materialLine.setStatusDate(DateUtil.getCurrentUTCDateTime());
        if (user != null) {
            materialLine.setStatusUserId(user.getId());
            materialLine.setStatusUserName(user.getUserName());
            if (isTraceRequired) {
                status.trace(materialLine, action, null, il18MessageCode, user, traceabilityRepository);
            }
        }
    }

    public static MaterialLine split(MaterialLine materialLineToSplit, MaterialLineStatus materialLineStatus, String binLocationCode, long quantity,
            MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository, UserDTO user,
            List<MaterialLineStatus> avoidTraceForMLStatus) throws GloriaApplicationException {
        return requestHeaderRepository.split(materialLineToSplit, materialLineStatus, quantity,
                                             requestHeaderRepository.findAMaterialLineByAndStatusAndPlacement(materialLineToSplit, materialLineStatus,
                                                                                                              binLocationCode), requestHeaderRepository,
                                             traceabilityRepository, user, avoidTraceForMLStatus);
    }

    public static MaterialLine split(MaterialLine materialLineToSplit, MaterialLineStatus materialLineStatus, long quantity,
            MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository, MaterialServices materialServices, UserDTO user,
            List<MaterialLineStatus> avoidTraceForMLStatus) throws GloriaApplicationException {
        return requestHeaderRepository.split(materialLineToSplit, materialLineStatus, quantity,
                                             requestHeaderRepository.findAMaterialLineByAndStatusAndPlacement(materialLineToSplit, materialLineStatus,
                                                                                                              materialLineToSplit.getBinLocationCode()),
                                             requestHeaderRepository, traceabilityRepository, user, avoidTraceForMLStatus);
    }

    public static MaterialLine split(MaterialLine materialLineToSplit, long quantity, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository, UserDTO user, List<MaterialLineStatus> avoidTraceForMLStatus) throws GloriaApplicationException {
        return split(materialLineToSplit, quantity, requestHeaderRepository, traceabilityRepository, null, user, avoidTraceForMLStatus);
    }

    public static MaterialLine split(MaterialLine materialLineToSplit, long quantity, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository, MaterialServices materialServices, UserDTO user, List<MaterialLineStatus> avoidTraceForMLStatus)
            throws GloriaApplicationException {
        return requestHeaderRepository.split(materialLineToSplit, materialLineToSplit.getStatus(), quantity, null, requestHeaderRepository,
                                             traceabilityRepository, user, avoidTraceForMLStatus);
    }

    public static MaterialLine merge(MaterialLine materialLineToMerge, long quantity, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository, UserDTO user, List<MaterialLineStatus> avoidTraceForMLStatus) throws GloriaApplicationException {
        return merge(materialLineToMerge, materialLineToMerge.getStatus(), quantity, null, requestHeaderRepository, traceabilityRepository, user,
                     avoidTraceForMLStatus);
    }

    public static MaterialLine merge(MaterialLine materialLineToMerge, MaterialLineStatus materialLineStatus, MaterialLine existingMaterialLine,
            MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository, UserDTO user,
            List<MaterialLineStatus> avoidTraceForMLStatus) throws GloriaApplicationException {
        return merge(materialLineToMerge, materialLineStatus, materialLineToMerge.getQuantity(), existingMaterialLine, requestHeaderRepository,
                     traceabilityRepository, user, avoidTraceForMLStatus);
    }

    public static MaterialLine merge(MaterialLine materialLineToMerge, MaterialLineStatus materialLineStatus, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository, UserDTO user, List<MaterialLineStatus> avoidTraceForMLStatus) throws GloriaApplicationException {
        return merge(materialLineToMerge,
                     materialLineStatus,
                     materialLineToMerge.getQuantity(),
                     requestHeaderRepository.findAMaterialLineByAndStatusAndPlacement(materialLineToMerge, materialLineStatus,
                                                                                      materialLineToMerge.getBinLocationCode()), requestHeaderRepository,
                     traceabilityRepository, user, avoidTraceForMLStatus);
    }

    public static MaterialLine merge(MaterialLine materialLineToMerge, MaterialLineStatus materialLineStatus, long quantity,
            MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository, UserDTO user,
            List<MaterialLineStatus> avoidTraceForMLStatus) throws GloriaApplicationException {
        return merge(materialLineToMerge,
                     materialLineStatus,
                     quantity,
                     requestHeaderRepository.findAMaterialLineByAndStatusAndPlacement(materialLineToMerge, materialLineStatus,
                                                                                      materialLineToMerge.getBinLocationCode()), requestHeaderRepository,
                     traceabilityRepository, user, avoidTraceForMLStatus);
    }

    public static MaterialLine merge(MaterialLine materialLineToMerge, MaterialLineStatus materialLineStatus, long quantity, MaterialLine existingMaterialLine,
            MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository, UserDTO user,
            List<MaterialLineStatus> avoidTraceForMLStatus) throws GloriaApplicationException {
        materialLineToMerge.setStatus(materialLineStatus);
        materialLineToMerge.setStatusDate(DateUtil.getCurrentUTCDateTime());
        materialLineToMerge.setQuantity(quantity);

        if (user != null) {
            materialLineToMerge.setStatusUserId(user.getId());
            materialLineToMerge.setStatusUserName(user.getUserName());
            boolean isTraceable = true;
            if (avoidTraceForMLStatus != null) {
                if (avoidTraceForMLStatus.contains(materialLineStatus)) {
                    isTraceable = false;
                }
            }
            if (isTraceable) {
                MaterialLineStatusHelper.createTraceabilityLog(materialLineToMerge, traceabilityRepository, materialLineStatus.name(), null, user.getId(),
                                                               user.getUserName(), GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE);
            }
        }

        return requestHeaderRepository.merge(materialLineToMerge, existingMaterialLine);
    }

    public static MaterialLine cloneMaterialline(MaterialLine materialLine) throws GloriaApplicationException {
        MaterialLine clonedMaterialLine = new MaterialLine();
        if (materialLine != null) {
            Material material = materialLine.getMaterial();
            clonedMaterialLine.setMaterial(material);
            copyMaterialLineAttributes(materialLine, clonedMaterialLine);
            DeliveryNoteLine deliveryNoteLine = materialLine.getDeliveryNoteLine();
            if (deliveryNoteLine != null) {
                clonedMaterialLine.setDeliveryNoteLine(deliveryNoteLine);
                deliveryNoteLine.getMaterialLine().add(clonedMaterialLine);
            }

            RequestGroup requestGroup = materialLine.getRequestGroup();
            if (requestGroup != null) {
                clonedMaterialLine.setRequestGroup(requestGroup);
                requestGroup.getMaterialLines().add(clonedMaterialLine);
            }

            PickList pickList = materialLine.getPickList();
            if (pickList != null) {
                clonedMaterialLine.setPickList(pickList);
            }

            Material materialOwner = materialLine.getMaterialOwner();
            if (materialOwner != null) {
                clonedMaterialLine.setMaterialOwner(materialLine.getMaterialOwner());
            }

            clonedMaterialLine.setOrderCancelled(materialLine.isOrderCancelled());
            material.addMaterialLine(clonedMaterialLine);

            MaterialLineStatusTime materialLineStatusTime = materialLine.getMaterialLineStatusTime();
            MaterialLineStatusTime clonedMaterialLineStatusTime = clonedMaterialLine.getMaterialLineStatusTime();
            if (materialLineStatusTime != null && clonedMaterialLineStatusTime != null) {
                clonedMaterialLineStatusTime.setReceivedTime(materialLineStatusTime.getReceivedTime());
                clonedMaterialLineStatusTime.setRequestTime(materialLineStatusTime.getRequestTime());
                clonedMaterialLineStatusTime.setShippedTime(materialLineStatusTime.getShippedTime());
                clonedMaterialLineStatusTime.setStoredTime(materialLineStatusTime.getStoredTime());
                clonedMaterialLineStatusTime.setPickedBinLocation(materialLineStatusTime.getPickedBinLocation());
                clonedMaterialLineStatusTime.setPickedQty(materialLineStatusTime.getPickedQty());
                clonedMaterialLineStatusTime.setPickedStorageRoom(materialLineStatusTime.getPickedStorageRoom());
                clonedMaterialLineStatusTime.setPickedTime(materialLineStatusTime.getPickedTime());
                clonedMaterialLineStatusTime.setRequestedQty(materialLineStatusTime.getRequestedQty());
                clonedMaterialLineStatusTime.setStoredBinLocation(materialLineStatusTime.getStoredBinLocation());
                clonedMaterialLineStatusTime.setStoredQty(materialLineStatusTime.getStoredQty());
                clonedMaterialLineStatusTime.setStoredStorageRoom(materialLineStatusTime.getStoredStorageRoom());
            }

            clonedMaterialLine.setConfirmationText(materialLine.getConfirmationText());
        }
        return clonedMaterialLine;
    }

    public static MaterialLine cloneMateriallineAttributes(MaterialLine materialLine) throws GloriaApplicationException {
        MaterialLine clonedMaterialLine = new MaterialLine();
        if (materialLine != null) {
            copyMaterialLineAttributes(materialLine, clonedMaterialLine);
        }
        return clonedMaterialLine;

    }

    private static void copyMaterialLineAttributes(MaterialLine materialLine, MaterialLine clonedMaterialLine) throws GloriaApplicationException {
        clonedMaterialLine.setQuantity(materialLine.getQuantity());
        clonedMaterialLine.setStatus(materialLine.getStatus());
        clonedMaterialLine.setPreviousStatus(materialLine.getPreviousStatus());
        clonedMaterialLine.setInspectionStatus(materialLine.getInspectionStatus());
        clonedMaterialLine.setPlacementOID(materialLine.getPlacementOID());
        clonedMaterialLine.setBinLocationCode(materialLine.getBinLocationCode());
        clonedMaterialLine.setZoneType(materialLine.getZoneType());
        clonedMaterialLine.setZoneCode(materialLine.getZoneCode());
        clonedMaterialLine.setZoneName(materialLine.getZoneName());
        clonedMaterialLine.setStorageRoomCode(materialLine.getStorageRoomCode());
        clonedMaterialLine.setStorageRoomName(materialLine.getStorageRoomName());
        clonedMaterialLine.setReceivedDate(materialLine.getReceivedDate());
        clonedMaterialLine.setBarCode(materialLine.getBarCode());
        clonedMaterialLine.setProcureType(materialLine.getProcureType());
        clonedMaterialLine.setDirectSend(materialLine.getDirectSend());
        clonedMaterialLine.setWhSiteId(materialLine.getWhSiteId());
        clonedMaterialLine.setFinalWhSiteId(materialLine.getFinalWhSiteId());
        clonedMaterialLine.setOrderNo(materialLine.getOrderNo());
        clonedMaterialLine.setTransportLabelCode(materialLine.getTransportLabelCode());
        clonedMaterialLine.setRequestedExcluded(materialLine.isRequestedExcluded());
        clonedMaterialLine.setBorrowed(materialLine.isBorrowed());
    }

    public static void assignMaterialLineToMaterial(Material material, MaterialLine materialLine) {
        if (materialLine != null) {
            materialLine.setMaterial(material);
            material.getMaterialLine().add(materialLine);
            defineMaterialLineOwner(material, materialLine);
        }
    }

    public static MaterialLine revertMaterialLineToOrderPlaced(long qtyCancelled, MaterialLine materialLine, DeliveryNoteLine deliveryNoteLine,
            GoodsReceiptLine goodsReceiptLine, UserDTO userDTO, MaterialServices materialServices, WarehouseServices warehouseServices,
            MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        long quantityCancelled = Math.min(materialLine.getQuantity(), qtyCancelled - deliveryNoteLine.getAlreadyCanceledQty());
        MaterialLine cancelledMaterialLine = null;
        if (quantityCancelled > 0) {
            MaterialLineStatus orderPlacedStatus = deliveryNoteLine.getOrderLine().getProcureLine().getProcureType().getOrderPlacedStatus();
            cancelledMaterialLine = split(materialLine, orderPlacedStatus, quantityCancelled, requestHeaderRepository, traceabilityRepository,
                                          materialServices, userDTO, null);
            deliveryNoteLine.setAlreadyCanceledQty(deliveryNoteLine.getAlreadyCanceledQty() + cancelledMaterialLine.getQuantity());
            deliveryNoteLine.setReceivedQuantity(deliveryNoteLine.getReceivedQuantity() - cancelledMaterialLine.getQuantity());

            // update qty on subLine based on directSend type
            DeliveryNoteSubLine subLine = deliveryNoteLine.getSubLine(cancelledMaterialLine.getDirectSend().isDirectSend());
            if (subLine != null) {
                subLine.setToReceiveQty(subLine.getToReceiveQty() - cancelledMaterialLine.getQuantity());
            }

            RequestGroup requestGroup = cancelledMaterialLine.getRequestGroup();
            List<MaterialLine> requestGroupMaterialLines = null;
            if (requestGroup != null) {
                requestGroupMaterialLines = requestGroup.getMaterialLines();
                requestGroupMaterialLines.remove(cancelledMaterialLine);
                if (requestGroupMaterialLines == null || requestGroupMaterialLines.isEmpty()) {
                    requestHeaderRepository.deleteRequestList(requestGroup.getRequestList());
                    materialServices.deleteRequestGroup(requestGroup);
                }
            }

            MaterialServicesHelper.removePlacement(cancelledMaterialLine, warehouseServices);
            cancelledMaterialLine.setDeliveryNoteLine(null);
            cancelledMaterialLine.setRequestGroup(null);
            cancelledMaterialLine.setPickList(null);
            MaterialLineStatusHelper.createTraceabilityLog(cancelledMaterialLine, traceabilityRepository, "GR Cancelled", null, userDTO.getId(),
                                                           userDTO.getUserName(), GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE);
        }
        return cancelledMaterialLine;
    }

    public static MaterialLine markAsDecreased(OrderLine orderLine, MaterialLine materialLine, long quantityToDecrease, UserDTO userDTO,
            MaterialServices materialServices, WarehouseServices warehouseServices, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        MaterialLine qtyDecreasedMaterialLine = null;
        quantityToDecrease = Math.min(quantityToDecrease - orderLine.getAlreadyDecreasedQty(), materialLine.getQuantity());
        if (quantityToDecrease > 0) {
            // avoid below trace if GPS send order for decreased qty for first time
            List<MaterialLineStatus> avoidTraceForMLStatus = new ArrayList<MaterialLineStatus>();
            avoidTraceForMLStatus.add(MaterialLineStatus.REQUISITION_SENT);
            qtyDecreasedMaterialLine = split(materialLine, MaterialLineStatus.QTY_DECREASED, quantityToDecrease, requestHeaderRepository,
                                             traceabilityRepository, materialServices, userDTO, avoidTraceForMLStatus);
            if (qtyDecreasedMaterialLine != null) {
                orderLine.setAlreadyDecreasedQty(orderLine.getAlreadyDecreasedQty() + quantityToDecrease);
            }
        }
        return qtyDecreasedMaterialLine;
    }

    public static void updateMaterialLinesWithOrderNo(List<MaterialLine> materialLines, String orderNo) {
        if (materialLines != null && !materialLines.isEmpty()) {
            for (MaterialLine materialLine : materialLines) {
                // if no order is already assigned
                if (materialLine.getMaterial().getMaterialType() != MaterialType.USAGE_REPLACED && StringUtils.isEmpty(materialLine.getOrderNo())) {
                    materialLine.setOrderNo(orderNo);
                }
            }
        }
    }

    public static void cancelInternalOrderPlacedMaterialLines(MaterialLine materialLine, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository, UserDTO userDTO) {
        cancelOrderPlacedMaterialLines(materialLine, requestHeaderRepository, traceabilityRepository, userDTO);
    }

    public static void cancelExternalOrderPlacedMaterialLines(MaterialLine materialLine, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository, UserDTO userDTO) {
        String action = "Order Cancelled";
        String actionDetail = MaterialLineStatusHelper.fetchActionDetail(materialLine, action);
        cancelOrderPlacedMaterialLines(materialLine, requestHeaderRepository, traceabilityRepository, userDTO);
        if (userDTO != null) {
            createTraceabilityLog(materialLine, traceabilityRepository, action, actionDetail, ProcurementServicesHelper.GPS, ProcurementServicesHelper.GPS,
                                  GloriaTraceabilityConstants.ORDER_CANCELLED);
        }
    }

    private static void cancelOrderPlacedMaterialLines(MaterialLine materialLine, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository, UserDTO userDTO) {
        MaterialType materialType = materialLine.getMaterial().getMaterialType();
        if (materialType == MaterialType.MODIFIED) {
            materialLine.setOrderCancelled(true);
            updateMaterialLineStatus(materialLine, MaterialLineStatus.QTY_DECREASED, "Quantity decreased",
                                     GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE, userDTO, traceabilityRepository, false);
        } else if (materialType == MaterialType.USAGE || materialType == MaterialType.ADDITIONAL_USAGE) {
            materialLine.setOrderNo(null);
            updateMaterialLineStatus(materialLine, MaterialLineStatus.WAIT_TO_PROCURE, "Wait to procure",
                                     GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE, userDTO, traceabilityRepository, false);
        } else if (materialType == MaterialType.ADDITIONAL || materialType == MaterialType.RELEASED) {
            materialLine.getMaterial().getMaterialLine().remove(materialLine);
            requestHeaderRepository.deleteMaterialLine(materialLine);
        }
    }

    public static MaterialLine markForQI(MaterialLine materialLineToMark, long quantity, MaterialServices materialServices,
            MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository, UserDTO user) throws GloriaApplicationException {
        MaterialLine materialLineMarked = split(materialLineToMark, MaterialLineStatus.MARKED_INSPECTION, quantity, requestHeaderRepository,
                                                traceabilityRepository, materialServices, user, null);
        materialLineMarked.getStatus().setInspectionStatus(materialLineMarked);
        return materialLineMarked;
    }

    public static MaterialLine unmarkFromQI(MaterialLine materialLineToUnMark, MaterialLineStatus previousStatus, long quantity,
            MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository, UserDTO user) throws GloriaApplicationException {
        MaterialLine materialLineUnmarked = merge(materialLineToUnMark, previousStatus, quantity, requestHeaderRepository, traceabilityRepository, user, null);
        materialLineUnmarked.getStatus().setInspectionStatus(materialLineUnmarked);
        return materialLineUnmarked;
    }

    public static MaterialLine resetInspectionStatus(MaterialLine materialLine) {
        materialLine.setInspectionStatus(null);
        return materialLine;
    }

    public static void traceAssignUnAssignReturn(UserDTO assignToUserDTO, UserDTO loggedInUserDTO, String teamName, String action,
            List<Material> materialsRelatedToHeader, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        if (loggedInUserDTO != null) {
            String actionDetail = null;
            if (action.startsWith("Assigned") && assignToUserDTO != null) {
                if (!StringUtils.isEmpty(teamName)) {
                    teamName = " (" + teamName + ")";
                }
                actionDetail = assignToUserDTO.getId() + " - " + assignToUserDTO.getUserName() + teamName;
            }
            for (Material material : materialsRelatedToHeader) {
                for (MaterialLine materialLine : material.getMaterialLine()) {
                    MaterialLineStatusHelper.createTraceabilityLog(materialLine, traceabilityRepository, action, actionDetail, loggedInUserDTO.getId(),
                                                                   loggedInUserDTO.getUserName(), null);
                }
            }
        }
    }

    public static void traceProcureRequestMessage(String senderLogicalId, ChangeId changeId, TraceabilityRepository traceabilityRepository) {
        if ("PROTOM".equals(senderLogicalId)) {
            List<Material> addRemoveMaterialList = new ArrayList<Material>();
            if (changeId.getAddMaterials() != null) {
                addRemoveMaterialList.addAll(changeId.getAddMaterials());
            }
            if (changeId.getRemoveMaterials() != null) {
                addRemoveMaterialList.addAll(changeId.getRemoveMaterials());
            }
            if (addRemoveMaterialList.size() > 0) {
                for (Material material : addRemoveMaterialList) {
                    if (material.getMaterialHeader() != null) {
                        List<MaterialLine> materialLineList = material.getMaterialLine();
                        if (materialLineList != null) {
                            for (MaterialLine materialLine : materialLineList) {
                                String actionDetail = getProtomActionDetail("Change Wait", materialLine);
                                createTraceabilityLog(materialLine, traceabilityRepository, "PROTOM", actionDetail, "PROTOM", "PROTOM", null);
                            }
                        }
                    }
                }
            } else {
                // auto accept case
                List<MaterialHeaderVersion> materialHeaderVersionList = changeId.getMaterialHeaderVersions();
                if (materialHeaderVersionList != null) {
                    for (MaterialHeaderVersion materialHeaderVersion : materialHeaderVersionList) {
                        if (materialHeaderVersion != null) {
                            MaterialHeader materialHeader = materialHeaderVersion.getMaterialHeader();
                            if (materialHeader != null) {
                                List<Material> materialList = materialHeader.getMaterials();
                                if (materialList != null) {
                                    for (Material material : materialList) {
                                        List<MaterialLine> materialLineList = material.getMaterialLine();
                                        if (materialLineList != null) {
                                            for (MaterialLine materialLine : materialLineList) {
                                                String actionDetail = getProtomActionDetail("Auto Accepted", materialLine);
                                                createTraceabilityLog(materialLine, traceabilityRepository, "PROTOM", actionDetail, "PROTOM", "PROTOM", null);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static String getProtomActionDetail(String changeState, MaterialLine materialLine) {
        String actionDetail = changeState + ", ";
        MaterialHeader materialHeader = materialLine.getMaterial().getMaterialHeader();
        if (materialHeader != null) {
            String mtrlRequestVersion = materialLine.getMaterial().getMtrlRequestVersionAccepted();
            String buildName = materialHeader.getBuildName() == null ? "" : materialHeader.getBuildName();
            String referenceId = materialHeader.getReferenceId() == null ? "" : materialHeader.getReferenceId();
            actionDetail = actionDetail + "Mtrl Req Ver : " + mtrlRequestVersion + ", Phase : " + buildName + ", Test Obj : " + referenceId;
        }
        return actionDetail;
    }

}
