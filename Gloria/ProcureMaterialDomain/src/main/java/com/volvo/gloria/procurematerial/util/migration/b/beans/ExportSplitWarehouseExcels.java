package com.volvo.gloria.procurematerial.util.migration.b.beans;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.procurematerial.util.migration.c.WarehouseNewTemplateExcelHandler;
import com.volvo.gloria.procurematerial.util.migration.c.dto.WarehouseMigrationDTO;
import com.volvo.gloria.util.excel.ExcelOperationUtil;

public final class ExportSplitWarehouseExcels {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExportSplitOrdersExcels.class);

    private static CellStyle cs;
    private static final String RESULT_FILE = "warehouse_<siteId>.xlsx";
    private static final String RESULT_PATH = "../GloriaConfig/src/main/resources/";
    private static final String OPEN_ORDER_DATA_PROPERTY_KEY = "MigrationData";
    private static final String NONOPERATIVE_OPEN_ORDER = "/MigrationData/";
    
    private ExportSplitWarehouseExcels() {

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void export(Map<String, List<WarehouseMigrationDTO>> siteIdWarehouseMap, Properties warehouseProperties) {
        try {

            Iterator iterator = siteIdWarehouseMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry pair = (Map.Entry) iterator.next();
                String siteId = (String) pair.getKey();
                List<WarehouseMigrationDTO> warehouses = (List<WarehouseMigrationDTO>) pair.getValue();

                LOGGER.info("Warehouses to be spilt for site - " + siteId + " is " + warehouses.size());

                if (siteId == null) {
                    siteId = "NoWhsite";
                }
                LOGGER.info("Starting the export excel file for Site Id - " + siteId);

                String fileName = getOutFilename(warehouseProperties, siteId);
                final File parentDir = new File(fileName).getParentFile();
                if (null != parentDir) {
                    parentDir.mkdirs();
                }
                
                SXSSFWorkbook workbook = ExcelOperationUtil.getSXSSFWorkbook();
                SXSSFSheet sheet = (SXSSFSheet) workbook.createSheet();
                cs = workbook.createCellStyle();
                ExcelOperationUtil.setUpHeaderInfo(workbook, sheet, WarehouseNewTemplateExcelHandler.getColumnList(), sheet.getPhysicalNumberOfRows());

                for (WarehouseMigrationDTO warehouseMigrationDTO : warehouses) {
                    setUpDataInExcel(warehouseMigrationDTO, workbook, sheet);
                }
                writeToExcel(workbook, fileName);
            }
        } catch (IOException e) {
            LOGGER.info(e.getMessage());
        }
    }

    private static void setUpDataInExcel(WarehouseMigrationDTO warehouseMigrationDTO, SXSSFWorkbook workbook, SXSSFSheet sheet) {
        int cellIndex = 0;
        SXSSFRow row = ExcelOperationUtil.createRow(sheet, sheet.getPhysicalNumberOfRows());

        cellIndex = createCellForUpdateInExcelCells(sheet, warehouseMigrationDTO.getWarehouseSiteId(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, warehouseMigrationDTO.getBinLocationCode(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, warehouseMigrationDTO.getStaDate(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, warehouseMigrationDTO.getOrderNo(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, warehouseMigrationDTO.getOrderDateOriginal(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, warehouseMigrationDTO.getPartAffiliationOriginal(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, warehouseMigrationDTO.getPartNumber(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, warehouseMigrationDTO.getStage(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, warehouseMigrationDTO.getIssue(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, warehouseMigrationDTO.getPartNameOriginal(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, warehouseMigrationDTO.getProjectId(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, warehouseMigrationDTO.getReference(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, warehouseMigrationDTO.getStorageQuantity(), cellIndex, row, false, true);
        cellIndex = createCellForUpdateInExcelCells(sheet, warehouseMigrationDTO.getTestObject(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, warehouseMigrationDTO.getUnitOfMeasure(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, Boolean.toString(warehouseMigrationDTO.isSplitted()), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, warehouseMigrationDTO.getBinlocationSplit(), cellIndex, row, false, false);
    }

    private static void writeToExcel(SXSSFWorkbook workbook, String fileName) throws IOException {
        FileOutputStream os = new FileOutputStream(fileName);
        workbook.write(os);
        os.flush();
        os.close();
    }

    private static int createCellForUpdateInExcelCells(SXSSFSheet sheet, String values, int cellIndex, SXSSFRow row, boolean wrapText, boolean isNumeric) {
        int cellIndexx = cellIndex++;
        SXSSFCell cell = ExcelOperationUtil.createCell(row, cellIndexx);
        if (isNumeric && !StringUtils.isEmpty(values)) {
            Double value = Double.valueOf(values.replaceAll("[^0-9.]|\\s+", ""));
            cell.setCellValue(value);
        } else {
            cell.setCellValue(handleEmpty(values));
        }
        if (wrapText) {
            cs.setWrapText(true);
            cell.setCellStyle(cs);
        }
        
        return cellIndex;
    }

    private static String handleEmpty(String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        return value;
    }

    private static String getOutFilename(Properties testDataProperties, String siteId) {
        String outFilename;
        if (((String) testDataProperties.get(OPEN_ORDER_DATA_PROPERTY_KEY)).startsWith(NONOPERATIVE_OPEN_ORDER)) {
            String location = "initDB/_global/data" + (String) testDataProperties.get(OPEN_ORDER_DATA_PROPERTY_KEY);
            outFilename =  RESULT_PATH + location + siteId + "/" + (RESULT_FILE).replace("<siteId>",  siteId);
        } else {
            // Find outfile dynamic!
            String location = (String) testDataProperties.get(OPEN_ORDER_DATA_PROPERTY_KEY);
            outFilename = location.replace("input", "output") + (RESULT_FILE).replace("<siteId>",  siteId);
        }
        
        return outFilename;
    }

}
