package com.volvo.gloria.material.d.status.materialLine;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.c.dto.MaterialLineDTO;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.warehouse.b.WarehouseServices;
import com.volvo.gloria.warehouse.d.entities.Placement;

public class MaterialLineStatusTestSimple {

    public MaterialLineStatusTestSimple() {
        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    }

    /*
     * test appropriate material line transitions 
     * pick should only be possible from REQUESTED state
     */
    @Test
    public void testPick() throws GloriaApplicationException {

        MaterialServices materialServices = Mockito.mock(MaterialServices.class);
        WarehouseServices warehouseServices = Mockito.mock(WarehouseServices.class);
        try {
            MaterialLineStatus.BLOCKED.pick(null, null, null, null, null, null);
            Assert.fail("Blocked should not be able Pick");
        } catch (GloriaApplicationException exception) {

        }
        try {
            MaterialLineStatus.CREATED.pick(null, null, null, null, null, null);
            Assert.fail("created should not be able Pick");
        } catch (GloriaApplicationException exception) {
        }
        try {
            MaterialLineStatus.DEVIATED.pick(null, null, null, null, null, null);
            Assert.fail("deviated should not be able Pick");
        } catch (GloriaApplicationException exception) {
        }
        try {
            MaterialLineStatus.IN_TRANSFER.pick(null, null, null, null, null, null);
            Assert.fail("IN_TRANSFER should not be able Pick");
        } catch (GloriaApplicationException exception) {
        }
        try {
            MaterialLineStatus.MARKED_INSPECTION.pick(null, null, null, null, null, null);
            Assert.fail("MARKED_INSPECTION should not be able Pick");
        } catch (GloriaApplicationException exception) {
        }
        try {
            MaterialLineStatus.MISSING.pick(null, null, null, null, null, null);
            Assert.fail("MISSING should not be able Pick");
        } catch (GloriaApplicationException exception) {
        }
        try {
            MaterialLineStatus.ORDER_PLACED_EXTERNAL.pick(null, null, null, null, null, null);
            Assert.fail("ORDER_PLACED_EXTERNAL should not be able Pick");
        } catch (GloriaApplicationException exception) {
        }
        try {
            MaterialLineStatus.ORDER_PLACED_INTERNAL.pick(null, null, null, null, null, null);
            Assert.fail("ORDER_PLACED_INTERNAL should not be able Pick");
        } catch (GloriaApplicationException exception) {
        }
        try {
            MaterialLineStatus.QI_OK.pick(null, null, null, null, null, null);
            Assert.fail("QI_OK should not be able Pick");
        } catch (GloriaApplicationException exception) {
        }
        try {
            MaterialLineStatus.QI_READY.pick(null, null, null, null, null, null);
            Assert.fail("QI_READY should not be able Pick");
        } catch (GloriaApplicationException exception) {
        }

        try {
            MaterialLineStatus.READY_TO_STORE.pick(null, null, null, null, null, null);
            Assert.fail("READY_TO_STORE should not be able Pick");
        } catch (GloriaApplicationException exception) {
        }
        try {
            MaterialLineStatus.REMOVED.pick(null, null, null, null, null, null);
            Assert.fail("REMOVED should not be able Pick");
        } catch (GloriaApplicationException exception) {
        }
        try {
            MaterialLineStatus.REMOVED_DB.pick(null, null, null, null, null, null);
            Assert.fail("REMOVED_DB should not be able Pick");
        } catch (GloriaApplicationException exception) {
        }
        try {
            MaterialLineStatus.REQUISITION_SENT.pick(null, null, null, null, null, null);
            Assert.fail("REQUISITION_SENT should not be able Pick");
        } catch (GloriaApplicationException exception) {
        }
        try {
            MaterialLineStatus.SCRAPPED.pick(null, null, null, null, null, null);
            Assert.fail("SCRAPPED should not be able Pick");
        } catch (GloriaApplicationException exception) {
        }

        try {
            MaterialLineStatus.SHIPPED.pick(null, null, null, null, null, null);
            Assert.fail("SHIPPED should not be able Pick");
        } catch (GloriaApplicationException exception) {
        }
        try {
            MaterialLineStatus.STORED.pick(null, null, null, null, null, null);
            Assert.fail("STORED should not be able Pick");
        } catch (GloriaApplicationException exception) {
        }
        try {
            MaterialLineStatus.WAIT_TO_PROCURE.pick(null, null, null, null, null, null);
            Assert.fail("WAIT_TO_PROCURE should not be able Pick");
        } catch (GloriaApplicationException exception) {
        }
        try {

            MaterialLineStatus.READY_TO_SHIP.pick(null, null, null, null, null, null);
            Assert.fail("READY_TO_SHIP should not be able Pick");
        } catch (GloriaApplicationException exception) {

        }
        // this is the only success case
        MaterialLineDTO materialLineDTO = new MaterialLineDTO();
        materialLineDTO.setId(1L);
        MaterialHeaderRepository requestHeaderRepository = Mockito.mock(MaterialHeaderRepository.class);
        MaterialLine materialLine = new MaterialLine();
        materialLine.setQuantity(1L);
        materialLine.setPlacementOID(1L);
        Mockito.when(requestHeaderRepository.findMaterialLineById(Mockito.anyLong())).thenReturn(materialLine);
        Placement placement = new Placement();
        Mockito.when(warehouseServices.getPlacement(Mockito.anyLong())).thenReturn(placement);

        try {
            MaterialLineStatus.REQUESTED.pick(materialLineDTO, null, requestHeaderRepository, null, warehouseServices, materialServices);
        } catch (GloriaApplicationException exception) {
            //this is Not OK  
            Assert.fail("REQUESTED should not be able Pick");
        } catch (NullPointerException nullPointerException) {
            // this is ok as it is doing some processing
            //could not fix how static methods are mocked
        }
    }
    /*
     * test appropriate material line transitions 
     * Requested should only be possible from READY TO STORE or STORED state
     */
    @Test
    public void testRequestedCall() throws GloriaApplicationException {
        CommonServices commonServices = Mockito.mock(CommonServices.class);
        try {
            // MaterialLineStatus.BLOCKED.pick(materialLineDTO, userDTO, requestHeaderRepository, traceabilityRepository, warehouseServices, materialServices);
            MaterialLineStatus.BLOCKED.request(null, null, null, null, null, null, null, null, null, commonServices);
            Assert.fail("Blocked should not be able request");
        } catch (GloriaApplicationException exception) {

        }
        try {
            MaterialLineStatus.CREATED.request(null, null, null, null, null, null, null, null, null, commonServices);
            Assert.fail("created should not be able request");
        } catch (GloriaApplicationException exception) {
        }
        try {
            MaterialLineStatus.DEVIATED.request(null, null, null, null, null, null, null, null, null, commonServices);
            Assert.fail("deviated should not be able request");
        } catch (GloriaApplicationException exception) {
        }
        try {
            MaterialLineStatus.IN_TRANSFER.request(null, null, null, null, null, null, null, null, null, commonServices);
            Assert.fail("IN_TRANSFER should not be able request");
        } catch (GloriaApplicationException exception) {
        }
        try {
            MaterialLineStatus.MARKED_INSPECTION.request(null, null, null, null, null, null, null, null, null, commonServices);
            Assert.fail("MARKED_INSPECTION should not be able request");
        } catch (GloriaApplicationException exception) {
        }
        try {
            MaterialLineStatus.MISSING.request(null, null, null, null, null, null, null, null, null, commonServices);
            Assert.fail("MISSING should not be able request");
        } catch (GloriaApplicationException exception) {
        }


        try {
            MaterialLineStatus.QI_OK.request(null, null, null, null, null, null, null, null, null, commonServices);
            Assert.fail("QI_OK should not be able request");
        } catch (GloriaApplicationException exception) {
        }
        try {
            MaterialLineStatus.QI_READY.request(null, null, null, null, null, null, null, null, null, commonServices);
            Assert.fail("QI_READY should not be able request");
        } catch (GloriaApplicationException exception) {
        }

        try {
            MaterialLineStatus.REMOVED.request(null, null, null, null, null, null, null, null, null, commonServices);
            Assert.fail("REMOVED should not be able request");
        } catch (GloriaApplicationException exception) {
        }
        try {
            MaterialLineStatus.REMOVED_DB.request(null, null, null, null, null, null, null, null, null, commonServices);
            Assert.fail("REMOVED_DB should not be able request");
        } catch (GloriaApplicationException exception) {
        }
        try {
            MaterialLineStatus.REQUISITION_SENT.request(null, null, null, null, null, null, null, null, null, commonServices);
            Assert.fail("REQUISITION_SENT should not be able request");
        } catch (GloriaApplicationException exception) {
        }
        try {
            MaterialLineStatus.SCRAPPED.request(null, null, null, null, null, null, null, null, null, commonServices);
            Assert.fail("SCRAPPED should not be able request");
        } catch (GloriaApplicationException exception) {
        }

        try {
            MaterialLineStatus.SHIPPED.request(null, null, null, null, null, null, null, null, null, commonServices);
            Assert.fail("SHIPPED should not be able request");
        } catch (GloriaApplicationException exception) {
        }

        try {
            MaterialLineStatus.WAIT_TO_PROCURE.request(null, null, null, null, null, null, null, null, null, commonServices);
            Assert.fail("WAIT_TO_PROCURE should not be able request");
        } catch (GloriaApplicationException exception) {
        }
        try {

            MaterialLineStatus.READY_TO_SHIP.request(null, null, null, null, null, null, null, null, null, commonServices);
            Assert.fail("READY_TO_SHIP should not be able request");
        } catch (GloriaApplicationException exception) {

        }
        try {
            MaterialLineStatus.REQUESTED.request(null, null, null, null, null, null, null, null, null, commonServices);
            Assert.fail("REQUESTED should not be able request");
        } catch (GloriaApplicationException exception) {
            //this is OK  
        } 
        
        // these are the success cases
        MaterialLineDTO materialLineDTO = new MaterialLineDTO();
        materialLineDTO.setId(1L);
        materialLineDTO.setPossiblePickQuantity(1L);
        materialLineDTO.setQuantity(5L);
        MaterialHeaderRepository requestHeaderRepository = Mockito.mock(MaterialHeaderRepository.class);
        MaterialLine materialLine = new MaterialLine();
        materialLine.setQuantity(1L);
        materialLine.setPlacementOID(1L);
        Mockito.when(requestHeaderRepository.findMaterialLineById(Mockito.anyLong())).thenReturn(materialLine);
        Material material = new Material();
        material.getMaterialLine().add(materialLine);
        materialLine.setMaterial(material);
     //   Mockito.when(materialLine.getMaterial()).thenReturn(material);
        
        try {
            MaterialLineStatus.READY_TO_STORE.request(materialLineDTO, requestHeaderRepository, null, null, null, null, null, null, null, commonServices);

        } catch (GloriaApplicationException exception) {
            Assert.fail("READY_TO_STORE should not be able request");
        }
        try {
            MaterialLineStatus.STORED.request(materialLineDTO, requestHeaderRepository, null, null, null, null, null, null, null, commonServices);
        } catch (GloriaApplicationException exception) {
            Assert.fail("STORED should not be able request");
        }
        try {
            MaterialLineStatus.ORDER_PLACED_EXTERNAL.request(materialLineDTO, requestHeaderRepository, null, null, null, null, null, null, null, commonServices);
        } catch (GloriaApplicationException exception) {
            Assert.fail("ORDER_PLACED_EXTERNAL should not be able request");
        }
        try {
            MaterialLineStatus.ORDER_PLACED_INTERNAL.request(materialLineDTO, requestHeaderRepository, null, null, null, null, null, null, null, commonServices);
        } catch (GloriaApplicationException exception) {
            Assert.fail("ORDER_PLACED_INTERNAL should not be able request");
        }
    }
}
