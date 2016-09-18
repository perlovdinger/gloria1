package com.volvo.gloria.common.repositories.b.beans;

import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.volvo.gloria.common.d.entities.UnitOfMeasure;
import com.volvo.gloria.common.repositories.b.UnitOfMeasureRepository;
import com.volvo.gloria.util.persistence.GenericAbstractRepositoryBean;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * repository for root UnitOfMeasure.
 * 
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class UnitOfMeasureRepositoryBean extends GenericAbstractRepositoryBean<UnitOfMeasure, Long> implements UnitOfMeasureRepository {

    @PersistenceContext(unitName = "CommonDomainPU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public UnitOfMeasure findUnitOfMeasureByCode(String code) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(UnitOfMeasure.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("code"), code));
        criteriaQuery.select(root);
        Query query = entityManager.createQuery(criteriaQuery);

        List resultList = query.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (UnitOfMeasure) resultList.get(0);
        }
        return null;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<UnitOfMeasure> findAllUnitOfMeasuresSupportedForGloria() {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(UnitOfMeasure.class);        
        criteriaQuery.select(root).where(criteriaBuilder.isTrue(root.get("gloriaCode")));
        Query query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }
}
