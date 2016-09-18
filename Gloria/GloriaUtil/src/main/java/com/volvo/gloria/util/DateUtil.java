/*
 * Copyright 2009 Volvo Information Technology AB 
 * 
 * Licensed under the Volvo IT Corporate Source License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *      http://java.volvo.com/licenses/CORPORATE-SOURCE-LICENSE 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.volvo.gloria.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.volvo.gloria.util.c.GloriaExceptionConstants;

/**
 * Utility class for Dates.
 */
public abstract class DateUtil {    
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DateUtil.class);
    
    private static final int NUM_59 = 59;
    private static final int NUM_11 = 11;
    private static final int FOREVER_DAY = 30;
    private static final int FOREVER_YEAR = 2099;
    static final String DATE_PATTERN = "yyyy-MM-dd";
    static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    private static final int ISO_8601_MINIMALDAYS_4 = 4;
    
    private DateUtil() {
        
    }

    public static String getGloriaDateformat() {
        return DATE_PATTERN;
    }

    public static String getGloriaDateTimeFormat() {
        return DATE_TIME_PATTERN;
    }

    public static synchronized Date getDateWithZeroTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.AM_PM, Calendar.AM);
        return cal.getTime();
    }
    
    public static synchronized Date getDateWithEndTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR, NUM_11);
        cal.set(Calendar.MINUTE, NUM_59);
        cal.set(Calendar.SECOND, NUM_59);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.AM_PM, Calendar.PM);
        return cal.getTime();
    }

    public static synchronized int compareDates(Date thisDate, Date otherDate) {
        if (thisDate == null && otherDate == null) {
            return 0;
        }
        if (thisDate == null) {
            return -1;
        }
        if (otherDate == null) {
            return 1;
        }
        return thisDate.compareTo(otherDate);
    }

    public static synchronized Date convertDateFromUtcToTimeZone(Date utcDate, TimeZone timezone) throws ParseException {
        SimpleDateFormat tzFormat = new SimpleDateFormat(DATE_TIME_PATTERN);
        tzFormat.setTimeZone(timezone);
        return new SimpleDateFormat(DATE_TIME_PATTERN).parse(tzFormat.format(utcDate));
    }

    public static synchronized Date getCurrentTimeZoneDate(TimeZone timezone) throws ParseException {
        SimpleDateFormat tzFormat = new SimpleDateFormat(DATE_TIME_PATTERN);
        tzFormat.setTimeZone(timezone);
        return new SimpleDateFormat(DATE_TIME_PATTERN).parse(tzFormat.format(new Date()));
    }

    public static synchronized Date getStringAsDate(String dateToFormat) throws ParseException {
        SimpleDateFormat utcFormat = new SimpleDateFormat(DATE_PATTERN);
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return utcFormat.parse(dateToFormat);
    }
    
    public static synchronized Date getStringAsDate(String dateToFormat, String pattern) throws ParseException {
        SimpleDateFormat utcFormat = new SimpleDateFormat(pattern);
        return utcFormat.parse(dateToFormat);
    }

    public static synchronized Date getForeverDate() {
        return new GregorianCalendar(FOREVER_YEAR, Calendar.DECEMBER, FOREVER_DAY).getTime();

    }

    public static synchronized java.sql.Date getForeverSqlDate() {
        java.util.Date utilDate = new GregorianCalendar(FOREVER_YEAR, Calendar.DECEMBER, FOREVER_DAY).getTime();
        return new java.sql.Date(utilDate.getTime());
    }

    public static synchronized java.sql.Date getSqlDate() {
        return new java.sql.Date(System.currentTimeMillis());
    }

    public static synchronized java.sql.Date getSqlDate(int year, int month, int day) {
        java.util.Date utilDate = new GregorianCalendar(year, month, day).getTime();
        return new java.sql.Date(utilDate.getTime());
    }

    public static synchronized Date getDate(int year, int month, int day) {
        return new GregorianCalendar(year, month, day).getTime();
    }
    
    public static synchronized Date getDateFromWeekNo(int year, int week, int dayOfweek) {
        Calendar calendar = new GregorianCalendar();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setMinimalDaysInFirstWeek(ISO_8601_MINIMALDAYS_4);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.WEEK_OF_YEAR, week);
        calendar.set(Calendar.DAY_OF_WEEK, dayOfweek);
        return getDateWithZeroTime(calendar.getTime());
    }  

    public static synchronized String getDateTimeAsString(Date date) {
        SimpleDateFormat utcFormat = new SimpleDateFormat(DATE_TIME_PATTERN);
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return utcFormat.format(date);
    }

    public static synchronized String getDateWithoutTimeAsString(Date date) {
        if (date != null) {
            SimpleDateFormat utcFormat = new SimpleDateFormat(DATE_PATTERN);
            return utcFormat.format(date);
        }
        return "";
    }

    public static synchronized Date getNextDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        return calendar.getTime();
    }
    
    public static synchronized java.sql.Date getPreviousDate(java.sql.Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -1);
        return new java.sql.Date(calendar.getTimeInMillis());
    }

    public static synchronized String calculateWeekForAGivenDate(Date date) {
        return new SimpleDateFormat("yyww").format(date);
    }
    
    public static synchronized long getMaxYear(int maxYear) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getDateWithZeroTime(getCurrentUTCDate()));
        int year = cal.get(Calendar.YEAR);
        cal.set(Calendar.YEAR, year + maxYear);
        return cal.getTime().getTime();
    }

    public static boolean isDateAfterPreviousYear(Date orderDate, int validYear, int validMonth, int validDay) {
        // @TODO: commented for now, considering open orders from 01-Jan-2014
        Calendar cal = Calendar.getInstance();
        cal.set(validYear, validMonth, validDay, 0, 0);
        Date dateStartingFrom = cal.getTime();
        boolean isOlder = false;
        if (orderDate.after(dateStartingFrom)) {
            isOlder = true;
        }
        return isOlder;
    }
    
    /**
     * Calculates the number of workdays between twoo dates.
     */
    public static int calculateWorkingDays(Date startDateParam, Date endDateParam) {
        Date startDate = null;
        Date endDate = null;
        int sign = 1;
        if (startDateParam.before(endDateParam)) {
            startDate = startDateParam;
            endDate = endDateParam;
        } else {
            startDate = endDateParam;
            endDate = startDateParam;
            sign = -1;
        }
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);
        int workDays = 0;
        if (startCal.getTimeInMillis() == endCal.getTimeInMillis()) {
            if (startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                ++workDays;
            }
        }
        if (startCal.getTimeInMillis() > endCal.getTimeInMillis()) {
            startCal.setTime(endDate);
            endCal.setTime(startDate);
        }

        do {
            if (startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                ++workDays;
            }
            startCal.add(Calendar.DAY_OF_MONTH, 1);
        } while (startCal.getTimeInMillis() <= endCal.getTimeInMillis());

        if (workDays > 0) {
            return (workDays - 1) * sign;
        } else {
            return workDays;
        }
    }
    
    public static boolean isSameDate(Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return true;
        }

        if (date1 != null && date2 != null && getDateWithoutTimeAsString(date1).equalsIgnoreCase(getDateWithoutTimeAsString(date2))) {
            return true;
        }
        return false;
    }
    
    public static boolean isPastDate(Date dateToCompare) {
        return getDateWithZeroTime(dateToCompare).before(getDateWithZeroTime(getSqlDate()));
    }
    
    public static boolean isFutureDate(Date dateToCompare) {
        return getDateWithZeroTime(dateToCompare).after(getDateWithZeroTime(getSqlDate()));
    }    
    
    public static boolean areDatesFromSameWeek(Date dateValueOne, Date dateValueTwo) {
        Calendar calDateValueOne = Calendar.getInstance();
        calDateValueOne.setFirstDayOfWeek(Calendar.MONDAY);
        calDateValueOne.setMinimalDaysInFirstWeek(ISO_8601_MINIMALDAYS_4);
        calDateValueOne.setTime(dateValueOne);
        
        Calendar calDateValueTwo = Calendar.getInstance();
        calDateValueTwo.setFirstDayOfWeek(Calendar.MONDAY);
        calDateValueTwo.setMinimalDaysInFirstWeek(ISO_8601_MINIMALDAYS_4);
        calDateValueTwo.setTime(dateValueTwo);
        if (calDateValueOne.get(Calendar.WEEK_OF_YEAR) == calDateValueTwo.get(Calendar.WEEK_OF_YEAR)
                && calDateValueOne.get(Calendar.YEAR) == calDateValueTwo.get(Calendar.YEAR)) {
            return true;
        }
        return false;
    }
    
    public static synchronized Date getCurrentUTCDateTime() {
        return getCurrentUTCDateTime(DATE_TIME_PATTERN);
    }

    public static synchronized Date getCurrentUTCDate() {
        return getCurrentUTCDate(DATE_PATTERN);
    }

    public static synchronized Date getCurrentUTCDateTime(String dateTimeFormat) {
        try {
            SimpleDateFormat utcFormat = new SimpleDateFormat(dateTimeFormat);
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return new SimpleDateFormat(dateTimeFormat).parse(utcFormat.format(new Date()));
        } catch (ParseException pe) {
            LOGGER.error("Exception parsing current timezone DATETIME - " + pe.getMessage());
            throw new GloriaSystemException("Parse exception parsing current timezone DATETIME", GloriaExceptionConstants.INVALID_DATE_FORMAT);
        }
    }

    public static synchronized Date getCurrentUTCDate(String dateFormat) {
        try {
            SimpleDateFormat utcFormat = new SimpleDateFormat(dateFormat);
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return new SimpleDateFormat(dateFormat).parse(utcFormat.format(new Date()));
        } catch (ParseException pe) {
            LOGGER.error("Exception parsing current timezone DATE - " + pe.getMessage());
            throw new GloriaSystemException("Exception parsing current timezone DATE", GloriaExceptionConstants.INVALID_DATE_FORMAT);
        }
    }
    
    public static synchronized Timestamp getUTCTimeStamp() {
        return new Timestamp(getCurrentUTCDateTime().getTime());
    }
    
    public static synchronized XMLGregorianCalendar getXMLGreorianCalendar(Date date) throws DatatypeConfigurationException {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        return DatatypeFactory.newInstance().newXMLGregorianCalendarDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH),
                                                                         DatatypeConstants.FIELD_UNDEFINED);
    }
    
    public static synchronized XMLGregorianCalendar getXMLGreorianCalendarWithTimeStamp(Date date) throws DatatypeConfigurationException {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH),
                                                                     cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND),
                                                                     cal.get(Calendar.MILLISECOND), DatatypeConstants.FIELD_UNDEFINED);
    }
}
