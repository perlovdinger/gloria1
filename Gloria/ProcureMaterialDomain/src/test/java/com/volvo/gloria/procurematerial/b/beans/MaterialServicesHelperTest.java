package com.volvo.gloria.procurematerial.b.beans;

import java.util.ArrayList;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;

import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.warehouse.b.WarehouseServices;

public class MaterialServicesHelperTest {
    @Inject
    private WarehouseServices warehouseServices;
   
    @Test
    public void testMaterialTypeValidForScrap() {

        Assert.assertEquals(true,MaterialType.USAGE.isScrappable());
        Assert.assertEquals(true,MaterialType.MODIFIED.isScrappable());
        Assert.assertEquals(true,MaterialType.ADDITIONAL.isScrappable());
        Assert.assertEquals(true,MaterialType.RELEASED.isScrappable());
        Assert.assertEquals(false,MaterialType.USAGE_REPLACED.isScrappable());
    }
    
    /*
     * You can only scrap material if it is in a specific status
     */
    @Test
    public void testMaterialLineStatus(){
        MaterialLine materialLine= new MaterialLine();
        Material material = new Material();
        materialLine.setMaterial(material);   
        material.setMaterialType(MaterialType.USAGE);
        try{
            MaterialLineStatus materialLineStatus =  MaterialLineStatus.CREATED;
            materialLineStatus.scrap(null, null, 1, null, null, null, null);
            //should throw exception
            Assert.fail();
        } catch (GloriaApplicationException e) {
        }
        try{
            MaterialLineStatus materialLineStatus =  MaterialLineStatus.DEVIATED;
            materialLineStatus.scrap(null, null, 1, null, null, null, null);
            //should throw exception
            Assert.fail();
        } catch (GloriaApplicationException e) {
        }
        try{
            MaterialLineStatus materialLineStatus =  MaterialLineStatus.IN_TRANSFER;
            materialLineStatus.scrap(null, null, 1, null, null, null, null);
            //should throw exception
            Assert.fail();
        } catch (GloriaApplicationException e) {
        }
        try{
            MaterialLineStatus materialLineStatus =  MaterialLineStatus.ORDER_PLACED_EXTERNAL;
            materialLineStatus.scrap(null, null, 1, null, null, null, null);
            //should throw exception
            Assert.fail();
        } catch (GloriaApplicationException e) {
        }
        try{
            MaterialLineStatus materialLineStatus =  MaterialLineStatus.ORDER_PLACED_INTERNAL;
            materialLineStatus.scrap(null, null, 1, null, null, null, null);
            //should throw exception
            Assert.fail();
        } catch (GloriaApplicationException e) {
        }
        try{
            MaterialLineStatus materialLineStatus =  MaterialLineStatus.READY_TO_SHIP;
            materialLineStatus.scrap(null, null, 1, null, null, null, null);
            //should throw exception
            Assert.fail();
        } catch (GloriaApplicationException e) {
        }
        try{
            MaterialLineStatus materialLineStatus =  MaterialLineStatus.REMOVED;
            materialLineStatus.scrap(null, null, 1, null, null, null, null);
            //should throw exception
            Assert.fail();
        } catch (GloriaApplicationException e) {
        }
        try{
            MaterialLineStatus materialLineStatus =  MaterialLineStatus.REMOVED_DB;
            materialLineStatus.scrap(null, null, 1, null, null, null, null);
            //should throw exception
            Assert.fail();
        } catch (GloriaApplicationException e) {
        }
        try{
            MaterialLineStatus materialLineStatus =  MaterialLineStatus.REQUESTED;
            materialLineStatus.scrap(null, null, 1, null, null, null, null);
            //should throw exception
            Assert.fail();
        } catch (GloriaApplicationException e) {
        }
        try{
            MaterialLineStatus materialLineStatus =  MaterialLineStatus.REQUISITION_SENT;
            materialLineStatus.scrap(null, null, 1, null, null, null, null);
            //should throw exception
            Assert.fail();
        } catch (GloriaApplicationException e) {
        }
        try{
            MaterialLineStatus materialLineStatus =  MaterialLineStatus.SCRAPPED;
            materialLineStatus.scrap(null, null, 1, null, null, null, null);
            //should throw exception
            Assert.fail();
        } catch (GloriaApplicationException e) {
        }
        try{
            MaterialLineStatus materialLineStatus =  MaterialLineStatus.SHIPPED;
            materialLineStatus.scrap(null, null, 1, null, null, null, null);
            //should throw exception
            Assert.fail();
        } catch (GloriaApplicationException e) {
        }
        try{
            MaterialLineStatus materialLineStatus =  MaterialLineStatus.WAIT_TO_PROCURE;
            materialLineStatus.scrap(null, null, 1, null, null, null, null);
            //should throw exception
            Assert.fail();
        } catch (GloriaApplicationException e) {
        }
    }
    /*
     * simple null check
     */
    @Test
    public void updateScrapMaterialLinesTest() throws GloriaApplicationException{
        MaterialServicesHelper.updateScrapMaterialLines(9,null,null, null, null, warehouseServices, null, null);     
        
    }
    /*
     * simple empty list check check
     */
    @Test
    public void updateScrapMaterialLinesTest1() throws GloriaApplicationException{
        MaterialServicesHelper.updateScrapMaterialLines(9,new ArrayList<MaterialLine>(),null, null, null, warehouseServices, null, null);          
    }
}
