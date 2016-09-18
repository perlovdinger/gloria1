package com.volvo.gloria.procurematerial.repositories.b.beans;

import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.volvo.gloria.procurematerial.d.entities.PartAlias;
import com.volvo.gloria.procurematerial.d.entities.PartNumber;
import com.volvo.gloria.procurematerial.repositories.b.PartNumberRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.persistence.GenericAbstractRepositoryBean;
import com.volvo.jvs.runtime.platform.ContainerManaged;

@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class PartNumberRepositoryBean extends GenericAbstractRepositoryBean<PartNumber, Long> implements PartNumberRepository {

    @PersistenceContext(unitName = "ProcureMaterialDomainPU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }

    @Override
    public void addPartNumber(PartNumber partNumber) {
        getEntityManager().persist(partNumber);
    }

    @Override
    public PartNumber findPartNumberById(long partNumberOid) throws GloriaApplicationException {
        return getEntityManager().find(PartNumber.class, partNumberOid);
    }

    @Override
    public void updatePartNumber(PartNumber partNumber) {
        getEntityManager().merge(partNumber);

    }

    @Override
    public void deletePartNumber(PartNumber partNumber) throws GloriaApplicationException {
        getEntityManager().remove(partNumber);

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<PartAlias> getPartAliases(String volvoPartNo) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(PartAlias.class);

        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("partNumber").get("volvoPartNumber"),
                                                                                                         volvoPartNo));

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        return resultSetQuery.getResultList();
    }

    @Override
    public void addPartAlias(PartAlias partAlias) {
        getEntityManager().persist(partAlias);
    }

    @Override
    public PartAlias findPartAliasById(long partAliasOid) throws GloriaApplicationException {
        return getEntityManager().find(PartAlias.class, partAliasOid);
    }

    @Override
    public void updatePartAlias(PartAlias partAlias) {
        getEntityManager().merge(partAlias);
    }

    @Override
    public void deletePartAlias(PartAlias partAlias) throws GloriaApplicationException {
        getEntityManager().remove(partAlias);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public PartNumber findVolvoPartWithAliasByPartNumber(String partNumber) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(PartNumber.class);
        root.join("partAlias");
        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root).where(criteriaBuilder.equal(criteriaBuilder.lower(root.get("volvoPartNumber")),
                                                                                                         partNumber.toLowerCase()));

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        List resultList = resultSetQuery.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (PartNumber) resultList.get(0);
        }
        return null;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public PartAlias findPartAliasByAliasName(String partAlias) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root root = criteriaQuery.from(PartAlias.class);
        CriteriaQuery criteriaQueryForResultSet = criteriaQuery.select(root).where(criteriaBuilder.equal(criteriaBuilder.lower(root.get("aliasPartNumber")),
                                                                                                         partAlias.toLowerCase()));

        Query resultSetQuery = entityManager.createQuery(criteriaQueryForResultSet);

        List resultList = resultSetQuery.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (PartAlias) resultList.get(0);
        }
        return null;
    }
}
