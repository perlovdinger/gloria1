package com.volvo.gloria.procurematerial.util.migration.b.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.procurematerial.util.migration.b.WarehouseSplitService;
import com.volvo.gloria.procurematerial.util.migration.c.WarehouseNewTemplateExcelHandler;
import com.volvo.gloria.procurematerial.util.migration.c.dto.WarehouseMigrationDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.IOUtil;
import com.volvo.gloria.util.c.GloriaExceptionConstants;

public class WarehouseSplitServiceBean implements WarehouseSplitService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderSplitServiceBean.class);

    private static final String OPEN_ORDER_DATA_PROPERTY_KEY = "MigrationData";
    private static final String WAREHOUSE = "warehouse";
    private static final int MILLI = 1000;

    @Override
    public void initiateWarehouseSplitData(Properties openOrderProperties, boolean mergeSplittedParts) throws GloriaApplicationException {
        try {
            for (String fullFilename : getInFilenames()) {

                if (fullFilename != null && fullFilename.contains(WAREHOUSE) && fullFilename.endsWith(IOUtil.FILE_TYPE_EXCEL_NEW)) {
                    long time = System.currentTimeMillis();
                    List<WarehouseMigrationDTO> warehouseMigrationDTOs = new ArrayList<WarehouseMigrationDTO>();

                    WarehouseNewTemplateExcelHandler warehouseNewTemplateExcelHandler = new WarehouseNewTemplateExcelHandler(fullFilename, mergeSplittedParts);
                    warehouseMigrationDTOs = warehouseNewTemplateExcelHandler.manageExcel();
                    LOGGER.info("Done Reading in - " + (System.currentTimeMillis() - time) / MILLI + " sec");

                    if (!warehouseMigrationDTOs.isEmpty()) {
                        generateExcels(warehouseMigrationDTOs, openOrderProperties);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.info("Failed to run Warehouse Split!");
            throw new GloriaApplicationException(GloriaExceptionConstants.DEFAULT_ERROR_CODE, "Failed to run Warehouse Split!", e);
        }
    }

    private void generateExcels(List<WarehouseMigrationDTO> warehouseMigrationDTOs, Properties warehouseProperties) {
        Map<String, List<WarehouseMigrationDTO>> siteIdWarehouseMap = new HashMap<String, List<WarehouseMigrationDTO>>();
        int total = warehouseMigrationDTOs.size();
        LOGGER.info("Total size :" + total);

        for (WarehouseMigrationDTO warehouseMigrationDTO : warehouseMigrationDTOs) {
            String siteId = warehouseMigrationDTO.getSiteId();

            if (siteIdWarehouseMap.containsKey(siteId)) {
                siteIdWarehouseMap.get(siteId).add(warehouseMigrationDTO);
            } else {
                siteIdWarehouseMap.put(siteId, new ArrayList<WarehouseMigrationDTO>(Arrays.asList(warehouseMigrationDTO)));
            }
        }

        ExportSplitWarehouseExcels.export(siteIdWarehouseMap, warehouseProperties);
    }

    private String[] getInFilenames() throws IOException {

        return IOUtil.loadFileContentsFromClasspath(OPEN_ORDER_DATA_PROPERTY_KEY + "/", IOUtil.FILE_TYPE_EXCEL_NEW, "initDB/_global/data/");

    }

}
