package com.volvo.gloria.warehouse.c.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Bay Setting Tranformer. 
 */
public class BaySettingTransformerDTO  implements Serializable  {

    private static final long serialVersionUID = -3520611092342471665L;

    private String bayCode;
    private long numberOfLevels;
    private long numberOfPositions;
    private List<BinLocationTransformerDTO> binLocationTransformerDTOs;
    
    public String getBayCode() {
        return bayCode;
    }
    public void setBayCode(String bayCode) {
        this.bayCode = bayCode;
    }
    public long getNumberOfLevels() {
        return numberOfLevels;
    }
    public void setNumberOfLevels(long numberOfLevels) {
        this.numberOfLevels = numberOfLevels;
    }
    public long getNumberOfPositions() {
        return numberOfPositions;
    }
    public void setNumberOfPositions(long numberOfPositions) {
        this.numberOfPositions = numberOfPositions;
    }
    public List<BinLocationTransformerDTO> getBinLocationTransformerDTOs() {
        return binLocationTransformerDTOs;
    }
    public void setBinLocationTransformerDTOs(List<BinLocationTransformerDTO> binLocationTransformerDTOs) {
        this.binLocationTransformerDTOs = binLocationTransformerDTOs;
    }
    
    
}
