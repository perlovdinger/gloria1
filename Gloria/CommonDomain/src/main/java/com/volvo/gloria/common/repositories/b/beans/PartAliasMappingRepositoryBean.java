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

import com.volvo.gloria.common.d.entities.PartAliasMapping;
import com.volvo.gloria.common.repositories.b.PartAliasMappingRepository;
import com.volvo.gloria.util.persistence.GenericAbstractRepositoryBean;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * repository for root PartAffiliation.
 * 
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class PartAliasMappingRepositoryBean extends GenericAbstractRepositoryBean<PartAliasMapping, Long> implements PartAliasMappingRepository {

    @PersistenceContext(unitName = "CommonDomainPU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public PartAliasMapping getGpsQualifier(String kolaDomain) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(PartAliasMapping.class);

        List<Predicate> predicateRules = new ArrayList<Predicate>();
        predicateRules.add(criteriaBuilder.equal(root.get("kolaDomain"), kolaDomain));
        Query query = entityManager.createQuery(criteriaQuery.select(root).where(predicateRules.toArray(new Predicate[predicateRules.size()])));
        List<PartAliasMapping> partAliasMapping = query.getResultList();
        if (partAliasMapping != null && !partAliasMapping.isEmpty()) {
            return partAliasMapping.get(0);
        }
        return null;

    }

}
