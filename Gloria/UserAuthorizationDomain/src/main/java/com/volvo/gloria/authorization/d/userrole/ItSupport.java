package com.volvo.gloria.authorization.d.userrole;

import java.util.HashSet;
import java.util.Set;

import com.volvo.gloria.authorization.d.entities.GloriaUser;
import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.d.entities.CompanyCode;

/**
 * ItSupport class.
 */
public class ItSupport extends UserRoleDefaultOperations {
    @Override
    public Set<String> getUserCompanyCodeCodeFilters(GloriaUser gloriaUser, CommonServices commonServices) {
        Set<String> companyCodeCodes = new HashSet<String>();
        for (CompanyCode companyCode : commonServices.findAllCompanyCodes()) {
            companyCodeCodes.add(companyCode.getCode());
        }
        return companyCodeCodes;
    }
    
    @Override
    public boolean isItSupport() {
        return true;
    }
}
