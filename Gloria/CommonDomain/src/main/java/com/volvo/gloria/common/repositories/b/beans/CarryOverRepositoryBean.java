package com.volvo.gloria.common.repositories.b.beans;

import java.sql.Date;
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
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;

import com.volvo.gloria.common.d.entities.CarryOver;
import com.volvo.gloria.common.repositories.b.CarryOverRepository;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.persistence.GenericAbstractRepositoryBean;
import com.volvo.jvs.runtime.platform.ContainerManaged;
/**
 * repository for root CarryOver.
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class CarryOverRepositoryBean extends GenericAbstractRepositoryBean<CarryOver, Long> implements CarryOverRepository {

    @PersistenceContext(unitName = "CommonDomainPU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }

    @Override
    public List<CarryOver> findCarryOverByPartNumberPartversionAndCustomerId(String partNumber, String partVersion, String partAffiliation, String customerId,
            String supplierId) {
        if (partNumber == null) {
            return new ArrayList<CarryOver>();
        }
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();

        Root root = criteriaQuery.from(CarryOver.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.equal(root.get("partNumber"), partNumber));
        if (!StringUtils.isEmpty(partVersion)) {
            predicatesRules.add(criteriaBuilder.equal(root.get("partVersion"), partVersion));
        }
        predicatesRules.add(criteriaBuilder.equal(root.get("partAffiliation"), partAffiliation));
        if (!StringUtils.isEmpty(customerId)) {
            predicatesRules.add(criteriaBuilder.equal(root.get("customerId"), customerId));
        }
        if (!StringUtils.isEmpty(supplierId)) {
            predicatesRules.add(criteriaBuilder.equal(root.get("supplierId"), supplierId));
        }
        Path<Date> startDate = (Path<Date>) root.get("startDate");
        predicatesRules.add(criteriaBuilder.or(criteriaBuilder.isNull((Expression) startDate),
                                               criteriaBuilder.lessThanOrEqualTo((Expression) startDate, DateUtil.getSqlDate())));

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root)
                                                               .where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])))
                                                               .orderBy(criteriaBuilder.desc(root.get("startDate")));
        Query queryForResultSets = entityManager.createQuery(criteriaQueryForResultSet);

        return queryForResultSets.getResultList();
    }
        
    @Override
    public List<CarryOver> findCarryOverAlias(String partNumber, String partAffiliation, String customerId) {
       return findCarryOverByPartNumberPartversionAndCustomerId(partNumber, null, partAffiliation, customerId,
                                                          null);
    }

    @Override
    public void deleteAllCarryOver() {
        Query query = getEntityManager().createNamedQuery("deleteAllCarryOver");
        query.executeUpdate();
    }
    
    @Override
    public CarryOver findUniqueCarryOver(String customerId, String supplierId, String partAffiliation, String partNumber, String partVersion) {
        if (customerId == null || supplierId == null || partAffiliation == null || partNumber == null || partVersion == null) {
            return null;
        }
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();

        Root root = criteriaQuery.from(CarryOver.class);

        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.equal(root.get("customerId"), customerId));
        predicatesRules.add(criteriaBuilder.equal(root.get("supplierId"), supplierId));
        predicatesRules.add(criteriaBuilder.equal(root.get("partAffiliation"), partAffiliation));
        predicatesRules.add(criteriaBuilder.equal(root.get("partNumber"), partNumber));
        predicatesRules.add(criteriaBuilder.equal(root.get("partVersion"), partVersion));
        Path<Date> startDate = (Path<Date>) root.get("startDate");
        predicatesRules.add(criteriaBuilder.or(criteriaBuilder.isNull((Expression) startDate),
                                               criteriaBuilder.lessThanOrEqualTo((Expression) startDate, DateUtil.getSqlDate())));

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root)
                                                               .where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()])));
        Query queryForResultSets = entityManager.createQuery(criteriaQueryForResultSet);
        
        List<CarryOver> resultList = queryForResultSets.getResultList();
        
        if (resultList != null && !resultList.isEmpty()) {
            return resultList.get(0);
        }
        return null;
    }

}
