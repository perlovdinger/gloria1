package com.volvo.gloria.common.repositories.b.beans;

import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.volvo.gloria.common.d.entities.DangerousGoods;
import com.volvo.gloria.common.repositories.b.DangerousGoodsRepository;
import com.volvo.gloria.util.persistence.GenericAbstractRepositoryBean;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * repository for root DangerousGoods.
 * 
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class DangerousGoodsRepositoryBean extends GenericAbstractRepositoryBean<DangerousGoods, Long> implements DangerousGoodsRepository {

    @PersistenceContext(unitName = "CommonDomainPU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }
    
    @Override
    @SuppressWarnings("unchecked")    
    public List<DangerousGoods> findAll() {
        Query query = getEntityManager().createNamedQuery("findAllDangerousGoods");
        return query.getResultList();
    }
}
