package com.volvo.gloria.util.tracing.c;

import java.io.Serializable;

/**
 * Custom property type. 
 * 
 * Use to set the additional attributes to support tracing.
 * 
 */
public class Traceable implements Serializable {

    private static final long serialVersionUID = 4172657757814178746L;

    private Object value;

    private Class<?> type;

    private boolean transform;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public boolean isTransform() {
        return transform;
    }

    public void setTransform(boolean transform) {
        this.transform = transform;
    }
}
