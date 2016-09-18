package com.volvo.gloria.procurematerial.d.type.request;

import java.util.List;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.common.repositories.b.CompanyCodeRepository;
import com.volvo.gloria.common.repositories.b.TeamRepository;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.util.GloriaApplicationException;

/**
 * Operations for SINGLE request type.
 */
public class Single extends RequestTypeDefaultOperations {

    @Override
    public List<MaterialHeader> identifyHeaders(String referenceId, String mtrlRequestId, String outboundLocationId, String procureRequestId, String buildId,
            ProcurementServices procurementServices) {
        return procurementServices.findMaterialHeaderByMtrlRequestId(mtrlRequestId, null);
    }
    
    @Override
    public void autoAssignToMC(MaterialHeader materialHeader, ProcurementServices procurementServices, UserServices userServices,
            TeamRepository teamRepository, CompanyCodeRepository companyCodeRepository) throws GloriaApplicationException {
        RequestTypeHelper.autoAssignToMC(materialHeader, procurementServices, userServices, teamRepository, companyCodeRepository);
    }    
}
