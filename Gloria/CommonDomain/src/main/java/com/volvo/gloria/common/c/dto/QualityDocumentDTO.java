/**
 * 
 */
package com.volvo.gloria.common.c.dto;

import java.io.Serializable;

public class QualityDocumentDTO implements Serializable {

    private static final long serialVersionUID = -2083152525295442443L;

    private long id;
    private String code;
    private String name;
    private long displaySeq;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDisplaySeq() {
        return displaySeq;
    }

    public void setDisplaySeq(long displaySeq) {
        this.displaySeq = displaySeq;
    }

}
