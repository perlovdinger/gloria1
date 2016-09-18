package com.volvo.gloria.procurematerial.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


import org.apache.commons.lang.StringUtils;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.common.c.dto.DeliveryLogDTO;
import com.volvo.gloria.common.d.entities.Traceability;
import com.volvo.gloria.procurematerial.b.DeliveryServices;
import com.volvo.gloria.procurematerial.c.dto.DeliveryNoteDTO;
import com.volvo.gloria.procurematerial.c.dto.DeliveryNoteLineDTO;
import com.volvo.gloria.procurematerial.c.dto.DeliveryNoteSubLineDTO;
import com.volvo.gloria.procurematerial.c.dto.DeliveryScheduleDTO;
import com.volvo.gloria.procurematerial.c.dto.GoodsReceiptLineDTO;
import com.volvo.gloria.procurematerial.c.dto.OrderDTO;
import com.volvo.gloria.procurematerial.c.dto.OrderLineDTO;
import com.volvo.gloria.procurematerial.c.dto.OrderLineLogDTO;
import com.volvo.gloria.procurematerial.c.dto.OrderLineVersionDTO;
import com.volvo.gloria.procurematerial.c.dto.OrderLogDTO;
import com.volvo.gloria.procurematerial.c.dto.OrderlineTracebilityDTO;
import com.volvo.gloria.procurematerial.c.dto.TransportLabelDTO;
import com.volvo.gloria.procurematerial.d.entities.AttachedDoc;
import com.volvo.gloria.procurematerial.d.entities.DeliveryLog;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNote;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteSubLine;
import com.volvo.gloria.procurematerial.d.entities.DeliverySchedule;
import com.volvo.gloria.procurematerial.d.entities.FinanceHeader;
import com.volvo.gloria.procurematerial.d.entities.GoodsReceiptLine;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.Order;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLineLog;
import com.volvo.gloria.procurematerial.d.entities.OrderLineVersion;
import com.volvo.gloria.procurematerial.d.entities.OrderLog;
import com.volvo.gloria.procurematerial.d.entities.ProblemDoc;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.QiDoc;
import com.volvo.gloria.procurematerial.d.entities.ReceiveDoc;
import com.volvo.gloria.procurematerial.d.entities.Supplier;
import com.volvo.gloria.procurematerial.d.entities.TransportLabel;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.DocumentDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.Utils;
import com.volvo.gloria.util.c.GloriaParams;
import com.volvo.gloria.warehouse.b.WarehouseServices;

/**
 * Entity transformation for delivery module.
 * 
 */
public class DeliveryHelper {

    protected DeliveryHelper() {

    }

    public static OrderLineVersionDTO transformAsDTO(OrderLineVersion orderLineVersion) {
        OrderLineVersionDTO orderLineVersionDTO = new OrderLineVersionDTO();
        if (orderLineVersion != null) {
            orderLineVersionDTO.setId(orderLineVersion.getOrderLineVersionOid());
            orderLineVersionDTO.setCurrency(orderLineVersion.getCurrency());
            orderLineVersionDTO.setOrderStaDate(orderLineVersion.getOrderStaDate());
            orderLineVersionDTO.setPartVersion(orderLineVersion.getPartVersion());
            orderLineVersionDTO.setQuantity(orderLineVersion.getQuantity());
            orderLineVersionDTO.setRevisionId(orderLineVersion.getRevisionId());
            orderLineVersionDTO.setStaAcceptedDate(orderLineVersion.getStaAcceptedDate());
            orderLineVersionDTO.setUnitPrice(orderLineVersion.getUnitPrice());
            orderLineVersionDTO.setVersion(orderLineVersion.getVersionNo());
            orderLineVersionDTO.setVersionDate(orderLineVersion.getVersionDate());
            orderLineVersionDTO.setBuyerId(orderLineVersion.getBuyerId());
            if (orderLineVersion.getPriceType() != null) {
                orderLineVersionDTO.setPriceType(orderLineVersion.getPriceType().name());
            }
        }
        return orderLineVersionDTO;
    }

    public static List<OrderLineVersionDTO> transformAsDTOs(List<OrderLineVersion> orderLineVersions) {
        List<OrderLineVersionDTO> orderLineVersionDTOs = new ArrayList<OrderLineVersionDTO>();
        for (OrderLineVersion orderLineVersion : orderLineVersions) {
            orderLineVersionDTOs.add(transformAsDTO(orderLineVersion));
        }
        return orderLineVersionDTOs;
    }


    public static Date getOrderLineLogEventTime(List<OrderLineLog> orderLineLogList) {
        Date eventTime = null;
        if (orderLineLogList != null) {
            for (OrderLineLog orderLineLog : orderLineLogList) {
                Date time = orderLineLog.getEventTime();
                if (eventTime != null) {
                    if (time != null && time.after(eventTime)) {
                        eventTime = time;
                    }
                } else {
                    eventTime = time;
                }

            }
        }
        return eventTime;
    }

    public static void setDataFromMaterial(List<Material> materials, OrderLineDTO orderLineDTO) {
        if (materials != null && !materials.isEmpty()) {

            orderLineDTO.setDirectSend(evaluateDirectSend(materials));
            // sort to pick the earliest buildStartDate from Materials.
            Collections.sort(materials, new Comparator<Material>() {
                public int compare(Material materialOne, Material materialTwo) {
                    if (materialOne.getMaterialHeader() == null || materialOne.getMaterialHeader().getAccepted().getOutboundStartDate() == null
                            || materialTwo.getMaterialHeader() == null || materialTwo.getMaterialHeader().getAccepted().getOutboundStartDate() == null) {
                        return 1;
                    }
                    return materialTwo.getMaterialHeader().getAccepted().getOutboundStartDate()
                                      .compareTo(materialOne.getMaterialHeader().getAccepted().getOutboundStartDate());
                }
            });
            MaterialHeader materialHeader = materials.get(0).getMaterialHeader();
            if (materialHeader != null) {
                orderLineDTO.setBuildStartDate(materialHeader.getAccepted().getOutboundStartDate());
            }

            List<String> referenceIDs = new ArrayList<String>();
            List<String> mailFormIds = new ArrayList<String>();
            String partModification = new String();
            for (Material material : materials) {
                String referenceId = null;

                if (material.getMaterialHeader() != null) {
                    referenceId = material.getMaterialHeader().getReferenceId();
                }
                if (referenceId != null && !referenceIDs.contains(referenceId)) {
                    referenceIDs.add(referenceId);
                }
                String mailFormId = material.getMailFormId();
                if (mailFormId != null && !referenceIDs.contains(mailFormId)) {
                    mailFormIds.add(mailFormId);
                }
                if (material.getMaterialType() == MaterialType.ADDITIONAL) {
                    orderLineDTO.setAdditionalQuantity(material.getQuantity());
                }
                String partModi = material.getPartModification();
                if (StringUtils.isNotBlank(partModi)) {
                    partModification = partModi;
                }
            }
            orderLineDTO.setReferenceId(StringUtils.join(referenceIDs, ","));
            orderLineDTO.setMailFormIds(StringUtils.join(mailFormIds, ","));
            orderLineDTO.setMaterialPartModification(partModification);
        }
    }

    private static boolean evaluateDirectSend(List<Material> materials) {
        for (Material material : materials) {
            if (material.getMaterialType() == MaterialType.USAGE || material.getMaterialType() == MaterialType.MODIFIED
                    || material.getMaterialType() == MaterialType.ADDITIONAL || material.getMaterialType() == MaterialType.ADDITIONAL_USAGE) {
                for (MaterialLine materialLine : material.getMaterialLine()) {
                    if (materialLine.getDirectSend().isDirectSend()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void sortOrderLineVersions(List<OrderLineVersion> orderLineVersions) {
        // sorts in reverse order
        Collections.sort(orderLineVersions, new Comparator<OrderLineVersion>() {
            public int compare(OrderLineVersion orderLineVersionOne, OrderLineVersion orderLineVersionTwo) {
                if (orderLineVersionOne.getOrderLineVersionOid() > (orderLineVersionTwo.getOrderLineVersionOid())) {
                    return -1;
                }
                return 1;
            }
        });
    }

    public static void setDataFromDeliverySchedule(List<DeliverySchedule> deliverySchedules, OrderLineDTO orderLineDTO) {
        Date plannedDispatchDate = null;
        if (deliverySchedules != null && !deliverySchedules.isEmpty()) {
            for (DeliverySchedule deliverySchedule : deliverySchedules) {
                orderLineDTO.setExpectedQty(deliverySchedule.getExpectedQuantity());
                orderLineDTO.setExpectedDate(deliverySchedule.getExpectedDate());
                if (deliverySchedule.getStatusFlag() != null) {
                    orderLineDTO.setStatusFlag(deliverySchedule.getStatusFlag().name());
                }   
                
                Date plannedDate = deliverySchedule.getPlannedDispatchDate();
                if (plannedDispatchDate != null) {
                    if (plannedDate != null && plannedDate.after(plannedDispatchDate)) {
                        plannedDispatchDate = plannedDate;
                    }
                } else {
                    plannedDispatchDate = plannedDate;
                }
            }
        }
        orderLineDTO.setPlannedDispatchDate(plannedDispatchDate);
    }

    public static void setDataFromProcureLine(ProcureLine procureLine, OrderLineDTO orderLineDTO) {
        if (procureLine != null) {
            orderLineDTO.setPartModification(procureLine.getpPartModification());
            orderLineDTO.setReferenceGroups(procureLine.getReferenceGroups());
            orderLineDTO.setDfuObjectNumber(procureLine.getDfuObjectNumber());
            orderLineDTO.setInternalOrderNo(procureLine.getOrderNo());
            orderLineDTO.setProcureInfo(procureLine.getProcureInfo());
            Supplier supplier = procureLine.getSupplier();
            if(supplier!=null){
                orderLineDTO.setConsignorName(supplier.getSupplierId());
                orderLineDTO.setConsignorName(supplier.getSupplierName());
            }         
            orderLineDTO.setRequisitionId(procureLine.getRequisitionId());
            orderLineDTO.setProcureDate(procureLine.getProcureDate());
            orderLineDTO.setWarehouseComment(procureLine.getWarehouseComment());
            orderLineDTO.setReference(procureLine.getReferenceGps());
            orderLineDTO.setRequiredStaDate(procureLine.getRequiredStaDate());
            
            FinanceHeader financeHeader = procureLine.getFinanceHeader();
            orderLineDTO.setWbsCode(financeHeader.getWbsCode());
            orderLineDTO.setCostCenter(financeHeader.getCostCenter());
            orderLineDTO.setGlAccount(financeHeader.getGlAccount());
            orderLineDTO.setInternalOrderNoSAP(financeHeader.getInternalOrderNoSAP());
            
        }
    }

    public static OrderDTO transformAsDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getOrderOID());
        orderDTO.setVersion(order.getVersion());
        orderDTO.setOrderNo(order.getOrderNo());
        orderDTO.setOrderDateTime(order.getOrderDateTime());

        OrderLine orderLine = order.getOrderLines().get(0);
        OrderLineVersion orderLineVersion = orderLine.getCurrent();
        orderDTO.setBuyerCode(orderLineVersion.getBuyerId());
        orderDTO.setBuyerName(orderLineVersion.getBuyerName());

        orderDTO.setSupplierId(order.getSupplierId());
        orderDTO.setSupplierName(order.getSupplierName());
        orderDTO.setSuffix(order.getSuffix());
        orderDTO.setProjectId(orderLine.getProjectId());
        orderDTO.setDeiveryControllerTeam(order.getDeliveryControllerTeam());
        orderDTO.setDeliveryControllerUserId(orderLine.getDeliveryControllerUserId());
        orderDTO.setInternalExternal(order.getInternalExternal().name());
        return orderDTO;
    }

    public static List<OrderDTO> transformAsDTO(List<Order> orders) {
        List<OrderDTO> orderDTOs = new ArrayList<OrderDTO>();
        for (Order order : orders) {
            orderDTOs.add(transformAsDTO(order));
        }
        return orderDTOs;
    }

    public static List<DeliveryNoteDTO> transaformAsDeliveryNoteDTOs(List<DeliveryNote> deliveryNotes) {
        List<DeliveryNoteDTO> deliveryNoteDTOs = new ArrayList<DeliveryNoteDTO>();
        for (DeliveryNote deliveryNote : deliveryNotes) {
            deliveryNoteDTOs.add(transformAsDTO(deliveryNote));
        }
        return deliveryNoteDTOs;
    }

    public static DeliveryNoteDTO transformAsDTO(DeliveryNote deliveryNote) {
        if (deliveryNote != null) {
            DeliveryNoteDTO deliveryNoteDTO = new DeliveryNoteDTO();
            deliveryNoteDTO.setId(deliveryNote.getId());
            deliveryNoteDTO.setVersion(deliveryNote.getVersion());
            deliveryNoteDTO.setReceiveType(deliveryNote.getReceiveType().name());
            deliveryNoteDTO.setCarrier(deliveryNote.getCarrier());
            deliveryNoteDTO.setDeliveryNoteDate(deliveryNote.getDeliveryNoteDate());
            deliveryNoteDTO.setDeliveryNoteNo(deliveryNote.getDeliveryNoteNo());
            deliveryNoteDTO.setSupplierId(deliveryNote.getSupplierId());
            deliveryNoteDTO.setTransportationNo(deliveryNote.getTransportationNo());
            deliveryNoteDTO.setWhSiteId(deliveryNote.getWhSiteId());
            deliveryNoteDTO.setOrderNo(deliveryNote.getOrderNo());
            deliveryNoteDTO.setSupplierName(deliveryNote.getSupplierName());

            List<DeliveryNoteLine> deliveryNoteLines = deliveryNote.getDeliveryNoteLine();
            if (deliveryNoteLines != null && !deliveryNoteLines.isEmpty()) {
                deliveryNoteDTO.setProjectId(deliveryNoteLines.get(0).getProjectId());
            }
            return deliveryNoteDTO;
        }
        return null;
    }

    public static List<DeliveryNoteSubLineDTO> transaformAsDeliveryNoteSubLineDTOs(List<DeliveryNoteSubLine> deliveryNoteSubLines) {
        List<DeliveryNoteSubLineDTO> deliveryNoteSubLineDTOs = new ArrayList<DeliveryNoteSubLineDTO>();
        for (DeliveryNoteSubLine deliveryNoteSubLine : deliveryNoteSubLines) {
            deliveryNoteSubLineDTOs.add(transformAsDTO(deliveryNoteSubLine));
        }
        return deliveryNoteSubLineDTOs;
    }

    public static DeliveryNoteSubLineDTO transformAsDTO(DeliveryNoteSubLine deliveryNoteSubLine) {
        if (deliveryNoteSubLine != null) {
            DeliveryNoteSubLineDTO deliveryNoteSubLineDTO = new DeliveryNoteSubLineDTO();
            deliveryNoteSubLineDTO.setId(deliveryNoteSubLine.getDeliveryNoteSubLineOID());
            deliveryNoteSubLineDTO.setVersion(deliveryNoteSubLine.getVersion());
            deliveryNoteSubLineDTO.setToReceiveQty(deliveryNoteSubLine.getToReceiveQty());
            deliveryNoteSubLineDTO.setDirectSend(deliveryNoteSubLine.isDirectSend());
            deliveryNoteSubLineDTO.setNextZoneCode(deliveryNoteSubLine.getNextZoneCode());
            deliveryNoteSubLineDTO.setToApproveQty(deliveryNoteSubLine.getToApproveQty());
            deliveryNoteSubLineDTO.setBinLocation(deliveryNoteSubLine.getBinLocation());
            deliveryNoteSubLineDTO.setBinLocationCode(deliveryNoteSubLine.getBinLocationCode());
            TransportLabel transportLabel = deliveryNoteSubLine.getTransportLabel();
            if (transportLabel != null) {
                deliveryNoteSubLineDTO.setTransportLabelId(transportLabel.getTransportLabelOid());
                deliveryNoteSubLineDTO.setTransportLabelCode(transportLabel.getCode());
            }
            return deliveryNoteSubLineDTO;
        }
        return null;
    }

    public static List<DeliveryNoteLineDTO> transaformAsDeliveryNoteLineDTOs(List<DeliveryNoteLine> deliveryNoteLines, String materialLineStatus) {
        List<DeliveryNoteLineDTO> deliveryNoteDTOs = new ArrayList<DeliveryNoteLineDTO>();
        for (DeliveryNoteLine deliveryNoteLine : deliveryNoteLines) {
            deliveryNoteDTOs.add(transformAsDTO(deliveryNoteLine, materialLineStatus));
        }
        return deliveryNoteDTOs;
    }

    public static DeliveryNoteLineDTO transformAsDTO(DeliveryNoteLine deliveryNoteLine, String materialLineStatus) {
        if (deliveryNoteLine != null) { 
            DeliveryNoteLineDTO deliveryNoteLineDTO = new DeliveryNoteLineDTO();
            deliveryNoteLineDTO.setId(deliveryNoteLine.getDeliveryNoteLineOID());
            deliveryNoteLineDTO.setVersion(deliveryNoteLine.getVersion());

            long receivedQuantity = deliveryNoteLine.getReceivedQuantity();
            deliveryNoteLineDTO.setReceivedQuantity(receivedQuantity);
            long deliveryNoteQuantity = deliveryNoteLine.getDeliveryNoteQuantity();
            deliveryNoteLineDTO.setDeliveryNoteQuantity(deliveryNoteQuantity);

            deliveryNoteLineDTO.setDamagedQuantity(deliveryNoteLine.getDamagedQuantity());
            deliveryNoteLineDTO.setExpirationDate(deliveryNoteLine.getExpirationDate());
            if (deliveryNoteLine.getStatus() != null) {
                deliveryNoteLineDTO.setStatus(deliveryNoteLine.getStatus().name());
            }

            DeliveryNote deliveryNote = deliveryNoteLine.getDeliveryNote();
            deliveryNoteLineDTO.setDeliveryNoteId(deliveryNote.getDeliveryNoteOID());

            deliveryNote.getReceiveType().setOrderOrMaterialInfo(deliveryNoteLine, deliveryNoteLineDTO);

            deliveryNoteLineDTO.setSendToQI(deliveryNoteLine.isSendToQI());
            deliveryNoteLineDTO.setHasMeasuringDoc(deliveryNoteLine.isHasMeasuringDoc());
            deliveryNoteLineDTO.setHasControlCertificateDoc(deliveryNoteLine.isHasControlCertificateDoc());
            deliveryNoteLineDTO.setHasDamagedParts(deliveryNoteLine.isHasDamagedParts());
            deliveryNoteLineDTO.setHasMissingInfo(deliveryNoteLine.isHasMissingInfo());
            deliveryNoteLineDTO.setHasOverDelivery(deliveryNoteLine.isHasOverDeliveries());
            deliveryNoteLineDTO.setProblemDescription(deliveryNoteLine.getProblemDescription());
            deliveryNoteLineDTO.setQualityInspectionComment(deliveryNoteLine.getQualityInspectionComment());
            deliveryNoteLineDTO.setDeliveryNoteDate(deliveryNote.getDeliveryNoteDate());
            deliveryNoteLineDTO.setCarrier(deliveryNote.getCarrier());
            deliveryNoteLineDTO.setTransportationNo(deliveryNote.getTransportationNo());
            deliveryNoteLineDTO.setDirectSendQuantity(deliveryNoteLine.getDirectSendQuantity());

            deliveryNoteLineDTO.setReceivedDetailsUpdated(deliveryNoteLine.isReceivedDetailsUpdated());
            deliveryNoteLineDTO.setQiDetailsUpdated(deliveryNoteLine.isQiDetailsUpdated());

            deliveryNoteLineDTO.setPartModification(deliveryNoteLine.getPartModification());
            if (deliveryNoteLine.getProcureType() != null) {
                deliveryNoteLineDTO.setProcureType(deliveryNoteLine.getProcureType().name());
            }
            deliveryNoteLineDTO.setDangerousGoodsFlag(deliveryNoteLine.isDangerousGoodsFlag());
            deliveryNoteLineDTO.setProcureInfo(deliveryNoteLine.getProcureInfo());
            deliveryNoteLineDTO.setProjectId(deliveryNoteLine.getProjectId());
            deliveryNoteLineDTO.setReceiveType(deliveryNote.getReceiveType().name());

            calculateBlockedQtyByStatus(deliveryNoteLine, deliveryNoteLineDTO);

            if (!StringUtils.isEmpty(materialLineStatus)) {
                calculateDirectSendQtyByStatus(deliveryNoteLine, MaterialLineStatus.valueOf(materialLineStatus), deliveryNoteLineDTO);
            }
            
            deliveryNoteLineDTO.setSupplierId(deliveryNoteLine.getDeliveryNote().getSupplierId());
            deliveryNoteLineDTO.setSupplierName(deliveryNoteLine.getDeliveryNote().getSupplierName());
            List<MaterialLine> materialLines = deliveryNoteLine.getMaterialLine();
            
            deliveryNoteLineDTO.setQualityDocumentName(deliveryNoteLine.getQualityDocName());
            
            /**
             * ?? do we need this code below ??
             * 
             */
            if (materialLines != null) {
                for (MaterialLine materialLine : materialLines) {
                    deliveryNoteLineDTO.setOrderLineReceivedQuantity(materialLine.getQuantity());
                    deliveryNoteLineDTO.setUnitOfMeasure(materialLine.getMaterial().getUnitOfMeasure());
                    deliveryNoteLineDTO.setOrderNo(materialLine.getMaterial().getOrderNo());
                }
            }
            return deliveryNoteLineDTO;
        }
        return null;
    }

    public static void calculateBlockedQtyByStatus(DeliveryNoteLine deliveryNoteLine, DeliveryNoteLineDTO deliveryNoteLineDTO) {
        List<MaterialLine> materialLines = deliveryNoteLine.getMaterialLine();
        long blockedQty = 0;
        if (materialLines != null && !materialLines.isEmpty()) {
            for (MaterialLine materialLine : materialLines) {
                if (materialLine.getStatus().equals(MaterialLineStatus.BLOCKED)) {
                    blockedQty += materialLine.getQuantity();
                }
            }
            deliveryNoteLineDTO.setBlockedQuantity(blockedQty);
        }
    }

    private static void calculateDirectSendQtyByStatus(DeliveryNoteLine deliveryNoteLine, MaterialLineStatus materialLineStatus,
            DeliveryNoteLineDTO deliveryNoteLineDTO) {
        List<MaterialLine> materialLines = deliveryNoteLine.getMaterialLine();
        long directSendQty = 0;
        if (materialLines != null && !materialLines.isEmpty()) {
            for (MaterialLine materialLine : materialLines) {
                boolean directSend = materialLine.getDirectSend().isDirectSend();
                if (materialLine.getStatus().equals(materialLineStatus) && directSend) {
                    directSendQty += materialLine.getQuantity();
                }
            }
            deliveryNoteLineDTO.setDirectSendQuantity(directSendQty);
        }
    }


    public static List<DocumentDTO> transformQiDocsAsDocumentDTO(List<QiDoc> qualityDocs, boolean isContentRequired) {
        List<DocumentDTO> documentDTOs = new ArrayList<DocumentDTO>();
        if (qualityDocs != null && !qualityDocs.isEmpty()) {
            for (QiDoc qualityDoc : qualityDocs) {
                documentDTOs.add(transformAsDTO(qualityDoc, isContentRequired));
            }
        }
        return documentDTOs;
    }

    public static DocumentDTO transformAsDTO(QiDoc qualityDoc, boolean isContentRequired) {
        DocumentDTO documentDTO = new DocumentDTO();
        documentDTO.setCategoryType(GloriaParams.DOCUMENT_CATEGORY_QUALITYDOCS);
        if (qualityDoc.getFileContent() != null) {
            if (isContentRequired) {
                documentDTO.setContent(Arrays.copyOf(qualityDoc.getFileContent(), qualityDoc.getFileContent().length));
            }
            documentDTO.setSize(qualityDoc.getFileContent().length);
        }
        documentDTO.setId(qualityDoc.getQiDocOID());
        documentDTO.setName(qualityDoc.getDocumentName());
        documentDTO.setUrl(GloriaParams.BASE_URL_INSPECTIONDOCS + "/" + qualityDoc.getQiDocOID());
        return documentDTO;
    }

    public static List<DocumentDTO> transformProblemDocsAsDocumentDTO(List<ProblemDoc> problemDocs, boolean isContentRequired) {
        List<DocumentDTO> documentDTOs = new ArrayList<DocumentDTO>();
        if (problemDocs != null && !problemDocs.isEmpty()) {
            for (ProblemDoc problemDoc : problemDocs) {
                documentDTOs.add(transformAsDTO(problemDoc, isContentRequired));
            }
        }
        return documentDTOs;
    }

    public static DocumentDTO transformAsDTO(ProblemDoc problemDoc, boolean isContentRequired) {
        DocumentDTO documentDTO = new DocumentDTO();
        documentDTO.setCategoryType(GloriaParams.DOCUMENT_CATEGORY_PROBLEMNOTES);
        if (problemDoc.getFileContent() != null) {
            if (isContentRequired) {
                documentDTO.setContent(Arrays.copyOf(problemDoc.getFileContent(), problemDoc.getFileContent().length));
            }
            documentDTO.setSize(problemDoc.getFileContent().length);
        }
        documentDTO.setId(problemDoc.getProblemDocOID());
        documentDTO.setName(problemDoc.getDocumentName());
        documentDTO.setUrl(GloriaParams.BASE_URL_PROBLEMNOTEDOCS + "/" + problemDoc.getProblemDocOID());
        return documentDTO;
    }

    public static List<DeliveryLogDTO> transaformAsDeliveryLogDTOs(List<DeliveryLog> deliveryLogs) {
        List<DeliveryLogDTO> deliveryLogDTOs = new ArrayList<DeliveryLogDTO>();
        if (deliveryLogs != null && !deliveryLogs.isEmpty()) {
            for (DeliveryLog deliveryLog : deliveryLogs) {
                deliveryLogDTOs.add(transformAsDTO(deliveryLog));
            }
        }
        return deliveryLogDTOs;
    }

    public static DeliveryLogDTO transformAsDTO(DeliveryLog deliveryLog) {
        if (deliveryLog != null) {
            DeliveryLogDTO deliveryLogDTO = new DeliveryLogDTO();
            deliveryLogDTO.setId(deliveryLog.getEventLogOid());
            deliveryLogDTO.setEventOriginatorId(deliveryLog.getEventOriginatorId());
            deliveryLogDTO.setEventOriginatorName(deliveryLog.getEventOriginatorName());
            deliveryLogDTO.setEventTime(deliveryLog.getEventTime());
            deliveryLogDTO.setEventValue(deliveryLog.getEventValue());
            return deliveryLogDTO;
        }
        return null;
    }

    public static List<OrderLogDTO> transaformAsOrderLogDTOs(List<OrderLog> orderLogs) {
        List<OrderLogDTO> orderLogDTOs = new ArrayList<OrderLogDTO>();
        if (orderLogs != null && !orderLogs.isEmpty()) {
            for (OrderLog orderLog : orderLogs) {
                orderLogDTOs.add(transformAsDTO(orderLog));
            }
        }
        return orderLogDTOs;
    }

    public static OrderLogDTO transformAsDTO(OrderLog orderLog) {
        if (orderLog != null) {
            OrderLogDTO orderLogDTO = new OrderLogDTO();
            orderLogDTO.setId(orderLog.getEventLogOid());
            orderLogDTO.setEventOriginatorId(orderLog.getEventOriginatorId());
            orderLogDTO.setEventOriginatorName(orderLog.getEventOriginatorName());
            orderLogDTO.setEventTime(orderLog.getEventTime());
            orderLogDTO.setEventValue(orderLog.getEventValue());
            return orderLogDTO;
        }
        return null;
    }

    public static List<DeliveryScheduleDTO> transformAsDeliveryScheduleDTOs(List<DeliverySchedule> deliverySchedules) {
        List<DeliveryScheduleDTO> deliveryScheduleDTOs = new ArrayList<DeliveryScheduleDTO>();
        if (deliverySchedules != null && !deliverySchedules.isEmpty()) {
            for (DeliverySchedule deliverySchedule : deliverySchedules) {
                deliveryScheduleDTOs.add(transformAsDTO(deliverySchedule));
            }
        }
        return deliveryScheduleDTOs;
    }

    public static DeliveryScheduleDTO transformAsDTO(DeliverySchedule deliverySchedule) {
        if (deliverySchedule != null) {
            DeliveryScheduleDTO deliveryScheduleDTO = new DeliveryScheduleDTO();
            deliveryScheduleDTO.setId(deliverySchedule.getDeliveryScheduleOID());
            deliveryScheduleDTO.setVersion(deliverySchedule.getVersion());
            deliveryScheduleDTO.setExpectedQuantity(deliverySchedule.getExpectedQuantity());
            deliveryScheduleDTO.setExpectedDate(deliverySchedule.getExpectedDate());
            if (deliverySchedule.getExpectedDate() != null) {
                deliveryScheduleDTO.setMarkPassedDate(DateUtil.getDateWithZeroTime(deliverySchedule.getExpectedDate())
                                                              .before(DateUtil.getDateWithZeroTime(DateUtil.getSqlDate())));
            }
            if (deliverySchedule.getStatusFlag() != null) {
                deliveryScheduleDTO.setStatusFlag(deliverySchedule.getStatusFlag().name());
            }
            deliveryScheduleDTO.setPlannedDispatchDate(deliverySchedule.getPlannedDispatchDate());
            return deliveryScheduleDTO;
        }
        return null;
    }

    public static List<OrderLineLogDTO> transaformAsOrderLineLogDTOs(List<OrderLineLog> orderLineLogs) {
        List<OrderLineLogDTO> orderLogDTOs = new ArrayList<OrderLineLogDTO>();
        if (orderLineLogs != null && !orderLineLogs.isEmpty()) {
            for (OrderLineLog orderLineLog : orderLineLogs) {
                orderLogDTOs.add(transformAsDTO(orderLineLog));
            }
        }
        return orderLogDTOs;
    }

    public static OrderLineLogDTO transformAsDTO(OrderLineLog orderLineLog) {
        if (orderLineLog != null) {
            OrderLineLogDTO orderLineLogDTO = new OrderLineLogDTO();
            orderLineLogDTO.setId(orderLineLog.getEventLogOid());
            orderLineLogDTO.setEventOriginatorId(orderLineLog.getEventOriginatorId());
            orderLineLogDTO.setEventOriginatorName(orderLineLog.getEventOriginatorName());
            orderLineLogDTO.setEventTime(orderLineLog.getEventTime());
            orderLineLogDTO.setEventValue(orderLineLog.getEventValue());
            return orderLineLogDTO;
        }
        return null;
    }

    public static List<DocumentDTO> transformAttachedDocsAsDocumentDTO(List<AttachedDoc> attachedDocs, boolean isContentRequired) {
        List<DocumentDTO> documentDTOs = new ArrayList<DocumentDTO>();
        if (attachedDocs != null && !attachedDocs.isEmpty()) {
            for (AttachedDoc attachedDoc : attachedDocs) {
                documentDTOs.add(transformAsDTO(attachedDoc, isContentRequired));
            }
        }
        return documentDTOs;
    }

    public static DocumentDTO transformAsDTO(AttachedDoc attachedDoc, boolean isContentRequired) {
        DocumentDTO documentDTO = new DocumentDTO();
        documentDTO.setCategoryType(GloriaParams.DOCUMENT_CATEGORY_ATTACHEDDOCS);
        if (attachedDoc.getFileContent() != null) {
            if (isContentRequired) {
                documentDTO.setContent(Arrays.copyOf(attachedDoc.getFileContent(), attachedDoc.getFileContent().length));
            }
            documentDTO.setSize(attachedDoc.getFileContent().length);
        }
        documentDTO.setId(attachedDoc.getAttachedDocOID());
        documentDTO.setName(attachedDoc.getDocumentName());
        documentDTO.setUrl(GloriaParams.BASE_URL_ATTACHEDDOCS + "/" + attachedDoc.getAttachedDocOID());
        return documentDTO;
    }

    public static List<TransportLabelDTO> transformAsTransportLabelDTOs(List<TransportLabel> transportLabels) {
        List<TransportLabelDTO> transportLabelDTOs = new ArrayList<TransportLabelDTO>();
        if (transportLabels != null && !transportLabels.isEmpty()) {
            for (TransportLabel transportLabel : transportLabels) {
                transportLabelDTOs.add(transformAsDTO(transportLabel));
            }
        }
        return transportLabelDTOs;
    }

    public static TransportLabelDTO transformAsDTO(TransportLabel transportLabel) {
        if (transportLabel != null) {
            TransportLabelDTO transportLabelDTO = new TransportLabelDTO();
            transportLabelDTO.setId(transportLabel.getTransportLabelOid());
            transportLabelDTO.setVersion(transportLabel.getVersion());
            transportLabelDTO.setCode(transportLabel.getCode());
            return transportLabelDTO;
        }
        return null;
    }

    public static List<DocumentDTO> transformReceiveDocsAsDocumentDTO(List<ReceiveDoc> inspectionDocs, boolean isContentRequired) {
        List<DocumentDTO> documentDTOs = new ArrayList<DocumentDTO>();
        if (inspectionDocs != null && !inspectionDocs.isEmpty()) {
            for (ReceiveDoc inspecDoc : inspectionDocs) {
                documentDTOs.add(transformAsDTO(inspecDoc, isContentRequired));
            }
        }
        return documentDTOs;
    }

    public static DocumentDTO transformAsDTO(ReceiveDoc receiveDoc, boolean isContentRequired) {
        DocumentDTO documentDTO = new DocumentDTO();
        documentDTO.setCategoryType(GloriaParams.DOCUMENT_CATEGORY_INSPECTIONDOCS);
        if (receiveDoc.getFileContent() != null) {
            if (isContentRequired) {
                documentDTO.setContent(Arrays.copyOf(receiveDoc.getFileContent(), receiveDoc.getFileContent().length));
            }
            documentDTO.setSize(receiveDoc.getFileContent().length);
        }
        documentDTO.setId(receiveDoc.getRecieveDocOID());
        documentDTO.setName(receiveDoc.getDocumentName());
        documentDTO.setUrl(GloriaParams.BASE_URL_QUALITYDOCS + "/" + receiveDoc.getRecieveDocOID());
        return documentDTO;
    }

    public static void deleteMissingDeliverySchedules(List<DeliverySchedule> deliverySchedules, List<DeliveryScheduleDTO> deliveryScheduleDTOs,
            String userId, DeliveryServices deliveryServices, OrderRepository orderRepository) throws GloriaApplicationException {
        Set<Long> allDeliveryScheduleOids = new TreeSet<Long>();
        for (DeliverySchedule aDeliverySchedule : deliverySchedules) {
            allDeliveryScheduleOids.add(aDeliverySchedule.getDeliveryScheduleOID());
        }

        Set<Long> updatedDeliveryScheduleOids = new TreeSet<Long>();
        for (DeliveryScheduleDTO aDeliveryScheduleDTO : deliveryScheduleDTOs) {
            updatedDeliveryScheduleOids.add(aDeliveryScheduleDTO.getId());
        }

        if (allDeliveryScheduleOids.removeAll(updatedDeliveryScheduleOids)) {
            for (long aDeliveryScheduleOid : allDeliveryScheduleOids) {
                orderRepository.delete(orderRepository.findDeliveryScheduleById(aDeliveryScheduleOid));
            }
        }
    }

    public static TransportLabel createTransportLabel(String whSiteId, UserServices userServices, DeliveryNoteRepository deliveryNoteRepository)
            throws GloriaApplicationException {

        TransportLabel transportLabel = new TransportLabel();
        transportLabel.setCreateTime(DateUtil.getCurrentUTCDateTime());
        transportLabel.setWhSiteId(whSiteId);
        transportLabel.setCode(whSiteId + "-" + Utils.createRandomString(GloriaParams.TRANSPORTLABEL_ALPHANUMERIC_CODE_LENGTH).toUpperCase());
        return deliveryNoteRepository.save(transportLabel);
    }
    
    public static List<GoodsReceiptLineDTO> transformAsGoodsReceiptLineDTOs(List<GoodsReceiptLine> goodsReceiptLines) {
        List<GoodsReceiptLineDTO> goodsReceiptLineDTOs = new ArrayList<GoodsReceiptLineDTO>();
        if (goodsReceiptLines != null && !goodsReceiptLines.isEmpty()) {
            for (GoodsReceiptLine goodsReceiptLine : goodsReceiptLines) {
                goodsReceiptLineDTOs.add(transformAsDTO(goodsReceiptLine));
            }
        }
        return goodsReceiptLineDTOs;
    }
    
    public static GoodsReceiptLineDTO transformAsDTO(GoodsReceiptLine goodsReceiptLine) {
        if (goodsReceiptLine != null) {
            GoodsReceiptLineDTO goodsReceiptLineDTO = new GoodsReceiptLineDTO();

            DeliveryNoteLine deliveryNoteLine = goodsReceiptLine.getDeliveryNoteLine();
            DeliveryNote deliveryNote = deliveryNoteLine.getDeliveryNote();

            goodsReceiptLineDTO.setId(goodsReceiptLine.getGoodsReceiptLineOId());
            goodsReceiptLineDTO.setVersion(goodsReceiptLine.getVersion());
            goodsReceiptLineDTO.setStatus(goodsReceiptLine.getStatus().name());
            goodsReceiptLineDTO.setPlant(goodsReceiptLine.getPlant());
            goodsReceiptLineDTO.setMovementType(goodsReceiptLine.getMovementType());
            goodsReceiptLineDTO.setVendor(goodsReceiptLine.getVendor());
            goodsReceiptLineDTO.setVendorMaterialNumber(goodsReceiptLine.getVendorMaterialNumber());
            goodsReceiptLineDTO.setOrderReference(goodsReceiptLine.getOrderReference());
            goodsReceiptLineDTO.setQuantity(goodsReceiptLine.getQuantity());
            goodsReceiptLineDTO.setQuantityCancelled(goodsReceiptLine.getQuantityCancelled());
            goodsReceiptLineDTO.setIsoUnitOfMeasure(goodsReceiptLine.getIsoUnitOfMeasure());
            goodsReceiptLineDTO.setCancelable(goodsReceiptLine.isCancelable());
            goodsReceiptLineDTO.setDeliveryNoteNo(deliveryNote.getDeliveryNoteNo());
            goodsReceiptLineDTO.setDeliveryNoteDate(deliveryNote.getDeliveryNoteDate());
            goodsReceiptLineDTO.setProjectId(deliveryNoteLine.getProjectId());
            goodsReceiptLineDTO.setReferenceId(deliveryNoteLine.getReferenceIds());
            goodsReceiptLineDTO.setPartNumber(deliveryNoteLine.getPartNumber());
            goodsReceiptLineDTO.setPartVersion(deliveryNoteLine.getPartVersion());
            goodsReceiptLineDTO.setPartName(deliveryNoteLine.getPartName());
            goodsReceiptLineDTO.setPartModification(deliveryNoteLine.getPartModification());
            goodsReceiptLineDTO.setReceivalDate(deliveryNoteLine.getReceivedDateTime());
            return goodsReceiptLineDTO;
        }
        return null;
    }
    
    public static List<OrderlineTracebilityDTO> transformAsOrderlineTraceabilityDTOs(List<Traceability> traceabilitys) {
        List<OrderlineTracebilityDTO> traceabilityDTOs = new ArrayList<OrderlineTracebilityDTO>();
        if (traceabilitys != null && !traceabilitys.isEmpty()) {
            for (Traceability traceability : traceabilitys) {
                traceabilityDTOs.add(transformAsOrderlineTraceabilityDTO(traceability));
            }
        }
        return traceabilityDTOs;
    }

    private static OrderlineTracebilityDTO transformAsOrderlineTraceabilityDTO(Traceability traceability) {
        OrderlineTracebilityDTO traceabilityDTO = new OrderlineTracebilityDTO();
        traceabilityDTO.setId(traceability.getTraceabilityOid());
        traceabilityDTO.setLoggedTime(traceability.getLoggedTime());
        traceabilityDTO.setUserId(traceability.getUserId());
        traceabilityDTO.setUserName(traceability.getUserName());
        traceabilityDTO.setI18MessageCode(traceability.getI18MessageCode());
        traceabilityDTO.setOlStatus(traceability.getOlStatus());
        traceabilityDTO.setStaAcceptedDate(traceability.getStaAcceptedDate());
        traceabilityDTO.setStaAgreedDate(traceability.getStaAgreedDate());
        traceabilityDTO.setAllowedQuantity(traceability.getAllowedQuantity());
        traceabilityDTO.setAction(traceability.getAction());
        traceabilityDTO.setActionDetail(traceability.getActionDetail());
        traceabilityDTO.setUserId(traceability.getUserId());
        traceabilityDTO.setUserName(traceability.getUserName());
        return traceabilityDTO;
    }
    
    public static List<DeliveryNoteSubLine> manageSublinesForQI(String materialLineStatus, DeliveryNoteLine deliveryNoteLine,
            List<DeliveryNoteSubLine> deliveryNoteSubLines, WarehouseServices warehouseServices, DeliveryServices deliveryServices)
            throws GloriaApplicationException {
        List<DeliveryNoteSubLine> deliveryNoteSubLinesForQI = new ArrayList<DeliveryNoteSubLine>();
        if (deliveryNoteSubLines != null && !deliveryNoteSubLines.isEmpty()) {
            for (DeliveryNoteSubLine deliveryNoteSubLine : deliveryNoteSubLines) {
                deliveryNoteSubLine.setNextZoneCode(deliveryNoteLine.getDeliveryNote()
                                                                    .getReceiveType()
                                                                    .nextLocation(deliveryNoteLine, deliveryNoteSubLine.isDirectSend(), warehouseServices,
                                                                                  deliveryServices));
                identifySublinesBasedOnMateriallineStatus(materialLineStatus, deliveryNoteLine, deliveryNoteSubLinesForQI, deliveryNoteSubLine);
            }
        }
        if (deliveryNoteSubLinesForQI != null && !deliveryNoteSubLinesForQI.isEmpty()) {
            return deliveryNoteSubLinesForQI;
        } else {
            return deliveryNoteSubLines;
        }
    }

    public static void identifySublinesBasedOnMateriallineStatus(String materialLineStatus, DeliveryNoteLine deliveryNoteLine,
            List<DeliveryNoteSubLine> deliveryNoteSubLinesForQI, DeliveryNoteSubLine deliveryNoteSubLine) {
        if (!StringUtils.isEmpty(materialLineStatus)) {
            List<MaterialLine> materialLines = deliveryNoteLine.getMaterialLine();
            if (materialLines != null && !materialLines.isEmpty()) {
                for (MaterialLine materialLine : materialLines) {
                    boolean directSend = materialLine.getDirectSend().isDirectSend();
                    if (materialLine.getStatus().equals(MaterialLineStatus.valueOf(materialLineStatus)) && directSend == deliveryNoteSubLine.isDirectSend()) {
                        deliveryNoteSubLinesForQI.add(deliveryNoteSubLine);
                        break;
                    }
                }
            }
        }
    }

    public static void setDataFromMaterial(List<Material> materials, OrderLineDTO orderLineDTO, Map<Long, List<MaterialLine>> materialIdMaterialLineMap) {
        if (materials != null && !materials.isEmpty()) {

            orderLineDTO.setDirectSend(evaluateDirectSend(materials, materialIdMaterialLineMap));
            // sort to pick the earliest buildStartDate from Materials.
            Collections.sort(materials, new Comparator<Material>() {
                public int compare(Material materialOne, Material materialTwo) {
                    if (materialOne.getMaterialHeader() == null || materialOne.getMaterialHeader().getAccepted().getOutboundStartDate() == null
                            || materialTwo.getMaterialHeader() == null || materialTwo.getMaterialHeader().getAccepted().getOutboundStartDate() == null) {
                        return 1;
                    }
                    return materialTwo.getMaterialHeader().getAccepted().getOutboundStartDate()
                                      .compareTo(materialOne.getMaterialHeader().getAccepted().getOutboundStartDate());
                }
            });
            MaterialHeader materialHeader = materials.get(0).getMaterialHeader();
            if (materialHeader != null) {
                orderLineDTO.setBuildStartDate(materialHeader.getAccepted().getOutboundStartDate());
            }

            List<String> referenceIDs = new ArrayList<String>();
            List<String> mailFormIds = new ArrayList<String>();
            String partModification = new String();
            for (Material material : materials) {
                String referenceId = null;

                if (material.getMaterialHeader() != null) {
                    referenceId = material.getMaterialHeader().getReferenceId();
                }
                if (referenceId != null && !referenceIDs.contains(referenceId)) {
                    referenceIDs.add(referenceId);
                }
                String mailFormId = material.getMailFormId();
                if (mailFormId != null && !referenceIDs.contains(mailFormId)) {
                    mailFormIds.add(mailFormId);
                }
                if (material.getMaterialType() == MaterialType.ADDITIONAL) {
                    orderLineDTO.setAdditionalQuantity(material.getQuantityForMaterialLineList(materialIdMaterialLineMap.get(material.getMaterialOID())));
                }
                String partModi = material.getPartModification();
                if (StringUtils.isNotBlank(partModi)) {
                    partModification = partModi;
                }
            }
            orderLineDTO.setReferenceId(StringUtils.join(referenceIDs, ","));
            orderLineDTO.setMailFormIds(StringUtils.join(mailFormIds, ","));
            orderLineDTO.setMaterialPartModification(partModification);
        }
    }

    private static boolean evaluateDirectSend(List<Material> materials, Map<Long, List<MaterialLine>> materialIdMaterialLineMap) {
        for (Material material : materials) {
            if (material.getMaterialType() == MaterialType.USAGE || material.getMaterialType() == MaterialType.MODIFIED
                    || material.getMaterialType() == MaterialType.ADDITIONAL || material.getMaterialType() == MaterialType.ADDITIONAL_USAGE) {
                List<MaterialLine> materialLineList = materialIdMaterialLineMap.get(material.getMaterialOID());
                if (materialLineList != null && !materialLineList.isEmpty()) {
                    for (MaterialLine materialLine : materialLineList) {
                        if (materialLine != null) {
                            if (materialLine.getDirectSend().isDirectSend()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
