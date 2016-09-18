package com.volvo.gloria.common.c.dto.reports;

import java.io.Serializable;

public class ReportWarehouseTransactionDTO implements Serializable, Cloneable{
   
    /**
     * 
     */
    private static final long serialVersionUID = -2386438918153856888L;
    private String action;
    private String[] storageRoom;
    private String[] warehouse;

    public ReportWarehouseTransactionDTO clone() {
        try {
            return (ReportWarehouseTransactionDTO) super.clone();
        } catch (CloneNotSupportedException e) {        
            throw new RuntimeException();
        }
    }

    public String[] getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String[] warehouse) {
        this.warehouse = warehouse;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String[] getStorageRoom() {
        return storageRoom;
    }

    public void setStorageRoom(String[] storageRoom) {
        this.storageRoom = storageRoom;
    }
}
