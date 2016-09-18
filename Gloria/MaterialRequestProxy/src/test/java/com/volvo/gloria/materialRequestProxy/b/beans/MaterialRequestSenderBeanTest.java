package com.volvo.gloria.materialRequestProxy.b.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.jms.core.JmsTemplate;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.materialRequestProxy.c.FinanceMaterialDTO;
import com.volvo.gloria.materialRequestProxy.c.MaterialRequestLineTransformerDTO;
import com.volvo.gloria.materialRequestProxy.c.MaterialRequestToSendDTO;
import com.volvo.gloria.materialRequestProxy.c.MaterialRequestTransformerDTO;
import com.volvo.gloria.materialRequestProxy.c.MaterialRequestVersionTransformerDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

public class MaterialRequestSenderBeanTest extends AbstractTransactionalTestCase {
    public MaterialRequestSenderBeanTest() {
        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    } 
    private ConnectionFactory connectionFactory;

    private CommonServices commonServices;

    @Inject
    @Named("procureRequestTOGatewayQueue")
    Destination destination;

    @Inject
    MaterialRequestSenderBean materialRequestSenderBean;

    @Inject
    MaterialRequestTransformerBean materialRequestTransformerBean;

    @Inject
    @Named("connectionFactory_NoTx")
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        new JmsTemplate(this.connectionFactory);
    }

    @Before
    public void setUpInventoryServicesCollaborator() throws Exception {
        commonServices = Mockito.mock(CommonServices.class);
        materialRequestSenderBean.setCommonServices(commonServices);
    }
    
    @Test
    @Ignore
    public void testSendMaterialRequestSender() throws JAXBException, GloriaApplicationException {
        //Arrange
        MaterialRequestToSendDTO materialRequestToSendDTO = new MaterialRequestToSendDTO();
        materialRequestToSendDTO.setMtrlRequestVersion("SD1V1");
        materialRequestToSendDTO.setChangeTechId("1111");
        List<MaterialRequestTransformerDTO> materialRequestTransformerDtos = new ArrayList<MaterialRequestTransformerDTO>();
        MaterialRequestTransformerDTO materialRequestTransformerDTO = new MaterialRequestTransformerDTO();
        materialRequestToSendDTO.setRequestType("SINGLE");
        materialRequestTransformerDTO.setContactPersonName("Smitha");
        materialRequestTransformerDTO.setMtrlRequestId("testId");
        materialRequestTransformerDTO.setReferenceGroup("RFGRP");
        materialRequestTransformerDTO.setReferenceId("TestObj1");
        materialRequestTransformerDTO.setRequesterId("a029682");
        materialRequestTransformerDTO.setRequesterName("Mathew");
        
        MaterialRequestVersionTransformerDTO materialRequestVersionDTO  = new MaterialRequestVersionTransformerDTO();
        materialRequestVersionDTO.setApprovalAmount(100);
        materialRequestVersionDTO.setApprovalCurrency("SEK");
        materialRequestVersionDTO.setMailFormId("smi");
        materialRequestVersionDTO.setOutboundLocationId("1622");
        materialRequestVersionDTO.setOutboundStartDate(new Date());
        materialRequestVersionDTO.setReceivedDateTime(new Date());
        materialRequestVersionDTO.setRequiredStaDate(new Date());
        materialRequestTransformerDTO.setMtrlRequestVersionDTO(materialRequestVersionDTO);
        
        List<MaterialRequestLineTransformerDTO> requestLineTransformerDTOs = new ArrayList<MaterialRequestLineTransformerDTO>();
        MaterialRequestLineTransformerDTO materialRequestLineTransformerDTO = new MaterialRequestLineTransformerDTO();
        materialRequestLineTransformerDTO.setFunctionGroup("FunGrp");
        materialRequestLineTransformerDTO.setPartAffiliation("partaff");
        materialRequestLineTransformerDTO.setPartModification("partMod");
        materialRequestLineTransformerDTO.setPartName("ABC");
        materialRequestLineTransformerDTO.setPartNumber("111");
        materialRequestLineTransformerDTO.setPartVersion("v1");
        materialRequestLineTransformerDTO.setProcureLinkId(123);
        materialRequestLineTransformerDTO.setProductClass("testC");
        materialRequestLineTransformerDTO.setQuantity(11);
        materialRequestLineTransformerDTO.setRemoveType(false);
        materialRequestLineTransformerDTO.setRequiredStaDate(new Date());
        materialRequestLineTransformerDTO.setUnitOfMeasure("PCE");
        
        FinanceMaterialDTO financeMaterialDTO = new FinanceMaterialDTO();
        financeMaterialDTO.setCompanyCode("cc");
        financeMaterialDTO.setCostCenter("costCenter");
        financeMaterialDTO.setGlAccount("glAcc");
        financeMaterialDTO.setInternalOrderNoSAP("inSAP");
        financeMaterialDTO.setProjectId("prj1");
        financeMaterialDTO.setWbsCode("wbs");
        materialRequestTransformerDTO.setFinanceMaterialDTO(financeMaterialDTO);
        materialRequestTransformerDTO.setMtrlRequestLineTransformerDTOs(requestLineTransformerDTOs);
        
        materialRequestTransformerDtos.add(materialRequestTransformerDTO);
        materialRequestToSendDTO.setMaterialRequestTransformerDtos(materialRequestTransformerDtos);
        
        //Act
        String xmlMessage = materialRequestSenderBean.sendMaterialRequestSender(materialRequestToSendDTO);
        
        //Assert
        Assert.assertNotNull(xmlMessage);
    }
}
