package com.volvo.gloria.procurematerial.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.util.Assert;

import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.procurematerial.c.ProcureResponsibility;
import com.volvo.gloria.procurematerial.c.dto.MaterialGroupDTO;
import com.volvo.gloria.procurematerial.c.dto.ProcureLineDTO;
import com.volvo.gloria.procurematerial.d.entities.FinanceHeader;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeaderVersion;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.status.requestline.MaterialStatus;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.gloria.procurematerial.d.type.request.RequestType;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.c.GloriaParams;

public class ProcureGroupHelperTest {

    @Mock
    private List<MaterialGroupDTO> groupDTOs = null;
    private MaterialGroupDTO materialGroupDTO = null;
    
    @Mock
    private List<Material> materials = null;
    private Material materialOne = null;
    private Material materialTwo = null;
    
    private MaterialHeader materialHeaderOne = null;
    private MaterialHeader materialHeaderTwo = null;
    
    private MaterialHeaderVersion acceptedVersionOne = null;
    private MaterialHeaderVersion acceptedVersionTwo = null;

    private FinanceHeader financeHeader = null;
    MaterialHeaderRepository requestHeaderRepository = null;
    private UserDTO userDTO = null;

    @SuppressWarnings("unchecked")
    public ProcureGroupHelperTest() {
        userDTO = Mockito.mock(UserDTO.class);
        requestHeaderRepository = Mockito.mock(MaterialHeaderRepository.class);
        financeHeader = Mockito.mock(FinanceHeader.class);
        Mockito.when(requestHeaderRepository.findMaterialLineQuantities(Matchers.anyList())).thenReturn(new HashMap<MaterialType, Long>());
        Mockito.when(financeHeader.getProjectId()).thenReturn("W60-2944");
        Mockito.when(financeHeader.getCostCenter()).thenReturn("costCenter1");
        Mockito.when(financeHeader.getGlAccount()).thenReturn("glAccount1");
        Mockito.when(financeHeader.getWbsCode()).thenReturn("wbsCode1");
        Mockito.when(financeHeader.getInternalOrderNoSAP()).thenReturn("internSap1");
        
        materialGroupDTO = Mockito.mock(MaterialGroupDTO.class);
        
        groupDTOs = Mockito.mock(ArrayList.class);        
        Iterator<MaterialGroupDTO> itrMaterialGroupDTO = Mockito.mock(Iterator.class);
        Mockito.when(itrMaterialGroupDTO.hasNext()).thenReturn(true, true, false);
        Mockito.when(itrMaterialGroupDTO.next()).thenReturn(materialGroupDTO);
        Mockito.when(groupDTOs.iterator()).thenReturn(itrMaterialGroupDTO);
        
        materialHeaderOne = Mockito.mock(MaterialHeader.class);
        acceptedVersionOne = Mockito.mock(MaterialHeaderVersion.class);
        materialOne = new Material();    
        
        materialHeaderTwo = Mockito.mock(MaterialHeader.class);
        acceptedVersionTwo = Mockito.mock(MaterialHeaderVersion.class);
        materialTwo = new Material();
        
        materials = Mockito.mock(ArrayList.class);
        Iterator<Material> itrMaterial = Mockito.mock(Iterator.class);
        Mockito.when(itrMaterial.hasNext()).thenReturn(true, true, false);
        Mockito.when(itrMaterial.next()).thenReturn(materialOne, materialTwo);
        Mockito.when(materials.iterator()).thenReturn(itrMaterial);
        
        Mockito.when(materialGroupDTO.getMaterials()).thenReturn(materials);
        
        materialOne.setStatus(MaterialStatus.ADDED);
        materialOne.setMaterialType(MaterialType.USAGE);
        materialOne.setFinanceHeader(financeHeader);
        materialOne.setMaterialHeader(materialHeaderOne);
        Mockito.when(materialHeaderOne.getAccepted()).thenReturn(acceptedVersionOne);
        Mockito.when(materialHeaderOne.getRequestType()).thenReturn(RequestType.SINGLE);
        
        materialTwo.setStatus(MaterialStatus.ADDED);
        materialTwo.setMaterialType(MaterialType.USAGE);
        materialTwo.setFinanceHeader(financeHeader);
        materialTwo.setMaterialHeader(materialHeaderTwo);
        Mockito.when(materialHeaderTwo.getAccepted()).thenReturn(acceptedVersionTwo);
        Mockito.when(materialHeaderTwo.getRequestType()).thenReturn(RequestType.SINGLE);
        
    }

    /**
     * part                     SAME
     * outboundlocation         SAME
     * carryOverExist           TRUE
     * carryOverExistAndMatched TRUE
     * 
     * @throws GloriaApplicationException
     *
     */
    @Test
    public void testDefaultGrouping1() throws GloriaApplicationException {
        // arrange
        Mockito.when(acceptedVersionOne.getOutboundLocationId()).thenReturn("1001");
        Mockito.when(acceptedVersionOne.getOutboundLocationType()).thenReturn("PLANT");
        materialOne.setPartAffiliation(GloriaParams.PART_AFFILIATION_VOLVO);
        materialOne.setCarryOverExist(true);
        materialOne.setCarryOverExistAndMatched(true);
        
        Mockito.when(acceptedVersionTwo.getOutboundLocationId()).thenReturn("1001");
        Mockito.when(acceptedVersionTwo.getOutboundLocationType()).thenReturn("PLANT");
        materialTwo.setPartAffiliation(GloriaParams.PART_AFFILIATION_VOLVO);
        materialTwo.setCarryOverExist(true);
        materialTwo.setCarryOverExistAndMatched(true);
        
        // act
        ProcureGroupHelper groupHelper = new ProcureGroupHelper(groupDTOs, GloriaParams.GROUP_TYPE_DEFAULT);
        List<ProcureLine> procureLines = groupHelper.getProcureLines(null, userDTO,requestHeaderRepository);

        // assert
        Assert.notEmpty(procureLines);
        Assert.isTrue(procureLines.size() == 1);
    } 
    
    /**
     * part                     SAME
     * due to change in the grouping logic the outboundlocation is not the only criteria which groups 
     * material.  The Responisbility determines if outbound location is to be added or not.  Hence changes to this 
     * test cases
     * outboundlocation         NOT SAME
     * carryOverExist           TRUE
     * carryOverExistAndMatched TRUE
     * 
     * @throws GloriaApplicationException
     * 
   */
    @Test
    public void testDefaultGrouping2() throws GloriaApplicationException {
        // arrange
        Mockito.when(acceptedVersionOne.getOutboundLocationId()).thenReturn("1001");
        Mockito.when(acceptedVersionOne.getOutboundLocationType()).thenReturn("PLANT");
        materialOne.setPartAffiliation(GloriaParams.PART_AFFILIATION_VOLVO);
        materialOne.setCarryOverExist(true);
        materialOne.setCarryOverExistAndMatched(true);
        
        Mockito.when(acceptedVersionTwo.getOutboundLocationId()).thenReturn("1622");
        Mockito.when(acceptedVersionTwo.getOutboundLocationType()).thenReturn("PLANT");
        materialTwo.setPartAffiliation(GloriaParams.PART_AFFILIATION_VOLVO);
        materialTwo.setCarryOverExist(true);
        ProcureLine temporaryProcureLine = new ProcureLine();
        ProcureResponsibility responsibility =  ProcureResponsibility.BUILDSITE;
        
        temporaryProcureLine.setResponsibility(responsibility);
        materialTwo.setTemporaryProcureLine(temporaryProcureLine);
        materialTwo.setCarryOverExistAndMatched(true);
        
        // act
        ProcureGroupHelper groupHelper = new ProcureGroupHelper(groupDTOs, GloriaParams.GROUP_TYPE_DEFAULT);
        List<ProcureLine> procureLines = groupHelper.getProcureLines(null, userDTO, requestHeaderRepository);

        // assert
        Assert.notEmpty(procureLines);
        Assert.isTrue(procureLines.size() == 2);
    }
    
    /**
     * part                     SAME
     * outboundlocation         NOT SAME
     * carryOverExist           TRUE
     * carryOverExistAndMatched FALSE
     * 
     * @throws GloriaApplicationException
     * */
    
    @Test
    public void testDefaultGrouping3() throws GloriaApplicationException {
        // arrange
        Mockito.when(acceptedVersionOne.getOutboundLocationId()).thenReturn("1165");
        Mockito.when(acceptedVersionOne.getOutboundLocationType()).thenReturn("PLANT");
        materialOne.setPartAffiliation(GloriaParams.PART_AFFILIATION_VOLVO);
        materialOne.setCarryOverExist(true);
        materialOne.setCarryOverExistAndMatched(false);
        
        Mockito.when(acceptedVersionTwo.getOutboundLocationId()).thenReturn("1722");
        Mockito.when(acceptedVersionTwo.getOutboundLocationType()).thenReturn("WORKSHOP");
        materialTwo.setPartAffiliation(GloriaParams.PART_AFFILIATION_VOLVO);
        materialTwo.setCarryOverExist(true);
        materialTwo.setCarryOverExistAndMatched(false);
        
        // act
        ProcureGroupHelper groupHelper = new ProcureGroupHelper(groupDTOs, GloriaParams.GROUP_TYPE_DEFAULT);
        List<ProcureLine> procureLines = groupHelper.getProcureLines(null, userDTO, requestHeaderRepository);

        // assert
        Assert.notEmpty(procureLines);
        Assert.isTrue(procureLines.size() == 1);
    }  
    
    /**
     * part                     SAME
     * outboundlocation         NOT SAME
     * carryOverExist           TRUE
     * carryOverExistAndMatched FALSE
     * 
     * WITH OUTBOUNDLOCATION ID NULL AND OUTBOUNDLOCATION NAME NULL.
     * 
     * @throws GloriaApplicationException
     * 
     * */
    
    @Test
    public void testDefaultGrouping4() throws GloriaApplicationException {
        // arrange
        Mockito.when(acceptedVersionOne.getOutboundLocationId()).thenReturn("1165");
        Mockito.when(acceptedVersionOne.getOutboundLocationType()).thenReturn("PLANT");
        materialOne.setPartAffiliation(GloriaParams.PART_AFFILIATION_VOLVO);
        materialOne.setCarryOverExist(true);
        materialOne.setCarryOverExistAndMatched(false);
        
        Mockito.when(acceptedVersionTwo.getOutboundLocationId()).thenReturn(null);
        Mockito.when(acceptedVersionTwo.getOutboundLocationType()).thenReturn(null);
        materialTwo.setPartAffiliation(GloriaParams.PART_AFFILIATION_VOLVO);
        materialTwo.setCarryOverExist(true);
        materialTwo.setCarryOverExistAndMatched(false);
        
        // act
        ProcureGroupHelper groupHelper = new ProcureGroupHelper(groupDTOs, GloriaParams.GROUP_TYPE_DEFAULT);
        List<ProcureLine> procureLines = groupHelper.getProcureLines(null, userDTO, requestHeaderRepository);

        // assert
        Assert.notEmpty(procureLines);
        Assert.isTrue(procureLines.size() == 1);
    } 
    /*
     * Given procureLineDTO have HasUnRead false 
     * Then all materials field AddedAfter is set to false 
     */
    @Test
    public void testUpdateMaterialAddedAfter() throws GloriaApplicationException {
        ProcureLineDTO procureLineDTO= new ProcureLineDTO();
        procureLineDTO.setHasUnread(false);
        Material material1 = new Material();
        material1.setAddedAfter(true);
        Material material2 = new Material();
        material2.setAddedAfter(false);
        List<Material> materialList = new ArrayList<Material>();
        materialList.add(material2);
        materialList.add(material1);
        ProcureLine procureLine = new ProcureLine();
        procureLine.setMaterials(materialList);
        ProcureGroupHelper.updateMaterialAddedAfter(procureLineDTO, procureLine);
        Assert.isTrue(!material1.isAddedAfter());
    }
    /*
     * null tests if List is null
     */
    @Test
    public void testUpdateMaterialAddedAfter1() throws GloriaApplicationException {
        ProcureLine procureLine = new ProcureLine();
        procureLine.setMaterials(null);
        ProcureGroupHelper.updateMaterialAddedAfter(null, procureLine);
    }
}