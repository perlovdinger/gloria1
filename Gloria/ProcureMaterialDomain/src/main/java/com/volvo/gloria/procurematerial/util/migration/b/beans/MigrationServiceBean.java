package com.volvo.gloria.procurematerial.util.migration.b.beans;

import java.util.Properties;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;

import com.volvo.gloria.procurematerial.util.migration.b.MigrationService;
import com.volvo.gloria.procurematerial.util.migration.b.OrderMigrationService;
import com.volvo.gloria.procurematerial.util.migration.b.OrderMigrationsToFinanceService;
import com.volvo.gloria.procurematerial.util.migration.b.WarehouseMigrationService;
import com.volvo.gloria.procurematerial.util.migration.c.dto.MigrationStatusDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.c.GloriaExceptionConstants;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * 
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class MigrationServiceBean implements MigrationService {
  
    private static final Logger LOGGER = LoggerFactory.getLogger(MigrationServiceBean.class);
    
    private static boolean isRunnungWH = false;
    private static boolean isRunnungOO = false;
    private static boolean isRunnungSendFinance = false;

    private OrderMigrationService openOrderMigrationService;
    private WarehouseMigrationService warehouseMigrationService;
    private OrderMigrationsToFinanceService orderMigrationsToFinanceService;

    @Override
    @PreAuthorize("hasAnyRole('IT_SUPPORT')")
    public void initiateWarehouseMigration(Properties testDataProperties, String[] sitesToBeMigrated) throws GloriaApplicationException {
        if (isRunnungWH) {
            throw new GloriaApplicationException(GloriaExceptionConstants.DEFAULT_ERROR_CODE, "Warehouse migration already running");
        }
        LOGGER.info("Starting warehouse migration");
        isRunnungWH = true;
        try {
            if (warehouseMigrationService == null) {
                warehouseMigrationService = new WarehouseMigrationServiceBean();
            }
            warehouseMigrationService.initiateWarehouseMigration(testDataProperties, sitesToBeMigrated);
        } finally {
            isRunnungWH = false;
        }

    }

    @Override
    @PreAuthorize("hasAnyRole('IT_SUPPORT')")
    public void initiateOrderMigration(Properties testDataProperties, String[] sitesToBeMigrated) throws GloriaApplicationException {
        if (isRunnungOO) {
            throw new GloriaApplicationException(GloriaExceptionConstants.DEFAULT_ERROR_CODE, "Order migration already running");
        }
        LOGGER.info("Starting open order migration");
        isRunnungOO = true;
        try {
            if (openOrderMigrationService == null) {
                openOrderMigrationService = new OrderMigrationServiceBean();
            }
            openOrderMigrationService.initiateOrderMigration(testDataProperties, sitesToBeMigrated);
        } finally {
            isRunnungOO = false;
        }
    }
    
    @Override
    @PreAuthorize("hasAnyRole('IT_SUPPORT')")
    public void initiateSendFinanceMessageForOrderMigration(String[] companyCodes) throws GloriaApplicationException {
        if (isRunnungSendFinance) {
            throw new GloriaApplicationException(GloriaExceptionConstants.DEFAULT_ERROR_CODE, "Sending of Finance Messages already running");
        }
        LOGGER.info("Starting Sending of Finance Messages for Order Migration");
        isRunnungSendFinance = true;
        try {
            if (orderMigrationsToFinanceService == null) {
                orderMigrationsToFinanceService = new OrderMigrationsToFinanceServiceBean();
            }
            orderMigrationsToFinanceService.sendPurcahseOrdersForMigratedOrders(companyCodes);
            orderMigrationsToFinanceService.sendGoodsMovementsForMigratedOrders(companyCodes);
        } finally {
            isRunnungSendFinance = false;
        }
    }

    @Override
    @PreAuthorize("hasAnyRole('IT_SUPPORT')")
    public MigrationStatusDTO getstatus() {
        if (warehouseMigrationService == null) {
            warehouseMigrationService = new WarehouseMigrationServiceBean();
        }
        if (openOrderMigrationService == null) {
            openOrderMigrationService = new OrderMigrationServiceBean();
        }
        if (orderMigrationsToFinanceService == null) {
            orderMigrationsToFinanceService = new OrderMigrationsToFinanceServiceBean();
        }
        if (!warehouseMigrationService.getstatus().isDone()) {
            return warehouseMigrationService.getstatus();
        } else if (!openOrderMigrationService.getstatus().isDone()) {
            return openOrderMigrationService.getstatus();
        } else if (!orderMigrationsToFinanceService.getstatus().isDone()) {
            return orderMigrationsToFinanceService.getstatus();
        }
        // Check exception
        MigrationStatusDTO migrationStatusDTO = new MigrationStatusDTO();
        migrationStatusDTO.setDone(true);
        migrationStatusDTO.setStatus("Nothing is running.");
        return migrationStatusDTO;
    }
}
