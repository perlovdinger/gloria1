package com.volvo.gloria.financeProxy.b;

import javax.xml.bind.JAXBException;

import com.volvo.gloria.financeProxy.c.ProcessPurchaseOrderDTO;


/**
 * 
 */
public interface ProcessPurchaseOrderSender {

String sendProcessPurchaseOrder(ProcessPurchaseOrderDTO processPurchaseOrderDTO) throws JAXBException;
}
