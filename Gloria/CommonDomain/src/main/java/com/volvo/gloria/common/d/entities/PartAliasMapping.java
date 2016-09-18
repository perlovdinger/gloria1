package com.volvo.gloria.common.d.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import com.volvo.gloria.util.persistence.GenericEntity;

/**
 * Entity class for PART_ALIAS_MAPPING.
 */
@Entity
@Table(name = "PART_ALIAS_MAPPING")
public class PartAliasMapping implements GenericEntity<Long>, Serializable {

    private static final long serialVersionUID = -5077141319546228341L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PART_ALIAS_MAPPING_OID")
    private long partAliasMappingOID;

    @Version
    private long version;
  
    private String name;
  
    private String kolaDomain;
    private String gpsQualifier;
    
    public long getPartAliasMappingOID() {
        return partAliasMappingOID;
    }
    public void setPartAliasMappingOID(long partAliasMappingOID) {
        this.partAliasMappingOID = partAliasMappingOID;
    }
    public long getVersion() {
        return version;
    }
    public void setVersion(long version) {
        this.version = version;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getKolaDomain() {
        return kolaDomain;
    }
    public void setKolaDomain(String kolaDomain) {
        this.kolaDomain = kolaDomain;
    }
    public String getGpsQualifier() {
        return gpsQualifier;
    }
    public void setGpsQualifier(String gpsQualifier) {
        this.gpsQualifier = gpsQualifier;
    }
   
    @Override
    public Long getId() {
        return partAliasMappingOID;
    }
   
}
