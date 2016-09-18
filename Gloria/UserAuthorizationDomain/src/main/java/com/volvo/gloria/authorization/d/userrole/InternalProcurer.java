package com.volvo.gloria.authorization.d.userrole;

import java.util.Set;

import com.volvo.gloria.authorization.d.entities.GloriaUser;
import com.volvo.gloria.common.b.CommonServices;

/**
 * InternalProcurer class.
 */
public class InternalProcurer extends UserRoleDefaultOperations {
    @Override
    public Set<String> getUserCompanyCodeCodeFilters(GloriaUser gloriaUser, CommonServices commonServices) {
        return UserRoleHelper.getUserTeamCompanyCodeCodes(gloriaUser, commonServices);
    }
}
