package com.volvo.gloria.procurematerial.d.type.request;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.d.entities.ChangeId;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurerequest.c.dto.RequestHeaderTransformerDTO;
import com.volvo.gloria.procurerequest.c.dto.RequestHeaderVersionTransformerDTO;
import com.volvo.gloria.util.GloriaApplicationException;

/**
 * Operations for TESTOBJECT request type.
 */
public class TestobjectFirstBuild extends RequestTypeDefaultOperations {
    @Override
    public boolean isProtomRequest() {
        return true;
    }
    
    @Override
    public List<MaterialHeader> identifyHeaders(String referenceId, String mtrlRequestId, String outboundLocationId, String procureRequestId, String buildId,
            ProcurementServices procurementServices) {

        // phase - referenceId
        return procurementServices.findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId(referenceId,
                                                                                                  buildId, outboundLocationId);
    }
    
    @Override
    public List<MaterialHeader> createOrUpdateHeaders(List<MaterialHeader> materialHeaders, RequestHeaderTransformerDTO requestHeaderTransformerDTO,
            String outboundLocationId, MaterialHeaderRepository requestHeaderRepository, ProcurementServices procurementServices, String changeIdCrId,RequestHeaderVersionTransformerDTO requestHeaderVersionTransformerDTO) throws GloriaApplicationException {
        List<MaterialHeader> newHeaders = new ArrayList<MaterialHeader>();
        if (materialHeaders == null || materialHeaders.isEmpty()) {
            // create new header if matching phase - referenceId doesnt exist
            newHeaders.add(procurementServices.createRequestHeader(requestHeaderTransformerDTO, outboundLocationId,
                                                                   requestHeaderRepository.getMaxFirstAssemblyIdSequence() + 1, changeIdCrId,requestHeaderVersionTransformerDTO));
        } else {
            // in available headers with matching phase - referenceId, check if matching otboundlocation exists
            newHeaders.addAll(procurementServices.findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId(materialHeaders.get(0).getReferenceId(),
                                                                                                                 materialHeaders.get(0).getBuildId(),
                                                                                                                 outboundLocationId));

            if (newHeaders == null || newHeaders.isEmpty()) {
                newHeaders.add(procurementServices.createRequestHeader(requestHeaderTransformerDTO, outboundLocationId,
                                                                       materialHeaders.get(0).getFirstAssemblyIdSequence(), changeIdCrId,requestHeaderVersionTransformerDTO));
            }
        }
        return newHeaders;
    }
    
    @Override
    public long getNextHeaderVersion(MaterialHeader materialHeader, ChangeId changeId, MaterialHeaderRepository requestHeaderRepository) {
        long headerVersion = requestHeaderRepository.getHeaderVersionsForFirstAssembly(materialHeader.getReferenceId(), materialHeader.getBuildId(), changeId);
        if (changeId == null || headerVersion == 0) {
            headerVersion = requestHeaderRepository.getHeaderVersionsForFirstAssembly(materialHeader.getReferenceId(), materialHeader.getBuildId(), null) + 1;
        }
        return headerVersion;
    }
    
    @Override
    public boolean isAlreadyAssigned(List<RequestHeaderTransformerDTO> requestHeaderTransformerDTOs, String procureRequestId, MaterialHeader materialHeader,
            ProcurementServices procurementServices) {
        boolean isAssigned = false;
        for (RequestHeaderTransformerDTO requestHeaderTransformerDTO : requestHeaderTransformerDTOs) {
            for (RequestHeaderVersionTransformerDTO requestHeaderVersionTransformerDTO : requestHeaderTransformerDTO.getRequestHeaderVersionTransformerDtos()) {
                String outboundLocationId = requestHeaderVersionTransformerDTO.getOutboundLocationId();
                List<MaterialHeader> materialHeaders = 
                        procurementServices.findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId(requestHeaderTransformerDTO.getReferenceId(),
                                                                                                           requestHeaderTransformerDTO.getBuildId(),
                                                                                                           null);
                if (materialHeaders != null) {
                    for (MaterialHeader header : materialHeaders) {
                        if (StringUtils.isNotEmpty(header.getMaterialControllerUserId())
                                && (StringUtils.isNotEmpty(outboundLocationId) && header.getAccepted() != null && outboundLocationId.equals(header.getAccepted()
                                                                                                                                                  .getOutboundLocationId()))) {
                            isAssigned = true;
                        }
                    }
                }
            }
        }
        return isAssigned;
    }

    @Override
    public String getReferenceId(RequestHeaderTransformerDTO requestHeaderTransformerDTO) {
        if (!StringUtils.isEmpty(requestHeaderTransformerDTO.getRemoveReferenceId())) {
            return requestHeaderTransformerDTO.getRemoveReferenceId();
        }
        return requestHeaderTransformerDTO.getReferenceId();
    }
}
