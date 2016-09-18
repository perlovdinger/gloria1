package com.volvo.gloria.procurematerial.util.migration.b.beans;

import static com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper.setAllBuildSites;
import static com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper.setAllCompanyCodes;
import static com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper.setAllMaterialControllerTeams;
import static com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper.setAllPurchaseOrganizations;
import static com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper.setAllSites;
import static com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper.setAllSupplierCounterParts;
import static com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper.setAllUserIdToUserNames;
import static com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper.sortAfterOrderNo;
import static com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper.validateApplyGeneralRules;
import static com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper.validateApplySiteSpecificRules;
import static com.volvo.gloria.procurematerial.util.migration.c.OrderMigrationHelper.validateMatchMaterialsForReceivedOrders;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.authorization.c.TeamType;
import com.volvo.gloria.authorization.d.entities.Team;
import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.d.entities.CompanyCode;
import com.volvo.gloria.common.d.entities.SupplierCounterPart;
import com.volvo.gloria.config.b.beans.ApplicationProperties;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.util.migration.b.OrderMigrationService;
import com.volvo.gloria.procurematerial.util.migration.c.OrderNewTemplateExcelHandler;
import com.volvo.gloria.procurematerial.util.migration.c.dto.MigrationStatusDTO;
import com.volvo.gloria.procurematerial.util.migration.c.dto.OrderMigrationDTO;
import com.volvo.gloria.procurematerial.util.migration.c.type.order.MigrationOrderType;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.GloriaExceptionLogger;
import com.volvo.gloria.util.IOUtil;
import com.volvo.gloria.util.c.GloriaExceptionConstants;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * Implementation class for OpenOrderMigrationService.
 */
public class OrderMigrationServiceBean implements OrderMigrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderMigrationServiceBean.class);

    private static final int TEN = 10;
    private static final int HUNDRED = 100;
    private static final int MILLI = 1000;
    private boolean isDone = true;
    private StringBuilder statusText = new StringBuilder();
    private int percentDone = 0;
    private String siteInProgress = "";
    private String siteDone = "";

    private static final String RESULT_FILE = "Orders_<siteId>.csv";
    private static final String RESULT_PATH = "C:\\Gloria\\migration\\Order\\result\\";
    private static final String NONOPERATIVE_ORDER = "/MigrationData/";
    private static final String ALL_ORDER_DATA_PROPERTY_KEY = "MigrationData";

    @Override
    public MigrationStatusDTO getstatus() {
        MigrationStatusDTO migrationStatusDTO = new MigrationStatusDTO();
        migrationStatusDTO.setDone(isDone);
        migrationStatusDTO.setSiteCompleted(siteDone);
        migrationStatusDTO.setCompleted(percentDone);
        migrationStatusDTO.setSiteInProgress(siteInProgress);
        migrationStatusDTO.setStatus(statusText.toString());
        return migrationStatusDTO;
    }

    @Override
    public void initiateOrderMigration(Properties testDataProperties, String[] sitesToBeMigrated) throws GloriaApplicationException {
        try {
            CommonServices commonServices = ServiceLocator.getService(CommonServices.class);
            MaterialHeaderRepository materialHeaderRepository = ServiceLocator.getService(MaterialHeaderRepository.class);
            UserServices userServices = ServiceLocator.getService(UserServices.class);
            ProcurementServices procurementServices = ServiceLocator.getService(ProcurementServices.class);
            
            isDone = false;
            statusText = new StringBuilder();
            setAllMaterialControllerTeams(new HashSet<Team>(userServices.getTeams(TeamType.MATERIAL_CONTROL.toString())));
            setAllCompanyCodes(new HashSet<CompanyCode>(commonServices.findAllCompanyCodes()));
            setAllSupplierCounterParts(new HashSet<SupplierCounterPart>(commonServices.getAllSupplierCounterParts()), sitesToBeMigrated);
            setAllUserIdToUserNames(new HashSet<UserDTO>(userServices.findAllUsers()));
            setAllBuildSites(commonServices.getAllBuildSites(null, null));
            setAllSites(commonServices.getAllSites());
            setAllPurchaseOrganizations(procurementServices.getAllPurchaseOrganizations());

            String location = (String) testDataProperties.get(ALL_ORDER_DATA_PROPERTY_KEY);
            // Read input excel

            for (String whSiteId : sitesToBeMigrated) {
                siteInProgress = "Open Orders - " + whSiteId;
                long start = System.currentTimeMillis();
                Properties updatedDataProperties = new Properties();
                updatedDataProperties.setProperty(ALL_ORDER_DATA_PROPERTY_KEY, location + whSiteId + File.separator);

                InputStream[] ins = getInputStreams(whSiteId);

                if (ins != null) {
                    for (int i = 0; i < ins.length; i++) {
                        long time = System.currentTimeMillis();
                        Set<OrderMigrationDTO> orderMigrationDTOs = new HashSet<OrderMigrationDTO>();
                        OrderNewTemplateExcelHandler openOrderNewTemplateExcelHandler = new OrderNewTemplateExcelHandler(ins[i]);
                        orderMigrationDTOs = openOrderNewTemplateExcelHandler.manageExcel();

                        log("Done Reading in - " + (System.currentTimeMillis() - time) / MILLI + " sec");
                        if (!orderMigrationDTOs.isEmpty()) {
                            // run migration
                            time = System.currentTimeMillis();
                            log("Generating report - " + whSiteId);
                            log("Migrating -" + orderMigrationDTOs.size());

                            List<String> uniqueOrderLineKeys = new ArrayList<String>();

                            validateApplyGeneralRules(orderMigrationDTOs, uniqueOrderLineKeys, materialHeaderRepository);

                            validateApplySiteSpecificRules(validOrders(orderMigrationDTOs), commonServices);

                            validateApplyFinancialInfoRulesForExternalOpen(validExternalOpenOrders(orderMigrationDTOs), orderMigrationDTOs);
                            validateApplyFinancialInfoRulesForExternalClosed(validExternalClosedOrders(orderMigrationDTOs), orderMigrationDTOs);

                            validateApplyFinancialInfoRulesForInternalOpen(validInternalOpenOrders(orderMigrationDTOs), orderMigrationDTOs);
                            validateApplyFinancialInfoRulesForInternalClosed(validInternalClosedOrders(orderMigrationDTOs), orderMigrationDTOs);

                            validateMatchMaterialsForReceivedOrders(validOrders(orderMigrationDTOs), materialHeaderRepository);

                            performOpenOrderMigration(validOrders(orderMigrationDTOs));

                            log("Generating report - " + whSiteId);

                            generateReport(orderMigrationDTOs, getOutFilename(testDataProperties, whSiteId), whSiteId);

                            log("Total - " + (System.currentTimeMillis() - start) / MILLI + " sec");
                        }
                    }
                }
                siteDone += (", " + whSiteId);
                siteInProgress = "";
            }
            isDone = true;
            statusText = new StringBuilder();
            siteInProgress = "";
        } catch (Exception e) {
            GloriaExceptionLogger.log(e, OrderMigrationServiceBean.class);
            throw new GloriaApplicationException(GloriaExceptionConstants.DEFAULT_ERROR_CODE, "Failed to run OpenOrder Migration!", e);
        } finally {
            statusText.append("Done");
            siteDone = Arrays.toString(sitesToBeMigrated);
        }
    }
    
    @SuppressWarnings("unchecked")
    private List<OrderMigrationDTO> validOrders(Set<OrderMigrationDTO> orderMigrationDTOs) {
        List<OrderMigrationDTO> validOrdersFiltered = new ArrayList<OrderMigrationDTO>();
        Collection<OrderMigrationDTO> filteredCollection = CollectionUtils.select(orderMigrationDTOs, new org.apache.commons.collections.Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return ((OrderMigrationDTO) object).isValid();
            }
        });

        if (filteredCollection != null && !filteredCollection.isEmpty()) {
            validOrdersFiltered.addAll(filteredCollection);
        }
        return validOrdersFiltered;
    }

    @SuppressWarnings("unchecked")
    private List<OrderMigrationDTO> validExternalOpenOrders(Set<OrderMigrationDTO> orderMigrationDTOs) {
        List<OrderMigrationDTO> externalOrdersFiltered = new ArrayList<OrderMigrationDTO>();
        Collection<OrderMigrationDTO> filteredCollection = CollectionUtils.select(orderMigrationDTOs, new org.apache.commons.collections.Predicate() {
            @Override
            public boolean evaluate(Object object) {
                OrderMigrationDTO order = (OrderMigrationDTO) object;
                return order.isExternal() && order.isValid() && MigrationOrderType.valueOf(order.getOrderType()) == MigrationOrderType.OPEN;
            }
        });

        if (filteredCollection != null && !filteredCollection.isEmpty()) {
            externalOrdersFiltered.addAll(filteredCollection);
        }
        return externalOrdersFiltered;
    }

    @SuppressWarnings("unchecked")
    private List<OrderMigrationDTO> validExternalClosedOrders(Set<OrderMigrationDTO> orderMigrationDTOs) {
        List<OrderMigrationDTO> externalOrdersFiltered = new ArrayList<OrderMigrationDTO>();
        Collection<OrderMigrationDTO> filteredCollection = CollectionUtils.select(orderMigrationDTOs, new org.apache.commons.collections.Predicate() {
            @Override
            public boolean evaluate(Object object) {
                OrderMigrationDTO order = (OrderMigrationDTO) object;
                return order.isExternal() && order.isValid() && MigrationOrderType.valueOf(order.getOrderType()) == MigrationOrderType.CLOSED;
            }
        });

        if (filteredCollection != null && !filteredCollection.isEmpty()) {
            externalOrdersFiltered.addAll(filteredCollection);
        }
        return externalOrdersFiltered;
    }

    @SuppressWarnings("unchecked")
    private List<OrderMigrationDTO> validInternalOpenOrders(Set<OrderMigrationDTO> orderMigrationDTOs) {
        List<OrderMigrationDTO> internalOrdersFiltered = new ArrayList<OrderMigrationDTO>();
        Collection<OrderMigrationDTO> filteredCollection = CollectionUtils.select(orderMigrationDTOs, new org.apache.commons.collections.Predicate() {
            @Override
            public boolean evaluate(Object object) {
                OrderMigrationDTO order = (OrderMigrationDTO) object;
                return (!order.isExternal()) && order.isValid() && MigrationOrderType.valueOf(order.getOrderType()) == MigrationOrderType.OPEN;
            }
        });

        if (filteredCollection != null && !filteredCollection.isEmpty()) {
            internalOrdersFiltered.addAll(filteredCollection);
        }
        return internalOrdersFiltered;
    }

    @SuppressWarnings("unchecked")
    private List<OrderMigrationDTO> validInternalClosedOrders(Set<OrderMigrationDTO> orderMigrationDTOs) {
        List<OrderMigrationDTO> internalOrdersFiltered = new ArrayList<OrderMigrationDTO>();
        Collection<OrderMigrationDTO> filteredCollection = CollectionUtils.select(orderMigrationDTOs, new org.apache.commons.collections.Predicate() {
            @Override
            public boolean evaluate(Object object) {
                OrderMigrationDTO order = (OrderMigrationDTO) object;
                return (!order.isExternal()) && order.isValid() && MigrationOrderType.valueOf(order.getOrderType()) == MigrationOrderType.CLOSED;
            }
        });

        if (filteredCollection != null && !filteredCollection.isEmpty()) {
            internalOrdersFiltered.addAll(filteredCollection);
        }
        return internalOrdersFiltered;
    }

    private void performOpenOrderMigration(List<OrderMigrationDTO> orderMigrationDTOs) throws GloriaApplicationException {
        Map<String, List<OrderMigrationDTO>> orderMap = sortAfterOrderNo(orderMigrationDTOs);
        int total = orderMap.keySet().size(), divisor = TEN, totalDone = 0;
        PerformOrderMigrationServiceBean performOpenOrderMigrationService = ServiceLocator.getService(PerformOrderMigrationServiceBean.class);
        for (String orderNoAndDate : orderMap.keySet()) {
            boolean isLastItem = false;
            if (totalDone + 1 == total) {
                isLastItem = true;
            }
            try {
                List<OrderMigrationDTO> sameOrderNoDtos = orderMap.get(orderNoAndDate);
                performOpenOrderMigrationService.performOpenOrderMigration(sameOrderNoDtos, isLastItem);
                totalDone++;
            } catch (Exception e) {
                log("Order no and date:" + orderNoAndDate);
                GloriaExceptionLogger.log(e, OrderMigrationServiceBean.class);
            }
            percentDone = (int) (totalDone * HUNDRED / total);
            if (percentDone >= divisor) {
                divisor += TEN;
                log(percentDone + "% - " + totalDone);
            }
        }
    }

    /**
     * Rules for External Open Orders.
     * 
     * Validate WBS, GL Account, Cost Center - for SAP supported company code.
     * 
     * @param allValidExternalOrders
     * @param openOrderMigrationDTOs
     * @return
     */
    private void validateApplyFinancialInfoRulesForExternalOpen(List<OrderMigrationDTO> allValidOrders, Set<OrderMigrationDTO> openOrderMigrationDTOs) {
        long time = System.currentTimeMillis();
        MigrationOrderType.OPEN.validateFinancialInfoForExternal(allValidOrders, openOrderMigrationDTOs);
        log("Done validateApplyGeneralRules in - " + (System.currentTimeMillis() - time) / 1000 + " sec");
    }

    /**
     * Rules for External Closed Orders.
     * 
     * Validate WBS, GL Account, Cost Center - for SAP supported company code.
     * 
     * @param allValidExternalOrders
     * @param openOrderMigrationDTOs
     * @return
     */
    private void validateApplyFinancialInfoRulesForExternalClosed(List<OrderMigrationDTO> allValidOrders, Set<OrderMigrationDTO> openOrderMigrationDTOs) {
        // do nothing
    }

    /**
     * Rules for Internal Open Orders.
     * 
     * @param allValidInternalOrders
     * @param openOrderMigrationDTOs
     * @return
     */
    private void validateApplyFinancialInfoRulesForInternalOpen(List<OrderMigrationDTO> allValidOrders, Set<OrderMigrationDTO> openOrderMigrationDTOs) {
        // do nothing
    }

    /**
     * Rules for Internal Closed Orders.
     * 
     * @param allValidInternalOrders
     * @param openOrderMigrationDTOs
     * @return
     */
    private void validateApplyFinancialInfoRulesForInternalClosed(List<OrderMigrationDTO> allValidOrders, Set<OrderMigrationDTO> openOrderMigrationDTOs) {
        // do nothing
    }

    private void log(String text) {
        statusText.append(text + "\n");
        LOGGER.info(text);
    }

    private void generateReport(Set<OrderMigrationDTO> orderMigrationDTOs, String fileName, String whSiteId) {
        if (orderMigrationDTOs != null && !orderMigrationDTOs.isEmpty()) {
            log("Total Orders input - [" + orderMigrationDTOs.size() + "] ");
            try {
                final File parentDir = new File(fileName).getParentFile();
                if (null != parentDir) {
                    parentDir.mkdirs();
                }
                FileWriter writer = new FileWriter(fileName);
                int notMigrated = 0;
                // Add Header
                writer.append(OrderNewTemplateExcelHandler.HEADER);
                writer.append('\n');
                for (OrderMigrationDTO openOrderMigrationDTO : orderMigrationDTOs) {
                    if (!openOrderMigrationDTO.isMigrated()) {
                        notMigrated++;
                    }
                    writer.append(openOrderMigrationDTO.toString());
                    writer.append('\n');
                }
                writer.flush();
                writer.close();
                log("Orders_" + whSiteId + " - " + notMigrated + " orders could not be migrated! " + fileName);
            } catch (IOException e) {
                log(e.getMessage());
            }
        }
    }

    private InputStream[] getInputStreams(String siteId) throws IOException {
        InputStream[] streams = null;
        String path = null;
        String orderMigrPath = "/data/MigrationData/" + siteId + "/";

        String env = ServiceLocator.getService(ApplicationProperties.class).getEnvironment();
        try {
            path = "initDB/" + env + orderMigrPath + "orders_*" + IOUtil.FILE_TYPE_EXCEL_NEW;

            streams = IOUtil.loadInputStreamFromClasspath(path);
        } catch (IOException e) {
            path = "initDB/" + "_global" + orderMigrPath + "orders_*" + IOUtil.FILE_TYPE_EXCEL_NEW;
            try {
                streams = IOUtil.loadInputStreamFromClasspath(path);
            } catch (Exception e1) {
                // Ignore
            }
        }
        return streams;
    }

    private static String getOutFilename(Properties testDataProperties, String whSiteId) {
        String outFilename;
        if (((String) testDataProperties.get(ALL_ORDER_DATA_PROPERTY_KEY)).startsWith(NONOPERATIVE_ORDER)) {
            outFilename = RESULT_PATH + (RESULT_FILE).replace("<siteId>", whSiteId);
        } else {
            // Find outfile dynamic!
            String location = (String) testDataProperties.get(ALL_ORDER_DATA_PROPERTY_KEY);
            outFilename = location.replace("input", "result") + (RESULT_FILE).replace("<siteId>", whSiteId);

        }
        LOGGER.info("outFilename=" + outFilename);
        return outFilename;
    }

}
