package com.volvo.gloria.procurematerial.util.migration.b.beans;

import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import com.volvo.gloria.financeProxy.b.GoodsReceiptSender;
import com.volvo.gloria.financeProxy.c.GoodsReceiptHeaderTransformerDTO;
import com.volvo.gloria.procurematerial.d.entities.GoodsReceiptHeader;
import com.volvo.gloria.procurematerial.d.entities.MessageStatus;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;
import com.volvo.gloria.procurematerial.util.MaterialTransformHelper;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.jvs.runtime.platform.ContainerManaged;

@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class PerformSendGMForMigratedOrdersService {
    @Inject
    private DeliveryNoteRepository deliveryNoteRepo;

    @Inject
    private GoodsReceiptSender goodsReceiptSender;

    public StringBuilder performSend(List<GoodsReceiptHeader> goodsReceiptHeaders) throws GloriaApplicationException {
        StringBuilder statusText = new StringBuilder();
        for (GoodsReceiptHeader goodsReceiptHeader : goodsReceiptHeaders) {
            if (goodsReceiptHeader.getMessageStatus() == MessageStatus.WAIT_FOR_SEND) {
                GoodsReceiptHeaderTransformerDTO goodsReceiptHeaderTransformerDTO = MaterialTransformHelper.transformAsDTOForProxy(goodsReceiptHeader);
                try {
                    goodsReceiptSender.sendGoodsReceipt(goodsReceiptHeaderTransformerDTO);
                    // update status: isSent.
                    goodsReceiptHeader.setMessageStatus(MessageStatus.SENT);                    
                    deliveryNoteRepo.save(goodsReceiptHeader);
                } catch (Exception e) {
                    statusText.append("ERROR : ").append(goodsReceiptHeader.getGoodReceiptHeaderOId()).append(" : Goods Receipt Movement couldn't be sent.\n")
                              .append(e.getMessage());
                }
            }
        }
        return statusText;
    }

}
