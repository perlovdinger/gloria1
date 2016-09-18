package com.volvo.gloria.procurematerial.d.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * Entity Class for Material Last Modified.
 */
@Entity
@Table(name = "MATERIAL_LAST_MODIFIED")
public class MaterialLastModified implements Serializable {
    private static final long serialVersionUID = 2505570938453745854L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MATERIAL_LAST_MODIFIED_OID")
    private long materialLastModifiedOID;


    @Version
    private long version;

    private boolean alertPartVersion;

    public boolean isAlertPartVersion() {
        return alertPartVersion;
    }

    public void setAlertPartVersion(boolean alertPartVersion) {
        this.alertPartVersion = alertPartVersion;
    }
    
    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public long getMaterialLastModifiedOID() {
        return materialLastModifiedOID;
    }

    public void setMaterialLastModifiedOID(long materialLastModifiedOID) {
        this.materialLastModifiedOID = materialLastModifiedOID;
    }

}
