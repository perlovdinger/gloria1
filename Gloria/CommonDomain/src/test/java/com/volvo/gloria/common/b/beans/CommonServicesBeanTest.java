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
package com.volvo.gloria.common.b.beans;

import static com.volvo.gloria.util.Utils.compress;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.naming.NamingException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.b.CompanyCodeTransformer;
import com.volvo.gloria.common.b.CostCenterTransformer;
import com.volvo.gloria.common.b.GLAccountTransformer;
import com.volvo.gloria.common.b.WBSElementTransformer;
import com.volvo.gloria.common.c.CarryOverActionType;
import com.volvo.gloria.common.c.dto.CostCenterDTO;
import com.volvo.gloria.common.c.dto.DeliveryFollowUpTeamDTO;
import com.volvo.gloria.common.c.dto.DeliveryFollowUpTeamFilterDTO;
import com.volvo.gloria.common.c.dto.PartAffiliationDTO;
import com.volvo.gloria.common.c.dto.ProjectDTO;
import com.volvo.gloria.common.c.dto.SiteDTO;
import com.volvo.gloria.common.c.dto.UnitOfMeasureDTO;
import com.volvo.gloria.common.c.dto.WbsElementDTO;
import com.volvo.gloria.common.c.dto.XmlLogEventDTO;
import com.volvo.gloria.common.carryover.c.dto.CarryOverItemDTO;
import com.volvo.gloria.common.carryover.c.dto.SyncPurchaseOrderCarryOverDTO;
import com.volvo.gloria.common.costcenter.c.dto.SyncCostCenterDTO;
import com.volvo.gloria.common.d.entities.CarryOver;
import com.volvo.gloria.common.d.entities.CompanyCode;
import com.volvo.gloria.common.d.entities.CostCenter;
import com.volvo.gloria.common.d.entities.DeliveryFollowUpTeam;
import com.volvo.gloria.common.d.entities.DeliveryFollowUpTeamFilter;
import com.volvo.gloria.common.d.entities.PartAffiliation;
import com.volvo.gloria.common.d.entities.SupplierCounterPart;
import com.volvo.gloria.common.d.entities.Traceability;
import com.volvo.gloria.common.d.entities.TraceabilityType;
import com.volvo.gloria.common.d.entities.UnitOfMeasure;
import com.volvo.gloria.common.d.entities.WbsElement;
import com.volvo.gloria.common.repositories.b.CarryOverRepository;
import com.volvo.gloria.common.repositories.b.CompanyCodeRepository;
import com.volvo.gloria.common.repositories.b.CostCenterRepository;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.common.repositories.b.WbsElementRepository;
import com.volvo.gloria.common.util.CommonHelper;
import com.volvo.gloria.common.wbs.c.dto.SyncWBSElementDTO;
import com.volvo.gloria.util.DateUtil;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.IOUtil;
import com.volvo.gloria.util.b.UtilServices;
import com.volvo.gloria.util.c.GloriaParams;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.paging.c.PageResults;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

public class CommonServicesBeanTest extends AbstractTransactionalTestCase {
    public CommonServicesBeanTest() {
        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    } 
    private static final boolean REQUESTABLE = true;
    private static final String INITDATA_PART_AFFILIATION_XML = "globaldataTest/PartAffiliation.xml";
    private static final String INITDATA_UNIT_OF_MEASURE_XML = "globaldataTest/UnitOfMeasure.xml";
    private static final String INITDATA_DELIVERY_FOLLOW_UP_TEAM_XML = "globaldataTest/DeliveryFollowUpTeam.xml";
    private static final String INITDATA_SITE_XML = "globaldataTest/Site.xml";
    private static final String INITDATA_WBSELEMENT_XML = "globaldataTest/WbsElement.xml";
    private static final String INITDATA_COMPANY_CODE_XML = "globaldataTest/CompanyCode.xml";
    private static final String INITDATA_COSTCENTER_XML = "globaldataTest/SyncCostCenter_1_0_01.xml";
    private static final String COSTCENTER_XML_LOAD = "globaldataTest/SyncCostCenter_Load.xml";
    private static final String COSTCENTER_XML_UPDATES = "globaldataTest/SyncCostCenter_Updates.xml";
    private static final String INITDATA_GLACCOUNT_XML = "globaldataTest/GlAccount.xml";

    @Inject
    private CommonServices commonServices;
    
    @Inject
    private WbsElementRepository wbsElementRepo;
    
    @Inject
    private CompanyCodeRepository companycodeRepo;
    
    @Inject
    private CostCenterRepository costCenterRepo;
    
    @Inject
    private WBSElementTransformer wbsElementStorageTransformer;
    
    @Inject
    private CostCenterTransformer costCenterStorageTransformer;
    
    @Inject
    private GLAccountTransformer gLAccountTransformer;
    
    @Inject
    private CompanyCodeTransformer companyCodeTransformer;
    
    @Inject
    private CarryOverRepository carryOverRepo;
    
    @Inject 
    private TraceabilityRepository traceabilityRepository;
    
    @Inject 
    UtilServices utilServices;
    
    @Before
    public void setUpTestData() throws Exception {
        commonServices.createDeliveryFollowUpTeamData(IOUtil.getStringFromClasspath(INITDATA_DELIVERY_FOLLOW_UP_TEAM_XML));
        
        commonServices.addSyncCompanyCode(companyCodeTransformer.transformCompanyCode(IOUtil.getStringFromClasspath(INITDATA_COMPANY_CODE_XML)));
        
        commonServices.createGlAccounts(gLAccountTransformer.transformGLAccount(IOUtil.getStringFromClasspath(INITDATA_GLACCOUNT_XML)));

        commonServices.syncCostCenter(costCenterStorageTransformer.transformStoredCostCenter(IOUtil.getStringFromClasspath(INITDATA_COSTCENTER_XML)));

        commonServices.syncWBSElement(wbsElementStorageTransformer.transformStoredWBSElement(IOUtil.getStringFromClasspath(INITDATA_WBSELEMENT_XML)));

        commonServices.createSitesData(IOUtil.getStringFromClasspath(INITDATA_SITE_XML));
    }

    //@Test
    public void testGetAllSupplierCounterParts() {
        // Act
        List<SupplierCounterPart> counterParts = commonServices.getAllSupplierCounterParts("1");
        // Assert
        Assert.assertNotNull(counterParts);
        Assert.assertTrue(counterParts.size() > 0);
    }

    @Test
    public void testFindAllPartAffiliations() throws IOException {
        // Arrange
        commonServices.createPartAffiliationData(IOUtil.getStringFromClasspath(INITDATA_PART_AFFILIATION_XML));
        // Act
        List<PartAffiliation> partAffiliation = commonServices.getAllPartAffiliations(REQUESTABLE);
        List<PartAffiliationDTO> partAffiliationDTO = CommonHelper.transformAsPartAffiliationDTO(partAffiliation);
        // Assert
        Assert.assertNotNull(partAffiliation);
       Assert.assertTrue(partAffiliation.size() > 0);
       Assert.assertEquals(4, partAffiliation.size());
        Assert.assertNotNull(partAffiliationDTO);
        Assert.assertTrue(partAffiliationDTO.size() > 0);
    }

    @Test
    public void testFindAllUnitOfMeasures() throws IOException {
        // Arrange
        commonServices.createUnitOfMeasureData(IOUtil.getStringFromClasspath(INITDATA_UNIT_OF_MEASURE_XML));
        // Act
        List<UnitOfMeasure> unitOfMeasures = commonServices.getAllUnitOfMeasures();
        List<UnitOfMeasureDTO> unitOfMeasureDTO = CommonHelper.transformAsUnitOfMeasureDTO(unitOfMeasures);
        // Assert
        Assert.assertNotNull(unitOfMeasures);
        Assert.assertTrue(unitOfMeasures.size() > 0);
        Assert.assertNotNull(unitOfMeasureDTO);
        Assert.assertTrue(unitOfMeasureDTO.size() > 0);
    }

    @Test
    public void testGetDeliveryFollowupTeam() {

        // Arrange
        String loggedInUserIdFollowUpName = "BLR";

        // Act
        DeliveryFollowUpTeamDTO deliveryFollowUpTeamDTO = commonServices.getDeliveryFollowupTeam(loggedInUserIdFollowUpName);

        // Assert
        Assert.assertEquals("BLR", deliveryFollowUpTeamDTO.getName());
        Assert.assertEquals("SUPPLIER", deliveryFollowUpTeamDTO.getFollowUpType());

    }

    @Test
    public void testUpdateDeliveryFollowUpTeam() throws GloriaApplicationException {
        // Arrange
        String loggedInUserIdFollowUpName = "BLR";
        DeliveryFollowUpTeamDTO deliveryFollowUpTeamDTO = commonServices.getDeliveryFollowupTeam(loggedInUserIdFollowUpName);
        deliveryFollowUpTeamDTO.setDefaultDcUserid("DefaultUserId");

        // Act
        DeliveryFollowUpTeam deliveryFDTO = commonServices.updateDeliveryFollowupTeam(deliveryFollowUpTeamDTO);

        // Assert
        Assert.assertEquals("DefaultUserId", deliveryFDTO.getDefaultDCUserId());

    }

    @Test
    public void testUpdateDeliveryFollowupTeamFilter() throws GloriaApplicationException {

        // Arrange
        String loggedInUserIdFollowUpName = "BLR";
        DeliveryFollowUpTeamDTO deliveryFollowUpTeamDTO = commonServices.getDeliveryFollowupTeam(loggedInUserIdFollowUpName);
        List<DeliveryFollowUpTeamFilter> dFollowUpTFs = commonServices.getDeliveryFollowUpTeamFilters(deliveryFollowUpTeamDTO.getId());
        List<DeliveryFollowUpTeamFilterDTO> dFollowUpTFDtos = CommonHelper.transformAsDeliveryFollowUpTeamFilterDTO(dFollowUpTFs);
        if (dFollowUpTFDtos.isEmpty()) {
            DeliveryFollowUpTeamFilterDTO deliveryFollowUpFDto = new DeliveryFollowUpTeamFilterDTO();
            deliveryFollowUpFDto.setSupplierId("1231");
            deliveryFollowUpFDto.setSuffix("129");
            deliveryFollowUpFDto.setDeliveryControllerUserId("1989");
            commonServices.addDeliveryFollowUpTeamFilter(deliveryFollowUpFDto, deliveryFollowUpTeamDTO.getId());
        }

        dFollowUpTFs = commonServices.getDeliveryFollowUpTeamFilters(deliveryFollowUpTeamDTO.getId());
        dFollowUpTFDtos = CommonHelper.transformAsDeliveryFollowUpTeamFilterDTO(dFollowUpTFs);
        dFollowUpTFDtos.get(0).setSuffix("512");

        // Act
        DeliveryFollowUpTeamFilter deliveryFTFDTO = commonServices.updateDeliveryFollowupTeamFilter(dFollowUpTFDtos.get(0));

        // Assert
        Assert.assertEquals("512", deliveryFTFDTO.getSuffix());

    }

    @Test
    public void testFindSupplierCounterPartByPPSuffixWithValidSuffix() {
        // Arrange
        String ppSuffix = "519";

        // Act
        SupplierCounterPart suppilerCounterPart = commonServices.findSupplierCounterPartByPPSuffix(ppSuffix);

        // Assert
        Assert.assertNotNull(suppilerCounterPart);
        Assert.assertEquals("Curitiba Bus", suppilerCounterPart.getComment());

    }

    @Test
    public void testFindSupplierCounterPartByPPSuffixWithInvalidSuffix() {
        // Arrange
        String ppSuffix = "5190";

        // Act
        SupplierCounterPart suppilerCounterPart = commonServices.findSupplierCounterPartByPPSuffix(ppSuffix);

        // Assert
        Assert.assertNull(suppilerCounterPart);

    }

    /**
     * Test case for matching delivery Controller when suffix(519) , supplier Id(543) and other values as empty are passed and the rules are as below: 1st rule
     * - Suffix(519) assigned to "gaynor" DCUserId , 2nd rule - SupplierId(543) assigned to "kerstin" DCuserId expected result is "Kerstin" as we try to match
     * the SupplierId rule first when compared to Suffix rule
     * 
     * @throws GloriaApplicationException
     */
    //@Test
    public void testMatchDeliveryControllerCase1() throws GloriaApplicationException {
        // Arrange
        List<DeliveryFollowUpTeam> teams = commonServices.findAllDeliveryFollowUpTeam();
        DeliveryFollowUpTeam deliveryFollowUpTeam = null;
        for (DeliveryFollowUpTeam delFollowUpTeam : teams) {
            if (delFollowUpTeam.getName().equalsIgnoreCase("DEL-FOLLOW-UP-CUR")) {
                deliveryFollowUpTeam = delFollowUpTeam;
                break;
            }
        }

        String ppSuffix = "";
        String supplierId = "";
        String projectId = "";
        List<DeliveryFollowUpTeamFilterDTO> teamFilters = new ArrayList<DeliveryFollowUpTeamFilterDTO>();

        // Arrange data for testing with just suffix matching
        DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO1 = getDTOForSuffixTest(ppSuffix, supplierId, projectId);
        teamFilters.add(deliveryFollowUpTeamFilterDTO1);
        // Arrange data for testing with just supplier id matching
        DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO2 = getDTOForSupplierTest(ppSuffix, supplierId, projectId);
        teamFilters.add(deliveryFollowUpTeamFilterDTO2);

        for (DeliveryFollowUpTeamFilterDTO dftf : teamFilters) {
            commonServices.addDeliveryFollowUpTeamFilter(dftf, deliveryFollowUpTeam.getDeliveryFollowUpTeamOid());
        }

        // Act
        String deliveryControllerUserID = commonServices.matchDeliveryController(deliveryFollowUpTeam, "519", "543", "");

        // Assert
        Assert.assertEquals("kerstin", deliveryControllerUserID);

    }

    /**
     * Test case for matching delivery Controller when suffix(519) and other values as empty are passed and the rules are as below: 1st rule - Suffix(519)
     * assigned to "gaynor" DCUserId , 2nd rule - SupplierId(543) assigned to "kerstin" DCuserId expected result is "gaynor" as suffix rule matches
     * 
     * @throws GloriaApplicationException
     */
    @Test
    public void testMatchDeliveryControllerCase2() throws GloriaApplicationException {
        // Arrange
        List<DeliveryFollowUpTeam> teams = commonServices.findAllDeliveryFollowUpTeam();
        DeliveryFollowUpTeam deliveryFollowUpTeam = null;
        for (DeliveryFollowUpTeam delFollowUpTeam : teams) {
            if (delFollowUpTeam.getName().equalsIgnoreCase("CUR")) {
                deliveryFollowUpTeam = delFollowUpTeam;
                break;
            }
        }

        String ppSuffix = "";
        String supplierId = "";
        String projectId = "";
        List<DeliveryFollowUpTeamFilterDTO> teamFilters = new ArrayList<DeliveryFollowUpTeamFilterDTO>();

        // Arrange data for testing with just suffix matching
        DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO1 = getDTOForSuffixTest(ppSuffix, supplierId, projectId);
        teamFilters.add(deliveryFollowUpTeamFilterDTO1);
        // Arrange data for testing with just supplier id matching
        DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO2 = getDTOForSupplierTest(ppSuffix, supplierId, projectId);
        teamFilters.add(deliveryFollowUpTeamFilterDTO2);

        for (DeliveryFollowUpTeamFilterDTO dftf : teamFilters) {
            commonServices.addDeliveryFollowUpTeamFilter(dftf, deliveryFollowUpTeam.getDeliveryFollowUpTeamOid());
        }

        // Act
        String deliveryControllerUserID = commonServices.matchDeliveryController(deliveryFollowUpTeam, "519", "", "");

        // Assert
        Assert.assertEquals("gaynor", deliveryControllerUserID);

    }

    /**
     * Test case for matching delivery Controller when supplierId(543) and other values as empty are passed and the rules are as below: 1st rule - Suffix(519)
     * assigned to "gaynor" DCUserId , 2nd rule - SupplierId(543) assigned to "kerstin" DCuserId expected result is "kerstin" as supplierId rule matches
     * 
     * @throws GloriaApplicationException
     */
    @Test
    public void testMatchDeliveryControllerCase3() throws GloriaApplicationException {
        // Arrange
        List<DeliveryFollowUpTeam> teams = commonServices.findAllDeliveryFollowUpTeam();
        DeliveryFollowUpTeam deliveryFollowUpTeam = null;
        for (DeliveryFollowUpTeam delFollowUpTeam : teams) {
            if (delFollowUpTeam.getName().equalsIgnoreCase("CUR")) {
                deliveryFollowUpTeam = delFollowUpTeam;
                break;
            }
        }

        String ppSuffix = "";
        String supplierId = "";
        String projectId = "";
        List<DeliveryFollowUpTeamFilterDTO> teamFilters = new ArrayList<DeliveryFollowUpTeamFilterDTO>();

        // Arrange data for testing with just suffix matching
        DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO1 = getDTOForSuffixTest(ppSuffix, supplierId, projectId);
        teamFilters.add(deliveryFollowUpTeamFilterDTO1);
        // Arrange data for testing with just supplier id matching
        DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO2 = getDTOForSupplierTest(ppSuffix, supplierId, projectId);
        teamFilters.add(deliveryFollowUpTeamFilterDTO2);

        for (DeliveryFollowUpTeamFilterDTO dftf : teamFilters) {
            commonServices.addDeliveryFollowUpTeamFilter(dftf, deliveryFollowUpTeam.getDeliveryFollowUpTeamOid());
        }

        // Act
        String deliveryControllerUserID = commonServices.matchDeliveryController(deliveryFollowUpTeam, "", "543", "");

        // Assert
        Assert.assertEquals("kerstin", deliveryControllerUserID);

    }

    /**
     * Test case for matching delivery Controller when suffix(519) , supplier Id(543) and other values as empty are passed and the rules are as below: 1st rule
     * - SupplierID (543) Suffix(519) assigned to "Grace" DCUserId, 2nd rule is Suffix(519) assigned to "gaynor" DCUserId , 3rd rule - SupplierId(543) assigned
     * to "kerstin" DCuserId expected result is "Grace" as we try to match the SupplierId and suffix rule first when compared to other rules
     * 
     * @throws GloriaApplicationException
     */
    //@Test
    public void testMatchDeliveryControllerCase4() throws GloriaApplicationException {
        // Arrange
        List<DeliveryFollowUpTeam> teams = commonServices.findAllDeliveryFollowUpTeam();
        DeliveryFollowUpTeam deliveryFollowUpTeam = null;
        for (DeliveryFollowUpTeam delFollowUpTeam : teams) {
            if (delFollowUpTeam.getName().equalsIgnoreCase("DEL-FOLLOW-UP-CUR")) {
                deliveryFollowUpTeam = delFollowUpTeam;
                break;
            }
        }

        String ppSuffix = "";
        String supplierId = "";
        String projectId = "";
        List<DeliveryFollowUpTeamFilterDTO> teamFilters = new ArrayList<DeliveryFollowUpTeamFilterDTO>();

        // Arrange data for testing with just suffix matching
        DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO1 = getDTOForSuffixTest(ppSuffix, supplierId, projectId);
        teamFilters.add(deliveryFollowUpTeamFilterDTO1);
        // Arrange data for testing with just supplier id matching
        DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO2 = getDTOForSupplierTest(ppSuffix, supplierId, projectId);
        teamFilters.add(deliveryFollowUpTeamFilterDTO2);

        DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO3 = getDTOForSuffixWithSupplierTest(ppSuffix, supplierId, projectId);
        teamFilters.add(deliveryFollowUpTeamFilterDTO3);

        for (DeliveryFollowUpTeamFilterDTO dftf : teamFilters) {
            commonServices.addDeliveryFollowUpTeamFilter(dftf, deliveryFollowUpTeam.getDeliveryFollowUpTeamOid());
        }

        // Act
        String deliveryControllerUserID = commonServices.matchDeliveryController(deliveryFollowUpTeam, "519", "543", "");

        // Assert
        Assert.assertEquals("Grace", deliveryControllerUserID);
    }

    /**
     * Test case for matching delivery Controller when suffix(5190) , supplier Id(5430) and other values as empty are passed and the rules are as below: 1st
     * rule - SupplierID (543) Suffix(519) assigned to "Grace" DCUserId, 2nd rule is Suffix(519) assigned to "gaynor" DCUserId , 3rd rule - SupplierId(543)
     * assigned to "kerstin" DCuserId expected result is "DefaultDCUserID" as no rule matches the parameters passed
     * 
     * @throws GloriaApplicationException
     */
    @Test
    public void testMatchDeliveryControllerCase5() throws GloriaApplicationException {
        // Arrange
        List<DeliveryFollowUpTeam> teams = commonServices.findAllDeliveryFollowUpTeam();
        DeliveryFollowUpTeam deliveryFollowUpTeam = null;
        for (DeliveryFollowUpTeam delFollowUpTeam : teams) {
            if (delFollowUpTeam.getName().equalsIgnoreCase("CUR")) {
                deliveryFollowUpTeam = delFollowUpTeam;
                break;
            }
        }

        String ppSuffix = "";
        String supplierId = "";
        String projectId = "";
        List<DeliveryFollowUpTeamFilterDTO> teamFilters = new ArrayList<DeliveryFollowUpTeamFilterDTO>();

        // Arrange data for testing with just suffix matching
        DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO1 = getDTOForSuffixTest(ppSuffix, supplierId, projectId);
        teamFilters.add(deliveryFollowUpTeamFilterDTO1);
        // Arrange data for testing with just supplier id matching
        DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO2 = getDTOForSupplierTest(ppSuffix, supplierId, projectId);
        teamFilters.add(deliveryFollowUpTeamFilterDTO2);

        DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO3 = getDTOForSuffixWithSupplierTest(ppSuffix, supplierId, projectId);
        teamFilters.add(deliveryFollowUpTeamFilterDTO3);

        for (DeliveryFollowUpTeamFilterDTO dftf : teamFilters) {
            commonServices.addDeliveryFollowUpTeamFilter(dftf, deliveryFollowUpTeam.getDeliveryFollowUpTeamOid());
        }
        // Act
        String deliveryControllerUserID = commonServices.matchDeliveryController(deliveryFollowUpTeam, "5190", "5430", "");

        // Assert
        Assert.assertEquals(deliveryFollowUpTeam.getDefaultDCUserId(), deliveryControllerUserID);
    }

    /**
     * Test case for matching delivery Controller when suffix(5190) , supplier Id(543) and other values as empty are passed and the rules are as below: 1st rule
     * - SupplierID (543) Suffix(519) assigned to "Grace" DCUserId, 2nd rule is Suffix(519) assigned to "gaynor" DCUserId , 3rd rule - SupplierId(543) assigned
     * to "kerstin" DCuserId expected result is "kerstin" as supplierId passed matches with the 3rd rule.
     * 
     * @throws GloriaApplicationException
     */
    @Test
    public void testMatchDeliveryControllerCase6() throws GloriaApplicationException {
        // Arrange
        List<DeliveryFollowUpTeam> teams = commonServices.findAllDeliveryFollowUpTeam();
        DeliveryFollowUpTeam deliveryFollowUpTeam = null;
        for (DeliveryFollowUpTeam delFollowUpTeam : teams) {
            if (delFollowUpTeam.getName().equalsIgnoreCase("CUR")) {
                deliveryFollowUpTeam = delFollowUpTeam;
                break;
            }
        }

        String ppSuffix = "";
        String supplierId = "";
        String projectId = "";
        List<DeliveryFollowUpTeamFilterDTO> teamFilters = new ArrayList<DeliveryFollowUpTeamFilterDTO>();

        // Arrange data for testing with just suffix matching
        DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO1 = getDTOForSuffixTest(ppSuffix, supplierId, projectId);
        teamFilters.add(deliveryFollowUpTeamFilterDTO1);
        // Arrange data for testing with just supplier id matching
        DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO2 = getDTOForSupplierTest(ppSuffix, supplierId, projectId);
        teamFilters.add(deliveryFollowUpTeamFilterDTO2);

        DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO3 = getDTOForSuffixWithSupplierTest(ppSuffix, supplierId, projectId);
        teamFilters.add(deliveryFollowUpTeamFilterDTO3);

        for (DeliveryFollowUpTeamFilterDTO dftf : teamFilters) {
            commonServices.addDeliveryFollowUpTeamFilter(dftf, deliveryFollowUpTeam.getDeliveryFollowUpTeamOid());
        }
        // Act
        String deliveryControllerUserID = commonServices.matchDeliveryController(deliveryFollowUpTeam, "5190", "543", "");

        // Assert
        Assert.assertEquals("kerstin", deliveryControllerUserID);
    }

    /**
     * Test case for matching delivery Controller when suffix(519) , supplier Id(567) and other values as empty are passed and the rules are as below: 1st rule
     * - SupplierID (543) Suffix(519) assigned to "Grace" DCUserId, 2nd rule is Suffix(519) assigned to "gaynor" DCUserId , 3rd rule - SupplierId(543) assigned
     * to "kerstin" DCuserId expected result is "gaynor" as suffix passed matches with the 2nd rule.
     * 
     * @throws GloriaApplicationException
     */
    @Test
    public void testMatchDeliveryControllerCase7() throws GloriaApplicationException {
        // Arrange
        List<DeliveryFollowUpTeam> teams = commonServices.findAllDeliveryFollowUpTeam();
        DeliveryFollowUpTeam deliveryFollowUpTeam = null;
        for (DeliveryFollowUpTeam delFollowUpTeam : teams) {
            if (delFollowUpTeam.getName().equalsIgnoreCase("CUR")) {
                deliveryFollowUpTeam = delFollowUpTeam;
                break;
            }
        }

        String ppSuffix = "";
        String supplierId = "";
        String projectId = "";
        List<DeliveryFollowUpTeamFilterDTO> teamFilters = new ArrayList<DeliveryFollowUpTeamFilterDTO>();

        // Arrange data for testing with just suffix matching
        DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO1 = getDTOForSuffixTest(ppSuffix, supplierId, projectId);
        teamFilters.add(deliveryFollowUpTeamFilterDTO1);
        // Arrange data for testing with just supplier id matching
        DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO2 = getDTOForSupplierTest(ppSuffix, supplierId, projectId);
        teamFilters.add(deliveryFollowUpTeamFilterDTO2);

        DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO3 = getDTOForSuffixWithSupplierTest(ppSuffix, supplierId, projectId);
        teamFilters.add(deliveryFollowUpTeamFilterDTO3);

        for (DeliveryFollowUpTeamFilterDTO dftf : teamFilters) {
            commonServices.addDeliveryFollowUpTeamFilter(dftf, deliveryFollowUpTeam.getDeliveryFollowUpTeamOid());
        }
        // Act
        String deliveryControllerUserID = commonServices.matchDeliveryController(deliveryFollowUpTeam, "519", "567", "");

        // Assert
        Assert.assertEquals("gaynor", deliveryControllerUserID);
    }

    /**
     * Test case for matching delivery Controller when a deliveryFollowUpTeam(DEL-FOLLOW-UP-BLR) which has no filters existing along with suffix(519) , supplier
     * Id(567) and other values as empty are passed and the rules are as below: 1st rule - SupplierID (543) Suffix(519) assigned to "Grace" DCUserId, 2nd rule
     * is Suffix(519) assigned to "gaynor" DCUserId , 3rd rule - SupplierId(543) assigned to "kerstin" DCuserId expected result is defaultDCUserId as it does
     * not have any filters for the passed delFollowUpTeam
     * 
     * @throws GloriaApplicationException
     */
    @Test
    public void testMatchDeliveryControllerCase8() throws GloriaApplicationException {
        // Arrange
        List<DeliveryFollowUpTeam> teams = commonServices.findAllDeliveryFollowUpTeam();
        DeliveryFollowUpTeam deliveryFollowUpTeam = null;
        for (DeliveryFollowUpTeam delFollowUpTeam : teams) {
            if (delFollowUpTeam.getName().equalsIgnoreCase("CUR")) {
                deliveryFollowUpTeam = delFollowUpTeam;
                break;
            }
        }

        String ppSuffix = "";
        String supplierId = "";
        String projectId = "";
        List<DeliveryFollowUpTeamFilterDTO> teamFilters = new ArrayList<DeliveryFollowUpTeamFilterDTO>();

        // Arrange data for testing with just suffix matching
        DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO1 = getDTOForSuffixTest(ppSuffix, supplierId, projectId);
        teamFilters.add(deliveryFollowUpTeamFilterDTO1);
        // Arrange data for testing with just supplier id matching
        DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO2 = getDTOForSupplierTest(ppSuffix, supplierId, projectId);
        teamFilters.add(deliveryFollowUpTeamFilterDTO2);

        DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO3 = getDTOForSuffixWithSupplierTest(ppSuffix, supplierId, projectId);
        teamFilters.add(deliveryFollowUpTeamFilterDTO3);

        for (DeliveryFollowUpTeamFilterDTO dftf : teamFilters) {
            commonServices.addDeliveryFollowUpTeamFilter(dftf, deliveryFollowUpTeam.getDeliveryFollowUpTeamOid());
        }

        for (DeliveryFollowUpTeam delFollowUpTeam : teams) {
            if (delFollowUpTeam.getName().equalsIgnoreCase("BLR")) {
                deliveryFollowUpTeam = delFollowUpTeam;
                break;
            }
        }
        // Act
        String deliveryControllerUserID = commonServices.matchDeliveryController(deliveryFollowUpTeam, "519", "543", "");

        // Assert
        Assert.assertEquals(deliveryFollowUpTeam.getDefaultDCUserId(), deliveryControllerUserID);
    }

    /**
     * Test case for matching delivery Controller for ProjectType and projectId(788) and other values as empty are passed and the rules are as below: 1st rule -
     * SupplierID (543) Suffix(519) assigned to "Grace" DCUserId, 2nd rule is Suffix(519) assigned to "gaynor" DCUserId , 3rd rule - SupplierId(543) assigned to
     * "kerstin" DCuserId 4th rule - ProjectID(788) is assigned to "mark" expected result is "mark" as we try to match the ProjectId rule for Project followUp
     * type team
     * 
     * @throws GloriaApplicationException
     */
    @Test
    public void testMatchDeliveryControllerCase9() throws GloriaApplicationException {
        // Arrange
        List<DeliveryFollowUpTeam> teams = commonServices.findAllDeliveryFollowUpTeam();
        DeliveryFollowUpTeam deliveryFollowUpTeam = null;
        for (DeliveryFollowUpTeam delFollowUpTeam : teams) {
            if (delFollowUpTeam.getName().equalsIgnoreCase("CUR")) {
                deliveryFollowUpTeam = delFollowUpTeam;
                break;
            }
        }

        String ppSuffix = "";
        String supplierId = "";
        String projectId = "";
        List<DeliveryFollowUpTeamFilterDTO> teamFilters = new ArrayList<DeliveryFollowUpTeamFilterDTO>();

        // Arrange data for testing with just suffix matching
        DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO1 = getDTOForSuffixTest(ppSuffix, supplierId, projectId);
        teamFilters.add(deliveryFollowUpTeamFilterDTO1);
        // Arrange data for testing with just supplier id matching
        DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO2 = getDTOForSupplierTest(ppSuffix, supplierId, projectId);
        teamFilters.add(deliveryFollowUpTeamFilterDTO2);

        for (DeliveryFollowUpTeamFilterDTO dftf : teamFilters) {
            commonServices.addDeliveryFollowUpTeamFilter(dftf, deliveryFollowUpTeam.getDeliveryFollowUpTeamOid());
        }

        for (DeliveryFollowUpTeam delFollowUpTeam : teams) {
            if (delFollowUpTeam.getName().equalsIgnoreCase("HAG")) {
                deliveryFollowUpTeam = delFollowUpTeam;
                break;
            }
        }

        DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTOForProject = getDTOForProjectTypeTest(ppSuffix, supplierId, projectId);
        commonServices.addDeliveryFollowUpTeamFilter(deliveryFollowUpTeamFilterDTOForProject, deliveryFollowUpTeam.getDeliveryFollowUpTeamOid());

        // Act
        String deliveryControllerUserID = commonServices.matchDeliveryController(deliveryFollowUpTeam, "", "", "788");

        // Assert
        Assert.assertEquals("mark", deliveryControllerUserID);
    }

    /**
     * Test case for matching delivery Controller for ProjectType and projectId(7889) and other values as empty are passed and the rules are as below: 1st rule
     * - SupplierID (543) Suffix(519) assigned to "Grace" DCUserId, 2nd rule is Suffix(519) assigned to "gaynor" DCUserId , 3rd rule - SupplierId(543) assigned
     * to "kerstin" DCuserId 4th rule - ProjectID(788) is assigned to "mark" expected result is "defaultDcUserID" as we try to match the ProjectId rule for
     * Project followUp type team and no projectId matches
     * 
     * @throws GloriaApplicationException
     */
    @Test
    public void testMatchDeliveryControllerCase10() throws GloriaApplicationException {
        // Arrange
        List<DeliveryFollowUpTeam> teams = commonServices.findAllDeliveryFollowUpTeam();
        DeliveryFollowUpTeam deliveryFollowUpTeam = null;
        for (DeliveryFollowUpTeam delFollowUpTeam : teams) {
            if (delFollowUpTeam.getName().equalsIgnoreCase("CUR")) {
                deliveryFollowUpTeam = delFollowUpTeam;
                break;
            }
        }

        String ppSuffix = "";
        String supplierId = "";
        String projectId = "";
        List<DeliveryFollowUpTeamFilterDTO> teamFilters = new ArrayList<DeliveryFollowUpTeamFilterDTO>();

        // Arrange data for testing with just suffix matching
        DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO1 = getDTOForSuffixTest(ppSuffix, supplierId, projectId);
        teamFilters.add(deliveryFollowUpTeamFilterDTO1);
        // Arrange data for testing with just supplier id matching
        DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO2 = getDTOForSupplierTest(ppSuffix, supplierId, projectId);
        teamFilters.add(deliveryFollowUpTeamFilterDTO2);

        for (DeliveryFollowUpTeamFilterDTO dftf : teamFilters) {
            commonServices.addDeliveryFollowUpTeamFilter(dftf, deliveryFollowUpTeam.getDeliveryFollowUpTeamOid());
        }

        for (DeliveryFollowUpTeam delFollowUpTeam : teams) {
            if (delFollowUpTeam.getName().equalsIgnoreCase("HAG")) {
                deliveryFollowUpTeam = delFollowUpTeam;
                break;
            }
        }

        DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTOForProject = getDTOForProjectTypeTest(ppSuffix, supplierId, projectId);
        commonServices.addDeliveryFollowUpTeamFilter(deliveryFollowUpTeamFilterDTOForProject, deliveryFollowUpTeam.getDeliveryFollowUpTeamOid());

        // Act
        String deliveryControllerUserID = commonServices.matchDeliveryController(deliveryFollowUpTeam, "", "", "7889");

        // Assert
        Assert.assertEquals(deliveryFollowUpTeam.getDefaultDCUserId(), deliveryControllerUserID);
    }

    private DeliveryFollowUpTeamFilterDTO getDTOForProjectTypeTest(String ppSuffix, String supplierId, String projectId) {
        DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO = new DeliveryFollowUpTeamFilterDTO();
        deliveryFollowUpTeamFilterDTO.setDeliveryControllerUserId("mark");
        deliveryFollowUpTeamFilterDTO.setSupplierId("");
        deliveryFollowUpTeamFilterDTO.setProjectId("788");
        deliveryFollowUpTeamFilterDTO.setSuffix("543");
        return deliveryFollowUpTeamFilterDTO;
    }

    private DeliveryFollowUpTeamFilterDTO getDTOForSupplierTest(String ppSuffix, String supplierId, String projectId) {
        DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO = new DeliveryFollowUpTeamFilterDTO();
        deliveryFollowUpTeamFilterDTO.setDeliveryControllerUserId("kerstin");
        deliveryFollowUpTeamFilterDTO.setSupplierId("543");
        deliveryFollowUpTeamFilterDTO.setProjectId("");
        deliveryFollowUpTeamFilterDTO.setSuffix("");
        return deliveryFollowUpTeamFilterDTO;
    }

    private DeliveryFollowUpTeamFilterDTO getDTOForSuffixTest(String ppSuffix, String supplierId, String projectId) {
        DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO = new DeliveryFollowUpTeamFilterDTO();
        deliveryFollowUpTeamFilterDTO.setDeliveryControllerUserId("gaynor");
        deliveryFollowUpTeamFilterDTO.setSuffix("519");
        deliveryFollowUpTeamFilterDTO.setSupplierId("");
        deliveryFollowUpTeamFilterDTO.setProjectId("");
        return deliveryFollowUpTeamFilterDTO;
    }

    private DeliveryFollowUpTeamFilterDTO getDTOForSuffixWithSupplierTest(String ppSuffix, String supplierId, String projectId) {
        DeliveryFollowUpTeamFilterDTO deliveryFollowUpTeamFilterDTO = new DeliveryFollowUpTeamFilterDTO();
        deliveryFollowUpTeamFilterDTO.setDeliveryControllerUserId("Grace");
        deliveryFollowUpTeamFilterDTO.setSuffix("519");
        deliveryFollowUpTeamFilterDTO.setSupplierId("543");
        deliveryFollowUpTeamFilterDTO.setProjectId("");
        return deliveryFollowUpTeamFilterDTO;
    }

    @Test
    public void testGetDeliveryFollowUpTeamFilters() throws GloriaApplicationException {
        // Arrange
        String loggedInUserIdFollowUpName = "BLR";
        DeliveryFollowUpTeamDTO deliveryFollowUpTeamDTO = commonServices.getDeliveryFollowupTeam(loggedInUserIdFollowUpName);

        DeliveryFollowUpTeamFilterDTO deliveryFollowUpFDto = new DeliveryFollowUpTeamFilterDTO();
        deliveryFollowUpFDto.setSupplierId("1231");
        deliveryFollowUpFDto.setSuffix("129");
        deliveryFollowUpFDto.setDeliveryControllerUserId("1989");
        commonServices.addDeliveryFollowUpTeamFilter(deliveryFollowUpFDto, deliveryFollowUpTeamDTO.getId());
        
        PageObject pageObject = new PageObject();
        pageObject.setResultsPerPage(100);

        // Act
        pageObject = commonServices.getDeliveryFollowUpTeamFilters(pageObject, deliveryFollowUpTeamDTO.getId());

        // Assert
        Assert.assertNotNull(pageObject.getGridContents());
    }
   
   
    
    @Test
    public void testFindWbsElementsByCompanyCodeAndProjectId(){
        //Arrange
        List<WbsElement> wbsElements = wbsElementRepo.findAll();        
        WbsElement wbsElement = wbsElements.get(0);
        wbsElement.setProjectId("W60-6010");
        wbsElement.setWbs("abc");
        wbsElementRepo.save(wbsElement);
       
        
        String companyCode = "SE26";
        
        PageObject pageObject = new PageObject();
        Map<String, String> predicates = new HashMap<String, String>();
        predicates.put("code", "abc");
        pageObject.setPredicates(predicates);
        pageObject.setCount(100);
        
        //Act
        pageObject = commonServices.findWbsElementsByCompanyCodeAndProjectId(companyCode, "W60-6010", "abc", pageObject);
        
        //Assert
        Assert.assertNotNull(pageObject);
        
        List<PageResults> pageResults = pageObject.getGridContents();
        for (PageResults pageResult : pageResults) {
            WbsElementDTO wbsElementDTO = (WbsElementDTO) pageResult;
            if(wbsElement.getWbsElementOid() == wbsElementDTO.getId()){
                WbsElement foundWbsElement = wbsElementRepo.findById(wbsElementDTO.getId());
                Assert.assertEquals(wbsElement.getProjectId(), foundWbsElement.getProjectId());
                Assert.assertEquals("SE26", foundWbsElement.getCompanyCode().getCode());
            }
        }
    }
    
    @Test
    public void testFindCostCentersByCompanyCode(){
        //Arrange
        CompanyCode companyCode = companycodeRepo.findCompanyCodeByCode("US10");
        String name = "2147483647";
        CostCenter costCenter = new CostCenter();
        costCenter.setCostCenter(name);
        costCenter.setCompanyCode(companyCode);
        costCenter.setEffectiveStartDate(DateUtil.getCurrentUTCDateTime());
        costCenterRepo.save(costCenter);
        
        PageObject pageObject = new PageObject();
        pageObject.setCount(100);

        // Act
        pageObject = commonServices.findCostCentersByCompanyCode("US10", "", pageObject);
        
        //Assert
        Assert.assertNotNull(pageObject);
        Assert.assertTrue(pageObject.getGridContents().size() > 0);
    }
    
    @Test
    public void testFindCostCentersByCompanyCodeCheckInvalidDate(){
        //Arrange
        PageObject pageObject = new PageObject();
        pageObject.setCount(100);

        // Act
        pageObject = commonServices.findCostCentersByCompanyCode("BR03", "", pageObject);
        
        //Assert
        List<PageResults> gridContents = pageObject.getGridContents();
        for (PageResults costCenterDTO : gridContents) {
            Assert.assertFalse("Found invalid cost center", ((CostCenterDTO)costCenterDTO).getCostCenter().equals("1230000721"));
        }
    }    
    
    @Test
    public void testFindGLAccountsByCompanyCode(){
        //Arrange
        String companyCode = "US10";
        PageObject pageObject = new PageObject();
        pageObject.setCount(100);
        //Act
        pageObject = commonServices.findGlAccountsByCompanyCode(companyCode, "", pageObject);
        
        //Assert
        Assert.assertNotNull(pageObject);
        Assert.assertEquals(12, pageObject.getGridContents().size());
    }
    
    @Test
    public void testFindProjects(){
        // Arrange
        List<WbsElement> wbsElements = wbsElementRepo.findAll();
        WbsElement wbsElement = wbsElements.get(0);
        wbsElement.setProjectId("W60-6010");
        wbsElement.setWbs("abc");
        wbsElementRepo.save(wbsElement);

        PageObject pageObject = new PageObject();
        pageObject.setCount(10);

        // Act
        List<ProjectDTO> result = commonServices.getProjectsByCompanyCode(pageObject, wbsElement.getCompanyCode().getCode(), "w60*");
        
        //Assert
        Assert.assertNotNull(result);
        Assert.assertTrue(result.size() > 0);
    }
    
    @Test
    public void testFindProjectsMaxResult(){
        // Arrange
        PageObject pageObject = new PageObject();
        pageObject.setResultsPerPage(5);

        // Act
        List<ProjectDTO> result = commonServices.getProjectsByCompanyCode(pageObject, "SE26", "W*");
        
        //Assert
        Assert.assertNotNull(result);
        Assert.assertTrue(result.size() == 5);
    }
    
    @Test
    public void testGetSites(){
        //Arrange
        List<String> siteIds = new ArrayList<String>();
        siteIds.add("664");
        siteIds.add("1001");
        
        //Act
        List<SiteDTO> sites = commonServices.getSite(siteIds);
        
        //Assert
        Assert.assertNotNull(sites);
        Assert.assertEquals(2, sites.size());
        Assert.assertEquals("SE26", sites.get(0).getCompanyCode());
        
        
    }
    
    @Test
    public void testLogXmlMessage() throws IOException{
        //Arrange
        String message = IOUtil.getStringFromClasspath(INITDATA_SITE_XML);
        XmlLogEventDTO xmlLogEventDTO = new XmlLogEventDTO("GPS", GloriaParams.XML_MESSAGE_LOG_RECEIVING, "CarryOverGateway", "messageId",
                                                           compress(message));
        
        //Act
        commonServices.logXMLMessage(xmlLogEventDTO);
    }
    
    @Test(expected = GloriaApplicationException.class)
    public void testAddDeliveryFollowUpTeamFilterForReqdDCUSerValidation() throws GloriaApplicationException{
     // Arrange
        String loggedInUserIdFollowUpName = "BLR";
        DeliveryFollowUpTeamDTO deliveryFollowUpTeamDTO = commonServices.getDeliveryFollowupTeam(loggedInUserIdFollowUpName);

        DeliveryFollowUpTeamFilterDTO deliveryFollowUpFDto = new DeliveryFollowUpTeamFilterDTO();
        deliveryFollowUpFDto.setSupplierId("1231");
        deliveryFollowUpFDto.setSuffix("129");
        
        //Act
        commonServices.addDeliveryFollowUpTeamFilter(deliveryFollowUpFDto, deliveryFollowUpTeamDTO.getId());
    }
    
    @Test(expected = GloriaApplicationException.class)
    public void testAddDeliveryFollowUpTeamFilterForReqdSuppIdOrSuffix() throws GloriaApplicationException{
     // Arrange
        String loggedInUserIdFollowUpName = "BLR";
        DeliveryFollowUpTeamDTO deliveryFollowUpTeamDTO = commonServices.getDeliveryFollowupTeam(loggedInUserIdFollowUpName);

        DeliveryFollowUpTeamFilterDTO deliveryFollowUpFDto = new DeliveryFollowUpTeamFilterDTO();
        deliveryFollowUpFDto.setDeliveryControllerUserId("1989");
        
        //Act
        commonServices.addDeliveryFollowUpTeamFilter(deliveryFollowUpFDto, deliveryFollowUpTeamDTO.getId());
    }
    
    @Test(expected = GloriaApplicationException.class)
    public void testAddDeliveryFollowUpTeamFilterForReqdProjIdOrDCUser() throws GloriaApplicationException{
     // Arrange
        String loggedInUserIdFollowUpName = "HAG";
        DeliveryFollowUpTeamDTO deliveryFollowUpTeamDTO = commonServices.getDeliveryFollowupTeam(loggedInUserIdFollowUpName);

        DeliveryFollowUpTeamFilterDTO deliveryFollowUpFDto = new DeliveryFollowUpTeamFilterDTO();
        deliveryFollowUpFDto.setDeliveryControllerUserId("1989");
        
        //Act
        commonServices.addDeliveryFollowUpTeamFilter(deliveryFollowUpFDto, deliveryFollowUpTeamDTO.getId());
    }
    
    @Test(expected = GloriaApplicationException.class)
    public void testAddDeliveryFollowUpTeamFilterForUniqueSupplierTypeValidation() throws GloriaApplicationException{
     // Arrange
        String loggedInUserIdFollowUpName = "BLR";
        DeliveryFollowUpTeamDTO deliveryFollowUpTeamDTO = commonServices.getDeliveryFollowupTeam(loggedInUserIdFollowUpName);

        DeliveryFollowUpTeamFilterDTO deliveryFollowUpFDto = new DeliveryFollowUpTeamFilterDTO();
        deliveryFollowUpFDto.setSupplierId("1231");
        deliveryFollowUpFDto.setSuffix("129");
        deliveryFollowUpFDto.setDeliveryControllerUserId("1989");
        commonServices.addDeliveryFollowUpTeamFilter(deliveryFollowUpFDto, deliveryFollowUpTeamDTO.getId());
        
        //Act
        commonServices.addDeliveryFollowUpTeamFilter(deliveryFollowUpFDto, deliveryFollowUpTeamDTO.getId());
    }
    
    @Test(expected = GloriaApplicationException.class)
    public void testAddDeliveryFollowUpTeamFilterForUniqueProjectTypeValidation() throws GloriaApplicationException{
     // Arrange
        String loggedInUserIdFollowUpName = "HAG";
        DeliveryFollowUpTeamDTO deliveryFollowUpTeamDTO = commonServices.getDeliveryFollowupTeam(loggedInUserIdFollowUpName);

        DeliveryFollowUpTeamFilterDTO deliveryFollowUpFDto = new DeliveryFollowUpTeamFilterDTO();
        deliveryFollowUpFDto.setProjectId("Pro1");
        deliveryFollowUpFDto.setDeliveryControllerUserId("1989");
        commonServices.addDeliveryFollowUpTeamFilter(deliveryFollowUpFDto, deliveryFollowUpTeamDTO.getId());
        
        //Act
        commonServices.addDeliveryFollowUpTeamFilter(deliveryFollowUpFDto, deliveryFollowUpTeamDTO.getId());
    }
    
    @Test(expected=GloriaApplicationException.class)
    public void testUpdateDeliveryFollowupTeamFilterValidation1() throws GloriaApplicationException {

        // Arrange
        String loggedInUserIdFollowUpName = "BLR";
        DeliveryFollowUpTeamDTO deliveryFollowUpTeamDTO = commonServices.getDeliveryFollowupTeam(loggedInUserIdFollowUpName);
        List<DeliveryFollowUpTeamFilter> dFollowUpTFs = commonServices.getDeliveryFollowUpTeamFilters(deliveryFollowUpTeamDTO.getId());
        List<DeliveryFollowUpTeamFilterDTO> dFollowUpTFDtos = CommonHelper.transformAsDeliveryFollowUpTeamFilterDTO(dFollowUpTFs);
        if (dFollowUpTFDtos.isEmpty()) {
            DeliveryFollowUpTeamFilterDTO deliveryFollowUpFDto = new DeliveryFollowUpTeamFilterDTO();
            deliveryFollowUpFDto.setSupplierId("1231");
            deliveryFollowUpFDto.setSuffix("129");
            deliveryFollowUpFDto.setDeliveryControllerUserId("1989");
            commonServices.addDeliveryFollowUpTeamFilter(deliveryFollowUpFDto, deliveryFollowUpTeamDTO.getId());
        }

        dFollowUpTFs = commonServices.getDeliveryFollowUpTeamFilters(deliveryFollowUpTeamDTO.getId());
        dFollowUpTFDtos = CommonHelper.transformAsDeliveryFollowUpTeamFilterDTO(dFollowUpTFs);
        dFollowUpTFDtos.get(0).setSuffix("");
        dFollowUpTFDtos.get(0).setSupplierId("");

        // Act
        commonServices.updateDeliveryFollowupTeamFilter(dFollowUpTFDtos.get(0));

    }
    
    @Test(expected=GloriaApplicationException.class)
    public void testUpdateDeliveryFollowupTeamFilterValidation2() throws GloriaApplicationException {

        // Arrange
        String loggedInUserIdFollowUpName = "BLR";
        DeliveryFollowUpTeamDTO deliveryFollowUpTeamDTO = commonServices.getDeliveryFollowupTeam(loggedInUserIdFollowUpName);
        List<DeliveryFollowUpTeamFilter> dFollowUpTFs = commonServices.getDeliveryFollowUpTeamFilters(deliveryFollowUpTeamDTO.getId());
        List<DeliveryFollowUpTeamFilterDTO> dFollowUpTFDtos = CommonHelper.transformAsDeliveryFollowUpTeamFilterDTO(dFollowUpTFs);
        if (dFollowUpTFDtos.isEmpty()) {
            DeliveryFollowUpTeamFilterDTO deliveryFollowUpFDto = new DeliveryFollowUpTeamFilterDTO();
            deliveryFollowUpFDto.setSupplierId("1231");
            deliveryFollowUpFDto.setSuffix("129");
            deliveryFollowUpFDto.setDeliveryControllerUserId("1989");
            commonServices.addDeliveryFollowUpTeamFilter(deliveryFollowUpFDto, deliveryFollowUpTeamDTO.getId());
        }

        dFollowUpTFs = commonServices.getDeliveryFollowUpTeamFilters(deliveryFollowUpTeamDTO.getId());
        dFollowUpTFDtos = CommonHelper.transformAsDeliveryFollowUpTeamFilterDTO(dFollowUpTFs);
        dFollowUpTFDtos.get(0).setSuffix("128");
        dFollowUpTFDtos.get(0).setSupplierId("1232");


        DeliveryFollowUpTeamFilterDTO deliveryFollowUpFDto = new DeliveryFollowUpTeamFilterDTO();
        deliveryFollowUpFDto.setSupplierId("1232");
        deliveryFollowUpFDto.setSuffix("128");
        deliveryFollowUpFDto.setDeliveryControllerUserId("1989");
        commonServices.addDeliveryFollowUpTeamFilter(deliveryFollowUpFDto, deliveryFollowUpTeamDTO.getId());
        List<DeliveryFollowUpTeamFilter> dFollowUpTFs2 = commonServices.getDeliveryFollowUpTeamFilters(deliveryFollowUpTeamDTO.getId());
        for (DeliveryFollowUpTeamFilter deliveryFollowUpTeamFilter : dFollowUpTFs2) {
            if(deliveryFollowUpTeamFilter.getSuffix().equals("128") && deliveryFollowUpTeamFilter.getSupplierId().equals("1232")){
                Assert.assertTrue(true);
            }
        }
        // Act
        commonServices.updateDeliveryFollowupTeamFilter(dFollowUpTFDtos.get(0));

    }
    
    @Test(expected=GloriaApplicationException.class)
    public void testUpdateDeliveryFollowupTeamFilterValidation3() throws GloriaApplicationException {

        // Arrange
        String loggedInUserIdFollowUpName = "HAG";
        DeliveryFollowUpTeamDTO deliveryFollowUpTeamDTO = commonServices.getDeliveryFollowupTeam(loggedInUserIdFollowUpName);
        List<DeliveryFollowUpTeamFilter> dFollowUpTFs = commonServices.getDeliveryFollowUpTeamFilters(deliveryFollowUpTeamDTO.getId());
        List<DeliveryFollowUpTeamFilterDTO> dFollowUpTFDtos = CommonHelper.transformAsDeliveryFollowUpTeamFilterDTO(dFollowUpTFs);
        if (dFollowUpTFDtos.isEmpty()) {
            DeliveryFollowUpTeamFilterDTO deliveryFollowUpFDto = new DeliveryFollowUpTeamFilterDTO();
            deliveryFollowUpFDto.setProjectId("111");
            deliveryFollowUpFDto.setDeliveryControllerUserId("1989");
            commonServices.addDeliveryFollowUpTeamFilter(deliveryFollowUpFDto, deliveryFollowUpTeamDTO.getId());
        }

        dFollowUpTFs = commonServices.getDeliveryFollowUpTeamFilters(deliveryFollowUpTeamDTO.getId());
        dFollowUpTFDtos = CommonHelper.transformAsDeliveryFollowUpTeamFilterDTO(dFollowUpTFs);
        dFollowUpTFDtos.get(0).setProjectId("");

        // Act
        commonServices.updateDeliveryFollowupTeamFilter(dFollowUpTFDtos.get(0));

    }
    
    @Test(expected=GloriaApplicationException.class)
    public void testUpdateDeliveryFollowupTeamFilterValidation4() throws GloriaApplicationException {

        // Arrange
        String loggedInUserIdFollowUpName = "HAG";
        DeliveryFollowUpTeamDTO deliveryFollowUpTeamDTO = commonServices.getDeliveryFollowupTeam(loggedInUserIdFollowUpName);
        List<DeliveryFollowUpTeamFilter> dFollowUpTFs = commonServices.getDeliveryFollowUpTeamFilters(deliveryFollowUpTeamDTO.getId());
        List<DeliveryFollowUpTeamFilterDTO> dFollowUpTFDtos = CommonHelper.transformAsDeliveryFollowUpTeamFilterDTO(dFollowUpTFs);
        if (dFollowUpTFDtos.isEmpty()) {
            DeliveryFollowUpTeamFilterDTO deliveryFollowUpFDto = new DeliveryFollowUpTeamFilterDTO();
            deliveryFollowUpFDto.setProjectId("111");
            deliveryFollowUpFDto.setDeliveryControllerUserId("1989");
            commonServices.addDeliveryFollowUpTeamFilter(deliveryFollowUpFDto, deliveryFollowUpTeamDTO.getId());
        }

        dFollowUpTFs = commonServices.getDeliveryFollowUpTeamFilters(deliveryFollowUpTeamDTO.getId());
        dFollowUpTFDtos = CommonHelper.transformAsDeliveryFollowUpTeamFilterDTO(dFollowUpTFs);
        dFollowUpTFDtos.get(0).setProjectId("1232");

        DeliveryFollowUpTeamFilterDTO deliveryFollowUpFDto = new DeliveryFollowUpTeamFilterDTO();
        deliveryFollowUpFDto.setProjectId("1232");
        deliveryFollowUpFDto.setDeliveryControllerUserId("1989");
        commonServices.addDeliveryFollowUpTeamFilter(deliveryFollowUpFDto, deliveryFollowUpTeamDTO.getId());
        // Act
        commonServices.updateDeliveryFollowupTeamFilter(dFollowUpTFDtos.get(0));

    }
    
    @Test
    public void testSyncWBSElement() throws Exception {
        // Arrange
        SyncWBSElementDTO syncWBSElementDTO = wbsElementStorageTransformer.transformStoredWBSElement(IOUtil.getStringFromClasspath(INITDATA_WBSELEMENT_XML));
        WbsElementDTO wbsElementDTO = syncWBSElementDTO.getWbsElementDTO().get(0);
        String companyCode = wbsElementDTO.getCompanyCode();
        
        syncWBSElementDTO.getWbsElementDTO().remove(wbsElementDTO);
        
        // Act
        commonServices.syncWBSElement(syncWBSElementDTO);

        // Assert
        Assert.assertEquals(true, wbsElementRepo.findWbsElementByCompanyCode(companyCode).size() == syncWBSElementDTO.getWbsElementDTO().size());
        Assert.assertEquals(null, wbsElementRepo.findWbsElementByCompanyCodeAndProjectId(wbsElementDTO.getCompanyCode(), wbsElementDTO.getProjectId(), wbsElementDTO.getWbs()));
    }

    @Test
    public void testSyncCostCenterEnglishDescription() throws Exception {
        // Arrange
        
        // Act
        
        // Assert
        PageObject pageObject = new PageObject();
        pageObject.setCount(100);
        Map<String, String> predicates = new HashMap<String, String>();
        predicates.put("costCenter", "1230000701");
        pageObject.setPredicates(predicates);
        pageObject = costCenterRepo.findCostCentersByCompanyCode("BR03", null, pageObject);
        
        Assert.assertEquals("English Desc Short", ((CostCenterDTO)pageObject.getGridContents().get(0)).getDescriptionShort());
    }
    
    @Test
    public void testSyncCostCenterLoad() throws IOException {
        // Arrange
        SyncCostCenterDTO syncCostCenterDTO = costCenterStorageTransformer.transformStoredCostCenter(IOUtil.getStringFromClasspath(COSTCENTER_XML_LOAD));

        // Act
        commonServices.syncCostCenter(syncCostCenterDTO);

        // Assert
        PageObject pageObject = new PageObject();
        pageObject.setCount(100);
        Map<String, String> predicates = new HashMap<String, String>();
        predicates.put("costCenter", "99999999");
        pageObject.setPredicates(predicates);
        pageObject = costCenterRepo.findCostCentersByCompanyCode("BR03", null, pageObject);
        Assert.assertTrue(pageObject.getGridContents().size() == 1);
        
        pageObject = new PageObject();
        pageObject.setCount(100);
        pageObject = costCenterRepo.findCostCentersByCompanyCode("BR03", null, pageObject);
        Assert.assertTrue(pageObject.getGridContents().size() == syncCostCenterDTO.getCostCenterItems().size());
    }
    
    @Test
    public void testSyncCostCenterCreateUpdate() throws IOException {
        // Arrange
        PageObject pageObject = new PageObject();
        pageObject.setResultsPerPage(1000);
        pageObject = costCenterRepo.findCostCentersByCompanyCode("BR03", null, pageObject);
        int totalCostCenterBeforeUpdate = pageObject.getGridContents().size();

        SyncCostCenterDTO syncCostCenterDTO = costCenterStorageTransformer.transformStoredCostCenter(IOUtil.getStringFromClasspath(COSTCENTER_XML_UPDATES));

        // Act
        commonServices.syncCostCenter(syncCostCenterDTO);

        // Assert
        // Create
        pageObject = new PageObject();
        pageObject.setResultsPerPage(1000);
        Assert.assertEquals(totalCostCenterBeforeUpdate + 1, costCenterRepo.findCostCentersByCompanyCode("BR03", null, pageObject).getGridContents().size());

        pageObject = new PageObject();
        Map<String, String> predicates = new HashMap<String, String>();
        predicates.put("costCenter", "88888888");
        pageObject.setPredicates(predicates);
        pageObject = costCenterRepo.findCostCentersByCompanyCode("BR03", null, pageObject);
        Assert.assertTrue(pageObject.getGridContents().size() == 1);

        // Update
        CostCenter updatedCostCenter = costCenterRepo.findCostCenterByCompanyCodeAndStartDate("BR03", "1230001110", DateUtil.getSqlDate(2015, 0, 21));
        Assert.assertNotNull(updatedCostCenter);
        Assert.assertEquals("Updated_Short", updatedCostCenter.getDescriptionShort());
        Assert.assertEquals("Updated_Long", updatedCostCenter.getDescriptionLong());
        Assert.assertEquals("someId", updatedCostCenter.getPersonResponisbleId());
        Assert.assertEquals("Other, Simon", updatedCostCenter.getPersonalResponsibleName());
        Assert.assertEquals(DateUtil.getSqlDate(2015, 0, 15), updatedCostCenter.getEffectiveStartDate());
        Assert.assertEquals(DateUtil.getSqlDate(2020, 11, 31), updatedCostCenter.getEffectiveEndDate());
        Assert.assertEquals(1, updatedCostCenter.getVersion());

        // Delete
        Assert.assertNull(costCenterRepo.findCostCenterByCompanyCodeAndStartDate("BR03", "1230000905", DateUtil.getSqlDate(2012, 0, 1)));
    }
    
    @Test
    public void testSyncCarryOverAdd() throws Exception {
        // Arrange
        SyncPurchaseOrderCarryOverDTO syncCarryOverDTO = new SyncPurchaseOrderCarryOverDTO();
        syncCarryOverDTO.setAction(CarryOverActionType.ADD.toString());
        List<CarryOverItemDTO> carryOverItemDTOs = new ArrayList<CarryOverItemDTO>();
        syncCarryOverDTO.setCarryOverItemDTOs(carryOverItemDTOs);
        CarryOverItemDTO carryOverItemDTO = new CarryOverItemDTO();
        carryOverItemDTO.setCustomerId("911");
        carryOverItemDTO.setSupplierId("112");
        carryOverItemDTO.setSupplierName("Sup_Name1");
        carryOverItemDTO.setPartNumber("420");
        carryOverItemDTOs.add(carryOverItemDTO);
        
        CarryOver anotherCarryOver = new CarryOver();
        anotherCarryOver.setCustomerId("911");
        anotherCarryOver.setSupplierId("112");
        anotherCarryOver.setSupplierName("Sup_Name");
        anotherCarryOver.setPartNumber("420");
        carryOverRepo.save(anotherCarryOver);        

        // Act
        commonServices.syncCarryOver(syncCarryOverDTO);
        
        // Assert
        List<CarryOver> carryOvers = carryOverRepo.findAll();
        Assert.assertNotNull(carryOvers);
        Assert.assertTrue(carryOvers.size() == 1);
        
//        CarryOver carryOver = carryOverRepo.findUniqueCarryOver(customerId, supplierId, partNumber, orderId)CarryOver(carryOverItemDTO.getCustomerId(), carryOverItemDTO.getSupplierId(), carryOverItemDTO.getPartNumber(), carryOverItemDTO.getOrderId());
//        Assert.assertNotNull(carryOver);
//        Assert.assertEquals("Sup_Name1", carryOver.getSupplierName());
    }
    
    @Test
    public void testSyncCarryOverChange() throws Exception {
        // Arrange
        SyncPurchaseOrderCarryOverDTO syncCarryOverDTO = new SyncPurchaseOrderCarryOverDTO();
        syncCarryOverDTO.setAction(CarryOverActionType.ADD.toString());
        List<CarryOverItemDTO> carryOverItemDTOs = new ArrayList<CarryOverItemDTO>();
        syncCarryOverDTO.setCarryOverItemDTOs(carryOverItemDTOs);
        CarryOverItemDTO carryOverItemDTO = new CarryOverItemDTO();
        carryOverItemDTO.setCustomerId("911");
        carryOverItemDTO.setSupplierId("112");
        carryOverItemDTO.setSupplierName("Sup_Name_changed");
        carryOverItemDTO.setPartNumber("420");
        carryOverItemDTOs.add(carryOverItemDTO);
        
        CarryOver anotherCarryOver = new CarryOver();
        anotherCarryOver.setCustomerId("911");
          anotherCarryOver.setSupplierId("112");
        anotherCarryOver.setSupplierName("Sup_Name");
        anotherCarryOver.setPartNumber("420");
        carryOverRepo.save(anotherCarryOver);        

        // Act
        commonServices.syncCarryOver(syncCarryOverDTO);
        
        // Assert
        List<CarryOver> carryOvers = carryOverRepo.findAll();
        Assert.assertNotNull(carryOvers);
        Assert.assertTrue(carryOvers.size() == 1);
      
    }
    
    @Test
    public void testSyncCarryOverDelete() throws Exception {
     // Arrange
        SyncPurchaseOrderCarryOverDTO syncCarryOverDTO = new SyncPurchaseOrderCarryOverDTO();
        syncCarryOverDTO.setAction(CarryOverActionType.DELETE.toString());
        List<CarryOverItemDTO> carryOverItemDTOs = new ArrayList<CarryOverItemDTO>();
        syncCarryOverDTO.setCarryOverItemDTOs(carryOverItemDTOs);
        CarryOverItemDTO carryOverItemDTO = new CarryOverItemDTO();
        carryOverItemDTO.setCustomerId("911");
        carryOverItemDTO.setSupplierId("112");
        carryOverItemDTO.setSupplierName("Sup_Name_changed");
        carryOverItemDTO.setPartNumber("420");        
        carryOverItemDTOs.add(carryOverItemDTO);
        
        CarryOver anotherCarryOver = new CarryOver();
        anotherCarryOver.setCustomerId("911");
        anotherCarryOver.setSupplierId("112");
        anotherCarryOver.setSupplierName("Sup_Name");
        anotherCarryOver.setPartNumber("420");
        carryOverRepo.save(anotherCarryOver); 
        
        CarryOver anotherCarryOver1 = new CarryOver();
        anotherCarryOver1.setCustomerId("9119");
        anotherCarryOver1.setSupplierId("112");
        anotherCarryOver1.setSupplierName("Sup_Name2");
        anotherCarryOver1.setPartNumber("420");
        carryOverRepo.save(anotherCarryOver1); 

        // Act
        commonServices.syncCarryOver(syncCarryOverDTO);
        
        // Assert
        List<CarryOver> carryOvers = carryOverRepo.findAll();
        Assert.assertNotNull(carryOvers);
        Assert.assertTrue(carryOvers.size() == 2);
    }
    
    @Test
    public void testGetMaterialLineTraceabilitys() {
        //Arrange
        Traceability traceability = new Traceability(TraceabilityType.MATERIAL_LINE);
        traceability.setAction("Created");
        traceability.setMaterialOID(1);
        traceability.setMaterialLineOId(1);
        traceability.setActionDetail("test action");
        traceabilityRepository.save(traceability);
        
        //Act
        List<Traceability> traceabilities = commonServices.getMaterialLineTraceabilitys(1);
        
        //Assert
        Assert.assertNotNull(traceabilities);
        Assert.assertEquals(1, traceabilities.size());
    }
    
    @Test
    public void testGetOrderLineTraceabilitys() {
        //Arrange
        Traceability traceability = new Traceability(TraceabilityType.DELIVERY_CONTROLLER);
        traceability.setAction("Created");
        traceability.setOrderLineOID(1);
        traceability.setActionDetail("test action");
        traceabilityRepository.save(traceability);
        
        //Act
        List<Traceability> traceabilities = commonServices.getOrderLineTraceabilitys(1);
        
        //Assert
        Assert.assertNotNull(traceabilities);
        Assert.assertEquals(1, traceabilities.size());
    }
    
    @Test
    public void testGetSupplierCounterPartsByCompanyCode() {
        //Arrange
        String companyCode = "SE27";
        
        //Act
        List<SupplierCounterPart> supplierCounterParts = commonServices.getSupplierCounterPartsByCompanyCode(companyCode);
        
        //Assert
        Assert.assertNotNull(supplierCounterParts);
        Assert.assertEquals(7, supplierCounterParts.size());
    }
    
    @Test
    @Ignore
    public void testLDAPUserData() throws IOException, NamingException, GloriaApplicationException {
        utilServices.getLDAPUserData("a035515");
    }

    @Test
    public void testGLO6658IsWbsValid() {
        //Arrange
        
        //Act
        
        //Assert
        Assert.assertFalse(commonServices.isWbsValid("FLE10-00092-04-04"));
        Assert.assertTrue(commonServices.isWbsValid("FLG10-09188-03-03"));
        Assert.assertTrue(commonServices.isWbsValid("FLT10"));
        Assert.assertTrue(commonServices.isWbsValid("FLW10-01125-00-A1"));
    }
}