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

import com.volvo.gloria.common.d.entities.Currency;
import com.volvo.gloria.common.repositories.b.CurrencyRepository;
import com.volvo.gloria.util.persistence.GenericAbstractRepositoryBean;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * repository for root Currency.
 * 
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class CurrencyRepositoryBean extends GenericAbstractRepositoryBean<Currency, Long> implements CurrencyRepository {

    @PersistenceContext(unitName = "CommonDomainPU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Currency findByCode(String code) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(Currency.class);
        List<Expression<Boolean>> expressions = new ArrayList<Expression<Boolean>>();
        if (!StringUtils.isEmpty(code)) {
            expressions.add(criteriaBuilder.equal(root.get("code"), code));
        }
        criteriaQuery.where(criteriaBuilder.and(expressions.toArray(new Predicate[expressions.size()])));

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root);
        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);
        List<Currency> currencies = resultSetQuery.getResultList();
        if (currencies != null && !currencies.isEmpty()) {
            return currencies.get(0);
        }
        return null;
    }
}
