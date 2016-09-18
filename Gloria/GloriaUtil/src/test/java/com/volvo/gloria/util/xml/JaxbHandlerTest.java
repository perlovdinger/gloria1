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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.volvo.gloria.common.c.baldo.GMFVerbType;
import com.volvo.gloria.common.c.baldo.OrganisationInfoType;
import com.volvo.gloria.common.c.baldo.UserAndOrganisation;
import com.volvo.gloria.common.c.baldo.UserOrganisationType;
import com.volvo.gloria.common.c.baldo.UserOrganisationType.OrganisationApplication;
import com.volvo.gloria.common.c.baldo.UserOrganisationType.OrganisationApplication.ApplicationSetting;
import com.volvo.gloria.common.c.baldo.UserOrganisationType.OrganisationApplication.UserCategory;
import com.volvo.gloria.util.IOUtil;
import com.volvo.group.costcenter._1_0.CostCenterType;
import com.volvo.group.costcenter._1_0.CostCenterType.CostCenterItem;
import com.volvo.group.costcenter._1_0.SyncCostCenter;
import com.volvo.group.finance.goodsmovement._1_0.GoodsMovementType;
import com.volvo.group.finance.goodsmovement._1_0.ProcessGoodsMovement;
import com.volvo.group.purchaseorder._1_0.PurchaseOrderType;
import com.volvo.group.purchaseorder._1_0.SyncPurchaseOrderDataAreaType;
import com.volvo.group.purchaseorder._1_0.SyncPurchaseOrderType;
import com.volvo.group.purchaseorder.components._1_0.ApplicationAreaType;
import com.volvo.group.purchaseorder.components._1_0.DescriptionType;
import com.volvo.group.purchaseorder.components._1_0.ItemType;
import com.volvo.group.purchaseorder.components._1_0.PurchaseOrderLineType;
import com.volvo.group.wbs._1_0.SyncWBSElementShort;
import com.volvo.group.wbs._1_0.WBSLoadType;

public class JaxbHandlerTest {
    private JaxbHandlerResult result;
    private JaxbHandler jaxbHandler;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testUnmarshalUserAndOrganisation() throws JAXBException, IOException {
        // Arrange
        jaxbHandler = new JaxbHandler(XmlConstants.SchemaFullPath.USER_AND_ORGANISATION, XmlConstants.PackageName.USER_AND_ORGANISATION);
        String userAndOrganisationXml = IOUtil.getStringFromClasspath("userOrganisation/UserOrganisation.xml");
        // Act
        result = jaxbHandler.unmarshalAndValidateXml(userAndOrganisationXml);
        Object obj = result.getJaxbObject();
        // Assert
        assertNotNull(obj);
        assertEquals(obj.getClass().getName(), "com.volvo.gloria.common.c.baldo.UserAndOrganisation");

        UserAndOrganisation userAndOrganisation = (UserAndOrganisation) obj;
        List<Object> gmfVerbAndUserOrganisationAndUserRoleDefs = userAndOrganisation.getGMFVerbAndUserOrganisationAndUserRoleDef();
        assertEquals(gmfVerbAndUserOrganisationAndUserRoleDefs.size(), 2);

        Object obj1 = gmfVerbAndUserOrganisationAndUserRoleDefs.get(0);
        assertNotNull(obj1);
        assertEquals(obj1.getClass().getName(), "com.volvo.gloria.common.c.baldo.UserOrganisationType");

        UserOrganisationType userOrganisationType = (UserOrganisationType) obj1;
        assertEquals(userOrganisationType.getOrganisationID(), "BA0000");

        Object obj2 = gmfVerbAndUserOrganisationAndUserRoleDefs.get(1);
        assertNotNull(obj2);
        assertEquals(obj2.getClass().getName(), "com.volvo.gloria.common.c.baldo.GMFVerbType");

        GMFVerbType gmfVerbType = (GMFVerbType) obj2;
        assertEquals(gmfVerbType.getLangCode(), "en");
    }

    @Test
    public void testUnmarshalOrganisationInfo() throws JAXBException, IOException {
        // Arrange
        jaxbHandler = new JaxbHandler(XmlConstants.SchemaFullPath.USER_AND_ORGANISATION, XmlConstants.PackageName.USER_AND_ORGANISATION);
        String organisationInfoXml = IOUtil.getStringFromClasspath("userOrganisation/OrganisationInfo.xml");
        // Act
        result = jaxbHandler.unmarshalAndValidateXml(organisationInfoXml);
        Object obj = result.getJaxbObject();
        // Assert
        assertNotNull(obj);
        assertEquals(obj.getClass().getName(), "com.volvo.gloria.common.c.baldo.UserAndOrganisation");

        UserAndOrganisation userAndOrganisation = (UserAndOrganisation) obj;
        List<Object> gmfVerbAndUserOrganisationAndUserRoleDefs = userAndOrganisation.getGMFVerbAndUserOrganisationAndUserRoleDef();
        assertEquals(gmfVerbAndUserOrganisationAndUserRoleDefs.size(), 2);

        Object obj1 = gmfVerbAndUserOrganisationAndUserRoleDefs.get(0);
        assertNotNull(obj1);
        assertEquals(obj1.getClass().getName(), "com.volvo.gloria.common.c.baldo.OrganisationInfoType");

        OrganisationInfoType userOrganisationType = (OrganisationInfoType) obj1;
        assertEquals(userOrganisationType.getOrganisationID(), "VOLVOIT");

        Object obj2 = gmfVerbAndUserOrganisationAndUserRoleDefs.get(1);
        assertNotNull(obj2);
        assertEquals(obj2.getClass().getName(), "com.volvo.gloria.common.c.baldo.GMFVerbType");

        GMFVerbType gmfVerbType = (GMFVerbType) obj2;
        assertEquals(gmfVerbType.getCountryCode(), "US");
    }

    @Test
    public void testUnmarshalUserOrganizationDetails() throws JAXBException, IOException {
        // Arrange
        jaxbHandler = new JaxbHandler(XmlConstants.SchemaFullPath.USER_AND_ORGANISATION, XmlConstants.PackageName.USER_AND_ORGANISATION);
        String userOrganisationDetails = IOUtil.getStringFromClasspath("userOrganisation/UserOrganisationDetails.xml");

        // Act
        result = jaxbHandler.unmarshalAndValidateXml(userOrganisationDetails);
        Object obj = result.getJaxbObject();

        // Assert
        assertNotNull(obj);
        assertEquals(obj.getClass().getName(), "com.volvo.gloria.common.c.baldo.UserAndOrganisation");

        UserAndOrganisation userAndOrganisation = (UserAndOrganisation) obj;
        List<Object> gmfVerbAndUserOrganisationAndUserRoleDefs = userAndOrganisation.getGMFVerbAndUserOrganisationAndUserRoleDef();

        assertEquals(gmfVerbAndUserOrganisationAndUserRoleDefs.size(), 11);

        // To get ApplicationSettings
        Object obj2 = gmfVerbAndUserOrganisationAndUserRoleDefs.get(4);
        assertNotNull(obj2);
        assertEquals(obj2.getClass().getName(), "com.volvo.gloria.common.c.baldo.UserOrganisationType");

        UserOrganisationType userOrganisationType = (UserOrganisationType) obj2;
        assertEquals(userOrganisationType.getOrganisationApplication().size(), 1);

        OrganisationApplication organisationApplication1 = userOrganisationType.getOrganisationApplication().get(0);

        assertEquals(organisationApplication1.getApplicationSetting().size(), 4);
        ApplicationSetting applicationSetting = organisationApplication1.getApplicationSetting().get(0);
        assertEquals(applicationSetting.getApplicationSettingID(), "WHSITE");
        assertEquals(applicationSetting.getApplicationSettingValue(), "1722");

        ApplicationSetting applicationSetting1 = organisationApplication1.getApplicationSetting().get(1);
        assertEquals(applicationSetting1.getApplicationSettingID(), "LIMITED");
        assertEquals(applicationSetting1.getApplicationSettingValue(), "true");

        ApplicationSetting applicationSetting2 = organisationApplication1.getApplicationSetting().get(2);
        assertEquals(applicationSetting2.getApplicationSettingID(), "PROCURETEAM");
        assertEquals(applicationSetting2.getApplicationSettingValue(), "PROCURE-CUR");

        List<UserCategory> userCategories = organisationApplication1.getUserCategory();

        UserCategory userCategory = userCategories.get(0);
        assertEquals(userCategory.getUserCategoryID(), "Material_Controller");
        assertEquals(userCategory.getUserCategoryDescription(), "Material Controller");

        UserCategory userCategory1 = userCategories.get(1);
        assertEquals(userCategory1.getUserCategoryID(), "Delivery_Controller");
        assertEquals(userCategory1.getUserCategoryDescription(), "Delivery Controller");

    }

    @Test
    public void testUnmarshalGoodsMovement() throws JAXBException, IOException {
        // Arrange
        jaxbHandler = new JaxbHandler(XmlConstants.SchemaFullPath.GOODS_MOVEMENT, XmlConstants.PackageName.GOODS_MOVEMENT);
        String goodsMovementXml = IOUtil.getStringFromClasspath("goodsMovement/ProcessGoodsMovement_1_0.xml");
        // Act
        result = jaxbHandler.unmarshalAndValidateXml(goodsMovementXml);
        Object obj = result.getJaxbObject();
        // Assert
        assertNotNull(obj);
        assertEquals(obj.getClass().getName(), "com.volvo.group.finance.goodsmovement._1_0.ProcessGoodsMovement");

        ProcessGoodsMovement processGoodsMovement = (ProcessGoodsMovement) obj;
        GoodsMovementType goodsMovementType = processGoodsMovement.getGoodsMovement();
        List<GoodsMovementType.GoodsMovementItems> goodsMovementItems = goodsMovementType.getGoodsMovementItems();
        assertEquals(1, goodsMovementItems.size());

        GoodsMovementType.GoodsMovementItems goodsMovementItem = goodsMovementItems.get(0);
        assertEquals("PCE", goodsMovementItem.getISOUnitOfMeasure());
        assertEquals("B", goodsMovementItem.getMovementIndicator());
        assertEquals("101", goodsMovementItem.getMovementType());
        assertEquals("1234567", goodsMovementItem.getOrderReference());
        assertEquals("N003", goodsMovementItem.getPlant());
        assertEquals(new BigDecimal("11.000"), goodsMovementItem.getQuantity());
        assertEquals(new BigDecimal("11.000"), goodsMovementItem.getShippedQuantity());
    }

    @Test
    public void testUnmarshalOrganisationInfoNotValid() throws JAXBException, IOException {
        // Arrange
        jaxbHandler = new JaxbHandler(XmlConstants.SchemaFullPath.USER_AND_ORGANISATION, XmlConstants.PackageName.USER_AND_ORGANISATION);
        String organisationInfoNotValidXml = IOUtil.getStringFromClasspath("userOrganisation/OrganisationInfoNotValid.xml");
        // Act
        result = jaxbHandler.unmarshalAndValidateXml(organisationInfoNotValidXml);
        // Assert
        assertEquals(false, result.validatedOk());
        Object obj = result.getJaxbObject();
        assertEquals(null, obj);
    }

    @Test
    public void testUnmarshalCostCenter() throws JAXBException, IOException {
        // Arrange
        jaxbHandler = new JaxbHandler(XmlConstants.SchemaFullPath.COST_CENTER, XmlConstants.PackageName.COST_CENTER);
        String costCenterXml = IOUtil.getStringFromClasspath("costCenter/SyncCostCenter_1_0_01.xml");
        // Act
        result = jaxbHandler.unmarshalAndValidateXml(costCenterXml);
        Object obj = result.getJaxbObject();
        // Assert
        assertNotNull(obj);
        assertEquals(obj.getClass().getName(), "com.volvo.group.costcenter._1_0.SyncCostCenter");
        SyncCostCenter syncCostCenter = (SyncCostCenter) obj;
        List<CostCenterType> costCenterTypes = syncCostCenter.getCostCenter();
        assertEquals(141, costCenterTypes.size());
        CostCenterType costCenterType = costCenterTypes.get(0);
        CostCenterItem costCenterItem = costCenterType.getCostCenterItem();
        assertEquals("1230000701", costCenterItem.getCostCenter());
        assertEquals("GB03", costCenterItem.getCompanyCode());
    }

    @Test
    public void testUnmarshalWbsElement() throws JAXBException, IOException {
        // Arrange
        jaxbHandler = new JaxbHandler(XmlConstants.SchemaFullPath.WBS, XmlConstants.PackageName.WBS);
        String wbsXml = IOUtil.getStringFromClasspath("wbs/WbsElement.xml");
        // Act
        result = jaxbHandler.unmarshalAndValidateXml(wbsXml);
        Object obj = result.getJaxbObject();
        // Assert
        assertNotNull(obj);
        assertEquals("com.volvo.group.wbs._1_0.SyncWBSElementShort", obj.getClass().getName());
        SyncWBSElementShort syncWBSElement = (SyncWBSElementShort) obj;
        List<WBSLoadType> wbsLoadTypes = syncWBSElement.getWBSRec();
        assertNotNull(wbsLoadTypes);
    }

    @Ignore
    public void testUnmarshalPurchaseOrder() throws JAXBException, IOException {
        // Arrange
        jaxbHandler = new JaxbHandler(XmlConstants.SchemaFullPath.PURCHASE_ORDER, XmlConstants.PackageName.PURCHASE_ORDER);
        String purchaseOrderXml = IOUtil.getStringFromClasspath("purchaseOrder/SyncPurchaseOrder_1.00.xml");
        // Act
        result = jaxbHandler.unmarshalAndValidateXml(purchaseOrderXml);
        Object obj = result.getJaxbObject();
        // Assert
        assertNotNull(obj);
        assertEquals("com.volvo.group.purchaseorder._1_0.SyncPurchaseOrderType", obj.getClass().getName());
        SyncPurchaseOrderType syncPurchaseOrderType = (SyncPurchaseOrderType) obj;
        assertEquals("Production", syncPurchaseOrderType.getSystemEnvironmentCode());
        ApplicationAreaType applicationAreaType = syncPurchaseOrderType.getApplicationArea();
        assertEquals("2011-05-23T01:37:57.474648", applicationAreaType.getCreationDateTime());
        SyncPurchaseOrderDataAreaType dataArea = syncPurchaseOrderType.getDataArea();
        List<PurchaseOrderType> purchaseOrder = dataArea.getPurchaseOrder();
        PurchaseOrderType purchaseOrderType = purchaseOrder.get(0);
        List<JAXBElement<? extends PurchaseOrderLineType>> purchaseOrderLine = purchaseOrderType.getPurchaseOrderLine();
        JAXBElement<? extends PurchaseOrderLineType> jaxbElement = purchaseOrderLine.get(0);
        PurchaseOrderLineType value = jaxbElement.getValue();
        ItemType item = value.getItem();
        List<DescriptionType> descriptions = item.getDescription();
        String description = descriptions.get(0).getValue();
        assertEquals("Front Spring Anchorage", description);
    }

    @Test(expected = JAXBException.class)
    public void testMandatoryPackageName() throws JAXBException {
        // Arrange
        // Act
        jaxbHandler = new JaxbHandler(XmlConstants.SchemaFullPath.USER_AND_ORGANISATION, "");
        // Assert
    }

    @Test
    public void testMandatoryXmlFileName() throws JAXBException {
        // Arrange
        jaxbHandler = new JaxbHandler(XmlConstants.SchemaFullPath.USER_AND_ORGANISATION, XmlConstants.PackageName.USER_AND_ORGANISATION);
        // Act
        result = jaxbHandler.unmarshalAndValidateXml("");
        // Assert
        assertEquals(false, result.validatedOk());
        Object obj = result.getJaxbObject();
        assertEquals(null, obj);
    }
    
    
    @Test
    public void testUnmarshalRequisition() throws JAXBException, IOException {
        // Arrange
        jaxbHandler = new JaxbHandler(XmlConstants.SchemaFullPath.REQUISITION, XmlConstants.PackageName.REQUISITION);
        String requisitionXml = IOUtil.getStringFromClasspath("processRequisition/ProcessRequisition.xml");
        // Act
        result = jaxbHandler.unmarshalAndValidateXml(requisitionXml);
        Object obj = result.getJaxbObject();
        // Assert
        assertNotNull(obj);
    }
}