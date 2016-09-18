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

import com.volvo.gloria.common.d.entities.Traceability;
import com.volvo.gloria.common.d.entities.TraceabilityType;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.util.persistence.GenericAbstractRepositoryBean;
import com.volvo.jvs.runtime.platform.ContainerManaged;

@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class TraceabilityRepositoryBean extends GenericAbstractRepositoryBean<Traceability, Long> implements TraceabilityRepository {

    @PersistenceContext(unitName = "CommonDomainPU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<Traceability> getMaterialLineTraceabilitys(long materialOid) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(Traceability.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        if (materialOid > 0) {
            predicatesRules.add(criteriaBuilder.equal(root.get("materialOID"), materialOid));
        }
        predicatesRules.add(criteriaBuilder.equal(root.get("traceabilityType"), TraceabilityType.MATERIAL_LINE));

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root)
                                                               .where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));

        criteriaQueryForResultSet.orderBy(criteriaBuilder.desc(root.get("loggedTime")));

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);
        List<Traceability> traceabilitys = resultSetQuery.getResultList();
        if (traceabilitys != null && !traceabilitys.isEmpty()) {
            return traceabilitys;
        }
        return new ArrayList<Traceability>();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<Traceability> getOrderLineTraceabilitys(long orderLineOid) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(Traceability.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        if (orderLineOid > 0) {
            predicatesRules.add(criteriaBuilder.equal(root.get("orderLineOID"), orderLineOid));
        }
        predicatesRules.add(criteriaBuilder.equal(root.get("traceabilityType"), TraceabilityType.DELIVERY_CONTROLLER));
        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root)
                                                               .where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));

        criteriaQueryForResultSet.orderBy(criteriaBuilder.desc(root.get("loggedTime")));
        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);
        List<Traceability> traceabilitys = resultSetQuery.getResultList();
        if (traceabilitys != null && !traceabilitys.isEmpty()) {
            return traceabilitys;
        }
        return new ArrayList<Traceability>();
    }

}
