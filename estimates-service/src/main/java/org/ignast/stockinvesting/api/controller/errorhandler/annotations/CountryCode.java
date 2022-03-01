package org.ignast.stockinvesting.api.controller.errorhandler.annotations;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Locale;

import static java.util.Objects.isNull;

@Constraint(validatedBy = CountryCodeValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CountryCode {
    String message() default "Must be a valid ISO 3166 alpha-2 code";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}

class CountryCodeValidator implements ConstraintValidator<CountryCode, String> {

    @Override
    public boolean isValid(String countryCode, ConstraintValidatorContext constraintValidatorContext) {
        if (isNull(countryCode)) {
            return true;
        } else {
            return Locale.getISOCountries(Locale.IsoCountryCode.PART1_ALPHA2).contains(countryCode);
        }
    }
}