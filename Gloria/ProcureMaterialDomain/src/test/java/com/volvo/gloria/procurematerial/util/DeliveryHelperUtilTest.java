package com.volvo.gloria.procurematerial.util;

import java.util.ArrayList;

import org.junit.Test;
import org.springframework.util.Assert;
import com.volvo.gloria.procurematerial.d.entities.OrderLineVersion;
public class DeliveryHelperUtilTest {
    
    @Test
    public void testSortOrderLineVersions() {
        OrderLineVersion version1=new OrderLineVersion();
        version1.setOrderLineVersionOid(5);
        OrderLineVersion version3=new OrderLineVersion();
        version3.setOrderLineVersionOid(4);
        OrderLineVersion version2=new OrderLineVersion();
        version2.setOrderLineVersionOid(2);
        ArrayList<OrderLineVersion> orderLineVersions=new ArrayList<OrderLineVersion>();
        orderLineVersions.add(version1);
        orderLineVersions.add(version2);
        DeliveryHelper.sortOrderLineVersions(orderLineVersions);
        Assert.isTrue(orderLineVersions.get(0).getOrderLineVersionOid()==5);        
    }
}
