package com.volvo.gloria.materialrequest.c;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.materialrequest.c.dto.MaterialRequestLineDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.c.GloriaExceptionConstants;
import com.volvo.gloria.util.excel.ExcelColumnHeader;

public class MaterialImportHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MaterialImportHandler.class);

    private InputStream is = null;
    private XSSFWorkbook workbook = null;
    private XSSFSheet sheet = null;

    public MaterialImportHandler(InputStream is) {
        this.is = is;
    }

    public List<MaterialRequestLineDTO> manageExcel() throws GloriaApplicationException {
        try {
            // Create Workbook instance holding reference to .xlsx file
            workbook = new XSSFWorkbook(is);
            // Get first/desired sheet from the workbook
            sheet = workbook.getSheetAt(0);
            // read rows/cells values
            return doRead();
        } catch (GloriaApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new GloriaSystemException(e, "Failed to load the excel file");
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                LOGGER.error("An error occured while managing excel " + e.getMessage());
            }
        }
    }

    protected List<MaterialRequestLineDTO> doRead() throws GloriaApplicationException {
        List<MaterialRequestLineDTO> resultsDTO = new ArrayList<MaterialRequestLineDTO>();
        if (getSheet().getPhysicalNumberOfRows() > 0) {
            try {

                Row firstRow = getSheet().getRow(0);
                for (int rNo = 1; rNo < getSheet().getPhysicalNumberOfRows(); rNo++) {
                    XSSFRow currentRow = getSheet().getRow(rNo);
                    MaterialRequestLineDTO requestLineDTO = new MaterialRequestLineDTO();
                    for (int cNo = currentRow.getFirstCellNum(); cNo < firstRow.getPhysicalNumberOfCells(); cNo++) {
                        XSSFCell cellPartAffiliation = currentRow.getCell(MaterialInputExcel.PARTAFFILIATION.sequenceNo());
                        requestLineDTO.setPartAffiliation(evaluateCell(cellPartAffiliation, String.class).toString());

                        XSSFCell cellPartNumber = currentRow.getCell(MaterialInputExcel.PARTNO.sequenceNo());
                        String partNumber = evaluateCell(cellPartNumber, String.class).toString();
                        if (StringUtils.isEmpty(partNumber)) {
                            return resultsDTO;
                        } else {
                            requestLineDTO.setPartNumber(partNumber);
                        }

                        XSSFCell cellPartVersion = currentRow.getCell(MaterialInputExcel.VERSION.sequenceNo());
                        requestLineDTO.setPartVersion(evaluateCell(cellPartVersion, String.class).toString());

                        XSSFCell cellPartName = currentRow.getCell(MaterialInputExcel.PARTNAME.sequenceNo());
                        requestLineDTO.setPartName(evaluateCell(cellPartName, String.class).toString());

                        XSSFCell cellPartModification = currentRow.getCell(MaterialInputExcel.PARTMODIFICATION.sequenceNo());
                        requestLineDTO.setPartModification(evaluateCell(cellPartModification, String.class).toString());

                        try {
                            XSSFCell cellQuantity = currentRow.getCell(MaterialInputExcel.QUANTITY.sequenceNo());
                            requestLineDTO.setQuantity((Long) evaluateCell(cellQuantity, Long.class));
                        } catch (ClassCastException classCastException) {
                            requestLineDTO.setQuantity(0L);
                        }

                        XSSFCell cellUnitOfMeasure = currentRow.getCell(MaterialInputExcel.UNITOFMEASURE.sequenceNo());
                        requestLineDTO.setUnitOfMeasure(evaluateCell(cellUnitOfMeasure, String.class).toString());

                        XSSFCell cellFunctionGroup = currentRow.getCell(MaterialInputExcel.FUNCTIONGROUP.sequenceNo());
                        requestLineDTO.setFunctionGroup(evaluateCell(cellFunctionGroup, String.class).toString());
                    }
                    resultsDTO.add(requestLineDTO);
                }
            } catch (Exception e) {
                throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_EXCEL_DATA,
                                                     "The data in excel is not valid. Please upload an excel with valid data", null);
            }
        }
        return resultsDTO;
    }

    @SuppressWarnings("rawtypes")
    protected Object evaluateCell(Cell cell, Class type) {
        if (cell != null) {
            switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return DateUtil.getJavaDate(cell.getNumericCellValue());
                } else {
                    return handleNumeric(cell.getNumericCellValue(), type);
                }
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue().trim();
            case Cell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue();
            case Cell.CELL_TYPE_BLANK:
                return "";
            default:
                throw new GloriaSystemException("EXCEL_ERROR", "what is this??" + cell.getCellType());
            }
        }
        return "";
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected <I> Object handleNumeric(I value, Class type) {
        String valueAsString = String.valueOf(value);
        if (type.isAssignableFrom(Float.class)) {
            return Float.parseFloat(valueAsString);
        } else if (type.isAssignableFrom(Double.class)) {
            return Double.parseDouble(valueAsString);
        } else if (type.isAssignableFrom(Long.class)) {
            return Double.valueOf(valueAsString).longValue();
        }
        return Double.valueOf(valueAsString).longValue();
    }

    public InputStream getIs() {
        return is;
    }

    public XSSFSheet getSheet() {
        return sheet;
    }

    public XSSFWorkbook getWorkbook() {
        return workbook;
    }

    enum MaterialInputExcel implements ExcelColumnHeader {

        PARTAFFILIATION(0, "Part Affiliation"), 
        PARTNO(1, "Part No"), 
        VERSION(2, "Version"), 
        PARTNAME(3, "Part Name"), 
        PARTMODIFICATION(4, "Part Modification"), 
        QUANTITY(5, "Qty"), 
        UNITOFMEASURE(6, "UoM"), 
        FUNCTIONGROUP(7, "Function Group");

        private int sequenceNo;
        private String columnName;

        MaterialInputExcel(int sequenceNo, String columnName) {
            this.sequenceNo = sequenceNo;
            this.columnName = columnName;
        }

        public int sequenceNo() {
            return sequenceNo;
        }

        public String columnName() {
            return columnName;
        }
    }
}
