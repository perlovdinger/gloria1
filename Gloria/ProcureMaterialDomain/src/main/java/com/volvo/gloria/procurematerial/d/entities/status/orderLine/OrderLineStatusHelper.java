package com.volvo.gloria.procurematerial.d.entities.status.orderLine;

import java.util.ArrayList;
import java.util.List;

import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.d.entities.Traceability;
import com.volvo.gloria.common.d.entities.TraceabilityType;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.c.CompleteType;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLineVersion;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.Requisition;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineStatus;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.util.migration.c.dto.OrderMigrationDTO;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.c.SAPParam;
import com.volvo.gloria.warehouse.b.WarehouseServices;

/**
 * helper class for OrderLine status.
 * 
 */
public final class OrderLineStatusHelper {

    private OrderLineStatusHelper() {
    }

    public static OrderLine revoke(OrderLine orderLine, long quantityCancelled, OrderRepository orderRepository) {
        long receivedQuantityAfterCancel = orderLine.getReceivedQuantity() - quantityCancelled;
        orderLine.setReceivedQuantity(receivedQuantityAfterCancel);
        ProcureLine procureLine = orderLine.getProcureLine();
        if (receivedQuantityAfterCancel > 0) {
            orderLine.setStatus(OrderLineStatus.RECEIVED_PARTLY);
            procureLine.setStatus(ProcureLineStatus.RECEIVED_PARTLY);
        } else {
            orderLine.setStatus(OrderLineStatus.PLACED);
            procureLine.setStatus(ProcureLineStatus.PLACED);
        }
        orderLine.setCompleteType(null);
        return orderLine;
    }

    public static OrderLine receive(OrderLine orderLine, long receivedQty) {
        long orderLineReceivedQty = orderLine.getReceivedQuantity();
        long possibleToReceiveQty = orderLine.getPossibleToReceiveQuantity() - orderLineReceivedQty;

        ProcureLine procureLine = orderLine.getProcureLine();
        if (receivedQty >= possibleToReceiveQty) {
            orderLine.setStatus(OrderLineStatus.COMPLETED);
            orderLine.setCompleteType(CompleteType.RECEIVED);
            procureLine.setStatus(ProcureLineStatus.RECEIVED);
        } else {
            orderLine.setStatus(OrderLineStatus.RECEIVED_PARTLY);
            procureLine.setStatus(ProcureLineStatus.RECEIVED_PARTLY);
        }

        orderLine.setReceivedQuantity(orderLineReceivedQty + receivedQty);
        
        return orderLine;
    }

    public static OrderLine markAsComplete(OrderLine orderLine, UserDTO userDTO, MaterialServices materialServices, WarehouseServices warehouseServices,
            MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        long quantityToDecrease = orderLine.getPossibleToReceiveQuantity() - orderLine.getReceivedQuantity();
        orderLine.setStatus(OrderLineStatus.COMPLETED);
        orderLine.setCompleteType(CompleteType.COMPLETE);
        orderLine.setPossibleToReceiveQuantity(orderLine.getReceivedQuantity());
        handleMaterialsWhenPossibleToReceiveQuantityIsDecreased(quantityToDecrease, orderLine, userDTO, materialServices, warehouseServices,
                                                                requestHeaderRepository, traceabilityRepository);
        materialServices.createAndSendProcessPurchaseOrder(orderLine.getOrder(), SAPParam.ACTION_CHANGE);
        createTracebilityForOrderLine(traceabilityRepository, orderLine, "Marked as complete",
                                      "", userDTO.getId(), userDTO.getUserName());
        return orderLine;
    }

    public static void handleMaterialsWhenPossibleToReceiveQuantityIsDecreased(long quantityToDecrease, OrderLine orderLine, UserDTO userDTO,
            MaterialServices materialServices, WarehouseServices warehouseServices, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository) throws GloriaApplicationException {
        if (quantityToDecrease > 0) {
            orderLine.setAlreadyDecreasedQty(0L);
            List<Material> materials = materialServices.getMaterialLinesForOrderLine(orderLine.getOrderLineOID());
            for (Material material : materials) {
                List<MaterialLine> materialLines = material.getMaterialLine();
                if (materialLines != null && !materialLines.isEmpty()) {
                    for (int idx = materialLines.size() - 1; idx >= 0; idx--) {
                        MaterialLine materialLine = materialLines.get(idx);
                        if (!materialLine.getStatus().isRemovedDb() && materialLine.getStatus() != MaterialLineStatus.QTY_DECREASED) {
                            materialLine.getStatus().markAsDecreased(orderLine, materialLine, quantityToDecrease, userDTO, materialServices, warehouseServices,
                                                                     requestHeaderRepository, traceabilityRepository);
                        }
                    }
                }
            }
        }
    }


    public static OrderLine cancel(OrderLine orderLine, ProcurementServices procurementServices, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository, UserDTO userDTO) throws GloriaApplicationException {

        List<Material> cancelledMaterialsToGroup = new ArrayList<Material>();
        ProcureLine existingProcureLine = procurementServices.findProcureLineByRequisitionId(orderLine.getRequisitionId());
        if (existingProcureLine != null) {
            orderLine.setProcureLine(existingProcureLine);
            List<Material> materials = existingProcureLine.getMaterials();
            if (materials != null && !materials.isEmpty()) {
                for (int idx = materials.size() - 1; idx >= 0; idx--) {
                    Material material = materials.get(idx);
                    List<Material> cancelledMaterials = material.getMaterialType().cancelMaterial(material, requestHeaderRepository, traceabilityRepository,
                                                                                                  orderLine, userDTO);
                    if (cancelledMaterials != null && !cancelledMaterials.isEmpty()) {
                        cancelledMaterialsToGroup.addAll(cancelledMaterials);
                    }
                }
                existingProcureLine.getMaterials().removeAll(cancelledMaterialsToGroup);
                procurementServices.regroupMaterialsOnOrderCancel(existingProcureLine, cancelledMaterialsToGroup);
            }
            existingProcureLine.setStatus(ProcureLineStatus.CANCELLED);
            Requisition requisition = existingProcureLine.getRequisition();
            if (requisition != null) {
                requisition.setCancelled(true);
            }
            orderLine.setPossibleToReceiveQuantity(orderLine.getReceivedQuantity());
            orderLine.setStatus(OrderLineStatus.COMPLETED);
            orderLine.setCompleteType(CompleteType.CANCELLED);
        }
        return orderLine;
    }

    public static long findOverDeliveredQtyForPlacedReceivedPartly(OrderMigrationDTO orderMigrationDTO) {
        if (orderMigrationDTO.getPossibleToReceiveQuantity() > orderMigrationDTO.getTotalTestobjectsQty()) {
            return orderMigrationDTO.getPossibleToReceiveQuantity() - orderMigrationDTO.getTotalTestobjectsQty();
        }
        return 0;
    }

    public static Traceability createTracebilityForOrderLine(TraceabilityRepository traceabilityRepository, OrderLine orderLine, String action,
            String actionDetail, String userId, String userName) {
        Traceability traceability = new Traceability(TraceabilityType.DELIVERY_CONTROLLER);
        traceability.setUserId(userId);
        traceability.setUserName(userName);
        traceability.setAction(action);
        traceability.setActionDetail(actionDetail);
        traceability.setOrderLineOID(orderLine.getOrderLineOID());
        traceability.setInternalExternal(orderLine.getOrder().getInternalExternal().toString());
        traceability.setLoggedTime(DateUtil.getUTCTimeStamp());
        traceability.setOlStatus(orderLine.getStatus().toString());

        OrderLineVersion currentOLVersion = orderLine.getCurrent();
        traceability.setOlQuantity(currentOLVersion.getQuantity());

        traceability.setOrderDate(orderLine.getOrder().getOrderDateTime());
        traceability.setOrderLineOID(orderLine.getOrderLineOID());
        traceability.setOrderNo(orderLine.getOrder().getOrderNo());
        traceabilityRepository.save(traceability);
        return traceability;
    }
}
