package com.volvo.gloria.procurematerial.util.migration.b.beans;

import static com.volvo.gloria.procurematerial.util.migration.c.WarehouseMigrationHelper.transformToDeliveryNoteEntity;
import static com.volvo.gloria.procurematerial.util.migration.c.WarehouseMigrationHelper.transformToEntity;

import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import com.volvo.gloria.procurematerial.b.MaterialServices;
import com.volvo.gloria.procurematerial.d.entities.DeliveryNoteLine;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;
import com.volvo.gloria.procurematerial.repositories.b.ProcureLineRepository;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.procurematerial.util.migration.c.dto.WarehouseMigrationDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.warehouse.repositories.b.WarehouseRepository;
import com.volvo.jvs.runtime.platform.ContainerManaged;

@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class PerformWarehouseMigrationServiceBean {

    @Inject
    private MaterialHeaderRepository requestHeaderRepository;

    @Inject
    private WarehouseRepository warehouseRepository;

    @Inject
    private MaterialServices materialServices;

    @Inject
    private ProcureLineRepository procureLineRepository;
    
    @Inject
    private DeliveryNoteRepository deliveryNoteRepository;
    
    public void performWarehouseMigration(List<WarehouseMigrationDTO> warehouseMigrationDTOs) throws GloriaApplicationException {
        for (WarehouseMigrationDTO warehouseMigrationDTO : warehouseMigrationDTOs) {
            Material material = transformToEntity(warehouseMigrationDTO);
            requestHeaderRepository.save(material.getAdd());
            MaterialLine materialLine = material.getMaterialLine().get(0);            
           
            MaterialHeader materialHeader = material.getMaterialHeader();
            if (materialHeader != null) {
                requestHeaderRepository.save(materialHeader);
            }

            procureLineRepository.save(material.getProcureLine());
            requestHeaderRepository.addMaterial(material);
            materialLine.setMaterial(material);
            materialLine.setMaterialOwner(material);

            
            DeliveryNoteLine deliveryNoteLine =  transformToDeliveryNoteEntity(warehouseMigrationDTO);
            deliveryNoteRepository.save(deliveryNoteLine.getDeliveryNote());
            materialLine.setDeliveryNoteLine(deliveryNoteLine);
            deliveryNoteLine.setMaterialLineOID(materialLine.getMaterialLineOID());
            
            // Add Placement 
            materialServices.createPlacement(warehouseMigrationDTO.getBinLocationOid(), materialLine);
            requestHeaderRepository.updateMaterialLine(materialLine);

            requestHeaderRepository.flush();
            procureLineRepository.flush();
            warehouseRepository.flush();
            warehouseMigrationDTO.setMigrated(true);
        }
    }

}
