package com.volvo.gloria.procurematerial.b.beans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.text.ParseException;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.authorization.b.beans.UserServicesBean;
import com.volvo.gloria.procurematerial.c.dto.RequestListDTO;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.RequestList;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.repositories.b.beans.MaterialHeaderRepositoryBean;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.c.GloriaExceptionConstants;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.warehouse.c.ZoneType;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

public class MaterialServiceBeanValidationTest extends AbstractTransactionalTestCase  {
    public MaterialServiceBeanValidationTest() {
        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    }
    @Inject
    private MaterialHeaderRepository requestHeaderRepository;

    /*
     * Test shows that if requestList is null then exception is thrown
     */
    @Test
    public void testUpdateRequestListForExceptions() throws GloriaApplicationException, ParseException{
        MaterialServicesBean bean = new MaterialServicesBean();
        MaterialHeaderRepository repository = Mockito.mock(MaterialHeaderRepositoryBean.class);           
        ReflectionTestUtils.setField(bean, "requestHeaderRepository", repository);
        
        UserServices userServices = Mockito.mock(UserServicesBean.class);        
        ReflectionTestUtils.setField(bean, "userServices",userServices);
        
        RequestList requestList = null;
        Mockito.when(repository.findRequestListById(Mockito.anyInt())).thenReturn(requestList);

        RequestListDTO requestListDTO = new RequestListDTO();
        // Act
        try{
            bean.updateRequestList(requestListDTO, null, "all");
            fail();
        }catch(GloriaSystemException e){
            // Assert
            Assert.assertEquals(e.getDetail(), GloriaExceptionConstants.INVALID_DATASET_OID);   
        }
    }
    
    /*
     * Test shows that if requestList is not null  then exception is thrown in following case
     * requestListDTO.getVersion() != requestList.getVersion()
     */
    @Test
    public void testUpdateRequestListForExceptions1() throws GloriaApplicationException, ParseException{
        MaterialServicesBean bean = new MaterialServicesBean(); 
        MaterialHeaderRepository repository = Mockito.mock(MaterialHeaderRepositoryBean.class);        
        ReflectionTestUtils.setField(bean, "requestHeaderRepository",repository);

        UserServices userServices = Mockito.mock(UserServicesBean.class);        
        ReflectionTestUtils.setField(bean, "userServices",userServices);
        RequestList requestList = new RequestList();
        Mockito.when(repository.findRequestListById(Mockito.anyInt())).thenReturn(requestList);
        RequestListDTO requestListDTO = new RequestListDTO();
        requestListDTO.setVersion(123);
        requestListDTO.setDeliveryAddressName("");
        requestListDTO.setDeliveryAddressId("");
        String action = "Send";
        // Act
        try{
            bean.updateRequestList(requestListDTO, action, "all");
            fail();
        }catch(GloriaApplicationException e){
            assertEquals(e.getMessage(),"This operation cannot be performed since the information seen in the page has already been updated.");

        }
    }
    /*
     * Test shows that if requestList is not null  then exception is thrown in following case
     * StringUtils.isEmpty(requestListDTO.getDeliveryAddressId()) && StringUtils.isEmpty(requestListDTO.getDeliveryAddressName()
    
    @Test
    public void testUpdateRequestListForExceptions2() throws GloriaApplicationException, ParseException{
        
        //setup Bean
        MaterialServicesBean bean = new MaterialServicesBean(); 
        RequestHeaderRepository repository = Mockito.mock(RequestHeaderRepositoryBean.class);        
        ReflectionTestUtils.setField(bean, "requestHeaderRepository",repository);
        UserServices userServices = Mockito.mock(UserServicesBean.class);        
        ReflectionTestUtils.setField(bean, "userServices",userServices);
        
        //
        RequestList requestList = new RequestList();
        requestList.setVersion(456);
        Mockito.when(repository.findRequestListById(Mockito.anyInt())).thenReturn(requestList);
        
        RequestListDTO requestListDTO = new RequestListDTO();
        // version is same in mocked and  in the dto
        requestListDTO.setVersion(456);
        requestListDTO.setDeliveryAddressName("");
        requestListDTO.setDeliveryAddressId("");
        String action = "Send";
        // Act
        try{
            bean.updateRequestList(requestListDTO, action, "all");
            fail();
        }catch(GloriaApplicationException e){
            assertEquals(e.getMessage(),"New Delivery Address or Transfer To Warehouse is required as there is no Build Location available for this request.");
        }
    } */
    
    @Test
    public void testGLO5099() throws GloriaApplicationException {
        //Arrange
        PageObject pageObject = new PageObject();
        pageObject.setResultsPerPage(100);
      /*  MaterialLine materialLine2 = Mockito.mock(MaterialLine.class);
        Material material2 = Mockito.mock(Material.class);
        Mockito.when(materialLine2.getMaterial()).thenReturn(material2);
        
        MaterialServicesBean bean = new MaterialServicesBean(); 
        RequestHeaderRepository repository = Mockito.mock(RequestHeaderRepositoryBean.class);        
        ReflectionTestUtils.setField(bean, "requestHeaderRepository",repository);
       
        UserServices userServices = Mockito.mock(UserServicesBean.class);        
        ReflectionTestUtils.setField(bean, "userServices",userServices);
        
        Query resultSetQuery = Mockito.mock(Query.class);
        
        Mockito.when(materialLine.getMaterial().getPartAffiliation()).thenReturn("v");
        Mockito.when(materialLine.getMaterial().getPartNumber()).thenReturn("1");
        Mockito.when(materialLine.getMaterial().getPartModification()).thenReturn("mod");
        Mockito.when(materialLine.getMaterial().getPartVersion()).thenReturn("a01");
        
        Mockito.when(materialLine2.getMaterial().getPartAffiliation()).thenReturn("x");
        Mockito.when(materialLine2.getMaterial().getPartNumber()).thenReturn("1");
        Mockito.when(materialLine2.getMaterial().getPartModification()).thenReturn("mod");
        Mockito.when(materialLine2.getMaterial().getPartVersion()).thenReturn("a01");*/
       
        
        MaterialLine materialLine = new MaterialLine();
        materialLine.setQuantity(10L);
        materialLine.setZoneType(ZoneType.TO_STORE);
        materialLine.setStatus(MaterialLineStatus.READY_TO_STORE);
        Material material = new Material();
        material.setPartAffiliation("v");
        material.setPartModification("mod");
        material.setPartNumber("1");
        material.setPartVersion("a01");
        materialLine.setMaterial(material);
        material.addMaterialLine(materialLine);
        requestHeaderRepository.addMaterial(material);
        
        MaterialLine materialLine2 = new MaterialLine();
        materialLine2.setQuantity(20L);
        materialLine2.setStatus(MaterialLineStatus.READY_TO_STORE);
        materialLine2.setZoneType(ZoneType.TO_STORE);
        Material material2 = new Material();
        material2.setPartAffiliation("x");
        material2.setPartModification("mod");
        material2.setPartNumber("1");
        material2.setPartVersion("a01");
        materialLine2.setMaterial(material2);
        material2.addMaterialLine(materialLine2);
        requestHeaderRepository.addMaterial(material2);
        
        //Act
        requestHeaderRepository.getMaterialLinesForWarehouse(pageObject, MaterialLineStatus.READY_TO_STORE.name(), true, null, true, null, null, null,null,null,null,null);
        
        //Assert
        Assert.assertEquals(2,  pageObject.getGridContents().size());
        
    }

}
