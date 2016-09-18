package com.volvo.gloria.util.c.dto;

public class LDAPUserDTO {
    private String userId;
    private String userName;
    private String email;
    private String department;
    private String volvoCostCentre;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getVolvoCostCentre() {
        return volvoCostCentre;
    }

    public void setVolvoCostCentre(String volvoCostCentre) {
        this.volvoCostCentre = volvoCostCentre;
    }
    
}
