package com.volvo.gloria.procurematerial.d.type.request;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.d.entities.Site;
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
 * Default operations.
 */
public class RequestTypeDefaultOperations implements RequestTypeOperations {

    @Override
    public ProcureType suggestProcureType(ProcureLine procureLine, boolean existsInStock) throws GloriaApplicationException {
        ProcureType procureType = procureLine.getProcureType();
        if (procureType.equals(ProcureType.INTERNAL)
                && existsInStock) {
            return ProcureType.INTERNAL_FROM_STOCK;
        } else if (procureType.equals(ProcureType.EXTERNAL)
                && existsInStock) {
            return ProcureType.EXTERNAL_FROM_STOCK;
        } else {
            return procureType;
        }
    }

    @Override
    public MaterialType getMaterialType() {
        return MaterialType.USAGE;
    }

    @Override
    public boolean isProtomRequest() {
        return false;
    }
    
    @Override
    public List<MaterialHeader> identifyHeaders(String referenceId, String mtrlRequestId, String outboundLocationId, String procureRequestId, String buildId,
            ProcurementServices procurementServices) {
        return procurementServices.findMaterialHeaderByMtrlRequestId(mtrlRequestId, referenceId);
    }
    
    @Override
    public List<MaterialHeader> createOrUpdateHeaders(List<MaterialHeader> materialHeaders, RequestHeaderTransformerDTO requestHeaderTransformerDTO,
            String outboundLocationId, MaterialHeaderRepository requestHeaderRepository, ProcurementServices procurementServices, String changeIdCrId,
            RequestHeaderVersionTransformerDTO requestHeaderVersionTransformerDTO) throws GloriaApplicationException {
        if (materialHeaders == null || materialHeaders.isEmpty()) {
            List<MaterialHeader> newHeaders = new ArrayList<MaterialHeader>();
            newHeaders.add(procurementServices.createRequestHeader(requestHeaderTransformerDTO, outboundLocationId, null, changeIdCrId,requestHeaderVersionTransformerDTO));
            return newHeaders;
        }
        return materialHeaders;
    }
    
    @Override
    public long getNextHeaderVersion(MaterialHeader materialHeader, ChangeId changeId, MaterialHeaderRepository requestHeaderRepository) {
        MaterialHeaderVersion acceptedVersion = materialHeader.getAccepted();
        if (acceptedVersion != null) {
            List<MaterialHeaderVersion> materialHeaderVersions = materialHeader.getMaterialHeaderVersions();
            if (materialHeaderVersions != null && !materialHeaderVersions.isEmpty()) {
                return materialHeaderVersions.size() + 1;
            }
        }
        return 1;
    }
    
    @Override
    public boolean isAlreadyAssigned(List<RequestHeaderTransformerDTO> requestHeaderTransformerDTOs, String procureRequestId, MaterialHeader materialHeader,
            ProcurementServices procurementServices) {
        return !StringUtils.isEmpty(materialHeader.getMaterialControllerUserId());
    }
    
    @Override
    public String getReferenceId(RequestHeaderTransformerDTO requestHeaderTransformerDTO) {
        return requestHeaderTransformerDTO.getReferenceId();
    }
    
    @Override
    public void planOrUnplan(RequestHeaderTransformerDTO requestHeaderTransformerDTO, MaterialHeader materialHeader, String crId,
            MaterialHeaderRepository requestHeaderRepository, ProcurementServices procurementServices) throws GloriaApplicationException {
        RequestTypeHelper.setBuild(requestHeaderTransformerDTO, materialHeader);
    }
    
    @Override
    public void swapTestObject(RequestHeaderTransformerDTO requestHeaderTransformerDTO, MaterialHeader materialHeader, String crId,
            MaterialHeaderRepository requestHeaderRepository, ProcurementServices procurementServices) throws GloriaApplicationException {
    }
    
    @Override
    public void setOutboundLocationInformation(RequestHeaderVersionTransformerDTO requestHeaderVersionTransformerDto,
            MaterialHeaderVersion materialHeaderVersion, CommonServices commonServices) {
        String outboundLocationId = requestHeaderVersionTransformerDto.getOutboundLocationId();
        Site outboundLocationSite = commonServices.getSiteBySiteId(outboundLocationId);
        materialHeaderVersion.setOutboundLocationId(outboundLocationId);
        if (outboundLocationSite != null) {
            materialHeaderVersion.setOutboundLocationName(outboundLocationSite.getSiteName());
            materialHeaderVersion.setOutboundLocationType(outboundLocationSite.getBuildSiteType());
        }
    }

    @Override
    public String getBuildId(RequestHeaderTransformerDTO requestHeaderTransformerDTO) {
        return requestHeaderTransformerDTO.getBuildId();
    }

    @Override
    public void autoAssignToMC(MaterialHeader materialHeader, ProcurementServices procurementServices, UserServices userServices,
            TeamRepository teamRepository, CompanyCodeRepository companyCodeRepository) throws GloriaApplicationException {
        // do nothing
    }
}
