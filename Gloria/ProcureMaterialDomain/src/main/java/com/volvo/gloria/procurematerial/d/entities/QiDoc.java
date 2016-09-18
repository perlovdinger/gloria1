/*
 * Copyright 2009 Volvo Information Technology AB 
 * 
 * Licensed under the Volvo IT Corporate Source License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *      http://java.volvo.com/licenses/CORPORATE-SOURCE-LICENSE 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
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
 * Entity class for QI Doc.
 * 
 */
@Entity
@Table(name = "QI_DOC")
public class QiDoc implements Serializable {

    private static final long serialVersionUID = 2346838863747947025L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QI_DOC_OID")
    private long qiDocOID;
    
    @ManyToOne
    @JoinColumn(name = "DELIVERY_NOTE_LINE_OID")
    private DeliveryNoteLine deliveryNoteLine;

    private String documentName;

    @Column(name = "CREATION_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @Lob
    //@Column(columnDefinition = "blob(5242880)")
    private byte[] fileContent;   

    public long getQiDocOID() {
        return qiDocOID;
    }

    public void setQiDocOID(long qiDocOID) {
        this.qiDocOID = qiDocOID;
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

    public DeliveryNoteLine getDeliveryNoteLine() {
        return deliveryNoteLine;
    }

    public void setDeliveryNoteLine(DeliveryNoteLine deliveryNoteLine) {
        this.deliveryNoteLine = deliveryNoteLine;
    }

    @PrePersist
    protected void onCreate() {
        creationDate = DateUtil.getCurrentUTCDateTime();
    }
}
