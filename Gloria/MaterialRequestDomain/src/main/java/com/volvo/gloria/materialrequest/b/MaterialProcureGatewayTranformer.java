package com.volvo.gloria.materialrequest.b;

import com.volvo.gloria.materialRequestProxy.c.MaterialProcureResponseDTO;

/**
 * 
 */
public interface MaterialProcureGatewayTranformer {

    MaterialProcureResponseDTO transformMaterialProcureResponse(String receivedMaterialProcureResponse);
}
