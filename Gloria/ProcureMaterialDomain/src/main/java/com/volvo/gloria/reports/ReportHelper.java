package com.volvo.gloria.reports;

import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_CURRENCY;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_CURRENCY_ORIGINAL;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_TOTAL_PRICE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_TOTAL_PRICE_ORIGINAL;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_UNIT_OF_PRICE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_UNIT_PRICE;
import static com.volvo.gloria.util.c.dto.reports.ReportColumnIdentifiers.REPORT_COLUMN_ID_UNIT_PRICE_ORIGINAL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Tuple;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang.StringUtils;

import com.volvo.gloria.authorization.c.ReportType;
import com.volvo.gloria.authorization.d.entities.ReportFilter;
import com.volvo.gloria.authorization.d.entities.ReportFilterMaterial;
import com.volvo.gloria.authorization.d.entities.ReportFilterOrder;
import com.volvo.gloria.common.c.CurrencyUtil;
import com.volvo.gloria.common.c.dto.reports.ReportFilterMaterialDTO;
import com.volvo.gloria.common.c.dto.reports.ReportFilterOrderDTO;
import com.volvo.gloria.common.c.dto.reports.ReportFilters;
import com.volvo.gloria.common.d.entities.CompanyCode;
import com.volvo.gloria.common.d.entities.CurrencyRate;
import com.volvo.gloria.common.repositories.b.CompanyCodeRepository;
import com.volvo.gloria.common.repositories.b.CurrencyRateRepository;
import com.volvo.gloria.procurematerial.d.entities.OrderLineVersion;
import com.volvo.gloria.util.c.dto.reports.ReportColumn;
import com.volvo.gloria.util.c.dto.reports.ReportRow;

/**
 * Entity transformation for Reports.
 * 
 */
public class ReportHelper {

    protected ReportHelper() {

    }

    public static List<ReportFilters> transformAsReportFilterDTOs(List<ReportFilter> reportFilters) {
        List<ReportFilters> reportFilterDTOs = new ArrayList<ReportFilters>();
        if (reportFilters != null && !reportFilters.isEmpty()) {
            for (ReportFilter reportFilter : reportFilters) {
                reportFilterDTOs.add(transformAsReportFilterDTO(reportFilter));
            }
        }
        return reportFilterDTOs;
    }

    public static ReportFilters transformAsReportFilterDTO(ReportFilter reportFilter) {
        if (reportFilter != null) {
            switch (ReportType.valueOf(reportFilter.getType())) {
            case ORDER:
                return transformAsOrderFilterDTO((ReportFilterOrder) reportFilter);
            case MATERIAL:
                return transformAsMaterialFilterDTO((ReportFilterMaterial) reportFilter);
            default:
                return null;
            }
        }
        return null;
    }

    public static ReportFilterOrderDTO transformAsOrderFilterDTO(ReportFilterOrder reportFilterOrder) {
        if (reportFilterOrder != null) {
            ReportFilterOrderDTO reportFilterOrderDTO = new ReportFilterOrderDTO();
            reportFilterOrderDTO.setId(reportFilterOrder.getId());
            reportFilterOrderDTO.setType(reportFilterOrder.getType());
            reportFilterOrderDTO.setName(reportFilterOrder.getName());
            reportFilterOrderDTO.setMailFormId(reportFilterOrder.isMailFormId());
            reportFilterOrderDTO.setProcureTeam(reportFilterOrder.isProcureTeam());
            reportFilterOrderDTO.setRequisitionId(reportFilterOrder.isRequisitionId());
            reportFilterOrderDTO.setRequisitionDate(reportFilterOrder.isRequisitionDate());
            reportFilterOrderDTO.setRequisitionSite(reportFilterOrder.isRequisitionSite());
            reportFilterOrderDTO.setOrderType(reportFilterOrder.isOrderType());
            reportFilterOrderDTO.setOrderNo(reportFilterOrder.isOrderNo());
            reportFilterOrderDTO.setPurchasingOrg(reportFilterOrder.isPurchasingOrg());
            reportFilterOrderDTO.setBuyerId(reportFilterOrder.isBuyerId());
            reportFilterOrderDTO.setBuyerName(reportFilterOrder.isBuyerName());
            reportFilterOrderDTO.setDeliveryNoteDate(reportFilterOrder.isDeliveryNoteDate());
            reportFilterOrderDTO.setStaAgreedLastUpdated(reportFilterOrder.isStaAgreedLastUpdated());
            reportFilterOrderDTO.setReceivedDateTime(reportFilterOrder.isReceivedDateTime());
            reportFilterOrderDTO.setProcureInfo(reportFilterOrder.isProcureInfo());
            reportFilterOrderDTO.setInternalProcurerId(reportFilterOrder.isInternalProcurerId());
            reportFilterOrderDTO.setOrderDate(reportFilterOrder.isOrderDate());
            reportFilterOrderDTO.setOrderQty(reportFilterOrder.isOrderQty());
            reportFilterOrderDTO.setPossibleToReceiveQty(reportFilterOrder.isPossibleToReceiveQty());
            reportFilterOrderDTO.setUnitOfMeasure(reportFilterOrder.isUnitOfMeasure());
            reportFilterOrderDTO.setOrderSta(reportFilterOrder.isOrderSta());
            reportFilterOrderDTO.setAgreedSta(reportFilterOrder.isAgreedSta());
            reportFilterOrderDTO.setReceivedQty(reportFilterOrder.isReceivedQty());
            reportFilterOrderDTO.setExpectedQty(reportFilterOrder.isExpectedQty());
            reportFilterOrderDTO.setExpectedArrival(reportFilterOrder.isExpectedArrival());
            reportFilterOrderDTO.setLastModifiedDate(reportFilterOrder.isLastModifiedDate());
            reportFilterOrderDTO.setBlockedQty(reportFilterOrder.isBlockedQty());
            reportFilterOrderDTO.setDamagedQty(reportFilterOrder.isDamagedQty());
            reportFilterOrderDTO.setPlannedDispatchDate(reportFilterOrder.isPlannedDispatchDate());
            reportFilterOrderDTO.setOrderLog(reportFilterOrder.isOrderLog());
            reportFilterOrderDTO.setOrderLineLog(reportFilterOrder.isOrderLineLog());
            reportFilterOrderDTO.setPrice(reportFilterOrder.isPrice());
            reportFilterOrderDTO.setUnitPrice(reportFilterOrder.isUnitPrice());
            reportFilterOrderDTO.setUnitOfPrice(reportFilterOrder.isUnitOfPrice());
            reportFilterOrderDTO.setCurrency(reportFilterOrder.isCurrency());
            reportFilterOrderDTO.setQtyTestObject(reportFilterOrder.isQtyTestObject());
            reportFilterOrderDTO.setAdditionalQty(reportFilterOrder.isAdditionalQty());
            reportFilterOrderDTO.setWbs(reportFilterOrder.isWbs());
            reportFilterOrderDTO.setCostCenter(reportFilterOrder.isCostCenter());
            reportFilterOrderDTO.setGlAccount(reportFilterOrder.isGlAccount());
            reportFilterOrderDTO.setContactPersonId(reportFilterOrder.isContactPersonId());
            reportFilterOrderDTO.setContactPersonName(reportFilterOrder.isContactPersonName());
            reportFilterOrderDTO.setMaterialControllerId(reportFilterOrder.isMaterialControllerId());
            reportFilterOrderDTO.setMaterialControllerName(reportFilterOrder.isMaterialControllerName());
            return reportFilterOrderDTO;
        }
        return null;
    }

    public static ReportFilterMaterialDTO transformAsMaterialFilterDTO(ReportFilterMaterial reportFilterMaterial) {
        if (reportFilterMaterial != null) {
            ReportFilterMaterialDTO reportFilterMaterialDTO = new ReportFilterMaterialDTO();
            reportFilterMaterialDTO.setId(reportFilterMaterial.getId());
            reportFilterMaterialDTO.setType(reportFilterMaterial.getType());
            reportFilterMaterialDTO.setName(reportFilterMaterial.getName());
            reportFilterMaterialDTO.setMailFormId(reportFilterMaterial.isMailFormId());
            reportFilterMaterialDTO.setCostCenter(reportFilterMaterial.isCostCenter());
            reportFilterMaterialDTO.setGlAccount(reportFilterMaterial.isGlAccount());
            reportFilterMaterialDTO.setInternalOrderSap(reportFilterMaterial.isInternalOrderSAP());
            reportFilterMaterialDTO.setFunctionGroup(reportFilterMaterial.isFunctionGroup());
            reportFilterMaterialDTO.setDesignGroup(reportFilterMaterial.isDesignGroup());
            reportFilterMaterialDTO.setModularHarness(reportFilterMaterial.isModularHarness());
            reportFilterMaterialDTO.setRequisitionId(reportFilterMaterial.isRequisitionId());
            reportFilterMaterialDTO.setRequisitionDate(reportFilterMaterial.isRequisitionDate());
            reportFilterMaterialDTO.setReference(reportFilterMaterial.isReference());
            reportFilterMaterialDTO.setRequiredSta(reportFilterMaterial.isRequiredSta());
            reportFilterMaterialDTO.setBuildLocationId(reportFilterMaterial.isBuildLocationId());
            reportFilterMaterialDTO.setBuildLocationName(reportFilterMaterial.isBuildLocationName());
            reportFilterMaterialDTO.setBuildStart(reportFilterMaterial.isBuildStart());
            reportFilterMaterialDTO.setMaterialControllerId(reportFilterMaterial.isMaterialControllerId());
            reportFilterMaterialDTO.setMaterialControllerName(reportFilterMaterial.isMaterialControllerName());
            reportFilterMaterialDTO.setRequesterId(reportFilterMaterial.isRequesterId());
            reportFilterMaterialDTO.setRequesterName(reportFilterMaterial.isRequesterName());
            reportFilterMaterialDTO.setContactPersonId(reportFilterMaterial.isContactPersonId());
            reportFilterMaterialDTO.setContactPersonName(reportFilterMaterial.isContactPersonName());
            reportFilterMaterialDTO.setWarehouse(reportFilterMaterial.isWarehouse());
            reportFilterMaterialDTO.setStorageRoom(reportFilterMaterial.isStorageRoom());
            reportFilterMaterialDTO.setBinLocation(reportFilterMaterial.isBinLocation());
            reportFilterMaterialDTO.setQty(reportFilterMaterial.isQty());
            reportFilterMaterialDTO.setUnitOfMeasure(reportFilterMaterial.isUnitOfMeasure());
            reportFilterMaterialDTO.setReceivedDate(reportFilterMaterial.isReceivedDate());
            reportFilterMaterialDTO.setDeliveryNoteNo(reportFilterMaterial.isDeliveryNoteNo());
            reportFilterMaterialDTO.setDispatchNoteNo(reportFilterMaterial.isDispatchNoteNo());
            reportFilterMaterialDTO.setDeliveryAddressId(reportFilterMaterial.isDeliveryAddressId());
            reportFilterMaterialDTO.setDeliveryAddressName(reportFilterMaterial.isDeliveryAddressName());
            reportFilterMaterialDTO.setSource(reportFilterMaterial.isSource());
            reportFilterMaterialDTO.setOrderNo(reportFilterMaterial.isOrderNo());
            reportFilterMaterialDTO.setOrderStatus(reportFilterMaterial.isOrderStatus());
            reportFilterMaterialDTO.setPurchasingOrganisation(reportFilterMaterial.isPurchasingOrganisation());
            reportFilterMaterialDTO.setBuyerId(reportFilterMaterial.isBuyerId());
            reportFilterMaterialDTO.setInternalProcurerId(reportFilterMaterial.isInternalProcurerId());
            reportFilterMaterialDTO.setOrderDate(reportFilterMaterial.isOrderDate());
            reportFilterMaterialDTO.setOrderQty(reportFilterMaterial.isOrderQty());
            reportFilterMaterialDTO.setPossibleToReceiveQty(reportFilterMaterial.isPossibleToReceiveQty());
            reportFilterMaterialDTO.setOrderSTA(reportFilterMaterial.isOrderSTA());
            reportFilterMaterialDTO.setAgreedSTA(reportFilterMaterial.isAgreedSTA());
            reportFilterMaterialDTO.setStaAccepted(reportFilterMaterial.isStaAccepted());
            reportFilterMaterialDTO.setExpectedQty(reportFilterMaterial.isExpectedQty());
            reportFilterMaterialDTO.setExpectedDate(reportFilterMaterial.isExpectedDate());
            reportFilterMaterialDTO.setPlannedDispatchDate(reportFilterMaterial.isPlannedDispatchDate());
            reportFilterMaterialDTO.setPrice(reportFilterMaterial.isPrice());
            reportFilterMaterialDTO.setCurrency(reportFilterMaterial.isCurrency());
            reportFilterMaterialDTO.setUnitPrice(reportFilterMaterial.isUnitPrice());
            reportFilterMaterialDTO.setUnitOfPrice(reportFilterMaterial.isUnitOfPrice());
            reportFilterMaterialDTO.setSupplierParmaId(reportFilterMaterial.isSupplierParmaId());
            reportFilterMaterialDTO.setSupplierName(reportFilterMaterial.isSupplierName());
            reportFilterMaterialDTO.setDeliveryControllerId(reportFilterMaterial.isDeliveryControllerId());
            reportFilterMaterialDTO.setDeliveryControllerName(reportFilterMaterial.isDeliveryControllerName());
            reportFilterMaterialDTO.setFlagOrderLine(reportFilterMaterial.isFlagOrderLine());
            reportFilterMaterialDTO.setProcureInfo(reportFilterMaterial.isProcureInfo());
            reportFilterMaterialDTO.setReceivedQty(reportFilterMaterial.isReceivedQty());
            reportFilterMaterialDTO.setBlockedQty(reportFilterMaterial.isBlockedQty());
            reportFilterMaterialDTO.setBuyerName(reportFilterMaterial.isBuyerName());
            reportFilterMaterialDTO.setOrderLineLogEventValue(reportFilterMaterial.isOrderLineLogEventValue());
            reportFilterMaterialDTO.setOrderLogEventValue(reportFilterMaterial.isOrderLogEventValue());
            reportFilterMaterialDTO.setProblemDescription(reportFilterMaterial.isProblemDescription());
            reportFilterMaterialDTO.setQualityInspectionComment(reportFilterMaterial.isQualityInspectionComment());
            reportFilterMaterialDTO.setStatusUserId(reportFilterMaterial.isStatusUserId());
            reportFilterMaterialDTO.setStatusUserName(reportFilterMaterial.isStatusUserName());
            reportFilterMaterialDTO.setStaAgreedLastUpdated(reportFilterMaterial.isStaAgreedLastUpdated());
            reportFilterMaterialDTO.setInspectionStatus(reportFilterMaterial.isInspectionStatus());
            reportFilterMaterialDTO.setFlagProcureLine(reportFilterMaterial.isFlagProcureLine());
            return reportFilterMaterialDTO;
        }
        return null;
    }
    
    public static Map<String, String> getCompanyCodeToDefaultCurrencyMap(CompanyCodeRepository companyCodeRepository) {
        Map<String, String> companyCodeToDefaultCurrencyMap = new HashMap<String, String>();
        List<CompanyCode> companyCodeList = companyCodeRepository.findAll();
        for (CompanyCode companyCode : companyCodeList) {
            companyCodeToDefaultCurrencyMap.put(companyCode.getCode(), companyCode.getDefaultCurrency());
        }
        return companyCodeToDefaultCurrencyMap;
    }
    
    public static Map<String, CurrencyRate> getCurrencyToCurrencyRateMap(CurrencyRateRepository currencyRateRepository) {
        Map<String, CurrencyRate> currencyToCurrencyRateMap = new HashMap<String, CurrencyRate>();
        List<CurrencyRate> currencyRateList = currencyRateRepository.findAll();
        for (CurrencyRate currencyRate : currencyRateList) {
            currencyToCurrencyRateMap.put(currencyRate.getCurrency().getCode(), currencyRate);
        }
        return currencyToCurrencyRateMap;
    }
    
    @SuppressWarnings("rawtypes")
    public static void prepareHandleTuplesForCurrency(String column, Tuple materialDataTuple, Selection selection, ReportRow reportRow,
            String deafultCurrencyCode, Map<String, CurrencyRate> currencyToCurrencyRateMap) {
        ReportColumn reportColumn = new ReportColumn();
        if (column == null) {
            reportColumn.setName(selection.getAlias().replace("_", " "));
        } else {
            reportColumn.setName(column.replace("_", " "));
        }
        reportColumn.setValue(handleTuplesForCurrency(materialDataTuple, selection, column, deafultCurrencyCode,
                                                                             currencyToCurrencyRateMap));
        reportRow.getReportColumns().add(reportColumn);
    }
    
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Object handleTuplesForCurrency(Tuple dataTauple, Selection selection, String columnName, String deafultCurrencyCode,
            Map<String, CurrencyRate> currencyToCurrencyRateMap) {
        if (dataTauple.get(selection) != null) {
            OrderLineVersion orderLineVersion = (OrderLineVersion) dataTauple.get(selection);
            if (orderLineVersion != null) {
                if (columnName.equals(REPORT_COLUMN_ID_UNIT_OF_PRICE)) {
                    return orderLineVersion.getPerQuantity();
                }
                if (columnName.equals(REPORT_COLUMN_ID_TOTAL_PRICE_ORIGINAL) || columnName.equals(REPORT_COLUMN_ID_TOTAL_PRICE)) {
                    Double price = 0.0;
                    if (!StringUtils.isEmpty(deafultCurrencyCode)) {
                        long possibleToReceiveQty = orderLineVersion.getOrderLine().getPossibleToReceiveQuantity();
                        long perQuantity = orderLineVersion.getPerQuantity();
                        Double unitPriceToCalculate = 0.0;
                        if (columnName.equals(REPORT_COLUMN_ID_TOTAL_PRICE_ORIGINAL)) {
                            unitPriceToCalculate = orderLineVersion.getUnitPrice();
                        } else {
                            unitPriceToCalculate = CurrencyUtil.convertCurrencyFromActualToDefault(orderLineVersion.getUnitPrice(),
                                                                                                   orderLineVersion.getCurrency(), deafultCurrencyCode,
                                                                                                   currencyToCurrencyRateMap);
                        }
                        if (perQuantity > 0) {
                            price = possibleToReceiveQty * (unitPriceToCalculate / perQuantity);
                        } else {
                            price = possibleToReceiveQty * unitPriceToCalculate;
                        }
                    }
                    return CurrencyUtil.roundOff(price, 2);
                }
                if (columnName.equals(REPORT_COLUMN_ID_CURRENCY_ORIGINAL)) {
                    return orderLineVersion.getCurrency();
                }
                if (columnName.equals(REPORT_COLUMN_ID_UNIT_PRICE_ORIGINAL)) {
                    return orderLineVersion.getUnitPrice();
                }

                if (columnName.equals(REPORT_COLUMN_ID_CURRENCY)) {
                    return deafultCurrencyCode;
                }
                if (columnName.equals(REPORT_COLUMN_ID_UNIT_PRICE)) {
                    Double unitPrice = 0.0;
                    if (!StringUtils.isEmpty(deafultCurrencyCode)) {
                        unitPrice = CurrencyUtil.convertCurrencyFromActualToDefault(orderLineVersion.getUnitPrice(), orderLineVersion.getCurrency(),
                                                                                    deafultCurrencyCode, currencyToCurrencyRateMap);
                    }
                    return CurrencyUtil.roundOff(unitPrice, 2);
                }
            }
        }
        return null;
    }
}
