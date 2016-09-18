package com.volvo.gloria.procurematerial.util.migration.b.beans;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.procurematerial.util.migration.c.OrderNewTemplateExcelHandler;
import com.volvo.gloria.procurematerial.util.migration.c.dto.OrderMigrationDTO;
import com.volvo.gloria.procurematerial.util.migration.c.dto.OrderReceivedDTO;
import com.volvo.gloria.util.excel.ExcelOperationUtil;

public final class ExportSplitOrdersExcels {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExportSplitOrdersExcels.class);

    private static final int ORDERLINE_PAID_CELL = 37;
    private static final int RECEIVED_QTY_CELL = 40;
    private static final int PACKING_SLIP_NUMBER_CELL = 58;
    private static CellStyle cs;
    private static final String RESULT_FILE = "orders_<siteId>.xlsx";
    private static final String RESULT_PATH = "../GloriaConfig/src/main/resources/";
    private static final String ALL_ORDER_DATA_PROPERTY_KEY = "MigrationData";
    private static final String NONOPERATIVE_ORDER = "/MigrationData/";
    
    private ExportSplitOrdersExcels() {

    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void export(Map<String, List<OrderMigrationDTO>> siteIdOrdersMap, Properties orderProperties) {
        try {

            Iterator iterator = siteIdOrdersMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry pair = (Map.Entry) iterator.next();
                String siteId = (String) pair.getKey();
                List<OrderMigrationDTO> orders = (List<OrderMigrationDTO>) pair.getValue();

                LOGGER.info("Orders to be spilt for site - " + siteId + " is " + orders.size());

                if (siteId == null) {
                    siteId = "NoWhsite";
                    continue;
                }
                LOGGER.info("Starting the export excel file for Site Id - " + siteId);

                String fileName = getOutFilename(orderProperties, siteId);
                final File parentDir = new File(fileName).getParentFile();
                if (null != parentDir) {
                    parentDir.mkdirs();
                }
                
                SXSSFWorkbook workbook = ExcelOperationUtil.getSXSSFWorkbook();
                SXSSFSheet sheet = (SXSSFSheet) workbook.createSheet();
                cs = workbook.createCellStyle();
                ExcelOperationUtil.setUpHeaderInfo(workbook, sheet, OrderNewTemplateExcelHandler.getColumnList(), sheet.getPhysicalNumberOfRows());

                for (OrderMigrationDTO orderMigrationDTO : orders) {
                    setUpDataInExcel(orderMigrationDTO, workbook, sheet);
                }
                writeToExcel(workbook, fileName);
            }
        } catch (IOException e) {
            LOGGER.info(e.getMessage());
        }
    }

    public static void writeToExcel(SXSSFWorkbook workbook, String fileName) throws IOException {
        FileOutputStream os = new FileOutputStream(fileName);
        workbook.write(os);
        os.flush();
        os.close();
    }
    
    private static void setUpDataInExcel(OrderMigrationDTO orderMigrationDTO, SXSSFWorkbook workbook, SXSSFSheet sheet) {
        setOrderLineToRow(sheet, orderMigrationDTO);
        setReceiveLineToRow(sheet, orderMigrationDTO.getOrderReceivedList());
    }

    public static void setReceiveLineToRow(SXSSFSheet sheet, List<OrderReceivedDTO> orderReceivedList) {
        if (orderReceivedList != null && !orderReceivedList.isEmpty()) {
            for (OrderReceivedDTO orderReceivedDTO : orderReceivedList) {
                int cellIndex = 0;
                SXSSFRow row = ExcelOperationUtil.createRow(sheet, sheet.getPhysicalNumberOfRows());
                cellIndex = createCellForUpdateInExcelCells(sheet, orderReceivedDTO.getItemId(), cellIndex, row, false, true);
                cellIndex = createCellForUpdateInExcelCells(sheet, orderReceivedDTO.getLineType(), cellIndex, row, false, false);
                cellIndex = createCellForUpdateInExcelCells(sheet, orderReceivedDTO.getReceivedQuantityorginal(), RECEIVED_QTY_CELL, row, false, true);
                cellIndex = createCellForUpdateInExcelCells(sheet, orderReceivedDTO.getReceivalDateOriginal(), cellIndex, row, false, false);
                cellIndex = createCellForUpdateInExcelCells(sheet, orderReceivedDTO.getOrderLinePaid(), ORDERLINE_PAID_CELL, row, false, false);
                cellIndex = createCellForUpdateInExcelCells(sheet, orderReceivedDTO.getPackingSlipNumber(), PACKING_SLIP_NUMBER_CELL, row, false, false);
            }
        }
    }

    public static void setOrderLineToRow(SXSSFSheet sheet, OrderMigrationDTO orderMigrationDTO) {
        int cellIndex = 0;
        
        SXSSFRow row = ExcelOperationUtil.createRow(sheet, sheet.getPhysicalNumberOfRows());

        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getItemId(), cellIndex, row, false, true);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getLineType(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getOrderNumber(), cellIndex, row, false, false);
        String orderDateOriginal = orderMigrationDTO.getOrderDateOriginal().replace("-", "/");
        cellIndex = createCellForUpdateInExcelCells(sheet, orderDateOriginal, cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getSuffix(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getPartQualifier(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getPartNumber(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getDescription(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getPartVersion(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getUnitOfMeasure(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getPriceOriginal(), cellIndex, row, false, true);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getUnitOfPrice(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getAmountOriginal(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getCurrency(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getMaterialUseridOriginal(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getPurchasingOrganization(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getBuyerCode(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getBuyerName(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getProject(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getCostCenter(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getGlAccount(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getWbsElement(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getSupplierCode(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getSupplierName(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getInspection(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getInternalFlow(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getIssuerId(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getOrdererId(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getNote(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getRequisitionNumberOriginal(), cellIndex, row, false, true);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getShipArriveDateOriginal(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getAcceptedStaDateOriginal(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getAgreedStaDateOriginal(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getExpectedStaDateOriginal(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getOrderLineStatusOriginal(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getIntExtOrder(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getOrderFromGps(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getOrderLinePaid(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getOrderedQtyoriginal(), cellIndex, row, false, true);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getPossibleToReceiveQuantityOriginal(), cellIndex, row, false, true);
        cellIndex = createCellForUpdateInExcelCells(sheet, "", cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, "", cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getTestObjectsQtyOriginal(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getTestObjectstotalQty(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getPaidQuantityOriginal(), cellIndex, row, false, true);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getPpaId(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getReference(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getSparePartQuantityOriginal(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getIssuerName(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getIssuerEmail(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getContactPersonName(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getContactPersonEmail(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getSupplierPartNo(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getAgreedSTALastUpdatedOriginal(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getModularHarness(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getProcureDateOriginal(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getObjectInformation(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getOrderInformation(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, "", cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getOrderSiteOriginal(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getWarehouseSite(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getTestObjectsPulledOriginal(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getOrderState(), cellIndex, row, false, false);
        cellIndex = createCellForUpdateInExcelCells(sheet, orderMigrationDTO.getOrderType(), cellIndex, row, false, false);
    }

    private static int createCellForUpdateInExcelCells(SXSSFSheet sheet, String values, int cellIndex, SXSSFRow row, boolean wrapText, boolean isNumeric) {
        int cellIndexx = cellIndex++;
        SXSSFCell cell = ExcelOperationUtil.createCell(row, cellIndexx);
        if (isNumeric && !StringUtils.isEmpty(values)) {
            Double value = Double.valueOf(values.replaceAll("[^0-9.]|\\s+", ""));
            cell.setCellValue(value);
            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
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
    
    private static String getOutFilename(Properties testDataProperties, String siteId) throws IOException {
        String outFilename;
        if (((String) testDataProperties.get(ALL_ORDER_DATA_PROPERTY_KEY)).startsWith(NONOPERATIVE_ORDER)) {
            String location = "initDB/_global/data" + (String) testDataProperties.get(ALL_ORDER_DATA_PROPERTY_KEY);
            outFilename =  RESULT_PATH + location + siteId + "/" + (RESULT_FILE).replace("<siteId>",  siteId);
        } else {
            // Find outfile dynamic!
            String location = (String) testDataProperties.get(ALL_ORDER_DATA_PROPERTY_KEY);
            outFilename = location.replace("input", "output") + (RESULT_FILE).replace("<siteId>",  siteId);
        }
        
        return outFilename;
    }
}
