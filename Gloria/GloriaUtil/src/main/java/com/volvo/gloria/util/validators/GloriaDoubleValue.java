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
 * Annotation class for Double validation.
 */
@Documented
@Constraint(validatedBy = GloriaDoubleValueValidator.class)
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface GloriaDoubleValue {
    String message() default  "GLO_ERR_59";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * @return size the element must be higher or equal to
     */
    double min() default 0;

    /**
     * @return size the element must be lower or equal to
     */
    double max() default Double.MAX_VALUE;
}


