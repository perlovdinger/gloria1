package com.volvo.gloria.procurematerial.c.export;

import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFRow;

import com.volvo.gloria.procurematerial.c.dto.ProcureLineDTO;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.FileToExportDTO;
import com.volvo.gloria.util.excel.ExcelOperationUtil;



public final class ExportToProcure extends ExcelExport{
    
    private List<ProcureLineDTO> procureLineDTOs = null;
    
    public ExportToProcure(List<ProcureLineDTO> procureLineDTOs) {
        this.procureLineDTOs = procureLineDTOs;
        
    }
    
    public FileToExportDTO export() {
        return super.export("To Procure");
    }
    
    @Override
    public void setUpHeader() {
        getHeaders().add("Project");
        getHeaders().add("Build Series");
        getHeaders().add("Test Object");
        getHeaders().add("Proc. Part Number");
        getHeaders().add("Proc. Part Version");
        getHeaders().add("Proc. Part Name");
        getHeaders().add("Proc. Part Modification");
        getHeaders().add("Proc. Quantity");
        getHeaders().add("Modular Harness");
        getHeaders().add("MTR ID");
        getHeaders().add("Mail Form Id");
        getHeaders().add("Required STA");
        getHeaders().add("Source");
    }

    @Override
    public void setUpData() {
        for (ProcureLineDTO procureLineDTO : procureLineDTOs) {
            int cellIndex = 0;
            XSSFRow row = ExcelOperationUtil.createRow(getSheet(), getSheet().getPhysicalNumberOfRows());
            cellIndex = createCellForUpdateInExcelCells(getSheet(), procureLineDTO.getProjectId(), cellIndex, row, false);
            cellIndex = createCellForUpdateInExcelCells(getSheet(), procureLineDTO.getReferenceGroups(), cellIndex, row, false);
            cellIndex = createCellForUpdateInExcelCells(getSheet(), procureLineDTO.getReferenceIds(), cellIndex, row, false);
            cellIndex = createCellForUpdateInExcelCells(getSheet(), procureLineDTO.getpPartNumber(), cellIndex, row, false);
            cellIndex = createCellForUpdateInExcelCells(getSheet(), procureLineDTO.getpPartVersion(), cellIndex, row, false); 
            cellIndex = createCellForUpdateInExcelCells(getSheet(), procureLineDTO.getpPartName(), cellIndex, row, false);
            cellIndex = createCellForUpdateInExcelCells(getSheet(), procureLineDTO.getpPartModification(), cellIndex, row, false);
            cellIndex = createCellForUpdateInExcelCells(getSheet(), procureLineDTO.getUsageQty(), cellIndex, row, false);
            cellIndex = createCellForUpdateInExcelCells(getSheet(), procureLineDTO.getModularHarness(), cellIndex, row, false);
            cellIndex = createCellForUpdateInExcelCells(getSheet(), procureLineDTO.getChangeRequestIds(), cellIndex, row, false);
            cellIndex = createCellForUpdateInExcelCells(getSheet(), procureLineDTO.getMailFormIds(), cellIndex, row, false);
            String requiredStaDate = "";
            if (procureLineDTO.getRequiredStaDate() != null) {
                requiredStaDate = DateUtil.getDateWithoutTimeAsString(procureLineDTO.getRequiredStaDate());
            }
            cellIndex = createCellForUpdateInExcelCells(getSheet(), requiredStaDate, cellIndex, row, false);
            cellIndex = createCellForUpdateInExcelCells(getSheet(), procureLineDTO.getProcureType(), cellIndex, row, false);
        }
    }
}
