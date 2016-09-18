package com.volvo.gloria.procurematerial.c.dto;

import java.io.Serializable;

/**
 * DTO to return the status of a rest call.
 * 
 * true/false
 * 
 */
public class ResponseDTO implements Serializable {

    private static final long serialVersionUID = 6677784935456960991L;

    private boolean success;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
