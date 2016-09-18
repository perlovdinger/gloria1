package com.volvo.gloria.procurematerial.repositories.b.beans;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;

import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.c.ProcureResponsibility;
import com.volvo.gloria.procurematerial.c.dto.ProcureLineDTO;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.Requisition;
import com.volvo.gloria.procurematerial.d.entities.Supplier;
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineStatus;
import com.volvo.gloria.procurematerial.d.type.procure.ProcureType;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.util.ProcurementHelper;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.GloriaFormateUtil;
import com.volvo.gloria.util.c.dto.reports.ReportFilterDTO;
import com.volvo.gloria.util.paging.c.JpaAttribute;
import com.volvo.gloria.util.paging.c.JpaAttributeType;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.paging.c.PageResults;
import com.volvo.gloria.util.persistence.GenericAbstractRepositoryBean;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * repository implementation for ROOT - ProcureLine.
 * 
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ProcureLineRepositoryBean extends GenericAbstractRepositoryBean<ProcureLine, Long> implements ProcureLineRepository {

    private static final String STATUS = "status";
    private static final String RESPONSIBILITY = "responsibility";
    
    @Inject
    private ProcurementServices procurementServices;

    @PersistenceContext(unitName = "ProcureMaterialDomainPU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ProcureLine> findAllProcureLines() {
        Query query = getEntityManager().createNamedQuery("findAllProcureLines");
        return query.getResultList();
    }

    /**
     * @throws GloriaApplicationException
     * @TODO: assignedMaterialController needs to be checked while fetching procureLine information. the status needs to be checked based on the boolean value,
     *        If true, it will return procure lines with ProcureLine:status set to "wait_to_procure" if false, it will return procure lines with
     *        ProcureLine:status set to "procured_GTT" or "procured_GTO". If not set, it will return procure lines without regard to status.
     * 
     */
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public PageObject getProcurements(List<String> procureResponsibilitiyList, String procureLineStatus, String assignedMaterialController,
            String assignedMaterialControllerTeam, PageObject pageObject, List<String> companyCodes, String showFilter) throws GloriaApplicationException {
        Map<String, JpaAttribute> fieldMap = new HashMap<String, JpaAttribute>();
        fieldMap.put("projectId", new JpaAttribute("financeHeader.projectId", JpaAttributeType.STRINGTYPE));
        fieldMap.put("groupNameReference", new JpaAttribute("referenceGroups", JpaAttributeType.STRINGTYPE));
        fieldMap.put("referenceGroups", new JpaAttribute("referenceGroups", JpaAttributeType.STRINGTYPE));
        fieldMap.put("pPartAffiliation", new JpaAttribute("pPartAffiliation", JpaAttributeType.STRINGTYPE));
        fieldMap.put("pPartNumber", new JpaAttribute("pPartNumber", JpaAttributeType.STRINGTYPE));
        fieldMap.put("pPartVersion", new JpaAttribute("pPartVersion", JpaAttributeType.STRINGTYPE));
        fieldMap.put("pPartName", new JpaAttribute("pPartName", JpaAttributeType.STRINGTYPE));
        fieldMap.put("pPartModification", new JpaAttribute("pPartModification", JpaAttributeType.STRINGTYPE));
        fieldMap.put("changeRequestId", new JpaAttribute("changeRequestIds", JpaAttributeType.STRINGTYPE));
        fieldMap.put("changeRequestIds", new JpaAttribute("changeRequestIds", JpaAttributeType.STRINGTYPE));
        fieldMap.put("groupReference", new JpaAttribute("referenceIds", JpaAttributeType.STRINGTYPE));
        fieldMap.put("referenceIds", new JpaAttribute("referenceIds", JpaAttributeType.STRINGTYPE));
        fieldMap.put("mailFormIds", new JpaAttribute("materials.mailFormId", JpaAttributeType.STRINGTYPE));
        fieldMap.put("requisitionId", new JpaAttribute("requisitionId", JpaAttributeType.STRINGTYPE));
        fieldMap.put("orderNo", new JpaAttribute("orderNo", JpaAttributeType.STRINGTYPE));
        fieldMap.put(STATUS, new JpaAttribute(STATUS, JpaAttributeType.STRINGTYPE));
        fieldMap.put("procureType", new JpaAttribute("procureType", JpaAttributeType.ENUMTYPE, ProcureType.class));
        fieldMap.put("procureForwardedId", new JpaAttribute("forwardedUserId", JpaAttributeType.STRINGTYPE));
        fieldMap.put("procureForwardedName", new JpaAttribute("forwardedUserName", JpaAttributeType.STRINGTYPE));
        fieldMap.put("requiredStaDate", new JpaAttribute("requiredStaDate", JpaAttributeType.DATETYPE));
        fieldMap.put("buyerCode", new JpaAttribute("buyerCode", JpaAttributeType.STRINGTYPE));
        fieldMap.put("needIsChanged", new JpaAttribute("needIsChanged", JpaAttributeType.STRINGTYPE));
        fieldMap.put("procureFailureDate", new JpaAttribute("procureFailureDate", JpaAttributeType.DATETYPE));
        fieldMap.put("outboundLocationId", new JpaAttribute("materials.materialHeader.accepted.outboundLocationId", JpaAttributeType.STRINGTYPE));
        fieldMap.put("modularHarness", new JpaAttribute("materials.modularHarness", JpaAttributeType.STRINGTYPE));
        fieldMap.put("supplierId", new JpaAttribute("supplier.supplierId,supplier.supplierName", JpaAttributeType.STRINGTYPE));
        fieldMap.put("statusFlag", new JpaAttribute("statusFlag", JpaAttributeType.STRINGTYPE));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root<ProcureLine> root = criteriaQuery.from(ProcureLine.class);
        
        List<Predicate> predicatesRules = new ArrayList<Predicate>();

        if (!StringUtils.isEmpty(procureLineStatus)) {
            Path<String> path = root.get(STATUS);
            Predicate predicateIn = path.in(GloriaFormateUtil.getValuesAsEnums(procureLineStatus, ProcureLineStatus.class));
            predicatesRules.add(criteriaBuilder.and(predicateIn));
        }

        if (!StringUtils.isEmpty(showFilter)) {
            if (showFilter.toUpperCase().equals("EDITED")) {
                predicatesRules.add(criteriaBuilder.isTrue((Expression) root.get("contentEdited")));
            } else if (showFilter.toUpperCase().equals("NOT_EDITED")) {
                predicatesRules.add(criteriaBuilder.isFalse((Expression) root.get("contentEdited")));
            }
        }
        
        if (procureResponsibilitiyList != null && !procureResponsibilitiyList.isEmpty()) {
            Path<String> path = root.get(RESPONSIBILITY);
            Set<ProcureResponsibility> enumSet = null;
            for (String procureResponsibility : procureResponsibilitiyList) {
                if (enumSet == null) {
                    enumSet = EnumSet.of(ProcureResponsibility.valueOf(procureResponsibility));
                }
                enumSet.add(ProcureResponsibility.valueOf(procureResponsibility));
            }
            Predicate predicateIn = path.in(enumSet);
            predicatesRules.add(criteriaBuilder.and(predicateIn));
        }

        if (!StringUtils.isEmpty(assignedMaterialController)) {
            Path<String> path = root.get("materialControllerId");
            Predicate predicateIn = path.in(assignedMaterialController);
            predicatesRules.add(criteriaBuilder.and(predicateIn));
        }

        if (!StringUtils.isEmpty(assignedMaterialControllerTeam)) {
            Path<String> path = root.get("materialControllerTeam");
            Predicate predicateIn = path.in(assignedMaterialControllerTeam);
            predicatesRules.add(criteriaBuilder.and(predicateIn));
        }

        // exclude forwarded procure lines
        predicatesRules.add(criteriaBuilder.and(root.get("forwardedUserId").isNull()));
        predicatesRules.add(criteriaBuilder.and(root.get("forwardedUserName").isNull()));
        predicatesRules.add(criteriaBuilder.and(root.get("forwardedTeam").isNull()));
        
        predicatesRules.add(root.get("financeHeader").get("companyCode").in(companyCodes));

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
        List<ProcureLine> procureLines = queryForResultSets.getResultList();
        
        if (procureLines != null && !procureLines.isEmpty()) {
            for (ProcureLine procureLine : procureLines) {
                procurementServices.suggestProcureType(procureLine, procureLine.getMaterials(), procurementServices.existsCarryOver(procureLine.getMaterials()));
            }
        }

        List<ProcureLineDTO> procureLineDTOs = ProcurementHelper.transformEntityToDTO(procureLines);
        if (procureLineDTOs != null) {
            pageObject.setGridContents(new ArrayList<PageResults>(procureLineDTOs));
        } else {
            pageObject.setGridContents(null);
        }

        return pageObject;
    }

    private void elseif(boolean b) {
        // TODO Auto-generated method stub
        
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public PageObject findProcureLinesByUserId(String procureForwardedId, String procureForwardedTeam, String procureLineStatus, PageObject pageObject,
            List<String> companyCodes) throws GloriaApplicationException {
        Map<String, JpaAttribute> fieldMap = new HashMap<String, JpaAttribute>();
        fieldMap.put("projectId", new JpaAttribute("financeHeader.projectId", JpaAttributeType.STRINGTYPE));
        fieldMap.put("groupNameReference", new JpaAttribute("referenceGroups", JpaAttributeType.STRINGTYPE));
        fieldMap.put("referenceGroups", new JpaAttribute("referenceGroups", JpaAttributeType.STRINGTYPE));
        fieldMap.put("pPartAffiliation", new JpaAttribute("pPartAffiliation", JpaAttributeType.STRINGTYPE));
        fieldMap.put("pPartNumber", new JpaAttribute("pPartNumber", JpaAttributeType.STRINGTYPE));
        fieldMap.put("pPartVersion", new JpaAttribute("pPartVersion", JpaAttributeType.STRINGTYPE));
        fieldMap.put("pPartName", new JpaAttribute("pPartName", JpaAttributeType.STRINGTYPE));
        fieldMap.put("pPartModification", new JpaAttribute("pPartModification", JpaAttributeType.STRINGTYPE));
        fieldMap.put("changeRequestId", new JpaAttribute("changeRequestIds", JpaAttributeType.STRINGTYPE));
        fieldMap.put("changeRequestIds", new JpaAttribute("changeRequestIds", JpaAttributeType.STRINGTYPE));
        fieldMap.put("groupReference", new JpaAttribute("referenceIds", JpaAttributeType.STRINGTYPE));
        fieldMap.put("referenceIds", new JpaAttribute("referenceIds", JpaAttributeType.STRINGTYPE));
        fieldMap.put("mailFormIds", new JpaAttribute("materials.mailFormId", JpaAttributeType.STRINGTYPE));
        fieldMap.put("requisitionId", new JpaAttribute("requisitionId", JpaAttributeType.NUMBERTYPE));
        fieldMap.put("orderNo", new JpaAttribute("orderNo", JpaAttributeType.STRINGTYPE));
        fieldMap.put(STATUS, new JpaAttribute(STATUS, JpaAttributeType.STRINGTYPE));
        fieldMap.put("procureType", new JpaAttribute("procureType", JpaAttributeType.ENUMTYPE, ProcureType.class));
        fieldMap.put("procureForwardedId", new JpaAttribute("forwardedUserId", JpaAttributeType.STRINGTYPE));
        fieldMap.put("procureForwardedName", new JpaAttribute("forwardedUserName", JpaAttributeType.STRINGTYPE));
        fieldMap.put("requiredStaDate", new JpaAttribute("requiredStaDate", JpaAttributeType.DATETYPE));
        fieldMap.put("procureFailureDate", new JpaAttribute("procureFailureDate", JpaAttributeType.DATETYPE));
        fieldMap.put("supplierId", new JpaAttribute("supplier.supplierId,supplier.supplierName", JpaAttributeType.STRINGTYPE));
        fieldMap.put("statusFlag", new JpaAttribute("statusFlag", JpaAttributeType.STRINGTYPE));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root<ProcureLine> root = criteriaQuery.from(ProcureLine.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();

        if (procureLineStatus != null) {
            Path<String> path = root.get(STATUS);
            Predicate predicateIn = path.in(GloriaFormateUtil.getValuesAsEnums(procureLineStatus, ProcureLineStatus.class));
            predicatesRules.add(criteriaBuilder.and(predicateIn));
        }

        predicatesRules.add(criteriaBuilder.equal(root.get(RESPONSIBILITY), ProcureResponsibility.PROCURER));
        predicatesRules.add(criteriaBuilder.equal(root.get("forwardedUserId"), procureForwardedId));
        if (!StringUtils.isEmpty(procureForwardedTeam)) {
            predicatesRules.add(root.get("forwardedTeam").in((Object[]) procureForwardedTeam.split(",")));
        }
        predicatesRules.add(root.get("financeHeader").get("companyCode").in(companyCodes));

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
        List<ProcureLineDTO> procureLineDTOs = ProcurementHelper.transformEntityToDTO(queryForResultSets.getResultList());
        if (procureLineDTOs != null) {
            pageObject.setGridContents(new ArrayList<PageResults>(procureLineDTOs));
        } else {
            pageObject.setGridContents(null);
        }
        return pageObject;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public PageObject findProcureLinesByInternalProcurerTeam(String procureForwardedTeam, String procureLineStatus, PageObject pageObject)
            throws GloriaApplicationException {
        Map<String, JpaAttribute> fieldMap = new HashMap<String, JpaAttribute>();
        fieldMap.put("projectId", new JpaAttribute("financeHeader.projectId", JpaAttributeType.STRINGTYPE));
        fieldMap.put("groupNameReference", new JpaAttribute("referenceGroups", JpaAttributeType.STRINGTYPE));
        fieldMap.put("referenceGroups", new JpaAttribute("referenceGroups", JpaAttributeType.STRINGTYPE));
        fieldMap.put("pPartAffiliation", new JpaAttribute("pPartAffiliation", JpaAttributeType.STRINGTYPE));
        fieldMap.put("pPartNumber", new JpaAttribute("pPartNumber", JpaAttributeType.STRINGTYPE));
        fieldMap.put("pPartVersion", new JpaAttribute("pPartVersion", JpaAttributeType.STRINGTYPE));
        fieldMap.put("pPartName", new JpaAttribute("pPartName", JpaAttributeType.STRINGTYPE));
        fieldMap.put("pPartModification", new JpaAttribute("pPartModification", JpaAttributeType.STRINGTYPE));
        fieldMap.put("changeRequestId", new JpaAttribute("changeRequestIds", JpaAttributeType.STRINGTYPE));
        fieldMap.put("changeRequestIds", new JpaAttribute("changeRequestIds", JpaAttributeType.STRINGTYPE));
        fieldMap.put("groupReference", new JpaAttribute("referenceIds", JpaAttributeType.STRINGTYPE));
        fieldMap.put("referenceIds", new JpaAttribute("referenceIds", JpaAttributeType.STRINGTYPE));
        fieldMap.put("mailFormIds", new JpaAttribute("materials.mailFormId", JpaAttributeType.STRINGTYPE));
        fieldMap.put("requisitionId", new JpaAttribute("requisitionId", JpaAttributeType.NUMBERTYPE));
        fieldMap.put("orderNo", new JpaAttribute("orderNo", JpaAttributeType.STRINGTYPE));
        fieldMap.put(STATUS, new JpaAttribute(STATUS, JpaAttributeType.STRINGTYPE));
        fieldMap.put("procureType", new JpaAttribute("procureType", JpaAttributeType.ENUMTYPE, ProcureType.class));
        fieldMap.put("procureForwardedId", new JpaAttribute("forwardedUserId", JpaAttributeType.STRINGTYPE));
        fieldMap.put("procureForwardedName", new JpaAttribute("forwardedUserName", JpaAttributeType.STRINGTYPE));
        fieldMap.put("requiredStaDate", new JpaAttribute("requiredStaDate", JpaAttributeType.DATETYPE));
        fieldMap.put("procureFailureDate", new JpaAttribute("procureFailureDate", JpaAttributeType.DATETYPE));
        fieldMap.put("supplierId", new JpaAttribute("supplier.supplierId,supplier.supplierName", JpaAttributeType.STRINGTYPE));
        fieldMap.put("statusFlag", new JpaAttribute("statusFlag", JpaAttributeType.STRINGTYPE));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root<ProcureLine> root = criteriaQuery.from(ProcureLine.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();

        if (procureLineStatus != null) {
            Path<String> path = root.get(STATUS);
            Predicate predicateIn = path.in(EnumSet.of(ProcureLineStatus.valueOf(procureLineStatus)));
            predicatesRules.add(criteriaBuilder.and(predicateIn));
        }

        predicatesRules.add(criteriaBuilder.equal(root.get(RESPONSIBILITY), ProcureResponsibility.PROCURER));

        if (!StringUtils.isEmpty(procureForwardedTeam)) {
            predicatesRules.add(root.get("forwardedTeam").in((Object[]) procureForwardedTeam.split(",")));
        }

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
        List<ProcureLineDTO> procureLineDTOs = ProcurementHelper.transformEntityToDTO(queryForResultSets.getResultList());
        if (procureLineDTOs != null) {
            pageObject.setGridContents(new ArrayList<PageResults>(procureLineDTOs));
        } else {
            pageObject.setGridContents(null);
        }
        return pageObject;
    }

    @Override
    public ProcureLine findProcureLineById(long procureLineOid) throws GloriaApplicationException {
        return getEntityManager().find(ProcureLine.class, procureLineOid);
    }
  
    
    @Override
    public List<ProcureLine> findProcureLineByParameters(Map<String, Object> predicateParams) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProcureLine> criteiraQuery = criteriaBuilder.createQuery(ProcureLine.class);
        Root<ProcureLine> procureLine = criteiraQuery.from(ProcureLine.class);
        criteiraQuery.select(procureLine);
        List<Predicate> predicates = new ArrayList<Predicate>();

        Path<String> forwardedUserIdPath = procureLine.get(ProcureLine.FORWARDEDUSERID);
        Object forwardedUserId = predicateParams.get(ProcureLine.FORWARDEDUSERID);
        if (StringUtils.isNotBlank((String) predicateParams.get(ProcureLine.FORWARDEDUSERID))) {
            predicates.add(criteriaBuilder.equal(forwardedUserIdPath, forwardedUserId));
        } else {
            Predicate predicateIn = forwardedUserIdPath.isNull();
            predicates.add(criteriaBuilder.and(predicateIn));
        }
        predicateParams.remove(ProcureLine.FORWARDEDUSERID);

        Path<String> forwardedTeamPath = procureLine.get(ProcureLine.FORWARDEDTEAM);
        Object forwardedTeam = predicateParams.get(ProcureLine.FORWARDEDTEAM);
        if (StringUtils.isNotBlank((String) predicateParams.get(ProcureLine.FORWARDEDTEAM))) {
            predicates.add(criteriaBuilder.equal(forwardedTeamPath, forwardedTeam));
        } else {
            Predicate predicateIn = forwardedTeamPath.isNull();
            predicates.add(criteriaBuilder.and(predicateIn));
        }
        predicateParams.remove(ProcureLine.FORWARDEDTEAM);
        
        List<String> grouping = (List<String>)predicateParams.get(ProcureLine.GROUPINGKEYMD5S);
        if (grouping != null && !grouping.isEmpty()) {
            predicates.add(criteriaBuilder.in(procureLine.get(ProcureLine.GROUPINGKEYMD5)).value(grouping));
        }
        predicateParams.remove(ProcureLine.GROUPINGKEYMD5S);

        for (Iterator<String> iterator = predicateParams.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            predicates.add(criteriaBuilder.equal(procureLine.get(key), predicateParams.get(key)));
        }

        TypedQuery<ProcureLine> query = entityManager.createQuery(criteiraQuery.select(procureLine)
                                                                  .where(predicates.toArray(new Predicate[predicates.size()])));
        return query.getResultList();
    } 

    @Override
    public void addRequisition(Requisition requisition) {
        getEntityManager().persist(requisition);
    }

    @Override
    public Requisition findRequisitionById(long requisitionOID) {
        return getEntityManager().find(Requisition.class, requisitionOID);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ProcureLine findProcureLineByRequisitionId(String requisitionId) {
        Query query = getEntityManager().createNamedQuery("findProcureLineByRequisitionId");
        query.setParameter("requisitionId", requisitionId);
        List<ProcureLine> procureLines = query.getResultList();
        if (procureLines != null && !procureLines.isEmpty()) {
            return procureLines.get(0);
        }
        return null;
    }

    @Override
    public ProcureLine save(ProcureLine instanceToSave) {
        ProcureLine toSave = instanceToSave;
        if (instanceToSave.getId() < 1) {
            getEntityManager().persist(instanceToSave);
        } else {
            toSave = getEntityManager().merge(instanceToSave);
        }
        return toSave;

    }

    @Override
    public void deleteRequisition(Requisition requisition) {
        getEntityManager().remove(requisition);
    }

    @Override
    public void delete(ProcureLine procureLine) {
        getEntityManager().remove(procureLine);
    }
    
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })    
    public PageObject getReferences(PageObject pageObject) {
        Map<String, JpaAttribute> fieldMap = new HashMap<String, JpaAttribute>();
        fieldMap.put("id", new JpaAttribute("referenceGps", JpaAttributeType.STRINGTYPE));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root<ProcureLine> root = criteriaQuery.from(ProcureLine.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.isNotEmpty((Expression) root.get("referenceGps")));
        predicatesRules.add(criteriaBuilder.isNotNull((Expression) root.get("referenceGps")));
        
        CriteriaQuery countCriteriaQueryFromPageObeject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root.get("referenceGps"), predicatesRules,
                                                                                pageObject, fieldMap, true);
        Query countQuery = entityManager.createQuery(countCriteriaQueryFromPageObeject);
        Long value = (Long) countQuery.getSingleResult();
        pageObject.setCount(value.intValue());

        CriteriaQuery resultSetCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root.get("referenceGps"), predicatesRules,
                                                                                   pageObject, fieldMap, false);
        Query queryForResultSets = entityManager.createQuery(resultSetCriteriaQueryFromPageObject.distinct(true));
        queryForResultSets.setFirstResult(pageObject.getFirstResult());
        queryForResultSets.setMaxResults(pageObject.getMaxResult());
        List<String> referenceGpsValues = queryForResultSets.getResultList();
        if (referenceGpsValues != null && !referenceGpsValues.isEmpty()) {
            List<ReportFilterDTO> reportReferenceDTOs = new ArrayList<ReportFilterDTO>();
            for (String referenceGpsValue : referenceGpsValues) {
                ReportFilterDTO reportReferenceDTO = new ReportFilterDTO();
                reportReferenceDTO.setId(referenceGpsValue);
                reportReferenceDTO.setText(referenceGpsValue);
                reportReferenceDTOs.add(reportReferenceDTO);
            }
            pageObject.setGridContents(new ArrayList<PageResults>(reportReferenceDTOs));
        } else {
            pageObject.setGridContents(null);
        }
        return pageObject;
    }
    
    
    @Override
    @SuppressWarnings("rawtypes")
    public PageObject getBuildNames(PageObject pageObject) {
        Map<String, JpaAttribute> fieldMap = new HashMap<String, JpaAttribute>();
        fieldMap.put("id", new JpaAttribute("buildName", JpaAttributeType.STRINGTYPE));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root<ProcureLine> root = criteriaQuery.from(MaterialHeader.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();

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
        List<MaterialHeader> procureLines = queryForResultSets.getResultList();
        if (procureLines != null && !procureLines.isEmpty()) {
            List<ReportFilterDTO> buildNameDTOs = new ArrayList<ReportFilterDTO>();
            for (MaterialHeader materialHeader : procureLines) {
                ReportFilterDTO phaseNameDTO = new ReportFilterDTO();
                phaseNameDTO.setId(materialHeader.getBuildName());
                phaseNameDTO.setText(materialHeader.getBuildName());
                buildNameDTOs.add(phaseNameDTO);
            }
            pageObject.setGridContents(new ArrayList<PageResults>(buildNameDTOs));
        } else {
            pageObject.setGridContents(null);
        }
        return pageObject;
    }


    @Override
    @SuppressWarnings("unchecked")
    public Requisition findRequisitionByRequisitionId(String requisitionId) {
        Query query = getEntityManager().createNamedQuery("findRequisitionByRequisitionId");
        query.setParameter("requisitionId", requisitionId);
        List<Requisition> requisitions = query.getResultList();
        if (requisitions != null && !requisitions.isEmpty()) {
            return requisitions.get(0);
        }
        return null;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<ProcureLine> findProcureLinesByPartInformation(String partNumber, String partVersion, String partAffiliation, String partModification,
            ProcureLineStatus status) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProcureLine> criteiraQuery = criteriaBuilder.createQuery(ProcureLine.class);
        Root<ProcureLine> root = criteiraQuery.from(ProcureLine.class);
        criteiraQuery.select(root);
        List<Predicate> predicates = new ArrayList<Predicate>();

        predicates.add(criteriaBuilder.equal(root.get("pPartNumber"), partNumber));
        predicates.add(criteriaBuilder.equal(root.get("pPartVersion"), partVersion));
        predicates.add(criteriaBuilder.equal(root.get("pPartAffiliation"), partAffiliation));
        if (!StringUtils.isEmpty(partModification)) {
            predicates.add(criteriaBuilder.equal(root.get("pPartModification"), partModification));
        }
        if (status != null) {
            predicates.add(criteriaBuilder.equal(root.get("status"), status));
        }
        Query query = entityManager.createQuery(criteiraQuery.select(root).where(predicates.toArray(new Predicate[predicates.size()])));
        return query.getResultList();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<ProcureLine> findByIds(List<Long> ids) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProcureLine> criteiraQuery = criteriaBuilder.createQuery(ProcureLine.class);
        Root<ProcureLine> root = criteiraQuery.from(ProcureLine.class);
        criteiraQuery.select(root);
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(root.get("procureLineOid").in(ids));
        Query query = entityManager.createQuery(criteiraQuery.select(root).where(predicates.toArray(new Predicate[predicates.size()])));
        return query.getResultList();
    }
    
    @Override
    public Requisition updateRequisition(Requisition requisition) {
       return getEntityManager().merge(requisition);
    }
    
    @Override
    public Supplier findSupplierById(Long supplierOid) {
        return getEntityManager().find(Supplier.class, supplierOid);
    }

    @Override
    public Supplier findSupplierBySupplierId(String supplierId) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Supplier> criteiraQuery = criteriaBuilder.createQuery(Supplier.class);
        Root<Supplier> root = criteiraQuery.from(Supplier.class);
        criteiraQuery.select(root);
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(criteriaBuilder.equal(root.get("supplierId"), supplierId));

        Query query = entityManager.createQuery(criteiraQuery.select(root).where(predicates.toArray(new Predicate[predicates.size()])));

        List<Supplier> resultList = query.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (Supplier) resultList.get(0);
        } else {
            return null;
        }
    }
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<ProcureLine> findAllProcureLinesByIds(List<Long> procureLineIds) throws GloriaApplicationException {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(ProcureLine.class);
        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root);
        if (!procureLineIds.isEmpty()) {
            criteriaQueryForResultSet = criteriaQuery.select(root).where(root.get("procureLineOid").in(procureLineIds));
        }
        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);
        return resultSetQuery.getResultList();
    }

}
