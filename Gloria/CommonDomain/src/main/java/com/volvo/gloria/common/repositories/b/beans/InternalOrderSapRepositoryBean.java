package com.volvo.gloria.common.repositories.b.beans;

import java.util.ArrayList;
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

import com.volvo.gloria.common.d.entities.InternalOrderSap;
import com.volvo.gloria.common.repositories.b.InternalOrderSapRepository;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.persistence.GenericAbstractRepositoryBean;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * repository for root InternalOrderSap.
 * 
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class InternalOrderSapRepositoryBean extends GenericAbstractRepositoryBean<InternalOrderSap, Long> implements InternalOrderSapRepository {

    private static final String WILDCARD = "%";

    @PersistenceContext(unitName = "CommonDomainPU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<InternalOrderSap> findInternalOrderSap(PageObject pageObject, String code) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(InternalOrderSap.class);
        List<Expression<Boolean>> predicates = new ArrayList<Expression<Boolean>>();
        if (!StringUtils.isEmpty(code)) {
            predicates.add(criteriaBuilder.or(criteriaBuilder.equal(root.get("code"), code),
                                              criteriaBuilder.like(root.get("code"), WILDCARD.concat(code).concat(WILDCARD))));
        }
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root);
        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);
        return resultSetQuery.getResultList();
    }
}
