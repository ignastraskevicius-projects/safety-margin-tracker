package org.ignast.stockinvesting.util.errorhandling.api.annotation;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.util.errorhandling.api.annotation.DomainClassConstraint.SupportedTypes.supporting;
import static org.ignast.stockinvesting.utiltest.ExceptionAssert.assertThatNullPointerExceptionIsThrownBy;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;
import java.util.Map;
import javax.validation.ConstraintValidatorContext;
import lombok.val;
import org.ignast.stockinvesting.util.errorhandling.api.annotation.DomainClassConstraintValidator.IntegerBackedObjectValidator;
import org.ignast.stockinvesting.util.errorhandling.api.annotation.DomainClassConstraintValidator.StringBackedObjectValidator;
import org.ignast.stockinvesting.utiltest.MockitoUtils;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

@SuppressWarnings("checkstyle:outertypefilename")
final class DomainClassStringBackedObjectValidatorValidatorTest {

    @Test
    public void shouldFailToConstructWithNull() {
        assertThatNullPointerExceptionIsThrownBy(
            () -> new StringBackedObjectValidator(null),
            () -> new StringBackedObjectValidator(supporting(null, null))
        );
    }

    @Test
    public void shouldBeValidForNullFieldToGivePreferenceToJavaxNonNullAnnotation() {
        final val validator = new StringBackedObjectValidator(supporting(emptyMap(), emptyMap()));
        validator.initialize(constrainedBy(ClassA.class));

        assertThat(validator.isValid(null, null)).isTrue();
    }

    @Test
    public void shouldThrowForUnsupportedTypes() {
        final val supportedTypes = supporting(Map.of(ClassA.class, ClassA::new), emptyMap());
        final val validator = new StringBackedObjectValidator(supportedTypes);
        validator.initialize(constrainedBy(ClassB.class));

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> validator.isValid("objectIsNotSupported", null))
            .withMessage("DomainClassConstraint is not configured for 'ClassB' class");
    }

    @Test
    public void shouldInvalidateInvalidButSupportedType() {
        final val supportedTypes = supporting(
            Map.of(ClassAlwaysInvalid.class, ClassAlwaysInvalid::new),
            emptyMap()
        );
        final val validator = new StringBackedObjectValidator(supportedTypes);
        validator.initialize(constrainedBy(ClassAlwaysInvalid.class));

        final val builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        final val context = MockitoUtils.mock(
            ConstraintValidatorContext.class,
            c -> when(c.buildConstraintViolationWithTemplate(ArgumentMatchers.any())).thenReturn(builder)
        );

        assertThat(validator.isValid("argument", context)).isFalse();

        verify(context).buildConstraintViolationWithTemplate(startsWith("this class is always invalid"));
        verify(builder).addConstraintViolation();
    }

    @Test
    public void shouldValidateValidSupportedType() {
        final val supportedTypes = supporting(Map.of(ClassA.class, ClassA::new), emptyMap());
        final val validator = new StringBackedObjectValidator(supportedTypes);
        validator.initialize(constrainedBy(ClassA.class));

        assertThat(validator.isValid("argument", null)).isTrue();
    }

    @SuppressWarnings("checkstyle:anoninnerlength")
    private DomainClassConstraint constrainedBy(final Class<?> domainClass) {
        final val constraint = new DomainClassConstraint() {
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

    private static final class ClassA {

        public ClassA(final String arg) {}
    }

    private static final class ClassB {

        public ClassB(final String arg) {}
    }

    private static final class ClassAlwaysInvalid {

        public ClassAlwaysInvalid(final String arg) {
            throw new IllegalArgumentException("this class is always invalid");
        }
    }
}

final class DomainClassIntegerBackedObjectValidatorValidatorTest {

    @Test
    public void shouldFailToConstructWithNull() {
        assertThatNullPointerExceptionIsThrownBy(
            () -> new IntegerBackedObjectValidator(null),
            () -> new IntegerBackedObjectValidator((supporting(null, null)))
        );
    }

    @Test
    public void shouldBeValidForNullFieldToGivePreferenceToJavaxNonNullAnnotation() {
        final val validator = new IntegerBackedObjectValidator(supporting(emptyMap(), emptyMap()));
        validator.initialize(constrainedBy(ClassA.class));

        assertThat(validator.isValid(null, null)).isTrue();
    }

    @Test
    public void shouldThrowForUnsupportedTypes() {
        final val supportedTypes = supporting(emptyMap(), Map.of(ClassA.class, ClassA::new));
        final val validator = new IntegerBackedObjectValidator(supportedTypes);
        validator.initialize(constrainedBy(ClassB.class));

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> validator.isValid(any(), null))
            .withMessage("DomainClassConstraint is not configured for 'ClassB' class");
    }

    @Test
    public void shouldInvalidateInvalidButSupportedType() {
        final val supportedTypes = supporting(
            emptyMap(),
            Map.of(ClassAlwaysInvalid.class, ClassAlwaysInvalid::new)
        );
        final val validator = new IntegerBackedObjectValidator(supportedTypes);
        validator.initialize(constrainedBy(ClassAlwaysInvalid.class));
        final val builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        final val context = MockitoUtils.mock(
            ConstraintValidatorContext.class,
            c -> when(c.buildConstraintViolationWithTemplate(ArgumentMatchers.any())).thenReturn(builder)
        );

        assertThat(validator.isValid(any(), context)).isFalse();

        verify(context).buildConstraintViolationWithTemplate(startsWith("this class is always invalid"));
        verify(builder).addConstraintViolation();
    }

    @Test
    public void shouldValidateValidSupportedType() {
        final val supportedTypes = supporting(emptyMap(), Map.of(ClassA.class, ClassA::new));
        final val validator = new IntegerBackedObjectValidator(supportedTypes);
        validator.initialize(constrainedBy(ClassA.class));

        assertThat(validator.isValid(any(), null)).isTrue();
    }

    @SuppressWarnings("checkstyle:magicnumber")
    private int any() {
        return 4;
    }

    @SuppressWarnings("checkstyle:anoninnerlength")
    private DomainClassConstraint constrainedBy(final Class<?> domainClass) {
        final val constraint = new DomainClassConstraint() {
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

    private static final class ClassA {

        public ClassA(final Integer arg) {}
    }

    private static final class ClassB {

        public ClassB(final Integer arg) {}
    }

    private static final class ClassAlwaysInvalid {

        public ClassAlwaysInvalid(final Integer arg) {
            throw new IllegalArgumentException("this class is always invalid");
        }
    }
}
