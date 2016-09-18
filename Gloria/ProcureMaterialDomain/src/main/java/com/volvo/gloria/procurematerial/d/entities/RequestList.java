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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.volvo.gloria.procurematerial.c.DeliveryAddressType;
import com.volvo.gloria.procurematerial.c.ShipmentType;
import com.volvo.gloria.procurematerial.d.entities.status.requestlist.RequestListStatus;

/**
 * Entity class for RequestList.
 * 
 */
@Entity
@Table(name = "REQUEST_LIST")
public class RequestList implements Serializable {

    private static final long serialVersionUID = -8166054619669946036L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REQUEST_LIST_OID")
    private long requestListOid;

    @Version
    private long version;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "requestList")
    private List<RequestGroup> requestGroups = new ArrayList<RequestGroup>();

    @OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH })
    @JoinColumn(name = "DISPATCH_NOTE_OID")
    private DispatchNote dispatchNote;

    private String whSiteId;
    private Date createdDate;
    private Date requiredDeliveryDate;
    private String requesterName;
    private String requestUserId;
    private Long priority;

    private String shipVia;

    @Enumerated(EnumType.STRING)
    private RequestListStatus status;
    private String deliveryAddressId;
    private String deliveryAddressName;
    
    @Enumerated(EnumType.STRING)
    private DeliveryAddressType deliveryAddressType;
    @Enumerated(EnumType.STRING)
    private ShipmentType shipmentType;
    private String whSiteName;
    
    @Transient
    private String deliveryAddress;
    
    @Transient
    private String whSiteAddress;

    @Transient
    private boolean cancelAllowed;
    
    public long getRequestListOid() {
        return requestListOid;
    }

    public void setRequestListOid(long requestListOid) {
        this.requestListOid = requestListOid;
    }

    public String getWhSiteId() {
        return whSiteId;
    }

    public void setWhSiteId(String whSiteId) {
        this.whSiteId = whSiteId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getRequiredDeliveryDate() {
        return requiredDeliveryDate;
    }

    public void setRequiredDeliveryDate(Date requiredDeliveryDate) {
        this.requiredDeliveryDate = requiredDeliveryDate;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getRequestUserId() {
        return requestUserId;
    }

    public void setRequestUserId(String requestUserId) {
        this.requestUserId = requestUserId;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public DispatchNote getDispatchNote() {
        return dispatchNote;
    }

    public void setDispatchNote(DispatchNote dispatchNote) {
        this.dispatchNote = dispatchNote;
    }

    public List<RequestGroup> getRequestGroups() {
        return requestGroups;
    }

    public void setRequestGroups(List<RequestGroup> requestGroups) {
        this.requestGroups = requestGroups;
    }

    public RequestListStatus getStatus() {
        return status;
    }

    public void setStatus(RequestListStatus status) {
        this.status = status;
    }

    public String getDeliveryAddressId() {
        return deliveryAddressId;
    }

    public void setDeliveryAddressId(String deliveryAddressId) {
        this.deliveryAddressId = deliveryAddressId;
    }

    public String getDeliveryAddressName() {
        return deliveryAddressName;
    }

    public void setDeliveryAddressName(String deliveryAddressName) {
        this.deliveryAddressName = deliveryAddressName;
    }

    public DeliveryAddressType getDeliveryAddressType() {
        return deliveryAddressType;
    }

    public void setDeliveryAddressType(DeliveryAddressType deliveryAddressType) {
        this.deliveryAddressType = deliveryAddressType;
    }

    public String getShipVia() {
        return shipVia;
    }

    public void setShipVia(String shipVia) {
        this.shipVia = shipVia;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public ShipmentType getShipmentType() {
        return shipmentType;
    }

    public void setShipmentType(ShipmentType shipmentType) {
        this.shipmentType = shipmentType;
    }

    public String getWhSiteName() {
        return whSiteName;
    }

    public void setWhSiteName(String whSiteName) {
        this.whSiteName = whSiteName;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getWhSiteAddress() {
        return whSiteAddress;
    }

    public void setWhSiteAddress(String whSiteAddress) {
        this.whSiteAddress = whSiteAddress;
    }
    
    public boolean isCancelAllowed() {
        return cancelAllowed;
    }
    
    public void setCancelAllowed(boolean cancelAllowed) {
        this.cancelAllowed = cancelAllowed;
    }
}
