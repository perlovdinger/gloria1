package com.volvo.gloria.common.c.dto;

import java.io.Serializable;

import com.volvo.gloria.util.paging.c.PageResults;

/**
 * Data class for wbselement details.
 */
public class WbsElementDTO implements Serializable, PageResults {

    private static final long serialVersionUID = 1055462446300792832L;
    
    private String action;
    private long id;
    private String wbs;
    private String description;
    private String projectId;
    private String companyCode;
    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getWbs() {
        return wbs;
    }
    
    public void setWbs(String wbs) {
        this.wbs = wbs;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getProjectId() {
        return projectId;
    }
    
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
    public String getCompanyCode() {
        return companyCode;
    }
    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }
    
    @Override
    public String toString() {
        return wbs;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj != null && ((WbsElementDTO) obj).getWbs().equals(this.wbs)) {
            return true;
        }
       
        return false;
    }
    
    @Override
    public int hashCode() {
        return wbs.hashCode();
    }
}
