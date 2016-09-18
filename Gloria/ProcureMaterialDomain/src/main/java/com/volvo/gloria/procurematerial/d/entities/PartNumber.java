package com.volvo.gloria.procurematerial.d.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.volvo.gloria.util.persistence.GenericEntity;

@Entity
@Table(name = "PART_NUMBER")
public class PartNumber implements GenericEntity<Long>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -3587984889031394083L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PART_NUMBER_OID")
    private long partNumberOid;

    @Version
    private long version;

    private String volvoPartNumber;
    private String volvoPartAffiliation;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "partNumber")
    private List<PartAlias> partAlias = new ArrayList<PartAlias>();

    public long getPartNumberOid() {
        return partNumberOid;
    }

    public void setPartNumberOid(long partNumberOid) {
        this.partNumberOid = partNumberOid;
    }

    public String getVolvoPartNumber() {
        return volvoPartNumber;
    }

    public void setVolvoPartNumber(String volvoPartNumber) {
        this.volvoPartNumber = volvoPartNumber;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public void setPartAlias(List<PartAlias> partAlias) {
        this.partAlias = partAlias;
    }

    public List<PartAlias> getPartAlias() {
        return partAlias;
    }
    
    @Override
    public Long getId() {
        return partNumberOid;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public String getVolvoPartAffiliation() {
        return volvoPartAffiliation;
    }

    public void setVolvoPartAffiliation(String volvoPartAffiliation) {
        this.volvoPartAffiliation = volvoPartAffiliation;
    }
}
