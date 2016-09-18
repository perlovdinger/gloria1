package com.volvo.gloria.procurematerial.b.beans;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.CurrencyUtil;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.purchaseorder.c.dto.PurchaseOrderScheduleDTO;
import com.volvo.gloria.common.purchaseorder.c.dto.StandAloneOrderHeaderDTO;
import com.volvo.gloria.common.purchaseorder.c.dto.StandAloneOrderLineDTO;
import com.volvo.gloria.common.purchaseorder.c.dto.SyncPurchaseOrderTypeDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.DeliveryServices;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.c.CompleteType;
import com.volvo.gloria.procurematerial.c.PriceType;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.DeliverySchedule;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.Order;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLineLastModified;
import com.volvo.gloria.procurematerial.d.entities.OrderLineVersion;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.Requisition;
import com.volvo.gloria.procurematerial.d.entities.status.orderLine.OrderLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.orderLine.OrderLineStatusHelper;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.util.ProcurementHelper;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.warehouse.b.WarehouseServices;

public final class ProcurementServicesHelper {
    
    public static final String GPS = "GPS";
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcurementServicesHelper.class);
    private static final int FIRST_VERSION_NO = 1;
    
    private ProcurementServicesHelper() {
    }
    
    public static Order createOrder(Order order, SyncPurchaseOrderTypeDTO syncPurchaseOrderTypeDTO, List<StandAloneOrderHeaderDTO> standAloneOrderHeaderDTOs,
            CommonServices commonServices, UserServices userServices, DeliveryServices deliveryServices, ProcureLineRepository procureLineRepository,
            MaterialServices materialServices, WarehouseServices warehouseServices, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository, UserDTO userDTO) throws GloriaApplicationException {

        for (StandAloneOrderHeaderDTO standAloneOrderHeaderDTO : standAloneOrderHeaderDTOs) {
            List<StandAloneOrderLineDTO> standAloneOrderLineDTOs = standAloneOrderHeaderDTO.getStandAloneOrderLineDTO();
            setOrderNoAndSupplierId(order, standAloneOrderHeaderDTO);

            for (StandAloneOrderLineDTO standAloneOrderLineDTO : standAloneOrderLineDTOs) {
                String requisitionId = standAloneOrderLineDTO.getRequisitionIds();
                Requisition requisition = procureLineRepository.findRequisitionByRequisitionId(requisitionId);

                OrderLine orderLine = deliveryServices.saveOrderLine(setAtrributeToOrderline(standAloneOrderLineDTO, procureLineRepository, requisition));
                orderLine.setOrder(order);
                order.getOrderLines().add(orderLine);

                List<OrderLineVersion> orderLineVersions = new ArrayList<OrderLineVersion>();
                orderLine.setOrderLineVersions(orderLineVersions);
                OrderLineVersion orderLineVersion = createOrderLineVersion(deliveryServices, standAloneOrderHeaderDTO, standAloneOrderLineDTO);
                // handle unitprice in EURO
                orderLineVersion.setUnitPriceInEuro(CurrencyUtil.convertUnitPriceToEUR(orderLineVersion.getUnitPrice(), orderLineVersion.getCurrency(),
                                                                                       commonServices));
                orderLine.setPossibleToReceiveQuantity(orderLineVersion.getQuantity());
                orderLineVersion.setOrderLine(orderLine);
                orderLineVersions.add(orderLineVersion);
                orderLine.setCurrent(orderLineVersion);
                assignOrderLineToMaterial(orderLine, deliveryServices);
                handleOrderQuantityAmmendmentFromGPS(orderLine, requisition.getQuantity(), orderLineVersion.getQuantity(), materialServices, warehouseServices,
                                                     requestHeaderRepository, traceabilityRepository, userDTO);
            }
            ProcurementHelper.setOrderAttributes(order, standAloneOrderHeaderDTO, commonServices, userServices);
        }
        return order;
    }

    private static void setOrderNoAndSupplierId(Order order, StandAloneOrderHeaderDTO standAloneOrderHeaderDTO) {
        String orderNo = standAloneOrderHeaderDTO.getOrderNo();
        order.setOrderNo(orderNo);
        order.setSupplierId(standAloneOrderHeaderDTO.getSupplierId());
    }

    private static void assignOrderLineToMaterial(OrderLine orderLine, DeliveryServices deliveryServices) {
        if (orderLine.getRequisitionId() != null) {
            deliveryServices.assignMaterialToOrderLine(orderLine.getRequisitionId(), orderLine);
        }
    }
    
    public static void handleOrderQuantityAmmendmentFromGPS(OrderLine orderLine, long orderedQuantity, long quantityAmmendedFromGPS,
            MaterialServices materialServices, WarehouseServices warehouseServices, MaterialHeaderRepository requestHeaderRepository,
            TraceabilityRepository traceabilityRepository, UserDTO userDTO) throws GloriaApplicationException {
        if (orderedQuantity != quantityAmmendedFromGPS) {
            orderLine.getOrderLineLastModified().setAlertQuantity(true);
        }
        
        long alreadyReceivedQty = orderLine.getReceivedQuantity();

        // if the incoming change in QTY is less than the RECEIVED_QTY for orderline, then POSSIBLE_RECEIVE_QTY = RECEIVED_QTY
        if (alreadyReceivedQty > quantityAmmendedFromGPS) {
            orderLine.setPossibleToReceiveQuantity(alreadyReceivedQty);
            orderLine.setStatus(OrderLineStatus.COMPLETED);
            orderLine.setCompleteType(CompleteType.COMPLETE);
        } else {
            orderLine.setPossibleToReceiveQuantity(quantityAmmendedFromGPS);
        }
        OrderLineStatusHelper.handleMaterialsWhenPossibleToReceiveQuantityIsDecreased(orderedQuantity - quantityAmmendedFromGPS, orderLine, userDTO,
                                                                                      materialServices, warehouseServices, requestHeaderRepository,
                                                                                      traceabilityRepository);
    }
    
    protected static OrderLineVersion createOrderLineVersion(DeliveryServices deliveryServices, StandAloneOrderHeaderDTO standAloneOrderHeaderDTO,
            StandAloneOrderLineDTO standAloneOrderLineDTO) {
        OrderLineVersion orderLineVersion = new OrderLineVersion();
        orderLineVersion.setRevisionId(standAloneOrderHeaderDTO.getRevisionId());
        orderLineVersion.setUnitPrice(standAloneOrderLineDTO.getAmount());
        if (!StringUtils.isEmpty(standAloneOrderLineDTO.getPriceType())) {
            orderLineVersion.setPriceType(PriceType.valueOf(standAloneOrderLineDTO.getPriceType()));
        }
        orderLineVersion.setCurrency(standAloneOrderLineDTO.getCurrency());
        orderLineVersion.setPartVersion(standAloneOrderLineDTO.getPartVersion());
        orderLineVersion.setVersionNo(FIRST_VERSION_NO);
        orderLineVersion.setPerQuantity(standAloneOrderLineDTO.getPerQuantity());
        orderLineVersion.setBuyerId(standAloneOrderLineDTO.getBuyerId());
        orderLineVersion.setBuyerSecurityId(standAloneOrderLineDTO.getBuyerSecurityId());
        orderLineVersion.setBuyerName(standAloneOrderLineDTO.getBuyerName());
        orderLineVersion.setBuyerEmail(standAloneOrderLineDTO.getBuyerEmail());
        List<PurchaseOrderScheduleDTO> purchaseOrderSchedules = standAloneOrderLineDTO.getPurchaseOrderSchedule();
        if (purchaseOrderSchedules != null && !purchaseOrderSchedules.isEmpty()) {
            PurchaseOrderScheduleDTO purchaseOrderScheduleFirstElement = purchaseOrderSchedules.get(0);
            String shipToArrive = purchaseOrderScheduleFirstElement.getShipToArrive();
            try {
                orderLineVersion.setOrderStaDate(DateUtil.getStringAsDate(shipToArrive));
            } catch (ParseException e) {
                LOGGER.error("Couldn't parse shipToArriveDate: " + shipToArrive + e.getMessage());
            }
            if (purchaseOrderScheduleFirstElement.getQuantity() != null) {
                orderLineVersion.setQuantity(purchaseOrderScheduleFirstElement.getQuantity());
            }
        }
        return deliveryServices.saveOrderLineVersion(orderLineVersion);
    }
    
    
    protected static OrderLine setAtrributeToOrderline(StandAloneOrderLineDTO standAloneOrderLineDTO, ProcureLineRepository procureLineRepository,
            Requisition requisition) {
        OrderLine orderLine = new OrderLine();
        orderLine.setPartAffiliation(standAloneOrderLineDTO.getPartQualifier());
        orderLine.setPartNumber(standAloneOrderLineDTO.getPartNumber());
        orderLine.setPartName(standAloneOrderLineDTO.getPartDescription());
        orderLine.setSupplierPartNo(standAloneOrderLineDTO.getSupplierPartNumber());
        try {
            orderLine.setPriceStartDateTime(DateUtil.getStringAsDate(standAloneOrderLineDTO.getUnitPriceTimePeriod()));
        } catch (ParseException e) {
            throw new GloriaSystemException(e, e.getLocalizedMessage());
        }
        orderLine.setCountryOfOrigin(standAloneOrderLineDTO.getCountryOfOrigin());
        orderLine.setCurrency(standAloneOrderLineDTO.getCurrency());
        orderLine.setUnitOfMeasure(standAloneOrderLineDTO.getUnitOfMeasure());
        orderLine.setShipToPartyId(standAloneOrderLineDTO.getShipToPartyId());
        orderLine.setShipToPartyName(standAloneOrderLineDTO.getShipToPartyName());
        orderLine.setFreightTermCode(standAloneOrderLineDTO.getFreightTermCode());
        orderLine.setPaymentTerm(standAloneOrderLineDTO.getPaymentTerm());
        orderLine.setRequisitionId(requisition.getRequisitionId());

        if (requisition != null) {
            orderLine.setProjectId(requisition.getProjectId());
        }

        orderLine.setBuyerPartyId(standAloneOrderLineDTO.getBuyerPartyId());
        orderLine.setStatus(OrderLineStatus.PLACED);

        List<PurchaseOrderScheduleDTO> purchaseOrderSchedules = standAloneOrderLineDTO.getPurchaseOrderSchedule();

        long orderLineQuantity = 0;
        List<DeliverySchedule> deliverySchedules = new ArrayList<DeliverySchedule>();
        orderLine.setDeliverySchedule(deliverySchedules);
        for (PurchaseOrderScheduleDTO purchaseOrderScheduleDTO : purchaseOrderSchedules) {
            DeliverySchedule deliverySchedule = setAttributeToDeliverySchedule(purchaseOrderScheduleDTO);
            deliverySchedule.setOrderLine(orderLine);
            deliverySchedules.add(deliverySchedule);
            // Sum of all DeliverySchedule:expectedQty
            orderLineQuantity += (orderLineQuantity + purchaseOrderScheduleDTO.getQuantity());
            orderLine.setEarliestExpectedDate(deliverySchedule.getExpectedDate());
        }

        OrderLineLastModified orderLineLastModified = new OrderLineLastModified();
        orderLine.setOrderLineLastModified(orderLineLastModified);

        return orderLine;
    }
    
    private static DeliverySchedule setAttributeToDeliverySchedule(PurchaseOrderScheduleDTO purchaseOrderScheduleDTO) {
        DeliverySchedule deliverySchedule = new DeliverySchedule();

        try {
            deliverySchedule.setExpectedDate(DateUtil.getStringAsDate(purchaseOrderScheduleDTO.getShipToArrive()));
        } catch (ParseException e) {
            throw new GloriaSystemException(e, e.getLocalizedMessage());
        }

        deliverySchedule.setExpectedQuantity(purchaseOrderScheduleDTO.getQuantity());
        return deliverySchedule;
    }

    public static String getUniqueRequisitionString(Long requisitionIdSequence) throws GloriaApplicationException {
        if (requisitionIdSequence != null) {
            return "G" + requisitionIdSequence;
        } else {
            throw new GloriaApplicationException(null, "Something went wrong while generation requisitionIdSequence");
        }
    }

    public static String getUniqueDispatchNoteNoString(Long dispatchNoteNoSequence) throws GloriaApplicationException {
        if (dispatchNoteNoSequence != null) {
            return "D" + dispatchNoteNoSequence;
        } else {
            throw new GloriaApplicationException(null, "Something went wrong while generation dispatchNoteNoSequence");
        }
    }

    public static String getUniquePickListCodeString(Long pickListCodeSequence) throws GloriaApplicationException {
        if (pickListCodeSequence != null) {
            return "P" + pickListCodeSequence;
        } else {
            throw new GloriaApplicationException(null, "Something went wrong while generation pickListCodeSequence");
        }
    }
    
    public static String replaceReferenceId(String referenceIds, String referenceIdToBeReplaced, String referenceIdToReplaceWith) {
        if (StringUtils.isNotBlank(referenceIds)) {
            return StringUtils.replace(referenceIds, referenceIdToBeReplaced, referenceIdToReplaceWith);
        }
        return referenceIdToBeReplaced;
    }

    public static void replaceDeliveryNoteLineReferenceIds(String referenceId, Material material, String previousReferenceId) {
        OrderLine orderLine = material.getOrderLine();
        if (orderLine != null) {
            List<DeliveryNoteLine> deliveryNoteLines = orderLine.getDeliveryNoteLines();
            if (deliveryNoteLines != null && !deliveryNoteLines.isEmpty()) {
                for (DeliveryNoteLine deliveryNoteLine : deliveryNoteLines) {
                    deliveryNoteLine.setReferenceIds(ProcurementServicesHelper.replaceReferenceId(deliveryNoteLine.getReferenceIds(), previousReferenceId,
                                                                                                  referenceId));
                }
            }
        }
    }

    public static void replaceProcurelineReferenceIds(String previousReferenceId, String referenceId, Material material, ProcureLine procureLine) {
        if (!material.getMaterialType().isAdditional()) {
            procureLine.setReferenceIds(ProcurementServicesHelper.replaceReferenceId(procureLine.getReferenceIds(), previousReferenceId, referenceId));
        }
    }
}
