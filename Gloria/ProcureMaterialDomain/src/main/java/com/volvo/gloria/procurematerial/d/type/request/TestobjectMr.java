package com.volvo.gloria.procurematerial.d.type.request;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurerequest.c.dto.RequestHeaderTransformerDTO;
import com.volvo.gloria.util.GloriaApplicationException;

/**
 * Operations for TESTOBJECT MR request type.
 */
public class TestobjectMr extends RequestTypeDefaultOperations {
    @Override
    public boolean isProtomRequest() {
        return true;
    }

    @Override
    public List<MaterialHeader> identifyHeaders(String referenceId, String mtrlRequestId, String outboundLocationId, String procureRequestId, String buildId,
            ProcurementServices procurementServices) {
        List<MaterialHeader> materialHeaders = procurementServices.findMaterialHeaderByReferenceIdAndBuildIdAndOutboundLocationId(
                                                                                                                                  referenceId, buildId,
                                                                                                                                  outboundLocationId);
        return materialHeaders;
    }
        
    @Override
    public void planOrUnplan(RequestHeaderTransformerDTO requestHeaderTransformerDTO, MaterialHeader materialHeader, String crId,
            MaterialHeaderRepository requestHeaderRepository, ProcurementServices procurementServices) throws GloriaApplicationException {
        boolean moveMaterials = false;
        if (RequestType.valueOf(requestHeaderTransformerDTO.getRequestType()).isProtomRequest() && !requestHeaderTransformerDTO.isExistsTestObjectRemove()) {
            if (requestHeaderTransformerDTO.isExistsPhaseRemove() && !requestHeaderTransformerDTO.isExistsPhase()) {
                moveMaterials = true;
                setNoBuild(materialHeader);
            }
            if (requestHeaderTransformerDTO.isExistsPhase() && !requestHeaderTransformerDTO.isExistsPhaseRemove()) {
                moveMaterials = true;
                RequestTypeHelper.setBuild(requestHeaderTransformerDTO, materialHeader);
            }            
            
            if (requestHeaderTransformerDTO.isExistsPhase() && requestHeaderTransformerDTO.isExistsPhaseRemove()) {
                moveMaterials = true;
                RequestTypeHelper.setBuild(requestHeaderTransformerDTO, materialHeader);
            }            
            
            if (crId != null && moveMaterials && !RequestTypeHelper.isContainingLines(requestHeaderTransformerDTO)) {
                RequestTypeHelper.moveMaterialsToCurrentHeader(materialHeader, crId, requestHeaderRepository, requestHeaderTransformerDTO, procurementServices);
            }
        }
    }



    @Override
    public void swapTestObject(RequestHeaderTransformerDTO requestHeaderTransformerDTO, MaterialHeader materialHeader, String crId,
            MaterialHeaderRepository requestHeaderRepository, ProcurementServices procurementServices) throws GloriaApplicationException {
        if (RequestType.valueOf(requestHeaderTransformerDTO.getRequestType()).isProtomRequest() && requestHeaderTransformerDTO.isExistsTestObjectRemove()) {
            String removeReferenceId = requestHeaderTransformerDTO.getRemoveReferenceId();
            if (StringUtils.isNotEmpty(removeReferenceId)) {
                setNoBuild(materialHeader);
                if (!RequestTypeHelper.isContainingLines(requestHeaderTransformerDTO)) {
                    RequestTypeHelper.moveMaterialsToCurrentHeader(materialHeader, crId, requestHeaderRepository, requestHeaderTransformerDTO, procurementServices);
                    materialHeader.setReferenceId(requestHeaderTransformerDTO.getReferenceId());
                }
            }
        }
    }

    private void setNoBuild(MaterialHeader materialHeader) {
        materialHeader.setBuildId(null);
        materialHeader.setBuildName(null);
        materialHeader.setBuildType(null);
        if (materialHeader.getAccepted() != null) {
            materialHeader.getAccepted().setOutboundStartDate(null);
        }
    }

}
