package org.ignast.stockinvesting.api.controller.errorhandler.annotations;

import lombok.val;
import org.ignast.stockinvesting.domain.MarketIdentifierCode;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;

import static org.assertj.core.api.Assertions.assertThat;

class DomainClassConstraintValidatorTest {

    private DomainClassConstraintValidator validator = new DomainClassConstraintValidator();

    @Test
    public void shouldBeValidForNullFieldToGivePreferenceToJavaxNonNullAnnotation() {
        validator.initialize(constrainedBy(MarketIdentifierCode.class));

        assertThat(validator.isValid(null, null)).isTrue();
    }

    @Test
    public void shouldInvalidateInvalidMIC() {
        validator.initialize(constrainedBy(MarketIdentifierCode.class));

        assertThat(validator.isValid("nont4Characters", null)).isFalse();
    }

    @Test
    public void shouldValidateValidMIC() {
        validator.initialize(constrainedBy(MarketIdentifierCode.class));

        assertThat(validator.isValid("XNYS", null)).isTrue();
    }

    private DomainClassConstraint constrainedBy(Class<?> domainClass) {
        val constraint = new DomainClassConstraint() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public String message() {
                return null;
            }

            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public Class<?>[] payload() {
                return new Class[0];
            }

            @Override
            public Class<?> domainClass() {
                return domainClass;
            }
        };
        assertThat(constraint.domainClass()).isEqualTo(domainClass);
        return constraint;
    }
}