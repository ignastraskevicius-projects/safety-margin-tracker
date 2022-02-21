package org.ignast.stockinvesting.api.controller.errorhandler;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.validation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.ConstraintViolation;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

public class AnnotationBasedValidationErrorsExtractorTest {

    private AnnotationBasedValidationErrorsExtractor errorsExtractor = new AnnotationBasedValidationErrorsExtractor();

    @Test
    public void shouldThrowIfExceptionContainsNullFieldErrors() throws NoSuchMethodException {
        MethodArgumentNotValidException exception = MethodArgumentNotValidExceptionMock.withFieldErrors(null);

        assertThatExceptionOfType(ValidationErrorsExtractionException.class)
                .isThrownBy(() -> errorsExtractor.extractAnnotationBasedErrorsFrom(exception))
                .withMessageContaining("javax.validation exception is expected to contain at least 1 field error");
    }

    @Test
    public void shouldThrowIfExceptionContainsNoFieldErrors() throws NoSuchMethodException {
        MethodArgumentNotValidException exception = MethodArgumentNotValidExceptionMock
                .withFieldErrors(new ArrayList<>());

        assertThatExceptionOfType(ValidationErrorsExtractionException.class)
                .isThrownBy(() -> errorsExtractor.extractAnnotationBasedErrorsFrom(exception))
                .withMessageContaining("javax.validation exception is expected to contain at least 1 field error");
    }

    @Test
    public void shouldThrowIfFieldErrorSourceIsNotConstraintViolation() {
        MethodArgumentNotValidException exception = MethodArgumentNotValidExceptionMock
                .withErrorFieldSourceNotBeingConstraintViolation();

        assertThatExceptionOfType(ValidationErrorsExtractionException.class)
                .isThrownBy(() -> errorsExtractor.extractAnnotationBasedErrorsFrom(exception))
                .withMessageContaining(
                        "Expected javax.validation ConstraintViolation but validation failed due to a different cause")
                .withCauseInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void shouldThrowIfAnyViolationIsNotCausedByJavaxAnnotation() {
        ConstraintViolation withoutDescriptor = new ViolationBuilder().withViolation().build();
        ConstraintViolation withoutAnnotation = new ViolationBuilder().withViolation().withDescriptor().build();
        ConstraintViolation withoutAnnotationType = new ViolationBuilder().withViolation().withDescriptor()
                .withAnnotation().build();
        asList(withoutDescriptor, withoutAnnotation, withoutAnnotationType).stream()
                .map(MethodArgumentNotValidExceptionMock::withErrorFieldViolation).forEach(exception -> {

                    assertThatExceptionOfType(ValidationErrorsExtractionException.class)
                            .isThrownBy(() -> errorsExtractor.extractAnnotationBasedErrorsFrom(exception))
                            .withMessage(
                                    "Extraction of javax.validation error was caused by violation not defined via annotation")
                            .withCauseInstanceOf(NullPointerException.class);
                });
    }

    @Test
    public void ensureThatUnderlyingFieldNameIsNeverNull() {
        String fieldName = null;
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new FieldError("company", fieldName, "message"));
    }

    @Test
    public void shouldExtractMissingFieldError() {
        MethodArgumentNotValidException exception = MethodArgumentNotValidExceptionMock
                .withFieldErrorCausedBy(javaxValidationNotNull());

        List<ValidationError> validationErrors = errorsExtractor.extractAnnotationBasedErrorsFrom(exception);

        assertThat(validationErrors).hasSize(1);
        ValidationError validationError = validationErrors.get(0);
        assertThat(validationError.getType()).isEqualTo(ViolationType.FIELD_IS_MISSING);
    }

    @Test
    public void shouldExtractFieldErrorRelatedToSizeRestrictions() {
        MethodArgumentNotValidException exception = MethodArgumentNotValidExceptionMock
                .withFieldErrorCausedBy(javaxValidationSize());

        List<ValidationError> validationErrors = errorsExtractor.extractAnnotationBasedErrorsFrom(exception);

        assertThat(validationErrors).hasSize(1);
        ValidationError validationError = validationErrors.get(0);
        assertThat(validationError.getType()).isEqualTo(ViolationType.VALUE_INVALID);
    }

    @Test
    public void shouldExtractFieldErrorRelatedToPatternRestrictions() {
        MethodArgumentNotValidException exception = MethodArgumentNotValidExceptionMock
                .withFieldErrorCausedBy(javaxValidationPattern());

        List<ValidationError> validationErrors = errorsExtractor.extractAnnotationBasedErrorsFrom(exception);

        assertThat(validationErrors).hasSize(1);
        ValidationError validationError = validationErrors.get(0);
        assertThat(validationError.getType()).isEqualTo(ViolationType.VALUE_INVALID);
    }

    @Test
    public void shouldDropFieldErrorRelatedToUnexpectedAnnotationsLikeOverride() {
        MethodArgumentNotValidException exception = MethodArgumentNotValidExceptionMock
                .withFieldErrorCausedBy(javaLangOverride());

        assertThatExceptionOfType(ValidationErrorsExtractionException.class)
                .isThrownBy(() -> errorsExtractor.extractAnnotationBasedErrorsFrom(exception)).withMessage(
                        "Extraction of javax.validation error due to violation caused by annotation 'java.lang.Override' is not supported");
    }

    @Test
    public void shouldDropFieldErrorRelatedToUnexpectedAnnotationsLikeSuppressWarning() {
        MethodArgumentNotValidException exception = MethodArgumentNotValidExceptionMock
                .withFieldErrorCausedBy(javaLangSuppressWarning());

        assertThatExceptionOfType(ValidationErrorsExtractionException.class)
                .isThrownBy(() -> errorsExtractor.extractAnnotationBasedErrorsFrom(exception)).withMessage(
                        "Extraction of javax.validation error due to violation caused by annotation 'java.lang.SuppressWarnings' is not supported");
    }

    @Test
    public void shouldExtractMultipleFieldErrors() {
        MethodArgumentNotValidException exception = MethodArgumentNotValidExceptionMock.withMultipleFields("path1",
                "message1", javaxValidationNotNull(), "path2", "message2", javaxValidationSize());

        List<ValidationError> validationErrors = errorsExtractor.extractAnnotationBasedErrorsFrom(exception);

        assertThat(validationErrors).hasSize(2);
        ValidationError validationError1 = validationErrors.get(0);
        ValidationError validationError2 = validationErrors.get(1);
        assertThat(validationError1.getPath()).isEqualTo("path1");
        assertThat(validationError1.getMessage()).isEqualTo("message1");
        assertThat(validationError1.getType()).isEqualTo(ViolationType.FIELD_IS_MISSING);
        assertThat(validationError2.getPath()).isEqualTo("path2");
        assertThat(validationError2.getMessage()).isEqualTo("message2");
        assertThat(validationError2.getType()).isEqualTo(ViolationType.VALUE_INVALID);
    }

    private static Override javaLangOverride() {
        Override annotation = new Override() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Override.class;
            }
        };
        assertThat(annotation.annotationType() == Override.class);
        return annotation;
    }

    private SuppressWarnings javaLangSuppressWarning() {
        SuppressWarnings annotation = new SuppressWarnings() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return SuppressWarnings.class;
            }

            @Override
            public String[] value() {
                return new String[0];
            }
        };
        assertThat(annotation.annotationType()).isEqualTo(SuppressWarnings.class);
        return annotation;
    }

    private NotNull javaxValidationNotNull() {
        NotNull annotation = new NotNull() {

            @Override
            public String message() {
                return null;
            }

            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public Class<? extends Payload>[] payload() {
                return new Class[0];
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return NotNull.class;
            }
        };
        assertThat(annotation.annotationType() == NotNull.class);
        return annotation;
    }

    private Pattern javaxValidationPattern() {
        Pattern annotation = new Pattern() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Pattern.class;
            }

            @Override
            public String regexp() {
                return null;
            }

            @Override
            public Flag[] flags() {
                return new Flag[0];
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
            public Class<? extends Payload>[] payload() {
                return new Class[0];
            }
        };
        assertThat(annotation.annotationType()).isEqualTo(Pattern.class);
        return annotation;
    }

    private Size javaxValidationSize() {
        Size annotation = new Size() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Size.class;
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
            public Class<? extends Payload>[] payload() {
                return new Class[0];
            }

            @Override
            public int min() {
                return 0;
            }

            @Override
            public int max() {
                return 0;
            }
        };
        assertThat(annotation.annotationType() == Size.class);
        return annotation;
    }

    static class ViolationBuilder {
        private ConstraintViolation violation = null;
        private ConstraintDescriptor descriptor = null;

        public ViolationBuilder withViolation() {
            violation = mock(ConstraintViolation.class);
            return this;
        }

        public ViolationBuilder withDescriptor() {
            descriptor = mock(ConstraintDescriptor.class);
            when(violation.getConstraintDescriptor()).thenReturn(descriptor);
            return this;
        }

        public ViolationBuilder withAnnotation() {
            when(descriptor.getAnnotation()).thenReturn(mock(Annotation.class));
            return this;
        }

        public ViolationBuilder withAnnotation(Annotation annotation) {
            when(descriptor.getAnnotation()).thenReturn(annotation);
            return this;
        }

        public ConstraintViolation build() {
            return violation;
        }
    }
}