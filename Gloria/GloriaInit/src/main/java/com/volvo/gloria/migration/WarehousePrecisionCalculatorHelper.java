package com.volvo.gloria.migration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.common.c.PaginatedArrayList;
import com.volvo.gloria.procurematerial.c.DeliveryNoteLineStatus;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLineVersion;
import com.volvo.gloria.procurematerial.d.entities.status.orderLine.OrderLineStatus;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.util.DateUtil;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

public class WarehousePrecisionCalculatorHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(WarehousePrecisionCalculatorHelper.class);

    protected static final String DB_LOCAL = "initDB/dev-local-tomcat";
    protected static final String DB_DEV = "initDB/dev-was";
    protected static final String DB_TEST = "initDB/test-was";
    protected static final String DB_QA = "initDB/qa-was";
    protected static final String DB_PROD = "initDB/prod-was";

    private String env;

    private static OrderRepository orderRepository = null;
    private static DeliveryNoteRepository deliveryNoteRepository = null;

    public WarehousePrecisionCalculatorHelper(String env) {
        this.env = env;
    }

    public static void main(String[] args) {
        // DB_LOCAL is default
        WarehousePrecisionCalculatorHelper init = new WarehousePrecisionCalculatorHelper(DB_LOCAL);
        try {
            init.process();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public void process() {
        LOGGER.info("***** Warehouse Precision UPDATION on " + env + " started");
        try {
            setEnvironment(DB_LOCAL);

            orderRepository = ServiceLocator.getService(OrderRepository.class);
            deliveryNoteRepository = ServiceLocator.getService(DeliveryNoteRepository.class);

            updatePrecisionValues();

            LOGGER.info("***** Warehouse Precision UPDATION on " + env + " completed successfully");
        } catch (Exception e) {
             LOGGER.error(e.getMessage());
        }
    }

    private void updatePrecisionValues() {
        List<Long> orderLineIds = orderRepository.findOrderLineIdsByStatus(Arrays.asList(OrderLineStatus.RECEIVED_PARTLY, OrderLineStatus.COMPLETED));
        if (orderLineIds != null && !orderLineIds.isEmpty()) {
            log(" Total Orderlines RECEIVED/RECEIVED_PARTLY :: " + orderLineIds.size());
            PaginatedArrayList<Long> currentIdList = new PaginatedArrayList<Long>(orderLineIds);
            for (List<Long> subListOfIds = null; (subListOfIds = currentIdList.nextPage()) != null;) {
                List<OrderLine> orderLines = orderRepository.findOrderLinesByIds(subListOfIds);
                for (OrderLine orderLine : orderLines) {
                    if (orderLine != null) {
                        List<DeliveryNoteLine> deliveryNoteLines = new ArrayList<DeliveryNoteLine>(
                                deliveryNoteRepository.findDeliveryNoteLinesByOrderLineId(orderLine.getOrderLineOID(),
                                                                                          DeliveryNoteLineStatus.RECEIVED));
                        if (deliveryNoteLines != null && !deliveryNoteLines.isEmpty()) {
                            Collections.sort(deliveryNoteLines, new Comparator<DeliveryNoteLine>() {
                                @Override
                                public int compare(DeliveryNoteLine dl1, DeliveryNoteLine dl2) {
                                    return dl1.getReceivedDateTime().compareTo(dl2.getReceivedDateTime());
                                }
                            });
                            DeliveryNoteLine deliveryNoteLine = deliveryNoteLines.get(0);
                            orderLine.setFirst(deliveryNoteLine);
                            Date deliveryNoteDate = deliveryNoteLine.getDeliveryNote().getDeliveryNoteDate();
                            if (deliveryNoteDate != null) {
                                log(" -------------Order Line Id " + orderLine.getOrderLineOID() + "------------- " + deliveryNoteDate + "---------------"
                                        + deliveryNoteLine.getReceivedDateTime());
                                OrderLineVersion currentVersion = orderLine.getCurrent();
                                currentVersion.setOrderStaDateOnTime(isDeliveryOnTime(currentVersion.getOrderStaDate(), deliveryNoteDate));
                                currentVersion.setStaAgreedDateOnTime(isDeliveryOnTime(currentVersion.getStaAgreedDate(), deliveryNoteDate));
                                //orderRepository.saveOrderLineVersion(currentVersion);
                            }
                            orderRepository.saveOrderLine(orderLine);
                        }
                    }
                }
            }
        }
    }

    private static Boolean isDeliveryOnTime(Date anyStaDate, Date deliveryDate) {
        if (anyStaDate != null && deliveryDate != null) {
            if (deliveryDate.before(anyStaDate) || DateUtil.areDatesFromSameWeek(deliveryDate, anyStaDate)) {
                return true;
            }
            return false;
        }
        return null;
    }

    private void setEnvironment(String env) {
        String propertyValue = env + "/applicationContext_migration.xml";
        System.setProperty("com.volvo.jvs.applicationContextName", propertyValue);
    }

    private void log(String logText) {
        LOGGER.info(logText);
    }
}
