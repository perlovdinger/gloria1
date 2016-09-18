/*
 * Copyright 2013 Volvo Information Technology AB 
 * 
 * Licensed under the Volvo IT Corporate Source License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *      http://java.volvo.com/licenses/CORPORATE-SOURCE-LICENSE 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.volvo.gloria.common.b.beans;

import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.common.b.CarryOverTransformer;
import com.volvo.gloria.common.carryover.c.dto.CarryOverItemDTO;
import com.volvo.gloria.common.carryover.c.dto.SyncPurchaseOrderCarryOverDTO;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.gloria.util.xml.XmlTransformer;
import com.volvo.group.purchaseorder._1_0.OrderModeType;
import com.volvo.group.purchaseorder._1_0.PurchaseOrderHeaderType;
import com.volvo.group.purchaseorder._1_0.PurchaseOrderType;
import com.volvo.group.purchaseorder._1_0.SyncPurchaseOrderDataAreaType;
import com.volvo.group.purchaseorder._1_0.SyncPurchaseOrderType;
import com.volvo.group.purchaseorder.components._1_0.ActionCriteriaType;
import com.volvo.group.purchaseorder.components._1_0.ActionExpressionType;
import com.volvo.group.purchaseorder.components._1_0.CustomerPartyType;
import com.volvo.group.purchaseorder.components._1_0.IdentifierType;
import com.volvo.group.purchaseorder.components._1_0.ItemIDType;
import com.volvo.group.purchaseorder.components._1_0.ItemType;
import com.volvo.group.purchaseorder.components._1_0.NameType;
import com.volvo.group.purchaseorder.components._1_0.PartyIDsType;
import com.volvo.group.purchaseorder.components._1_0.PurchaseOrderLineType;
import com.volvo.group.purchaseorder.components._1_0.SupplierPartyType;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * Service implementation of Carry over message transformer interface.
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class CarryOverTransformerBean extends XmlTransformer implements CarryOverTransformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CarryOverTransformerBean.class);
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    public CarryOverTransformerBean() {
        super(XmlConstants.SchemaFullPath.PURCHASE_ORDER, XmlConstants.PackageName.PURCHASE_ORDER);
    }

    @Override
    public SyncPurchaseOrderCarryOverDTO transformStoredCarryOver(String receivedCarryOverMessage) {
        try {
            return (SyncPurchaseOrderCarryOverDTO) transform(receivedCarryOverMessage);
        } catch (JAXBException e) {
            // Throw an exception to roll-back the transaction.
            LOGGER.error("Unable to unmarshall the received message to a SyncCarryOverDTO object, message will be discarded. Exception is:", e);
            throw new GloriaSystemException(e, "Unable to unmarshall the received message to a SyncCarryOverDTO object, message will be discarded.");
        }
    }

    @Override
    protected Object transformFromJAXBToDTO(Object jaxbOject) {
        SyncPurchaseOrderType syncPurchaseOrderType = (SyncPurchaseOrderType) jaxbOject;

        // Substructure : SyncPurchaseOrder.DataArea.*
        SyncPurchaseOrderDataAreaType dataAreaType = syncPurchaseOrderType.getDataArea();
        SyncPurchaseOrderCarryOverDTO syncPurchaseOrderCarryOverDTO = new SyncPurchaseOrderCarryOverDTO();

        com.volvo.group.purchaseorder.components._1_0.ApplicationAreaType applicationArea = syncPurchaseOrderType.getApplicationArea();
        String creationDateTime = applicationArea.getCreationDateTime();
        syncPurchaseOrderCarryOverDTO.setCreationDateTime(creationDateTime);

        if (dataAreaType != null) {
            // Action
            List<ActionCriteriaType> actionCriteriaTypes = dataAreaType.getSync().getActionCriteria();
            if (actionCriteriaTypes != null && !actionCriteriaTypes.isEmpty()) {
                ActionCriteriaType actionCriteriaType = actionCriteriaTypes.get(0);
                List<ActionExpressionType> actionExpression = actionCriteriaType.getActionExpression();
                if (actionExpression != null && !actionExpression.isEmpty()) {
                    syncPurchaseOrderCarryOverDTO.setAction(actionExpression.get(0).getActionCode()); // Action
                }
            }

            // Substructure : SyncPurchaseOrder.DataArea.PurchaseOrder.*
            List<PurchaseOrderType> purchaseOrderTypes = dataAreaType.getPurchaseOrder();
            List<CarryOverItemDTO> carryOverItemDTOs = new ArrayList<CarryOverItemDTO>();
            syncPurchaseOrderCarryOverDTO.setCarryOverItemDTOs(carryOverItemDTOs);

            for (PurchaseOrderType purchaseOrderType : purchaseOrderTypes) {
                CarryOverItemDTO carryOverItemDTO = new CarryOverItemDTO();
                carryOverItemDTOs.add(carryOverItemDTO);

                // Substructure : PurchaseOrderHeader
                readPurchaseOrderHeader(carryOverItemDTO, purchaseOrderType);

                // Substructure : PurchaseOrderLine
                readPurchaseOrderLine(carryOverItemDTO, purchaseOrderType);
            }
        }
        return syncPurchaseOrderCarryOverDTO;
    }

    @Override
    protected Object transformFromDTOToJAXB(Object dto) {
        return null;
    }

    private void readPurchaseOrderHeader(CarryOverItemDTO carryOverItemDTO, PurchaseOrderType purchaseOrderType) {
        JAXBElement<? extends com.volvo.group.purchaseorder.components._1_0.PurchaseOrderHeaderType> purchaseOrderHeaderTypeEl 
                                                            = purchaseOrderType.getPurchaseOrderHeader();
        if (isJAXBElementNotNull(purchaseOrderHeaderTypeEl)) {
            PurchaseOrderHeaderType purchaseOrderHeaderType = (PurchaseOrderHeaderType) purchaseOrderHeaderTypeEl.getValue();

            OrderModeType orderMode = purchaseOrderHeaderType.getOrderMode();
            carryOverItemDTO.setOrderMode(orderMode.value());
            carryOverItemDTO.setDocumentId(getElIdentifierTypeValue(purchaseOrderHeaderType.getDocumentID().getID()));
           
            // Substructure : PurchaseOrderHeader.CustomerParty
            CustomerPartyType customerPartyType = purchaseOrderHeaderType.getCustomerParty();
            // Substructure : PurchaseOrderHeader.CustomerParty.PartyIDs
            JAXBElement<? extends PartyIDsType> custPartyIDEl = customerPartyType.getPartyIDs();
            if (isJAXBElementNotNull(custPartyIDEl)) {
                PartyIDsType partyIDsType = custPartyIDEl.getValue();
                carryOverItemDTO.setCustomerId(getElIdentifierTypeValue(partyIDsType.getID()));
            }
            carryOverItemDTO.setCustomerName(getFirstOccurrenceOfName(customerPartyType.getName()));

            // Substructure : PurchaseOrderHeader.SupplierParty
            JAXBElement<? extends SupplierPartyType> supplierPartyTypeEl = purchaseOrderHeaderType.getSupplierParty();
            if (isJAXBElementNotNull(supplierPartyTypeEl)) {
                SupplierPartyType supplierPartyType = supplierPartyTypeEl.getValue();
                JAXBElement<? extends PartyIDsType> suppPartyIDsTypeEl = supplierPartyType.getPartyIDs();
                if (isJAXBElementNotNull(suppPartyIDsTypeEl)) {
                    carryOverItemDTO.setSupplierId(getElIdentifierTypeValue(suppPartyIDsTypeEl.getValue().getID()));
                }
                carryOverItemDTO.setSupplierName(getFirstOccurrenceOfName(supplierPartyType.getName()));
                carryOverItemDTO.setSupplierCountryCode(supplierPartyType.getLocation().get(0).getAddress().get(0).getCountryCode().getValue());
            }

            // StartDate
            try {
                if (purchaseOrderHeaderType.getRequestedShipDateTime() != null) {
                    Date startDate = new Date(DateUtil.getStringAsDate(purchaseOrderHeaderType.getRequestedShipDateTime(), DATE_PATTERN).getTime());
                    carryOverItemDTO.setStartDate(startDate);
                }
            } catch (ParseException e) {
                throw new GloriaSystemException(e, "Invalid Date Format");
            }
        }
    }

    private void readPurchaseOrderLine(CarryOverItemDTO carryOverItemDTO, PurchaseOrderType purchaseOrderType) {
        List<JAXBElement<? extends PurchaseOrderLineType>> purchaseOrderLineTypes = purchaseOrderType.getPurchaseOrderLine();
        for (JAXBElement<? extends PurchaseOrderLineType> purchaseOrderLineTypeEl : purchaseOrderLineTypes) {
            if (isJAXBElementNotNull(purchaseOrderLineTypeEl)) {
                PurchaseOrderLineType purchaseOrderLineType = purchaseOrderLineTypeEl.getValue();
                // Substructure : PurchaseOrderLine.Item
                ItemType itemType = purchaseOrderLineType.getItem();

                // Substructure : PurchaseOrderLine.Item.ItemID
                List<JAXBElement<? extends ItemIDType>> partItemIDs = itemType.getItemID();
                if (partItemIDs != null && !partItemIDs.isEmpty()) {
                    // first element is relevant
                    ItemIDType partIDType = partItemIDs.get(0).getValue();
                    if (partIDType != null) {
                        carryOverItemDTO.setPartNumber(getElIdentifierTypeValue(partIDType.getID()));
                        carryOverItemDTO.setPartVersion(getIdentifierTypeValue(partIDType.getRevisionID()));
                        carryOverItemDTO.setPartAffliation(getQualifierValue(partIDType.getID()));
                    }
                }
                // Substructure : PurchaseOrderLine.Item.SupplierItemID
                ItemIDType suppPartItemIDs = itemType.getSupplierItemID();
                if (suppPartItemIDs != null) {
                    carryOverItemDTO.setSupplierPartNumber(getElIdentifierTypeValue(suppPartItemIDs.getID()));
                    carryOverItemDTO.setSupplierPartVersion(getIdentifierTypeValue(suppPartItemIDs.getRevisionID()));
                }
                carryOverItemDTO.setUnitCode(purchaseOrderLineType.getQuantity().getUnitCode());
                carryOverItemDTO.setCurrency(purchaseOrderLineType.getUnitPrice().getValue().getAmount().getCurrencyID());
                carryOverItemDTO.setAmount(purchaseOrderLineType.getUnitPrice().getValue().getAmount().getValue().doubleValue());
                carryOverItemDTO.setPriceUnit(purchaseOrderLineType.getUnitPrice().getValue().getPerQuantity().getUnitCode());
            }
        }
    }

    private String getElIdentifierTypeValue(JAXBElement<? extends IdentifierType> element) {
        if (isJAXBElementNotNull(element)) {
            IdentifierType identifier = element.getValue();
            return identifier.getValue();
        }
        return null;
    }
    
    private String getQualifierValue(JAXBElement<? extends IdentifierType> element) {
        if (isJAXBElementNotNull(element)) {
            com.volvo.group.purchaseorder._1_0.IdentifierType identifier = (com.volvo.group.purchaseorder._1_0.IdentifierType) element.getValue();
            return identifier.getQualifier();
        }
        return null;
    }

    private String getElIdentifierTypeValue(List<JAXBElement<? extends IdentifierType>> element) {
        if (element != null && !element.isEmpty()) {
            return getElIdentifierTypeValue(element.get(0));
        }
        return null;
    }

    private String getIdentifierTypeValue(IdentifierType identifier) {
        if (identifier != null) {
            return identifier.getValue();
        }
        return null;
    }

    private Boolean isJAXBElementNotNull(JAXBElement<?> element) {
        return !element.isNil();
    }

    private String getFirstOccurrenceOfName(List<NameType> names) {
        if (names != null && !names.isEmpty()) {
            NameType nameType = names.get(0);
            return nameType.getValue();
        }
        return null;
    }
}
