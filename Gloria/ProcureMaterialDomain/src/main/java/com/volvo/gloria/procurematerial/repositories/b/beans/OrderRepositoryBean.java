package com.volvo.gloria.procurematerial.repositories.b.beans;

import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_COMPANY_CODE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_CURRENCY;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_CURRENCY_ORIGINAL;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_ORDERLINE_VERSION;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_TOTAL_PRICE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_TOTAL_PRICE_ORIGINAL;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_UNIT_OF_PRICE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_UNIT_PRICE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_UNIT_PRICE_ORIGINAL;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang.StringUtils;

import com.volvo.gloria.common.c.dto.reports.ReportFilterOrderDTO;
import com.volvo.gloria.common.c.dto.reports.ReportGeneralDTO;
import com.volvo.gloria.common.c.dto.reports.ReportPartDeliveryPrecisionDTO;
import com.volvo.gloria.common.c.dto.reports.ReportPerformanceDTO;
import com.volvo.gloria.common.d.entities.CurrencyRate;
import com.volvo.gloria.procurematerial.b.ProcurementDtoTransformer;
import com.volvo.gloria.procurematerial.c.CompleteType;
import com.volvo.gloria.procurematerial.c.DeliveryStatusFlag;
import com.volvo.gloria.procurematerial.c.EventType;
import com.volvo.gloria.procurematerial.c.QiMarking;
import com.volvo.gloria.procurematerial.c.dto.OrderLineDTO;
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
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.orderLine.OrderLineStatus;
import com.volvo.gloria.procurematerial.d.type.directsend.DirectSendType;
import com.volvo.gloria.procurematerial.d.type.internalexternal.InternalExternal;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.util.DeliveryHelper;
import com.volvo.gloria.reports.ReportHelper;
import com.volvo.gloria.reports.c.dto.ReportSupplierParmaIdDTO;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.GloriaFormateUtil;
import com.volvo.gloria.util.c.GloriaExceptionConstants;
import com.volvo.gloria.util.c.dto.reports.ReportColumn;
import com.volvo.gloria.util.c.dto.reports.ReportRow;
import com.volvo.gloria.util.paging.c.JpaAttribute;
import com.volvo.gloria.util.paging.c.JpaAttributeType;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.paging.c.PageResults;
import com.volvo.gloria.util.persistence.GenericAbstractRepositoryBean;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * Repository bean for Order.
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class OrderRepositoryBean extends GenericAbstractRepositoryBean<Order, Long> implements OrderRepository {

    @Inject
    private MaterialHeaderRepository requestHeaderRepository;
    
    @Inject
    private ProcurementDtoTransformer procurementDtoTransformer;
    
    @PersistenceContext(unitName = "ProcureMaterialDomainPU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<OrderLine> getOrderLines(String deliveryNoteNumber, String orderNumber, String status) {
        Query query = getEntityManager().createNamedQuery("getOrderLinesByStatus");
        query.setParameter("status", status);
        query.setParameter("orderNo", orderNumber);
        query.setParameter("deliveryNoteNo", deliveryNoteNumber);
        return query.getResultList();
    }

    @Override
    public List<Tuple> getDistinctBuyerPartyIdsFromOrderLine() {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root<OrderLine> root = criteriaQuery.from(OrderLine.class); 
        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.and(root.get("buyerPartyId").isNotNull()));
        criteriaQuery.where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));
        TypedQuery<Tuple> query = entityManager.createQuery(criteriaQuery.select(criteriaBuilder.tuple(root.get("buyerPartyId"))).distinct(true));
        return query.getResultList();
    }
   
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public PageObject findOrders(PageObject pageObject, String deliveryFollowUpTeam, List<String> whSiteIds, boolean isDeliveryControlModule) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(Order.class);

        Map<String, JpaAttribute> fieldMap = new HashMap<String, JpaAttribute>();
        fieldMap.put("internalExternal", new JpaAttribute("internalExternal", JpaAttributeType.STRINGTYPE));
        fieldMap.put("deliveryControllerUserId", new JpaAttribute("orderLines.deliveryControllerUserId", JpaAttributeType.STRINGTYPE));
        fieldMap.put("orderNo", new JpaAttribute("orderNo", JpaAttributeType.STRINGTYPE));
        fieldMap.put("buyerCode", new JpaAttribute("orderLines.orderLineVersions.buyerId,orderLines.orderLineVersions.buyerName", JpaAttributeType.STRINGTYPE));
        fieldMap.put("supplierId", new JpaAttribute("supplierId", JpaAttributeType.STRINGTYPE));
        fieldMap.put("supplierName", new JpaAttribute("supplierName", JpaAttributeType.STRINGTYPE));
        fieldMap.put("suffix", new JpaAttribute("suffix", JpaAttributeType.STRINGTYPE));
        fieldMap.put("projectId", new JpaAttribute("projectId", JpaAttributeType.STRINGTYPE));

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        if (deliveryFollowUpTeam != null) {
            predicatesRules.add(criteriaBuilder.and(root.get("deliveryControllerTeam").in((Object[]) deliveryFollowUpTeam.split(","))));
        }

        if (!isDeliveryControlModule) {
            predicatesRules.add(criteriaBuilder.and(root.get("shipToId").in(whSiteIds)));
        }

        predicatesRules.add(criteriaBuilder.notEqual(root.get("orderLines").get("status"), OrderLineStatus.COMPLETED));
        
        // count
        CriteriaQuery criteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject, fieldMap, true);
        Query countQuery = entityManager.createQuery(criteriaQueryFromPageObject);
        pageObject.setCount(((Long) countQuery.getSingleResult()).intValue());

        CriteriaQuery resultSetCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject, fieldMap,
                                                                                   false);
        Query resultSetQuery = entityManager.createQuery(resultSetCriteriaQueryFromPageObject);
        resultSetQuery.setFirstResult(pageObject.getFirstResult());
        resultSetQuery.setMaxResults(pageObject.getMaxResult());

        pageObject.setGridContents(new ArrayList<PageResults>(DeliveryHelper.transformAsDTO(resultSetQuery.getResultList())));
        return pageObject;
    }

    @Override
    public void addOrder(Order order) {
        getEntityManager().persist(order);
    }

    @Override
    public Order findOrderById(Long orderOid) {
        Order order = null;
        order = getEntityManager().find(Order.class, orderOid);
        return order;
    }

    @Override
    public Order updateOrder(Order order) {
        getEntityManager().merge(order);
        return order;
    }

    @Override
    @SuppressWarnings("unchecked")
    public OrderLine findOrderLineById(Long id) {
        Query query = getEntityManager().createNamedQuery("findOrderLineWithMaterialById");
        query.setParameter("orderLineOID", id);
        List<OrderLine> orderLines = query.getResultList();
        if (orderLines != null && !orderLines.isEmpty()) {
            return orderLines.get(0);
        }
        return null;
    }

    @Override
    public Order getOrderForOrderLine(long orderLineId) {
        return findOrderLineById(orderLineId).getOrder();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Order> findOrderBysuffix(String suffix) {
        Query query = getEntityManager().createNamedQuery("getOrderBySuffix");
        query.setParameter("suffix", suffix);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<OrderLine> findOrderLineByRequisitionId(String requisitionId) {
        Query query = getEntityManager().createNamedQuery("getOrderLineByRequisitionId");
        query.setParameter("requisitionId", requisitionId);
        return query.getResultList();
    }

    @Override
    public void updateOrderLine(OrderLine orderLine) {
        getEntityManager().merge(orderLine);
    }

    @Override
    public Order findOrderByOrderNo(String orderNo) {
        TypedQuery<Order> query = getEntityManager().createNamedQuery("getOrderWithOrderLinesByOrderNo", Order.class);
        query.setParameter("orderNo", orderNo);
        List<Order> orders = query.getResultList();
        if (orders != null && !orders.isEmpty()) {
            return orders.get(0);
        }
        return null;
    }
    
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<MaterialLine> findProcuredMaterialLinesByOrderId(long orderLineId) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(MaterialLine.class);
        Join materialJoin = root.join("material", JoinType.LEFT);
        Join materialHeaderJoin = materialJoin.join("materialHeader", JoinType.LEFT);
        Join acceptedHeaderVersionJoin = materialHeaderJoin.join("accepted", JoinType.LEFT);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.and(materialJoin.get("orderLine").get("orderLineOID").in(orderLineId)));
        predicatesRules.add(criteriaBuilder.and(root.get("status").in(MaterialLineStatus.ORDER_PLACED_EXTERNAL, MaterialLineStatus.ORDER_PLACED_INTERNAL)));
        Predicate wherePredicate = criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()]));

        List<javax.persistence.criteria.Order> orderByColumns = new ArrayList<javax.persistence.criteria.Order>();
        orderByColumns.add(criteriaBuilder.desc(materialJoin.get("materialType")));
        orderByColumns.add(criteriaBuilder.desc(criteriaBuilder.selectCase().when(acceptedHeaderVersionJoin.get("outboundStartDate").isNull(), 0).otherwise(1)));
        orderByColumns.add(criteriaBuilder.asc(acceptedHeaderVersionJoin.get("outboundStartDate")));
        
        Query resultSetQuery = entityManager.createQuery(criteriaQuery.select(root).distinct(true).where(wherePredicate).orderBy(orderByColumns));
        return resultSetQuery.getResultList();
    }

    @Override
    public OrderLine saveOrderLine(OrderLine orderLine) {
        OrderLine orderLineToSave = orderLine;
        if (orderLine.getOrderLineOID() < 1) {
            getEntityManager().persist(orderLine);
        } else {
            orderLineToSave = getEntityManager().merge(orderLine);
        }
        return orderLineToSave;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<OrderLine> getAllOrderLines() {
        Query query = getEntityManager().createNamedQuery("getAllOrderLines");
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<OrderLine> getOrderLinesByOrder(long orderId) {
        Query query = getEntityManager().createNamedQuery("getOrderLinesByOrder");
        query.setParameter("orderOID", orderId);
        return query.getResultList();
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public PageObject findOrderLines(String expectedArrivalFrom, String expectedArrivalTo, String expectedArrival, String buildStartDateFrom, String buildStartDateTo,
            String buildStartDate, String orderStaDateFrom, String orderStaDateTo, String orderStaDate, String statusFlag,
            String deliveryControllerId, String internalExternal, String whSiteId, boolean markedAsComplete, String status,
            boolean calculateInStock, PageObject pageObject, boolean isDeliveryControlModule, String deliveryControllerTeam, boolean unassigned,
            List<String> companyCodes, String completeType, Map<String, List<String>> queryParams, boolean loadDeliverySchedules) 
                    throws GloriaApplicationException {
      
        Map<String, JpaAttribute> fieldMap = new HashMap<String, JpaAttribute>();
        fieldMap.put("id", new JpaAttribute("orderLineOID", JpaAttributeType.NUMBERTYPE));
        fieldMap.put("directSend", new JpaAttribute("deliveryNoteLines.deliveryNoteSubLines.directSend", JpaAttributeType.ENUMTYPE, DirectSendType.class));
        fieldMap.put("supplierId", new JpaAttribute("order.supplierId", JpaAttributeType.STRINGTYPE));
        fieldMap.put("supplierName", new JpaAttribute("order.supplierName", JpaAttributeType.STRINGTYPE));
        fieldMap.put("projectId", new JpaAttribute("projectId", JpaAttributeType.STRINGTYPE));
        fieldMap.put("orderNo", new JpaAttribute("order.orderNo", JpaAttributeType.STRINGTYPE));
        fieldMap.put("partVersion", new JpaAttribute("orderLineVersions.partVersion", JpaAttributeType.STRINGTYPE));
        fieldMap.put("partName", new JpaAttribute("partName", JpaAttributeType.STRINGTYPE));
        fieldMap.put("buildStartDate", new JpaAttribute("materials.materialHeader.accepted.outboundStartDate", JpaAttributeType.DATETYPE));
        fieldMap.put("orderStaDate", new JpaAttribute("current.orderStaDate", JpaAttributeType.DATETYPE));
        fieldMap.put("staAcceptedDate", new JpaAttribute("current.staAcceptedDate", JpaAttributeType.DATETYPE));
        fieldMap.put("agreedStaDate", new JpaAttribute("staAgreedDate", JpaAttributeType.DATETYPE));
        fieldMap.put("referenceGroups", new JpaAttribute("procureLine.referenceGroups", JpaAttributeType.STRINGTYPE));
        fieldMap.put("reference", new JpaAttribute("procureLine.referenceGps", JpaAttributeType.STRINGTYPE));
        fieldMap.put("consignorName", new JpaAttribute("procureLine.supplier.supplierName", JpaAttributeType.STRINGTYPE));
        fieldMap.put("expectedDate", new JpaAttribute("earliestExpectedDate", JpaAttributeType.DATETYPE));
        fieldMap.put("qiMarking", new JpaAttribute("qiMarking", JpaAttributeType.ENUMTYPE, QiMarking.class));
        fieldMap.put("referenceId", new JpaAttribute("materials.materialHeader.referenceId", JpaAttributeType.STRINGTYPE));
        fieldMap.put("internalExternal", new JpaAttribute("order.internalExternal", JpaAttributeType.STRINGTYPE));
        fieldMap.put("buyerId", new JpaAttribute("orderLineVersions.buyerId,orderLineVersions.buyerName", JpaAttributeType.STRINGTYPE));
        fieldMap.put("suffix", new JpaAttribute("order.suffix", JpaAttributeType.STRINGTYPE));
        fieldMap.put("deliveryControllerUserId", new JpaAttribute("deliveryControllerUserId,deliveryControllerUserName", JpaAttributeType.STRINGTYPE));
        fieldMap.put("materialControllerUserId", new JpaAttribute("procureLine.materialControllerId,procureLine.materialControllerName", 
                                                                  JpaAttributeType.STRINGTYPE));    
        fieldMap.put("materialPartModification", new JpaAttribute("materials.partModification", JpaAttributeType.STRINGTYPE));    
        
        // @TODO: support multi field check
        fieldMap.put("partAlias", new JpaAttribute("supplierPartNo", JpaAttributeType.STRINGTYPE));
        fieldMap.put("partNumber", new JpaAttribute("partNumber", JpaAttributeType.STRINGTYPE));
        fieldMap.put("partModification", new JpaAttribute("procureLine.pPartModification", JpaAttributeType.STRINGTYPE));
        fieldMap.put("orderQueryStr", new JpaAttribute("partNumber,supplierPartNo,order.supplierId", JpaAttributeType.STRINGTYPE));

        // default sort
        pageObject.setDefaultSort("id", "asc");

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(OrderLine.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();

        if (!StringUtils.isEmpty(status)) {
            predicatesRules.add(root.get("status").in(GloriaFormateUtil.getValuesAsEnums(status, OrderLineStatus.class)));
        }

        if (!StringUtils.isEmpty(completeType)) {
            predicatesRules.add(root.get("completeType").in(GloriaFormateUtil.getValuesAsEnums(completeType, CompleteType.class)));
        }

        if (isDeliveryControlModule) {
            predicatesRules.add(root.get("order").get("companyCode").in(companyCodes));
        }

        updatePredicatesWithOrders(deliveryControllerId, internalExternal, whSiteId, criteriaBuilder, root, predicatesRules, isDeliveryControlModule,
                                   deliveryControllerTeam, unassigned);

        predicatesRules.addAll(addDatePredicates(buildStartDateFrom, buildStartDateTo, buildStartDate, 
                                                 criteriaBuilder, root.joinList("materials", JoinType.LEFT).get("materialHeader").get("accepted").get("outboundStartDate")));
        
        predicatesRules.addAll(addDatePredicates(orderStaDateFrom, orderStaDateTo, orderStaDate, 
                                                 criteriaBuilder, root.get("current").get("orderStaDate")));
        
        predicatesRules.addAll(addDatePredicates(expectedArrivalFrom, expectedArrivalTo, expectedArrival, 
                                                 criteriaBuilder, root.joinList("deliverySchedule").get("expectedDate")));
        if (statusFlag != null) {
            predicatesRules.add(root.joinList("deliverySchedule").get("statusFlag").in(DeliveryStatusFlag.valueOf(statusFlag)));
        }
        
        //three fields used for order date time
        String orderDateTimeFrom = getParameterFromMap(queryParams, "orderDateTimeFrom");
        String orderDateTimeTo = getParameterFromMap(queryParams, "orderDateTimeTo");
        String orderDateTime = getParameterFromMap(queryParams, "orderDateTime");
        
        predicatesRules.addAll(addDatePredicates(orderDateTimeFrom, orderDateTimeTo, orderDateTime, criteriaBuilder, root.join("order").get("orderDateTime")));

        //three fields used for requiredStaDateTime
        String requiredStaDateFrom = getParameterFromMap(queryParams, "requiredStaDateFrom");
        String requiredStaDateTo = getParameterFromMap(queryParams, "requiredStaDateTo");
        String requiredStaDateTime = getParameterFromMap(queryParams, "requiredStaDate");
        predicatesRules.addAll(addDatePredicates(requiredStaDateFrom, requiredStaDateTo, requiredStaDateTime, 
                                                 criteriaBuilder, root.join("procureLine").get("requiredStaDate")));

        CriteriaQuery criteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject, fieldMap, true);
        Query countQuery = entityManager.createQuery(criteriaQueryFromPageObject);
        pageObject.setCount(((Long) countQuery.getSingleResult()).intValue());

        // below query is used to fetch only required orderline ids for a page and the Object id list is passed to fetch data for next query
        // This query helps in improving performance of over all fetch
        criteriaQuery.select(root.get("orderLineOID"));
        Query orderLineOIDRsetQuery = entityManager.createQuery(PageObject.buildQuery(criteriaBuilder, criteriaQuery, criteriaQuery.getSelection(),
                                                                                         predicatesRules, pageObject, fieldMap, false));
        orderLineOIDRsetQuery.setFirstResult(pageObject.getFirstResult());
        orderLineOIDRsetQuery.setMaxResults(pageObject.getMaxResult());

        List<Long> orderLineOIDList = orderLineOIDRsetQuery.getResultList();

        // below query uses above list of order ids to fetch data
        predicatesRules.add(criteriaBuilder.and(root.get("orderLineOID").in(orderLineOIDList)));
        
        CriteriaQuery resultSetCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject, fieldMap,
                                                                                   false);
        Query resultSetQuery = entityManager.createQuery(resultSetCriteriaQueryFromPageObject);
      
        List<OrderLineDTO> orderLineDTOs = procurementDtoTransformer.transformAsOrderLineDTOs(resultSetQuery.getResultList(), loadDeliverySchedules, orderLineOIDList);
        if (orderLineDTOs != null) {
            updateWithEarliestBuildStartDate(orderLineDTOs);
            for (OrderLineDTO orderLineDTO : orderLineDTOs) {
                if (calculateInStock) {
                    double stockBalance = requestHeaderRepository.calculateStockBalance(orderLineDTO.getPartNumber(), orderLineDTO.getPartVersion(), whSiteId,
                                                                                        orderLineDTO.getPartModification(), orderLineDTO.getPartAffiliation());
                    if (stockBalance > 0) {
                        orderLineDTO.setInStock(true);
                    }
                }
            }
            pageObject.setGridContents(new ArrayList<PageResults>(orderLineDTOs));
        } else {
            pageObject.setGridContents(null);
        }

        return pageObject;
    }

    private String getParameterFromMap(Map<String, List<String>> queryParams, String parameter) {
        String parameterToReturn = "";
        if (queryParams != null) {
            List<String> parameterList = queryParams.get(parameter);
            if (parameterList != null) {
                parameterToReturn = (String) parameterList.get(parameterList.size() - 1);
            }
        }
        return parameterToReturn;
    }
    
    private void updateWithEarliestBuildStartDate(List<OrderLineDTO> orderLineDTOs) {
        Map<Long, Date> buildStartDateForOrder = new HashMap<Long, Date>();
        for (OrderLineDTO orderLineDTO : orderLineDTOs) {
            Date buildStartDate = orderLineDTO.getBuildStartDate();
            if (buildStartDate != null
                    && (!buildStartDateForOrder.containsKey(orderLineDTO.getOrderId()) 
                            || (buildStartDateForOrder.get(orderLineDTO.getOrderId()) != null 
                            && buildStartDateForOrder.get(orderLineDTO.getOrderId()).compareTo(buildStartDate) > 0))) {
                buildStartDateForOrder.put(orderLineDTO.getOrderId(), buildStartDate);
            }
        }

        for (OrderLineDTO dto : orderLineDTOs) {
            dto.setBuildStartDate(buildStartDateForOrder.get(dto.getOrderId()));
        }
    }

    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void updatePredicatesWithOrders(String deliveryControllerId, String internalExternal, String whSiteId, CriteriaBuilder criteriaBuilder, 
            Root root, List<Predicate> predicatesRules, boolean isDeliveryControlModule, String deliveryControllerTeam, boolean unassigned) {

        if (!StringUtils.isEmpty(whSiteId) && !isDeliveryControlModule) {
            predicatesRules.add(root.get("order").get("shipToId").in(whSiteId));
        }

        if (internalExternal != null) {
            predicatesRules.add(root.get("order").get("internalExternal").in(GloriaFormateUtil.getValuesAsEnums(internalExternal, InternalExternal.class)));
        }

        if (deliveryControllerId != null) {
            predicatesRules.add(root.get("deliveryControllerUserId").in(deliveryControllerId));
        } else if (unassigned) {
            predicatesRules.add(root.get("deliveryControllerUserId").isNull());
        }

        if (!StringUtils.isEmpty(deliveryControllerTeam)) {
            predicatesRules.add(root.get("order").get("deliveryControllerTeam").in(deliveryControllerTeam));
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List<Predicate> addDatePredicates(String fromDate, String toDate, String beforeDate, CriteriaBuilder criteriaBuilder,
            Path<Date> field) throws GloriaApplicationException {
        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        try {
            if (StringUtils.isNotBlank(fromDate) || StringUtils.isNotBlank(toDate)) {
                createPredicateRulesForArrivalDate(predicatesRules, field, criteriaBuilder, fromDate, toDate);
            }
            if (StringUtils.isNotBlank(beforeDate)) {
                Path<Date> expectedDate = field;
                Date dateToMatch = DateUtil.getStringAsDate(beforeDate);
                Date dateRangeFrom = DateUtil.getDateWithZeroTime(dateToMatch);
                Date dateRangeTo = DateUtil.getNextDate(dateRangeFrom);
                predicatesRules.add(criteriaBuilder.greaterThanOrEqualTo((Expression) expectedDate, dateRangeFrom));
                predicatesRules.add(criteriaBuilder.lessThan((Expression) expectedDate, dateRangeTo));
            }
        } catch (ParseException pe) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_DATE_FORMAT, "Failed to parse date.");
        }
        return predicatesRules;
    }

    @Override
    public OrderLog save(OrderLog orderLog) {
        OrderLog orderLogToSave = orderLog;
        if (orderLog.getEventLogOid() < 1) {
            getEntityManager().persist(orderLog);
        } else {
            orderLogToSave = getEntityManager().merge(orderLog);
        }
        return orderLogToSave;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<OrderLog> findOrderLogsByOrderId(Long orderId) {
        Query query = getEntityManager().createNamedQuery("findOrderLogsByOrderId");
        query.setParameter("id", orderId);
        return query.getResultList();
    }

    @Override
    public OrderLineLog save(OrderLineLog instanceToSave) {
        OrderLineLog orderLoneLogToSave = instanceToSave;
        if (instanceToSave.getEventLogOid() < 1) {
            getEntityManager().persist(instanceToSave);
        } else {
            orderLoneLogToSave = getEntityManager().merge(instanceToSave);
        }
        return orderLoneLogToSave;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<OrderLineLog> findOrderLineLogsByOrderLineId(Long orderlineId) {
        Query query = getEntityManager().createNamedQuery("findOrderLineLogsByOrderLineId");
        query.setParameter("orderlineId", orderlineId);
        return query.getResultList();
    }

    @Override
    public DeliverySchedule save(DeliverySchedule deliverySchedule) {
        DeliverySchedule deliveryScheduleToSave = deliverySchedule;
        if (deliverySchedule.getDeliveryScheduleOID() < 1) {
            getEntityManager().persist(deliverySchedule);
        } else {
            deliveryScheduleToSave = getEntityManager().merge(deliverySchedule);
        }
        return deliveryScheduleToSave;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void createPredicateRulesForArrivalDate(List<Predicate> predicacteRules, Path<Date> field,
            CriteriaBuilder criteriaBuilder, String expectedArrivalFrom, String expectedArrivalTo) throws ParseException {
        if ((expectedArrivalFrom != null) && (expectedArrivalTo != null)) {
            Date expectedArrivalDateFrom = DateUtil.getStringAsDate(expectedArrivalFrom);
            Date expectedArrivalDateTo = DateUtil.getStringAsDate(expectedArrivalTo);
            Date dateRangeFrom = DateUtil.getDateWithZeroTime(expectedArrivalDateFrom);
            Date dateRangeTo = DateUtil.getDateWithZeroTime(DateUtil.getNextDate(expectedArrivalDateTo));
            predicacteRules.add(criteriaBuilder.greaterThanOrEqualTo((Expression) field, dateRangeFrom));
            predicacteRules.add(criteriaBuilder.lessThan((Expression) field, dateRangeTo));
        }

        if ((expectedArrivalFrom != null) && (expectedArrivalTo == null)) {
            Date expectedArrivalDateFrom = DateUtil.getStringAsDate(expectedArrivalFrom);
            Date dateRangeFrom = DateUtil.getDateWithZeroTime(expectedArrivalDateFrom);
            predicacteRules.add(criteriaBuilder.greaterThanOrEqualTo((Expression) field, dateRangeFrom));
        }

        if ((expectedArrivalFrom == null) && (expectedArrivalTo != null)) {
            Date expectedArrivalDateTo = DateUtil.getStringAsDate(expectedArrivalTo);
            Date dateRangeTo = DateUtil.getDateWithZeroTime(expectedArrivalDateTo);
            predicacteRules.add(criteriaBuilder.lessThanOrEqualTo((Expression) field, dateRangeTo));
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<OrderLine> findOrderLinesByIds(List<Long> orderLineIds) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(OrderLine.class);

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root)
                .where(root.get("orderLineOID").in(orderLineIds));

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        return resultSetQuery.getResultList();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<OrderLine> findOrderLinebyMaterialId(long materialOid) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(OrderLine.class);

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root)
                .where(criteriaBuilder.equal(root.get("materials").get("materialOID"), materialOid));

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        return resultSetQuery.getResultList();
    }
    
    @Override
    public DeliverySchedule findDeliveryScheduleById(Long deliveryScheduleId) {
        return getEntityManager().find(DeliverySchedule.class, deliveryScheduleId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DeliverySchedule> findDeliverySchedules(String deliveryControllerId, Long orderLineId) {
        Query query = getEntityManager().createNamedQuery("findDeliverySchedulesByOrderLineAndDeliveryControllerId");
        query.setParameter("id", orderLineId);
        query.setParameter("deliveryControllerId", deliveryControllerId);
        return query.getResultList();
    }

    @Override
    public void delete(DeliverySchedule deliverySchedule) {
        getEntityManager().remove(deliverySchedule);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AttachedDoc> findAttachedDocs(long deliveryScheduleId) {
        Query query = getEntityManager().createNamedQuery("findAttachedDocsByDeliveryScheduleId");
        query.setParameter("id", deliveryScheduleId);
        return query.getResultList();
    }

    @Override
    public void delete(AttachedDoc attachedDoc) {
        getEntityManager().remove(attachedDoc);
    }
    
    @Override
    public AttachedDoc findAttachedDocById(long attachedDocId) {
        return getEntityManager().find(AttachedDoc.class, attachedDocId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DeliveryLog> findDeliveryLogsByDeliveryScheduleId(Long deliveryScheduleId, String eventType) {
        Query query = getEntityManager().createNamedQuery("findDeliveryLogsByDeliveryScheduleId");
        query.setParameter("id", deliveryScheduleId);
        query.setParameter("eventType", EventType.valueOf(eventType));
        return query.getResultList();
    }

    @Override
    public DeliveryLog save(DeliveryLog deliveryLog) {
        DeliveryLog deliveryLogToSave = deliveryLog;
            if (deliveryLog.getEventLogOid() < 1) {
                getEntityManager().persist(deliveryLog);
            } else {
                deliveryLogToSave = getEntityManager().merge(deliveryLog);
            }
        return deliveryLogToSave;
    }
    
    @Override
    public AttachedDoc save(AttachedDoc attachedDoc) {
        AttachedDoc attachedDocToSave = attachedDoc;
            if (attachedDoc.getAttachedDocOID() < 1) {
                getEntityManager().persist(attachedDoc);
            } else {
                attachedDocToSave = getEntityManager().merge(attachedDoc);
            }
        return attachedDocToSave;
    }

    @Override
    public void delete(OrderLine orderLine) {
        getEntityManager().remove(orderLine);
    }
    
    @Override
    public OrderLineVersion saveOrderLineVersion(OrderLineVersion orderLineVersion) {
        OrderLineVersion orderLineVersionToSave = orderLineVersion;
        if (orderLineVersion.getOrderLineVersionOid() < 1) {
            getEntityManager().persist(orderLineVersion);
        } else {
            orderLineVersionToSave = getEntityManager().merge(orderLineVersion);
        }
        return orderLineVersionToSave;
    }
    
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public PageObject getSupplierParmaIds(PageObject pageObject, List<String> suffixes, List<String> companyCodes) {
        Map<String, JpaAttribute> fieldMap = new HashMap<String, JpaAttribute>();
        fieldMap.put("id", new JpaAttribute("supplierId", JpaAttributeType.STRINGTYPE));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(Order.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.in(root.get("orderLines").get("materials").get("financeHeader").get("companyCode")).value(companyCodes));
        predicatesRules.add(criteriaBuilder.in(root.get("suffix")).value(suffixes));

        CriteriaQuery resultCountCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root.get("supplierId"), predicatesRules,
                                                                                     pageObject, fieldMap, true);
        Query countQuery = entityManager.createQuery(resultCountCriteriaQueryFromPageObject);
        Long value = (Long) countQuery.getSingleResult();
        pageObject.setCount(value.intValue());

        Selection<ReportSupplierParmaIdDTO> selection = criteriaBuilder.construct(ReportSupplierParmaIdDTO.class, root.get("supplierId"),
                                                                                  root.get("supplierId"), root.get("supplierName"));

        CriteriaQuery resultSetCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, selection, predicatesRules, pageObject,
                                                                                   fieldMap, false);
        Query queryForResultSets = entityManager.createQuery(resultSetCriteriaQueryFromPageObject);

        // change the type
        List<ReportSupplierParmaIdDTO> reportSupplierParmaIdDTOs = queryForResultSets.getResultList();
        if (reportSupplierParmaIdDTOs != null && !reportSupplierParmaIdDTOs.isEmpty()) {
            pageObject.setGridContents(new ArrayList<PageResults>(reportSupplierParmaIdDTOs));
        } else {
            pageObject.setGridContents(null);
        }
        return pageObject;
    }
    
   
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<ReportRow> fetchOrderLinesForReport(ReportFilterOrderDTO reportFilterOrderDTO, Date fromDate, Date toDate,
            Map<String, String> companyCodeToDefaultCurrencyMap, Map<String, CurrencyRate> currencyToCurrencyRateMap) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root root = criteriaQuery.from(OrderLine.class);
        Join materialJoin = root.join("materials", JoinType.LEFT);
        Join materialHeaderJoin = materialJoin.join("materialHeader", JoinType.LEFT);    
        
        List<Selection> columnSelections = new ArrayList<Selection>();        
        List<Expression> groupByExpressions = new ArrayList<Expression>();
        List<Predicate> predicatesRules = new ArrayList<Predicate>();
                
        OrderRepositoryBeanHelper.buildSelections(columnSelections, groupByExpressions, reportFilterOrderDTO, criteriaBuilder, root, materialHeaderJoin,
                                                  materialJoin);

        OrderRepositoryBeanHelper.buildReportPredicates(predicatesRules, reportFilterOrderDTO, criteriaBuilder, fromDate, toDate, root, materialHeaderJoin,
                                                        materialJoin);

        criteriaQuery.multiselect(columnSelections.toArray(new Selection[columnSelections.size()]));

        criteriaQuery.where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));

        criteriaQuery.groupBy(groupByExpressions.toArray(new Expression[groupByExpressions.size()]));

        List<Tuple> orderDataTuples = entityManager.createQuery(criteriaQuery).getResultList();

        List<ReportRow> reportRows = new ArrayList<ReportRow>();

        if (orderDataTuples != null && !orderDataTuples.isEmpty()) {
            for (Tuple orderDataTuple : orderDataTuples) {
                ReportRow reportRow = new ReportRow();
                String companyCode = null;
                for (Selection selection : columnSelections) {
                    if (selection.getAlias().equals(REPORT_COLUMN_ID_COMPANY_CODE)) {
                        companyCode = (orderDataTuple.get(selection)).toString();
                    }
                    if (selection.getAlias().equals(REPORT_COLUMN_ID_ORDERLINE_VERSION)) {
                        String defaultCurrencyCode = companyCodeToDefaultCurrencyMap.get(companyCode);
                        if (reportFilterOrderDTO.isUnitOfPrice()) {
                            ReportHelper.prepareHandleTuplesForCurrency(REPORT_COLUMN_ID_UNIT_OF_PRICE, orderDataTuple, selection, reportRow,
                                                                        defaultCurrencyCode, currencyToCurrencyRateMap);
                        }
                        if (reportFilterOrderDTO.isCurrency()) {
                            ReportHelper.prepareHandleTuplesForCurrency(REPORT_COLUMN_ID_CURRENCY_ORIGINAL, orderDataTuple, selection, reportRow,
                                                                        defaultCurrencyCode, currencyToCurrencyRateMap);
                        }
                        if (reportFilterOrderDTO.isUnitPrice()) {
                            ReportHelper.prepareHandleTuplesForCurrency(REPORT_COLUMN_ID_UNIT_PRICE_ORIGINAL, orderDataTuple, selection, reportRow,
                                                                        defaultCurrencyCode, currencyToCurrencyRateMap);
                        }
                        if (reportFilterOrderDTO.isPrice()) {
                            ReportHelper.prepareHandleTuplesForCurrency(REPORT_COLUMN_ID_TOTAL_PRICE_ORIGINAL, orderDataTuple, selection, reportRow,
                                                                        defaultCurrencyCode, currencyToCurrencyRateMap);
                        }
                        if (reportFilterOrderDTO.isCurrency()) {
                            ReportHelper.prepareHandleTuplesForCurrency(REPORT_COLUMN_ID_CURRENCY, orderDataTuple, selection, reportRow,
                                                                        defaultCurrencyCode, currencyToCurrencyRateMap);
                        }
                        if (reportFilterOrderDTO.isUnitPrice()) {
                            ReportHelper.prepareHandleTuplesForCurrency(REPORT_COLUMN_ID_UNIT_PRICE, orderDataTuple, selection, reportRow,
                                                                        defaultCurrencyCode, currencyToCurrencyRateMap);
                        }
                        if (reportFilterOrderDTO.isPrice()) {
                            ReportHelper.prepareHandleTuplesForCurrency(REPORT_COLUMN_ID_TOTAL_PRICE, orderDataTuple, selection, reportRow,
                                                                        defaultCurrencyCode, currencyToCurrencyRateMap);
                        }
                        
                    } else {
                        prepareHandleTuples(null, orderDataTuple, selection, reportRow, reportFilterOrderDTO);
                    }
                }
                reportRows.add(reportRow);
            }
        }
        return reportRows;
    }
    
    @SuppressWarnings("rawtypes")
    private void prepareHandleTuples(String column, Tuple orderDataTuple, Selection selection, ReportRow reportRow, ReportFilterOrderDTO reportFilterOrderDTO) {
        ReportColumn reportColumn = new ReportColumn();
        reportColumn.setName(selection.getAlias().replace("_", " "));
        reportColumn.setValue(OrderRepositoryBeanHelper.handleTuples(orderDataTuple, selection, reportFilterOrderDTO));
        reportRow.getReportColumns().add(reportColumn);
    }
   
    @Override
    public OrderLineLastModified updateOrderLineLastModified(OrderLineLastModified orderLineLastModified) {
        return getEntityManager().merge(orderLineLastModified);
    }
    
    @Override
    public List<Tuple> fetchDeliveryPrecisionIdentifiersForOrders(ReportPartDeliveryPrecisionDTO reportPartDeliveryPrecisionDTO, Date fromDate, Date toDate,
            boolean isSummary) {
        return OrderRepositoryBeanHelper.fetchDeliveryPrecisionIdentifiersForOrders(reportPartDeliveryPrecisionDTO, fromDate, toDate, isSummary,
                                                                                    getEntityManager());
    }

    @Override
    public List<Tuple> pPReqAndOrderPlacedDateDifference(ReportPerformanceDTO reportPerformanceDTOParam, Date fromDate, Date toDate) {
        return OrderRepositoryBeanHelper.pPReqAndOrderPlacedDateDifference(reportPerformanceDTOParam, fromDate, toDate,  getEntityManager());
    }
    @Override
    public List<Tuple> orderSTAAndDeliverySTAReport(ReportPerformanceDTO reportPerformanceDTOParam, Date fromDate, Date toDate) {
        return OrderRepositoryBeanHelper.orderSTAAndDeliverySTAReport(reportPerformanceDTOParam, fromDate, toDate,  getEntityManager());
    }
    @Override
    public List<Tuple> materialPerformanceReportData(ReportPerformanceDTO reportPerformanceDTOParam, Date fromDate, Date toDate) {
        return OrderRepositoryBeanHelper.materialPerformanceReportData(reportPerformanceDTOParam, fromDate, toDate,  getEntityManager());
    }

    @Override
    public List<Tuple> getDistinctPartsFromOrderLine() {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root<OrderLine> root = criteriaQuery.from(OrderLine.class); 
        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.and(root.get("partNumber").isNotNull()));
        criteriaQuery.where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));
        TypedQuery<Tuple> query = entityManager.createQuery(criteriaQuery.select(criteriaBuilder.tuple(root.get("partNumber"))).distinct(true));
        return query.getResultList();
    }

    @Override
    public List<Tuple> getGeneralReportData(ReportGeneralDTO reportGeneralDTO) {
        return OrderRepositoryBeanHelper.getGeneralReportData(reportGeneralDTO, this.getEntityManager());
    }   
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<Order> findOrdersForWarehouse(List<String> whSiteIds, String orderNo) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(Order.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.and(root.get("shipToId").in(whSiteIds)));
        if (!StringUtils.isEmpty(orderNo)) {
            predicatesRules.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("orderNo")), orderNo.toLowerCase()));
        }

        predicatesRules.add(criteriaBuilder.notEqual(root.get("orderLines").get("status"), OrderLineStatus.COMPLETED));
        Query query = entityManager.createQuery(criteriaQuery.select(root).where(predicatesRules.toArray(new Predicate[predicatesRules.size()])));
        return query.getResultList();
    }

    @Override
    public List<Tuple> getGeneralReportDataWithNullOrderLines(ReportGeneralDTO reportGeneralDTO) {
        return OrderRepositoryBeanHelper.getGeneralReportDataWithNullOrderLines(reportGeneralDTO, this.getEntityManager());
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<DeliverySchedule> findDeliveryScheduleByOrderLineList(List<Long> orderLineList) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(DeliverySchedule.class);
        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root).distinct(true)
                                                               .where(root.get("orderLine").get("orderLineOID").in(orderLineList));

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        return resultSetQuery.getResultList();
    }
    
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<OrderLineLog> findOrderLineLogByOrderLineList(List<Long> orderLineList) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(OrderLineLog.class);
        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root).distinct(true)
                                                               .where(root.get("orderLine").get("orderLineOID").in(orderLineList));

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        return resultSetQuery.getResultList();
    }
    
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<Long> findOrderLineIdsByStatus(List<OrderLineStatus> orderLineStatus) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(OrderLine.class);
        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root.get("orderLineOID")).distinct(true).where(root.get("status").in(orderLineStatus));

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        return resultSetQuery.getResultList();
    }
    
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<OrderLine> getOrderLinesForRelatedMigratedOrder(String orderno, String partno, String partAffiliation, String partVersion, String companycode) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(OrderLine.class);
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("order").get("orderNo"), orderno),
                                           criteriaBuilder.equal(root.get("partNumber"), partno),
                                           criteriaBuilder.equal(root.get("partAffiliation"), partAffiliation),
                                           criteriaBuilder.equal(root.get("current").get("partVersion"), partVersion),
                                           criteriaBuilder.equal(root.get("order").get("companyCode"), companycode)));
        Query query = entityManager.createQuery(criteriaQuery.select(root).where(predicates.toArray(new Predicate[predicates.size()])));
        return query.getResultList();
    }
}
