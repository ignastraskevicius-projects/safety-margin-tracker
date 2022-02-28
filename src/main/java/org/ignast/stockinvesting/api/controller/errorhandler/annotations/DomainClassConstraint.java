package org.ignast.stockinvesting.api.controller.errorhandler.annotations;

import org.ignast.stockinvesting.domain.DomainString;
import org.ignast.stockinvesting.domain.MarketIdentifierCode;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;

@Constraint(validatedBy = DomainClassConstraintValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DomainClassConstraint {
    String message() default "Market Identifier is not 4 characters long (ISO 10383 standard)";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};

    Class<?> domainClass();
}

class DomainClassConstraintValidator implements ConstraintValidator<DomainClassConstraint, String> {
    private static final Map<Class<? extends DomainString>, DomainStringConstructor> domainStrings = new HashMap<>() {
        {
            put(MarketIdentifierCode.class, MarketIdentifierCode::new);
        }
    };

    private Class<?> domainClass;

    public void initialize(DomainClassConstraint constraint) {
        this.domainClass = constraint.domainClass();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        } else {
            return validate(value);
        }
    }

    private boolean validate(String value) {
        try {
            domainStrings.get(domainClass).construct(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private interface DomainStringConstructor {
        DomainString construct(String arg);
    }
}
