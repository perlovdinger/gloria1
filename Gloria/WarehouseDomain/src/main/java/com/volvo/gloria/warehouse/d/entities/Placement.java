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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.volvo.gloria.util.persistence.GenericEntity;

/**
 * Entity class for Placement.
 */
@Entity
@Table(name = "PLACEMENT")
public class Placement implements GenericEntity<Long>, Serializable {
    private static final long serialVersionUID = 7006379252261212738L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PLACEMENT_OID")
    private Long placementOid;

    @Version
    private long version;

    private Long materialLineOID;
    
    private long quantity;

    @ManyToOne
    @JoinColumn(name = "BIN_LOCATION_OID")
    @Column(name = "BIN_LOCATION_OID")
    private BinLocation binLocation;

    @ManyToOne
    @JoinColumn(name = "BINLOCATION_BALANCE_OID")
    private BinlocationBalance binlocationBalance;
    
    public Long getPlacementOid() {
        return placementOid;
    }

    public void setPlacementOid(Long placementOid) {
        this.placementOid = placementOid;
    }

    public BinLocation getBinLocation() {
        return binLocation;
    }

    public void setBinLocation(BinLocation binLocation) {
        this.binLocation = binLocation;
    }

    public Long getMaterialLineOID() {
        return materialLineOID;
    }

    public void setMaterialLineOID(Long materialLineOID) {
        this.materialLineOID = materialLineOID;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public long getVersion() {
        return version;
    }

    public BinlocationBalance getBinlocationBalance() {
        return binlocationBalance;
    }
    
    public void setBinlocationBalance(BinlocationBalance binlocationBalance) {
        this.binlocationBalance = binlocationBalance;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    @Override
    public Long getId() {
        return placementOid;
    }

}
