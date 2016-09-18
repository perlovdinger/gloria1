package com.volvo.gloria.procurematerial.d.entities.status.goodsReceiptLine;

import com.volvo.gloria.procurematerial.d.entities.GoodsReceiptLine;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;
import com.volvo.gloria.util.GloriaApplicationException;

/**
 * default operations on behavioural classes supporting statuses.
 * 
 */
public class GoodsReceiptLineDefaultOperations implements GoodsReceiptLineOperations {

    public static final String EXCEPTION_MESSAGE = "This operation is not supported in current state.";
    
    @Override
    public GoodsReceiptLine cancel(GoodsReceiptLine goodsReceiptLine, DeliveryNoteRepository deliveryNoteRepository)
            throws GloriaApplicationException {
        throw new GloriaApplicationException(null, EXCEPTION_MESSAGE + this.getClass().getName());      
    }

}
