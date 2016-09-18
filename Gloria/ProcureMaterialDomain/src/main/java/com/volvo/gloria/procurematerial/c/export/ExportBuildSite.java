package com.volvo.gloria.procurematerial.c.export;

import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFRow;

import com.volvo.gloria.procurematerial.c.dto.ProcureLineDTO;
import com.volvo.gloria.util.FileToExportDTO;
import com.volvo.gloria.util.excel.ExcelOperationUtil;

/**
 * to support export of 'Build Site' items.
 * 
 */
public final class ExportBuildSite extends ExcelExport {

    private List<ProcureLineDTO> procureLineDTOs = null;

    public ExportBuildSite(List<ProcureLineDTO> procureLineDTOs) {
        this.procureLineDTOs = procureLineDTOs;
    }

    public FileToExportDTO export() {
        return super.export("BuildSite");
    }

    @Override
    public void setUpHeader() {
        getHeaders().add("Project");
        getHeaders().add("Build Series");
        getHeaders().add("Test Object/Single Demand");
        getHeaders().add("Part Affiliation");
        getHeaders().add("Part Number");
        getHeaders().add("Version");
        getHeaders().add("Part Name");
        getHeaders().add("Quantity");
        getHeaders().add("Mail Form Id");
    }

    @Override
    public void setUpData() {
        for (ProcureLineDTO procureLineDTO : procureLineDTOs) {
            int cellIndex = 0;
            XSSFRow row = ExcelOperationUtil.createRow(getSheet(), getSheet().getPhysicalNumberOfRows());
            cellIndex = createCellForUpdateInExcelCells(getSheet(), procureLineDTO.getProjectId(), cellIndex, row, false);
            cellIndex = createCellForUpdateInExcelCells(getSheet(), procureLineDTO.getReferenceGroups(), cellIndex, row, false);
            cellIndex = createCellForUpdateInExcelCells(getSheet(), procureLineDTO.getReferenceIds(), cellIndex, row, false);
            cellIndex = createCellForUpdateInExcelCells(getSheet(), procureLineDTO.getpPartAffiliation(), cellIndex, row, false);
            cellIndex = createCellForUpdateInExcelCells(getSheet(), procureLineDTO.getpPartNumber(), cellIndex, row, false);
            cellIndex = createCellForUpdateInExcelCells(getSheet(), procureLineDTO.getpPartVersion(), cellIndex, row, false);
            cellIndex = createCellForUpdateInExcelCells(getSheet(), procureLineDTO.getpPartName(), cellIndex, row, false);
            cellIndex = createCellForUpdateInExcelCells(getSheet(), procureLineDTO.getUsageQty(), cellIndex, row, false);
            cellIndex = createCellForUpdateInExcelCells(getSheet(), procureLineDTO.getMailFormIds().replace(',', ' '), cellIndex, row, false);
        }
    }
}
