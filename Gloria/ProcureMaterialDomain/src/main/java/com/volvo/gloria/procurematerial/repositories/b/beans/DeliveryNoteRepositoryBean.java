package com.volvo.gloria.procurematerial.repositories.b.beans;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.volvo.gloria.common.c.dto.reports.ReportWarehouseTransactionDTO;
import com.volvo.gloria.procurematerial.b.DeliveryServices;
import com.volvo.gloria.procurematerial.c.DeliveryNoteLineStatus;
import com.volvo.gloria.procurematerial.c.QiMarking;
import com.volvo.gloria.procurematerial.c.dto.DeliveryNoteLineDTO;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNote;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteSubLine;
import com.volvo.gloria.procurematerial.d.entities.GoodsReceiptHeader;
import com.volvo.gloria.procurematerial.d.entities.GoodsReceiptLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLineVersion;
import com.volvo.gloria.procurematerial.d.entities.ProblemDoc;
import com.volvo.gloria.procurematerial.d.entities.QiDoc;
import com.volvo.gloria.procurematerial.d.entities.ReceiveDoc;
import com.volvo.gloria.procurematerial.d.entities.TransportLabel;
import com.volvo.gloria.procurematerial.d.entities.status.goodsReceiptLine.GoodsReceiptLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.orderLine.OrderLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.requestlist.RequestListStatus;
import com.volvo.gloria.procurematerial.d.type.receive.ReceiveType;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;
import com.volvo.gloria.procurematerial.util.DeliveryHelper;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.GloriaFormateUtil;
import com.volvo.gloria.util.paging.c.JpaAttribute;
import com.volvo.gloria.util.paging.c.JpaAttributeType;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.paging.c.PageResults;
import com.volvo.gloria.util.persistence.GenericAbstractRepositoryBean;
import com.volvo.gloria.warehouse.b.WarehouseServices;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * repository for DeliveryNote.
 * 
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class DeliveryNoteRepositoryBean extends GenericAbstractRepositoryBean<DeliveryNote, Long> implements DeliveryNoteRepository {

    @Inject
    private WarehouseServices warehouseServices;

    @Inject
    private DeliveryServices deliveryServices;

    @PersistenceContext(unitName = "ProcureMaterialDomainPU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<DeliveryNoteLine> findDeliveryNoteLinesByDeliveryNoteId(Long deliveryNoteOid, String whSiteId, DeliveryNoteLineStatus status,
            ReceiveType receiveType) throws GloriaApplicationException {
        Query query = getEntityManager().createNamedQuery("findDeliveryNoteLinesByDeliveryNoteId");
        query.setParameter("deliveryNoteOid", deliveryNoteOid);
        query.setParameter("whSiteId", whSiteId);
        query.setParameter("status", status);
        if (receiveType != null && receiveType.equals(ReceiveType.REGULAR)) {
            query.setParameter("orderLineStatus", OrderLineStatus.COMPLETED);
        } else {
            query.setParameter("orderLineStatus", null);
        }
        return query.getResultList();
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public DeliveryNoteLine findDeliveryNoteLineById(long deliveryNoteLineId) {
        Query query = getEntityManager().createNamedQuery("findDeliveryNoteLineId");
        query.setParameter("deliveryNoteLineOID", deliveryNoteLineId);
        List<DeliveryNoteLine> resultList = query.getResultList();
        if (resultList != null && resultList.size() > 0) {
            return resultList.get(0);
        }
        return null;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public DeliveryNote findDeliveryNote(String orderNo, String deliveryNoteNo, ReceiveType receiveType, Date date) {
        List<Predicate> predicates = new ArrayList<Predicate>();
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(DeliveryNote.class);

        if (!StringUtils.isEmpty(orderNo)) {
            predicates.add(criteriaBuilder.equal(root.get("orderNo"), orderNo));
        }

        if (!StringUtils.isEmpty(deliveryNoteNo)) {
            predicates.add(criteriaBuilder.equal(root.get("deliveryNoteNo"), deliveryNoteNo));
        }

        if (date != null) {
            Date dateRangeFrom = DateUtil.getDateWithZeroTime(date);
            Date dateRangeTo = DateUtil.getNextDate(dateRangeFrom);
            Path<Date> datePath = (Path<Date>) root.get("deliveryNoteDate");
            predicates.add(criteriaBuilder.greaterThanOrEqualTo((Expression) datePath, dateRangeFrom));
            predicates.add(criteriaBuilder.lessThan((Expression) datePath, dateRangeTo));
        }

        predicates.add(criteriaBuilder.equal(root.get("receiveType"), receiveType));

        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root);

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);
        List<DeliveryNote> deliveryNotes = resultSetQuery.getResultList();
        if (deliveryNotes != null && !deliveryNotes.isEmpty()) {
            return deliveryNotes.get(0);
        }
        return null;
    }

    @Override
    public DeliveryNoteLine save(DeliveryNoteLine instanceToSave) {
        DeliveryNoteLine deliveryNoteLineTosave = instanceToSave;
        if (instanceToSave.getDeliveryNoteLineOID() < 1) {
            getEntityManager().persist(instanceToSave);
        } else {
            deliveryNoteLineTosave = getEntityManager().merge(instanceToSave);
        }
        return deliveryNoteLineTosave;
    }

    @Override
    public void delete(DeliveryNoteLine deliveryNoteLine) {
        getEntityManager().remove(deliveryNoteLine);
    }

    @Override
    public QiDoc save(QiDoc instanceToSave) {
        QiDoc qualityDocToSave = instanceToSave;
        if (instanceToSave.getQiDocOID() < 1) {
            getEntityManager().persist(instanceToSave);
        } else {
            qualityDocToSave = getEntityManager().merge(instanceToSave);
        }
        return qualityDocToSave;
    }

    @Override
    public ProblemDoc save(ProblemDoc instanceToSave) {
        ProblemDoc problemNoteDocToSave = instanceToSave;
        if (instanceToSave.getProblemDocOID() < 1) {
            getEntityManager().persist(instanceToSave);
        } else {
            problemNoteDocToSave = getEntityManager().merge(instanceToSave);
        }
        return problemNoteDocToSave;
    }

    @Override
    public QiDoc findQualityDocById(long qualityDocId) {
        return getEntityManager().find(QiDoc.class, qualityDocId);
    }

    @Override
    public void delete(QiDoc qualityDoc) {
        getEntityManager().remove(qualityDoc);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<QiDoc> findQiDocs(long deliveryNoteLineId) {
        Query query = getEntityManager().createNamedQuery("findQiDocsByDeliveryNoteLineId");
        query.setParameter("id", deliveryNoteLineId);
        return query.getResultList();
    }

    @Override
    public ProblemDoc findProblemDocById(long problemNoteDocId) {
        return getEntityManager().find(ProblemDoc.class, problemNoteDocId);
    }

    @Override
    public void delete(ProblemDoc problemDoc) {
        getEntityManager().remove(problemDoc);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ProblemDoc> findProblemDocs(long deliveryNoteLineId) {
        Query query = getEntityManager().createNamedQuery("findProblemDocsByDeliveryNoteLineId");
        query.setParameter("id", deliveryNoteLineId);
        return query.getResultList();
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<DeliveryNoteLine> findDeliveryNoteLinesByOrderLineId(long orderLineId, DeliveryNoteLineStatus status) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(DeliveryNoteLine.class);

        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(criteriaBuilder.equal(root.get("orderLine").get("orderLineOID"), orderLineId));
        if (status != null) {
            predicates.add(criteriaBuilder.equal(root.get("status"), status));
            if (status == DeliveryNoteLineStatus.RECEIVED) {
                predicates.add(criteriaBuilder.gt(root.get("receivedQuantity"), 0L));
            }
        }
        Query query = entityManager.createQuery(criteriaQuery.select(root).where(predicates.toArray(new Predicate[predicates.size()])));
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<TransportLabel> findTransportLabelByWhSiteId(String whSite) {
        Query query = getEntityManager().createNamedQuery("findTransportLabelByWhSiteId");
        query.setParameter("whSiteId", whSite);
        return query.getResultList();
    }

    @Override
    public TransportLabel save(TransportLabel instanceToSave) {
        TransportLabel transportLabel = instanceToSave;
        if (instanceToSave.getTransportLabelOid() < 1) {
            getEntityManager().persist(instanceToSave);
        } else {
            transportLabel = getEntityManager().merge(instanceToSave);
        }
        return transportLabel;
    }

    @Override
    public TransportLabel findTransportLabelById(long transportLabelId) {
        return getEntityManager().find(TransportLabel.class, transportLabelId);
    }

    @Override
    public TransportLabel findTransportLabelByIdAndWhSiteId(long transportlabelID, String whSite) {
        TransportLabel transportLabel = findTransportLabelById(transportlabelID);
        if (transportLabel != null && !StringUtils.isEmpty(whSite) && transportLabel.getWhSiteId().equals(whSite)) {
            return transportLabel;
        }
        return null;
    }

    @Override
    public GoodsReceiptLine save(GoodsReceiptLine goodsReceiptLine) {
        GoodsReceiptLine toSave = goodsReceiptLine;
        if (goodsReceiptLine.getGoodsReceiptLineOId() < 1) {
            getEntityManager().persist(goodsReceiptLine);
        } else {
            toSave = getEntityManager().merge(goodsReceiptLine);
        }
        return toSave;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public GoodsReceiptLine findGoodsReceiptLineById(long goodsReceiptLineOId) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(GoodsReceiptLine.class);

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("goodsReceiptLineOId"), goodsReceiptLineOId));

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        List resultList = resultSetQuery.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (GoodsReceiptLine) resultList.get(0);
        }
        return null;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public List<GoodsReceiptLine> findAllGoodsReceiptLine() {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(GoodsReceiptLine.class);

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root);

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        return resultSetQuery.getResultList();
    }

    @Override
    public void deleteGoodsReceiptLine(GoodsReceiptLine goodsReceiptLine) {
        getEntityManager().remove(goodsReceiptLine);

    }

    @Override
    public GoodsReceiptHeader save(GoodsReceiptHeader goodsReceiptHeader) {
        GoodsReceiptHeader toSave = goodsReceiptHeader;
        if (goodsReceiptHeader.getGoodReceiptHeaderOId() < 1) {
            getEntityManager().persist(goodsReceiptHeader);
        } else {
            toSave = getEntityManager().merge(goodsReceiptHeader);
        }
        return toSave;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public GoodsReceiptHeader findGoodsReceiptHeaderById(long goodsReceiptHeaderOId) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(GoodsReceiptHeader.class);

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("goodReceiptHeaderOId"),
                                                                                                         goodsReceiptHeaderOId));

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        List resultList = resultSetQuery.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (GoodsReceiptHeader) resultList.get(0);
        }
        return null;
    }

    @Override
    public void deleteGoodsReceiptHeader(GoodsReceiptHeader goodsReceiptHeader) {
        getEntityManager().remove(goodsReceiptHeader);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public List<GoodsReceiptHeader> findAllGoodsReceiptHeaders() {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(GoodsReceiptHeader.class);

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root);

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        return resultSetQuery.getResultList();
    }

    @Override
    public ReceiveDoc save(ReceiveDoc receiveDoc) {
        ReceiveDoc inspectionDocToSave = receiveDoc;
        if (receiveDoc.getRecieveDocOID() < 1) {
            getEntityManager().persist(receiveDoc);
        } else {
            inspectionDocToSave = getEntityManager().merge(receiveDoc);
        }
        return inspectionDocToSave;
    }

    @Override
    public ReceiveDoc findReceiveDocById(long inspectionDocId) {
        return getEntityManager().find(ReceiveDoc.class, inspectionDocId);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<ReceiveDoc> findInspectionDocs(long deliveryNoteLineOId) {

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(ReceiveDoc.class);

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("deliveryNoteLine")
                                                                                                             .get("deliveryNoteLineOID"), deliveryNoteLineOId));

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        return resultSetQuery.getResultList();
    }

    @Override
    public void delete(ReceiveDoc inspectionDoc) {
        getEntityManager().remove(inspectionDoc);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public List<OrderLineVersion> findAllOrderLineVersions(long orderLineOid) {

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(OrderLineVersion.class);

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("orderLine").get("orderLineOID"),
                                                                                                         orderLineOid));

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        List resultList = resultSetQuery.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (List<OrderLineVersion>) resultList;
        }
        return new ArrayList<OrderLineVersion>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<GoodsReceiptHeader> findAllGoodsMovementsForCompanyCode(String companyCode) {
        Query query = getEntityManager().createNamedQuery("findAllGoodsMovementsForCompanyCode");
        query.setParameter("comapnyCode", companyCode);
        return query.getResultList();
    }

    @Override
    public DeliveryNoteSubLine findDeliveryNoteSubLineById(long deliveryNoteSubLineid) {
        return getEntityManager().find(DeliveryNoteSubLine.class, deliveryNoteSubLineid);
    }

    @Override
    public DeliveryNoteSubLine save(DeliveryNoteSubLine instanceToSave) {
        DeliveryNoteSubLine deliveryNoteSubLineTosave = instanceToSave;
        if (instanceToSave.getDeliveryNoteSubLineOID() < 1) {
            getEntityManager().persist(instanceToSave);
        } else {
            deliveryNoteSubLineTosave = getEntityManager().merge(instanceToSave);
        }
        return deliveryNoteSubLineTosave;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<DeliveryNoteSubLine> findDeliveryNoteSubLinesLineById(long deliveryNoteLineOid) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(DeliveryNoteSubLine.class);

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root)
                                                               .where(criteriaBuilder.equal(root.get("deliveryNoteLine").get("deliveryNoteLineOID"),
                                                                                            deliveryNoteLineOid))
                                                               .orderBy(criteriaBuilder.desc((Expression) root.get("directSend")),
                                                                        criteriaBuilder.desc((Expression) root.get("deliveryNoteLine")
                                                                                                              .get("deliveryNoteLineOID")));

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        return resultSetQuery.getResultList();
    }

    @Override
    public void delete(DeliveryNoteSubLine deliveryNoteSubLine) {
        getEntityManager().remove(deliveryNoteSubLine);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public PageObject findDeliveryNoteLinesForQI(PageObject pageObject, String materialLineStatus, String qiMarking, String whSiteId, boolean needSublines)
            throws GloriaApplicationException {

        Map<String, JpaAttribute> materialLineFields = new HashMap<String, JpaAttribute>();

        materialLineFields.put("partNumber", new JpaAttribute("partNumber", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("partVersion", new JpaAttribute("partVersion", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("partName", new JpaAttribute("partName", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("pPartModification", new JpaAttribute("partModification", JpaAttributeType.STRINGTYPE));
        materialLineFields.put("binLocation", new JpaAttribute("materialLine.binLocationCode", JpaAttributeType.STRINGTYPE));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(DeliveryNoteLine.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();

        if (!StringUtils.isEmpty(materialLineStatus)) {
            predicatesRules.add(criteriaBuilder.isMember(MaterialLineStatus.valueOf(materialLineStatus), root.get("materialLine").get("status")));
        }

        if (!StringUtils.isEmpty(whSiteId)) {
            predicatesRules.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("deliveryNote").get("whSiteId")), whSiteId.toLowerCase()));
        }

        if (!StringUtils.isEmpty(qiMarking)) {
            Path<String> path = root.get("orderLine").get("qiMarking");
            Predicate predicateIn = path.in(GloriaFormateUtil.getValuesAsEnums(qiMarking, QiMarking.class));
            predicatesRules.add(criteriaBuilder.and(predicateIn));
        }

        CriteriaQuery criteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject,
                                                                          materialLineFields, true);
        Query countQuery = entityManager.createQuery(criteriaQueryFromPageObject);
        pageObject.setCount(((Long) countQuery.getSingleResult()).intValue());

        CriteriaQuery resultSetCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject,
                                                                                   materialLineFields, false);
        Query resultSetQuery = entityManager.createQuery(resultSetCriteriaQueryFromPageObject);
        resultSetQuery.setFirstResult(pageObject.getFirstResult());
        resultSetQuery.setMaxResults(pageObject.getMaxResult());

        List<DeliveryNoteLine> deliveryNoteLines = resultSetQuery.getResultList();
        if (deliveryNoteLines != null && !deliveryNoteLines.isEmpty()) {
            List<DeliveryNoteLineDTO> deliveryNoteLineDTOs = new ArrayList<DeliveryNoteLineDTO>();
            for (DeliveryNoteLine deliveryNoteLine : deliveryNoteLines) {
                DeliveryNoteLineDTO deliveryNoteLineDTO = DeliveryHelper.transformAsDTO(deliveryNoteLine, materialLineStatus);
                if (needSublines) {
                    deliveryNoteLineDTO.setDeliveryNoteSubLineDTOs(DeliveryHelper.transaformAsDeliveryNoteSubLineDTOs(DeliveryHelper.manageSublinesForQI(materialLineStatus,
                                                                                                                                                         deliveryNoteLine,
                                                                                                                                                         deliveryNoteLine.getDeliveryNoteSubLines(),
                                                                                                                                                         warehouseServices,
                                                                                                                                                         deliveryServices)));
                }
                deliveryNoteLineDTOs.add(deliveryNoteLineDTO);
            }
            pageObject.setGridContents(new ArrayList<PageResults>(deliveryNoteLineDTOs));
        }
        return pageObject;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public PageObject findGoodsReceiptLinesByPlant(PageObject pageObject, String plant, String status) {

        Map<String, JpaAttribute> goodsReceiptLineFields = new HashMap<String, JpaAttribute>();

        goodsReceiptLineFields.put("deliveryNoteNo", new JpaAttribute("deliveryNoteLine.deliveryNote.deliveryNoteNo", JpaAttributeType.STRINGTYPE));
        goodsReceiptLineFields.put("orderReference", new JpaAttribute("deliveryNoteLine.deliveryNote.orderNo", JpaAttributeType.STRINGTYPE));
        goodsReceiptLineFields.put("receivalDate", new JpaAttribute("deliveryNoteLine.receivedDateTime", JpaAttributeType.DATETYPE));
        goodsReceiptLineFields.put("projectId", new JpaAttribute("deliveryNoteLine.projectId", JpaAttributeType.STRINGTYPE));
        goodsReceiptLineFields.put("referenceId", new JpaAttribute("deliveryNoteLine.referenceIds", JpaAttributeType.STRINGTYPE));
        goodsReceiptLineFields.put("partName", new JpaAttribute("deliveryNoteLine.partName", JpaAttributeType.STRINGTYPE));
        goodsReceiptLineFields.put("partNumber", new JpaAttribute("deliveryNoteLine.partNumber", JpaAttributeType.STRINGTYPE));
        goodsReceiptLineFields.put("partVersion", new JpaAttribute("deliveryNoteLine.partVersion", JpaAttributeType.STRINGTYPE));
        goodsReceiptLineFields.put("partModification", new JpaAttribute("deliveryNoteLine.partModification", JpaAttributeType.STRINGTYPE));
        goodsReceiptLineFields.put("quantity", new JpaAttribute("quantity", JpaAttributeType.NUMBERTYPE));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(GoodsReceiptLine.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.equal(root.get("deliveryNoteLine").get("deliveryNote").get("whSiteId"), plant));
        predicatesRules.add(root.get("status").in(GloriaFormateUtil.getValuesAsEnums(status, GoodsReceiptLineStatus.class)));

        CriteriaQuery criteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject,
                                                                          goodsReceiptLineFields, true);
        Query countQuery = entityManager.createQuery(criteriaQueryFromPageObject);
        pageObject.setCount(((Long) countQuery.getSingleResult()).intValue());

        CriteriaQuery resultSetCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject,
                                                                                   goodsReceiptLineFields, false);
        Query resultSetQuery = entityManager.createQuery(resultSetCriteriaQueryFromPageObject);
        resultSetQuery.setFirstResult(pageObject.getFirstResult());
        resultSetQuery.setMaxResults(pageObject.getMaxResult());

        List<GoodsReceiptLine> goodsReceiptLines = resultSetQuery.getResultList();
        if (goodsReceiptLines != null && !goodsReceiptLines.isEmpty()) {
            pageObject.setGridContents(new ArrayList<PageResults>(DeliveryHelper.transformAsGoodsReceiptLineDTOs(goodsReceiptLines)));
        }
        return pageObject;
    }

    @Override
    public List<Tuple> findDeliveryNoteByTypeAndProject(Date fromDate, Date toDate, String[] projectIds, String[] whSiteId) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root<DeliveryNote> root = criteriaQuery.from(DeliveryNote.class);
        Path<String> deliveryNoteNoPath = root.get("deliveryNoteNo");
        // add date
        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        // this has been added to keep migrated data out of reports
        predicatesRules.add(criteriaBuilder.notLike(deliveryNoteNoPath, "MIG-%"));

        Path<Timestamp> path = root.get("deliveryNoteDate");
        if (fromDate != null) {
            predicatesRules.add(criteriaBuilder.greaterThanOrEqualTo(path, new Timestamp(fromDate.getTime())));
        }
        if (toDate != null) {
            predicatesRules.add(criteriaBuilder.lessThan(path, new Timestamp(toDate.getTime())));
        }
        if (projectIds != null && projectIds.length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("deliveryNoteLine").get("projectId")).value(Arrays.asList(projectIds)));
        }
        if (whSiteId != null && whSiteId.length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("whSiteId")).value(Arrays.asList(whSiteId)));
        }
        TypedQuery<Tuple> query = entityManager.createQuery(criteriaQuery.select(criteriaBuilder.tuple(criteriaBuilder.count(root.get("deliveryNoteOID")),
                                                                                                       root.get("receiveType")))
                                                                         .where(predicatesRules.toArray(new Predicate[predicatesRules.size()]))
                                                                         .groupBy(root.get("receiveType")));

        return query.getResultList();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List<Tuple> getTransactionReceivalsReportData(ReportWarehouseTransactionDTO reportWarehouseTransactionDTO, Date frmDate, Date toDate,
            String[] projectIds) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root<DeliveryNoteLine> root = criteriaQuery.from(DeliveryNoteLine.class);
        List<Selection> columnSelections = getColumnSelectionsForTransactionReceivalsReport(root);
        columnSelections.add(criteriaBuilder.literal("EUR"));
        List<Predicate> predicatesRules = getPredicateRulesForTransactionReceivalsReport(reportWarehouseTransactionDTO, criteriaBuilder, root, frmDate, toDate,
                                                                                         projectIds);
        criteriaQuery.where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));
        criteriaQuery.multiselect(columnSelections.toArray(new Selection[columnSelections.size()])).distinct(true);
        TypedQuery<Tuple> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }

    @SuppressWarnings("rawtypes")
    private static List<Selection> getColumnSelectionsForTransactionReceivalsReport(Root<DeliveryNoteLine> root) {
        List<Selection> columnSelections = new ArrayList<Selection>();
        Join materialLine = root.join("materialLine", JoinType.LEFT);
        Join material = materialLine.join("material", JoinType.LEFT);
        Join materialHeader = material.join("materialHeader", JoinType.LEFT);
        columnSelections.add(root.get("deliveryNote").get("deliveryNoteNo"));
        columnSelections.add(root.get("deliveryNote").get("deliveryNoteDate"));
        columnSelections.add(root.get("deliveryNote").get("orderNo"));
        columnSelections.add(root.get("orderLine").get("order").get("orderDateTime"));
        columnSelections.add(root.get("orderLine").get("current").get("quantity"));
        columnSelections.add(root.get("receivedQuantity"));
        columnSelections.add(root.get("goodsReceiptLines").get("quantityCancelled"));
        columnSelections.add(root.get("damagedQuantity"));
        columnSelections.add(root.get("goodsReceiptLines").get("status"));
        columnSelections.add(root.get("deliveryNote").get("supplierId"));
        columnSelections.add(root.get("deliveryNote").get("supplierName"));
        columnSelections.add(root.get("orderLine").get("current").get("orderStaDate"));
        columnSelections.add(root.get("receivedDateTime"));
        columnSelections.add(root.get("partAffiliation"));
        columnSelections.add(root.get("partNumber"));
        columnSelections.add(root.get("partVersion"));
        columnSelections.add(root.get("partName"));
        columnSelections.add(root.get("partModification"));
        columnSelections.add(root.get("partAlias"));
        columnSelections.add(root.get("projectId"));
        columnSelections.add(materialHeader.get("accepted").get("contactPersonId"));
        columnSelections.add(root.get("orderLine").get("deliveryControllerUserId"));
        columnSelections.add(root.get("orderLine").get("deliveryControllerUserName"));
        columnSelections.add(root.get("orderLine").get("current").get("unitPriceInEuro"));
        return columnSelections;
    }

    private static List<Predicate> getPredicateRulesForTransactionReceivalsReport(ReportWarehouseTransactionDTO reportWarehouseTransactionDTO,
            CriteriaBuilder criteriaBuilder, Root<DeliveryNoteLine> root, Date fromDate, Date toDate, String[] projectIds) {
        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        Path<DeliveryNote> deliveryNote = root.get("deliveryNote");
        Path<Date> pathDeliveryDateTime = deliveryNote.get("deliveryNoteDate");
        if (fromDate != null) {
            predicatesRules.add(criteriaBuilder.greaterThanOrEqualTo(pathDeliveryDateTime, fromDate));
        }
        if (toDate != null) {
            predicatesRules.add(criteriaBuilder.lessThan(pathDeliveryDateTime, toDate));
        }
        if (projectIds != null && projectIds.length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("projectId")).value(Arrays.asList(projectIds)));
        }
        if (reportWarehouseTransactionDTO.getWarehouse().length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("materialLine").get("whSiteId"))
                                .value(Arrays.asList(reportWarehouseTransactionDTO.getWarehouse())));
        }
        predicatesRules.add(criteriaBuilder.equal(root.get("deliveryNote").get("receiveType"), ReceiveType.REGULAR));
        predicatesRules.add(criteriaBuilder.equal(root.get("status"), DeliveryNoteLineStatus.RECEIVED));
        return predicatesRules;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List<Tuple> getTransactionPicksReportData(ReportWarehouseTransactionDTO reportWarehouseTransactionDTO, Date fromDate, Date toDate) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root<DeliveryNoteLine> root = criteriaQuery.from(DeliveryNoteLine.class);

        Join materialLine = root.join("materialLine", JoinType.LEFT);
        Join material = materialLine.join("material", JoinType.LEFT);
        Join requestGroup = materialLine.join("requestGroup", JoinType.INNER);
        Join requestList = requestGroup.join("requestList", JoinType.INNER);
        Join materialOwner = materialLine.join("materialOwner", JoinType.INNER);
        Join financeHeader = materialOwner.join("financeHeader", JoinType.INNER);
        Join materialLineStatusTime = materialLine.join("materialLineStatusTime", JoinType.INNER);
        Join partAlias = material.join("procureLine", JoinType.LEFT).join("partAlias", JoinType.LEFT);
        Join pickList = materialLine.join("pickList", JoinType.INNER);
        
        List<Selection<Object>> columnSelections = getColumnSelectionsForTransactionPicksReport(criteriaBuilder, pickList, partAlias, material,
                                                                                                materialLine, requestList, financeHeader,
                                                                                                materialLineStatusTime);

        List<Predicate> predicatesRules = getPredicateRulesForTransactionPicksReport(reportWarehouseTransactionDTO, criteriaBuilder, fromDate, toDate,
                                                                                     materialLine, materialLineStatusTime, requestList, pickList);
        criteriaQuery.where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));
        criteriaQuery.multiselect(columnSelections.toArray(new Selection[columnSelections.size()]));

        List<Expression<Object>> groupByExpressions = getGroupByRulesForTransactionPicksReport(criteriaBuilder, pickList, partAlias, material,
                                                                                               materialLine, requestList, financeHeader,
                                                                                               materialLineStatusTime);
        criteriaQuery.groupBy(groupByExpressions.toArray(new Expression[groupByExpressions.size()]));
        TypedQuery<Tuple> query = entityManager.createQuery(criteriaQuery);

        return query.getResultList();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List<Expression<Object>> getGroupByRulesForTransactionPicksReport(CriteriaBuilder criteriaBuilder, Join pickList,
            Join partAlias, Join material, Join materialLine, Join requestList, Join financeHeader, Join materialLineStatusTime) {
        List groupByItems = new ArrayList<Expression<Object>>();
        groupByItems.add(materialLine.get("orderNo"));
        groupByItems.add(financeHeader.get("projectId"));
        groupByItems.add(material.get("mtrlRequestVersionAccepted"));
        groupByItems.add(requestList.get("requestUserId"));
        groupByItems.add(pickList.get("pulledByUserId"));
        groupByItems.add(requestList.get("requestListOid"));
        groupByItems.add(pickList.get("code"));
        groupByItems.add(material.get("partAffiliation"));
        groupByItems.add(material.get("partNumber"));
        groupByItems.add(material.get("partVersion"));
        groupByItems.add(material.get("partName"));
        groupByItems.add(material.get("partModification"));
        groupByItems.add(materialLineStatusTime.get("storedStorageRoom"));
        groupByItems.add(materialLineStatusTime.get("storedBinLocation"));
        groupByItems.add(partAlias.get("domain"));
        return groupByItems;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private List<Predicate> getPredicateRulesForTransactionPicksReport(ReportWarehouseTransactionDTO reportWarehouseTransactionDTO,
            CriteriaBuilder criteriaBuilder, Date fromDate, Date toDate, Join materialLine, Join materialLineStatusTime,
            Join requestList, Join pickList) {
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
        
        String[] warehouses = reportWarehouseTransactionDTO.getWarehouse();
        if (warehouses != null && warehouses.length > 0) {
            predicatesRules.add(criteriaBuilder.in(materialLine.get("whSiteId")).value(Arrays.asList(reportWarehouseTransactionDTO.getWarehouse())));
        }
        return predicatesRules;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List<Selection<Object>> getColumnSelectionsForTransactionPicksReport(CriteriaBuilder criteriaBuilder, Join pickList,
            Join partAlias, Join material, Join materialLine, Join requestList, Join financeHeader, Join materialLineStatusTime) {

        List columnSelections = new ArrayList<Selection<Object>>();
        columnSelections.add(materialLine.get("orderNo"));
        columnSelections.add(financeHeader.get("projectId"));
        columnSelections.add(material.get("mtrlRequestVersionAccepted"));
        columnSelections.add(requestList.get("requiredDeliveryDate"));
        columnSelections.add(requestList.get("requestUserId"));
        columnSelections.add(requestList.get("createdDate"));
        columnSelections.add(criteriaBuilder.selectCase()
                                                 .when(criteriaBuilder.isNull(pickList.get("code")), requestList.get("createdDate"))
                                                 .otherwise(criteriaBuilder.max((Expression) materialLineStatusTime.get("pickedTime"))));
        columnSelections.add(pickList.get("pulledByUserId"));
        columnSelections.add(criteriaBuilder.sum((Expression) criteriaBuilder.selectCase()
                                                                             .when(criteriaBuilder.isNull(pickList.get("code")), materialLine.get("quantity"))
                                                                             .otherwise(materialLineStatusTime.get("requestedQty"))));
        columnSelections.add(criteriaBuilder.sum((Expression) criteriaBuilder.selectCase()
                                                 .when(criteriaBuilder.isNull(pickList.get("code")), materialLine.get("quantity"))
                                                 .otherwise(materialLineStatusTime.get("pickedQty"))));
        columnSelections.add(criteriaBuilder.max((Expression) materialLineStatusTime.get("shippedTime")));
        columnSelections.add(requestList.get("requestListOid"));
        columnSelections.add(pickList.get("code"));

        columnSelections.add(material.get("partAffiliation"));
        columnSelections.add(material.get("partNumber"));
        columnSelections.add(material.get("partVersion"));
        columnSelections.add(material.get("partName"));
        columnSelections.add(material.get("partModification"));
        columnSelections.add(materialLineStatusTime.get("storedStorageRoom"));
        columnSelections.add(materialLineStatusTime.get("storedBinLocation"));
        columnSelections.add(partAlias.get("domain"));
        return columnSelections;
    }

    @Override
    public List<Tuple> getTransactionReturnsReportData(ReportWarehouseTransactionDTO reportWarehouseTransactionDTO, Date fromDate, Date toDate,
            String[] projectIds) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root<DeliveryNoteLine> root = criteriaQuery.from(DeliveryNoteLine.class);
        List<Selection<Object>> columnSelections = getColumnSelectionsForTransactionReturnsReport(root);
        List<Predicate> predicatesRules = getPredicateRulesForTransactionReturnsReport(reportWarehouseTransactionDTO, criteriaBuilder, root, fromDate, toDate,
                                                                                       projectIds);
        criteriaQuery.where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));
        criteriaQuery.multiselect(columnSelections.toArray(new Selection[columnSelections.size()]));
        TypedQuery<Tuple> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }

    private List<Predicate> getPredicateRulesForTransactionReturnsReport(ReportWarehouseTransactionDTO reportWarehouseTransactionDTO,
            CriteriaBuilder criteriaBuilder, Root<DeliveryNoteLine> root, Date fromDate, Date toDate, String[] projectIds) {
        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        Path<DeliveryNote> deliveryNote = root.get("deliveryNote");
        Path<Date> pathDeliveryDateTime = deliveryNote.get("deliveryNoteDate");
       
        if (fromDate != null) {
            predicatesRules.add(criteriaBuilder.greaterThanOrEqualTo(pathDeliveryDateTime, fromDate));
        }
        if (toDate != null) {
            predicatesRules.add(criteriaBuilder.lessThan(pathDeliveryDateTime, toDate));
        }
        String[] warehouses = reportWarehouseTransactionDTO.getWarehouse();
        if (projectIds != null && projectIds.length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("projectId")).value(Arrays.asList(projectIds)));
        }
        if (warehouses != null && warehouses.length > 0) {
            predicatesRules.add(criteriaBuilder.in(root.get("deliveryNote").get("whSiteId"))
                                .value(Arrays.asList(reportWarehouseTransactionDTO.getWarehouse())));
        }
        predicatesRules.add(criteriaBuilder.equal(root.get("deliveryNote").get("receiveType"), ReceiveType.RETURN));
        return predicatesRules;
    }

    private List<Selection<Object>> getColumnSelectionsForTransactionReturnsReport(Root<DeliveryNoteLine> root) {
        List<Selection<Object>> columnSelections = new ArrayList<Selection<Object>>();
        columnSelections.add(root.get("deliveryNote").get("deliveryNoteNo"));
        columnSelections.add(root.get("deliveryNote").get("deliveryNoteDate"));
        columnSelections.add(root.get("receivedDateTime"));
        columnSelections.add(root.get("possibleToReceiveQty"));
        columnSelections.add(root.get("receivedQuantity"));
        columnSelections.add(root.get("deliveryNote").get("returnDeliveryAddressId"));
        columnSelections.add(root.get("deliveryNote").get("returnDeliveryAddressName"));
        columnSelections.add(root.get("partAffiliation"));
        columnSelections.add(root.get("partNumber"));
        columnSelections.add(root.get("partVersion"));
        columnSelections.add(root.get("partName"));
        columnSelections.add(root.get("partModification"));
        columnSelections.add(root.get("partAlias"));
        columnSelections.add(root.get("projectId"));
        columnSelections.add(root.get("referenceIds"));
        return columnSelections;
    }
}
