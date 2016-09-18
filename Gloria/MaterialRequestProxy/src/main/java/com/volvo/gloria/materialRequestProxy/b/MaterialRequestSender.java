package com.volvo.gloria.materialRequestProxy.b;

import javax.xml.bind.JAXBException;

import com.volvo.gloria.materialRequestProxy.c.MaterialRequestToSendDTO;
import com.volvo.gloria.util.GloriaApplicationException;

/**
 * interface for MaterialRequestSender.
 */
public interface MaterialRequestSender {

    String sendMaterialRequestSender(MaterialRequestToSendDTO materialRequestToSendDTO) throws JAXBException, GloriaApplicationException;
}
