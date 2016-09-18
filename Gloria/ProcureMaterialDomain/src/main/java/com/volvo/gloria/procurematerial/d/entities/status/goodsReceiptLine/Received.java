package com.volvo.gloria.procurematerial.d.entities.status.goodsReceiptLine;

import com.volvo.gloria.procurematerial.d.entities.GoodsReceiptLine;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;

/**
 * Operations for Received.
 * 
 */
public class Received extends GoodsReceiptLineDefaultOperations {

    @Override
    public GoodsReceiptLine cancel(GoodsReceiptLine goodsReceiptLine, DeliveryNoteRepository deliveryNoteRepository) {
        return GoodsReceiptLineStatusHelper.cancelGoodsReceiptLine(goodsReceiptLine, deliveryNoteRepository);
    }   
}
