package com.volvo.gloria.procurematerial.d.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.volvo.gloria.common.c.KolaDomain;

/**
 * 
 */
@Entity
@Table(name = "MATERIAL_PART_ALIAS")
public class MaterialPartAlias implements Serializable {

    private static final long serialVersionUID = 863595594055807675L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MATERIAL_PART_ALIAS_OID")
    private long materialPartAliasOid;

    @Version
    private long version;

    @ManyToOne
    @JoinColumn(name = "MATERIAL_OID")
    private Material material;

    @Enumerated(EnumType.STRING)
    private KolaDomain kolaDomain;

    private String partNumber;

    public long getMaterialPartAliasOid() {
        return materialPartAliasOid;
    }

    public void setMaterialPartAliasOid(long materialPartAliasOid) {
        this.materialPartAliasOid = materialPartAliasOid;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

  
    public KolaDomain getKolaDomain() {
        return kolaDomain;
    }

    public void setKolaDomain(KolaDomain kolaDomain) {
        this.kolaDomain = kolaDomain;
    }

    
    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

}
