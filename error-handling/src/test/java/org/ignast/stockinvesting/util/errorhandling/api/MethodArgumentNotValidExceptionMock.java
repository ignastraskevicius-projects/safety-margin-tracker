package org.ignast.stockinvesting.util.errorhandling.api;

import lombok.val;
import org.ignast.stockinvesting.util.errorhandling.api.MethodArgumentNotValidExceptionMock.ViolationMockBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.function.Consumer;

import javax.validation.ConstraintViolation;
import javax.validation.metadata.ConstraintDescriptor;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.util.errorhandling.api.AnnotationStubs.javaLangOverride;
import static org.ignast.stockinvesting.util.errorhandling.api.AnnotationStubs.javaLangSuppressWarning;
import static org.ignast.stockinvesting.util.errorhandling.api.MethodArgumentNotValidExceptionMock.anyMethodParameter;
import static org.ignast.stockinvesting.util.errorhandling.api.MethodArgumentNotValidExceptionMock.withErrorFieldViolation;
import static org.ignast.stockinvesting.util.errorhandling.api.MethodArgumentNotValidExceptionMock.withFieldErrorCausedBy;
import static org.ignast.stockinvesting.util.errorhandling.api.MethodArgumentNotValidExceptionMock.withFieldErrorSourceNotBeingConstraintViolation;
import static org.ignast.stockinvesting.util.errorhandling.api.MethodArgumentNotValidExceptionMock.withFieldErrors;
import static org.ignast.stockinvesting.util.errorhandling.api.MethodArgumentNotValidExceptionMock.withMultipleFields;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class ExpectationsForMethodArgumentNotValidExceptionTest {

    @Test
    public void exceptionShouldAlwaysContainBindingResult() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new MethodArgumentNotValidException(anyMethodParameter(), null))
                .withMessageContaining("BindingResult");
    }

    @Test
    public void shouldThrowOnUnwrappingIfExceptionIsNotDueToConstraintViolation() {
        final val fieldError = new FieldError("company", "anyName", "anyMessage");
        final val source = new Object();
        fieldError.wrap(source);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> fieldError.unwrap(ConstraintViolation.class))
                .withMessageContaining("No source object of the given type available: ")
                .withMessageContaining("ConstraintViolation");
    }

    @Test
    public void shouldThrowOnUnwrappingIfThereIsNoErrorSourceIndicated() {
        final val fieldError = new FieldError("company", "anyName", "anyMessage");

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> fieldError.unwrap(ConstraintViolation.class))
                .withMessageContaining("No source object of the given type available: ")
                .withMessageContaining("ConstraintViolation");
    }

    @Test
    public void shouldThrowIfThereIsNullSourceIndicated() {
        final val fieldError = new FieldError("company", "anyName", "anyMessage");
        fieldError.wrap(null);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> fieldError.unwrap(ConstraintViolation.class))
                .withMessageContaining("No source object of the given type available: ")
                .withMessageContaining("ConstraintViolation");
    }
}

public final class MethodArgumentNotValidExceptionMock {
    private MethodArgumentNotValidExceptionMock() {

    }

    public static MethodArgumentNotValidException withFieldErrors(final List<FieldError> fieldErrors) {
        return new MethodArgumentNotValidException(anyMethodParameter(), bindingResultWithFieldErrorsOf(fieldErrors));
    }

    public static MethodArgumentNotValidException withFieldErrorSourceNotBeingConstraintViolation() {
        final val fieldError = mockFieldErrorWithNameAndMessage("any", "any");
        when(fieldError.unwrap(ArgumentMatchers.any())).thenThrow(IllegalArgumentException.class);
        return withFieldErrors(List.of(fieldError));
    }

    public static MethodArgumentNotValidException withErrorFieldViolation(
            final Consumer<ViolationMockBuilder> violationCustomizer) {
        final val fieldError = mockFieldErrorCausedByViolation("any", "any", violationCustomizer);
        return withFieldErrors(List.of(fieldError));
    }

    public static MethodArgumentNotValidException withMultipleFields(final String underlyingPath1, final String defaultMessage1,
                                                                     final Annotation annotation1, final String underlyingPath2, final String defaultMessage2, final Annotation annotation2) {
        final val fieldError1 = mockFieldErrorCausedByViolation(underlyingPath1, defaultMessage1,
                createViolationCausedBy(annotation1));
        final val fieldError2 = mockFieldErrorCausedByViolation(underlyingPath2, defaultMessage2,
                createViolationCausedBy(annotation2));
        return withFieldErrors(asList(fieldError1, fieldError2));
    }

    private static FieldError mockFieldErrorCausedByViolation(final String path, final String message,
                                                              final Consumer<ViolationMockBuilder> violationCustomizer) {
        final val builder = new ViolationMockBuilder();
        violationCustomizer.accept(builder);
        final val fieldError = mockFieldErrorWithNameAndMessage(path, message);
        when(fieldError.unwrap(ArgumentMatchers.any())).thenReturn(builder.build());
        return fieldError;
    }

    public static MethodArgumentNotValidException withFieldErrorCausedBy(final Annotation annotation) {
        return withErrorFieldViolation(createViolationCausedBy(annotation));
    }

    private static Consumer<ViolationMockBuilder> createViolationCausedBy(final Annotation annotation) {
        return b -> b.withDescriptor().withAnnotation(annotation);
    }

    private static FieldError mockFieldErrorWithNameAndMessage(final String field, final String defaultMessage) {
        final val fieldError = mock(FieldError.class);
        when(fieldError.getField()).thenReturn(field);
        when(fieldError.getDefaultMessage()).thenReturn(defaultMessage);
        return fieldError;
    }

    static MethodParameter anyMethodParameter() {
        try {
            final val anyMethod = String.class.getMethod("charAt", int.class);
            return new MethodParameter(anyMethod, 0);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static BindingResult bindingResultWithFieldErrorsOf(final List<FieldError> fieldErrors) {
        final val result = mock(BindingResult.class);
        when(result.getFieldErrors()).thenReturn(fieldErrors);
        return result;
    }

    static final class ViolationMockBuilder {
        private final ConstraintViolation violation = mock(ConstraintViolation.class);

        private ConstraintDescriptor descriptor;

        public ViolationMockBuilder withDescriptor() {
            descriptor = mock(ConstraintDescriptor.class);
            when(violation.getConstraintDescriptor()).thenReturn(descriptor);
            return this;
        }

        public ViolationMockBuilder withAnnotation() {
            when(descriptor.getAnnotation()).thenReturn(mock(Annotation.class));
            return this;
        }

        private ViolationMockBuilder withAnnotation(final Annotation annotation) {
            when(descriptor.getAnnotation()).thenReturn(annotation);
            return this;
        }

        private ConstraintViolation build() {
            return violation;
        }
    }
}

final class MethodArgumentNotValidExceptionMockTest {

    @Test
    public void shouldCreateMockWithNullFieldErrors() {
        final val exception = withFieldErrors(null);

        assertThat(exception.getBindingResult().getFieldErrors()).isNull();
    }

    @Test
    public void shouldCreateMockWithEmptyFieldErrors() {
        final val exception = withFieldErrors(List.of());

        assertThat(exception.getBindingResult().getFieldErrors()).hasSize(0);
    }

    @Test
    public void shouldCreateMockContainingErrorsWithoutConstrainViolation() {
        final val exception = withFieldErrorSourceNotBeingConstraintViolation();

        final val fieldErrors = exception.getBindingResult().getFieldErrors();
        assertThat(fieldErrors).hasSize(1);
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> fieldErrors.get(0).unwrap(ConstraintViolation.class));
    }

    @Test
    public void shouldCreateMockContainingErrorsWithViolationsButWithoutDescriptor() {
        final val exception = withErrorFieldViolation(c -> {
        });

        final val violation = exception.getBindingResult().getFieldErrors().get(0)
                .unwrap(ConstraintViolation.class);
        assertThat(violation).isNotNull();
        assertThat(violation.getConstraintDescriptor()).isNull();
    }

    @Test
    public void shouldCreateMockContainingErrorsWithViolationDescriptorButWithoutAnnotation() {
        final val exception = withErrorFieldViolation(ViolationMockBuilder::withDescriptor);

        final val descriptor = exception.getBindingResult().getFieldErrors().get(0)
                .unwrap(ConstraintViolation.class).getConstraintDescriptor();
        assertThat(descriptor).isNotNull();
        assertThat(descriptor.getAnnotation()).isNull();
    }

    @Test
    public void shouldCreateMockContainingErrorsWithCausingAnnotationsButWithoutTheType() {
        final val exception = withErrorFieldViolation(c -> c.withDescriptor().withAnnotation());

        final val violationCausingAnnotation = exception.getBindingResult().getFieldErrors().get(0)
                .unwrap(ConstraintViolation.class).getConstraintDescriptor().getAnnotation();
        assertThat(violationCausingAnnotation).isNotNull();
        assertThat(violationCausingAnnotation.annotationType()).isNull();
    }

    @Test
    public void shouldCreateMockContainingErrorsWithCausingAnnotations() {
        final val exception = withFieldErrorCausedBy(javaLangOverride());

        final val violationCausingAnnotation = exception.getBindingResult().getFieldErrors().get(0)
                .unwrap(ConstraintViolation.class).getConstraintDescriptor().getAnnotation();
        assertThat(violationCausingAnnotation.annotationType()).isNotNull();
    }

    @Test
    public void shouldCreateMockContainingMultipleErrors() {
        final val exception = withMultipleFields("path1", "message1", javaLangOverride(), "path2",
                "message2", javaLangSuppressWarning());

        final val fieldErrors = exception.getBindingResult().getFieldErrors();
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