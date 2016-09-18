package com.volvo.gloria.procurematerial.c.export;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.CurrencyUtil;
import com.volvo.gloria.procurematerial.c.DeliveryAddressType;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.RequestGroup;
import com.volvo.gloria.procurematerial.d.entities.RequestList;
import com.volvo.gloria.util.FileToExportDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.excel.ExcelOperationUtil;

/**
 * Export proforma implementation. 
 */
public final class ExportProforma {

    private static final int NUMBER_2 = 2;

    private static final int TOTAL_QTY_COLUMN_IDX = 7;
    
    private ExportProforma() {
    }

    public static FileToExportDTO export(RequestList requestList, String defaultCurrency, CommonServices commonServices)
            throws GloriaApplicationException {
        FileToExportDTO exportDTO = new FileToExportDTO();
        if (requestList != null) {
            List<RequestGroup> requestGroups = requestList.getRequestGroups();
            if (requestGroups != null && !requestGroups.isEmpty()) {

                XSSFWorkbook workbook = ExcelOperationUtil.getXSSFWorkbook();
                XSSFSheet sheet = ExcelOperationUtil.createXSSFSheet(workbook);
                ExcelOperationUtil.setUpHeaderInfo(workbook, sheet, evalShipmentInfo1(requestList), sheet.getPhysicalNumberOfRows());
                ExcelOperationUtil.setUpHeaderInfo(workbook, sheet, shipmentParmaIdForInfo1(requestList), sheet.getPhysicalNumberOfRows());
                ExcelOperationUtil.nextRow(sheet);
                ExcelOperationUtil.setUpHeaderInfo(workbook, sheet, evalShipmentInfo2(requestList), sheet.getPhysicalNumberOfRows());
                ExcelOperationUtil.setUpHeaderInfo(workbook, sheet, shipmentParmaIdForInfo2(requestList), sheet.getPhysicalNumberOfRows());
                ExcelOperationUtil.nextRow(sheet);
                int value = sheet.getPhysicalNumberOfRows();
                ExcelOperationUtil.setUpHeaderInfo(workbook, sheet, evalHeaderForMaterial(), sheet.getPhysicalNumberOfRows());
                for (RequestGroup requestGroup : requestGroups) {
                    List<MaterialLine> materialLines = requestGroup.getMaterialLines();
                    if (materialLines != null && !materialLines.isEmpty()) {
                        setUpDataInExcel(workbook, sheet, materialLines, sheet.getPhysicalNumberOfRows(), defaultCurrency, commonServices);
                    }
                }
                ExcelOperationUtil.nextRow(sheet);
                ExcelOperationUtil.nextRow(sheet);
                ExcelOperationUtil.nextRow(sheet);
                ExcelOperationUtil.nextRow(sheet);
                setUpFooterInfo(workbook, sheet, value + NUMBER_2);
                exportDTO.setContent(ExcelOperationUtil.getExcelBytes(workbook));
                exportDTO.setName("Proforma");
            }
        }
        return exportDTO;
    }

    private static List<String> shipmentParmaIdForInfo2(RequestList requestList) {
        List<String> xclRows = new ArrayList<String>();
        xclRows.add("");
        xclRows.add("");
        xclRows.add("");
        xclRows.add("");
        xclRows.add("");
        xclRows.add("");
        xclRows.add("");
        if (!requestList.getDeliveryAddressType().equals(DeliveryAddressType.NEW_DELIVERY_ADDRESS)) {
            xclRows.add("Parma ID:".concat(handleEmpty(requestList.getDeliveryAddressId())));
        } 
        xclRows.add("");
        return xclRows;
    }

    private static List<String> shipmentParmaIdForInfo1(RequestList requestList) {
        List<String> xclRows = new ArrayList<String>();
        xclRows.add("Parma ID:".concat(handleEmpty(requestList.getWhSiteId())));
        xclRows.add("");
        xclRows.add("");
        xclRows.add("");
        xclRows.add("");
        xclRows.add("");
        xclRows.add("");
        if (!requestList.getDeliveryAddressType().equals(DeliveryAddressType.NEW_DELIVERY_ADDRESS)) {
            xclRows.add("Parma ID:".concat(handleEmpty(requestList.getDeliveryAddressId())));
        } 
        xclRows.add("");
        return xclRows;
    }

    public static List<String> evalShipmentInfo1(RequestList requestList) {
        List<String> xclRows = new ArrayList<String>();
        xclRows.add("Ship From:".concat(handleEmpty(requestList.getWhSiteAddress())));
        xclRows.add("");
        xclRows.add("");
        xclRows.add("");
        xclRows.add("");
        xclRows.add("");
        xclRows.add("");
        if (requestList.getDeliveryAddressType().equals(DeliveryAddressType.NEW_DELIVERY_ADDRESS)) {
            xclRows.add("Bill To:".concat(handleEmpty(requestList.getDeliveryAddressName())));
        } else {
            xclRows.add("Bill To:".concat(handleEmpty(requestList.getDeliveryAddress())));
        }
        xclRows.add("");
        return xclRows;
    }

    public static List<String> evalShipmentInfo2(RequestList requestList) {
        List<String> xclRows = new ArrayList<String>();
        xclRows.add("");
        xclRows.add("");
        xclRows.add("");
        xclRows.add("");
        xclRows.add("");
        xclRows.add("");
        xclRows.add("");
        if (requestList.getDeliveryAddressType().equals(DeliveryAddressType.NEW_DELIVERY_ADDRESS)) {
            xclRows.add("Ship To:".concat(handleEmpty(requestList.getDeliveryAddressName())));
        } else {
            xclRows.add("Ship To:".concat(handleEmpty(requestList.getDeliveryAddress())));
        }
        xclRows.add("");
        return xclRows;
    }

    public static List<String> evalHeaderForMaterial() {
        List<String> xclHeaders = new ArrayList<String>();
        xclHeaders.add("Part Name");
        xclHeaders.add("Part Number");
        xclHeaders.add("Net Weight");
        xclHeaders.add("Qty");
        xclHeaders.add("Unit Price");
        xclHeaders.add("Unit of Price");
        xclHeaders.add("Currency");
        xclHeaders.add("Total Price");
        xclHeaders.add("Customs Code");
        xclHeaders.add("Country of Origin");
        return xclHeaders;
    }

    private static String getCountryOfOrigin(OrderLine orderLine) {
        if (orderLine != null) {
            return handleEmpty(orderLine.getCountryOfOrigin());
        }
        return "";
    }

    private static double getUnitPrice(OrderLine orderLine, String defaultCurrency, CommonServices commonServices)
            throws GloriaApplicationException {
        if (orderLine != null) {
            String actualCurrency = orderLine.getCurrent().getCurrency();
            double unitPrice = orderLine.getCurrent().getUnitPrice();
            return CurrencyUtil.convertCurrencyFromActualToDefault(unitPrice, actualCurrency, defaultCurrency, commonServices);
        }
        return 0;
    }

    private static double getUnitOfPrice(OrderLine orderLine) throws GloriaApplicationException {
        if (orderLine != null) {
            return orderLine.getCurrent().getPerQuantity();
        }
        return 0;
    }
    
    private static String handleEmpty(String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        return value;
    }


    public static XSSFWorkbook setUpDataInExcel(XSSFWorkbook workbook, XSSFSheet sheet, List<MaterialLine> materialLines, int rowIndex, String defaultCurrency,
             CommonServices commonServices) throws GloriaApplicationException {
        for (MaterialLine materialLine : materialLines) {
            ProcureLine procureLine = materialLine.getMaterial().getProcureLine();

            int cellIndex = 0;
            XSSFRow row = ExcelOperationUtil.createRow(sheet, rowIndex++);
            
            
            cellIndex = createCellForUpdateInExcelCells(sheet, procureLine.getpPartName(), cellIndex, row);
            cellIndex = createCellForUpdateInExcelCells(sheet, procureLine.getpPartNumber(), cellIndex, row);
            cellIndex = createCellForUpdateInExcelCells(sheet, "", cellIndex, row);
            cellIndex = createCellForUpdateInExcelCells(sheet, materialLine.getQuantity(), cellIndex, row);
            OrderLine orderLine = materialLine.getMaterial().getOrderLine();
            Double unitPrice = getUnitPrice(orderLine, defaultCurrency, commonServices);
            Double unitOfPrice = getUnitOfPrice(orderLine);
            cellIndex = createCellForUpdateInExcelCells(sheet, unitPrice, cellIndex, row);
            cellIndex = createCellForUpdateInExcelCells(sheet, unitOfPrice, cellIndex, row);
            cellIndex = createCellForUpdateInExcelCells(sheet, defaultCurrency, cellIndex, row);
            //cellIndex = createCellForUpdateInExcelCells(sheet, materialLine.getQuantity() * getUnitPrice(orderLine), cellIndex, row);
            double totalPrice = 0;
            if (unitPrice != null && unitOfPrice != null) {
                if (unitOfPrice != 0) {
                    totalPrice = CurrencyUtil.roundOff(((unitPrice / unitOfPrice) * materialLine.getQuantity()), 2);
                } else {
                    totalPrice = CurrencyUtil.roundOff((unitPrice * materialLine.getQuantity()), 2);
                }
            }
            cellIndex = createCellForUpdateInExcelCells(sheet, totalPrice, cellIndex,
                                                        row);
            cellIndex = createCellForUpdateInExcelCells(sheet, "", cellIndex, row);
            cellIndex = createCellForUpdateInExcelCells(sheet, getCountryOfOrigin(orderLine), cellIndex, row);
            cellIndex = createCellForUpdateInExcelCells(sheet, "", cellIndex, row);

        }
        return workbook;
    }

    private static int createCellForUpdateInExcelCells(XSSFSheet sheet, String values, int cellIndex, XSSFRow row) {
        int cellIndexx = cellIndex++;
        XSSFCell cell = ExcelOperationUtil.createCell(row, cellIndexx);
        cell.setCellValue(handleEmpty(values));
        return cellIndex;
    }

    private static int createCellForUpdateInExcelCells(XSSFSheet sheet, double values, int cellIndex, XSSFRow row) {
        int cellIndexx = cellIndex++;
        XSSFCell cell = ExcelOperationUtil.createCell(row, cellIndexx);
        cell.setCellValue(values);
        return cellIndex;
    }

    public static void setUpFooterInfo(XSSFWorkbook workbook, XSSFSheet sheet, int firstRowIndex) {
        int lastRowIndex = sheet.getLastRowNum() + 1;
        String formula = "SUM(" + ExcelOperationUtil.getColumnName(TOTAL_QTY_COLUMN_IDX) + firstRowIndex + ":"
                + ExcelOperationUtil.getColumnName(TOTAL_QTY_COLUMN_IDX) + (lastRowIndex) + ")";
        XSSFRow row = ExcelOperationUtil.createRow(sheet, lastRowIndex);
        XSSFCell cellForSum = ExcelOperationUtil.createCell(row, TOTAL_QTY_COLUMN_IDX - 1);
        cellForSum.setCellValue("SUM");

        XSSFCell cell = ExcelOperationUtil.createCell(row, TOTAL_QTY_COLUMN_IDX);
        cell.setCellFormula(formula);
        XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
    }

}
