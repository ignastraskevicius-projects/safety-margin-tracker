package org.ignast.stockinvesting.api.controller.errorhandler.annotations;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Currency;

import static java.util.Objects.isNull;

@Constraint(validatedBy = CurrencyValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrencyCode {
    String message() default "Currency must be a valid ISO 4217 code";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}

class CurrencyValidator implements ConstraintValidator<CurrencyCode, String> {

    @Override
    public boolean isValid(String currencyCode, ConstraintValidatorContext constraintValidatorContext) {
        if (isNull(currencyCode)) {
            return true;
        }
        try {
            Currency.getInstance(currencyCode);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}