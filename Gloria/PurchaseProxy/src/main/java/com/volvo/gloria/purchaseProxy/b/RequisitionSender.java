package com.volvo.gloria.purchaseProxy.b;

import javax.xml.bind.JAXBException;

import com.volvo.gloria.purchaseProxy.c.RequisitionDTO;

/**
 * Interface for RequistionSender.
 */
public interface RequisitionSender {
    String sendRequsition(RequisitionDTO requisitionDto) throws JAXBException;
}
