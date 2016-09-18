package com.volvo.gloria.util.tracing.c;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * DTO to support tracing.
 * 
 */
public abstract class TraceabilityDTO implements Serializable {

    private static final long serialVersionUID = 114905410433634226L;

    private long id;    
    private Timestamp loggedTime;
    private String action;
    private String actionDetail;
    private String userId;
    private String userName;
    private String i18MessageCode;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Timestamp getLoggedTime() {
        return loggedTime;
    }

    public void setLoggedTime(Timestamp loggedTime) {
        this.loggedTime = loggedTime;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getActionDetail() {
        return actionDetail;
    }

    public void setActionDetail(String actionDetail) {
        this.actionDetail = actionDetail;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getI18MessageCode() {
        return i18MessageCode;
    }

    public void setI18MessageCode(String i18MessageCode) {
        this.i18MessageCode = i18MessageCode;
    }
}
