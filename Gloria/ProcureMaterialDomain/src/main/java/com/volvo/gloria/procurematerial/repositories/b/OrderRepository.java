package com.volvo.gloria.procurematerial.repositories.b;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Tuple;

import com.volvo.gloria.common.c.dto.reports.ReportFilterOrderDTO;
import com.volvo.gloria.common.c.dto.reports.ReportGeneralDTO;
import com.volvo.gloria.common.c.dto.reports.ReportPartDeliveryPrecisionDTO;
import com.volvo.gloria.common.c.dto.reports.ReportPerformanceDTO;
import com.volvo.gloria.common.d.entities.CurrencyRate;
import com.volvo.gloria.procurematerial.d.entities.AttachedDoc;
import com.volvo.gloria.procurematerial.d.entities.DeliveryLog;
import com.volvo.gloria.procurematerial.d.entities.DeliverySchedule;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.Order;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLineLastModified;
import com.volvo.gloria.procurematerial.d.entities.OrderLineLog;
import com.volvo.gloria.procurematerial.d.entities.OrderLineVersion;
import com.volvo.gloria.procurematerial.d.entities.OrderLog;
import com.volvo.gloria.procurematerial.d.entities.status.orderLine.OrderLineStatus;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.c.dto.reports.ReportRow;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.persistence.GenericRepository;

/**
 * Services for OrderRepository.
 */
public interface OrderRepository extends GenericRepository<Order, Long> {

    List<OrderLine> getOrderLines(String deliveryNoteNumber, String orderNumber, String status);

    PageObject findOrders(PageObject pageObject, String deliveryFollowUpTeam, List<String> whSiteIds, boolean isDeliveryControlModule);

    void addOrder(Order order);

    Order findOrderById(Long orderOid);

    Order updateOrder(Order order);

    OrderLine findOrderLineById(Long id);

    Order getOrderForOrderLine(long orderLineId);

    Order findOrderByOrderNo(String orderNo);

    List<MaterialLine> findProcuredMaterialLinesByOrderId(long orderLineOid);

    OrderLine saveOrderLine(OrderLine orderLine);
    
    List<OrderLine> getAllOrderLines();

    List<Order> findOrderBysuffix(String suffix);

    List<OrderLine> findOrderLineByRequisitionId(String requistionId);

    void updateOrderLine(OrderLine orderLine);

    List<OrderLine> getOrderLinesByOrder(long orderId);

    PageObject findOrderLines(String expectedArrivalFrom, String expectedArrivalTo, String expectedArrival, String buildStartDateFrom, String buildStartDateTo,
            String buildStartDate, String orderStaDateFrom, String orderStaDateTo, String orderStaDate, String statusFlag, String deliveryControllerId,
            String internalExternal, String whSiteId, boolean markedAsComplete, String status, boolean calculateInStock, PageObject pageObject,
            boolean isDeliveryControlModule, String deliveryControllerTeam, boolean unassigned, List<String> companyCodes, String completeType,
            Map<String, List<String>> queryParams, boolean loadDeliverySchedules) throws GloriaApplicationException;

    OrderLog save(OrderLog orderLog);

    List<OrderLog> findOrderLogsByOrderId(Long orderId);

    OrderLineLog save(OrderLineLog orderLineLog);

    List<OrderLineLog> findOrderLineLogsByOrderLineId(Long orderlineId);

    DeliverySchedule save(DeliverySchedule deliverySchedule);

    List<OrderLine> findOrderLinesByIds(List<Long> orderLineIds);

    List<OrderLine> findOrderLinebyMaterialId(long materialOid);

    AttachedDoc save(AttachedDoc attachedDoc);

    DeliveryLog save(DeliveryLog deliveryLog);

    List<DeliveryLog> findDeliveryLogsByDeliveryScheduleId(Long deliveryScheduleId, String eventType);

    AttachedDoc findAttachedDocById(long attachedDocId);

    void delete(AttachedDoc attachedDoc);

    List<AttachedDoc> findAttachedDocs(long deliveryScheduleId);

    void delete(DeliverySchedule deliverySchedule);

    List<DeliverySchedule> findDeliverySchedules(String deliveryControllerId, Long orderLineId);

    DeliverySchedule findDeliveryScheduleById(Long deliveryScheduleId);

    void delete(OrderLine orderLine);

    OrderLineVersion saveOrderLineVersion(OrderLineVersion orderLineVersion);

    PageObject getSupplierParmaIds(PageObject pageObject, List<String> suffix, List<String> companyCodes);

    List<ReportRow> fetchOrderLinesForReport(ReportFilterOrderDTO reportFilterOrderDTO, Date fromDate, Date toDate,
            Map<String, String> companyCodeToDefaultCurrencyMap, Map<String, CurrencyRate> currencyToCurrencyRateMap);

    OrderLineLastModified updateOrderLineLastModified(OrderLineLastModified orderLineLastModified);

    List<Tuple>  getDistinctBuyerPartyIdsFromOrderLine();

    List<Tuple> pPReqAndOrderPlacedDateDifference(ReportPerformanceDTO reportPerformanceDTOParam, Date fromDate, Date toDate);

    List<Tuple> orderSTAAndDeliverySTAReport(ReportPerformanceDTO reportPerformanceDTOParam, Date fromDate, Date toDate);

    List<Tuple> materialPerformanceReportData(ReportPerformanceDTO reportPerformanceDTOParam, Date fromDate, Date toDate);

    List<Tuple> getDistinctPartsFromOrderLine();

    List<Tuple> getGeneralReportData(ReportGeneralDTO reportGeneralDTO);

    List<Order> findOrdersForWarehouse(List<String> whSiteIds, String orderNo);

    List<Tuple> getGeneralReportDataWithNullOrderLines(ReportGeneralDTO reportGeneralDTO);
    
    List<DeliverySchedule> findDeliveryScheduleByOrderLineList(List<Long> orderLineList);
    
    List<OrderLineLog> findOrderLineLogByOrderLineList(List<Long> orderLineList);

    List<Tuple> fetchDeliveryPrecisionIdentifiersForOrders(ReportPartDeliveryPrecisionDTO reportPartDeliveryPrecisionDTO, Date fromDate, 
                                                           Date toDate, boolean isSummary);

    List<Long> findOrderLineIdsByStatus(List<OrderLineStatus> orderLineStatus);

    List<OrderLine> getOrderLinesForRelatedMigratedOrder(String orderno, String partno, String partAffiliation, String partVersion, String companycode);

}
