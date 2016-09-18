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
 * Enitity class for Dangerous Goods.
 * 
 */
@Entity
@Table(name = "DANGEROUS_GOODS")
public class DangerousGoods implements GenericEntity<Long>, Serializable {

    private static final long serialVersionUID = -5621902741436470578L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DANGEROUS_GOODS_OID")
    private long dangerousGoodsOID;


    @Version
    private long version;

    private String name;

    private boolean flag;

    private int displaySeq;

    public long getDangerousGoodsOID() {
        return dangerousGoodsOID;
    }

    public void setDangerousGoodsOID(long dangerousGoodsOID) {
        this.dangerousGoodsOID = dangerousGoodsOID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public int getDisplaySeq() {
        return displaySeq;
    }

    public void setDisplaySeq(int displaySeq) {
        this.displaySeq = displaySeq;
    }


    @Override
    public Long getId() {
        return dangerousGoodsOID;
    }

    @Override
    public long getVersion() {
        return version;
    }
}
