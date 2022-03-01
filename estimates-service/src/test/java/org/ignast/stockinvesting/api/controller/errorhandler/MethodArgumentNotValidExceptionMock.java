package org.ignast.stockinvesting.api.controller.errorhandler;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.ConstraintViolation;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.api.controller.errorhandler.AnnotationStubs.*;
import static org.ignast.stockinvesting.api.controller.errorhandler.MethodArgumentNotValidExceptionMock.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExpectationsForMethodArgumentNotValidExceptionTest {

    @Test
    public void exceptionShouldAlwaysContainBindingResult() throws NoSuchMethodException {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new MethodArgumentNotValidException(anyMethodParameter(), null))
                .withMessageContaining("BindingResult");
    }

    @Test
    public void shouldThrowOnUnwrappingIfExceptionIsNotDueToConstraintViolation() throws NoSuchMethodException {
        FieldError fieldError = new FieldError("company", "anyName", "anyMessage");
        Object source = new Object();
        fieldError.wrap(source);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> fieldError.unwrap(ConstraintViolation.class))
                .withMessageContaining("No source object of the given type available: ")
                .withMessageContaining("ConstraintViolation");
    }

    @Test
    public void shouldThrowOnUnwrappingIfThereIsNoErrorSourceIndicated() throws NoSuchMethodException {
        FieldError fieldError = new FieldError("company", "anyName", "anyMessage");

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> fieldError.unwrap(ConstraintViolation.class))
                .withMessageContaining("No source object of the given type available: ")
                .withMessageContaining("ConstraintViolation");
    }

    @Test
    public void shouldThrowIfThereIsNullSourceIndicated() {
        FieldError fieldError = new FieldError("company", "anyName", "anyMessage");
        fieldError.wrap(null);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> fieldError.unwrap(ConstraintViolation.class))
                .withMessageContaining("No source object of the given type available: ")
                .withMessageContaining("ConstraintViolation");
    }
}

public class MethodArgumentNotValidExceptionMock {
    public static MethodArgumentNotValidException withFieldErrors(List<FieldError> fieldErrors) {
        return new MethodArgumentNotValidException(anyMethodParameter(), bindingResultWithFieldErrorsOf(fieldErrors));
    }

    public static MethodArgumentNotValidException withFieldErrorSourceNotBeingConstraintViolation() {
        FieldError fieldError = mockFieldErrorWithNameAndMessage("any", "any");
        when(fieldError.unwrap(any())).thenThrow(IllegalArgumentException.class);
        return withFieldErrors(asList(fieldError));
    }

    public static MethodArgumentNotValidException withErrorFieldViolation(
            Consumer<ViolationMockBuilder> violationCustomizer) {
        FieldError fieldError = mockFieldErrorCausedByViolation("any", "any", violationCustomizer);
        return withFieldErrors(asList(fieldError));
    }

    public static MethodArgumentNotValidException withMultipleFields(String underlyingPath1, String defaultMessage1,
            Annotation annotation1, String underlyingPath2, String defaultMessage2, Annotation annotation2) {
        FieldError fieldError1 = mockFieldErrorCausedByViolation(underlyingPath1, defaultMessage1,
                createViolationCausedBy(annotation1));
        FieldError fieldError2 = mockFieldErrorCausedByViolation(underlyingPath2, defaultMessage2,
                createViolationCausedBy(annotation2));
        return withFieldErrors(asList(fieldError1, fieldError2));
    }

    private static FieldError mockFieldErrorCausedByViolation(String path, String message,
            Consumer<ViolationMockBuilder> violationCustomizer) {
        ViolationMockBuilder builder = new ViolationMockBuilder();
        violationCustomizer.accept(builder);
        FieldError fieldError = mockFieldErrorWithNameAndMessage(path, message);
        when(fieldError.unwrap(any())).thenReturn(builder.build());
        return fieldError;
    }

    public static MethodArgumentNotValidException withFieldErrorCausedBy(Annotation annotation) {
        return withErrorFieldViolation(createViolationCausedBy(annotation));
    }

    private static Consumer<ViolationMockBuilder> createViolationCausedBy(Annotation annotation) {
        return b -> b.withDescriptor().withAnnotation(annotation);
    }

    private static FieldError mockFieldErrorWithNameAndMessage(String field, String defaultMessage) {
        FieldError fieldError = mock(FieldError.class);
        when(fieldError.getField()).thenReturn(field);
        when(fieldError.getDefaultMessage()).thenReturn(defaultMessage);
        return fieldError;
    }

    static MethodParameter anyMethodParameter() {
        try {
            Method anyMethod = String.class.getMethod("charAt", int.class);
            return new MethodParameter(anyMethod, 0);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static BindingResult bindingResultWithFieldErrorsOf(List<FieldError> fieldErrors) {
        BindingResult result = mock(BindingResult.class);
        when(result.getFieldErrors()).thenReturn(fieldErrors);
        return result;
    }

    static class ViolationMockBuilder {
        private ConstraintViolation violation = mock(ConstraintViolation.class);
        private ConstraintDescriptor descriptor = null;

        public ViolationMockBuilder withDescriptor() {
            descriptor = mock(ConstraintDescriptor.class);
            when(violation.getConstraintDescriptor()).thenReturn(descriptor);
            return this;
        }

        public ViolationMockBuilder withAnnotation() {
            when(descriptor.getAnnotation()).thenReturn(mock(Annotation.class));
            return this;
        }

        private ViolationMockBuilder withAnnotation(Annotation annotation) {
            when(descriptor.getAnnotation()).thenReturn(annotation);
            return this;
        }

        private ConstraintViolation build() {
            return violation;
        }
    }
}

class MethodArgumentNotValidExceptionMockTest {

    @Test
    public void shouldCreateMockWithNullFieldErrors() {
        MethodArgumentNotValidException exception = withFieldErrors(null);

        assertThat(exception.getBindingResult().getFieldErrors()).isNull();
    }

    @Test
    public void shouldCreateMockWithEmptyFieldErrors() {
        MethodArgumentNotValidException exception = withFieldErrors(asList());

        assertThat(exception.getBindingResult().getFieldErrors()).hasSize(0);
    }

    @Test
    public void shouldCreateMockContainingErrorsWithoutConstrainViolation() {
        MethodArgumentNotValidException exception = withFieldErrorSourceNotBeingConstraintViolation();

        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        assertThat(fieldErrors).hasSize(1);
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> fieldErrors.get(0).unwrap(ConstraintViolation.class));
    }

    @Test
    public void shouldCreateMockContainingErrorsWithViolationsButWithoutDescriptor() {
        MethodArgumentNotValidException exception = withErrorFieldViolation(c -> {
        });

        ConstraintViolation violation = exception.getBindingResult().getFieldErrors().get(0)
                .unwrap(ConstraintViolation.class);
        assertThat(violation).isNotNull();
        assertThat(violation.getConstraintDescriptor()).isNull();
    }

    @Test
    public void shouldCreateMockContainingErrorsWithViolationDescriptorButWithoutAnnotation() {
        MethodArgumentNotValidException exception = withErrorFieldViolation(c -> c.withDescriptor());

        ConstraintDescriptor descriptor = exception.getBindingResult().getFieldErrors().get(0)
                .unwrap(ConstraintViolation.class).getConstraintDescriptor();
        assertThat(descriptor).isNotNull();
        assertThat(descriptor.getAnnotation()).isNull();
    }

    @Test
    public void shouldCreateMockContainingErrorsWithCausingAnnotationsButWithoutTheType() {
        MethodArgumentNotValidException exception = withErrorFieldViolation(c -> c.withDescriptor().withAnnotation());

        Annotation violationCausingAnnotation = exception.getBindingResult().getFieldErrors().get(0)
                .unwrap(ConstraintViolation.class).getConstraintDescriptor().getAnnotation();
        assertThat(violationCausingAnnotation).isNotNull();
        assertThat(violationCausingAnnotation.annotationType()).isNull();
    }

    @Test
    public void shouldCreateMockContainingErrorsWithCausingAnnotations() {
        MethodArgumentNotValidException exception = withFieldErrorCausedBy(javaLangOverride());

        Annotation violationCausingAnnotation = exception.getBindingResult().getFieldErrors().get(0)
                .unwrap(ConstraintViolation.class).getConstraintDescriptor().getAnnotation();
        assertThat(violationCausingAnnotation.annotationType()).isNotNull();
    }

    @Test
    public void shouldCreateMockContainingMultipleErrors() {
        MethodArgumentNotValidException exception = withMultipleFields("path1", "message1", javaLangOverride(), "path2",
                "message2", javaLangSuppressWarning());

        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        assertThat(fieldErrors.size()).isEqualTo(2);
        assertThat(fieldErrors.get(0).getField()).isEqualTo("path1");
        assertThat(fieldErrors.get(0).getDefaultMessage()).isEqualTo("message1");
        assertThat(fieldErrors.get(0).unwrap(ConstraintViolation.class).getConstraintDescriptor().getAnnotation()
                .annotationType()).isEqualTo(Override.class);
        assertThat(fieldErrors.get(1).getField()).isEqualTo("path2");
        assertThat(fieldErrors.get(1).getDefaultMessage()).isEqualTo("message2");
        assertThat(fieldErrors.get(1).unwrap(ConstraintViolation.class).getConstraintDescriptor().getAnnotation()
                .annotationType()).isEqualTo(SuppressWarnings.class);

    }
}