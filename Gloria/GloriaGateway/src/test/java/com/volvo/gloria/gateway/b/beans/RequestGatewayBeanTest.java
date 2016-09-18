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
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.b.RequestTransformer;
import com.volvo.gloria.procurerequest.c.dto.RequestGatewayDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.IOUtil;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

public class RequestGatewayBeanTest extends AbstractTransactionalTestCase {
    public RequestGatewayBeanTest() {
        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    }
    private JmsTemplate jmsTemplate;
    private ConnectionFactory connectionFactory;
    private ProcurementServices procurementServices;
    private CommonServices commonServices;
    
    
    @Inject
    @Named("requestTOGatewayQueue")
    Destination destination;

    @Inject
    RequestGatewayBean RequestGatewayBean;

    @Inject
    RequestTransformer requestTransformer;
    
    @Inject
    @Named("connectionFactory_NoTx")
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        this.jmsTemplate = new JmsTemplate(this.connectionFactory);
    }

    @Before
    public void setUpInventoryServicesCollaborator() throws Exception {
        procurementServices = Mockito.mock(ProcurementServices.class);
        RequestGatewayBean.setProcurementServices(procurementServices);
        
        commonServices = Mockito.mock(CommonServices.class);
        RequestGatewayBean.setCommonServices(commonServices);
    }
    
    @Ignore
    @Test
    public void testaddRequestTO() throws InterruptedException, IOException, GloriaApplicationException {
        // Arrange
        final String message = IOUtil.getStringFromClasspath("msg1_new.xml");
        this.jmsTemplate.send(this.destination, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(message);
            }
        });
        // Sleep for a short while, to allow JMS listener thread to process the message
        Thread.sleep(10000);
        
        // Act
        List<RequestGatewayDTO> requestGatewayDTOs = requestTransformer.transformRequest(message);
        RequestGatewayDTO requestDTO = requestGatewayDTOs.get(0);
        
        // Assert
        // Verify addProcureRequestTO Service collaborator was called with appropriate arguments
        // as a result of processing the message
        Assert.assertNotNull(requestGatewayDTOs);
        Assert.assertEquals("111111", requestDTO.getRequestHeaderTransformerDtos().get(0).getMtrlRequestId());
        
        Mockito.verify(procurementServices).manageRequest(Mockito.eq(requestGatewayDTOs));
    }
}
