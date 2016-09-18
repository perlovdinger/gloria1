package com.volvo.gloria.util.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validation implementation for Strings.
 */
public class GloriaStringSizeValidator implements ConstraintValidator<GloriaStringSize, String> {
    private int min;
    private int max;

    @Override
    public void initialize(GloriaStringSize stringSize) {
        this.min = stringSize.min();
        this.max = stringSize.max();
        validateParameters();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext context) {
        if (s == null) {
            return true;
        }
        int length = s.length();
        return length >= min && length <= max;
    }

    private void validateParameters() {
        if (min < 0) {
            throw new IllegalArgumentException("The min parameter cannot be negative.");
        }
        if (max < 0) {
            throw new IllegalArgumentException("The max parameter cannot be negative.");
        }
        if (max < min) {
            throw new IllegalArgumentException("The length cannot be negative.");
        }
    }
}
