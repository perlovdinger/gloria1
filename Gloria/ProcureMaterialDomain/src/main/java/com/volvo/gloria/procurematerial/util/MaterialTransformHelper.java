package com.volvo.gloria.procurematerial.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.volvo.gloria.financeProxy.c.GoodsReceiptHeaderTransformerDTO;
import com.volvo.gloria.financeProxy.c.GoodsReceiptLineTransformerDTO;
import com.volvo.gloria.financeProxy.c.OrderSapAccountsDTO;
import com.volvo.gloria.financeProxy.c.OrderSapLineDTO;
import com.volvo.gloria.financeProxy.c.OrderSapScheduleDTO;
import com.volvo.gloria.financeProxy.c.ProcessPurchaseOrderDTO;
import com.volvo.gloria.procurematerial.c.ShipmentType;
import com.volvo.gloria.procurematerial.c.dto.DispatchNoteDTO;
import com.volvo.gloria.procurematerial.c.dto.MaterialLineDTO;
import com.volvo.gloria.procurematerial.c.dto.PickListDTO;
import com.volvo.gloria.procurematerial.c.dto.RequestGroupDTO;
import com.volvo.gloria.procurematerial.c.dto.RequestListDTO;
import com.volvo.gloria.procurematerial.d.entities.ChangeId;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNote;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteSubLine;
import com.volvo.gloria.procurematerial.d.entities.DeliverySchedule;
import com.volvo.gloria.procurematerial.d.entities.DispatchNote;
import com.volvo.gloria.procurematerial.d.entities.FinanceHeader;
import com.volvo.gloria.procurematerial.d.entities.GoodsReceiptHeader;
import com.volvo.gloria.procurematerial.d.entities.GoodsReceiptLine;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeaderVersion;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.Order;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.OrderSap;
import com.volvo.gloria.procurematerial.d.entities.OrderSapAccounts;
import com.volvo.gloria.procurematerial.d.entities.OrderSapLine;
import com.volvo.gloria.procurematerial.d.entities.OrderSapSchedule;
import com.volvo.gloria.procurematerial.d.entities.PickList;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.RequestGroup;
import com.volvo.gloria.procurematerial.d.entities.RequestList;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.type.directsend.DirectSendType;
import com.volvo.gloria.procurematerial.repositories.b.beans.RequestHeaderRepositoryBeanHelper;
import com.volvo.gloria.util.Utils;

/**
 * Class transforming material module classes.
 * 
 */
public class MaterialTransformHelper {

    protected MaterialTransformHelper() {

    }

    public static RequestListDTO transformAsRequestListDTO(RequestList requestList) {
        RequestListDTO requestListDTO = null;
        if (requestList != null) {
            requestListDTO = new RequestListDTO();
            requestListDTO.setId(requestList.getRequestListOid());
            requestListDTO.setVersion(requestList.getVersion());
            requestListDTO.setDeliveryAddressName(requestList.getDeliveryAddressName());
            requestListDTO.setDeliveryAddressId(requestList.getDeliveryAddressId());
            
            requestListDTO.setDeliveryAddress(getSiteAddress(requestList.getDeliveryAddressId(), requestList.getDeliveryAddressName(),
                                                             requestList.getDeliveryAddress()));
            requestListDTO.setDeliveryAddressType(requestList.getDeliveryAddressType().name());
            
            requestListDTO.setWhSiteId(requestList.getWhSiteId());
            requestListDTO.setWhSiteName(requestList.getWhSiteName());
            requestListDTO.setWhSiteAddress(getSiteAddress(requestList.getWhSiteId(), requestList.getWhSiteName(), requestList.getWhSiteAddress()));
            requestListDTO.setRequiredDeliveryDate(requestList.getRequiredDeliveryDate());
            requestListDTO.setPriority(requestList.getPriority());
            requestListDTO.setRequestUserId(requestList.getRequestUserId());
            requestListDTO.setRequesterName(requestList.getRequesterName());
            requestListDTO.setStatus(requestList.getStatus().name());
            requestListDTO.setCreatedDate(requestList.getCreatedDate());
            requestListDTO.setShipVia(requestList.getShipVia());
            requestListDTO.setShipmentType(requestList.getShipmentType().name());
            requestListDTO.setCancelAllowed(requestList.isCancelAllowed());
            if (requestList.getDispatchNote() != null) {
                DispatchNote dispatchNote = requestList.getDispatchNote();
                requestListDTO.setDispatchNoteId(dispatchNote.getDispatchNoteOID());
                requestListDTO.setDispatchNoteNumber(dispatchNote.getDispatchNoteNo());
                requestListDTO.setDispatchNoteVersion(dispatchNote.getVersion());
                requestListDTO.setCarrier(dispatchNote.getCarrier());
                requestListDTO.setTrackingNo(dispatchNote.getTrackingNo());
            }
        }
        return requestListDTO;
    }
    
    public static String getSiteAddress(String siteId, String siteName, String siteAddress) {
        String siteFullAddress = null;
        if (!StringUtils.isEmpty(siteId)) {
            siteFullAddress = siteId + "-" + siteName;
        } else {
            siteFullAddress =  siteName;
        }
        if (siteAddress != null) {
            siteFullAddress = siteFullAddress + ", " + siteAddress;
        }
        return siteFullAddress;
    }

    public static List<RequestListDTO> transformAsRequestListDTOs(List<RequestList> requestLists) {
        List<RequestListDTO> listOfRequestList = new ArrayList<RequestListDTO>();

        if (requestLists != null && !requestLists.isEmpty()) {
            for (RequestList requestList : requestLists) {
                listOfRequestList.add(transformAsRequestListDTO(requestList));
            }
        }
        return listOfRequestList;
    }

    public static List<RequestGroupDTO> transformAsRequestGroupDTOs(List<RequestGroup> requestGroups) {
        List<RequestGroupDTO> listOfRequestGroupDTOs = new ArrayList<RequestGroupDTO>();
        if (requestGroups != null && !requestGroups.isEmpty()) {
            for (RequestGroup requestGroup : requestGroups) {
                listOfRequestGroupDTOs.add(transformAsRequestGroupDTO(requestGroup));
            }
        }
        return listOfRequestGroupDTOs;
    }

    public static RequestGroupDTO transformAsRequestGroupDTO(RequestGroup requestGroup) {
        if (requestGroup != null) {
            RequestGroupDTO requestGroupDTO = new RequestGroupDTO();
            requestGroupDTO.setId(requestGroup.getRequestGroupOid());
            requestGroupDTO.setVersion(requestGroup.getVersion());
            
            List<MaterialLine> materialLines = requestGroup.getMaterialLines();

            PickList pickList = requestGroup.getPickList();
            if (pickList != null) {
                requestGroupDTO.setStatus(pickList.getStatus().name());
                requestGroupDTO.setPickListCode(pickList.getCode());
                requestGroupDTO.setPulledByUserId(pickList.getPulledByUserId());
                requestGroupDTO.setPickListId(pickList.getPickListOid());
                requestGroupDTO.setReservedUserId(pickList.getReservedUserId());
            } else {
                Material directSendMaterial = materialLines.get(0).getMaterial();
                requestGroupDTO.setDirectSendPartNo(directSendMaterial.getPartNumber());
                requestGroupDTO.setDirectSendPartVersion(directSendMaterial.getPartVersion());
                requestGroupDTO.setDirectSendPartAffiliation(directSendMaterial.getPartAffiliation());
            }
            requestGroupDTO.setZoneId(requestGroup.getZoneId());
            requestGroupDTO.setItems(String.valueOf(calculateDeliveryNoteLinesForParts(materialLines)));
            long quantity = 0;
            for (MaterialLine materialLine : materialLines) {
                quantity += materialLine.getQuantity();
            }
            requestGroupDTO.setTotalQuantity(quantity);
            RequestList requestList = requestGroup.getRequestList();
            
            ShipmentType shipmentType = requestList.getShipmentType();
            if (shipmentType != null) {
                requestGroupDTO.setShipmentType(shipmentType.name());
            }
            
            if (requestList.getPriority() != null) {
                requestGroupDTO.setPriority(requestList.getPriority());
            }
            requestGroupDTO.setProjectId(requestGroup.getProjectId());
            requestGroupDTO.setReferenceGroup(requestGroup.getReferenceGroup());
            requestGroupDTO.setReferenceId(requestGroup.getReferenceId());
            requestGroupDTO.setChangeRequestIds(requestGroup.getChangeRequestIds());
            requestGroupDTO.setRequiredDeliveryDate(requestList.getRequiredDeliveryDate());
            requestGroupDTO.setRequestListID(requestList.getRequestListOid());
            requestGroupDTO.setDeliveryAddressName(requestList.getDeliveryAddressName());
            requestGroupDTO.setDeliveryAddressId(requestList.getDeliveryAddressId());
            requestGroupDTO.setRequesterUserId(requestList.getRequestUserId());
            requestGroupDTO.setRequesterUserName(requestList.getRequesterName());
            requestGroupDTO.setCreatedDate(requestList.getCreatedDate());
            return requestGroupDTO;
        }
        return null;
    }

    private static int calculateDeliveryNoteLinesForParts(List<MaterialLine> listOfMaterialLines) {
        int countForDeliveryNoteline = 0;
        if (listOfMaterialLines != null && !listOfMaterialLines.isEmpty()) {
            Map<String, String> mapOfDeliveryNoteLines = new HashMap<String, String>();
            for (MaterialLine materialLine : listOfMaterialLines) {
                DeliveryNoteLine line = materialLine.getDeliveryNoteLine();
                if (line != null && !mapOfDeliveryNoteLines.containsKey(line.getPartNumber())) {
                    mapOfDeliveryNoteLines.put(materialLine.getDeliveryNoteLine().getPartNumber(), null);
                    countForDeliveryNoteline = countForDeliveryNoteline + 1;
                }
            }
        }
        return countForDeliveryNoteline;
    }

    public static DispatchNoteDTO transformAsDTO(DispatchNote dispatchNote) {
        if (dispatchNote != null) {
            DispatchNoteDTO dispatchNoteDTO = new DispatchNoteDTO();
            dispatchNoteDTO.setId(dispatchNote.getDispatchNoteOID());
            dispatchNoteDTO.setVersion(dispatchNote.getVersion());
            dispatchNoteDTO.setDeliveryDate(dispatchNote.getDeliveryDate());
            dispatchNoteDTO.setDispatchNoteDate(dispatchNote.getDispatchNoteDate());
            dispatchNoteDTO.setDispatchNoteNo(dispatchNote.getDispatchNoteNo());
            dispatchNoteDTO.setCarrier(dispatchNote.getCarrier());
            dispatchNoteDTO.setHeight(dispatchNote.getHeight());
            dispatchNoteDTO.setNote(dispatchNote.getNote());
            dispatchNoteDTO.setRequestListStatus(dispatchNote.getRequestList().getStatus().toString());
            dispatchNoteDTO.setTrackingNo(dispatchNote.getTrackingNo());
            dispatchNoteDTO.setTransportMode(dispatchNote.getTransportMode());
            dispatchNoteDTO.setWeight(dispatchNote.getWeight());
            dispatchNoteDTO.setRequestListOId(dispatchNote.getRequestList().getRequestListOid());
            dispatchNoteDTO.setShipVia(dispatchNote.getRequestList().getShipVia());
            return dispatchNoteDTO;
        }
        return null;
    }

    public static List<PickListDTO> transformAsPickListDTOs(List<PickList> listOfPickList) {
        List<PickListDTO> listOfPickListDTO = new ArrayList<PickListDTO>();
        if (listOfPickList != null && !listOfPickList.isEmpty()) {
            for (PickList pickList : listOfPickList) {
                listOfPickListDTO.add(transformAsPickListDTO(pickList));
            }
        }
        return listOfPickListDTO;

    }

    public static PickListDTO transformAsPickListDTO(PickList pickList) {
        if (pickList != null) {
            PickListDTO pickListDto = new PickListDTO();
            pickListDto.setShipSkippable(pickList.isShipSkippable());
            pickListDto.setId(pickList.getPickListOid());
            pickListDto.setVersion(pickList.getVersion());
            pickListDto.setCode(pickList.getCode());
            pickListDto.setPulledByUserId(pickList.getPulledByUserId());
            pickListDto.setStatus(pickList.getStatus().name());
            List<RequestGroup> requestGroups = pickList.getRequestGroups();
            if (requestGroups != null && !requestGroups.isEmpty()) {
                pickListDto.setZoneId(requestGroups.get(0).getZoneId());
            }
            List<MaterialLine> materialLines = pickList.getMaterialLines();
            long totalQty = 0;
            if (materialLines != null && !materialLines.isEmpty()) {
                for (MaterialLine materialLine : materialLines) {
                    totalQty += materialLine.getQuantity();
                }
            }
            pickListDto.setTotalQty(totalQty);
            pickListDto.setItems(calculateDeliveryNoteLinesForParts(materialLines));
            return pickListDto;
        }
        return null;
    }
    
    public static GoodsReceiptHeaderTransformerDTO transformAsDTOForProxy(GoodsReceiptHeader goodsReceiptHeader) {
        return transform(goodsReceiptHeader, 0L);
    }

    public static GoodsReceiptHeaderTransformerDTO transformAsDTOForProxy(GoodsReceiptHeader goodsReceiptHeader, long quantityCancelled) {
        return transform(goodsReceiptHeader, quantityCancelled);
    }

    private static GoodsReceiptHeaderTransformerDTO transform(GoodsReceiptHeader goodsReceiptHeader, long quantityCancelled) {
        if (goodsReceiptHeader != null) {
            GoodsReceiptHeaderTransformerDTO goodsReceiptHeaderDTO = new GoodsReceiptHeaderTransformerDTO();
            goodsReceiptHeaderDTO.setAssignCodeGM(goodsReceiptHeader.getAssignCodeGM());
            goodsReceiptHeaderDTO.setCompanyCode(goodsReceiptHeader.getCompanyCode());
            goodsReceiptHeaderDTO.setDocumentDate(goodsReceiptHeader.getDocumentDate());
            goodsReceiptHeaderDTO.setHeaderText(goodsReceiptHeader.getHeaderText());
            goodsReceiptHeaderDTO.setPostingDateTime(goodsReceiptHeader.getPostedDateTime());
            goodsReceiptHeaderDTO.setReferenceDocument(goodsReceiptHeader.getReferenceDocument());

            for (GoodsReceiptLine goodsReceiptLine : goodsReceiptHeader.getGoodsReceiptLines()) {
                GoodsReceiptLineTransformerDTO goodReceiptLineDTO = new GoodsReceiptLineTransformerDTO();
                goodReceiptLineDTO.setIsoUnitOfMeasure(goodsReceiptLine.getIsoUnitOfMeasure());
                goodReceiptLineDTO.setMovementType(goodsReceiptLine.getMovementType());
                goodReceiptLineDTO.setOrderReference(goodsReceiptLine.getOrderReference());
                goodReceiptLineDTO.setPlant(goodsReceiptLine.getPlant());
                if (quantityCancelled > 0) {
                    goodReceiptLineDTO.setQuantity(quantityCancelled);
                } else {
                    goodReceiptLineDTO.setQuantity(goodsReceiptLine.getQuantity());
                }
                goodReceiptLineDTO.setMovementIndicator(goodsReceiptLine.getMovementIndicator());
                goodReceiptLineDTO.setVendor(goodsReceiptLine.getVendor());
                goodReceiptLineDTO.setVendorMaterialNumber(goodsReceiptLine.getVendorMaterialNumber());
                goodsReceiptHeaderDTO.getGoodsReceiptLineTransformerDTOs().add(goodReceiptLineDTO);
            }
            return goodsReceiptHeaderDTO;
        }
        return null;
    }

    public static ProcessPurchaseOrderDTO prepareProcessPurchaseOrderDTO(List<OrderSapLine> orderSapLines, String processAction) {
        if (orderSapLines != null && !orderSapLines.isEmpty()) {
            
            // reorder orderSaplines "asc" to maintain proper re-creating of "sequence[purchaseOrderItem]", during GPS ammendment.
            Collections.sort(orderSapLines, new Comparator<OrderSapLine>() {
                @Override
                public int compare(OrderSapLine o1, OrderSapLine o2) {
                    return Utils.compare(o1.getPurchaseOrderitem(), o2.getPurchaseOrderitem());
                }
            });
            
            List<OrderSapLineDTO> orderSapLineDTOList = new ArrayList<OrderSapLineDTO>();
            OrderSap orderSap = orderSapLines.get(0).getOrderSap();

            ProcessPurchaseOrderDTO processPurchaseOrderDTO = new ProcessPurchaseOrderDTO();

            if (!StringUtils.isEmpty(orderSap.getCompanyCode())) {
                processPurchaseOrderDTO.setCompanyCode(orderSap.getCompanyCode());
            }

            processPurchaseOrderDTO.setAction(processAction);
            processPurchaseOrderDTO.setOrderType(orderSap.getOrderType());
            processPurchaseOrderDTO.setVendor(orderSap.getVendor());
            processPurchaseOrderDTO.setPurchaseOrganisation(orderSap.getPurchaseOrganization());
            processPurchaseOrderDTO.setPurhchaseGroup(orderSap.getPurchaseGroup());
            processPurchaseOrderDTO.setDocumentDate(orderSap.getDocumentDate());
            processPurchaseOrderDTO.setCurrency(orderSap.getCurrency());
            processPurchaseOrderDTO.setSuppressDecimal(orderSap.isSuppressDecimal());
            processPurchaseOrderDTO.setPurchaseType(orderSap.getPurchaseType());
            processPurchaseOrderDTO.setUniqueExtOrder(orderSap.getUniqueExtOrder());

            for (OrderSapLine orderSapLine : orderSapLines) {
                OrderSapLineDTO orderSapLineDTO = new OrderSapLineDTO();
                orderSapLineDTO.setRequisitionId(orderSapLine.getRequisitionId());
                orderSapLineDTO.setPurchaseOrderItem(String.valueOf(orderSapLine.getPurchaseOrderitem()));
                orderSapLineDTO.setAction(orderSapLine.getAction());
                orderSapLineDTO.setOrderReference(orderSapLine.getOrderReference());
                orderSapLineDTO.setPartNumber(orderSapLine.getPartNumber());
                orderSapLineDTO.setShortText(orderSapLine.getShortText());
                orderSapLineDTO.setCancelDate(orderSapLine.getCancelDate());
                orderSapLineDTO.setPlant(orderSapLine.getPlant());
                orderSapLineDTO.setCurrentBuyer(orderSapLine.getCurrentBuyer());
                orderSapLineDTO.setMaterialGroup(orderSapLine.getMaterialGroup());
                orderSapLineDTO.setQuantity(BigDecimal.valueOf(orderSapLine.getQuantity()));
                orderSapLineDTO.setIsoOrderPriceUnit(orderSapLine.getIsoOrderPriceUnit());
                orderSapLineDTO.setIsoPurchaseOrderUnit(orderSapLine.getIsoPurchaseOrderUnit());
                orderSapLineDTO.setNetPrice(BigDecimal.valueOf(orderSapLine.getNetPrice()));
                orderSapLineDTO.setPriceUnit(BigDecimal.valueOf(Long.valueOf(orderSapLine.getPriceUnit())));
                orderSapLineDTO.setEstimatedPriceIndicator(orderSapLine.isEstimatedPriceIndicator());
                orderSapLineDTO.setTaxCode(orderSapLine.getTaxCode());
                orderSapLineDTO.setAccountAssignmentCategory(orderSapLine.getAccountAssignmentCategory());
                orderSapLineDTO.setUnlimitedDeliveryIndicator(Boolean.valueOf(orderSapLine.getUnlimitedDeliveryIndicator()));
                orderSapLineDTO.setGrIndicator(Boolean.valueOf(orderSapLine.getGrIndicator())); // changed
                orderSapLineDTO.setNonValuedGrIndicator(Boolean.valueOf(orderSapLine.getNonValuedGrIndicator()));
                orderSapLineDTO.setIrIndicator(Boolean.valueOf(orderSapLine.getIrIndicator()));
                orderSapLineDTO.setAcknowledgementNumber(orderSapLine.getAcknowledgementNumber());
                orderSapLineDTO.setPurchaseRequisitionNumber(orderSapLine.getPurchaseRequisitionNumber());

                List<OrderSapScheduleDTO> orderSapScheduleDTOList = new ArrayList<OrderSapScheduleDTO>();
                for (OrderSapSchedule orderSapSchedule : orderSapLine.getOrderSapSchedules()) {
                    OrderSapScheduleDTO orderSapScheduleDTO = new OrderSapScheduleDTO();
                    orderSapScheduleDTO.setCategoryOfDeliveryDate(String.valueOf(orderSapSchedule.getCategoryOfDeliveryDate()));
                    orderSapScheduleDTO.setDeliveryDate(orderSapSchedule.getDeliveryDate());
                    orderSapScheduleDTOList.add(orderSapScheduleDTO);
                }
                orderSapLineDTO.setOrderSapSchedule(orderSapScheduleDTOList);

                List<OrderSapAccountsDTO> orderSapAccountsDTOList = new ArrayList<OrderSapAccountsDTO>();
                for (OrderSapAccounts orderSapAccounts : orderSapLine.getOrderSapAccounts()) {
                    OrderSapAccountsDTO accountsDTO = new OrderSapAccountsDTO();
                    accountsDTO.setSequence(String.valueOf(orderSapAccounts.getSequence()));
                    accountsDTO.setGeneralLedgerAccount(orderSapAccounts.getGeneralLedgerAccount());
                    accountsDTO.setCostCenter(orderSapAccounts.getCostCenter());
                    accountsDTO.setWbsElement(orderSapAccounts.getWbsElement());
                    orderSapAccountsDTOList.add(accountsDTO);
                }
                orderSapLineDTO.setOrderSapAccounts(orderSapAccountsDTOList);
                orderSapLineDTOList.add(orderSapLineDTO);
            }
            processPurchaseOrderDTO.setOrderSapLines(orderSapLineDTOList);
            return processPurchaseOrderDTO;
        }
        return null;
    }

    public static List<MaterialLineDTO> transformAsMaterialLineDTOs(List<MaterialLine> materialLines) {
        List<MaterialLineDTO> materialLineDTOs = new ArrayList<MaterialLineDTO>();
        for (MaterialLine materialLine : materialLines) {
            MaterialLineDTO materialLineDTO = transformAsMaterialLineDTO(materialLine, null);
            materialLineDTOs.add(materialLineDTO);
        }
        return materialLineDTOs;
    }

    public static MaterialLineDTO transformAsMaterialLineDTO(MaterialLine materialLine, String suggestedBinLocation) {
        MaterialLineDTO materialLineDTO = new MaterialLineDTO();
        if (materialLine != null && materialLine.getMaterialOwner() != null) {
            Material materialOwner = materialLine.getMaterialOwner();
            ProcureLine procureLine = materialOwner.getProcureLine();
            MaterialHeader materialHeader = materialOwner.getMaterialHeader();
            RequestGroup requestGroup = materialLine.getRequestGroup();
            RequestList requestList = requestGroup == null ? null : requestGroup.getRequestList();
            DispatchNote dispatchNote = requestList == null ? null : requestList.getDispatchNote();

            DeliveryNoteLine deliveryNoteLine = materialLine.getDeliveryNoteLine();
            ChangeId changeId = materialLine.getMaterial().getAdd();
            OrderLine orderLine = materialLine.getMaterial().getOrderLine();
            PickList pickList = materialLine.getPickList();

            materialLineDTO.setRequestListDeliveryAddressId(requestList == null ? null : requestList.getDeliveryAddressId());
            materialLineDTO.setExpirationDate(materialLine.getExpirationDate());
            if (materialLine.getExpirationDate() != null) {
                if (DateUtils.addMilliseconds(DateUtils.ceiling(materialLine.getExpirationDate(), Calendar.DATE), -1).before(new Date())) {
                    materialLineDTO.setMarkPassedDate(true);
                }
            }
            materialLineDTO.setId(materialLine.getMaterialLineOID());
            materialLineDTO.setVersion(materialLine.getVersion());
            materialLineDTO.setStorageRoomName(materialLine.getStorageRoomName());

            FinanceHeader financeHeader = materialOwner.getFinanceHeader();
            if (financeHeader != null) {
                materialLineDTO.setFinanceHeaderCompanyCode(financeHeader.getCompanyCode());
                materialLineDTO.setFinanceHeaderWbsCode(financeHeader.getWbsCode());
                materialLineDTO.setFinanceHeaderGlAccount(financeHeader.getGlAccount());
                materialLineDTO.setFinanceHeaderCostCenter(financeHeader.getCostCenter());
                materialLineDTO.setFinanceHeaderInternalOrderNoSAP(financeHeader.getInternalOrderNoSAP());
                if (!materialLine.getMaterial().getMaterialType().isReleased()) {
                    materialLineDTO.setProjectId(financeHeader.getProjectId());
                }
            }

            MaterialHeaderVersion materialHeaderVersion = null;

            if (materialHeader != null && !materialLine.getMaterial().getMaterialType().isReleased()) {
                if (!materialLine.getMaterial().getMaterialType().isAdditional()) {
                    materialLineDTO.setReferenceId(materialHeader.getReferenceId());
                }
                materialLineDTO.setBuildId(materialHeader.getBuildId());
                materialLineDTO.setReferenceGroup(materialHeader.getAccepted().getReferenceGroup());
                materialLineDTO.setOutBoundLocationId(handleEmpty(materialHeader.getAccepted().getOutboundLocationId()));
                materialLineDTO.setOutBoundLocationName(materialHeader.getAccepted().getOutboundLocationName());
                materialLineDTO.setOutboundStartDate(materialHeader.getAccepted().getOutboundStartDate());
                materialLineDTO.setAssignedMaterialControllerId(materialHeader.getMaterialControllerUserId());
                materialLineDTO.setAssignedMaterialControllerName(materialHeader.getMaterialControllerName());
                materialLineDTO.setBuildName(materialHeader.getBuildName());
                if (materialHeader.getBuildType() != null) {
                    materialLineDTO.setBuildType(materialHeader.getBuildType().name());
                }
                if (changeId != null) {
                    materialLineDTO.setMtrlRequestType(materialHeader.getRequestType().name());
                }
                materialHeaderVersion = materialHeader.getAccepted();

            } else {
                materialLineDTO.setOutBoundLocationId("");
            }
            if (materialHeaderVersion != null) {
                materialLineDTO.setMaterialHeaderVersionRequesterName(materialHeaderVersion.getRequesterName());
                materialLineDTO.setMaterialHeaderVersionRequesterUserId(materialHeaderVersion.getRequesterUserId());
                materialLineDTO.setMaterialHeaderVersionContactPersonId(materialHeaderVersion.getContactPersonId());
                materialLineDTO.setMaterialHeaderVersionContactPersonName(materialHeaderVersion.getContactPersonName());
            }
                materialLineDTO.setMtrlRequestVersion(materialOwner.getMtrlRequestVersionAccepted());

            materialLineDTO.setpPartVersion(materialOwner.getPartVersion());

            populateMaterialLineDTOFromProcureLine(materialLine, materialOwner, procureLine, materialLineDTO);

            materialLineDTO.setWhSiteId(materialLine.getWhSiteId());
            materialLineDTO.setFinalWhSiteId(materialLine.getFinalWhSiteId());
            materialLineDTO.setQuantity(materialLine.getQuantity());
            materialLineDTO.setStatus(materialLine.getStatus().toString());
            materialLineDTO.setSuggestedBinLocation(suggestedBinLocation);
            materialLineDTO.setPlacementID(materialLine.getPlacementOID());
            materialLineDTO.setStockBalance(materialLine.getStockBalance());
            materialLineDTO.setMaterialType(materialOwner.getMaterialType());
            materialLineDTO.setOrderNo(materialLine.getOrderNo());
            materialLineDTO.setStatusDate(materialLine.getStatusDate());
            materialLineDTO.setMaterialLineExpirationDate(materialLine.getExpirationDate());
            if (pickList != null) {
                materialLineDTO.setPickListCode(pickList.getCode());
            }
            if (requestList != null) {
                materialLineDTO.setRequestListID(requestList.getRequestListOid());
                materialLineDTO.setRequestListVersion(requestList.getVersion());
                materialLineDTO.setPriority(requestList.getPriority());
                materialLineDTO.setRequiredDeliveryDate(requestList.getRequiredDeliveryDate());
                materialLineDTO.setDeliveryAddressName(requestList.getDeliveryAddressName());
                materialLineDTO.setParmaID(requestList.getWhSiteId());
                materialLineDTO.setParmaName(requestList.getWhSiteName());
            }
            if (dispatchNote != null) {
                materialLineDTO.setNote(dispatchNote.getNote());
                materialLineDTO.setDispatchNoteNo(dispatchNote.getDispatchNoteNo());
                materialLineDTO.setTransportationNo(dispatchNote.getTrackingNo());
                materialLineDTO.setCarrier(dispatchNote.getCarrier());
            }

            materialLineDTO.setUnitOfMeasure(materialOwner.getUnitOfMeasure());
            materialLineDTO.setMaterialId(materialLine.getMaterial().getMaterialOID());
            materialLineDTO.setMaterialOwnerId(materialOwner.getMaterialOID());
            materialLineDTO.setMaterialPartModification(materialOwner.getPartModification());
            materialLineDTO.setMaterialPartAffiliation(materialOwner.getPartAffiliation());
            materialLineDTO.setMaterialMailFormId(materialOwner.getMailFormId());
            materialLineDTO.setMaterialFunctionGroup(materialOwner.getFunctionGroup());
            materialLineDTO.setMaterialDesignResponsible(materialOwner.getDesignResponsible());
            materialLineDTO.setMaterialModularHarness(materialOwner.getModularHarness());
            materialLineDTO.setAlertPartVersion(materialOwner.getMaterialLastModified().isAlertPartVersion());

            materialLineDTO.setBinLocationCode(materialLine.getBinLocationCode());

            Order order = null;
            if (orderLine != null) {
                materialLineDTO.setPartAlias(orderLine.getSupplierPartNo());
                materialLineDTO.setDeliveryControllerId(orderLine.getDeliveryControllerUserId());
                materialLineDTO.setDeliveryControllerName(orderLine.getDeliveryControllerUserName());
                order = orderLine.getOrder();
            }
            if (order != null) {
                materialLineDTO.setOrderSuffix(order.getSuffix());
            }

            if (deliveryNoteLine != null) {
                DeliveryNoteSubLine deliveryNoteSubLine = deliveryNoteLine.getSubLine(materialLine.getDirectSend() != DirectSendType.NO);
                if (deliveryNoteSubLine != null && deliveryNoteSubLine.getTransportLabel() != null) {
                    materialLineDTO.setTransportLabel(deliveryNoteSubLine.getTransportLabel().getCode());
                }
                DeliveryNote deliveryNote = deliveryNoteLine.getDeliveryNote();
                materialLineDTO.setDeliveryNoteLineId(deliveryNoteLine.getDeliveryNoteLineOID());
                materialLineDTO.setDeliveryNoteNo(deliveryNote.getDeliveryNoteNo());
                materialLineDTO.setDeliveryNoteDate(deliveryNote.getDeliveryNoteDate());
                materialLineDTO.setSupplierId(deliveryNote.getSupplierId());
                materialLineDTO.setSupplierName(deliveryNote.getSupplierName());
            }

            materialLineDTO.setPossiblePickQuantity(materialLine.getQuantity());
            if (materialLine.getStatus() == MaterialLineStatus.READY_TO_SHIP || materialLine.getStatus() == MaterialLineStatus.SHIPPED) {
                materialLineDTO.setPickedQuantity(materialLine.getQuantity());
            }
            
            //set expected arrival date
            if (orderLine != null) {
                DeliverySchedule earliestDeliverySchedule = RequestHeaderRepositoryBeanHelper.evaluateEarliestDeliverySchedule(orderLine.getDeliverySchedule());
                if (earliestDeliverySchedule != null) {
                    materialLineDTO.setExpectedDate(earliestDeliverySchedule.getExpectedDate());
                } 
            }
        }

        return materialLineDTO;
    }

    private static void populateMaterialLineDTOFromProcureLine(MaterialLine materialLine, Material material, 
            ProcureLine procureLine, MaterialLineDTO materialLineDTO) {
        if (procureLine != null) {
            materialLineDTO.setpPartAffiliation(procureLine.getpPartAffiliation());
            materialLineDTO.setpPartName(procureLine.getpPartName());
            materialLineDTO.setChangeRequestIds(procureLine.getChangeRequestIds());
            materialLineDTO.setAdditionalQuantity(procureLine.getAdditionalQuantity());
            materialLineDTO.setpPartNumber(procureLine.getpPartNumber());
            materialLineDTO.setpPartModification(procureLine.getpPartModification());
            materialLineDTO.setProcureUserId(procureLine.getProcureUserId());
            materialLineDTO.setProcureTeam(procureLine.getProcureTeam());
            materialLineDTO.setProcureLineId(procureLine.getProcureLineOid());
            materialLineDTO.setPartNumber(procureLine.getpPartNumber());
            materialLineDTO.setProcureLineMaterialControllerTeam(procureLine.getMaterialControllerTeam());
            materialLineDTO.setProcureLineRequisitionId(procureLine.getRequisitionId());
            materialLineDTO.setProcureLineProcureDate(procureLine.getProcureDate());
            materialLineDTO.setProcureLineReferenceGps(procureLine.getReferenceGps());
            materialLineDTO.setProcureLineRequiredStaDate(procureLine.getRequiredStaDate());
            materialLineDTO.setProcureLineMaterialControllerId(procureLine.getMaterialControllerId());
            materialLineDTO.setProcureLineMaterialControllerName(procureLine.getMaterialControllerName());
        } else {
            materialLineDTO.setpPartAffiliation(material.getPartAffiliation());
            materialLineDTO.setpPartName(material.getPartName());
            materialLineDTO.setpPartNumber(material.getPartNumber());
            materialLineDTO.setpPartModification(material.getPartModification());
            materialLineDTO.setPartNumber(materialLine.getMaterial().getPartNumber());
            materialLineDTO.setPartVersion(material.getPartVersion());
        }
    }

    private static String handleEmpty(String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        } 
        return value;
    }

}
