/*
 * Copyright 2014 Volvo Information Technology AB 
 * 
 * Licensed under the Volvo IT Corporate Source License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *      http://java.volvo.com/licenses/CORPORATE-SOURCE-LICENSE 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.volvo.gloria.procurematerial.b.beans;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.authorization.c.TeamType;
import com.volvo.gloria.authorization.d.entities.Team;
import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.DeliveryLogDTO;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.d.entities.QualityDocument;
import com.volvo.gloria.common.repositories.b.DangerousGoodsRepository;
import com.volvo.gloria.common.repositories.b.QualityDocumentRepository;
import com.volvo.gloria.common.repositories.b.TeamRepository;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.financeProxy.b.GoodsReceiptSender;
import com.volvo.gloria.procurematerial.b.DeliveryNoteTransformer;
import com.volvo.gloria.procurematerial.b.DeliveryServices;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.c.DeliveryNoteLineStatus;
import com.volvo.gloria.procurematerial.c.DeliveryStatusFlag;
import com.volvo.gloria.procurematerial.c.EventType;
import com.volvo.gloria.procurematerial.c.QiMarking;
import com.volvo.gloria.procurematerial.c.dto.DeliveryNoteDTO;
import com.volvo.gloria.procurematerial.c.dto.DeliveryNoteLineDTO;
import com.volvo.gloria.procurematerial.c.dto.DeliveryNoteSubLineDTO;
import com.volvo.gloria.procurematerial.c.dto.DeliveryScheduleDTO;
import com.volvo.gloria.procurematerial.c.dto.GoodsReceiptLineDTO;
import com.volvo.gloria.procurematerial.c.dto.OrderDTO;
import com.volvo.gloria.procurematerial.c.dto.OrderLineDTO;
import com.volvo.gloria.procurematerial.c.dto.OrderLineLogDTO;
import com.volvo.gloria.procurematerial.c.dto.OrderLogDTO;
import com.volvo.gloria.procurematerial.d.entities.AttachedDoc;
import com.volvo.gloria.procurematerial.d.entities.DeliveryLog;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNote;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteSubLine;
import com.volvo.gloria.procurematerial.d.entities.DeliverySchedule;
import com.volvo.gloria.procurematerial.d.entities.GoodsReceiptLine;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialLastModified;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.Order;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLineLastModified;
import com.volvo.gloria.procurematerial.d.entities.OrderLineLog;
import com.volvo.gloria.procurematerial.d.entities.OrderLineVersion;
import com.volvo.gloria.procurematerial.d.entities.OrderLog;
import com.volvo.gloria.procurematerial.d.entities.ProblemDoc;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.QiDoc;
import com.volvo.gloria.procurematerial.d.entities.ReceiveDoc;
import com.volvo.gloria.procurematerial.d.entities.TransportLabel;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatusHelper;
import com.volvo.gloria.procurematerial.d.entities.status.orderLine.OrderLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.orderLine.OrderLineStatusHelper;
import com.volvo.gloria.procurematerial.d.type.directsend.DirectSendType;
import com.volvo.gloria.procurematerial.d.type.receive.ReceiveType;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.util.DeliveryHelper;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.DocumentDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.GloriaFormateUtil;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.b.PrintLabelServices;
import com.volvo.gloria.util.c.GloriaExceptionConstants;
import com.volvo.gloria.util.c.GloriaParams;
import com.volvo.gloria.util.c.PrintLabelTemplate;
import com.volvo.gloria.util.c.SAPParam;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.warehouse.b.WarehouseServices;
import com.volvo.gloria.warehouse.d.entities.BinLocation;
import com.volvo.gloria.warehouse.d.entities.Printer;
import com.volvo.gloria.warehouse.d.entities.QualityInspectionPart;
import com.volvo.gloria.warehouse.d.entities.QualityInspectionProject;
import com.volvo.gloria.warehouse.d.entities.QualityInspectionSupplier;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * Implementation class for DeliveryServices.
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class DeliveryServicesBean implements DeliveryServices {

    private static final int DELIVERY_NOTE_NO_MAX_LENGTH = 16;
    private static final String DOCUMENT_UPLOAD_LIMIT_EXCEEDED = " Document upload limit exceeded ";
    private static final String DOCUMENT_SIZE_EXCEEDS_5MB = " Document size exceeds 5MB ";

    private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryServicesBean.class);

    @Inject
    private DeliveryNoteRepository deliveryNoteRepository;

    @Inject
    private OrderRepository orderRepository;

    @Inject
    private MaterialHeaderRepository requestHeaderRepository;

    @Inject
    private UserServices userServices;

    @Inject
    private DeliveryNoteTransformer deliveryNoteTransformer;

    @Inject
    private PrintLabelServices printLabelServices;

    @Inject
    private WarehouseServices warehouseServices;

    @Inject
    private MaterialServices materialServices;

    @Inject
    private CommonServices commonServices;

    @Inject
    private DangerousGoodsRepository dangerousGoodsRepository;

    @Inject
    private TraceabilityRepository traceabilityRepository;

    @Inject
    private TeamRepository teamRepository;

    @Inject
    private GoodsReceiptSender goodsReceiptSender;
    
    @Inject
    private QualityDocumentRepository qualityDocumentRepository;

    @Override
    @PreAuthorize("hasAnyRole( 'DELIVERY','IT_SUPPORT')")
    public PageObject findOrders(PageObject pageObject, String deliveryManagerId, String userId, boolean isDeliveryControlModule)
            throws GloriaApplicationException {

        String deliveryFollowUpTeam = null;

        if (!StringUtils.isEmpty(deliveryManagerId)) {
            UserDTO userDTO = userServices.getUser(deliveryManagerId);
            deliveryFollowUpTeam = userDTO.getDelFollowUpTeam();
        }
        List<String> whSiteIds = userServices.getUserSiteIds(userId);

        return orderRepository.findOrders(pageObject, deliveryFollowUpTeam, whSiteIds, isDeliveryControlModule);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'DELIVERY','WH_DEFAULT','GR_ONLY','IT_SUPPORT')")
    public List<Order> findOrdersForWarehouse(String userId, String whSiteId, String orderNo) {
        userServices.validateAccess(userId, whSiteId);

        List<String> whSiteIds = new ArrayList<String>();
        whSiteIds.add(whSiteId);
        List<Order> orders = orderRepository.findOrdersForWarehouse(whSiteIds, orderNo);
        if (orders != null) {
            for (Order order : orders) {
                // lazy loading
                order.getOrderLines();
            }
        }
        return orders;
    }

    @Override
    public void addOrder(Order order) {
        orderRepository.save(order);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'DELIVERY')")
    public Order updateOrder(OrderDTO orderDTO) throws GloriaApplicationException {
        Long orderOid = orderDTO.getId();
        Order order = orderRepository.findOrderById(orderOid);

        if (orderDTO.getVersion() != order.getVersion()) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_UPDATION_OF_STALE_DATASET,
                                                 "This operation cannot be performed since the information seen in the page has already been updated.");
        }

        UserDTO userDTO = userServices.getUser(orderDTO.getDeliveryControllerUserId());
        order = orderRepository.save(order);
        // temporary solution until the GUI will be changed for GLO-5007
        List<OrderLine> orderLines = order.getOrderLines();
        OrderLine orderLine = orderLines.get(0);
        orderLine.setDeliveryControllerUserId(orderDTO.getDeliveryControllerUserId());
        orderLine.setDeliveryControllerUserName(userDTO.getUserName());

        return order;
    }

    @Override
    @PreAuthorize("hasAnyRole('DELIVERY','GR_ONLY','IT_SUPPORT')")
    public Order getOrderForOrderLine(long orderLineId) {
        return orderRepository.getOrderForOrderLine(orderLineId);
    }

    @Override
    public List<Order> findOrderBySuffix(String suffix) {
        return orderRepository.findOrderBysuffix(suffix);
    }

    @Override
    public void assignMaterialToOrderLine(String requisitionId, OrderLine orderLine) {
        List<Material> materials = requestHeaderRepository.findMaterialByRequisitionId(requisitionId);
        if (materials != null && !materials.isEmpty()) {
            for (Material material : materials) {
                material.setOrderNo(orderLine.getOrder().getOrderNo());
                MaterialLineStatusHelper.updateMaterialLinesWithOrderNo(material.getMaterialLine(), orderLine.getOrder().getOrderNo());
                if (!orderLine.getPartName().equals(material.getPartName())) {
                    material.setPartName(orderLine.getPartName());
                    material.setPartNameUpdated(true);
                }
                material.setOrderLine(orderLine);
            }
        }
        orderLine.setMaterials(materials);

        // QI
        if (!materials.isEmpty()) {
            MaterialLine materialLine = materials.get(0).getMaterialLine().get(0);
            updateQIMarking(orderLine, materials.get(0).getProcureLine(), materialLine.getWhSiteId());
        }
    }

    private void updateQIMarking(OrderLine orderLine, ProcureLine procureLine, String whSiteId) {
        if (orderLine != null) {
            orderLine.setQiMarking(evaluateQIMarking(orderLine.getProjectId(), orderLine.getOrder().getSupplierId(), orderLine.getPartNumber(),
                                                     orderLine.getPartName(), whSiteId));
            orderLine.setProcureLine(procureLine);
        }
    }

    @Override
    @PreAuthorize("hasAnyRole( 'DELIVERY', 'WH_DEFAULT','GR_ONLY')")
    public DeliveryNote createOrUpdateDeliveryNote(DeliveryNoteDTO deliveryNoteDTO) throws GloriaApplicationException {
        ReceiveType receiveType = ReceiveType.valueOf(deliveryNoteDTO.getReceiveType());

        /*
         * Validation RELAXED for timezone handling
         * 
         * if (deliveryNoteDTO.getDeliveryNoteDate() != null) {
            if (DateUtil.isFutureDate(deliveryNoteDTO.getDeliveryNoteDate())) {
                throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_FUTURE_DELIVERYNOTE_DATE,
                                                     "The delivery note date should not be greater than current date.");
            }
        }*/

        if (deliveryNoteDTO.getDeliveryNoteNo().length() > DELIVERY_NOTE_NO_MAX_LENGTH) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_DELIVERYNOTE_NO_LENGTH,
                                                 "The length of delivery note number should be less than 16 characters");
        }

        if (receiveType != null) {
            return receiveType.createDeliveryInformation(deliveryNoteDTO, deliveryNoteRepository, orderRepository, dangerousGoodsRepository,
                                                         requestHeaderRepository);
        }
        return null;
    }

    @Override
    @PreAuthorize("hasAnyRole( 'DELIVERY', 'WH_DEFAULT','GR_ONLY')")
    public List<DeliveryNoteLine> updateDeliveryNoteLines(List<DeliveryNoteLineDTO> deliveryNoteLineDTOs, boolean receive, String userId)
            throws GloriaApplicationException {
        UserDTO userDTO = userServices.getUser(userId);
        List<DeliveryNoteLine> deliveryNoteLines = new ArrayList<DeliveryNoteLine>();
        if (deliveryNoteLineDTOs != null && !deliveryNoteLineDTOs.isEmpty()) {
            for (DeliveryNoteLineDTO deliveryNoteLineDTO : deliveryNoteLineDTOs) {
                DeliveryNoteLine deliveryNoteLine = deliveryNoteRepository.findDeliveryNoteLineById(deliveryNoteLineDTO.getId());
                deliveryNoteLines.add(updateDeliveryNoteLine(deliveryNoteLineDTO, userId, deliveryNoteLine));
            }

            if (receive) {
                DeliveryNoteLine deliveryNoteLine = deliveryNoteLines.get(0);
                DeliveryNote deliveryNote = deliveryNoteLine.getDeliveryNote();
                deliveryNote.getReceiveType().receive(deliveryNote.getDeliveryNoteOID(), userDTO, deliveryNoteRepository, orderRepository,
                                                      requestHeaderRepository, materialServices, commonServices, traceabilityRepository, warehouseServices);
            }
        }
        return deliveryNoteLines;
    }

    @Override
    @PreAuthorize("hasAnyRole( 'WH_DEFAULT','WH_QI','GR_ONLY')")
    public DeliveryNoteSubLine updateDeliveryNoteSubLine(DeliveryNoteSubLineDTO deliveryNoteSubLineDTO) throws GloriaApplicationException {
        DeliveryNoteSubLine deliveryNoteSubLine = deliveryNoteRepository.findDeliveryNoteSubLineById(deliveryNoteSubLineDTO.getId());
        if (deliveryNoteSubLine != null) {

            if (deliveryNoteSubLineDTO.getToApproveQty() != null
                    && deliveryNoteSubLine.getDeliveryNoteLine().getReceivedQuantity() < deliveryNoteSubLineDTO.getToApproveQty().longValue()) {
                throw new GloriaApplicationException(GloriaExceptionConstants.IN_VALID_QUANTITY, "Enter a valid quantity");
            }

            deliveryNoteSubLine.setToReceiveQty(deliveryNoteSubLineDTO.getToReceiveQty());

            deliveryNoteSubLine.setToApproveQty(deliveryNoteSubLineDTO.getToApproveQty());

            updateWithTransportLabel(deliveryNoteSubLineDTO, deliveryNoteSubLine);

            if (deliveryNoteSubLineDTO.getBinLocation() > 0) {
                BinLocation binLocation = warehouseServices.findBinLocationById(deliveryNoteSubLineDTO.getBinLocation());
                if (binLocation == null) {
                    throw new GloriaApplicationException(GloriaExceptionConstants.WRONG_BIN_LOCATION, "Enter a valid bin location");
                }
                deliveryNoteSubLine.setBinLocation(deliveryNoteSubLineDTO.getBinLocation());
                deliveryNoteSubLine.setBinLocationCode(binLocation.getCode());
            } else {
                deliveryNoteSubLine.setBinLocation(0);
                deliveryNoteSubLine.setBinLocationCode(null);
            }

            deliveryNoteSubLine = deliveryNoteRepository.save(deliveryNoteSubLine);
            deliveryNoteSubLine.setNextZoneCode(deliveryNoteSubLine.getDeliveryNoteLine()
                                                                   .getDeliveryNote()
                                                                   .getReceiveType()
                                                                   .nextLocation(deliveryNoteSubLine.getDeliveryNoteLine(), deliveryNoteSubLine.isDirectSend(),
                                                                                 warehouseServices, this));
            return deliveryNoteSubLine;
        }
        return null;
    }

    private void updateWithTransportLabel(DeliveryNoteSubLineDTO deliveryNoteSubLineDTO, DeliveryNoteSubLine deliveryNoteSubLine) {
        TransportLabel transportLabel = null;
        if (deliveryNoteSubLineDTO.getTransportLabelId() > 0) {
            transportLabel = deliveryNoteRepository.findTransportLabelById(deliveryNoteSubLineDTO.getTransportLabelId());
        }
        deliveryNoteSubLine.setTransportLabel(transportLabel);
        updateRelatedMateriallinesWithTranspotLabelCode(transportLabel, deliveryNoteSubLine);
    }

    private void updateRelatedMateriallinesWithTranspotLabelCode(TransportLabel transportLabel, DeliveryNoteSubLine deliveryNoteSubLine) {
        List<MaterialLine> materialLines = deliveryNoteSubLine.getDeliveryNoteLine().getMaterialLine();
        if (materialLines != null && !materialLines.isEmpty()) {
            String transportLabelCode = null;
            if (transportLabel != null) {
                transportLabelCode = transportLabel.getCode();
            }
            for (MaterialLine materialLine : materialLines) {
                if (materialLine.getStatus() == MaterialLineStatus.BLOCKED || materialLine.getStatus() == MaterialLineStatus.QI_READY) {
                    if (materialLine.getDirectSend().isDirectSend() == deliveryNoteSubLine.isDirectSend()) {
                        materialLine.setTransportLabelCode(transportLabelCode);
                    }
                }
            }
        }
    }

    @Override
    @PreAuthorize("hasAnyRole( 'WH_DEFAULT', 'WH_QI')")
    public DeliveryNoteLine updateDeliveryNoteLine(DeliveryNoteLineDTO deliveryNoteLineDTO, String userId, boolean receive) throws GloriaApplicationException {
        UserDTO userDTO = userServices.getUser(userId);
        DeliveryNoteLine deliveryNoteLine = deliveryNoteRepository.findDeliveryNoteLineById(deliveryNoteLineDTO.getId());
        DeliveryNote deliveryNote = deliveryNoteLine.getDeliveryNote();
        deliveryNoteLine = updateDeliveryNoteLine(deliveryNoteLineDTO, userId, deliveryNoteLine);

        // lazy load
        OrderLine orderLineToLoad = deliveryNoteLine.getOrderLine();
        if (orderLineToLoad != null) {
            orderLineToLoad.getOrderLineVersions();
            List<Material> materials = orderLineToLoad.getMaterials();
            if (materials != null && !materials.isEmpty()) {
                for (Material material : materials) {
                    material.getMaterialLine();
                }
            }
        }
        deliveryNoteLine.getMaterialLine();

        if (receive) {
            deliveryNote.getReceiveType().receive(deliveryNote.getDeliveryNoteOID(), userDTO, deliveryNoteRepository, orderRepository, requestHeaderRepository,
                                                  materialServices, commonServices, traceabilityRepository, warehouseServices);
        }
        return deliveryNoteLine;
    }

    private DeliveryNoteLine updateDeliveryNoteLine(DeliveryNoteLineDTO deliveryNoteLineDTO, String userId, DeliveryNoteLine deliveryNoteLine)
            throws GloriaApplicationException {
        UserDTO userDTO = userServices.getUser(userId);
        // lazy load
        OrderLine orderLineToLoad = deliveryNoteLine.getOrderLine();
        if (orderLineToLoad != null) {
            orderLineToLoad.getOrderLineVersions();
        }
        long deliveryNoteQuantity = deliveryNoteLineDTO.getDeliveryNoteQuantity();
        long receivedQuantity = 0L;

        for (DeliveryNoteSubLine deliveryNoteSubLine : deliveryNoteLine.getDeliveryNoteSubLines()) {
            receivedQuantity += deliveryNoteSubLine.getToReceiveQty();
        }

        if (deliveryNoteLineDTO.getVersion() != deliveryNoteLine.getVersion()) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_UPDATION_OF_STALE_DATASET,
                                                 "This operation cannot be performed since the information seen in the page has already been updated.");
        }

        validateQty(deliveryNoteLineDTO, deliveryNoteQuantity, receivedQuantity);

        deliveryNoteLine.setDamagedQuantity(deliveryNoteLineDTO.getDamagedQuantity());

        deliveryNoteLine.setHasDamagedParts(deliveryNoteLineDTO.isHasDamagedParts());

        deliveryNoteLine.setExpirationDate(deliveryNoteLineDTO.getExpirationDate());
        deliveryNoteLine.setHasMeasuringDoc(deliveryNoteLineDTO.isHasMeasuringDoc());
        deliveryNoteLine.setHasControlCertificateDoc(deliveryNoteLineDTO.isHasControlCertificateDoc());
        deliveryNoteLine.setHasMissingInfo(deliveryNoteLineDTO.isHasMissingInfo());
        deliveryNoteLine.setHasOverDeliveries(deliveryNoteLineDTO.isHasOverDelivery());
        deliveryNoteLine.setProblemDescription(deliveryNoteLineDTO.getProblemDescription());
        deliveryNoteLine.setQualityInspectionComment(deliveryNoteLineDTO.getQualityInspectionComment());
        deliveryNoteLine.setReceivedDetailsUpdated(deliveryNoteLineDTO.isReceivedDetailsUpdated());
        deliveryNoteLine.setQiDetailsUpdated(deliveryNoteLineDTO.isQiDetailsUpdated());
        deliveryNoteLine.setReceivedQuantity(receivedQuantity);
        deliveryNoteLine.setPartVersion(deliveryNoteLineDTO.getPartVersion());
        List<MaterialLine> materialLines = deliveryNoteLine.getMaterialLine();
        if (materialLines != null && !materialLines.isEmpty()) {
            for (MaterialLine materialLine : materialLines) {
                MaterialLineStatusHelper.updateAndAlertPartversionChangeForDelyveryNoteLine(deliveryNoteLine, materialLine, userDTO, materialServices,
                                                                                            traceabilityRepository);
            }
        }
        OrderLine orderLine = orderRepository.findOrderLineById(deliveryNoteLineDTO.getOrderLineId());
        if (orderLine != null) {
            orderLine.setFreeText(deliveryNoteLineDTO.getFreeText());
            OrderLineLastModified orderLineLastModified = orderLine.getOrderLineLastModified();
            orderLineLastModified.setModifiedByUserId(userId);
            orderLineLastModified.setModifiedTime(DateUtil.getCurrentUTCDateTime());
            orderRepository.saveOrderLine(orderLine);
        }

        updateWithDeliveryNoteQty(deliveryNoteLineDTO, deliveryNoteLine, deliveryNoteQuantity, receivedQuantity);

        return deliveryNoteRepository.save(deliveryNoteLine);
    }

    private void updateWithDeliveryNoteQty(DeliveryNoteLineDTO deliveryNoteLineDTO, DeliveryNoteLine deliveryNoteLine, long deliveryNoteQuantity,
            long receivedQuantity) throws GloriaApplicationException {
        if (deliveryNoteLineDTO.isReceivedDetailsUpdated()) {
            if (deliveryNoteQuantity != receivedQuantity) {
                deliveryNoteLine.setDeliveryNoteQuantity(deliveryNoteQuantity);
            } else {
                deliveryNoteLine.setDeliveryNoteQuantity(receivedQuantity);
            }
        } else {
            if (deliveryNoteQuantity == 0 || receivedQuantity != 0) {
                deliveryNoteLine.setDeliveryNoteQuantity(receivedQuantity);
            }
        }
    }

    private void validateQty(DeliveryNoteLineDTO deliveryNoteLineDTO, long deliveryNoteQuantity, long receivedQuantity) throws GloriaApplicationException {
        if (receivedQuantity < 0) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_RECEIVED_QTY, "Received Quantity should not be empty.");
        }
        if (deliveryNoteQuantity <= 0 && deliveryNoteLineDTO.isReceivedDetailsUpdated()) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_DELIVERYNOTE_QTY, "Delivery Note Quantity should not be empty.");
        }
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE','PROCURE-INTERNAL', 'DELIVERY','IT_SUPPORT')")
    public OrderLine getOrderLine(Long orderLineId) {
        OrderLine orderLine = orderRepository.findOrderLineById(orderLineId);
        if (orderLine != null) {
            // lazy load
            orderLine.getOrderLineVersions();
            List<Material> materials = orderLine.getMaterials();
            for (Material material : materials) {
                material.getMaterialLine();
            }
        }

        return orderLine;
    }

    @Override
    @PreAuthorize("hasAnyRole( 'WH_DEFAULT', 'WH_QI','GR_ONLY','IT_SUPPORT')")
    public List<DeliveryNoteSubLine> getDeliveryNoteSubLinesByDeliveryNoteLineId(long deliveryNoteLineOid, String materialLineStatus)
            throws GloriaApplicationException {
        return evaluateValidSublinesForDeliveryNoteLine(deliveryNoteLineOid, materialLineStatus);
    }

    private List<DeliveryNoteSubLine> evaluateValidSublinesForDeliveryNoteLine(long deliveryNoteLineOid, String materialLineStatus)
            throws GloriaApplicationException {
        DeliveryNoteLine deliveryNoteLine = deliveryNoteRepository.findDeliveryNoteLineById(deliveryNoteLineOid);
        if (deliveryNoteLine != null) {
            return DeliveryHelper.manageSublinesForQI(materialLineStatus, deliveryNoteLine,
                                                      deliveryNoteRepository.findDeliveryNoteSubLinesLineById(deliveryNoteLineOid), warehouseServices, this);
        }
        return new ArrayList<DeliveryNoteSubLine>();
    }

    @Override
    @PreAuthorize("hasAnyRole( 'WH_DEFAULT', 'WH_QI')")
    public DeliveryNoteLine getDeliveryNoteLineById(long deliveryNoteLineOid) {
        DeliveryNoteLine deliveryNoteLine = deliveryNoteRepository.findDeliveryNoteLineById(deliveryNoteLineOid);
        if (deliveryNoteLine != null) {
            // lazy load
            OrderLine orderLine = deliveryNoteLine.getOrderLine();
            if (orderLine != null) {
                orderLine.getOrderLineVersions();
            }
            deliveryNoteLine.getMaterialLine();
        }
        return deliveryNoteLine;
    }

    @Override
    @PreAuthorize("hasAnyRole( 'WH_DEFAULT', 'WH_QI', 'DELIVERY', 'PROCURE','IT_SUPPORT', 'VIEWER_PRICE', 'VIEWER')")
    public DeliveryNoteLine getDeliveryNoteLineByIdAndSetMaterialLine(long deliveryNoteLineOid) {

        DeliveryNoteLine deliveryNoteLine = this.getDeliveryNoteLineById(deliveryNoteLineOid);
        if (deliveryNoteLine != null) {
            OrderLine orderLine = deliveryNoteLine.getOrderLine();
            if (orderLine != null) {
                QualityDocument qualityDocument = qualityDocumentRepository.findById(orderLine.getProcureLine().getQualityDocumentOID());
                if (qualityDocument != null) {
                    deliveryNoteLine.setQualityDocName(qualityDocument.getName());
                }
            }

            if (!ReceiveType.REGULAR.equals(deliveryNoteLine.getDeliveryNote().getReceiveType())) {
                MaterialLine materialLine = materialServices.findMaterialLineById(deliveryNoteLine.getMaterialLineOID());
                if (materialLine != null) {
                    List<MaterialLine> materialLines = new ArrayList<MaterialLine>();
                    materialLines.add(materialLine);
                    deliveryNoteLine.setMaterialLine(materialLines);
                }
            }
            deliveryNoteLine.getMaterialLine();
            return deliveryNoteLine;
        }
        return null;
    }

    @Override
    @PreAuthorize("hasAnyRole( 'WH_DEFAULT','GR_ONLY','IT_SUPPORT')")
    public List<DeliveryNoteLine> getDeliveryNoteLines(long deliveryNoteId, String whSiteId, DeliveryNoteLineStatus status, ReceiveType receiveType)
            throws GloriaApplicationException {
        List<DeliveryNoteLine> deliveryNoteLines = deliveryNoteRepository.findDeliveryNoteLinesByDeliveryNoteId(deliveryNoteId, whSiteId, status, receiveType);
        if (deliveryNoteLines != null && !deliveryNoteLines.isEmpty()) {
            // lazy load
            for (DeliveryNoteLine deliveryNoteLine : deliveryNoteLines) {
                OrderLine orderLine = deliveryNoteLine.getOrderLine();
                if (orderLine != null) {
                    orderLine.getOrderLineVersions();
                    List<Material> materials = orderLine.getMaterials();
                    if (materials != null && !materials.isEmpty()) {
                        for (Material material : materials) {
                            material.getMaterialLine();
                        }
                    }
                }
                deliveryNoteLine.getMaterialLine();
            }
        }
        return deliveryNoteLines;
    }

    @Override
    @PreAuthorize("hasAnyRole( 'WH_QI', 'WH_DEFAULT', 'DELIVERY','GR_ONLY','IT_SUPPORT')")
    public PageObject findOrderLinesForWarehouse(String internalExternal, String status, String userId, String whSiteId, boolean calculateInStock,
            PageObject pageObject) throws ParseException, GloriaApplicationException {
        return orderRepository.findOrderLines(null, null, null, null, null, null, null, null, null, null, null, internalExternal, whSiteId, false, status,
                                              calculateInStock, pageObject, false, null, false, null, null, null, false);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'WH_QI', 'WH_DEFAULT', 'DELIVERY','IT_SUPPORT')")
    public PageObject findOrderLinesForDeliveryControl(String expectedArrivalFrom, String expectedArrivalTo, String expectedArrival, String buildStartDateFrom, String buildStartDateTo,
            String buildStartDate, String orderStaDateFrom, String orderStaDateTo, String orderStaDate, String statusFlag,
            String deliveryControllerId, String internalExternal, String partNumber, Boolean markedAsComplete, String status, String userId,
            boolean calculateInStock, PageObject pageObject, String deliveryControllerTeam, boolean unassigned, String completeType,
            Map<String, List<String>> queryParams, boolean loadDeliverySchedules) throws ParseException, GloriaApplicationException {
        return orderRepository.findOrderLines(expectedArrivalFrom, expectedArrivalTo, expectedArrival, buildStartDateFrom, buildStartDateTo, buildStartDate,
                                              orderStaDateFrom, orderStaDateTo, orderStaDate, statusFlag, deliveryControllerId, internalExternal, null, false,
                                              status, calculateInStock, pageObject, true, deliveryControllerTeam, unassigned,
                                              userServices.getUserCompanyCodeCodes(userId, deliveryControllerTeam, TeamType.DELIVERY_CONTROL.name()),
                                              completeType, queryParams, loadDeliverySchedules);
    }

    @Override
    public List<DeliveryNote> getAllDeliveryNotes() {
        return deliveryNoteRepository.findAll();
    }

    @Override
    public void addDeliveryNote(DeliveryNote deliveryNote) {
        deliveryNoteRepository.save(deliveryNote);
    }

    @Override
    public List<OrderLine> getAllOrderLines() {
        return orderRepository.getAllOrderLines();
    }

    @Override
    @PreAuthorize("hasAnyRole( 'WH_DEFAULT','GR_ONLY','IT_SUPPORT')")
    public DeliveryNote getDeliveryNoteById(long deliveryNoteId, String whSiteId) {
        DeliveryNote deliveryNote = deliveryNoteRepository.findById(deliveryNoteId);
        if (deliveryNote != null && deliveryNote.getWhSiteId().equals(whSiteId)) {
            // lazy load
            deliveryNote.getDeliveryNoteLine();
            return deliveryNote;
        }
        return null;
    }

    @Override
    @PreAuthorize("hasAnyRole( 'WH_DEFAULT', 'PROCURE','WH_QI', 'DELIVERY','IT_SUPPORT', 'VIEWER_PRICE', 'VIEWER')")
    public List<QiDoc> getQiDocs(long deliveryNoteLineId) {
        return deliveryNoteRepository.findQiDocs(deliveryNoteLineId);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'WH_DEFAULT', 'PROCURE', 'DELIVERY','WH_QI','IT_SUPPORT', 'VIEWER_PRICE', 'VIEWER')")
    public List<ProblemDoc> getProblemDocuments(long deliveryNoteLineId) {
        List<ProblemDoc> problemDocs = deliveryNoteRepository.findProblemDocs(deliveryNoteLineId);
        return problemDocs;
    }

    @Override
    public List<AttachedDoc> getAttachedDocuments(long deliveryScheduleId) {
        return orderRepository.findAttachedDocs(deliveryScheduleId);
    }

    @Override
    public List<ReceiveDoc> getReceiveDocuments(long deliveryNoteLineId) {
        return deliveryNoteRepository.findInspectionDocs(deliveryNoteLineId);
    }

    @Override
    public QiDoc getQiDoc(long qualityDocId) {
        return deliveryNoteRepository.findQualityDocById(qualityDocId);
    }

    @Override
    public ProblemDoc getProblemDocument(long problemNoteDocId) {
        return deliveryNoteRepository.findProblemDocById(problemNoteDocId);
    }

    @Override
    public AttachedDoc getAttachedDocument(long attachedDocId) {
        return orderRepository.findAttachedDocById(attachedDocId);
    }

    @Override
    public ReceiveDoc getReceiveDocument(long inspectionDocId) {
        return deliveryNoteRepository.findReceiveDocById(inspectionDocId);
    }

    @Override
    public void deleteQiDoc(long qualityDocId) {
        QiDoc qualityDoc = deliveryNoteRepository.findQualityDocById(qualityDocId);
        if (qualityDoc != null) {
            deliveryNoteRepository.delete(qualityDoc);
        }
    }

    @Override
    public void deleteProblemDoc(long problemNoteDocId) {
        ProblemDoc problemDoc = deliveryNoteRepository.findProblemDocById(problemNoteDocId);
        if (problemDoc != null) {
            deliveryNoteRepository.delete(problemDoc);
        }
    }

    @Override
    public void deleteAttachedDoc(long attachedNoteDocId) {
        AttachedDoc attachedDoc = orderRepository.findAttachedDocById(attachedNoteDocId);
        if (attachedDoc != null) {
            orderRepository.delete(attachedDoc);
        }
    }

    @Override
    public void deleteReceiveDocument(long inspectionDocId) {
        ReceiveDoc receiveDoc = deliveryNoteRepository.findReceiveDocById(inspectionDocId);
        if (receiveDoc != null) {
            deliveryNoteRepository.delete(receiveDoc);
        }
    }

    @Override
    public QiDoc uploadQiDocs(long deliveryNoteLineId, DocumentDTO document) throws GloriaApplicationException {
        DeliveryNoteLine deliveryNoteLine = deliveryNoteRepository.findDeliveryNoteLineById(deliveryNoteLineId);
        if (deliveryNoteLine == null) {
            LOGGER.error("No DeliveryNoteLine exists for id : " + deliveryNoteLineId);
            throw new GloriaSystemException("This operation cannot be performed. No DeliveryNoteLine exists for id : " + deliveryNoteLineId,
                                            GloriaExceptionConstants.INVALID_DATASET_OID);
        }
        if (deliveryNoteLine.getQualityDocs().size() >= GloriaParams.DOCUMENT_UPLOAD_LIMIT) {
            throw new GloriaApplicationException(deliveryNoteLineId, "qualitydocupload", GloriaExceptionConstants.UPLOAD_LIMIT_EXCEEDED,
                                                 DOCUMENT_UPLOAD_LIMIT_EXCEEDED, "DocumentDTO");
        } else if (document.getSize() > GloriaParams.DOCUMENT_SIZE_LIMIT) {
            throw new GloriaApplicationException(deliveryNoteLineId, "qualitydocupload", GloriaExceptionConstants.UPLOAD_SIZE_EXCEEDED,
                                                 DOCUMENT_SIZE_EXCEEDS_5MB, "DocumentDTO");
        }

        QiDoc qualityDoc = new QiDoc();
        qualityDoc.setDeliveryNoteLine(deliveryNoteLine);
        qualityDoc.setDocumentName(document.getName());
        qualityDoc.setFileContent(document.getContent());
        deliveryNoteLine.getQualityDocs().add(qualityDoc);
        return deliveryNoteRepository.save(qualityDoc);
    }

    @Override
    public ProblemDoc uploadProblemDocuments(long deliveryNoteLineId, DocumentDTO document) throws GloriaApplicationException {
        DeliveryNoteLine deliveryNoteLine = deliveryNoteRepository.findDeliveryNoteLineById(deliveryNoteLineId);
        if (deliveryNoteLine == null) {
            LOGGER.error("No DeliveryNoteLine exists for id : " + deliveryNoteLineId);
            throw new GloriaSystemException("This operation cannot be performed. No DeliveryNoteLine exists for id : " + deliveryNoteLineId,
                                            GloriaExceptionConstants.INVALID_DATASET_OID);
        }
        if (deliveryNoteLine.getProblemDocs().size() >= GloriaParams.DOCUMENT_UPLOAD_LIMIT) {
            throw new GloriaApplicationException(deliveryNoteLineId, "problemnoteupload", GloriaExceptionConstants.UPLOAD_LIMIT_EXCEEDED,
                                                 DOCUMENT_UPLOAD_LIMIT_EXCEEDED, "DocumentDTO");
        } else if (document.getSize() > GloriaParams.DOCUMENT_SIZE_LIMIT) {
            throw new GloriaApplicationException(deliveryNoteLineId, "problemnoteupload", GloriaExceptionConstants.UPLOAD_SIZE_EXCEEDED,
                                                 DOCUMENT_SIZE_EXCEEDS_5MB, "DocumentDTO");
        }

        ProblemDoc problemDoc = new ProblemDoc();
        problemDoc.setDeliveryNoteLine(deliveryNoteLine);
        problemDoc.setDocumentName(document.getName());
        problemDoc.setFileContent(document.getContent());
        deliveryNoteLine.getProblemDocs().add(problemDoc);
        return deliveryNoteRepository.save(problemDoc);
    }

    @Override
    public AttachedDoc uploadAttachedDocuments(long deliveryScheduleId, DocumentDTO document) throws GloriaApplicationException {
        DeliverySchedule deliverySchedule = orderRepository.findDeliveryScheduleById(deliveryScheduleId);
        if (deliverySchedule == null) {
            LOGGER.error("No DeliverySchedule exists for id : " + deliveryScheduleId);
            throw new GloriaSystemException("This operation cannot be performed. No DeliveryNoteLine exists for id : " + deliveryScheduleId,
                                            GloriaExceptionConstants.INVALID_DATASET_OID);
        }
        if (deliverySchedule.getAttachedDocs().size() >= GloriaParams.DOCUMENT_UPLOAD_LIMIT) {
            throw new GloriaApplicationException(deliveryScheduleId, "deliveryAttachment3", GloriaExceptionConstants.UPLOAD_LIMIT_EXCEEDED,
                                                 DOCUMENT_UPLOAD_LIMIT_EXCEEDED, "DocumentDTO");
        } else if (document.getSize() > GloriaParams.DOCUMENT_SIZE_LIMIT) {
            throw new GloriaApplicationException(deliveryScheduleId, "deliveryAttachment3", GloriaExceptionConstants.UPLOAD_SIZE_EXCEEDED,
                                                 DOCUMENT_SIZE_EXCEEDS_5MB, "DocumentDTO");
        }

        AttachedDoc attachedDoc = new AttachedDoc();
        attachedDoc.setDeliverySchedule(deliverySchedule);
        attachedDoc.setDocumentName(document.getName());
        attachedDoc.setFileContent(document.getContent());
        deliverySchedule.getAttachedDocs().add(attachedDoc);
        return orderRepository.save(attachedDoc);
    }

    @Override
    public List<DeliveryLog> getDeliveryLogs(Long deliveryScheduleId, String eventType) {
        return orderRepository.findDeliveryLogsByDeliveryScheduleId(deliveryScheduleId, eventType);
    }

    @Override
    @PreAuthorize("hasAnyRole('DELIVERY', 'PROCURE','IT_SUPPORT')")
    public List<DeliverySchedule> getDeliverySchedules(String deliveryControllerId, Long orderLineId) {
        return orderRepository.findDeliverySchedules(deliveryControllerId, orderLineId);
    }

    @Override
    @PreAuthorize("hasAnyRole('DELIVERY', 'PROCURE','IT_SUPPORT')")
    public DeliverySchedule getDeliverySchedule(Long deliveryScheduleId) {
        return orderRepository.findDeliveryScheduleById(deliveryScheduleId);
    }

    @Override
    public DeliveryLog addDeliveryLog(Long deliveryScheduleId, String eventType, DeliveryLogDTO deliveryLogDTO, String userId)
            throws GloriaApplicationException {
        DeliveryLog deliveryLog = new DeliveryLog();
        deliveryLog.setDeliverySchedule(orderRepository.findDeliveryScheduleById(deliveryScheduleId));

        UserDTO userDTO = userServices.getUser(userId);
        if (userDTO != null) {
            deliveryLog.setEventOriginatorId(userDTO.getId());
            deliveryLog.setEventOriginatorName(userDTO.getUserName());
        }
        deliveryLog.setEventTime(DateUtil.getCurrentUTCDateTime());
        if (!StringUtils.isEmpty(eventType)) {
            deliveryLog.setEventType(EventType.valueOf(eventType));
        }
        deliveryLog.setEventValue(deliveryLogDTO.getEventValue());
        return orderRepository.save(deliveryLog);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'DELIVERY','IT_SUPPORT')")
    public Order findOrderById(Long orderId) {
        return orderRepository.findOrderById(orderId);
    }

    @Override
    public List<OrderLog> getOrderLogs(Long orderId) {
        return orderRepository.findOrderLogsByOrderId(orderId);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'DELIVERY')")
    public List<DeliverySchedule> updateDeliverySchedules(long orderLineId, List<DeliveryScheduleDTO> deliveryScheduleDTOs, String userId)
            throws GloriaApplicationException {
        List<DeliverySchedule> deliverySchedules = new ArrayList<DeliverySchedule>();
        OrderLine orderLine = orderRepository.findOrderLineById(orderLineId);
        if (orderLine != null) {
            DeliveryHelper.deleteMissingDeliverySchedules(orderLine.getDeliverySchedule(), deliveryScheduleDTOs, userId, this, orderRepository);
            if (deliveryScheduleDTOs != null && !deliveryScheduleDTOs.isEmpty()) {
                for (DeliveryScheduleDTO deliveryScheduleDTO : deliveryScheduleDTOs) {
                    DeliverySchedule updatetedDeliverySchedule = createOrUpdateDeliverySchedule(orderLine, deliveryScheduleDTO, userId);
                    deliverySchedules.add(updatetedDeliverySchedule);
                }

                updateDeliveryDeviation(orderLine, deliveryScheduleDTOs);
            }
        }
        return deliverySchedules;
    }

    private void updateDeliveryDeviation(OrderLine orderLine, List<DeliveryScheduleDTO> deliveryScheduleDTOs) {
        long expectedQuantity = orderLine.getPossibleToReceiveQuantity() - orderLine.getReceivedQuantity();
        long deliveryScheduleQuantity = 0;
        for (DeliveryScheduleDTO deliveryScheduleDTO : deliveryScheduleDTOs) {
            deliveryScheduleQuantity += deliveryScheduleDTO.getExpectedQuantity();
        }
        if (expectedQuantity == deliveryScheduleQuantity) {
            orderLine.setDeliveryDeviation(false);
        }
    }

    @Override
    @PreAuthorize("hasAnyRole( 'DELIVERY', 'WH_DEFAULT')")
    public DeliverySchedule updateDeliverySchedule(long orderLineId, DeliveryScheduleDTO deliveryScheduleDTO, String userId) throws GloriaApplicationException {
        if (deliveryScheduleDTO != null) {
            OrderLine orderLine = orderRepository.findOrderLineById(orderLineId);
            return createOrUpdateDeliverySchedule(orderLine, deliveryScheduleDTO, userId);
        }
        return null;
    }

    private DeliverySchedule createOrUpdateDeliverySchedule(OrderLine orderLine, DeliveryScheduleDTO deliveryScheduleDTO, String userId)
            throws GloriaApplicationException {
        UserDTO userDTO = userServices.getUser(userId);
        DeliverySchedule deliverySchedule = null;
        Date expectedDate = deliveryScheduleDTO.getExpectedDate();
        StringBuffer actionDetailBuffer = new StringBuffer();
        long expectedQuantity = deliveryScheduleDTO.getExpectedQuantity();
        if (deliveryScheduleDTO.getId() > 0) {
            deliverySchedule = orderRepository.findDeliveryScheduleById(deliveryScheduleDTO.getId());
            if(!orderLine.isContentEdited()) {
                orderLine.setContentEdited(isScheduleUpdated(deliverySchedule, deliveryScheduleDTO));
            }
            if (deliveryScheduleDTO.getVersion() != deliverySchedule.getVersion()) {
                throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_UPDATION_OF_STALE_DATASET,
                                                     "This operation cannot be performed since the information seen in the page has already been updated.");
            }
            if (!DateUtil.isSameDate(expectedDate, deliverySchedule.getExpectedDate())) {
                actionDetailBuffer.append(DateUtil.getDateWithoutTimeAsString(expectedDate));
            }
            if (deliverySchedule.getExpectedQuantity() != null && expectedQuantity != deliverySchedule.getExpectedQuantity()) {
                actionDetailBuffer.append(" qty=" + expectedQuantity);
            }
        } else {
            actionDetailBuffer.append(DateUtil.getDateWithoutTimeAsString(expectedDate));
            actionDetailBuffer.append(" qty=" + expectedQuantity);
            deliverySchedule = new DeliverySchedule();
            deliverySchedule.setOrderLine(orderLine);
            orderLine.getDeliverySchedule().add(deliverySchedule);
            Date earliestExpectedDate = orderLine.getEarliestExpectedDate();
            if (earliestExpectedDate != null && earliestExpectedDate.after(expectedDate)) {
                orderLine.setEarliestExpectedDate(expectedDate);
            }
        }

        String actionDetail = actionDetailBuffer.toString();
        if (!StringUtils.isEmpty(actionDetail)) {
            OrderLineStatusHelper.createTracebilityForOrderLine(traceabilityRepository, orderLine, "Delivery Schedule Updated", actionDetail, userDTO.getId(),
                                                                userDTO.getUserName());
        }

        if (!StringUtils.isEmpty(deliveryScheduleDTO.getStatusFlag())) {
            deliverySchedule.setStatusFlag(DeliveryStatusFlag.valueOf(deliveryScheduleDTO.getStatusFlag()));
        } else {
            deliverySchedule.setStatusFlag(null);
        }
        OrderLineLastModified orderLineLastModified = orderLine.getOrderLineLastModified();
        orderLineLastModified.setModifiedByUserId(userId);
        orderLineLastModified.setModifiedTime(DateUtil.getCurrentUTCDateTime());
        deliverySchedule.setExpectedDate(expectedDate);
        deliverySchedule.setExpectedQuantity(expectedQuantity);

        Date plannedDispatchDate = deliveryScheduleDTO.getPlannedDispatchDate();
        if (!DateUtil.isSameDate(plannedDispatchDate, deliverySchedule.getPlannedDispatchDate())) {
            OrderLineStatusHelper.createTracebilityForOrderLine(traceabilityRepository, orderLine, "Planned Dispatch Date Updated",
                                                                DateUtil.getDateWithoutTimeAsString(plannedDispatchDate), userDTO.getId(),
                                                                userDTO.getUserName());
        }

        deliverySchedule.setPlannedDispatchDate(plannedDispatchDate);
        orderRepository.save(deliverySchedule);
        
        return deliverySchedule;
    }


    private boolean isScheduleUpdated(DeliverySchedule deliverySchedule, DeliveryScheduleDTO deliveryScheduleDTO) {
        boolean hasChange = false;
        if (!DateUtil.isSameDate(deliverySchedule.getExpectedDate(), deliveryScheduleDTO.getExpectedDate())) {
            hasChange = true;
        }
        if (!DateUtil.isSameDate(deliverySchedule.getPlannedDispatchDate(), deliveryScheduleDTO.getPlannedDispatchDate())) {
            hasChange = true;
        }
        if ((deliveryScheduleDTO.getStatusFlag() != null && deliverySchedule.getStatusFlag() == null)
                || (deliveryScheduleDTO.getStatusFlag() != null && deliveryScheduleDTO.getStatusFlag() == null)
             ) {
            hasChange = true;
        }
        if (deliveryScheduleDTO.getStatusFlag() != null && deliveryScheduleDTO.getStatusFlag() != null) {
            if (DeliveryStatusFlag.valueOf(deliveryScheduleDTO.getStatusFlag()) != deliverySchedule.getStatusFlag()) {
                hasChange = true;
            }
        }
        return hasChange;
    }

    @PreAuthorize("hasAnyRole('DELIVERY','WH_QI','PROCURE','PROCURE-INTERNAL', 'WH_DEFAULT','GR_ONLY')")
    @Override
    public List<OrderLine> updateOrderLines(List<OrderLineDTO> orderLineDTOs, String userId) throws GloriaApplicationException {
        List<OrderLine> orderLines = new ArrayList<OrderLine>();
        if (orderLineDTOs != null && !orderLineDTOs.isEmpty()) {
            for (OrderLineDTO orderLineDTO : orderLineDTOs) {
                orderLines.add(updateOrderLine(orderLineDTO, userId));
            }
        }
        return orderLines;
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE','PROCURE-INTERNAL','DELIVERY')")
    public OrderLine updateOrderLine(OrderLineDTO orderLineDTO, String userId) throws GloriaApplicationException {
        UserDTO userDTO = userServices.getUser(userId);
        OrderLine orderLine = orderRepository.findOrderLineById(orderLineDTO.getId());
        if (orderLineDTO.getVersion() != orderLine.getVersion()) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_UPDATION_OF_STALE_DATASET,
                                                 "This operation cannot be performed since the information seen in the page has already been updated.");
        }

        OrderLineVersion currentOrderLineVersion = orderLine.getCurrent();
        Date staAcceptedDate = orderLineDTO.getStaAcceptedDate();
        if (!DateUtil.isSameDate(staAcceptedDate, currentOrderLineVersion.getStaAcceptedDate())) {
            if (!orderLine.isContentEdited()) {
                orderLine.setContentEdited(true);
            }
            OrderLineStatusHelper.createTracebilityForOrderLine(traceabilityRepository, orderLine, "STA Accepted Updated",
                                                                DateUtil.getDateWithoutTimeAsString(staAcceptedDate), userDTO.getId(), userDTO.getUserName());
        }

        currentOrderLineVersion.setStaAcceptedDate(staAcceptedDate);

        Date staAgreedDate = orderLineDTO.getStaAgreedDate();
        if (!DateUtil.isSameDate(staAgreedDate, currentOrderLineVersion.getStaAgreedDate())) {
            if (!orderLine.isContentEdited()) {
                orderLine.setContentEdited(true);
            }
            OrderLineStatusHelper.createTracebilityForOrderLine(traceabilityRepository, orderLine, "Agreed STA Updated",
                                                                DateUtil.getDateWithoutTimeAsString(staAgreedDate), userDTO.getId(), userDTO.getUserName());
            currentOrderLineVersion.setStaAgreedLastUpdated(DateUtil.getCurrentUTCDateTime());
        }
        if (currentOrderLineVersion.getStaAgreedDate() == null) {
            List<DeliverySchedule> deliverySchedules = orderLine.getDeliverySchedule();
            for (DeliverySchedule deliverySchedule : deliverySchedules) {
                deliverySchedule.setExpectedDate(staAgreedDate);
            }
        }
        currentOrderLineVersion.setStaAgreedDate(staAgreedDate);

        doUpdatePossibleToReceiveQty(orderLineDTO, userDTO, orderLine, materialServices);

        boolean markedAsComplete = orderLineDTO.isMarkedAsComplete();
        if (markedAsComplete) {
            orderLine.getStatus().markAsComplete(orderLine, userDTO, materialServices, warehouseServices, requestHeaderRepository, traceabilityRepository);
        }

        orderLine.setFreeText(orderLineDTO.getFreeText());

        OrderLineLastModified orderLineLastModified = orderLine.getOrderLineLastModified();
        orderLineLastModified.setModifiedByUserId(userId);
        orderLineLastModified.setModifiedTime(DateUtil.getCurrentUTCDateTime());
        if (!orderLine.isContentEdited()) {
            orderLine.setContentEdited(orderLineDTO.isContentEdited());
        }
        orderLine = orderRepository.saveOrderLine(orderLine);

        // lazy load fix
        orderLine.getOrderLineVersions();
        orderLine.getDeliverySchedule();

        return orderLine;
    }

    private boolean doUpdatePossibleToReceiveQty(OrderLineDTO orderLineDTO, UserDTO userDTO, OrderLine orderLine, MaterialServices materialServices)
            throws GloriaApplicationException {
        boolean isPossibleToReceiveQtyUpdated = false;
        long existingAllowedQty = orderLine.getPossibleToReceiveQuantity();
        long updatedAllowedQty = orderLineDTO.getAllowedQuantity();
        orderLine.setPossibleToReceiveQuantity(updatedAllowedQty);
        if (existingAllowedQty != updatedAllowedQty) {
            materialServices.createAndSendProcessPurchaseOrder(orderLine.getOrder(), SAPParam.ACTION_CHANGE);
            OrderLineStatusHelper.createTracebilityForOrderLine(traceabilityRepository, orderLine, "PTR Updated", "qty= " + orderLineDTO.getAllowedQuantity(),
                                                                userDTO.getId(), userDTO.getUserName());
            isPossibleToReceiveQtyUpdated = true;
        }
        return isPossibleToReceiveQtyUpdated;
    }

    @Override
    @PreAuthorize("hasAnyRole('WH_QI', 'WH_DEFAULT')")
    public List<OrderLine> updateOrderlinesQIMarking(List<OrderLineDTO> orderLineDTOs, String userId) throws GloriaApplicationException {
        List<OrderLine> orderLines = new ArrayList<OrderLine>();
        if (orderLineDTOs != null && !orderLineDTOs.isEmpty()) {
            for (OrderLineDTO orderLineDTO : orderLineDTOs) {
                orderLines.add(updateOrderlineQIMarking(orderLineDTO, userId));
            }
        }
        return orderLines;
    }

    @Override
    @PreAuthorize("hasAnyRole( 'WH_QI', 'WH_DEFAULT')")
    public OrderLine updateOrderlineQIMarking(OrderLineDTO orderLineDTO, String userId) throws GloriaApplicationException {
        OrderLine orderLine = orderRepository.findOrderLineById(orderLineDTO.getId());
        if (orderLineDTO.getVersion() != orderLine.getVersion()) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_UPDATION_OF_STALE_DATASET,
                                                 "This operation cannot be performed since the information seen in the page has already been updated.");
        }

        QiMarking qiMarking = QiMarking.VISUAL;

        // case of UnMark
        if (!StringUtils.isEmpty(orderLineDTO.getQiMarking())) {
            qiMarking = QiMarking.valueOf(orderLineDTO.getQiMarking());
        }

        List<Material> materials = orderLine.getMaterials();
        if (materials != null && !materials.isEmpty()) {
            for (Material material : materials) {
                List<MaterialLine> materialLines = material.getMaterialLine();
                DirectSendType directSendType = materialLines.get(0).getDirectSend();
                if (directSendType.isDirectSend() && qiMarking == QiMarking.OPTIONAL && orderLine.getQiMarking() != qiMarking) {
                    throw new GloriaApplicationException(GloriaExceptionConstants.QIMARKING_INVALID,
                                                         "The part with direct send can only be set as Mandatory not Optional.");
                }
            }
            orderLine.setQiMarking(qiMarking);
        }

        OrderLineLastModified orderLineLastModified = orderLine.getOrderLineLastModified();
        orderLineLastModified.setModifiedByUserId(userId);
        orderLineLastModified.setModifiedTime(DateUtil.getCurrentUTCDateTime());
        orderLine = orderRepository.saveOrderLine(orderLine);

        // lazy load fix
        orderLine.getOrderLineVersions();
        orderLine.getDeliverySchedule();

        return orderLine;
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE', 'PROCURE-INTERNAL', 'DELIVERY')")
    public OrderLine updateDeliveryDeviation(OrderLineDTO orderLineDTO, String userId) throws GloriaApplicationException {
        Long orderLineOid = orderLineDTO.getId();
        OrderLine orderLine = orderRepository.findOrderLineById(orderLineOid);
        orderLine.setDeliveryDeviation(orderLineDTO.isDeliveryDeviation());
        orderLine = orderRepository.saveOrderLine(orderLine);
        return orderLine;
    }

    @Override
    public void createDeliveryNoteData(String xmlContent) {
        List<DeliveryNoteDTO> deliveryNoteDTOs = deliveryNoteTransformer.transformDeliveryNote(xmlContent);
        for (DeliveryNoteDTO deliveryNoteDTO : deliveryNoteDTOs) {
            try {
                createOrUpdateDeliveryNote(deliveryNoteDTO);
            } catch (GloriaApplicationException gae) {
                LOGGER.error("createDeliveryNoteData failed to load xml data : " + gae.getErrorMessage());
            }
        }
    }

    @Override
    @PreAuthorize("hasAnyRole( 'DELIVERY', 'PROCURE', 'REQUESTER_FOR_PULL','IT_SUPPORT')")
    public List<OrderLineLog> getOrderLineLogs(Long orderlineId) {
        return orderRepository.findOrderLineLogsByOrderLineId(orderlineId);
    }

    @Override
    public List<OrderLineLog> addOrderLineLogs(Long orderLineId, List<OrderLineLogDTO> orderLineLogDTOs, String userId) throws GloriaApplicationException {
        List<OrderLineLog> orderLineLogs = new ArrayList<OrderLineLog>();
        if (orderLineLogDTOs != null && !orderLineLogDTOs.isEmpty()) {
            for (OrderLineLogDTO orderLineLogDTO : orderLineLogDTOs) {
                orderLineLogs.add(addOrderLineLog(orderLineId, orderLineLogDTO, userId));
            }
        }
        return orderLineLogs;
    }

    @Override
    @PreAuthorize("hasAnyRole( 'DELIVERY', 'PROCURE', 'REQUESTER_FOR_PULL')")
    public OrderLineLog addOrderLineLog(Long orderLineId, OrderLineLogDTO orderLineLogDTO, String userId) throws GloriaApplicationException {
        OrderLineLog orderLineLog = new OrderLineLog();
        orderLineLog.setOrderLine(orderRepository.findOrderLineById(orderLineId));

        UserDTO userDTO = userServices.getUser(userId);
        if (userDTO != null) {
            orderLineLog.setEventOriginatorId(userDTO.getId());
            orderLineLog.setEventOriginatorName(userDTO.getUserName());
        }
        orderLineLog.setEventTime(DateUtil.getCurrentUTCDateTime());
        orderLineLog.setEventValue(orderLineLogDTO.getEventValue());
        return orderRepository.save(orderLineLog);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'DELIVERY', 'PROCURE', 'REQUESTER_FOR_PULL','IT_SUPPORT')")
    public List<OrderLog> getOrderLogsByOrderLineId(Long orderlineId) {
        OrderLine orderLine = orderRepository.findOrderLineById(orderlineId);
        if (orderLine != null) {
            return orderRepository.findOrderLogsByOrderId(orderLine.getOrder().getOrderOID());
        }
        return new ArrayList<OrderLog>();
    }

    @Override
    public List<OrderLog> addOrderLogs(Long orderLineId, List<OrderLogDTO> orderLogDTOs, String userId) throws GloriaApplicationException {
        List<OrderLog> orderLogs = new ArrayList<OrderLog>();
        if (orderLogDTOs != null && !orderLogDTOs.isEmpty()) {
            for (OrderLogDTO orderLogDTO : orderLogDTOs) {
                orderLogs.add(addOrderLog(orderLineId, orderLogDTO, userId));
            }
        }
        return orderLogs;
    }

    @Override
    @PreAuthorize("hasAnyRole( 'DELIVERY', 'PROCURE', 'REQUESTER_FOR_PULL')")
    public OrderLog addOrderLog(Long orderLineId, OrderLogDTO orderLogDTO, String userId) throws GloriaApplicationException {
        OrderLine orderLine = orderRepository.findOrderLineById(orderLineId);
        if (orderLine != null) {
            OrderLog orderLog = new OrderLog();
            orderLog.setOrders(orderLine.getOrder());

            UserDTO userDTO = userServices.getUser(userId);
            if (userDTO != null) {
                orderLog.setEventOriginatorId(userDTO.getId());
                orderLog.setEventOriginatorName(userDTO.getUserName());
            }
            orderLog.setEventTime(DateUtil.getCurrentUTCDateTime());
            orderLog.setEventValue(orderLogDTO.getEventValue());
            return orderRepository.save(orderLog);
        }
        return null;
    }

    @Override
    @PreAuthorize("hasAnyRole( 'DELIVERY', 'WH_DEFAULT', 'PROCURE','IT_SUPPORT')")
    public List<DeliveryNoteLine> getDeliveryNoteLinesByOrderLineId(long orderLineId, String status) {
        DeliveryNoteLineStatus deliveryNoteLineStatus = null;
        if (status != null) {
            deliveryNoteLineStatus = DeliveryNoteLineStatus.valueOf(status);
        }
        List<DeliveryNoteLine> deliveryNoteLines = deliveryNoteRepository.findDeliveryNoteLinesByOrderLineId(orderLineId, deliveryNoteLineStatus);
        if (deliveryNoteLines != null && !deliveryNoteLines.isEmpty()) {
            // lazy load
            for (DeliveryNoteLine deliveryNoteLine : deliveryNoteLines) {
                deliveryNoteLine.getOrderLine().getOrderLineVersions();
                deliveryNoteLine.getMaterialLine();
            }
        }
        return deliveryNoteLines;
    }

    @Override
    @PreAuthorize("hasAnyRole( 'WH_DEFAULT', 'WH_QI', 'IT_SUPPORT')")
    public TransportLabel getTransportLabel(String userId, String whSiteId, String action, long transportLabelID) throws GloriaApplicationException {
        userServices.validateAccess(userId, whSiteId);

        TransportLabel transportLabel = null;
        if (!StringUtils.isEmpty(action) && "create".equals(action)) {
            transportLabel = DeliveryHelper.createTransportLabel(whSiteId, userServices, deliveryNoteRepository);
        } else if (!StringUtils.isEmpty(action) && "printLabel".equals(action)) {
            transportLabel = printTransportLabel(transportLabelID, 1, userId, whSiteId);
        } else {
            transportLabel = deliveryNoteRepository.findTransportLabelById(transportLabelID);
        }
        return transportLabel;
    }

    @Override
    @PreAuthorize("hasAnyRole( 'WH_DEFAULT', 'WH_QI','GR_ONLY','IT_SUPPORT')")
    public List<TransportLabel> getTransportLabels(String userId, String whSiteId, String action, long transportLabelCopies) throws GloriaApplicationException {
        userServices.validateAccess(userId, whSiteId);

        List<TransportLabel> transportLabels = new ArrayList<TransportLabel>();
        if (!StringUtils.isEmpty(action) && "create".equals(action)) {
            int x = 0;
            while (x < transportLabelCopies) {
                transportLabels.add(DeliveryHelper.createTransportLabel(whSiteId, userServices, deliveryNoteRepository));
                x++;
            }
        } else {
            transportLabels.addAll(deliveryNoteRepository.findTransportLabelByWhSiteId(whSiteId));
        }
        return transportLabels;
    }

    @Override
    public void addTransportlabel(TransportLabel transportLabel) {
        deliveryNoteRepository.save(transportLabel);
    }

    @Override
    public TransportLabel printTransportLabel(long transportLabelId, int labelCopies, String userId, String whSiteId) throws GloriaApplicationException {
        userServices.validateAccess(userId, whSiteId);

        TransportLabel transportLabel = deliveryNoteRepository.findTransportLabelByIdAndWhSiteId(transportLabelId, whSiteId);
        if (transportLabel != null) {
            List<Printer> printer = warehouseServices.findListOfPrinters(whSiteId);
            if (printer != null && !printer.isEmpty()) {
                String printerInfo = printer.get(0).getHostAddress();
                printLabelServices.doPrint(String.format(PrintLabelTemplate.ZPL_LABEL_TEMPLATE_TRANSPORT_LABEL, transportLabel.getCode(), labelCopies),
                                           printerInfo);
                return transportLabel;
            }
        }

        return null;
    }

    @Override
    public List<OrderLine> findOrderLineByRequisitionId(String requistionId) {
        return orderRepository.findOrderLineByRequisitionId(requistionId);
    }

    @Override
    public OrderLine saveOrderLine(OrderLine orderLine) {
        return orderRepository.saveOrderLine(orderLine);
    }

    @Override
    public QiMarking evaluateQIMarking(String projectId, String supplierId, String partNumber, String partName, String whSiteId) {
        QiMarking qiMarking = QiMarking.VISUAL;
        List<QualityInspectionProject> qiProjectsForCheck = warehouseServices.findQualityinspectionProjects(whSiteId);
        if (qiProjectsForCheck != null && !qiProjectsForCheck.isEmpty()) {
            for (QualityInspectionProject inspectionProject : qiProjectsForCheck) {
                if (projectId != null && projectId.equalsIgnoreCase(inspectionProject.getProject())) {
                    if (inspectionProject.isMandatory()) {
                        qiMarking = QiMarking.MANDATORY;
                    } else {
                        qiMarking = QiMarking.OPTIONAL;
                    }
                }
            }
        }

        List<QualityInspectionSupplier> qiSuppliersForCheck = warehouseServices.findQualityinspectionSupplier(whSiteId);
        if (qiSuppliersForCheck != null && !qiSuppliersForCheck.isEmpty()) {
            for (QualityInspectionSupplier inspectionSupplier : qiSuppliersForCheck) {
                if (supplierId != null && supplierId.equalsIgnoreCase(inspectionSupplier.getSupplier())) {
                    if (inspectionSupplier.isMandatory()) {
                        qiMarking = QiMarking.MANDATORY;
                    } else if (qiMarking != QiMarking.MANDATORY) {
                        qiMarking = QiMarking.OPTIONAL;
                    }
                }
            }
        }

        List<QualityInspectionPart> qiPartsForCheck = warehouseServices.findQualityinspectionPart(whSiteId);
        qiMarking = getMarkingFromQiPartRule(partNumber, partName, qiMarking, qiPartsForCheck);
        return qiMarking;
    }

    private QiMarking getMarkingFromQiPartRule(String partNumber, String partName, QiMarking existingQiMarking, List<QualityInspectionPart> qiPartsForCheck) {
        QiMarking currentMarking = existingQiMarking;
        if (qiPartsForCheck != null && !qiPartsForCheck.isEmpty()) {
            for (QualityInspectionPart inspectionPart : qiPartsForCheck) {
                if (!StringUtils.isEmpty(inspectionPart.getPartNumber()) && !StringUtils.isEmpty(inspectionPart.getPartName())) {
                    String partInfo = checkValues(partNumber).concat(checkValues(partName));
                    String ruleInfo = checkValues(inspectionPart.getPartNumber()).concat(checkValues(inspectionPart.getPartName()));
                    if (partInfo.equalsIgnoreCase(ruleInfo)) {
                        if (inspectionPart.isMandatory()) {
                            currentMarking = QiMarking.MANDATORY;
                            break;
                        } else if (currentMarking != QiMarking.MANDATORY) {
                            currentMarking = QiMarking.OPTIONAL;
                        }
                    }
                }

                if (!StringUtils.isEmpty(inspectionPart.getPartNumber()) && StringUtils.isEmpty(inspectionPart.getPartName())) {
                    String partInfo = checkValues(partNumber);
                    String ruleInfo = checkValues(inspectionPart.getPartNumber());
                    if (partInfo.equalsIgnoreCase(ruleInfo)) {
                        if (inspectionPart.isMandatory()) {
                            currentMarking = QiMarking.MANDATORY;
                            break;
                        } else if (currentMarking != QiMarking.MANDATORY) {
                            currentMarking = QiMarking.OPTIONAL;
                        }
                    }
                }

                if (!StringUtils.isEmpty(inspectionPart.getPartName()) && StringUtils.isEmpty(inspectionPart.getPartNumber())) {
                    String partInfo = checkValues(partName);
                    String ruleInfo = checkValues(inspectionPart.getPartName());
                    if (partInfo.equalsIgnoreCase(ruleInfo)) {
                        if (inspectionPart.isMandatory()) {
                            currentMarking = QiMarking.MANDATORY;
                            break;
                        } else if (currentMarking != QiMarking.MANDATORY) {
                            currentMarking = QiMarking.OPTIONAL;
                        }
                    }
                }

            }
        }
        return currentMarking;
    }

    private String checkValues(String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        return value.trim();
    }

    @Override
    public ReceiveDoc uploadReceiveDocuments(long deliveryNoteLineId, DocumentDTO document) throws GloriaApplicationException {
        DeliveryNoteLine deliveryNoteLine = deliveryNoteRepository.findDeliveryNoteLineById(deliveryNoteLineId);
        if (deliveryNoteLine == null) {
            LOGGER.error("No DeliveryNoteLine exists for id : " + deliveryNoteLineId);
            throw new GloriaSystemException("This operation cannot be performed. No DeliveryNoteLine exists for id : " + deliveryNoteLineId,
                                            GloriaExceptionConstants.INVALID_DATASET_OID);
        }
        if (deliveryNoteLine.getQualityDocs().size() >= GloriaParams.DOCUMENT_UPLOAD_LIMIT) {
            throw new GloriaApplicationException(deliveryNoteLineId, "inspectiondocupload", GloriaExceptionConstants.UPLOAD_LIMIT_EXCEEDED,
                                                 DOCUMENT_UPLOAD_LIMIT_EXCEEDED, "DocumentDTO");
        } else if (document.getSize() > GloriaParams.DOCUMENT_SIZE_LIMIT) {
            throw new GloriaApplicationException(deliveryNoteLineId, "inspectiondocupload", GloriaExceptionConstants.UPLOAD_SIZE_EXCEEDED,
                                                 DOCUMENT_SIZE_EXCEEDS_5MB, "DocumentDTO");
        }

        ReceiveDoc measuringDoc = new ReceiveDoc();
        measuringDoc.setDeliveryNoteLine(deliveryNoteLine);
        measuringDoc.setDocumentName(document.getName());
        measuringDoc.setFileContent(document.getContent());
        deliveryNoteLine.getMeasuringDocs().add(measuringDoc);
        return deliveryNoteRepository.save(measuringDoc);
    }

    @Override
    public List<OrderLineVersion> findAllOrderLineVersions(long orderLineOid) {
        return deliveryNoteRepository.findAllOrderLineVersions(orderLineOid);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'DELIVERY', 'WH_DEFAULT')")
    public void deleteDeliveryNoteLines(String deliveryNoteLineIds) {
        List<Long> lineIds = GloriaFormateUtil.getValuesAsLong(deliveryNoteLineIds);
        for (long materialLineId : lineIds) {
            DeliveryNoteLine deliveryNoteLine = deliveryNoteRepository.findDeliveryNoteLineById(materialLineId);
            if (deliveryNoteLine != null) {
                deliveryNoteRepository.delete(deliveryNoteLine);
            }
        }
    }

    @Override
    public String suggestNextLocation(String whSiteId, String partNumber, String partVersion, boolean directSend, String partModification,
            String partAffiliation) {
        return requestHeaderRepository.suggestNextlocation(partNumber, partVersion, whSiteId, directSend, partModification, partAffiliation);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'PROCURE','PROCURE-INTERNAL','WH_QI','WH_DEFAULT','IT_SUPPORT')")
    public PageObject getDeliveryNoteLinesForQI(PageObject pageObject, String materialLineStatus, String qiMarking, String whSiteId, boolean needSublines)
            throws GloriaApplicationException {
        return deliveryNoteRepository.findDeliveryNoteLinesForQI(pageObject, materialLineStatus, qiMarking, whSiteId, needSublines);
    }

    @Override
    @PreAuthorize("hasAnyRole( 'DELIVERY','IT_SUPPORT' )")
    public PageObject findOrderLines(PageObject pageObject, long teamId, String userId, String status) throws GloriaApplicationException {
        Team team = teamRepository.findById(teamId);
        String teamName = "";
        if (team != null) {
             teamName = team.getName();
        }
        return orderRepository.findOrderLines(null, null, null, null, null, null, null, null, null, null, null, null, null, false, status, false, pageObject,
                                              true, null, false, userServices.getUserCompanyCodeCodes(userId, teamName, TeamType.DELIVERY_CONTROL.name()),
                                              null, null, false);
    }

    @Override
    public List<OrderLine> assignOrderLines(List<OrderLineDTO> orderLineDTOs) throws GloriaApplicationException {
        List<OrderLine> orderLines = new ArrayList<OrderLine>();
        if (orderLineDTOs != null && !orderLineDTOs.isEmpty()) {
            for (OrderLineDTO orderLineDTO : orderLineDTOs) {
                OrderLine orderLine = orderRepository.findOrderLineById(orderLineDTO.getId());
                if (orderLine != null) {
                    if (orderLineDTO.getVersion() != orderLine.getVersion()) {
                        throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_UPDATION_OF_STALE_DATASET,
                                                             "This operation cannot be performed since the information seen in the page has already been updated.");
                    }
                    orderLine.setDeliveryControllerUserId(orderLineDTO.getDeliveryControllerUserId());
                    orderLine.setDeliveryControllerUserName(orderLineDTO.getDeliveryControllerUserName());
                    orderLines.add(orderLine);
                }
            }
        }
        return orderLines;
    }

    @Override
    public OrderLineVersion saveOrderLineVersion(OrderLineVersion orderLineVersion) {
        return orderRepository.saveOrderLineVersion(orderLineVersion);
    }

    @Override
    public OrderLineLastModified updateOrderLineLastModified(Long orderlineId) {
        OrderLine orderLine = orderRepository.findOrderLineById(orderlineId);
        if (orderLine != null) {
            OrderLineLastModified orderLineLastModified = orderLine.getOrderLineLastModified();
            orderLineLastModified.setAlertOrderStaDate(false);
            orderLineLastModified.setAlertPartVersion(false);
            orderLineLastModified.setAlertQuantity(false);
            return orderRepository.updateOrderLineLastModified(orderLineLastModified);
        }
        return null;
    }

    @Override
    public void updateMaterialLastModified(Long materialLineId) {
        MaterialLine materialLine = materialServices.findMaterialLineById(materialLineId);
        if (materialLine != null) {
            Material material = materialLine.getMaterial();
            MaterialLastModified materialLastModified = material.getMaterialLastModified();
            materialLastModified.setAlertPartVersion(false);
            requestHeaderRepository.updateMaterial(material);
        }
    }

    @Override
    public GoodsReceiptLine findGoodsReceiptLineById(long goodsReceiptLineOID) {
        GoodsReceiptLine goodsReceiptLine = deliveryNoteRepository.findGoodsReceiptLineById(goodsReceiptLineOID);
        if (goodsReceiptLine != null) {
            boolean isCancelable = true;
            List<MaterialLine> materialLines = goodsReceiptLine.getDeliveryNoteLine().getMaterialLine();
            for (MaterialLine materialLine : materialLines) {
                if (!materialLine.getStatus().isCancelGoodsReceiptAllowed(materialLine)) {
                    isCancelable = false;
                    break;
                }
            }
            goodsReceiptLine.setCancelable(isCancelable);
            return goodsReceiptLine;
        }
        return null;
    }

    @Override
    public PageObject findGoodsReceiptLinesByPlant(PageObject pageObject, String plant, String status) {
        return deliveryNoteRepository.findGoodsReceiptLinesByPlant(pageObject, plant, status);
    }

    @Override
    public GoodsReceiptLine cancelGoodsReceiptLine(GoodsReceiptLineDTO goodsReceiptLineDTO, String userId) throws GloriaApplicationException {
        if (goodsReceiptLineDTO != null) {
            GoodsReceiptLine goodsReceiptLine = deliveryNoteRepository.findGoodsReceiptLineById(goodsReceiptLineDTO.getId());
            if (goodsReceiptLine != null) {
                long quantityCancelled = goodsReceiptLineDTO.getQuantityCancelled();

                if (quantityCancelled > goodsReceiptLine.getQuantity()) {
                    throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_GOODS_RECEIPT_CANCEL_QTY,
                                                         "Cancel Quantity should be less than Received Quantity");
                }

                goodsReceiptLine.setMovementType(SAPParam.CANCELLED_MOVEMENT_TYPE);
                goodsReceiptLine.setQuantityCancelled(goodsReceiptLine.getQuantityCancelled() + quantityCancelled);
                goodsReceiptLine.setQuantity(goodsReceiptLine.getQuantity() - quantityCancelled);
                goodsReceiptLine.getStatus().cancel(goodsReceiptLine, deliveryNoteRepository);

                DeliveryNoteLine deliveryNoteLine = goodsReceiptLine.getDeliveryNoteLine();
                OrderLine orderLine = deliveryNoteLine.getOrderLine();
                orderLine.getStatus().revoke(orderLine, quantityCancelled, orderRepository);

                // cancelled quantity
                deliveryNoteLine.setAlreadyCanceledQty(0L);

                UserDTO userDTO = userServices.getUser(userId);
                List<MaterialLine> materialLines = deliveryNoteLine.getMaterialLine();
                if (materialLines != null && !materialLines.isEmpty()) {
                    for (int idx = materialLines.size() - 1; idx >= 0; idx--) {
                        MaterialLine materialLine = materialLines.get(idx);
                        materialLine.getStatus().cancelReceivedMaterialLine(quantityCancelled, deliveryNoteLine, goodsReceiptLine, materialLine,
                                                                            materialServices, warehouseServices, deliveryNoteRepository,
                                                                            requestHeaderRepository, traceabilityRepository, userDTO);
                    }
                }

                orderLine.getProcureLine().getProcureType()
                         .cancelGoodsReceipt(quantityCancelled, goodsReceiptLine.getGoodsReceiptHeader(), goodsReceiptSender, commonServices);

                // update precision values on cancel goods receipt
                updatePrecisionValuesOnCancelGoodsReceipt(orderLine);
                
                return deliveryNoteRepository.save(goodsReceiptLine);
            }
        }
        return null;
    }

    private void updatePrecisionValuesOnCancelGoodsReceipt(OrderLine orderLine) {
        OrderLineVersion currentVersion = orderLine.getCurrent();

        DeliveryNoteLine firstDeliveryNoteLine = null;
        Boolean orderStaDateOnTime = null;
        Boolean staAgreedDateOnTime = null;
        if (!orderLine.getStatus().equals(OrderLineStatus.PLACED)) {
            List<DeliveryNoteLine> deliveryNoteLines = new ArrayList<DeliveryNoteLine>(
                                                        deliveryNoteRepository.findDeliveryNoteLinesByOrderLineId(orderLine.getOrderLineOID(),
                                                                                                DeliveryNoteLineStatus.RECEIVED));
            if (deliveryNoteLines != null && !deliveryNoteLines.isEmpty()) {
                Collections.sort(deliveryNoteLines, new Comparator<DeliveryNoteLine>() {
                    @Override
                    public int compare(DeliveryNoteLine dl1, DeliveryNoteLine dl2) {
                        return dl1.getReceivedDateTime().compareTo(dl2.getReceivedDateTime());
                    }
                });
                firstDeliveryNoteLine = deliveryNoteLines.get(0);
                Date deliveryNoteDate = firstDeliveryNoteLine.getDeliveryNote().getDeliveryNoteDate();
                if (deliveryNoteDate != null) {
                    orderStaDateOnTime = isDeliveryOnTime(currentVersion.getOrderStaDate(), deliveryNoteDate);
                    staAgreedDateOnTime = isDeliveryOnTime(currentVersion.getStaAgreedDate(), deliveryNoteDate);
                }
            }
        }
        orderLine.setFirst(firstDeliveryNoteLine);
        currentVersion.setOrderStaDateOnTime(orderStaDateOnTime);
        currentVersion.setStaAgreedDateOnTime(staAgreedDateOnTime);
    }
    
    private static Boolean isDeliveryOnTime(Date anyStaDate, Date deliveryDate) {
        if (anyStaDate != null && deliveryDate != null) {
            if (deliveryDate.before(anyStaDate) || DateUtil.areDatesFromSameWeek(deliveryDate, anyStaDate)) {
                return true;
            }
            return false;
        }
        return null;
    }

    @Override
    public OrderLine revokeOrderline(OrderLineDTO orderLineDTO, String userId) throws GloriaApplicationException {
        UserDTO user = userServices.getUser(userId);
        OrderLine orderLine = orderRepository.findOrderLineById(orderLineDTO.getId());
        if (orderLineDTO.getVersion() != orderLine.getVersion()) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_UPDATION_OF_STALE_DATASET,
                                                 "This operation cannot be performed since the information seen in the page has already been updated.");
        }

        if (orderLine.getPossibleToReceiveQuantity() > orderLineDTO.getAllowedQuantity()) {
            throw new GloriaApplicationException(GloriaExceptionConstants.DECREASE_POSSIBLE_TO_RECEIVE_QTY_NOT_ALLOWED,
                                                 "Possible to receive qty cannot be decreased.");
        }

        boolean isUpdated = doUpdatePossibleToReceiveQty(orderLineDTO, user, orderLine, materialServices);

        if (isUpdated) {
            orderLine.getStatus().revoke(orderLine, 0L, orderRepository);
            orderLine.setDeliveryControllerUserId(user.getId());
            orderLine.setDeliveryControllerUserName(user.getUserName());
        }
        return orderLine;
    }
}
