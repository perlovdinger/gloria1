package com.volvo.gloria.init;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.authorization.util.InitDataTeam;
import com.volvo.gloria.common.util.InitDataCompanyCode;
import com.volvo.gloria.common.util.InitDataCostCenter;
import com.volvo.gloria.common.util.InitDataDeliveryFollowUpSupplierFilter;
import com.volvo.gloria.common.util.InitDataDeliveryFollowUpTeam;
import com.volvo.gloria.common.util.InitDataForGlAccount;
import com.volvo.gloria.common.util.InitDataWbsElement;
import com.volvo.gloria.procurematerial.util.InitDataPurchaseOrganisation;
import com.volvo.gloria.warehouse.util.InitDataPrinter;
import com.volvo.gloria.warehouse.util.InitDataWarehouse;

/**
 * The purpose of this class is to add Rollout specific Data to a environment.
 */
public final class GloriaRollout {

    private static final Logger LOGGER = LoggerFactory.getLogger(GloriaRollout.class);

    private Properties testDataProperties = new Properties();
    
    protected static final String DB_LOCAL = "initDB/dev-local-tomcat";
    protected static final String DB_DEV = "initDB/dev-was";
    protected static final String DB_TEST = "initDB/test-was";
    protected static final String DB_QA = "initDB/qa-was";
    protected static final String DB_PROD = "initDB/prod-was";

    private static final String DB_PROCESS_LOAD_DATA = "load-data";

    // Prevent instantiation
    private GloriaRollout() {
    }

    public static void main(String[] args) {
        GloriaRollout init = new GloriaRollout();
        try {
            // Never ever check in with any other default!!!
            init.process(DB_LOCAL);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private void process(String env) throws Exception {
        String dbprocess = getDbProcess(env);
        setEnvironment(env, dbprocess);
        LOGGER.info("*****" + dbprocess + " for " + env + " started");

        initBaseData(env);
        initNonOperativeData(env);

       /*
        * will be started from Gloria UI interface
        * 
        *  GloriaMigrator migr = new GloriaMigrator(DB_LOCAL);
        String[] sites = { "42102", "47670" };
        migr.process(sites);*/
        
        LOGGER.info("*****" + dbprocess + " for " + env + " completed successfully");
    }

    private void initBaseData(String env) throws Exception {
        LOGGER.info("***** initBaseData for " + env + " started");

        new InitDataCompanyCode(testDataProperties, env, "_Rollout1/CompanyCode.xml").initCompanyCodes(env, true);

        new InitDataForGlAccount(testDataProperties, env, "_Rollout1/GlAccount.xml").initGlAccounts();

        InitDataWarehouse.initWarehouses(env, "/data/base/Warehouse/_Rollout1/", true);

        new InitDataPrinter(testDataProperties, env, "/Printer/_Rollout1/Printer.xml").initPrinter();

        new InitDataTeam(testDataProperties, env, "common/_Rollout1").initTeam();
        new InitDataDeliveryFollowUpTeam(testDataProperties, env, "common/_Rollout1").initDeliveryFollowUpTeam();
        new InitDataDeliveryFollowUpSupplierFilter().initDataDeliveryFollowUpSupplierFilter(env, "/data/base/common/_Rollout1/");
        new InitDataPurchaseOrganisation(testDataProperties, env, "_Rollout1/PurchaseOrganisation.xml").initPurchaseOrganisation();
        
        LOGGER.info("***** initBaseData for " + env + " completed successfully");
    }

    private void initNonOperativeData(String env) throws Exception {
        LOGGER.info("***** initNonOperativeData for " + env + " started");
        LOGGER.info("***** initCostCenters started");
        new InitDataCostCenter().initCostCenters(env, "/data/nonOperative/CostCenter/_Rollout1/");
        LOGGER.info("***** initCostCenters ended");
        LOGGER.info("***** initWbsElement started");
        new InitDataWbsElement().initWbsElement(env, "/data/nonOperative/WbsElement/_Rollout1/");
        LOGGER.info("***** initWbsElement ended");
        LOGGER.info("***** initNonOperativeData for " + env + " completed successfully");
    }

    private String getDbProcess(String env) {
        String dbInitConfig = null;
        dbInitConfig = DB_PROCESS_LOAD_DATA;
        return dbInitConfig;
    }

    private void setEnvironment(String env, String dbprocess) {
        String propertyValue = env + "/applicationContext_no_update.xml";
        LOGGER.info("***** setEnvironmentNonOperative: applicationContextName=" + propertyValue);
        System.setProperty("com.volvo.jvs.applicationContextName", propertyValue);
    }
}
