/**
 * 
 */
package org.test.validator;

import java.lang.reflect.Field;
import java.time.LocalDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author jcgueriaud
 *
 */
public class DateStartEndFieldsValidator implements ConstraintValidator<DateStartEndFields, Object> {
	 
    private String startField;
    private String endField;
 
    @Override
    public void initialize(DateStartEndFields constraint) {
        startField = constraint.startField();
        endField = constraint.endField();
    }
 
    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        try {
        	LocalDate startDate = (LocalDate) getFieldValue(object, startField);
        	LocalDate endDate = (LocalDate)  getFieldValue(object, endField);
            
            return startDate != null && !startDate.isAfter(endDate);
        } catch (Exception e) {
            // log error
            return false;
        }
    }
 
    private Object getFieldValue(Object object, String fieldName) throws Exception {
        Class<?> clazz = object.getClass();
        Field foundField = clazz.getDeclaredField(fieldName);
        foundField.setAccessible(true);
        return foundField.get(object);
    }
 
}