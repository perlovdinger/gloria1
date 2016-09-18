package com.volvo.gloria.carryoverrelatedtestcases;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.c.KolaDomain;
import com.volvo.gloria.common.d.entities.CarryOver;
import com.volvo.gloria.common.d.entities.Currency;
import com.volvo.gloria.common.d.entities.PartAliasMapping;
import com.volvo.gloria.common.repositories.b.CarryOverRepository;
import com.volvo.gloria.common.repositories.b.PartAliasMappingRepository;
import com.volvo.gloria.procurematerial.b.ProcurementServices;
import com.volvo.gloria.procurematerial.b.beans.ProcurementServicesBean;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeaderVersion;
import com.volvo.gloria.procurematerial.d.entities.MaterialPartAlias;
import com.volvo.gloria.procurematerial.d.type.material.MaterialType;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

public class PocureResponsibilityTest extends AbstractTransactionalTestCase {

    // does real calls
    @InjectMocks
    private ProcurementServices procurementServices = new ProcurementServicesBean();

    private CommonServices commonServices = Mockito.mock(CommonServices.class);
    private CarryOverRepository carryOverRepo = Mockito.mock(CarryOverRepository.class);
    private PartAliasMappingRepository partAliasMappingRepo = Mockito.mock(PartAliasMappingRepository.class);

    private MaterialHeader materialHeader = Mockito.mock(MaterialHeader.class);
    private MaterialHeaderVersion materialHeaderVersion = Mockito.mock(MaterialHeaderVersion.class);
    private Material material = Mockito.mock(Material.class);
    private MaterialPartAlias materialPartAlias = Mockito.mock(MaterialPartAlias.class);
    private PartAliasMapping partAliasMapping = Mockito.mock(PartAliasMapping.class);

    @Before
    public void setUpTestData() throws IOException {

        // inject dependent mocks to ProcurementServices
        MockitoAnnotations.initMocks(this);

        Mockito.when(material.getMaterialHeader()).thenReturn(materialHeader);
        Mockito.when(material.getMaterialHeader().getAccepted()).thenReturn(materialHeaderVersion);
        Mockito.when(material.getMaterialType()).thenReturn(MaterialType.USAGE);
        Mockito.when(material.getPartNumber()).thenReturn("22119009");

        Mockito.when(materialPartAlias.getMaterial()).thenReturn(material);
        Mockito.when(materialPartAlias.getKolaDomain()).thenReturn(KolaDomain.RVI);
        Mockito.when(materialPartAlias.getPartNumber()).thenReturn("7422119009");

        Mockito.when(partAliasMapping.getGpsQualifier()).thenReturn("I");

        Mockito.when(commonServices.findAllCurreny()).thenReturn(new ArrayList<Currency>());
    }

    // for non volvo part
    @Test
    public void testResponsibility_CARRYOVEREXIST_NO_FOR_NON_VOLVO_PART() {
        // arrange
        Mockito.when(material.getPartAffiliation()).thenReturn("X");
        Mockito.when(material.getPartVersion()).thenReturn("P02");
        // act
        material = procurementServices.updateMaterialWithCarryOverInfo(material, null, null);
        // assert
        Assert.assertFalse(material.isCarryOverExist());
        Assert.assertFalse(material.isCarryOverExistAndMatched());
    }

    // for volvo part with version start other than 'P'
    @Test
    public void testResponsibility_CARRYOVEREXIST_NO_FOR_VOLVO_PART_WITH_PARTVERSION_NOT_STAGE_P() {
        // arrange
        Mockito.when(material.getPartAffiliation()).thenReturn("V");
        Mockito.when(material.getPartVersion()).thenReturn("X02");
        // act
        material = procurementServices.updateMaterialWithCarryOverInfo(material, null, null);
        // assert
        Assert.assertFalse(material.isCarryOverExist());
        Assert.assertFalse(material.isCarryOverExistAndMatched());
    }

    // for volvo part with out matching carry overs
    @Test
    public void testResponsibility_CARRYOVEREXIST_NO() {
        // arrange
        Mockito.when(material.getPartAffiliation()).thenReturn("V");
        Mockito.when(material.getPartVersion()).thenReturn("P02");
        Mockito.when(material.getPartAlias()).thenReturn(new ArrayList<MaterialPartAlias>());
        Mockito.when(carryOverRepo.findCarryOverByPartNumberPartversionAndCustomerId(material.getPartNumber(), material.getPartVersion(),
                                                                                     material.getPartAffiliation(),
                                                                                     materialHeaderVersion.getOutboundLocationId(), null)).thenReturn(null);

        // act
        material = procurementServices.updateMaterialWithCarryOverInfo(material, null, null);
        // assert
        Assert.assertFalse(material.isCarryOverExist());
        Assert.assertFalse(material.isCarryOverExistAndMatched());
    }

    @Test
    public void testResponsibility_CARRYOVEREXIST_EXIST_FOR_PARTNUMBER() {
        // arrange
        Mockito.when(material.getPartAffiliation()).thenReturn("V");
        Mockito.when(material.getPartVersion()).thenReturn("P02");
        Mockito.when(material.getPartAlias()).thenReturn(new ArrayList<MaterialPartAlias>());
        Mockito.when(carryOverRepo.findCarryOverByPartNumberPartversionAndCustomerId(material.getPartNumber(), material.getPartVersion(),
                                                                                     material.getPartAffiliation(),
                                                                                     materialHeaderVersion.getOutboundLocationId(), null))
               .thenAnswer(new Answer<List<CarryOver>>() {
                   @Override
                   public List<CarryOver> answer(InvocationOnMock invocation) throws Throwable {
                       return Arrays.asList(new CarryOver());
                   }
               });

        // act
        material = procurementServices.updateMaterialWithCarryOverInfo(material, null, null);
        // assert
        Mockito.verify(material, Mockito.atLeastOnce()).setCarryOverExist(true);
        Assert.assertFalse(material.isCarryOverExistAndMatched());
    }

    @Test
    public void testResponsibility_CARRYOVEREXIST_EXISTANDMATCH_PARTNUMBER() {
        // arrange
        Mockito.when(material.getPartAffiliation()).thenReturn("V");
        Mockito.when(material.getPartVersion()).thenReturn("P02");
        Mockito.when(material.getPartAlias()).thenReturn(new ArrayList<MaterialPartAlias>());

        // build site info
        Mockito.when(materialHeaderVersion.getOutboundLocationId()).thenReturn("1001");
        Mockito.when(materialHeaderVersion.getOutboundLocationType()).thenReturn("PLANT");

        // to mock EXIST
        Mockito.when(carryOverRepo.findCarryOverByPartNumberPartversionAndCustomerId(material.getPartNumber(), material.getPartVersion(),
                                                                                     material.getPartAffiliation(),
                                                                                     null, null))
               .thenAnswer(new Answer<List<CarryOver>>() {
                   @Override
                   public List<CarryOver> answer(InvocationOnMock invocation) throws Throwable {
                       return Arrays.asList(new CarryOver());
                   }
               });

        // to mock EXIST_MATCH
        Mockito.when(carryOverRepo.findCarryOverByPartNumberPartversionAndCustomerId(material.getPartNumber(), material.getPartVersion(),
                                                                                     material.getPartAffiliation(),
                                                                                     materialHeaderVersion.getOutboundLocationId(), null))
               .thenAnswer(new Answer<List<CarryOver>>() {
                   @Override
                   public List<CarryOver> answer(InvocationOnMock invocation) throws Throwable {
                       return Arrays.asList(new CarryOver());
                   }
               });

        // act
        material = procurementServices.updateMaterialWithCarryOverInfo(material, null, null);
        // assert
        Mockito.verify(material, Mockito.atLeastOnce()).setCarryOverExist(true);
        Mockito.verify(material, Mockito.atLeastOnce()).setCarryOverExistAndMatched(true);
    }

    @Test
    public void testResponsibility_CARRYOVEREXIST_EXIST_FOR_ALIASNUMBER() {
        // arrange
        Mockito.when(material.getPartAffiliation()).thenReturn("V");
        Mockito.when(material.getPartVersion()).thenReturn("P02");
        Mockito.when(material.getPartAlias()).thenReturn(Arrays.asList(materialPartAlias));
        Mockito.when(partAliasMappingRepo.getGpsQualifier(materialPartAlias.getKolaDomain().name())).thenReturn(partAliasMapping);

        Mockito.when(carryOverRepo.findCarryOverByPartNumberPartversionAndCustomerId(material.getPartNumber(), material.getPartVersion(),
                                                                                     material.getPartAffiliation(),
                                                                                     materialHeaderVersion.getOutboundLocationId(), null)).thenReturn(null);

        // to mock EXIST
        Mockito.when(carryOverRepo.findCarryOverAlias(materialPartAlias.getPartNumber(), partAliasMapping.getGpsQualifier(), null))
               .then(new Answer<List<CarryOver>>() {
                   @Override
                   public List<CarryOver> answer(InvocationOnMock invocation) throws Throwable {
                       return Arrays.asList(new CarryOver());
                   }
               });

        // to mock EXIST_MATCH
        Mockito.when(carryOverRepo.findCarryOverAlias(materialPartAlias.getPartNumber(), partAliasMapping.getGpsQualifier(),
                                                      materialHeaderVersion.getOutboundLocationId())).then(new Answer<List<CarryOver>>() {
            @Override
            public List<CarryOver> answer(InvocationOnMock invocation) throws Throwable {
                return Arrays.asList(new CarryOver());
            }
        });

        // act
        material = procurementServices.updateMaterialWithCarryOverInfo(material, null, null);
        // assert
        Mockito.verify(material, Mockito.atLeastOnce()).setCarryOverExist(true);
        Assert.assertFalse(material.isCarryOverExistAndMatched());
    }

    @Test
    public void testResponsibility_CARRYOVEREXIST_EXISTANDMATCH_ALIASNUMBER() {
        // arrange
        Mockito.when(material.getPartAffiliation()).thenReturn("V");
        Mockito.when(material.getPartVersion()).thenReturn("P02");
        Mockito.when(material.getPartAlias()).thenReturn(Arrays.asList(materialPartAlias));
        Mockito.when(partAliasMappingRepo.getGpsQualifier(materialPartAlias.getKolaDomain().name())).thenReturn(partAliasMapping);

        // build site info
        Mockito.when(materialHeaderVersion.getOutboundLocationId()).thenReturn("1001");
        Mockito.when(materialHeaderVersion.getOutboundLocationType()).thenReturn("PLANT");

        Mockito.when(carryOverRepo.findCarryOverByPartNumberPartversionAndCustomerId(material.getPartNumber(), material.getPartVersion(),
                                                                                     material.getPartAffiliation(),
                                                                                     materialHeaderVersion.getOutboundLocationId(), null)).thenReturn(null);

        // to mock EXIST
        Mockito.when(carryOverRepo.findCarryOverAlias(materialPartAlias.getPartNumber(), partAliasMapping.getGpsQualifier(), null))
               .then(new Answer<List<CarryOver>>() {
                   @Override
                   public List<CarryOver> answer(InvocationOnMock invocation) throws Throwable {
                       return Arrays.asList(new CarryOver());
                   }
               });

        // to mock EXIST_MATCH
        Mockito.when(carryOverRepo.findCarryOverAlias(materialPartAlias.getPartNumber(), partAliasMapping.getGpsQualifier(),
                                                      materialHeaderVersion.getOutboundLocationId())).then(new Answer<List<CarryOver>>() {
            @Override
            public List<CarryOver> answer(InvocationOnMock invocation) throws Throwable {
                return Arrays.asList(new CarryOver());
            }
        });

        // act
        material = procurementServices.updateMaterialWithCarryOverInfo(material, null, null);
        // assert
        Mockito.verify(material, Mockito.atLeastOnce()).setCarryOverExist(true);
        Mockito.verify(material, Mockito.atLeastOnce()).setCarryOverExistAndMatched(true);
    }

}
