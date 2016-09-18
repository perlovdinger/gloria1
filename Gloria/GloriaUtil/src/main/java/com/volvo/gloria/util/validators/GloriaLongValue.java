/**
 * 
 */
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
 * Annotation class for Long validation.
 */
@Documented
@Constraint(validatedBy = GloriaLongValueValidator.class)
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface GloriaLongValue {
    String message() default  "GLO_ERR_59";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * @return size the element must be higher or equal to
     */
    long min() default 0;

    /**
     * @return size the element must be lower or equal to
     */
    long max() default Long.MAX_VALUE;
}


