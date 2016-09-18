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
package com.volvo.gloria.warehouse.c.dto;

import java.io.Serializable;
import java.util.List;

/**
 * DTO class holding material storage information, ie in which bin location a material will be stored.
 */
public class ToStoreDTO implements Serializable {
    private static final long serialVersionUID = 2248004218157291372L;

    private Long id;
    private String partNo;
    private String partDesc;
    private String partStage;
    private String partAlias;
    private String partIssue;
    private Integer receivedQty;
    private Integer storedQty;
    private Long storageRoom;
    private Long binLocation;
    private Long binLocSgtn;
    private String assignTo;
    private String mtrlLineStatus;
    private String suggestStorageRoom;
    private Long materialLineId;
    private List<BinLocationDTO> binLocationList;
    private List<StorageRoomDTO> storageRoomList;
    private String directSend;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPartNo() {
        return partNo;
    }

    public void setPartNo(String partNo) {
        this.partNo = partNo;
    }

    public String getPartDesc() {
        return partDesc;
    }

    public void setPartDesc(String partDesc) {
        this.partDesc = partDesc;
    }

    public String getPartAlias() {
        return partAlias;
    }

    public void setPartAlias(String partAlias) {
        this.partAlias = partAlias;
    }

    public String getPartStage() {
        return partStage;
    }

    public void setPartStage(String partStage) {
        this.partStage = partStage;
    }

    public String getPartIssue() {
        return partIssue;
    }

    public void setPartIssue(String partIssue) {
        this.partIssue = partIssue;
    }

    public Integer getReceivedQty() {
        return receivedQty;
    }

    public void setReceivedQty(Integer receivedQty) {
        this.receivedQty = receivedQty;
    }

    public Integer getStoredQty() {
        return storedQty;
    }

    public void setStoredQty(Integer storedQty) {
        this.storedQty = storedQty;
    }

    public Long getStorageRoom() {
        return storageRoom;
    }

    public void setStorageRoom(Long storageRoom) {
        this.storageRoom = storageRoom;
    }

    public Long getBinLocation() {
        return binLocation;
    }

    public void setBinLocation(Long binLocation) {
        this.binLocation = binLocation;
    }

    public Long getBinLocSgtn() {
        return binLocSgtn;
    }

    public void setBinLocSgtn(Long binLocSgtn) {
        this.binLocSgtn = binLocSgtn;
    }

    public String getAssignTo() {
        return assignTo;
    }

    public void setAssignTo(String assignTo) {
        this.assignTo = assignTo;
    }

    public String getMtrlLineStatus() {
        return mtrlLineStatus;
    }

    public void setMtrlLineStatus(String mtrlLineStatus) {
        this.mtrlLineStatus = mtrlLineStatus;
    }

    public String getSuggestStorageRoom() {
        return suggestStorageRoom;
    }

    public void setSuggestStorageRoom(String suggestStorageRoom) {
        this.suggestStorageRoom = suggestStorageRoom;
    }

    public Long getMaterialLineId() {
        return materialLineId;
    }

    public void setMaterialLineId(Long materialLineId) {
        this.materialLineId = materialLineId;
    }

    public List<BinLocationDTO> getBinLocationList() {
        return binLocationList;
    }

    public void setBinLocationList(List<BinLocationDTO> binLocationList) {
        this.binLocationList = binLocationList;
    }

    public List<StorageRoomDTO> getStorageRoomList() {
        return storageRoomList;
    }

    public void setStorageRoomList(List<StorageRoomDTO> storageRoomList) {
        this.storageRoomList = storageRoomList;
    }

    public String getDirectSend() {
        return directSend;
    }

    public void setDirectSend(String directSend) {
        this.directSend = directSend;
    }
}
