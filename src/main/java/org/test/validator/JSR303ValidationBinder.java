/**
 * 
 */
package org.test.validator;

import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;

import com.vaadin.data.BeanPropertySet.NestedBeanPropertyDefinition;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.Binder;
import com.vaadin.data.HasValue;
import com.vaadin.data.PropertyDefinition;
import com.vaadin.data.RequiredFieldConfigurator;
import com.vaadin.data.util.BeanUtil;
import com.vaadin.data.validator.BeanValidator;

/**
 * @author jcgueriaud
 *
 */
public class JSR303ValidationBinder<BEAN> extends Binder<BEAN> {

	private static final long serialVersionUID = 8574024110578919726L;

	private final Class<BEAN> beanType;

    private RequiredFieldConfigurator requiredConfigurator = RequiredFieldConfigurator.DEFAULT;

    /**
     * Creates a new binder that uses reflection based on the provided bean type
     * to resolve bean properties. It assumes that JSR-303 bean validation
     * implementation is present on the classpath. If there is no such
     * implementation available then {@link Binder} class should be used instead
     * (this constructor will throw an exception). Otherwise
     * {@link BeanValidator} is added to each binding that is defined using a
     * property name.
     *
     * @param beanType
     *            the bean type to use, not <code>null</code>
     */
    public JSR303ValidationBinder(Class<BEAN> beanType) {
        super(beanType);
        if (!BeanUtil.checkBeanValidationAvailable()) {
            throw new IllegalStateException(
                    BeanValidationBinder.class.getSimpleName()
                            + " cannot be used because a JSR-303 Bean Validation "
                            + "implementation not found on the classpath. Use "
                            + Binder.class.getSimpleName() + " instead");
        }
        this.beanType = beanType;
        // add bean validation
        JSR303BeanValidator validator = new JSR303BeanValidator(beanType,null);
        this.withValidator(validator);
    }

    /**
     * Sets a logic which allows to configure require indicator via
     * {@link HasValue#setRequiredIndicatorVisible(boolean)} based on property
     * descriptor.
     * <p>
     * Required indicator configuration will not be used at all if
     * {@code configurator} is null.
     * <p>
     * By default the {@link RequiredFieldConfigurator#DEFAULT} configurator is
     * used.
     *
     * @param configurator
     *            required indicator configurator, may be {@code null}
     */
    public void setRequiredConfigurator(
            RequiredFieldConfigurator configurator) {
        requiredConfigurator = configurator;
    }

    /**
     * Gets field required indicator configuration logic.
     *
     * @see #setRequiredConfigurator(RequiredFieldConfigurator)
     *
     * @return required indicator configurator, may be {@code null}
     */
    public RequiredFieldConfigurator getRequiredConfigurator() {
        return requiredConfigurator;
    }

    @Override
    protected BindingBuilder<BEAN, ?> configureBinding(
            BindingBuilder<BEAN, ?> binding,
            PropertyDefinition<BEAN, ?> definition) {
        Class<?> actualBeanType = findBeanType(beanType, definition);
        JSR303BeanValidator validator = new JSR303BeanValidator(actualBeanType,
                definition.getName());
        if (requiredConfigurator != null) {
            configureRequired(binding, definition, validator);
        }
        return binding.withValidator(validator);
    }

    /**
     * Finds the bean type containing the property the given definition refers
     * to.
     *
     * @param beanType
     *            the root beanType
     * @param definition
     *            the definition for the property
     * @return the bean type containing the given property
     */
    @SuppressWarnings({ "rawtypes" })
    private Class<?> findBeanType(Class<BEAN> beanType,
            PropertyDefinition<BEAN, ?> definition) {
        if (definition instanceof NestedBeanPropertyDefinition) {
            return ((NestedBeanPropertyDefinition) definition).getParent()
                    .getType();
        } else {
            // Non nested properties must be defined in the main type
            return beanType;
        }
    }

    private void configureRequired(BindingBuilder<BEAN, ?> binding,
            PropertyDefinition<BEAN, ?> definition, JSR303BeanValidator validator) {
        assert requiredConfigurator != null;
        Class<?> propertyHolderType = definition.getPropertyHolderType();
        BeanDescriptor descriptor = validator.getJavaxBeanValidator()
                .getConstraintsForClass(propertyHolderType);
        PropertyDescriptor propertyDescriptor = descriptor
                .getConstraintsForProperty(definition.getName());
        if (propertyDescriptor == null) {
            return;
        }
        if (propertyDescriptor.getConstraintDescriptors().stream()
                .map(ConstraintDescriptor::getAnnotation)
                .anyMatch(requiredConfigurator)) {
            binding.getField().setRequiredIndicatorVisible(true);
        }
    }

}
