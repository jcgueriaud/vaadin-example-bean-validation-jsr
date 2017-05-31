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
import org.test.validator.JSR303ValidationBinder;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
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

public class JSRBufferedValidatorLayout extends Composite  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7600167433897257964L;
	private TextField value = new TextField("Value");
	private DateField dateFrom = new DateField("Date from");
	private DateField dateTo = new DateField("Date to");
	
	private Button validateBoundBeanButton = new Button("Submit and validateBound bean", this::validate);
	
	private Label checkErrorLabel = new Label();
	
	private Label computedVaadinErrorLabel = new Label();
	private Pojo pojo = new Pojo();
	// i change this binder to "mine"
	// Add a validator to the binder so if each property is valid then validate the bean
	// Works automatically with setBean
	// validate on writeBean if readBean
	private Binder<Pojo> binder = new JSR303ValidationBinder<Pojo>(Pojo.class);
	
    private static Validator validator;
    
    /**
	 * 
	 */
	public JSRBufferedValidatorLayout() {
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
		 * With JSRValidator it won't bind the error to the field but the constraint will be checked
		binder.bind(value, Pojo::getValue, Pojo::setValue);
		binder.bind(dateFrom, Pojo::getDateFrom, Pojo::setDateFrom);
		binder.bind(dateTo, Pojo::getDateTo, Pojo::setDateTo);
		*/
		
		binder.bind(value, "value");
		binder.bind(dateFrom,"dateFrom");
		binder.bind(dateTo, "dateTo");
		
		binder.readBean(pojo);
		
	//	binder.withValidator(pojo ->!"test".equals(pojo.getValue()),"Error added bean validation");
		binder.setStatusLabel(computedVaadinErrorLabel);
	
		
		return layout;
	}
	
	private void validate(ClickEvent e){
		checkErrorLabel.setStyleName(ValoTheme.LABEL_SUCCESS);
		try {
		    computedVaadinErrorLabel.setVisible(false);
			binder.writeBean(pojo);
		} catch (ValidationException e1) {
			List<ValidationResult> errors = e1.getValidationErrors();

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
		}
		if (binder.writeBeanIfValid(pojo)){ // if readBean binder.writeBeanIfValid(pojo)
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
