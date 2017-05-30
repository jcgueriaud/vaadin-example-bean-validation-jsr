package org.test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatusHandler;
import com.vaadin.data.ValidationResult;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Composite;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class BeanValidatorLayout extends Composite  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7600167433897257964L;
	private TextField value = new TextField("Value");
	private DateField dateFrom = new DateField("Date from");
	private DateField dateTo = new DateField("Date to");
	
	private Button validateBoundBeanButton = new Button("ValidateBound bean", this::validate);
	
	private Label checkErrorLabel = new Label();
	
	private Label computedVaadinErrorLabel = new Label();
	private Pojo pojo = new Pojo();
	
	private Binder<Pojo> binder = new BeanValidationBinder<Pojo>(Pojo.class);
	// Try 
//	private Binder<Pojo> binder = new JSR303ValidationBinder<Pojo>(Pojo.class);
	
    private static Validator validator;
    
    /**
	 * 
	 */
	public BeanValidatorLayout() {
		setCompositionRoot(getLayout());
	}
    
	private VerticalLayout getLayout() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        checkErrorLabel.setContentMode(ContentMode.HTML);
        computedVaadinErrorLabel.setContentMode(ContentMode.HTML);
		final VerticalLayout layout = new VerticalLayout();
		layout.addComponents(value,dateFrom,dateTo,validateBoundBeanButton, checkErrorLabel,computedVaadinErrorLabel);
		
		/*
		 * This is not working with BeanValidationBinder and hibernate validator
		binder.bind(value, Pojo::getValue, Pojo::setValue);
		binder.bind(dateFrom, Pojo::getDateFrom, Pojo::setDateFrom);
		binder.bind(dateTo, Pojo::getDateTo, Pojo::setDateTo);
		*/
		
		binder.bind(value, "value");
		binder.bind(dateFrom,"dateFrom");
		binder.bind(dateTo, "dateTo");
		
		binder.setBean(pojo); // binder.readBean(pojo);
		
	//	binder.withValidator(pojo ->!"test".equals(pojo.getValue()),"Error added bean validation");
		
//		binder.setStatusLabel(computedVaadinErrorLabel);
	
	BinderValidationStatusHandler<Pojo> defaultHandler = binder.getValidationStatusHandler();

		binder.setValidationStatusHandler(status -> {
			System.out.println("setValidationStatusHandler");
			List<ValidationResult> errors = status.getValidationErrors();

		    // collect all bean level error messages into a single string,
		    // separating each message with a <br> tag
		    String errorMessage = errors.stream().map(ValidationResult::getErrorMessage)
		        // sanitize the individual error strings to avoid code injection
		        // since we are displaying the resulting string as HTML
		        .map(errorString -> Jsoup.clean(errorString, Whitelist.simpleText()))
		        .collect(Collectors.joining("<br/>"));

		    // finally, display all bean level validation errors in a single label
		    computedVaadinErrorLabel.setValue(errorMessage);
		    System.out.println("errorMessage"+errorMessage);
		    computedVaadinErrorLabel.setVisible(!errorMessage.isEmpty());

		    // Let the default handler show messages for each field
		    defaultHandler.statusChange(status);
		});
		
		return layout;
	}
	
	private void validate(ClickEvent e){
		checkErrorLabel.setStyleName(ValoTheme.LABEL_SUCCESS);
		if (binder.isValid()){ // if readBean binder.writeBeanIfValid(pojo)
			Notification.show("valid", Notification.Type.HUMANIZED_MESSAGE);
		} else {
			Notification.show("not valid", Notification.Type.ERROR_MESSAGE);
		}
		String errorMessage = "";
        Set<ConstraintViolation<Pojo>> constraintViolations =
                validator.validate( pojo );
        
        for (ConstraintViolation<Pojo> constraintViolation : constraintViolations) {
        	checkErrorLabel.setStyleName(ValoTheme.LABEL_FAILURE);
        	
        	errorMessage += constraintViolation.getPropertyPath().toString() + "--" +constraintViolation.getMessage()+ "<br/>";
		}
        checkErrorLabel.setValue(errorMessage);
	}

}
