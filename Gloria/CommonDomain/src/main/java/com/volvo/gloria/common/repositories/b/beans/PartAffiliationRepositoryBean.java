package com.volvo.gloria.common.repositories.b.beans;

import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.volvo.gloria.common.d.entities.PartAffiliation;
import com.volvo.gloria.common.repositories.b.PartAffiliationRepository;
import com.volvo.gloria.util.persistence.GenericAbstractRepositoryBean;
import com.volvo.jvs.runtime.platform.ContainerManaged;
import javax.persistence.Query;

/**
 * repository for root PartAffiliation.
 * 
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class PartAffiliationRepositoryBean extends GenericAbstractRepositoryBean<PartAffiliation, Long> implements PartAffiliationRepository {
    
    @PersistenceContext(unitName = "CommonDomainPU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }

    
    @Override
    @SuppressWarnings({ "unchecked" })
    public List<PartAffiliation> getAllPartAffiliations(boolean requestable) {     
        Query query = getEntityManager().createNamedQuery("getAllPartAffiliations");
        query.setParameter("requestable", requestable);
        return query.getResultList();
    }
    
}
