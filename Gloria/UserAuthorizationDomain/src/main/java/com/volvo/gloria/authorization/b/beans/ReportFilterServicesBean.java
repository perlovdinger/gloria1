package com.volvo.gloria.authorization.b.beans;

import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import com.volvo.gloria.authorization.b.ReportFilterServices;
import com.volvo.gloria.authorization.c.ReportType;
import com.volvo.gloria.authorization.d.entities.ReportFilter;
import com.volvo.gloria.authorization.d.entities.ReportFilterMaterial;
import com.volvo.gloria.authorization.d.entities.ReportFilterOrder;
import com.volvo.gloria.common.c.dto.reports.ReportFilterMaterialDTO;
import com.volvo.gloria.common.c.dto.reports.ReportFilterOrderDTO;
import com.volvo.gloria.common.repositories.b.ReportFilterRepository;
import com.volvo.gloria.common.repositories.b.TeamRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * service implementation for Reports.
 * 
 */
@ContainerManaged(name = "reportFilterServices")
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ReportFilterServicesBean implements ReportFilterServices {

    @Inject
    private TeamRepository teamRepository;

    @Inject
    private ReportFilterRepository reportFilterRepository;

    @Override
    public List<ReportFilter> getReportFilters(String reportType, String userId) {
        return reportFilterRepository.getReportFilters(reportType, userId);
    }

    @Override
    public ReportFilterOrder createOrderReportFilter(ReportFilterOrderDTO reportFilterOrderDTO, String userId) throws GloriaApplicationException {
       if (reportFilterOrderDTO != null) {
            ReportFilterOrder reportFilterOrder = new ReportFilterOrder();
            reportFilterOrder.setType(ReportType.ORDER.name());
            reportFilterOrder.setName(reportFilterOrderDTO.getName());
            reportFilterOrder.setRequisitionId(reportFilterOrderDTO.isRequisitionId());
            reportFilterOrder.setRequisitionDate(reportFilterOrderDTO.isRequisitionDate());
            reportFilterOrder.setRequisitionSite(reportFilterOrderDTO.isRequisitionSite());
            reportFilterOrder.setOrderType(reportFilterOrderDTO.isOrderType());
            reportFilterOrder.setOrderNo(reportFilterOrderDTO.isOrderNo());
            reportFilterOrder.setPurchasingOrg(reportFilterOrderDTO.isPurchasingOrg());
            reportFilterOrder.setBuyerId(reportFilterOrderDTO.isBuyerId());
            reportFilterOrder.setBuyerName(reportFilterOrderDTO.isBuyerName());
            reportFilterOrder.setStaAgreedLastUpdated(reportFilterOrderDTO.isStaAgreedLastUpdated());
            reportFilterOrder.setReceivedDateTime(reportFilterOrderDTO.isReceivedDateTime());
            reportFilterOrder.setProcureInfo(reportFilterOrderDTO.isProcureInfo());
            reportFilterOrder.setInternalProcurerId(reportFilterOrderDTO.isInternalProcurerId());
            reportFilterOrder.setOrderDate(reportFilterOrderDTO.isOrderDate());
            reportFilterOrder.setOrderQty(reportFilterOrderDTO.isOrderQty());
            reportFilterOrder.setPossibleToReceiveQty(reportFilterOrderDTO.isPossibleToReceiveQty());
            reportFilterOrder.setUnitOfMeasure(reportFilterOrderDTO.isUnitOfMeasure());
            reportFilterOrder.setOrderSta(reportFilterOrderDTO.isOrderSta());
            reportFilterOrder.setAgreedSta(reportFilterOrderDTO.isAgreedSta());
            reportFilterOrder.setReceivedQty(reportFilterOrderDTO.isReceivedQty());
            reportFilterOrder.setExpectedQty(reportFilterOrderDTO.isExpectedQty());
            reportFilterOrder.setExpectedArrival(reportFilterOrderDTO.isExpectedArrival());
            reportFilterOrder.setLastModifiedDate(reportFilterOrderDTO.isLastModifiedDate());
            reportFilterOrder.setBlockedQty(reportFilterOrderDTO.isBlockedQty());
            reportFilterOrder.setDamagedQty(reportFilterOrderDTO.isDamagedQty());
            reportFilterOrder.setPlannedDispatchDate(reportFilterOrderDTO.isPlannedDispatchDate());
            reportFilterOrder.setOrderLog(reportFilterOrderDTO.isOrderLog());
            reportFilterOrder.setOrderLineLog(reportFilterOrderDTO.isOrderLineLog());
            reportFilterOrder.setPrice(reportFilterOrderDTO.isPrice());
            reportFilterOrder.setUnitPrice(reportFilterOrderDTO.isUnitPrice());
            reportFilterOrder.setUnitOfPrice(reportFilterOrderDTO.isUnitOfPrice());
            reportFilterOrder.setCurrency(reportFilterOrderDTO.isCurrency());
            reportFilterOrder.setQtyTestObject(reportFilterOrderDTO.isQtyTestObject());
            reportFilterOrder.setAdditionalQty(reportFilterOrderDTO.isAdditionalQty());
            reportFilterOrder.setWbs(reportFilterOrderDTO.isWbs());
            reportFilterOrder.setCostCenter(reportFilterOrderDTO.isCostCenter());
            reportFilterOrder.setGlAccount(reportFilterOrderDTO.isGlAccount());
            reportFilterOrder.setContactPersonId(reportFilterOrderDTO.isContactPersonId());
            reportFilterOrder.setContactPersonName(reportFilterOrderDTO.isContactPersonName());
            reportFilterOrder.setMaterialControllerId(reportFilterOrderDTO.isMaterialControllerId());
            reportFilterOrder.setMaterialControllerName(reportFilterOrderDTO.isMaterialControllerName());
            reportFilterOrder.setProcureTeam(reportFilterOrderDTO.isProcureTeam());
            reportFilterOrder.setMailFormId(reportFilterOrderDTO.isMailFormId());
            reportFilterOrder.setGloriaUser(teamRepository.findUserByUserId(userId));
            reportFilterOrder.setDeliveryNoteDate(reportFilterOrderDTO.isDeliveryNoteDate());
            return reportFilterRepository.save(reportFilterOrder);
        }
        return null;
    }

    @Override
    public void removeReportFilter(long reportFilterId) throws GloriaApplicationException {
        reportFilterRepository.remove(reportFilterId);
    }
    
    @Override
    public ReportFilterMaterial createMaterialReportFilter(ReportFilterMaterialDTO reportFilterMaterialDTO, String userId) throws GloriaApplicationException {
        if (reportFilterMaterialDTO != null) {
            ReportFilterMaterial reportFilterMaterial = new ReportFilterMaterial();
            reportFilterMaterial.setType(ReportType.MATERIAL.name());
            reportFilterMaterial.setName(reportFilterMaterialDTO.getName());
            reportFilterMaterial.setCostCenter(reportFilterMaterialDTO.isCostCenter());
            reportFilterMaterial.setGlAccount(reportFilterMaterialDTO.isGlAccount());
            reportFilterMaterial.setInternalOrderSAP(reportFilterMaterialDTO.isInternalOrderSap());
            reportFilterMaterial.setMailFormId(reportFilterMaterialDTO.isMailFormId());
            reportFilterMaterial.setFunctionGroup(reportFilterMaterialDTO.isFunctionGroup());
            reportFilterMaterial.setDesignGroup(reportFilterMaterialDTO.isDesignGroup());
            reportFilterMaterial.setModularHarness(reportFilterMaterialDTO.isModularHarness());
            reportFilterMaterial.setRequisitionId(reportFilterMaterialDTO.isRequisitionId());
            reportFilterMaterial.setRequisitionDate(reportFilterMaterialDTO.isRequisitionDate());
            reportFilterMaterial.setReference(reportFilterMaterialDTO.isReference());
            reportFilterMaterial.setRequiredSta(reportFilterMaterialDTO.isRequiredSta());
            reportFilterMaterial.setBuildLocationId(reportFilterMaterialDTO.isBinLocation());
            reportFilterMaterial.setBuildLocationName(reportFilterMaterialDTO.isBuildLocationName());
            reportFilterMaterial.setBuildStart(reportFilterMaterialDTO.isBuildStart());
            reportFilterMaterial.setRequisitionDate(reportFilterMaterialDTO.isRequisitionDate());
            reportFilterMaterial.setMaterialControllerId(reportFilterMaterialDTO.isMaterialControllerId());
            reportFilterMaterial.setMaterialControllerName(reportFilterMaterialDTO.isMaterialControllerName());
            reportFilterMaterial.setRequesterId(reportFilterMaterialDTO.isRequesterId());
            reportFilterMaterial.setRequesterName(reportFilterMaterialDTO.isRequesterName());
            reportFilterMaterial.setContactPersonId(reportFilterMaterialDTO.isContactPersonId());
            reportFilterMaterial.setContactPersonName(reportFilterMaterialDTO.isContactPersonName());
            reportFilterMaterial.setWarehouse(reportFilterMaterialDTO.isWarehouse());
            reportFilterMaterial.setStorageRoom(reportFilterMaterialDTO.isStorageRoom());
            reportFilterMaterial.setBinLocation(reportFilterMaterialDTO.isBinLocation());
            reportFilterMaterial.setQty(reportFilterMaterialDTO.isQty());
            reportFilterMaterial.setUnitOfMeasure(reportFilterMaterialDTO.isUnitOfMeasure());
            reportFilterMaterial.setReceivedDate(reportFilterMaterialDTO.isReceivedDate());
            reportFilterMaterial.setDeliveryNoteNo(reportFilterMaterialDTO.isDeliveryNoteNo());
            reportFilterMaterial.setDispatchNoteNo(reportFilterMaterialDTO.isDispatchNoteNo());
            reportFilterMaterial.setDeliveryAddressId(reportFilterMaterialDTO.isDeliveryAddressId());
            reportFilterMaterial.setDeliveryAddressName(reportFilterMaterialDTO.isDeliveryAddressName());
            reportFilterMaterial.setSource(reportFilterMaterialDTO.isSource());
            reportFilterMaterial.setOrderNo(reportFilterMaterialDTO.isOrderNo());
            reportFilterMaterial.setOrderStatus(reportFilterMaterialDTO.isOrderStatus());
            reportFilterMaterial.setPurchasingOrganisation(reportFilterMaterialDTO.isPurchasingOrganisation());
            reportFilterMaterial.setBuyerId(reportFilterMaterialDTO.isBuyerId());
            reportFilterMaterial.setInternalProcurerId(reportFilterMaterialDTO.isInternalProcurerId());
            reportFilterMaterial.setOrderDate(reportFilterMaterialDTO.isOrderDate());
            reportFilterMaterial.setOrderQty(reportFilterMaterialDTO.isOrderQty());
            reportFilterMaterial.setPossibleToReceiveQty(reportFilterMaterialDTO.isPossibleToReceiveQty());
            reportFilterMaterial.setOrderSTA(reportFilterMaterialDTO.isOrderSTA());
            reportFilterMaterial.setAgreedSTA(reportFilterMaterialDTO.isAgreedSTA());
            reportFilterMaterial.setStaAccepted(reportFilterMaterialDTO.isStaAccepted());
            reportFilterMaterial.setExpectedQty(reportFilterMaterialDTO.isExpectedQty());
            reportFilterMaterial.setExpectedDate(reportFilterMaterialDTO.isExpectedDate());
            reportFilterMaterial.setPlannedDispatchDate(reportFilterMaterialDTO.isPlannedDispatchDate());
            reportFilterMaterial.setPrice(reportFilterMaterialDTO.isPrice());
            reportFilterMaterial.setCurrency(reportFilterMaterialDTO.isCurrency());
            reportFilterMaterial.setUnitPrice(reportFilterMaterialDTO.isUnitPrice());
            reportFilterMaterial.setUnitOfPrice(reportFilterMaterialDTO.isUnitOfPrice());
            reportFilterMaterial.setSupplierParmaId(reportFilterMaterialDTO.isSupplierParmaId());
            reportFilterMaterial.setSupplierName(reportFilterMaterialDTO.isSupplierName());
            reportFilterMaterial.setDeliveryControllerId(reportFilterMaterialDTO.isDeliveryControllerId());
            reportFilterMaterial.setDeliveryControllerName(reportFilterMaterialDTO.isDeliveryControllerName());
            reportFilterMaterial.setGloriaUser(teamRepository.findUserByUserId(userId));
            reportFilterMaterial.setProcureInfo(reportFilterMaterialDTO.isProcureInfo());
            reportFilterMaterial.setReceivedQty(reportFilterMaterialDTO.isReceivedQty());
            reportFilterMaterial.setBlockedQty(reportFilterMaterialDTO.isBlockedQty());
            reportFilterMaterial.setBuyerName(reportFilterMaterialDTO.isBuyerName());
            reportFilterMaterial.setOrderLineLogEventValue(reportFilterMaterialDTO.isOrderLineLogEventValue());
            reportFilterMaterial.setOrderLogEventValue(reportFilterMaterialDTO.isOrderLogEventValue());
            reportFilterMaterial.setProblemDescription(reportFilterMaterialDTO.isProblemDescription());
            reportFilterMaterial.setQualityInspectionComment(reportFilterMaterialDTO.isQualityInspectionComment());
            reportFilterMaterial.setStatusUserId(reportFilterMaterialDTO.isStatusUserId());
            reportFilterMaterial.setStatusUserName(reportFilterMaterialDTO.isStatusUserName());
            reportFilterMaterial.setStaAgreedLastUpdated(reportFilterMaterialDTO.isStaAgreedLastUpdated());
            reportFilterMaterial.setInspectionStatus(reportFilterMaterialDTO.isInspectionStatus());
            reportFilterMaterial.setFlagOrderLine(reportFilterMaterialDTO.isFlagOrderLine());
            reportFilterMaterial.setFlagProcureLine(reportFilterMaterialDTO.isFlagProcureLine());
            
            return reportFilterRepository.save(reportFilterMaterial);
        }
        return null;
    }

    @Override
    public ReportFilter getReportFilterById(long id) {
        return reportFilterRepository.findReportFilterById(id);
    }
}
