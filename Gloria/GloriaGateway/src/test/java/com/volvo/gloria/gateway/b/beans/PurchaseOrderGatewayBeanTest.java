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
import java.util.List;

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

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.purchaseorder.c.dto.PurchaseOrderScheduleDTO;
import com.volvo.gloria.common.purchaseorder.c.dto.StandAloneOrderHeaderDTO;
import com.volvo.gloria.common.purchaseorder.c.dto.StandAloneOrderLineDTO;
import com.volvo.gloria.common.purchaseorder.c.dto.SyncPurchaseOrderTypeDTO;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.b.PurchaseOrderTransformer;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.IOUtil;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

public class PurchaseOrderGatewayBeanTest extends AbstractTransactionalTestCase {
    public PurchaseOrderGatewayBeanTest() {
        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    }
    private JmsTemplate jmsTemplate;

    private ConnectionFactory connectionFactory;

    private ProcurementServices procurementServices;
    private CommonServices commonServices;

    @Inject
    @Named("purchaseOrderGatewayQueue")
    Destination destination;

    @Inject
    PurchaseOrderGatewayBean purchaseOrderGatewayBean;

    @Inject
    PurchaseOrderTransformer purchaseOrderTransformer;

    @Inject
    @Named("connectionFactory_NoTx")
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        this.jmsTemplate = new JmsTemplate(this.connectionFactory);
    }

    @Before
    public void setUpInventoryServicesCollaborator() throws Exception {
        procurementServices = Mockito.mock(ProcurementServices.class);
        purchaseOrderGatewayBean.setProcurementServices(procurementServices);
        
        commonServices = Mockito.mock(CommonServices.class);
        purchaseOrderGatewayBean.setCommonServices(commonServices);
    }

    @Test
    public void testPurchaseOrderJMSMessage() throws InterruptedException, IOException, GloriaApplicationException {
        // Arrange
        // Act
        final String message = IOUtil.getStringFromClasspath("SyncStandAloneOrder_Prototype 1.00.xml");
        this.jmsTemplate.send(this.destination, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(message);
            }
        });
        // Sleep for a short while, to allow JMS listener thread to process the message
        Thread.sleep(10000);
        // Assert
        // Verify Purchase Services collaborator was called with appropriate arguments
        // as a result of processing the message
        SyncPurchaseOrderTypeDTO syncPurchaseOrderType = purchaseOrderTransformer.transformStoredPurchaseOrder(message);

        List<StandAloneOrderHeaderDTO> standAloneOrderHeaderDTOs = syncPurchaseOrderType.getStandAloneOrderHeaderDTO();
        Assert.assertNotNull(standAloneOrderHeaderDTOs);

        StandAloneOrderHeaderDTO standAloneOrderHeaderDTO = standAloneOrderHeaderDTOs.get(0);
        Assert.assertEquals("2049-900019-S123456", standAloneOrderHeaderDTO.getOrderNo());
        Assert.assertEquals("Prototype", standAloneOrderHeaderDTO.getOrderMode());
        Assert.assertEquals("S489692", standAloneOrderHeaderDTO.getOrderIdGps());
        Assert.assertEquals("1001", standAloneOrderHeaderDTO.getMaterialUserId());
        Assert.assertEquals("VTC Tuve", standAloneOrderHeaderDTO.getMaterialUserName());
        Assert.assertEquals("79", standAloneOrderHeaderDTO.getSupplierId());
        Assert.assertEquals("Organization", standAloneOrderHeaderDTO.getMaterialUserCategory());

        List<StandAloneOrderLineDTO> aloneOrderLineDTOs = standAloneOrderHeaderDTO.getStandAloneOrderLineDTO();

        StandAloneOrderLineDTO aloneOrderLineDTO = aloneOrderLineDTOs.get(0);
        Assert.assertNotNull(aloneOrderLineDTO);
        Assert.assertEquals("1590744", aloneOrderLineDTO.getPartNumber());
        Assert.assertEquals("V", aloneOrderLineDTO.getPartQualifier());
        Assert.assertEquals(new Long(1), aloneOrderLineDTO.getPerQuantity());
        Assert.assertEquals("PCE", aloneOrderLineDTO.getUnitOfMeasure());

        List<PurchaseOrderScheduleDTO> orderScheduleDTOs = aloneOrderLineDTO.getPurchaseOrderSchedule();
        PurchaseOrderScheduleDTO aloneOrderScheduleDTO = orderScheduleDTOs.get(0);
        Assert.assertNotNull(aloneOrderScheduleDTO);
        Assert.assertEquals(new Long(10), aloneOrderScheduleDTO.getQuantity());
        Assert.assertEquals("2011-07-05", aloneOrderScheduleDTO.getShipToArrive());

        Mockito.verify(procurementServices).createOrUpdatePurchaseOrder(Mockito.eq(syncPurchaseOrderType));
    }

}
