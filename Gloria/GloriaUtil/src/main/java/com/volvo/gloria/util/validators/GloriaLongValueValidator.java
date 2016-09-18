package com.volvo.gloria.util.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validation implementation for long.
 */
public class GloriaLongValueValidator implements ConstraintValidator<GloriaLongValue, Long> {

    private long min;
    private long max;

    @Override
    public void initialize(GloriaLongValue longValue) {
        this.min = longValue.min();
        this.max = longValue.max();
    }
    
    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value < min || value > max) {
            return false;
        }

        return true;
    }
}
