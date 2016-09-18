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
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.volvo.gloria.common.d.entities.ConversionUnit;
import com.volvo.gloria.common.repositories.b.ConversionUnitRepository;
import com.volvo.gloria.util.persistence.GenericAbstractRepositoryBean;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * repository for root Conversionunit.
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ConversionUnitRepositoryBean  extends GenericAbstractRepositoryBean<ConversionUnit, Long> implements ConversionUnitRepository {
    
    @PersistenceContext(unitName = "CommonDomainPU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public ConversionUnit findConversionUnit(String conversionFromApp, String conversionToApp, String codeToConvert) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(ConversionUnit.class);

        List<Predicate> predicateRules = new ArrayList<Predicate>();
        predicateRules.add(criteriaBuilder.equal(root.get("applFrom"), conversionFromApp));
        predicateRules.add(criteriaBuilder.equal(root.get("applTo"), conversionToApp));
        predicateRules.add(criteriaBuilder.equal(root.get("codeFrom"), codeToConvert));

        Query query = entityManager.createQuery(criteriaQuery.select(root).where(predicateRules.toArray(new Predicate[predicateRules.size()])));
        List<ConversionUnit> conversionUnits = query.getResultList();
        if (conversionUnits != null && !conversionUnits.isEmpty()) {
            return conversionUnits.get(0);
        }
        return null;
    }
}
