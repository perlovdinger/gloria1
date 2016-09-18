/**
 * 
 */
package com.volvo.gloria.common.repositories.b.beans;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.volvo.gloria.common.d.entities.QualityDocument;
import com.volvo.gloria.common.repositories.b.QualityDocumentRepository;
import com.volvo.gloria.util.persistence.GenericAbstractRepositoryBean;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * 
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class QualityDocumentRepositoryBean extends GenericAbstractRepositoryBean<QualityDocument, Long> implements QualityDocumentRepository {

    @PersistenceContext(unitName = "CommonDomainPU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }

}
