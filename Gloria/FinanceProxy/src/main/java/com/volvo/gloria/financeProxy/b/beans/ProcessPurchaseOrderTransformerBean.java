package com.volvo.gloria.financeProxy.b.beans;

import static com.volvo.gloria.util.c.GloriaParams.APPLICATION_ID;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;

import com.volvo.gloria.financeProxy.b.ProcessPurchaseOrderTransformer;
import com.volvo.gloria.financeProxy.c.OrderSapAccountsDTO;
import com.volvo.gloria.financeProxy.c.OrderSapLineDTO;
import com.volvo.gloria.financeProxy.c.OrderSapScheduleDTO;
import com.volvo.gloria.financeProxy.c.ProcessPurchaseOrderDTO;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.Utils;
import com.volvo.gloria.util.c.SAPParam;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.gloria.util.xml.XmlTransformer;
import com.volvo.group.finance.common._1_0.MessageHeaderType;
import com.volvo.group.finance.common._1_0.MessageHeaderType.Receiver;
import com.volvo.group.finance.common._1_0.MessageHeaderType.Sender;
import com.volvo.group.finance.common._1_0.ProcessType;
import com.volvo.group.processpurchaseorder._1_0.ProcessPurchaseOrder;
import com.volvo.group.processpurchaseorder._1_0.PurchaseOrderType;
import com.volvo.group.processpurchaseorder._1_0.PurchaseOrderType.POHeaderData;
import com.volvo.group.processpurchaseorder._1_0.PurchaseOrderType.POHeaderData.PoItemData;
import com.volvo.group.processpurchaseorder._1_0.PurchaseOrderType.POHeaderData.PoItemData.POAccountAssignment;
import com.volvo.group.processpurchaseorder._1_0.PurchaseOrderType.POHeaderData.PoItemData.POScheduleLine;
import com.volvo.jvs.runtime.platform.ContainerManaged;

@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ProcessPurchaseOrderTransformerBean extends XmlTransformer implements ProcessPurchaseOrderTransformer {

    private static final int NUMBER_2 = 2;
    private static final int NUMBER_1 = 1;
    private static final int MAX_DECIMAL_LIMIT_PRICEUNIT = 4;
    private static final int THREE = 3;
    private static final int TEN = 10;
    private static final String MSG_VERSION = "1.0.0";
    private static final String RECEIVER_LOGICALID = "SAP";
    

    public ProcessPurchaseOrderTransformerBean() {
        super(XmlConstants.SchemaFullPath.PROCESS_PURCHASE_ORDER, XmlConstants.PackageName.PROCESS_PURCHASE_ORDER);
    }

    @Override
    protected Object transformFromJAXBToDTO(Object jaxbOject) {
        return null;
    }

    @Override
    protected ProcessPurchaseOrder transformFromDTOToJAXB(Object dto) {
        ProcessPurchaseOrderDTO orderSapDTO = (ProcessPurchaseOrderDTO) dto;

        ProcessPurchaseOrder processPurchaseOrder = new ProcessPurchaseOrder();
        ProcessType processType = new ProcessType();
        // Setters
        processType.setAction(orderSapDTO.getAction());
        MessageHeaderType messageHeaderType = new MessageHeaderType();
        messageHeaderType.setMsgVersion(MSG_VERSION);
        messageHeaderType.setCreationDateTime(evalCurrentDateTime());

        Receiver receiver = new Receiver();
        receiver.setLogicalId(RECEIVER_LOGICALID);
        messageHeaderType.setReceiver(receiver);

        Sender sender = new Sender();
        sender.setLogicalId(APPLICATION_ID);
        messageHeaderType.setSender(sender);

        processType.setMessageHeader(messageHeaderType);
        processPurchaseOrder.setProcess(processType);

        PurchaseOrderType purchaseOrderType = new PurchaseOrderType();
        POHeaderData pOHeaderData = new POHeaderData();

        pOHeaderData.setCompanyCode(orderSapDTO.getCompanyCode());
        pOHeaderData.setOrderType(orderSapDTO.getOrderType());

        pOHeaderData.setVendor(orderSapDTO.getVendor());
        pOHeaderData.setPurchaseOrganization(orderSapDTO.getPurchaseOrganisation());
        pOHeaderData.setPurchaseGroup(orderSapDTO.getPurhchaseGroup());
        pOHeaderData.setDocumentDate(evalDate(orderSapDTO.getDocumentDate()));

        pOHeaderData.setCurrency(orderSapDTO.getCurrency());
        pOHeaderData.setPurchaseType(orderSapDTO.getPurchaseType());
        pOHeaderData.setUniqueExtOrder(orderSapDTO.getUniqueExtOrder());

        for (OrderSapLineDTO orderSapLineDTO : orderSapDTO.getOrderSapLines()) {
            PoItemData poItemData = new PoItemData();

            poItemData.setPurchaseOrderItem(orderSapLineDTO.getPurchaseOrderItem());
            poItemData.setAction(orderSapLineDTO.getAction());
            poItemData.setOrderReference(orderSapLineDTO.getOrderReference());
            poItemData.setPartNumber(orderSapLineDTO.getPartNumber());
            poItemData.setShortText(orderSapLineDTO.getShortText());
            poItemData.setPlant(orderSapLineDTO.getPlant());
            poItemData.setCurrentBuyer(orderSapLineDTO.getCurrentBuyer());
            poItemData.setMaterialGroup(orderSapLineDTO.getMaterialGroup());
            poItemData.setISOOrderPriceUnit(orderSapLineDTO.getIsoOrderPriceUnit());
            poItemData.setISOPurchaseOrderUnit(orderSapLineDTO.getIsoPurchaseOrderUnit());
            poItemData.setEstimatedPriceIndicator(orderSapLineDTO.isEstimatedPriceIndicator());
            BigDecimal netPriceSAP = orderSapLineDTO.getNetPrice();
            BigDecimal priceUnitSAP = orderSapLineDTO.getPriceUnit();
            // Transform to 2 or 0 decimals for SAP
            
            if (orderSapDTO.isSuppressDecimal()) {
                BigDecimal pricePerPCE = netPriceSAP.divide(priceUnitSAP);
                if (pricePerPCE.scale() > MAX_DECIMAL_LIMIT_PRICEUNIT) {
                    netPriceSAP = netPriceSAP.setScale(netPriceSAP.scale() - (pricePerPCE.scale() - MAX_DECIMAL_LIMIT_PRICEUNIT), 
                                                       RoundingMode.CEILING);
                }
                int fromScale = netPriceSAP.scale();
                netPriceSAP = Utils.roundOffDecimalPoints(netPriceSAP, fromScale, 0);
                BigDecimal value = new BigDecimal(Math.pow(TEN, fromScale));
                priceUnitSAP = priceUnitSAP.multiply(value);
            } else if (netPriceSAP.scale() >= THREE) {
                netPriceSAP = Utils.roundOffDecimalPoints(netPriceSAP, NUMBER_1, NUMBER_2);
                BigDecimal value = new BigDecimal(Math.pow(TEN, 1));
                priceUnitSAP = priceUnitSAP.multiply(value);
            }
            
            poItemData.setNetPrice(netPriceSAP);
            poItemData.setPriceUnit(priceUnitSAP);
            String taxCode = orderSapLineDTO.getTaxCode();
            if (StringUtils.isNotEmpty(taxCode)) {
                poItemData.setTaxCode(taxCode);
            }
            poItemData.setAccountAssignmentCategory(orderSapLineDTO.getAccountAssignmentCategory());
            poItemData.setUnlimitedDeliveryIndicator(orderSapLineDTO.isUnlimitedDeliveryIndicator());
            poItemData.setGRIndicator(orderSapLineDTO.isGrIndicator());
            poItemData.setNonValuatedGRIndicator(orderSapLineDTO.isNonValuedGrIndicator());
            poItemData.setIRIndicator(orderSapLineDTO.isIrIndicator());
            poItemData.setAcknowledgementNumber(orderSapLineDTO.getAcknowledgementNumber());
            poItemData.setPurchaseRequisitionNumber(orderSapLineDTO.getPurchaseRequisitionNumber());

            if (orderSapLineDTO.getCancelDate() != null) {
                poItemData.setCancelDate(evalDate(orderSapLineDTO.getCancelDate()));
            } else {
                poItemData.setQuantity(orderSapLineDTO.getQuantity());
                
                if (orderSapLineDTO.getAction().equals(SAPParam.ACTION_CREATE)) {
                    OrderSapAccountsDTO orderSapAccount = orderSapLineDTO.getOrderSapAccounts().get(0);
                    POAccountAssignment poAccountAssignment = new POAccountAssignment();
                    poAccountAssignment.setSequence(orderSapAccount.getSequence());
                    poAccountAssignment.setGeneralLedgerAccount(orderSapAccount.getGeneralLedgerAccount());
                    poAccountAssignment.setCostCenter(orderSapAccount.getCostCenter());
                    poAccountAssignment.setWBSElement(orderSapAccount.getWbsElement());
                    poItemData.setPOAccountAssignment(poAccountAssignment);
                }

                OrderSapScheduleDTO orderSapSchedule = orderSapLineDTO.getOrderSapSchedule().get(0);
                POScheduleLine poScheduleLine = new POScheduleLine();
                poScheduleLine.setCategoryOfDeliveryDate(orderSapSchedule.getCategoryOfDeliveryDate());
                poScheduleLine.setDeliveryDate(evalDate(orderSapSchedule.getDeliveryDate()));
                poItemData.setPOScheduleLine(poScheduleLine);
            }

            pOHeaderData.getPoItemData().add(poItemData);
        }
        purchaseOrderType.setPOHeaderData(pOHeaderData);
        processPurchaseOrder.setPurchaseOrder(purchaseOrderType);

        return processPurchaseOrder;
    }

    private XMLGregorianCalendar evalDate(Date date) {
        try {
            return DateUtil.getXMLGreorianCalendar(date);
        } catch (DatatypeConfigurationException e1) {
            return null;
        }
    }

    private XMLGregorianCalendar evalCurrentDateTime() {
        try {
            return DateUtil.getXMLGreorianCalendarWithTimeStamp(DateUtil.getCurrentUTCDate());
        } catch (DatatypeConfigurationException e1) {
            return null;
        }
    }
}
