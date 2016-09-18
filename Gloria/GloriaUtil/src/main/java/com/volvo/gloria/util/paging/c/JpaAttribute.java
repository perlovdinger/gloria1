package com.volvo.gloria.util.paging.c;

/**
 * entity field mapping definition for sorting/filtering.
 * 
 */
public class JpaAttribute {
    private String jpaAttributeName;
    private JpaAttributeType jpaAttributeType;
    private Class<?> jpaAttributeClass;

    public JpaAttribute(String jpaAttributeName, JpaAttributeType jpaAttributeType, Class<?> jpaAttributeClass) {
        this.jpaAttributeName = jpaAttributeName;
        this.jpaAttributeType = jpaAttributeType;
        this.jpaAttributeClass = jpaAttributeClass;
    }

    public JpaAttribute(String jpaAttributeName, JpaAttributeType jpaAttributeType) {
        this.jpaAttributeName = jpaAttributeName;
        this.jpaAttributeType = jpaAttributeType;
    }

    public String getJpaAttributeName() {
        return jpaAttributeName;
    }

    public void setJpaAttributeName(String jpaAttributeName) {
        this.jpaAttributeName = jpaAttributeName;
    }

    public JpaAttributeType getJpaAttributeType() {
        return jpaAttributeType;
    }

    public void setJpaAttributeType(JpaAttributeType jpaAttributeType) {
        this.jpaAttributeType = jpaAttributeType;
    }

    public Class<?> getJpaAttributeClass() {
        return jpaAttributeClass;
    }

    public void setJpaAttributeClass(Class<?> jpaAttributeClass) {
        this.jpaAttributeClass = jpaAttributeClass;
    }
}
