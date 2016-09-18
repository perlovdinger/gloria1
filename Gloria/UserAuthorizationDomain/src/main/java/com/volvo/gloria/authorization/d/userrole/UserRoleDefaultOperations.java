package com.volvo.gloria.authorization.d.userrole;

import java.util.HashSet;
import java.util.Set;

import com.volvo.gloria.authorization.d.entities.GloriaUser;
import com.volvo.gloria.common.b.CommonServices;

public class UserRoleDefaultOperations implements UserRoleOperations{

    @Override
    public Set<String> getUserCompanyCodeCodeFilters(GloriaUser gloriaUser, CommonServices commonServices) {
        return new HashSet<String>();
    }

    @Override
    public Set<String> getUserSiteSiteIdFilters(GloriaUser gloriaUser, CommonServices commonServices) {
        Set<String> userTeamCompanyCodeCodes = getUserCompanyCodeCodeFilters(gloriaUser, commonServices);
        return UserRoleHelper.getSiteSiteIdsForCompanyCodeCodes(commonServices, userTeamCompanyCodeCodes);
    }
    
    @Override
    public boolean isItSupport() {
        return false;
    }
}
