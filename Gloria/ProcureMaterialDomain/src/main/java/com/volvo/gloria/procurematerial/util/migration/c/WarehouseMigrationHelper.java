package com.volvo.gloria.procurematerial.util.migration.c;

import static org.apache.commons.lang.StringUtils.isEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.volvo.gloria.authorization.d.entities.Team;
import com.volvo.gloria.common.d.entities.CompanyCode;
import com.volvo.gloria.common.d.entities.Site;
import com.volvo.gloria.procurematerial.c.ChangeType;
import com.volvo.gloria.procurematerial.c.DeliveryNoteLineStatus;
import com.volvo.gloria.procurematerial.d.entities.ChangeId;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNote;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteSubLine;
import com.volvo.gloria.procurematerial.d.entities.FinanceHeader;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.status.changeid.ChangeIdStatus;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.requestline.MaterialStatus;
import com.volvo.gloria.procurematerial.d.type.directsend.DirectSendType;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.procurematerial.d.type.procure.ProcureType;
import com.volvo.gloria.procurematerial.d.type.receive.ReceiveType;
import com.volvo.gloria.procurematerial.util.migration.c.dto.WarehouseMigrationDTO;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.c.UniqueItems;
import com.volvo.gloria.warehouse.d.entities.BinLocation;

/**
 * Common utility for Warehouse Migration.
 */
public final class WarehouseMigrationHelper {

    private static final int MAX_QTY_VALUE_99999 = 99999;
    private static final int MAX_TEXT_LENGTH_2048 = 2048;
    private static final long RANDOM_999L = 999;
    private static Map<String, String> companyCodeToMaterialControllerTeamMap = new HashMap<String, String>();
    private static Map<String, String> companyGroupToMaterialControllerTeamMap = new HashMap<String, String>();
    private static List<String> releasedProjects = Arrays.asList("FREE", "STOCK", "STOCK 3P", "STOCK3P", "STOCK-3P", "STOCKGTT");
    private static Map<String, Site> sitesMap = new HashMap<String, Site>();
    private static long randomNumber = RANDOM_999L;
    private static final String MISSING_PART_NAME = "undefined";
    
    private WarehouseMigrationHelper() {
    }

    public static List<WarehouseMigrationDTO> getValidList(List<WarehouseMigrationDTO> warehouseMigrationDTOs) {
        List<WarehouseMigrationDTO> validatList = new ArrayList<WarehouseMigrationDTO>();
        List<String> orderNoPartKey = new ArrayList<String>();
        for (WarehouseMigrationDTO warehouseMigrationDTO : warehouseMigrationDTOs) {
            boolean isFail = false;
            if (isEmpty(warehouseMigrationDTO.getPartNumber())) {
                warehouseMigrationDTO.setReason("Part number missing. ");
                isFail = true;
            }
            if (isEmpty(warehouseMigrationDTO.getPartVersion())) {
                warehouseMigrationDTO.setReason("STAGE/ISSUE missing. ");
                isFail = true;
            }
            if (warehouseMigrationDTO.getQuantity() == null || warehouseMigrationDTO.getQuantity() < 0
                    || warehouseMigrationDTO.getQuantity() > MAX_QTY_VALUE_99999) {
                warehouseMigrationDTO.setReason("Storage quantity is missing or negative or more than 99999. ");
                isFail = true;
            }
            String orderNoPart = warehouseMigrationDTO.getOrderPartIdentifierKey();
            if (!orderNoPartKey.contains(orderNoPart)) {
                orderNoPartKey.add(orderNoPart);
            } else {
                if (warehouseMigrationDTO.isSplitted()) {
                    warehouseMigrationDTO.setReasonIT("Part stored at different binlocation for same Order.");
                } else {
                    warehouseMigrationDTO.setReason("Duplicate order and part info.");
                }
                isFail = true;
            }

            // Validation for warning cases
            if (warehouseMigrationDTO.getProjectId() == null) {
                warehouseMigrationDTO.setReason("PROJECT missing. ");
            }
            if (warehouseMigrationDTO.getOrderNo() == null) {
                warehouseMigrationDTO.setReason("Order number missing. ");
            }

            List<String> testobjects = warehouseMigrationDTO.getTestObjectsQty();
            UniqueItems idSets = new UniqueItems();
            for (String testObject : testobjects) {
                if (!StringUtils.isEmpty(testObject)) {
                    testObject = testObject.replace("[", "");
                    idSets.add(StringUtils.substringBefore(testObject, "@"));
                }
            }
            warehouseMigrationDTO.setReferenceIds(idSets.createCommaSeparatedKey());

            if (!isFail) {
                validatList.add(warehouseMigrationDTO);
            }
        }
        return validatList;
    }

    public static Material transformToEntity(WarehouseMigrationDTO warehouseMigrationDTO) throws GloriaApplicationException {
        Material material = new Material();
        MaterialLine materialLine = new MaterialLine();

        materialLine.setStatus(MaterialLineStatus.STORED);
        materialLine.setQuantity(warehouseMigrationDTO.getQuantity());
        materialLine.setStatusDate(DateUtil.getDateWithZeroTime(DateUtil.getCurrentUTCDateTime()));
        materialLine.setRequestedExcluded(false);
        materialLine.setWhSiteId(warehouseMigrationDTO.getSiteId());
        materialLine.setFinalWhSiteId(warehouseMigrationDTO.getSiteId());
        materialLine.setDirectSend(DirectSendType.NO);
        materialLine.setOrderNo(warehouseMigrationDTO.getOrderNo());
        material.addMaterialLine(materialLine);
        if (warehouseMigrationDTO.getProjectId() == null || releasedProjects.contains(warehouseMigrationDTO.getProjectId())) {
            material.setMaterialType(MaterialType.RELEASED);
        } else {
            material.setMaterialType(MaterialType.ADDITIONAL);
        }

        material.setStatus(MaterialStatus.ADDED);
        material.setPartAffiliation(warehouseMigrationDTO.getPartAffiliation());
        material.setPartNumber(warehouseMigrationDTO.getPartNumber());
        material.setPartVersion(warehouseMigrationDTO.getPartVersion());
        material.setPartVersionOriginal(warehouseMigrationDTO.getPartVersion());
        material.setPartName(warehouseMigrationDTO.getPartName());
        material.setOrderNo(warehouseMigrationDTO.getOrderNo());
        material.setMigrated(true);
        material.setCreatedDate(DateUtil.getDateWithZeroTime(DateUtil.getCurrentUTCDateTime()));
        String unitOfMeasure = warehouseMigrationDTO.getUnitOfMeasure();
        if (StringUtils.isEmpty(unitOfMeasure)) {
            unitOfMeasure = "PCE";
        }
        material.setUnitOfMeasure(unitOfMeasure);

        // financeHeader
        FinanceHeader financeHeader = new FinanceHeader();
        if (!material.getMaterialType().equals(MaterialType.RELEASED)) {
            financeHeader.setProjectId(warehouseMigrationDTO.getProjectId());
        }
        financeHeader.setCompanyCode(warehouseMigrationDTO.getCompanyCode());
        material.setFinanceHeader(financeHeader);

        // changeId -- null chk instead
        ChangeId changeId = new ChangeId();
        changeId.setType(ChangeType.SINGLE);
        changeId.setStatus(ChangeIdStatus.ACCEPTED);
        changeId.setMtrlRequestVersion("Migrated");
        material.setAdd(changeId);

        ProcureLine procureLine = new ProcureLine();
        procureLine.setProcureType(ProcureType.EXTERNAL);
        procureLine.setpPartAffiliation(warehouseMigrationDTO.getPartAffiliation());
        procureLine.setpPartNumber(warehouseMigrationDTO.getPartNumber());
        procureLine.setpPartVersion(warehouseMigrationDTO.getPartVersion());
        procureLine.setpPartName(StringUtils.isEmpty(warehouseMigrationDTO.getPartName()) ? MISSING_PART_NAME : warehouseMigrationDTO.getPartName());
        procureLine.setStatus(ProcureLineStatus.RECEIVED);
        procureLine.setWhSiteId(warehouseMigrationDTO.getSiteId());
        procureLine.setOrderNo(warehouseMigrationDTO.getOrderNo());
        procureLine.setFinanceHeader(financeHeader);
        procureLine.setMaterialControllerTeam(warehouseMigrationDTO.getMaterialControllerTeam());
        material.setProcureLine(procureLine);

        return material;
    }

    public static DeliveryNoteLine transformToDeliveryNoteEntity(WarehouseMigrationDTO warehouseMigrationDTO) throws GloriaApplicationException {
        DeliveryNote deliveryNote = new DeliveryNote();
        deliveryNote.setDeliveryNoteDate(DateUtil.getCurrentUTCDateTime());
        deliveryNote.setReceiveType(ReceiveType.TRANSFER);
        String randomString = "MIG-" + ++randomNumber;
        deliveryNote.setDeliveryNoteNo(randomString);
        deliveryNote.setOrderNo(warehouseMigrationDTO.getOrderNo());
        deliveryNote.setWhSiteId(warehouseMigrationDTO.getSiteId());

        DeliveryNoteLine deliveryNoteLine = new DeliveryNoteLine();
        deliveryNoteLine.setPartAffiliation(warehouseMigrationDTO.getPartAffiliation());
        deliveryNoteLine.setPartName(warehouseMigrationDTO.getPartName());
        deliveryNoteLine.setPartNumber(warehouseMigrationDTO.getPartNumber());
        deliveryNoteLine.setPartVersion(warehouseMigrationDTO.getPartVersion());
        deliveryNoteLine.setProjectId(warehouseMigrationDTO.getProjectId());
        deliveryNoteLine.setProcureType(ProcureType.EXTERNAL);
        deliveryNoteLine.setReceivedQuantity(warehouseMigrationDTO.getQuantity());
        deliveryNoteLine.setDeliveryNoteQuantity(warehouseMigrationDTO.getQuantity());
        deliveryNoteLine.setStatus(DeliveryNoteLineStatus.RECEIVED);
        deliveryNoteLine.setReceivedDateTime(DateUtil.getCurrentUTCDateTime());

        String referenceIds = warehouseMigrationDTO.getReferenceIds();
        if (referenceIds != null && referenceIds.length() > MAX_TEXT_LENGTH_2048) {
            referenceIds = referenceIds.substring(0, MAX_TEXT_LENGTH_2048);
        }
        deliveryNoteLine.setReferenceIds(referenceIds);

        DeliveryNoteSubLine deliveryNoteSubLine = new DeliveryNoteSubLine();
        deliveryNoteSubLine.setDirectSend(false);
        deliveryNoteSubLine.setToApproveQty(0L);
        deliveryNoteSubLine.setToReceiveQty(warehouseMigrationDTO.getQuantity());
        deliveryNoteSubLine.setBinLocation(warehouseMigrationDTO.getBinLocationOid());
        deliveryNoteSubLine.setBinLocationCode(warehouseMigrationDTO.getBinLocationCode());

        deliveryNoteSubLine.setDeliveryNoteLine(deliveryNoteLine);
        deliveryNoteLine.getDeliveryNoteSubLines().add(deliveryNoteSubLine);
        deliveryNoteLine.setDeliveryNote(deliveryNote);
        deliveryNote.getDeliveryNoteLine().add(deliveryNoteLine);
        return deliveryNoteLine;
    }

    public static List<WarehouseMigrationDTO> filterMissingBinLocation(List<WarehouseMigrationDTO> warehouseMigrationDTOs, List<BinLocation> binLocations) {
        List<WarehouseMigrationDTO> missingList = new ArrayList<WarehouseMigrationDTO>();
        for (WarehouseMigrationDTO warehouseMigrationDTO : warehouseMigrationDTOs) {
            boolean missing = true;
            for (BinLocation binLocation : binLocations) {
                if (warehouseMigrationDTO.getBinLocationCode().equals(binLocation.getCode())) {
                    warehouseMigrationDTO.setBinLocationOid(binLocation.getBinLocationOid());
                    missing = false;
                    break;
                }
            }
            if (missing) {
                missingList.add(warehouseMigrationDTO);
                warehouseMigrationDTO.setReason("BinLocation code missing in Gloria.");
            }
        }
        warehouseMigrationDTOs.removeAll(missingList);
        return warehouseMigrationDTOs;
    }

    public static Set<String> getAllBinLocationCodes(List<WarehouseMigrationDTO> warehouseMigrationDTOs) {
        Set<String> binLocationCodes = new HashSet<String>();
        for (WarehouseMigrationDTO dto : warehouseMigrationDTOs) {
            binLocationCodes.add(dto.getBinLocationCode());
        }
        return binLocationCodes;
    }

    public static void setAllMaterialControllerTeams(Set<Team> materialControllerTeams) {
        for (Team team : materialControllerTeams) {
            companyGroupToMaterialControllerTeamMap.put(team.getCompanyCodeGroup(), team.getName());
        }

    }

    public static void populateAdditionalAttibutes(List<WarehouseMigrationDTO> validDTOs) {
        for (WarehouseMigrationDTO warehouseMigrationDTO : validDTOs) {
            warehouseMigrationDTO.setCompanyCode(findCompanyCodeForSite(warehouseMigrationDTO.getSiteId()));
            warehouseMigrationDTO.setMaterialControllerTeam(findMaterialControllerTeamForCompanyCode(warehouseMigrationDTO.getCompanyCode()));
        }
    }

    private static String findCompanyCodeForSite(String siteId) {
        Site site = sitesMap.get(siteId);
        return site.getCompanyCode();
    }

    public static String findMaterialControllerTeamForCompanyCode(String companyCode) {
        return companyCodeToMaterialControllerTeamMap.get(companyCode);
    }

    public static void setAllCompanyCodes(Set<CompanyCode> allCompanyCodes) {
        for (CompanyCode companyCode : allCompanyCodes) {
            companyCodeToMaterialControllerTeamMap.put(companyCode.getCode(),
                                                       companyGroupToMaterialControllerTeamMap.get(companyCode.getCompanyGroup().getCode()));
        }
    }

    public static void setAllSitesInfo(Set<Site> allSites) {
        for (Site site : allSites) {
            sitesMap.put(site.getSiteId(), site);
        }
    }
}
