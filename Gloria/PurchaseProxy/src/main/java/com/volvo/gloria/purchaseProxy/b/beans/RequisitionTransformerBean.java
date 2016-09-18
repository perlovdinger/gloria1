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
package com.volvo.gloria.purchaseProxy.b.beans;

import static com.volvo.gloria.util.c.GloriaParams.APPLICATION_ID;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;

import com.volvo.gloria.purchaseProxy.b.RequisitionTransformer;
import com.volvo.gloria.purchaseProxy.c.RequisitionDTO;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.gloria.util.xml.XmlTransformer;
import com.volvo.group.processrequisition._1_0.ContactType;
import com.volvo.group.processrequisition._1_0.CostCollectorType;
import com.volvo.group.processrequisition._1_0.DeliveryScheduleType;
import com.volvo.group.processrequisition._1_0.FinancialInformationType;
import com.volvo.group.processrequisition._1_0.ItemIDType;
import com.volvo.group.processrequisition._1_0.ItemType;
import com.volvo.group.processrequisition._1_0.OrderModeType;
import com.volvo.group.processrequisition._1_0.ProcessRequisitionDataAreaType;
import com.volvo.group.processrequisition._1_0.ProcessRequisitionType;
import com.volvo.group.processrequisition._1_0.RequisitionHeaderType;
import com.volvo.group.processrequisition._1_0.RequisitionLineType;
import com.volvo.group.processrequisition._1_0.RequisitionType;
import com.volvo.group.processrequisition._1_0.SemanticContactType;
import com.volvo.group.processrequisition._1_0.YesNoType;
import com.volvo.group.processrequisition.components._1_0.ActionCriteriaType;
import com.volvo.group.processrequisition.components._1_0.ActionExpressionType;
import com.volvo.group.processrequisition.components._1_0.AmountType;
import com.volvo.group.processrequisition.components._1_0.ApplicationAreaType;
import com.volvo.group.processrequisition.components._1_0.CodeType;
import com.volvo.group.processrequisition.components._1_0.CommunicationType;
import com.volvo.group.processrequisition.components._1_0.CustomerPartyType;
import com.volvo.group.processrequisition.components._1_0.DescriptionType;
import com.volvo.group.processrequisition.components._1_0.DocumentIDType;
import com.volvo.group.processrequisition.components._1_0.DocumentReferenceType;
import com.volvo.group.processrequisition.components._1_0.IdentifierType;
import com.volvo.group.processrequisition.components._1_0.NameType;
import com.volvo.group.processrequisition.components._1_0.NoteType;
import com.volvo.group.processrequisition.components._1_0.PartyIDsType;
import com.volvo.group.processrequisition.components._1_0.PlanningScheduleLineType;
import com.volvo.group.processrequisition.components._1_0.PriceABIEType;
import com.volvo.group.processrequisition.components._1_0.ProcessType;
import com.volvo.group.processrequisition.components._1_0.QuantityType;
import com.volvo.group.processrequisition.components._1_0.SemanticPartyType;
import com.volvo.group.processrequisition.components._1_0.TextType;
import com.volvo.group.processrequisition.components._1_0.UOMCodeType;
import com.volvo.group.processrequisition.components._1_0.UserAreaType;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * Requisition transformer service implementation.
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class RequisitionTransformerBean extends XmlTransformer implements RequisitionTransformer {
    private static final int REFERENCE_LENGTH = 10;
    private static final String NAMESPACE_URI_OPEN_APPLICATION = "http://www.openapplications.org/oagis/9";
    private static final String NAMESPACE_URI_VOLVO = "http://www.volvo.com/3P/Purchasing/2008/10";
    private static final int PROJEKT_ID_LENGTH = 8;
    private static final int PART_NAME_LENGTH = 25;
    private static final int PART_VERSION_LENGTH = 3;
    private static final int PART_NUMBER_LENGTH = 20;
    private static final int ISSUER_ORGANISATION_LENGTH = 4;
    private static final int REQUISITION_ID_LENGTH = 7;
    private static final int ISSUER_USER_ID = 8;
    private static final int ISSUER_PHONE_NO_LENGTH = 20;
    private static final int ISSUER_DEPARTMENT_LENGTH = 6;
    private static final int NAME_LENGTH = 35;
    private static final int MATERIAL_USER_ID_LENGTH = 9;
    private static final String YES = "Yes";    
    private static final String WBS = "WBS";
    private static final String RELEASE_ID_9_3 = "9.3";
    private static final String ADD = "Add";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public RequisitionTransformerBean() {
        super(XmlConstants.SchemaFullPath.REQUISITION, XmlConstants.PackageName.REQUISITION);
    }

    @Override
    protected Object transformFromJAXBToDTO(Object jaxbOject) {
        return null;
    }

    @Override
    protected ProcessRequisitionType transformFromDTOToJAXB(Object dto) {
        RequisitionDTO requisitionDTO = (RequisitionDTO) dto;
        ProcessRequisitionType processRequisitionType = new ProcessRequisitionType();
        ProcessRequisitionDataAreaType processRequisitionDataAreaType = new ProcessRequisitionDataAreaType();
        RequisitionType requisitionType = new RequisitionType();
        ApplicationAreaType applicationAreaType = new ApplicationAreaType();

        addRequisitionHeader(requisitionType, requisitionDTO);

        ProcessType processType = new ProcessType();
        ActionCriteriaType actionCriteriaType = new ActionCriteriaType();
        ActionExpressionType actionExpressionType = new ActionExpressionType();
        actionExpressionType.setActionCode(ADD);
        actionCriteriaType.getActionExpression().add(actionExpressionType);
        processType.getActionCriteria().add(actionCriteriaType);

        addRequisitionLine(requisitionType, requisitionDTO);

        processRequisitionDataAreaType.setProcess(processType);
        processRequisitionDataAreaType.getRequisition().add(requisitionType);
        processRequisitionType.setDataArea(processRequisitionDataAreaType);
        processRequisitionType.setSendingApplication(APPLICATION_ID);
        processRequisitionType.setReleaseID(RELEASE_ID_9_3);
        applicationAreaType.setCreationDateTime(DATE_FORMAT.format(DateUtil.getCurrentUTCDateTime()));
        processRequisitionType.setApplicationArea(applicationAreaType);
        return processRequisitionType;
    }

    private void addRequisitionHeader(RequisitionType requisitionType, RequisitionDTO requisitionDTO) {
        RequisitionHeaderType requisitionHeaderType = new RequisitionHeaderType();

        addDocumentID(requisitionHeaderType, requisitionDTO);

        addBuyerParty(requisitionDTO, requisitionHeaderType);

        addDocumentReference(requisitionHeaderType, requisitionDTO);

        addRequesterParty(requisitionHeaderType, requisitionDTO);

        requisitionHeaderType.setOrderMode(OrderModeType.PROTOTYPE);

        JAXBElement<? extends RequisitionHeaderType> requisitionHeaderElement = new JAXBElement<RequisitionHeaderType>(new QName(NAMESPACE_URI_VOLVO,
                                                                                                                                 "RequisitionHeader"),
                                                                                                                       RequisitionHeaderType.class,
                                                                                                                       requisitionHeaderType);
        requisitionType.setRequisitionHeader(requisitionHeaderElement);

    }

    private void addRequesterParty(RequisitionHeaderType requisitionHeaderType, RequisitionDTO requisitionDTO) {
        
        UserAreaType userAreaType = new UserAreaType();
        CodeType ppSuffix = new CodeType();
        ppSuffix.setValue(requisitionDTO.getPpSuffix());
        JAXBElement<CodeType> ppSuffixElement = new JAXBElement<CodeType>(new QName(NAMESPACE_URI_OPEN_APPLICATION, "Code"),
                CodeType.class, ppSuffix);
        userAreaType.getAny().add(ppSuffixElement);
        requisitionHeaderType.setUserArea(userAreaType);

        IdentifierType requesterId = new IdentifierType();
        requesterId.setValue(truncate(requisitionDTO.getMaterialUserId(), MATERIAL_USER_ID_LENGTH));
        JAXBElement<IdentifierType> requesterPartyIDTypeElement = new JAXBElement<IdentifierType>(new QName(NAMESPACE_URI_OPEN_APPLICATION, "ID"),
                                                                                                  IdentifierType.class, requesterId);
        PartyIDsType partyIDs = new PartyIDsType();
        JAXBElement<? extends PartyIDsType> partyIDsElement = new JAXBElement<PartyIDsType>(new QName(NAMESPACE_URI_OPEN_APPLICATION, "PartyIDs"),
                                                                                            PartyIDsType.class, partyIDs);
        partyIDs.getID().add(requesterPartyIDTypeElement);
        SemanticPartyType requesterParty = new SemanticPartyType();
        requesterParty.setPartyIDs(partyIDsElement);

        //Originator of the requisition - Requester
        ContactType contactTypeOriginator = new ContactType();

        NameType contactNameOriginator = new NameType();
        contactNameOriginator.setValue(truncate(requisitionDTO.getOriginatorUserId() + ", " + requisitionDTO.getOriginatorName(), NAME_LENGTH));
        contactTypeOriginator.getName().add(contactNameOriginator);

        JAXBElement<? extends ContactType> contactTypeElementOriginator = new JAXBElement<ContactType>(
                                                                                             new QName(NAMESPACE_URI_VOLVO, "Contact"),
                                                                                             ContactType.class, contactTypeOriginator);
        requesterParty.getContact().add(contactTypeElementOriginator);
        
        requisitionHeaderType.setRequesterParty(requesterParty);
    }

    private void addDocumentID(RequisitionHeaderType requisitionHeaderType, RequisitionDTO requisitionDTO) {
        DocumentIDType documentID = new DocumentIDType();
        IdentifierType id = new IdentifierType();
        id.setValue(truncate(requisitionDTO.getRequisitionId(), REQUISITION_ID_LENGTH));
        JAXBElement<IdentifierType> documentIDType = new JAXBElement<IdentifierType>(new QName(NAMESPACE_URI_OPEN_APPLICATION, "ID"),
                                                                                     IdentifierType.class, id);
        documentID.setID(documentIDType);
        IdentifierType variationId = new IdentifierType();
        variationId.setValue("");
        documentID.setVariationID(variationId);
        requisitionHeaderType.setDocumentID(documentID);
    }

    private void addDocumentReference(RequisitionHeaderType requisitionHeaderType, RequisitionDTO requisitionDTO) {
        DocumentReferenceType documentReference = new DocumentReferenceType();
        PartyIDsType partyIDType = new PartyIDsType();
        JAXBElement<? extends PartyIDsType> partyIDsType = new JAXBElement<PartyIDsType>(new QName(NAMESPACE_URI_OPEN_APPLICATION, "PartyIDs"),
                                                                                         PartyIDsType.class, partyIDType);
        IdentifierType partId = new IdentifierType();
        partId.setValue(truncate(requisitionDTO.getIssuerOrganisation(), ISSUER_ORGANISATION_LENGTH));
        JAXBElement<IdentifierType> partyIDTypeElement = new JAXBElement<IdentifierType>(new QName(NAMESPACE_URI_OPEN_APPLICATION, "ID"),
                                                                                         IdentifierType.class, partId);
        partyIDType.getID().add(partyIDTypeElement);
        SemanticPartyType issuingParty = new SemanticPartyType();
        issuingParty.setPartyIDs(partyIDsType);

        ContactType contactType = new ContactType();

        com.volvo.group.processrequisition._1_0.IdentifierType issuerIdentifier = new com.volvo.group.processrequisition._1_0.IdentifierType();
        issuerIdentifier.setValue("");
        JAXBElement<IdentifierType> issuerContactId = new JAXBElement<IdentifierType>(new QName(NAMESPACE_URI_VOLVO, "ID"),
                                                                                      IdentifierType.class, issuerIdentifier);
        contactType.getID().add(issuerContactId);

        NameType contactName = new NameType();
        contactName.setValue(truncate(requisitionDTO.getIssuerUserId() + ", " + requisitionDTO.getIssuerName(), NAME_LENGTH));
        contactType.getName().add(contactName);

        CommunicationType communicationType = new CommunicationType();
        TextType textType = new TextType();
        textType.setValue(truncate(requisitionDTO.getIssuerPhoneNo(), ISSUER_PHONE_NO_LENGTH));
        communicationType.setDialNumber(textType);
        contactType.getCommunication().add(communicationType);

        NameType contactDepartement = new NameType();
        contactDepartement.setValue(truncate(requisitionDTO.getIssuerDepartment(), ISSUER_DEPARTMENT_LENGTH));
        contactType.setDepartmentName(contactDepartement);

        IdentifierType securityId = new IdentifierType();
        securityId.setValue(truncate(requisitionDTO.getIssuerUserId(), ISSUER_USER_ID));
        contactType.getSecurityId().add(securityId);
        JAXBElement<? extends ContactType> contactTypeElement = new JAXBElement<ContactType>(
                                                                                             new QName(NAMESPACE_URI_VOLVO, "Contact"),
                                                                                             ContactType.class, contactType);
        issuingParty.getContact().add(contactTypeElement);
        documentReference.setIssuingParty(issuingParty);

        requisitionHeaderType.getDocumentReference().add(documentReference);
    }

    private void addBuyerParty(RequisitionDTO requisitionDTO, RequisitionHeaderType requisitionHeaderType) {
        CustomerPartyType buyerParty = new CustomerPartyType();
        IdentifierType buyerId = new IdentifierType();
        buyerId.setValue(requisitionDTO.getPurchaseOrganizationCode());
        JAXBElement<IdentifierType> buyerPartyIDElement = new JAXBElement<IdentifierType>(new QName(NAMESPACE_URI_OPEN_APPLICATION, "ID"),
                                                                                          IdentifierType.class, buyerId);
        PartyIDsType partyIDTs = new PartyIDsType();
        partyIDTs.getID().add(buyerPartyIDElement);
        JAXBElement<? extends PartyIDsType> customerPartyIDsType = new JAXBElement<PartyIDsType>(new QName(NAMESPACE_URI_OPEN_APPLICATION,
                                                                                                           "PartyIDs"), PartyIDsType.class, partyIDTs);
        buyerParty.setPartyIDs(customerPartyIDsType);

        SemanticContactType buyerContact = new SemanticContactType();
        com.volvo.group.processrequisition._1_0.IdentifierType buyerIdentifier = new com.volvo.group.processrequisition._1_0.IdentifierType();
        buyerIdentifier.setValue(requisitionDTO.getBuyerId());
        JAXBElement<IdentifierType> buyerContactId = new JAXBElement<IdentifierType>(new QName(NAMESPACE_URI_VOLVO, "ID"),
                                                                                     IdentifierType.class, buyerIdentifier);
        buyerContact.getID().add(buyerContactId);
        
        JAXBElement<String> plantBuyerYes = new JAXBElement<String>(new QName(NAMESPACE_URI_VOLVO, "PlantBuyer"), String.class, YES);
        buyerContact.getPlantBuyer().add(plantBuyerYes);
        JAXBElement<SemanticContactType> buyerContactElement = new JAXBElement<SemanticContactType>(new QName(NAMESPACE_URI_VOLVO,
                                                                                                              "BuyerContact"), SemanticContactType.class,
                                                                                                    buyerContact);
        buyerParty.setBuyerContact(buyerContactElement);

        requisitionHeaderType.setBuyerParty(buyerParty);
    }

    private void addRequisitionLine(RequisitionType requisitionType, RequisitionDTO requisitionDTO) {

        RequisitionLineType requisitionLineType = new RequisitionLineType();

        addQuantity(requisitionLineType, requisitionDTO);

        addUnitPrice(requisitionLineType, requisitionDTO);

        addRequiredDeliveryDateTime(requisitionLineType, requisitionDTO);

        addUserArea(requisitionLineType, requisitionDTO);

        addItem(requisitionLineType, requisitionDTO);

        addUOMCode(requisitionLineType, requisitionDTO);

        addFinantialInformation(requisitionLineType, requisitionDTO);

        addDeliverySchedule(requisitionLineType, requisitionDTO);

        JAXBElement<RequisitionLineType> requisitionLineTypeElement = new JAXBElement<RequisitionLineType>(new QName(NAMESPACE_URI_VOLVO, "RequisitionLine"),
                                                                                                           RequisitionLineType.class, requisitionLineType);
        requisitionType.getRequisitionLine().add(requisitionLineTypeElement);
    }

    private void addRequiredDeliveryDateTime(RequisitionLineType requisitionLineType, RequisitionDTO requisitionDTO) {
        if (requisitionDTO.getRequiredStaDate() != null) {
            requisitionLineType.setRequiredDeliveryDateTime(DATE_FORMAT.format(requisitionDTO.getRequiredStaDate()));
        }
    }

    private void addItem(RequisitionLineType requisitionLineType, RequisitionDTO requisitionDTO) {
        ItemType itemType = new ItemType();
        com.volvo.group.processrequisition._1_0.IdentifierType identifierType;
        ItemIDType itemIDType = new ItemIDType();
        identifierType = new com.volvo.group.processrequisition._1_0.IdentifierType();
        identifierType.setValue(truncate(requisitionDTO.getPartNumber(), PART_NUMBER_LENGTH));
        identifierType.setQualifier(requisitionDTO.getPartQualifier());
        JAXBElement<IdentifierType> idElement = new JAXBElement<IdentifierType>(new QName(NAMESPACE_URI_VOLVO, "ID"),
                                                                                IdentifierType.class, identifierType);
        itemIDType.setID(idElement);

        IdentifierType partVersion = new IdentifierType();
        partVersion.setValue(truncate(requisitionDTO.getPartVersion(), PART_VERSION_LENGTH));
        itemIDType.setRevisionID(partVersion);

        JAXBElement<? extends ItemIDType> itemIDTypeElement = new JAXBElement<ItemIDType>(new QName(NAMESPACE_URI_VOLVO, "ItemID"),
                                                                                          ItemIDType.class, itemIDType);
        itemType.getItemID().add(itemIDTypeElement);
        DescriptionType descriptionType = new DescriptionType();

        descriptionType.setValue(truncate(requisitionDTO.getPartName(), PART_NAME_LENGTH));
        itemType.getDescription().add(descriptionType);
        itemType.setInspectionPart(YesNoType.YES);
        itemType.setApproveToPurchase(YES);
        itemType.setFunctionGroup(BigInteger.valueOf(0));

        JAXBElement<? extends ItemType> itemTypeElement = new JAXBElement<ItemType>(new QName(NAMESPACE_URI_VOLVO, "Item"),
                                                                                    ItemType.class, itemType);
        requisitionLineType.getRest().add(itemTypeElement);

    }

    private void addUserArea(RequisitionLineType requisitionLineType, RequisitionDTO requisitionDTO) {
        UserAreaType userArea = new UserAreaType();
        String purchaseInfo1 = requisitionDTO.getPurchaseInfo1();
        if (!StringUtils.isEmpty(purchaseInfo1)) {
            NoteType note1 = new NoteType();
            note1.setValue(purchaseInfo1);
            JAXBElement<? extends NoteType> note1Element = new JAXBElement<NoteType>(new QName(NAMESPACE_URI_OPEN_APPLICATION, "Note"),
                                                                                     NoteType.class, note1);

            userArea.getAny().add(note1Element);
        }
        String purchaseInfo2 = requisitionDTO.getPurchaseInfo2();
        if (!StringUtils.isEmpty(purchaseInfo2)) {
            NoteType note2 = new NoteType();
            note2.setValue(purchaseInfo2);
            JAXBElement<? extends NoteType> note2Element = new JAXBElement<NoteType>(new QName(NAMESPACE_URI_OPEN_APPLICATION, "Note"),
                                                                                     NoteType.class, note2);

            userArea.getAny().add(note2Element);
        }
        requisitionLineType.setUserArea(userArea);
    }

    private void addUnitPrice(RequisitionLineType requisitionLineType, RequisitionDTO requisitionDTO) {
        if (requisitionDTO.getPriceType() != null) {
            AmountType amountType = new AmountType();
            if (requisitionDTO.getMaximumPrice() != null) {
                amountType.setValue(BigDecimal.valueOf(requisitionDTO.getMaximumPrice()));
            } else {
                amountType.setValue(BigDecimal.ZERO);
            }
            amountType.setCurrencyID(requisitionDTO.getMaximumcurrency());
            PriceABIEType priceABIEType = new PriceABIEType();
            priceABIEType.setAmount(amountType);
            QuantityType quantityType = new QuantityType();
            quantityType.setValue(BigDecimal.ONE);
            priceABIEType.setPerQuantity(quantityType);
            CodeType code = new CodeType();
            code.setValue(requisitionDTO.getPriceType());
            priceABIEType.getCode().add(code);
            requisitionLineType.setUnitPrice(priceABIEType);
        }
    }

    private void addQuantity(RequisitionLineType requisitionLineType, RequisitionDTO requisitionDTO) {
        QuantityType quantityType = new QuantityType();
        if (requisitionDTO.getQuantity() != null) {
            quantityType.setValue(BigDecimal.valueOf(requisitionDTO.getQuantity()));
        } else {
            quantityType.setValue(BigDecimal.ZERO);
        }
        requisitionLineType.setQuantity(quantityType);

    }

    private void addDeliverySchedule(RequisitionLineType requisitionLineType, RequisitionDTO requisitionDTO) {
        DeliveryScheduleType deliveryScheduleType = new DeliveryScheduleType();

        PlanningScheduleLineType planningScheduleLine = new PlanningScheduleLineType();

        if (requisitionDTO.getRequiredStaDate() != null) {
            planningScheduleLine.setShipmentDateTime(DATE_FORMAT.format(requisitionDTO.getRequiredStaDate()));
        }

        QuantityType itemQuantityType = new QuantityType();
        BigDecimal itemQuantity = new BigDecimal(requisitionDTO.getQuantity());
        itemQuantityType.setValue(itemQuantity);
        planningScheduleLine.setItemQuantity(itemQuantityType);
        if (!StringUtils.isEmpty(requisitionDTO.getReference())) {
            NoteType note = new NoteType();
            note.setValue(truncate(requisitionDTO.getReference(), REFERENCE_LENGTH));
            planningScheduleLine.getNote().add(note);
        }

        deliveryScheduleType.getPlanningScheduleLine().add(planningScheduleLine);
        JAXBElement<DeliveryScheduleType> deliveryScheduleTypeElement = 
                new JAXBElement<DeliveryScheduleType>(
                    new QName(NAMESPACE_URI_VOLVO, "DeliverySchedule"),
                    DeliveryScheduleType.class, deliveryScheduleType);

        requisitionLineType.getRest().add(deliveryScheduleTypeElement);
    }

    private void addFinantialInformation(RequisitionLineType requisitionLineType, RequisitionDTO requisitionDTO) {

        FinancialInformationType financialInformationType = new FinancialInformationType();
        financialInformationType.setObjectID(truncate(requisitionDTO.getProjectId(), PROJEKT_ID_LENGTH));
        financialInformationType.setCostCenter(requisitionDTO.getCostCenter());
        financialInformationType.setFinancialAccount(requisitionDTO.getGlAccount());
        CostCollectorType costCollector = new CostCollectorType();
        costCollector.setCostCollectorType(WBS);
        costCollector.setValue(requisitionDTO.getWbsCode());
        financialInformationType.setCostCollector(costCollector);
        JAXBElement<FinancialInformationType> financialInformationTypeElement = new JAXBElement<FinancialInformationType>(new QName(NAMESPACE_URI_VOLVO,
                                                                                                                                    "FinancialInformation"),
                                                                                                                          FinancialInformationType.class,
                                                                                                                          financialInformationType);

        requisitionLineType.getRest().add(financialInformationTypeElement);
    }

    private void addUOMCode(RequisitionLineType requisitionLineType, RequisitionDTO requisitionDTO) {
        UOMCodeType uomCodeType = new UOMCodeType();
        // TODO: WBS codes should be used
        uomCodeType.setValue(requisitionDTO.getUnitOfMeasure());
        JAXBElement<UOMCodeType> uomTypeElement = new JAXBElement<UOMCodeType>(new QName(NAMESPACE_URI_VOLVO, "UOMCode"),
                                                                               UOMCodeType.class, uomCodeType);

        requisitionLineType.getRest().add(uomTypeElement);
    }

    private String truncate(String str, int end) {
        return StringUtils.substring(str, 0, end);

    }

}
