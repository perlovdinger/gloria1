package com.volvo.gloria.migration;

import static com.volvo.gloria.util.c.SAPParam.ACCOUNT_ASSIGNMENT_CATEGORY;
import static com.volvo.gloria.util.c.SAPParam.CATEGORY_OF_DELIVERY_DATE;
import static com.volvo.gloria.util.c.SAPParam.GR_INDICATOR;
import static com.volvo.gloria.util.c.SAPParam.IR_INDICATOR;
import static com.volvo.gloria.util.c.SAPParam.MATERIAL_GROUP;
import static com.volvo.gloria.util.c.SAPParam.NON_VALUED_GR_INDICATOR;
import static com.volvo.gloria.util.c.SAPParam.ORDER_TYPE;
import static com.volvo.gloria.util.c.SAPParam.PURCHASE_GROUP;
import static com.volvo.gloria.util.c.SAPParam.PURCHASE_TYPE;
import static com.volvo.gloria.util.c.SAPParam.TAX_CODE;
import static com.volvo.gloria.util.c.SAPParam.UNLIMITED_DELIVERY_INDICATOR;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.procurematerial.d.entities.FinanceHeader;
import com.volvo.gloria.procurematerial.d.entities.MessageStatus;
import com.volvo.gloria.procurematerial.d.entities.Order;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLineVersion;
import com.volvo.gloria.procurematerial.d.entities.OrderSap;
import com.volvo.gloria.procurematerial.d.entities.OrderSapAccounts;
import com.volvo.gloria.procurematerial.d.entities.OrderSapLine;
import com.volvo.gloria.procurematerial.d.entities.OrderSapSchedule;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderSapRepository;
import com.volvo.gloria.util.Utils;
import com.volvo.gloria.util.c.SAPParam;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * 
 * Utility to create SAP related informations for Orders.
 * 
 */
public class SAPPOHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SAPPOHelper.class);

    protected static final String DB_LOCAL = "initDB/dev-local-tomcat";
    protected static final String DB_DEV = "initDB/dev-was";
    protected static final String DB_TEST = "initDB/test-was";
    protected static final String DB_QA = "initDB/qa-was";
    protected static final String DB_PROD = "initDB/prod-was";

    private static final int LEFT_PAD_VENDOR = 10;
    private static final int LEFT_PAD_CURRENTBUYER = 4;
    private static final int LEFT_PAD_GENERALLEDGER = 10;
    private static final int LEFT_PAD_COSTCENTER = 10;

    private String env;

    // NOTE: one order at a time
    // SET THE ORDER NUMBER AND PART INFORMATION FOR THE ORDERLINES FOR WHICH SAP INFORMATION HAS TO BE CREATED IN GLORIA
    
    // private String orderToBeConsidered = "073-824485-780";
    private String orderToBeConsidered = "";
    private static String partNumber = "";
    private static String partVersion = "";
    private static String partAffiliation = "";
  
    
    private static OrderRepository orderRepository = null;
    private static OrderSapRepository orderSapRepository = null;

    public SAPPOHelper(String env) {
        this.env = env;
    }

    public static void main(String[] args) {
        // DB_LOCAL is default
        SAPPOHelper init = new SAPPOHelper(DB_LOCAL);
        try {
            init.process();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public void process() {
        LOGGER.info("***** PO CREATION on " + env + " started");
        try {
            setEnvironment(DB_LOCAL);
            orderRepository = ServiceLocator.getService(OrderRepository.class);
            orderSapRepository = ServiceLocator.getService(OrderSapRepository.class);
            createPOs(Arrays.asList(orderToBeConsidered));
            LOGGER.info("***** PO CREATION on " + env + " completed successfully");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void createPOs(List<String> ordersToBeConsidered) {
        if (ordersToBeConsidered != null && !ordersToBeConsidered.isEmpty()) {
            for (String orderNo : ordersToBeConsidered) {
                Order order = orderRepository.findOrderByOrderNo(orderNo);
                createOrderSAP(order, orderRepository.getOrderLinesByOrder(order.getOrderOID()));
            }
        }
    }

    private void createOrderSAP(Order order, List<OrderLine> orderLines) {

        OrderSap orderSap = orderSapRepository.findOrderSapByUniqueExtOrder(order.getOrderIdGps());

        if (orderSap == null) {
            orderSap = new OrderSap();
            orderSap.setCompanyCode("FR46");
            orderSap.setOrderType(ORDER_TYPE);
            orderSap.setVendor(StringUtils.leftPad(order.getSupplierId(), LEFT_PAD_VENDOR, '0'));
            orderSap.setPurchaseOrganization("FL02");
            orderSap.setPurchaseGroup(PURCHASE_GROUP);
            orderSap.setDocumentDate(order.getOrderDateTime());

            OrderLine firstOrderLine = orderLines.get(0);
            orderSap.setCurrency(firstOrderLine.getCurrency());
            orderSap.setPurchaseType(PURCHASE_TYPE);
            orderSap.setUniqueExtOrder(order.getOrderIdGps());
            orderSap.setMessageStatus(MessageStatus.SHOULD_BE_SEND);
        }
        List<OrderSapLine> orderSapLines = createOrderSapLines(orderLines, order, orderSap);

        if (orderSapLines != null && !orderSapLines.isEmpty()) {
            orderSap.getOrderSapLines().addAll(orderSapLines);
        }

        orderSapRepository.save(orderSap);
    }

    private static List<OrderSapLine> createOrderSapLines(List<OrderLine> orderLines, Order order, OrderSap orderSap) {
        List<OrderSapLine> orderSapLines = new ArrayList<OrderSapLine>();
        // reorder orderlines "asc" to maintain proper re-creating of "sequence[purchaseOrderItem]", during GPS ammendment.
        Collections.sort(orderLines, new Comparator<OrderLine>() {
            @Override
            public int compare(OrderLine o1, OrderLine o2) {
                return Utils.compare(o1.getOrderLineOID(), o2.getOrderLineOID());
            }
        });
        long sequence = 1;
        for (OrderLine orderLine : orderLines) {
            if (orderLine.getPartNumber().equalsIgnoreCase(partNumber) && orderLine.getPartAffiliation().equalsIgnoreCase(partAffiliation)
                    && orderLine.getProcureLine().getpPartVersion().equalsIgnoreCase(partVersion)) {
                OrderLineVersion orderLineVersion = orderLine.getCurrent();
                long processPurchaseOrderQty = 0;
                processPurchaseOrderQty = orderLine.getPossibleToReceiveQuantity() - orderLine.getPaidQuantity();
                if (processPurchaseOrderQty > 0) {
                    OrderSapLine orderSapLine = new OrderSapLine();
                    orderSapLine.setOrderSap(orderSap);
                    orderSapLine.setPurchaseOrderitem(sequence);
                    orderSapLine.setAction(SAPParam.ACTION_CREATE);
                    orderSapLine.setOrderReference(order.getOrderNo());
                    orderSapLine.setPartNumber(orderLine.getPartNumber());
                    orderSapLine.setShortText(orderLine.getPartName());
                    orderSapLine.setPlant(order.getMaterialUserId());
                    orderSapLine.setMaterialGroup(MATERIAL_GROUP);
                    orderSapLine.setIsoPurchaseOrderUnit(orderLine.getUnitOfMeasure());
                    orderSapLine.setIsoOrderPriceUnit(orderLine.getUnitOfMeasure());

                    orderSapLine.setOrderLineVersion(orderLineVersion);
                    orderSapLine.setQuantity(processPurchaseOrderQty);
                    orderSapLine.setNetPrice(orderLineVersion.getUnitPrice());
                    orderSapLine.setPriceUnit(String.valueOf(orderLineVersion.getPerQuantity()));
                    orderSapLine.setCurrentBuyer(StringUtils.leftPad(orderLineVersion.getBuyerId(), LEFT_PAD_CURRENTBUYER, '0'));
                    orderSapLine.setTaxCode(TAX_CODE);
                    orderSapLine.setAccountAssignmentCategory(ACCOUNT_ASSIGNMENT_CATEGORY);
                    orderSapLine.setUnlimitedDeliveryIndicator(UNLIMITED_DELIVERY_INDICATOR);
                    orderSapLine.setGrIndicator(GR_INDICATOR);
                    orderSapLine.setNonValuedGrIndicator(NON_VALUED_GR_INDICATOR);
                    orderSapLine.setIrIndicator(IR_INDICATOR);
                    orderSapLine.setAcknowledgementNumber(order.getOrderNo());
                    orderSapLine.setPurchaseRequisitionNumber("V072974");

                    orderSapLine.setOrderSapAccounts(createOrderSapAccounts(orderLine.getProcureLine(), orderSapLine));
                    orderSapLine.setOrderSapSchedules(createOrderSapSchedules(order, orderSapLine));
                    orderSapLines.add(orderSapLine);
                    sequence++;

                }
            }
        }
        return orderSapLines;
    }

    private static List<OrderSapAccounts> createOrderSapAccounts(ProcureLine procureLine, OrderSapLine orderSapLine) {
        List<OrderSapAccounts> orderSapAccounts = new ArrayList<OrderSapAccounts>();
        OrderSapAccounts ordSapAccounts = new OrderSapAccounts();
        // set sequenceGenerator
        ordSapAccounts.setSequence(1);
        if (procureLine != null) {
            FinanceHeader financeHeader = procureLine.getFinanceHeader();
            ordSapAccounts.setGeneralLedgerAccount(StringUtils.leftPad(financeHeader.getGlAccount(), LEFT_PAD_GENERALLEDGER, '0'));
            ordSapAccounts.setCostCenter(StringUtils.leftPad(financeHeader.getCostCenter(), LEFT_PAD_COSTCENTER, '0'));
            ordSapAccounts.setWbsElement(financeHeader.getWbsCode());
        }
        ordSapAccounts.setOrderSapLine(orderSapLine);
        orderSapAccounts.add(ordSapAccounts);
        return orderSapAccounts;
    }

    private static List<OrderSapSchedule> createOrderSapSchedules(Order order, OrderSapLine orderSapLine) {
        List<OrderSapSchedule> orderSapSchedules = new ArrayList<OrderSapSchedule>();
        OrderSapSchedule orderSapSchedule = new OrderSapSchedule();
        orderSapSchedule.setOrderSapLine(orderSapLine);
        orderSapSchedule.setCategoryOfDeliveryDate(CATEGORY_OF_DELIVERY_DATE);
        orderSapSchedule.setDeliveryDate(order.getOrderDateTime());
        orderSapSchedules.add(orderSapSchedule);
        return orderSapSchedules;
    }

    private void setEnvironment(String env) {
        String propertyValue = env + "/applicationContext_migration.xml";
        System.setProperty("com.volvo.jvs.applicationContextName", propertyValue);
    }
}
