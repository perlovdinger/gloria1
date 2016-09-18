package com.volvo.gloria.authorization.d.userrole;

import java.util.Set;

import com.volvo.gloria.authorization.d.entities.GloriaUser;
import com.volvo.gloria.common.b.CommonServices;

/**
 * UserRole operations. 
 */
public interface UserRoleOperations {
    Set<String> getUserCompanyCodeCodeFilters(GloriaUser gloriaUser, CommonServices commonServices);

    Set<String> getUserSiteSiteIdFilters(GloriaUser gloriaUser, CommonServices commonServices);

    boolean isItSupport();
}
