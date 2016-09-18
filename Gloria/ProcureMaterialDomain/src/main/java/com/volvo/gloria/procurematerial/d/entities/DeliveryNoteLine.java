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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.volvo.gloria.procurematerial.c.DeliveryNoteLineStatus;
import com.volvo.gloria.procurematerial.d.type.procure.ProcureType;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.Utils;

/**
 * Entity class for delivery note line.
 */
@Entity
@Table(name = "DELIVERY_NOTE_LINE")
public class DeliveryNoteLine implements Serializable {

    private static final int _2048 = 2048;

    private static final long serialVersionUID = -5358802097885881677L;

    private static final String DELIVERY_NOTE_LINE_MAPPED_BY_VAR = "deliveryNoteLine";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DELIVERY_NOTE_LINE_OID")
    private long deliveryNoteLineOID;

    @Version
    private long version;

    @ManyToOne
    @JoinColumn(name = "DELIVERY_NOTE_OID")
    private DeliveryNote deliveryNote;

    @ManyToOne
    @JoinColumn(name = "ORDER_LINE_OID")
    private OrderLine orderLine;

    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH }, 
            mappedBy = DELIVERY_NOTE_LINE_MAPPED_BY_VAR)
    private List<MaterialLine> materialLine = new ArrayList<MaterialLine>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = DELIVERY_NOTE_LINE_MAPPED_BY_VAR)
    private List<QiDoc> qualityDocs = new ArrayList<QiDoc>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = DELIVERY_NOTE_LINE_MAPPED_BY_VAR)
    private List<ProblemDoc> problemDocs = new ArrayList<ProblemDoc>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = DELIVERY_NOTE_LINE_MAPPED_BY_VAR)
    private List<GoodsReceiptLine> goodsReceiptLines = new ArrayList<GoodsReceiptLine>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = DELIVERY_NOTE_LINE_MAPPED_BY_VAR)
    private List<ReceiveDoc> measuringDocs = new ArrayList<ReceiveDoc>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = DELIVERY_NOTE_LINE_MAPPED_BY_VAR)
    private List<DeliveryNoteSubLine> deliveryNoteSubLines = new ArrayList<DeliveryNoteSubLine>();

    private String partAffiliation;
    private String partNumber;
    private String partVersion;
    private String partName;    
    private String partModification;    
    private String partAlias;
    private String projectId;
    @Enumerated(EnumType.STRING)
    private ProcureType procureType;
    private String procureInfo;
    private long possibleToReceiveQty;
    private long receivedQuantity;
    private long directSendQuantity;
    private long deliveryNoteQuantity;
    private long damagedQuantity;
    private boolean sendToQI;
    private boolean dangerousGoodsFlag;
    private String dangerousGoodsName;
    private Date expirationDate;
    private boolean hasMeasuringDoc;
    private boolean hasControlCertificateDoc;
    private String problemDescription;
    private boolean hasMissingInfo;
    private boolean hasOverDeliveries;
    private boolean hasDamagedParts;
    private boolean receivedDetailsUpdated;
    private String qualityInspectionComment;
    @Column(length = _2048)
    private String referenceIds;
    @Enumerated
    private DeliveryNoteLineStatus status;
    
    private boolean qiDetailsUpdated;
   
    private long materialLineOID;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date receivedDateTime;
    
    @Transient
    private long alreadyReceivedQty;
    
    @Transient
    private long alreadyBlockedQty;
    
    @Transient
    private long previouslyBlockedQty;    

    @Transient
    private long alreadyCanceledQty;
    
    @Transient
    private String qualityDocName;
    
    public long getPreviouslyBlockedQty() {
        return previouslyBlockedQty;
    }

    public void setPreviouslyBlockedQty(long previouslyBlockedQty) {
        this.previouslyBlockedQty = previouslyBlockedQty;
    }

    public long getDeliveryNoteLineOID() {
        return deliveryNoteLineOID;
    }

    public void setDeliveryNoteLineOID(long deliveryNoteLineOID) {
        this.deliveryNoteLineOID = deliveryNoteLineOID;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public DeliveryNote getDeliveryNote() {
        return deliveryNote;
    }

    public void setDeliveryNote(DeliveryNote deliveryNote) {
        this.deliveryNote = deliveryNote;
    }

    public OrderLine getOrderLine() {
        return orderLine;
    }

    public void setOrderLine(OrderLine orderLine) {
        this.orderLine = orderLine;
    }

    public List<MaterialLine> getMaterialLine() {
        return materialLine;
    }

    public void setMaterialLine(List<MaterialLine> materialLine) {
        this.materialLine = materialLine;
    }

    public List<QiDoc> getQualityDocs() {
        return qualityDocs;
    }

    public void setQualityDocs(List<QiDoc> qualityDocs) {
        this.qualityDocs = qualityDocs;
    }

    public List<ProblemDoc> getProblemDocs() {
        return problemDocs;
    }
    
    public void setProblemDocs(List<ProblemDoc> problemDocs) {
        this.problemDocs = problemDocs;
    }

    public long getReceivedQuantity() {
        return receivedQuantity;
    }

    public void setReceivedQuantity(long receivedQuantity) throws GloriaApplicationException {
        Utils.validatePostiveNumberLong(receivedQuantity);
        this.receivedQuantity = receivedQuantity;
    }

    public long getDeliveryNoteQuantity() {
        return deliveryNoteQuantity;
    }

    public void setDeliveryNoteQuantity(long deliveryNoteQuantity) throws GloriaApplicationException {
        Utils.validatePostiveNumberLong(deliveryNoteQuantity);
        this.deliveryNoteQuantity = deliveryNoteQuantity;

    }

    public long getDamagedQuantity() {
        return damagedQuantity;
    }

    public void setDamagedQuantity(long damagedQuantity) throws GloriaApplicationException {
        Utils.validatePostiveNumberLong(damagedQuantity);
        this.damagedQuantity = damagedQuantity;

    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public DeliveryNoteLineStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryNoteLineStatus status) {
        this.status = status;
    }

    public boolean isHasMeasuringDoc() {
        return hasMeasuringDoc;
    }

    public void setHasMeasuringDoc(boolean hasMeasuringDoc) {
        this.hasMeasuringDoc = hasMeasuringDoc;
    }

    public boolean isHasControlCertificateDoc() {
        return hasControlCertificateDoc;
    }

    public void setHasControlCertificateDoc(boolean hasControlCertificateDoc) {
        this.hasControlCertificateDoc = hasControlCertificateDoc;
    }

    public boolean isHasMissingInfo() {
        return hasMissingInfo;
    }

    public void setHasMissingInfo(boolean hasMissingInfo) {
        this.hasMissingInfo = hasMissingInfo;
    }

    public boolean isHasOverDeliveries() {
        return hasOverDeliveries;
    }

    public void setHasOverDeliveries(boolean hasOverDeliveries) {
        this.hasOverDeliveries = hasOverDeliveries;
    }

    public void setHasDamagedParts(boolean hasDamagedParts) {
        this.hasDamagedParts = hasDamagedParts;
    }

    public boolean isHasDamagedParts() {
        return hasDamagedParts;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

    public boolean isSendToQI() {
        return sendToQI;
    }

    public void setSendToQI(boolean sendToQI) {
        this.sendToQI = sendToQI;
    }

    public boolean isDangerousGoodsFlag() {
        return dangerousGoodsFlag;
    }

    public void setDangerousGoodsFlag(boolean dangerousGoodsFlag) {
        this.dangerousGoodsFlag = dangerousGoodsFlag;
    }

    public String getDangerousGoodsName() {
        return dangerousGoodsName;
    }

    public void setDangerousGoodsName(String dangerousGoodsName) {
        this.dangerousGoodsName = dangerousGoodsName;
    }

    public ProcureType getProcureType() {
        return procureType;
    }

    public void setProcureType(ProcureType procureType) {
        this.procureType = procureType;
    }

    public String getProcureInfo() {
        return procureInfo;
    }

    public void setProcureInfo(String procureInfo) {
        this.procureInfo = procureInfo;
    }

    public List<GoodsReceiptLine> getGoodsReceiptLines() {
        return goodsReceiptLines;
    }

    public void setGoodsReceiptLines(List<GoodsReceiptLine> goodsReceiptLines) {
        this.goodsReceiptLines = goodsReceiptLines;
    }

    public String getQualityInspectionComment() {
        return qualityInspectionComment;
    }

    public void setQualityInspectionComment(String qualityInspectionComment) {
        this.qualityInspectionComment = qualityInspectionComment;
    }

    public List<ReceiveDoc> getMeasuringDocs() {
        return measuringDocs;
    }
    
    public void setMeasuringDocs(List<ReceiveDoc> measuringDocs) {
        this.measuringDocs = measuringDocs;
    }

    public boolean isReceivedDetailsUpdated() {
        return receivedDetailsUpdated;
    }

    public void setReceivedDetailsUpdated(boolean receivedDetailsUpdated) {
        this.receivedDetailsUpdated = receivedDetailsUpdated;
    }

    public boolean isQiDetailsUpdated() {
        return qiDetailsUpdated;
    }

    public void setQiDetailsUpdated(boolean qiDetailsUpdated) {
        this.qiDetailsUpdated = qiDetailsUpdated;
    }

    public String getPartVersion() {
        return partVersion;
    }

    public void setPartVersion(String partVersion) {
        this.partVersion = partVersion;
    }

    public String getPartModification() {
        return partModification;
    }

    public void setPartModification(String partModification) {
        this.partModification = partModification;
    }

    public String getPartAffiliation() {
        return partAffiliation;
    }

    public void setPartAffiliation(String partAffiliation) {
        this.partAffiliation = partAffiliation;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getPartAlias() {
        return partAlias;
    }

    public long getPossibleToReceiveQty() {
        return possibleToReceiveQty;
    }

    public void setPossibleToReceiveQty(long possibleToReceiveQty) {
        this.possibleToReceiveQty = possibleToReceiveQty;
    }

    public long getDirectSendQuantity() {
        return directSendQuantity;
    }

    public void setDirectSendQuantity(long directSendQuantity) {
        this.directSendQuantity = directSendQuantity;
    }
    
    public void setPartAlias(String partAlias) {
        this.partAlias = partAlias;
    }
    
    public long getMaterialLineOID() {
        return materialLineOID;
    }
    
    public void setMaterialLineOID(long materialLineOID) {
        this.materialLineOID = materialLineOID;
    }
        
    public String getProjectId() {
        return projectId;
    }
    
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
    
    public List<DeliveryNoteSubLine> getDeliveryNoteSubLines() {
        return deliveryNoteSubLines;
    }
    
    public void setDeliveryNoteSubLines(List<DeliveryNoteSubLine> deliveryNoteSubLines) {
        this.deliveryNoteSubLines = deliveryNoteSubLines;
    }
    
    public DeliveryNoteSubLine getSubLine(boolean directSend) {
        return evaluateDeliveryNoteSubLineOnDirectSend(directSend);
    }

    public long getAlreadyReceivedQty() {
        return alreadyReceivedQty;
    }
    
    public void setAlreadyReceivedQty(long alreadyReceivedQty) {
        this.alreadyReceivedQty = alreadyReceivedQty;
    }
    
    public long getAlreadyBlockedQty() {
        return alreadyBlockedQty;
    }
    
    public void setAlreadyBlockedQty(long alreadyBlockedQty) {
        this.alreadyBlockedQty = alreadyBlockedQty;
    }

    public long getAlreadyCanceledQty() {
        return alreadyCanceledQty;
    }
    
    public void setAlreadyCanceledQty(long alreadyCanceledQty) {
        this.alreadyCanceledQty = alreadyCanceledQty;
    }
    
    private DeliveryNoteSubLine evaluateDeliveryNoteSubLineOnDirectSend(boolean directSend) {
        if (deliveryNoteSubLines != null && !deliveryNoteSubLines.isEmpty()) {
            for (DeliveryNoteSubLine deliveryNoteSubLine : deliveryNoteSubLines) {
                if (deliveryNoteSubLine.isDirectSend() == directSend) {
                    return deliveryNoteSubLine;
                }
            }
        }
        return null;
    }

    public String getReferenceIds() {
        return referenceIds;
    }

    public void setReferenceIds(String referenceIds) {
        this.referenceIds = referenceIds;
    }

    public Date getReceivedDateTime() {
        return receivedDateTime;
    }

    public void setReceivedDateTime(Date receivedDateTime) {
        this.receivedDateTime = receivedDateTime;
    }

    public String getQualityDocName() {
        return qualityDocName;
    }

    public void setQualityDocName(String qualityDocName) {
        this.qualityDocName = qualityDocName;
    }
    
}
