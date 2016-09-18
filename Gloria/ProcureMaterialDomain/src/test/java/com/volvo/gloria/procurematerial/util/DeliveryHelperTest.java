package com.volvo.gloria.procurematerial.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import com.volvo.gloria.procurematerial.b.ProcurementDtoTransformer;
import com.volvo.gloria.procurematerial.c.CompleteType;
import com.volvo.gloria.procurematerial.c.dto.OrderLineDTO;
import com.volvo.gloria.procurematerial.d.entities.DeliverySchedule;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.Order;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLineLastModified;
import com.volvo.gloria.procurematerial.d.entities.OrderLineLog;
import com.volvo.gloria.procurematerial.d.entities.OrderLineVersion;
import com.volvo.gloria.procurematerial.d.entities.status.orderLine.OrderLineStatus;
import com.volvo.gloria.procurematerial.d.type.internalexternal.InternalExternal;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

public class DeliveryHelperTest extends AbstractTransactionalTestCase {
    public DeliveryHelperTest() {
        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    }

    
    @Inject
    private ProcurementDtoTransformer procurementDtoTransformer;
    
    /*
     * this test tests whether the field partModifciation 
     * is set to an appropriate value in the DTO
     */
    @Test
    public void testAdditionOfParameters() {
        OrderLine orderLine = new OrderLine();
        Material material1 = new Material();
        Material material2 = new Material();
        material2.setPartModification("abc");
        Material material3 = new Material();
        material3.setPartModification(null);
        Material material4 = new Material();
        material4.setPartModification("");
        List<Material> materialList = new ArrayList<Material>();
        materialList.add(material1);
        materialList.add(material2);
        materialList.add(material3);
        materialList.add(material4);
        
        orderLine.setMaterials(materialList);
        
        OrderLineVersion orderLineVersion = new OrderLineVersion();
        orderLine.setCurrent(orderLineVersion);
        OrderLineLastModified orderLineLastModified = new OrderLineLastModified();
        orderLine.setOrderLineLastModified(orderLineLastModified);
        Order order = new Order();
        orderLine.setOrder(order);        
        orderLine.setStatus(OrderLineStatus.COMPLETED);
        orderLine.setCompleteType(CompleteType.RECEIVED);
        order.setInternalExternal(InternalExternal.INTERNAL);
        
        //
        OrderLineDTO orderLineDTO = procurementDtoTransformer.transformAsDTO(orderLine, false);
        
        //assert
        assertEquals("ABC", orderLineDTO.getMaterialPartModification());
    }
    
    /*
     * this test tests whether the field DeliverySchedule getPlannedDispatchDate
     * is set to an appropriate value in the DTO
     */
    @Test
    public void testAdditionOfParameters1() {
        
        Calendar rightNow = Calendar.getInstance();
        OrderLine orderLine = new OrderLine();
        Material material1 = new Material();
        List<Material> materialList = new ArrayList<Material>();
        materialList.add(material1);
       
        orderLine.setMaterials(materialList);
        
        OrderLineVersion orderLineVersion = new OrderLineVersion();
        orderLine.setCurrent(orderLineVersion);
        OrderLineLastModified orderLineLastModified = new OrderLineLastModified();
        orderLine.setOrderLineLastModified(orderLineLastModified);
        Order order = new Order();
        orderLine.setOrder(order);        
        orderLine.setStatus(OrderLineStatus.COMPLETED);
        orderLine.setCompleteType(CompleteType.RECEIVED);
        order.setInternalExternal(InternalExternal.INTERNAL);
        
        DeliverySchedule deliverySchedule1 =  new DeliverySchedule();
        deliverySchedule1.setPlannedDispatchDate(rightNow.getTime());
        DeliverySchedule deliverySchedule2 =  new DeliverySchedule();
        deliverySchedule2.setPlannedDispatchDate(DateUtils.addHours(rightNow.getTime(), 12));
        List<DeliverySchedule> deliveryScheduleList = new ArrayList<DeliverySchedule>();
        DeliverySchedule deliverySchedule3 =  new DeliverySchedule();
        deliveryScheduleList.add(deliverySchedule1);
        deliveryScheduleList.add(deliverySchedule2);
        deliveryScheduleList.add(deliverySchedule3);
        deliverySchedule3.setPlannedDispatchDate(null);
        orderLine.setDeliverySchedule(deliveryScheduleList);        
        //
        OrderLineDTO orderLineDTO = procurementDtoTransformer.transformAsDTO(orderLine, false);

        //assert
        assertNotNull(orderLineDTO.getPlannedDispatchDate());
        assertTrue(orderLineDTO.getPlannedDispatchDate().after(new Date()));
    }
    
    /*
     * this test tests whether the orderLineLog f ield  eventTime
     * is set to an appropriate value in the DTO
     */
    @Test
    public void testAdditionOfParameters3() {
        

        OrderLine orderLine = setUpOrderLine();        
        //
        OrderLineDTO orderLineDTO = procurementDtoTransformer.transformAsDTO(orderLine, false);

        //assert even if orderLineLog is null the code still works
        assertNull(orderLineDTO.getEventTime());
    }

    private OrderLine setUpOrderLine() {
        OrderLine orderLine = new OrderLine();
        Material material1 = new Material();
        material1.setPartModification("partModification");
        List<Material> materialList = new ArrayList<Material>();
        materialList.add(material1);
       
        orderLine.setMaterials(materialList);
        
        OrderLineVersion orderLineVersion = new OrderLineVersion();
        orderLine.setCurrent(orderLineVersion);
        OrderLineLastModified orderLineLastModified = new OrderLineLastModified();
        orderLine.setOrderLineLastModified(orderLineLastModified);
        Order order = new Order();
        orderLine.setOrder(order);        
        orderLine.setStatus(OrderLineStatus.COMPLETED);
        orderLine.setCompleteType(CompleteType.RECEIVED);
        order.setInternalExternal(InternalExternal.INTERNAL);
        
        orderLine.setOrderLineLog(null);
        return orderLine;
    }
    /*
     * this test tests whether the field orderLineLog eventTime
     * is set to an appropriate value in the DTO
     */
    @Test
    public void testAdditionOfParameters2() {
        
        Calendar rightNow = Calendar.getInstance();
        OrderLine orderLine = new OrderLine();
        Material material1 = new Material();
        List<Material> materialList = new ArrayList<Material>();
        materialList.add(material1);
       
        orderLine.setMaterials(materialList);
        
        OrderLineVersion orderLineVersion = new OrderLineVersion();
        orderLine.setCurrent(orderLineVersion);
        OrderLineLastModified orderLineLastModified = new OrderLineLastModified();
        orderLine.setOrderLineLastModified(orderLineLastModified);
        Order order = new Order();
        orderLine.setOrder(order);        
        orderLine.setStatus(OrderLineStatus.COMPLETED);
        orderLine.setCompleteType(CompleteType.RECEIVED);
        order.setInternalExternal(InternalExternal.INTERNAL);
        
        OrderLineLog orderLineLog1 =  new OrderLineLog();
        orderLineLog1.setEventTime(rightNow.getTime());
        OrderLineLog orderLineLog2 =  new OrderLineLog();
        orderLineLog2.setEventTime(DateUtils.addHours(rightNow.getTime(), 12));
        List<OrderLineLog> orderLineLogList = new ArrayList<OrderLineLog>();
        OrderLineLog orderLineLog3 =  new OrderLineLog();
        orderLineLogList.add(orderLineLog1);
        orderLineLogList.add(orderLineLog2);
        orderLineLogList.add(orderLineLog3);
        //orderLineLogList.setPlannedDispatchDate(null);
        orderLine.setOrderLineLog(orderLineLogList);        
        //
        OrderLineDTO orderLineDTO = procurementDtoTransformer.transformAsDTO(orderLine, false);

        //assert
        assertNotNull(orderLineDTO.getEventTime());
        assertTrue(orderLineDTO.getEventTime().after(new Date()));
    }
    
    @Test
    public void testTransform(){
        OrderLine orderLine = setUpOrderLine();        
        orderLine.setPossibleToReceiveQuantity(10);
        DeliverySchedule deliverySchedule = new DeliverySchedule();
        deliverySchedule.setPlannedDispatchDate(new Date());
        DeliverySchedule deliverySchedule1 = new DeliverySchedule();
        deliverySchedule1.setPlannedDispatchDate(DateUtils.addYears(new Date(), -5 ));
        List<DeliverySchedule> deliveryScheduleList = new ArrayList<DeliverySchedule>();
        deliveryScheduleList.add(deliverySchedule);
        deliveryScheduleList.add(deliverySchedule1);
        OrderLineLog orderLineLog = new OrderLineLog();
        orderLineLog.setEventTime(new Date());
        OrderLineLog orderLineLog1 = new OrderLineLog();
        orderLineLog1.setEventTime(DateUtils.addYears(new Date(), -5 ));
        orderLine.setDeliverySchedule(deliveryScheduleList);
        List<OrderLineLog> orderLineLogList = new ArrayList<OrderLineLog>();
        orderLineLogList.add(orderLineLog);   
        orderLineLogList.add(orderLineLog1);  
        orderLine.setOrderLineLog(orderLineLogList);
        
        OrderLineDTO orderLineDTO = procurementDtoTransformer.transformAsDTO(orderLine, false);
        assertEquals(10, orderLineDTO.getAllowedQuantity());
        assertEquals("partModification".toUpperCase(),orderLineDTO.getMaterialPartModification());
        assertNotNull(orderLineDTO.getPlannedDispatchDate());
        // check to see if planneddispatchdate is the latest time
        assertTrue(DateUtils.isSameDay(new Date(), orderLineDTO.getPlannedDispatchDate()));
        assertNotNull(orderLineDTO.getEventTime());
        // check to see if event time is the latest time
        assertTrue(DateUtils.isSameDay(new Date(), orderLineDTO.getEventTime()));        
    }
}
