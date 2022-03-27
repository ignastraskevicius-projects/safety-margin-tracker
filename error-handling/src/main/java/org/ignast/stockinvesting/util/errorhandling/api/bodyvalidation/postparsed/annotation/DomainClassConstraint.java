package org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.postparsed.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import javax.validation.Constraint;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Constraint(
    validatedBy = {
        DomainClassConstraintValidator.IntegerBackedObjectValidator.class,
        DomainClassConstraintValidator.StringBackedObjectValidator.class,
    }
)
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
