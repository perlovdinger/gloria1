package com.volvo.gloria.common.c.dto;

import java.io.Serializable;

/**
 * Data Transfer Object for Delivery FollowUp Team. 
 */
public class DeliveryFollowUpTeamDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private long version;
    private Long id;
    private String name;
    private String defaultDcUserid;
    private String followUpType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefaultDcUserid() {
        return defaultDcUserid;
    }

    public void setDefaultDcUserid(String defaultDcUserid) {
        this.defaultDcUserid = defaultDcUserid;
    }

    public String getFollowUpType() {
        return followUpType;
    }

    public void setFollowUpType(String followUpType) {
        this.followUpType = followUpType;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
