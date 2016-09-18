package com.volvo.gloria.procurematerial.repositories.b.beans;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.warehouse.b.WarehouseServices;
import com.volvo.gloria.warehouse.c.ZoneType;
import com.volvo.gloria.warehouse.d.entities.Zone;

public class RequestHeaderRepositoryBeanTestSimple {

    @Test
    public void test() {
        MaterialLine null1 = new MaterialLine();
        null1.setBinLocationCode(null);
        MaterialLine first = new MaterialLine();
        first.setBinLocationCode("0101-H01-01");
        MaterialLine second = new MaterialLine();
        second.setBinLocationCode("0101-F01-01");
        List<MaterialLine> e = new ArrayList<MaterialLine>();
        e.add(first);
        e.add(second);
        e.add(null1);
        MaterialHeaderRepositoryBean bean = new MaterialHeaderRepositoryBean();
        MaterialLine request= bean.getMaterialLineByBinLocation(e);
        assertEquals(request.getBinLocationCode(), "0101-F01-01");

        MaterialLine null2 = new MaterialLine();
        null2.setBinLocationCode(null);
        e.add(null2);

        request= bean.getMaterialLineByBinLocation(e);
        assertEquals(request.getBinLocationCode(), "0101-F01-01");
        
        MaterialLine null3 = new MaterialLine();
        null3.setBinLocationCode("0101-C01-01");
        e.add(null3);

        request= bean.getMaterialLineByBinLocation(e);
        assertEquals(request.getBinLocationCode(), "0101-C01-01");
    }
    /*
     * When you try to get a bin location where the binlocationcode is null then it will return a null object
     * and not the Material line as it does not matter
     */
    @Test
    public void test1() {
        MaterialLine null1 = new MaterialLine();
        null1.setBinLocationCode(null);
        List<MaterialLine> e = new ArrayList<MaterialLine>();
        e.add(null1);
        MaterialHeaderRepositoryBean bean = new MaterialHeaderRepositoryBean();
        MaterialLine request= bean.getMaterialLineByBinLocation(e);
        assertNull(request);
    }
    /*
     * suggestNextlocation :  When its a direct send then the shipping zone is returned
     */
    @Test
    public void testSuggestNextLocation(){
        MaterialHeaderRepositoryBean bean = new MaterialHeaderRepositoryBean();
        WarehouseServices warehouseService = Mockito.mock(WarehouseServices.class) ;
        ReflectionTestUtils.setField( bean, "warehouseServices", warehouseService );
        Zone zone= new Zone();
        zone.setCode("abc");
        Mockito.when(warehouseService.findZoneCodes(ZoneType.SHIPPING, "site")).thenReturn(zone); 
        String result = bean.suggestNextlocation("", "", "site", true , "", "");
        assertEquals("abc", result);
    }

}
