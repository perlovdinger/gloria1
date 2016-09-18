package com.volvo.gloria.common.d.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import com.volvo.gloria.util.persistence.GenericEntity;

/**
 * Entity class for Traceability table.
 */
@Entity
@Table(name = "Traceability")
public class Traceability implements GenericEntity<Long>, Serializable {

    private static final long serialVersionUID = -8789968105549958791L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TRACEABILITY_OID")
    private long traceabilityOid;

    @Version
    private long version;

    @Enumerated(EnumType.STRING)
    private TraceabilityType traceabilityType;
    private Timestamp loggedTime;
    private String userId;
    private String userName;
    private String action;
    private String actionDetail;
    private String userText;
    private long materialOID;
    private long materialLineOId;
    private String mlStatus;
    private long mlQuantity;
    private String orderNo;
    private Date orderDate;
    private String internalExternal;
    private long olQuantity;
    private String olStatus;
    private long allowedQuantity;
    private Date staAgreedDate;
    private Date staAcceptedDate;
    private long deliveryScheduleOID;
    private Date expectedDate;
    private Date plannedDispatchDate;
    private long expectedQty;
    private String whSiteId;
    private String binLocationCode;
    private long orderLineOID;
    private String i18MessageCode;
    private String link;
    private String linkType;

    public Traceability(TraceabilityType traceabilityType) {
        this.traceabilityType = traceabilityType;
    }
    public long getTraceabilityOid() {
        return traceabilityOid;
    }

    public void setTraceabilityOid(long traceabilityOid) {
        this.traceabilityOid = traceabilityOid;
    }

    public long getVersion() {
        return version;
    }

    public Timestamp getLoggedTime() {
        return loggedTime;
    }

    public void setLoggedTime(Timestamp loggedTime) {
        this.loggedTime = loggedTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getActionDetail() {
        return actionDetail;
    }

    public void setActionDetail(String actionDetail) {
        this.actionDetail = actionDetail;
    }

    public String getUserText() {
        return userText;
    }

    public void setUserText(String userText) {
        this.userText = userText;
    }

    public long getMaterialOID() {
        return materialOID;
    }

    public void setMaterialOID(long materialOID) {
        this.materialOID = materialOID;
    }

    public long getMaterialLineOId() {
        return materialLineOId;
    }

    public void setMaterialLineOId(long materialLineOId) {
        this.materialLineOId = materialLineOId;
    }

    public String getMlStatus() {
        return mlStatus;
    }

    public void setMlStatus(String mlStatus) {
        this.mlStatus = mlStatus;
    }

    public long getMlQuantity() {
        return mlQuantity;
    }

    public void setMlQuantity(long mlQuantity) {
        this.mlQuantity = mlQuantity;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getInternalExternal() {
        return internalExternal;
    }

    public void setInternalExternal(String internalExternal) {
        this.internalExternal = internalExternal;
    }

    public long getOlQuantity() {
        return olQuantity;
    }

    public void setOlQuantity(long olQuantity) {
        this.olQuantity = olQuantity;
    }

    public String getOlStatus() {
        return olStatus;
    }

    public void setOlStatus(String olStatus) {
        this.olStatus = olStatus;
    }

    public long getAllowedQuantity() {
        return allowedQuantity;
    }

    public void setAllowedQuantity(long allowedQuantity) {
        this.allowedQuantity = allowedQuantity;
    }

    public Date getStaAgreedDate() {
        return staAgreedDate;
    }

    public void setStaAgreedDate(Date staAgreedDate) {
        this.staAgreedDate = staAgreedDate;
    }

    public Date getStaAcceptedDate() {
        return staAcceptedDate;
    }

    public void setStaAcceptedDate(Date staAcceptedDate) {
        this.staAcceptedDate = staAcceptedDate;
    }

    public long getDeliveryScheduleOID() {
        return deliveryScheduleOID;
    }

    public void setDeliveryScheduleOID(long deliveryScheduleOID) {
        this.deliveryScheduleOID = deliveryScheduleOID;
    }

    public Date getExpectedDate() {
        return expectedDate;
    }

    public void setExpectedDate(Date expectedDate) {
        this.expectedDate = expectedDate;
    }

    public Date getPlannedDispatchDate() {
        return plannedDispatchDate;
    }

    public void setPlannedDispatchDate(Date plannedDispatchDate) {
        this.plannedDispatchDate = plannedDispatchDate;
    }

    public long getExpectedQty() {
        return expectedQty;
    }

    public void setExpectedQty(long expectedQty) {
        this.expectedQty = expectedQty;
    }

    public String getWhSiteId() {
        return whSiteId;
    }

    public void setWhSiteId(String whSiteId) {
        this.whSiteId = whSiteId;
    }

    public String getBinLocationCode() {
        return binLocationCode;
    }

    public void setBinLocationCode(String binLocationCode) {
        this.binLocationCode = binLocationCode;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public long getOrderLineOID() {
        return orderLineOID;
    }

    public void setOrderLineOID(long orderLineOID) {
        this.orderLineOID = orderLineOID;
    }

    @Override
    public Long getId() {
        return traceabilityOid;
    }

    public String getI18MessageCode() {
        return i18MessageCode;
    }

    public void setI18MessageCode(String i18MessageCode) {
        this.i18MessageCode = i18MessageCode;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    public TraceabilityType getTraceabilityType() {
        return traceabilityType;
    }

    public void setTraceabilityType(TraceabilityType traceabilityType) {
        this.traceabilityType = traceabilityType;
    }
    
}
