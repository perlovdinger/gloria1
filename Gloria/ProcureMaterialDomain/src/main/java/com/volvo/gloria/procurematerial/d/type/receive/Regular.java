package com.volvo.gloria.procurematerial.d.type.receive;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.DangerousGoodsRepository;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.DeliveryServices;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.c.DeliveryNoteLineStatus;
import com.volvo.gloria.procurematerial.c.dto.DeliveryNoteDTO;
import com.volvo.gloria.procurematerial.c.dto.DeliveryNoteLineDTO;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNote;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.Order;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLineVersion;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.util.DeliveryHelper;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.warehouse.b.WarehouseServices;
import com.volvo.gloria.warehouse.c.ZoneType;
import com.volvo.gloria.warehouse.d.entities.Zone;

/**
 * Operation for type REGULAR.
 * 
 */
public class Regular extends ReceiveTypeDefaultOperations {
    @Override
    public String nextLocation(DeliveryNoteLine deliveryNoteLine, boolean directSend, WarehouseServices warehouseServices, DeliveryServices deliveryServices)
            throws GloriaApplicationException {
        if (deliveryNoteLine.isSendToQI() && deliveryNoteLine.getStatus().equals(DeliveryNoteLineStatus.IN_WORK)) {
            Zone qiZone = warehouseServices.findZoneCodes(ZoneType.QI, deliveryNoteLine.getDeliveryNote().getWhSiteId());
            if (qiZone != null) {
                return qiZone.getCode();
            }
            return "";
        }
        return deliveryServices.suggestNextLocation(deliveryNoteLine.getDeliveryNote().getWhSiteId(), deliveryNoteLine.getPartNumber(),
                                                    deliveryNoteLine.getPartVersion(), directSend, 
                                                    deliveryNoteLine.getPartModification(), deliveryNoteLine.getPartAffiliation());
    }

    @Override
    public DeliveryNote createDeliveryInformation(DeliveryNoteDTO deliveryNoteDTO, DeliveryNoteRepository deliveryNoteRepository,
            OrderRepository orderRepository, DangerousGoodsRepository dangerousGoodsRepository, MaterialHeaderRepository requestHeaderRepository)
            throws GloriaApplicationException {
        DeliveryNote deliveryNote = ReceiveTypeHelper.createDeliveryNoteForRegular(deliveryNoteDTO, deliveryNoteRepository);
        return ReceiveTypeHelper.createDeliveryNoteLinesForRegular(deliveryNote, deliveryNoteRepository, orderRepository, dangerousGoodsRepository);
    }

    @Override
    public void receive(long deliveryNoteId, UserDTO user, DeliveryNoteRepository deliveryNoteRepository, OrderRepository orderRepository,
            MaterialHeaderRepository requestHeaderRepository, MaterialServices materialServices, CommonServices commonServices,
            TraceabilityRepository traceabilityRepository, WarehouseServices warehouseServices) throws GloriaApplicationException {
        ReceiveTypeHelper.receiveRegular(deliveryNoteId, user, deliveryNoteRepository, orderRepository, requestHeaderRepository, materialServices,
                                         commonServices, traceabilityRepository, warehouseServices);
    }
        
    @Override
    public void setOrderOrMaterialInfo(DeliveryNoteLine deliveryNoteLine, DeliveryNoteLineDTO deliveryNoteLineDTO) {
        OrderLine orderLine = deliveryNoteLine.getOrderLine();
        if (orderLine != null) {
            deliveryNoteLineDTO.setPartAffiliation(orderLine.getPartAffiliation());
            deliveryNoteLineDTO.setPartNumber(orderLine.getPartNumber());
            deliveryNoteLineDTO.setPartName(orderLine.getPartName());
            List<OrderLineVersion> orderLineVersions = orderLine.getOrderLineVersions();
            if (orderLineVersions != null && !orderLineVersions.isEmpty()) {
                DeliveryHelper.sortOrderLineVersions(orderLineVersions);
                OrderLineVersion orderLineVersion = orderLineVersions.get(0);
                deliveryNoteLineDTO.setOrderLineQuantity(orderLineVersion.getQuantity());                
                String partVersionInDeliveryNoteLine = deliveryNoteLine.getPartVersion();
                if (partVersionInDeliveryNoteLine != null) {
                    deliveryNoteLineDTO.setPartVersion(partVersionInDeliveryNoteLine);
                } else {
                    deliveryNoteLineDTO.setPartVersion(orderLineVersion.getPartVersion());
                }
            }
            deliveryNoteLineDTO.setOrderLineId(orderLine.getOrderLineOID());
            if (deliveryNoteLine.getStatus() == DeliveryNoteLineStatus.RECEIVED) {
                deliveryNoteLineDTO.setReceivedQuantity(deliveryNoteLine.getReceivedQuantity());
                deliveryNoteLineDTO.setPossibleToReceiveQuantity(deliveryNoteLine.getPossibleToReceiveQty());
            } else {
                deliveryNoteLineDTO.setReceivedQuantity(orderLine.getReceivedQuantity());
                deliveryNoteLineDTO.setPossibleToReceiveQuantity(orderLine.getPossibleToReceiveQuantity());
            }
            deliveryNoteLineDTO.setOrderLineReceivedQuantity(orderLine.getReceivedQuantity());
            Order order = orderLine.getOrder();
            deliveryNoteLineDTO.setSupplierId(order.getSupplierId());
            deliveryNoteLineDTO.setSupplierName(order.getSupplierName());
            deliveryNoteLineDTO.setOrderNo(order.getOrderNo());
            
            ProcureLine procureLine = orderLine.getProcureLine();
            if (procureLine != null) {
                deliveryNoteLineDTO.setFreeText(procureLine.getProcureInfo());
            }
            deliveryNoteLineDTO.setOrderLineId(orderLine.getOrderLineOID());
            deliveryNoteLineDTO.setOrderLineVersion(orderLine.getVersion());
            deliveryNoteLineDTO.setUnitOfMeasure(orderLine.getUnitOfMeasure());
            deliveryNoteLineDTO.setHasNotes(!StringUtils.isEmpty(deliveryNoteLine.getProcureInfo()) || !StringUtils.isEmpty(orderLine.getFreeText()));
            deliveryNoteLineDTO.setPartAlias(orderLine.getSupplierPartNo());
            if (orderLine.getQiMarking() != null) {
                deliveryNoteLineDTO.setQiMarking(orderLine.getQiMarking().name());
            }
            List<Material> materials = orderLine.getMaterials();
            if (materials != null && !materials.isEmpty()) {
                MaterialHeader materialHeader = materials.get(0).getMaterialHeader();
                if (materialHeader != null) {
                    deliveryNoteLineDTO.setReferenceId(materialHeader.getReferenceId());
                }
            }
        }
    }
}
