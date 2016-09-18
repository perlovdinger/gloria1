package com.volvo.gloria.procurematerial.d.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

/**
 * Entity implementation class for Entity: TransportLabel.
 * 
 */
@Entity
@Table(name = "TRANSPORT_LABEL")
public class TransportLabel implements Serializable {

    private static final long serialVersionUID = -6407550640617512069L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TRANSPORT_LABEL_OID")
    private long transportLabelOid;
    
    @Version
    private long version;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "transportLabel")
    private List<DeliveryNoteSubLine> deliveryNoteSubLines = new ArrayList<DeliveryNoteSubLine>();

    private String code;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
    
    private String whSiteId;

    public long getTransportLabelOid() {
        return transportLabelOid;
    }

    public void setTransportLabelOid(long transportLabelOid) {
        this.transportLabelOid = transportLabelOid;
    }
    
    public long getVersion() {
        return version;
    }
    
    public void setVersion(long version) {
        this.version = version;
    }    

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getWhSiteId() {
        return whSiteId;
    }

    public void setWhSiteId(String whSiteId) {
        this.whSiteId = whSiteId;
    }

    public List<DeliveryNoteSubLine> getDeliveryNoteSubLines() {
        return deliveryNoteSubLines;
    }
    
    public void setDeliveryNoteSubLines(List<DeliveryNoteSubLine> deliveryNoteSubLines) {
        this.deliveryNoteSubLines = deliveryNoteSubLines;
    }
}
