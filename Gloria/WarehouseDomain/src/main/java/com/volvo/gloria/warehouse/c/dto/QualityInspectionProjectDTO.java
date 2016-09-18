package com.volvo.gloria.warehouse.c.dto;

import java.io.Serializable;

public class QualityInspectionProjectDTO implements Serializable {

    private static final long serialVersionUID = 9116546004185716331L;
    private long id;
    private long version;
    private String project;
    private boolean mandatory;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

}
