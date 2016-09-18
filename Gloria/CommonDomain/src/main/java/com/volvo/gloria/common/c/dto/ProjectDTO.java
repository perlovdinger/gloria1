package com.volvo.gloria.common.c.dto;

import java.io.Serializable;

import com.volvo.gloria.util.paging.c.PageResults;

public class ProjectDTO implements Serializable, PageResults {

    /**
     * 
     */
    private static final long serialVersionUID = 6624090762565476944L;
    
    private String projectId;
    
    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
    
    @Override
    public String toString() {
        return projectId;
    }
}
