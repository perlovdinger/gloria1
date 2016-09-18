package com.volvo.gloria.procurematerial.repositories.b.beans;

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

import com.volvo.gloria.procurematerial.d.entities.MessageStatus;
import com.volvo.gloria.procurematerial.d.entities.OrderSap;
import com.volvo.gloria.procurematerial.d.entities.OrderSapAccounts;
import com.volvo.gloria.procurematerial.d.entities.OrderSapLine;
import com.volvo.gloria.procurematerial.d.entities.OrderSapSchedule;
import com.volvo.gloria.procurematerial.repositories.b.OrderSapRepository;
import com.volvo.gloria.util.persistence.GenericAbstractRepositoryBean;
import com.volvo.jvs.runtime.platform.ContainerManaged;

@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class OrderSapRepositoryBean extends GenericAbstractRepositoryBean<OrderSap, Long> implements OrderSapRepository {

    @PersistenceContext(unitName = "ProcureMaterialDomainPU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }

    @Override
    public void addOrderSapLine(OrderSapLine orderSapLine) {
        getEntityManager().persist(orderSapLine);
    }

    @Override
    public OrderSapLine findOrderSapLineById(Long orderSapLineOid) {
        return getEntityManager().find(OrderSapLine.class, orderSapLineOid);
    }

    @Override
    public OrderSapLine updateOrderSapLine(OrderSapLine orderSapLine) {
        return getEntityManager().merge(orderSapLine);
    }

    @Override
    public void deleteOrderSapLine(long orderSapLineOid) {
        getEntityManager().remove(orderSapLineOid);
    }

    @Override
    public void addOrderSapSchedule(OrderSapSchedule orderSapSchedule) {
        getEntityManager().persist(orderSapSchedule);
    }

    @Override
    public OrderSapSchedule findOrderSapScheduleById(Long orderSapScheduleOid) {
        return getEntityManager().find(OrderSapSchedule.class, orderSapScheduleOid);
    }

    @Override
    public OrderSapSchedule updateOrderSapSchedule(OrderSapSchedule orderSapSchedule) {
        return getEntityManager().merge(orderSapSchedule);
    }

    @Override
    public void deleteOrderSapSchedule(long orderSapScheduleOid) {
        getEntityManager().remove(orderSapScheduleOid);
    }

    @Override
    public void addOrderSapAccounts(OrderSapAccounts orderSapAccounts) {
        getEntityManager().persist(orderSapAccounts);
    }

    @Override
    public OrderSapAccounts findOrderSapAccountsById(Long orderSapAccountsOid) {
        return getEntityManager().find(OrderSapAccounts.class, orderSapAccountsOid);
    }

    @Override
    public OrderSapAccounts updateOrderSapAccounts(OrderSapAccounts orderSapAccounts) {
        return getEntityManager().merge(orderSapAccounts);
    }

    @Override
    public void deleteOrderSapAccounts(long orderSapAccountsOid) {
        getEntityManager().remove(orderSapAccountsOid);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public OrderSap findOrderSapByUniqueExtOrder(String uniqueExtOrder) {
        Query query = getEntityManager().createNamedQuery("findOrderSapByUniqueExtOrder");
        query.setParameter("uniqueExtOrder", uniqueExtOrder);

        List<OrderSap> orderSapList = query.getResultList();
        if (orderSapList != null && !orderSapList.isEmpty()) {
            return orderSapList.get(0);
        }
        return null;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<OrderSap> findOrderSapForCompanyCode(String companyCode) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(OrderSap.class);
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(criteriaBuilder.and(root.get("companyCode").in(companyCode)));
        predicates.add(criteriaBuilder.and(root.get("messageStatus").in(MessageStatus.WAIT_FOR_SEND)));

        Query query = entityManager.createQuery(criteriaQuery.select(root).distinct(true).where(predicates.toArray(new Predicate[predicates.size()]))
                                                             .orderBy(criteriaBuilder.desc(root.get("orderSapLines").get("orderSapLineOID"))));
        List<OrderSap> result = query.getResultList();

        for (OrderSap orderSap : result) {
            for (OrderSapLine orderSapLine : orderSap.getOrderSapLines()) {
                orderSapLine.getOrderSapAccounts();
                orderSapLine.getOrderSapSchedules();
            }
        }

        return result;
    }
}
