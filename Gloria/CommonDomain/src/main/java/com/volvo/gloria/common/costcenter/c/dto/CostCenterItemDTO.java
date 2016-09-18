package com.volvo.gloria.common.costcenter.c.dto;

import java.sql.Date;

import com.volvo.gloria.common.c.CostCenterActionType;

/**
 * DTO class for Cost Center Item.
 */
public class CostCenterItemDTO {

    private CostCenterActionType action;
    private String descriptionShort;
    private String descriptionLong;
    private String costCenter;
    private String companyCode;
    private String personResponsibleUserId;
    private String personResponsibleName;
    private Date effectiveStartDate;
    private Date effectiveEndDate;
    private boolean primaryPostings;
    private boolean planning;
    private boolean actualSecondaryCosts;
    private boolean actualRevenuePostings;
    private boolean commitmentUpdate;
    private boolean planSecondaryCosts;
    private boolean planningRevenues;

    public CostCenterActionType getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = CostCenterActionType.fromValue(action);
    }

    public String getCostCenter() {
        return costCenter;
    }

    public String getDescriptionShort() {
        return descriptionShort;
    }

    public void setDescriptionShort(String descriptionShort) {
        this.descriptionShort = descriptionShort;
    }

    public String getDescriptionLong() {
        return descriptionLong;
    }

    public void setDescriptionLong(String descriptionLong) {
        this.descriptionLong = descriptionLong;
    }

    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getPersonResponsibleUserId() {
        return personResponsibleUserId;
    }

    public void setPersonResponsibleUserId(String personResponsibleUserId) {
        this.personResponsibleUserId = personResponsibleUserId;
    }

    public String getPersonResponsibleName() {
        return personResponsibleName;
    }

    public void setPersonResponsibleName(String personResponsibleName) {
        this.personResponsibleName = personResponsibleName;
    }

    public Date getEffectiveStartDate() {
        return effectiveStartDate;
    }

    public void setEffectiveStartDate(Date effictiveStartDate) {
        this.effectiveStartDate = effictiveStartDate;
    }

    public Date getEffectiveEndDate() {
        return effectiveEndDate;
    }

    public void setEffectiveEndDate(Date effectiveEndDate) {
        this.effectiveEndDate = effectiveEndDate;
    }

    public boolean isPrimaryPostings() {
        return primaryPostings;
    }

    public void setPrimaryPostings(boolean primaryPostings) {
        this.primaryPostings = primaryPostings;
    }

    public boolean isPlanning() {
        return planning;
    }

    public void setPlanning(boolean planning) {
        this.planning = planning;
    }

    public boolean isActualSecondaryCosts() {
        return actualSecondaryCosts;
    }

    public void setActualSecondaryCosts(boolean actualSecondaryCosts) {
        this.actualSecondaryCosts = actualSecondaryCosts;
    }

    public boolean isActualRevenuePostings() {
        return actualRevenuePostings;
    }

    public void setActualRevenuePostings(boolean actualRevenuePostings) {
        this.actualRevenuePostings = actualRevenuePostings;
    }

    public boolean isCommitmentUpdate() {
        return commitmentUpdate;
    }

    public void setCommitmentUpdate(boolean commitmentUpdate) {
        this.commitmentUpdate = commitmentUpdate;
    }

    public boolean isPlanSecondaryCosts() {
        return planSecondaryCosts;
    }

    public void setPlanSecondaryCosts(boolean planSecondaryCosts) {
        this.planSecondaryCosts = planSecondaryCosts;
    }

    public boolean isPlanningRevenues() {
        return planningRevenues;
    }

    public void setPlanningRevenues(boolean planningRevenues) {
        this.planningRevenues = planningRevenues;
    }

    public void setAction(CostCenterActionType action) {
        this.action = action;
    }
}
