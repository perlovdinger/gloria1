package com.volvo.gloria.init;

import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.authorization.b.UserAuthorizationServices;
import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.authorization.util.InitDataTeam;
import com.volvo.gloria.authorization.util.InitDataTeamUser;
import com.volvo.gloria.authorization.util.InitDataUser;
import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.util.InitDataCarryOver;
import com.volvo.gloria.common.util.InitDataCompanyCode;
import com.volvo.gloria.common.util.InitDataConversionUnits;
import com.volvo.gloria.common.util.InitDataCostCenter;
import com.volvo.gloria.common.util.InitDataCurrency;
import com.volvo.gloria.common.util.InitDataCurrencyRate;
import com.volvo.gloria.common.util.InitDataDangerousGoods;
import com.volvo.gloria.common.util.InitDataDeliveryFollowUpSupplierFilter;
import com.volvo.gloria.common.util.InitDataDeliveryFollowUpTeam;
import com.volvo.gloria.common.util.InitDataForGlAccount;
import com.volvo.gloria.common.util.InitDataInternalOrderSap;
import com.volvo.gloria.common.util.InitDataPartAffiliation;
import com.volvo.gloria.common.util.InitDataPartAliasMapping;
import com.volvo.gloria.common.util.InitDataQualityDocument;
import com.volvo.gloria.common.util.InitDataSites;
import com.volvo.gloria.common.util.InitDataUnitOfMeasure;
import com.volvo.gloria.common.util.InitDataWbsElement;
import com.volvo.gloria.materialrequest.b.MaterialRequestServices;
import com.volvo.gloria.materialrequest.repositories.b.MaterialRequestRepository;
import com.volvo.gloria.procurematerial.b.DeliveryServices;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNote;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;
import com.volvo.gloria.procurematerial.util.InitDataPurchaseOrganisation;
import com.volvo.gloria.util.IOUtil;
import com.volvo.gloria.warehouse.b.WarehouseServices;
import com.volvo.gloria.warehouse.util.InitDataPrinter;
import com.volvo.gloria.warehouse.util.InitDataWarehouse;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * The purpose of this class is to populate Entities for a specific environment.
 */
public final class GloriaInitializer {

    private Properties testDataProperties = new Properties();
    private static final Logger LOGGER = LoggerFactory.getLogger(GloriaInitializer.class);
    private static final String DB_LOCAL = "initDB/dev-local-tomcat";
    private static final String DB_DEV = "initDB/dev-was";
    private static final String DB_TEST = "initDB/test-was";
    private static final String DB_QA = "initDB/qa-was";
    private static final String DB_PROD = "initDB/prod-was";

    // INIT
    // Drop all schema components in the schema XML.
    // It brings the schema up-to-date (Adding tables,,,).
    // Execute SQL to delete all rows from all tables that OpenJPA knows about.

    // UPDATE
    // Keep all schema components in the given XML definition, but drop the rest from the database.
    // It brings the schema up-to-date (Adding tables,,,)
    
    //LOAD_DATA
    // inserts all base and Non operative Data

    private static final String DB_PROCESS_INIT_LOAD_DATA = "init-load-data";
    private static final String DB_PROCESS_UPDATE = "update";
    private static final String DB_PROCESS_LOAD_DATA = "load-data";
    private static final String DB_PROCESS_INIT = "init";    

    // Prevent instantiation
    public GloriaInitializer() {
    }

    public static void main(String[] args) {
        GloriaInitializer init = new GloriaInitializer();
        try {
            // Never ever check in with any other default!!!
            init.process(DB_LOCAL);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
    }

    protected void process(String env) throws Exception {
        String dbprocess = getDbProcess(env);
        setEnvironment(env, dbprocess);
        LOGGER.info("*****" + dbprocess + " for " + env + " started");

        if (dbprocess.equals(DB_PROCESS_INIT_LOAD_DATA)) {
            initBaseData(env);
            initNonOperativeData(env);
        } else if (dbprocess.equals(DB_PROCESS_UPDATE)) {
            triggerEntityManager();

        } else if (dbprocess.equals(DB_PROCESS_LOAD_DATA)) {
            initBaseData(env);
            initNonOperativeData(env);           
        } else if (dbprocess.equals(DB_PROCESS_INIT)) {
            triggerEntityManager();
        }
        LOGGER.info("*****" + dbprocess + " for " + env + " completed successfully");

    }

    private void initBaseData(String env) throws Exception {
        LOGGER.info("***** initBaseData for " + env + " started");

        // CommonDomain
        trickCommonDomain();

        new InitDataCurrency(testDataProperties, env).initCurrencys();
        new InitDataCompanyCode(testDataProperties, env, null).initCompanyCodes(env, false);
        new InitDataCurrencyRate(testDataProperties, env).initCurrencyRate();
        new InitDataDangerousGoods(testDataProperties, env).initDangerousGoods();
        new InitDataInternalOrderSap(testDataProperties, env).initInternalOrderSap();
        new InitDataForGlAccount(testDataProperties, env, null).initGlAccounts();
        new InitDataPartAffiliation(testDataProperties, env).initPartAffiliations();
        new InitDataPartAliasMapping(testDataProperties, env).initPartAliasMapping();
        new InitDataQualityDocument(testDataProperties, env).initQualityDocument();
        new InitDataSites(testDataProperties, env).initSites(env);
        new InitDataUnitOfMeasure(testDataProperties, env).initUnitOfMeasure();
        new InitDataConversionUnits(testDataProperties, env).initConversionUnits();
        
        // ProcureMaterialDomain
        trickProcureMaterialDomain();

        new InitDataPurchaseOrganisation(testDataProperties, env, null).initPurchaseOrganisation();

        // MaterialRequestDomain
        trickMaterialRequestDomain();

        // UserAuthorizationDomain
        trickUserAuthorizationDomain();

        new InitDataTeam(testDataProperties, env, null).initTeam();
        InitDataUser.initUsers(env);
        InitDataTeamUser.initTeamUsers(env);
        new InitDataDeliveryFollowUpTeam(testDataProperties, env, null).initDeliveryFollowUpTeam();
        new InitDataDeliveryFollowUpSupplierFilter().initDataDeliveryFollowUpSupplierFilter(env, "/data/base/common/");
        
        if (DB_LOCAL.equals(env)) {
            // WarehouseDomain
            InitDataWarehouse.initWarehouses(env, null, false);
            new InitDataPrinter(testDataProperties, env, null).initPrinter();
        }
        
        LOGGER.info("***** initBaseData for " + env + " completed successfully");
    }

    private void initNonOperativeData(String env) throws Exception {
        // Init load
        if (DB_LOCAL.equals(env)) {
            LOGGER.info("***** initNonOperativeData for " + env + " started");
            LOGGER.info("***** initCostCenters started");
            new InitDataCostCenter().initCostCenters(env, null);

            LOGGER.info("***** initCarryOvers started");
            String carryOverPath1 = "/data/nonOperative/CarryOver1/";
            new InitDataCarryOver().initDataCarryOver(env, IOUtil.FILE_TYPE_EXCEL_NEW, carryOverPath1);
            String carryOverPath2 = "/data/nonOperative/CarryOver1/";
            new InitDataCarryOver().initDataCarryOver(env, IOUtil.FILE_TYPE_EXCEL_NEW, carryOverPath2);

            LOGGER.info("***** initWbsElement started");
            new InitDataWbsElement().initWbsElement(env, null);
            LOGGER.info("***** initWbsElement ended");
            LOGGER.info("***** initNonOperativeData for " + env + " completed successfully");
        }

    }

    private void triggerEntityManager() throws Exception {
        trickCommonDomain();
        trickWarehouseDomain();
        trickProcureMaterialDomain();
        trickMaterialRequestDomain();
        trickUserAuthorizationDomain();

    }

    private void trickCommonDomain() {
        // Special trick to get EntityManager to create Entities
        CommonServices commonServices = ServiceLocator.getService(CommonServices.class);
        commonServices.getAllGlAccounts();
        commonServices.getDeliveryFollowupTeam("LYS");
        commonServices.getDeliveryFollowUpTeamFilters(1);

    }

    private void trickWarehouseDomain() {
        // Special trick to get EntityManager to create Entities
        WarehouseServices warehouseServices = ServiceLocator.getService(WarehouseServices.class);
        warehouseServices.getWarehouseList();

    }

    private void trickProcureMaterialDomain() {
        // Special trick to get EntityManager to create Entities
        ServiceLocator.getService(ProcurementServices.class);
        DeliveryServices delService = ServiceLocator.getService(DeliveryServices.class);
        delService.getAllOrderLines();
        DeliveryNoteRepository delNoteBean = ServiceLocator.getService(DeliveryNoteRepository.class);
        List<DeliveryNote> list = delNoteBean.findAll();
        for (DeliveryNote deliveryNote : list) {
            deliveryNote.getGoodsReceiptHeader();
        }
    }

    private void trickMaterialRequestDomain() {
        // Special trick to get EntityManager to create Entities
        MaterialRequestServices mr = ServiceLocator.getService(MaterialRequestServices.class);
        mr.equals(null);
        MaterialRequestRepository materialRequestRepository = ServiceLocator.getService(MaterialRequestRepository.class);
        materialRequestRepository.findAll();
    }

    private void trickUserAuthorizationDomain() throws Exception {
        // Special trick to get EntityManager to create Entities
        UserServices userServices = ServiceLocator.getService(UserServices.class);
        userServices.getUser("GOT_CV");
        UserAuthorizationServices userAuthorizationServices = ServiceLocator.getService(UserAuthorizationServices.class);
        userAuthorizationServices.getApplicationSettings("GOT_CV");
    }

    private String getDbProcess(String env) {
        String dbInitConfig = null;
        if (env.equals(DB_LOCAL)) {
            dbInitConfig = DB_PROCESS_INIT_LOAD_DATA;
        } else if (env.equals(DB_DEV)) {
            dbInitConfig = DB_PROCESS_INIT_LOAD_DATA;
        } else if (env.equals(DB_TEST)) {
            dbInitConfig = DB_PROCESS_INIT_LOAD_DATA;
        } else if (env.equals(DB_QA)) {
            dbInitConfig = DB_PROCESS_INIT_LOAD_DATA;
        } else if (env.equals(DB_PROD)) {
            dbInitConfig = DB_PROCESS_INIT_LOAD_DATA;
        }
        return dbInitConfig;
    }

    private void setEnvironment(String env, String dbprocess) {
        if (dbprocess.equals(DB_PROCESS_INIT_LOAD_DATA) || dbprocess.equals(DB_PROCESS_INIT)) {
            String propertyValue = env + "/applicationContext_init.xml";
            LOGGER.info("***** setEnvironment: applicationContextName=" + propertyValue);
            System.setProperty("com.volvo.jvs.applicationContextName", propertyValue);
        } else if (dbprocess.equals(DB_PROCESS_UPDATE)) {
            String propertyValue = env + "/applicationContext_update.xml";
            LOGGER.info("***** setEnvironmentNonOperative: applicationContextName=" + propertyValue);
            System.setProperty("com.volvo.jvs.applicationContextName", propertyValue);
        } else if (dbprocess.equals(DB_PROCESS_LOAD_DATA)) {
            String propertyValue = env + "/applicationContext_no_update.xml";
            LOGGER.info("***** setEnvironmentNonOperative: applicationContextName=" + propertyValue);
            System.setProperty("com.volvo.jvs.applicationContextName", propertyValue);
        }        
    }
}
