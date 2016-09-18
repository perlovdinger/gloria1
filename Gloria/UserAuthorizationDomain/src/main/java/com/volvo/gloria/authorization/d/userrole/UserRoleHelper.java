package com.volvo.gloria.authorization.d.userrole;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.volvo.gloria.authorization.d.entities.ApplicationSetting;
import com.volvo.gloria.authorization.d.entities.GloriaUser;
import com.volvo.gloria.authorization.d.entities.Team;
import com.volvo.gloria.authorization.d.entities.UserApplication;
import com.volvo.gloria.authorization.d.entities.UserCategory;
import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.d.entities.CompanyCode;
import com.volvo.gloria.common.d.entities.CompanyGroup;

/**
 * Helper class for OrderLine status.
 * 
 */
public final class UserRoleHelper {

    private static final String WHSITE = "WHSITE";

    private UserRoleHelper() {
    }

    public static Set<String> getAllUserCompanyCodeCodes(GloriaUser gloriaUser, CommonServices commonServices) {
        Set<String> allUserCompanyCodeCodes = new HashSet<String>();
        for (UserCategory userCategory : getAllUserCategories(gloriaUser, commonServices)) {
            allUserCompanyCodeCodes.addAll(UserRole.valueOf(userCategory.getUserCategoryID()).getUserCompanyCodeCodeFilters(gloriaUser, commonServices));
        }
        return allUserCompanyCodeCodes;
    }

    public static Set<String> getAllUserSites(GloriaUser gloriaUser, CommonServices commonServices) {
        Set<String> allUserSites = new HashSet<String>();
        for (UserCategory userCategory : getAllUserCategories(gloriaUser, commonServices)) {
            allUserSites.addAll(UserRole.valueOf(userCategory.getUserCategoryID()).getUserSiteSiteIdFilters(gloriaUser, commonServices));
        }
        return allUserSites;
    }

    public static boolean isItSupport(GloriaUser gloriaUser, CommonServices commonServices) {
        for (UserCategory userCategory : getAllUserCategories(gloriaUser, commonServices)) {
            if (UserRole.valueOf(userCategory.getUserCategoryID()).isItSupport()) {
                return true;
            }
        }
        return false;
    }

    public static List<UserCategory> getAllUserCategories(GloriaUser gloriaUser, CommonServices commonServices) {
        List<UserCategory> allUserCategories = new ArrayList<UserCategory>();
        if (gloriaUser != null) {
            List<UserApplication> userApplications = gloriaUser.getUserApplication();
            for (UserApplication userApplication : userApplications) {
                allUserCategories.addAll(userApplication.getUserCategory());
            }
        }
        return allUserCategories;
    }

    public static Set<String> getWarehouseUserSiteSiteIds(GloriaUser gloriaUser) {
        Set<String> warehouseUserSiteSiteIds = new HashSet<String>();
        List<UserApplication> userApplications = gloriaUser.getUserApplication();
        for (UserApplication userApplication : userApplications) {
            List<ApplicationSetting> applicationSettings = userApplication.getApplicationSetting();
            for (ApplicationSetting applicationSetting : applicationSettings) {
                if (applicationSetting.getApplicationSettingID().equalsIgnoreCase(WHSITE)) {
                    warehouseUserSiteSiteIds.add(applicationSetting.getApplicationSettingValue());
                }
            }
        }
        return warehouseUserSiteSiteIds;
    }

    public static List<String> getUserCompanyCodeCodes(GloriaUser gloriaUser, CommonServices commonServices) {
        List<String> companyGroupCodes = new ArrayList<String>();
        List<UserApplication> userApplications = gloriaUser.getUserApplication();
        for (UserApplication userApplication : userApplications) {
            List<ApplicationSetting> applicationSettings = userApplication.getApplicationSetting();
            for (ApplicationSetting applicationSetting : applicationSettings) {
                if (applicationSetting.getApplicationSettingID().equalsIgnoreCase("CCGRP")) {
                    companyGroupCodes.add(applicationSetting.getApplicationSettingValue());
                }
            }
        }
        return commonServices.getCompanyCodeCodes(companyGroupCodes);
    }

    public static Set<String> getUserTeamCompanyCodeCodes(GloriaUser gloriaUser, CommonServices commonServices) {
        Set<String> companyCodeCodes = new HashSet<String>();
        for (Team team : gloriaUser.getTeams()) {
            CompanyGroup companyGroup = commonServices.getCompanyGroupByCode(team.getCompanyCodeGroup());
            if (companyGroup != null) {
                for (CompanyCode companyCode : companyGroup.getCompanyCodes()) {
                    companyCodeCodes.add(companyCode.getCode());
                }
            }
        }
        return companyCodeCodes;
    }

    public static Set<String> getSiteSiteIdsForCompanyCodeCodes(CommonServices commonServices, Set<String> userTeamCompanyCodeCodes) {
        List<String> siteIds = commonServices.getSiteIdsByCompanyCodes(userTeamCompanyCodeCodes);
        if (siteIds != null) {
            return new HashSet<String>(siteIds);
        } else {
            return new HashSet<String>();
        }
    }

}
