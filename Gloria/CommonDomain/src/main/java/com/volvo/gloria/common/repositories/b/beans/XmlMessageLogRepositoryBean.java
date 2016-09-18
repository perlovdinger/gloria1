package com.volvo.gloria.common.repositories.b.beans;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.volvo.gloria.common.d.entities.XmlMessageLog;
import com.volvo.gloria.common.repositories.b.XmlMessageLogRepository;
import com.volvo.gloria.util.persistence.GenericAbstractRepositoryBean;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * Repository for root XmlMessageLog.
 * 
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class XmlMessageLogRepositoryBean extends GenericAbstractRepositoryBean<XmlMessageLog, Long> implements XmlMessageLogRepository {

    @PersistenceContext(unitName = "CommonDomainPU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }
    
}
