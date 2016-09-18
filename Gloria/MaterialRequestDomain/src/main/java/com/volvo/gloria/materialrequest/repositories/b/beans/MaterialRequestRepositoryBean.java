package com.volvo.gloria.materialrequest.repositories.b.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;

import com.volvo.gloria.materialrequest.b.MaterialRequestHelper;
import com.volvo.gloria.materialrequest.c.dto.MaterialRequestDTO;
import com.volvo.gloria.materialrequest.c.dto.MaterialRequestLineDTO;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequest;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequestLine;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequestObject;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequestVersion;
import com.volvo.gloria.materialrequest.d.status.materialrequest.MaterialRequestStatus;
import com.volvo.gloria.materialrequest.d.type.materialrequest.MaterialRequestType;
import com.volvo.gloria.materialrequest.repositories.b.MaterialRequestRepository;
import com.volvo.gloria.util.paging.c.JpaAttribute;
import com.volvo.gloria.util.paging.c.JpaAttributeType;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.paging.c.PageResults;
import com.volvo.gloria.util.persistence.GenericAbstractRepositoryBean;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * repository for root MaterialRequestHeader.
 */
@ContainerManaged(name = "materialRequestRepository")
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class MaterialRequestRepositoryBean extends GenericAbstractRepositoryBean<MaterialRequest, Long> implements MaterialRequestRepository {

    @PersistenceContext(unitName = "MaterialRequestDomainPU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }

    @Override
    public MaterialRequest save(MaterialRequest instanceToSave) {
        MaterialRequest toSave = instanceToSave;
        if (instanceToSave.getId() <= 0) {
            getEntityManager().persist(instanceToSave);
        } else {
            toSave = getEntityManager().merge(instanceToSave);
        }
        return toSave;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })    
    public PageObject getMaterialRequests(PageObject pageObject, List<String> companyCodes) {
        Map<String, JpaAttribute> fieldMap = new HashMap<String, JpaAttribute>();

        fieldMap.put("projectId", new JpaAttribute("financeMaterial.projectId", JpaAttributeType.STRINGTYPE));
        fieldMap.put("referenceGroup", new JpaAttribute("referenceGroup", JpaAttributeType.STRINGTYPE));
        fieldMap.put("name", new JpaAttribute("current.materialRequestObject.name,current.materialRequestLines.materialRequestObject.name", 
                                              JpaAttributeType.STRINGTYPE));
        fieldMap.put("title", new JpaAttribute("current.title", JpaAttributeType.STRINGTYPE));
        fieldMap.put("mtrlRequestVersion", new JpaAttribute("current.mtrlRequestVersion", JpaAttributeType.STRINGTYPE));
        fieldMap.put("materialRequestVersionStatus", new JpaAttribute("current.status", JpaAttributeType.ENUMTYPE, MaterialRequestStatus.class));
        fieldMap.put("materialRequestVersionStatusDate", new JpaAttribute("current.statusDate", JpaAttributeType.DATETYPE));
        fieldMap.put("contactPersonName", new JpaAttribute("contactPersonName", JpaAttributeType.STRINGTYPE));
        fieldMap.put("requesterId", new JpaAttribute("requesterId", JpaAttributeType.STRINGTYPE));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(MaterialRequest.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        
        predicatesRules.add(root.get("financeMaterial").get("companyCode").in(companyCodes));

        CriteriaQuery countCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject, fieldMap,
                                                                                 true);
        Query countQuery = entityManager.createQuery(countCriteriaQueryFromPageObject);
        pageObject.setCount(((Long) countQuery.getSingleResult()).intValue());

        CriteriaQuery resultsetOfCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject,
                                                                                       fieldMap, false);
        Query resultSetQuery = entityManager.createQuery(resultsetOfCriteriaQueryFromPageObject);
        resultSetQuery.setFirstResult(pageObject.getFirstResult());
        resultSetQuery.setMaxResults(pageObject.getMaxResult());
        List<MaterialRequestDTO> materialRequestDTO = MaterialRequestHelper.transformListOfMaterialRequestDTOs(resultSetQuery.getResultList());
        if (materialRequestDTO != null) {
            pageObject.setGridContents(new ArrayList<PageResults>(materialRequestDTO));
        } else {
            pageObject.setGridContents(null);
        }

        return pageObject;
    }

    @Override
    public MaterialRequestLine saveOrUpdateMaterialRequestLine(MaterialRequestLine materialRequestLine) {
        MaterialRequestLine toSave = materialRequestLine;
        if (materialRequestLine.getMaterialRequestLineOid() <= 0) {
            getEntityManager().persist(materialRequestLine);
        } else {
            toSave = getEntityManager().merge(materialRequestLine);
        }
        return toSave;
    }

    @Override
    public void deleteMaterialRequestLine(Long materialRequestLineOid) {
        MaterialRequestLine materialRequestLine = getEntityManager().find(MaterialRequestLine.class, materialRequestLineOid);
        getEntityManager().remove(materialRequestLine);        
    }

    @Override
    public MaterialRequestLine findMaterialRequestLineById(Long materialRequestLineOid) {
        return getEntityManager().find(MaterialRequestLine.class, materialRequestLineOid);
    }

    @Override
    public void deleteMaterialRequestVersion(MaterialRequestVersion materialRequestVersion) {
        getEntityManager().remove(materialRequestVersion);
    }

    @Override
    public MaterialRequestVersion findCurrentMaterialRequestVersionById(long materialRequestVersionOid) {
        return getEntityManager().find(MaterialRequestVersion.class, materialRequestVersionOid);
    }
    
    @Override
    public MaterialRequestObject save(MaterialRequestObject instanceToSave) {
        MaterialRequestObject toSave = instanceToSave;
        if (instanceToSave.getMaterialRequestObjectOid() <= 0) {
            getEntityManager().persist(instanceToSave);
        } else {
            toSave = getEntityManager().merge(instanceToSave);
        }
        return toSave;        
    }
    
    @Override
    public void delete(MaterialRequestObject materialRequestObject) {
        getEntityManager().remove(materialRequestObject);
    }
    
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public MaterialRequest findMaterialRequestByMtrlRequestId(String mtrlRequestId) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(MaterialRequest.class);
        if (!StringUtils.isEmpty(mtrlRequestId)) {
            criteriaQuery.where(criteriaBuilder.equal(root.get("materialRequestId"), mtrlRequestId));
        }

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root).distinct(true);

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        List<MaterialRequest> headers = resultSetQuery.getResultList();
        if (headers != null && !headers.isEmpty()) {
            return headers.get(0);
        }
        return null;
    }

    @Override
    public List<MaterialRequestLine> saveOrUpdateMaterialRequestLines(List<MaterialRequestLine> materialRequestLinesToUpdate) {
        List<MaterialRequestLine> materialRequestLines = new ArrayList<MaterialRequestLine>();
        for (MaterialRequestLine materialRequestLine : materialRequestLinesToUpdate) {
            materialRequestLines.add(saveOrUpdateMaterialRequestLine(materialRequestLine));
        }
        return materialRequestLines;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public PageObject findMaterialRequestLinesByHeaderId(PageObject pageObject, long materialRequestId) {
        Map<String, JpaAttribute> fieldMap = new HashMap<String, JpaAttribute>();

        fieldMap.put("partAffiliation", new JpaAttribute("partAffiliation", JpaAttributeType.STRINGTYPE));
        fieldMap.put("partNumber", new JpaAttribute("partNumber", JpaAttributeType.STRINGTYPE));
        fieldMap.put("partVersion", new JpaAttribute("partVersion", JpaAttributeType.STRINGTYPE));
        fieldMap.put("partName", new JpaAttribute("partName", JpaAttributeType.STRINGTYPE));
        fieldMap.put("quantity", new JpaAttribute("quantity", JpaAttributeType.NUMBERTYPE));
        fieldMap.put("unitOfMeasure", new JpaAttribute("unitOfMeasure", JpaAttributeType.STRINGTYPE));
        fieldMap.put("partModification", new JpaAttribute("partModification", JpaAttributeType.STRINGTYPE));
        fieldMap.put("functionGroup", new JpaAttribute("functionGroup", JpaAttributeType.STRINGTYPE));
        fieldMap.put("name", new JpaAttribute("materialRequestObject.name", JpaAttributeType.STRINGTYPE));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(MaterialRequestLine.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        Predicate predicateMaterialRequestId = criteriaBuilder.equal(root.get("materialRequestVersion").get("materialRequest").get("materialRequestOid"),
                                                                     materialRequestId);
        Predicate predicateCurrentMRLines = criteriaBuilder.equal(root.get("materialRequestVersion").get("materialRequestVersionOid"),
                                                                  root.get("materialRequestVersion").get("materialRequest").get("current")
                                                                      .get("materialRequestVersionOid"));
        predicatesRules.add(criteriaBuilder.and(predicateMaterialRequestId, predicateCurrentMRLines));

        CriteriaQuery countCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject, fieldMap,
                                                                               true);
        Query countQuery = entityManager.createQuery(countCriteriaQueryFromPageObject);
        pageObject.setCount(((Long) countQuery.getSingleResult()).intValue());

        CriteriaQuery resultsetOfCriteriaQueryFromPageObject = PageObject.buildQuery(criteriaBuilder, criteriaQuery, root, predicatesRules, pageObject,
                                                                                     fieldMap, false);
        Query resultSetQuery = entityManager.createQuery(resultsetOfCriteriaQueryFromPageObject);
        resultSetQuery.setFirstResult(pageObject.getFirstResult());
        resultSetQuery.setMaxResults(pageObject.getMaxResult());
        List<MaterialRequestLineDTO> materialRequestLineDTOs = MaterialRequestHelper.transformListOfMaterialRequestLineDTOs(resultSetQuery.getResultList());
        if (materialRequestLineDTOs != null) {
            pageObject.setGridContents(new ArrayList<PageResults>(materialRequestLineDTOs));
        } else {
            pageObject.setGridContents(null);
        }

        return pageObject;
    }
    
    @Override
    public Long getMaxMaterialRequestIdSequence() {
        Query query = getEntityManager().createNamedQuery("getMaxmaterialRequestIdSequence");
        Object result = query.getSingleResult();
        if (result == null) {
            return new Long(0);
        } else {
            return (Long) result;
        }
    }
    
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public boolean validateMaterialRequest(long materialRequestId, String materialRequestType) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(MaterialRequestLine.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        Predicate predicateMaterialRequestId = criteriaBuilder.equal(root.get("materialRequestVersion").get("materialRequest").get("materialRequestOid"),
                                                                     materialRequestId);
        Predicate predicateCurrentMRLines = criteriaBuilder.equal(root.get("materialRequestVersion").get("materialRequestVersionOid"),
                                                                  root.get("materialRequestVersion").get("materialRequest").get("current")
                                                                      .get("materialRequestVersionOid"));
        predicatesRules.add(criteriaBuilder.and(predicateMaterialRequestId, predicateCurrentMRLines));        
        
        List<Predicate> predicatesRulesForMandatoryItems = new ArrayList<Predicate>();
        
        if (MaterialRequestType.MULTIPLE.name().equals(materialRequestType)) {
            Predicate partNamePredicate = root.get("materialRequestObject").get("name").isNull();
            predicatesRulesForMandatoryItems.add(partNamePredicate);
        }
        
        predicatesRulesForMandatoryItems.add(addEmptyCheckPredicate(criteriaBuilder, root.get("partAffiliation")));
        predicatesRulesForMandatoryItems.add(addEmptyCheckPredicate(criteriaBuilder, root.get("partNumber")));
        predicatesRulesForMandatoryItems.add(addEmptyCheckPredicate(criteriaBuilder, root.get("partVersion")));
        predicatesRulesForMandatoryItems.add(addEmptyCheckPredicate(criteriaBuilder, root.get("partName")));
        predicatesRulesForMandatoryItems.add(criteriaBuilder.le(root.get("quantity"), 0));
        predicatesRulesForMandatoryItems.add(addEmptyCheckPredicate(criteriaBuilder, root.get("unitOfMeasure")));
        predicatesRules.add(criteriaBuilder.or(predicatesRulesForMandatoryItems.toArray(new Predicate[predicatesRulesForMandatoryItems.size()])));

        CriteriaQuery criteriaQueryForCount = criteriaQuery.select(criteriaBuilder.count(root))
                                                           .where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));
        Query countQuery = entityManager.createQuery(criteriaQueryForCount);
        int count = ((Long) countQuery.getSingleResult()).intValue();

        if (count > 0) {
            return false;
        }
        return true;
    }

    @SuppressWarnings("rawtypes")
    private Predicate addEmptyCheckPredicate(CriteriaBuilder criteriaBuilder, Path path) {
        return criteriaBuilder.or(path.isNull(), criteriaBuilder.equal(path, ""));
    }
}
