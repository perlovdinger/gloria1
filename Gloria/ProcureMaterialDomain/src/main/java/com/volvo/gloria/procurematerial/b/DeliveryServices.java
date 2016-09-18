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
package com.volvo.gloria.procurematerial.b;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import com.volvo.gloria.common.c.dto.DeliveryLogDTO;
import com.volvo.gloria.procurematerial.c.DeliveryNoteLineStatus;
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
import com.volvo.gloria.procurematerial.d.entities.ReceiveDoc;
import com.volvo.gloria.procurematerial.d.entities.Order;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLineLastModified;
import com.volvo.gloria.procurematerial.d.entities.OrderLineLog;
import com.volvo.gloria.procurematerial.d.entities.OrderLineVersion;
import com.volvo.gloria.procurematerial.d.entities.OrderLog;
import com.volvo.gloria.procurematerial.d.entities.ProblemDoc;
import com.volvo.gloria.procurematerial.d.entities.QiDoc;
import com.volvo.gloria.procurematerial.d.entities.TransportLabel;
import com.volvo.gloria.procurematerial.d.type.receive.ReceiveType;
import com.volvo.gloria.util.DocumentDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.paging.c.PageObject;

/**
 * Service interface to MaterialDomain delivery, order, orderLine, delivery note components.
 */

public interface DeliveryServices {

    PageObject findOrders(PageObject pageObject, String deliveryManagerId, String userId, boolean isDeliveryControlModule) throws GloriaApplicationException;

    public List<Order> findOrdersForWarehouse(String userId, String whSiteId, String orderNo);

    void addOrder(Order order);

    Order updateOrder(OrderDTO orderDTO) throws GloriaApplicationException;

    Order getOrderForOrderLine(long orderLineId);

    List<Order> findOrderBySuffix(String suffix);

    void assignMaterialToOrderLine(String requisitionId, OrderLine orderLine);

    List<OrderLine> getAllOrderLines();

    List<DeliveryNote> getAllDeliveryNotes();

    void addDeliveryNote(DeliveryNote deliveryNote);

    List<DeliveryNoteLine> getDeliveryNoteLines(long deliveryNoteId, String whSiteId, DeliveryNoteLineStatus status, ReceiveType receiveType) throws GloriaApplicationException;

    DeliveryNote createOrUpdateDeliveryNote(DeliveryNoteDTO deliveryNoteDTO) throws GloriaApplicationException;

    DeliveryNoteLine updateDeliveryNoteLine(DeliveryNoteLineDTO deliveryNoteLineDTO, String userId, boolean receive) throws GloriaApplicationException;

    DeliveryNoteLine getDeliveryNoteLineById(long deliveryNoteLineOid);

    PageObject findOrderLinesForWarehouse(String internalExternal, String status, String userId, String whSiteId, boolean calculateInStock,
            PageObject pageObject) throws ParseException, GloriaApplicationException;

    PageObject findOrderLinesForDeliveryControl(String expectedArrivalFrom, String expectedArrivalTo, String expectedArrival, String buildStartDateFrom,
            String buildStartDateTo, String buildStartDate, String orderStaDateFrom, String orderStaDateTo, String orderStaDate, String statusFlag,
            String deliveryControllerId, String internalExternal, String partNumber, Boolean markedAsComplete, String status, String userId,
            boolean calculateInStock, PageObject pageObject, String deliveryControllerTeam, boolean unassigned, String completeType,
            Map<String, List<String>> queryParams, boolean loadDeliverySchedules) throws ParseException, GloriaApplicationException;

    OrderLine getOrderLine(Long orderLineId);

    List<DeliveryNoteLine> updateDeliveryNoteLines(List<DeliveryNoteLineDTO> deliveryNoteLineDTOs, boolean receive, String userId)
            throws GloriaApplicationException;

    DeliveryNote getDeliveryNoteById(long deliveryNoteId, String whSiteId);

    List<QiDoc> getQiDocs(long deliveryNoteLineId);

    List<ProblemDoc> getProblemDocuments(long deliveryNoteLineId);

    QiDoc getQiDoc(long qualityDocId);

    ProblemDoc getProblemDocument(long problemNoteDocId);

    void deleteQiDoc(long qualityDocId);

    void deleteProblemDoc(long problemNoteDocId);

    QiDoc uploadQiDocs(long deliveryNoteLineId, DocumentDTO document) throws GloriaApplicationException;

    ProblemDoc uploadProblemDocuments(long deliveryNoteLineId, DocumentDTO document) throws GloriaApplicationException;

    List<DeliverySchedule> getDeliverySchedules(String deliveryControllerId, Long orderLineId);

    DeliverySchedule getDeliverySchedule(Long deliveryScheduleId);

    List<DeliveryLog> getDeliveryLogs(Long deliveryScheduleId, String eventType);

    DeliveryLog addDeliveryLog(Long deliveryScheduleId, String eventType, DeliveryLogDTO deliveryLogDTO, String userId) throws GloriaApplicationException;

    Order findOrderById(Long orderId);

    List<OrderLog> getOrderLogs(Long orderId);

    AttachedDoc uploadAttachedDocuments(long deliveryScheduleId, DocumentDTO document) throws GloriaApplicationException;

    void deleteAttachedDoc(long attachedNoteDocId);

    AttachedDoc getAttachedDocument(long attachedDocId);

    List<AttachedDoc> getAttachedDocuments(long deliveryScheduleId);

    List<DeliverySchedule> updateDeliverySchedules(long orderLineId, List<DeliveryScheduleDTO> deliveryScheduleDTOs, String userId)
            throws GloriaApplicationException;

    OrderLine updateOrderLine(OrderLineDTO orderLineDTO, String userId) throws GloriaApplicationException;

    List<OrderLine> updateOrderLines(List<OrderLineDTO> orderLineDTOs, String userId) throws GloriaApplicationException;

    void createDeliveryNoteData(String xmlContent);

    List<OrderLineLog> getOrderLineLogs(Long orderlineId);

    OrderLineLog addOrderLineLog(Long orderLineId, OrderLineLogDTO orderLineLogDTO, String userId) throws GloriaApplicationException;

    List<OrderLineLog> addOrderLineLogs(Long orderLineId, List<OrderLineLogDTO> orderLineLogDTOs, String userId) throws GloriaApplicationException;

    List<OrderLog> getOrderLogsByOrderLineId(Long orderlineId);

    List<OrderLog> addOrderLogs(Long orderLineId, List<OrderLogDTO> orderLogDTOs, String userId) throws GloriaApplicationException;

    OrderLog addOrderLog(Long orderLineId, OrderLogDTO orderLogDTO, String userId) throws GloriaApplicationException;

    List<DeliveryNoteLine> getDeliveryNoteLinesByOrderLineId(long orderLineId, String status);

    DeliverySchedule updateDeliverySchedule(long orderLineId, DeliveryScheduleDTO deliveryScheduleDTO, String deliveryControllerId)
            throws GloriaApplicationException;

    void addTransportlabel(TransportLabel transportLabel);

    List<TransportLabel> getTransportLabels(String userId, String whSiteId, String action, long transportLabelCopies) throws GloriaApplicationException;

    TransportLabel getTransportLabel(String userId, String whSiteId, String action, long transportLabelID) throws GloriaApplicationException;

    TransportLabel printTransportLabel(long transportLabelId, int labelCopies, String userId, String whSiteId) throws GloriaApplicationException;

    List<OrderLine> findOrderLineByRequisitionId(String requisitionId);

    OrderLine saveOrderLine(OrderLine orderLine);

    QiMarking evaluateQIMarking(String projectId, String supplierId, String partNumber, String partName, String whSiteId);

    ReceiveDoc uploadReceiveDocuments(long deliveryNoteLineId, DocumentDTO document) throws GloriaApplicationException;

    void deleteReceiveDocument(long inspectionDocId);

    ReceiveDoc getReceiveDocument(long inspectionDocId);

    List<ReceiveDoc> getReceiveDocuments(long deliveryNoteLineId);

    List<OrderLineVersion> findAllOrderLineVersions(long orderLineOid);

    void deleteDeliveryNoteLines(String deliveryNoteLineIds);

    OrderLine updateDeliveryDeviation(OrderLineDTO orderLineDTO, String userId) throws GloriaApplicationException;

    DeliveryNoteSubLine updateDeliveryNoteSubLine(DeliveryNoteSubLineDTO deliveryNoteSubLineDTO) throws GloriaApplicationException;

    List<DeliveryNoteSubLine> getDeliveryNoteSubLinesByDeliveryNoteLineId(long deliveryNoteLineOid, String materialLineStatus)
            throws GloriaApplicationException;

    String suggestNextLocation(String whSiteId, String partNumber, String partVersion, boolean directSend, String partModification, String partAffiliation);

    PageObject getDeliveryNoteLinesForQI(PageObject pageObject, String materialLineStatus, String qiMarking, String whSiteId, boolean needSublines)
            throws GloriaApplicationException;

    DeliveryNoteLine getDeliveryNoteLineByIdAndSetMaterialLine(long deliveryNoteLineOid);

    PageObject findOrderLines(PageObject pageObject, long teamId, String userId, String status) throws GloriaApplicationException;

    List<OrderLine> assignOrderLines(List<OrderLineDTO> orderLineDTOs) throws GloriaApplicationException;

    OrderLineVersion saveOrderLineVersion(OrderLineVersion orderLineVersion);

    OrderLineLastModified updateOrderLineLastModified(Long orderlineId);

    PageObject findGoodsReceiptLinesByPlant(PageObject pageObject, String plant, String status);

    GoodsReceiptLine findGoodsReceiptLineById(long goodsReceiptLineOID);

    GoodsReceiptLine cancelGoodsReceiptLine(GoodsReceiptLineDTO goodsReceiptLineDTO, String userId) throws GloriaApplicationException;

    List<OrderLine> updateOrderlinesQIMarking(List<OrderLineDTO> orderLineDTOs, String userId) throws GloriaApplicationException;

    OrderLine updateOrderlineQIMarking(OrderLineDTO orderLineDTO, String userId) throws GloriaApplicationException;

    OrderLine revokeOrderline(OrderLineDTO orderLineDTO, String loggedInUserId) throws GloriaApplicationException;

    void updateMaterialLastModified(Long materialLineId);
}
