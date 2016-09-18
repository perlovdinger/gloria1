package com.volvo.gloria.materialRequestProxy.b;

import javax.xml.bind.JAXBException;

import com.volvo.gloria.materialRequestProxy.c.MaterialRequestToSendDTO;

/**
 * Sender interface for material request cancellation.
 */
public interface MaterialRequestCancelSender {

    String sendCancelMaterialRequest(MaterialRequestToSendDTO materialRequestToSendDTO) throws JAXBException;

}
