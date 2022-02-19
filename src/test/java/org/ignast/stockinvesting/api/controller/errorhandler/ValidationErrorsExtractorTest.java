package org.ignast.stockinvesting.api.controller.errorhandler;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.validation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.ConstraintViolation;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

class ValidationErrorsExtractorTest {

    private ValidationErrorsExtractor errorsExtractor = new ValidationErrorsExtractor();

    @Test
    public void exceptionShouldAlwaysContainBindingResult() throws NoSuchMethodException {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new MethodArgumentNotValidException(anyMethodParameter(), null))
                .withMessageContaining("BindingResult");
    }

    @Test
    public void shouldExtractNoErrorsIfExceptionContainsNullFieldErrors() throws NoSuchMethodException {
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(anyMethodParameter(),
                bindingResultWithFieldErrorsOf(null));

        assertThat(errorsExtractor.extractAnnotationBasedErrorsFrom(exception)).isEmpty();
    }

    @Test
    public void shouldExtractNoErrorsIfExceptionContainsNoFieldErrors() throws NoSuchMethodException {
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(anyMethodParameter(),
                bindingResultWithFieldErrorsOf(new ArrayList<>()));

        assertThat(errorsExtractor.extractAnnotationBasedErrorsFrom(exception)).isEmpty();
    }

    @Test
    public void shouldSkipErrorIfItIsNotDueToConstraintViolation() throws NoSuchMethodException {
        FieldError fieldError = new FieldError("company", "anyName", "anyMessage");
        Object source = new Object();
        fieldError.wrap(source);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(anyMethodParameter(),
                bindingResultWithFieldErrorsOf(Arrays.asList(fieldError)));

        assertThat(errorsExtractor.extractAnnotationBasedErrorsFrom(exception)).isEmpty();
    }

    @Test
    public void shouldSkipErrorIfThereIsNoErrorSourceIndicated() throws NoSuchMethodException {
        FieldError fieldError = new FieldError("company", "anyName", "anyMessage");
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(anyMethodParameter(),
                bindingResultWithFieldErrorsOf(Arrays.asList(fieldError)));

        assertThat(errorsExtractor.extractAnnotationBasedErrorsFrom(exception)).isEmpty();
    }

    @Test
    public void shouldSkipErrorIfThereIsNoErrorViolationsCausedByJavaxAnnotation() {
        ConstraintViolation withoutViolation = new ViolationBuilder().build();
        ConstraintViolation withoutDescriptor = new ViolationBuilder().withViolation().build();
        ConstraintViolation withoutAnnotation = new ViolationBuilder().withViolation().withDescriptor().build();
        ConstraintViolation withoutAnnotationType = new ViolationBuilder().withViolation().withDescriptor()
                .withAnnotation().build();
        Arrays.asList(withoutViolation, withoutDescriptor, withoutAnnotation, withoutAnnotationType).stream()
                .map(violation -> {
                    FieldError fieldError = new FieldError("invalid", "invalid", "invalid");
                    fieldError.wrap(violation);
                    return fieldError;
                })
                .map(error -> new MethodArgumentNotValidException(anyMethodParameter(), bindingResultWithFieldErrorsOf(
                        Arrays.asList(validFieldErrorWithPath("valid1"), error, validFieldErrorWithPath("valid3")))))
                .forEach(exception -> {

                    List<ValidationError> validationErrors = errorsExtractor
                            .extractAnnotationBasedErrorsFrom(exception);

                    assertThat(validationErrors).hasSize(2);
                    assertThat(validationErrors.get(0).getPath()).isEqualTo("valid1");
                    assertThat(validationErrors.get(1).getPath()).isEqualTo("valid3");
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
        String underlyingPath = "some.path";
        String message = "some message";
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(anyMethodParameter(),
                bindingResultWithFieldErrorsOf(
                        Arrays.asList(fieldError(underlyingPath, message, javaxValidationNotNull()))));

        List<ValidationError> validationErrors = errorsExtractor.extractAnnotationBasedErrorsFrom(exception);

        assertThat(validationErrors).hasSize(1);
        ValidationError validationError = validationErrors.get(0);
        assertThat(validationError.getPath()).isEqualTo(underlyingPath);
        assertThat(validationError.getMessage()).isEqualTo(message);
        assertThat(validationError.getType()).isEqualTo(ViolationType.FIELD_IS_MISSING);
    }

    @Test
    public void shouldExtractFieldErrorRelatedToSizeRestrictions() {
        String underlyingPath = "some.path";
        String message = "some message";
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(anyMethodParameter(),
                bindingResultWithFieldErrorsOf(
                        Arrays.asList(fieldError(underlyingPath, message, javaxValidationSize()))));

        List<ValidationError> validationErrors = errorsExtractor.extractAnnotationBasedErrorsFrom(exception);

        assertThat(validationErrors).hasSize(1);
        ValidationError validationError = validationErrors.get(0);
        assertThat(validationError.getType()).isEqualTo(ViolationType.VALUE_INVALID);
    }

    @Test
    public void shouldExtractMultipleFieldErrors() {
        FieldError fieldError1 = fieldError("path1", "message1", javaxValidationNotNull());
        FieldError fieldError2 = fieldError("path2", "message2", javaxValidationSize());
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(anyMethodParameter(),
                bindingResultWithFieldErrorsOf(Arrays.asList(fieldError1, fieldError2)));

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

    private FieldError validFieldErrorWithPath(String path) {
        return fieldError(path, "anyMessage", javaxValidationNotNull());
    }

    private FieldError fieldError(String underlyingPath, String message, Annotation annotation) {
        FieldError fieldError = new FieldError("company", underlyingPath, message);
        fieldError.wrap(new ViolationBuilder().withViolation().withDescriptor().withAnnotation(annotation).build());
        return fieldError;
    }

    private NotNull javaxValidationNotNull() {
        return new NotNull() {

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
    }

    private Size javaxValidationSize() {
        return new Size() {

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
    }

    private BindingResult bindingResultWithFieldErrorsOf(List<FieldError> fieldErrors) {
        BindingResult bindingResult = new DataBinder("").getBindingResult();
        BindingResult spyBindingResult = spy(bindingResult);
        doReturn(fieldErrors).when(spyBindingResult).getFieldErrors();
        return spyBindingResult;
    }

    private MethodParameter anyMethodParameter() {
        try {
            Method anyMethod = String.class.getMethod("charAt", int.class);
            return new MethodParameter(anyMethod, 0);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    class ViolationBuilder {
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

        public ViolationBuilder withAnnotationType() {
            withAnnotation(new AnnotationMock());
            return this;
        }

        public ConstraintViolation build() {
            return violation;
        }
    }

    class AnnotationMock implements Annotation {

        @Override
        public boolean equals(Object obj) {
            return false;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public String toString() {
            return null;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return Override.class;
        }
    }

}