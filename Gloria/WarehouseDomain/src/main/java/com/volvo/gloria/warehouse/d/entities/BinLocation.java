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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.volvo.gloria.util.persistence.GenericEntity;
import com.volvo.gloria.warehouse.c.Allocation;

/**
 * Entity class Bin Location.
 */
@Entity
@Table(name = "BIN_LOCATION")
public class BinLocation implements GenericEntity<Long>, Serializable {
    private static final long serialVersionUID = 8731043063261939636L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BIN_LOCATION_OID")
    private Long binLocationOid;

    @Version
    private long version;

    private String aisleRackRowCode;
    private String bayCode;
    private long level;
    private long position;
    private String code;
    @Enumerated(EnumType.STRING)
    private Allocation allocation;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "binLocation")
    private List<Placement> placements = new ArrayList<Placement>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "binLocation")
    private List<BinlocationBalance> binlocationBalances = new ArrayList<BinlocationBalance>();

    @ManyToOne
    @JoinColumn(name = "ZONE_OID")
    private Zone zone;

    public BinLocation() {
    }

    public BinLocation(String code) {
        this.code = code;
    }

    public Long getBinLocationOid() {
        return binLocationOid;
    }

    public void setBinLocationOid(Long binLocationOid) {
        this.binLocationOid = binLocationOid;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public List<Placement> getPlacements() {
        return placements;
    }

    public void setPlacements(List<Placement> placements) {
        this.placements = placements;
    }

    public long getVersion() {
        return version;
    }

    public String getBayCode() {
        return bayCode;
    }

    public void setBayCode(String bayCode) {
        this.bayCode = bayCode;
    }

    public long getLevel() {
        return level;
    }

    public void setLevel(long level) {
        this.level = level;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Allocation getAllocation() {
        return allocation;
    }

    public void setAllocation(Allocation allocation) {
        this.allocation = allocation;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    @Override
    public Long getId() {
        return binLocationOid;
    }

    public String getAisleRackRowCode() {
        return aisleRackRowCode;
    }

    public void setAisleRackRowCode(String aisleRackRowCode) {
        this.aisleRackRowCode = aisleRackRowCode;
    }

    public List<BinlocationBalance> getBinlocationBalances() {
        return binlocationBalances;
    }

    public void setBinlocationBalances(List<BinlocationBalance> binlocationBalances) {
        this.binlocationBalances = binlocationBalances;
    }
}
