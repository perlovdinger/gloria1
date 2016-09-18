package com.volvo.gloria.procurematerial.d.type.procure;

import java.util.List;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;

import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.c.ProcureResponsibility;
import com.volvo.gloria.procurematerial.d.entities.FinanceHeader;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeaderVersion;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.requestline.MaterialStatus;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.procurematerial.d.type.request.RequestType;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

public class ProcureTypeTest extends AbstractTransactionalTestCase {
    public ProcureTypeTest() {
        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    }
    @Inject
    private ProcureLineRepository procureLineRepository;
      
    @Inject
    private MaterialHeaderRepository requestHeaderRepository;

    @Inject
    private ProcurementServices procurementServices;
    
    @Inject
    private TraceabilityRepository traceabilityRepository;

    @Test
    public void testSetExternal() throws GloriaApplicationException {
        // Arrange

        // Act

        // Assert
        Assert.assertEquals(ProcureType.EXTERNAL.setProcureType(2, 1, 0), ProcureType.EXTERNAL);
    }

    @Test
    public void testSetExternalFromStock() throws GloriaApplicationException {
        // Arrange

        // Act

        // Assert
        Assert.assertEquals(ProcureType.EXTERNAL.setProcureType(2, 1, 2), ProcureType.EXTERNAL_FROM_STOCK);
    }

    @Test
    public void testSetInternal() throws GloriaApplicationException {
        // Arrange

        // Act

        // Assert
        Assert.assertEquals(ProcureType.INTERNAL.setProcureType(2, 1, 0), ProcureType.INTERNAL);
    }

    @Test
    public void testSetInternalFromStock() throws GloriaApplicationException {
        // Arrange

        // Act

        // Assert
        Assert.assertEquals(ProcureType.INTERNAL.setProcureType(2, 1, 1), ProcureType.INTERNAL_FROM_STOCK);
    }

    @Test
    public void testSetFromStock() throws GloriaApplicationException {
        // Arrange

        // Act

        // Assert
        Assert.assertEquals(ProcureType.INTERNAL.setProcureType(2, 1, 4), ProcureType.FROM_STOCK);
    }

    @Test(expected = GloriaApplicationException.class)
    public void testForwardExternal() throws GloriaApplicationException {
        // Arrange
        ProcureLine procureLine = new ProcureLine();
        UserDTO userDTO = new UserDTO();
        userDTO.setId("xx");
        // Act
        ProcureType.EXTERNAL.forward(procureLine, "INT_PROC_TEAM", "INT_PROC_USER_1", "INT_PROC_USER_NAME", procureLineRepository, procurementServices,
                                     userDTO, traceabilityRepository);
        // Assert
    }

    @Test(expected = GloriaApplicationException.class)
    public void testForwardExternalFromStock() throws GloriaApplicationException {
        // Arrange
        ProcureLine procureLine = new ProcureLine();
        UserDTO userDTO = new UserDTO();
        userDTO.setId("xx");
        // Act
        ProcureType.EXTERNAL_FROM_STOCK.forward(procureLine, "INT_PROC_TEAM", "INT_PROC_USER_1", "INT_PROC_USER_NAME", procureLineRepository,
                                                procurementServices, userDTO, traceabilityRepository);
        // Assert
    }

    @Test(expected = GloriaApplicationException.class)
    public void testReturnExternal() throws GloriaApplicationException {
        // Arrange
        ProcureLine procureLine = new ProcureLine();
        procureLine.setForwardedUserId("INT_PROC_USER_1");
        procureLine.setForwardedUserName("INT_PROC_USER_NAME");
        procureLine.setForwardedTeam("INT_PROC_TEAM");
        // Act
        ProcureType.EXTERNAL.returnProcureLine(procureLine, procureLineRepository,null);
        // Assert
    }

    @Test(expected = GloriaApplicationException.class)
    public void testReturnExternalFromStock() throws GloriaApplicationException {
        // Arrange
        ProcureLine procureLine = new ProcureLine();
        procureLine.setForwardedUserId("INT_PROC_USER_1");
        procureLine.setForwardedUserName("INT_PROC_USER_NAME");
        procureLine.setForwardedTeam("INT_PROC_TEAM");
        // Act
        ProcureType.EXTERNAL_FROM_STOCK.returnProcureLine(procureLine, procureLineRepository,null);
        // Assert
    }
    
    @Test
        public void testAssignInternalInternal() throws GloriaApplicationException {
            // Arrange
            ProcureLine procureLine = new ProcureLine();
            // Act
            ProcureType.INTERNAL.assignInternal(procureLine, "INT_PROC_TEAM", "INT_PROC_USER_1", "INT_PROC_USER_NAME", procureLineRepository);
            // Assert
            Assert.assertEquals("INT_PROC_TEAM", procureLine.getForwardedTeam());
        }

    @Test
        public void testAssignInternalInternalFromStock() throws GloriaApplicationException {
            // Arrange
            ProcureLine procureLine = new ProcureLine();
            // Act
            ProcureType.INTERNAL_FROM_STOCK.assignInternal(procureLine, "INT_PROC_TEAM", "INT_PROC_USER_1", "INT_PROC_USER_NAME", procureLineRepository);
            // Assert
            Assert.assertEquals("INT_PROC_TEAM", procureLine.getForwardedTeam());
        }

    @Test
        public void testAssignInternalFromStock() throws GloriaApplicationException {
            // Arrange
            ProcureLine procureLine = new ProcureLine();
            // Act
            ProcureType.FROM_STOCK.assignInternal(procureLine, "INT_PROC_TEAM", "INT_PROC_USER_1", "INT_PROC_USER_NAME", procureLineRepository);
            // Assert
            Assert.assertEquals("INT_PROC_TEAM", procureLine.getForwardedTeam());
        }

    @Test(expected = GloriaApplicationException.class)
        public void testAssignInternalExternal() throws GloriaApplicationException {
            // Arrange
            ProcureLine procureLine = new ProcureLine();
            // Act
            ProcureType.EXTERNAL.assignInternal(procureLine, "INT_PROC_TEAM", "INT_PROC_USER_1", "INT_PROC_USER_NAME", procureLineRepository);
            // Assert
        }

    @Test(expected = GloriaApplicationException.class)
        public void testAssignInternalExternalFromStock() throws GloriaApplicationException {
            // Arrange
            ProcureLine procureLine = new ProcureLine();
            // Act
            ProcureType.EXTERNAL_FROM_STOCK.assignInternal(procureLine, "INT_PROC_TEAM", "INT_PROC_USER_1", "INT_PROC_USER_NAME", procureLineRepository);
            // Assert
        }
    
    @Test
    public void testGLO4502RevertProcureLineForModified() throws GloriaApplicationException {
        // Arrange
        MaterialHeader materialHeader = new MaterialHeader();
        materialHeader.setMaterialControllerUserId("tin3000");
        materialHeader.setRequestType(RequestType.SINGLE);
        MaterialHeaderVersion acceptedHeaderVersion = new MaterialHeaderVersion();
        materialHeader.setAccepted(acceptedHeaderVersion);

        Material material = new Material();
        material.setPartAffiliation("V");
        material.setMaterialType(MaterialType.MODIFIED);
        MaterialLine materialLine = new MaterialLine();
        materialLine.setProcureType(ProcureType.INTERNAL);
        materialLine.setStatus(MaterialLineStatus.ORDER_PLACED_INTERNAL);
        materialLine.setQuantity(10L);
        materialLine.setMaterial(material);
        material.getMaterialLine().add(materialLine);
        material.setMaterialHeader(materialHeader);
        materialHeader.getMaterials().add(material);

        Material materialUR = new Material();
        materialUR.setPartAffiliation("V");
        materialUR.setMaterialType(MaterialType.USAGE_REPLACED);
        materialUR.setStatus(MaterialStatus.ADDED);
        MaterialLine materialLine2 = new MaterialLine();
        materialLine2.setProcureType(ProcureType.INTERNAL);
        materialLine2.setStatus(MaterialLineStatus.ORDER_PLACED_INTERNAL);
        materialLine2.setQuantity(10L);
        materialLine2.setMaterial(materialUR);
        materialUR.getMaterialLine().add(materialLine2);
        materialUR.setMaterialHeader(materialHeader);
        materialHeader.getMaterials().add(materialUR);
        FinanceHeader financeHeader = new FinanceHeader();
        materialUR.setFinanceHeader(financeHeader);

        requestHeaderRepository.save(materialHeader);

        ProcureLine procureLine = new ProcureLine();
        procureLine.setResponsibility(ProcureResponsibility.PROCURER);
        procureLine.getMaterials().add(material);
        procureLine.getMaterials().add(materialUR);
        
        UserDTO userDTO = new UserDTO();
        userDTO.setId("123");

        // Act
        ProcureTypeHelper.revertMaterial(procureLine, requestHeaderRepository, procureLineRepository, procurementServices, userDTO, null);

        // Assert
        List<ProcureLine> procureLines = procureLineRepository.findAll();
        Assert.assertEquals(1, procureLines.size());
        Assert.assertEquals(1, procureLines.get(0).getMaterials().size());
        Assert.assertEquals(MaterialType.USAGE, procureLines.get(0).getMaterials().get(0).getMaterialType());
    }
   
}
