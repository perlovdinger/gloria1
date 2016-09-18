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
 * Entity for the Inspection Document.
 */
@Entity
@Table(name = "RECEIVE_DOC")
public class ReceiveDoc implements Serializable {

    private static final long serialVersionUID = 2372062300476598378L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RECEIVE_DOC_OID")
    private long receiveDocOID;

    @ManyToOne
    @JoinColumn(name = "DELIVERY_NOTE_LINE_OID")
    private DeliveryNoteLine deliveryNoteLine;

    private String documentName;

    @Column(name = "CREATION_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @Lob
    // @Column(columnDefinition = "blob(5242880)")
    private byte[] fileContent;

    public long getRecieveDocOID() {
        return receiveDocOID;
    }
    
    public void setRecieveDocOID(long receiveDocOID) {
        this.receiveDocOID = receiveDocOID;
    }

    public DeliveryNoteLine getDeliveryNoteLine() {
        return deliveryNoteLine;
    }

    public void setDeliveryNoteLine(DeliveryNoteLine deliveryNoteLine) {
        this.deliveryNoteLine = deliveryNoteLine;
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

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public void setFileContent(byte[] content) {
        if (content == null) {
            this.fileContent = new byte[0];
        } else {
            this.fileContent = Arrays.copyOf(content, content.length);
        }
    }
    
    @PrePersist
    protected void onCreate() {
        creationDate = DateUtil.getCurrentUTCDateTime();
    }
}
