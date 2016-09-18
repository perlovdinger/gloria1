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
package com.volvo.gloria.warehouse.d.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.volvo.gloria.util.persistence.GenericEntity;
import com.volvo.gloria.warehouse.c.Setup;

/**
 * This is the Warehouse Entity.
 */
@Entity
@Table(name = "WAREHOUSE")
public class Warehouse implements GenericEntity<Long>, Serializable {
    private static final long serialVersionUID = -4633549122118535252L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WAREHOUSE_OID")
    private Long warehouseOid;

    @Version
    private long version;

    private String siteId;

    @Enumerated(EnumType.STRING)
    private Setup setUp;

    private boolean qiSupported;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "warehouse")
    private List<StorageRoom> storageRooms = new ArrayList<StorageRoom>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "warehouse")
    private List<QualityInspectionPart> qualityInspectionParts = new ArrayList<QualityInspectionPart>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "warehouse")
    private List<QualityInspectionSupplier> qualityInspectionSuppliers = new ArrayList<QualityInspectionSupplier>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "warehouse")
    private List<QualityInspectionProject> qualityInspectionProjects = new ArrayList<QualityInspectionProject>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "warehouse")
    private List<Printer> printers = new ArrayList<Printer>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "warehouse")
    private List<BinlocationBalance> binlocationBalances = new ArrayList<BinlocationBalance>();

    public Long getWarehouseOid() {
        return warehouseOid;
    }

    public void setWarehouseOid(Long warehouseOid) {
        this.warehouseOid = warehouseOid;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

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

    public List<StorageRoom> getStorageRooms() {
        return storageRooms;
    }

    public void setStorageRooms(List<StorageRoom> storageRooms) {
        this.storageRooms = storageRooms;
    }

    public List<QualityInspectionPart> getQualityInspectionParts() {
        return qualityInspectionParts;
    }

    public void setQualityInspectionParts(List<QualityInspectionPart> qualityInspectionParts) {
        this.qualityInspectionParts = qualityInspectionParts;
    }

    public List<QualityInspectionSupplier> getQualityInspectionSuppliers() {
        return qualityInspectionSuppliers;
    }

    public void setQualityInspectionSuppliers(List<QualityInspectionSupplier> qualityInspectionSuppliers) {
        this.qualityInspectionSuppliers = qualityInspectionSuppliers;
    }

    public List<QualityInspectionProject> getQualityInspectionProjects() {
        return qualityInspectionProjects;
    }

    public void setQualityInspectionProjects(List<QualityInspectionProject> qualityInspectionProjects) {
        this.qualityInspectionProjects = qualityInspectionProjects;
    }

    public boolean isQiSupported() {
        return qiSupported;
    }

    public void setQiSupported(boolean qiSupported) {
        this.qiSupported = qiSupported;
    }

    public List<Printer> getPrinters() {
        return printers;
    }

    public void setPrinters(List<Printer> printers) {
        this.printers = printers;
    }

    public List<BinlocationBalance> getBinlocationBalances() {
        return binlocationBalances;
    }

    public void setBinlocationBalances(List<BinlocationBalance> binlocationBalances) {
        this.binlocationBalances = binlocationBalances;
    }

    @Override
    public Long getId() {
        return warehouseOid;
    }
}
