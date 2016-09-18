package com.volvo.gloria.migration;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.util.migration.b.beans.OrderMigrationServiceBean;
import com.volvo.gloria.procurematerial.util.migration.c.OrderNewTemplateExcelHandler;
import com.volvo.gloria.procurematerial.util.migration.c.dto.OrderMigrationDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.GloriaExceptionLogger;
import com.volvo.gloria.util.IOUtil;
import com.volvo.gloria.util.c.GloriaExceptionConstants;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * 
 * describe
 * utility class to add data from ORDER_LINE_PAID column from Migration file to the related Orderline in DB for column
 * OrderLinePaidOnMigration of Order Line table
 * 
 */
public class OrderLinePaidInfoHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderLinePaidInfoHelper.class);
    private OrderRepository orderRepository;

    protected static final String DB_LOCAL = "initDB/dev-local-tomcat";
    protected static final String DB_DEV = "initDB/dev-was";
    protected static final String DB_TEST = "initDB/test-was";
    protected static final String DB_QA = "initDB/qa-was";
    private String env;

    public OrderLinePaidInfoHelper(String env) {
        this.env = env;
    }

    public static void main(String[] args) {
        // DB_LOCAL is default
        OrderLinePaidInfoHelper init = new OrderLinePaidInfoHelper(DB_DEV);
        try {
            init.compareMigrationDataWithDb();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public void compareMigrationDataWithDb() throws GloriaApplicationException {
        setEnvironment(env);
        orderRepository = ServiceLocator.getService(OrderRepository.class);
        try {
            InputStream[] ins = getInputStreams("34347");
            if (ins != null) {
                for (int i = 0; i < ins.length; i++) {
                    Set<OrderMigrationDTO> orderMigrationDTOs = new HashSet<OrderMigrationDTO>();
                    OrderNewTemplateExcelHandler openOrderNewTemplateExcelHandler = new OrderNewTemplateExcelHandler(ins[i]);
                    orderMigrationDTOs = openOrderNewTemplateExcelHandler.manageExcel();
                    if (!orderMigrationDTOs.isEmpty()) {
                        for (Iterator<OrderMigrationDTO> it = orderMigrationDTOs.iterator(); it.hasNext();) {
                            OrderMigrationDTO orderlineDto = it.next();
                            String orderno = orderlineDto.getOrderNumber();
                            String partno = orderlineDto.getPartNumber();
                            String partAffiliation = orderlineDto.getPartQualifier();
                            String partVersion = orderlineDto.getPartVersion();
                            String companycode = "FR46";
                            String orderLinePaid = orderlineDto.getOrderLinePaid();
                            if (!orderno.isEmpty() && !partno.isEmpty() && !partAffiliation.isEmpty() && !partVersion.isEmpty() && !companycode.isEmpty()) {
                                List<OrderLine> ordLine = new ArrayList<OrderLine>();
                                ordLine.addAll(orderRepository.getOrderLinesForRelatedMigratedOrder(orderno, partno, partAffiliation, partVersion, companycode));
                                if (ordLine != null && !ordLine.isEmpty()) {
                                    for (OrderLine orderLine : ordLine) {

                                        orderLine.setOrderlinePaidOnMigration(false);
                                        if (!orderLinePaid.isEmpty() && orderLinePaid.equalsIgnoreCase("YES")) {
                                            orderLine.setOrderlinePaidOnMigration(true);

                                        }
                                        orderRepository.saveOrderLine(orderLine);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            LOGGER.info("***** Done*****");
        } catch (Exception e) {
            GloriaExceptionLogger.log(e, OrderMigrationServiceBean.class);
            throw new GloriaApplicationException(GloriaExceptionConstants.DEFAULT_ERROR_CODE, "Failed to map the data to DB", e);
        } 
    }

    private InputStream[] getInputStreams(String siteId) throws IOException {
        InputStream[] streams = null;
        String path = null;
        String orderMigrPath = "/data/MigrationData/" + siteId + "/";
        try {
            path = env + orderMigrPath + "orders_*" + IOUtil.FILE_TYPE_EXCEL_NEW;
            streams = IOUtil.loadInputStreamFromClasspath(path);
        } catch (IOException e) {
            path = "initDB/" + "_global" + orderMigrPath + "orders_*" + IOUtil.FILE_TYPE_EXCEL_NEW;
            try {
                streams = IOUtil.loadInputStreamFromClasspath(path);
            } catch (Exception e1) {
                // Ignore
            }
        }
        return streams;
    }

    private void setEnvironment(String env) {
        String propertyValue = env + "/applicationContext_migration.xml";
        System.setProperty("com.volvo.jvs.applicationContextName", propertyValue);
    }

}
