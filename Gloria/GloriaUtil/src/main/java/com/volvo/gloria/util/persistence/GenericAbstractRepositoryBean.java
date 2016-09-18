package com.volvo.gloria.util.persistence;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * @param <T>
 * @param <ID>
 */
@ContainerManaged
@Transactional(propagation = Propagation.REQUIRED)
@SuppressWarnings("unchecked")
public abstract class GenericAbstractRepositoryBean<T extends GenericEntity<ID>, ID extends Serializable> implements GenericRepository<T, ID> {
    
    private Class<T> persistentClass;

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericAbstractRepositoryBean.class);

    private EntityManager entityManager;
    
    public GenericAbstractRepositoryBean() {
        this.persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    public Class<T> getPersistentClass() {
        return persistentClass;
    }

    public T save(T instanceToSave) {
        T toSave = instanceToSave;
            if (instanceToSave.getId() == null || new Long(0).equals(instanceToSave.getId())) {
                LOGGER.debug("Creating new instance {} ...", instanceToSave);
                entityManager.persist(instanceToSave);
                LOGGER.debug("Creating new instance {} done.", instanceToSave);
            } else {
                LOGGER.debug("Updating {} ...", instanceToSave);
                toSave = entityManager.merge(instanceToSave);
                LOGGER.debug("Updating {} done.", instanceToSave);
            }
        return toSave;
    }

    public T findById(ID id) {
        T result = null;
            result = entityManager.find(persistentClass, id);
        return result;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public T findByIdNoTx(ID id) {
        T result = null;
            result = entityManager.find(persistentClass, id);
        return result;
    }

    public void delete(T instanceToDelete) {
        delete((ID) instanceToDelete.getId());
    }
    
    public void delete(ID id) {
            T instanceToRemove = findById(id);
            entityManager.remove(instanceToRemove);
    }

    public List<T> findAll() {
        List<T> result = new ArrayList<T>();
        Query query = entityManager.createQuery("SELECT persistentClass FROM " + persistentClass.getSimpleName() + " persistentClass");
        result = query.getResultList();
        return result;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<T> findAllNoTx() {
        List<T> result = new ArrayList<T>();
        Query query = entityManager.createQuery("SELECT persistentClass FROM " + persistentClass.getSimpleName() + " persistentClass");
        result = query.getResultList();
        return result;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public long count() {
        long result = 0;
        Query query = entityManager.createQuery("SELECT COUNT(*) AS TOTAL FROM " + persistentClass.getSimpleName());
        result = (Long) query.getSingleResult();
        return result;
    }

    public List<T> findByIds(List<ID> ids) {
        Query query = entityManager.createQuery("SELECT persistentClass FROM " + persistentClass.getSimpleName() + " persistentClass "
                + "WHERE persistentClass.id IN (:ids) ORDER BY persistentClass.id");
        query.setParameter("ids", ids);
        return query.getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<T> findByIdsNoTx(List<ID> ids) {
        Query query = entityManager.createQuery("SELECT persistentClass FROM " + persistentClass.getSimpleName() + " persistentClass "
                + "WHERE persistentClass.id IN (:ids) ORDER BY persistentClass.id");
        query.setParameter("ids", ids);
        return query.getResultList();
    }

    public void flush() {
        entityManager.flush();
    }
}
