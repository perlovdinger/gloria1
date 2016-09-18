package com.volvo.gloria.procurematerial.d.type.request;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.common.repositories.b.CompanyCodeRepository;
import com.volvo.gloria.common.repositories.b.TeamRepository;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.util.GloriaApplicationException;


/**
 * Operations for MULTIPLE request type.
 */
public class Multiple extends RequestTypeDefaultOperations {
    
    @Override
    public void autoAssignToMC(MaterialHeader materialHeader, ProcurementServices procurementServices, UserServices userServices,
            TeamRepository teamRepository, CompanyCodeRepository companyCodeRepository) throws GloriaApplicationException {
        RequestTypeHelper.autoAssignToMC(materialHeader, procurementServices, userServices, teamRepository, companyCodeRepository);
    }     
}
