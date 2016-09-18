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

import com.volvo.gloria.authorization.d.entities.ReportFilter;
import com.volvo.gloria.authorization.d.entities.ReportFilterMaterial;
import com.volvo.gloria.authorization.d.entities.ReportFilterOrder;
import com.volvo.gloria.common.repositories.b.ReportFilterRepository;
import com.volvo.gloria.util.persistence.GenericAbstractRepositoryBean;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * Repository class ReportFilter.
 * 
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ReportFilterRepositoryBean extends GenericAbstractRepositoryBean<ReportFilter, Long> implements ReportFilterRepository {

    @PersistenceContext(unitName = "UserAuthorizationDomainPU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }

    @Override
    public ReportFilterMaterial save(ReportFilterMaterial reportFilterMaterial) {
        ReportFilterMaterial toSave = reportFilterMaterial;
        if (reportFilterMaterial.getId() == null || new Long(0).equals(reportFilterMaterial.getId())) {
            getEntityManager().persist(reportFilterMaterial);
        } else {
            toSave = getEntityManager().merge(reportFilterMaterial);
        }
        return toSave;
    }

    @Override
    public ReportFilterOrder save(ReportFilterOrder reportFilterOrder) {
        ReportFilterOrder toSave = reportFilterOrder;
        if (reportFilterOrder.getId() == null || new Long(0).equals(reportFilterOrder.getId())) {
            getEntityManager().persist(reportFilterOrder);
        } else {
            toSave = getEntityManager().merge(reportFilterOrder);
        }
        return toSave;
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<ReportFilter> getReportFilters(String reportType, String userId) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(ReportFilter.class);
        List<Predicate> predicatesRules = new ArrayList<Predicate>();
        predicatesRules.add(criteriaBuilder.equal(root.get("type"), reportType));
        predicatesRules.add(criteriaBuilder.equal(root.get("gloriaUser").get("userID"), userId));
        Query resultSetQuery = entityManager.createQuery(criteriaQuery.select(root)
                                            .where(criteriaBuilder.and(predicatesRules.toArray(new Predicate[predicatesRules.size()]))));
        return resultSetQuery.getResultList();
    }

    @Override
    public void remove(long reportFilterId) {
        delete(reportFilterId);
    }
    
    @Override
    public ReportFilter findReportFilterById(long id) {
        return findById(id);
    }
}
