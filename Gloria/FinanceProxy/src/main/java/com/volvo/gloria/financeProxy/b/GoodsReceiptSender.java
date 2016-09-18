package com.volvo.gloria.financeProxy.b;

import javax.xml.bind.JAXBException;

import com.volvo.gloria.financeProxy.c.GoodsReceiptHeaderTransformerDTO;

/**
 * Interface for GoodsReceiptSender.
 */
public interface GoodsReceiptSender {

    String sendGoodsReceipt(GoodsReceiptHeaderTransformerDTO goodsReceiptHeaderDTO) throws JAXBException;

}
