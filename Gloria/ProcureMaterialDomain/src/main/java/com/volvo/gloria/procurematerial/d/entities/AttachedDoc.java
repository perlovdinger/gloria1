package com.volvo.gloria.procurematerial.d.entities;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.volvo.gloria.util.DateUtil;

/**
 * Entity implementation class for Entity: AttachedDoc.
 * 
 */
@Entity
@Table(name = "ATTACHED_DOC")
public class AttachedDoc implements Serializable {

    private static final long serialVersionUID = 4962381446255309949L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ATTACHED_DOC_OID")
    private long attachedDocOID;

    @ManyToOne
    @JoinColumn(name = "DELIVERY_SCHEDULE_OID")
    private DeliverySchedule deliverySchedule;

    private String documentName;

    @Column(name = "CREATION_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @Lob
    //@Column(columnDefinition = "blob(5242880)")
    private byte[] fileContent;

    public long getAttachedDocOID() {
        return attachedDocOID;
    }

    public void setAttachedDocOID(long attachedDocOID) {
        this.attachedDocOID = attachedDocOID;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public void setFileContent(byte[] content) {
        if (content != null) {
            this.fileContent = Arrays.copyOf(content, content.length);
        }
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public DeliverySchedule getDeliverySchedule() {
        return deliverySchedule;
    }

    public void setDeliverySchedule(DeliverySchedule deliverySchedule) {
        this.deliverySchedule = deliverySchedule;
    }

    @PrePersist
    protected void onCreate() {
        creationDate = DateUtil.getCurrentUTCDateTime();
    }
}
