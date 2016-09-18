package com.volvo.gloria.common.c.dto;


/**
 * object for Team transformation.
 * 
 */
public class TeamTransformerDTO {

    private String name;

    private String type;
    
    private String companyCodeGroup;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCompanyCodeGroup() {
        return companyCodeGroup;
    }

    public void setCompanyCodeGroup(String companyCodeGroup) {
        this.companyCodeGroup = companyCodeGroup;
    }
}
