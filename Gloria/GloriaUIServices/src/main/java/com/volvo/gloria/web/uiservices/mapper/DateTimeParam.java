package com.volvo.gloria.web.uiservices.mapper;

import java.text.ParseException;
import java.util.Date;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.StringUtils;

import com.volvo.gloria.util.DateUtil;

/**
 * @QueryParam/@PathParam date conversion handling.
 * 
 */
public class DateTimeParam {

    private String dateParameter = null;

    public DateTimeParam(String dateParameter) {
        this.dateParameter = dateParameter;
    }

    public Date getDateTime() {
        if (!StringUtils.isEmpty(this.dateParameter)) {
            return parse();
        }
        return null;
    }

    private Date parse() {
        try {
            return DateUtil.getStringAsDate(this.dateParameter);
        } catch (ParseException e) {
            throw new WebApplicationException(onError(e));
        }
    }

    private Response onError(Exception e) {
        return Response.status(Status.BAD_REQUEST).entity("Invalid date parameter " + dateParameter + " " + e.getMessage()).build();
    }
}
