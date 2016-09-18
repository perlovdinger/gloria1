package com.volvo.gloria.procurematerial.util.migration.b.beans;

import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.xml.bind.JAXBException;

import com.volvo.gloria.financeProxy.b.ProcessPurchaseOrderSender;
import com.volvo.gloria.financeProxy.c.ProcessPurchaseOrderDTO;
import com.volvo.gloria.procurematerial.d.entities.MessageStatus;
import com.volvo.gloria.procurematerial.d.entities.OrderSap;
import com.volvo.gloria.procurematerial.d.entities.OrderSapLine;
import com.volvo.gloria.procurematerial.repositories.b.OrderSapRepository;
import com.volvo.gloria.procurematerial.util.MaterialTransformHelper;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.c.SAPParam;
import com.volvo.jvs.runtime.platform.ContainerManaged;

@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class PerformSendPOForMigratedOrdersService {
    @Inject
    private OrderSapRepository orderSapRepo;

    @Inject
    private ProcessPurchaseOrderSender processPurchaseOrderSender;

    public StringBuilder performSend(List<OrderSap> orderSapList) throws GloriaApplicationException {
        StringBuilder statusText = new StringBuilder();
        for (OrderSap orderSap : orderSapList) {
            if (orderSap.getMessageStatus() == MessageStatus.WAIT_FOR_SEND) {
                List<OrderSapLine> orderSapLines = orderSap.getOrderSapLines();
                // TODO check action?
                ProcessPurchaseOrderDTO processPurchaseOrderDTO = MaterialTransformHelper.prepareProcessPurchaseOrderDTO(orderSapLines,
                                                                                                                         SAPParam.ACTION_CREATE); 
                try {
                    processPurchaseOrderSender.sendProcessPurchaseOrder(processPurchaseOrderDTO);
                    // update status: isSent.
                    orderSap.setMessageStatus(MessageStatus.SENT);
                    orderSapRepo.save(orderSap);
                } catch (JAXBException e) {
                    statusText.append("ERROR : ").append(orderSap.getOrderSapOID()).append(" : process purchase order couldn't be sent.\n")
                              .append(e.getMessage());
                }
            }
        }
        return statusText;
    }

}
