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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.b.CostCenterTransformer;
import com.volvo.gloria.common.costcenter.c.dto.SyncCostCenterDTO;
import com.volvo.gloria.util.IOUtil;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

public class CostCenterGatewayBeanTest extends AbstractTransactionalTestCase {
    public CostCenterGatewayBeanTest() {
        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    }
    private JmsTemplate jmsTemplate;

    private ConnectionFactory connectionFactory;

    private CommonServices commonServices;

    @Inject
    @Named("costCenterGatewayQueue")
    Destination destination;

    @Inject
    CostCenterGatewayBean costCenterGatewayBean;

    @Inject
    CostCenterTransformer costCenterTransformer;

    @Inject
    @Named("connectionFactory_NoTx")
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        this.jmsTemplate = new JmsTemplate(this.connectionFactory);
    }

    @Before
    public void setUpInventoryServicesCollaborator() throws Exception {
        commonServices = Mockito.mock(CommonServices.class);
        costCenterGatewayBean.setCommonServices(commonServices);
    }

    @Test
    public void testAddSyncCostCenter() throws InterruptedException, IOException {
        // Arrange
        // Act
        final String message = IOUtil.getStringFromClasspath("SyncCostCenter_1_0_01.xml");
        this.jmsTemplate.send(this.destination, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(message);
            }
        });
        // Sleep for a short while, to allow JMS listener thread to process the message
        Thread.sleep(10000);
        // Assert
        // Verify Cost Services collaborator was called with appropriate arguments
        // as a result of processing the message
        SyncCostCenterDTO syncCostCenterer = costCenterTransformer.transformStoredCostCenter(message);
        Mockito.verify(commonServices).syncCostCenter(Mockito.eq(syncCostCenterer));
    }

}
