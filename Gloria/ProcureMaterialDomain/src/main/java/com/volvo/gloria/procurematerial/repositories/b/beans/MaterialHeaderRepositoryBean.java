package com.volvo.gloria.procurematerial.repositories.b.beans;

import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_COMPANY_CODE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_CURRENCY;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_CURRENCY_ORIGINAL;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_EXPECTED_DATE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_EXPECTED_QTY;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_FLAG_ORDER_LINE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_INVENTORY_ADJUSTMENT_VALUE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_ORDERLINE_VERSION;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_ORDER_LINE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_ORDER_LINE_LOG;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_PLANNED_DISPATCH_DATE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_PROJECT;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_QTY;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_SCRAPPED_PART_VALUE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_STATUS;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_TOTAL_PRICE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_TOTAL_PRICE_ORIGINAL;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_UNIT_OF_PRICE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_UNIT_PRICE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_UNIT_PRICE_ORIGINAL;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_WAREHOUSE;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang.StringUtils;

import com.volvo.gloria.authorization.d.entities.GloriaUser;
import com.volvo.gloria.authorization.d.userrole.UserRoleHelper;
import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.CurrencyUtil;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.c.dto.reports.ReportFilterMaterialDTO;
import com.volvo.gloria.common.c.dto.reports.ReportWarehouseCostDTO;
import com.volvo.gloria.common.c.dto.reports.ReportWarehouseTransactionDTO;
import com.volvo.gloria.common.d.entities.CurrencyRate;
import com.volvo.gloria.common.repositories.b.TeamRepository;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.b.ProcurementDtoTransformer;
import com.volvo.gloria.procurematerial.b.beans.MaterialServicesHelper;
import com.volvo.gloria.procurematerial.c.DeliveryAddressType;
import com.volvo.gloria.procurematerial.c.ProcureResponsibility;
import com.volvo.gloria.procurematerial.c.QiMarking;
import com.volvo.gloria.procurematerial.c.ShipmentType;
import com.volvo.gloria.procurematerial.c.dto.ChangeIdDTO;
import com.volvo.gloria.procurematerial.c.dto.MaterialDTO;
import com.volvo.gloria.procurematerial.c.dto.MaterialHeaderDTO;
import com.volvo.gloria.procurematerial.c.dto.MaterialLineDTO;
import com.volvo.gloria.procurematerial.c.dto.MaterialLineQiDTO;
import com.volvo.gloria.procurematerial.c.dto.PickListDTO;
import com.volvo.gloria.procurematerial.c.dto.RequestGroupDTO;
import com.volvo.gloria.procurematerial.c.dto.RequestListDTO;
import com.volvo.gloria.procurematerial.d.entities.ChangeId;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.DispatchNote;
import com.volvo.gloria.procurematerial.d.entities.FinanceHeader;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeaderVersion;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.PickList;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.RequestGroup;
import com.volvo.gloria.procurematerial.d.entities.RequestList;
import com.volvo.gloria.procurematerial.d.entities.status.changeid.ChangeIdStatus;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.InspectionStatus;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatusHelper;
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.requestline.MaterialStatus;
import com.volvo.gloria.procurematerial.d.entities.status.requestlist.RequestListStatus;
import com.volvo.gloria.procurematerial.d.type.directsend.DirectSendType;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.procurematerial.d.type.request.RequestType;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.util.MaterialTransformHelper;
import com.volvo.gloria.procurematerial.util.ProcurementHelper;
import com.volvo.gloria.procurerequest.c.dto.MaterialLineAvailableDTO;
import com.volvo.gloria.reports.ReportHelper;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.GloriaFormateUtil;
import com.volvo.gloria.util.c.GloriaExceptionConstants;
import com.volvo.gloria.util.c.dto.reports.ReportColumn;
import com.volvo.gloria.util.c.dto.reports.ReportFilterDTO;
import com.volvo.gloria.util.c.dto.reports.ReportRow;
import com.volvo.gloria.util.paging.c.JpaAttribute;
import com.volvo.gloria.util.paging.c.JpaAttributeType;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.paging.c.PageResults;
import com.volvo.gloria.util.persistence.GenericAbstractRepositoryBean;
import com.volvo.gloria.warehouse.b.WarehouseServices;
import com.volvo.gloria.warehouse.c.ZoneType;
import com.volvo.gloria.warehouse.d.entities.BinLocation;
import com.volvo.gloria.warehouse.d.entities.BinlocationBalance;
import com.volvo.gloria.warehouse.d.entities.Placement;
import com.volvo.gloria.warehouse.d.entities.Zone;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * repository implementation for ROOT - RequestHeader.
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class MaterialHeaderRepositoryBean extends GenericAbstractRepositoryBean<MaterialHeader, Long> implements MaterialHeaderRepository {

    private static final String KEY_DELIMITER_HYPHEN = "-";
    private static final String KEY_DELIMITER_COMMA = ",";
    private static final int KEY_ITEM_1 = 0;
    private static final int KEY_ITEM_2 = 1;
    private static final int KEY_ITEM_3 = 2;
    private static final int KEY_ITEM_4 = 3;

    private static final int QUERY_PARAM_LOCATION_ONE = 1;
    private static final int QUERY_PARAM_LOCATION_TWO = 2;
    private static final int QUERY_PARAM_LOCATION_THREE = 3;

    @Inject
    private WarehouseServices warehouseServices;

    @Inject
    private MaterialServices materialServices;

    @Inject
    private ProcurementDtoTransformer procurementDtoTransformer;

    @Inject
    private CommonServices commonServices;

    @Inject
    private TeamRepository teamRepository;

    /**
     * Search attributes for procure request headers.
     */
    private static final Map<String, JpaAttribute> REQUEST_HEADER_FIELDS = new HashMap<String, JpaAttribute>();
    private static final String WILDCARD = "%";
    static {
        REQUEST_HEADER_FIELDS.put("projectId", new JpaAttribute("materials.financeHeader.projectId", JpaAttributeType.STRINGTYPE));
        REQUEST_HEADER_FIELDS.put("referenceGroup", new JpaAttribute("accepted.referenceGroup", JpaAttributeType.STRINGTYPE));
        REQUEST_HEADER_FIELDS.put("referenceId", new JpaAttribute("referenceId", JpaAttributeType.STRINGTYPE));
        REQUEST_HEADER_FIELDS.put("mtrlRequestVersion", new JpaAttribute("accepted.changeId.mtrlRequestVersion", JpaAttributeType.STRINGTYPE));
        REQUEST_HEADER_FIELDS.put("outboundLocationId", new JpaAttribute("accepted.outboundLocationId", JpaAttributeType.STRINGTYPE));
        REQUEST_HEADER_FIELDS.put("requesterUserId", new JpaAttribute("accepted.requesterUserId", JpaAttributeType.STRINGTYPE));
        REQUEST_HEADER_FIELDS.put("receivedDateTime", new JpaAttribute("accepted.receivedDateTime", JpaAttributeType.DATETYPE));
        REQUEST_HEADER_FIELDS.put("outboundStartDate", new JpaAttribute("accepted.outboundStartDate", JpaAttributeType.DATETYPE));
        REQUEST_HEADER_FIELDS.put("assignedMaterialControllerId", new JpaAttribute("materialControllerUserId", JpaAttributeType.STRINGTYPE));
    }

    @PersistenceContext(unitName = "ProcureMaterialDomainPU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<ChangeId> findChangeId(long procureLineOId, ChangeIdStatus... changeIdStatuses) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(ChangeId.class);

        if ((changeIdStatuses != null && changeIdStatuses.length > 0) && procureLineOId > 0) {
            Path<String> path = root.get("status");
            Predicate predicateIn = path.in(Arrays.asList(changeIdStatuses));
            criteriaQuery.where(criteriaBuilder.and(predicateIn,
                                                    criteriaBuilder.equal(root.get("removeMaterials").get("procureLine").get("procureLineOid"), procureLineOId)));
        }

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root);

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        return resultSetQuery.getResultList();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public ChangeId findChangeIdByOid(long changeIdOId) {
        ChangeId changeId = null;
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(ChangeId.class);

        criteriaQuery.where(criteriaBuilder.equal(root.get("changeIdOid"), changeIdOId));
        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root);
        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        Object singleResult = resultSetQuery.getSingleResult();
        if (singleResult != null) {
            changeId = (ChangeId) singleResult;
        }
        return changeId;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<Material> getMaterialsByChangeId(long changeIdOId) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(Material.class);
        Predicate addChangeId = criteriaBuilder.equal(root.get("add").get("changeIdOid"), changeIdOId);
        Predicate removeChangeId = criteriaBuilder.equal(root.get("remove").get("changeIdOid"), changeIdOId);

        Path<String> path = root.get("materialType");
        Predicate materialTypePredicate = path.in(MaterialType.USAGE, MaterialType.USAGE_REPLACED, MaterialType.RELEASED);

        criteriaQuery.where(criteriaBuilder.and(criteriaBuilder.or(addChangeId, removeChangeId), materialTypePredicate));
        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root);
        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        return resultSetQuery.getResultList();
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<MaterialLine> getMaterialLinesInStock(String partNumber, String partVersion, String partAffiliation, String projectId, String partModification,
            String companyCode) {
        Collection<String> companyCodeCodes = new ArrayList<String>();
        if (companyCode != null) {
            companyCodeCodes.add(companyCode);
        }
        List<String> siteIds = commonServices.getSiteIdsByCompanyCodes(companyCodeCodes);
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(MaterialLine.class);

        Predicate releasedMaterials = criteriaBuilder.and(criteriaBuilder.equal(root.get("material").get("partNumber"), partNumber),
                                                          criteriaBuilder.equal(root.get("material").get("partVersion"), partVersion),
                                                          criteriaBuilder.equal(root.get("material").get("partAffiliation"), partAffiliation),
                                                          criteriaBuilder.equal(root.get("material").get("partModification"), partModification),
                                                          criteriaBuilder.equal(root.get("material").get("materialType"), MaterialType.RELEASED));

        Predicate additionalMaterials = criteriaBuilder.and(criteriaBuilder.equal(root.get("material").get("partNumber"), partNumber),
                                                            criteriaBuilder.equal(root.get("material").get("partVersion"), partVersion),
                                                            criteriaBuilder.equal(root.get("material").get("partAffiliation"), partAffiliation),
                                                            criteriaBuilder.equal(root.get("material").get("partModification"), partModification),
                                                            root.get("material").get("materialType").in(MaterialType.ADDITIONAL, MaterialType.ADDITIONAL_USAGE));

        criteriaQuery.where(criteriaBuilder.and(root.get("whSiteId").in(siteIds),
                                                criteriaBuilder.or(releasedMaterials, additionalMaterials),
                                                criteriaBuilder.or(criteriaBuilder.equal(root.get("status"), MaterialLineStatus.STORED),
                                                                   criteriaBuilder.equal(root.get("status"), MaterialLineStatus.READY_TO_STORE))));

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root);
        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        return resultSetQuery.getResultList();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public PageObject findAllChangeId(PageObject pageObject, String assignedMaterialController, String assignedMaterialControllerTeam, List<String> companyCodes) {

        Map<String, JpaAttribute> fieldMap = new HashMap<String, JpaAttribute>();
        fieldMap.put("changeId", new JpaAttribute("materialHeaderVersions.changeId.mtrlRequestVersion", JpaAttributeType.STRINGTYPE));
        fieldMap.put("status", new JpaAttribute("materialHeaderVersions.changeId.status", JpaAttributeType.ENUMTYPE, ChangeIdStatus.class));
        fieldMap.put("receivedDate", new JpaAttribute("materialHeaderVersions.changeId.receivedDate", JpaAttributeType.DATETYPE));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(MaterialHeader.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();

        if (!StringUtils.isEmpty(assignedMaterialController)) {
            Path<String> path = root.get("materialControllerUserId");
            Predicate predicateIn = path.in(assignedMaterialController);
            predicatesRules.add(criteriaBuilder.and(predicateIn));
        }

        if (!StringUtils.isEmpty(assignedMaterialControllerTeam)) {
            Path<String> path = root.get("materialControllerTeam");
            Predicate predicateIn = path.in(assignedMaterialControllerTeam);
            predicatesRules.add(criteriaBuilder.and(predicateIn));
        }

        predicatesRules.add(criteriaBuilder.and(root.get("materialHeaderVersions").get("changeId").get("visibleUi").in(true)));

        Predicate companyCode = criteriaBuilder.in(root.get("materials").get("financeHeader").get("companyCode")).value(companyCodes);
        predicatesRules.add(companyCode);

        CriteriaQuery countCriteriaQueryFromPageObeject = PageObject.buildQuery(criteriaBuilder, criteriaQuery,
                                                                                root.get("materialHeaderVersions").get("changeId"), predicatesRules,
                                                                                pageObject, fieldMap, true);
        Query countQuery = entityManager.createQuery(countCriteriaQueryFromPageObeject);
        Long value = (Long) countQuery.getSingleResult();
        pageObject.setCount(value.intValue());

        CriteriaQuery resultSetCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery,
                                                                                   root.get("materialHeaderVersions").get("changeId"), predicatesRules,
                                                                                   pageObject, fieldMap, false);
        Query queryForResultSets = entityManager.createQuery(resultSetCriteriaQueryFromPageObject);
        queryForResultSets.setFirstResult(pageObject.getFirstResult());
        queryForResultSets.setMaxResults(pageObject.getMaxResult());
        List<ChangeIdDTO> changeIdDTOs = ProcurementHelper.transformChangeIdDTOs(queryForResultSets.getResultList());
        if (changeIdDTOs != null) {
            pageObject.setGridContents(new ArrayList<PageResults>(changeIdDTOs));
        } else {
            pageObject.setGridContents(null);
        }

        return pageObject;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<MaterialHeader> findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId(String referenceId, String buildId, String outboundLocationId) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(MaterialHeader.class);
        List<Expression<Boolean>> predicates = new ArrayList<Expression<Boolean>>();
        predicates.add(criteriaBuilder.isTrue(root.get("active")));

        if (!StringUtils.isEmpty(referenceId)) {
            predicates.add(criteriaBuilder.equal(root.get("referenceId"), referenceId));
        }

        predicates.add(criteriaBuilder.equal(root.get("buildId"), buildId));

        if (!StringUtils.isEmpty(outboundLocationId)) {
            predicates.add(criteriaBuilder.equal(root.get("materialHeaderVersions").get("outboundLocationId"), outboundLocationId));
        }

        predicates.add(criteriaBuilder.and(root.get("requestType").in(RequestType.TESTOBJECT_FIRST_BUILD, RequestType.TESTOBJECT_MR)));

        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root).distinct(true);
        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);
        return resultSetQuery.getResultList();
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<MaterialHeader> findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId(String requestType, String referenceId, String buildId,
            String outboundLocationId, String procureRequestId) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(MaterialHeader.class);
        List<Expression<Boolean>> predicates = new ArrayList<Expression<Boolean>>();
        predicates.add(criteriaBuilder.isTrue(root.get("active")));
        if (!StringUtils.isEmpty(requestType)) {
            predicates.add(criteriaBuilder.equal(root.get("requestType"), RequestType.valueOf(requestType)));
        }
        if (!StringUtils.isEmpty(referenceId)) {
            predicates.add(criteriaBuilder.equal(root.get("referenceId"), referenceId));
        }

        if (!StringUtils.isEmpty(buildId)) {
            predicates.add(criteriaBuilder.equal(root.get("buildId"), buildId));
        } else if (!StringUtils.isEmpty(procureRequestId)) {
            predicates.add(criteriaBuilder.equal(root.get("accepted").get("changeId").get("procureRequestId"), procureRequestId));
        }

        if (!StringUtils.isEmpty(outboundLocationId)) {
            predicates.add(criteriaBuilder.equal(root.get("accepted").get("outboundLocationId"), outboundLocationId));
        }

        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root).distinct(true);
        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);
        return resultSetQuery.getResultList();
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<MaterialHeader> findMaterialHeaderByMtrlRequestId(String mtrlRequestId, String referenceId) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(MaterialHeader.class);
        List<Expression<Boolean>> predicates = new ArrayList<Expression<Boolean>>();
        if (!StringUtils.isEmpty(mtrlRequestId)) {
            predicates.add(criteriaBuilder.equal(root.get("mtrlRequestId"), mtrlRequestId));
        }

        if (!StringUtils.isEmpty(referenceId)) {
            predicates.add(criteriaBuilder.equal(root.get("referenceId"), referenceId));
        }

        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root);
        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);
        return resultSetQuery.getResultList();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public PageObject getRequestHeaders(PageObject pageObject, Boolean assignedMaterialController, List<String> companyCodes) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(MaterialHeader.class);

        pageObject.setDefaultSort("assignedMaterialControllerId", "desc");

        List<Predicate> predicatesRules = new ArrayList<Predicate>();

        predicatesRules.add(criteriaBuilder.isTrue(root.get("active")));
        predicatesRules.add(criteriaBuilder.isNotNull(root.get("accepted")));
        if (assignedMaterialController != null) {
            if (Boolean.TRUE.equals(assignedMaterialController)) {
                predicatesRules.add(criteriaBuilder.and(criteriaBuilder.isNotNull(root.get("materialControllerUserId"))));
            } else {
                predicatesRules.add(criteriaBuilder.and(root.get("materialControllerUserId").isNull()));
            }
        }
        predicatesRules.add(criteriaBuilder.in(root.get("materials").get("financeHeader").get("companyCode")).value(companyCodes));
        predicatesRules.add(criteriaBuilder.or(root.get("materials").get("procureLine").get("status")
                                                   .in(ProcureLineStatus.START, ProcureLineStatus.CANCELLED, ProcureLineStatus.WAIT_TO_PROCURE),
                                               criteriaBuilder.isNull(root.get("materials").get("procureLine"))));

        CriteriaQuery criteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject,
                                                                          REQUEST_HEADER_FIELDS, true);
        Query countQuery = entityManager.createQuery(criteriaQueryFromPageObject);
        pageObject.setCount(((Long) countQuery.getSingleResult()).intValue());

        CriteriaQuery resultSetCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject,
                                                                                   REQUEST_HEADER_FIELDS, false);
        Query resultSetQuery = entityManager.createQuery(resultSetCriteriaQueryFromPageObject);
        resultSetQuery.setFirstResult(pageObject.getFirstResult());
        resultSetQuery.setMaxResults(pageObject.getMaxResult());
        List<MaterialHeaderDTO> materialHeaderDTOs = null;
        List<MaterialHeader> materialHeaders = resultSetQuery.getResultList();
        if (materialHeaders != null && !materialHeaders.isEmpty()) {
            materialHeaderDTOs = new ArrayList<MaterialHeaderDTO>();
            materialHeaderDTOs.addAll(procurementDtoTransformer.transformAsDTO(materialHeaders));
        }

        if (materialHeaderDTOs != null) {
            pageObject.setGridContents(new ArrayList<PageResults>(materialHeaderDTOs));
        } else {
            pageObject.setGridContents(null);
        }

        return pageObject;
    }

    @Override
    public void addMaterial(Material material) {
        getEntityManager().persist(material);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Material> getMaterialsForHeaderIds(List<MaterialHeader> requestsToAssign) {
        Query query = getEntityManager().createNamedQuery("getMaterialsForHeaderIds");
        query.setParameter("idList", ProcurementHelper.getValues(requestsToAssign));
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Material> findAllMaterials() {
        Query query = getEntityManager().createNamedQuery("findAllMaterials");
        return query.getResultList();
    }

    @Override
    public Material findMaterialById(Long materialOid) {
        return getEntityManager().find(Material.class, materialOid);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Material> findMaterialsByProcureLineId(long procureLineOid) {
        Query query = getEntityManager().createNamedQuery("findMaterialsByProcureLineId");
        query.setParameter("procureLineOid", procureLineOid);
        return query.getResultList();
    }

    @Override
    public void addFinanceHeader(FinanceHeader financeHeader) {
        getEntityManager().persist(financeHeader);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public PageObject findMaterialsByMaterialHeaderId(long materialHeaderId, PageObject pageObject) throws GloriaApplicationException {

        Map<String, JpaAttribute> fieldMap = new HashMap<String, JpaAttribute>();
        fieldMap.put("partAffiliation", new JpaAttribute("partAffiliation", JpaAttributeType.STRINGTYPE));
        fieldMap.put("partNumber", new JpaAttribute("partNumber", JpaAttributeType.STRINGTYPE));
        fieldMap.put("partVersion", new JpaAttribute("partVersion", JpaAttributeType.STRINGTYPE));
        fieldMap.put("partName", new JpaAttribute("partName", JpaAttributeType.STRINGTYPE));
        fieldMap.put("partModification", new JpaAttribute("partModification", JpaAttributeType.STRINGTYPE));
        fieldMap.put("quantity", new JpaAttribute("quantity", JpaAttributeType.NUMBERTYPE));
        fieldMap.put("unitOfMeasure", new JpaAttribute("unitOfMeasure", JpaAttributeType.STRINGTYPE));
        fieldMap.put("functionGroup", new JpaAttribute("functionGroup", JpaAttributeType.STRINGTYPE));
        fieldMap.put("functionGroupSuffix", new JpaAttribute("functionGroupSuffix", JpaAttributeType.STRINGTYPE));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(Material.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.equal(root.get("materialHeader").get("materialHeaderOid"), materialHeaderId));

        CriteriaQuery countCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject, fieldMap,
                                                                               true);
        Query countQuery = entityManager.createQuery(countCriteriaQueryFromPageObject);
        pageObject.setCount(((Long) countQuery.getSingleResult()).intValue());

        CriteriaQuery resultsetOfCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject,
                                                                                     fieldMap, false);
        Query resultSetQuery = entityManager.createQuery(resultsetOfCriteriaQueryFromPageObject);
        resultSetQuery.setFirstResult(pageObject.getFirstResult());
        resultSetQuery.setMaxResults(pageObject.getMaxResult());
        List<MaterialDTO> materialDTOs = procurementDtoTransformer.transformAsMaterialDTOs(resultSetQuery.getResultList());
        if (materialDTOs != null) {
            pageObject.setGridContents(new ArrayList<PageResults>(materialDTOs));
        } else {
            pageObject.setGridContents(null);
        }

        return pageObject;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public PageObject findMaterialsByStatusAndResponsability(ProcureLineStatus procureLineStatus, ProcureResponsibility procureResponsibility,
            String assignedMaterialControllerId, String assignedMaterialControllerTeam, String procureForwardedId, PageObject pageObject, String materialType,
            List<String> companyCodes) {
        Map<String, JpaAttribute> fieldMap = new HashMap<String, JpaAttribute>();

        fieldMap.put("mtrlRequestVersion", new JpaAttribute("add.mtrlRequestVersion", JpaAttributeType.STRINGTYPE));
        fieldMap.put("referenceId", new JpaAttribute("materialHeader.referenceId", JpaAttributeType.STRINGTYPE));
        fieldMap.put("partNumber", new JpaAttribute("partNumber", JpaAttributeType.STRINGTYPE));
        fieldMap.put("partVersion", new JpaAttribute("partVersion", JpaAttributeType.STRINGTYPE));
        fieldMap.put("partName", new JpaAttribute("partName", JpaAttributeType.STRINGTYPE));
        fieldMap.put("partModification", new JpaAttribute("partModification", JpaAttributeType.STRINGTYPE));
        fieldMap.put("quantity", new JpaAttribute("quantity", JpaAttributeType.NUMBERTYPE));
        fieldMap.put("pPartNumber", new JpaAttribute("procureLine.pPartNumber", JpaAttributeType.STRINGTYPE));
        fieldMap.put("pPartAffiliation", new JpaAttribute("procureLine.pPartAffiliation", JpaAttributeType.STRINGTYPE));
        fieldMap.put("modularHarness", new JpaAttribute("modularHarness", JpaAttributeType.STRINGTYPE));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(Material.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        if (procureLineStatus != null) {
            Path<String> path = root.get("procureLine").get("status");
            Predicate predicateIn = path.in(procureLineStatus);
            predicatesRules.add(criteriaBuilder.and(predicateIn));
        }

        Path<String> pathOfResponsibility = root.get("procureLine").get("responsibility");
        Predicate predicateForRespIn = pathOfResponsibility.in(procureResponsibility);
        predicatesRules.add(criteriaBuilder.and(predicateForRespIn));

        if (!StringUtils.isEmpty(assignedMaterialControllerId)) {
            predicatesRules.add(criteriaBuilder.equal(root.get("materialHeader").get("materialControllerUserId"), assignedMaterialControllerId));
        }

        if (!StringUtils.isEmpty(assignedMaterialControllerTeam)) {
            predicatesRules.add(criteriaBuilder.equal(root.get("materialHeader").get("materialControllerTeam"), assignedMaterialControllerTeam));
        }

        if (!StringUtils.isEmpty(procureForwardedId)) {
            predicatesRules.add(criteriaBuilder.equal(root.get("procureLine").get("forwardedUserId"), procureForwardedId));
        }

        if (materialType != null) {
            List<String> materialTypeAsString = GloriaFormateUtil.getValuesAsString(materialType);
            List<MaterialType> materialTypes = new ArrayList<MaterialType>();
            for (String matType : materialTypeAsString) {
                materialTypes.add(MaterialType.valueOf(matType));
            }

            Path<String> path = root.get("materialType");
            Predicate materialTypePredicate = path.in(materialTypes);

            predicatesRules.add(criteriaBuilder.and(materialTypePredicate));
        }

        predicatesRules.add(root.get("financeHeader").get("companyCode").in(companyCodes));

        CriteriaQuery criteriaQueryForPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject, fieldMap, true);
        Query countQuery = entityManager.createQuery(criteriaQueryForPageObject);
        pageObject.setCount(((Long) countQuery.getSingleResult()).intValue());

        CriteriaQuery resultSetCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject, fieldMap,
                                                                                   false);
        Query resultSetQuery = entityManager.createQuery(resultSetCriteriaQueryFromPageObject);
        resultSetQuery.setFirstResult(pageObject.getFirstResult());
        resultSetQuery.setMaxResults(pageObject.getMaxResult());
        List<MaterialDTO> materialDTOs = procurementDtoTransformer.transformAsMaterialDTOs(resultSetQuery.getResultList());

        if (materialDTOs != null) {
            pageObject.setGridContents(new ArrayList<PageResults>(materialDTOs));
        } else {
            pageObject.setGridContents(null);
        }

        return pageObject;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Material findMaterialByProcureLinkId(long procureLinkId) {
        Material material = null;
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(Material.class);

        criteriaQuery.where(criteriaBuilder.equal(root.get("procureLinkId"), procureLinkId));

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root);
        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);
        List<Material> materials = resultSetQuery.getResultList();
        if (materials != null && !materials.isEmpty()) {
            material = materials.get(0);
        }
        return material;
    }

    /*
     * this method tries to get the related FinanceHeader based on the Mateial hEADER STORED IN mATERIAL
     */
    @Override
    public FinanceHeader findFinanceHeaderByMaterialHeaderOid(long materialHeaderOid) {
        return RequestHeaderRepositoryBeanHelper.findFinanceHeaderByMaterialHeaderOid(materialHeaderOid, this.getEntityManager());
    }

    @Override
    public FinanceHeader findFinanceHeaderById(long financeHeaderXOid) {
        return getEntityManager().find(FinanceHeader.class, financeHeaderXOid);
    }

    @Override
    public PageObject getProjects(PageObject pageObject, List<String> companyCodes, String projectId) {
        List<ReportFilterDTO> reportFilterDTOs = new ArrayList<ReportFilterDTO>();
        List<String> projectIds = this.getProjects(pageObject, projectId);
        if (projectIds != null) {
            for (String aProjectId : projectIds) {
                ReportFilterDTO reportFilterDTO = new ReportFilterDTO(aProjectId, aProjectId);
                reportFilterDTOs.add(reportFilterDTO);
            }
        }
        if (!reportFilterDTOs.isEmpty()) {
            pageObject.setCount(reportFilterDTOs.size());
            pageObject.setGridContents(new ArrayList<PageResults>(reportFilterDTOs));
        } else {
            pageObject.setGridContents(null);
        }
        return pageObject;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<String> getProjects(PageObject pageObject, String projectId) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
        Root root = criteriaQuery.from(FinanceHeader.class);
        List<Expression<Boolean>> predicates = new ArrayList<Expression<Boolean>>();
        if (!StringUtils.isEmpty(projectId)) {
            String projectIdStr = projectId.replaceFirst("\\*", WILDCARD).concat(WILDCARD).toLowerCase();
            predicates.add(criteriaBuilder.and(criteriaBuilder.like(criteriaBuilder.lower(root.get("projectId")), projectIdStr)));
        }
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root.get("projectId")).distinct(true);
        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);
        resultSetQuery.setMaxResults(pageObject.getResultsPerPage());
        return resultSetQuery.getResultList();
    }

    @Override
    @SuppressWarnings("rawtypes")
    public PageObject getWbsElements(PageObject pageObject, String wbsCode) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root<FinanceHeader> root = criteriaQuery.from(FinanceHeader.class);
        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        Path<String> projectIdPath = root.get("wbsCode");

        if (wbsCode != null) {
            predicatesRules.add(criteriaBuilder.like(projectIdPath, wbsCode.replaceAll("\\*", WILDCARD).concat(WILDCARD)));
            criteriaQuery.where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));
        }
        List<Selection> columnSelections = new ArrayList<Selection>();
        columnSelections.add(root.get("wbsCode"));
        criteriaQuery.multiselect(columnSelections.toArray(new Selection[columnSelections.size()]));
        criteriaQuery.distinct(true).orderBy(criteriaBuilder.asc(root.get("wbsCode")));
        TypedQuery<Tuple> query = getEntityManager().createQuery(criteriaQuery);
        List<Tuple> tuples = query.getResultList();
        List<ReportFilterDTO> reportProjectDTOs = new ArrayList<ReportFilterDTO>();
        for (Tuple tuple : tuples) {
            String aProjectId = (String) tuple.get(0);
            if (StringUtils.isNotBlank(aProjectId)) {
                ReportFilterDTO reportFilterDTO = new ReportFilterDTO(aProjectId, aProjectId);
                reportProjectDTOs.add(reportFilterDTO);
            }
        }
        if (reportProjectDTOs != null && !reportProjectDTOs.isEmpty()) {
            pageObject.setCount(reportProjectDTOs.size());
            pageObject.setGridContents(new ArrayList<PageResults>(reportProjectDTOs));
        } else {
            pageObject.setGridContents(null);
        }
        return pageObject;
    }

    @Override
    public ChangeId save(ChangeId changeId) {
        ChangeId toSave = changeId;
        if (changeId.getChangeIdOid() < 1) {
            getEntityManager().persist(changeId);
        } else {
            toSave = getEntityManager().merge(changeId);
        }
        return toSave;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<MaterialHeader> findAll() {
        Query query = getEntityManager().createNamedQuery("findAllMaterialHeadersWithMaterialsById");
        return query.getResultList();
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public MaterialHeader findById(Long id) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(MaterialHeader.class);
        if (id != null && id > 0) {
            criteriaQuery.where(criteriaBuilder.equal(root.get("materialHeaderOid"), id));
        }

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root);

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        List<MaterialHeader> headers = resultSetQuery.getResultList();
        if (headers != null && !headers.isEmpty()) {
            return headers.get(0);
        }
        return null;
    }

    @Override
    public void updateMaterial(Material material) {
        getEntityManager().merge(material);
    }

    @Override
    public void deleteMaterial(Material material) {
        getEntityManager().remove(material);
    }

    // Material Procure repo

    @Override
    @SuppressWarnings("unchecked")
    public Material getMaterialWithMaterialLinesById(long materialOid) {
        Query query = getEntityManager().createNamedQuery("findMaterialWithMaterialLinesById");
        query.setParameter("materialOID", materialOid);
        List<Material> materials = query.getResultList();
        if (materials != null && !materials.isEmpty()) {
            return materials.get(0);
        }
        return null;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<Material> findMaterialByRequisitionId(String requisitionId) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(Material.class);
        List<Expression<Boolean>> predicates = new ArrayList<Expression<Boolean>>();
        if (!StringUtils.isEmpty(requisitionId)) {
            predicates.add(criteriaBuilder.equal(root.get("procureLine").get("requisitionId"), requisitionId));
        }
        predicates.add(criteriaBuilder.notEqual(root.get("status"), MaterialStatus.REMOVED));
        predicates.add(criteriaBuilder.notEqual(root.get("materialType"), MaterialType.USAGE_REPLACED));

        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root);
        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);
        return resultSetQuery.getResultList();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public PageObject getMaterialLinesForWarehouse(PageObject pageObject, String materialLineStatus, boolean calculateStockBalance, String whSiteId,
            Boolean suggestBinLocation, String partNo, String transportLabel, String userId, String partAffiliation, String partVersion,
            String partModification, String partName) {
        Map<String, JpaAttribute> materialLineFields = new HashMap<String, JpaAttribute>();
        materialLineFields.put("pPartNumber", new JpaAttribute("material.partNumber", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("pPartName", new JpaAttribute("material.partName", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("pPartModification", new JpaAttribute("material.partModification", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("transportLabel", new JpaAttribute("transportLabelCode", JpaAttributeType.STRINGTYPE));

        materialLineFields.put("partAffiliation", new JpaAttribute("material.partAffiliation", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("partVersion", new JpaAttribute("material.partVersion", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("partModification", new JpaAttribute("material.partModification", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("partName", new JpaAttribute("material.partName", JpaAttributeType.STRINGTYPE));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(MaterialLine.class);
        Join materialJoin = root.join("material", JoinType.LEFT);

        List<Predicate> predicatesRules = buildPredicates(materialLineStatus, whSiteId, partNo, transportLabel, userId, criteriaBuilder, root, partAffiliation,
                                                          partVersion, partModification, partName);

        CriteriaQuery criteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject,
                                                                          materialLineFields, true);

        criteriaQueryFromPageObject.groupBy(materialJoin.get("partNumber"), materialJoin.get("partName"), materialJoin.get("partAffiliation"),
                                            materialJoin.get("partModification"), materialJoin.get("partVersion"), root.get("transportLabelCode"),
                                            root.get("whSiteId"));

        // need to override the order by outside of PageObject
        updateCriteriaWithOrderbyClause(pageObject, criteriaQueryFromPageObject, criteriaBuilder, materialJoin);

        Query countQuery = entityManager.createQuery(criteriaQueryFromPageObject);
        if (countQuery.getResultList() != null) {
            pageObject.setCount(countQuery.getResultList().size());
        } else {
            pageObject.setCount(0);
        }

        Selection<MaterialLineDTO> selection = criteriaBuilder.construct(MaterialLineDTO.class, criteriaBuilder.max(root.get("materialLineOID")),
                                                                         criteriaBuilder.max(materialJoin.get("materialOID")), materialJoin.get("partNumber"),
                                                                         materialJoin.get("partVersion"), materialJoin.get("partName"),
                                                                         materialJoin.get("partAffiliation"), materialJoin.get("partModification"),
                                                                         criteriaBuilder.sum(root.get("quantity")), root.get("transportLabelCode"),
                                                                         root.get("whSiteId"));

        CriteriaQuery resultSetCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, selection, predicatesRules, pageObject,
                                                                                   materialLineFields, false);
        resultSetCriteriaQueryFromPageObject.distinct(false);
        resultSetCriteriaQueryFromPageObject.groupBy(materialJoin.get("partNumber"), materialJoin.get("partName"), materialJoin.get("partAffiliation"),
                                                     materialJoin.get("partModification"), materialJoin.get("partVersion"), root.get("transportLabelCode"),
                                                     root.get("whSiteId"));

        // need to override the order by outside of PageObject
        updateCriteriaWithOrderbyClause(pageObject, resultSetCriteriaQueryFromPageObject, criteriaBuilder, materialJoin);

        Query resultSetQuery = entityManager.createQuery(resultSetCriteriaQueryFromPageObject);
        resultSetQuery.setFirstResult(pageObject.getFirstResult());
        resultSetQuery.setMaxResults(pageObject.getMaxResult());

        List<MaterialLineDTO> materialLineDTOs = resultSetQuery.getResultList();

        if (materialLineDTOs != null && !materialLineDTOs.isEmpty()) {
            if (calculateStockBalance) {
                for (MaterialLineDTO materialLineDTO : materialLineDTOs) {
                    materialLineDTO.setStatus(MaterialLineStatus.READY_TO_STORE.toString());
                    materialLineDTO.setStockBalance(calculateStockBalance(materialLineDTO.getpPartNumber(), materialLineDTO.getpPartVersion(),
                                                                          materialLineDTO.getWhSiteId(), materialLineDTO.getpPartModification(),
                                                                          materialLineDTO.getpPartAffiliation()));

                    BinLocation binLocation = suggestBinLocation(materialLineDTO.getpPartNumber(), materialLineDTO.getpPartVersion(),
                                                                 materialLineDTO.getWhSiteId(), materialLineDTO.getpPartModification(),
                                                                 materialLineDTO.getpPartAffiliation());
                    if (binLocation != null) {
                        materialLineDTO.setSuggestedBinLocation(binLocation.getCode());
                        materialLineDTO.setSuggestedBinLocationId(binLocation.getId());
                    }
                }
                pageObject.setGridContents(new ArrayList<PageResults>(materialLineDTOs));
            }
        }
        return pageObject;
    }

    @SuppressWarnings({ "rawtypes" })
    private void updateCriteriaWithOrderbyClause(PageObject pageObject, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder, Join materialJoin) {
        // need to override the order clause outside of PageObject, to avoid query syntax issues
        if (!StringUtils.isEmpty(pageObject.getSortBy())) {
            if ("asc".equalsIgnoreCase(pageObject.getSortOrder())) {
                criteriaQuery.orderBy(criteriaBuilder.asc(materialJoin.get("partNumber")));
            } else {
                criteriaQuery.orderBy(criteriaBuilder.desc(materialJoin.get("partNumber")));
            }
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<Predicate> buildPredicates(String materialLineStatus, String whSiteId, String partNo, String transportLabel, String userId,
            CriteriaBuilder criteriaBuilder, Root root, String partAffiliation, String partVersion, String partModification, String partName) {
        List<Predicate> predicatesRules = new ArrayList<Predicate>();

        if (!StringUtils.isEmpty(materialLineStatus)) {
            Path<String> path = root.get("status");
            Predicate predicateIn = path.in(GloriaFormateUtil.getValuesAsEnums(materialLineStatus, MaterialLineStatus.class));
            predicatesRules.add(criteriaBuilder.and(predicateIn));
        }

        if (!StringUtils.isEmpty(whSiteId)) {
            predicatesRules.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("whSiteId")), whSiteId.toLowerCase()));
        }

        if (!StringUtils.isEmpty(partNo)) {
            predicatesRules.add(criteriaBuilder.equal(root.get("material").get("partNumber"), partNo));
        }
        //
        if (!StringUtils.isEmpty(partAffiliation)) {
            predicatesRules.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("material").get("partAffiliation")), partAffiliation.toLowerCase()));
        }
        if (!StringUtils.isEmpty(partVersion)) {
            predicatesRules.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("material").get("partVersion")), partVersion.toLowerCase()));
        }
        if (!StringUtils.isEmpty(partModification)) {
            predicatesRules.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("material").get("partModification")), partModification.toLowerCase()));
        }
        if (!StringUtils.isEmpty(partName)) {
            predicatesRules.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("material").get("partName")), partName.toLowerCase()));
        }

        if (!StringUtils.isEmpty(transportLabel)) {
            predicatesRules.add(criteriaBuilder.equal(root.get("transportLabelCode"), transportLabel));
        }

        predicatesRules.add(criteriaBuilder.equal(root.get("zoneType"), ZoneType.TO_STORE));

        if (!StringUtils.isEmpty(userId)) {
            predicatesRules.add(criteriaBuilder.or(criteriaBuilder.equal(root.get("reservedUserId"), userId),
                                                   criteriaBuilder.isNull(root.get("reservedUserId"))));
        }
        return predicatesRules;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public PageObject getMaterialLines(PageObject pageObject, String expirationDate, String expirationTo, String expirationFrom, Boolean allExpired)
            throws GloriaApplicationException {
        Map<String, JpaAttribute> materialLineFields = RequestHeaderRepositoryBeanHelper.getMaterialLinesFields();
        pageObject.setDefaultSort("id", "desc");

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(MaterialLine.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();

        predicatesRules.add(criteriaBuilder.not(root.get("materialOwner").get("materialType").in(MaterialType.USAGE_REPLACED)));

        try {
            predicatesRules.addAll(RequestHeaderRepositoryBeanHelper.handleDates(expirationDate, expirationTo, expirationFrom, allExpired, criteriaBuilder,
                                                                                 root.get("expirationDate")));
        } catch (ParseException e) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_DATE_FORMAT, "Failed to parse date.");
        }
        predicatesRules.add(criteriaBuilder.or(criteriaBuilder.not(root.get("materialOwner")
                                                                       .get("add")
                                                                       .get("status")
                                                                       .in(ChangeIdStatus.WAIT_CONFIRM, ChangeIdStatus.REJECTED, ChangeIdStatus.CANCEL_REJECTED)),
                                               root.get("materialOwner").get("add").isNull()));

        predicatesRules.add(criteriaBuilder.not(root.get("materialOwner").get("status").in(MaterialStatus.ADD_NOT_ACCEPTED)));
        predicatesRules.add(criteriaBuilder.not(root.get("status").in(MaterialLineStatus.REMOVED_DB)));

        if (StringUtils.isEmpty(pageObject.getPredicate("procureUserId")) && StringUtils.isEmpty(pageObject.getPredicate("procureTeam"))) {
            GloriaUser gloriaUser = teamRepository.findUserByUserId(pageObject.getPredicate("userId"));
            Set<String> allUserSites = UserRoleHelper.getAllUserSites(gloriaUser, commonServices);
            Set<String> allUserCompanyCodes = UserRoleHelper.getAllUserCompanyCodeCodes(gloriaUser, commonServices);

            predicatesRules.add(criteriaBuilder.or(root.get("materialOwner").get("financeHeader").get("companyCode").in(allUserCompanyCodes),
                                                   root.get("whSiteId").in(allUserSites),
                                                   root.get("finalWhSiteId").in(allUserSites),
                                                   criteriaBuilder.and(root.get("requestGroup").get("requestList").get("whSiteId").in(allUserSites),
                                                                       root.get("status").in(MaterialLineStatus.IN_TRANSFER),
                                                                       root.get("requestGroup").get("requestList").get("shipmentType")
                                                                           .in(ShipmentType.TRANSFER))));

        }

        predicatesRules.add(criteriaBuilder.or(criteriaBuilder.notEqual(root.get("materialOwner").get("procureLine").get("responsibility"),
                                                                        ProcureResponsibility.BUILDSITE),
                                               root.get("materialOwner").get("procureLine").get("responsibility").isNull(),
                                               root.get("materialOwner").get("procureLine").isNull()));

        pageObject.setCount(((Long) entityManager.createQuery(PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject,
                                                                                    materialLineFields, true)).getSingleResult()).intValue());

        // below query is used to fetch all the material line ids required for a page and the Object id list is passed to fetch data for next query
        criteriaQuery.select(root.get("materialLineOID"));
        Query materialLineOidRsetQuery = entityManager.createQuery(PageObject.buildQuery(criteriaBuilder, criteriaQuery, criteriaQuery.getSelection(),
                                                                                         predicatesRules, pageObject, materialLineFields, false));
        materialLineOidRsetQuery.setFirstResult(pageObject.getFirstResult());
        materialLineOidRsetQuery.setMaxResults(pageObject.getMaxResult());

        List<Long> materialLineOids = materialLineOidRsetQuery.getResultList();

        // below query uses above list of material ids to fetch data
        predicatesRules.add(criteriaBuilder.and(root.get("materialLineOID").in(materialLineOids)));
        Query resultSetQuery = entityManager.createQuery(PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject,
                                                                               materialLineFields, false));
        List<MaterialLine> materialLines = resultSetQuery.getResultList();

        if (materialLines != null) {
            pageObject.setGridContents(new ArrayList<PageResults>(MaterialTransformHelper.transformAsMaterialLineDTOs(materialLines)));
        }
        return pageObject;
    }

    private MaterialLineDTO transformAsMaterialLineDTO(MaterialLine materialLine, boolean suggestBinLocation) {
        String suggestedBinLocation = null;
        if (suggestBinLocation) {
            Material material = materialLine.getMaterial();
            BinLocation binLocation = suggestBinLocation(material.getPartNumber(), material.getPartVersion(), materialLine.getWhSiteId(),
                                                         material.getPartModification(), material.getPartAffiliation());
            if (binLocation != null) {
                suggestedBinLocation = binLocation.getCode();
            }
        }
        return MaterialTransformHelper.transformAsMaterialLineDTO(materialLine, suggestedBinLocation);
    }

    @Override
    public MaterialLine findMaterialLineById(Long materialLineId, boolean calculateStockBalance, List<String> whSiteIds) {
        MaterialLine materialLine = getEntityManager().find(MaterialLine.class, materialLineId);
        if (materialLine != null) {
            ProcureLine procureLine = materialLine.getMaterial().getProcureLine();
            if (procureLine != null) {
                materialLine.setStockBalance(calculateStockBalance(procureLine.getpPartNumber(), procureLine.getpPartVersion(), whSiteIds,
                                                                   procureLine.getpPartModification(), procureLine.getpPartAffiliation()));
            }
        }
        return materialLine;
    }

    @Override
    public MaterialLine updateMaterialLine(MaterialLine materialLine) {
        if (materialLine.getMaterialLineOID() == 0) {
            getEntityManager().persist(materialLine);
        } else {
            getEntityManager().merge(materialLine);
        }
        return materialLine;
    }

    @Override
    public MaterialLine findMaterialLineByPartInfoAndStatus(String pPartNumber, String pPartVersion, List<MaterialLineStatus> statuses, String whSiteId,
            String pPartModification, String pPartAffiliation) {
        TypedQuery<MaterialLine> query = getEntityManager().createNamedQuery("findMaterialLinesByPartInfoAndStatus", MaterialLine.class);
        query.setParameter("pPartNumber", pPartNumber);
        query.setParameter("pPartVersion", pPartVersion);
        query.setParameter("pPartModification", pPartModification);
        query.setParameter("pPartAffiliation", pPartAffiliation);
        query.setParameter("status", statuses);
        if (StringUtils.isNotEmpty(whSiteId)) {
            query.setParameter("whSiteId", whSiteId);
        } else {
            query.setParameter("whSiteId", null);
        }
        List<MaterialLine> materialLines = query.getResultList();
        return getMaterialLineByBinLocation(materialLines);
    }

    public MaterialLine getMaterialLineByBinLocation(List<MaterialLine> materialLines) {
        MaterialLine lineToReturn = null;
        if (materialLines != null && !materialLines.isEmpty()) {
            for (MaterialLine line : materialLines) {
                String binLocationCode = line.getBinLocationCode();
                if (!StringUtils.isEmpty(binLocationCode)) {
                    if (lineToReturn == null) {
                        lineToReturn = line;
                    } else {
                        int i = lineToReturn.getBinLocationCode().compareTo(line.getBinLocationCode());
                        if (i > 0) {
                            lineToReturn = line;
                        }
                    }
                }
            }
        }
        return lineToReturn;
    }

    /*
     * This method tryies to access the Material Line specific partnumbers and partversion available in a given site. It then selects the MaterialLine which has
     * the "lowest" binLocationCode and then searches and returns that BinLocation
     */

    @Override
    public BinLocation suggestBinLocation(String pPartNumber, String pPartVersion, String whSiteId, String partModification, String partAffiliation) {
        BinLocation binLocation = null;

        List<MaterialLineStatus> statuses = new ArrayList<MaterialLineStatus>();
        statuses.add(MaterialLineStatus.STORED);
        statuses.add(MaterialLineStatus.MARKED_INSPECTION);
        statuses.add(MaterialLineStatus.REQUESTED);
        statuses.add(MaterialLineStatus.DEVIATED);

        MaterialLine materialLine = findMaterialLineByPartInfoAndStatus(pPartNumber, pPartVersion, statuses, whSiteId, partModification, partAffiliation);
        if (materialLine != null) {
            Placement placement = warehouseServices.getPlacement(materialLine.getPlacementOID());
            if (placement != null) {
                binLocation = placement.getBinLocation();
            }
        }
        return binLocation;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Material> findMaterialByOrderLineId(Long orderlineId) {
        Query query = getEntityManager().createNamedQuery("findMaterialByOrderLineId");
        query.setParameter("orderlineId", orderlineId);
        List<MaterialType> materialTypes = new ArrayList<MaterialType>();
        materialTypes.add(MaterialType.ADDITIONAL);
        materialTypes.add(MaterialType.USAGE_REPLACED);
        query.setParameter("materialTypes", materialTypes);
        return query.getResultList();
    }

    @Override
    public MaterialLine findMaterialLineById(Long materialLineId) {
        return getEntityManager().find(MaterialLine.class, materialLineId);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public PageObject getRequestGroup(PageObject pageObject, String whSiteId, String userId) {

        Map<String, JpaAttribute> materialLineFields = new HashMap<String, JpaAttribute>();
        materialLineFields.put("status", new JpaAttribute("pickList.status", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("zoneId", new JpaAttribute("zoneId", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("priority", new JpaAttribute("requestList.priority", JpaAttributeType.NUMBERTYPE));
        materialLineFields.put("projectId", new JpaAttribute("projectId", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("referenceGroup", new JpaAttribute("referenceGroup", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("referenceId", new JpaAttribute("referenceId", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("changeRequestIds", new JpaAttribute("changeRequestIds", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("requiredDeliveryDate", new JpaAttribute("requestList.requiredDeliveryDate", JpaAttributeType.DATETYPE));
        materialLineFields.put("outboundLocationId", new JpaAttribute("requestList.deliveryAddressId,requestList.deliveryAddressName",
                                                                      JpaAttributeType.STRINGTYPE));
        materialLineFields.put("outboundLocationName", new JpaAttribute("requestList.deliveryAddressName", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("requesterUserId", new JpaAttribute("requestList.requestUserId", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("requesterUserName", new JpaAttribute("requestList.requesterUserName", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("pickListCode", new JpaAttribute("pickList.code", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("pulledByUserId", new JpaAttribute("pickList.pulledByUserId", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("requestListID", new JpaAttribute("requestList.requestListOid", JpaAttributeType.NUMBERTYPE));
        materialLineFields.put("reservedUserId", new JpaAttribute("pickList.reservedUserId", JpaAttributeType.STRINGTYPE));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(RequestGroup.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();

        if (!StringUtils.isEmpty(whSiteId)) {
            predicatesRules.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("requestList").get("whSiteId")), whSiteId.toLowerCase()));
        }

        Path<String> path = root.get("materialLines").get("status");
        Predicate materialLineStatus = path.in(MaterialLineStatus.REQUESTED);
        predicatesRules.add(criteriaBuilder.and(materialLineStatus));

        predicatesRules.add(criteriaBuilder.notEqual(root.get("requestList").get("status"), RequestListStatus.CREATED));

        CriteriaQuery criteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject,
                                                                          materialLineFields, true);
        Query countQuery = entityManager.createQuery(criteriaQueryFromPageObject);
        pageObject.setCount(((Long) countQuery.getSingleResult()).intValue());

        CriteriaQuery resultSetCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject,
                                                                                   materialLineFields, false);
        Query resultSetQuery = entityManager.createQuery(resultSetCriteriaQueryFromPageObject);
        resultSetQuery.setFirstResult(pageObject.getFirstResult());
        resultSetQuery.setMaxResults(pageObject.getMaxResult());
        List<RequestGroup> requestGroups = resultSetQuery.getResultList();

        List<RequestGroupDTO> listOfRequestGroupDto = MaterialTransformHelper.transformAsRequestGroupDTOs(requestGroups);

        if (listOfRequestGroupDto != null) {
            pageObject.setGridContents(new ArrayList<PageResults>(listOfRequestGroupDto));
        } else {
            pageObject.setGridContents(null);
        }

        return pageObject;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<RequestGroup> getAllRequestGroups() {
        Query query = getEntityManager().createNamedQuery("findAllRequestGroups");
        return query.getResultList();
    }

    @Override
    public RequestList save(RequestList instanceToSave) {
        RequestList toSave = instanceToSave;
        if (instanceToSave.getRequestListOid() < 1) {
            getEntityManager().persist(instanceToSave);
        } else {
            toSave = getEntityManager().merge(instanceToSave);
        }
        return toSave;
    }

    @Override
    public PickList findPickListById(Long pickListId) {
        Query query = getEntityManager().createNamedQuery("findPickListById");
        query.setParameter("pickListId", pickListId);
        List<PickList> pickLists = query.getResultList();
        if (pickLists != null && !pickLists.isEmpty()) {
            return pickLists.get(0);
        }
        return null;
    }

    @Override
    public PickList updatePickList(PickList pickList) {
        getEntityManager().merge(pickList);
        return pickList;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public PageObject findMaterialLineBypickListIdAndStatus(PageObject pageObject, long pickListId, String status, boolean suggestBinLocation)
            throws GloriaApplicationException {
        Map<String, JpaAttribute> materialLineFields = new HashMap<String, JpaAttribute>();
        materialLineFields.put("pPartNumber", new JpaAttribute("material.partNumber", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("pPartName", new JpaAttribute("material.partName", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("binLocationCode", new JpaAttribute("binLocationCode", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("sortBy", new JpaAttribute("binLocationCode,material.partNumber", JpaAttributeType.STRINGTYPE));

        // set defaults
        pageObject.setDefaultSort("sortBy", "asc");

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(MaterialLine.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();

        if (!StringUtils.isEmpty(status)) {
            Path<String> path = root.get("status");
            Predicate predicateIn = path.in(EnumSet.of(MaterialLineStatus.valueOf(status)));
            predicatesRules.add(criteriaBuilder.and(predicateIn));
        }

        predicatesRules.add(criteriaBuilder.equal(root.get("pickList").get("pickListOid"), pickListId));
        predicatesRules.add(criteriaBuilder.notEqual(root.get("status"), MaterialLineStatus.MISSING));

        // criteria for count
        CriteriaQuery criteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject,
                                                                          materialLineFields, true);
        Query countQuery = entityManager.createQuery(criteriaQueryFromPageObject);
        pageObject.setCount(((Long) countQuery.getSingleResult()).intValue());

        // find the pull quantity for materials
        // criteria for result list
        CriteriaQuery resultSetCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject,
                                                                                   materialLineFields, false);
        Query resultSetQuery = entityManager.createQuery(resultSetCriteriaQueryFromPageObject);
        resultSetQuery.setFirstResult(pageObject.getFirstResult());
        resultSetQuery.setMaxResults(pageObject.getMaxResult());
        List<MaterialLine> resultSetMaterials = resultSetQuery.getResultList();

        List<MaterialLineDTO> materialLineDTOs = new ArrayList<MaterialLineDTO>();
        for (MaterialLine materialLine : resultSetMaterials) {
            Material material = materialLine.getMaterial();
            MaterialLineDTO materialLineDTO = transformAsMaterialLineDTO(materialLine, suggestBinLocation);
            materialLineDTO.setBinlocationBalance(calculateBinlocationBalance(material.getPartNumber(), material.getPartVersion(),
                                                                              materialLine.getBinLocationCode(), materialLine.getWhSiteId(),
                                                                              material.getPartModification(), material.getPartAffiliation()));
            materialLineDTO.setStockBalance(calculateStockBalance(material.getPartNumber(), material.getPartVersion(), materialLine.getWhSiteId(),
                                                                  material.getPartModification(), material.getPartAffiliation()));
            materialLineDTO.setQuantity(materialLine.getQuantity());
            materialLineDTO.setPickedQuantity(materialLine.getQuantity());
            materialLineDTOs.add(materialLineDTO);
        }

        if (materialLineDTOs != null) {
            pageObject.setGridContents(new ArrayList<PageResults>(materialLineDTOs));
        }

        return pageObject;
    }

    private double calculateBinlocationBalance(String partNumber, String partVersion, String binlocationCode, String whSiteId, String partModification,
            String partAffiliation) {
        return calculateBalance(partNumber, partVersion, partModification, partAffiliation, Arrays.asList(whSiteId), binlocationCode);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<MaterialLine> findMaterialLinesByPickListId(long pickListId) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(MaterialLine.class);

        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(criteriaBuilder.equal(root.get("pickList").get("pickListOid"), pickListId));

        List<Order> orderExpressions = new ArrayList<Order>();
        orderExpressions.add(criteriaBuilder.asc(root.get("binLocationCode")));
        orderExpressions.add(criteriaBuilder.asc(root.get("material").get("partNumber")));

        Query query = entityManager.createQuery(criteriaQuery.select(root).distinct(true).where(predicates.toArray(new Predicate[predicates.size()]))
                                                             .orderBy(orderExpressions));
        return query.getResultList();
    }

    @Override
    public PickList savePickList(PickList instanceToSave) {
        PickList toSave = instanceToSave;
        if (instanceToSave.getPickListOid() < 1) {
            getEntityManager().persist(instanceToSave);
        } else {
            toSave = getEntityManager().merge(instanceToSave);
        }
        return toSave;
    }

    @Override
    public Material saveMaterial(Material instanceToSave) {
        Material toSave = instanceToSave;
        if (instanceToSave.getMaterialOID() < 1) {
            getEntityManager().persist(instanceToSave);
        } else {
            toSave = getEntityManager().merge(instanceToSave);
        }
        return toSave;
    }

    @Override
    public RequestGroup findRequestGroupById(Long requestGroupOid) {
        Query query = getEntityManager().createNamedQuery("findRequestGroupByOId");
        query.setParameter("requestGroupOid", requestGroupOid);
        return (RequestGroup) query.getSingleResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MaterialLine> findMaterialLines(List<Long> materialLineOIds) {

        if (materialLineOIds != null) {
            Query query = getEntityManager().createNamedQuery("findMaterialLinesByIds");
            query.setParameter("materialLineOIds", materialLineOIds);
            return query.getResultList();
        }
        return new ArrayList<MaterialLine>();
    }

    @Override
    public RequestGroup saveRequestGroup(RequestGroup requestGroup) {
        RequestGroup toSave = requestGroup;
        if (requestGroup.getRequestGroupOid() < 1) {
            getEntityManager().persist(requestGroup);
        } else {
            toSave = getEntityManager().merge(requestGroup);
        }
        return toSave;
    }

    @Override
    public RequestList findRequestListById(long requestListOid) {
        return getEntityManager().find(RequestList.class, requestListOid);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<RequestList> findRequestListByUserId(String requesterUserId, String status) {
        Query query = getEntityManager().createNamedQuery("findRequestListByUserId");
        query.setParameter("requesterUserId", requesterUserId);
        query.setParameter("status", RequestListStatus.valueOf(status));
        return query.getResultList();
    }

    @Override
    public void deleteRequestList(RequestList requestList) {
        if (requestList != null) {
            List<RequestGroup> requestGroups = requestList.getRequestGroups();
            for (RequestGroup requestGroup : requestGroups) {
                requestGroup.getMaterialLines().clear();
            }
        }
        getEntityManager().remove(requestList);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public PageObject findPickListByStatus(PageObject pageObject, String whSiteId, String status, String userId) {

        Map<String, JpaAttribute> pickListFields = new HashMap<String, JpaAttribute>();

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(PickList.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.and(root.get("materialLines").get("status").in(MaterialLineStatus.valueOf(status))));
        predicatesRules.add(criteriaBuilder.and(root.get("materialLines").get("requestGroup").get("requestList").get("whSiteId").in(whSiteId)));

        if (!StringUtils.isEmpty(userId)) {
            predicatesRules.add(criteriaBuilder.or(criteriaBuilder.equal(root.get("reservedUserId"), userId),
                                                   criteriaBuilder.isNull(root.get("reservedUserId"))));
        }

        CriteriaQuery criteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject, pickListFields,
                                                                          true);
        Query countQuery = entityManager.createQuery(criteriaQueryFromPageObject);
        pageObject.setCount(((Long) countQuery.getSingleResult()).intValue());

        CriteriaQuery resultSetCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject,
                                                                                   pickListFields, false);
        Query resultSetQuery = entityManager.createQuery(resultSetCriteriaQueryFromPageObject);
        resultSetQuery.setFirstResult(pageObject.getFirstResult());
        resultSetQuery.setMaxResults(pageObject.getMaxResult());
        List<PickList> pickLists = resultSetQuery.getResultList();

        List<PickListDTO> pickListDTOs = MaterialTransformHelper.transformAsPickListDTOs(pickLists);

        if (pickListDTOs != null) {
            pageObject.setGridContents(new ArrayList<PageResults>(pickListDTOs));
        }

        return pageObject;
    }

    @Override
    public PickList findPickListByCode(String pickListCode) {
        Query query = getEntityManager().createNamedQuery("findPickListByCode");
        query.setParameter("pickListCode", pickListCode);
        return (PickList) query.getSingleResult();
    }

    @Override
    public double calculateStockBalance(String partNumber, String partVersion, String whSiteId, String partModification, String partAffiliation) {
        return calculateStockBalance(partNumber, partVersion, Arrays.asList(whSiteId), partModification, partAffiliation);
    }

    @Override
    public double calculateStockBalance(String partNumber, String partVersion, List<String> whSiteIds, String partModification, String partAffiliation) {
        return calculateBalance(partNumber, partVersion, partModification, partAffiliation, whSiteIds, null);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private long calculateBalance(String partNumber, String partVersion, String partModification, String partAffiliation, List<String> whSiteIds,
            String binlocationCode) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(MaterialLine.class);

        Path<String> path = root.get("material");

        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(criteriaBuilder.and((Predicate) root.get("status").in(MaterialLineStatus.READY_TO_STORE, MaterialLineStatus.STORED,
                                                                             MaterialLineStatus.MARKED_INSPECTION, MaterialLineStatus.REQUESTED)));
        predicates.add(criteriaBuilder.equal((Expression) path.get("partNumber"), partNumber));
        predicates.add(criteriaBuilder.equal((Expression) path.get("partVersion"), partVersion));
        predicates.add(criteriaBuilder.equal((Expression) path.get("partModification"), partModification));
        predicates.add(criteriaBuilder.equal((Expression) path.get("partAffiliation"), partAffiliation));

        // remove any null elements in list
        if (whSiteIds != null && !whSiteIds.isEmpty()) {
            List filteredWhSiteIds = new ArrayList(whSiteIds);
            filteredWhSiteIds.removeAll(Collections.singleton(null));
            predicates.add(root.get("whSiteId").in(filteredWhSiteIds));
        }

        if (!StringUtils.isEmpty(binlocationCode)) {
            predicates.add(criteriaBuilder.equal(root.get("binLocationCode"), binlocationCode));
        }

        Query query = entityManager.createQuery(criteriaQuery.select(criteriaBuilder.sum(root.get("quantity")))
                                                             .where(predicates.toArray(new Predicate[predicates.size()])));
        return ((Long) query.getSingleResult()).longValue();
    }

    @Override
    public DispatchNote findDispatchNoteById(long dispatchNoteId) {
        return getEntityManager().find(DispatchNote.class, dispatchNoteId);
    }

    @Override
    public DispatchNote saveDispatchNote(DispatchNote dispatchNote) {
        DispatchNote toSave = dispatchNote;
        if (dispatchNote.getDispatchNoteOID() < 1) {
            getEntityManager().persist(dispatchNote);
        } else {
            toSave = getEntityManager().merge(dispatchNote);
        }
        return toSave;
    }

    @Override
    public void deleteDispatchNote(long dispatchNoteId) {
        DispatchNote dispatchNote = findDispatchNoteById(dispatchNoteId);
        if (dispatchNote != null) {
            getEntityManager().remove(dispatchNote);
        }
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public PageObject getRequestLists(PageObject pageObject, String requestListStatus, String whSiteId, String outBoundLocationId, String requesterId)
            throws GloriaApplicationException {
        Map<String, JpaAttribute> requestListFields = new HashMap<String, JpaAttribute>();
        requestListFields.put("id", new JpaAttribute("requestListOid", JpaAttributeType.NUMBERTYPE));
        requestListFields.put("requesterName", new JpaAttribute("requesterName", JpaAttributeType.STRINGTYPE));
        requestListFields.put("requestUserId", new JpaAttribute("requestUserId,requesterName", JpaAttributeType.STRINGTYPE));
        requestListFields.put("priority", new JpaAttribute("priority", JpaAttributeType.NUMBERTYPE));
        requestListFields.put("requiredDeliveryDate", new JpaAttribute("requiredDeliveryDate", JpaAttributeType.DATETYPE));
        requestListFields.put("outboundLocationName", new JpaAttribute("deliveryAddressName", JpaAttributeType.STRINGTYPE));
        requestListFields.put("outboundLocationId", new JpaAttribute("deliveryAddressId,deliveryAddressName", JpaAttributeType.STRINGTYPE));
        requestListFields.put("dispatchNoteNumber", new JpaAttribute("dispatchNote.dispatchNoteNo", JpaAttributeType.STRINGTYPE));
        requestListFields.put("carrier", new JpaAttribute("dispatchNote.carrier", JpaAttributeType.STRINGTYPE));
        requestListFields.put("trackingNo", new JpaAttribute("dispatchNote.trackingNo", JpaAttributeType.STRINGTYPE));
        requestListFields.put("status", new JpaAttribute("status", JpaAttributeType.ENUMTYPE, RequestListStatus.class));
        requestListFields.put("shipmentType", new JpaAttribute("shipmentType", JpaAttributeType.STRINGTYPE));
        
        // default sort on status, PICK_COMPLETED items should appear first
        pageObject.setSortBy("status");
        pageObject.setSortOrder("asc");

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(RequestList.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();

        if (!StringUtils.isEmpty(requestListStatus)) {
            Path<String> path = root.get("status");
            Predicate predicateIn = path.in(GloriaFormateUtil.getValuesAsEnums(requestListStatus, RequestListStatus.class));
            predicatesRules.add(criteriaBuilder.and(predicateIn));
        }

        if (!StringUtils.isEmpty(whSiteId)) {
            predicatesRules.add(root.get("whSiteId").in((Object[]) whSiteId.split(",")));
        }

        if (!StringUtils.isEmpty(outBoundLocationId) && !StringUtils.isEmpty(requesterId)) {
            predicatesRules.add(criteriaBuilder.or(criteriaBuilder.equal(root.get("deliveryAddressId"), outBoundLocationId),
                                                   criteriaBuilder.notEqual(root.get("deliveryAddressType"), DeliveryAddressType.OUTBOUND_LOCATION)));
            predicatesRules.add(criteriaBuilder.equal(root.get("requestUserId"), requesterId));
        }

        predicatesRules.add(root.get("requestGroups").get("materialLines").isNotNull());
        
        String directSendTypePredicateValue = pageObject.getPredicate("materialDirectSendType");
        if (!StringUtils.isEmpty(directSendTypePredicateValue)) {
            if (directSendTypePredicateValue.equalsIgnoreCase("YES")) {
                predicatesRules.add(root.get("requestGroups").get("pickList").isNull());
            } else {
                predicatesRules.add(root.get("requestGroups").get("pickList").isNotNull());
            }
        }

        CriteriaQuery criteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject, requestListFields,
                                                                          true);
        Query countQuery = entityManager.createQuery(criteriaQueryFromPageObject);
        pageObject.setCount(((Long) countQuery.getSingleResult()).intValue());

        CriteriaQuery resultSetCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject,
                                                                                   requestListFields, false);
        Query resultSetQuery = entityManager.createQuery(resultSetCriteriaQueryFromPageObject);
        resultSetQuery.setFirstResult(pageObject.getFirstResult());
        resultSetQuery.setMaxResults(pageObject.getMaxResult());
        List<RequestList> requestLists = resultSetQuery.getResultList();
        List<RequestListDTO> listOfRequestListDto = new ArrayList<RequestListDTO>();
        if (requestLists != null && !requestLists.isEmpty()) {
            for (RequestList requestList : requestLists) {
                RequestListDTO requestListDTO = MaterialTransformHelper.transformAsRequestListDTO(requestList);
                List<PickList> existingPickList = findPickListByRequestListId(requestList.getRequestListOid());
                if ((requestList.getStatus() == RequestListStatus.PICK_COMPLETED || requestList.getStatus() == RequestListStatus.READY_TO_SHIP 
                        || requestList.getStatus() == RequestListStatus.SHIPPED) && (existingPickList == null || existingPickList.isEmpty())) {
                    requestListDTO.setMaterialDirectSendType("YES");
                } else {
                    requestListDTO.setMaterialDirectSendType("NO");
                }
                listOfRequestListDto.add(requestListDTO);
            }
            pageObject.setGridContents(new ArrayList<PageResults>(listOfRequestListDto));
        } else {
            pageObject.setGridContents(null);
        }

        return pageObject;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<RequestGroup> findAllRequestGroupsByRequestListId(long requestListOid) {
        Query query = getEntityManager().createNamedQuery("findAllRequestGroupsByRequestListId");
        query.setParameter("requestListOID", requestListOid);
        return query.getResultList();
    }

    @Override
    public void updateRequestList(RequestList requestList) {
        getEntityManager().merge(requestList);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<MaterialLine> getMovingMaterialLines(String pPartNumber, String pPartVersion, String binLocationCode, Date scrapExpirationDate, String whSiteId) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(MaterialLine.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();

        Path<String> path = root.get("status");
        predicatesRules.add(criteriaBuilder.equal(path, MaterialLineStatus.STORED));

        if (!StringUtils.isEmpty(whSiteId)) {
            predicatesRules.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("whSiteId")), whSiteId.toLowerCase()));
        }

        if (!StringUtils.isEmpty(binLocationCode) && !StringUtils.isEmpty(pPartNumber) && !StringUtils.isEmpty(pPartVersion)) {
            predicatesRules.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("binLocationCode")), binLocationCode.toLowerCase()));
            predicatesRules.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("material").get("procureLine").get("pPartNumber")),
                                                      pPartNumber.toLowerCase()));
            predicatesRules.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("material").get("procureLine").get("pPartVersion")),
                                                      pPartVersion.toLowerCase()));
            if (scrapExpirationDate != null) {
                Date dateToMatch = scrapExpirationDate;
                Date dateRangeFrom = DateUtil.getDateWithZeroTime(dateToMatch);
                Date dateRangeTo = DateUtil.getNextDate(dateRangeFrom);
                Path<Date> datePath = (Path<Date>) root.get("deliveryNoteLine").get("expirationDate");
                predicatesRules.add(criteriaBuilder.greaterThanOrEqualTo((Expression) datePath, dateRangeFrom));
                predicatesRules.add(criteriaBuilder.lessThan((Expression) datePath, dateRangeTo));
            }
        }

        if (predicatesRules != null && !predicatesRules.isEmpty()) {
            criteriaQuery.where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));
        }

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root);

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        return resultSetQuery.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MaterialLine> getMaterialLinesWithPartNumberaAndBinlocation(String pPartNumber, String binLocationCode) {
        Query query = getEntityManager().createNamedQuery("findMaterialLinesByPartNumberAndBinlocation");
        query.setParameter("pPartNumber", pPartNumber);
        query.setParameter("binLocationCode", binLocationCode);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<RequestGroup> findRequestGroupsByDispatchNoteId(long dispatchNoteOID) {
        Query query = getEntityManager().createNamedQuery("findAllRequestGroupsByDispatchNoteId");
        query.setParameter("dispatchNoteOID", dispatchNoteOID);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MaterialLine> findMaterialLinesByRequestGroupId(long requestGroupOid) {
        Query query = getEntityManager().createNamedQuery("findMaterialLinesByRequestGroupId");
        query.setParameter("requestGroupOid", requestGroupOid);
        return query.getResultList();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<MaterialLine> findMaterialsByPartStatusSiteAndTransportLabel(String pPartNumber, String pPartVersion, String status, String whSiteId,
            String pPartModification, String pPartAffiliation, String transportLabel) {

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(MaterialLine.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        if (!StringUtils.isEmpty(pPartNumber)) {
            Path<String> partNumberPath = root.get("material").get("partNumber");
            predicatesRules.add(criteriaBuilder.equal(partNumberPath, pPartNumber));
        }

        if (!StringUtils.isEmpty(pPartVersion)) {
            Path<String> partVersionPath = root.get("material").get("partVersion");
            predicatesRules.add(criteriaBuilder.equal(partVersionPath, pPartVersion));
        }

        if (!StringUtils.isEmpty(pPartModification)) {
            Path<String> partModificationPath = root.get("material").get("partModification");
            predicatesRules.add(criteriaBuilder.equal(partModificationPath, pPartModification));
        }

        if (!StringUtils.isEmpty(pPartAffiliation)) {
            Path<String> partAffiliationPath = root.get("material").get("partAffiliation");
            predicatesRules.add(criteriaBuilder.equal(partAffiliationPath, pPartAffiliation));
        }

        if (!StringUtils.isEmpty(status)) {
            Path<String> path = root.get("status");
            Predicate predicateIn = path.in(GloriaFormateUtil.getValuesAsEnums(status, MaterialLineStatus.class));
            predicatesRules.add(criteriaBuilder.and(predicateIn));
        }

        if (!StringUtils.isEmpty(whSiteId)) {
            Path<String> whSitePath = root.get("whSiteId");
            predicatesRules.add(criteriaBuilder.equal(whSitePath, whSiteId));
        }

        if (!StringUtils.isEmpty(transportLabel)) {
            predicatesRules.add(criteriaBuilder.equal(root.get("transportLabelCode"), transportLabel));
        }
        predicatesRules.add(criteriaBuilder.equal(root.get("zoneType"), ZoneType.TO_STORE));

        Query query = entityManager.createQuery(criteriaQuery.select(root).distinct(true).where(predicatesRules.toArray(new Predicate[predicatesRules.size()])));
        return query.getResultList();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public PageObject findMaterialLineByQi(PageObject pageObject, String status, String qiMarking, boolean suggestBinLocation, String whSiteId) {

        Map<String, JpaAttribute> materialLineFields = new HashMap<String, JpaAttribute>();

        materialLineFields.put("id", new JpaAttribute("materialLineOID", JpaAttributeType.NUMBERTYPE));
        materialLineFields.put("pPartNumber", new JpaAttribute("material.partNumber", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("pPartVersion", new JpaAttribute("material.partVersion", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("pPartName", new JpaAttribute("material.partName", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("pPartModification", new JpaAttribute("material.partModification", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("binLocation", new JpaAttribute("binLocationCode", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("materialOrderNo", new JpaAttribute("material.orderLine.order.orderNo", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("projectId", new JpaAttribute("material.financeHeader.projectId", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("storageRoomCode", new JpaAttribute("storageRoomCode", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("referenceId", new JpaAttribute("material.materialHeader.referenceId", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("contactPersonId", new JpaAttribute("material.materialHeader.accepted.contactPersonId", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("contactPersonName", new JpaAttribute("material.materialHeader.accepted.contactPersonName", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("inspectionStatus", new JpaAttribute("inspectionStatus", JpaAttributeType.ENUMTYPE, InspectionStatus.class));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(MaterialLine.class);

        pageObject.setDefaultSort("id", "ASC");

        List<Predicate> predicatesRules = new ArrayList<Predicate>();

        if (!StringUtils.isEmpty(status)) {
            Path<String> path = root.get("status");
            Predicate predicateIn = path.in(GloriaFormateUtil.getValuesAsEnums(status, MaterialLineStatus.class));
            predicatesRules.add(criteriaBuilder.and(predicateIn));
        }

        if (!StringUtils.isEmpty(whSiteId)) {
            predicatesRules.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("whSiteId")), whSiteId.toLowerCase()));
        }

        if (!StringUtils.isEmpty(qiMarking)) {
            Path<String> path = root.get("material").get("orderLine").get("qiMarking");
            Predicate predicateIn = path.in(GloriaFormateUtil.getValuesAsEnums(qiMarking, QiMarking.class));
            predicatesRules.add(criteriaBuilder.and(predicateIn));
        }

        predicatesRules.add(criteriaBuilder.isNotNull(root.get("deliveryNoteLine")));

        CriteriaQuery criteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject,
                                                                          materialLineFields, true);
        Query countQuery = entityManager.createQuery(criteriaQueryFromPageObject);
        pageObject.setCount(((Long) countQuery.getSingleResult()).intValue());

        CriteriaQuery resultSetCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject,
                                                                                   materialLineFields, false);
        Query resultSetQuery = entityManager.createQuery(resultSetCriteriaQueryFromPageObject);
        resultSetQuery.setFirstResult(pageObject.getFirstResult());
        resultSetQuery.setMaxResults(pageObject.getMaxResult());

        List<MaterialLine> materialLines = resultSetQuery.getResultList();
        if (materialLines != null && !materialLines.isEmpty()) {
            List<PageResults> materialLineQiDTOs = new ArrayList<PageResults>();
            for (MaterialLine materialLine : materialLines) {
                MaterialLineQiDTO materialLineQiDTO = new MaterialLineQiDTO();
                DeliveryNoteLine deliveryNoteLine = materialLine.getDeliveryNoteLine();
                materialLineQiDTO.setDeliveryNoteLineId(deliveryNoteLine.getDeliveryNoteLineOID());
                materialLineQiDTO.setDeliveryNoteLineVersion(deliveryNoteLine.getVersion());
                materialLineQiDTO.setHasDetails(deliveryNoteLine.isQiDetailsUpdated());
                materialLineQiDTO.setMaterialLineIds("" + materialLine.getMaterialLineOID());
                if (materialLine.getStatus() == MaterialLineStatus.MARKED_INSPECTION) {
                    materialLineQiDTO.setMarkedForInspection(deliveryNoteLine.isSendToQI());
                }
                if (materialLine.getInspectionStatus() != null) {
                    materialLineQiDTO.setInspectionStatus(materialLine.getInspectionStatus().name());
                }
                Material material = materialLine.getMaterial();
                if (material != null) {
                    materialLineQiDTO.setpPartNumber(material.getPartNumber());
                    materialLineQiDTO.setpPartName(material.getPartName());
                    materialLineQiDTO.setpPartVersion(material.getPartVersion());
                    materialLineQiDTO.setpPartModification(material.getPartModification());
                    if (material.getOrderLine() != null && material.getOrderLine().getQiMarking() != null) {
                        materialLineQiDTO.setQiMarking(material.getOrderLine().getQiMarking().name());
                    }

                    MaterialHeader materialHeader = material.getMaterialHeader();
                    if (materialHeader != null) {
                        materialLineQiDTO.setReferenceId(materialHeader.getReferenceId());

                        MaterialHeaderVersion accepted = materialHeader.getAccepted();
                        materialLineQiDTO.setContactPersonId(accepted.getContactPersonId());
                        materialLineQiDTO.setContactPersonName(accepted.getContactPersonName());
                    }

                    FinanceHeader financeHeader = material.getFinanceHeader();
                    if (financeHeader != null) {
                        materialLineQiDTO.setProjectId(financeHeader.getProjectId());
                    }

                    OrderLine orderLine = material.getOrderLine();
                    if (orderLine != null) {
                        materialLineQiDTO.setMaterialOrderNo(orderLine.getOrder().getOrderNo());
                    }
                }

                materialLineQiDTO.setQuantity(materialLine.getQuantity());
                materialLineQiDTO.setId(materialLine.getMaterialLineOID());
                materialLineQiDTO.setBinLocation(materialLine.getBinLocationCode());
                materialLineQiDTO.setStorageRoomCode(materialLine.getStorageRoomCode());
                if (suggestBinLocation && material != null) {
                    materialLineQiDTO.setSuggestedBinLocation(suggestNextlocation(material.getPartNumber(), material.getPartVersion(), whSiteId,
                                                                                  materialLine.getDirectSend().isDirectSend(), material.getPartModification(),
                                                                                  material.getPartAffiliation()));
                }
                materialLineQiDTO.setVersion(materialLine.getVersion());
                materialLineQiDTOs.add(materialLineQiDTO);
            }
            pageObject.setGridContents(materialLineQiDTOs);
        }

        return pageObject;
    }

    @Override
    public String suggestNextlocation(String partNumber, String partVersion, String whSiteId, boolean directSend, String partModification,
            String partAffiliation) {
        Zone zone = null;
        if (directSend) {
            zone = warehouseServices.findZoneCodes(ZoneType.SHIPPING, whSiteId);
        } else {
            BinLocation suggestedLocation = suggestBinLocation(partNumber, partVersion, whSiteId, partModification, partAffiliation);
            if (suggestedLocation != null) {
                zone = suggestedLocation.getZone();
            } else {
                zone = warehouseServices.findZoneCodes(ZoneType.TO_STORE, whSiteId);
            }
        }
        if (zone != null) {
            return zone.getCode();
        }
        return "";
    }

    @Override
    public MaterialLine findMaterialLineByIdForQi(Long materialLineId) {
        return getEntityManager().find(MaterialLine.class, materialLineId);
    }

    @Override
    public void addMaterialLine(MaterialLine materialLine) {
        getEntityManager().persist(materialLine);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public ChangeId findChangeIdByTechId(String changeTechId) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(ChangeId.class);

        if (!StringUtils.isEmpty(changeTechId)) {
            criteriaQuery.where(criteriaBuilder.equal(root.get("changeTechId"), changeTechId));
        }

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root).orderBy(criteriaBuilder.desc(root.get("changeIdOid")));

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);
        List<ChangeId> changeIds = resultSetQuery.getResultList();
        if (changeIds != null && !changeIds.isEmpty()) {
            return changeIds.get(0);
        }
        return null;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<ChangeId> findChangeIdByProcureRequestId(String procureRequestId) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(ChangeId.class);

        if (!StringUtils.isEmpty(procureRequestId)) {
            criteriaQuery.where(criteriaBuilder.equal(root.get("procureRequestId"), procureRequestId));
        }

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root).orderBy(criteriaBuilder.desc(root.get("changeIdOid")));

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);
        return resultSetQuery.getResultList();
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public PageObject getAccumulatedMaterialLinesInStock(PageObject pageObject, String partNumber, String partVersion, String partAffiliation,
            String projectId, String partModification, String companyCode) {
        Collection<String> companyCodeCodes = new ArrayList<String>();
        companyCodeCodes.add(companyCode);
        List<String> siteIds = commonServices.getSiteIdsByCompanyCodes(companyCodeCodes);
        Map<String, JpaAttribute> materialLineFields = new HashMap<String, JpaAttribute>();
        materialLineFields.put("countryCode", new JpaAttribute("material.orderLine.countryOfOrigin", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("siteId", new JpaAttribute("whSiteId", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("projectId", new JpaAttribute("material.financeHeader.projectId", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("quantity", new JpaAttribute("quantity", JpaAttributeType.NUMBERTYPE));
        materialLineFields.put("assignedMaterialController", new JpaAttribute("material.procureLine.materialControllerId", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("materialType", new JpaAttribute("materialOwner.materialType", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("mtrlRequestVersion", new JpaAttribute("materialOwner.mtrlRequestVersionAccepted", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("referenceId", new JpaAttribute("materialOwner.materialHeader.referenceId", JpaAttributeType.STRINGTYPE));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createTupleQuery();
        Root root = criteriaQuery.from(MaterialLine.class);
        Join<MaterialLine, Material> material = root.join("material", JoinType.LEFT);
        Join<Material, ProcureLine> procureLine = material.join("procureLine", JoinType.LEFT);
        Join<Material, FinanceHeader> financeHeader = material.join("financeHeader", JoinType.LEFT);
        Join<Material, OrderLine> orderLine = material.join("orderLine", JoinType.LEFT);
        Join<Material, MaterialHeader> materialHeader = material.join("materialHeader", JoinType.LEFT);

        List<MaterialLineAvailableDTO> listOfAvilableMaterialLineDTOs = new ArrayList<MaterialLineAvailableDTO>();

        List<Selection> activeSelections = new ArrayList<Selection>();
        List<Expression> groupByExpressions = new ArrayList<Expression>();

        // -------------------- handle RELEASED TYPE
        List<Predicate> predicatesRulesForReleased = getPredicatesForReleasedMaterials(siteIds, criteriaBuilder, root, partNumber, partVersion,
                                                                                       partAffiliation, projectId, partModification);

        addSelections(true, criteriaBuilder.max(root.get("materialLineOID")), "maxId", activeSelections, false, groupByExpressions);
        addSelections(true, orderLine.get("countryOfOrigin"), "countryOfOrigin", activeSelections, true, groupByExpressions);
        addSelections(true, root.get("whSiteId"), "whSiteId", activeSelections, true, groupByExpressions);
        addSelections(true, criteriaBuilder.sum(root.get("quantity")), "sumOfQty", activeSelections, false, groupByExpressions);
        addSelections(true, procureLine.get("materialControllerId"), "materialControllerId", activeSelections, true, groupByExpressions);
        addSelections(true, root.get("material").get("materialType"), "materialType", activeSelections, true, groupByExpressions);
        addSelections(true,
                      criteriaBuilder.trim(criteriaBuilder.<String> selectCase()
                                                          .when(criteriaBuilder.notEqual(root.get("material").get("materialType"), MaterialType.RELEASED),
                                                                financeHeader.<String> get("projectId")).otherwise("")), "projectId", activeSelections,
                      groupByExpressions, financeHeader.<String> get("projectId"));

        if (groupByExpressions != null && !groupByExpressions.isEmpty()) {
            criteriaQuery.groupBy(groupByExpressions.toArray(new Expression[groupByExpressions.size()]));
        }

        CriteriaQuery resultSetCriteriaQueryForReleaseType = criteriaQuery.multiselect(activeSelections.toArray(new Selection[activeSelections.size()]))
                                                                          .where(predicatesRulesForReleased.toArray(new Predicate[predicatesRulesForReleased.size()]));
        handleForStockTupleResults(entityManager.createQuery(resultSetCriteriaQueryForReleaseType).getResultList(), listOfAvilableMaterialLineDTOs, false);

        // ---------------- handle ADDITIONAL / ADDITIONAL_USAGE
        List<Predicate> predicatesRulesForAdditional = getPredicatesForAdditionalMaterials(siteIds, criteriaBuilder, root, partNumber, partVersion,
                                                                                           partAffiliation, projectId, partModification, financeHeader);
        addSelections(true,
                      criteriaBuilder.trim(criteriaBuilder.<String> selectCase()
                                                          .when(criteriaBuilder.notEqual(root.get("material").get("materialType"), MaterialType.RELEASED),
                                                                root.get("material").get("mtrlRequestVersionAccepted")).otherwise("")), "mtrId",
                      activeSelections, groupByExpressions, root.get("material").get("mtrlRequestVersionAccepted"));
        addSelections(true,
                      criteriaBuilder.trim(criteriaBuilder.<String> selectCase()
                                                          .when(criteriaBuilder.notEqual(root.get("material").get("materialType"), MaterialType.RELEASED),
                                                                materialHeader.<String> get("referenceId")).otherwise("")), "referenceId", activeSelections,
                      groupByExpressions, materialHeader.<String> get("referenceId"));

        if (groupByExpressions != null && !groupByExpressions.isEmpty()) {
            criteriaQuery.groupBy(groupByExpressions.toArray(new Expression[groupByExpressions.size()]));
        }
        CriteriaQuery resultSetCriteriaQueryForAdditionalType = criteriaQuery.multiselect(activeSelections.toArray(new Selection[activeSelections.size()]))
                                                                             .where(predicatesRulesForAdditional.toArray(new Predicate[predicatesRulesForAdditional.size()]));
        handleForStockTupleResults(entityManager.createQuery(resultSetCriteriaQueryForAdditionalType).getResultList(), listOfAvilableMaterialLineDTOs, true);

        if (listOfAvilableMaterialLineDTOs != null) {
            pageObject.setCount(listOfAvilableMaterialLineDTOs.size());
            pageObject.setGridContents(new ArrayList<PageResults>(listOfAvilableMaterialLineDTOs));
        }
        return pageObject;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private List<Predicate> getPredicatesForReleasedMaterials(List<String> siteIds, CriteriaBuilder criteriaBuilder, Root root, String partNumber,
            String partVersion, String partAffiliation, String projectId, String partModification) {
        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.or(criteriaBuilder.equal(root.get("status"), MaterialLineStatus.STORED),
                                               criteriaBuilder.equal(root.get("status"), MaterialLineStatus.READY_TO_STORE)));
        predicatesRules.add(criteriaBuilder.and(root.get("whSiteId").in(siteIds)));
        predicatesRules.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("material").get("partNumber"), partNumber),
                                                criteriaBuilder.equal(root.get("material").get("partVersion"), partVersion),
                                                criteriaBuilder.equal(root.get("material").get("partAffiliation"), partAffiliation),
                                                criteriaBuilder.equal(root.get("material").get("partModification"), partModification),
                                                criteriaBuilder.equal(root.get("material").get("materialType"), MaterialType.RELEASED)));
        return predicatesRules;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private List<Predicate> getPredicatesForAdditionalMaterials(List<String> siteIds, CriteriaBuilder criteriaBuilder, Root root, String partNumber,
            String partVersion, String partAffiliation, String projectId, String partModification, Join financeHeader) {
        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.or(criteriaBuilder.equal(root.get("status"), MaterialLineStatus.STORED),
                                               criteriaBuilder.equal(root.get("status"), MaterialLineStatus.READY_TO_STORE)));
        predicatesRules.add(criteriaBuilder.and(root.get("whSiteId").in(siteIds)));
        predicatesRules.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("material").get("partNumber"), partNumber),
                                                criteriaBuilder.equal(root.get("material").get("partVersion"), partVersion),
                                                criteriaBuilder.equal(root.get("material").get("partAffiliation"), partAffiliation),
                                                criteriaBuilder.equal(root.get("material").get("partModification"), partModification),
                                                criteriaBuilder.equal(financeHeader.get("projectId"), projectId),
                                                root.get("material").get("materialType").in(MaterialType.ADDITIONAL, MaterialType.ADDITIONAL_USAGE)));
        return predicatesRules;
    }

    private void handleForStockTupleResults(List<Tuple> resultsTuples, List<MaterialLineAvailableDTO> listOfAvilableMaterialLineDTOs, boolean isAdditional) {
        if (resultsTuples != null && !resultsTuples.isEmpty()) {
            for (Tuple resultTuple : resultsTuples) {
                long maxId = (Long) resultTuple.get("maxId");
                String countryOfOrigin = String.valueOf(resultTuple.get("countryOfOrigin"));
                String whSiteId = String.valueOf(resultTuple.get("whSiteId"));
                Long sumOfQty = (Long) resultTuple.get("sumOfQty");
                String materialControllerId = String.valueOf(resultTuple.get("materialControllerId"));
                String materialType = String.valueOf(resultTuple.get("materialType"));
                String projectId = null;
                String referenceId = null;
                if (isAdditional) {
                    projectId = String.valueOf(resultTuple.get("projectId"));
                    referenceId = String.valueOf(resultTuple.get("referenceId"));
                }

                MaterialLineAvailableDTO availableDTO = new MaterialLineAvailableDTO(maxId, countryOfOrigin, whSiteId, projectId, sumOfQty,
                                                                                     materialControllerId, MaterialType.valueOf(materialType), projectId,
                                                                                     referenceId);
                listOfAvilableMaterialLineDTOs.add(availableDTO);
            }
        }
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

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked", "unused" })
    public List<MaterialLine> findMaterialLinesFromStock(String partNumber, String partVersion, String partAffiliation, String projectId,
            String materialLineKeys) {
        if (!StringUtils.isEmpty(materialLineKeys)) {
            EntityManager entityManager = getEntityManager();
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
            Root root = criteriaQuery.from(MaterialLine.class);
            Join<MaterialLine, Material> material = root.join("material", JoinType.LEFT);
            Join<Material, ProcureLine> procureLine = material.join("procureLine", JoinType.LEFT);
            Join<Material, FinanceHeader> financeHeader = material.join("financeHeader", JoinType.LEFT);
            Join<Material, OrderLine> orderLine = material.join("orderLine", JoinType.LEFT);

            List<Predicate> predicatesRules = new ArrayList<Predicate>();
            Predicate releasedMaterials = criteriaBuilder.and(criteriaBuilder.equal(root.get("material").get("partNumber"), partNumber),
                                                              criteriaBuilder.equal(root.get("material").get("partVersion"), partVersion),
                                                              criteriaBuilder.equal(root.get("material").get("partAffiliation"), partAffiliation),
                                                              criteriaBuilder.equal(root.get("material").get("materialType"), MaterialType.RELEASED));

            Predicate additionalMaterials = criteriaBuilder.and(criteriaBuilder.equal(root.get("material").get("partNumber"), partNumber),
                                                                criteriaBuilder.equal(root.get("material").get("partVersion"), partVersion),
                                                                criteriaBuilder.equal(root.get("material").get("partAffiliation"), partAffiliation),
                                                                criteriaBuilder.equal(financeHeader.get("projectId"), projectId),
                                                                root.get("material").get("materialType")
                                                                    .in(MaterialType.ADDITIONAL, MaterialType.ADDITIONAL_USAGE));
            predicatesRules.add(criteriaBuilder.or(releasedMaterials, additionalMaterials));
            predicatesRules.add(criteriaBuilder.or(criteriaBuilder.equal(root.get("status"), MaterialLineStatus.STORED),
                                                   criteriaBuilder.equal(root.get("status"), MaterialLineStatus.READY_TO_STORE)));
            String[] keys = materialLineKeys.split(KEY_DELIMITER_COMMA);
            List<Predicate> rules = new ArrayList<Predicate>();
            for (String key : keys) {
                String[] keyItems = key.split(KEY_DELIMITER_HYPHEN);
                List<Predicate> materialPredicates = new ArrayList<Predicate>();
                materialPredicates.add(buildExpression(criteriaBuilder, orderLine.get("countryOfOrigin"), isNull(keyItems[KEY_ITEM_1])));
                materialPredicates.add(buildExpression(criteriaBuilder, root.get("whSiteId"), isNull(keyItems[KEY_ITEM_2])));
                materialPredicates.add(buildExpression(criteriaBuilder, procureLine.get("materialControllerId"), isNull(keyItems[KEY_ITEM_3])));
                materialPredicates.add(buildExpression(criteriaBuilder, root.get("material").get("materialType"), MaterialType.valueOf(keyItems[KEY_ITEM_4])));
                rules.add(criteriaBuilder.and(materialPredicates.toArray(new Predicate[materialPredicates.size()])));
            }
            predicatesRules.add(criteriaBuilder.or(rules.toArray(new Predicate[rules.size()])));
            criteriaQuery.where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));
            CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root);

            Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);
            return resultSetQuery.getResultList();
        }
        return new ArrayList<MaterialLine>();
    }

    private Predicate buildExpression(CriteriaBuilder criteriaBuilder, Expression<?> expression, Object keyItem) {
        if (keyItem != null) {
            return criteriaBuilder.equal(expression, keyItem);
        } else {
            return criteriaBuilder.or(criteriaBuilder.isNull(expression), criteriaBuilder.equal(expression, ""));
        }
    }

    private Object isNull(String value) {
        if (!StringUtils.isEmpty(value) && !"null".equals(value)) {
            return value;
        }
        return null;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public PageObject getBorrowableMaterialLines(PageObject pageObject, String pPartNumber, String pPartVersion, String pPartAffiliation, String referenceId,
            String mtrlRequestId) {

        Map<String, JpaAttribute> fieldMap = new HashMap<String, JpaAttribute>();
        fieldMap.put("projectId", new JpaAttribute("material.financeHeader.projectId", JpaAttributeType.STRINGTYPE));
        fieldMap.put("referenceGroup", new JpaAttribute("material.materialHeader.referenceGroup", JpaAttributeType.STRINGTYPE));
        fieldMap.put("referenceId", new JpaAttribute("material.materialHeader.referenceId", JpaAttributeType.STRINGTYPE));
        fieldMap.put("changeRequestIds", new JpaAttribute("material.procureLine.changeRequestIds", JpaAttributeType.STRINGTYPE));
        fieldMap.put("status", new JpaAttribute("status", JpaAttributeType.ENUMTYPE, MaterialLineStatus.class));

        pageObject.setDefaultSort("status", "desc");

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(MaterialLine.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();

        predicatesRules.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("material").get("procureLine").get("pPartNumber"), pPartNumber),
                                                criteriaBuilder.equal(root.get("material").get("procureLine").get("pPartVersion"), pPartVersion),
                                                criteriaBuilder.equal(root.get("material").get("procureLine").get("pPartAffiliation"), pPartAffiliation)));

        predicatesRules.add(root.get("material").get("materialType")
                                .in(MaterialType.MODIFIED, MaterialType.USAGE, MaterialType.RELEASED, MaterialType.ADDITIONAL, MaterialType.ADDITIONAL_USAGE));

        predicatesRules.add(criteriaBuilder.and(root.get("status").in(MaterialLineStatus.ORDER_PLACED_INTERNAL, MaterialLineStatus.ORDER_PLACED_EXTERNAL,
                                                                      MaterialLineStatus.READY_TO_STORE, MaterialLineStatus.STORED)));

        if (!StringUtils.isEmpty(referenceId)) {
            predicatesRules.add(criteriaBuilder.notEqual(root.get("material").get("materialHeader").get("referenceId"), referenceId));
        }
        if (!StringUtils.isEmpty(mtrlRequestId)) {
            predicatesRules.add(criteriaBuilder.notEqual(root.get("material").get("materialHeader").get("mtrlRequestId"), mtrlRequestId));
        }

        CriteriaQuery criteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject, fieldMap, true);
        Query countQuery = entityManager.createQuery(criteriaQueryFromPageObject);
        Long value = (Long) countQuery.getSingleResult();
        pageObject.setCount(value.intValue());

        CriteriaQuery resultSetCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject, fieldMap,
                                                                                   false);
        Query resultSetQuery = entityManager.createQuery(resultSetCriteriaQueryFromPageObject);
        resultSetQuery.setFirstResult(pageObject.getFirstResult());
        resultSetQuery.setMaxResults(pageObject.getMaxResult());

        List<MaterialLine> materialLines = resultSetQuery.getResultList();
        if (materialLines != null) {
            pageObject.setGridContents(new ArrayList<PageResults>(MaterialTransformHelper.transformAsMaterialLineDTOs(materialLines)));
        }
        return pageObject;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<MaterialLine> findMaterialLinesByDispatchNoteInTransferOrReturn(String dispatchNoteNo, Date dispatchNoteDate) {
        List<Predicate> predicates = new ArrayList<Predicate>();
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(MaterialLine.class);
        List<MaterialLineStatus> inTransferOrReturnStatus = new ArrayList<MaterialLineStatus>();
        inTransferOrReturnStatus.add(MaterialLineStatus.IN_TRANSFER);
        inTransferOrReturnStatus.add(MaterialLineStatus.SHIPPED);
        Path<String> path = root.get("status");
        predicates.add(criteriaBuilder.and(path.in(inTransferOrReturnStatus)));

        if (!StringUtils.isEmpty(dispatchNoteNo)) {
            predicates.add(criteriaBuilder.equal(root.get("requestGroup").get("requestList").get("dispatchNote").get("dispatchNoteNo"), dispatchNoteNo));
        }

        if (dispatchNoteDate != null) {
            Date dateRangeFrom = DateUtil.getDateWithZeroTime(dispatchNoteDate);
            Date dateRangeTo = DateUtil.getNextDate(dateRangeFrom);
            Path<Date> datePath = (Path<Date>) root.get("requestGroup").get("requestList").get("dispatchNote").get("dispatchNoteDate");
            predicates.add(criteriaBuilder.greaterThanOrEqualTo((Expression) datePath, dateRangeFrom));
            predicates.add(criteriaBuilder.lessThan((Expression) datePath, dateRangeTo));
        }

        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root);

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);
        return resultSetQuery.getResultList();
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public DispatchNote findDispatchNote(String dispatchNoteNo, Date dispatchNoteDate) {
        List<Predicate> predicates = new ArrayList<Predicate>();
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(DispatchNote.class);

        if (!StringUtils.isEmpty(dispatchNoteNo)) {
            predicates.add(criteriaBuilder.equal(root.get("dispatchNoteNo"), dispatchNoteNo));
        }

        if (dispatchNoteDate != null) {
            Date dateRangeFrom = DateUtil.getDateWithZeroTime(dispatchNoteDate);
            Date dateRangeTo = DateUtil.getNextDate(dateRangeFrom);
            Path<Date> datePath = (Path<Date>) root.get("dispatchNoteDate");
            predicates.add(criteriaBuilder.greaterThanOrEqualTo((Expression) datePath, dateRangeFrom));
            predicates.add(criteriaBuilder.lessThan((Expression) datePath, dateRangeTo));
        }

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root);
        criteriaQueryForResultSet.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);
        List<DispatchNote> dispatchNotes = resultSetQuery.getResultList();
        if (dispatchNotes != null && !dispatchNotes.isEmpty()) {
            return (DispatchNote) dispatchNotes.get(0);
        }
        return null;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public List<Material> findMaterialsByModificationId(long modificationId) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(Material.class);

        if (modificationId > 0) {
            criteriaQuery.where(criteriaBuilder.equal(root.get("modificationId"), modificationId));
        }

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root);

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);
        return resultSetQuery.getResultList();
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public PageObject findMaterialLinesByDispatchNote(PageObject pageObject, String receiveType, String whSiteId, String deliveryAddressId,
            String shipmentType, String dispatchNoteNo, String partNo, String partVersion, String status) {
        Map<String, JpaAttribute> materialLineFields = new HashMap<String, JpaAttribute>();
        materialLineFields.put("search", new JpaAttribute("material.partNumber,requestGroup.requestList.dispatchNote.dispatchNoteNo,"
                + "deliveryNoteLine.deliveryNote.supplierId," + "material.orderLine.supplierPartNo,requestGroup.requestList.whSiteId",
                                                          JpaAttributeType.STRINGTYPE));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(MaterialLine.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();

        if (!StringUtils.isEmpty(status)) {
            Path<String> path = root.get("status");
            Predicate predicateIn = path.in(EnumSet.of(MaterialLineStatus.valueOf(status)));
            predicatesRules.add(criteriaBuilder.and(predicateIn));
        }

        evaluatePredicates(whSiteId, deliveryAddressId, shipmentType, dispatchNoteNo, partNo, partVersion, criteriaBuilder, root, predicatesRules);

        CriteriaQuery criteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject,
                                                                          materialLineFields, true);

        Query countQuery = entityManager.createQuery(criteriaQueryFromPageObject);
        pageObject.setCount(((Long) countQuery.getSingleResult()).intValue());

        CriteriaQuery resultSetCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject,
                                                                                   materialLineFields, false);

        Query resultSetQuery = entityManager.createQuery(resultSetCriteriaQueryFromPageObject);
        resultSetQuery.setFirstResult(pageObject.getFirstResult());
        resultSetQuery.setMaxResults(pageObject.getMaxResult());
        List<MaterialLine> materialLines = resultSetQuery.getResultList();
        pageObject.setGridContents(new ArrayList<PageResults>(MaterialTransformHelper.transformAsMaterialLineDTOs(materialLines)));
        return pageObject;
    }

    @SuppressWarnings("rawtypes")
    private void evaluatePredicates(String whSiteId, String deliveryAddressId, String shipmentType, String dispatchNoteNo, String partNo, String partVersion,
            CriteriaBuilder criteriaBuilder, Root root, List<Predicate> predicatesRules) {
        if (!StringUtils.isEmpty(whSiteId)) {
            predicatesRules.add(criteriaBuilder.equal(root.get("requestGroup").get("requestList").get("whSiteId"), whSiteId));
        } else {
            predicatesRules.add(criteriaBuilder.equal(root.get("requestGroup").get("requestList").get("deliveryAddressId"), deliveryAddressId));
        }

        if (!StringUtils.isEmpty(shipmentType)) {
            predicatesRules.add(criteriaBuilder.equal(root.get("requestGroup").get("requestList").get("shipmentType"), ShipmentType.valueOf(shipmentType)));
        }

        if (!StringUtils.isEmpty(dispatchNoteNo)) {
            predicatesRules.add(criteriaBuilder.equal(root.get("requestGroup").get("requestList").get("dispatchNote").get("dispatchNoteNo"), dispatchNoteNo));
        }

        if (!StringUtils.isEmpty(partNo)) {
            predicatesRules.add(criteriaBuilder.equal(root.get("material").get("partNumber"), partNo));
        }

        if (!StringUtils.isEmpty(partVersion)) {
            predicatesRules.add(criteriaBuilder.equal(root.get("material").get("partVersion"), partVersion));
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public PageObject findMaterialsByProcureLineIds(List<Long> procureLineIds, PageObject pageObject) {
        Map<String, JpaAttribute> fieldMap = new HashMap<String, JpaAttribute>();

        fieldMap.put("mtrlRequestVersion", new JpaAttribute("add.mtrlRequestVersion", JpaAttributeType.STRINGTYPE));
        fieldMap.put("referenceId", new JpaAttribute("materialHeader.referenceId", JpaAttributeType.STRINGTYPE));
        fieldMap.put("partNumber", new JpaAttribute("partNumber", JpaAttributeType.STRINGTYPE));
        fieldMap.put("partVersion", new JpaAttribute("partVersion", JpaAttributeType.STRINGTYPE));
        fieldMap.put("partName", new JpaAttribute("partName", JpaAttributeType.STRINGTYPE));
        fieldMap.put("partModification", new JpaAttribute("partModification", JpaAttributeType.STRINGTYPE));
        fieldMap.put("quantity", new JpaAttribute("quantity", JpaAttributeType.NUMBERTYPE));
        fieldMap.put("pPartNumber", new JpaAttribute("procureLine.pPartNumber", JpaAttributeType.STRINGTYPE));
        fieldMap.put("pPartAffiliation", new JpaAttribute("procureLine.pPartAffiliation", JpaAttributeType.STRINGTYPE));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(Material.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        if (procureLineIds != null) {
            Path<String> path = root.get("procureLine").get("procureLineOid");
            Predicate predicateIn = path.in(procureLineIds);
            predicatesRules.add(criteriaBuilder.and(predicateIn));
        }

        CriteriaQuery criteriaQueryForPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject, fieldMap, true);
        Query countQuery = entityManager.createQuery(criteriaQueryForPageObject);
        pageObject.setCount(((Long) countQuery.getSingleResult()).intValue());

        CriteriaQuery resultSetCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject, fieldMap,
                                                                                   false);
        Query resultSetQuery = entityManager.createQuery(resultSetCriteriaQueryFromPageObject);
        resultSetQuery.setFirstResult(pageObject.getFirstResult());
        resultSetQuery.setMaxResults(pageObject.getMaxResult());
        List<MaterialDTO> materialDTOs = procurementDtoTransformer.transformAsMaterialDTOs(resultSetQuery.getResultList());

        if (materialDTOs != null) {
            pageObject.setGridContents(new ArrayList<PageResults>(materialDTOs));
        } else {
            pageObject.setGridContents(null);
        }

        return pageObject;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<ProcureLine> findProcureLinesByModificationId(long modificationId) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(ProcureLine.class);

        if (modificationId > 0) {
            criteriaQuery.where(criteriaBuilder.equal(root.get("materials").get("modificationId"), modificationId));
        }

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root).distinct(true);

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);
        List<ProcureLine> procureLines = resultSetQuery.getResultList();
        if (procureLines != null && !procureLines.isEmpty()) {
            return procureLines;
        }
        return new ArrayList<ProcureLine>();
    }

    @Override
    public void deleteRequestGroup(RequestGroup requestGroup) {
        getEntityManager().remove(requestGroup);
    }

    @Override
    public void deleteMaterialLine(MaterialLine materialLine) {
        getEntityManager().remove(materialLine);
    }
    
    @Override
    public void delete(PickList pickList) {
        getEntityManager().remove(pickList);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<Material> findMaterialsForOrderNos(Set<String> orderNos) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();

        Root root = criteriaQuery.from(Material.class);
        root.fetch("materialLine", JoinType.INNER);

        criteriaQuery.where(root.get("orderNo").in(Arrays.asList(orderNos)));
        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root);
        Query queryForResultSets = entityManager.createQuery(criteriaQueryForResultSet);
        return queryForResultSets.getResultList();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public List<MaterialLine> findMaterialLinesByReservedUserId(String userId) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(MaterialLine.class);
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(criteriaBuilder.equal(root.get("reservedUserId"), userId));
        Query query = entityManager.createQuery(criteriaQuery.select(root).where(predicates.toArray(new Predicate[predicates.size()])));
        return query.getResultList();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public List<PickList> findPickListByReservedUserId(String userId) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(PickList.class);
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(criteriaBuilder.equal(root.get("reservedUserId"), userId));
        Query query = entityManager.createQuery(criteriaQuery.select(root).where(predicates.toArray(new Predicate[predicates.size()])));
        return query.getResultList();
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public MaterialLine findAMaterialLineByAndStatusAndPlacement(MaterialLine materialLine, MaterialLineStatus materialLineStatus, String binLocationCode) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(MaterialLine.class);
        List<Predicate> predicates = new ArrayList<Predicate>();

        predicates.add(criteriaBuilder.notEqual(root.get("materialLineOID"), materialLine.getMaterialLineOID()));
        predicates.add(criteriaBuilder.equal(root.get("deliveryNoteLine"), materialLine.getDeliveryNoteLine()));
        predicates.add(criteriaBuilder.equal(root.get("requestGroup"), materialLine.getRequestGroup()));
        predicates.add(criteriaBuilder.equal(root.get("pickList"), materialLine.getPickList()));
        predicates.add(criteriaBuilder.equal(root.get("material"), materialLine.getMaterial()));
        predicates.add(criteriaBuilder.equal(root.get("status"), materialLineStatus));
        predicates.add(criteriaBuilder.equal(root.get("inspectionStatus"), materialLine.getInspectionStatus()));
        if (binLocationCode != null) {
            predicates.add(criteriaBuilder.equal(root.get("binLocationCode"), binLocationCode));
        }
        Query query = entityManager.createQuery(criteriaQuery.select(root).where(predicates.toArray(new Predicate[predicates.size()])));
        List<MaterialLine> materialLines = query.getResultList();
        if (materialLines != null && !materialLines.isEmpty()) {
            return materialLines.get(0);
        }
        return null;
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Long getMaxFirstAssemblyIdSequence() {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(MaterialHeader.class);
        Query query = entityManager.createQuery(criteriaQuery.select(criteriaBuilder.max(root.get("firstAssemblyIdSequence"))));
        return (Long) query.getSingleResult();
    }

    @Override
    public MaterialLine merge(MaterialLine materialLineToMerge, MaterialLine existingMaterialLine) throws GloriaApplicationException {
        if (existingMaterialLine != null) {
            materialLineToMerge.setQuantity(existingMaterialLine.getQuantity() + materialLineToMerge.getQuantity());
            removedDb(existingMaterialLine);
        }
        this.updateMaterialLine(materialLineToMerge);
        return materialLineToMerge;
    }

    private void removedDb(MaterialLine existingMaterialLine) throws GloriaApplicationException {
        MaterialServicesHelper.removePlacement(existingMaterialLine, warehouseServices);
        RequestGroup requestGroup = existingMaterialLine.getRequestGroup();
        if (requestGroup != null) {
            List<MaterialLine> requestGroupMaterialLines = requestGroup.getMaterialLines();
            if (requestGroupMaterialLines != null && requestGroupMaterialLines.contains(existingMaterialLine)) {
                requestGroupMaterialLines.remove(existingMaterialLine);
            }
        }
        existingMaterialLine.setStatus(MaterialLineStatus.REMOVED_DB);
        existingMaterialLine.setMaterialLineStatusTime(null);
        existingMaterialLine.setQuantity(0L);
        existingMaterialLine.setPlacementOID(null);
        existingMaterialLine.setBinLocationCode(null);
        existingMaterialLine.setZoneCode(null);
        existingMaterialLine.setZoneName(null);
        existingMaterialLine.setStorageRoomCode(null);
        existingMaterialLine.setStorageRoomName(null);
        existingMaterialLine.setZoneType(null);

        existingMaterialLine.setRequestGroup(null);
        existingMaterialLine.setPickList(null);
        existingMaterialLine.setDeliveryNoteLine(null);
        existingMaterialLine.setBorrowed(false);
        this.updateMaterialLine(existingMaterialLine);
    }

    @Override
    public MaterialLine split(MaterialLine materialLineToSplit, MaterialLineStatus materialLineStatus, long quantity, MaterialLine existingMaterialLine,
            MaterialHeaderRepository requestHeaderRepository, TraceabilityRepository traceabilityRepository, UserDTO user,
            List<MaterialLineStatus> avoidTraceForMLStatus) throws GloriaApplicationException {
        MaterialLine clonedMaterialLine = MaterialLineStatusHelper.cloneMaterialline(materialLineToSplit);
        this.updateMaterialLine(clonedMaterialLine);
        if (materialLineToSplit.getQuantity() == quantity) {
            Long placementOID = materialLineToSplit.getPlacementOID();
            BinLocation materialLineBinLocation = null;
            if (placementOID != null) {
                Placement placement = warehouseServices.getPlacement(placementOID);
                if (placement != null) {
                    materialLineBinLocation = placement.getBinLocation();
                }
            }
            removedDb(materialLineToSplit);

            if (materialLineBinLocation != null) {
                materialServices.createPlacement(materialLineBinLocation, clonedMaterialLine);
            }
        } else {
            MaterialLineStatusHelper.merge(materialLineToSplit, materialLineToSplit.getQuantity() - quantity, requestHeaderRepository, traceabilityRepository,
                                           user, avoidTraceForMLStatus);
        }
        return MaterialLineStatusHelper.merge(clonedMaterialLine, materialLineStatus, quantity, existingMaterialLine, requestHeaderRepository,
                                              traceabilityRepository, user, avoidTraceForMLStatus);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public long getHeaderVersionsForFirstAssembly(String referenceId, String buildId, ChangeId changeId) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(MaterialHeaderVersion.class);
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(criteriaBuilder.equal(root.get("materialHeader").get("referenceId"), referenceId));
        predicates.add(criteriaBuilder.equal(root.get("materialHeader").get("buildId"), buildId));
        if (changeId != null) {
            predicates.add(criteriaBuilder.equal(root.get("changeId"), changeId));
        }
        Query query = entityManager.createQuery(criteriaQuery.select(criteriaBuilder.max(root.get("headerVersion")))
                                                             .where(predicates.toArray(new Predicate[predicates.size()])));
        return (Long) query.getSingleResult();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<MaterialLine> findMaterialByBinlocatioinBalance(BinlocationBalance binlocationBalance) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(MaterialLine.class);

        Path<String> path = root.get("material");

        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(criteriaBuilder.equal((Expression) path.get("partNumber"), binlocationBalance.getPartNumber()));
        predicates.add(criteriaBuilder.equal((Expression) path.get("partVersion"), binlocationBalance.getPartVersion()));
        predicates.add(criteriaBuilder.equal((Expression) path.get("partModification"), binlocationBalance.getPartModification()));
        predicates.add(criteriaBuilder.equal((Expression) path.get("partAffiliation"), binlocationBalance.getPartAffiliation()));
        String siteId = binlocationBalance.getBinLocation().getZone().getStorageRoom().getWarehouse().getSiteId();
        predicates.add(root.get("whSiteId").in(siteId));

        Query query = entityManager.createQuery(criteriaQuery.select(root).where(predicates.toArray(new Predicate[predicates.size()])));
        return query.getResultList();
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public PageObject getReferenceGroups(PageObject pageObject, List<String> companyCodes) {
        Map<String, JpaAttribute> fieldMap = new HashMap<String, JpaAttribute>();
        fieldMap.put("id", new JpaAttribute("referenceGroup", JpaAttributeType.STRINGTYPE));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(MaterialHeaderVersion.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.in(root.get("materialHeader").get("companyCode")).value(companyCodes));

        CriteriaQuery countCriteriaQueryFromPageObeject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject, fieldMap,
                                                                                true);
        Query countQuery = entityManager.createQuery(countCriteriaQueryFromPageObeject);
        Long value = (Long) countQuery.getSingleResult();
        pageObject.setCount(value.intValue());

        CriteriaQuery resultSetCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject, fieldMap,
                                                                                   false);
        Query queryForResultSets = entityManager.createQuery(resultSetCriteriaQueryFromPageObject);
        queryForResultSets.setFirstResult(pageObject.getFirstResult());
        queryForResultSets.setMaxResults(pageObject.getMaxResult());
        List<MaterialHeaderVersion> materialHeaderVersions = queryForResultSets.getResultList();
        if (materialHeaderVersions != null && !materialHeaderVersions.isEmpty()) {
            List<ReportFilterDTO> reportBuildSerieDTOs = new ArrayList<ReportFilterDTO>();
            for (MaterialHeaderVersion materialHeaderVersion : materialHeaderVersions) {
                ReportFilterDTO reportBuildSerieDTO = new ReportFilterDTO();
                reportBuildSerieDTO.setId(materialHeaderVersion.getReferenceGroup());
                reportBuildSerieDTO.setText(materialHeaderVersion.getReferenceGroup());
                reportBuildSerieDTOs.add(reportBuildSerieDTO);
            }
            pageObject.setGridContents(new ArrayList<PageResults>(reportBuildSerieDTOs));
        } else {
            pageObject.setGridContents(null);
        }
        return pageObject;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public PageObject getTestObjects(PageObject pageObject) {
        Map<String, JpaAttribute> fieldMap = new HashMap<String, JpaAttribute>();
        fieldMap.put("id", new JpaAttribute("referenceId", JpaAttributeType.STRINGTYPE));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(MaterialHeader.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();

        CriteriaQuery resultCountCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root.get("referenceId"), predicatesRules,
                                                                                     pageObject, fieldMap, true);
        Query countQuery = entityManager.createQuery(resultCountCriteriaQueryFromPageObject);
        Long value = (Long) countQuery.getSingleResult();
        pageObject.setCount(value.intValue());

        Selection<ReportFilterDTO> selection = criteriaBuilder.construct(ReportFilterDTO.class, root.get("referenceId"), root.get("referenceId"));

        CriteriaQuery resultSetCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, selection, predicatesRules, pageObject,
                                                                                   fieldMap, false);
        Query queryForResultSets = entityManager.createQuery(resultSetCriteriaQueryFromPageObject);
        queryForResultSets.setFirstResult(pageObject.getFirstResult());
        queryForResultSets.setMaxResults(pageObject.getMaxResult());

        List<ReportFilterDTO> reportTestObjectDTOs = queryForResultSets.getResultList();
        if (reportTestObjectDTOs != null && !reportTestObjectDTOs.isEmpty()) {
            pageObject.setGridContents(new ArrayList<PageResults>(reportTestObjectDTOs));
        } else {
            pageObject.setGridContents(null);
        }
        return pageObject;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public PageObject getChangeIds(PageObject pageObject) {
        Map<String, JpaAttribute> fieldMap = new HashMap<String, JpaAttribute>();
        fieldMap.put("id", new JpaAttribute("mtrlRequestVersion", JpaAttributeType.STRINGTYPE));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(ChangeId.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();

        CriteriaQuery resultCountCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root.get("mtrlRequestVersion"),
                                                                                     predicatesRules, pageObject, fieldMap, true);
        Query countQuery = entityManager.createQuery(resultCountCriteriaQueryFromPageObject);
        Long value = (Long) countQuery.getSingleResult();
        pageObject.setCount(value.intValue());

        Selection<ReportFilterDTO> selection = criteriaBuilder.construct(ReportFilterDTO.class, root.get("mtrlRequestVersion"), root.get("mtrlRequestVersion"));

        CriteriaQuery resultSetCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, selection, predicatesRules, pageObject,
                                                                                   fieldMap, false);
        Query queryForResultSets = entityManager.createQuery(resultSetCriteriaQueryFromPageObject);
        queryForResultSets.setFirstResult(pageObject.getFirstResult());
        queryForResultSets.setMaxResults(pageObject.getMaxResult());

        List<ReportFilterDTO> reportMTRIdDTOs = queryForResultSets.getResultList();
        if (reportMTRIdDTOs != null && !reportMTRIdDTOs.isEmpty()) {
            pageObject.setGridContents(new ArrayList<PageResults>(reportMTRIdDTOs));
        } else {
            pageObject.setGridContents(null);
        }
        return pageObject;
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<ReportRow> fetchMaterialLinesForReport(ReportFilterMaterialDTO reportFilterMaterialDTO, Map<String, String> companyCodeToDefaultCurrencyMap,
            Map<String, CurrencyRate> currencyToCurrencyRateMap) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root root = criteriaQuery.from(MaterialLine.class);
        Join materialJoin = root.join("material", JoinType.LEFT);
        Join changeAddJoin = materialJoin.join("add", JoinType.LEFT);
        Join changeRemoveJoin = materialJoin.join("remove", JoinType.LEFT);
        Join materialHeaderJoin = materialJoin.join("materialHeader", JoinType.LEFT);
        Join orderlineJoin = materialJoin.join("orderLine", JoinType.LEFT);
        Join procureLineJoin = materialJoin.join("procureLine", JoinType.LEFT);
        Join deliveryNotelineJoin = root.join("deliveryNoteLine", JoinType.LEFT);
        Join requestGroupJoin = root.join("requestGroup", JoinType.LEFT);

        List<Selection> activeSelections = RequestHeaderRepositoryBeanHelper.buildSelections(reportFilterMaterialDTO, criteriaBuilder, root,
                                                                                             materialHeaderJoin, changeAddJoin, changeRemoveJoin,
                                                                                             orderlineJoin, procureLineJoin, deliveryNotelineJoin,
                                                                                             requestGroupJoin);

        List<Predicate> predicatesRules = RequestHeaderRepositoryBeanHelper.buildReportPredicates(reportFilterMaterialDTO, criteriaBuilder, root,
                                                                                                  materialHeaderJoin, changeAddJoin, changeRemoveJoin,
                                                                                                  orderlineJoin, procureLineJoin, deliveryNotelineJoin,
                                                                                                  requestGroupJoin);

        criteriaQuery.multiselect(activeSelections.toArray(new Selection[activeSelections.size()]));

        criteriaQuery.where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));

        List<Tuple> materialDataTuples = entityManager.createQuery(criteriaQuery).getResultList();

        List<ReportRow> reportRows = new ArrayList<ReportRow>();

        if (materialDataTuples != null && !materialDataTuples.isEmpty()) {
            for (Tuple materialDataTuple : materialDataTuples) {
                String companyCode = null;
                ReportRow reportRow = new ReportRow();
                for (Selection selection : activeSelections) {
                    if (selection.getAlias().equals(REPORT_COLUMN_ID_COMPANY_CODE)) {
                        companyCode = (materialDataTuple.get(selection)).toString();
                    }
                    if (selection.getAlias().equals(REPORT_COLUMN_ID_ORDER_LINE)) {
                        // handle tuples belonging to order line join
                        if (reportFilterMaterialDTO.isOrderLineLogEventValue()) {
                            prepareHandleTuples(REPORT_COLUMN_ID_ORDER_LINE_LOG, materialDataTuple, selection, reportRow);
                        }
                        if (reportFilterMaterialDTO.isExpectedQty()) {
                            prepareHandleTuples(REPORT_COLUMN_ID_EXPECTED_QTY, materialDataTuple, selection, reportRow);
                        }
                        if (reportFilterMaterialDTO.isExpectedDate()) {
                            prepareHandleTuples(REPORT_COLUMN_ID_EXPECTED_DATE, materialDataTuple, selection, reportRow);
                        }
                        if (reportFilterMaterialDTO.isPlannedDispatchDate()) {
                            prepareHandleTuples(REPORT_COLUMN_ID_PLANNED_DISPATCH_DATE, materialDataTuple, selection, reportRow);
                        }
                        if (reportFilterMaterialDTO.isFlagOrderLine()) {
                            prepareHandleTuples(REPORT_COLUMN_ID_FLAG_ORDER_LINE, materialDataTuple, selection, reportRow);
                        }
                    } else if (selection.getAlias().equals(REPORT_COLUMN_ID_ORDERLINE_VERSION)) {
                        String defaultCurrencyCode = companyCodeToDefaultCurrencyMap.get(companyCode);
                        if (reportFilterMaterialDTO.isUnitOfPrice()) {
                            ReportHelper.prepareHandleTuplesForCurrency(REPORT_COLUMN_ID_UNIT_OF_PRICE, materialDataTuple, selection, reportRow,
                                                                        defaultCurrencyCode, currencyToCurrencyRateMap);
                        }
                        if (reportFilterMaterialDTO.isCurrency()) {
                            ReportHelper.prepareHandleTuplesForCurrency(REPORT_COLUMN_ID_CURRENCY_ORIGINAL, materialDataTuple, selection, reportRow,
                                                                        defaultCurrencyCode, currencyToCurrencyRateMap);
                        }
                        if (reportFilterMaterialDTO.isUnitPrice()) {
                            ReportHelper.prepareHandleTuplesForCurrency(REPORT_COLUMN_ID_UNIT_PRICE_ORIGINAL, materialDataTuple, selection, reportRow,
                                                                        defaultCurrencyCode, currencyToCurrencyRateMap);
                        }
                        if (reportFilterMaterialDTO.isPrice()) {
                            ReportHelper.prepareHandleTuplesForCurrency(REPORT_COLUMN_ID_TOTAL_PRICE_ORIGINAL, materialDataTuple, selection, reportRow,
                                                                        defaultCurrencyCode, currencyToCurrencyRateMap);
                        }
                        if (reportFilterMaterialDTO.isCurrency()) {
                            ReportHelper.prepareHandleTuplesForCurrency(REPORT_COLUMN_ID_CURRENCY, materialDataTuple, selection, reportRow,
                                                                        defaultCurrencyCode, currencyToCurrencyRateMap);
                        }
                        if (reportFilterMaterialDTO.isUnitPrice()) {
                            ReportHelper.prepareHandleTuplesForCurrency(REPORT_COLUMN_ID_UNIT_PRICE, materialDataTuple, selection, reportRow,
                                                                        defaultCurrencyCode, currencyToCurrencyRateMap);
                        }
                        if (reportFilterMaterialDTO.isPrice()) {
                            ReportHelper.prepareHandleTuplesForCurrency(REPORT_COLUMN_ID_TOTAL_PRICE, materialDataTuple, selection, reportRow,
                                                                        defaultCurrencyCode, currencyToCurrencyRateMap);
                        }

                    } else {
                        prepareHandleTuples(null, materialDataTuple, selection, reportRow);
                    }
                }
                reportRows.add(reportRow);
            }
        }
        return reportRows;
    }

    @SuppressWarnings("rawtypes")
    private void prepareHandleTuples(String column, Tuple materialDataTuple, Selection selection, ReportRow reportRow) {
        ReportColumn reportColumn = new ReportColumn();
        if (column == null) {
            reportColumn.setName(selection.getAlias().replace("_", " "));
        } else {
            reportColumn.setName(column.replace("_", " "));
        }
        reportColumn.setValue(RequestHeaderRepositoryBeanHelper.handleTuples(materialDataTuple, selection, column));
        reportRow.getReportColumns().add(reportColumn);
    }

    @Override
    public void updateFinanceHeader(FinanceHeader financeHeader) {
        getEntityManager().merge(financeHeader);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<MaterialLine> findMaterialLinesForOrderline(long orderLineOID) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(MaterialLine.class);

        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(criteriaBuilder.equal(root.get("material").get("orderLine").get("orderLineOID"), orderLineOID));
        predicates.add(root.get("status").in(MaterialLineStatus.ORDER_PLACED_EXTERNAL, MaterialLineStatus.ORDER_PLACED_INTERNAL,
                                             MaterialLineStatus.REQUISITION_SENT));
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get("material").get("materialType")),
                              criteriaBuilder.desc(root.get("material").get("materialHeader").get("accepted").get("outboundStartDate")));
        Query query = entityManager.createQuery(criteriaQuery.select(root).where(predicates.toArray(new Predicate[predicates.size()])));
        return query.getResultList();
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<Material> findAllMaterials(long materialHeaderOid) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(Material.class);

        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(criteriaBuilder.equal(root.get("materialHeader").get("materialHeaderOid"), materialHeaderOid));
        Query query = entityManager.createQuery(criteriaQuery.select(root).where(predicates.toArray(new Predicate[predicates.size()])));
        return query.getResultList();
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<Material> findAllMaterialsByReplacedByOID(long replacedByOid) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(Material.class);

        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(criteriaBuilder.equal(root.get("replacedByOid"), replacedByOid));
        Query query = entityManager.createQuery(criteriaQuery.select(root).where(predicates.toArray(new Predicate[predicates.size()])));
        return query.getResultList();
    }

    @Override
    public List<RequestList> findRequestListByPicklist(long picklistOid) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<RequestList> criteriaQuery = criteriaBuilder.createQuery(RequestList.class);
        Root<RequestList> requestList = criteriaQuery.from(RequestList.class);
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(criteriaBuilder.equal(requestList.get("requestGroups").get("pickList").get("pickListOid"), picklistOid));
        TypedQuery<RequestList> query = entityManager.createQuery(criteriaQuery.select(requestList).distinct(true)
                                                                               .where(predicates.toArray(new Predicate[predicates.size()])));
        return query.getResultList();
    }

    /*
     * Finds a List of DispatchNotes betweeen two dates for a given set of Project IDS and WhsiteIds
     */
    public List<DispatchNote> findDispatchNote(Date fromDate, Date toDate, String[] projectIds, String[] whSiteId) {
        return RequestHeaderRepositoryBeanHelper.findDispatchNote(fromDate, toDate, projectIds, whSiteId, this.getEntityManager());
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<ReportRow> fetchWarehouseCostReport(ReportWarehouseCostDTO reportWarehouseCostDTO, Date fromDate, Date toDate,
            Map<String, String> companyCodeToDefaultCurrencyMap, Map<String, CurrencyRate> currencyToCurrencyRateMap) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root root = criteriaQuery.from(MaterialLine.class);
        Join materialJoin = root.join("material", JoinType.INNER);
        Join orderlineJoin = materialJoin.join("orderLine", JoinType.INNER);

        List<Selection> activeSelections = new ArrayList<Selection>();
        List<Expression> groupByExpressions = new ArrayList<Expression>();
        List<Predicate> predicatesRules = new ArrayList<Predicate>();

        RequestHeaderRepositoryBeanHelper.buildSelections(activeSelections, groupByExpressions, reportWarehouseCostDTO, criteriaBuilder, root, orderlineJoin);

        RequestHeaderRepositoryBeanHelper.buildReportPredicates(predicatesRules, reportWarehouseCostDTO, criteriaBuilder, root, orderlineJoin, 
                                                                fromDate, toDate);

        criteriaQuery.multiselect(activeSelections.toArray(new Selection[activeSelections.size()]));

        criteriaQuery.where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));

        criteriaQuery.groupBy(groupByExpressions.toArray(new Expression[groupByExpressions.size()]));

        List<Tuple> warehouseMaterialDataTuples = entityManager.createQuery(criteriaQuery).getResultList();

        List<ReportRow> reportRows = new ArrayList<ReportRow>();

        Map<String, Map<String, Object>> warehouseMaterialDataTaupleMap = new HashMap<String, Map<String, Object>>();

        if (warehouseMaterialDataTuples != null && !warehouseMaterialDataTuples.isEmpty()) {
            for (Tuple warehouseMaterialDataTuple : warehouseMaterialDataTuples) {

                String companyCode = null;
                String warehouseId = null;
                MaterialLineStatus mateiralLineStatus = null;
                String project = null;
                long mlQuantity = 0;
                double untiPrice = 0.0;
                long perQty = 0;
                String originalCurrency = null;
                String defaultCurrencyCode = null;
                for (Selection selection : activeSelections) {
                    if (selection.getAlias().equals(REPORT_COLUMN_ID_COMPANY_CODE)) {
                        companyCode = (warehouseMaterialDataTuple.get(selection)).toString();
                        defaultCurrencyCode = companyCodeToDefaultCurrencyMap.get(companyCode);
                    }
                    if (selection.getAlias().equals(REPORT_COLUMN_ID_WAREHOUSE)) {
                        warehouseId = (warehouseMaterialDataTuple.get(selection)).toString();
                    }
                    if (selection.getAlias().equals(REPORT_COLUMN_ID_PROJECT)) {
                        project = (warehouseMaterialDataTuple.get(selection)).toString();
                    }
                    if (selection.getAlias().equals(REPORT_COLUMN_ID_STATUS)) {
                        mateiralLineStatus = (MaterialLineStatus) warehouseMaterialDataTuple.get(selection);
                    }
                    if (selection.getAlias().equals(REPORT_COLUMN_ID_QTY)) {
                        mlQuantity = (Long) warehouseMaterialDataTuple.get(selection);
                    }
                    if (selection.getAlias().equals(REPORT_COLUMN_ID_UNIT_PRICE)) {
                        untiPrice = (Double) warehouseMaterialDataTuple.get(selection);
                    }
                    if (selection.getAlias().equals(REPORT_COLUMN_ID_UNIT_OF_PRICE)) {
                        perQty = (Long) warehouseMaterialDataTuple.get(selection);
                    }
                    if (selection.getAlias().equals(REPORT_COLUMN_ID_CURRENCY)) {
                        originalCurrency = warehouseMaterialDataTuple.get(selection) != null ? warehouseMaterialDataTuple.get(selection).toString() : null;
                        groupDataForWareHouseCost(companyCode, warehouseId, project, untiPrice, perQty, originalCurrency, defaultCurrencyCode,
                                                  mateiralLineStatus, mlQuantity, currencyToCurrencyRateMap, warehouseMaterialDataTaupleMap);
                    }
                }
            }
        }

        if (warehouseMaterialDataTaupleMap.size() == 0) {
            ReportRow reportRow = new ReportRow();
            addEmptyColumn(reportRow, REPORT_COLUMN_ID_WAREHOUSE);
            addEmptyColumn(reportRow, REPORT_COLUMN_ID_PROJECT);
            addEmptyColumn(reportRow, REPORT_COLUMN_ID_SCRAPPED_PART_VALUE);
            addEmptyColumn(reportRow, REPORT_COLUMN_ID_INVENTORY_ADJUSTMENT_VALUE);
            addEmptyColumn(reportRow, REPORT_COLUMN_ID_CURRENCY);

            reportRows.add(reportRow);
        } else {
            Iterator<String> warehouseRowMapKeys = warehouseMaterialDataTaupleMap.keySet().iterator();
            while (warehouseRowMapKeys.hasNext()) {
                ReportRow reportRow = new ReportRow();
                Map<String, Object> warehouseCostRowMap = warehouseMaterialDataTaupleMap.get(warehouseRowMapKeys.next());
                Iterator<String> warehouseCostColumnKeys = warehouseCostRowMap.keySet().iterator();
                while (warehouseCostColumnKeys.hasNext()) {
                    ReportColumn reportColumn = new ReportColumn();
                    String columnkey = warehouseCostColumnKeys.next();
                    reportColumn.setName(columnkey.replace("_", " "));
                    reportColumn.setValue(warehouseCostRowMap.get(columnkey));
                    reportRow.getReportColumns().add(reportColumn);
                }
                reportRows.add(reportRow);
            }
        }
        return reportRows;
    }

    private void addEmptyColumn(ReportRow reportRow, String columnName) {
        ReportColumn reportColumn = new ReportColumn();
        reportColumn.setName(columnName.replace("_", " "));
        reportColumn.setValue(null);
        reportRow.getReportColumns().add(reportColumn);
    }

    private void groupDataForWareHouseCost(String companyCode, String warehouseId, String project, double untiPrice, long perQty, String originalCurrency,
            String defaultCurrencyCode, MaterialLineStatus mateiralLineStatus, long mlQuantity, Map<String, CurrencyRate> currencyToCurrencyRateMap,
            Map<String, Map<String, Object>> warehouseMaterialDataTaupleMap) {
        Map<String, Object> warehouseCostDataMap = new LinkedHashMap<String, Object>();
        double convertedUnitPrice = CurrencyUtil.convertCurrencyFromActualToDefault(untiPrice, originalCurrency, defaultCurrencyCode,
                                                                                    currencyToCurrencyRateMap);
        String warehouseCostKey = warehouseId + project;
        if (warehouseMaterialDataTaupleMap.containsKey(warehouseCostKey)) {
            warehouseCostDataMap = warehouseMaterialDataTaupleMap.get(warehouseCostKey);
        } else {
            warehouseCostDataMap.put(REPORT_COLUMN_ID_WAREHOUSE, warehouseId);
            warehouseCostDataMap.put(REPORT_COLUMN_ID_PROJECT, project);
            warehouseCostDataMap.put(REPORT_COLUMN_ID_SCRAPPED_PART_VALUE, 0.0);
            warehouseCostDataMap.put(REPORT_COLUMN_ID_INVENTORY_ADJUSTMENT_VALUE, 0.0);
            warehouseCostDataMap.put(REPORT_COLUMN_ID_CURRENCY, defaultCurrencyCode);
        }
        
        double finalPrice = 0.0;
        if (perQty > 0) {
            finalPrice = mlQuantity * (convertedUnitPrice / perQty);
        } else {
            finalPrice = mlQuantity * convertedUnitPrice;
        }
        finalPrice = CurrencyUtil.roundOff(finalPrice, 2);
        if (mateiralLineStatus.equals(MaterialLineStatus.SCRAPPED)) {
            double currentScrappedValue = (Double) warehouseCostDataMap.get(REPORT_COLUMN_ID_SCRAPPED_PART_VALUE);
            double newScrappedValue = currentScrappedValue + finalPrice;
            warehouseCostDataMap.put(REPORT_COLUMN_ID_SCRAPPED_PART_VALUE, newScrappedValue);
        } else if (mateiralLineStatus.equals(MaterialLineStatus.MISSING)) {
            double currentInvAdjValue = (Double) warehouseCostDataMap.get(REPORT_COLUMN_ID_INVENTORY_ADJUSTMENT_VALUE);
            double newInvAdjValue = currentInvAdjValue + finalPrice;
            warehouseCostDataMap.put(REPORT_COLUMN_ID_INVENTORY_ADJUSTMENT_VALUE, newInvAdjValue);
        }
        warehouseMaterialDataTaupleMap.put(warehouseCostKey, warehouseCostDataMap);
    }

    @Override
    public List<Tuple> getTransactionShipmentReportData(ReportWarehouseTransactionDTO reportWarehouseTransactionDTO, Date fromDate, Date toDate,
            String[] projectIds) {
        return RequestHeaderRepositoryBeanHelper.getTransactionReportShipmentReportData(reportWarehouseTransactionDTO, this.getEntityManager(), fromDate,
                                                                                        toDate, projectIds);
    }

    @Override
    public List<Tuple> getTransactionStoresReportData(ReportWarehouseTransactionDTO reportWarehouseTransactionDTO, Date fromDate, Date toDate) {
        return RequestHeaderRepositoryBeanHelper.getTransactionReportStoresReportData(reportWarehouseTransactionDTO, this.getEntityManager(), fromDate, toDate);

    }

    @SuppressWarnings("rawtypes")
    @Override
    public Map<Long, Long> findProcureLineOidsMap(long materialHeaderOid) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root<Material> root = criteriaQuery.from(Material.class);
        List<Selection> columnSelections = new ArrayList<Selection>();
        columnSelections.add(root.get("procureLine").get("procureLineOid"));
        columnSelections.add(criteriaBuilder.countDistinct(root.<Long> get("materialOID")));
        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.equal(root.get("materialHeader").get("materialHeaderOid"), materialHeaderOid));
        criteriaQuery.where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));
        criteriaQuery.multiselect(columnSelections.toArray(new Selection[columnSelections.size()])).distinct(true);
        criteriaQuery.groupBy(root.get("procureLine").get("procureLineOid"));
        TypedQuery<Tuple> query = getEntityManager().createQuery(criteriaQuery);
        List<Tuple> tuples = query.getResultList();
        Map<Long, Long> resultList = new HashMap<Long, Long>();
        for (Tuple tuple : tuples) {
            resultList.put((Long) tuple.get(0), (Long) tuple.get(1));

        }
        return resultList;
    }

    @Override
    public void updateProcureLineControllerDetails(String materialControllerUserId, String materialControllerName, String materialControllerTeam,
            List<Long> procureLineOids) {
        Query query = getEntityManager().createQuery("UPDATE ProcureLine p set p.materialControllerId = ?1 " + ",p.materialControllerName = ?2"
                                                             + ", p.materialControllerTeam = ?3 " + "WHERE p.procureLineOid in  ?4");
        query.setParameter(1, materialControllerUserId);
        query.setParameter(2, materialControllerName);
        query.setParameter(3, materialControllerTeam);
        query.setParameter(4, procureLineOids);
        query.executeUpdate();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<ProcureLine> findProcureLinesByIds(List<Long> procureLineIds) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(ProcureLine.class);
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(root.get("procureLineOid").in(procureLineIds));
        Query query = entityManager.createQuery(criteriaQuery.select(root).where(predicates.toArray(new Predicate[predicates.size()])));
        return query.getResultList();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public long findProcureLineSumQuantities(long procureLineOid, List<MaterialType> materialTypes) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root<Material> root = criteriaQuery.from(Material.class);
        List<Selection> columnSelections = new ArrayList<Selection>();
        columnSelections.add(criteriaBuilder.sum(root.get("materialLine").<Long> get("quantity")));

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.equal(root.get("procureLine").get("procureLineOid"), procureLineOid));
        predicatesRules.add(criteriaBuilder.in(root.get("materialType")).value(materialTypes));

        criteriaQuery.where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));
        criteriaQuery.multiselect(columnSelections.toArray(new Selection[columnSelections.size()]));

        TypedQuery<Tuple> query = getEntityManager().createQuery(criteriaQuery);
        List<Tuple> tuples = query.getResultList();
        long sum = 0;
        for (Tuple tuple : tuples) {
            sum = sum + (Long) tuple.get(0);
        }
        return sum;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Map<MaterialType, Long> findMaterialLineQuantities(List<Long> materialOids) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root<Material> root = criteriaQuery.from(Material.class);
        List<Selection> columnSelections = new ArrayList<Selection>();
        columnSelections.add(criteriaBuilder.sum(root.get("materialLine").<Long> get("quantity")));
        columnSelections.add(root.get("materialType"));

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(root.get("materialOID").in(materialOids));

        criteriaQuery.groupBy(root.get("materialType"));
        criteriaQuery.where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));
        criteriaQuery.multiselect(columnSelections.toArray(new Selection[columnSelections.size()]));

        TypedQuery<Tuple> query = getEntityManager().createQuery(criteriaQuery);
        List<Tuple> tuples = query.getResultList();
        Map<MaterialType, Long> result = new HashMap<MaterialType, Long>();
        for (Tuple tuple : tuples) {
            result.put((MaterialType) tuple.get(1), (Long) tuple.get(0));
        }
        return result;
    }

    /*
     * @Override public List<MaterialLine> getMaterialLines(long materialOid) { EntityManager entityManager = getEntityManager(); CriteriaBuilder
     * criteriaBuilder = entityManager.getCriteriaBuilder(); CriteriaQuery<MaterialLine> criteriaQuery = criteriaBuilder.createQuery(MaterialLine.class);
     * Root<MaterialLine> root = criteriaQuery.from(MaterialLine.class); List<Predicate> predicates = new ArrayList<Predicate>();
     * predicates.add(criteriaBuilder.equal(root.get("material").get("materialOID"), materialOid)); TypedQuery<MaterialLine> query =
     * entityManager.createQuery(criteriaQuery.select(root).where(predicates.toArray(new Predicate[predicates.size()]))); return query.getResultList(); }
     */

    @SuppressWarnings("unused")
    @Override
    public void updateMaterialLinesStatus(long materialHeaderOid, MaterialLineStatus fromStatus, MaterialLineStatus toStatus) {
        Query query = getEntityManager().createQuery("update MaterialLine m set m.status = ?1 "
                                                             + " WHERE  m.status = ?2  and m.material.materialHeader.materialHeaderOid = ?3");
        query.setParameter(QUERY_PARAM_LOCATION_ONE, toStatus);
        query.setParameter(QUERY_PARAM_LOCATION_TWO, fromStatus);
        query.setParameter(QUERY_PARAM_LOCATION_THREE, materialHeaderOid);
        int updates = query.executeUpdate();
    }

    @SuppressWarnings("unused")
    @Override
    public void updateMaterialLinesStatus(List<Long> materialOids, MaterialLineStatus toStatus) {
        if (materialOids != null && materialOids.size() > 0) {
            Query query = getEntityManager().createQuery("update MaterialLine m set m.status = ?1 " + " WHERE  m.material.materialOID in ?2 "
                                                                 + " and m.status in (?3)");
            query.setParameter(QUERY_PARAM_LOCATION_ONE, toStatus);
            query.setParameter(QUERY_PARAM_LOCATION_TWO, materialOids);
            query.setParameter(QUERY_PARAM_LOCATION_THREE, EnumSet.of(MaterialLineStatus.CREATED, MaterialLineStatus.ORDER_PLACED_INTERNAL,
                                                                      MaterialLineStatus.REQUISITION_SENT, MaterialLineStatus.ORDER_PLACED_EXTERNAL));
            int updates = query.executeUpdate();
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<Material> findMaterialsByProcureLineIds(List<Long> procureLineOid) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(Material.class);
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(root.get("procureLine").get("procureLineOid").in(procureLineOid));
        Query query = entityManager.createQuery(criteriaQuery.select(root).where(predicates.toArray(new Predicate[predicates.size()])));
        return query.getResultList();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<Material> findMaterialsByMaterialHeaderId(Long materialHeaderOid) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(Material.class);
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(root.get("materialHeader").get("materialHeaderOid").in(materialHeaderOid));
        Query query = entityManager.createQuery(criteriaQuery.select(root).where(predicates.toArray(new Predicate[predicates.size()])));
        return query.getResultList();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<ChangeId> findChangeIdByCrId(String crId) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(ChangeId.class);
        return entityManager.createQuery(criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("crId"), crId))).getResultList();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<Material> getMaterialsForOrderLine(long orderLineOid) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(Material.class);
        Join materialHeaderJoin = root.join("materialHeader", JoinType.LEFT);
        Join acceptedHeaderVersionJoin = materialHeaderJoin.join("accepted", JoinType.LEFT);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.and(root.get("orderLine").get("orderLineOID").in(orderLineOid)));
        Predicate wherePredicate = criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()]));

        List<javax.persistence.criteria.Order> orderByColumns = new ArrayList<javax.persistence.criteria.Order>();
        orderByColumns.add(criteriaBuilder.asc(root.get("materialType")));
        orderByColumns.add(criteriaBuilder.asc(criteriaBuilder.selectCase().when(acceptedHeaderVersionJoin.get("outboundStartDate").isNull(), 0).otherwise(1)));
        orderByColumns.add(criteriaBuilder.desc(acceptedHeaderVersionJoin.get("outboundStartDate")));

        Query resultSetQuery = entityManager.createQuery(criteriaQuery.select(root).distinct(true).where(wherePredicate).orderBy(orderByColumns));
        return resultSetQuery.getResultList();
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<MaterialLine> findMaterialLinesByOrderLineList(List<Long> orderLineList) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(MaterialLine.class);
        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root).distinct(true)
                                                               .where(root.get("material").get("orderLine").get("orderLineOID").in(orderLineList));

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        return resultSetQuery.getResultList();
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public long countPicks(Date fromDate, Date toDate, String[] project, String[] warehouse) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root<DeliveryNoteLine> root = criteriaQuery.from(DeliveryNoteLine.class);

        Join materialLine = root.join("materialLine", JoinType.LEFT);
        Join material = materialLine.join("material", JoinType.LEFT);
        Join requestGroup = materialLine.join("requestGroup", JoinType.INNER);
        Join requestList = requestGroup.join("requestList", JoinType.INNER);
        Join materialOwner = materialLine.join("materialOwner", JoinType.INNER);
        Join financeHeader = materialOwner.join("financeHeader", JoinType.INNER);
        Join materialLineStatusTime = materialLine.join("materialLineStatusTime", JoinType.INNER);
        Join pickList = materialLine.join("pickList", JoinType.INNER);
        
        Expression<String> concat1 = criteriaBuilder.concat((Expression<String>) material.get("partNumber"), (Expression<String>) material.get("partVersion"));
        Expression<String> concat2 = criteriaBuilder.concat((Expression<String>) material.get("partName"),
                                                            (Expression<String>) material.get("partModification"));
        Expression<String> fullconcat = criteriaBuilder.concat(concat1, concat2);

        Expression<String> partSelectionExpression = criteriaBuilder.concat((Expression<String>) material.get("partAffiliation"), fullconcat);
        Expression<String> pickListSelectionExpression = pickList.get("code");

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        List<Predicate> deliveryDatePredicateList = new ArrayList<Predicate>();
        List<Predicate> reqListDatePredicateList = new ArrayList<Predicate>();
        Path<Date> pathDeliveryDateTime = materialLineStatusTime.get("pickedTime");
        Path<Date> pathRequestListDate = requestList.get("createdDate");

        deliveryDatePredicateList.add(criteriaBuilder.isNotNull(pickList.get("code")));
        if (fromDate != null) {
            deliveryDatePredicateList.add(criteriaBuilder.greaterThanOrEqualTo(pathDeliveryDateTime, fromDate));
        }
        if (toDate != null) {
            deliveryDatePredicateList.add(criteriaBuilder.lessThan(pathDeliveryDateTime, toDate));
        }
        Predicate deliveryDatePredicate = criteriaBuilder.and(deliveryDatePredicateList.toArray(new Predicate[deliveryDatePredicateList.size()]));

        reqListDatePredicateList.add(criteriaBuilder.isNull(pickList.get("code")));
        if (fromDate != null) {
            reqListDatePredicateList.add(criteriaBuilder.greaterThanOrEqualTo(pathRequestListDate, fromDate));
        }
        if (toDate != null) {
            reqListDatePredicateList.add(criteriaBuilder.lessThan(pathRequestListDate, toDate));
        }
        Predicate reqListDatePredicate = criteriaBuilder.and(reqListDatePredicateList.toArray(new Predicate[reqListDatePredicateList.size()]));

        predicatesRules.add(criteriaBuilder.or(deliveryDatePredicate, reqListDatePredicate));
        predicatesRules.add(criteriaBuilder.notEqual(requestList.get("status"), RequestListStatus.CREATED));
        predicatesRules.add(criteriaBuilder.notEqual(requestList.get("status"), RequestListStatus.SENT));

        predicatesRules.add(materialLine.get("status").in(MaterialLineStatus.READY_TO_SHIP, MaterialLineStatus.SHIPPED, MaterialLineStatus.IN_TRANSFER));
        predicatesRules.add(criteriaBuilder.isNotNull(materialLine.get("requestGroup")));

        if (project != null && project.length > 0) {
            predicatesRules.add(criteriaBuilder.in(financeHeader.get("projectId")).value(Arrays.asList(project)));
        }

        if (warehouse != null && warehouse.length > 0) {
            predicatesRules.add(criteriaBuilder.in(materialLine.get("whSiteId")).value(Arrays.asList(warehouse)));
        }

        List<Selection<Object>> columnSelections = new ArrayList<Selection<Object>>();
        columnSelections.add(pickList.get("code"));
        columnSelections.add(criteriaBuilder.selectCase().when(criteriaBuilder.isNull(pickList.get("code")), requestList.get("requestListOid")));
        columnSelections.add(criteriaBuilder.selectCase().when(criteriaBuilder.isNull(pickList.get("code")), requestList.get("createdDate")));
        columnSelections.add(material.get("partAffiliation"));
        columnSelections.add(material.get("partNumber"));
        columnSelections.add(material.get("partVersion"));
        columnSelections.add(material.get("partName"));
        columnSelections.add(material.get("partModification"));
        
        criteriaQuery.where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));
        criteriaQuery.multiselect(columnSelections.toArray(new Selection[columnSelections.size()])).distinct(true);
        TypedQuery<Tuple> query = entityManager.createQuery(criteriaQuery);
        return Long.valueOf(query.getResultList().size());
    }
    
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<MaterialLine> getMaterialLinesByRequestListId(long requestListOid) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(MaterialLine.class);
        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root).distinct(true)
                                                               .where(root.get("requestGroup").get("requestList").get("requestListOid").in(requestListOid));

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        return resultSetQuery.getResultList();
    }
    
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<PickList> findPickListByRequestListId(long requestListOid) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(PickList.class);
        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root).distinct(true)
                                                               .where(root.get("requestGroups").get("requestList").get("requestListOid").in(requestListOid));

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        return resultSetQuery.getResultList();
    }
    
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<MaterialLine> findProcuredMaterialLinesByMaterialLineIds(List<Long> materialLineIds) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(MaterialLine.class);
        Join materialJoin = root.join("material", JoinType.LEFT);
        Join materialHeaderJoin = materialJoin.join("materialHeader", JoinType.LEFT);
        Join acceptedHeaderVersionJoin = materialHeaderJoin.join("accepted", JoinType.LEFT);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.and(root.get("materialLineOID").in(materialLineIds)));
        predicatesRules.add(criteriaBuilder.and(root.get("status").in(MaterialLineStatus.ORDER_PLACED_EXTERNAL, MaterialLineStatus.ORDER_PLACED_INTERNAL)));
        Predicate wherePredicate = criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()]));

        List<javax.persistence.criteria.Order> orderByColumns = new ArrayList<javax.persistence.criteria.Order>();
        orderByColumns.add(criteriaBuilder.desc(materialJoin.get("materialType")));
        orderByColumns.add(criteriaBuilder.desc(criteriaBuilder.selectCase().when(acceptedHeaderVersionJoin.get("outboundStartDate").isNull(), 0).otherwise(1)));
        orderByColumns.add(criteriaBuilder.asc(acceptedHeaderVersionJoin.get("outboundStartDate")));

        Query resultSetQuery = entityManager.createQuery(criteriaQuery.select(root).distinct(true).where(wherePredicate).orderBy(orderByColumns));
        return resultSetQuery.getResultList();
    }
}
