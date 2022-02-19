package org.ignast.stockinvesting.api.controller.errorhandler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.MethodParameter;
import org.springframework.validation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.ConstraintViolation;
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
    public void shouldSkipErrorIfThereIsNoErrorConstraintDescriptorProvided() throws NoSuchMethodException {
        ConstraintViolation withoutViolation = new ViolationBuilder().build();
        ConstraintViolation withoutDescriptor = new ViolationBuilder().withViolation().build();
        ConstraintViolation withoutAnnotation = new ViolationBuilder().withViolation().withDescriptor().build();
        ConstraintViolation withoutAnnotationType = new ViolationBuilder().withViolation().withDescriptor()
                .withAnnotation().build();
        Arrays.asList(withoutViolation, withoutDescriptor, withoutAnnotation, withoutAnnotationType).stream()
                .map(violation -> {
                    FieldError fieldError = new FieldError("company", "anyName", "anyMessage");
                    fieldError.wrap(violation);
                    return fieldError;
                }).forEach(error -> {
                    MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
                            anyMethodParameter(), bindingResultWithFieldErrorsOf(Arrays.asList(error)));

                    assertThat(errorsExtractor.extractAnnotationBasedErrorsFrom(exception)).isEmpty();
                });
    }

    @Test
    public void ensureThatUnderlyingFieldNameIsNeverNull() {
        String fieldName = null;
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new FieldError("company", fieldName, "message"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "some.path", "some.other.path" })
    public void shouldExtractErrorPath(String path) throws NoSuchMethodException {
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(anyMethodParameter(),
                bindingResultWithFieldErrorsOf(Arrays.asList(fieldErrorWithUnderlyingPath(path))));

        List<ValidationError> validationErrors = errorsExtractor.extractAnnotationBasedErrorsFrom(exception);

        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).getPath()).isEqualTo(path);
    }

    @ParameterizedTest
    @ValueSource(strings = { "message1", "message2" })
    public void shouldExtractErrorMessage(String message) throws NoSuchMethodException {
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(anyMethodParameter(),
                bindingResultWithFieldErrorsOf(Arrays.asList(fieldErrorWithMessage(message))));

        List<ValidationError> validationErrors = errorsExtractor.extractAnnotationBasedErrorsFrom(exception);

        assertThat(validationErrors).hasSize(1);
        assertThat(validationErrors.get(0).getMessage()).isEqualTo(message);
    }

    private FieldError fieldErrorWithMessage(String message) {
        FieldError fieldError = new FieldError("company", "anyName", message);
        fieldError.wrap(
                new ViolationBuilder().withViolation().withDescriptor().withAnnotation().withAnnotationType().build());
        return fieldError;
    }

    private FieldError fieldErrorWithUnderlyingPath(String underlyingPath) {
        FieldError fieldError = new FieldError("company", underlyingPath, "message");
        fieldError.wrap(
                new ViolationBuilder().withViolation().withDescriptor().withAnnotation().withAnnotationType().build());
        return fieldError;
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

        public ViolationBuilder withAnnotationType() {
            when(descriptor.getAnnotation()).thenReturn(new AnnotationMock());
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