package com.volvo.gloria.procurematerial.repositories.b.beans;

import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_AGREED_STA;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_AGREED_STA_LAST_MODIFIED_DATE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_BINLOCATION;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_BLOCKED_QTY;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_BUILDLOCATION_ID;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_BUILDLOCATION_NAME;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_BUILD_NAME;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_BUILD_SERIES;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_BUILD_START;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_BUYER_ID;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_BUYER_NAME;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_COMPANY_CODE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_CONTACT_PERSON_ID;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_CONTACT_PERSON_NAME;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_COST_CENTER;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_CURRENCY;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_DELIVERY_ADDRESS_ID;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_DELIVERY_ADDRESS_NAME;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_DELIVERY_CONTROLLER_ID;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_DELIVERY_CONTROLLER_NAME;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_DELIVERY_NOTE_NO;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_DESIGN_GROUP;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_DISPATCH_NOTE_NO;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_EXPECTED_DATE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_EXPECTED_QTY;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_FLAG_ORDER_LINE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_FLAG_PROCURE_LINE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_FUNCTION_GROUP;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_GL_ACCOUNT;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_INSPECTION_STATUS;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_INTERNAL_ORDER_SAP;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_INTERNAL_PROCURER_ID;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_MAILFORM_ID;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_MATERIAL_CONTROLLER_ID;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_MATERIAL_CONTROLLER_NAME;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_MATERIAL_CONTROLLER_TEAM;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_MATERIAL_TYPE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_MODULAR_HARNESS;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_MTR_ID;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_ORDERLINE_VERSION;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_ORDER_DATE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_ORDER_LINE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_ORDER_LINE_LOG;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_ORDER_LOG;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_ORDER_NO;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_ORDER_QTY;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_ORDER_STA;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_ORDER_STATUS;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_ORDER_STATUS_USER_NAME;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_PART_AFFILIATION;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_PART_MODIFICATION;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_PART_NAME;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_PART_NUMBER;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_PART_VERSION;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_PART_VERSION_ORIGINAL;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_PLANNED_DISPATCH_DATE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_POSSIBLE_TO_RECEIVE_QTY;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_PROBLEM_DESCRIPION;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_PROCURE_INFO1;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_PROCURE_INFO2;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_PROJECT;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_PURCHASE_ORGANIZATION;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_QTY;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_QUALITY_INSPECTION_COMMENT;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_RECEIVED_DATE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_RECEIVED_QTY;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_REFERENCE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_REQUESTER_ID;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_REQUESTER_NAME;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_REQUIRED_STA_DATE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_REQUISITION_DATE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_REQUISITION_ID;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_SOURCE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_STATUS;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_STA_ACCEPTED;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_STORAGE_ROOM;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_SUFFIX;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_SUPPLIER_NAME;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_SUPPLIER_PARMA_ID;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_TEST_OBJECT;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_UNIT_OF_MEASURE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_UNIT_OF_PRICE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_UNIT_PRICE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_WAREHOUSE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_WBS;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_ORDER_STATUS_USER_ID;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang.StringUtils;

import com.volvo.gloria.common.c.dto.reports.ReportFilterMaterialDTO;
import com.volvo.gloria.common.c.dto.reports.ReportWarehouseCostDTO;
import com.volvo.gloria.common.c.dto.reports.ReportWarehouseTransactionDTO;
import com.volvo.gloria.procurematerial.c.CompleteType;
import com.volvo.gloria.procurematerial.d.entities.DeliverySchedule;
import com.volvo.gloria.procurematerial.d.entities.DispatchNote;
import com.volvo.gloria.procurematerial.d.entities.FinanceHeader;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.Order;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.RequestList;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.orderLine.OrderLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.requestlist.RequestListStatus;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.procurematerial.d.type.receive.ReceiveType;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.GloriaExceptionLogger;
import com.volvo.gloria.util.GloriaFormateUtil;
import com.volvo.gloria.util.paging.c.JpaAttribute;
import com.volvo.gloria.util.paging.c.JpaAttributeType;

public final class RequestHeaderRepositoryBeanHelper {
    private RequestHeaderRepositoryBeanHelper() {

    }

    public static List<DispatchNote> findDispatchNote(Date fromDate, Date toDate, String[] projectIds, String[] whSiteId, EntityManager entityManager) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<DispatchNote> criteriaQuery = criteriaBuilder.createQuery(DispatchNote.class);
        Root<DispatchNote> root = criteriaQuery.from(DispatchNote.class);
        criteriaQuery.select(root);
        // add date
        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        Path<Timestamp> path = root.get("dispatchNoteDate");
        if (fromDate != null) {
            predicatesRules.add(criteriaBuilder.greaterThanOrEqualTo(path, new Timestamp(fromDate.getTime())));
        }
        if (toDate != null) {
            predicatesRules.add(criteriaBuilder.lessThan(path, new Timestamp(toDate.getTime())));
        }
        if (projectIds != null && projectIds.length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("requestList").get("requestGroups").get("projectId")).value(Arrays.asList(projectIds)));
        }
        if (whSiteId != null && whSiteId.length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("requestList").get("whSiteId")).value(Arrays.asList(whSiteId)));
        }

        if (!predicatesRules.isEmpty()) {
            criteriaQuery.where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));
        }
        TypedQuery<DispatchNote> query = entityManager.createQuery(criteriaQuery.select(root).distinct(true)
                                                                                .where(predicatesRules.toArray(new Predicate[predicatesRules.size()])));

        return query.getResultList();
    }

    public static Map<String, JpaAttribute> getMaterialLinesFields() {
        Map<String, JpaAttribute> materialLineFields = new HashMap<String, JpaAttribute>();
        materialLineFields.put("id", new JpaAttribute("materialOwner.materialOID", JpaAttributeType.NUMBERTYPE));
        materialLineFields.put("orderNo", new JpaAttribute("orderNo", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("projectId", new JpaAttribute("materialOwner.financeHeader.projectId", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("referenceGroup", new JpaAttribute("materialOwner.materialHeader.accepted.referenceGroup", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("referenceId", new JpaAttribute("materialOwner.materialHeader.referenceId", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("buildName", new JpaAttribute("materialOwner.materialHeader.buildName", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("changeRequestId", new JpaAttribute("materialOwner.procureLine.changeRequestIds", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("pPartNumber", new JpaAttribute("materialOwner.partNumber", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("pPartVersion", new JpaAttribute("materialOwner.partVersion", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("pPartName", new JpaAttribute("materialOwner.partName", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("outboundStartDate", new JpaAttribute("materialOwner.materialHeader.accepted.outboundStartDate", JpaAttributeType.DATETYPE));
        materialLineFields.put("outBoundLocationId", new JpaAttribute("materialOwner.materialHeader.accepted.outboundLocationId", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("status", new JpaAttribute("status", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("procureTeam", new JpaAttribute("materialOwner.procureLine.materialControllerTeam", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("procureUserId", new JpaAttribute("materialOwner.procureLine.materialControllerId", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("transportLabel", new JpaAttribute("deliveryNoteLine.deliveryNoteSubLines.transportLabel.code", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("whSiteId", new JpaAttribute("whSiteId", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("finalWhSiteId", new JpaAttribute("finalWhSiteId", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("mtrlRequestVersion", new JpaAttribute("materialOwner.mtrlRequestVersionAccepted", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("materialType", new JpaAttribute("materialOwner.materialType", JpaAttributeType.STRINGTYPE));

        materialLineFields.put("materialPartModification", new JpaAttribute("materialOwner.partModification", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("materialPartAffiliation", new JpaAttribute("materialOwner.partAffiliation", JpaAttributeType.STRINGTYPE));

        materialLineFields.put("orderSuffix", new JpaAttribute("materialOwner.orderLine.order.suffix", JpaAttributeType.STRINGTYPE));

        materialLineFields.put("financeHeaderWbsCode", new JpaAttribute("materialOwner.financeHeader.wbsCode", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("financeHeaderCostCenter", new JpaAttribute("materialOwner.financeHeader.costCenter", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("financeHeaderGlAccount", new JpaAttribute("materialOwner.financeHeader.glAccount", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("financeHeaderCompanyCode", new JpaAttribute("materialOwner.financeHeader.companyCode", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("financeHeaderInternalOrderNoSAP", new JpaAttribute("materialOwner.financeHeader.internalOrderNoSAP",
                                                                                   JpaAttributeType.STRINGTYPE));

        materialLineFields.put("materialMailFormId", new JpaAttribute("materialOwner.mailFormId", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("materialFunctionGroup", new JpaAttribute("materialOwner.functionGroup", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("materialDesignResponsible", new JpaAttribute("materialOwner.designResponsible", JpaAttributeType.STRINGTYPE));

        materialLineFields.put("materialModularHarness", new JpaAttribute("materialOwner.modularHarness", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("procureLineProcureDate", new JpaAttribute("materialOwner.procureLine.procureDate", JpaAttributeType.DATETYPE));

        materialLineFields.put("procureLineReferenceGps", new JpaAttribute("materialOwner.procureLine.referenceGps", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("procureLineRequiredStaDate", new JpaAttribute("materialOwner.procureLine.requiredStaDate", JpaAttributeType.DATETYPE));

        materialLineFields.put("procureLineMaterialControllerId", new JpaAttribute("materialOwner.procureLine.materialControllerId",
                                                                                   JpaAttributeType.STRINGTYPE));
        materialLineFields.put("procureLineRequisitionId", new JpaAttribute("materialOwner.procureLine.requisitionId", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("procureLineMaterialControllerName", new JpaAttribute("materialOwner.procureLine.materialControllerName",
                                                                                     JpaAttributeType.STRINGTYPE));

        materialLineFields.put("materialHeaderVersionRequesterUserId", new JpaAttribute("materialOwner.materialHeader.accepted.requesterUserId",
                                                                                        JpaAttributeType.STRINGTYPE));
        materialLineFields.put("materialHeaderVersionRequesterName", new JpaAttribute("materialOwner.materialHeader.accepted.requesterName",
                                                                                      JpaAttributeType.STRINGTYPE));
        materialLineFields.put("materialHeaderVersionContactPersonId", new JpaAttribute("materialOwner.materialHeader.accepted.contactPersonId",
                                                                                        JpaAttributeType.STRINGTYPE));
        materialLineFields.put("materialHeaderVersionContactPersonName", new JpaAttribute("materialOwner.materialHeader.accepted.contactPersonName",
                                                                                          JpaAttributeType.STRINGTYPE));
        materialLineFields.put("storageRoomName", new JpaAttribute("storageRoomName", JpaAttributeType.STRINGTYPE));

        materialLineFields.put("binLocationCode", new JpaAttribute("binLocationCode", JpaAttributeType.STRINGTYPE));

        materialLineFields.put("requestListDeliveryAddressId", new JpaAttribute("requestGroup.requestList.deliveryAddressId", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("requestListDeliveryAddressName", new JpaAttribute("requestGroup.requestList.deliveryAddressName", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("requestListID", new JpaAttribute("requestGroup.requestList.requestListOid", JpaAttributeType.NUMBERTYPE));

        materialLineFields.put("materialLineExpirationDate", new JpaAttribute("expirationDate", JpaAttributeType.DATETYPE));
        materialLineFields.put("deliveryNoteNo", new JpaAttribute("deliveryNoteLine.deliveryNote.deliveryNoteNo", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("deliveryNoteDate", new JpaAttribute("deliveryNoteLine.deliveryNote.deliveryNoteDate", JpaAttributeType.DATETYPE));
        materialLineFields.put("dispatchNoteNo", new JpaAttribute("requestGroup.requestList.dispatchNote.dispatchNoteNo", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("expectedDate", new JpaAttribute("materialOwner.orderLine.deliverySchedule.expectedDate", JpaAttributeType.DATETYPE));
        materialLineFields.put("unitOfMeasure", new JpaAttribute("materialOwner.unitOfMeasure", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("deliveryControllerId",
                               new JpaAttribute("material.orderLine.deliveryControllerUserId,material.orderLine.deliveryControllerUserName",
                                                JpaAttributeType.STRINGTYPE));

        return materialLineFields;
    }

    public static List<Predicate> handleDates(String expirationString, String expirationTo, String expirationFrom, boolean allExpired,
            CriteriaBuilder criteriaBuilder, Path<Date> path) throws ParseException {
        List<Predicate> predicateRules = new ArrayList<Predicate>();
        if (allExpired) {
            predicateRules.add(criteriaBuilder.lessThan(path, new Date(System.currentTimeMillis())));
        } else if (StringUtils.isNotBlank(expirationString)) {
            Date expirationDate = DateUtil.getStringAsDate(expirationString);
            Date fromDate = DateUtil.getDateWithZeroTime(expirationDate);
            Date toDate = DateUtil.getNextDate(expirationDate);
            predicateRules.add(criteriaBuilder.greaterThanOrEqualTo(path, fromDate));
            predicateRules.add(criteriaBuilder.lessThan(path, toDate));
        } else if (StringUtils.isNotBlank(expirationTo) && StringUtils.isNotBlank(expirationFrom)) {
            Date fromDate = DateUtil.getDateWithZeroTime(DateUtil.getStringAsDate(expirationFrom));
            Date toDate = DateUtil.getNextDate(DateUtil.getStringAsDate(expirationTo));
            if (fromDate.after(toDate)) {
                Date tmpDate = fromDate;
                fromDate = toDate;
                toDate = tmpDate;
            }
            predicateRules.add(criteriaBuilder.greaterThanOrEqualTo(path, fromDate));
            predicateRules.add(criteriaBuilder.lessThan(path, toDate));
        } else if (StringUtils.isNotBlank(expirationTo)) {
            Date toDate = DateUtil.getNextDate(DateUtil.getNextDate(DateUtil.getStringAsDate(expirationTo)));
            predicateRules.add(criteriaBuilder.lessThan(path, toDate));
        } else if (StringUtils.isNotBlank(expirationFrom)) {
            Date fromDate = DateUtil.getNextDate(DateUtil.getDateWithZeroTime(DateUtil.getStringAsDate(expirationFrom)));
            predicateRules.add(criteriaBuilder.lessThan(path, fromDate));
        }
        return predicateRules;
    }

    public static List<Tuple> getTransactionReportShipmentReportData(ReportWarehouseTransactionDTO reportWarehouseTransactionDTO, EntityManager entityManager,
            Date fromDate, Date toDate, String[] projectIds) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root<RequestList> root = criteriaQuery.from(RequestList.class);
        List<Selection<Object>> columnSelections = getColumnSelectionsForTransactionShipmentReport(root);
        List<Predicate> predicatesRules = getPredicateRulesForTransactionShipmentReport(reportWarehouseTransactionDTO, criteriaBuilder, root, fromDate, toDate,
                                                                                        projectIds);
        criteriaQuery.where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));
        criteriaQuery.multiselect(columnSelections.toArray(new Selection[columnSelections.size()]));
        TypedQuery<Tuple> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }

    private static List<Selection<Object>> getColumnSelectionsForTransactionShipmentReport(Root<RequestList> root) {
        List<Selection<Object>> columnSelections = new ArrayList<Selection<Object>>();
        columnSelections.add(root.get("dispatchNote").get("dispatchNoteNo"));
        columnSelections.add(root.get("dispatchNote").get("dispatchNoteDate"));
        columnSelections.add(root.get("requiredDeliveryDate"));
        columnSelections.add(root.get("dispatchNote").get("deliveryDate"));
        columnSelections.add(root.get("dispatchNote").get("weight"));
        columnSelections.add(root.get("dispatchNote").get("height"));
        columnSelections.add(root.get("dispatchNote").get("carrier"));
        columnSelections.add(root.get("requestUserId"));
        columnSelections.add(root.get("shipmentType"));
        columnSelections.add(root.get("deliveryAddressId"));
        columnSelections.add(root.get("deliveryAddressName"));
        columnSelections.add(root.get("dispatchNote").get("trackingNo"));
        return columnSelections;
    }

    private static List<Predicate> getPredicateRulesForTransactionShipmentReport(ReportWarehouseTransactionDTO reportWarehouseTransactionDTO,
            CriteriaBuilder criteriaBuilder, Root<RequestList> root, Date fromDate, Date toDate, String[] projectIds) {
        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        Path<Date> pathDeliveryDateTime = root.get("dispatchNote").get("deliveryDate");
        if (fromDate != null) {
            predicatesRules.add(criteriaBuilder.greaterThanOrEqualTo(pathDeliveryDateTime, fromDate));
        }
        if (toDate != null) {
            predicatesRules.add(criteriaBuilder.lessThan(pathDeliveryDateTime, toDate));
        }
        if (reportWarehouseTransactionDTO.getWarehouse().length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("whSiteId")).value(Arrays.asList(reportWarehouseTransactionDTO.getWarehouse())));
        }
        if (projectIds != null && projectIds.length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("requestGroups").get("projectId")).value(Arrays.asList(projectIds)));
        }
        predicatesRules.add(criteriaBuilder.equal(root.get("status"), RequestListStatus.SHIPPED));
        return predicatesRules;
    }

    public static List<Tuple> getTransactionReportStoresReportData(ReportWarehouseTransactionDTO reportWarehouseTransactionDTO, EntityManager entityManager,
            Date fromDate, Date toDate) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root<MaterialLine> root = criteriaQuery.from(MaterialLine.class);
        List<Selection<Object>> columnSelections = getColumnSelectionsForTransactionStoresReport(root);
        List<Predicate> predicatesRules = getPredicateRulesForTransactionStoresReport(reportWarehouseTransactionDTO, criteriaBuilder, root, fromDate, toDate);
        criteriaQuery.where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));
        criteriaQuery.multiselect(columnSelections.toArray(new Selection[columnSelections.size()]));
        TypedQuery<Tuple> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }

    private static List<Predicate> getPredicateRulesForTransactionStoresReport(ReportWarehouseTransactionDTO reportWarehouseTransactionDTO,
            CriteriaBuilder criteriaBuilder, Root<MaterialLine> root, Date fromDate, Date toDate) {
        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        Path<Date> pathDeliveryDateTime = root.get("materialLineStatusTime").get("storedTime");
        if (fromDate != null) {
            predicatesRules.add(criteriaBuilder.greaterThanOrEqualTo(pathDeliveryDateTime, fromDate));
        }
        if (toDate != null) {
            predicatesRules.add(criteriaBuilder.lessThan(pathDeliveryDateTime, toDate));
        }
        String[] storageRooms = reportWarehouseTransactionDTO.getStorageRoom();
        if (storageRooms != null && storageRooms.length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("materialLineStatusTime").get("storedStorageRoom"))
                                               .value(Arrays.asList(reportWarehouseTransactionDTO.getWarehouse())));
        }
        String[] warehouses = reportWarehouseTransactionDTO.getWarehouse();
        if (warehouses != null && warehouses.length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("whSiteId")).value(Arrays.asList(reportWarehouseTransactionDTO.getWarehouse())));
        }
        predicatesRules.add(criteriaBuilder.isNotNull(root.get("materialLineStatusTime").get("receivedTime")));
        return predicatesRules;
    }

    private static List<Selection<Object>> getColumnSelectionsForTransactionStoresReport(Root<MaterialLine> root) {
        List<Selection<Object>> columnSelections = new ArrayList<Selection<Object>>();
        columnSelections.add(root.get("orderNo"));
        columnSelections.add(root.get("materialOwner").get("financeHeader").get("projectId"));
        columnSelections.add(root.get("material").get("mtrlRequestVersionAccepted"));
        columnSelections.add(root.get("materialLineStatusTime").get("receivedTime"));
        columnSelections.add(root.get("materialLineStatusTime").get("storedTime"));
        columnSelections.add(root.get("materialLineStatusTime").get("storedQty"));
        columnSelections.add(root.get("material").get("partAffiliation"));
        columnSelections.add(root.get("material").get("partNumber"));
        columnSelections.add(root.get("material").get("partVersion"));
        columnSelections.add(root.get("material").get("partName"));
        columnSelections.add(root.get("material").get("partModification"));
        // TODO this is a potential issue
        columnSelections.add(root.get("material").get("partModification"));

        columnSelections.add(root.get("materialLineStatusTime").get("storedStorageRoom"));
        columnSelections.add(root.get("materialLineStatusTime").get("storedBinLocation"));
        columnSelections.add(root.get("material").get("mailFormId"));
        return columnSelections;
    }

    public static FinanceHeader findFinanceHeaderByMaterialHeaderOid(long materialHeaderOid, EntityManager entityManager) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Material> criteriaQuery = criteriaBuilder.createQuery(Material.class);
        Root<Material> root = criteriaQuery.from(Material.class);
        List<Selection<Object>> columnSelections = new ArrayList<Selection<Object>>();
        columnSelections.add(root.get("financeHeader").get("projectId"));
        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.equal(root.get("materialHeader").get("materialHeaderOid"), materialHeaderOid));
        criteriaQuery.where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));
        // We need only one material to get to the finance Header
        TypedQuery<Material> query = entityManager.createQuery(criteriaQuery).setMaxResults(1);
        List<Material> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            return results.get(0).getFinanceHeader();
        }
        return null;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List<Predicate> buildReportPredicates(ReportFilterMaterialDTO reportFilterMaterialDTO, CriteriaBuilder criteriaBuilder, Root root,
            Join materialHeaderJoin, Join changeAddJoin, Join changeRemoveJoin, Join orderlineJoin, Join procurelineJoin, Join deliveryNotelineJoin,
            Join requestGroupJoin) {
        List<Predicate> predicatesRules = new ArrayList<Predicate>();

        String[] selectedCompanyCodes = reportFilterMaterialDTO.getCompanyCode();
        if (selectedCompanyCodes != null && selectedCompanyCodes.length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("material").get("financeHeader").get("companyCode")).value(Arrays.asList(selectedCompanyCodes)));
        }

        String[] selectedSuffixes = reportFilterMaterialDTO.getSuffix();
        if (selectedSuffixes != null && selectedSuffixes.length > 0) {
            predicatesRules.add(criteriaBuilder.in(orderlineJoin.get("order").get("suffix")).value(Arrays.asList(selectedSuffixes)));
        }

        String[] selectedProjects = reportFilterMaterialDTO.getProject();
        if (selectedProjects != null && selectedProjects.length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("material").get("financeHeader").get("projectId")).value(Arrays.asList(selectedProjects)));
        }

        String[] selectedBuildSerie = reportFilterMaterialDTO.getBuildSeries();
        if (selectedBuildSerie != null && selectedBuildSerie.length > 0) {
            predicatesRules.add(criteriaBuilder.in(materialHeaderJoin.get("accepted").get("referenceGroup")).value(Arrays.asList(selectedBuildSerie)));
        }

        String[] selectedTestObjects = reportFilterMaterialDTO.getTestObject();
        if (selectedTestObjects != null && selectedTestObjects.length > 0) {
            predicatesRules.add(criteriaBuilder.in(materialHeaderJoin.get("referenceId")).value(Arrays.asList(selectedTestObjects)));
        }

        String[] selectedPhases = reportFilterMaterialDTO.getPhaseName();
        if (selectedPhases != null && selectedPhases.length > 0) {
            predicatesRules.add(criteriaBuilder.in(materialHeaderJoin.get("buildName")).value(Arrays.asList(selectedPhases)));
        }

        String[] selectedMtrIds = reportFilterMaterialDTO.getMtrId();
        if (selectedMtrIds != null && selectedMtrIds.length > 0) {
            predicatesRules.add(criteriaBuilder.selectCase(root.get("status")).when(MaterialLineStatus.REMOVED, changeRemoveJoin.get("mtrlRequestVersion"))
                                               .otherwise(changeAddJoin.get("mtrlRequestVersion")).in(Arrays.asList(selectedMtrIds)));
        }

        String[] selectedWbs = reportFilterMaterialDTO.getWbs();
        if (selectedWbs != null && selectedWbs.length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("material").get("financeHeader").get("wbsCode")).value(Arrays.asList(selectedWbs)));
        }

        String[] selectedMaterialStatuses = reportFilterMaterialDTO.getStatus();
        if (selectedMaterialStatuses != null && selectedMaterialStatuses.length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("status"))
                                               .value(GloriaFormateUtil.getValuesAsEnums(StringUtils.join(selectedMaterialStatuses, ","),
                                                                                         MaterialLineStatus.class)));
        }

        String[] selectedMaterialTypes = reportFilterMaterialDTO.getMaterialType();
        if (selectedMaterialTypes != null && selectedMaterialTypes.length > 0) {
            Set<String> selectedMaterialTypesSet = new HashSet<String>(Arrays.asList(selectedMaterialTypes));
            if (selectedMaterialTypesSet.contains("ADDITIONAL")) {
                selectedMaterialTypesSet.add("ADDITIONAL_USAGE");
            }
            selectedMaterialTypes = selectedMaterialTypesSet.toArray(new String[selectedMaterialTypesSet.size()]);
        }

        if (selectedMaterialTypes != null && selectedMaterialTypes.length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("material").get("materialType"))
                                               .value(GloriaFormateUtil.getValuesAsEnums(StringUtils.join(selectedMaterialTypes, ","), MaterialType.class)));
        }

        String[] selectedMaterialControllerTeam = reportFilterMaterialDTO.getMaterialControllerTeam();
        if (selectedMaterialControllerTeam != null && selectedMaterialControllerTeam.length > 0) {
            predicatesRules.add(criteriaBuilder.or(criteriaBuilder.in(materialHeaderJoin.get("materialControllerTeam"))
                                                                  .value(Arrays.asList(selectedMaterialControllerTeam)),
                                                   criteriaBuilder.in(procurelineJoin.get("materialControllerTeam"))
                                                                  .value(Arrays.asList(selectedMaterialControllerTeam))));
        }

        predicatesRules.add(criteriaBuilder.notEqual(root.get("status"), MaterialLineStatus.REMOVED_DB));

        return predicatesRules;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List<Selection> buildSelections(ReportFilterMaterialDTO reportFilterMaterialDTO, CriteriaBuilder criteriaBuilder, Root root,
            Join materialHeaderJoin, Join changeAddJoin, Join changeRemoveJoin, Join orderlineJoin, Join procurelineJoin, Join deliveryNotelineJoin,
            Join requestGroupJoin) {

        List<Selection> activeSelections = new ArrayList<Selection>();

        // default selections
        addSelections(true, root.get("material").get("financeHeader").get("companyCode"), REPORT_COLUMN_ID_COMPANY_CODE, activeSelections);
        addSelections(true, orderlineJoin.get("order").get("suffix"), REPORT_COLUMN_ID_SUFFIX, activeSelections);
        addSelections(true, root.get("material").get("financeHeader").get("projectId"), REPORT_COLUMN_ID_PROJECT, activeSelections);
        addSelections(true, materialHeaderJoin.get("accepted").get("referenceGroup"), REPORT_COLUMN_ID_BUILD_SERIES, activeSelections);
        addSelections(true, materialHeaderJoin.get("referenceId"), REPORT_COLUMN_ID_TEST_OBJECT, activeSelections);
        addSelections(true, materialHeaderJoin.get("buildName"), REPORT_COLUMN_ID_BUILD_NAME, activeSelections);
        addSelections(true, criteriaBuilder.selectCase(root.get("status")).when(MaterialLineStatus.REMOVED, changeRemoveJoin.get("mtrlRequestVersion"))
                                           .otherwise(changeAddJoin.get("mtrlRequestVersion")), REPORT_COLUMN_ID_MTR_ID, activeSelections);
        addSelections(true, root.get("material").get("financeHeader").get("wbsCode"), REPORT_COLUMN_ID_WBS, activeSelections);
        addSelections(true, root.get("status"), REPORT_COLUMN_ID_STATUS, activeSelections);
        addSelections(true,
                      criteriaBuilder.selectCase(root.get("material").get("materialType")).when("ADDITIONAL_USAGE", "ADDITIONAL")
                                     .otherwise(root.get("material").get("materialType")), REPORT_COLUMN_ID_MATERIAL_TYPE, activeSelections);
        addSelections(true,
                      criteriaBuilder.selectCase()
                                     .when(materialHeaderJoin.get("materialControllerTeam").isNull(), procurelineJoin.get("materialControllerTeam"))
                                     .otherwise(materialHeaderJoin.get("materialControllerTeam")), REPORT_COLUMN_ID_MATERIAL_CONTROLLER_TEAM, activeSelections);
        addSelections(true, root.get("material").get("partAffiliation"), REPORT_COLUMN_ID_PART_AFFILIATION, activeSelections);
        addSelections(true, root.get("material").get("partNumber"), REPORT_COLUMN_ID_PART_NUMBER, activeSelections);
        addSelections(true, root.get("material").get("partVersion"), REPORT_COLUMN_ID_PART_VERSION, activeSelections);
        addSelections(true, root.get("material").get("partVersionOriginal"), REPORT_COLUMN_ID_PART_VERSION_ORIGINAL, activeSelections);
        addSelections(true, root.get("material").get("partName"), REPORT_COLUMN_ID_PART_NAME, activeSelections);
        addSelections(true, root.get("material").get("partModification"), REPORT_COLUMN_ID_PART_MODIFICATION, activeSelections);

        // user selections
        addSelections(reportFilterMaterialDTO.isCostCenter(), root.get("material").get("financeHeader").get("costCenter"), REPORT_COLUMN_ID_COST_CENTER,
                      activeSelections);

        addSelections(reportFilterMaterialDTO.isGlAccount(), root.get("material").get("financeHeader").get("glAccount"), REPORT_COLUMN_ID_GL_ACCOUNT,
                      activeSelections);

        addSelections(reportFilterMaterialDTO.isInternalOrderSap(), root.get("material").get("financeHeader").get("internalOrderNoSAP"),
                      REPORT_COLUMN_ID_INTERNAL_ORDER_SAP, activeSelections);

        addSelections(reportFilterMaterialDTO.isMailFormId(), root.get("material").get("mailFormId"), REPORT_COLUMN_ID_MAILFORM_ID, activeSelections);

        addSelections(reportFilterMaterialDTO.isFunctionGroup(), root.get("material").get("functionGroup"), REPORT_COLUMN_ID_FUNCTION_GROUP, activeSelections);

        addSelections(reportFilterMaterialDTO.isDesignGroup(), root.get("material").get("designResponsible"), REPORT_COLUMN_ID_DESIGN_GROUP, activeSelections);

        addSelections(reportFilterMaterialDTO.isModularHarness(), root.get("material").get("modularHarness"), REPORT_COLUMN_ID_MODULAR_HARNESS,
                      activeSelections);
        addSelections(reportFilterMaterialDTO.isRequisitionId(), procurelineJoin.get("requisitionId"), REPORT_COLUMN_ID_REQUISITION_ID, activeSelections);
        addSelections(reportFilterMaterialDTO.isRequisitionDate(), procurelineJoin.get("procureDate"), REPORT_COLUMN_ID_REQUISITION_DATE, activeSelections);

        addSelections(reportFilterMaterialDTO.isReference(), procurelineJoin.get("referenceGps"), REPORT_COLUMN_ID_REFERENCE, activeSelections);
        addSelections(reportFilterMaterialDTO.isRequiredSta(), root.get("material").get("requiredStaDate"), REPORT_COLUMN_ID_REQUIRED_STA_DATE,
                      activeSelections);
        addSelections(reportFilterMaterialDTO.isBuildLocationId(), materialHeaderJoin.get("accepted").get("outboundLocationId"),
                      REPORT_COLUMN_ID_BUILDLOCATION_ID, activeSelections);
        addSelections(reportFilterMaterialDTO.isBuildLocationName(), materialHeaderJoin.get("accepted").get("outboundLocationName"),
                      REPORT_COLUMN_ID_BUILDLOCATION_NAME, activeSelections);
        addSelections(reportFilterMaterialDTO.isBuildStart(), materialHeaderJoin.get("accepted").get("outboundStartDate"), REPORT_COLUMN_ID_BUILD_START,
                      activeSelections);
        addSelections(reportFilterMaterialDTO.isMaterialControllerId(), materialHeaderJoin.get("materialControllerUserId"),
                      REPORT_COLUMN_ID_MATERIAL_CONTROLLER_ID, activeSelections);
        addSelections(reportFilterMaterialDTO.isMaterialControllerName(), materialHeaderJoin.get("materialControllerName"),
                      REPORT_COLUMN_ID_MATERIAL_CONTROLLER_NAME, activeSelections);
        addSelections(reportFilterMaterialDTO.isRequesterId(), materialHeaderJoin.get("accepted").get("requesterUserId"), REPORT_COLUMN_ID_REQUESTER_ID,
                      activeSelections);
        addSelections(reportFilterMaterialDTO.isRequesterName(), materialHeaderJoin.get("accepted").get("requesterName"), REPORT_COLUMN_ID_REQUESTER_NAME,
                      activeSelections);
        addSelections(reportFilterMaterialDTO.isContactPersonId(), materialHeaderJoin.get("accepted").get("contactPersonId"),
                      REPORT_COLUMN_ID_CONTACT_PERSON_ID, activeSelections);
        addSelections(reportFilterMaterialDTO.isContactPersonName(), materialHeaderJoin.get("accepted").get("contactPersonName"),
                      REPORT_COLUMN_ID_CONTACT_PERSON_NAME, activeSelections);
        addSelections(reportFilterMaterialDTO.isWarehouse(), root.get("whSiteId"), REPORT_COLUMN_ID_WAREHOUSE, activeSelections);
        addSelections(reportFilterMaterialDTO.isStorageRoom(), root.get("storageRoomCode"), REPORT_COLUMN_ID_STORAGE_ROOM, activeSelections);
        addSelections(reportFilterMaterialDTO.isBinLocation(), root.get("binLocationCode"), REPORT_COLUMN_ID_BINLOCATION, activeSelections);
        addSelections(reportFilterMaterialDTO.isQty(), root.get("quantity"), REPORT_COLUMN_ID_QTY, activeSelections);
        addSelections(reportFilterMaterialDTO.isUnitOfMeasure(), root.get("material").get("unitOfMeasure"), REPORT_COLUMN_ID_UNIT_OF_MEASURE, activeSelections);
        addSelections(reportFilterMaterialDTO.isReceivedDate(), root.get("receivedDate"), REPORT_COLUMN_ID_RECEIVED_DATE, activeSelections);
        addSelections(reportFilterMaterialDTO.isDeliveryNoteNo(),
                      criteriaBuilder.selectCase()
                                     .when(criteriaBuilder.equal(deliveryNotelineJoin.get("deliveryNote").get("receiveType"), ReceiveType.REGULAR),
                                           deliveryNotelineJoin.get("deliveryNote").get("deliveryNoteNo")).otherwise(""), REPORT_COLUMN_ID_DELIVERY_NOTE_NO,
                      activeSelections);
        addSelections(reportFilterMaterialDTO.isDispatchNoteNo(),
                      criteriaBuilder.selectCase()
                                     .when(criteriaBuilder.notEqual(deliveryNotelineJoin.get("deliveryNote").get("receiveType"), ReceiveType.REGULAR),
                                           deliveryNotelineJoin.get("deliveryNote").get("deliveryNoteNo"))
                                     .when(root.get("status").in(MaterialLineStatus.SHIPPED, MaterialLineStatus.IN_TRANSFER),
                                           requestGroupJoin.get("requestList").get("dispatchNote").get("dispatchNoteNo")).otherwise(""),
                      REPORT_COLUMN_ID_DISPATCH_NOTE_NO, activeSelections);
        addSelections(reportFilterMaterialDTO.isDeliveryAddressId(), requestGroupJoin.get("requestList").get("deliveryAddressId"),
                      REPORT_COLUMN_ID_DELIVERY_ADDRESS_ID, activeSelections);
        addSelections(reportFilterMaterialDTO.isDeliveryAddressName(), requestGroupJoin.get("requestList").get("deliveryAddressName"),
                      REPORT_COLUMN_ID_DELIVERY_ADDRESS_NAME, activeSelections);
        addSelections(reportFilterMaterialDTO.isSource(), procurelineJoin.get("procureType"), REPORT_COLUMN_ID_SOURCE, activeSelections);
        addSelections(reportFilterMaterialDTO.isOrderNo(), orderlineJoin.get("order").get("orderNo"), REPORT_COLUMN_ID_ORDER_NO, activeSelections);

        addSelections(reportFilterMaterialDTO.isOrderStatus(),
                      criteriaBuilder.selectCase().when(orderlineJoin.get("status").in(OrderLineStatus.PLACED), "PLACED")
                                     .when(orderlineJoin.get("status").in(OrderLineStatus.RECEIVED_PARTLY), "RECEIVED PARTLY")
                                     .when(root.get("status").in(MaterialLineStatus.WAIT_TO_PROCURE), "NOT PLACED")
                                     .when(root.get("status").in(MaterialLineStatus.REQUISITION_SENT), "NOT PLACED")
                                     .when(root.get("status").in(MaterialLineStatus.CREATED), "NOT PLACED")
                                     .when(root.get("status").in(MaterialLineStatus.REMOVED), "NOT PLACED")
                                     .when(criteriaBuilder.in(orderlineJoin.get("completeType")).value(CompleteType.RECEIVED), "RECEIVED")
                                     .when(criteriaBuilder.in(orderlineJoin.get("completeType")).value(CompleteType.COMPLETE), "COMPLETE")
                                     .when(criteriaBuilder.in(orderlineJoin.get("completeType")).value(CompleteType.CANCELLED), "CANCELLED"),
                      REPORT_COLUMN_ID_ORDER_STATUS, activeSelections);

        addSelections(reportFilterMaterialDTO.isPurchasingOrganisation(), procurelineJoin.get("purchaseOrgCode"), REPORT_COLUMN_ID_PURCHASE_ORGANIZATION,
                      activeSelections);

        addSelections(reportFilterMaterialDTO.isBuyerId(),
                      criteriaBuilder.selectCase().when(root.get("material").get("orderLine").isNotNull(), orderlineJoin.get("current").get("buyerId"))
                                     .otherwise(procurelineJoin.get("buyerCode")), REPORT_COLUMN_ID_BUYER_ID, activeSelections);

        addSelections(reportFilterMaterialDTO.isBuyerName(), orderlineJoin.get("current").get("buyerName"), REPORT_COLUMN_ID_BUYER_NAME, activeSelections);
        addSelections(reportFilterMaterialDTO.isInternalProcurerId(),
                      criteriaBuilder.selectCase().when(procurelineJoin.get("forwardedUserId").isNotNull(), procurelineJoin.get("forwardedUserId"))
                                     .otherwise(""), REPORT_COLUMN_ID_INTERNAL_PROCURER_ID, activeSelections);
        addSelections(reportFilterMaterialDTO.isOrderDate(), orderlineJoin.get("order").get("orderDateTime"), REPORT_COLUMN_ID_ORDER_DATE, activeSelections);
        addSelections(reportFilterMaterialDTO.isOrderQty(), orderlineJoin.get("current").get("quantity"), REPORT_COLUMN_ID_ORDER_QTY, activeSelections);
        addSelections(reportFilterMaterialDTO.isPossibleToReceiveQty(), orderlineJoin.get("possibleToReceiveQuantity"),
                      REPORT_COLUMN_ID_POSSIBLE_TO_RECEIVE_QTY, activeSelections);
        addSelections(reportFilterMaterialDTO.isOrderSTA(), orderlineJoin.get("current").get("orderStaDate"), REPORT_COLUMN_ID_ORDER_STA, activeSelections);
        addSelections(reportFilterMaterialDTO.isAgreedSTA(), orderlineJoin.get("current").get("staAgreedDate"), REPORT_COLUMN_ID_AGREED_STA, activeSelections);
        addSelections(reportFilterMaterialDTO.isStaAccepted(), orderlineJoin.get("current").get("staAcceptedDate"), REPORT_COLUMN_ID_STA_ACCEPTED,
                      activeSelections);

        // Handle Order line version columns for price calculations inside tuples
        boolean isOrdLineVersionSelection = (reportFilterMaterialDTO.isPrice() || reportFilterMaterialDTO.isCurrency() 
                                                            || reportFilterMaterialDTO.isUnitPrice() || reportFilterMaterialDTO.isUnitOfPrice());

        addSelections(isOrdLineVersionSelection, orderlineJoin.get("current"), REPORT_COLUMN_ID_ORDERLINE_VERSION, activeSelections);

        addSelections(reportFilterMaterialDTO.isSupplierParmaId(), orderlineJoin.get("order").get("supplierId"), REPORT_COLUMN_ID_SUPPLIER_PARMA_ID,
                      activeSelections);
        addSelections(reportFilterMaterialDTO.isSupplierName(), orderlineJoin.get("order").get("supplierName"), REPORT_COLUMN_ID_SUPPLIER_NAME,
                      activeSelections);
        addSelections(reportFilterMaterialDTO.isDeliveryControllerId(), orderlineJoin.get("deliveryControllerUserId"), REPORT_COLUMN_ID_DELIVERY_CONTROLLER_ID,
                      activeSelections);
        addSelections(reportFilterMaterialDTO.isDeliveryControllerName(), orderlineJoin.get("deliveryControllerUserName"),
                      REPORT_COLUMN_ID_DELIVERY_CONTROLLER_NAME, activeSelections);

        addSelections(reportFilterMaterialDTO.isProcureInfo(), procurelineJoin.join("requisition").get("purchaseInfo1"), REPORT_COLUMN_ID_PROCURE_INFO1,
                      activeSelections);
        addSelections(reportFilterMaterialDTO.isProcureInfo(), procurelineJoin.join("requisition").get("purchaseInfo2"), REPORT_COLUMN_ID_PROCURE_INFO2,
                      activeSelections);
        addSelections(reportFilterMaterialDTO.isReceivedQty(),
                      criteriaBuilder.selectCase()
                                     .when(root.get("status").in(MaterialLineStatus.RECEIVED, MaterialLineStatus.QI_READY, MaterialLineStatus.BLOCKED,
                                                                 MaterialLineStatus.QI_OK, MaterialLineStatus.READY_TO_STORE, MaterialLineStatus.STORED,
                                                                 MaterialLineStatus.REQUESTED, MaterialLineStatus.READY_TO_SHIP, MaterialLineStatus.SHIPPED,
                                                                 MaterialLineStatus.DEVIATED, MaterialLineStatus.SCRAPPED, MaterialLineStatus.MISSING,
                                                                 MaterialLineStatus.MARKED_INSPECTION, MaterialLineStatus.QTY_DECREASED,
                                                                 MaterialLineStatus.IN_TRANSFER), root.get("quantity")).otherwise(0L),
                      REPORT_COLUMN_ID_RECEIVED_QTY, activeSelections);
        addSelections(reportFilterMaterialDTO.isBlockedQty(),
                      criteriaBuilder.selectCase().when(root.get("status").in(MaterialLineStatus.BLOCKED), root.get("quantity")).otherwise(0L),
                      REPORT_COLUMN_ID_BLOCKED_QTY, activeSelections);
        addSelections((reportFilterMaterialDTO.isOrderLineLogEventValue() || reportFilterMaterialDTO.isExpectedQty()
                              || reportFilterMaterialDTO.isExpectedDate() || reportFilterMaterialDTO.isPlannedDispatchDate() 
                              || reportFilterMaterialDTO.isFlagOrderLine()),
                      orderlineJoin, REPORT_COLUMN_ID_ORDER_LINE, activeSelections);
        addSelections(reportFilterMaterialDTO.isOrderLogEventValue(), orderlineJoin.get("order"), REPORT_COLUMN_ID_ORDER_LOG, activeSelections);
        addSelections(reportFilterMaterialDTO.isInspectionStatus(), root.get("inspectionStatus"), REPORT_COLUMN_ID_INSPECTION_STATUS, activeSelections);
        addSelections(reportFilterMaterialDTO.isStaAgreedLastUpdated(), orderlineJoin.get("current").get("staAgreedLastUpdated"),
                      REPORT_COLUMN_ID_AGREED_STA_LAST_MODIFIED_DATE, activeSelections);
        addSelections(reportFilterMaterialDTO.isProblemDescription(), deliveryNotelineJoin.get("problemDescription"), REPORT_COLUMN_ID_PROBLEM_DESCRIPION,
                      activeSelections);
        addSelections(reportFilterMaterialDTO.isQualityInspectionComment(), deliveryNotelineJoin.get("qualityInspectionComment"),
                      REPORT_COLUMN_ID_QUALITY_INSPECTION_COMMENT, activeSelections);
        addSelections(reportFilterMaterialDTO.isStatusUserId(), root.get("statusUserId"), REPORT_COLUMN_ID_ORDER_STATUS_USER_ID, activeSelections);
        addSelections(reportFilterMaterialDTO.isStatusUserName(), root.get("statusUserName"), REPORT_COLUMN_ID_ORDER_STATUS_USER_NAME, activeSelections);
		addSelections(reportFilterMaterialDTO.isFlagProcureLine(), procurelineJoin.get("statusFlag"), REPORT_COLUMN_ID_FLAG_PROCURE_LINE, activeSelections);

        return activeSelections;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static List<Predicate> buildReportPredicates(List<Predicate> predicatesRules, ReportWarehouseCostDTO reportWarehouseCostDTO,
            CriteriaBuilder criteriaBuilder, Root root, Join orderlineJoin, Date fromDate, Date toDate) {

        predicatesRules.add(root.get("material").get("materialType").in(MaterialType.USAGE, MaterialType.ADDITIONAL, MaterialType.MODIFIED));
        predicatesRules.add(root.get("status").in(MaterialLineStatus.SCRAPPED, MaterialLineStatus.MISSING));

        String[] selectedWarehouses = reportWarehouseCostDTO.getWarehouse();
        if (selectedWarehouses != null && selectedWarehouses.length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("whSiteId")).value(Arrays.asList(selectedWarehouses)));
        }

        String[] selectedProjects = reportWarehouseCostDTO.getProject();
        if (selectedProjects != null && selectedProjects.length > 0) {
            predicatesRules.add(criteriaBuilder.in(orderlineJoin.get("projectId")).value(Arrays.asList(selectedProjects)));
        }

        if (fromDate != null) {
            Date dateRangeFrom = DateUtil.getDateWithZeroTime(fromDate);
            predicatesRules.add(criteriaBuilder.greaterThanOrEqualTo(root.get("material").get("createdDate"), dateRangeFrom));
        }

        if (toDate != null) {
            Date dateRangeTo = DateUtil.getNextDate(toDate);
            predicatesRules.add(criteriaBuilder.lessThan(root.get("material").get("createdDate"), dateRangeTo));
        }

        return predicatesRules;
    }

    @SuppressWarnings({ "rawtypes" })
    public static List<Selection> buildSelections(List<Selection> activeSelections, List<Expression> groupByExpressions,
            ReportWarehouseCostDTO reportWarehouseCostDTO, CriteriaBuilder criteriaBuilder, Root root, Join orderlineJoin) {

        // default selections
        addSelections(true, root.get("material").get("financeHeader").get("companyCode"), REPORT_COLUMN_ID_COMPANY_CODE, activeSelections);
        addSelections(true, root.get("whSiteId"), REPORT_COLUMN_ID_WAREHOUSE, activeSelections);
        addSelections(true, orderlineJoin.get("projectId"), REPORT_COLUMN_ID_PROJECT, activeSelections);
        addSelections(true, root.get("status"), REPORT_COLUMN_ID_STATUS, activeSelections);
        addSelections(true, root.get("quantity"), REPORT_COLUMN_ID_QTY, activeSelections);
        addSelections((reportWarehouseCostDTO.isInventoryAdjustmentValue() || reportWarehouseCostDTO.isScrappedPartValue()),
                      orderlineJoin.get("current").get("unitPrice"), REPORT_COLUMN_ID_UNIT_PRICE, activeSelections);
        addSelections((reportWarehouseCostDTO.isInventoryAdjustmentValue() || reportWarehouseCostDTO.isScrappedPartValue()),
                      orderlineJoin.get("current").get("perQuantity"), REPORT_COLUMN_ID_UNIT_OF_PRICE, activeSelections);
        addSelections((reportWarehouseCostDTO.isInventoryAdjustmentValue() || reportWarehouseCostDTO.isScrappedPartValue()),
                      orderlineJoin.get("current").get("currency"), REPORT_COLUMN_ID_CURRENCY, activeSelections);
        return activeSelections;
    }

    @SuppressWarnings("rawtypes")
    private static void addSelections(boolean isToBeSelected, Selection activeSelection, String columnName, List<Selection> activeSelections) {
        if (isToBeSelected) {
            try {
                activeSelections.add(activeSelection.alias(columnName));
            } catch (Exception e) {
                GloriaExceptionLogger.log(e, RequestHeaderRepositoryBeanHelper.class);
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Object handleTuples(Tuple materialDataTuple, Selection selection, String orderLineColumn) {
        if (orderLineColumn == null) {
            if (selection.getJavaType() != null && Order.class.isAssignableFrom(selection.getJavaType())) {
                Object materialDataTupleSelection = materialDataTuple.get(selection);
                if (materialDataTupleSelection != null) {
                    return OrderRepositoryBeanHelper.handleLogs(((Order) materialDataTupleSelection).getOrderLog());
                } else {
                    return null;
                }
            }
        } else {
            if (orderLineColumn.equals(REPORT_COLUMN_ID_ORDER_LINE_LOG)) {
                Object orderLineSelection = materialDataTuple.get(selection);
                if (orderLineSelection != null) {
                    return OrderRepositoryBeanHelper.handleLogs(((OrderLine) orderLineSelection).getOrderLineLog());
                } else {
                    return null;
                }
            } else {
                if (materialDataTuple.get(selection) != null) {
                    DeliverySchedule earliestDeliverySchedule = evaluateEarliestDeliverySchedule(((OrderLine) materialDataTuple.get(selection)).getDeliverySchedule());

                    // manage delivery schedule attributes
                    if (earliestDeliverySchedule != null && orderLineColumn.equals(REPORT_COLUMN_ID_EXPECTED_QTY)) {
                        return earliestDeliverySchedule.getExpectedQuantity();
                    } else if (earliestDeliverySchedule != null && orderLineColumn.equals(REPORT_COLUMN_ID_EXPECTED_DATE)) {
                        return earliestDeliverySchedule.getExpectedDate();
                    } else if (earliestDeliverySchedule != null && orderLineColumn.equals(REPORT_COLUMN_ID_PLANNED_DISPATCH_DATE)) {
                        return earliestDeliverySchedule.getPlannedDispatchDate();
                    } else if (earliestDeliverySchedule != null && orderLineColumn.equals(REPORT_COLUMN_ID_FLAG_ORDER_LINE)) {
                        return earliestDeliverySchedule.getStatusFlag();
                    }
                }

            }
        }
        return materialDataTuple.get(selection);
    }

    public static DeliverySchedule evaluateEarliestDeliverySchedule(List<DeliverySchedule> deliverySchedules) {
        if (deliverySchedules != null && !deliverySchedules.isEmpty()) {
            // sorts for earliest date
            Collections.sort(deliverySchedules, new Comparator<DeliverySchedule>() {
                public int compare(DeliverySchedule deliveryScheduleOne, DeliverySchedule deliveryScheduleTwo) {
                    if (deliveryScheduleOne.getExpectedDate().before(deliveryScheduleTwo.getExpectedDate())) {
                        return -1;
                    }
                    return 1;
                }
            });
            return deliverySchedules.get(0);
        }
        return null;
    }
}
