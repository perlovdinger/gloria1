package com.volvo.gloria.procurematerial.d.type.receive;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.TransformerUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.d.entities.DangerousGoods;
import com.volvo.gloria.common.repositories.b.DangerousGoodsRepository;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.b.beans.MaterialServicesHelper;
import com.volvo.gloria.procurematerial.c.ChangeType;
import com.volvo.gloria.procurematerial.c.DeliveryNoteLineStatus;
import com.volvo.gloria.procurematerial.c.QiMarking;
import com.volvo.gloria.procurematerial.c.ShipmentType;
import com.volvo.gloria.procurematerial.c.dto.DeliveryNoteDTO;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNote;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteSubLine;
import com.volvo.gloria.procurematerial.d.entities.DeliverySchedule;
import com.volvo.gloria.procurematerial.d.entities.DispatchNote;
import com.volvo.gloria.procurematerial.d.entities.FinanceHeader;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeaderVersion;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.Order;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLineLastModified;
import com.volvo.gloria.procurematerial.d.entities.OrderLineVersion;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.RequestGroup;
import com.volvo.gloria.procurematerial.d.entities.RequestList;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.InspectionStatus;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatusHelper;
import com.volvo.gloria.procurematerial.d.entities.status.orderLine.OrderLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineHelper;
import com.volvo.gloria.procurematerial.d.type.directsend.DirectSendType;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.procurematerial.d.type.procure.ProcureType;
import com.volvo.gloria.procurematerial.d.type.procure.ProcureTypeHelper;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.repositories.b.OrderRepository;
import com.volvo.gloria.procurematerial.util.ReceivedOrderEmailTemplate;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.MailSender;
import com.volvo.gloria.util.c.GloriaExceptionConstants;
import com.volvo.gloria.util.c.UniqueItems;
import com.volvo.gloria.util.tracing.c.GloriaTraceabilityConstants;
import com.volvo.gloria.warehouse.b.WarehouseServices;
import com.volvo.gloria.warehouse.c.ZoneType;


/**
 * helper class to support the behavious of ReceiveType.
 * 
 */
public final class ReceiveTypeHelper {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiveTypeHelper.class);
    private static final String SPLITTER = "-------------------------------------------------------------------------";

    private ReceiveTypeHelper() {
    }

    public static DeliveryNote createDeliveryNoteForRegular(DeliveryNoteDTO deliveryNoteDTO, DeliveryNoteRepository deliveryNoteRepository) {
        DeliveryNote deliveryNote = deliveryNoteRepository.findDeliveryNote(deliveryNoteDTO.getOrderNo(), deliveryNoteDTO.getDeliveryNoteNo(),
                                                                            ReceiveType.REGULAR, null);
        if (deliveryNote == null) {
            deliveryNote = new DeliveryNote();
            deliveryNote.setReceiveType(ReceiveType.REGULAR);
            deliveryNote.setDeliveryNoteNo(deliveryNoteDTO.getDeliveryNoteNo());
            deliveryNote.setOrderNo(deliveryNoteDTO.getOrderNo());
            deliveryNote.setWhSiteId(deliveryNoteDTO.getWhSiteId());
            deliveryNote.setSupplierId(deliveryNoteDTO.getSupplierId());
            deliveryNote.setSupplierName(deliveryNoteDTO.getSupplierName());
        }
        deliveryNote.setCarrier(deliveryNoteDTO.getCarrier());
        deliveryNote.setDeliveryNoteDate(deliveryNoteDTO.getDeliveryNoteDate());
        deliveryNote.setTransportationNo(deliveryNoteDTO.getTransportationNo());
        return deliveryNote;
    }

    public static DeliveryNote createDeliveryNoteLinesForRegular(DeliveryNote deliveryNote, DeliveryNoteRepository deliveryNoteRepository,
            OrderRepository orderRepository, DangerousGoodsRepository dangerousGoodsRepository) throws GloriaApplicationException {
        Order order = orderRepository.findOrderByOrderNo(deliveryNote.getOrderNo());
        if (order == null) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVLID_ORDERNO, "No valid purchase order exists for no :" 
                                                        + deliveryNote.getOrderNo());
        }
        List<OrderLine> orderLines = order.getOrderLines();
        if (!hasValidOrderLine(orderLines)) {
            throw new GloriaApplicationException(GloriaExceptionConstants.ORDER_RECEIVED, "No more parts to receive for this purchase order:"
                    + deliveryNote.getOrderNo());
        }

        if (orderLines != null && !orderLines.isEmpty()) {
            for (OrderLine orderLine : orderLines) {
                if (!orderLine.getStatus().equals(OrderLineStatus.COMPLETED)) {
                    createDeliveryNoteLineForOrderline(orderLine, deliveryNote, dangerousGoodsRepository);
                }
            }
        }

        deliveryNote.setMaterialUserId(order.getMaterialUserId());
        return deliveryNoteRepository.save(deliveryNote);
    }
    
    private static Boolean isDeliveryOnTime(Date anyStaDate, Date deliveryDate) {
        if (anyStaDate != null && deliveryDate != null) {
            if (deliveryDate.before(anyStaDate) || DateUtil.areDatesFromSameWeek(deliveryDate, anyStaDate)) {
                return true;
            }
            return false;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static void receiveRegular(long deliveryNoteId, UserDTO user, DeliveryNoteRepository deliveryNoteRepository, OrderRepository orderRepository,
            MaterialHeaderRepository requestHeaderRepository, MaterialServices materialServices, CommonServices commonServices,
            TraceabilityRepository traceabilityRepository, WarehouseServices warehouseServices) throws GloriaApplicationException {
        List<DeliveryNoteLine> deliveryNoteLines = deliveryNoteRepository.findDeliveryNoteLinesByDeliveryNoteId(deliveryNoteId, null, 
                                                                                                                null, ReceiveType.REGULAR);
        Map<String, String> emailIdToEmailBodyMap = new HashMap<String, String>();
        String orderNumber = "";
        for (DeliveryNoteLine deliveryNoteLine : deliveryNoteLines) {
            if (deliveryNoteLine != null && deliveryNoteLine.getStatus() != DeliveryNoteLineStatus.RECEIVED) {
                OrderLine orderLine = deliveryNoteLine.getOrderLine();

                if (deliveryNoteLine != null && deliveryNoteLine.getReceivedQuantity() > 0) {

                    orderLine.getStatus().receive(orderLine, deliveryNoteLine, deliveryNoteLine.getReceivedQuantity());

                    OrderLineVersion orderLineVersion = orderLine.getCurrent();
                    if (orderLineVersion != null && orderLineVersion.isOrderStaDateOnTime() == null && orderLineVersion.isStaAgreedDateOnTime() == null) {
                        Date deliveryNoteDate = deliveryNoteLine.getDeliveryNote().getDeliveryNoteDate();

                        Date orderStaDate = orderLineVersion.getOrderStaDate();
                        orderLineVersion.setOrderStaDateOnTime(isDeliveryOnTime(orderStaDate, deliveryNoteDate));

                        Date staAgreedDate = orderLineVersion.getStaAgreedDate();
                        orderLineVersion.setStaAgreedDateOnTime(isDeliveryOnTime(staAgreedDate, deliveryNoteDate));
                    }
                    
                    OrderLineLastModified orderLineLastModified = orderLine.getOrderLineLastModified();
                    orderLineLastModified.setModifiedByUserId(user.getId());
                    orderLineLastModified.setModifiedTime(DateUtil.getCurrentUTCDateTime());
                    
                    updateWithDeviationInfo(orderLine, deliveryNoteLine, orderRepository);

                    List<MaterialLine> materialLines = orderRepository.findProcuredMaterialLinesByOrderId(orderLine.getOrderLineOID());
                    
                    if (orderLine.getStatus().isReceived(orderLine)) {
                        updateFinanceHeader(requestHeaderRepository, materialLines);
                    }
                    
                    List<MaterialLine> shippedMaterialLines = receiveMaterialLinesToShip(deliveryNoteLine, materialLines, requestHeaderRepository,
                                                                                        materialServices, user, traceabilityRepository, deliveryNoteRepository,
                                                                                        warehouseServices);
                    List<MaterialLine> storedMaterialLines = receiveMaterialLinesToStore(deliveryNoteLine, materialLines, requestHeaderRepository,
                                                                                          materialServices, user, traceabilityRepository,
                                                                                          deliveryNoteRepository, warehouseServices);

                    String companyCode = commonServices.getCompanyCodeFromMaterialUserId(deliveryNoteLine.getOrderLine().getOrder().getMaterialUserId());
                    ProcureType procureType = deliveryNoteLine.getOrderLine().getProcureLine().getProcureType();
                    long approvedQuantity = deliveryNoteLine.getReceivedQuantity() - deliveryNoteLine.getDamagedQuantity();

                    materialServices.sendGoodsReceipt(companyCode, deliveryNoteLine, approvedQuantity, procureType);

                    deliveryNoteLine.setStatus(DeliveryNoteLineStatus.RECEIVED);
                    deliveryNoteLine.setReceivedDateTime(DateUtil.getCurrentUTCDateTime());
                    deliveryNoteRepository.save(deliveryNoteLine);
                    
                    DeliveryNoteSubLine subLineToShip = deliveryNoteLine.getSubLine(true);
                    if (subLineToShip != null && !subLineToShip.isStore()) {
                        MaterialServicesHelper.doDirectSend(requestHeaderRepository, deliveryNoteLine, deliveryNoteLine.getMaterialLine(), user,
                                                            materialServices, commonServices);
                    }
                    String siteId = "";
                    if (deliveryNoteLine.getDeliveryNote() != null) {
                        siteId = deliveryNoteLine.getDeliveryNote().getWhSiteId();
                    } 

                    try {       
                        List<String> list = new ArrayList<String>();
                        list.add(siteId);
                        int stockBalance = (int) requestHeaderRepository.calculateStockBalance(orderLine.getMaterials().get(0).getPartNumber(), 
                                                                                               orderLine.getMaterials().get(0).getPartVersion(), 
                                                                                               list, 
                                                                                               orderLine.getMaterials().get(0).getPartModification(), 
                                                                                               orderLine.getMaterials().get(0).getPartAffiliation());                        
                        prepareEmialSendingForOrderReceived(deliveryNoteLine, orderLine, stockBalance,
                                                           CollectionUtils.union(storedMaterialLines, shippedMaterialLines), emailIdToEmailBodyMap);
                        orderNumber = orderLine.getOrder().getOrderNo();
                    } catch (Exception exception) {
                        //ignored exception handling so as to not fail as part of requirement
                        LOGGER.warn("Exception when sending email for Order", exception);
                    }
                    
                    // remove projectId on materials which are released and request type FOR_STOCK
                    List<MaterialLine> receivedLines = deliveryNoteLine.getMaterialLine();
                    for (MaterialLine receivedLine : receivedLines) {
                        Material materialReceived = receivedLine.getMaterial();
                        if (materialReceived.getMaterialType() == MaterialType.RELEASED
                                || (materialReceived.getAdd() != null && materialReceived.getAdd().getType() == ChangeType.FOR_STOCK)) {
                            receivedLine.getMaterial().getFinanceHeader().setProjectId(null);
                        }
                    }                   
                }                
            }
        }
        try {
            sendEmailToRecepients(emailIdToEmailBodyMap, orderNumber);
        } catch (Exception exception) {
            // ignored exception handling so as to not fail as part of requirement
            LOGGER.warn("Exception when sending email for Order", exception);
        }
    }

    private static void sendEmailToRecepients(Map<String, String> emailIdToEmailBodyMap, String orderNumber) {
        if (emailIdToEmailBodyMap.size() > 0) {
            Set<String> emailIdKeySet = emailIdToEmailBodyMap.keySet();
            Iterator<String> emailIdIterator = emailIdKeySet.iterator();
            while (emailIdIterator.hasNext()) {
                String emailId = emailIdIterator.next();
                String emailBody = emailIdToEmailBodyMap.get(emailId);
                if (!StringUtils.isEmpty(emailBody)) {
                    MailSender sender = new MailSender();
                    String genericBody = ReceivedOrderEmailTemplate.getGenericBody(sender.getEnvironment());
                    String subject = ReceivedOrderEmailTemplate.getSubject(sender.getEnvironment());
                    subject = subject.replace("ORDER_NUMBER", StringUtils.trimToEmpty(orderNumber));
                    sender.sendMail(emailId, subject, genericBody + emailBody);
                }
            }
        }
    }

    public static void updateFinanceHeader(MaterialHeaderRepository requestHeaderRepository, List<MaterialLine> materialLines) {
        if (materialLines != null && !materialLines.isEmpty()) {
            List<Long> financeHeaderId = new ArrayList<Long>();
            for (MaterialLine materialLine : materialLines) {
                Material material = materialLine.getMaterial();
                FinanceHeader financeHeader = material.getFinanceHeader();
                if (financeHeader != null && material.getMaterialType().equals(MaterialType.RELEASED) 
                        && !financeHeaderId.contains(financeHeader.getFinanceHeaderXOid())) {
                    financeHeader.setProjectId(null);
                    requestHeaderRepository.updateFinanceHeader(financeHeader);
                    financeHeaderId.add(financeHeader.getFinanceHeaderXOid());
                }
            }
        }
    }

    protected static void prepareEmialSendingForOrderReceived(DeliveryNoteLine deliveryNoteLine, OrderLine orderLine, int stockBalance,
            Collection<MaterialLine> receivedMaterialLines, Map<String, String> emailIdToEmailBodyMap) {
        Map<String, MaterialLine> mapOfUniqueEmailRequestGroup = new HashMap<String, MaterialLine>();
        for (MaterialLine receivedMaterialLine : receivedMaterialLines) {
            Material materialOwner = receivedMaterialLine.getMaterialOwner();
            MaterialHeader header = materialOwner.getMaterialHeader();
            if (header != null && header.getAccepted() != null) {
                String emailTo = header.getAccepted().getContactPersonEmail();
                if (!StringUtils.isEmpty(emailTo)) {
                    if (!mapOfUniqueEmailRequestGroup.containsKey(emailTo)) {
                        mapOfUniqueEmailRequestGroup.put(emailTo, receivedMaterialLine);
                    }
                }
            }
        }

        Set<String> emailSet = mapOfUniqueEmailRequestGroup.keySet();
        Iterator<String> emaiISetIterator = emailSet.iterator();
        while (emaiISetIterator.hasNext()) {
            String emailTo = emaiISetIterator.next();
            String mailBody = createMailBodyForEachMaterial(deliveryNoteLine, orderLine, stockBalance, mapOfUniqueEmailRequestGroup.get(emailTo));
            String mailBodyFromMap = emailIdToEmailBodyMap.get(emailTo);
            if (!StringUtils.isEmpty(mailBodyFromMap)) {
                mailBodyFromMap = mailBodyFromMap + "\n" + SPLITTER + "\n" + mailBody;
            } else {
                mailBodyFromMap = mailBody;
            }
            emailIdToEmailBodyMap.put(emailTo, mailBodyFromMap);
        }
    }
    
    protected static void sendEmailForOrderReceived(DeliveryNoteLine deliveryNoteLine, OrderLine orderLine, int stockBalance,
            Collection<MaterialLine> receivedMaterialLines) {      
        for (MaterialLine receivedMaterialLine : receivedMaterialLines) {          
            sendEmailForEachMaterial(deliveryNoteLine, orderLine, stockBalance, receivedMaterialLine);            
        }
    }


    private static String createMailBodyForEachMaterial(DeliveryNoteLine deliveryNoteLine, OrderLine orderLine, int stockBalance,
            MaterialLine materialLine) {
        Material materialOwner = materialLine.getMaterialOwner();
        MaterialHeader header = materialOwner.getMaterialHeader();
        String partModification = materialOwner.getPartModification();
        String partName = materialOwner.getPartName();
        Order order = orderLine.getOrder();
        
        String messageBody = null;
        if (header != null && header.getAccepted() != null) {           
            MailSender sender = new MailSender();
            messageBody = ReceivedOrderEmailTemplate.getBody(sender.getEnvironment());
            messageBody = messageBody.replace("PART_NUMBER", StringUtils.trimToEmpty(orderLine.getPartNumber()));
            messageBody = messageBody.replace("PART_NAME", StringUtils.trimToEmpty(partName));
            messageBody = messageBody.replace("PART_MODIFICATION", StringUtils.trimToEmpty(partModification));
            messageBody = messageBody.replace("PROJECT_NUMBER", StringUtils.trimToEmpty(orderLine.getProjectId()));
            messageBody = messageBody.replace("DATE_OF_RECEIVAL", DateUtil.getDateWithoutTimeAsString(deliveryNoteLine.getReceivedDateTime()));
            messageBody = messageBody.replace("RECEIVED_QUANTITY", Long.toString(deliveryNoteLine.getReceivedQuantity()));
            messageBody = messageBody.replace("PART_VERSION", StringUtils.trimToEmpty(materialOwner.getPartVersion()));
            messageBody = messageBody.replaceAll("MATERIAL_CONTROLLER_NAME", StringUtils.trimToEmpty(header.getMaterialControllerName()));
            messageBody = messageBody.replace("MATERIAL_CONTROLLER_ID", StringUtils.trimToEmpty(header.getMaterialControllerUserId()));
            messageBody = messageBody.replace("REFERENCE", StringUtils.trimToEmpty(orderLine.getProcureLine().getReferenceGps()));
            int damagedQuantity = (int) deliveryNoteLine.getDamagedQuantity();
            DirectSendType directSend = materialLine.getDirectSend();
            if (directSend != null) {
                DeliveryNoteSubLine deliveryNoteSubLine = deliveryNoteLine.getSubLine(directSend.isDirectSend());
                if (deliveryNoteSubLine != null) {
                    int receivedQuantity = (int) deliveryNoteSubLine.getToReceiveQty();
                    int stockBalanceReplace = calculateStockBalanceToEmail(stockBalance, receivedQuantity, damagedQuantity);
                    messageBody = messageBody.replace("CURRENT_BALANCE", Integer.toString(stockBalanceReplace));
                }
            }
            String supplierName = order.getSupplierName();
            if (supplierName != null) {
                messageBody = messageBody.replace("SUPPLIER_NAME", supplierName);
            }
            messageBody = messageBody.replace("ORDER_NUMBER", order.getOrderNo());
            String supplierId = order.getSupplierId();
            if (supplierId != null) {
                messageBody = messageBody.replace("SUPPLIER_ID", supplierId);
            }
            messageBody = messageBody.replace("DAMAGED_QUANTITY", Long.toString(damagedQuantity));                   
        }
        return messageBody;
    }

    public static void sendEmailForEachMaterial(DeliveryNoteLine deliveryNoteLine, OrderLine orderLine, int stockBalance, MaterialLine materialLine) {
        Material materialOwner = materialLine.getMaterialOwner();
        MaterialHeader header = materialOwner.getMaterialHeader();
        String partModification = materialOwner.getPartModification();
        String partName = materialOwner.getPartName();
        Order order = orderLine.getOrder();
        Set<String> emailTos = new HashSet<String>();
        if (header != null && header.getAccepted() != null) {
            String emailTo = header.getAccepted().getContactPersonEmail();
            if (!StringUtils.isEmpty(emailTo) && !emailTos.contains(emailTo)) {
                MailSender sender = new MailSender();
                String subject = ReceivedOrderEmailTemplate.getSubject(sender.getEnvironment());
                subject = subject.replace("ORDER_NUMBER", StringUtils.trimToEmpty(order.getOrderNo()));
                String messageBody = ReceivedOrderEmailTemplate.getBody(sender.getEnvironment());
                messageBody = messageBody.replace("PART_NUMBER", StringUtils.trimToEmpty(orderLine.getPartNumber()));
                messageBody = messageBody.replace("PART_NAME", StringUtils.trimToEmpty(partName));
                messageBody = messageBody.replace("PART_MODIFICATION", StringUtils.trimToEmpty(partModification));
                messageBody = messageBody.replace("PROJECT_NUMBER", StringUtils.trimToEmpty(orderLine.getProjectId()));
                messageBody = messageBody.replace("DATE_OF_RECEIVAL", DateUtil.getDateWithoutTimeAsString(deliveryNoteLine.getReceivedDateTime()));
                messageBody = messageBody.replace("RECEIVED_QUANTITY", Long.toString(deliveryNoteLine.getReceivedQuantity()));
                messageBody = messageBody.replace("PART_VERSION", StringUtils.trimToEmpty(materialOwner.getPartVersion()));
                messageBody = messageBody.replaceAll("MATERIAL_CONTROLLER_NAME", StringUtils.trimToEmpty(header.getMaterialControllerName()));
                messageBody = messageBody.replace("MATERIAL_CONTROLLER_ID", StringUtils.trimToEmpty(header.getMaterialControllerUserId()));
                messageBody = messageBody.replace("REFERENCE", StringUtils.trimToEmpty(orderLine.getProcureLine().getReferenceGps()));
                int damagedQuantity = (int) deliveryNoteLine.getDamagedQuantity();
                DirectSendType directSend = materialLine.getDirectSend();
                if (directSend != null) {
                    DeliveryNoteSubLine deliveryNoteSubLine = deliveryNoteLine.getSubLine(directSend.isDirectSend());
                    if (deliveryNoteSubLine != null) {
                        int receivedQuantity = (int) deliveryNoteSubLine.getToReceiveQty();
                        int stockBalanceReplace = calculateStockBalanceToEmail(stockBalance, receivedQuantity, damagedQuantity);
                        messageBody = messageBody.replace("CURRENT_BALANCE", Integer.toString(stockBalanceReplace));
                    }
                }
                String supplierName = order.getSupplierName();
                if (supplierName != null) {
                    messageBody = messageBody.replace("SUPPLIER_NAME", supplierName);
                }
                messageBody = messageBody.replace("ORDER_NUMBER", order.getOrderNo());
                String supplierId = order.getSupplierId();
                if (supplierId != null) {
                    messageBody = messageBody.replace("SUPPLIER_ID", supplierId);
                }
                messageBody = messageBody.replace("DAMAGED_QUANTITY", Long.toString(damagedQuantity));
                sender.sendMail(emailTo, subject, messageBody);
                emailTos.add(emailTo);
            }
        }
    }

    protected static int calculateStockBalanceToEmail(int stockBalance, int receivedQuantity, int damagedQuantity) {
        //receivedQuantity is always greater than damagedQuantity
        int receivedQuantityLocal = receivedQuantity - damagedQuantity;
        if (stockBalance > 0 && receivedQuantityLocal >= 0) {
            // stockBalance should be always greater than receivedQuantityLocal so this value should be greater than 0
            return stockBalance - receivedQuantityLocal;
        }
        return 0;
    }

    private static List<MaterialLine> receiveMaterialLinesToStore(DeliveryNoteLine deliveryNoteLine, List<MaterialLine> materialLines,
            MaterialHeaderRepository requestHeaderRepository, MaterialServices materialServices, UserDTO user, TraceabilityRepository traceabilityRepository,
            DeliveryNoteRepository deliveryNoteRepository, WarehouseServices warehouseServices) throws GloriaApplicationException {
        List<MaterialLine> storedMaterialLines = new ArrayList<MaterialLine>();       
        DeliveryNoteSubLine subLineToStore = deliveryNoteLine.getSubLine(false);
        if (subLineToStore != null && subLineToStore.getToReceiveQty() > 0) {
            List<MaterialLine> materialLinesToStore = evaluateMaterialLinesOnDirectSendType(materialLines, DirectSendType.NO);
            storedMaterialLines = updateMaterialOnGoodsReceival(deliveryNoteLine, subLineToStore, materialLinesToStore, requestHeaderRepository,
                                                                  materialServices, user, traceabilityRepository, warehouseServices);
        } else {
            deliveryNoteLine.getDeliveryNoteSubLines().remove(subLineToStore);
            deliveryNoteRepository.delete(subLineToStore);
        }
        return storedMaterialLines;
    }

    private static List<MaterialLine> receiveMaterialLinesToShip(DeliveryNoteLine deliveryNoteLine, List<MaterialLine> materialLines,
            MaterialHeaderRepository requestHeaderRepository, MaterialServices materialServices, UserDTO user, TraceabilityRepository traceabilityRepository,
            DeliveryNoteRepository deliveryNoteRepository, WarehouseServices warehouseServices) throws GloriaApplicationException {
        List<MaterialLine> shippedMaterialLines = new ArrayList<MaterialLine>();       
        DeliveryNoteSubLine subLineToShip = deliveryNoteLine.getSubLine(true);
        if (subLineToShip != null && subLineToShip.getToReceiveQty() > 0) {
            List<MaterialLine> materialLinesToShip = evaluateMaterialLinesOnDirectSendType(materialLines, DirectSendType.YES_REQUESTED,
                                                                                           DirectSendType.YES_TRANSFER);
            shippedMaterialLines = updateMaterialOnGoodsReceival(deliveryNoteLine, subLineToShip, materialLinesToShip, requestHeaderRepository,
                                                                 materialServices, user, traceabilityRepository, warehouseServices);
        } else {
            deliveryNoteLine.getDeliveryNoteSubLines().remove(subLineToShip);
            deliveryNoteRepository.delete(subLineToShip);
        }
        return shippedMaterialLines;
    }

    public static List<MaterialLine> evaluateMaterialLinesOnDirectSendType(List<MaterialLine> materialLines, DirectSendType... directSendTypes) {
        List<MaterialLine> lines = new ArrayList<MaterialLine>();
        for (MaterialLine materialLine : materialLines) {
            Material material = materialLine.getMaterial();
            if (material != null && Arrays.asList(directSendTypes).contains(materialLine.getDirectSend())) {
                lines.add(materialLine);
            }
        }
        return lines;
    }

    private static void updateWithDeviationInfo(OrderLine orderLine, DeliveryNoteLine deliveryNoteLine, OrderRepository orderRepository) {
        boolean hasDeviation = true;
        List<DeliverySchedule> deliverySchedules = orderRepository.findDeliverySchedules(null, orderLine.getOrderLineOID());
        if (deliverySchedules != null && !deliverySchedules.isEmpty()) {
            for (DeliverySchedule deliverySchedule : deliverySchedules) {
                if (DateUtil.compareDates(deliverySchedule.getExpectedDate(), deliveryNoteLine.getDeliveryNote().getDeliveryNoteDate()) == 0
                        && deliverySchedule.getExpectedQuantity() == deliveryNoteLine.getReceivedQuantity()) {
                    hasDeviation = false;
                }
            }
        }
        orderLine.setDeliveryDeviation(hasDeviation);
    }

    public static DeliveryNote createDeliveryNoteForTransferReturn(DeliveryNoteDTO deliveryNoteDTO, DeliveryNoteRepository deliveryNoteRepository,
            MaterialHeaderRepository requestHeaderRepository) {
        ReceiveType receiveType = ReceiveType.valueOf(deliveryNoteDTO.getReceiveType());        
        DeliveryNote deliveryNote = deliveryNoteRepository.findDeliveryNote(null, deliveryNoteDTO.getDeliveryNoteNo(), receiveType, null);
        if (deliveryNote == null) {
            DispatchNote dispatchNote = requestHeaderRepository.findDispatchNote(deliveryNoteDTO.getDeliveryNoteNo(), null);
            if (dispatchNote != null) {
                deliveryNote = new DeliveryNote();
                deliveryNote.setReceiveType(receiveType);
                deliveryNote.setDeliveryNoteNo(dispatchNote.getDispatchNoteNo());
                deliveryNote.setDeliveryNoteDate(dispatchNote.getDispatchNoteDate());
                deliveryNote.setReturnDeliveryAddressId(dispatchNote.getRequestList().getDeliveryAddressId());
                deliveryNote.setReturnDeliveryAddressName(dispatchNote.getRequestList().getDeliveryAddressName());
                if (!StringUtils.isEmpty(deliveryNoteDTO.getTransportationNo())) {
                    deliveryNote.setTransportationNo(deliveryNoteDTO.getTransportationNo());
                } else {
                    deliveryNote.setTransportationNo(dispatchNote.getTrackingNo());
                }
                
                if (!StringUtils.isEmpty(deliveryNoteDTO.getCarrier())) {
                    deliveryNote.setCarrier(deliveryNoteDTO.getCarrier());
                } else {
                    deliveryNote.setCarrier(dispatchNote.getCarrier());
                }
                deliveryNote.setSupplierId(deliveryNoteDTO.getSupplierId());
                deliveryNote.setSupplierName(deliveryNoteDTO.getSupplierName());
                return deliveryNoteRepository.save(deliveryNote);
            }
        } else {
            deliveryNote.setTransportationNo(deliveryNoteDTO.getTransportationNo());
            deliveryNote.setCarrier(deliveryNoteDTO.getCarrier());
        }
        return deliveryNoteRepository.save(deliveryNote);
    }

    public static DeliveryNote createDeliveryNoteLinesForTransferReturn(DeliveryNote deliveryNote, DeliveryNoteRepository deliveryNoteRepository,
            MaterialHeaderRepository requestHeaderRepository) throws GloriaApplicationException {
        if (deliveryNote != null) {
            List<DeliveryNoteLine> deliveryNoteLines = deliveryNote.getDeliveryNoteLine();
            if (!hasValidDeliveryNoteLines(deliveryNote, deliveryNoteLines)) {
                List<MaterialLine> materialLines = requestHeaderRepository.findMaterialLinesByDispatchNoteInTransferOrReturn(deliveryNote.getDeliveryNoteNo(),
                                                                                                           deliveryNote.getDeliveryNoteDate());
                if (materialLines != null && !materialLines.isEmpty()) {
                    RequestGroup requestGroup = materialLines.get(0).getRequestGroup();
                    deliveryNote.setWhSiteId(deliveryNote.getReceiveType().whSite(requestGroup));
                    deliveryNote = deliveryNoteRepository.save(deliveryNote);

                    // create delivery note line for each material line - associate
                    for (MaterialLine materialLine : materialLines) {
                        DeliveryNoteLine deliveryNoteLine = new DeliveryNoteLine();
                        deliveryNoteLine.setDeliveryNote(deliveryNote);
                        deliveryNoteLine.setSendToQI(false);
                        deliveryNoteLine.setStatus(DeliveryNoteLineStatus.IN_WORK);

                        Material material = materialLine.getMaterial();
                        deliveryNoteLine.setProjectId(material.getFinanceHeader().getProjectId());
                        deliveryNoteLine = deliveryNoteRepository.save(deliveryNoteLine);
                        deliveryNoteLine.setMaterialLineOID(materialLine.getMaterialLineOID());

                        deliveryNoteLine.setPartAffiliation(material.getPartAffiliation());
                        deliveryNoteLine.setPartNumber(material.getPartNumber());
                        deliveryNoteLine.setPartVersion(material.getPartVersion());
                        deliveryNoteLine.setPartName(material.getPartName());

                        ProcureLine procureLine = material.getProcureLine();
                        if (procureLine != null) {
                            if (procureLine.getPartAlias() != null) {
                                deliveryNoteLine.setPartAlias(procureLine.getPartAlias().getAliasPartNumber());
                            }
                            deliveryNoteLine.setPartModification(procureLine.getpPartModification());
                        }
                        deliveryNoteLine.setProcureType(materialLine.getProcureType());
                        if (materialLine.getMaterial().getMaterialHeader() != null) {
                            deliveryNoteLine.setReferenceIds(materialLine.getMaterial().getMaterialHeader().getReferenceId());
                        }
                        deliveryNoteLine.setDeliveryNoteQuantity(materialLine.getQuantity());
                        deliveryNoteLine.setPossibleToReceiveQty(materialLine.getQuantity());

                        DeliveryNoteSubLine deliveryNoteSubLine = new DeliveryNoteSubLine();
                        deliveryNoteSubLine.setDeliveryNoteLine(deliveryNoteLine);
                        deliveryNoteSubLine.setDirectSend(false);
                        deliveryNoteLine.getDeliveryNoteSubLines().add(deliveryNoteSubLine);

                        deliveryNoteLines.add(deliveryNoteLine);
                    }
                    return deliveryNoteRepository.save(deliveryNote);
                }
            } else {
                // @Comment:Added for GLO-5304. The quantity has to be reset to deliveryNoteLine when a valid deliveryNoteLine exists.  
                for (DeliveryNoteLine deliveryNoteLine : deliveryNoteLines) {
                    if (deliveryNoteLine.getMaterialLineOID() > 0) {
                        MaterialLine materialLine = requestHeaderRepository.findMaterialLineById(deliveryNoteLine.getMaterialLineOID());
                        if (materialLine != null) {
                            deliveryNoteLine.setDeliveryNoteQuantity(materialLine.getQuantity());
                            deliveryNoteLine.setPossibleToReceiveQty(materialLine.getQuantity());
                            deliveryNoteRepository.save(deliveryNoteLine);
                        }
                    }
                }
            }
        }
        return deliveryNote;
    }

    public static void receiveTransferReturn(long deliveryNoteId, UserDTO user, DeliveryNoteRepository deliveryNoteRepository, OrderRepository orderRepository,
            MaterialHeaderRepository requestHeaderRepository, MaterialServices materialServices, TraceabilityRepository traceabilityRepository,
            boolean isReturnOfTransfer, WarehouseServices warehouseServices) throws GloriaApplicationException {
        List<DeliveryNoteLine> deliveryNoteLines = deliveryNoteRepository.findDeliveryNoteLinesByDeliveryNoteId(deliveryNoteId, null, null, ReceiveType.RETURN_TRANSFER);
        for (DeliveryNoteLine deliveryNoteLine : deliveryNoteLines) {
            if (deliveryNoteLine != null && deliveryNoteLine.getStatus() != DeliveryNoteLineStatus.RECEIVED && deliveryNoteLine.getReceivedQuantity() > 0) {
                MaterialLine materialLine = requestHeaderRepository.findMaterialLineById(deliveryNoteLine.getMaterialLineOID());
                DeliveryNoteSubLine deliveryNoteSubLine = deliveryNoteLine.getSubLine(false);
                if (materialLine != null) {
                    MaterialLine transferedOrReturenedMaterialLine = updateMaterialOnGoodsTransferReturn(deliveryNoteLine, deliveryNoteSubLine,
                                                                                              materialLine, requestHeaderRepository, materialServices, user,
                                                                                              traceabilityRepository, materialLine.getMaterial(),
                                                                                              warehouseServices);
                    if (transferedOrReturenedMaterialLine != null) {
                        updateWhSiteIdForTransferedOrReturnedMaterial(requestHeaderRepository, deliveryNoteLine, transferedOrReturenedMaterialLine,
                                                                      isReturnOfTransfer);
                        materialServices.placeIntoZone(transferedOrReturenedMaterialLine, ZoneType.TO_STORE);
                        MaterialLineStatus transferedOrReturenedStatus = MaterialLineStatus.READY_TO_STORE;
                        InspectionStatus inspectionStatus = transferedOrReturenedMaterialLine.getInspectionStatus();
                        if (inspectionStatus != null && inspectionStatus.equals(InspectionStatus.INSPECTING)) {
                            transferedOrReturenedStatus = MaterialLineStatus.MARKED_INSPECTION;
                            deliveryNoteLine.setSendToQI(true);
                        }
                        List<MaterialLineStatus> avoidTraceForMLStatus = new ArrayList<MaterialLineStatus>();
                        avoidTraceForMLStatus.add(transferedOrReturenedMaterialLine.getStatus());
                        MaterialLineStatusHelper.merge(transferedOrReturenedMaterialLine, transferedOrReturenedStatus, requestHeaderRepository,
                                                       traceabilityRepository, user, avoidTraceForMLStatus);

                        MaterialLineStatusHelper.createTraceabilityLog(transferedOrReturenedMaterialLine, traceabilityRepository, "Received", null,
                                                                       user.getId(), user.getUserName(), GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE);

                        // do store
                        if (deliveryNoteSubLine.isStore()) {
                            transferedOrReturenedMaterialLine.getStatus().storeReceiveAndQi(transferedOrReturenedMaterialLine, materialServices, user,
                                                                                            traceabilityRepository, deliveryNoteSubLine, warehouseServices,
                                                                                            requestHeaderRepository);
                        }
                    }

                    deliveryNoteLine.setStatus(DeliveryNoteLineStatus.RECEIVED);
                    deliveryNoteLine.setReceivedDateTime(DateUtil.getCurrentUTCDateTime());
                    deliveryNoteLine.setMaterialLineOID(0L);
                    deliveryNoteRepository.save(deliveryNoteLine);

                    DeliveryNote deliveryNote = deliveryNoteLine.getDeliveryNote();
                    DispatchNote dispatchNote = requestHeaderRepository.findDispatchNote(deliveryNote.getDeliveryNoteNo(), null);
                    dispatchNote.setCarrier(deliveryNote.getCarrier());
                    dispatchNote.setTrackingNo(deliveryNote.getTransportationNo());
                    requestHeaderRepository.saveDispatchNote(dispatchNote);
                }
            } else if (DeliveryNoteLineStatus.IN_WORK.equals(deliveryNoteLine.getStatus())) {
                deliveryNoteRepository.delete(deliveryNoteLine);
            }
        }
    }

    private static void updateWhSiteIdForTransferedOrReturnedMaterial(MaterialHeaderRepository requestHeaderRepository, DeliveryNoteLine deliveryNoteLine,
            MaterialLine materialLine, boolean isReturnOfTransfer) throws GloriaApplicationException {
        if (materialLine != null) {
            RequestGroup requestGroup = materialLine.getRequestGroup();
            if (requestGroup != null) {
                RequestList requestList = requestGroup.getRequestList();
                if (requestList != null) {
                    if (requestList.getShipmentType() == ShipmentType.SHIPMENT) {
                        materialLine.setWhSiteId(requestList.getWhSiteId());
                    } else {
                        if (isReturnOfTransfer) {
                            materialLine.setWhSiteId(requestList.getWhSiteId());
                        } else {
                            materialLine.setWhSiteId(requestList.getDeliveryAddressId());
                        }
                    }

                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static List<MaterialLine> updateMaterialOnGoodsReceival(DeliveryNoteLine deliveryNoteLine, DeliveryNoteSubLine deliveryNoteSubLine,
            List<MaterialLine> materialLines, MaterialHeaderRepository requestHeaderRepository, MaterialServices materialServices, UserDTO user,
            TraceabilityRepository traceabilityRepository, WarehouseServices warehouseServices) throws GloriaApplicationException {
        List<MaterialLine> updatedMaterialLines = new ArrayList<MaterialLine>();
        deliveryNoteLine.setPreviouslyBlockedQty(deliveryNoteLine.getAlreadyBlockedQty());
        deliveryNoteLine.setAlreadyBlockedQty(0L);
        deliveryNoteLine.setAlreadyReceivedQty(0L);
        if (deliveryNoteLine.getDamagedQuantity() > 0) {
            MaterialLineStatusHelper.blockMaterialLines(deliveryNoteLine, deliveryNoteSubLine, materialLines, requestHeaderRepository, user,
                                                        traceabilityRepository, materialServices);
        }
        
        List<Long> materialLineOIds = new ArrayList<Long>(CollectionUtils.collect(materialLines, TransformerUtils.invokerTransformer("getMaterialLineOID")));
        materialLines = null;
        materialLines = requestHeaderRepository.findProcuredMaterialLinesByMaterialLineIds(materialLineOIds);
        if (materialLines != null && !materialLines.isEmpty()) {
            for (MaterialLine materialLine : materialLines) {
               
                    MaterialLine receivedMaterialLine = materialLine.getStatus().receive(deliveryNoteLine, deliveryNoteSubLine, materialLine,
                                                                                         requestHeaderRepository, user, traceabilityRepository,
                                                                                         materialServices);

                    // QI & store
                    if (receivedMaterialLine != null) {
                        OrderLine orderLine = deliveryNoteLine.getOrderLine();
                        if (orderLine != null) {
                            QiMarking qiMarking = orderLine.getQiMarking();
                            if (qiMarking != null && qiMarking == QiMarking.MANDATORY) {
                                receivedMaterialLine.setInspectionStatus(InspectionStatus.INSPECTING);
                            }
                        }
                        updatedMaterialLines.add(receivedMaterialLine);
                        receivedMaterialLine.getStatus().qualityInspectMaterial(receivedMaterialLine, materialServices, user, traceabilityRepository,
                                                                                deliveryNoteSubLine);

                        MaterialLineStatusHelper.createTraceabilityLog(receivedMaterialLine, traceabilityRepository, "Received", null, user.getId(),
                                                                       user.getUserName(), GloriaTraceabilityConstants.MATERIAL_STATE_QTY_CHANGE);

                        if (deliveryNoteSubLine.isStore()) {
                            receivedMaterialLine.getStatus().storeReceiveAndQi(receivedMaterialLine, materialServices, user, traceabilityRepository,
                                                                               deliveryNoteSubLine, warehouseServices, requestHeaderRepository);
                        }
                    }
                
            }
        }
        
        // applicable if 'possibleToReceiveQty' has been increased for which there are no Materials in Placed status.
        createReleaseAdhocMaterial(deliveryNoteLine, deliveryNoteSubLine, materialServices, user, traceabilityRepository, warehouseServices,
                                   requestHeaderRepository);
        return updatedMaterialLines;
    }

    private static void createReleaseAdhocMaterial(DeliveryNoteLine deliveryNoteLine, DeliveryNoteSubLine deliveryNoteSubLine,
            MaterialServices materialServices, UserDTO user, TraceabilityRepository traceabilityRepository, WarehouseServices warehouseServices,
            MaterialHeaderRepository requestHeaderRepository) throws GloriaApplicationException {

        long toReceiveWithBlockedQty = deliveryNoteSubLine.getToReceiveQty() - deliveryNoteLine.getAlreadyReceivedQty()
                - deliveryNoteLine.getAlreadyBlockedQty();
        long damagedQty = deliveryNoteLine.getDamagedQuantity() - deliveryNoteLine.getAlreadyBlockedQty();
        long adhocReceiveQty = toReceiveWithBlockedQty - damagedQty;

        if (toReceiveWithBlockedQty > 0) {
            OrderLine orderLine = deliveryNoteLine.getOrderLine();
            ProcureLine procureLine = orderLine.getProcureLine();
            List<Material> materials = orderLine.getMaterials();
            // create RELEASED material from order if materials doesn't exist in case of CANCELLED order from GPS.
            if (materials != null && !materials.isEmpty()) {
                Material material = materials.get(0);
                MaterialLine materialLine = material.getMaterialLine().get(0);
                Material newMaterial = createAdhocMaterialOfTypeReleased(deliveryNoteLine, material.getUnitOfMeasure(), material.getPartAffiliation(),
                                                                         material.getPartModification(), material.getPartName(), material.getPartNumber(),
                                                                         material.getPartVersion(), material.getMailFormId(), material.getReceiver(),
                                                                         material.getRequiredStaDate(), material.getFinanceHeader());

                MaterialLine newMaterialLine = createAdhocMateriallineForReleasedMaterial(deliveryNoteLine, materialServices, damagedQty, adhocReceiveQty,
                                                                                          materialLine.getWhSiteId(), newMaterial);
                // QI
                if (newMaterialLine != null) {
                    newMaterialLine.getStatus().qualityInspectMaterial(newMaterialLine, materialServices, user, traceabilityRepository, deliveryNoteSubLine);

                    if (deliveryNoteSubLine.isStore()) {
                        newMaterialLine.getStatus().storeReceiveAndQi(newMaterialLine, materialServices, user, traceabilityRepository, deliveryNoteSubLine,
                                                                      warehouseServices, requestHeaderRepository);
                    }
                }
            } else {
                FinanceHeader financeHeader = new FinanceHeader();
                financeHeader.setCompanyCode(orderLine.getOrder().getCompanyCode());
                requestHeaderRepository.addFinanceHeader(financeHeader);
                Material newMaterial = createAdhocMaterialOfTypeReleased(deliveryNoteLine, orderLine.getUnitOfMeasure(), procureLine.getpPartAffiliation(),
                                                                         procureLine.getpPartVersion(), procureLine.getpPartName(),
                                                                         procureLine.getpPartNumber(), procureLine.getpPartVersion(), null, null,
                                                                         orderLine.getDeliverySchedule().get(0).getExpectedDate(), financeHeader);

                MaterialLine newMaterialLine = createAdhocMateriallineForReleasedMaterial(deliveryNoteLine, materialServices, damagedQty, adhocReceiveQty,
                                                                                          orderLine.getOrder().getShipToId(), newMaterial);
                // QI
                if (newMaterialLine != null) {
                    newMaterialLine.getStatus().qualityInspectMaterial(newMaterialLine, materialServices, user, traceabilityRepository, deliveryNoteSubLine);

                    if (deliveryNoteSubLine.isStore()) {
                        newMaterialLine.getStatus().storeReceiveAndQi(newMaterialLine, materialServices, user, traceabilityRepository, deliveryNoteSubLine,
                                                                      warehouseServices, requestHeaderRepository);
                    }
                }
            }
        }
    }

    private static MaterialLine createAdhocMateriallineForReleasedMaterial(DeliveryNoteLine deliveryNoteLine, MaterialServices materialServices,
            long damagedQty, long adhocReceiveQty, String whSiteId, Material newMaterial) throws GloriaApplicationException {
        MaterialLine newMaterialLine = null;
        
        String orderNo = deliveryNoteLine.getOrderLine().getOrder().getOrderNo();
        if (adhocReceiveQty > 0) {
            newMaterialLine = new MaterialLine();
            newMaterialLine.setDirectSend(DirectSendType.NO);
            newMaterialLine.setStatus(MaterialLineStatus.RECEIVED);
            newMaterialLine.setWhSiteId(whSiteId);
            newMaterialLine.setFinalWhSiteId(whSiteId);
            newMaterialLine.setOrderNo(orderNo);
            newMaterialLine.setStatusDate(DateUtil.getCurrentUTCDateTime());
            newMaterialLine.setMaterial(newMaterial);
            newMaterialLine.setMaterialOwner(newMaterial);
            newMaterialLine.setReceivedDate(DateUtil.getCurrentUTCDateTime());
            newMaterialLine.setQuantity(adhocReceiveQty);
            newMaterialLine.setDeliveryNoteLine(deliveryNoteLine);
            newMaterialLine.setProcureType(deliveryNoteLine.getOrderLine().getProcureLine().getProcureType().getMaterialLineProcureType());
            newMaterial.getMaterialLine().add(newMaterialLine);
        }

        if (damagedQty > 0) {
            MaterialLine blockedMaterialLine = new MaterialLine();
            blockedMaterialLine.setStatus(MaterialLineStatus.BLOCKED);
            blockedMaterialLine.setDirectSend(DirectSendType.NO);
            blockedMaterialLine.setMaterial(newMaterial);
            blockedMaterialLine.setMaterialOwner(newMaterial);
            blockedMaterialLine.setWhSiteId(whSiteId);
            blockedMaterialLine.setFinalWhSiteId(whSiteId);
            blockedMaterialLine.setOrderNo(orderNo);
            blockedMaterialLine.setStatusDate(DateUtil.getCurrentUTCDateTime());
            blockedMaterialLine.setReceivedDate(DateUtil.getCurrentUTCDateTime());
            blockedMaterialLine.setQuantity(damagedQty);
            blockedMaterialLine.setDeliveryNoteLine(deliveryNoteLine);
            blockedMaterialLine.setProcureType(deliveryNoteLine.getOrderLine().getProcureLine().getProcureType().getMaterialLineProcureType());
            blockedMaterialLine.getStatus().setInspectionStatus(blockedMaterialLine);
            materialServices.placeIntoZone(blockedMaterialLine, ZoneType.QUARANTINE);
            newMaterial.getMaterialLine().add(blockedMaterialLine);
        }
        
        materialServices.addMaterial(newMaterial);

        return newMaterialLine;
    }

    private static Material createAdhocMaterialOfTypeReleased(DeliveryNoteLine deliveryNoteLine, String unitOfMeasure,
            String partAffiliation, String partModification, String partName, String partNumber, String partVersion, String mailFormId, String receiver,
            Date requiredSTADate, FinanceHeader financeHeader) {
        OrderLine orderLine = deliveryNoteLine.getOrderLine();
        
        Material newMaterial = new Material();
        newMaterial.setMaterialType(MaterialType.RELEASED);
        newMaterial.setOrderLine(orderLine);
        newMaterial.setUnitOfMeasure(unitOfMeasure);
        newMaterial.setRequiredStaDate(requiredSTADate);
        newMaterial.setOrderNo(orderLine.getOrder().getOrderNo());
        newMaterial.setPartAffiliation(partAffiliation);
        
        newMaterial.setPartModification(partModification);
        newMaterial.setPartName(partName);
        newMaterial.setPartNumber(partNumber);
        newMaterial.setPartVersion(partVersion);
        
        FinanceHeader newFinanceHeader = ProcureTypeHelper.cloneFinanceHeader(financeHeader);
        newFinanceHeader.setProjectId(null);
        newMaterial.setFinanceHeader(newFinanceHeader);
        
        newMaterial.setMailFormId(mailFormId);
        newMaterial.setReceiver(receiver);

        ProcureLineHelper.associateMaterialWithProcureLine(newMaterial, orderLine.getProcureLine());

        orderLine.getMaterials().add(newMaterial);
        
        return newMaterial;
    }

    private static MaterialLine updateMaterialOnGoodsTransferReturn(DeliveryNoteLine deliveryNoteLine, DeliveryNoteSubLine deliveryNoteSubLine,
            MaterialLine materialLine, MaterialHeaderRepository requestHeaderRepository, MaterialServices materialServices, UserDTO user,
            TraceabilityRepository traceabilityRepository, Material transferMaterial, WarehouseServices warehouseServices) throws GloriaApplicationException {
        return materialLine.getStatus().transferReturn(deliveryNoteLine, deliveryNoteSubLine, materialLine, requestHeaderRepository, user,
                                                       traceabilityRepository, materialServices, transferMaterial, warehouseServices);
    }

    private static boolean hasValidOrderLine(List<OrderLine> orderLines) {
        boolean isValid = false;
        if (orderLines != null && !orderLines.isEmpty()) {
            for (OrderLine orderLine : orderLines) {
                if (orderLine.getStatus() != OrderLineStatus.COMPLETED) {
                    isValid = true;
                    break;
                }
            }
        }
        return isValid;
    }

    private static void createDeliveryNoteLineForOrderline(OrderLine orderLine, DeliveryNote deliveryNote, DangerousGoodsRepository dangerousGoodsRepository)
            throws GloriaApplicationException {
        List<Material> materials = orderLine.getMaterials();
        DeliveryNoteLine deliveryNoteLine = identifyValidDeliveryNoteLinesIfAny(deliveryNote, orderLine.getDeliveryNoteLines());
        Map<String, Long> directSendMaterialUsage = createDirectSendMaterialUsageMap(orderLine, materials);
        if (deliveryNoteLine == null) {
            deliveryNoteLine = new DeliveryNoteLine();
            deliveryNoteLine.setDeliveryNote(deliveryNote);
            deliveryNoteLine.setOrderLine(orderLine);
            deliveryNoteLine.setProjectId(orderLine.getProjectId());
            UniqueItems idSets = new UniqueItems();
            for (Material material : orderLine.getMaterials()) {
                MaterialHeader materialHeader = material.getMaterialHeader();
                if (materialHeader != null) {
                    idSets.add(materialHeader.getReferenceId());
                }
            }
            deliveryNoteLine.setReferenceIds(idSets.createCommaSeparatedKey());
            if (orderLine.getQiMarking() != null && orderLine.getQiMarking() == QiMarking.MANDATORY) {
                deliveryNoteLine.setSendToQI(true);
            }

            deliveryNoteLine.setStatus(DeliveryNoteLineStatus.IN_WORK);

            OrderLineVersion currentOrderLineVersion = orderLine.getCurrent();
            if (currentOrderLineVersion != null) {
                deliveryNoteLine.setPartVersion(currentOrderLineVersion.getPartVersion());
            }
            
            ProcureLine procureLine = orderLine.getProcureLine();
            if (procureLine != null) {
                deliveryNoteLine.setProcureInfo(procureLine.getWarehouseComment());
                deliveryNoteLine.setProcureType(procureLine.getProcureType());
                deliveryNoteLine.setPartModification(procureLine.getpPartModification());
                deliveryNoteLine.setPartNumber(procureLine.getpPartNumber());
                deliveryNoteLine.setPartName(procureLine.getpPartName());
                deliveryNoteLine.setPartAffiliation(procureLine.getpPartAffiliation());
                if (procureLine.getPartAlias() != null) {
                    deliveryNoteLine.setPartAlias(procureLine.getPartAlias().getAliasPartNumber());
                }
                DangerousGoods dangerousGood = dangerousGoodsRepository.findById(procureLine.getDangerousGoodsOID());
                if (dangerousGood != null) {
                    deliveryNoteLine.setDangerousGoodsName(dangerousGood.getName());
                    deliveryNoteLine.setDangerousGoodsFlag(dangerousGood.isFlag());
                }
            }
            deliveryNote.getDeliveryNoteLine().add(deliveryNoteLine);

            for (String key : directSendMaterialUsage.keySet()) {
                if (directSendMaterialUsage.get(key) > 0) {
                    DeliveryNoteSubLine deliveryNoteSubLine = new DeliveryNoteSubLine();
                    deliveryNoteSubLine.setDeliveryNoteLine(deliveryNoteLine);
                    deliveryNoteSubLine.setDirectSend("YES".equals(key));
                    deliveryNoteLine.getDeliveryNoteSubLines().add(deliveryNoteSubLine);
                }
            }
        }
        // update the quantities always
        deliveryNoteLine.setPossibleToReceiveQty(orderLine.getPossibleToReceiveQuantity() - orderLine.getReceivedQuantity());
        deliveryNoteLine.setDirectSendQuantity(directSendMaterialUsage.get("YES"));
        deliveryNoteLine.setDeliveryNoteQuantity(directSendMaterialUsage.get("YES") + directSendMaterialUsage.get("NO"));
    }

    private static Map<String, Long> createDirectSendMaterialUsageMap(OrderLine orderLine, List<Material> materials) {
        Map<String, Long> directSendMaterialUsage = new HashMap<String, Long>();
        directSendMaterialUsage.put("YES", 0L);
        directSendMaterialUsage.put("NO", 0L);

        for (Material material : materials) {
            directSendMaterialUsage.put("YES", evalueMaterialsForDirectSendOnRegularReceive(directSendMaterialUsage, material, "YES"));
        }

        Long toStoreQty = (orderLine.getPossibleToReceiveQuantity() - orderLine.getReceivedQuantity()) - directSendMaterialUsage.get("YES");
        directSendMaterialUsage.put("NO", toStoreQty);

        return directSendMaterialUsage;
    }

    private static Long evalueMaterialsForDirectSendOnRegularReceive(Map<String, Long> directSendMaterialUsage, Material material, String directSendType) {
        List<MaterialLine> materialLines = material.getMaterialLine();
        Long updatedQty = directSendMaterialUsage.get(directSendType);
        for (MaterialLine materialLine : materialLines) {
            DirectSendType directSend = materialLine.getDirectSend();
            if (isRemovedMaterialLines(materialLine) 
                    || (directSend != null && !directSend.isDirectSend()) || !material.getMaterialType().isReceiveble()) {
                continue;
            }
            // exclude the TRANSFERED / RETURNED items.(materials once requested will not be considered for REGULAR receive again)
            if ((materialLine.getDeliveryNoteLine() == null || materialLine.getDeliveryNoteLine().getDeliveryNote().getReceiveType() == ReceiveType.REGULAR)
                    && materialLine.getStatus().isReceiveble()) {
                updatedQty += materialLine.getQuantity();
            }
        }
        return updatedQty;
    }

    private static boolean isRemovedMaterialLines(MaterialLine materialLine) {
        return materialLine.getStatus() == MaterialLineStatus.REMOVED_DB || materialLine.getStatus() == MaterialLineStatus.REMOVED;
    }

    private static boolean hasValidDeliveryNoteLines(DeliveryNote deliveryNote, List<DeliveryNoteLine> deliveryNoteLines) {
        if (deliveryNoteLines != null && !deliveryNoteLines.isEmpty()) {
            for (DeliveryNoteLine deliveryNoteLine : deliveryNoteLines) {
                if (deliveryNote.getDeliveryNoteOID() == deliveryNoteLine.getDeliveryNote().getDeliveryNoteOID()
                        && deliveryNoteLine.getStatus() == DeliveryNoteLineStatus.IN_WORK) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static DeliveryNoteLine identifyValidDeliveryNoteLinesIfAny(DeliveryNote deliveryNote, List<DeliveryNoteLine> deliveryNoteLines) {
        if (deliveryNoteLines != null && !deliveryNoteLines.isEmpty()) {
            for (DeliveryNoteLine deliveryNoteLine : deliveryNoteLines) {
                if (deliveryNote.getDeliveryNoteOID() == deliveryNoteLine.getDeliveryNote().getDeliveryNoteOID()
                        && deliveryNoteLine.getStatus() == DeliveryNoteLineStatus.IN_WORK) {
                    return deliveryNoteLine;
                }
            }
        }
        return null;
    }

}
