package com.volvo.gloria.migration;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.config.b.beans.ApplicationUtils;
import com.volvo.gloria.procurematerial.util.migration.b.MigrationService;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

public class GloriaMigrator {
    private static final Logger LOGGER = LoggerFactory.getLogger(GloriaMigrator.class);
   
    

    private static final String NONOPERATIVE_MIGRATION_DATA = "/MigrationData/";
    private static final String MIGRATION_DATA_PROPERTY_KEY = "MigrationData";

    protected static final String DB_LOCAL = "initDB/dev-local-tomcat";
    protected static final String DB_DEV = "initDB/dev-was";
    protected static final String DB_TEST = "initDB/test-was";
    protected static final String DB_QA = "initDB/qa-was";
    private String env;

    public GloriaMigrator(String env) {
        this.env = env;
    }

    public static void main(String[] args) {
        // DB_LOCAL is default
        GloriaMigrator init = new GloriaMigrator(DB_LOCAL);
        try {
            init.process(null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public void process(String[] sites) {
        LOGGER.info("***** Data Migration for " + env + " started");
        try {
            setEnvironment(DB_LOCAL);
            String[] sitesToBeMigrated = null;
            if (sites  != null) {
                sitesToBeMigrated = sites;
            } else {
                sitesToBeMigrated = ApplicationUtils.getSitesToMigrate(env);
            }
          
           
            initMigration(sitesToBeMigrated);
            LOGGER.info("***** Data Migration for " + env + " completed successfully");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void initMigration(String[] sitesToBeMigrated) throws GloriaApplicationException {
        MigrationService migrationService = ServiceLocator.getService(MigrationService.class);
        Properties warehouseProperties = new Properties();
        warehouseProperties.setProperty(MIGRATION_DATA_PROPERTY_KEY, NONOPERATIVE_MIGRATION_DATA);
        migrationService.initiateWarehouseMigration(warehouseProperties, sitesToBeMigrated);

        Properties openOrderProperties = new Properties();
        openOrderProperties.setProperty(MIGRATION_DATA_PROPERTY_KEY, NONOPERATIVE_MIGRATION_DATA);

        migrationService.initiateOrderMigration(openOrderProperties, sitesToBeMigrated);
    }

    private void setEnvironment(String env) {
        String propertyValue = env + "/applicationContext_migration.xml";
        System.setProperty("com.volvo.jvs.applicationContextName", propertyValue);
    }

}
