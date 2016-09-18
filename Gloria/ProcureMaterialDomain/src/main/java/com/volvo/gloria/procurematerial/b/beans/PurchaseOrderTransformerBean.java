/*
 * Copyright 2009 Volvo Information Technology AB 
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
package com.volvo.gloria.procurematerial.b.beans;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.common.purchaseorder.c.dto.PurchaseOrderScheduleDTO;
import com.volvo.gloria.common.purchaseorder.c.dto.StandAloneOrderHeaderDTO;
import com.volvo.gloria.common.purchaseorder.c.dto.StandAloneOrderLineDTO;
import com.volvo.gloria.common.purchaseorder.c.dto.SyncPurchaseOrderTypeDTO;
import com.volvo.gloria.procurematerial.b.PurchaseOrderTransformer;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.gloria.util.xml.XmlTransformer;
import com.volvo.group.prototypepurchaseorder._1_0.CostCollectorType;
import com.volvo.group.prototypepurchaseorder._1_0.FinancialInformationType;
import com.volvo.group.prototypepurchaseorder._1_0.PriceStatusType;
import com.volvo.group.prototypepurchaseorder._1_0.StandAloneOrderHeaderType;
import com.volvo.group.prototypepurchaseorder._1_0.StandAloneOrderLineType;
import com.volvo.group.prototypepurchaseorder._1_0.StandAloneOrderType;
import com.volvo.group.prototypepurchaseorder._1_0.SyncStandAloneOrderDataAreaType;
import com.volvo.group.prototypepurchaseorder._1_0.SyncStandAloneOrderType;
import com.volvo.group.prototypepurchaseorder.components._1_0.ActionCriteriaType;
import com.volvo.group.prototypepurchaseorder.components._1_0.ActionExpressionType;
import com.volvo.group.prototypepurchaseorder.components._1_0.AmountType;
import com.volvo.group.prototypepurchaseorder.components._1_0.ApplicationAreaType;
import com.volvo.group.prototypepurchaseorder.components._1_0.CommunicationType;
import com.volvo.group.prototypepurchaseorder.components._1_0.CountryCodeType;
import com.volvo.group.prototypepurchaseorder.components._1_0.CustomerPartyType;
import com.volvo.group.prototypepurchaseorder.components._1_0.DescriptionType;
import com.volvo.group.prototypepurchaseorder.components._1_0.DocumentIDType;
import com.volvo.group.prototypepurchaseorder.components._1_0.FreightTermCodeType;
import com.volvo.group.prototypepurchaseorder.components._1_0.IdentifierType;
import com.volvo.group.prototypepurchaseorder.components._1_0.ItemIDType;
import com.volvo.group.prototypepurchaseorder.components._1_0.ItemType;
import com.volvo.group.prototypepurchaseorder.components._1_0.NameType;
import com.volvo.group.prototypepurchaseorder.components._1_0.OrderReferenceType;
import com.volvo.group.prototypepurchaseorder.components._1_0.PartyIDsType;
import com.volvo.group.prototypepurchaseorder.components._1_0.PaymentTermType;
import com.volvo.group.prototypepurchaseorder.components._1_0.PriceABIEType;
import com.volvo.group.prototypepurchaseorder.components._1_0.PurchaseOrderHeaderType;
import com.volvo.group.prototypepurchaseorder.components._1_0.PurchaseOrderLineType;
import com.volvo.group.prototypepurchaseorder.components._1_0.PurchaseOrderScheduleType;
import com.volvo.group.prototypepurchaseorder.components._1_0.QuantityType;
import com.volvo.group.prototypepurchaseorder.components._1_0.SemanticContactType;
import com.volvo.group.prototypepurchaseorder.components._1_0.SemanticPartyType;
import com.volvo.group.prototypepurchaseorder.components._1_0.SupplierPartyType;
import com.volvo.group.prototypepurchaseorder.components._1_0.TimePeriodType;
import com.volvo.group.prototypepurchaseorder.components._1_0.TransportationTermType;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * Purchase Order message transformer service implementation.
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class PurchaseOrderTransformerBean extends XmlTransformer implements PurchaseOrderTransformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseOrderTransformerBean.class);

    public PurchaseOrderTransformerBean() {
        super(XmlConstants.SchemaFullPath.PROTOTYPE_PURCHASE_ORDER, XmlConstants.PackageName.PROTOTYPE_PURCHASE_ORDER);
    }

    /**
     * Perform transformation from external information model to the application internal model.
     * 
     * @param receivedPurchaseOrderMessage
     *            the Message to transform
     * @return a SyncPurchaseOrderTypeDTO object
     */
    @Override
    public SyncPurchaseOrderTypeDTO transformStoredPurchaseOrder(String receivedPurchaseOrderMessage) {
        try {
            return (SyncPurchaseOrderTypeDTO) transform(receivedPurchaseOrderMessage);
        } catch (JAXBException e) {
            // Throw an exception to roll-back the transaction.
            LOGGER.warn("Unable to unmarshall the received message to a SyncPurchaseOrderTypeDTO object, message will be discarded. Exception is:", e);
            throw new GloriaSystemException(e, "Unable to unmarshall the received message to a SyncPurchaseOrderTypeDTO object, message will be discarded.");
        }
    }

    @Override
    protected Object transformFromJAXBToDTO(Object jaxbOject) {
        SyncStandAloneOrderType syncPurchaseOrderType = (SyncStandAloneOrderType) jaxbOject;
        SyncStandAloneOrderDataAreaType dataAreaType = syncPurchaseOrderType.getDataArea();
        
        SyncPurchaseOrderTypeDTO syncPurchaseOrderTypeDTO = new SyncPurchaseOrderTypeDTO();
        
        List<ActionCriteriaType> actionCriteriaTypes = dataAreaType.getSync().getActionCriteria();
        if (!actionCriteriaTypes.isEmpty()) {
            ActionCriteriaType actionCriteriaType = actionCriteriaTypes.get(0);
            List<ActionExpressionType> actionExpression = actionCriteriaType.getActionExpression();
            if (!actionExpression.isEmpty()) {
                syncPurchaseOrderTypeDTO.setAction(actionExpression.get(0).getActionCode()); // Action                
            }
        }
        
        syncPurchaseOrderTypeDTO.setReleaseID(syncPurchaseOrderType.getReleaseID());
        ApplicationAreaType applicationArea = syncPurchaseOrderType.getApplicationArea();
        String creationDateTime = applicationArea.getCreationDateTime();
        syncPurchaseOrderTypeDTO.setCreationDateTime(creationDateTime);

        List<StandAloneOrderType> standAloneOrderTypes = dataAreaType.getStandAloneOrder();
        List<StandAloneOrderHeaderDTO> standAloneOrderHeaderDTOs = new ArrayList<StandAloneOrderHeaderDTO>();
        syncPurchaseOrderTypeDTO.setStandAloneOrderHeaderDTO(standAloneOrderHeaderDTOs);
        for (StandAloneOrderType standAloneOrderType : standAloneOrderTypes) {
            StandAloneOrderHeaderDTO standAloneOrderHeaderDTO = new StandAloneOrderHeaderDTO();
            standAloneOrderHeaderDTOs.add(standAloneOrderHeaderDTO);

            readPurchaseOrderHeader(standAloneOrderHeaderDTO, standAloneOrderType.getPurchaseOrderHeader());

            List<StandAloneOrderLineDTO> standAloneOrderLineDTOs = new ArrayList<StandAloneOrderLineDTO>();
            standAloneOrderHeaderDTO.setStandAloneOrderLineDTO(standAloneOrderLineDTOs);

            List<JAXBElement<? extends PurchaseOrderLineType>> standAloneOrderLineTypes = standAloneOrderType.getPurchaseOrderLine();
            readPurchaseOrderLine(standAloneOrderLineDTOs, standAloneOrderLineTypes);
        }

        return syncPurchaseOrderTypeDTO;
    }

    private void readPurchaseOrderHeader(StandAloneOrderHeaderDTO standAloneOrderHeaderDTO,
            JAXBElement<? extends PurchaseOrderHeaderType> standAloneOrderHeaderTypeEl) {
        if (isJAXBElementNotNull(standAloneOrderHeaderTypeEl)) {
            StandAloneOrderHeaderType standAlonePurchaseOrderHeaderType = (StandAloneOrderHeaderType) standAloneOrderHeaderTypeEl.getValue();

            DocumentIDType documentIDType = standAlonePurchaseOrderHeaderType.getDocumentID();
            JAXBElement<? extends IdentifierType> documentIdEL = documentIDType.getID();
            standAloneOrderHeaderDTO.setOrderNo(getElIdentifierTypeValue(documentIdEL)); // OrderId

            List<DocumentIDType> altDocumentIDTypes = standAlonePurchaseOrderHeaderType.getAlternateDocumentID();
            if (!altDocumentIDTypes.isEmpty()) {
                DocumentIDType altDocumentIDType = altDocumentIDTypes.get(0);
                standAloneOrderHeaderDTO.setOrderIdGps(getElIdentifierTypeValue(altDocumentIDType.getID())); // GPSOrderID
                standAloneOrderHeaderDTO.setRevisionId(getIdentifierTypeValue(altDocumentIDType.getRevisionID()));
            }
            CustomerPartyType customerPartyType = standAlonePurchaseOrderHeaderType.getCustomerParty();

            JAXBElement<? extends PartyIDsType> custPartyIDsTypeEl = customerPartyType.getPartyIDs();
            if (isJAXBElementNotNull(custPartyIDsTypeEl)) {
                PartyIDsType custPartyIDType = custPartyIDsTypeEl.getValue();
                List<JAXBElement<? extends IdentifierType>> custPartyIds = custPartyIDType.getID();
                if (!custPartyIds.isEmpty()) {
                    JAXBElement<? extends IdentifierType> custPartyIdentifierType = custPartyIds.get(0);
                    standAloneOrderHeaderDTO.setMaterialUserId(getElIdentifierTypeValue(custPartyIdentifierType)); // MaterialUserID
                }

                List<NameType> custPartyNameTypes = customerPartyType.getName();
                if (!custPartyNameTypes.isEmpty()) {
                    NameType nameType = custPartyNameTypes.get(0);
                    standAloneOrderHeaderDTO.setMaterialUserName(nameType.getValue()); // MaterialUserName
                }

                standAloneOrderHeaderDTO.setMaterialUserCategory(customerPartyType.getCategory()); // MaterialUserCategory
            }

            JAXBElement<? extends SupplierPartyType> supplierPartyTypeEl = standAlonePurchaseOrderHeaderType.getSupplierParty();
            if (isJAXBElementNotNull(supplierPartyTypeEl)) {
                SupplierPartyType supplierPartyType = supplierPartyTypeEl.getValue();

                JAXBElement<? extends PartyIDsType> suppPartyIdTypeEl = supplierPartyType.getPartyIDs();
                if (isJAXBElementNotNull(suppPartyIdTypeEl)) {
                    PartyIDsType suppPartyId = suppPartyIdTypeEl.getValue();
                    List<JAXBElement<? extends IdentifierType>> suppPartyIds = suppPartyId.getID();
                    if (!suppPartyIds.isEmpty()) {
                        JAXBElement<? extends IdentifierType> suppPartyIdEl = suppPartyIds.get(0);
                        standAloneOrderHeaderDTO.setSupplierId(getElIdentifierTypeValue(suppPartyIdEl)); // SupplierID
                    }
                }

                List<NameType> suppNameTypes = supplierPartyType.getName();
                if (!suppNameTypes.isEmpty()) {
                    NameType suppName = suppNameTypes.get(0);
                    standAloneOrderHeaderDTO.setSupplierName(suppName.getValue()); // SupplierName
                }

                standAloneOrderHeaderDTO.setSupplierCategory(supplierPartyType.getCategory()); // SupplierCategory
            }

            standAloneOrderHeaderDTO.setOrderDateTime(standAlonePurchaseOrderHeaderType.getOrderDateTime()); // OrderDateTime
            standAloneOrderHeaderDTO.setOrderMode(standAlonePurchaseOrderHeaderType.getOrderMode().value()); // OrderMode
        }
    }
    
    private void loadPartDetails(StandAloneOrderLineDTO standAloneOrderLineDTO, ItemType itemType) {
        List<JAXBElement<? extends ItemIDType>> itemIDTypes = itemType.getItemID();
        if (!itemIDTypes.isEmpty()) {
            JAXBElement<? extends ItemIDType> itemIDTypeEl = itemIDTypes.get(0);
            if (isJAXBElementNotNull(itemIDTypeEl)) {
                ItemIDType itemIDType = itemIDTypeEl.getValue();

                JAXBElement<? extends IdentifierType> itemIdIdentifierType = itemIDType.getID();
                standAloneOrderLineDTO.setPartNumber(getElIdentifierTypeValue(itemIdIdentifierType)); // PartNumber
                standAloneOrderLineDTO.setPartVersion(getIdentifierTypeValue(itemIDType.getRevisionID())); // PartVersion

                com.volvo.group.prototypepurchaseorder._1_0.IdentifierType qualifierIdentifier 
                        = (com.volvo.group.prototypepurchaseorder._1_0.IdentifierType) itemIdIdentifierType.getValue();
                standAloneOrderLineDTO.setPartQualifier(qualifierIdentifier.getQualifier()); // PartQualifier
            }

            List<DescriptionType> descriptionTypes = itemType.getDescription();
            if (!descriptionTypes.isEmpty()) {
                DescriptionType partDescriptiontype = descriptionTypes.get(0);
                standAloneOrderLineDTO.setPartDescription(partDescriptiontype.getValue()); // PartDescription
            }
        }
        // Country Of Origin
        CountryCodeType countryCodeType = itemType.getCountryOfOriginCode();
        if (countryCodeType != null) {
            standAloneOrderLineDTO.setCountryOfOrigin(countryCodeType.getValue());
        }        
    }
    
    private void loadUnitDetail(StandAloneOrderLineDTO standAloneOrderLineDTO, PriceABIEType unitPrice) {
        AmountType amounttype = unitPrice.getAmount();
        // Unit Price OR Amount
        standAloneOrderLineDTO.setAmount(amounttype.getValue().doubleValue()); 
        
        QuantityType quantityType = unitPrice.getPerQuantity();
        // PerQuantity
        standAloneOrderLineDTO.setPerQuantity(quantityType.getValue().longValue()); 
        // Currency
        standAloneOrderLineDTO.setCurrency(amounttype.getCurrencyID()); 
        TimePeriodType timePeriodType = unitPrice.getTimePeriod();
        // UnitPriceTimePeriod
        standAloneOrderLineDTO.setUnitPriceTimePeriod(timePeriodType.getStartDateTime()); 
    }
    
    private void loadShipToDetail(StandAloneOrderLineDTO standAloneOrderLineDTO, SemanticPartyType shipToPartyType) {
        JAXBElement<? extends PartyIDsType> shipToPartyIdsEl = shipToPartyType.getPartyIDs();
        PartyIDsType shipToPartyId = shipToPartyIdsEl.getValue();        
        List<JAXBElement<? extends IdentifierType>> shipToPartyIds = shipToPartyId.getID();
        if (!shipToPartyIds.isEmpty()) {
            JAXBElement<? extends IdentifierType> shipToPartyIdEl = shipToPartyIds.get(0);
            // ShipToPartyID
            standAloneOrderLineDTO.setShipToPartyId(getElIdentifierTypeValue(shipToPartyIdEl)); 
        }
        List<NameType> shipToPartyNameTypes = shipToPartyType.getName();
        if (!shipToPartyNameTypes.isEmpty()) {
            NameType shipToPartyNameType = shipToPartyNameTypes.get(0);
            // ShipToPartyName
            standAloneOrderLineDTO.setShipToPartyName(shipToPartyNameType.getValue()); 
        }
    }
    
    private void readPurchaseOrderLine(List<StandAloneOrderLineDTO> standAloneOrderLineDTOs,
            List<JAXBElement<? extends PurchaseOrderLineType>> standAloneOrderLineTypes) {
        for (JAXBElement<? extends PurchaseOrderLineType> standAloneOrderLineType : standAloneOrderLineTypes) {
            StandAloneOrderLineDTO standAloneOrderLineDTO = new StandAloneOrderLineDTO();
            standAloneOrderLineDTOs.add(standAloneOrderLineDTO);

            if (isJAXBElementNotNull(standAloneOrderLineType)) {
                StandAloneOrderLineType standAlonePurchaseOrderLineType = (StandAloneOrderLineType) standAloneOrderLineType.getValue();

                ItemType itemType = standAlonePurchaseOrderLineType.getItem();

                loadPartDetails(standAloneOrderLineDTO, itemType);

                ItemIDType suppItemIDType = itemType.getSupplierItemID();

                JAXBElement<? extends IdentifierType> suppItemIDTypeEl = suppItemIDType.getID();
                // SupplierPartNumber
                standAloneOrderLineDTO.setSupplierPartNumber(getElIdentifierTypeValue(suppItemIDTypeEl)); 
                // Unit of Measure
                standAloneOrderLineDTO.setUnitOfMeasure(standAlonePurchaseOrderLineType.getUOMCode().getValue()); 
                
                PriceStatusType priceStatus = standAlonePurchaseOrderLineType.getPriceStatus();
                if (priceStatus != null) {
                    standAloneOrderLineDTO.setPriceType(priceStatus.value());
                }
                
                PriceABIEType unitPrice = standAlonePurchaseOrderLineType.getUnitPrice();

                loadUnitDetail(standAloneOrderLineDTO, unitPrice);

                SemanticPartyType shipToPartyType = standAlonePurchaseOrderLineType.getShipToParty();
                loadShipToDetail(standAloneOrderLineDTO, shipToPartyType);

                List<TransportationTermType> transportationTermTypes = standAlonePurchaseOrderLineType.getTransportationTerm();
                TransportationTermType transportationTermType = transportationTermTypes.get(0);
                FreightTermCodeType freightTermCodeType = transportationTermType.getFreightTermCode();
                standAloneOrderLineDTO.setFreightTermCode(freightTermCodeType.getValue()); // FreightTermCode

                List<PaymentTermType> paymentTermTypes = standAlonePurchaseOrderLineType.getPaymentTerm();
                PaymentTermType paymentTermType = paymentTermTypes.get(0);
                standAloneOrderLineDTO.setPaymentTerm(paymentTermType.getType()); // PaymentTerm

                List<OrderReferenceType> requisitionReferenceTypes = standAlonePurchaseOrderLineType.getRequisitionReference();
                OrderReferenceType orderReferenceType = requisitionReferenceTypes.get(0);
                DocumentIDType documentIDType = orderReferenceType.getDocumentID();
                JAXBElement<? extends IdentifierType> documentIDEl = documentIDType.getID();
                standAloneOrderLineDTO.setRequisitionIds(getElIdentifierTypeValue(documentIDEl)); // RequisitionID

                CustomerPartyType buyerPartyType = standAlonePurchaseOrderLineType.getBuyerParty();
                if (buyerPartyType != null) {
                    setBuyerDetail(standAloneOrderLineDTO, buyerPartyType);
                }
                FinancialInformationType financialInformationType = standAlonePurchaseOrderLineType.getFinancialInformation();
                if (financialInformationType != null) {
                    loadFinancialInformation(standAloneOrderLineDTO, financialInformationType);
                }
                List<PurchaseOrderScheduleDTO> purchaseOrderScheduleDTOs = new ArrayList<PurchaseOrderScheduleDTO>();
                standAloneOrderLineDTO.setPurchaseOrderSchedule(purchaseOrderScheduleDTOs);

                loadscheduleDetails(standAlonePurchaseOrderLineType, purchaseOrderScheduleDTOs);
            }
        }
    }
    
    private void setBuyerDetail(StandAloneOrderLineDTO standAloneOrderLineDTO, CustomerPartyType buyerPartyType) {
        JAXBElement<? extends PartyIDsType> buyerPartyIdsEl = buyerPartyType.getPartyIDs();
        PartyIDsType buyerPartyIDsType = buyerPartyIdsEl.getValue();
        List<JAXBElement<? extends IdentifierType>> buyerPartyIDsTypes = buyerPartyIDsType.getID();
        if (!buyerPartyIDsTypes.isEmpty()) {
            JAXBElement<? extends IdentifierType> buyerPartyIDTypeEl = buyerPartyIDsTypes.get(0);
            standAloneOrderLineDTO.setBuyerPartyId(getElIdentifierTypeValue(buyerPartyIDTypeEl)); // BuyerPartyID
        }
        JAXBElement<? extends SemanticContactType> buyerContactEl = buyerPartyType.getBuyerContact();
        com.volvo.group.prototypepurchaseorder._1_0.SemanticContactType buyerSemanticContactType =
                (com.volvo.group.prototypepurchaseorder._1_0.SemanticContactType) buyerContactEl.getValue();
        List<JAXBElement<? extends IdentifierType>> buyerIds = buyerSemanticContactType.getID();
        if (!buyerIds.isEmpty()) {
            JAXBElement<? extends IdentifierType> buyerIdEl = buyerIds.get(0);
            standAloneOrderLineDTO.setBuyerId(getElIdentifierTypeValue(buyerIdEl)); // BuyerId
        }
        standAloneOrderLineDTO.setBuyerName(getFirstOccurrenceOfName(buyerSemanticContactType.getName())); // BuyerName
        
        List<CommunicationType> communicationTypes = buyerSemanticContactType.getCommunication();
        if (!communicationTypes.isEmpty()) {
            standAloneOrderLineDTO.setBuyerEmail(communicationTypes.get(0).getURI()); // ContactInfo
        }

        List<IdentifierType> buyerSecurityIds = buyerSemanticContactType.getSecurityId();
        if (!buyerSecurityIds.isEmpty()) {
            standAloneOrderLineDTO.setBuyerSecurityId(getIdentifierTypeValue(buyerSecurityIds.get(0))); // SecurityID
        }
    }
    
    private void loadFinancialInformation(StandAloneOrderLineDTO standAloneOrderLineDTO, FinancialInformationType financialInformationType) {
        standAloneOrderLineDTO.setProjectNumber(financialInformationType.getObjectID()); // ProjectNumber

        CostCollectorType costCollectorType = financialInformationType.getCostCollector();

        // WBS one of these two will be WBS info costCollectorType.getCostCollectorType
        if (costCollectorType != null) {
            // WBS code
            standAloneOrderLineDTO.setWbsCode(costCollectorType.getValue()); 
        }
        // account
        standAloneOrderLineDTO.setAccount(financialInformationType.getFinancialAccount()); 
        // CostCenter
        standAloneOrderLineDTO.setCostCenter(financialInformationType.getCostCenter()); 
    }
    
    private void loadscheduleDetails(StandAloneOrderLineType standAlonePurchaseOrderLineType, List<PurchaseOrderScheduleDTO> purchaseOrderScheduleDTOs) {
        List<JAXBElement<PurchaseOrderScheduleType>> purchaseOrderSchedules = standAlonePurchaseOrderLineType.getPurchaseOrderSchedule();
        if (!purchaseOrderSchedules.isEmpty()) {
            for (JAXBElement<PurchaseOrderScheduleType> purchaseOrderSchedule : purchaseOrderSchedules) {
                PurchaseOrderScheduleDTO purchaseOrderScheduleDTO = new PurchaseOrderScheduleDTO();
                purchaseOrderScheduleDTOs.add(purchaseOrderScheduleDTO);

                if (isJAXBElementNotNull(purchaseOrderSchedule)) {
                    PurchaseOrderScheduleType purchaseOrderScheduleType = purchaseOrderSchedule.getValue();
                    QuantityType schedulQuantityType = purchaseOrderScheduleType.getQuantity();

                    purchaseOrderScheduleDTO.setQuantity(schedulQuantityType.getValue().longValue()); // Quantity
                    purchaseOrderScheduleDTO.setShipToArrive(purchaseOrderScheduleType.getRequiredDeliveryDateTime()); // ShipToArrive
                }
            }
        }
    }

    private String getFirstOccurrenceOfName(List<NameType> names) {
        if (!names.isEmpty()) {
            NameType nameType = names.get(0);
            return nameType.getValue();
        }
        return null;
    }

    private String getElIdentifierTypeValue(JAXBElement<? extends IdentifierType> element) {
        if (isJAXBElementNotNull(element)) {
            IdentifierType identifier = element.getValue();
            return identifier.getValue();
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

    @Override
    protected Object transformFromDTOToJAXB(Object dto) {
        return null;
    }

}
