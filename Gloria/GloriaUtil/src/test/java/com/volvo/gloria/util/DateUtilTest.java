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

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class DateUtilTest {

    @Test
    public void testDateWithZeroTime() {
        // Arrange
        // Act
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.DAY_OF_MONTH, 29);
        cal.set(Calendar.YEAR, 2013);
        cal.set(Calendar.MONTH, 8);
        Date date = DateUtil.getDateWithZeroTime(cal.getTime());
        // Assert
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        assertEquals(0, calendar.get(Calendar.HOUR));
        assertEquals(0, calendar.get(Calendar.MINUTE));
        assertEquals(0, calendar.get(Calendar.SECOND));
        assertEquals(0, calendar.get(Calendar.MILLISECOND));
        assertEquals(2013, calendar.get(Calendar.YEAR));
        assertEquals(8, calendar.get(Calendar.MONTH));
        assertEquals(29, calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void testCompareDates() {
        // Arrange
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        Date now = calendar.getTime();

        calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 1);
        Date afterOneHour = calendar.getTime();
        // Act
        // Assert
        assertEquals(0, DateUtil.compareDates(null, null));
        assertEquals(1, DateUtil.compareDates(now, null));
        assertEquals(-1, DateUtil.compareDates(null, now));
        assertEquals(0, DateUtil.compareDates(now, now));
        assertEquals(0, DateUtil.compareDates(afterOneHour, afterOneHour));
        assertEquals(-1, DateUtil.compareDates(now, afterOneHour));
    }

    @Test
    public void getSqlForeverDate() {
        // Arrange
        // Act
        java.sql.Date date = DateUtil.getForeverSqlDate();
        // Assert
        assertEquals("2099-12-30", date.toString());

    }
    
    @Test
    public void getcalculateWeekForAGivenDate() throws ParseException {
        // Arrange
        // Act
        String weekno = DateUtil.calculateWeekForAGivenDate(DateUtil.getStringAsDate("2013-12-13"));
        // Assert
        assertEquals("1350", weekno);

    }

    @Test
    public void testDateAfterJan01012014() throws ParseException {
        Date orderDate = DateUtil.getStringAsDate("01-04-2014 00:00:00", "MM-dd-yyyy HH:mm:ss");
        Assert.assertTrue(DateUtil.isDateAfterPreviousYear(orderDate, 2014, 0, 1));
    }
    
    @Test
    public void testDateBeforeJan01012014() throws ParseException {
        Date orderDate = DateUtil.getStringAsDate("12-31-2013 00:00:00", "MM-dd-yyyy HH:mm:ss");
        Assert.assertFalse(DateUtil.isDateAfterPreviousYear(orderDate, 2014, 0 , 1));
    }
    
    @Test
    public void testCalculateWorkingDays() throws ParseException {
        // Arrange
        Date start = DateUtil.getStringAsDate("12-28-2013 00:00:00", "MM-dd-yyyy HH:mm:ss");
        Date end = DateUtil.getStringAsDate("12-31-2013 00:00:00", "MM-dd-yyyy HH:mm:ss");
        Assert.assertEquals(1, DateUtil.calculateWorkingDays(start, end));
    }
        
    @Test
    public void testBugGLO6633() throws ParseException {
        // Arrange
        Date start = DateUtil.getStringAsDate("03-04-2016 00:00:00", "MM-dd-yyyy HH:mm:ss");
        Date end = DateUtil.getStringAsDate("03-09-2016 00:00:00", "MM-dd-yyyy HH:mm:ss");
        Assert.assertEquals(3, DateUtil.calculateWorkingDays(start, end));
    }        

    @Test
    public void testBugMinusGLO6633() throws ParseException {
        // Arrange
        Date start = DateUtil.getStringAsDate("03-09-2016 00:00:00", "MM-dd-yyyy HH:mm:ss");
        Date end = DateUtil.getStringAsDate("03-04-2016 00:00:00", "MM-dd-yyyy HH:mm:ss");
        Assert.assertEquals(-3, DateUtil.calculateWorkingDays(start, end));
    }  
    
    @Test
    public void testCheckDatesFromSameWeek() {
        // arrange
        Calendar calendarOne = Calendar.getInstance();
        calendarOne.setTime(new Date());
        Date dateOne = calendarOne.getTime();
        Calendar calendarTwo = Calendar.getInstance();
        calendarTwo.setTime(new Date());
        Date dateTwo = calendarTwo.getTime();
        // assert
        Assert.assertTrue(DateUtil.areDatesFromSameWeek(dateOne, dateTwo));
    }
    
    @Test
    public void testCheckDatesFromDifferentWeek() {
        // arrange
        Calendar calendarOne = Calendar.getInstance();
        calendarOne.setTime(new Date());
        calendarOne.add(Calendar.DAY_OF_WEEK, +7);
        Date dateOne = calendarOne.getTime();
        Calendar calendarTwo = Calendar.getInstance();
        calendarTwo.setTime(new Date());
        Date dateTwo = calendarTwo.getTime();
        // assert
        Assert.assertFalse(DateUtil.areDatesFromSameWeek(dateOne, dateTwo));
    }
    
    @Test
    public void testIsPastDate() {
        //arrange
        Calendar calendarPast = Calendar.getInstance();
        calendarPast.setTime(new Date());
        calendarPast.add(Calendar.DAY_OF_MONTH, -5);
        
        Calendar calendarFuture = Calendar.getInstance();
        calendarFuture.setTime(new Date());
        calendarFuture.add(Calendar.DAY_OF_MONTH, 2);
        //assert
        Assert.assertTrue(DateUtil.isPastDate(calendarPast.getTime()));
        Assert.assertFalse(DateUtil.isPastDate(calendarFuture.getTime()));
    }
    
    @Test
    public void testIsFutureDate() {
        //arrange
        Calendar calendarPast = Calendar.getInstance();
        calendarPast.setTime(new Date());
        calendarPast.add(Calendar.DAY_OF_MONTH, -5);
        
        Calendar calendarFuture = Calendar.getInstance();
        calendarFuture.setTime(new Date());
        calendarFuture.add(Calendar.DAY_OF_MONTH, 2);
        //assert
        Assert.assertFalse(DateUtil.isFutureDate(calendarPast.getTime()));
        Assert.assertTrue(DateUtil.isFutureDate(calendarFuture.getTime()));
    }
}
