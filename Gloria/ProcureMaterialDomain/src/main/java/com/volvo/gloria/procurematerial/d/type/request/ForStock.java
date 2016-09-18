package com.volvo.gloria.procurematerial.d.type.request;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.common.repositories.b.CompanyCodeRepository;
import com.volvo.gloria.common.repositories.b.TeamRepository;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.procurematerial.d.type.procure.ProcureType;
import com.volvo.gloria.util.GloriaApplicationException;

/**
 * Operations for FOR_STOCK request type.
 */
public class ForStock extends RequestTypeDefaultOperations {
    @Override
    public ProcureType suggestProcureType(ProcureLine procureLine, boolean existsInStock) throws GloriaApplicationException {
        return procureLine.getProcureType().suggestProcureTypeForStock(existsInStock);
    }
    
    @Override
    public MaterialType getMaterialType() {
        return MaterialType.RELEASED;
    }
    
    @Override
    public void autoAssignToMC(MaterialHeader materialHeader, ProcurementServices procurementServices, UserServices userServices,
            TeamRepository teamRepository, CompanyCodeRepository companyCodeRepository) throws GloriaApplicationException {
        RequestTypeHelper.autoAssignToMC(materialHeader, procurementServices, userServices, teamRepository, companyCodeRepository);
    }  
}
