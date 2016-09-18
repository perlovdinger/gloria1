package com.volvo.gloria.procurerequest.c.dto;

import java.io.Serializable;

/**
 * 
 */
public class ChangeIdTransformerDTO implements Serializable {
    private static final long serialVersionUID = -1707276070300893034L;
    
    private String changeTechId;
    private String changeId;
    private String changeVersion;
    private String type;
    private String procureNotes;
    private String priority;
    private String procureRequestId;   
   

    private String procureMessageId;
    private String title;
    private String materialRequestChangeAddId;
    
    public String getChangeTechId() {
        return changeTechId;
    }
    
    public void setChangeTechId(String changeTechId) {
        this.changeTechId = changeTechId;
    }
    
    public String getChangeId() {
        return changeId;
    }

    public void setChangeId(String changeId) {
        this.changeId = changeId;
    }
    
    public String getChangeVersion() {
        return changeVersion;
    }
    
    public void setChangeVersion(String changeVersion) {
        this.changeVersion = changeVersion;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getProcureNotes() {
        return procureNotes;
    }
    
    public void setProcureNotes(String procureNotes) {
        this.procureNotes = procureNotes;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getProcureRequestId() {
        return procureRequestId;
    }

    public void setProcureRequestId(String procureRequestId) {
        this.procureRequestId = procureRequestId;
    }

    public String getProcureMessageId() {
        return procureMessageId;
    }

    public void setProcureMessageId(String procureMessageId) {
        this.procureMessageId = procureMessageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        if (this.getChangeTechId().equalsIgnoreCase(((ChangeIdTransformerDTO) obj).getChangeTechId())) {
            return true;
        }
        return false;
    }

    // Base hashCode on articleId and storageLocationId
    @Override
    public int hashCode() {
        return this.getChangeTechId().hashCode();
    }

    public String getMaterialRequestChangeAddId() {
        return materialRequestChangeAddId;
    }

    public void setMaterialRequestChangeAddId(String materialRequestChangeAddId) {
        this.materialRequestChangeAddId = materialRequestChangeAddId;
    }

}
