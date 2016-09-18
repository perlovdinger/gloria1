package com.volvo.gloria.materialrequest.c;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;

import com.volvo.gloria.materialrequest.c.dto.MaterialRequestLineDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.c.GloriaExceptionConstants;
import com.volvo.gloria.util.excel.ExcelColumnHeader;

public class MultipleMaterialImportHandler extends MaterialImportHandler {

    private static final String UNIT_OF_MEASURE = "PCE";
   
    public MultipleMaterialImportHandler(InputStream is) {
        super(is);
    }

    @Override
    protected List<MaterialRequestLineDTO> doRead() throws GloriaApplicationException {
        List<MaterialRequestLineDTO> resultsDTO = new ArrayList<MaterialRequestLineDTO>();
        if (getSheet().getPhysicalNumberOfRows() > 0) {
            try {
                Row firstRow = getSheet().getRow(0);
                for (int cNo = MaterialInputExcel.values().length; cNo < firstRow.getPhysicalNumberOfCells(); cNo++) {
                    for (int rNo = 1; rNo < getSheet().getPhysicalNumberOfRows(); rNo++) {
                        XSSFRow currentRow = getSheet().getRow(rNo);
                        Cell currentCell = currentRow.getCell(cNo);
                        if (currentCell != null && currentCell.getCellType() != Cell.CELL_TYPE_BLANK) {
                            MaterialRequestLineDTO requestLineDTO = new MaterialRequestLineDTO();
                            
                            XSSFCell cellPartNumber = currentRow.getCell(MaterialInputExcel.PARTNO.sequenceNo());
                            requestLineDTO.setPartNumber(evaluateCell(cellPartNumber, String.class).toString());

                            XSSFCell cellPartVersion = currentRow.getCell(MaterialInputExcel.VERSION.sequenceNo());
                            requestLineDTO.setPartVersion(evaluateCell(cellPartVersion, String.class).toString());
                            
                            XSSFCell cellPartAffiliation = currentRow.getCell(MaterialInputExcel.PART_AFFILIATION.sequenceNo());
                            requestLineDTO.setPartAffiliation(evaluateCell(cellPartAffiliation, String.class).toString());

                            XSSFCell cellPartName = currentRow.getCell(MaterialInputExcel.PARTNAME.sequenceNo());
                            requestLineDTO.setPartName(evaluateCell(cellPartName, String.class).toString());

                            requestLineDTO.setUnitOfMeasure(UNIT_OF_MEASURE);

                            requestLineDTO.setQuantity((Long) evaluateCell(currentRow.getCell(cNo), Long.class));
                            requestLineDTO.setName(evaluateCell(firstRow.getCell(cNo), String.class).toString());
                            resultsDTO.add(requestLineDTO);
                        }
                    }
                }

            } catch (Exception e) {
                throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_EXCEL_DATA,
                                                     "The data in excel is not valid. Please upload an excel with valid data", null);
            }
        }
        return resultsDTO;
    }

    enum MaterialInputExcel implements ExcelColumnHeader {

        PARTNO(0, "Part No"), VERSION(1, "Version"), PART_AFFILIATION(2, "Part Affiliation"), PARTNAME(3, "Part name");
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
