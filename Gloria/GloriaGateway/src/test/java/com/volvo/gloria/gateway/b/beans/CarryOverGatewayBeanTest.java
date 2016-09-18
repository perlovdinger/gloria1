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
package com.volvo.gloria.gateway.b.beans;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.volvo.gloria.common.b.CarryOverTransformer;
import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.carryover.c.dto.CarryOverItemDTO;
import com.volvo.gloria.common.carryover.c.dto.SyncPurchaseOrderCarryOverDTO;
import com.volvo.gloria.util.IOUtil;
import com.volvo.group.purchaseorder._1_0.OrderModeType;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

public class CarryOverGatewayBeanTest extends AbstractTransactionalTestCase {
    public CarryOverGatewayBeanTest() {
        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    } 
    private JmsTemplate jmsTemplate;

    private ConnectionFactory connectionFactory;

    private CommonServices commonServices;

    @Inject
    @Named("carryOverGatewayQueue")
    Destination destination;

    @Inject
    CarryOverGatewayBean carryOverGatewayBean;

    @Inject
    CarryOverTransformer carryOverTransformer;

    @Inject
    @Named("connectionFactory_NoTx")
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        this.jmsTemplate = new JmsTemplate(this.connectionFactory);
    }

    @Before
    public void setUpInventoryServicesCollaborator() throws Exception {
        commonServices = Mockito.mock(CommonServices.class);
        carryOverGatewayBean.setCommonServices(commonServices);
    }

    @Test
    public void testSyncCarryOver() throws InterruptedException, IOException {
        // Arrange
        // Act
        final String message = IOUtil.getStringFromClasspath("SyncPurchaseOrder_1.00.xml");
        this.jmsTemplate.send(this.destination, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(message);
            }
        });
        // Sleep for a short while, to allow JMS listener thread to process the message
        Thread.sleep(10000);
        // Assert
        // Verify syncCarryOver Service collaborator was called with appropriate arguments
        // as a result of processing the message
        SyncPurchaseOrderCarryOverDTO syncPurchaseOrderCarryOverDTO = carryOverTransformer.transformStoredCarryOver(message);

        Assert.assertNotNull(syncPurchaseOrderCarryOverDTO);
        Assert.assertEquals("Add", syncPurchaseOrderCarryOverDTO.getAction());
        Assert.assertEquals(1, syncPurchaseOrderCarryOverDTO.getCarryOverItemDTOs().size());
        CarryOverItemDTO carryOverItemDTO = syncPurchaseOrderCarryOverDTO.getCarryOverItemDTOs().get(0);
        Assert.assertEquals("1001", carryOverItemDTO.getCustomerId());
        Assert.assertEquals("VTC Tuve", carryOverItemDTO.getCustomerName());
        Assert.assertEquals("79", carryOverItemDTO.getSupplierId());
        Assert.assertEquals("SE", carryOverItemDTO.getSupplierCountryCode());
        Assert.assertEquals("1590744", carryOverItemDTO.getPartNumber());
        Assert.assertEquals("P12", carryOverItemDTO.getPartVersion());
        Assert.assertEquals("Volvo 1590744", carryOverItemDTO.getSupplierPartNumber());
        Assert.assertEquals("P12", carryOverItemDTO.getSupplierPartVersion());
        Assert.assertEquals(true, new Double(25).equals(carryOverItemDTO.getAmount()));
        Assert.assertEquals("SEK", carryOverItemDTO.getCurrency());
        Assert.assertEquals("PCE", carryOverItemDTO.getPriceUnit());
        Assert.assertEquals("PCE", carryOverItemDTO.getUnitCode());

        Mockito.verify(commonServices).syncCarryOver(Mockito.eq(syncPurchaseOrderCarryOverDTO));
    }
    
    /**
     * GLO-512 BUG:Carry over messages are not processed due to null pointer exception.
     */
    @Test
    public void testBugSyncCarryOver() throws InterruptedException, IOException {
        // Arrange
        // Act
        final String message = IOUtil.getStringFromClasspath("carryover_glo512.xml");
        this.jmsTemplate.send(this.destination, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(message);
            }
        });
        // Sleep for a short while, to allow JMS listener thread to process the message
        Thread.sleep(10000);
        // Assert
        // Verify syncCarryOver Service collaborator was called with appropriate arguments
        // as a result of processing the message
        SyncPurchaseOrderCarryOverDTO syncPurchaseOrderCarryOverDTO = carryOverTransformer.transformStoredCarryOver(message);

        Assert.assertNotNull(syncPurchaseOrderCarryOverDTO);
        Assert.assertEquals(1, syncPurchaseOrderCarryOverDTO.getCarryOverItemDTOs().size());
        CarryOverItemDTO carryOverItemDTO = syncPurchaseOrderCarryOverDTO.getCarryOverItemDTOs().get(0);
        Assert.assertEquals("1001", carryOverItemDTO.getCustomerId());
        Assert.assertEquals("VTC  /TUVE", carryOverItemDTO.getCustomerName());
        Assert.assertEquals("37916", carryOverItemDTO.getSupplierId());
        Assert.assertEquals("190788", carryOverItemDTO.getPartNumber());
        Assert.assertEquals("P01", carryOverItemDTO.getPartVersion());
        Assert.assertEquals(null, carryOverItemDTO.getSupplierPartNumber());
        Assert.assertEquals(null, carryOverItemDTO.getSupplierPartVersion());

        Mockito.verify(commonServices).syncCarryOver(Mockito.eq(syncPurchaseOrderCarryOverDTO));
    }
    
    @Test
    public void testProcessesCarryOverWithStandAloneOrderMode() throws InterruptedException, IOException {
        // Arrange
        // Act
        final String message = IOUtil.getStringFromClasspath("carryover_glo1922.xml");
        this.jmsTemplate.send(this.destination, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(message);
            }
        });
        // Sleep for a short while, to allow JMS listener thread to process the message
        Thread.sleep(10000);
        // Assert
        // Verify syncCarryOver Service collaborator was called with appropriate arguments
        // as a result of processing the message
        SyncPurchaseOrderCarryOverDTO syncPurchaseOrderCarryOverDTO = carryOverTransformer.transformStoredCarryOver(message);

        Assert.assertNotNull(syncPurchaseOrderCarryOverDTO);
        Assert.assertEquals(1, syncPurchaseOrderCarryOverDTO.getCarryOverItemDTOs().size());
        Assert.assertEquals(OrderModeType.STAND_ALONE.value(), syncPurchaseOrderCarryOverDTO.getCarryOverItemDTOs().get(0).getOrderMode());
        
        Mockito.verify(commonServices).syncCarryOver(Mockito.eq(syncPurchaseOrderCarryOverDTO));
    }
}
