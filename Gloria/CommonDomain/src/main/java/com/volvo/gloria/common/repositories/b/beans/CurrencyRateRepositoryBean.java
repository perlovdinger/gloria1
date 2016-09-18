/**
 * 
 */
package com.volvo.gloria.common.repositories.b.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.volvo.gloria.common.d.entities.CurrencyRate;
import com.volvo.gloria.common.repositories.b.CurrencyRateRepository;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.persistence.GenericAbstractRepositoryBean;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * 
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class CurrencyRateRepositoryBean extends GenericAbstractRepositoryBean<CurrencyRate, Long> implements CurrencyRateRepository {

    @PersistenceContext(unitName = "CommonDomainPU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public CurrencyRate findValidCurrencyRate(String currencyCode) {

        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(CurrencyRate.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.equal(criteriaBuilder.upper(root.get("currency").get("code")), currencyCode.toUpperCase()));
        Date currentDate = DateUtil.getDateWithZeroTime(DateUtil.getSqlDate());
        predicatesRules.add(criteriaBuilder.lessThanOrEqualTo(root.get("startDate"), currentDate));
        predicatesRules.add(criteriaBuilder.greaterThanOrEqualTo(root.get("endDate"), currentDate));

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root)
                                                               .where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));
        criteriaQueryForResultSet.orderBy(criteriaBuilder.desc(root.get("endDate")));

        Query queryForResultSets = entityManager.createQuery(criteriaQueryForResultSet);
        List<CurrencyRate> currencyRates = queryForResultSets.getResultList();
        if (currencyRates != null && !currencyRates.isEmpty()) {
            return currencyRates.get(0);
        }

        return null;
    }
}
