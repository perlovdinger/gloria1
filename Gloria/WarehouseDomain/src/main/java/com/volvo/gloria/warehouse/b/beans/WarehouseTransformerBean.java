package com.volvo.gloria.warehouse.b.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.util.GloriaSystemException;
import com.volvo.gloria.util.xml.XmlConstants;
import com.volvo.gloria.util.xml.XmlTransformer;
import com.volvo.gloria.warehouse.b.WarehouseTransformer;
import com.volvo.gloria.warehouse.c.Allocation;
import com.volvo.gloria.warehouse.c.BaySides;
import com.volvo.gloria.warehouse.c.Setup;
import com.volvo.gloria.warehouse.c.ZoneType;
import com.volvo.gloria.warehouse.c.dto.AisleRackRowTransformerDTO;
import com.volvo.gloria.warehouse.c.dto.BaySettingTransformerDTO;
import com.volvo.gloria.warehouse.c.dto.BinLocationTransformerDTO;
import com.volvo.gloria.warehouse.c.dto.StorageRoomTransformerDTO;
import com.volvo.gloria.warehouse.c.dto.WarehouseTransformerDTO;
import com.volvo.gloria.warehouse.c.dto.ZoneTransformerDTO;
import com.volvo.group.init.warehouse._1_0.AisleRackRow;
import com.volvo.group.init.warehouse._1_0.BaySetting;
import com.volvo.group.init.warehouse._1_0.Warehouses;
import com.volvo.jvs.runtime.platform.ContainerManaged;

/**
 * Warehouse message transformer service implementation.
 * 
 */
@ContainerManaged
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class WarehouseTransformerBean extends XmlTransformer implements WarehouseTransformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(WarehouseTransformerBean.class);

    public WarehouseTransformerBean() throws IOException {
        super(XmlConstants.SchemaFullPath.INIT_WAREHOUSE, XmlConstants.PackageName.INIT_WAREHOUSE);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<WarehouseTransformerDTO> transformWarehouse(String xmlContent) {
        try {
            return (List<WarehouseTransformerDTO>) transform(xmlContent);
        } catch (JAXBException e) {
            // Throw an exception to roll-back the transaction.
            LOGGER.error("Unable to unmarshall the received message to a WarehouseDTOs object, message will be discarded. Exception is:", e);
            throw new GloriaSystemException(e, "Unable to unmarshall the received message to a WarehouseDTOs object, message will be discarded");
        }
    }

    @Override
    protected Object transformFromJAXBToDTO(Object jaxbOject) {
        Warehouses warehouses = (Warehouses) jaxbOject;
        List<WarehouseTransformerDTO> wareHouseDTOList = new ArrayList<WarehouseTransformerDTO>();
        for (com.volvo.group.init.warehouse._1_0.Warehouse warehouseInitData : warehouses.getWarehouse()) {
            WarehouseTransformerDTO warehouseTransformerDTO = createWareHouseTransformDTO(warehouseInitData);
            wareHouseDTOList.add(warehouseTransformerDTO);
        }
        return wareHouseDTOList;
    }

    private WarehouseTransformerDTO createWareHouseTransformDTO(com.volvo.group.init.warehouse._1_0.Warehouse warehouseInitData) {
        WarehouseTransformerDTO warehouseTransformerDTO = new WarehouseTransformerDTO();
        warehouseTransformerDTO.setSiteId(warehouseInitData.getSiteId());
        warehouseTransformerDTO.setSetUp(Setup.valueOf(warehouseInitData.getSetUp()));
        warehouseTransformerDTO.setQiSupported(warehouseInitData.isQiSupported());
        List<StorageRoomTransformerDTO> storageRoomDTOs = new ArrayList<StorageRoomTransformerDTO>();
        for (com.volvo.group.init.warehouse._1_0.StorageRoom storageRoomInitData : warehouseInitData.getStorageRoom()) {
            StorageRoomTransformerDTO storageRoomDTO = createStorageRoomDTO(storageRoomInitData);
            storageRoomDTOs.add(storageRoomDTO);
        }
        warehouseTransformerDTO.setStorageRoomDTOs(storageRoomDTOs);
        return warehouseTransformerDTO;
    }

    private StorageRoomTransformerDTO createStorageRoomDTO(com.volvo.group.init.warehouse._1_0.StorageRoom storageRoomInitData) {
        StorageRoomTransformerDTO storageRoomDTO = new StorageRoomTransformerDTO();
        storageRoomDTO.setCode(storageRoomInitData.getCode());
        storageRoomDTO.setName(storageRoomInitData.getName());
        storageRoomDTO.setDescription(storageRoomInitData.getDescription());
        List<ZoneTransformerDTO> zoneTransformerDTOs = new ArrayList<ZoneTransformerDTO>();
        for (com.volvo.group.init.warehouse._1_0.Zon zonInitData : storageRoomInitData.getZon()) {
            ZoneTransformerDTO zoneDTO = createZoneTransformDTO(zonInitData);
            zoneTransformerDTOs.add(zoneDTO);
        }
        storageRoomDTO.setZoneDTOs(zoneTransformerDTOs);
        return storageRoomDTO;
    }

    private ZoneTransformerDTO createZoneTransformDTO(com.volvo.group.init.warehouse._1_0.Zon zonInitData) {
        ZoneTransformerDTO zoneDTO = new ZoneTransformerDTO();
        zoneDTO.setCode(zonInitData.getCode());
        zoneDTO.setName(zonInitData.getName());
        zoneDTO.setDescription(zonInitData.getDescription());
        zoneDTO.setType(ZoneType.valueOf(zonInitData.getType()));
        List<AisleRackRowTransformerDTO> aisleRackRowTransformerDTOs = new ArrayList<AisleRackRowTransformerDTO>();
        for (com.volvo.group.init.warehouse._1_0.AisleRackRow aisleRackRowInitData : zonInitData.getAisleRackRow()) {
            AisleRackRowTransformerDTO aisleRackRowTransformerDTO = createAisleRackRowTransformDTO(aisleRackRowInitData);
            aisleRackRowTransformerDTOs.add(aisleRackRowTransformerDTO);
        }
        zoneDTO.setAisleTransformerDTOs(aisleRackRowTransformerDTOs);
        List<BinLocationTransformerDTO> binLocationTransformerDTOs = new ArrayList<BinLocationTransformerDTO>();
        for (com.volvo.group.init.warehouse._1_0.BinLocation binLocationInitData : zonInitData.getBinLocation()) {
            BinLocationTransformerDTO binLocationTransformerDTO = createBinLocationTransformerDTO(binLocationInitData);
            binLocationTransformerDTOs.add(binLocationTransformerDTO);
        }
        zoneDTO.setBinLocationTransformerDTOs(binLocationTransformerDTOs);
        return zoneDTO;
    }

    private AisleRackRowTransformerDTO createAisleRackRowTransformDTO(AisleRackRow aisleRackRowInitData) {
        AisleRackRowTransformerDTO aisleRackRowTransformerDTO = new AisleRackRowTransformerDTO();
        if (aisleRackRowInitData.getSetUp() != null && aisleRackRowInitData.getSetUp().equals(Setup.AISLE.name())
                && aisleRackRowInitData.getBaySides() != null) {
            aisleRackRowTransformerDTO.setBaySides(BaySides.valueOf(aisleRackRowInitData.getBaySides()));
        }
        aisleRackRowTransformerDTO.setCode(aisleRackRowInitData.getCode());
        aisleRackRowTransformerDTO.setNumberOfBay(aisleRackRowInitData.getNumberOfBay());
        aisleRackRowTransformerDTO.setSetUp(Setup.valueOf(aisleRackRowInitData.getSetUp()));
        List<BaySettingTransformerDTO> baySettingTransformerDTOs = new ArrayList<BaySettingTransformerDTO>();
        for (com.volvo.group.init.warehouse._1_0.BaySetting baySetting : aisleRackRowInitData.getBaySetting()) {
            BaySettingTransformerDTO baySettingTransformerDTO = createBaySettingTransformerDTO(baySetting);
            baySettingTransformerDTOs.add(baySettingTransformerDTO);
        }
        aisleRackRowTransformerDTO.setBaySettingTransformerDTOs(baySettingTransformerDTOs);
        return aisleRackRowTransformerDTO;
    }

    private BaySettingTransformerDTO createBaySettingTransformerDTO(BaySetting baySetting) {
        BaySettingTransformerDTO baySettingTransformerDTO = new BaySettingTransformerDTO();
        baySettingTransformerDTO.setBayCode(baySetting.getBayCode());
        baySettingTransformerDTO.setNumberOfLevels(baySetting.getNumberOfLevels());
        baySettingTransformerDTO.setNumberOfPositions(baySetting.getNumberOfPositions());
        return baySettingTransformerDTO;
    }

    private BinLocationTransformerDTO createBinLocationTransformerDTO(com.volvo.group.init.warehouse._1_0.BinLocation binLocationInitData) {
        BinLocationTransformerDTO binLocationTransformerDTO = new BinLocationTransformerDTO();
        if (binLocationInitData.getAllocation() != null) {
            binLocationTransformerDTO.setAllocation(Allocation.valueOf(binLocationInitData.getAllocation()));
        }
        binLocationTransformerDTO.setBay(binLocationInitData.getBayCode());
        binLocationTransformerDTO.setCode(binLocationInitData.getCode());
        binLocationTransformerDTO.setLevel(binLocationInitData.getLevel());
        binLocationTransformerDTO.setPosition(binLocationInitData.getPosition());
        return binLocationTransformerDTO;
    }

    @Override
    protected Object transformFromDTOToJAXB(Object dto) {
        return null;
    }
}
