package com.volvo.gloria.util.persistence;

import java.io.Serializable;

/**
 * Base class for repository entities.
 * 
 * @param <T>
 */
public interface GenericEntity<T> extends Serializable {
    T getId();

    long getVersion();
}
