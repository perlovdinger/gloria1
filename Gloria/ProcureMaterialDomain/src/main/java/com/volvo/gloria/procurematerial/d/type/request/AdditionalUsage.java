package com.volvo.gloria.procurematerial.d.type.request;

import java.util.List;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeaderVersion;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.procurerequest.c.dto.RequestHeaderTransformerDTO;
import com.volvo.gloria.procurerequest.c.dto.RequestHeaderVersionTransformerDTO;

/**
 * Operations for ADDITIONAL_USAGE request type.
 */
public class AdditionalUsage extends RequestTypeDefaultOperations {
    
    @Override
    public boolean isProtomRequest() {
        return true;
    }

    @Override
    public List<MaterialHeader> identifyHeaders(String referenceId, String mtrlRequestId, String outboundLocationId, String procureRequestId, String buildId,
            ProcurementServices procurementServices) {
        return procurementServices.findMaterialHeaderByMtrlRequestId(mtrlRequestId, referenceId);
    }
    
    @Override
    public boolean isAlreadyAssigned(List<RequestHeaderTransformerDTO> requestHeaderTransformerDTOs, String procureRequestId, MaterialHeader materialHeader,
            ProcurementServices procurementServices) {
        return false;
    }
    
    @Override
    public MaterialType getMaterialType() {
        return MaterialType.ADDITIONAL_USAGE;
    }
       
    @Override
    public void setOutboundLocationInformation(RequestHeaderVersionTransformerDTO requestHeaderVersionTransformerDto,
            MaterialHeaderVersion materialHeaderVersion, CommonServices commonServices) {
        materialHeaderVersion.setOutboundLocationId(null);
        materialHeaderVersion.setOutboundLocationName(null);
        materialHeaderVersion.setOutboundLocationType(null);
    }
}
