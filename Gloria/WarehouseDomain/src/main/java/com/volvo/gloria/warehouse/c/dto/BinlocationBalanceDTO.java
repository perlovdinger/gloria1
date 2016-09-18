package com.volvo.gloria.warehouse.c.dto;

import java.io.Serializable;
import java.util.Date;

import com.volvo.gloria.util.paging.c.PageResults;
import com.volvo.gloria.warehouse.c.ZoneType;

public class BinlocationBalanceDTO implements Serializable, PageResults {

    private static final long serialVersionUID = 2607052651064222153L;

    private long version;
    private long id;
    private String deviation;
    private String partAffiliation;
    private String partNumber;
    private String partVersion;
    private String partName;
    private String partModification;
    private String code;
    private long quantity;
    private Date scrapDate;
    private ZoneType zoneType;

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDeviation() {
        return deviation;
    }

    public void setDeviation(String deviation) {
        this.deviation = deviation;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getPartVersion() {
        return partVersion;
    }

    public void setPartVersion(String partVersion) {
        this.partVersion = partVersion;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }
    
    public String getPartModification() {
        return partModification;
    }
    
    public void setPartModification(String partModification) {
        this.partModification = partModification;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public Date getScrapDate() {
        return scrapDate;
    }

    public void setScrapDate(Date scrapDate) {
        this.scrapDate = scrapDate;
    }
    
    public String getPartAffiliation() {
        return partAffiliation;
    }
    
    public void setPartAffiliation(String partAffiliation) {
        this.partAffiliation = partAffiliation;
    }

    public ZoneType getZoneType() {
        return zoneType;
    }

    public void setZoneType(ZoneType zoneType) {
        this.zoneType = zoneType;
    }
}
