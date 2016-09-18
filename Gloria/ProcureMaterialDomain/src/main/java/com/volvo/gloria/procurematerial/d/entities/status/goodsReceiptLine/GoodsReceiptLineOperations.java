package com.volvo.gloria.procurematerial.d.entities.status.goodsReceiptLine;

import com.volvo.gloria.procurematerial.d.entities.GoodsReceiptLine;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;
import com.volvo.gloria.util.GloriaApplicationException;

/**
 * operations on behavioural classes supporting statuses.
 * 
 */
public interface GoodsReceiptLineOperations {

    GoodsReceiptLine cancel(GoodsReceiptLine goodsReceiptLine, DeliveryNoteRepository deliveryNoteRepository)
            throws GloriaApplicationException;

}
