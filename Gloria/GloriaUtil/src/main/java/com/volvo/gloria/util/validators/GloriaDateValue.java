package com.volvo.gloria.util.validators;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Defining an annotation for the Date validation.
 */
@Documented
@Constraint(validatedBy = GloriaDateValueValidator.class)
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface GloriaDateValue {

    String message() default "GLO_ERR_64";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /*
     * Max years attribute is used to check for the valid future year. This value is as per the business requirement and also varies based on attribute. So we
     * cannot predict the max default value. Hence we are setting default value as 0. In the validator, max value validation is taken care and call happen only
     * if the max year is greaterthan 0.
     */

    int maxYears() default 0;

}
