package com.volvo.gloria.procurematerial.repositories.b.beans;

import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_ADDITION_QTY_FOR_PROJECT;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_AGREED_STA;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_AGREED_STA_LAST_MODIFIED_DATE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_BLOCKED_QTY;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_BUILD_SERIES;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_BUYER_ID;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_BUYER_NAME;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_COMPANY_CODE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_CONTACT_PERSON_ID;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_CONTACT_PERSON_NAME;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_COST_CENTER;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_DELIVERY_CONTROLLER_ID;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_DELIVERY_CONTROLLER_NAME;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_EXPECTED_ARRIVAL;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_EXPECTED_QTY;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_GL_ACCOUNT;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_INTERNAL_PROCURER_ID;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_LAST_MODIFIED_DATE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_MAILFORM_ID;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_MATERIAL_CONTROLLER_ID;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_MATERIAL_CONTROLLER_NAME;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_MTR_ID;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_ORDERLINE_VERSION;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_ORDER_DATE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_ORDER_LINE_LOG;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_ORDER_LOG;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_ORDER_NO;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_ORDER_QTY;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_ORDER_STA;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_ORDER_STATUS;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_ORDER_TYPE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_PART_AFFILIATION;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_PART_MODIFICATION;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_PART_NAME;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_PART_NUMBER;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_PART_VERSION;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_PLANNED_DISPATCH_DATE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_POSSIBLE_TO_RECEIVE_QTY;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_PROCURE_INFO1;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_PROCURE_INFO2;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_PROCURE_TEAM;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_PROJECT;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_PURCHASE_ORGANIZATION;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_QTY_FOR_EACH_TO;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_RECEIVED_DATE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_RECEIVED_QTY;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_REFERENCE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_REQUESTER_ID;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_REQUISITION_DATE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_SUFFIX;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_SUPPLIER_NAME;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_SUPPLIER_PARMA_ID;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_TEST_OBJECT;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_UNIT_OF_MEASURE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_WBS;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_DELIVERY_NOTE_DATE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
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
import javax.persistence.criteria.Subquery;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.TransformerUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.time.DateUtils;

import com.volvo.gloria.common.c.dto.reports.ReportFilterOrderDTO;
import com.volvo.gloria.common.c.dto.reports.ReportGeneralDTO;
import com.volvo.gloria.common.c.dto.reports.ReportPartDeliveryPrecisionDTO;
import com.volvo.gloria.common.c.dto.reports.ReportPerformanceDTO;
import com.volvo.gloria.procurematerial.c.CompleteType;
import com.volvo.gloria.procurematerial.c.DeliveryNoteLineStatus;
import com.volvo.gloria.procurematerial.d.entities.EventLog;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.Order;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.orderLine.OrderLineStatus;
import com.volvo.gloria.procurematerial.d.type.internalexternal.InternalExternal;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers;

public final class OrderRepositoryBeanHelper {
    
    private static final String SIPD_659261 = "659261";
    private static final int VALUE_100 = 100;

    private OrderRepositoryBeanHelper() {
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List<Tuple> fetchDeliveryPrecisionIdentifiersForOrders(ReportPartDeliveryPrecisionDTO reportPartDeliveryPrecisionDTO, Date fromDate,
            Date toDate, boolean isSummary, EntityManager entityManager) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root<OrderLine> root = criteriaQuery.from(OrderLine.class);
        
        List<Selection> activeSelections = new ArrayList<Selection>();
        List<Expression> groupByExpressions = new ArrayList<Expression>();

        Expression<Double> selectCaseExpOrderStaOnTime = criteriaBuilder.<Double> selectCase()
                                                                        .when(criteriaBuilder.isTrue((Path) root.get("current").get("orderStaDateOnTime")), 1D)
                                                                        .otherwise(0D);
        Expression<Double> selectCaseExpAgreedStaOnTime = criteriaBuilder.<Double> selectCase()
                                                                         .when(criteriaBuilder.isTrue((Path) root.get("current").get("staAgreedDateOnTime")),
                                                                               1D).otherwise(0D);

        addSelections(true, root.get("order").get("companyCode"), ReportColumnIdentifiers.REPORT_COLUMN_ID_COMPANY_CODE, activeSelections, true,
                      groupByExpressions);
        addSelections(true, criteriaBuilder.countDistinct(root), ReportColumnIdentifiers.REPORT_COLUMN_ID_PRECISION_NO_OF_ORDERLINES, activeSelections, false,
                      groupByExpressions);
        addSelections(reportPartDeliveryPrecisionDTO.isDeliveriesVSOrderSTA(),
                      criteriaBuilder.quot(criteriaBuilder.prod(criteriaBuilder.sum(selectCaseExpOrderStaOnTime), VALUE_100),
                                           criteriaBuilder.countDistinct(root)), ReportColumnIdentifiers.REPORT_COLUMN_ID_PRECISION_ONTIME_ORDERSTA,
                      activeSelections, false, groupByExpressions);
        addSelections(reportPartDeliveryPrecisionDTO.isDeliveriesVSAgreedSTA(),
                      criteriaBuilder.quot(criteriaBuilder.prod(criteriaBuilder.sum(selectCaseExpAgreedStaOnTime), VALUE_100),
                                           criteriaBuilder.countDistinct(root)), ReportColumnIdentifiers.REPORT_COLUMN_ID_PRECISION_ONTIME_AGREEDSTA,
                      activeSelections, false, groupByExpressions);

        /**  - NO OF DELIVERIES ON TIME 
        addSelections(reportPartDeliveryPrecisionDTO.isDeliveriesVSOrderSTA(), criteriaBuilder.sum(selectCaseExpOrderStaOnTime), "orderSTAOnTimexx",
                      activeSelections, false, groupByExpressions);
        addSelections(reportPartDeliveryPrecisionDTO.isDeliveriesVSAgreedSTA(), criteriaBuilder.sum(selectCaseExpAgreedStaOnTime), "agreedSTAOnTimexx",
                      activeSelections, false, groupByExpressions);
        */

        if (!isSummary) {
            addSelections(true, root.get("order").get("supplierId"), ReportColumnIdentifiers.REPORT_COLUMN_ID_SUPPLIER_PARMA_ID, activeSelections, true,
                          groupByExpressions);
            addSelections(true, criteriaBuilder.upper((Expression) root.get("order").get("supplierName")),
                          ReportColumnIdentifiers.REPORT_COLUMN_ID_SUPPLIER_NAME, activeSelections, true, groupByExpressions);
        }
        
        
        List<Predicate> predicatesRules = getPredicateRules(reportPartDeliveryPrecisionDTO, fromDate, toDate, criteriaBuilder, root);
        
        Subquery<OrderLine> subquery = criteriaQuery.subquery(OrderLine.class);
        Root subRoot = subquery.from(OrderLine.class);
        List<Predicate> subQueryPredicatesRules = new ArrayList<Predicate>();

        if (fromDate != null) {
            subQueryPredicatesRules.add(criteriaBuilder.greaterThanOrEqualTo(subRoot.get("first").get("deliveryNote").get("deliveryNoteDate"),
                                                                     DateUtil.getDateWithZeroTime(fromDate)));
        }
        if (toDate != null) {
            subQueryPredicatesRules.add(criteriaBuilder.lessThan(subRoot.get("first").get("deliveryNote").get("deliveryNoteDate"),
                                                         DateUtil.getDateWithZeroTime(DateUtils.addDays(toDate, 1))));
        }
        subQueryPredicatesRules.add(subRoot.get("first").get("status").in(DeliveryNoteLineStatus.RECEIVED));
        subquery.select(subRoot).distinct(true).where(subQueryPredicatesRules.toArray(new Predicate[subQueryPredicatesRules.size()]));
        
        predicatesRules.add(criteriaBuilder.in(root).value(subquery));
        criteriaQuery.multiselect(activeSelections.toArray(new Selection[activeSelections.size()]))
                     .where(predicatesRules.toArray(new Predicate[predicatesRules.size()]));
        
        if (groupByExpressions != null && !groupByExpressions.isEmpty()) {
            criteriaQuery.groupBy(groupByExpressions.toArray(new Expression[groupByExpressions.size()]));
        }

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    private static List<Predicate> getPredicateRules(ReportPartDeliveryPrecisionDTO reportPartDeliveryPrecisionDTO, Date fromDate, Date toDate,
            CriteriaBuilder criteriaBuilder, Root<OrderLine> root) {
        List<Predicate> predicatesRules = new ArrayList<Predicate>();
       
        if (reportPartDeliveryPrecisionDTO.getCompanyCode().length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("order").get("companyCode")).value(Arrays.asList(reportPartDeliveryPrecisionDTO.getCompanyCode())));
        }
        if (reportPartDeliveryPrecisionDTO.getBuyerId().length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("current").get("buyerId"))
                                               .value(Arrays.asList(reportPartDeliveryPrecisionDTO.getBuyerId())));
        }
        if (reportPartDeliveryPrecisionDTO.getDeliveryControllerId().length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("deliveryControllerUserId"))
                                               .value(Arrays.asList(reportPartDeliveryPrecisionDTO.getDeliveryControllerId())));
        }
        if (reportPartDeliveryPrecisionDTO.getProject().length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("projectId")).value(Arrays.asList(reportPartDeliveryPrecisionDTO.getProject())));
        }
        // ordertype

        if (reportPartDeliveryPrecisionDTO.getSource().length > 0) {
            String[] source = reportPartDeliveryPrecisionDTO.getSource();
            List<InternalExternal> internalExternal = new ArrayList<InternalExternal>();
            for (int i = 0; i <= source.length - 1; i++) {
                if (InternalExternal.EXTERNAL.name().equals(source[i])) {
                    internalExternal.add(InternalExternal.EXTERNAL);
                } else if (InternalExternal.INTERNAL.name().equals(source[i])) {
                    internalExternal.add(InternalExternal.INTERNAL);
                }
            }
            predicatesRules.add(criteriaBuilder.in(root.get("order").get("internalExternal")).value(internalExternal));
        }
        if (reportPartDeliveryPrecisionDTO.getSuffix().length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("order").get("suffix")).value(Arrays.asList(reportPartDeliveryPrecisionDTO.getSuffix())));
        }
        if (reportPartDeliveryPrecisionDTO.getSupplierParmaId().length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("order").get("supplierId"))
                                               .value(Arrays.asList(reportPartDeliveryPrecisionDTO.getSupplierParmaId())));
        }

        // exclude SIPD orders
        predicatesRules.add(criteriaBuilder.notEqual(root.get("procureLine").get("financeHeader").get("glAccount"), SIPD_659261));
        
        return predicatesRules;
    }

    @SuppressWarnings("rawtypes")
    public static List<Tuple> pPReqAndOrderPlacedDateDifference(ReportPerformanceDTO reportPerformanceDTOParam, Date fromDate, Date toDate,
            EntityManager entityManager) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root<OrderLine> root = criteriaQuery.from(OrderLine.class);  
        List<Selection> columnSelections = setColumnSelectionsForpPReqReport(root);   
        columnSelections.add(root.get("order").get("orderDateTime"));
        columnSelections.add(root.get("procureLine").get("procureDate"));
        List<Predicate> predicatesRules = getPredicateRulesPPreq(reportPerformanceDTOParam, fromDate, toDate,  criteriaBuilder, root);
        criteriaQuery.where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));
        criteriaQuery.multiselect(columnSelections.toArray(new Selection[columnSelections.size()])); 
        TypedQuery<Tuple> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }
    
    @SuppressWarnings("rawtypes")
    public static List<Tuple> orderSTAAndDeliverySTAReport(ReportPerformanceDTO reportPerformanceDTOParam, Date fromDate, Date toDate,
            EntityManager entityManager) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root<OrderLine> root = criteriaQuery.from(OrderLine.class);

        List<Selection> columnSelections = setColumnSelectionsForpPReqReport(root);
        columnSelections.add(root.get("deliveryNoteLines").get("deliveryNote").get("deliveryNoteDate"));
        columnSelections.add(root.get("orderLineVersions").get("orderStaDate"));
        columnSelections.add(root.get("orderLineVersions").get("staAgreedDate"));
        
        List<Predicate> predicatesRules = getPredicateRules(reportPerformanceDTOParam, fromDate, toDate, criteriaBuilder, root);
        criteriaQuery.where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));
        criteriaQuery.multiselect(columnSelections.toArray(new Selection[columnSelections.size()])).distinct(true);
        TypedQuery<Tuple> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }

    @SuppressWarnings("rawtypes")
    private static List<Selection> setColumnSelectionsForpPReqReport(Path path) {
        List<Selection> columnSelections = new ArrayList<Selection>();
        columnSelections.add(path.get("order").get("shipToId"));
        columnSelections.add(path.get("order").get("suffix"));
        columnSelections.add(path.get("projectId"));
        columnSelections.add(path.get("procureLine").get("referenceGroups"));
        columnSelections.add(path.get("procureLine").get("referenceIds"));
        
        columnSelections.add(path.get("current").get("buyerId"));
        columnSelections.add(path.get("partNumber"));
        columnSelections.add(path.get("order").get("orderNo"));
        return columnSelections;
    }

    private static List<Predicate> getPredicateRules(ReportPerformanceDTO reportPerformanceDTOParam, Date fromDate, Date toDate,
            CriteriaBuilder criteriaBuilder, Path<OrderLine> root) {
        List<Predicate> predicatesRules = new ArrayList<Predicate>();

        Path<Date> pathOrderDateTime = root.get("order").get("orderDateTime");
        if (fromDate != null) {
            predicatesRules.add(criteriaBuilder.greaterThanOrEqualTo(pathOrderDateTime, fromDate));
        }
        if (toDate != null) {
            predicatesRules.add(criteriaBuilder.lessThan(pathOrderDateTime, toDate));
        }
       
        if (reportPerformanceDTOParam.getBuyerId().length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("buyerPartyId")).value(Arrays.asList(reportPerformanceDTOParam.getBuyerId())));
        }
        if (reportPerformanceDTOParam.getProject().length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("projectId")).value(Arrays.asList(reportPerformanceDTOParam.getProject())));
        } 
        if (reportPerformanceDTOParam.getSuffix().length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("order").get("suffix")).value(Arrays.asList(reportPerformanceDTOParam.getSuffix())));
        }
        if (reportPerformanceDTOParam.getWarehouse().length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("order").get("shipToId")).value(Arrays.asList(reportPerformanceDTOParam.getWarehouse())));
        }
        if (reportPerformanceDTOParam.getBuildSeries().length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("procureLine").get("referenceGroups")).value(
                  Arrays.asList(reportPerformanceDTOParam.getBuildSeries())));
        }
        if (reportPerformanceDTOParam.getPartNumber().length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("partNumber")).value(
                  Arrays.asList(reportPerformanceDTOParam.getPartNumber())));
        }
        predicatesRules.add(criteriaBuilder.equal(root.get("orderLineVersions").get("versionNo"), 1));
        return predicatesRules;
    }

    private static List<Predicate> getPredicateRulesPPreq(ReportPerformanceDTO reportPerformanceDTOParam, Date fromDate, Date toDate,
            CriteriaBuilder criteriaBuilder, Path<OrderLine> root) {
        List<Predicate> predicatesRules = new ArrayList<Predicate>();

        Path<Date> pathOrderDateTime = root.get("order").get("orderDateTime");
        if (fromDate != null) {
            predicatesRules.add(criteriaBuilder.greaterThanOrEqualTo(pathOrderDateTime, fromDate));
        }
        if (toDate != null) {
            predicatesRules.add(criteriaBuilder.lessThan(pathOrderDateTime, toDate));
        }

        if (reportPerformanceDTOParam.getBuyerId().length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("procureLine").get("buyerCode")).value(Arrays.asList(reportPerformanceDTOParam.getBuyerId())));
        }
        if (reportPerformanceDTOParam.getProject().length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("projectId")).value(Arrays.asList(reportPerformanceDTOParam.getProject())));
        }
        if (reportPerformanceDTOParam.getSuffix().length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("order").get("suffix")).value(Arrays.asList(reportPerformanceDTOParam.getSuffix())));
        }
        if (reportPerformanceDTOParam.getWarehouse().length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("order").get("shipToId")).value(Arrays.asList(reportPerformanceDTOParam.getWarehouse())));
        }
        if (reportPerformanceDTOParam.getBuildSeries().length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("procureLine").get("referenceGroups"))
                                               .value(Arrays.asList(reportPerformanceDTOParam.getBuildSeries())));
        }
        if (reportPerformanceDTOParam.getPartNumber().length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("partNumber")).value(Arrays.asList(reportPerformanceDTOParam.getPartNumber())));
        }
        predicatesRules.add(criteriaBuilder.equal(root.get("order").get("internalExternal"), InternalExternal.EXTERNAL));
        return predicatesRules;
    }

    @SuppressWarnings("rawtypes")
    public static List<Tuple> materialPerformanceReportData(ReportPerformanceDTO reportPerformanceDTOParam, Date fromDate, Date toDate,
            EntityManager entityManager) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root<MaterialLine> root = criteriaQuery.from(MaterialLine.class);

        List<Selection> columnSelections = setColumnSelectionsForpPReqReport(root.get("material").get("orderLine"));
        columnSelections.add(root.get("materialLineStatusTime").get("receivedTime"));
        columnSelections.add(root.get("materialLineStatusTime").get("storedTime"));
        columnSelections.add(root.get("materialLineStatusTime").get("requestTime"));        
        columnSelections.add(root.get("materialLineStatusTime").get("shippedTime"));        
        List<Predicate> predicatesRules = getPredicateRulesForPerformanceReport(reportPerformanceDTOParam, fromDate, toDate, criteriaBuilder,  
                                                            root);
        criteriaQuery.where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));
        criteriaQuery.multiselect(columnSelections.toArray(new Selection[columnSelections.size()]));
        TypedQuery<Tuple> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }

    private static List<Predicate> getPredicateRulesForPerformanceReport(ReportPerformanceDTO reportPerformanceDTOParam, Date fromDate, Date toDate,
            CriteriaBuilder criteriaBuilder, Path<MaterialLine> root) {
        List<Predicate> predicatesRules = new ArrayList<Predicate>();

        Path<Date> pathOrderDateTime = root.get("material").get("orderLine").get("order").get("orderDateTime");
        if (fromDate != null) {
            predicatesRules.add(criteriaBuilder.greaterThanOrEqualTo(pathOrderDateTime, fromDate));
        }
        if (toDate != null) {
            predicatesRules.add(criteriaBuilder.lessThan(pathOrderDateTime, toDate));
        }

        if (reportPerformanceDTOParam.getBuyerId().length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("material").get("orderLine").get("buyerPartyId"))
                                .value(Arrays.asList(reportPerformanceDTOParam.getBuyerId())));
        }
        if (reportPerformanceDTOParam.getProject().length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("material").get("orderLine").get("projectId"))
                                .value(Arrays.asList(reportPerformanceDTOParam.getProject())));
        }
        if (reportPerformanceDTOParam.getSuffix().length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("material").get("orderLine").get("order").get("suffix"))
                                .value(Arrays.asList(reportPerformanceDTOParam.getSuffix())));
        }
        if (reportPerformanceDTOParam.getWarehouse().length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("material").get("orderLine").get("order").get("shipToId"))
                                .value(Arrays.asList(reportPerformanceDTOParam.getWarehouse())));
        }
        if (reportPerformanceDTOParam.getBuildSeries().length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("material").get("orderLine").get("procureLine").get("referenceGroups"))
                                               .value(Arrays.asList(reportPerformanceDTOParam.getBuildSeries())));
        }
        if (reportPerformanceDTOParam.getPartNumber().length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("material").get("orderLine").get("partNumber"))
                                .value(Arrays.asList(reportPerformanceDTOParam.getPartNumber())));
        }
        
        predicatesRules.add(criteriaBuilder.isNotNull(root.get("materialLineStatusTime").get("storedTime")));
        return predicatesRules;
    }

    @SuppressWarnings("rawtypes")
    public static List<Tuple> getGeneralReportData(ReportGeneralDTO generalReportData, EntityManager entityManager) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root<MaterialLine> root = criteriaQuery.from(MaterialLine.class);
        List<Selection> columnSelections = getColumnSelectionsForGeneralReport(root);
        columnSelections.add(root.get("quantity"));
        columnSelections.add(root.get("material").get("orderLine").get("current").get("unitPriceInEuro"));
        columnSelections.add(root.get("material").get("orderLine").get("current").get("perQuantity"));
        
        List<Predicate> predicatesRules = getPredicateRulesForGeneralReport(generalReportData, criteriaBuilder, root);
        criteriaQuery.where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));
        criteriaQuery.multiselect(columnSelections.toArray(new Selection[columnSelections.size()]));
        TypedQuery<Tuple> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }

    private static List<Predicate> getPredicateRulesForGeneralReport(ReportGeneralDTO generalReportData, CriteriaBuilder criteriaBuilder,
            Root<MaterialLine> root) {
        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        if (generalReportData.getProject().length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("material").get("financeHeader").get("projectId"))
                                .value(Arrays.asList(generalReportData.getProject())));
        }
        if (generalReportData.getWarehouse().length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("whSiteId"))
                                .value(Arrays.asList(generalReportData.getWarehouse())));
        }
        predicatesRules.add(criteriaBuilder.equal(root.get("status"), MaterialLineStatus.STORED));

        return predicatesRules;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List<Predicate> buildReportPredicates(List<Predicate> predicatesRules, ReportFilterOrderDTO reportFilterOrderDTO,
            CriteriaBuilder criteriaBuilder, Date fromDate, Date toDate, Root root, Join materialHeaderJoin, Join materialJoin) {
        
        String[] selectedCompanyCodes = reportFilterOrderDTO.getCompanyCode();
        if (selectedCompanyCodes != null && selectedCompanyCodes.length > 0) {
            predicatesRules.add(criteriaBuilder.selectCase().when(root.get("completeType").in(CompleteType.CANCELLED), root.get("order").get("companyCode"))
                                               .otherwise(materialJoin.get("financeHeader").get("companyCode")).in(Arrays.asList(selectedCompanyCodes)));
        }

        String[] selectedSuffixes = reportFilterOrderDTO.getSuffix();
        if (selectedSuffixes != null && selectedSuffixes.length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("order").get("suffix")).value(Arrays.asList(selectedSuffixes)));
        }

        String[] selectedProjects = reportFilterOrderDTO.getProject();
        if (selectedProjects != null && selectedProjects.length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("projectId")).value(Arrays.asList(selectedProjects)));
        }

        String[] selectedBuildSerie = reportFilterOrderDTO.getBuildSeries();
        if (selectedBuildSerie != null && selectedBuildSerie.length > 0) {
            predicatesRules.add(criteriaBuilder.in(materialHeaderJoin.get("accepted").get("referenceGroup"))
                                               .value(Arrays.asList(selectedBuildSerie)));
        }

        String[] selectedTestObjects = reportFilterOrderDTO.getTestObject();
        if (selectedTestObjects != null && selectedTestObjects.length > 0) {
            predicatesRules.add(criteriaBuilder.in(materialHeaderJoin.get("referenceId"))
                                               .value(Arrays.asList(selectedTestObjects)));
        }

        String[] selectedSupplierParmaIds = reportFilterOrderDTO.getSupplierParmaId();
        if (selectedSupplierParmaIds != null && selectedSupplierParmaIds.length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("order").get("supplierId")).value(Arrays.asList(selectedSupplierParmaIds)));
        }
        
        String[] selectedReferences = reportFilterOrderDTO.getReference();
        if (selectedReferences != null && selectedReferences.length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("procureLine").get("referenceGps")).value(Arrays.asList(selectedReferences)));
        }

        String[] selectedMtrIds = reportFilterOrderDTO.getMtrId();
        if (selectedMtrIds != null && selectedMtrIds.length > 0) {
            predicatesRules.add(criteriaBuilder.in(materialHeaderJoin.get("accepted").get("changeId")
                                                       .get("mtrlRequestVersion")).value(Arrays.asList(selectedMtrIds)));
        }

        String[] selectedDeliveryControllerIds = reportFilterOrderDTO.getDeliveryControllerId();
        if (selectedDeliveryControllerIds != null && selectedDeliveryControllerIds.length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("deliveryControllerUserId")).value(Arrays.asList(selectedDeliveryControllerIds)));
        }
        
        String[] selectedOrderStatuses = reportFilterOrderDTO.getOrderStatus();
        if (selectedOrderStatuses != null && selectedOrderStatuses.length > 0) {
            List<Predicate> statusPredicates = new ArrayList<Predicate>();
            if (ArrayUtils.contains(selectedOrderStatuses, "PLACED")) {
                statusPredicates.add(root.get("status").in(OrderLineStatus.PLACED));
            }

            if (ArrayUtils.contains(selectedOrderStatuses, "RECEIVED_PARTLY")) {
                statusPredicates.add(root.get("status").in(OrderLineStatus.RECEIVED_PARTLY));
            }

            if (ArrayUtils.contains(selectedOrderStatuses, "COMPLETE")) {
                statusPredicates.add(criteriaBuilder.in(root.get("completeType")).value(CompleteType.COMPLETE));
            }

            if (ArrayUtils.contains(selectedOrderStatuses, "RECEIVED")) {
                statusPredicates.add(criteriaBuilder.in(root.get("completeType")).value(CompleteType.RECEIVED));
            }

            if (ArrayUtils.contains(selectedOrderStatuses, "CANCELLED")) {
                statusPredicates.add(criteriaBuilder.in(root.get("completeType")).value(CompleteType.CANCELLED));
            }
            predicatesRules.add(criteriaBuilder.or(statusPredicates.toArray(new Predicate[statusPredicates.size()])));
        }

        if (fromDate != null) {
            Date dateRangeFrom = DateUtil.getDateWithZeroTime(fromDate);
            predicatesRules.add(criteriaBuilder.greaterThanOrEqualTo(root.get("order").get("orderDateTime"), dateRangeFrom));
        }

        if (toDate != null) {
            Date dateRangeTo = DateUtil.getNextDate(toDate);
            predicatesRules.add(criteriaBuilder.lessThan(root.get("order").get("orderDateTime"), dateRangeTo));
        }
        return predicatesRules;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List<Selection> buildSelections(List<Selection> columnSelections, List<Expression> groupByExpressions,
            ReportFilterOrderDTO reportFilterOrderDTO, CriteriaBuilder criteriaBuilder, Root root, Join materialHeaderJoin, Join materialJoin) {
        addSelections(true,
                      criteriaBuilder.selectCase()
                                     .when(root.get("completeType").in(CompleteType.CANCELLED), root.get("order").get("companyCode"))
                                     .otherwise(materialJoin.get("financeHeader").get("companyCode")), REPORT_COLUMN_ID_COMPANY_CODE,
                      columnSelections, groupByExpressions, root.get("completeType"), root.get("order").get("companyCode"),
                      materialJoin.get("financeHeader").get("companyCode"));
        addSelections(true, root.get("order").get("suffix"), REPORT_COLUMN_ID_SUFFIX, columnSelections, true, groupByExpressions);
        addSelections(true, root.get("projectId"), REPORT_COLUMN_ID_PROJECT , columnSelections, true, groupByExpressions);
        addSelections(true, materialHeaderJoin.get("accepted").get("referenceGroup"), REPORT_COLUMN_ID_BUILD_SERIES, 
                      columnSelections, true, groupByExpressions);
        addSelections(true, materialHeaderJoin.get("referenceId"), REPORT_COLUMN_ID_TEST_OBJECT, columnSelections, true, groupByExpressions);
        addSelections(true, root.get("order").get("supplierId"), REPORT_COLUMN_ID_SUPPLIER_PARMA_ID, columnSelections, true, groupByExpressions);
        addSelections(true, root.get("order").get("supplierName"), REPORT_COLUMN_ID_SUPPLIER_NAME, columnSelections, true, groupByExpressions);
        addSelections(true, root.get("procureLine").get("referenceGps"), REPORT_COLUMN_ID_REFERENCE, columnSelections, true, groupByExpressions);
        addSelections(true, materialHeaderJoin.get("accepted").get("changeId").get("mtrlRequestVersion"), REPORT_COLUMN_ID_MTR_ID, columnSelections, true,
                      groupByExpressions);
        addSelections(true, root.get("deliveryControllerUserId"), REPORT_COLUMN_ID_DELIVERY_CONTROLLER_ID, columnSelections, true, groupByExpressions);
        addSelections(true, root.get("deliveryControllerUserName"), REPORT_COLUMN_ID_DELIVERY_CONTROLLER_NAME, columnSelections, true, groupByExpressions);
        addSelections(true,
                      criteriaBuilder.selectCase().when(root.get("status").in(OrderLineStatus.PLACED), "PLACED")
                                     .when(root.get("status").in(OrderLineStatus.RECEIVED_PARTLY), "RECEIVED PARTLY")
                                     .when(criteriaBuilder.in(root.get("completeType")).value(CompleteType.RECEIVED), "RECEIVED")
                                     .when(criteriaBuilder.in(root.get("completeType")).value(CompleteType.COMPLETE), "COMPLETE")
                                     .when(criteriaBuilder.in(root.get("completeType")).value(CompleteType.CANCELLED), "CANCELLED"),
                      REPORT_COLUMN_ID_ORDER_STATUS, columnSelections, groupByExpressions, root.get("completeType"),
                      root.get("status"));
        addSelections(true, root.get("procureLine").get("pPartNumber"), REPORT_COLUMN_ID_PART_NUMBER, columnSelections, true, groupByExpressions);
        addSelections(true, root.get("current").get("partVersion"), REPORT_COLUMN_ID_PART_VERSION, columnSelections, true, groupByExpressions);
        addSelections(true, root.get("procureLine").get("pPartName"), REPORT_COLUMN_ID_PART_NAME, columnSelections, true, groupByExpressions);
        addSelections(true, root.get("procureLine").get("pPartAffiliation"), REPORT_COLUMN_ID_PART_AFFILIATION, columnSelections, true, groupByExpressions);
        addSelections(true, root.get("procureLine").get("pPartModification"), REPORT_COLUMN_ID_PART_MODIFICATION, columnSelections, true, groupByExpressions);
        addSelections(reportFilterOrderDTO.isMailFormId(), materialJoin.get("mailFormId"), REPORT_COLUMN_ID_MAILFORM_ID, columnSelections, true,
                     groupByExpressions);
        addSelections(reportFilterOrderDTO.isRequisitionId(), root.get("procureLine").get("requisitionId"), REPORT_COLUMN_ID_REQUESTER_ID, columnSelections,
                      true, groupByExpressions);
        addSelections(reportFilterOrderDTO.isRequisitionDate(), root.get("procureLine").get("procureDate"), REPORT_COLUMN_ID_REQUISITION_DATE,
                      columnSelections, true, groupByExpressions);
        addSelections(reportFilterOrderDTO.isProcureTeam(), root.get("procureLine").get("materialControllerTeam"), REPORT_COLUMN_ID_PROCURE_TEAM,
                      columnSelections, true, groupByExpressions);
        addSelections(reportFilterOrderDTO.isOrderType(), root.get("order").get("internalExternal"), REPORT_COLUMN_ID_ORDER_TYPE, columnSelections, true,
                      groupByExpressions);
        addSelections(reportFilterOrderDTO.isOrderNo(), root.get("order").get("orderNo"), REPORT_COLUMN_ID_ORDER_NO, columnSelections, 
                      true, groupByExpressions);
        addSelections(reportFilterOrderDTO.isPurchasingOrg(), root.get("procureLine").get("purchaseOrgCode"), REPORT_COLUMN_ID_PURCHASE_ORGANIZATION,
                      columnSelections, true, groupByExpressions);
        addSelections(reportFilterOrderDTO.isBuyerId(), root.get("current").get("buyerId"), REPORT_COLUMN_ID_BUYER_ID, columnSelections, true,
                      groupByExpressions);
        
        addSelections(reportFilterOrderDTO.isBuyerName(), root.get("current").get("buyerName"), REPORT_COLUMN_ID_BUYER_NAME, columnSelections, true,
                      groupByExpressions);
        addSelections(reportFilterOrderDTO.isInternalProcurerId(),
                      criteriaBuilder.selectCase()
                                     .when(root.get("procureLine").get("forwardedUserId").isNotNull(), root.get("procureLine").get("forwardedUserId"))
                                     .otherwise(""), REPORT_COLUMN_ID_INTERNAL_PROCURER_ID, columnSelections, true, groupByExpressions);
        addSelections(reportFilterOrderDTO.isOrderDate(), root.get("order").get("orderDateTime"), REPORT_COLUMN_ID_ORDER_DATE, columnSelections, true,
                      groupByExpressions);
        addSelections(reportFilterOrderDTO.isOrderQty(), root.get("current").get("quantity"), REPORT_COLUMN_ID_ORDER_QTY, columnSelections, true,
                      groupByExpressions);
        addSelections(reportFilterOrderDTO.isPossibleToReceiveQty(), root.get("possibleToReceiveQuantity"), REPORT_COLUMN_ID_POSSIBLE_TO_RECEIVE_QTY,
                      columnSelections, true, groupByExpressions);
       addSelections(reportFilterOrderDTO.isUnitOfMeasure(), materialJoin.get("unitOfMeasure"), REPORT_COLUMN_ID_UNIT_OF_MEASURE, columnSelections,
                      true, groupByExpressions);
        addSelections(reportFilterOrderDTO.isOrderSta(), root.get("current").get("orderStaDate"), REPORT_COLUMN_ID_ORDER_STA, columnSelections, true,
                      groupByExpressions);
        addSelections(reportFilterOrderDTO.isAgreedSta(), root.get("current").get("staAgreedDate"), REPORT_COLUMN_ID_AGREED_STA, columnSelections, true,
                      groupByExpressions);
        addSelections(reportFilterOrderDTO.isStaAgreedLastUpdated(), root.get("current").get("staAgreedLastUpdated"), 
                      REPORT_COLUMN_ID_AGREED_STA_LAST_MODIFIED_DATE, columnSelections, true, groupByExpressions);
        addSelections(reportFilterOrderDTO.isReceivedQty(), root.get("receivedQuantity"), REPORT_COLUMN_ID_RECEIVED_QTY, columnSelections, true,
                      groupByExpressions);
        addSelections(reportFilterOrderDTO.isReceivedDateTime(), criteriaBuilder.max(root.join("deliveryNoteLines", JoinType.LEFT).get("receivedDateTime")),
                      REPORT_COLUMN_ID_RECEIVED_DATE, columnSelections, false, groupByExpressions);
        addSelections(reportFilterOrderDTO.isExpectedQty(), root.get("deliverySchedule").get("expectedQuantity"), REPORT_COLUMN_ID_EXPECTED_QTY,
                      columnSelections, true, groupByExpressions);
        addSelections(reportFilterOrderDTO.isBlockedQty(), criteriaBuilder.sum(criteriaBuilder.<Number> selectCase()
                                                                                              .when(criteriaBuilder.equal(materialJoin
                                                                                                                              .get("materialLine")
                                                                                                                              .get("status"),
                                                                                                                          MaterialLineStatus.BLOCKED),
                                                                                                    materialJoin.get("materialLine").get("quantity"))
                                                                                              .otherwise(0)), REPORT_COLUMN_ID_BLOCKED_QTY, columnSelections,
                      false, groupByExpressions);
        addSelections(reportFilterOrderDTO.isExpectedArrival(), root.get("deliverySchedule").get("expectedDate"), REPORT_COLUMN_ID_EXPECTED_ARRIVAL,
                      columnSelections, true, groupByExpressions);
        addSelections(reportFilterOrderDTO.isLastModifiedDate(), root.get("orderLineLastModified").get("modifiedTime"), REPORT_COLUMN_ID_LAST_MODIFIED_DATE,
                      columnSelections, true, groupByExpressions);
        addSelections(reportFilterOrderDTO.isPlannedDispatchDate(), root.get("deliverySchedule").get("plannedDispatchDate"),
                      REPORT_COLUMN_ID_PLANNED_DISPATCH_DATE, columnSelections, true, groupByExpressions);
        addSelections(reportFilterOrderDTO.isOrderLineLog(), root, REPORT_COLUMN_ID_ORDER_LINE_LOG, columnSelections,
                      groupByExpressions, root);
        addSelections(reportFilterOrderDTO.isOrderLog(), root.get("order"), REPORT_COLUMN_ID_ORDER_LOG, columnSelections, true, groupByExpressions);
        
        addSelections(reportFilterOrderDTO.isProcureInfo(), root.get("procureLine").get("requisition").get("purchaseInfo1"), REPORT_COLUMN_ID_PROCURE_INFO1,
                      columnSelections, true, groupByExpressions);
        addSelections(reportFilterOrderDTO.isProcureInfo(), root.get("procureLine").get("requisition").get("purchaseInfo2"), REPORT_COLUMN_ID_PROCURE_INFO2,
                      columnSelections, true, groupByExpressions);
        
      //Handle Order line version columns for price calculations inside tuples
        boolean isOrdLineVersionSelection = (reportFilterOrderDTO.isPrice() || reportFilterOrderDTO.isCurrency() 
                || reportFilterOrderDTO.isUnitPrice() || reportFilterOrderDTO.isUnitOfPrice());
        
        addSelections(isOrdLineVersionSelection, root.get("current"), REPORT_COLUMN_ID_ORDERLINE_VERSION, columnSelections, true, groupByExpressions);
        
        addSelections(reportFilterOrderDTO.isQtyTestObject(),
                      criteriaBuilder.sum(criteriaBuilder.<Number> selectCase()
                                                         .when(materialJoin.get("materialHeader").in(materialHeaderJoin),
                                                               materialJoin.get("materialLine").get("quantity")).otherwise(0)),
                      REPORT_COLUMN_ID_QTY_FOR_EACH_TO, columnSelections, false, groupByExpressions);
        addSelections(reportFilterOrderDTO.isAdditionalQty(),
                      criteriaBuilder.sum(criteriaBuilder.<Number> selectCase()
                                                         .when(materialJoin.get("materialType").in(MaterialType.ADDITIONAL, MaterialType.ADDITIONAL_USAGE),
                                                               materialJoin.get("materialLine").get("quantity")).otherwise(0)),
                      REPORT_COLUMN_ID_ADDITION_QTY_FOR_PROJECT, columnSelections, false, groupByExpressions);
        addSelections(reportFilterOrderDTO.isWbs(), materialJoin.get("financeHeader").get("wbsCode"), REPORT_COLUMN_ID_WBS, columnSelections, true,
                      groupByExpressions);
        addSelections(reportFilterOrderDTO.isCostCenter(), materialJoin.get("financeHeader").get("costCenter"), REPORT_COLUMN_ID_COST_CENTER,
                      columnSelections, true, groupByExpressions);
        addSelections(reportFilterOrderDTO.isGlAccount(), materialJoin.get("financeHeader").get("glAccount"), REPORT_COLUMN_ID_GL_ACCOUNT,
                      columnSelections, true, groupByExpressions);
        addSelections(reportFilterOrderDTO.isContactPersonId(), materialHeaderJoin.get("accepted").get("contactPersonId"), REPORT_COLUMN_ID_CONTACT_PERSON_ID,
                      columnSelections, true, groupByExpressions);
        addSelections(reportFilterOrderDTO.isContactPersonName(), materialHeaderJoin.get("accepted").get("contactPersonName"),
                      REPORT_COLUMN_ID_CONTACT_PERSON_NAME, columnSelections, true, groupByExpressions);
        addSelections(reportFilterOrderDTO.isMaterialControllerId(), root.get("procureLine").get("materialControllerId"),
                      REPORT_COLUMN_ID_MATERIAL_CONTROLLER_ID, columnSelections, true, groupByExpressions);
        addSelections(reportFilterOrderDTO.isMaterialControllerName(), root.get("procureLine").get("materialControllerName"),
                      REPORT_COLUMN_ID_MATERIAL_CONTROLLER_NAME, columnSelections, true, groupByExpressions);
        addSelections(reportFilterOrderDTO.isDeliveryNoteDate(), root.join("first", JoinType.LEFT).get("deliveryNote").get("deliveryNoteDate"),
                      REPORT_COLUMN_ID_DELIVERY_NOTE_DATE, columnSelections, true, groupByExpressions);
        return columnSelections;
    }

    @SuppressWarnings("rawtypes")
    private static void addSelections(boolean isToBeSelected, Selection activeSelection, String columnName, List<Selection> activeSelections,
            boolean isToBeGrouped, List<Expression> groupByExpressions) {
        if (isToBeGrouped) {
            addSelections(isToBeSelected, activeSelection, columnName, activeSelections, groupByExpressions, (Expression) activeSelection);
        } else {
            addSelections(isToBeSelected, activeSelection, columnName, activeSelections, groupByExpressions);
        }
    }
    
    @SuppressWarnings("rawtypes")
    private static void addSelections(boolean isToBeSelected, Selection activeSelection, String columnName, List<Selection> activeSelections,
            List<Expression> groupByExpressions, Expression... groupExpression) {
        if (isToBeSelected) {
            activeSelections.add(activeSelection.alias(columnName));
            if (groupExpression != null && groupExpression.length > 0) {
                groupByExpressions.addAll(Arrays.asList(groupExpression));
            }
        }
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Object handleTuples(Tuple orderDataTuple, Selection selection, ReportFilterOrderDTO reportFilterOrderDTO) {
        if (selection.getJavaType() != null) {
            if (Order.class.isAssignableFrom(selection.getJavaType())) {
                return handleLogs(((Order) orderDataTuple.get(selection)).getOrderLog());
            } else if (OrderLine.class.isAssignableFrom(selection.getJavaType())) {
                return handleLogs(((OrderLine) orderDataTuple.get(selection)).getOrderLineLog());
            }
        }
        return orderDataTuple.get(selection);
    }

    public static Collection<?> handleLogs(List<? extends EventLog> eventLogs) {
        if (eventLogs != null && !eventLogs.isEmpty()) {
            return CollectionUtils.collect(eventLogs, TransformerUtils.invokerTransformer("getEventLog"));
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    public static List<Tuple> getGeneralReportDataWithNullOrderLines(ReportGeneralDTO reportGeneralDTO, EntityManager entityManager) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root<MaterialLine> root = criteriaQuery.from(MaterialLine.class);
        List<Selection> columnSelections = getColumnSelectionsForGeneralReport(root);
        List<Predicate> predicatesRules = getPredicateRulesForGeneralReport(reportGeneralDTO, criteriaBuilder, root);
        List<Expression> groupByExpressions = new ArrayList<Expression>();

        groupByExpressions.add(root.get("material").get("partNumber"));
        groupByExpressions.add(root.get("material").get("partModification"));
        groupByExpressions.add(root.get("material").get("partVersion"));
        
        predicatesRules.add(criteriaBuilder.isNull(root.get("material").get("orderLine")));
        criteriaQuery.where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));
        criteriaQuery.multiselect(columnSelections.toArray(new Selection[columnSelections.size()]));
        criteriaQuery.groupBy(groupByExpressions.toArray(new Expression[groupByExpressions.size()]));
        TypedQuery<Tuple> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }

    @SuppressWarnings("rawtypes")
    private static List<Selection> getColumnSelectionsForGeneralReport(Root<MaterialLine> root) {
        List<Selection> columnSelections =  new ArrayList<Selection>(); 
        columnSelections.add(root.get("material").get("partNumber"));
        columnSelections.add(root.get("material").get("partModification"));
        columnSelections.add(root.get("material").get("partVersion"));
        return columnSelections;
    }
}
