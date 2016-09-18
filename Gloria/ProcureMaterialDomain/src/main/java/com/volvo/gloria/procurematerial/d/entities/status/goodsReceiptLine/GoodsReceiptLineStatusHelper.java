package com.volvo.gloria.procurematerial.d.entities.status.goodsReceiptLine;

import com.volvo.gloria.procurematerial.d.entities.GoodsReceiptLine;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;

/**
 * helper class for MaterialLineStatus.
 * 
 */
public final class GoodsReceiptLineStatusHelper {

    private GoodsReceiptLineStatusHelper() {
        
    }

    public static GoodsReceiptLine cancelGoodsReceiptLine(GoodsReceiptLine goodsReceiptLine, DeliveryNoteRepository deliveryNoteRepository) {
        GoodsReceiptLineStatus status = GoodsReceiptLineStatus.PARTIAL_CANCELLED;
        if (goodsReceiptLine.getQuantity() <= 0) {
            status = GoodsReceiptLineStatus.CANCELLED;
        }
        goodsReceiptLine.setStatus(status);
        return deliveryNoteRepository.save(goodsReceiptLine);
    }
}
