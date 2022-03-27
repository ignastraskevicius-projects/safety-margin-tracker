package org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.postparsed.annotation;

import static java.lang.String.format;

import java.util.Map;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

abstract class DomainClassConstraintValidator<T> implements ConstraintValidator<DomainClassConstraint, T> {

    private final Map<Class<?>, From1ParamConstructor<T>> supportedObjects;

    private Class<?> domainClass;

    private DomainClassConstraintValidator(final Map<Class<?>, From1ParamConstructor<T>> supportedObjects) {
        this.supportedObjects = supportedObjects;
    }

    public void initialize(final DomainClassConstraint constraint) {
        this.domainClass = constraint.domainClass();
    }

    @Override
    public boolean isValid(final T value, final ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        } else {
            return validate(value, context);
        }
    }

    private boolean validate(final T value, final ConstraintValidatorContext context) {
        if (!supportedObjects.containsKey(domainClass)) {
            throw new IllegalArgumentException(
                format("DomainClassConstraint is not configured for '%s' class", domainClass.getSimpleName())
            );
        } else {
            try {
                supportedObjects.get(domainClass).construct(value);
                return true;
            } catch (IllegalArgumentException e) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(e.getMessage()).addConstraintViolation();
                return false;
            }
        }
    }

    static class IntegerBackedObjectValidator extends DomainClassConstraintValidator<Integer> {

        public IntegerBackedObjectValidator(final DomainClassConstraint.SupportedTypes supportedObjects) {
            super(supportedObjects.getTypesConstructableFromInteger());
        }
    }

    static class StringBackedObjectValidator extends DomainClassConstraintValidator<String> {

        public StringBackedObjectValidator(final DomainClassConstraint.SupportedTypes supportedObjects) {
            super(supportedObjects.getTypesConstructableFromString());
        }
    }
}
