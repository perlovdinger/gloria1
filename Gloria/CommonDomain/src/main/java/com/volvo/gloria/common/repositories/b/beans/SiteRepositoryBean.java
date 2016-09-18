package com.volvo.gloria.common.repositories.b.beans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;

import com.volvo.gloria.common.d.entities.Site;
import com.volvo.gloria.common.repositories.b.SiteRepository;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.persistence.GenericAbstractRepositoryBean;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * Implementation for SiteRepository services.
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class SiteRepositoryBean extends GenericAbstractRepositoryBean<Site, Long> implements SiteRepository {

    private static final String WILDCARD = "%";

    @PersistenceContext(unitName = "CommonDomainPU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Site> getSiteBySiteIds(List<String> siteIds) {
        Query query = getEntityManager().createNamedQuery("getSiteBySiteIds");
        query.setParameter("siteIds", siteIds);
        return query.getResultList();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Site getSiteBySiteId(String siteId) {
        Query query = getEntityManager().createNamedQuery("getSiteBySiteId");
        query.setParameter("siteId", siteId);

        List resultList = query.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (Site) resultList.get(0);
        }
        return null;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<Site> getAllBuildSites(PageObject pageObject, String siteId) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(Site.class);
        List<Expression<Boolean>> predicates = new ArrayList<Expression<Boolean>>();
        predicates.add(criteriaBuilder.equal(root.get("buildSite"), true));
        if (!StringUtils.isEmpty(siteId)) {
            String siteStr = siteId.replaceFirst("\\*", WILDCARD).concat(WILDCARD);
            predicates.add(criteriaBuilder.or(criteriaBuilder.like(criteriaBuilder.lower(root.get("siteId")), siteStr),
                                              criteriaBuilder.like(criteriaBuilder.lower(root.get("siteName")), siteStr)));
        }
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root);
        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);
        return resultSetQuery.getResultList();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<Site> getAllsparePartSites() {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(Site.class);
        criteriaQuery.select(root);
        Query query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Site getSiteBySiteCode(String siteCode) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(Site.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("siteCode"), siteCode));
        criteriaQuery.select(root);
        Query query = entityManager.createQuery(criteriaQuery);
        List<Site> sites = query.getResultList();
        if (sites != null && !sites.isEmpty()) {
            return sites.get(0);
        }
        return null;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<String> getSiteIdsByCompanyCodes(Collection<String> companyCodeCodes) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
        Root root = criteriaQuery.from(Site.class);
        List<Expression<Boolean>> predicates = new ArrayList<Expression<Boolean>>();
        if (companyCodeCodes != null) {
            predicates.add(root.get("companyCode").in(companyCodeCodes));
        }
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root.get("siteId")).distinct(true);
        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);
        return resultSetQuery.getResultList();
    }

}
