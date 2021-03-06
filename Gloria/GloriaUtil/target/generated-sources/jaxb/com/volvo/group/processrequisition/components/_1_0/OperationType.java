//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:29 em CEST 
//


package com.volvo.group.processrequisition.components._1_0;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;


/**
 * <p>Java class for OperationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OperationType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.openapplications.org/oagis/9}HeaderType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}BOMReference" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}RouteReference" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}Type" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}CostType" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}ContainerType" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}DepartmentID" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}CostAmount" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}ProcessCode" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}MachineSetupDependencyCodes" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}MaximumParallelTeamsFactor" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}FixedRejectQuantity" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}RejectedQuantity" minOccurs="0"/>
 *         &lt;group ref="{http://www.openapplications.org/oagis/9}OperationGroup" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}Site" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}MultipleRunSaveTimeIndicator" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}TrackingIndicator" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}QualifiedResource" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}Charge" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}Distribution" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}Step" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.openapplications.org/oagis/9}UserArea" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OperationType", propOrder = {
    "bomReference",
    "routeReference",
    "type",
    "costType",
    "containerType",
    "departmentID",
    "costAmount",
    "processCode",
    "machineSetupDependencyCodes",
    "maximumParallelTeamsFactor",
    "fixedRejectQuantity",
    "rejectedQuantity",
    "batchSizeQuantity",
    "perishedInOperationQuantity",
    "perishedBetweenOperationsQuantity",
    "transferLotQuantity",
    "setupTimeDuration",
    "waitTimeDuration",
    "runTimeDuration",
    "tearDownDuration",
    "batchDuration",
    "queueDuration",
    "moveDuration",
    "fixedDuration",
    "rejectPercent",
    "site",
    "multipleRunSaveTimeIndicator",
    "trackingIndicator",
    "qualifiedResource",
    "charge",
    "distribution",
    "step",
    "userArea"
})
public class OperationType
    extends HeaderType
{

    @XmlElement(name = "BOMReference")
    protected List<BOMReferenceType> bomReference;
    @XmlElement(name = "RouteReference")
    protected List<RouteReferenceType> routeReference;
    @XmlElement(name = "Type")
    protected CodeType type;
    @XmlElement(name = "CostType")
    protected CodeType costType;
    @XmlElement(name = "ContainerType")
    protected CodeType containerType;
    @XmlElement(name = "DepartmentID")
    protected IdentifierType departmentID;
    @XmlElement(name = "CostAmount")
    protected AmountType costAmount;
    @XmlElement(name = "ProcessCode")
    protected CodeType processCode;
    @XmlElement(name = "MachineSetupDependencyCodes")
    protected CodesType machineSetupDependencyCodes;
    @XmlElement(name = "MaximumParallelTeamsFactor")
    protected BigDecimal maximumParallelTeamsFactor;
    @XmlElement(name = "FixedRejectQuantity")
    protected QuantityType fixedRejectQuantity;
    @XmlElement(name = "RejectedQuantity")
    protected QuantityType rejectedQuantity;
    @XmlElement(name = "BatchSizeQuantity")
    protected QuantityType batchSizeQuantity;
    @XmlElement(name = "PerishedInOperationQuantity")
    protected QuantityType perishedInOperationQuantity;
    @XmlElement(name = "PerishedBetweenOperationsQuantity")
    protected QuantityType perishedBetweenOperationsQuantity;
    @XmlElement(name = "TransferLotQuantity")
    protected QuantityType transferLotQuantity;
    @XmlElement(name = "SetupTimeDuration")
    protected Duration setupTimeDuration;
    @XmlElement(name = "WaitTimeDuration")
    protected Duration waitTimeDuration;
    @XmlElement(name = "RunTimeDuration")
    protected Duration runTimeDuration;
    @XmlElement(name = "TearDownDuration")
    protected Duration tearDownDuration;
    @XmlElement(name = "BatchDuration")
    protected Duration batchDuration;
    @XmlElement(name = "QueueDuration")
    protected Duration queueDuration;
    @XmlElement(name = "MoveDuration")
    protected Duration moveDuration;
    @XmlElement(name = "FixedDuration")
    protected Duration fixedDuration;
    @XmlElement(name = "RejectPercent")
    protected BigDecimal rejectPercent;
    @XmlElement(name = "Site")
    protected List<LocationType> site;
    @XmlElement(name = "MultipleRunSaveTimeIndicator")
    protected Boolean multipleRunSaveTimeIndicator;
    @XmlElement(name = "TrackingIndicator")
    protected Boolean trackingIndicator;
    @XmlElement(name = "QualifiedResource")
    protected List<QualifiedResourceType> qualifiedResource;
    @XmlElement(name = "Charge")
    protected List<ChargeType> charge;
    @XmlElement(name = "Distribution")
    protected List<DistributionType> distribution;
    @XmlElement(name = "Step")
    protected List<StepType> step;
    @XmlElement(name = "UserArea")
    protected UserAreaType userArea;

    /**
     * Gets the value of the bomReference property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the bomReference property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBOMReference().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BOMReferenceType }
     * 
     * 
     */
    public List<BOMReferenceType> getBOMReference() {
        if (bomReference == null) {
            bomReference = new ArrayList<BOMReferenceType>();
        }
        return this.bomReference;
    }

    /**
     * Gets the value of the routeReference property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the routeReference property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRouteReference().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RouteReferenceType }
     * 
     * 
     */
    public List<RouteReferenceType> getRouteReference() {
        if (routeReference == null) {
            routeReference = new ArrayList<RouteReferenceType>();
        }
        return this.routeReference;
    }

    /**
     * Is used to indicate how the rates are specified. 
     * 
     * @return
     *     possible object is
     *     {@link CodeType }
     *     
     */
    public CodeType getType() {
        return type;
    }

    /**
     * Is used to indicate how the rates are specified. 
     * 
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *     
     */
    public void setType(CodeType value) {
        this.type = value;
    }

    /**
     * Is the.methodology by which the value of an item is determined.
     * 
     * Values are:
     * Standard
     * Moving Average
     * LIFO - last in first out.
     * FIFO - first in first out.
     * 
     * @return
     *     possible object is
     *     {@link CodeType }
     *     
     */
    public CodeType getCostType() {
        return costType;
    }

    /**
     * Is the.methodology by which the value of an item is determined.
     * 
     * Values are:
     * Standard
     * Moving Average
     * LIFO - last in first out.
     * FIFO - first in first out.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *     
     */
    public void setCostType(CodeType value) {
        this.costType = value;
    }

    /**
     * Identifies the type of container used to place the items that result from the operation.
     * 
     * @return
     *     possible object is
     *     {@link CodeType }
     *     
     */
    public CodeType getContainerType() {
        return containerType;
    }

    /**
     * Identifies the type of container used to place the items that result from the operation.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *     
     */
    public void setContainerType(CodeType value) {
        this.containerType = value;
    }

    /**
     * Indicates the department in which the operation is performed.
     * 
     * @return
     *     possible object is
     *     {@link IdentifierType }
     *     
     */
    public IdentifierType getDepartmentID() {
        return departmentID;
    }

    /**
     * Indicates the department in which the operation is performed.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentifierType }
     *     
     */
    public void setDepartmentID(IdentifierType value) {
        this.departmentID = value;
    }

    /**
     * Gets the value of the costAmount property.
     * 
     * @return
     *     possible object is
     *     {@link AmountType }
     *     
     */
    public AmountType getCostAmount() {
        return costAmount;
    }

    /**
     * Sets the value of the costAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountType }
     *     
     */
    public void setCostAmount(AmountType value) {
        this.costAmount = value;
    }

    /**
     * Is a process code used for grouping like operations.
     * Possible examples are:  Heat Treat, Dyeing
     * 
     * @return
     *     possible object is
     *     {@link CodeType }
     *     
     */
    public CodeType getProcessCode() {
        return processCode;
    }

    /**
     * Is a process code used for grouping like operations.
     * Possible examples are:  Heat Treat, Dyeing
     * 
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *     
     */
    public void setProcessCode(CodeType value) {
        this.processCode = value;
    }

    /**
     * Indicates whether the machine can be setup for the operation early or whether setup must happen at a specific time depending upon conditions.
     * 
     * Possible values:
     * - Early	
     * - NotDelayed	
     * - DelayedByPredicessor
     * - DelayedByMaterial
     * 
     * @return
     *     possible object is
     *     {@link CodesType }
     *     
     */
    public CodesType getMachineSetupDependencyCodes() {
        return machineSetupDependencyCodes;
    }

    /**
     * Indicates whether the machine can be setup for the operation early or whether setup must happen at a specific time depending upon conditions.
     * 
     * Possible values:
     * - Early	
     * - NotDelayed	
     * - DelayedByPredicessor
     * - DelayedByMaterial
     * 
     * @param value
     *     allowed object is
     *     {@link CodesType }
     *     
     */
    public void setMachineSetupDependencyCodes(CodesType value) {
        this.machineSetupDependencyCodes = value;
    }

    /**
     * Gets the value of the maximumParallelTeamsFactor property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getMaximumParallelTeamsFactor() {
        return maximumParallelTeamsFactor;
    }

    /**
     * Sets the value of the maximumParallelTeamsFactor property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setMaximumParallelTeamsFactor(BigDecimal value) {
        this.maximumParallelTeamsFactor = value;
    }

    /**
     * Gets the value of the fixedRejectQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link QuantityType }
     *     
     */
    public QuantityType getFixedRejectQuantity() {
        return fixedRejectQuantity;
    }

    /**
     * Sets the value of the fixedRejectQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link QuantityType }
     *     
     */
    public void setFixedRejectQuantity(QuantityType value) {
        this.fixedRejectQuantity = value;
    }

    /**
     * Gets the value of the rejectedQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link QuantityType }
     *     
     */
    public QuantityType getRejectedQuantity() {
        return rejectedQuantity;
    }

    /**
     * Sets the value of the rejectedQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link QuantityType }
     *     
     */
    public void setRejectedQuantity(QuantityType value) {
        this.rejectedQuantity = value;
    }

    /**
     * Gets the value of the batchSizeQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link QuantityType }
     *     
     */
    public QuantityType getBatchSizeQuantity() {
        return batchSizeQuantity;
    }

    /**
     * Sets the value of the batchSizeQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link QuantityType }
     *     
     */
    public void setBatchSizeQuantity(QuantityType value) {
        this.batchSizeQuantity = value;
    }

    /**
     * Gets the value of the perishedInOperationQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link QuantityType }
     *     
     */
    public QuantityType getPerishedInOperationQuantity() {
        return perishedInOperationQuantity;
    }

    /**
     * Sets the value of the perishedInOperationQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link QuantityType }
     *     
     */
    public void setPerishedInOperationQuantity(QuantityType value) {
        this.perishedInOperationQuantity = value;
    }

    /**
     * Gets the value of the perishedBetweenOperationsQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link QuantityType }
     *     
     */
    public QuantityType getPerishedBetweenOperationsQuantity() {
        return perishedBetweenOperationsQuantity;
    }

    /**
     * Sets the value of the perishedBetweenOperationsQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link QuantityType }
     *     
     */
    public void setPerishedBetweenOperationsQuantity(QuantityType value) {
        this.perishedBetweenOperationsQuantity = value;
    }

    /**
     * Gets the value of the transferLotQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link QuantityType }
     *     
     */
    public QuantityType getTransferLotQuantity() {
        return transferLotQuantity;
    }

    /**
     * Sets the value of the transferLotQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link QuantityType }
     *     
     */
    public void setTransferLotQuantity(QuantityType value) {
        this.transferLotQuantity = value;
    }

    /**
     * Gets the value of the setupTimeDuration property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getSetupTimeDuration() {
        return setupTimeDuration;
    }

    /**
     * Sets the value of the setupTimeDuration property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setSetupTimeDuration(Duration value) {
        this.setupTimeDuration = value;
    }

    /**
     * Gets the value of the waitTimeDuration property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getWaitTimeDuration() {
        return waitTimeDuration;
    }

    /**
     * Sets the value of the waitTimeDuration property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setWaitTimeDuration(Duration value) {
        this.waitTimeDuration = value;
    }

    /**
     * Gets the value of the runTimeDuration property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getRunTimeDuration() {
        return runTimeDuration;
    }

    /**
     * Sets the value of the runTimeDuration property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setRunTimeDuration(Duration value) {
        this.runTimeDuration = value;
    }

    /**
     * Gets the value of the tearDownDuration property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getTearDownDuration() {
        return tearDownDuration;
    }

    /**
     * Sets the value of the tearDownDuration property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setTearDownDuration(Duration value) {
        this.tearDownDuration = value;
    }

    /**
     * Gets the value of the batchDuration property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getBatchDuration() {
        return batchDuration;
    }

    /**
     * Sets the value of the batchDuration property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setBatchDuration(Duration value) {
        this.batchDuration = value;
    }

    /**
     * Gets the value of the queueDuration property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getQueueDuration() {
        return queueDuration;
    }

    /**
     * Sets the value of the queueDuration property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setQueueDuration(Duration value) {
        this.queueDuration = value;
    }

    /**
     * Gets the value of the moveDuration property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getMoveDuration() {
        return moveDuration;
    }

    /**
     * Sets the value of the moveDuration property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setMoveDuration(Duration value) {
        this.moveDuration = value;
    }

    /**
     * Gets the value of the fixedDuration property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getFixedDuration() {
        return fixedDuration;
    }

    /**
     * Sets the value of the fixedDuration property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setFixedDuration(Duration value) {
        this.fixedDuration = value;
    }

    /**
     * Gets the value of the rejectPercent property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getRejectPercent() {
        return rejectPercent;
    }

    /**
     * Sets the value of the rejectPercent property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setRejectPercent(BigDecimal value) {
        this.rejectPercent = value;
    }

    /**
     * Identifies the Location that the work is done.Gets the value of the site property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the site property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSite().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LocationType }
     * 
     * 
     */
    public List<LocationType> getSite() {
        if (site == null) {
            site = new ArrayList<LocationType>();
        }
        return this.site;
    }

    /**
     * Gets the value of the multipleRunSaveTimeIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMultipleRunSaveTimeIndicator() {
        return multipleRunSaveTimeIndicator;
    }

    /**
     * Sets the value of the multipleRunSaveTimeIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMultipleRunSaveTimeIndicator(Boolean value) {
        this.multipleRunSaveTimeIndicator = value;
    }

    /**
     * Indicates whether the operation should be tracked.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTrackingIndicator() {
        return trackingIndicator;
    }

    /**
     * Indicates whether the operation should be tracked.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTrackingIndicator(Boolean value) {
        this.trackingIndicator = value;
    }

    /**
     * Gets the value of the qualifiedResource property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the qualifiedResource property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQualifiedResource().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link QualifiedResourceType }
     * 
     * 
     */
    public List<QualifiedResourceType> getQualifiedResource() {
        if (qualifiedResource == null) {
            qualifiedResource = new ArrayList<QualifiedResourceType>();
        }
        return this.qualifiedResource;
    }

    /**
     * Gets the value of the charge property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the charge property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCharge().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ChargeType }
     * 
     * 
     */
    public List<ChargeType> getCharge() {
        if (charge == null) {
            charge = new ArrayList<ChargeType>();
        }
        return this.charge;
    }

    /**
     * Gets the value of the distribution property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the distribution property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDistribution().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DistributionType }
     * 
     * 
     */
    public List<DistributionType> getDistribution() {
        if (distribution == null) {
            distribution = new ArrayList<DistributionType>();
        }
        return this.distribution;
    }

    /**
     * Gets the value of the step property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the step property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStep().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StepType }
     * 
     * 
     */
    public List<StepType> getStep() {
        if (step == null) {
            step = new ArrayList<StepType>();
        }
        return this.step;
    }

    /**
     * Gets the value of the userArea property.
     * 
     * @return
     *     possible object is
     *     {@link UserAreaType }
     *     
     */
    public UserAreaType getUserArea() {
        return userArea;
    }

    /**
     * Sets the value of the userArea property.
     * 
     * @param value
     *     allowed object is
     *     {@link UserAreaType }
     *     
     */
    public void setUserArea(UserAreaType value) {
        this.userArea = value;
    }

}
