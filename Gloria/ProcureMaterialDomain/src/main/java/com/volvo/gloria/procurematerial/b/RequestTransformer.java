package com.volvo.gloria.procurematerial.b;

import java.util.List;

import com.volvo.gloria.procurerequest.c.dto.RequestGatewayDTO;


/**
 *  Service interface for procure request message transformer.
 */
public interface RequestTransformer {
    /**
     * @param requestMessage
     * @return
     */
    List<RequestGatewayDTO> transformRequest(String requestMessage);
}
