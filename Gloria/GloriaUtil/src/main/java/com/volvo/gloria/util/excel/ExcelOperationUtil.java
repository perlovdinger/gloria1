package com.volvo.gloria.util.excel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.volvo.gloria.util.GloriaSystemException;

/**
 * Excel export utility.
 */
public final class ExcelOperationUtil {
    
    private ExcelOperationUtil() {

    }
    
    public static XSSFWorkbook getXSSFWorkbook() {
        return new XSSFWorkbook();
    }

    public static XSSFSheet createXSSFSheet(XSSFWorkbook workbook) {
        return workbook.createSheet();
    }

    public static XSSFRow createRow(XSSFSheet sheet, int index) {
        return sheet.createRow(index);
    }
    
    public static XSSFCell createCell(XSSFRow row, int index) {
        return row.createCell(index);
    }
    
    public static void setUpHeaderInfo(XSSFWorkbook workbook, XSSFSheet sheet,
            List<String> headers, int rowIndex) {
        XSSFRow row = ExcelOperationUtil.createRow(sheet, rowIndex);
        int cellIndex = 0;

        // Simple style
        XSSFCellStyle style = workbook.createCellStyle();

        // Bold Fond
        XSSFFont bold = workbook.createFont();
        bold.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
        style.setFont(bold);

        for (String header : headers) {
            XSSFCell cell = ExcelOperationUtil.createCell(row, cellIndex);
            cell.setCellValue(header);
            cell.setCellStyle(style);            
            cellIndex++;
        }        
    }
    
    public static String getColumnName(int columnNumber) {
        String columnName = "";
        int dividend = columnNumber + 1;
        int modulus;
        final int asciiA = 65;
        final int alphabetsCount = 26;
        while (dividend > 0) {           
            modulus = (dividend - 1) % alphabetsCount;
            columnName = (char) (asciiA + modulus) + columnName;
            dividend = (int) ((dividend - modulus) / alphabetsCount);
        }
        return columnName;
    }
    
    public static SXSSFWorkbook getSXSSFWorkbook() {
        return new SXSSFWorkbook();
    }
    
    public static SXSSFRow createRow(SXSSFSheet sheet, int rowIndex) {
        return (SXSSFRow) sheet.createRow(rowIndex);
    }

    public static SXSSFCell createCell(SXSSFRow row, int cellIndex) {
        return (SXSSFCell) row.createCell(cellIndex);
    }

    public static void setUpHeaderInfo(SXSSFWorkbook workbook, SXSSFSheet sheet, List<String> headers, int rowIndex) {
        SXSSFRow row = ExcelOperationUtil.createRow(sheet, rowIndex);
        int cellIndex = 0;

        // Simple style
        CellStyle  style = workbook.createCellStyle();

        // Bold Fond
        Font bold = workbook.createFont();
        bold.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style.setFont(bold);

        for (String header : headers) {
            SXSSFCell cell = ExcelOperationUtil.createCell(row, cellIndex);
            cell.setCellValue(header);
            cell.setCellStyle(style);            
            cellIndex++;
        }        
    }
  
    public static int createCellForUpdateInExcelCells(XSSFSheet sheet, String values, int cellIndex, XSSFRow row) {
        int cellIndexx = cellIndex++;
        XSSFCell cell = ExcelOperationUtil.createCell(row, cellIndexx);
        cell.setCellValue(handleEmpty(values));
        return cellIndex;
    }

    private static String handleEmpty(String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        return value;
    }

    public static byte[] getExcelBytes(XSSFWorkbook workbook) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
        } catch (IOException e) {
            throw new GloriaSystemException(e, "Failed to export data to excel.");
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                throw new GloriaSystemException(e, "Failed to export data to excel.");
            }
        }
        return bos.toByteArray();
    }

    public static XSSFRow nextRow(XSSFSheet sheet) {
        return sheet.createRow(sheet.getPhysicalNumberOfRows());
    }
}
