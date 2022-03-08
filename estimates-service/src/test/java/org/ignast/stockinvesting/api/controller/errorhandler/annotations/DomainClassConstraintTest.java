package org.ignast.stockinvesting.api.controller.errorhandler.annotations;

import lombok.val;
import org.ignast.stockinvesting.estimates.domain.MarketIdentifierCode;
import org.ignast.stockinvesting.estimates.domain.StockSymbol;
import org.ignast.stockinvesting.mockito.MockitoUtils;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.api.controller.errorhandler.annotations.DomainClassConstraintValidator.SupportedTypes.supporting;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DomainClassConstraintValidatorTest {

    private DomainClassConstraintValidator validator = new DomainClassConstraintValidator(supporting(emptyMap()));

    @Test
    public void shouldFailToConstructWithNull() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new DomainClassConstraintValidator(null));
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new DomainClassConstraintValidator(supporting(null)));
    }

    @Test
    public void shouldBeValidForNullFieldToGivePreferenceToJavaxNonNullAnnotation() {
        validator.initialize(constrainedBy(MarketIdentifierCode.class));

        assertThat(validator.isValid(null, null)).isTrue();
    }

    @Test
    public void shouldThrowForUnsupportedTypes() {
        validator = new DomainClassConstraintValidator(supporting(Map.of(MarketIdentifierCode.class, MarketIdentifierCode::new)));
        validator.initialize(constrainedBy(StockSymbol.class));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> validator.isValid("objectIsNotSupported", null))
                .withMessage("DomainClassConstraint is not configured for 'StockSymbol' class");
    }

    @Test
    public void shouldInvalidateInvalidButSupportedType() {
        validator = new DomainClassConstraintValidator(supporting(Map.of(MarketIdentifierCode.class, MarketIdentifierCode::new)));
        validator.initialize(constrainedBy(MarketIdentifierCode.class));
        val builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        val context = MockitoUtils.mock(ConstraintValidatorContext.class,
                c -> when(c.buildConstraintViolationWithTemplate(any())).thenReturn(builder));

        assertThat(validator.isValid("nont4Characters", context)).isFalse();

        verify(context).buildConstraintViolationWithTemplate(startsWith("Market Identifier"));
        verify(builder).addConstraintViolation();
    }

    @Test
    public void shouldValidateValidSupportedType() {
        validator = new DomainClassConstraintValidator(supporting(Map.of(MarketIdentifierCode.class, MarketIdentifierCode::new)));
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