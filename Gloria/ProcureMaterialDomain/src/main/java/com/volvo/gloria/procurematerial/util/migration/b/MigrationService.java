package com.volvo.gloria.procurematerial.util.migration.b;

import java.util.Properties;

import com.volvo.gloria.procurematerial.util.migration.c.dto.MigrationStatusDTO;
import com.volvo.gloria.util.GloriaApplicationException;

/**
 * Migration service.
 */
public interface MigrationService {
    void initiateWarehouseMigration(Properties testDataProperties, String[] sitesToBeMigrated) throws GloriaApplicationException;
    
    void initiateOrderMigration(Properties testDataProperties, String[] sitesToBeMigrated) throws GloriaApplicationException;

    MigrationStatusDTO getstatus();

    void initiateSendFinanceMessageForOrderMigration(String[] companyCodes) throws GloriaApplicationException;
}
