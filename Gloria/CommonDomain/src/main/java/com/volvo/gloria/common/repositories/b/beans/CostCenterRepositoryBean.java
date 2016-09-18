package com.volvo.gloria.common.repositories.b.beans;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;

import com.volvo.gloria.common.c.PaginatedArrayList;
import com.volvo.gloria.common.c.dto.CostCenterDTO;
import com.volvo.gloria.common.d.entities.CostCenter;
import com.volvo.gloria.common.repositories.b.CostCenterRepository;
import com.volvo.gloria.common.util.CommonHelper;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.paging.c.JpaAttribute;
import com.volvo.gloria.util.paging.c.JpaAttributeType;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.paging.c.PageResults;
import com.volvo.gloria.util.persistence.GenericAbstractRepositoryBean;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * repository for root CostCenter.
 * 
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@SuppressWarnings({ "unchecked", "rawtypes" })
public class CostCenterRepositoryBean extends GenericAbstractRepositoryBean<CostCenter, Long> implements CostCenterRepository {

    @PersistenceContext(unitName = "CommonDomainPU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }

    @Override
    public PageObject findCostCentersByCompanyCode(String companyCode, String costCenter, PageObject pageObject) {
        Map<String, JpaAttribute> fieldMap = new HashMap<String, JpaAttribute>();
        fieldMap.put("costCenter", new JpaAttribute("costCenter", JpaAttributeType.STRINGTYPE));

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();

        Root root = criteriaQuery.from(CostCenter.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.equal(root.get("companyCode").get("code"), companyCode));

        // Check Validity
        Date currentDate = DateUtil.getCurrentUTCDateTime();
        Path<Date> startDate = (Path<Date>) root.get("effectiveStartDate");
        Path<Date> endDate = (Path<Date>) root.get("effectiveEndDate");

        predicatesRules.add(criteriaBuilder.lessThanOrEqualTo((Expression) startDate, currentDate));
        predicatesRules.add(criteriaBuilder.or(criteriaBuilder.greaterThanOrEqualTo((Expression) endDate, currentDate), criteriaBuilder.isNull(endDate)));

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
        List<CostCenterDTO> costCenters = CommonHelper.transformCCEtyToDTOs(queryForResultSets.getResultList());
        if (costCenters != null) {
            pageObject.setGridContents(new ArrayList<PageResults>(costCenters));
        } else {
            pageObject.setGridContents(null);
        }
        return pageObject;
    }
    
    @Override
    public CostCenter findCostCenterByCompanyCodeAndStartDate(String companyCode, String costCenter, Date effictiveStartDate) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();

        Root root = criteriaQuery.from(CostCenter.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        if (!StringUtils.isEmpty(companyCode)) {
            predicatesRules.add(criteriaBuilder.equal(root.get("companyCode").get("code"), companyCode));
        }
        if (!StringUtils.isEmpty(costCenter)) {
            predicatesRules.add(criteriaBuilder.equal(root.get("costCenter"), costCenter));
        }
        if (effictiveStartDate == null) {
            effictiveStartDate = DateUtil.getCurrentUTCDateTime();
            
        }
        
        Path<Date> startDate = (Path<Date>) root.get("effectiveStartDate");
        Path<Date> endDate = (Path<Date>) root.get("effectiveEndDate");

        predicatesRules.add(criteriaBuilder.lessThanOrEqualTo((Expression) startDate, effictiveStartDate));
        predicatesRules.add(criteriaBuilder.or(criteriaBuilder.greaterThanOrEqualTo((Expression) endDate, effictiveStartDate), 
                                               criteriaBuilder.isNull(endDate)));

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root)
                                                               .where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));
        Query queryForResultSets = entityManager.createQuery(criteriaQueryForResultSet);

        List<CostCenter> costCenters = queryForResultSets.getResultList();
        if (costCenters != null && !costCenters.isEmpty()) {
            return costCenters.get(0);
        }
        return null;
    }
    
    @Override
    public void deleteCostCentersByCompanyCode(String companyCode) {
        Query query = getEntityManager().createNamedQuery("deleteCostCentersByCompanyCode");
        query.setParameter("companyCode", companyCode);
        query.executeUpdate();
    }
    
    @Override
    public Set<String> filterInvalidCostCenters(Set<String> costCenterList, String companyCode) {
        List<String> result = new ArrayList<String>();
        PaginatedArrayList<String> costCenterPaginatedList = new PaginatedArrayList<String>(costCenterList);
        EntityManager entityManager = getEntityManager();
        for (List<String> subListCostCenter = null; (subListCostCenter = costCenterPaginatedList.nextPage()) != null;) {
            Query query = entityManager.createNamedQuery("filterInvalidCostCenter");
            query.setParameter("costCenter", subListCostCenter);
            query.setParameter("companyCode", companyCode);
            
            result.addAll(query.getResultList());
        }
        return new HashSet<String>(result);
    }
}
