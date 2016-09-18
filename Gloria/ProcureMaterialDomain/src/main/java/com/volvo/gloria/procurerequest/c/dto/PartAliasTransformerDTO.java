package com.volvo.gloria.procurerequest.c.dto;

import java.io.Serializable;

/**
 * DTO for PartAlias.
 */
public class PartAliasTransformerDTO implements Serializable{

    private static final long serialVersionUID = 1689415266978363495L;
    
    private String code;
    private String domain;
    
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getDomain() {
        return domain;
    }
    public void setDomain(String domain) {
        this.domain = domain;
    }

}
