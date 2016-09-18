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
package com.volvo.gloria.util.xml;

/**
 * XML Constants.
 */
public abstract class XmlConstants {
    
    public static final String W3C_SCHEMA_INSTANCE_2001_NAMESPACE_URI = javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
    public static final String W3C_SCHEMA_INSTANCE_2001_NAMESPACE_PREFIX = "xsi";
    
    private XmlConstants() {
        // should not be initialized
    }

    /**
     * Inner class for SchemaFullPath.
     */
    public static final class SchemaFullPath {

        public static final String USER_AND_ORGANISATION = "xsd/userorganisation/UserAndOrganisation_1_4_0.xsd";
        public static final String USER_TEAM = "xsd/initdata/teamUser/TeamUser.xsd";
        public static final String GOODS_MOVEMENT = "xsd/goodsmovement/GoodsMovement_1_0/GoodsMovement_1_0.xsd";
        public static final String COST_CENTER = "xsd/costcenter/1FC_Messages/CostCenter_1_0.xsd";
        public static final String WBS = "xsd/wbs/1FC_Messages/WBSElementShort_1_0.xsd";
        public static final String PURCHASE_ORDER = "xsd/purchaseorder/org_volvo_purchasing/3P/BODs/outgoingMessages/SyncPurchaseOrder.xsd";
        public static final String PROTOTYPE_PURCHASE_ORDER = "xsd/purchaseorder/org_volvo_purchasing/3P/BODs/pporderOutgoingMessages/SyncStandAloneOrder.xsd";
        public static final String INIT_WAREHOUSE = "xsd/initdata/warehouse/Warehouse.xsd";
        public static final String INIT_BUILDCHANGE = "xsd/initdata/buildchange/BuildChange.xsd";
        public static final String INIT_PROCUREMENT_BUILDCHANGE = "xsd/initdata/procurement/Procurement.xsd";
        public static final String INIT_MATERIAL = "xsd/initdata/material/Material.xsd";
        public static final String INIT_DELIVERY = "xsd/initdata/delivery/Delivery.xsd";
        public static final String INIT_USER = "xsd/initdata/userdomain/UserDomainDetails.xsd";
        public static final String CONFIG_GLORIA_USER_ROLE = "xsd/userroleconfig/GloriaUserRolesMappingToBaldoCategory.xsd";
        public static final String INIT_GLACCOUNT = "xsd/initdata/glAccount/GlAccount.xsd";
        public static final String PROCURE_REQUEST_OBJECT = "xsd/procureRequest/ProcureRequest.xsd";
        public static final String INIT_PART_AFFILIATION = "xsd/initdata/partAffiliation/PartAffiliation.xsd";
        public static final String INIT_PART_ALIAS_MAPPING = "xsd/initdata/partAliasMapping/PartAliasMapping.xsd";
        public static final String INIT_UNIT_OF_MEASURE = "xsd/initdata/unitOfMeasure/UnitOfMeasure.xsd";
        public static final String INIT_INTERNALORDERSAP = "xsd/initdata/internalOrderSap/InternalOrderSap.xsd";
        public static final String INIT_COMPANYCODE = "xsd/initdata/companyCode/CompanyCode.xsd";
        public static final String INIT_CURRENCY = "xsd/initdata/currency/Currency.xsd";
        public static final String REQUISITION = "xsd/purchaseorder/org_volvo_purchasing/3P/BODs/incomingMessages/ProcessRequisition.xsd";
        public static final String INIT_DELIVERY_FOLLOW_UP_TEAM = "xsd/initdata/deliveryFollowUpTeam/DeliveryFollowUpTeam.xsd";
        public static final String INIT_DELIVERY_NOTE = "xsd/initdata/deliveryNote/DeliveryNote.xsd";
        public static final String INIT_PURCHASE_ORGANISATION = "xsd/initdata/purchaseOrganisation/PurchaseOrganisation.xsd";
        public static final String INIT_DANGEROUS_GOODS = "xsd/initdata/dangerousGoods/DangerousGoods.xsd";
        public static final String INIT_PROCURE = "xsd/initdata/procure/Procure.xsd";
        public static final String PROCURE_TO_REQUEST_OBJECT = "xsd/materialrequest/MaterialProcureRequest.xsd";
        public static final String INIT_SITE = "xsd/initdata/site/Site.xsd";
        public static final String PROCESS_PURCHASE_ORDER = "xsd/processpurchaseorder/1FC_Messages/ProcessPurchaseOrder_1_0.xsd";
        public static final String MATERIAL_PROCESS_RESPONSE = "xsd/materialrequest/MaterialProcureResponse.xsd";
        public static final String INIT_PRINTER = "xsd/initdata/warehouse/Printer.xsd";
        public static final String INIT_QUALITY_DOCUMENT = "xsd/initdata/qualitydocument/QualityDocument.xsd";
        public static final String INIT_CURRENCY_RATE = "xsd/initdata/currencyrate/CurrencyRate.xsd";
        public static final String INIT_TEAM = "xsd/initdata/team/Team.xsd";
        public static final String INIT_UNIT_FOR_CONVERSION = "xsd/initdata/conversionUnit/ConversionUnit.xsd";  
        
        private SchemaFullPath() {
            // should not be initialized
        }
    }

    /**
     * Inner class for PackageName.
     */
    public static final class PackageName {

        public static final String USER_AND_ORGANISATION = "com.volvo.gloria.common.c.baldo";
        public static final String USER_TEAM = "com.volvo.group.init.teamUser._1_0";
        public static final String GOODS_MOVEMENT = "com.volvo.group.finance.goodsmovement._1_0";
        public static final String COST_CENTER = "com.volvo.group.costcenter._1_0";
        public static final String WBS = "com.volvo.group.wbs._1_0";
        public static final String PURCHASE_ORDER = "com.volvo.group.purchaseorder._1_0";
        public static final String PROTOTYPE_PURCHASE_ORDER = "com.volvo.group.prototypepurchaseorder._1_0";
        public static final String INIT_WAREHOUSE = "com.volvo.group.init.warehouse._1_0";
        public static final String INIT_BUILDCHANGE = "com.volvo.group.init.buildchange._1_0";
        public static final String INIT_PROCUREMENT_BUILDCHANGE = "com.volvo.group.init.procurement._1_0";
        public static final String INIT_MATERIAL = "com.volvo.group.init.material._1_0";
        public static final String INIT_DELIVERY = "com.volvo.group.init.delivery._1_0";
        public static final String INIT_USER = "com.volvo.group.init.userdomain._1_0";
        public static final String CONFIG_GLORIA_USER_ROLE = "com.volvo.gloria.baldo.userrole";
        public static final String INIT_GLACCOUNT = "com.volvo.group.init.glAccount._1_0";
        public static final String PROCURE_REQUEST_OBJECT = "com.volvo.group.procurerequest._1_0";
        public static final String INIT_PART_AFFILIATION = "com.volvo.group.init.partAffiliation._1_0";
        public static final String INIT_PART_ALIAS_MAPPING = "com.volvo.group.init.partAliasMapping._1_0";
        public static final String INIT_UNIT_OF_MEASURE = "com.volvo.group.init.unitOfMeasure._1_0";
        public static final String INIT_INTERNALORDERSAP = "com.volvo.group.init.internalOrderSap._1_0";
        public static final String INIT_COMPANYCODE = "com.volvo.group.init.companyCode._1_0";
        public static final String INIT_CURRENCY = "com.volvo.group.init.currency._1_0";
        public static final String REQUISITION = "com.volvo.group.processrequisition._1_0";
        public static final String INIT_DELIVERY_FOLLOW_UP_TEAM = "com.volvo.group.init.deliveryFollowUpTeam._1_0";
        public static final String INIT_DELIVERYNOTE = "com.volvo.group.init.deliveryNote._1_0";
        public static final String INIT_PURCHASE_ORGANISATION = "com.volvo.group.init.purchaseOrganisation._1_0";
        public static final String INIT_DANGEROUS_GOODS = "com.volvo.group.init.dangerousGoods._1_0";
        public static final String INIT_PROCURE = "com.volvo.group.init.procure._1_0";
        public static final String PROCURE_TO_REQUEST_OBJECT = "com.volvo.group.materialProcureRequest._1_0";
        public static final String INIT_SITE = "com.volvo.group.init.site._1_0";
        public static final String PROCESS_PURCHASE_ORDER = "com.volvo.group.processpurchaseorder._1_0";
        public static final String MATERIAL_PROCESS_RESPONSE = "com.volvo.materialprocureresponse._1_0";
        public static final String INIT_PRINTER = "com.volvo.group.init.warehouse._1_0";
        public static final String INIT_QUALITY_DOCUMENT = "com.volvo.group.init.qualitydocument._1_0";
        public static final String INIT_CURRENCY_RATE = "com.volvo.group.init.currencyrate._1_0";
        public static final String INIT_TEAM = "com.volvo.group.init.team._1_0";
        public static final String INIT_UNIT_FOR_CONVERSION = "com.volvo.group.init.conversionUnit._1_0";       
        
        private PackageName() {
            // should not be initialized
        }
    }
}
