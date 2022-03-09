package org.ignast.stockinvesting.util.errorhandling.api.annotation;

import lombok.*;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

import static java.lang.String.format;

@Constraint(validatedBy = DomainClassConstraintValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DomainClassConstraint {
    String message() default "Market Identifier is not 4 characters long (ISO 10383 standard)";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};

    Class<?> domainClass();

    @Getter
    @RequiredArgsConstructor(staticName = "supporting")
    class SupportedTypes {
        @NonNull
        private final Map<Class<?>, FromStringConstructor> constructableTypes;
    }
}

class DomainClassConstraintValidator implements ConstraintValidator<DomainClassConstraint, String> {
    private final Map<Class<?>, FromStringConstructor> supportedObjects;

    private Class<?> domainClass;

    public DomainClassConstraintValidator(DomainClassConstraint.SupportedTypes supportedObjects) {
        this.supportedObjects = supportedObjects.getConstructableTypes();
    }

    public void initialize(DomainClassConstraint constraint) {
        this.domainClass = constraint.domainClass();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        } else {
            return validate(value, context);
        }
    }

    private boolean validate(String value, ConstraintValidatorContext context) {
        if (!supportedObjects.keySet().contains(domainClass)) {
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
}
