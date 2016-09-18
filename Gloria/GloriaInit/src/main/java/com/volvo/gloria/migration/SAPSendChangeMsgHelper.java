package com.volvo.gloria.migration;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.util.c.SAPParam;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

public class SAPSendChangeMsgHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(SAPSendChangeMsgHelper.class);

    protected static final String DB_LOCAL = "initDB/dev-local-tomcat";
    protected static final String DB_DEV = "initDB/dev-was";
    protected static final String DB_TEST = "initDB/test-was";
    protected static final String DB_QA = "initDB/qa-was";
    protected static final String DB_PROD = "initDB/prod-was";

    private String env;
    private String orderNo = "324-A15255-780";
    private String orderIdGps = "MA15255";

    private static ProcurementServices procurementServices;

    public SAPSendChangeMsgHelper(String env) {
        this.env = env;
    }

    public static void main(String[] args) {
        // DB_LOCAL is default
        SAPSendChangeMsgHelper init = new SAPSendChangeMsgHelper(DB_LOCAL);
        try {
            init.process();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public void process() {
        LOGGER.info("***** PO CREATION on " + env + " started");
        try {
            setEnvironment(DB_LOCAL);
            procurementServices = ServiceLocator.getService(ProcurementServices.class);
            procurementServices.triggerPOMessageForSAP(orderNo, orderIdGps, SAPParam.ACTION_CHANGE);
            LOGGER.info("***** PO CHANGE CREATION on " + env + " completed successfully");
        } catch (Exception e) {
            LOGGER.info(ExceptionUtils.getFullStackTrace(e));
        }
    }

    private void setEnvironment(String env) {
        String propertyValue = env + "/applicationContext_migration.xml";
        System.setProperty("com.volvo.jvs.applicationContextName", propertyValue);
    }

}
