package com.volvo.gloria.procurematerial.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.util.Assert;

import com.volvo.gloria.procurematerial.c.ChangeType;
import com.volvo.gloria.procurematerial.c.dto.MaterialLineDTO;
import com.volvo.gloria.procurematerial.c.dto.RequestGroupDTO;
import com.volvo.gloria.procurematerial.d.entities.ChangeId;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.FinanceHeader;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeaderVersion;
import com.volvo.gloria.procurematerial.d.entities.MaterialLastModified;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.Order;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.RequestGroup;
import com.volvo.gloria.procurematerial.d.entities.RequestList;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;

public class MaterialTransformHelperTest {

    private MaterialLine prepareMaterialLine() {
        // Set up object structure
        MaterialLine materialLine = Mockito.mock(MaterialLine.class);
        MaterialLastModified materialLastModified = Mockito.mock(MaterialLastModified.class);
        Material material = Mockito.mock(Material.class);
        Mockito.when(materialLine.getMaterial()).thenReturn(material);
        MaterialHeader materialHeader  = Mockito.mock(MaterialHeader.class);
        Mockito.when(material.getMaterialHeader()).thenReturn(materialHeader);
        MaterialHeaderVersion materialHeaderVersion = Mockito.mock(MaterialHeaderVersion.class);
        Mockito.when(materialHeader.getAccepted()).thenReturn(materialHeaderVersion);
        FinanceHeader financeHeader = Mockito.mock(FinanceHeader.class);
        Mockito.when(material.getFinanceHeader()).thenReturn(financeHeader);
        ChangeId changeId = Mockito.mock(ChangeId.class);
        Mockito.when(materialHeaderVersion.getChangeId()).thenReturn(changeId);
        Mockito.when(material.getMaterialLastModified()).thenReturn(materialLastModified);
        Mockito.when(materialLastModified.isAlertPartVersion()).thenReturn(true);
        // Mock basic values
        Mockito.when(materialLine.getStatus()).thenReturn(MaterialLineStatus.CREATED);
        Mockito.when(changeId.getType()).thenReturn(ChangeType.SINGLE);
        Mockito.when(material.getMaterialType()).thenReturn(MaterialType.USAGE);
        
        return materialLine;
    }

    @Test
    public void nullValuesHandledInMaterialLineTransformer() {
        // Arrange
        MaterialLine materialLine = prepareMaterialLine();
        
        
        // Act
        materialLine.setStatus(null); // Must handle null status
        MaterialLineDTO materialLineDTO = MaterialTransformHelper.transformAsMaterialLineDTO(materialLine, null);
        
        // Assert
        Assert.isNull(materialLineDTO.getPriority());
    }

    
    /**
     * should calculate items even if there is no connected orderLines for returned or transffered material.getDeliveryNoteLine()
     */
    @Test
    public void testGLO5169CalculateItems() {
        // Arrange
        RequestGroup requestGroup = Mockito.mock(RequestGroup.class);
        List<MaterialLine> materialLines = new ArrayList<MaterialLine>();
        MaterialLine materialLine = prepareMaterialLine();
        materialLines.add(materialLine);
        Mockito.when(requestGroup.getMaterialLines()).thenReturn(materialLines);
        
        
        RequestList requestList = Mockito.mock(RequestList.class);
        Mockito.when(requestGroup.getRequestList()).thenReturn(requestList);
        DeliveryNoteLine deliveryNoteLine = Mockito.mock(DeliveryNoteLine.class);
        Mockito.when(materialLine.getDeliveryNoteLine()).thenReturn(deliveryNoteLine);
        
        // Act
        RequestGroupDTO requestGroupDTO = MaterialTransformHelper.transformAsRequestGroupDTO(requestGroup);
        
        // Assert
        Assert.isTrue(requestGroupDTO.getItems().equals("1"));
    }
    /*
     * Test for addition of fields to MaterialLineDTO as per GLO-5107
     * https://confluence.it.volvo.net:9443/display/GLO/MaterialLineDTO
     */
    
    @Test
    public void testTransformMaterialLineDTO(){
        
        //Arrange
        MaterialLine materialLine = new MaterialLine();
        materialLine.setStorageRoomName("getStorageRoomName");
        Material material = new Material();
        materialLine.setMaterial(material);
        materialLine.setMaterialOwner(material);
        materialLine.setExpirationDate(new Date());
        materialLine.setStatus(MaterialLineStatus.ORDER_PLACED_INTERNAL);        
        ProcureLine procureLine = new ProcureLine();
        procureLine.setMaterialControllerTeam("procureLineMaterialControllerTeam");
        procureLine.setRequisitionId("1");
        procureLine.setProcureDate(new Date());
        procureLine.setReferenceGps("procureLineReferenceGps");
        procureLine.setRequiredStaDate(new Date());
        procureLine.setMaterialControllerId("setMaterialControllerId");
        procureLine.setMaterialControllerName("setmaterialControllerName");
      
        material.setProcureLine(procureLine);
        material.setPartModification("partModification");
        material.setPartAffiliation("partAffiliation");
        material.setMailFormId("mailFormId");
        material.setFunctionGroup("functionGroup");
        material.setDesignResponsible("designResponsible");
        material.setModularHarness("modularHarness");
        material.setMaterialType(MaterialType.USAGE);
        RequestGroup requestGroup = new RequestGroup();
        materialLine.setRequestGroup(requestGroup);
        RequestList requestList = new RequestList();
        requestGroup.setRequestList(requestList);
        requestList.setDeliveryAddressId("getRequestListDeliveryAddressId");
        
        MaterialHeader materialHeader= new MaterialHeader();
        material.setMaterialHeader(materialHeader);
        MaterialHeaderVersion materialHeaderVersion = new MaterialHeaderVersion();
        materialHeaderVersion.setRequesterUserId("getMaterialHeaderVersionRequesterUserId");
        materialHeaderVersion.setRequesterName("getMaterialHeaderVersionRequesterName");
        materialHeaderVersion.setContactPersonId("getMaterialHeaderVersionContactPersonId");
        materialHeaderVersion.setContactPersonName("getMaterialHeaderVersionContactPersonName");
        materialHeader.setAccepted(materialHeaderVersion);
        OrderLine orderLine = new OrderLine();
        material.setOrderLine(orderLine);
        Order order = new Order();
        order.setSuffix("orderSuffix");
        orderLine.setOrder(order);
        FinanceHeader financeHeader = new FinanceHeader();
        financeHeader.setWbsCode("wbsCode");
        financeHeader.setCostCenter("financeHeaderCostCenter");
        financeHeader.setGlAccount("glAccount");
        material.setFinanceHeader(financeHeader);
        financeHeader.setCompanyCode("companyCode");
        
        //Act
        MaterialLineDTO materialLineDTO=MaterialTransformHelper.transformAsMaterialLineDTO(materialLine,null);
        
        //Assert
        org.junit.Assert.assertEquals(StringUtils.upperCase("partModification"),materialLineDTO.getMaterialPartModification());
        org.junit.Assert.assertEquals(StringUtils.upperCase("partAffiliation"),materialLineDTO.getMaterialPartAffiliation());
        org.junit.Assert.assertEquals("mailFormId",materialLineDTO.getMaterialMailFormId());
        org.junit.Assert.assertEquals("functionGroup",materialLineDTO.getMaterialFunctionGroup());
        org.junit.Assert.assertEquals("designResponsible",materialLineDTO.getMaterialDesignResponsible());
        org.junit.Assert.assertEquals("modularHarness",materialLineDTO.getMaterialModularHarness());
        org.junit.Assert.assertEquals("mailFormId", materialLineDTO.getMaterialMailFormId());
        org.junit.Assert.assertEquals("orderSuffix",materialLineDTO.getOrderSuffix());
        org.junit.Assert.assertEquals("procureLineMaterialControllerTeam",materialLineDTO.getProcureLineMaterialControllerTeam());
        org.junit.Assert.assertEquals("companyCode",materialLineDTO.getFinanceHeaderCompanyCode());
        org.junit.Assert.assertEquals("glAccount",materialLineDTO.getFinanceHeaderGlAccount());
        org.junit.Assert.assertEquals("wbsCode",materialLineDTO.getFinanceHeaderWbsCode());
        org.junit.Assert.assertEquals("financeHeaderCostCenter",materialLineDTO.getFinanceHeaderCostCenter());
        Assert.notNull(materialLineDTO.getMaterialLineExpirationDate());
        org.junit.Assert.assertEquals("1",materialLineDTO.getProcureLineRequisitionId());
        Assert.notNull(materialLineDTO.getProcureLineProcureDate());
        org.junit.Assert.assertEquals("procureLineReferenceGps",materialLineDTO.getProcureLineReferenceGps());
        Assert.notNull(materialLineDTO.getProcureLineRequiredStaDate());
        org.junit.Assert.assertEquals("setMaterialControllerId",materialLineDTO.getProcureLineMaterialControllerId());
        org.junit.Assert.assertEquals("setmaterialControllerName",materialLineDTO.getProcureLineMaterialControllerName());
        org.junit.Assert.assertEquals("getMaterialHeaderVersionRequesterUserId",materialLineDTO.getMaterialHeaderVersionRequesterUserId());
        org.junit.Assert.assertEquals("getMaterialHeaderVersionRequesterName",materialLineDTO.getMaterialHeaderVersionRequesterName());
        org.junit.Assert.assertEquals("getMaterialHeaderVersionContactPersonId",materialLineDTO.getMaterialHeaderVersionContactPersonId());
        org.junit.Assert.assertEquals("getMaterialHeaderVersionContactPersonName",materialLineDTO.getMaterialHeaderVersionContactPersonName());
        
        org.junit.Assert.assertEquals("getStorageRoomName",materialLineDTO.getStorageRoomName());
        org.junit.Assert.assertEquals("getRequestListDeliveryAddressId",materialLineDTO.getRequestListDeliveryAddressId());

    }
}
