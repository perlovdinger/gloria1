package com.volvo.gloria.procurematerial.c.dto;

import java.io.Serializable;
import java.util.Date;

import com.volvo.gloria.util.paging.c.PageResults;

/**
 * 
 */
public class ChangeIdDTO implements Serializable, PageResults{

    private static final long serialVersionUID = -1707276070300893034L;

    private long id;
    private long version;
    private String procureNotes;
    private String changeId;
    private String status;
    private Date receivedDate;

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

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

    public String getProcureNotes() {
        return procureNotes;
    }

    public void setProcureNotes(String procureNotes) {
        this.procureNotes = procureNotes;
    }

    public String getChangeId() {
        return changeId;
    }

    public void setChangeId(String changeId) {
        this.changeId = changeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
