package com.volvo.gloria.financeProxy.b.beans;

import static com.volvo.gloria.util.c.GloriaParams.APPLICATION_ID;

import java.math.BigDecimal;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.financeProxy.b.GoodsReceiptTransformer;
import com.volvo.gloria.financeProxy.c.GoodsReceiptHeaderTransformerDTO;
import com.volvo.gloria.financeProxy.c.GoodsReceiptLineTransformerDTO;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.gloria.util.xml.XmlTransformer;
import com.volvo.group.finance.common._1_0.MessageHeaderType;
import com.volvo.group.finance.common._1_0.MessageHeaderType.Receiver;
import com.volvo.group.finance.common._1_0.MessageHeaderType.Sender;
import com.volvo.group.finance.common._1_0.ProcessType;
import com.volvo.group.finance.goodsmovement._1_0.GoodsMovementType;
import com.volvo.group.finance.goodsmovement._1_0.GoodsMovementType.GoodsMovementHeader;
import com.volvo.group.finance.goodsmovement._1_0.GoodsMovementType.GoodsMovementItems;
import com.volvo.group.finance.goodsmovement._1_0.ProcessGoodsMovement;
import com.volvo.jvs.runtime.platform.ContainerManaged;

@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class GoodsReceiptTransformerBean extends XmlTransformer implements GoodsReceiptTransformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsReceiptTransformerBean.class);
    
    private static final String ACTION = "Create";
    private static final String MSG_VERSION = "1.0.0";
    private static final String RECEIVER_LOGICAL_ID = "VGMF";
    private static final String RECEIVER_COMPONENT_ID = "FD1";

    public GoodsReceiptTransformerBean() {
        super(XmlConstants.SchemaFullPath.GOODS_MOVEMENT, XmlConstants.PackageName.GOODS_MOVEMENT);
    }

    @Override
    protected ProcessGoodsMovement transformFromDTOToJAXB(Object dto) {

        GoodsReceiptHeaderTransformerDTO goodsReceiptHeaderTransformerDTO = (GoodsReceiptHeaderTransformerDTO) dto;

        ProcessGoodsMovement processGoodsMovement = new ProcessGoodsMovement();

        ProcessType processType = new ProcessType();
        MessageHeaderType messageHeaderType = new MessageHeaderType();

        messageHeaderType.setMsgVersion(MSG_VERSION);

        Sender sender = new Sender();
        sender.setLogicalId(APPLICATION_ID);
        messageHeaderType.setSender(sender);

        Receiver receiver = new Receiver();
        receiver.setLogicalId(RECEIVER_LOGICAL_ID);
        receiver.setComponentId(RECEIVER_COMPONENT_ID);
        messageHeaderType.setReceiver(receiver);

        XMLGregorianCalendar creationDateTime = null;

        try {
            creationDateTime = DateUtil.getXMLGreorianCalendarWithTimeStamp(DateUtil.getCurrentUTCDate());
        } catch (DatatypeConfigurationException e1) {
            LOGGER.error("An error occured transforming ProcessGoodsMovement DTO to JAX - Date conversion creationDateTime - " + e1.getMessage());
        }

        messageHeaderType.setCreationDateTime(creationDateTime);

        processType.setMessageHeader(messageHeaderType);

        processType.setAction(ACTION);

        processGoodsMovement.setProcess(processType);

        GoodsMovementType goodsMovementType = new GoodsMovementType();

        GoodsMovementHeader goodsMovementHeader = new GoodsMovementHeader();
        goodsMovementHeader.setCompanyCode(goodsReceiptHeaderTransformerDTO.getCompanyCode());
        goodsMovementHeader.setAssignCodeGM(goodsReceiptHeaderTransformerDTO.getAssignCodeGM());
        XMLGregorianCalendar documentDate = null;
        XMLGregorianCalendar postingDate = null;
        try {
            documentDate =  DateUtil.getXMLGreorianCalendar(goodsReceiptHeaderTransformerDTO.getDocumentDate());
            postingDate = DateUtil.getXMLGreorianCalendar(goodsReceiptHeaderTransformerDTO.getPostingDateTime());
        } catch (DatatypeConfigurationException e) {
            LOGGER.error("An error occured transforming ProcessGoodsMovement DTO to JAX - Date conversion documentDate/postingDate - " + e.getMessage());
        }
        goodsMovementHeader.setDocumentDate(documentDate);
        goodsMovementHeader.setPostingDate(postingDate);

        goodsMovementHeader.setHeadertext(goodsReceiptHeaderTransformerDTO.getHeaderText());
        goodsMovementHeader.setReferenceDocument(goodsReceiptHeaderTransformerDTO.getReferenceDocument());
        goodsMovementType.setGoodsMovementHeader(goodsMovementHeader);

        for (GoodsReceiptLineTransformerDTO gloriaReceiptLineTransformerDTO : goodsReceiptHeaderTransformerDTO.getGoodsReceiptLineTransformerDTOs()) {
            GoodsMovementItems goodsMovementItem = new GoodsMovementItems();
            goodsMovementItem.setMovementType(gloriaReceiptLineTransformerDTO.getMovementType());
            goodsMovementItem.setOrderReference(gloriaReceiptLineTransformerDTO.getOrderReference());
            goodsMovementItem.setVendor(gloriaReceiptLineTransformerDTO.getVendor());
            goodsMovementItem.setVendorMaterialNumber(gloriaReceiptLineTransformerDTO.getVendorMaterialNumber());
            goodsMovementItem.setPlant(gloriaReceiptLineTransformerDTO.getPlant());
            goodsMovementItem.setISOUnitOfMeasure(gloriaReceiptLineTransformerDTO.getIsoUnitOfMeasure());
            goodsMovementItem.setQuantity(new BigDecimal(gloriaReceiptLineTransformerDTO.getQuantity()));
            goodsMovementItem.setMovementIndicator(gloriaReceiptLineTransformerDTO.getMovementIndicator());
            goodsMovementType.getGoodsMovementItems().add(goodsMovementItem);
        }
        processGoodsMovement.setGoodsMovement(goodsMovementType);

        return processGoodsMovement;
    }

    @Override
    protected Object transformFromJAXBToDTO(Object jaxbOject) {
        return null;
    }
}
