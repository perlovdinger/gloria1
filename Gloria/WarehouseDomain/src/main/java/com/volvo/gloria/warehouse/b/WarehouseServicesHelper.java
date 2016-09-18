package com.volvo.gloria.warehouse.b;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.d.entities.Site;
import com.volvo.gloria.util.FileToExportDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.c.GloriaExceptionConstants;
import com.volvo.gloria.util.excel.ExcelOperationUtil;
import com.volvo.gloria.warehouse.c.Deviation;
import com.volvo.gloria.warehouse.c.Setup;
import com.volvo.gloria.warehouse.c.dto.AisleRackRowDTO;
import com.volvo.gloria.warehouse.c.dto.BaySettingDTO;
import com.volvo.gloria.warehouse.c.dto.BinLocationDTO;
import com.volvo.gloria.warehouse.c.dto.BinlocationBalanceDTO;
import com.volvo.gloria.warehouse.c.dto.QualityInspectionPartDTO;
import com.volvo.gloria.warehouse.c.dto.QualityInspectionProjectDTO;
import com.volvo.gloria.warehouse.c.dto.QualityInspectionSupplierDTO;
import com.volvo.gloria.warehouse.c.dto.StorageRoomDTO;
import com.volvo.gloria.warehouse.c.dto.WarehouseDTO;
import com.volvo.gloria.warehouse.c.dto.ZoneDTO;
import com.volvo.gloria.warehouse.d.entities.AisleRackRow;
import com.volvo.gloria.warehouse.d.entities.BaySetting;
import com.volvo.gloria.warehouse.d.entities.BinLocation;
import com.volvo.gloria.warehouse.d.entities.BinlocationBalance;
import com.volvo.gloria.warehouse.d.entities.QualityInspectionPart;
import com.volvo.gloria.warehouse.d.entities.QualityInspectionProject;
import com.volvo.gloria.warehouse.d.entities.QualityInspectionSupplier;
import com.volvo.gloria.warehouse.d.entities.StorageRoom;
import com.volvo.gloria.warehouse.d.entities.Warehouse;
import com.volvo.gloria.warehouse.d.entities.Zone;
import com.volvo.gloria.warehouse.repositories.b.AisleRepository;

/**
 * Helper class for Warehouse related entities.
 * 
 */
public class WarehouseServicesHelper {

    protected WarehouseServicesHelper() {

    }

    public static List<StorageRoomDTO> transformAsStorageRoomDTOs(List<StorageRoom> storageRooms) {
        List<StorageRoomDTO> storageRoomDTOs = new ArrayList<StorageRoomDTO>();
        if (storageRooms != null && !storageRooms.isEmpty()) {
            for (StorageRoom storageRoom : storageRooms) {
                storageRoomDTOs.add(transformAsDTO(storageRoom));
            }
        }
        return storageRoomDTOs;
    }

    public static StorageRoomDTO transformAsDTO(StorageRoom storageRoom) {
        StorageRoomDTO storageRoomDTO = new StorageRoomDTO();
        if (storageRoom != null) {
            storageRoomDTO.setId(storageRoom.getStorageRoomOid());
            storageRoomDTO.setVersion(storageRoom.getVersion());
            storageRoomDTO.setCode(storageRoom.getCode());
            storageRoomDTO.setName(storageRoom.getName());
            storageRoomDTO.setDescription(storageRoom.getDescription());
        }
        return storageRoomDTO;
    }

    public static List<ZoneDTO> transformAsZoneDTOs(List<Zone> zones) {
        List<ZoneDTO> zoneDTOs = new ArrayList<ZoneDTO>();
        if (zones != null && !zones.isEmpty()) {
            for (Zone zone : zones) {
                zoneDTOs.add(transformAsDTO(zone));
            }
        }
        return zoneDTOs;
    }

    public static ZoneDTO transformAsDTO(Zone zone) {
        ZoneDTO zoneDTO = new ZoneDTO();
        if (zone != null) {
            zoneDTO.setVersion(zone.getVersion());
            zoneDTO.setId(zone.getZoneOid());
            zoneDTO.setCode(zone.getCode());
            zoneDTO.setDescription(zone.getDescription());
            if (zone.getType() != null) {
                zoneDTO.setType(zone.getType().name());
            }
            zoneDTO.setName(zone.getName());
            if (zone.getAisleRackRows() != null && !zone.getAisleRackRows().isEmpty()) {
                zoneDTO.setNumberOfAisleRows(zone.getAisleRackRows().size());
            }
        }
        return zoneDTO;
    }

    public static List<AisleRackRowDTO> transformAsAisleDTOs(List<AisleRackRow> aisleRackRows) {
        List<AisleRackRowDTO> aisleRackRowDTOs = new ArrayList<AisleRackRowDTO>();
        if (aisleRackRows != null && !aisleRackRows.isEmpty()) {
            for (AisleRackRow aisleRackRow : aisleRackRows) {
                aisleRackRowDTOs.add(transformAsDTO(aisleRackRow));
            }
        }
        return aisleRackRowDTOs;
    }

    public static AisleRackRowDTO transformAsDTO(AisleRackRow aisleRackRow) {
        AisleRackRowDTO aisleRackRowDTO = new AisleRackRowDTO();
        if (aisleRackRow != null) {
            aisleRackRowDTO.setId(aisleRackRow.getAisleRackRowOid());
            aisleRackRowDTO.setCode(aisleRackRow.getCode());
            if (aisleRackRow.getSetUp() != null) {
                aisleRackRowDTO.setSetUp(aisleRackRow.getSetUp().name());
            }
            if (aisleRackRow.getBaySides() != null) {
                aisleRackRowDTO.setBaySides(aisleRackRow.getBaySides().name());
            }
            aisleRackRowDTO.setVersion(aisleRackRow.getVersion());
            aisleRackRowDTO.setNumberOfBays(aisleRackRow.getNumberOfBay());
        }
        return aisleRackRowDTO;
    }

    public static List<BinLocationDTO> transformAsBinlocationDTOs(List<BinLocation> binLocations) {
        List<BinLocationDTO> binLocationDTOs = new ArrayList<BinLocationDTO>();
        if (binLocations != null && !binLocations.isEmpty()) {
            for (BinLocation binLocation : binLocations) {
                binLocationDTOs.add(transformAsDTO(binLocation));
            }
        }
        return binLocationDTOs;
    }

    public static BinLocationDTO transformAsDTO(BinLocation binLocation) {
        BinLocationDTO binLocationDTO = new BinLocationDTO();
        if (binLocation != null) {
            binLocationDTO.setVersion(binLocation.getVersion());
            binLocationDTO.setId(binLocation.getBinLocationOid());
            binLocationDTO.setCode(binLocation.getCode());
            binLocationDTO.setAisleRackRowCode(binLocation.getAisleRackRowCode());
            binLocationDTO.setBay(binLocation.getBayCode());
            binLocationDTO.setLevel(binLocation.getLevel());
            binLocationDTO.setPosition(binLocation.getPosition());
        }
        return binLocationDTO;
    }

    public static WarehouseDTO transformAsDTO(Warehouse warehouse, Site site) {
        WarehouseDTO warehouseDTO = new WarehouseDTO();
        warehouseDTO.setId(warehouse.getWarehouseOid());
        warehouseDTO.setVersion(warehouse.getVersion());
        warehouseDTO.setSiteId(site.getSiteId());
        warehouseDTO.setSiteCode(site.getSiteCode());
        warehouseDTO.setSiteName(site.getSiteName());
        warehouseDTO.setCountryCode(site.getCountryCode());
        warehouseDTO.setCompanyCode(site.getCompanyCode());
        warehouseDTO.setAddress(site.getAddress());
        warehouseDTO.setQiSupported(warehouse.isQiSupported());
        Setup setUp = warehouse.getSetUp();
        if (setUp != null) {
            warehouseDTO.setSetUp(setUp.name());
        }
        return warehouseDTO;
    }

    public static List<BaySettingDTO> transformAsBaySettingDTOs(List<BaySetting> baySettings) {
        List<BaySettingDTO> baySettingDTOs = new ArrayList<BaySettingDTO>();
        if (baySettings != null && !baySettings.isEmpty()) {
            for (BaySetting baySetting : baySettings) {
                baySettingDTOs.add(transformAsDTO(baySetting));
            }
        }
        return baySettingDTOs;
    }

    public static BaySettingDTO transformAsDTO(BaySetting baySetting) {
        BaySettingDTO baySettingDTO = new BaySettingDTO();
        if (baySetting != null) {
            baySettingDTO.setId(baySetting.getBaySettingOid());
            baySettingDTO.setVersion(baySetting.getVersion());
            baySettingDTO.setBayCode(baySetting.getBayCode());
            baySettingDTO.setNumberOfLevels(baySetting.getNumberOfLevels());
            baySettingDTO.setNumberOfPositions(baySetting.getNumberOfPositions());
        }
        return baySettingDTO;
    }

    public static List<QualityInspectionPartDTO> transformAsQualityInspectionPartDTOs(List<QualityInspectionPart> qualityInspectionParts) {
        List<QualityInspectionPartDTO> qualityInspectionPartDTOs = new ArrayList<QualityInspectionPartDTO>();
        if (qualityInspectionParts != null && !qualityInspectionParts.isEmpty()) {
            for (QualityInspectionPart qualityInspectionPart : qualityInspectionParts) {
                qualityInspectionPartDTOs.add(transformAsDTO(qualityInspectionPart));
            }
        }
        return qualityInspectionPartDTOs;
    }

    public static QualityInspectionPartDTO transformAsDTO(QualityInspectionPart qualityInspectionPart) {
        QualityInspectionPartDTO qualityInspectionPartDTO = new QualityInspectionPartDTO();
        if (qualityInspectionPart != null) {
            qualityInspectionPartDTO.setId(qualityInspectionPart.getQualityInspectionPartOId());
            qualityInspectionPartDTO.setVersion(qualityInspectionPart.getVersion());
            qualityInspectionPartDTO.setPartName(qualityInspectionPart.getPartName());
            qualityInspectionPartDTO.setPartNumber(qualityInspectionPart.getPartNumber());
            qualityInspectionPartDTO.setMandatory(qualityInspectionPart.isMandatory());
        }
        return qualityInspectionPartDTO;
    }

    public static List<QualityInspectionProjectDTO> transformAsQualityInspectionProjectDTOs(List<QualityInspectionProject> qualityInspectionProjects) {
        List<QualityInspectionProjectDTO> qualityInspectionProjectDTOs = new ArrayList<QualityInspectionProjectDTO>();
        if (qualityInspectionProjects != null && !qualityInspectionProjects.isEmpty()) {
            for (QualityInspectionProject qualityInspectionProject : qualityInspectionProjects) {
                qualityInspectionProjectDTOs.add(transformAsDTO(qualityInspectionProject));
            }
        }
        return qualityInspectionProjectDTOs;
    }

    public static QualityInspectionProjectDTO transformAsDTO(QualityInspectionProject qualityInspectionProject) {
        QualityInspectionProjectDTO qualityInspectionProjectDTO = new QualityInspectionProjectDTO();
        if (qualityInspectionProject != null) {
            qualityInspectionProjectDTO.setId(qualityInspectionProject.getQualityInspectionProjectOId());
            qualityInspectionProjectDTO.setVersion(qualityInspectionProject.getVersion());
            qualityInspectionProjectDTO.setProject(qualityInspectionProject.getProject());
            qualityInspectionProjectDTO.setMandatory(qualityInspectionProject.isMandatory());
        }
        return qualityInspectionProjectDTO;
    }

    public static List<QualityInspectionSupplierDTO> transformAsQualityInspectionSupplierDTOs(List<QualityInspectionSupplier> qualityInspectionSuppliers) {
        List<QualityInspectionSupplierDTO> qualityInspectionSupplierDTOs = new ArrayList<QualityInspectionSupplierDTO>();
        if (qualityInspectionSuppliers != null && !qualityInspectionSuppliers.isEmpty()) {
            for (QualityInspectionSupplier qualityInspectionSupplier : qualityInspectionSuppliers) {
                qualityInspectionSupplierDTOs.add(transformAsDTO(qualityInspectionSupplier));
            }
        }
        return qualityInspectionSupplierDTOs;
    }

    public static QualityInspectionSupplierDTO transformAsDTO(QualityInspectionSupplier qualityInspectionSupplier) {
        QualityInspectionSupplierDTO qualityInspectionSupplierDTO = new QualityInspectionSupplierDTO();
        if (qualityInspectionSupplier != null) {
            qualityInspectionSupplierDTO.setId(qualityInspectionSupplier.getQualityInspectionSupplierOId());
            qualityInspectionSupplierDTO.setVersion(qualityInspectionSupplier.getVersion());
            qualityInspectionSupplierDTO.setSupplier(qualityInspectionSupplier.getSupplier());
            qualityInspectionSupplierDTO.setMandatory(qualityInspectionSupplier.isMandatory());
        }
        return qualityInspectionSupplierDTO;
    }

    public static List<BinlocationBalanceDTO> transforAsBinlocationBalanceDTOs(List<BinlocationBalance> binlocationBalances) {
        List<BinlocationBalanceDTO> binlocationBalanceDTOs = new ArrayList<BinlocationBalanceDTO>();
        if (binlocationBalances != null && !binlocationBalances.isEmpty()) {
            for (BinlocationBalance binlocationBalance : binlocationBalances) {
                binlocationBalanceDTOs.add(transformAsDTO(binlocationBalance));
            }
        }
        return binlocationBalanceDTOs;
    }

    public static BinlocationBalanceDTO transformAsDTO(BinlocationBalance binlocationBalance) {
        BinlocationBalanceDTO binlocationBalanceDTO = new BinlocationBalanceDTO();
        if (binlocationBalance != null) {
            binlocationBalanceDTO.setId(binlocationBalance.getBinlocationBalanceOid());
            binlocationBalanceDTO.setVersion(binlocationBalance.getVersion());
            if (binlocationBalance.getDeviation() != null) {
                binlocationBalanceDTO.setDeviation(binlocationBalance.getDeviation().name());
            }
            binlocationBalanceDTO.setPartAffiliation(binlocationBalance.getPartAffiliation());
            binlocationBalanceDTO.setPartNumber(binlocationBalance.getPartNumber());
            binlocationBalanceDTO.setPartName(binlocationBalance.getPartName());
            binlocationBalanceDTO.setPartVersion(binlocationBalance.getPartVersion());
            binlocationBalanceDTO.setPartModification(binlocationBalance.getPartModification());
            binlocationBalanceDTO.setQuantity(binlocationBalance.getQuantity());
            BinLocation binLocation = binlocationBalance.getBinLocation();
            if (binLocation != null) {
                binlocationBalanceDTO.setCode(binLocation.getCode());
                Zone zone = binLocation.getZone();
                if (zone != null) {
                    binlocationBalanceDTO.setZoneType(zone.getType());
                }
            }
        }
        return binlocationBalanceDTO;
    }

    public static List<WarehouseDTO> transformToDTO(List<Warehouse> warehouses, CommonServices commonServices) {
        List<WarehouseDTO> warehouseDtos = new ArrayList<WarehouseDTO>();
        for (Warehouse warehouse : warehouses) {
            Site site = commonServices.getSiteBySiteId(warehouse.getSiteId());
            if (site == null) {
                // Happens when data is wrongly setup
                continue;
            }
            WarehouseDTO warehouseDto = WarehouseServicesHelper.transformAsDTO(warehouse, site);
            warehouseDtos.add(warehouseDto);
        }
        Collections.sort(warehouseDtos, new Comparator<WarehouseDTO>() {
            public int compare(WarehouseDTO warehouseDTOOne, WarehouseDTO warehouseDTOTwo) {
                return warehouseDTOOne.getSiteName().compareTo(warehouseDTOTwo.getSiteName());
            }
        });
        return warehouseDtos;
    }

    public static void validateQISupplierOrProject(Object obj) throws GloriaApplicationException {
        if (obj instanceof QualityInspectionSupplierDTO) {
            QualityInspectionSupplierDTO qualityInspectionSupplierDTO = (QualityInspectionSupplierDTO) obj;
            if (StringUtils.isEmpty(qualityInspectionSupplierDTO.getSupplier())) {
                throw new GloriaApplicationException(GloriaExceptionConstants.NO_QI_SUPPLIER, "Supplier Name is mandatory.", null);
            }
        } else if (obj instanceof QualityInspectionProjectDTO) {
            QualityInspectionProjectDTO qualityInspectionProjectDTO = (QualityInspectionProjectDTO) obj;
            if (StringUtils.isEmpty(qualityInspectionProjectDTO.getProject())) {
                throw new GloriaApplicationException(GloriaExceptionConstants.NO_QI_PROJECT, "Project Name is mandatory.", null);
            }
        }
    }

    public static void validateUniqueQISupplierOrProject(Object objDTO, Object obj) throws GloriaApplicationException {
        if (obj instanceof QualityInspectionSupplier) {
            QualityInspectionSupplierDTO qualityInspectionSupplierDTO = (QualityInspectionSupplierDTO) objDTO;
            QualityInspectionSupplier qualityInspectionSupplier = (QualityInspectionSupplier) obj;
            if (qualityInspectionSupplier != null && qualityInspectionSupplierDTO != null
                    && qualityInspectionSupplierDTO.getId() != qualityInspectionSupplier.getQualityInspectionSupplierOId()) {
                throw new GloriaApplicationException(GloriaExceptionConstants.SUPPLIER_EXISTS, "Supplier name should be unique.", null);
            }
        } else if (obj instanceof QualityInspectionProject) {
            QualityInspectionProjectDTO qualityInspectionProjectDTO = (QualityInspectionProjectDTO) objDTO;
            QualityInspectionProject qualityInspectionProject = (QualityInspectionProject) obj;
            if (qualityInspectionProject != null && qualityInspectionProjectDTO != null
                    && qualityInspectionProjectDTO.getId() != qualityInspectionProject.getQualityInspectionProjectOId()) {
                throw new GloriaApplicationException(GloriaExceptionConstants.PROJECT_EXISTS, "Project name should be unique.", null);
            }
        }
    }

    public static void updateBaySettingsForAisleRackRow(AisleRackRow aisleRackRow, AisleRackRowDTO aisleRackRowDTO, AisleRepository aisleRepository) {
        if (aisleRackRow != null) {
            if (aisleRackRow.getBaySettings() != null && !aisleRackRow.getBaySettings().isEmpty()) {
                List<BaySetting> baySettings = aisleRackRow.getBaySettings();
                int baySettingSize = baySettings.size();
                for (int i = baySettingSize - 1; i >= 0; i--) {
                    aisleRepository.deleteBaySetting(baySettings.get(i).getBaySettingOid());
                }
                aisleRackRow.getBaySettings().clear();
            }
            long numberOfBays = aisleRackRowDTO.getNumberOfBays();
            aisleRackRow.setNumberOfBay(numberOfBays);
            for (int bayNo = 0; bayNo < numberOfBays; bayNo++) {
                BaySetting baySetting = new BaySetting();
                baySetting.setAisleRackRow(aisleRackRow);
                aisleRackRow.getBaySettings().add(baySetting);
            }
        }
    }
    
    public static FileToExportDTO exportPartBlanace(List<BinlocationBalance> binlocationBalances) throws GloriaApplicationException {
        FileToExportDTO exportDTO = new FileToExportDTO();
        XSSFWorkbook workbook = ExcelOperationUtil.getXSSFWorkbook();
        XSSFSheet sheet = ExcelOperationUtil.createXSSFSheet(workbook);
        ExcelOperationUtil.setUpHeaderInfo(workbook, sheet, defineHeader(), sheet.getPhysicalNumberOfRows());
        ExcelOperationUtil.nextRow(sheet);
        setUpDataInExcel(workbook, sheet, binlocationBalances, sheet.getPhysicalNumberOfRows());
        ExcelOperationUtil.nextRow(sheet);
        ExcelOperationUtil.nextRow(sheet);
        exportDTO.setContent(ExcelOperationUtil.getExcelBytes(workbook));
        exportDTO.setName("Inventory");
        return exportDTO;
    }
    
    public static List<String> defineHeader() {
        List<String> xclRows = new ArrayList<String>();
        xclRows.add("Deviation");
        xclRows.add("Part No.");
        xclRows.add("Version");
        xclRows.add("Part Name");
        xclRows.add("Part Modification");
        xclRows.add("Bin Location");
        xclRows.add("Bin Location Balance");
        return xclRows;
    }

    public static XSSFWorkbook setUpDataInExcel(XSSFWorkbook workbook, XSSFSheet sheet, List<BinlocationBalance> binlocationBalances, int rowIndex)
            throws GloriaApplicationException {
        for (BinlocationBalance binlocationBalance : binlocationBalances) {

            int cellIndex = 0;
            XSSFRow row = ExcelOperationUtil.createRow(sheet, rowIndex++);

            Deviation deviation = binlocationBalance.getDeviation();
            if (deviation != null) {
                cellIndex = ExcelOperationUtil.createCellForUpdateInExcelCells(sheet, deviation.name(), cellIndex, row);
            } else {
                cellIndex = ExcelOperationUtil.createCellForUpdateInExcelCells(sheet, "", cellIndex, row);
            }
            cellIndex = ExcelOperationUtil.createCellForUpdateInExcelCells(sheet, binlocationBalance.getPartNumber(), cellIndex, row);
            cellIndex = ExcelOperationUtil.createCellForUpdateInExcelCells(sheet, binlocationBalance.getPartVersion(), cellIndex, row);
            cellIndex = ExcelOperationUtil.createCellForUpdateInExcelCells(sheet, binlocationBalance.getPartName(), cellIndex, row);
            cellIndex = ExcelOperationUtil.createCellForUpdateInExcelCells(sheet, binlocationBalance.getPartModification(), cellIndex, row);
            cellIndex = ExcelOperationUtil.createCellForUpdateInExcelCells(sheet, binlocationBalance.getBinLocation().getCode(), cellIndex, row);
            cellIndex = ExcelOperationUtil.createCellForUpdateInExcelCells(sheet, "" + binlocationBalance.getQuantity(), cellIndex, row);

        }
        return workbook;
    }

}
