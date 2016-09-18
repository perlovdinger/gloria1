package com.volvo.gloria.reports.c;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.FileToExportDTO;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.c.dto.reports.ReportColumn;
import com.volvo.gloria.util.c.dto.reports.ReportRow;
import com.volvo.gloria.util.excel.ExcelOperationUtil;

/**
 * Excel utility for reports.
 * 
 */
public final class ExportReport {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExportReport.class);
    
    protected ExportReport() {

    }

    public static FileToExportDTO export(List<ReportRow> reportRows, String fileName) {
        FileToExportDTO fileToExportDTO = new FileToExportDTO();

        SXSSFWorkbook workbook = ExcelOperationUtil.getSXSSFWorkbook();
        SXSSFSheet sheet = (SXSSFSheet) workbook.createSheet();
        
        
        // default cell style
        CellStyle defaultCellStyle = workbook.createCellStyle();
        defaultCellStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);   
        
        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(workbook.createDataFormat().getFormat(DateUtil.getGloriaDateformat()));
        
        // multi line cell style
        CellStyle multilineCellStyle = workbook.createCellStyle();
        multilineCellStyle.setWrapText(true);
        multilineCellStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);   
        
        if (reportRows != null && !reportRows.isEmpty()) {
            // add column names
            int headerCellIndex = 0;
            SXSSFRow headerRow = ExcelOperationUtil.createRow(sheet, sheet.getPhysicalNumberOfRows());
            for (ReportColumn reportColumn : reportRows.get(0).getReportColumns()) {
                headerCellIndex = createCell(reportColumn.getName(), headerCellIndex, headerRow, null);                
            }

            // add column values
            for (ReportRow reportRow : reportRows) {
                int cellIndex = 0;
                SXSSFRow row = ExcelOperationUtil.createRow(sheet, sheet.getPhysicalNumberOfRows());
                for (ReportColumn reportColumn : reportRow.getReportColumns()) {
                    Object cellValue = reportColumn.getValue();
                   
                    if (cellValue != null && cellValue instanceof Collection<?>) {
                        cellIndex = createCell(StringUtils.join((Collection<?>) cellValue, "\n"), cellIndex, row, multilineCellStyle);
                    } else if (cellValue != null && cellValue instanceof java.util.Date) {
                        cellIndex = createCell(cellValue, cellIndex, row, dateCellStyle);
                    } else {
                        cellIndex = createCell(cellValue, cellIndex, row, defaultCellStyle);
                    }
                }
            }
        }
        fileToExportDTO.setContent(getExcelBytes(workbook));
        fileToExportDTO.setName(fileName);

        return fileToExportDTO;
    }
    
    private static int createCell(Object value, int cellIndex, SXSSFRow row, CellStyle style) {
        int currentIndex = cellIndex;
        SXSSFCell cell = ExcelOperationUtil.createCell(row, cellIndex);

        cell.setCellValue(handleEmpty(value));
        if (value instanceof Date) {
            cell.setCellValue(DateUtil.getDateWithoutTimeAsString((Date) value));
        }

        if (value instanceof java.lang.Number) {
            if (value instanceof java.lang.Double) {
                cell.setCellValue((Double) value);
                cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
            }
            if (value instanceof java.lang.Long) {
                cell.setCellValue((Long) value);
                cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
            }
        }

        if (style != null) {
            cell.setCellStyle(style);
        }
        return currentIndex + 1;
    }

    private static byte[] getExcelBytes(SXSSFWorkbook workbook) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
        } catch (IOException e) {
            throw new GloriaSystemException(e, "Failed to export data to excel - REPORT.");
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                LOGGER.error("Failed to export data to excel - REPORT.");
            }
        }
        return bos.toByteArray();
    }

    private static String handleEmpty(Object value) {
        if (value == null || String.valueOf(value).isEmpty()) {
            return "";
        }
        return String.valueOf(value);
    }
}
