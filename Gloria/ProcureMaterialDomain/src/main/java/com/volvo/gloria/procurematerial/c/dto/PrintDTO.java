package com.volvo.gloria.procurematerial.c.dto;

import java.io.Serializable;

/**
 * To keep print information.
 */
public class PrintDTO implements Serializable {

    private static final long serialVersionUID = -7917947874785511544L;

    private Long id;
    private String partNo;
    private String partVersion;
    private int copies;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPartNo() {
        return partNo;
    }

    public void setPartNo(String partNo) {
        this.partNo = partNo;
    }

    public String getPartVersion() {
        return partVersion;
    }

    public void setPartVersion(String partVersion) {
        this.partVersion = partVersion;
    }

    public int getCopies() {
        return copies;
    }

    public void setCopies(int copies) {
        this.copies = copies;
    }
}
