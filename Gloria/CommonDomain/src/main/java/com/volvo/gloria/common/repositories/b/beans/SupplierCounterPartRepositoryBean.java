package com.volvo.gloria.common.repositories.b.beans;

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
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.volvo.gloria.common.d.entities.SupplierCounterPart;
import com.volvo.gloria.common.repositories.b.SupplierCounterPartRepository;
import com.volvo.gloria.util.c.dto.reports.ReportFilterDTO;
import com.volvo.gloria.util.paging.c.JpaAttribute;
import com.volvo.gloria.util.paging.c.JpaAttributeType;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.paging.c.PageResults;
import com.volvo.gloria.util.persistence.GenericAbstractRepositoryBean;
import com.volvo.jvs.runtime.platform.ContainerManaged;


/**
 * repository for root SupplierCounterPart.
 * 
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class SupplierCounterPartRepositoryBean extends GenericAbstractRepositoryBean<SupplierCounterPart, Long> implements SupplierCounterPartRepository {
    
    @PersistenceContext(unitName = "CommonDomainPU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    public List<SupplierCounterPart> getAllSupplierCounterParts(String deliveryFollowUpTeamId) {
        long deliveryFollowUpTeamOId = Long.parseLong(deliveryFollowUpTeamId);
        Query query = getEntityManager().createNamedQuery("getSupplierCounterPartsByDeliveryFollowUpTeamId");
        query.setParameter("deliveryFollowUpTeamId", deliveryFollowUpTeamOId);
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public SupplierCounterPart findSupplierCounterPartByPPSuffix(String ppSuffix) {
        Query query = getEntityManager().createNamedQuery("findSupplierCounterPartByPPSuffix");
        query.setParameter("ppSuffix", ppSuffix);
        List<Object> resultList = query.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (SupplierCounterPart) resultList.get(0);
        }
        return null;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public SupplierCounterPart findSupplierCounterPartByMaterialUserId(String materialUserId) {
        Query query = getEntityManager().createNamedQuery("findSupplierCounterPartByMaterialUserId");
        query.setParameter("materialUserId", materialUserId);
        List<Object> resultList = query.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (SupplierCounterPart) resultList.get(0);
        }
        return null;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public List<SupplierCounterPart> getSupplierCounterPartsByCompanyCode(String companyCode) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(SupplierCounterPart.class);
        Predicate[] restrictions = { criteriaBuilder.equal(root.get("companyCode"), companyCode), criteriaBuilder.equal(root.get("disabledProcure"), false) };
        Query resultSetQuery = entityManager.createQuery(criteriaQuery.select(root).where(criteriaBuilder.and(restrictions)));
        return resultSetQuery.getResultList();
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public PageObject getSupplierCounterParts(PageObject pageObject) {
        Map<String, JpaAttribute> fieldMap = new HashMap<String, JpaAttribute>();
        fieldMap.put("id", new JpaAttribute("ppSuffix", JpaAttributeType.STRINGTYPE));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(SupplierCounterPart.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.and(root.get("disabledProcure").in(false)));


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
        List<SupplierCounterPart> supplierCounterParts = queryForResultSets.getResultList();
        if (supplierCounterParts != null && !supplierCounterParts.isEmpty()) {
            List<ReportFilterDTO> reportSuffixDTOs = new ArrayList<ReportFilterDTO>();
            for (SupplierCounterPart supplierCounterPart : supplierCounterParts) {
                ReportFilterDTO reportCompanyCodeDTO = new ReportFilterDTO();
                reportCompanyCodeDTO.setId(supplierCounterPart.getPpSuffix());
                reportCompanyCodeDTO.setText(supplierCounterPart.getPpSuffix());
                reportSuffixDTOs.add(reportCompanyCodeDTO);
            }
            pageObject.setGridContents(new ArrayList<PageResults>(reportSuffixDTOs));
        } else {
            pageObject.setGridContents(null);
        }
        return pageObject;
    }
    
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<SupplierCounterPart> getSupplierCounterPartsBySuffix(List<String> suffixes) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(SupplierCounterPart.class);
        Predicate[] restrictions = { criteriaBuilder.in(root.get("ppSuffix")).value(suffixes), criteriaBuilder.equal(root.get("disabledProcure"), false) };
        Query resultSetQuery = entityManager.createQuery(criteriaQuery.select(root).where(criteriaBuilder.and(restrictions)));
        return resultSetQuery.getResultList();
    }
}
