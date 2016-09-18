package com.volvo.gloria.warehouse.c.dto;

import java.util.List;

import com.volvo.gloria.warehouse.c.BaySides;
import com.volvo.gloria.warehouse.c.Setup;

/**
 * data class for aisle/rack row.
 * 
 */
public class AisleRackRowTransformerDTO {

    private String code;
    private Setup setUp;
    private BaySides baySides;
    private long numberOfBay;
    private List<BaySettingTransformerDTO> baySettingTransformerDTOs;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BaySides getBaySides() {
        return baySides;
    }

    public void setBaySides(BaySides baySides) {
        this.baySides = baySides;
    }

    public Setup getSetUp() {
        return setUp;
    }

    public void setSetUp(Setup setUp) {
        this.setUp = setUp;
    }

    public long getNumberOfBay() {
        return numberOfBay;
    }

    public void setNumberOfBay(long numberOfBay) {
        this.numberOfBay = numberOfBay;
    }

    public List<BaySettingTransformerDTO> getBaySettingTransformerDTOs() {
        return baySettingTransformerDTOs;
    }

    public void setBaySettingTransformerDTOs(List<BaySettingTransformerDTO> baySettingTransformerDTOs) {
        this.baySettingTransformerDTOs = baySettingTransformerDTOs;
    }

}
