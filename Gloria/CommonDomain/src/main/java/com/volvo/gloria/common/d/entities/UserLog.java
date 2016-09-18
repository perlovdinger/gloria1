package com.volvo.gloria.common.d.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import com.volvo.gloria.util.persistence.GenericEntity;

/**
 * Entity Class for User Log.
 */
@Entity
@Table(name = "USER_LOG")
public class UserLog implements GenericEntity<Long>, Serializable {

    private static final long serialVersionUID = -3581779987220940989L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_LOG_OID")
    private long userLogOID;

    @Version
    private long version;
    private String userId;
    private String userName;
    @Temporal(TemporalType.TIMESTAMP)
    private Date loggedTime;
    private String userText;
    private String action;
    private String actionDetail;

    public long getUserLogOID() {
        return userLogOID;
    }

    public void setUserLogOID(long userLogOID) {
        this.userLogOID = userLogOID;
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

    public Date getLoggedTime() {
        return loggedTime;
    }

    public void setLoggedTime(Date loggedTime) {
        this.loggedTime = loggedTime;
    }

    public String getUserText() {
        return userText;
    }

    public void setUserText(String userText) {
        this.userText = userText;
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

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public Long getId() {
        return userLogOID;
    }

    @Override
    public long getVersion() {
        return version;
    }   
}
