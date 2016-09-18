package com.volvo.gloria.reports.b;

import java.util.Date;

import com.volvo.gloria.common.c.dto.reports.ReportFilterMaterialDTO;
import com.volvo.gloria.common.c.dto.reports.ReportFilterOrderDTO;
import com.volvo.gloria.common.c.dto.reports.ReportGeneralDTO;
import com.volvo.gloria.common.c.dto.reports.ReportPartDeliveryPrecisionDTO;
import com.volvo.gloria.common.c.dto.reports.ReportPerformanceDTO;
import com.volvo.gloria.common.c.dto.reports.ReportWarehouseActionDTO;
import com.volvo.gloria.common.c.dto.reports.ReportWarehouseCostDTO;
import com.volvo.gloria.common.c.dto.reports.ReportWarehouseTransactionDTO;
import com.volvo.gloria.util.FileToExportDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.paging.c.PageObject;

/**
 * 
 * services for Report support.
 * 
 */
public interface ReportServices {

    PageObject getCompanyCodeFilters(PageObject pageObject, String userId) throws GloriaApplicationException;
        
    PageObject getSuffixFilters(PageObject pageObject);
    
    PageObject getProjectFilters(PageObject pageObject, String companyCode, String userId, String projectId) throws GloriaApplicationException;
    
    PageObject getBuildSerieFilters(PageObject pageObject, String companyCode, String userId) throws GloriaApplicationException;
    
    PageObject getTestObjectFilters(PageObject pageObject);
    
    PageObject getSupplierParmaIdFilters(PageObject pageObject, String companyCode, String suffix, String userId) throws GloriaApplicationException;
    
    PageObject getSupplierParmaNameFilters(PageObject pageObject, String companyCode, String suffix, String userId);
    
    PageObject getReferenceFilters(PageObject pageObject);
    
    PageObject getMtrIdFilters(PageObject pageObject);
    
    PageObject getDeliveryControllerIdFilters(PageObject pageObject, String companyCode, String suffix, String userId) throws GloriaApplicationException;
    
    PageObject getDeliveryControllerNameFilters(PageObject pageObject, String companyCode, String suffix, String userId);
    
    PageObject getOrderStatusFilters(PageObject pageObject);
    
    PageObject getWbsFilters(PageObject pageObject, String wbsCode);
    
    PageObject getMaterialStatusFilters(PageObject pageObject);
    
    PageObject getMaterialControllerTeamFilters(PageObject pageObject);

    FileToExportDTO generateOrderReport(ReportFilterOrderDTO reportFilterOrderDTO, Date fromDate, Date toDate, String userId) 
            throws GloriaApplicationException;

    FileToExportDTO generateMaterialReport(ReportFilterMaterialDTO reportFilterMaterialDTO, String userId) 
            throws GloriaApplicationException;

    FileToExportDTO generateWarehouseActionReport(Date fromDateParam, Date toDateParam, ReportWarehouseActionDTO reportWarehouseActionDTO);

    PageObject getBuyerFilters(PageObject pageObject);

    FileToExportDTO generatePartDeliveryPrecisionReport(Date fromDate, Date toDate, String userId, 
            ReportPartDeliveryPrecisionDTO reportPartDeliveryPrecisionDTO) throws GloriaApplicationException;

    PageObject getBuyersFromOrderLine(PageObject pageObject);

    FileToExportDTO generatePeformanceReport(Date fromDate, Date toDate, String dateType, ReportPerformanceDTO reportPerformanceDTO);

    PageObject getPartsFromOrderLine(PageObject pageObject);

    FileToExportDTO generateGeneralWarehouseReport(ReportGeneralDTO reportGeneralDTO);

    FileToExportDTO generateWarehouseCostReport(ReportWarehouseCostDTO reportWarehouseCostDTO, Date fromDate, Date toDate, String userId) 
            throws GloriaApplicationException;

    PageObject getWarehouseFilters(PageObject pageObject);

    FileToExportDTO generateWarehouseTransactionReport(ReportWarehouseTransactionDTO reportWarehouseTransactionDTO, Date fromDate, Date toDate) 
            throws GloriaApplicationException;

    PageObject getStorageRooms(PageObject pageObject);

    PageObject getBuildNameFilters(PageObject pageObject);
     
}
