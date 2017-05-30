/**
 * 
 */
package org.test.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * @author jcgueriaud
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {DateStartEndFieldsValidator.class})
public @interface DateStartEndFields {

    String message() default "{error.dateField}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
 
    String startField();
 
    String endField();
}