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
 * Annotation class for String validation.
 */
@Documented
@Constraint(validatedBy = GloriaStringSizeValidator.class)
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface GloriaStringSize {
    String message() default  "GLO_ERR_45";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * @return size the element must be higher or equal to
     */
    int min() default 0;

    /**
     * @return size the element must be lower or equal to
     */
    int max() default Integer.MAX_VALUE;
}


