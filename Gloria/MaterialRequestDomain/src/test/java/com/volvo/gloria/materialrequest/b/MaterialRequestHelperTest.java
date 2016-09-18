package com.volvo.gloria.materialrequest.b;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

public class MaterialRequestHelperTest {

    @Test
    public void testWriteRowCells() {
        //writeRowCells(List<Object> cellContents, XSSFRow row)
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();
        XSSFRow row = sheet.createRow(0);
        List<Object> cellContents = new ArrayList<Object>();
        cellContents.add(new String("abc"));
        cellContents.add(new Long("1"));        
        MaterialRequestHelper.writeRowCells(cellContents, row);
        assertEquals(row.getPhysicalNumberOfCells(), 2);
        assertEquals(row.getCell(0).getCellType(),XSSFCell.CELL_TYPE_STRING);
        assertEquals(row.getCell(1).getCellType(),XSSFCell.CELL_TYPE_NUMERIC);
        assertEquals(row.getCell(0).getStringCellValue(),"abc");
        assertEquals(row.getCell(1).getNumericCellValue(),1,0);              
    }

}
