package com.volvo.gloria.authorization.d.userrole;

import java.util.Set;

import com.volvo.gloria.authorization.d.entities.GloriaUser;
import com.volvo.gloria.common.b.CommonServices;

/**
 * WarehousePersonell class.
 */
public class WarehousePersonell extends UserRoleDefaultOperations {
    @Override
    public Set<String> getUserSiteSiteIdFilters(GloriaUser gloriaUser, CommonServices commonServices) {
        return UserRoleHelper.getWarehouseUserSiteSiteIds(gloriaUser);
    }
}
