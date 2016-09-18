package com.volvo.gloria.procurematerial.util.migration.c.dto;

import java.io.Serializable;

import com.volvo.gloria.util.paging.c.PageResults;

/**
 * progressive status of migration process.
 */
public class MigrationStatusDTO implements Serializable, PageResults{
    /**
     * 
     */
    private static final long serialVersionUID = 6572272203599877953L;
    private boolean isDone;
    private String status;
    private int completed;
    private String siteCompleted;
    private String siteInProgress;
    public boolean isDone() {
        return isDone;
    }
    public void setDone(boolean isDone) {
        this.isDone = isDone;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public int getCompleted() {
        return completed;
    }
    public void setCompleted(int completed) {
        this.completed = completed;
    }
    public String getSiteCompleted() {
        return siteCompleted;
    }
    public void setSiteCompleted(String siteCompleted) {
        this.siteCompleted = siteCompleted;
    }
    public String getSiteInProgress() {
        return siteInProgress;
    }
    public void setSiteInProgress(String siteInProgress) {
        this.siteInProgress = siteInProgress;
    }

}
