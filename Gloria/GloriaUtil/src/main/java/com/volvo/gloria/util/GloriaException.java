package com.volvo.gloria.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Checked exception for Gloria exception handling.
 */
public class GloriaException extends Throwable {
    private static final long serialVersionUID = -1023973279333198312L;
    private Calendar calendar = Calendar.getInstance();
    private String errorCode;
    private String message;

    public GloriaException(String message, String errorCode) {
        this.calendar.setTime(new Date());
        this.message = message;
        this.errorCode = errorCode;
    }

    public GloriaException(Date date, String message, String errorCode) {
        this.calendar.setTime(date);
        this.message = message;
        this.errorCode = errorCode;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

}
