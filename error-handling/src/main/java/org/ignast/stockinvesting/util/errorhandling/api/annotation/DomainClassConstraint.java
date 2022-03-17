package org.ignast.stockinvesting.util.errorhandling.api.annotation;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static java.lang.String.format;

@Constraint(validatedBy = {DomainClassConstraintValidator.BackedByInteger.class, DomainClassConstraintValidator.BackedByString.class})
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DomainClassConstraint {

    String message() default "";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};

    Class<?> domainClass();

    @Getter
    @RequiredArgsConstructor(staticName = "supporting")
    class SupportedTypes {
        @NonNull
        private final Map<Class<?>, From1ParamConstructor<String>> typesConstructableFromString;

        private final Map<Class<?>, From1ParamConstructor<Integer>> typesConstructableFromInteger;
    }
}




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
                    format("DomainClassConstraint is not configured for '%s' class", domainClass.getSimpleName()));
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

    static class BackedByInteger extends DomainClassConstraintValidator<Integer> {
        public BackedByInteger(final DomainClassConstraint.SupportedTypes supportedObjects) {
            super(supportedObjects.getTypesConstructableFromInteger());
        }
    }

    static class BackedByString extends DomainClassConstraintValidator<String> {
        public BackedByString(final DomainClassConstraint.SupportedTypes supportedObjects) {
            super(supportedObjects.getTypesConstructableFromString());
        }
    }
}

