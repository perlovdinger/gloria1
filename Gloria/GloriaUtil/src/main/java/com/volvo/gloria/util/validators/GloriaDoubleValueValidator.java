package com.volvo.gloria.util.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validation implementation for double.
 */
public class GloriaDoubleValueValidator implements ConstraintValidator<GloriaDoubleValue, Double> {

    private double min;
    private double max;

    @Override
    public void initialize(GloriaDoubleValue longValue) {
        this.min = longValue.min();
        this.max = longValue.max();
    }
    
    @Override
    public boolean isValid(Double value, ConstraintValidatorContext context) {
        if (value < min || value > max) {
            return false;
        }

        return true;
    }
}
