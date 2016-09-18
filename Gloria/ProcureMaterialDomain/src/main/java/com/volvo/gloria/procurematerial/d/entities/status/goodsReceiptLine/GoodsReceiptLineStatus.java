package com.volvo.gloria.procurematerial.d.entities.status.goodsReceiptLine;

import com.volvo.gloria.procurematerial.d.entities.GoodsReceiptLine;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;
import com.volvo.gloria.util.GloriaApplicationException;

/**
 * GoodsReceiptLineStatus possible statuses supporting operations in behaviour classes..
 */
public enum GoodsReceiptLineStatus implements GoodsReceiptLineOperations {

    RECEIVED(new Received()), 
    PARTIAL_CANCELLED(new PartialCancelled()), 
    CANCELLED(new Cancelled());

    private final GoodsReceiptLineOperations goodsReceiptLineOperations;

    GoodsReceiptLineStatus(GoodsReceiptLineOperations goodsReceiptLineOperations) {
        this.goodsReceiptLineOperations = goodsReceiptLineOperations;
    }

    @Override
    public GoodsReceiptLine cancel(GoodsReceiptLine goodsReceiptLine, DeliveryNoteRepository deliveryNoteRepository) throws GloriaApplicationException {
        return goodsReceiptLineOperations.cancel(goodsReceiptLine, deliveryNoteRepository);
    }

}
