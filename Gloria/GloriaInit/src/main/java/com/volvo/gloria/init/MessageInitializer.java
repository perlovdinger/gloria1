package com.volvo.gloria.init;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.common.b.CarryOverTransformer;
import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.b.CostCenterTransformer;
import com.volvo.gloria.common.b.WBSElementTransformer;
import com.volvo.gloria.common.carryover.c.dto.SyncPurchaseOrderCarryOverDTO;
import com.volvo.gloria.common.costcenter.c.dto.SyncCostCenterDTO;
import com.volvo.gloria.common.purchaseorder.c.dto.SyncPurchaseOrderTypeDTO;
import com.volvo.gloria.common.wbs.c.dto.SyncWBSElementDTO;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.b.PurchaseOrderTransformer;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * 
 * Class to load the JMS messages from the 'gloria_gateway_xml' log.
 * 
 * transform the xml content and save.
 * 
 * Note: testdata/testdata.properties , should must have an entry 'logged.messages.dir' set to a valid directory to enable/disable message loading in test
 * environment.
 * 
 */
public class MessageInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageInitializer.class);

    private static final String LOGGED_MESSGES_DIR_PROPERTY_KEY = "logged.messages.dir";

    private String loggedMessagesDir;

    // services
    private ProcurementServices procurementServices;
    private CommonServices commonServices;
    // transformers
    private CarryOverTransformer carryOverTransformer;
    private PurchaseOrderTransformer purchaseOrderTransformer;
    private CostCenterTransformer costCenterTransformer;
    
    private WBSElementTransformer wbsElementTransformer;

    public MessageInitializer(Properties testDataProperties) {
        if (testDataProperties.containsKey(LOGGED_MESSGES_DIR_PROPERTY_KEY)) {
            loggedMessagesDir = testDataProperties.getProperty(LOGGED_MESSGES_DIR_PROPERTY_KEY);
        }
    }

    private void prepareResources() {
        procurementServices = ServiceLocator.getService(ProcurementServices.class);
        commonServices = ServiceLocator.getService(CommonServices.class);
        carryOverTransformer = ServiceLocator.getService(CarryOverTransformer.class);
        purchaseOrderTransformer = ServiceLocator.getService(PurchaseOrderTransformer.class);
        costCenterTransformer = ServiceLocator.getService(CostCenterTransformer.class);
         wbsElementTransformer = ServiceLocator.getService(WBSElementTransformer.class);
    }

    public void loadMessages() {
        if (loggedMessagesDir != null) {
            LOGGER.info("Logged messages are loaded from directory " + loggedMessagesDir);

            prepareResources();
            File configuredLocation = new File(loggedMessagesDir);
            if (configuredLocation.exists()) {
                File[] logFiles = configuredLocation.listFiles();
                if (logFiles != null && logFiles.length > 0) {
                    for (File logFile : logFiles) {
                        LOGGER.info("--------------- Loading XML messages from file : " + logFile.getName());
                        FileInputStream fs = null;
                        try {
                            fs = new FileInputStream(logFile);
                        } catch (FileNotFoundException fileNotFoundException) {
                            LOGGER.error("Couldn't find file: " + logFile, fileNotFoundException);
                        }
                        BufferedReader br = new BufferedReader(new InputStreamReader(fs));
                        loadMessage(logFile, fs, br);
                    }
                }
            }
        }
    }

    private void loadMessage(File logFile, FileInputStream fs, BufferedReader br) {
        String message;
        try {
            while ((message = br.readLine()) != null) {
                if (message.contains("<?xml version=\"1.0\" encoding")) {
                    loadMessages(message);
                }
            }
            fs.close();
        } catch (IOException e) {
            LOGGER.error("IO error for file: " + logFile, e);
        } catch (GloriaApplicationException e) {
            LOGGER.error("GloriaApplicationException: " + e.getMessage(), e);
        }
    }

    private void loadMessages(String message) throws GloriaApplicationException {
        String xmlMessage = message.substring(message.indexOf("<?xml version=\"1.0\" encoding"), message.length() - 1);
        loadCarryOverMessages(message, xmlMessage);
        loadPurchaseOrderMessages(message, xmlMessage);
        loadCostCenterMessages(message, xmlMessage);
        loadWbsElementMessages(message, xmlMessage);
    }

    private void loadCarryOverMessages(String message, String xmlMessage) {
        if (message.contains("CarryOverGatewayBean")) {
            LOGGER.info("CarryOverGatewayBean:: loading message --> " + xmlMessage);
            SyncPurchaseOrderCarryOverDTO syncCarryOverDTO = carryOverTransformer.transformStoredCarryOver(xmlMessage);
            commonServices.syncCarryOver(syncCarryOverDTO);
        }
    }

    private void loadPurchaseOrderMessages(String message, String xmlMessage) throws GloriaApplicationException {
        if (message.contains("PurchaseOrderGatewayBean")) {
            LOGGER.info("PurchaseOrderGatewayBean:: loading message --> " + xmlMessage);
            SyncPurchaseOrderTypeDTO syncPurchaseOrderTypeDTO = purchaseOrderTransformer.transformStoredPurchaseOrder(xmlMessage);
            procurementServices.createOrUpdatePurchaseOrder(syncPurchaseOrderTypeDTO);
        }
    }

    private void loadCostCenterMessages(String message, String xmlMessage) {
        if (message.contains("CostCenterGatewayBean")) {
            LOGGER.info("CostCenterGatewayBean:: loading message --> " + xmlMessage);
            SyncCostCenterDTO syncCostCenterDTO = costCenterTransformer.transformStoredCostCenter(xmlMessage);
            commonServices.syncCostCenter(syncCostCenterDTO);
        }
    }   

    private void loadWbsElementMessages(String message, String xmlMessage) {
        if (message.contains("WBSElementGatewayBean")) {
            LOGGER.info("WBSElementGatewayBean:: loading message --> " + xmlMessage);
            SyncWBSElementDTO syncWBSElementDTO = wbsElementTransformer.transformStoredWBSElement(xmlMessage);
            commonServices.syncWBSElement(syncWBSElementDTO);
        }
    }
}
