package com.volvo.gloria.authorization.d.userrole;

import java.util.HashSet;
import java.util.Set;

import com.volvo.gloria.authorization.d.entities.GloriaUser;
import com.volvo.gloria.common.b.CommonServices;

/**
 * Viewer class.
 */
public class Viewer extends UserRoleDefaultOperations {
    @Override
    public Set<String> getUserCompanyCodeCodeFilters(GloriaUser gloriaUser, CommonServices commonServices) {
        return new HashSet<String>(UserRoleHelper.getUserCompanyCodeCodes(gloriaUser, commonServices));
    }
}
