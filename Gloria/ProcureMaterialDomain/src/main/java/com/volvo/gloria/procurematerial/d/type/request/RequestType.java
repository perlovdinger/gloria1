package com.volvo.gloria.procurematerial.d.type.request;

import java.util.List;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.repositories.b.CompanyCodeRepository;
import com.volvo.gloria.common.repositories.b.TeamRepository;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.d.entities.ChangeId;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeaderVersion;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.procurematerial.d.type.procure.ProcureType;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurerequest.c.dto.RequestHeaderTransformerDTO;
import com.volvo.gloria.procurerequest.c.dto.RequestHeaderVersionTransformerDTO;
import com.volvo.gloria.util.GloriaApplicationException;

/**
 * RequestType possible types, supporting operations in behaviour classes.
 */
public enum RequestType implements RequestTypeOperations {

    TESTOBJECT_FIRST_BUILD(new TestobjectFirstBuild()), 
    TESTOBJECT_MR(new TestobjectMr()), 
    SINGLE(new Single()),  
    MULTIPLE(new Multiple()), 
    FOR_STOCK(new ForStock()), 
    ADDITIONAL_USAGE(new AdditionalUsage());

    private final RequestTypeOperations requestTypeOperations;

    RequestType(RequestTypeOperations requestTypeOperations) {
        this.requestTypeOperations = requestTypeOperations;
    }

    @Override
    public ProcureType suggestProcureType(ProcureLine procureLine, boolean existsInStock) throws GloriaApplicationException {
        return requestTypeOperations.suggestProcureType(procureLine, existsInStock);
    }

    @Override
    public MaterialType getMaterialType() {
        return requestTypeOperations.getMaterialType();
    }

    @Override
    public boolean isProtomRequest() {
        return requestTypeOperations.isProtomRequest();
    }

    @Override
    public List<MaterialHeader> identifyHeaders(String referenceId, String mtrlRequestId, String outboundLocationId, String procureRequestId, String buildId,
            ProcurementServices procurementServices) {
        return requestTypeOperations.identifyHeaders(referenceId, mtrlRequestId, outboundLocationId, procureRequestId, buildId, procurementServices);
    }

    @Override
    public List<MaterialHeader> createOrUpdateHeaders(List<MaterialHeader> materialHeaders, RequestHeaderTransformerDTO requestHeaderTransformerDTO,
            String outboundLocationId, MaterialHeaderRepository requestHeaderRepository, ProcurementServices procurementServices, String changeIdCrId,
            RequestHeaderVersionTransformerDTO requestHeaderVersionTransformerDTO)
            throws GloriaApplicationException {
        return requestTypeOperations.createOrUpdateHeaders(materialHeaders, requestHeaderTransformerDTO, outboundLocationId, requestHeaderRepository,
                                                           procurementServices, changeIdCrId,requestHeaderVersionTransformerDTO);
    }

    @Override
    public long getNextHeaderVersion(MaterialHeader materialHeader, ChangeId changeId, MaterialHeaderRepository requestHeaderRepository) {
        return requestTypeOperations.getNextHeaderVersion(materialHeader, changeId, requestHeaderRepository);
    }
    
    @Override
    public boolean isAlreadyAssigned(List<RequestHeaderTransformerDTO> requestHeaderTransformerDTOs, String procureRequestId, MaterialHeader materialHeader,
            ProcurementServices procurementServices) {
        return requestTypeOperations.isAlreadyAssigned(requestHeaderTransformerDTOs, procureRequestId, materialHeader, procurementServices);
    }
    
    @Override
    public String getReferenceId(RequestHeaderTransformerDTO requestHeaderTransformerDTO) {
        return requestTypeOperations.getReferenceId(requestHeaderTransformerDTO);
    }
 
    @Override
    public void planOrUnplan(RequestHeaderTransformerDTO requestHeaderTransformerDTO, MaterialHeader materialHeader, String crId,
            MaterialHeaderRepository requestHeaderRepository, ProcurementServices procurementServices) throws GloriaApplicationException {
        requestTypeOperations.planOrUnplan(requestHeaderTransformerDTO, materialHeader, crId, requestHeaderRepository, procurementServices);
    }

    @Override
    public void swapTestObject(RequestHeaderTransformerDTO requestHeaderTransformerDTO, MaterialHeader materialHeader, String crId,
            MaterialHeaderRepository requestHeaderRepository, ProcurementServices procurementServices) throws GloriaApplicationException {
        requestTypeOperations.swapTestObject(requestHeaderTransformerDTO, materialHeader, crId, requestHeaderRepository, procurementServices);
    }

    @Override
    public void setOutboundLocationInformation(RequestHeaderVersionTransformerDTO requestHeaderVersionTransformerDto,
            MaterialHeaderVersion requestHeaderVersion, CommonServices commonServices) {
        requestTypeOperations.setOutboundLocationInformation(requestHeaderVersionTransformerDto, requestHeaderVersion, commonServices);
    }

    @Override
    public String getBuildId(RequestHeaderTransformerDTO requestHeaderTransformerDTO) {
        return requestTypeOperations.getBuildId(requestHeaderTransformerDTO);
    }

    public void autoAssignToMC(MaterialHeader materialHeader, ProcurementServices procurementServices, UserServices userServices,
            TeamRepository teamRepository, CompanyCodeRepository companyCodeRepository) throws GloriaApplicationException {
        requestTypeOperations.autoAssignToMC(materialHeader, procurementServices, userServices, teamRepository, companyCodeRepository);
    }
}
