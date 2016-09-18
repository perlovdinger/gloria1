package com.volvo.gloria.util.persistence;

import java.io.Serializable;
import java.util.List;


/**
 * @param <T> The entity class
 * @param <ID> The class representing the ID of the Entity
 */
public interface GenericRepository<T extends GenericEntity<?>, ID extends Serializable> {

    T findById(ID id);
    /**
     * Suspends a current transaction before done.
     * @return the return object will be detached since no transaction is supported
     */
    T findByIdNoTx(ID id);
    void delete(T instanceToDelete);
    void delete(ID id);
    List<T> findAll();
    /**
     * Suspends a current transaction before done.
     * @return the return object will be detached since no transaction is supported
     */
    List<T> findAllNoTx();
    T save(T instanceToCreate);
    List<T> findByIds(List<ID> ids);  
    List<T> findByIdsNoTx(List<ID> ids);  

    long count();
    void flush();
}
