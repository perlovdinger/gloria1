package com.volvo.gloria.util;

import java.util.Date;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.openjpa.persistence.jest.JSONObject;

import com.volvo.jvs.runtime.platform.SystemException;

/**
 * Gloria system exception for unrecoverable exceptions.
 */
public class GloriaSystemException extends SystemException {
    private static final long serialVersionUID = -593674690832207181L;
    private String exception;
    private String dateTime;
    private String detail;

    public GloriaSystemException(String message, String detail) {
        super(message);
        this.detail = detail;
        this.dateTime = DateUtil.getDateTimeAsString(new Date());
    }

    public GloriaSystemException(Throwable cause, String... message) {
        super(message[0], cause);
        if (message.length > 1) {
            this.detail = message[1];
        }
        this.exception = cause.getClass().getSimpleName();
        this.dateTime = DateUtil.getDateTimeAsString(new Date());
    }
    
    public String getTimestamp() {
        return dateTime;
    }
    
    public String getException() {
        return exception;
    }

    public String getDetail() {
        return detail;
    }

    @Override
    public String toString() {
        return "{\"exception\" : \"" + getException() + "\",\"message\" : \"" + getMessage() + "\",\"timestamp\" : \"" + getTimestamp() + "\",\"detail\" : \""
                + getDetail() + "\"}";
    }
    
    public String toJson() {
        JSONObject json = new JSONObject("GloriaApplicationException", -1, false);
        json.set("exception", StringEscapeUtils.escapeJavaScript(getException()));
        json.set("message", StringEscapeUtils.escapeJavaScript(getMessage()));
        json.set("timestamp", StringEscapeUtils.escapeJavaScript(getTimestamp()));
        json.set("detail", StringEscapeUtils.escapeJavaScript(getDetail()));
        return json.toString();
    }
}
