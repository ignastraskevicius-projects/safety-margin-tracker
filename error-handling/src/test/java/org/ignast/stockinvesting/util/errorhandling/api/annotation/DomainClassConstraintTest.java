package org.ignast.stockinvesting.util.errorhandling.api.annotation;

import lombok.val;
import org.ignast.stockinvesting.util.mockito.MockitoUtils;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.util.errorhandling.api.annotation.DomainClassConstraint.SupportedTypes.supporting;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DomainClassBackedByStringValidatorTest {

    private DomainClassConstraintValidator validator = new DomainClassConstraintValidator.BackedByString(supporting(emptyMap(), emptyMap()));

    @Test
    public void shouldFailToConstructWithNull() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new DomainClassConstraintValidator.BackedByString(null));
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new DomainClassConstraintValidator.BackedByString(supporting(null, null)));
    }

    @Test
    public void shouldBeValidForNullFieldToGivePreferenceToJavaxNonNullAnnotation() {
        validator.initialize(constrainedBy(ClassA.class));

        assertThat(validator.isValid(null, null)).isTrue();
    }

    @Test
    public void shouldThrowForUnsupportedTypes() {
        validator = new DomainClassConstraintValidator.BackedByString(supporting(Map.of(ClassA.class, ClassA::new), emptyMap()));
        validator.initialize(constrainedBy(ClassB.class));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> validator.isValid("objectIsNotSupported", null))
                .withMessage("DomainClassConstraint is not configured for 'ClassB' class");
    }

    @Test
    public void shouldInvalidateInvalidButSupportedType() {
        validator = new DomainClassConstraintValidator.BackedByString(supporting(Map.of(ClassAlwaysInvalid.class, ClassAlwaysInvalid::new), emptyMap()));
        validator.initialize(constrainedBy(ClassAlwaysInvalid.class));
        val builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        val context = MockitoUtils.mock(ConstraintValidatorContext.class,
                c -> when(c.buildConstraintViolationWithTemplate(ArgumentMatchers.any())).thenReturn(builder));

        assertThat(validator.isValid("argument", context)).isFalse();

        verify(context).buildConstraintViolationWithTemplate(startsWith("this class is always invalid"));
        verify(builder).addConstraintViolation();
    }

    @Test
    public void shouldValidateValidSupportedType() {
        validator = new DomainClassConstraintValidator.BackedByString(supporting(Map.of(ClassA.class, ClassA::new), emptyMap()));
        validator.initialize(constrainedBy(ClassA.class));

        assertThat(validator.isValid("argument", null)).isTrue();
    }

    @SuppressWarnings("checkstyle:anoninnerlength")
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

    class ClassA {
        public ClassA(String arg) {
        }
    }

    class ClassB {
        public ClassB(String arg){
        }
    }

    class ClassAlwaysInvalid {
        public ClassAlwaysInvalid(String arg) {
            throw new IllegalArgumentException("this class is always invalid");
        }
    }
}

class DomainClassBackedByIntegerValidatorTest {

    private DomainClassConstraintValidator validator = new DomainClassConstraintValidator.BackedByInteger(supporting(emptyMap(), emptyMap()));

    @Test
    public void shouldFailToConstructWithNull() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new DomainClassConstraintValidator.BackedByInteger(null));
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new DomainClassConstraintValidator.BackedByInteger((supporting(null, null))));
    }

    @Test
    public void shouldBeValidForNullFieldToGivePreferenceToJavaxNonNullAnnotation() {
        validator.initialize(constrainedBy(ClassA.class));

        assertThat(validator.isValid(null, null)).isTrue();
    }

    @Test
    public void shouldThrowForUnsupportedTypes() {
        validator = new DomainClassConstraintValidator.BackedByInteger(supporting(emptyMap(), Map.of(ClassA.class, ClassA::new)));
        validator.initialize(constrainedBy(ClassB.class));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> validator.isValid("objectIsNotSupported", null))
                .withMessage("DomainClassConstraint is not configured for 'ClassB' class");
    }

    @Test
    public void shouldInvalidateInvalidButSupportedType() {
        validator = new DomainClassConstraintValidator.BackedByInteger(supporting(emptyMap(), Map.of(ClassAlwaysInvalid.class, ClassAlwaysInvalid::new)));
        validator.initialize(constrainedBy(ClassAlwaysInvalid.class));
        val builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        val context = MockitoUtils.mock(ConstraintValidatorContext.class,
                c -> when(c.buildConstraintViolationWithTemplate(ArgumentMatchers.any())).thenReturn(builder));

        assertThat(validator.isValid(4, context)).isFalse();

        verify(context).buildConstraintViolationWithTemplate(startsWith("this class is always invalid"));
        verify(builder).addConstraintViolation();
    }

    @Test
    public void shouldValidateValidSupportedType() {
        validator = new DomainClassConstraintValidator.BackedByInteger(supporting(emptyMap(), Map.of(ClassA.class, ClassA::new)));
        validator.initialize(constrainedBy(ClassA.class));

        assertThat(validator.isValid(4, null)).isTrue();
    }

    @SuppressWarnings("checkstyle:anoninnerlength")
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

    class ClassA {
        public ClassA(Integer arg) {
        }
    }

    class ClassB {
        public ClassB(Integer arg){
        }
    }

    class ClassAlwaysInvalid {
        public ClassAlwaysInvalid(Integer arg) {
            throw new IllegalArgumentException("this class is always invalid");
        }
    }
}