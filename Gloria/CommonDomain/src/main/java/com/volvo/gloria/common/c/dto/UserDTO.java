package com.volvo.gloria.common.c.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.volvo.gloria.util.paging.c.PageResults;

/**
 * DTO class for user.
 */
public class UserDTO implements PageResults {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNo;
    private String departament;
    private String languageCode;
    private String countryCode;
    private boolean limitedAccess;
    private String procureTeam;
    private String delFollowUpTeam;
    private String internalProcureTeam;
    private String whSite;
    private String teamNames;
    private String userName;
    private String userCategoryID;
    private List<String> companyGroupCodes = new ArrayList<String>();

    public String getId() {
        return id;
    }

    public void setId(String userId) {
        this.id = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getDepartament() {
        return departament;
    }

    public void setDepartament(String departament) {
        this.departament = departament;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public boolean hasLimitedAccess() {
        return limitedAccess;
    }

    public void setLimitedAccess(boolean limitedAccess) {
        this.limitedAccess = limitedAccess;
    }

    public String getProcureTeam() {
        return procureTeam;
    }

    public void setProcureTeam(String procureTeam) {
        this.procureTeam = procureTeam;
    }

    public String getDelFollowUpTeam() {
        return delFollowUpTeam;
    }

    public void setDelFollowUpTeam(String delFollowUpTeam) {
        this.delFollowUpTeam = delFollowUpTeam;
    }

    public String getInternalProcureTeam() {
        return internalProcureTeam;
    }

    public void setInternalProcureTeam(String internalProcureTeam) {
        this.internalProcureTeam = internalProcureTeam;
    }

    public String getWhSite() {
        return whSite;
    }

    public void setWhSite(String whSite) {
        this.whSite = whSite;
    }

    public String getTeamNames() {
        return teamNames;
    }

    public void setTeamNames(String teamNames) {
        this.teamNames = teamNames;
    }

    public String getUserName() {
        this.userName = "";
        if (!StringUtils.isEmpty(this.lastName)) {
            userName = this.lastName;
            userName = userName.concat(" ");
        }
        if (!StringUtils.isEmpty(this.firstName)) {
            userName = userName.concat(this.firstName);
        }
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserCategoryID() {
        return userCategoryID;
    }

    public void setUserCategoryID(String userCategoryID) {
        this.userCategoryID = userCategoryID;
    }

    public List<String> getCompanyGroupCodes() {
        return companyGroupCodes;
    }

    public void setCompanyGroupCodes(List<String> companyGroupCodes) {
        this.companyGroupCodes = companyGroupCodes;
    }

}
