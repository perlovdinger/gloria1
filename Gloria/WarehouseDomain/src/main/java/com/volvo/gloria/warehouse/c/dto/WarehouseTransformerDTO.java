package com.volvo.gloria.warehouse.c.dto;

import java.util.List;

import com.volvo.gloria.warehouse.c.Setup;

/**
 * DTO class for warehouseTransformer.
 */
public class WarehouseTransformerDTO {

    private String siteId;
    private Setup setUp;
    private boolean qiSupported;
    private List<StorageRoomTransformerDTO> storageRoomDTOs;
    
    public String getSiteId() {
        return siteId;
    }
    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }
    public Setup getSetUp() {
        return setUp;
    }
    public void setSetUp(Setup setUp) {
        this.setUp = setUp;
    }
    public List<StorageRoomTransformerDTO> getStorageRoomDTOs() {
        return storageRoomDTOs;
    }
    public void setStorageRoomDTOs(List<StorageRoomTransformerDTO> storageRoomDTOs) {
        this.storageRoomDTOs = storageRoomDTOs;
    }
    public boolean isQiSupported() {
        return qiSupported;
    }
    public void setQiSupported(boolean qiSupported) {
        this.qiSupported = qiSupported;
    }
    
    
}
