package com.volvo.gloria.procurematerial.util.migration.b;

import java.util.Properties;

import com.volvo.gloria.procurematerial.util.migration.c.dto.MigrationStatusDTO;
import com.volvo.gloria.util.GloriaApplicationException;



/**
 * services for Open Order Migration.
 */
public interface WarehouseMigrationService {
    
    void initiateWarehouseMigration(Properties testDataProperties, String[] sitesToBeMigrated) throws GloriaApplicationException;

    MigrationStatusDTO getstatus();
}
