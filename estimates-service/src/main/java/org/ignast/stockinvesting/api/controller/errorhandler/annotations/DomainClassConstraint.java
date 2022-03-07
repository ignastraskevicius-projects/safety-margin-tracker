package org.ignast.stockinvesting.api.controller.errorhandler.annotations;

import org.ignast.stockinvesting.estimates.domain.BackedByString;
import org.ignast.stockinvesting.estimates.domain.MarketIdentifierCode;
import org.ignast.stockinvesting.estimates.domain.StockSymbol;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
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
}

class DomainClassConstraintValidator implements ConstraintValidator<DomainClassConstraint, String> {
    private static final Map<Class<? extends BackedByString>, FromStringConstructor> supportedObjects = new HashMap<>() {
        {
            put(MarketIdentifierCode.class, MarketIdentifierCode::new);
            put(StockSymbol.class, StockSymbol::new);
        }
    };

    private Class<?> domainClass;

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

    private interface FromStringConstructor {
        BackedByString construct(String arg);
    }
}
