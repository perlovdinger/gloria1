package com.volvo.gloria.materialrequest.d.status.materialrequest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.materialRequestProxy.b.MaterialRequestSender;
import com.volvo.gloria.materialrequest.d.entities.FinanceMaterial;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequest;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequestLine;
import com.volvo.gloria.materialrequest.d.entities.MaterialRequestVersion;
import com.volvo.gloria.materialrequest.d.type.materialrequest.MaterialRequestType;
import com.volvo.gloria.materialrequest.repositories.b.MaterialRequestRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

public class MaterialRequestStatusTest extends AbstractTransactionalTestCase {
    public MaterialRequestStatusTest() {
        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    }
    private MaterialRequest materialRequest;
    private MaterialRequestRepository materialRequestRepository;
    private MaterialRequestSender materialRequestSender;
    private MaterialRequestVersion materialRequestVersion;
    private FinanceMaterial financeMaterial;
    private CommonServices commonServices;

    private List<MaterialRequestVersion> materialRequestVersions = new ArrayList<MaterialRequestVersion>();
    private List<MaterialRequestLine> materialRequestLines = new ArrayList<MaterialRequestLine>();

    @Before
    public void setUp() {
        materialRequest = Mockito.mock(MaterialRequest.class);
        materialRequestVersion = Mockito.mock(MaterialRequestVersion.class);
        materialRequestVersions.add(materialRequestVersion);

        financeMaterial = Mockito.mock(FinanceMaterial.class);

        Mockito.when(materialRequest.getCurrent()).thenReturn(materialRequestVersion);
        Mockito.when(materialRequest.getType()).thenReturn(MaterialRequestType.SINGLE);
        Mockito.when(materialRequest.getFinanceMaterial()).thenReturn(financeMaterial);
        Mockito.when(materialRequest.getMaterialRequestVersions()).thenReturn(materialRequestVersions);
        Mockito.when(materialRequestVersion.getMaterialRequestLines()).thenReturn(materialRequestLines);
        Mockito.when(materialRequestVersion.getMtrlRequestVersion()).thenReturn("S1V1");
        Mockito.when(materialRequestVersion.getChangeVersion()).thenReturn(1L);

        materialRequestRepository = Mockito.mock(MaterialRequestRepository.class);
        materialRequestSender = Mockito.mock(MaterialRequestSender.class);
        commonServices = Mockito.mock(CommonServices.class);

    }

    @Test
    public void testSendForCREATEDState() throws GloriaApplicationException {
        // Arrange
        Mockito.when(materialRequestVersion.getStatus()).thenReturn(MaterialRequestStatus.CREATED);
        Mockito.when(materialRequestRepository.validateMaterialRequest(materialRequest.getMaterialRequestOid(), materialRequest.getType().toString())).thenReturn(true);

        // Act
        materialRequest.getCurrent().getStatus().send(materialRequest, materialRequestRepository, materialRequestSender, commonServices);

        // Assert
        Mockito.verify(materialRequestVersion).setStatus(Mockito.eq(MaterialRequestStatus.SENT_ACCEPTED));
    }

    @Test
    public void testSendForUPDATEDState() throws GloriaApplicationException {
        // Arrange
        Mockito.when(materialRequestVersion.getStatus()).thenReturn(MaterialRequestStatus.UPDATED);
        Mockito.when(materialRequestRepository.validateMaterialRequest(materialRequest.getMaterialRequestOid(), materialRequest.getType().toString())).thenReturn(true);

        // Act
        materialRequest.getCurrent().getStatus().send(materialRequest, materialRequestRepository, materialRequestSender, commonServices);

        // Assert
        Mockito.verify(materialRequestVersion).setStatus(Mockito.eq(MaterialRequestStatus.SENT_WAIT));
    }

    @Test
    public void testAcceptForSENT_WAITState() throws GloriaApplicationException {
        // Arrange
        Mockito.when(materialRequestVersion.getStatus()).thenReturn(MaterialRequestStatus.SENT_WAIT);

        // Act
        materialRequest.getCurrent().getStatus().changeState(materialRequest, materialRequestRepository, "Accepted");

        // Assert
        Mockito.verify(materialRequestVersion).setStatus(Mockito.eq(MaterialRequestStatus.SENT_ACCEPTED));
    }

    @Test
    public void testRejecttForSENT_WAITState() throws GloriaApplicationException {
        // Arrange
        Mockito.when(materialRequestVersion.getStatus()).thenReturn(MaterialRequestStatus.SENT_WAIT);

        // Act
        materialRequest.getCurrent().getStatus().changeState(materialRequest, materialRequestRepository, "Rejected");

        // Assert
        Mockito.verify(materialRequestVersion).setStatus(Mockito.eq(MaterialRequestStatus.SENT_REJECTED));
    }

    @Test
    public void testCancelConfirmedCANCEL_WAITState() throws GloriaApplicationException {
        // Arrange
        Mockito.when(materialRequestVersion.getStatus()).thenReturn(MaterialRequestStatus.CANCEL_WAIT);

        // Act
        materialRequest.getCurrent().getStatus().changeState(materialRequest, materialRequestRepository, "CANCELLED");

        // Assert
        Mockito.verify(materialRequestVersion).setStatus(Mockito.eq(MaterialRequestStatus.CANCELLED));
    }

    @Test
    public void testCancelRejectedCANCEL_WAITState() throws GloriaApplicationException {
        // Arrange
        Mockito.when(materialRequestVersion.getStatus()).thenReturn(MaterialRequestStatus.CANCEL_WAIT);

        // Act
        materialRequest.getCurrent().getStatus().changeState(materialRequest, materialRequestRepository, "CANCEL_REJECTED");

        // Assert
        Mockito.verify(materialRequestVersion).setStatus(Mockito.eq(MaterialRequestStatus.CANCEL_REJECTED));
    }

}
