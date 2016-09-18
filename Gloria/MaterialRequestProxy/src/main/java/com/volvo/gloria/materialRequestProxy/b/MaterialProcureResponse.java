package com.volvo.gloria.materialRequestProxy.b;

import javax.xml.bind.JAXBException;

/**
 * 
 */
public interface MaterialProcureResponse {

     /**
     * @param changeId
     * @param userId
     * @param response
     * @param changeTechId 
     * @param mtrlRequestId 
     * @return String 
     * @throws JAXBException
     */
    String  sendMaterialProcureResponse(String changeId, String userId, String response, String changeTechId, String mtrlRequestId) throws JAXBException;
}
