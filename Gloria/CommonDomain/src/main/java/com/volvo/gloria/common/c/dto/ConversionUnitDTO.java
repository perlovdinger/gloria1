package com.volvo.gloria.common.c.dto;

/**
 * DTO class for Units conversion.
 * 
 */
public class ConversionUnitDTO {

    private long id;
    private String applFrom;
    private String codeFrom;
    private String applTo;
    private String codeTo;
    private long dividedBy;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getApplFrom() {
        return applFrom;
    }

    public void setApplFrom(String applFrom) {
        this.applFrom = applFrom;
    }

    public String getCodeFrom() {
        return codeFrom;
    }

    public void setCodeFrom(String codeFrom) {
        this.codeFrom = codeFrom;
    }

    public String getApplTo() {
        return applTo;
    }

    public void setApplTo(String applTo) {
        this.applTo = applTo;
    }

    public String getCodeTo() {
        return codeTo;
    }

    public void setCodeTo(String codeTo) {
        this.codeTo = codeTo;
    }

    public long getDividedBy() {
        return dividedBy;
    }

    public void setDividedBy(long dividedBy) {
        this.dividedBy = dividedBy;
    }
}
