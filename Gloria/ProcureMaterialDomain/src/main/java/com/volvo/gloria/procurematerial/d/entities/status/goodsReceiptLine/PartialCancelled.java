package com.volvo.gloria.procurematerial.d.entities.status.goodsReceiptLine;

import com.volvo.gloria.procurematerial.d.entities.GoodsReceiptLine;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;
import com.volvo.gloria.util.GloriaApplicationException;

/**
 * operations for PARTIAL_CANCELLED.
 * 
 */
public class PartialCancelled extends GoodsReceiptLineDefaultOperations {
    @Override
    public GoodsReceiptLine cancel(GoodsReceiptLine goodsReceiptLine, DeliveryNoteRepository deliveryNoteRepository)
            throws GloriaApplicationException {
        return GoodsReceiptLineStatusHelper.cancelGoodsReceiptLine(goodsReceiptLine, deliveryNoteRepository);
    }
}
