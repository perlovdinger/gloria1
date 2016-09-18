package com.volvo.gloria.procurematerial.d.type.receive;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteSubLine;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeaderVersion;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.Order;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLineLastModified;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.type.directsend.DirectSendType;

public class ReceiveTypeHelperTest {

    /**
     * The purpose of this test is to see that when we have two Material records for a OrderLine then the mail is sent twice
     */
    @Test
    public void testEmailSending(){
        DeliveryNoteLine deliveryNoteLine = Mockito.mock(DeliveryNoteLine.class);
        Mockito.when(deliveryNoteLine.getDamagedQuantity()).thenReturn((long) 2020);
        Mockito.when(deliveryNoteLine.getSubLine(false)).thenReturn(new DeliveryNoteSubLine());
        
        OrderLine orderLine = new OrderLine();
        orderLine.setPartNumber("def");
        orderLine.setProjectId("jhi");
        ProcureLine procureLine = new ProcureLine();
        procureLine.setReferenceGps("");
        orderLine.setProcureLine(procureLine);
        
        OrderLineLastModified orderLineLastModified11 = new OrderLineLastModified();
        orderLineLastModified11.setModifiedTime(new Date());
        orderLine.setOrderLineLastModified(orderLineLastModified11);
        
        orderLine.setReceivedQuantity(22);
        Order order = new Order();
        order.setOrderNo("abc");
        order.setSupplierId("");
        order.setSupplierName("");
        orderLine.setOrder(order);
        
        //CASE 2 if there are valid email addresses and not used then there is no email dispatched
        Material material1=new Material();
        material1.setPartVersion("222");
        MaterialHeader materialHeader1= new MaterialHeader();
        MaterialHeaderVersion accepted1 = new MaterialHeaderVersion();
        accepted1.setContactPersonEmail("rahul.mahindrakar@volvo.com");        
        materialHeader1.setAccepted(accepted1);
        materialHeader1.setMaterialControllerName("abc");
        materialHeader1.setMaterialControllerTeam("def");
        materialHeader1.setMaterialControllerUserId("def");
        materialHeader1.setReferenceId("");
        material1.setMaterialHeader(materialHeader1);
        
        //CASE 2 if there are empty email addresses then there is no mail dispatched
        Material material2 = new Material();
        MaterialHeader materialHeader2= new MaterialHeader();
        MaterialHeaderVersion accepted2 = new MaterialHeaderVersion();  
        accepted2.setContactPersonEmail("");  
        materialHeader2.setAccepted(accepted2);
        materialHeader2.setMaterialControllerName("abc");
        materialHeader2.setMaterialControllerTeam("def");
        materialHeader2.setMaterialControllerUserId("def");
        materialHeader2.setReferenceId("");
        material2.setMaterialHeader(materialHeader2);
        material2.setPartVersion("222");
        
        //CASE 3 if there are duplicate email addresses then no email dispatched
        Material material3 = new Material();
        MaterialHeader materialHeader3= new MaterialHeader();
        MaterialHeaderVersion accepted3 = new MaterialHeaderVersion();  
        accepted3.setContactPersonEmail("rahul.mahindrakar@volvo.com");  
        materialHeader3.setAccepted(accepted3);
        materialHeader3.setMaterialControllerName("abc");
        materialHeader3.setMaterialControllerTeam("def");
        materialHeader3.setMaterialControllerUserId("def");
        materialHeader3.setReferenceId("");
        material3.setMaterialHeader(materialHeader3);
        material3.setPartVersion("222");
        
        //CASE 4 if version is null then this is not a problem and this is ignored
        Material material4 = new Material();
        MaterialHeader materialHeader4= new MaterialHeader();
        MaterialHeaderVersion accepted4 = null;
        //accepted3.setContactPersonEmail("rahul.mahindrakar@gmail.com");  
        materialHeader4.setAccepted(accepted4);
        materialHeader4.setMaterialControllerName("abc");
        materialHeader4.setMaterialControllerTeam("def");
        materialHeader4.setMaterialControllerUserId("def");
        materialHeader4.setReferenceId("");
        material4.setMaterialHeader(materialHeader4);
        material4.setPartVersion("222");
        //CASE 5 if materialheader is null then it still works and is ignored
        Material material5 = new Material();
        material5.setMaterialHeader(null);        
        ArrayList<Material> list = new ArrayList<Material>();
        list.add(material1);
        list.add(material2);
        list.add(material3);
        list.add(material4);
        list.add(material5);
        orderLine.setMaterials(list);
        MaterialLine materialLine = new MaterialLine();
        DirectSendType directSend = DirectSendType.NO;
        materialLine.setDirectSend(directSend);
        materialLine.setMaterialOwner(material1);
        ReceiveTypeHelper.sendEmailForEachMaterial(deliveryNoteLine,  orderLine, 4, materialLine);
        Mockito.verify(deliveryNoteLine, Mockito.times(1)).getDamagedQuantity();        
    }

    
    @Test
    public void testcalculateStockBalanceToEmail(){
        // when stock balance is -1 then stockBalance is 0, should not be the case
        Assert.assertEquals(0,ReceiveTypeHelper.calculateStockBalanceToEmail(-1 , 5, 5));
        // when stock balance is equel to  than received quantity then its still 0
        Assert.assertEquals(0,ReceiveTypeHelper.calculateStockBalanceToEmail(5 , 5, 0));
        Assert.assertEquals(0,ReceiveTypeHelper.calculateStockBalanceToEmail(4 , 5, 1));
        Assert.assertEquals(5,ReceiveTypeHelper.calculateStockBalanceToEmail(9 , 5, 1));
        // all received are damaged nothing was added to stock
        Assert.assertEquals(9,ReceiveTypeHelper.calculateStockBalanceToEmail(9 , 5, 5));
        //  should not happen
        //Assert.assertEquals(0,ReceiveTypeHelper.calculateStockBalanceToEmail(5 , 10, 0));
    }
}
