package com.volvo.gloria.util.validators;

import java.text.ParseException;
import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.volvo.gloria.util.DateUtil;

/**
 * Implementation of the Date validator.
 */
public class GloriaDateValueValidator implements ConstraintValidator<GloriaDateValue, Date> {

    private int max;

    @Override
    public void initialize(GloriaDateValue dateValue) {
        if (dateValue.maxYears() > 0) {
            this.max = dateValue.maxYears();
        }
    }

    @Override
    public boolean isValid(Date value, ConstraintValidatorContext context) {
        if (value != null) {
            Date sqlDate = DateUtil.getDateWithZeroTime(DateUtil.getSqlDate());
            int comparedValue = value.compareTo(sqlDate);
            if (comparedValue >= 0) {
                try {
                    if (this.max > 0 && (!(DateUtil.getMaxYear(this.max) > value.getTime()))) {
                        return false;
                    }
                } catch (ParseException e) {
                    e.getMessage();
                }
                return true;
            }
            return false;
        }
        return true;
    }
}
