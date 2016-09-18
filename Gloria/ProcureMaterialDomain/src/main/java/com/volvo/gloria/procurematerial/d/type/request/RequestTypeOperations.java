package com.volvo.gloria.procurematerial.d.type.request;

import java.util.List;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.repositories.b.CompanyCodeRepository;
import com.volvo.gloria.common.repositories.b.TeamRepository;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.b.beans.ProcurementServicesBean;
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
 * Possible operations for request types.
 */
public interface RequestTypeOperations {
    ProcureType suggestProcureType(ProcureLine procureLine, boolean existsInStock) throws GloriaApplicationException;

    MaterialType getMaterialType();
    
    boolean isProtomRequest();

    List<MaterialHeader> identifyHeaders(String referenceId, String mtrlRequestId, String outboundLocationId, String procureRequestId, String buildId,
            ProcurementServices procurementServices);

    List<MaterialHeader> createOrUpdateHeaders(List<MaterialHeader> materialHeaders, RequestHeaderTransformerDTO requestHeaderTransformerDTO,
            String outboundLocationId, MaterialHeaderRepository requestHeaderRepository, ProcurementServices procurementServices, String changeIdCrId,
            RequestHeaderVersionTransformerDTO requestHeaderVersionTransformerDTO)
            throws GloriaApplicationException;

    long getNextHeaderVersion(MaterialHeader materialHeader, ChangeId changeId, MaterialHeaderRepository requestHeaderRepository);

    boolean isAlreadyAssigned(List<RequestHeaderTransformerDTO> requestHeaderTransformerDTOs, String procureRequestId, MaterialHeader materialHeader,
            ProcurementServices procurementServices);

    String getReferenceId(RequestHeaderTransformerDTO requestHeaderTransformerDTO);

    void planOrUnplan(RequestHeaderTransformerDTO requestHeaderTransformerDTO, MaterialHeader materialHeader, String crId,
            MaterialHeaderRepository requestHeaderRepository, ProcurementServices procurementServices) throws GloriaApplicationException;

    void setOutboundLocationInformation(RequestHeaderVersionTransformerDTO requestHeaderVersionTransformerDto, MaterialHeaderVersion requestHeaderVersion,
            CommonServices commonServices);
    void swapTestObject(RequestHeaderTransformerDTO requestHeaderTransformerDTO, MaterialHeader materialHeader, String crId,
            MaterialHeaderRepository requestHeaderRepository, ProcurementServices procurementServices) throws GloriaApplicationException;
    String getBuildId(RequestHeaderTransformerDTO requestHeaderTransformerDTO);

    void autoAssignToMC(MaterialHeader materialHeader, ProcurementServices procurementServices, UserServices userServices,
            TeamRepository teamRepository, CompanyCodeRepository companyCodeRepository) throws GloriaApplicationException;
}
