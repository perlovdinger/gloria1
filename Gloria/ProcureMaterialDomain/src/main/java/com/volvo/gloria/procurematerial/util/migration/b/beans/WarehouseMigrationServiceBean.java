package com.volvo.gloria.procurematerial.util.migration.b.beans;

import static com.volvo.gloria.procurematerial.util.migration.c.WarehouseMigrationHelper.filterMissingBinLocation;
import static com.volvo.gloria.procurematerial.util.migration.c.WarehouseMigrationHelper.getAllBinLocationCodes;
import static com.volvo.gloria.procurematerial.util.migration.c.WarehouseMigrationHelper.getValidList;
import static com.volvo.gloria.procurematerial.util.migration.c.WarehouseMigrationHelper.populateAdditionalAttibutes;
import static com.volvo.gloria.procurematerial.util.migration.c.WarehouseMigrationHelper.setAllCompanyCodes;
import static com.volvo.gloria.procurematerial.util.migration.c.WarehouseMigrationHelper.setAllMaterialControllerTeams;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.authorization.c.TeamType;
import com.volvo.gloria.authorization.d.entities.Team;
import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.PaginatedArrayList;
import com.volvo.gloria.common.d.entities.CompanyCode;
import com.volvo.gloria.common.d.entities.Site;
import com.volvo.gloria.config.b.beans.ApplicationProperties;
import com.volvo.gloria.procurematerial.util.migration.b.WarehouseMigrationService;
import com.volvo.gloria.procurematerial.util.migration.c.WarehouseMigrationHelper;
import com.volvo.gloria.procurematerial.util.migration.c.WarehouseNewTemplateExcelHandler;
import com.volvo.gloria.procurematerial.util.migration.c.dto.MigrationStatusDTO;
import com.volvo.gloria.procurematerial.util.migration.c.dto.WarehouseMigrationDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.GloriaExceptionLogger;
import com.volvo.gloria.util.IOUtil;
import com.volvo.gloria.util.c.GloriaExceptionConstants;
import com.volvo.gloria.warehouse.d.entities.BinLocation;
import com.volvo.gloria.warehouse.repositories.b.WarehouseRepository;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

public class WarehouseMigrationServiceBean implements WarehouseMigrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WarehouseMigrationServiceBean.class);

    private static final int TEN = 10;
    private static final int HUNDRED = 100;
    private static final int MILLI = 1000;
    private boolean isDone = true;
    private StringBuilder statusText = new StringBuilder();
    private int percentDone = 0;
    private String siteInProgress = "";
    private String siteDone = "";
    private static final int PAGE_SIZE = 20;

    private static final String NONOPERATIVE_WAREHOUSE = "/MigrationData/";
    private static final String WAREHOUSE_DATA_PROPERTY_KEY = "MigrationData";
    private static final String RESULT_FILE = "warehouse_<site>.csv";
    private static final String RESULT_PATH = "C:\\Gloria\\migration\\wh\\result\\";

    private static WarehouseRepository warehouseRepository;

    static {
        warehouseRepository = ServiceLocator.getService(WarehouseRepository.class);
    }

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
    public void initiateWarehouseMigration(Properties testDataProperties, String[] whToBeMigrated) throws GloriaApplicationException {
        isDone = false;
        statusText = new StringBuilder();
        try {
            CommonServices commonServices = ServiceLocator.getService(CommonServices.class);
            UserServices userServices = ServiceLocator.getService(UserServices.class);

            String location = (String) testDataProperties.get(WAREHOUSE_DATA_PROPERTY_KEY);
            setAllMaterialControllerTeams(new HashSet<Team>(userServices.getTeams(TeamType.MATERIAL_CONTROL.toString())));
            setAllCompanyCodes(new HashSet<CompanyCode>(commonServices.findAllCompanyCodes()));
            WarehouseMigrationHelper.setAllSitesInfo(new HashSet<Site>(commonServices.getAllSites()));

            // Read input excel
            for (String whSiteId : whToBeMigrated) {
                siteInProgress = "Warehouse - " + whSiteId;
                long start = System.currentTimeMillis();
                Properties updatedDataProperties = new Properties();
                updatedDataProperties.setProperty(WAREHOUSE_DATA_PROPERTY_KEY, location + whSiteId + File.separator);
                List<WarehouseMigrationDTO> warehouseMigrationDTOs = new ArrayList<WarehouseMigrationDTO>();
                InputStream[] ins = getInputStreams(whSiteId);
                if (ins != null) {
                    for (int i = 0; i < ins.length; i++) {
                        log(whSiteId);
                        WarehouseNewTemplateExcelHandler warehouseMigrationExcelHandler = new WarehouseNewTemplateExcelHandler(ins[i]);
                        warehouseMigrationDTOs.addAll(warehouseMigrationExcelHandler.manageExcel());

                        List<WarehouseMigrationDTO> validDTOs = getValidList(warehouseMigrationDTOs);

                        Set<String> binLocationCodes = getAllBinLocationCodes(warehouseMigrationDTOs);

                        List<BinLocation> binLocations = warehouseRepository.findBinLocations(whSiteId, binLocationCodes);
                        validDTOs = filterMissingBinLocation(validDTOs, binLocations);

                        populateAdditionalAttibutes(validDTOs);

                        PaginatedArrayList<WarehouseMigrationDTO> migrationDto = new PaginatedArrayList<WarehouseMigrationDTO>(validDTOs);
                        migrationDto.setPageSize(PAGE_SIZE);
                        int totalDone = 0, divisor = TEN, total = validDTOs.size();
                        for (List<WarehouseMigrationDTO> subList = null; (subList = migrationDto.nextPage()) != null;) {
                            PerformWarehouseMigrationServiceBean serviceBean = ServiceLocator.getService(PerformWarehouseMigrationServiceBean.class);
                            serviceBean.performWarehouseMigration(subList);
                            totalDone += subList.size();
                            percentDone = (int) (totalDone * HUNDRED / total);
                            if (percentDone >= divisor) {
                                divisor += TEN;
                                log(percentDone + "% - " + totalDone);
                            }
                        }

                        generateReport(whSiteId, warehouseMigrationDTOs, getOutFilename(testDataProperties, whSiteId));
                        log(whSiteId + " took " + (System.currentTimeMillis() - start) / MILLI + " sec");
                    }
                }

                siteDone += (", " + whSiteId);
                siteInProgress = "";
            }
            isDone = true;
            statusText = new StringBuilder();
            siteInProgress = "";
        } catch (Exception e) {
            GloriaExceptionLogger.log(e, WarehouseMigrationServiceBean.class);
            throw new GloriaApplicationException(GloriaExceptionConstants.DEFAULT_ERROR_CODE, "Failed to run Warehouse Migration!", e);
        } finally {
            statusText.append("\nDone");
        }
    }

    private void generateReport(String whSiteId, List<WarehouseMigrationDTO> warehouseMigrationDTOs, String fileName) {
        if (warehouseMigrationDTOs != null && !warehouseMigrationDTOs.isEmpty()) {
            try {
                final File parentDir = new File(fileName).getParentFile();
                if (null != parentDir) {
                    parentDir.mkdirs();
                }
                FileWriter writer = new FileWriter(fileName);
                int notMigrated = 0;
                int migrated = 0;
                // Add Header
                writer.append(WarehouseNewTemplateExcelHandler.HEADER);
                writer.append('\n');
                for (WarehouseMigrationDTO warehouseMigrationDTO : warehouseMigrationDTOs) {
                    if (!warehouseMigrationDTO.isMigrated()) {
                        notMigrated++;
                    } else {
                        migrated++;
                    }
                    writer.append(warehouseMigrationDTO.toString());
                    writer.append('\n');
                }
                writer.flush();
                writer.close();
                log("WH_" + whSiteId + " - " + migrated + " materials " + fileName);
                log("WH_" + whSiteId + " - " + notMigrated + " materials could not be migrated! " + fileName);
            } catch (IOException e) {
                GloriaExceptionLogger.log(e, WarehouseMigrationServiceBean.class);
            }
        }
    }

    private InputStream[] getInputStreams(String whSiteId) throws IOException {
        InputStream[] streams = null;
        String path = null;
        String warehouseMigrPath = "/data/MigrationData/" + whSiteId + "/";
        String env = ServiceLocator.getService(ApplicationProperties.class).getEnvironment();
        try {
            path = "initDB/" + env + warehouseMigrPath + "warehouse_*" + IOUtil.FILE_TYPE_EXCEL_NEW;
            streams = IOUtil.loadInputStreamFromClasspath(path);
        } catch (IOException e) {
            path = "initDB/" + "_global" + warehouseMigrPath + "warehouse_*" + IOUtil.FILE_TYPE_EXCEL_NEW;
            try {
                streams = IOUtil.loadInputStreamFromClasspath(path);
            } catch (Exception e1) {
                GloriaExceptionLogger.log(e1, WarehouseMigrationServiceBean.class);
            }
        }
        return streams;
    }

    private String getOutFilename(Properties testDataProperties, String whSiteId) {
        String outFilename;
        if (((String) testDataProperties.get(WAREHOUSE_DATA_PROPERTY_KEY)).startsWith(NONOPERATIVE_WAREHOUSE)) {

            outFilename = RESULT_PATH + (RESULT_FILE).replace("<site>", whSiteId);
        } else {
            String location = (String) testDataProperties.get(WAREHOUSE_DATA_PROPERTY_KEY);
            outFilename = location.replace("input", "result") + (RESULT_FILE).replace("<site>", whSiteId);
        }
        log("outFilename=" + outFilename);
        return outFilename;
    }

    private void log(String text) {
        statusText.append(text + "\n");
        LOGGER.info(text);
    }
}
