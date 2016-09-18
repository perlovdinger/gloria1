package com.volvo.gloria.migration;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.procurematerial.util.migration.b.SplitService;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

public class SplitGloriaMigrationData {

    private static final Logger LOGGER = LoggerFactory.getLogger(SplitGloriaMigrationData.class);

    private static final String NONOPERATIVE_MIGRATION_DATA = "/MigrationData/";
    private static final String MIGRATION_DATA_PROPERTY_KEY = "MigrationData";

    private static final String DB_LOCAL = "initDB/dev-local-tomcat";
    // private static final String DB_DEV = "initDB/dev-was";
    // private static final String DB_TEST = "initDB/test-was";
    // private static final String DB_QA = "initDB/qa-was";
    private String env;
    private SplitService splitService = null;
    private Properties migrationDataProperties = new Properties();

    public SplitGloriaMigrationData(String env) {
        this.env = env;
    }

    public static void main(String[] args) {
        // DB_LOCAL is default
        SplitGloriaMigrationData init = new SplitGloriaMigrationData(DB_LOCAL);
        try {
            init.process();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public void process() {
        try {
            setEnvironment();
            splitService = ServiceLocator.getService(SplitService.class);
            migrationDataProperties.setProperty(MIGRATION_DATA_PROPERTY_KEY, NONOPERATIVE_MIGRATION_DATA);
            LOGGER.info("***** Data Split for open orders-" + env + " started");
            initSplitOrdersData();
            LOGGER.info("***** Data Split for open orders-" + env + " completed successfully");

            LOGGER.info("***** Data Split for warehouse-" + env + " started");
            initSplitWarehouseData();
            LOGGER.info("***** Data Split for warehouse-" + env + " completed successfully");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void initSplitWarehouseData() throws GloriaApplicationException {
        splitService.initiateSplitWarehouseData(migrationDataProperties);
    }

    private void initSplitOrdersData() throws GloriaApplicationException {
        splitService.initiateSplitOrderData(migrationDataProperties);
    }

    private void setEnvironment() {
        String propertyValue = env + "/applicationContext_migration.xml";
        System.setProperty("com.volvo.jvs.applicationContextName", propertyValue);
    }
}
