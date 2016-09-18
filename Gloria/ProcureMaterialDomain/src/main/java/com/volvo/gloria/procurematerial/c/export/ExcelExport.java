package com.volvo.gloria.procurematerial.c.export;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.util.FileToExportDTO;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.excel.ExcelOperationUtil;

/**
 * 
 * abstract class with methods to support excel export.
 */
public abstract class ExcelExport {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelExport.class);

    private static final int DEFAULT_COLUMN_WIDTH = 30;

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private CellStyle cellStyle;

    private List<String> headers = new ArrayList<String>();

    public ExcelExport() {
        // TODO Auto-generated constructor stub
    }

    public FileToExportDTO export(String name) {
        // initialize excel
        workbook = ExcelOperationUtil.getXSSFWorkbook();
        sheet = ExcelOperationUtil.createXSSFSheet(workbook);
        cellStyle = workbook.createCellStyle();

        // write data to excel
        write();

        // update the DTO with excel content
        FileToExportDTO exportDTO = new FileToExportDTO();
        exportDTO.setContent(getExcelBytes());
        exportDTO.setName(name);

        return exportDTO;
    }

    public abstract void setUpHeader();

    public abstract void setUpData();

    protected int createCellForUpdateInExcelCells(XSSFSheet sheet, double values, int cellIndex, XSSFRow row, boolean wrapText) {
        int cellIndexx = cellIndex++;
        XSSFCell cell = ExcelOperationUtil.createCell(row, cellIndexx);
        cell.setCellValue(values);
        if (wrapText) {
            cellStyle.setWrapText(true);
            cell.setCellStyle(cellStyle);
        }
        return cellIndex;
    }

    protected int createCellForUpdateInExcelCells(XSSFSheet sheet, String values, int cellIndex, XSSFRow row, boolean wrapText) {
        int cellIndexx = cellIndex++;
        XSSFCell cell = ExcelOperationUtil.createCell(row, cellIndexx);
        cell.setCellValue(handleEmpty(values));
        if (wrapText) {
            cellStyle.setWrapText(true);
            cell.setCellStyle(cellStyle);
        }
        return cellIndex;
    }

    protected String handleEmpty(String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        return value;
    }

    protected XSSFWorkbook getWorkbook() {
        return workbook;
    }

    protected XSSFSheet getSheet() {
        return sheet;
    }

    protected List<String> getHeaders() {
        return headers;
    }

    private void write() {
        // write header cells
        setUpHeader();
        ExcelOperationUtil.setUpHeaderInfo(workbook, sheet, headers, sheet.getPhysicalNumberOfRows());

        // write contents
        setUpData();

        // resize cells
        sheet.setDefaultColumnWidth(DEFAULT_COLUMN_WIDTH);
    }

    private byte[] getExcelBytes() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
        } catch (IOException e) {
            throw new GloriaSystemException(e, "Failed to export data to excel - " + this.getClass().getName());
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                LOGGER.error("Failed to export data to excel.");
            }
        }
        return bos.toByteArray();
    }
}
