package com.volvo.gloria.procurematerial.util.migration.b.beans;

import java.util.Properties;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;

import com.volvo.gloria.procurematerial.util.migration.b.OrderSplitService;
import com.volvo.gloria.procurematerial.util.migration.b.SplitService;
import com.volvo.gloria.procurematerial.util.migration.b.WarehouseSplitService;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.c.GloriaExceptionConstants;
import com.volvo.jvs.runtime.platform.ContainerManaged;

@ContainerManaged
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class SplitServiceBean implements SplitService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SplitServiceBean.class);
    
    private static boolean isRunningOO = false;
    
    private static final boolean MERGE_SPLITTED_PARTS = true;
    
    private OrderSplitService orderSplitService;
    private WarehouseSplitService warehouseSplitService;
    
    @Override
    @PreAuthorize("hasAnyRole('IT_SUPPORT')")
    public void initiateSplitOrderData(Properties openOrderProperties) throws GloriaApplicationException {
        if (isRunningOO) {
            throw new GloriaApplicationException(GloriaExceptionConstants.DEFAULT_ERROR_CODE, "Order split is already running");
        }
        LOGGER.info("Starting open order data split");
        isRunningOO = true;
        try {
            if (orderSplitService == null) {
                orderSplitService = new OrderSplitServiceBean();
            }
            orderSplitService.initiateOrderSplitData(openOrderProperties);
        } finally {
            isRunningOO = false;
        }
    }

    @Override
    @PreAuthorize("hasAnyRole('IT_SUPPORT')")
    public void initiateSplitWarehouseData(Properties warehouseProperties) throws GloriaApplicationException {
        if (isRunningOO) {
            throw new GloriaApplicationException(GloriaExceptionConstants.DEFAULT_ERROR_CODE, "Warehouse split is already running");
        }
        LOGGER.info("Starting Warehouse data split");
        isRunningOO = true;
        try {
            if (warehouseSplitService == null) {
                warehouseSplitService = new WarehouseSplitServiceBean();
            }
            warehouseSplitService.initiateWarehouseSplitData(warehouseProperties, MERGE_SPLITTED_PARTS);
        } finally {
            isRunningOO = false;
        }
    }
}
