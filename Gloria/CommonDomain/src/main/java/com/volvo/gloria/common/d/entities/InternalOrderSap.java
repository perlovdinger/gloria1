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
 * Entity class for InternalOrderSAP.
 */
@Entity
@Table(name = "INTERNAL_ORDER_SAP")
public class InternalOrderSap implements GenericEntity<Long>, Serializable {
    private static final long serialVersionUID = 3022665886652396843L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INTERNAL_ORDER_SAP_OID")
    private long internalOrderSapOid;
    @Version
    private long version;
    private String code;

    public long getInternalOrderSapOid() {
        return internalOrderSapOid;
    }

    public void setInternalOrderSapOid(long internalOrderSapOid) {
        this.internalOrderSapOid = internalOrderSapOid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public Long getId() {
        return internalOrderSapOid;
    }

    @Override
    public long getVersion() {
        return version;
    }
}
