package com.volvo.gloria.procurematerial.b.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import com.volvo.gloria.common.d.entities.QualityDocument;
import com.volvo.gloria.common.repositories.b.QualityDocumentRepository;
import com.volvo.gloria.procurematerial.b.ProcurementDtoTransformer;
import com.volvo.gloria.procurematerial.c.QiMarking;
import com.volvo.gloria.procurematerial.c.dto.BuyerCodeDTO;
import com.volvo.gloria.procurematerial.c.dto.MaterialDTO;
import com.volvo.gloria.procurematerial.c.dto.MaterialHeaderDTO;
import com.volvo.gloria.procurematerial.c.dto.OrderLineDTO;
import com.volvo.gloria.procurematerial.c.dto.SupplierDTO;
import com.volvo.gloria.procurematerial.d.entities.Buyer;
import com.volvo.gloria.procurematerial.d.entities.ChangeId;
import com.volvo.gloria.procurematerial.d.entities.DeliverySchedule;
import com.volvo.gloria.procurematerial.d.entities.FinanceHeader;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeaderVersion;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.Order;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLineLastModified;
import com.volvo.gloria.procurematerial.d.entities.OrderLineLog;
import com.volvo.gloria.procurematerial.d.entities.OrderLineVersion;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.Supplier;
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineStatus;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.procurematerial.d.type.request.RequestType;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.util.DeliveryHelper;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * Service Implmentations for ProcurementDtoTransformer.
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ProcurementDtoTransformerBean implements ProcurementDtoTransformer {

    @Inject
    private MaterialHeaderRepository requestHeaderRepository;
    
    @Inject
    private OrderRepository orderRepository;
    
    @Inject
    private QualityDocumentRepository qualityDocumentRepository;

    @Override
    public MaterialDTO transformAsDTO(Material material) {
        if (material != null) {
            MaterialDTO materialDTO = new MaterialDTO();
            ProcureLine procureLine = material.getProcureLine();
            MaterialHeader materialHeader = material.getMaterialHeader();
                       
            ChangeId changeId = material.getAdd();
            materialDTO.setId(material.getMaterialOID());
            materialDTO.setVersion(material.getVersion());
            materialDTO.setIsRead(!material.isAddedAfter());
            materialDTO.setMigrated(material.isMigrated());
            
            FinanceHeader financeHeader = material.getFinanceHeader();
            if (financeHeader != null) {
                materialDTO.setCompanyCode(financeHeader.getCompanyCode());
                materialDTO.setWbsCode(financeHeader.getWbsCode());
                materialDTO.setCostCenter(financeHeader.getCostCenter());
                materialDTO.setGlAccount(financeHeader.getGlAccount());
                materialDTO.setInternalOrderNoSAP(financeHeader.getInternalOrderNoSAP());
                materialDTO.setProjectId(financeHeader.getProjectId());
            }
            OrderLine orderLine = material.getOrderLine();
            if (orderLine != null) {
                materialDTO.setOrderId(orderLine.getOrder().getOrderOID());
                materialDTO.setDeliveryControllerUserId(orderLine.getDeliveryControllerUserId());
                materialDTO.setDeliveryControllerUserName(orderLine.getDeliveryControllerUserName());
            }
            
            materialDTO.setQuantity(material.getQuantity());
            materialDTO.setQuantityInclusiveCancelled(material.getQuantityInclusiveCancelled());
            materialDTO.setUnitOfMeasure(material.getUnitOfMeasure());
            materialDTO.setPartNumber(material.getPartNumber());
            materialDTO.setPartVersion(material.getPartVersion());
            materialDTO.setPartName(material.getPartName());
            materialDTO.setPartModification(material.getPartModification());
            materialDTO.setOrderNo(material.getOrderNo());
            materialDTO.setRequiredStaDate(material.getRequiredStaDate());
            materialDTO.setReceiver(material.getReceiver());
            if (materialHeader != null && !material.getMaterialType().isReleased()) {
                MaterialHeaderVersion acceptedVersion = materialHeader.getAccepted();
                
                materialDTO.setRequestType(materialHeader.getRequestType().name());
                if (!material.getMaterialType().isAdditional()) {
                    materialDTO.setReferenceId(materialHeader.getReferenceId());
                }
                materialDTO.setBuildId(materialHeader.getBuildId());
                materialDTO.setMtrlRequestId(materialHeader.getMtrlRequestId());
                materialDTO.setMaterialControllerUserId(materialHeader.getMaterialControllerUserId());
                materialDTO.setMaterialControllerName(materialHeader.getMaterialControllerName());                
                materialDTO.setBuildName(materialHeader.getBuildName());
                if (materialHeader.getBuildType() != null) {
                    materialDTO.setBuildType(materialHeader.getBuildType().name());
                }                
                if (acceptedVersion != null) {
                    materialDTO.setReferenceGroup(acceptedVersion.getReferenceGroup());
                    materialDTO.setOutboundStartDate(acceptedVersion.getOutboundStartDate());
                    materialDTO.setOutboundLocationId(acceptedVersion.getOutboundLocationId());
                    materialDTO.setContactPersonId(acceptedVersion.getContactPersonId());
                    materialDTO.setContactPersonName(acceptedVersion.getContactPersonName());
                    materialDTO.setRequesterUserId(acceptedVersion.getRequesterUserId());
                    materialDTO.setRequesterName(acceptedVersion.getRequesterName());
                }
            }

            if (changeId != null) {
                materialDTO.setMtrlRequestVersion(material.getMtrlRequestVersionAccepted());
                materialDTO.setMtrlRequestType(changeId.getType().name());
            }

            
            materialDTO.setPartAffiliation(material.getPartAffiliation());
            materialDTO.setFunctionGroup(material.getFunctionGroup());
            materialDTO.setDesignResponsible(material.getDesignResponsible());
            materialDTO.setItemToVariantLinkId(material.getItemToVariantLinkId());
            materialDTO.setModularHarness(material.getModularHarness());
            materialDTO.setMark(material.getMark());
            materialDTO.setMailFormId(material.getMailFormId());            
            
            if (procureLine != null) {
                if (procureLine.getStatus() == ProcureLineStatus.WAIT_TO_PROCURE) {
                    materialDTO.setFinalWhSiteId(material.getMaterialLine().get(0).getFinalWhSiteId());
                }
                materialDTO.setFinalWhSiteNames(material.getFinalWhSiteNames());
                materialDTO.setAdditionalQuantity(procureLine.getAdditionalQuantity());
                materialDTO.setUnitPrice(procureLine.getUnitPrice());
                materialDTO.setCurrency(procureLine.getCurrency());
                materialDTO.setpPartAffiliation(procureLine.getpPartAffiliation());
                materialDTO.setpPartNumber(procureLine.getpPartNumber());
                materialDTO.setpPartVersion(procureLine.getpPartVersion());
                materialDTO.setpPartName(procureLine.getpPartName());
                materialDTO.setDfuObjectNumber(procureLine.getDfuObjectNumber());
                materialDTO.setProcureLineId(procureLine.getProcureLineOid());
                materialDTO.setProcureLineStatus(procureLine.getStatus().name());
                materialDTO.setProcureType(procureLine.getProcureType().toString());
            }

            materialDTO.setMaterialType(material.getMaterialType().name());
            materialDTO.setModificationId(material.getModificationId());
            if (material.getModificationType() != null) {
                materialDTO.setModificationType(material.getModificationType().name());
            }
            
            materialDTO.setAlertPartVersion(material.getMaterialLastModified().isAlertPartVersion());
            materialDTO.setChangeAction(material.getChangeAction());
            materialDTO.setProcureComment(material.getProcureComment());            
            return materialDTO;
        }
        return null;
    }

    @Override
    public  List<MaterialDTO> transformAsMaterialDTOs(List<Material> materials) {
        List<MaterialDTO> materialDTOs = new ArrayList<MaterialDTO>();
        if (!materials.isEmpty()) {
            for (Material material : materials) {
                materialDTOs.add(transformAsDTO(material));
            }
        }
        return materialDTOs;
    }

    @Override
    public BuyerCodeDTO transformAsDTO(Buyer buyerCode) {
        if (buyerCode != null) {
            BuyerCodeDTO buyerCodeDTO = new BuyerCodeDTO();
            buyerCodeDTO.setId(buyerCode.getBuyerOid());
            buyerCodeDTO.setVersion(buyerCode.getVersion());
            buyerCodeDTO.setBuyerId(buyerCode.getCode());
            buyerCodeDTO.setBuyerName(buyerCode.getName());
            return buyerCodeDTO;
        }
        return null;
    }
    
    @Override
    public List<BuyerCodeDTO> transformBuyerCodeEtyToDTO(List<Buyer> buyerCodes) {
        List<BuyerCodeDTO> buyerCodeDTOs = new ArrayList<BuyerCodeDTO>();
        if (!buyerCodes.isEmpty()) {
            for (Buyer buyerCode : buyerCodes) {
                buyerCodeDTOs.add(transformAsDTO(buyerCode));
            }

        }
        return buyerCodeDTOs;
    }

    @Override
    public MaterialHeaderDTO transformAsDTO(MaterialHeader requestHeader) {
        if (requestHeader != null) {
            MaterialHeaderDTO materialHeaderDTO = new MaterialHeaderDTO();
            materialHeaderDTO.setId(requestHeader.getMaterialHeaderOid());
            materialHeaderDTO.setVersion(requestHeader.getVersion());
            
            MaterialHeaderVersion acceptedVersion = requestHeader.getAccepted();
            materialHeaderDTO.setRequesterUserId(acceptedVersion.getRequesterUserId());
            materialHeaderDTO.setRequesterName(acceptedVersion.getRequesterName());
            materialHeaderDTO.setAssignedMaterialControllerId(requestHeader.getMaterialControllerUserId());
            materialHeaderDTO.setAssignedMaterialControllerName(requestHeader.getMaterialControllerName());
            materialHeaderDTO.setAssignedMaterialControllerTeam(requestHeader.getMaterialControllerTeam());
            materialHeaderDTO.setReceivedDateTime(acceptedVersion.getReceivedDateTime());
            materialHeaderDTO.setReferenceGroup(acceptedVersion.getReferenceGroup());
            if (requestHeader.getRequestType() != RequestType.ADDITIONAL_USAGE) {
                materialHeaderDTO.setReferenceId(requestHeader.getReferenceId());
            }
            materialHeaderDTO.setOutboundStartDate(acceptedVersion.getOutboundStartDate());
            materialHeaderDTO.setOutboundLocationId(acceptedVersion.getOutboundLocationId());
            materialHeaderDTO.setCompanyCode(requestHeader.getCompanyCode());
            materialHeaderDTO.setBuildId(requestHeader.getBuildId());
            materialHeaderDTO.setBuildName(requestHeader.getBuildName());
            materialHeaderDTO.setRequestType(requestHeader.getRequestType().name());
            FinanceHeader financeHeader = requestHeaderRepository.findFinanceHeaderByMaterialHeaderOid(requestHeader.getMaterialHeaderOid());
            if (financeHeader != null) {
                materialHeaderDTO.setProjectId(financeHeader.getProjectId());
            }
            materialHeaderDTO.setMtrlRequestVersion(acceptedVersion.getChangeId().getMtrlRequestVersion());
            boolean unassignable = true;
            for (MaterialHeaderVersion materialHeaderVersion : requestHeader.getMaterialHeaderVersions()) {
                if (materialHeaderVersion.getChangeId().getStatus().isChangeInWait()) {
                    unassignable = false;
                    break;
                }
            }
            materialHeaderDTO.setUnAssignable(unassignable);
            return materialHeaderDTO;
        }
        return null;
    }

    @Override
    public List<MaterialHeaderDTO> transformAsDTO(List<MaterialHeader> requestHeaders) {
        List<MaterialHeaderDTO> requestHeaderDTOs = new ArrayList<MaterialHeaderDTO>();
        if (requestHeaders != null && !requestHeaders.isEmpty()) {
            for (MaterialHeader requestHeader : requestHeaders) {
                requestHeaderDTOs.add(transformAsDTO(requestHeader));
            }
        }
        return requestHeaderDTOs;
    }
    

    @Override
    public List<MaterialDTO> transformAsUsageReplacedMaterialDTOs(List<Material> materials) {
        List<MaterialDTO> materialDTOs = new ArrayList<MaterialDTO>();
        if (!materials.isEmpty()) {
            Map<Long, MaterialDTO> materialMap = new HashMap<Long, MaterialDTO>();
            for (Material material : materials) {
                if (material.getMaterialType().equals(MaterialType.MODIFIED)) {
                    MaterialDTO materialDTO = new MaterialDTO();
                    materialDTO.setProcurementQty(material.getQuantity());
                    materialMap.put(material.getMaterialOID(), materialDTO);
                }
            }

            for (Material material : materials) {
                if (material.getMaterialType().equals(MaterialType.USAGE_REPLACED) && materialMap.containsKey(material.getReplacedByOid())) {
                    MaterialDTO materialDTO = materialMap.get(material.getReplacedByOid());
                    materialDTOs.add(transformAsMaterialDTO(material, materialDTO.getProcurementQty()));
                }
            }
        }
        return materialDTOs;
    }

    private static MaterialDTO transformAsMaterialDTO(Material material, long procurementQty) {
        ProcureLine procureLine = material.getProcureLine();
        MaterialHeader materialHeader = material.getMaterialHeader();
        ChangeId changeId = null;
        MaterialDTO materialDTO = new MaterialDTO();
        materialDTO.setId(material.getMaterialOID());
        materialDTO.setVersion(material.getVersion());
        materialDTO.setIsRead(!material.isAddedAfter());
        materialDTO.setMigrated(material.isMigrated());
        materialDTO.setProcurementQty(procurementQty);
        materialDTO.setReceiver(material.getReceiver());
        if (materialHeader != null && !material.getMaterialType().isReleased()) {
            changeId = materialHeader.getAccepted().getChangeId();
            materialDTO.setReferenceId(materialHeader.getReferenceId());
            materialDTO.setOutboundStartDate(materialHeader.getAccepted().getOutboundStartDate());
            materialDTO.setOutboundLocationId(materialHeader.getAccepted().getOutboundLocationId());
            materialDTO.setBuildId(materialHeader.getBuildId());
            materialDTO.setContactPersonId(materialHeader.getAccepted().getContactPersonId());
            materialDTO.setContactPersonName(materialHeader.getAccepted().getContactPersonName());
            materialDTO.setRequesterUserId(materialHeader.getAccepted().getRequesterUserId());
            materialDTO.setRequesterName(materialHeader.getAccepted().getRequesterName());
            if (materialHeader.getBuildType() != null) {
                materialDTO.setBuildType(materialHeader.getBuildType().name());
            }
        }
        materialDTO.setQuantity(material.getQuantity());
        materialDTO.setUnitOfMeasure(material.getUnitOfMeasure());
        materialDTO.setPartNumber(material.getPartNumber());
        materialDTO.setPartVersion(material.getPartVersion());
        materialDTO.setPartName(material.getPartName());
        materialDTO.setPartModification(material.getPartModification());
        materialDTO.setOrderNo(material.getOrderNo());
        materialDTO.setRequiredStaDate(material.getRequiredStaDate());
        
        materialDTO.setMtrlRequestVersion(material.getMtrlRequestVersionAccepted());
        materialDTO.setMtrlRequestType(changeId.getType().name());
        materialDTO.setPartAffiliation(material.getPartAffiliation());
        materialDTO.setFunctionGroup(material.getFunctionGroup());
        materialDTO.setDesignResponsible(material.getDesignResponsible());
        materialDTO.setItemToVariantLinkId(material.getItemToVariantLinkId());
        materialDTO.setModularHarness(material.getModularHarness());
        materialDTO.setMark(material.getMark());
        materialDTO.setMailFormId(material.getMailFormId());
        

        if (procureLine != null) {
            if (procureLine.getStatus() == ProcureLineStatus.WAIT_TO_PROCURE) {
                materialDTO.setFinalWhSiteId(material.getMaterialLine().get(0).getFinalWhSiteId());
            }           
            materialDTO.setAdditionalQuantity(procureLine.getAdditionalQuantity());
            materialDTO.setUnitPrice(procureLine.getUnitPrice());
            materialDTO.setCurrency(procureLine.getCurrency());
            materialDTO.setpPartAffiliation(procureLine.getpPartAffiliation());
            materialDTO.setpPartNumber(procureLine.getpPartNumber());
            materialDTO.setpPartVersion(procureLine.getpPartVersion());
            materialDTO.setpPartName(procureLine.getpPartName());
            materialDTO.setDfuObjectNumber(procureLine.getDfuObjectNumber());
            materialDTO.setProcureLineId(procureLine.getProcureLineOid());
            materialDTO.setProcureLineStatus(procureLine.getStatus().name());
        }
        
        OrderLine orderLine = material.getOrderLine();
        if (orderLine != null) {
            materialDTO.setOrderId(orderLine.getOrder().getOrderOID());
        }

        materialDTO.setMaterialType(material.getMaterialType().name());
        materialDTO.setModificationId(material.getModificationId());
        if (material.getModificationType() != null) {
            materialDTO.setModificationType(material.getModificationType().name());
        }
        return materialDTO;
    }
    
    @Override
    public OrderLineDTO transformAsDTO(OrderLine orderLine, boolean loadDeliverySchedule) {
        OrderLineDTO orderLineDTO = new OrderLineDTO();
        
        transformAsDTOCommon(orderLineDTO, orderLine);
        
        DeliveryHelper.setDataFromMaterial(orderLine.getMaterials(), orderLineDTO);
        DeliveryHelper.setDataFromDeliverySchedule(orderLine.getDeliverySchedule(), orderLineDTO);
        orderLineDTO.setEventTime(DeliveryHelper.getOrderLineLogEventTime(orderLine.getOrderLineLog()));
        
        if (loadDeliverySchedule) {
            orderLineDTO.setDeliveryScheduleDTOs(DeliveryHelper.transformAsDeliveryScheduleDTOs(orderLine.getDeliverySchedule()));
        }
        
        return orderLineDTO;
    }

    @Override
    public List<OrderLineDTO> transformAsOrderLineDTOs(List<OrderLine> orderLines, boolean loadDeliverySchedule) {
        List<OrderLineDTO> orderLineDTOs = new ArrayList<OrderLineDTO>();
        for (OrderLine orderLine : orderLines) {
            orderLineDTOs.add(transformAsDTO(orderLine, loadDeliverySchedule));
        }
        return orderLineDTOs;
    }
    
    @Override
    public OrderLineDTO transformDeliveryScheduleAsOrderLineDTOs(DeliverySchedule deliverySchedule) {
        if (deliverySchedule != null) {
            OrderLine orderLine = deliverySchedule.getOrderLine();
            // Delivery related details to be transformed.
            return transformAsDTO(orderLine, false);
        }
        return null;
    }
    
    @Override
    public List<OrderLineDTO> transformDeliverySchedulesAsOrderLineDTOs(List<DeliverySchedule> deliverySchedules) {
        List<OrderLineDTO> orderLineDTOs = new ArrayList<OrderLineDTO>();
        if (deliverySchedules != null && !deliverySchedules.isEmpty()) {
            for (DeliverySchedule deliverySchedule : deliverySchedules) {
                orderLineDTOs.add(transformDeliveryScheduleAsOrderLineDTOs(deliverySchedule));
            }
        }
        return orderLineDTOs;
    }

    @Override
    public SupplierDTO transformAsDTO(Supplier supplier) {
        SupplierDTO supplierDTO = new SupplierDTO();
        supplierDTO.setAlias(supplier.isAlias());
        supplierDTO.setCurrency(supplier.getCurrency());
        supplierDTO.setDomain(supplier.getDomain());
        supplierDTO.setId(supplier.getSupplierOid());
        supplierDTO.setPartAffiliation(supplier.getPartAffiliation());
        supplierDTO.setPartName(supplier.getPartNumber());
        supplierDTO.setPartVersion(supplier.getPartVersion());
        if (supplier.getPriceUnit() != null && supplier.getPriceUnit().trim().equalsIgnoreCase("PCE")) {
            supplierDTO.setPriceUnit("1");
        } else if (supplier.getPriceUnit() != null && supplier.getPriceUnit().trim().equalsIgnoreCase("PCE2")) {
            supplierDTO.setPriceUnit("100");
        } else if (supplier.getPriceUnit() != null && supplier.getPriceUnit().trim().equalsIgnoreCase("PCE3")) {
            supplierDTO.setPriceUnit("1000");
        }
      
        supplierDTO.setSupplierId(supplier.getSupplierId());
        supplierDTO.setSupplierName(supplier.getSupplierName());
        
        supplierDTO.setUnitPrice(supplier.getUnitPrice());
        supplierDTO.setVersion(supplier.getVersion());
        return supplierDTO;
    }

    @Override
    public List<SupplierDTO> transformAsSuppliersDTO(List<Supplier> suppliers) {
        List<SupplierDTO> supplierDTOs = new ArrayList<SupplierDTO>();
        for (Supplier supplier : suppliers) {
            supplierDTOs.add(transformAsDTO(supplier));
        }
        return supplierDTOs;
    }
    
    /**
     * Due to performance reasons this function is created to pre-fetct all the required data in advance
     */
    @Override
    public List<OrderLineDTO> transformAsOrderLineDTOs(List<OrderLine> orderLines, boolean loadDeliverySchedule, List<Long> orderLineOIDList ) {
        List<OrderLineDTO> orderLineDTOs = new ArrayList<OrderLineDTO>();
       
        if (orderLines != null && !orderLines.isEmpty()) {
            
            List<MaterialLine> materialLineList = requestHeaderRepository.findMaterialLinesByOrderLineList(orderLineOIDList);
            List<DeliverySchedule> deliveryScheduleList = orderRepository.findDeliveryScheduleByOrderLineList(orderLineOIDList);
            List<OrderLineLog> orderLineLogList = orderRepository.findOrderLineLogByOrderLineList(orderLineOIDList);
            
            Map<Long, List<Material>> orderLineIdMaterialMap = new HashMap<Long, List<Material>>();
            Map<Long, List<MaterialLine>> materialIdMaterialLineMap = new HashMap<Long, List<MaterialLine>>();
            Map<Long, List<DeliverySchedule>> orderLineIdDeliveryScheduleMap = new HashMap<Long, List<DeliverySchedule>>();
            Map<Long, List<OrderLineLog>> orderLineIdOrderLineLogMap = new HashMap<Long, List<OrderLineLog>>();
            
            //put all materials and material lines to map
            for (MaterialLine materialLine: materialLineList) {
                Material material = materialLine.getMaterial();
                long materialId = material.getMaterialOID();
                long orderLineId = material.getOrderLine().getOrderLineOID();
            
                List<Material> tempMaterialList = new ArrayList<Material>();
                if (orderLineIdMaterialMap.containsKey(orderLineId)) {
                    tempMaterialList = orderLineIdMaterialMap.get(orderLineId);
                }
                tempMaterialList.add(material);
                orderLineIdMaterialMap.put(orderLineId, tempMaterialList);
                
                List<MaterialLine> tempMaterialLineList = new ArrayList<MaterialLine>();
                if (materialIdMaterialLineMap.containsKey(materialId)) {
                    tempMaterialLineList = materialIdMaterialLineMap.get(materialId);
                }
                tempMaterialLineList.add(materialLine);
                materialIdMaterialLineMap.put(materialId, tempMaterialLineList);
            }
            
            //put all delivery schedules to map
            for (DeliverySchedule deliverySchedule: deliveryScheduleList) {
                long orderLineId = deliverySchedule.getOrderLine().getOrderLineOID();
                List<DeliverySchedule> tempDeliveryScheduleList = new ArrayList<DeliverySchedule>();
                if (orderLineIdDeliveryScheduleMap.containsKey(orderLineId)) {
                    tempDeliveryScheduleList = orderLineIdDeliveryScheduleMap.get(orderLineId);
                }
                tempDeliveryScheduleList.add(deliverySchedule);
                orderLineIdDeliveryScheduleMap.put(orderLineId, tempDeliveryScheduleList);
            }
            
            //put all Order Line Logs to map
            for (OrderLineLog orderLineLog: orderLineLogList) {
                long orderLineId = orderLineLog.getOrderLine().getOrderLineOID();
                List<OrderLineLog> tempOrderLineLogList = new ArrayList<OrderLineLog>();
                if (orderLineIdOrderLineLogMap.containsKey(orderLineId)) {
                    tempOrderLineLogList = orderLineIdOrderLineLogMap.get(orderLineId);
                }
                tempOrderLineLogList.add(orderLineLog);
                orderLineIdOrderLineLogMap.put(orderLineId, tempOrderLineLogList);
            }
            
            for (OrderLine orderLine : orderLines) {
                orderLineDTOs.add(transformAsDTO(orderLine, loadDeliverySchedule, orderLineIdMaterialMap.get(orderLine.getOrderLineOID()),
                                                 materialIdMaterialLineMap, orderLineIdDeliveryScheduleMap.get(orderLine.getOrderLineOID()),
                                                 orderLineIdOrderLineLogMap.get(orderLine.getOrderLineOID())));
            }
        }
       
        return orderLineDTOs;
    }
    
    public void transformAsDTOCommon(OrderLineDTO orderLineDTO, OrderLine orderLine){
        
        Order order = orderLine.getOrder();
        
        orderLineDTO.setId(orderLine.getOrderLineOID());
        orderLineDTO.setVersion(orderLine.getVersion());
        orderLineDTO.setPartNumber(orderLine.getPartNumber());

        OrderLineVersion orderLineVersion = orderLine.getCurrent();
        orderLineDTO.setPartVersion(orderLineVersion.getPartVersion());
        orderLineDTO.setOrderStaDate(orderLineVersion.getOrderStaDate());
        orderLineDTO.setQuantity(orderLineVersion.getQuantity());
        orderLineDTO.setAmount(orderLineVersion.getUnitPrice());
        orderLineDTO.setCurrency(orderLineVersion.getCurrency());
        orderLineDTO.setStaAcceptedDate(orderLineVersion.getStaAcceptedDate());
        orderLineDTO.setStaAgreedDate(orderLineVersion.getStaAgreedDate());
        orderLineDTO.setUnitPrice(orderLineVersion.getUnitPrice());
        orderLineDTO.setPerQuantity(orderLineVersion.getPerQuantity());
        orderLineDTO.setBuyerId(orderLineVersion.getBuyerId());
        orderLineDTO.setBuyerName(orderLineVersion.getBuyerName());
        orderLineDTO.setVersionDate(orderLineVersion.getVersionDate());
        orderLineDTO.setUnitOfPrice(orderLineVersion.getPerQuantity());

        OrderLineLastModified orderLineLastModified = orderLine.getOrderLineLastModified();
        orderLineDTO.setOrderStaChanged(orderLineLastModified.isAlertOrderStaDate());
        orderLineDTO.setAlertPartVersion(orderLineLastModified.isAlertPartVersion());
        orderLineDTO.setAlertQuantiy(orderLineLastModified.isAlertQuantity());

        orderLineDTO.setPartAffiliation(orderLine.getPartAffiliation());
        orderLineDTO.setPartName(orderLine.getPartName());
        orderLineDTO.setSupplierId(order.getSupplierId());
        orderLineDTO.setSupplierName(order.getSupplierName());
        orderLineDTO.setOrderNo(order.getOrderNo());
        orderLineDTO.setUnitOfMeasure(orderLine.getUnitOfMeasure());
        orderLineDTO.setCurrency(orderLine.getCurrency());

        orderLineDTO.setFreightTermCode(orderLine.getFreightTermCode());
        orderLineDTO.setStatus(orderLine.getStatus().name());
        if (orderLine.getCompleteType() != null) {
            orderLineDTO.setCompleteType(orderLine.getCompleteType().name());
        }

        orderLineDTO.setReceivedQuantity(orderLine.getReceivedQuantity());
        orderLineDTO.setOrderId(order.getOrderOID());
        orderLineDTO.setProjectNumber(orderLine.getProjectId());

        orderLineDTO.setOrderDateTime(order.getOrderDateTime());
        orderLineDTO.setFreeText(orderLine.getFreeText());
        orderLineDTO.setDeliveryDeviation(orderLine.isDeliveryDeviation());

        orderLineDTO.setDeliveryControllerUserId(orderLine.getDeliveryControllerUserId());
        orderLineDTO.setDeliveryControllerUserName(orderLine.getDeliveryControllerUserName());
        orderLineDTO.setDeliveryControllerTeam(order.getDeliveryControllerTeam());

        orderLineDTO.setSuffix(order.getSuffix());

        QiMarking qiMarking = orderLine.getQiMarking();
        if (qiMarking != null && qiMarking != QiMarking.VISUAL) {
            orderLineDTO.setQiMarking(qiMarking.name());
        }
        
        ProcureLine procureLine = orderLine.getProcureLine();
        if (procureLine != null && procureLine.getQualityDocumentOID() != null) {
            Long qualityDocumentId = procureLine.getQualityDocumentOID();
            QualityDocument qualityDocument = qualityDocumentRepository.findById(qualityDocumentId);
            if (qualityDocument != null) {
                orderLineDTO.setQualityDocumentName(qualityDocument.getName());
            }
        }
        
        DeliveryHelper.setDataFromProcureLine(procureLine, orderLineDTO);

        orderLineDTO.setProjectId(orderLine.getProjectId());
        orderLineDTO.setMarkedAsComplete(orderLine.getStatus().isMarkedAsComplete(orderLine));
        orderLineDTO.setAllowedQuantity(orderLine.getPossibleToReceiveQuantity());
        orderLineDTO.setPartAlias(orderLine.getSupplierPartNo());

        orderLineDTO.setInternalExternal(order.getInternalExternal().name());
        orderLineDTO.setLastModifiedByUserId(orderLine.getOrderLineLastModified().getModifiedByUserId());
        orderLineDTO.setLastModifiedDate(orderLine.getOrderLineLastModified().getModifiedTime());
        orderLineDTO.setPossibleToReceiveQty(orderLine.getPossibleToReceiveQuantity());
        if (procureLine != null) {
            orderLineDTO.setMaterialControllerUserId(procureLine.getMaterialControllerId());
            orderLineDTO.setMaterialControllerUserName(procureLine.getMaterialControllerName());
        }
        orderLineDTO.setContentEdited(orderLine.isContentEdited());
    }

    @Override
    public OrderLineDTO transformAsDTO(OrderLine orderLine, boolean loadDeliverySchedule, List<Material> materials,
            Map<Long, List<MaterialLine>> materialIdMaterialLineMap, List<DeliverySchedule> deliverySchedules, List<OrderLineLog> orderLineLogs) {
        OrderLineDTO orderLineDTO = new OrderLineDTO();
        
        transformAsDTOCommon(orderLineDTO, orderLine);

        DeliveryHelper.setDataFromMaterial(materials, orderLineDTO, materialIdMaterialLineMap);
        DeliveryHelper.setDataFromDeliverySchedule(deliverySchedules, orderLineDTO);
        orderLineDTO.setEventTime(DeliveryHelper.getOrderLineLogEventTime(orderLineLogs));

        
        if (loadDeliverySchedule) {
            orderLineDTO.setDeliveryScheduleDTOs(DeliveryHelper.transformAsDeliveryScheduleDTOs(deliverySchedules));
        }

        return orderLineDTO;
    }

}
