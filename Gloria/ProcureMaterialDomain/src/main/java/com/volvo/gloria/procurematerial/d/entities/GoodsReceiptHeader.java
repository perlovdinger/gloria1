package com.volvo.gloria.procurematerial.d.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "GOODS_RECEIPT_HEADER")
public class GoodsReceiptHeader implements Serializable {

    private static final long serialVersionUID = 2151387271924507794L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GOODS_RECEIPT_HEADER_OID")
    private long goodReceiptHeaderOId;
    @Version
    private long version;
    private String companyCode;
    private Date postedDateTime;
    private Date documentDate;
    private String referenceDocument;
    private String headerText;
    private String assignCodeGM;    
    
    @Enumerated(EnumType.STRING)
    private MessageStatus messageStatus;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "goodsReceiptHeader")
    private List<GoodsReceiptLine> goodsReceiptLines = new ArrayList<GoodsReceiptLine>();

    @ManyToOne
    @JoinColumn(name = "DELIVERY_NOTE_OID")
    private DeliveryNote deliveryNote;

    public long getGoodReceiptHeaderOId() {
        return goodReceiptHeaderOId;
    }

    public void setGoodReceiptHeaderOId(long goodReceiptHeaderOId) {
        this.goodReceiptHeaderOId = goodReceiptHeaderOId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public Date getPostedDateTime() {
        return postedDateTime;
    }

    public void setPostedDateTime(Date postedDateTime) {
        this.postedDateTime = postedDateTime;
    }

    public Date getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(Date documentDate) {
        this.documentDate = documentDate;
    }

    public String getReferenceDocument() {
        return referenceDocument;
    }

    public void setReferenceDocument(String referenceDocument) {
        this.referenceDocument = referenceDocument;
    }

    public String getHeaderText() {
        return headerText;
    }

    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }

    public String getAssignCodeGM() {
        return assignCodeGM;
    }

    public void setAssignCodeGM(String assignCodeGM) {
        this.assignCodeGM = assignCodeGM;
    }

    public List<GoodsReceiptLine> getGoodsReceiptLines() {
        return goodsReceiptLines;
    }

    public void setGoodsReceiptLines(List<GoodsReceiptLine> goodsReceiptLines) {
        this.goodsReceiptLines = goodsReceiptLines;
    }

    public DeliveryNote getDeliveryNote() {
        return deliveryNote;
    }

    public void setDeliveryNote(DeliveryNote deliveryNote) {
        this.deliveryNote = deliveryNote;
    }    

    public MessageStatus getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(MessageStatus messageStatus) {
        this.messageStatus = messageStatus;
    }

}
