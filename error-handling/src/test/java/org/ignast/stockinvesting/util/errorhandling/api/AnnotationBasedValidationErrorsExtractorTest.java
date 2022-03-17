package org.ignast.stockinvesting.util.errorhandling.api;

import lombok.val;
import org.ignast.stockinvesting.util.errorhandling.api.MethodArgumentNotValidExceptionMock.ViolationMockBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.util.errorhandling.api.AnnotationStubs.javaLangOverride;
import static org.ignast.stockinvesting.util.errorhandling.api.AnnotationStubs.javaLangSuppressWarning;
import static org.ignast.stockinvesting.util.errorhandling.api.AnnotationStubs.javaxValidationDomainClassConstraint;
import static org.ignast.stockinvesting.util.errorhandling.api.AnnotationStubs.javaxValidationNotNull;
import static org.ignast.stockinvesting.util.errorhandling.api.AnnotationStubs.javaxValidationPattern;
import static org.ignast.stockinvesting.util.errorhandling.api.AnnotationStubs.javaxValidationSize;
import static org.ignast.stockinvesting.util.errorhandling.api.MethodArgumentNotValidExceptionMock.withErrorFieldViolation;

public final class AnnotationBasedValidationErrorsExtractorTest {

    private final AnnotationBasedValidationErrorsExtractor errorsExtractor = new AnnotationBasedValidationErrorsExtractor();

    @Test
    public void shouldThrowIfExceptionContainsNullFieldErrors() {
        final val exception = MethodArgumentNotValidExceptionMock.withFieldErrors(null);

        assertThatExceptionOfType(ValidationErrorsExtractionException.class)
                .isThrownBy(() -> errorsExtractor.extractAnnotationBasedErrorsFrom(exception))
                .withMessageContaining("javax.validation exception is expected to contain at least 1 field error");
    }

    @Test
    public void shouldThrowIfExceptionContainsNoFieldErrors() {
        final val exception = MethodArgumentNotValidExceptionMock
                .withFieldErrors(new ArrayList<>());

        assertThatExceptionOfType(ValidationErrorsExtractionException.class)
                .isThrownBy(() -> errorsExtractor.extractAnnotationBasedErrorsFrom(exception))
                .withMessageContaining("javax.validation exception is expected to contain at least 1 field error");
    }

    @Test
    public void shouldThrowIfFieldErrorSourceIsNotConstraintViolation() {
        final val exception = MethodArgumentNotValidExceptionMock
                .withFieldErrorSourceNotBeingConstraintViolation();

        assertThatExceptionOfType(ValidationErrorsExtractionException.class)
                .isThrownBy(() -> errorsExtractor.extractAnnotationBasedErrorsFrom(exception))
                .withMessageContaining(
                        "Expected javax.validation ConstraintViolation but validation failed due to a different cause")
                .withCauseInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void shouldThrowIfAnyViolationIsNotCausedByJavaxAnnotation() {
        final val withoutDescriptor = withErrorFieldViolation(b -> {
        });
        final val withoutAnnotation = withErrorFieldViolation(ViolationMockBuilder::withDescriptor);
        final val withoutAnnotationType = withErrorFieldViolation(
                b -> b.withDescriptor().withAnnotation());
        Stream.of(withoutDescriptor, withoutAnnotation, withoutAnnotationType).forEach(exception ->
                assertThatExceptionOfType(ValidationErrorsExtractionException.class)
                .isThrownBy(() -> errorsExtractor.extractAnnotationBasedErrorsFrom(exception))
                .withMessage(
                        "Extraction of javax.validation error was caused by violation not defined via annotation")
                .withCauseInstanceOf(NullPointerException.class));
    }

    @Test
    public void ensureThatUnderlyingFieldNameIsNeverNull() {
        final String fieldName = null;
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new FieldError("company", fieldName, "message"));
    }

    @Test
    public void shouldExtractMissingFieldError() {
        final val exception = MethodArgumentNotValidExceptionMock
                .withFieldErrorCausedBy(javaxValidationNotNull());

        final val validationErrors = errorsExtractor.extractAnnotationBasedErrorsFrom(exception);

        assertThat(validationErrors).hasSize(1);
        final val validationError = validationErrors.get(0);
        assertThat(validationError.getErrorName()).isEqualTo("fieldIsMissing");
    }

    @Test
    public void shouldExtractFieldErrorRelatedToSizeRestrictions() {
        final val exception = MethodArgumentNotValidExceptionMock
                .withFieldErrorCausedBy(javaxValidationSize());

        final val validationErrors = errorsExtractor.extractAnnotationBasedErrorsFrom(exception);

        assertThat(validationErrors).hasSize(1);
        final val validationError = validationErrors.get(0);
        assertThat(validationError.getErrorName()).isEqualTo("valueIsInvalid");
    }

    @Test
    public void shouldExtractFieldErrorRelatedToPatternRestrictions() {
        final val exception = MethodArgumentNotValidExceptionMock
                .withFieldErrorCausedBy(javaxValidationPattern());

        final val validationErrors = errorsExtractor.extractAnnotationBasedErrorsFrom(exception);

        assertThat(validationErrors).hasSize(1);
        final val validationError = validationErrors.get(0);
        assertThat(validationError.getErrorName()).isEqualTo("valueIsInvalid");
    }

    @Test
    public void shouldExtractFieldErrorRelatedToDomainClassConstraint() {
        final val exception = MethodArgumentNotValidExceptionMock
                .withFieldErrorCausedBy(javaxValidationDomainClassConstraint());

        final val validationErrors = errorsExtractor.extractAnnotationBasedErrorsFrom(exception);

        assertThat(validationErrors).hasSize(1);
        final val validationError = validationErrors.get(0);
        assertThat(validationError.getErrorName()).isEqualTo("valueIsInvalid");
    }

    @Test
    public void shouldDropFieldErrorRelatedToUnexpectedAnnotationsLikeOverride() {
        final val exception = MethodArgumentNotValidExceptionMock
                .withFieldErrorCausedBy(javaLangOverride());

        assertThatExceptionOfType(ValidationErrorsExtractionException.class)
                .isThrownBy(() -> errorsExtractor.extractAnnotationBasedErrorsFrom(exception)).withMessage(
                        "Extraction of javax.validation error due to violation caused by annotation 'java.lang.Override' is not supported");
    }

    @Test
    public void shouldDropFieldErrorRelatedToUnexpectedAnnotationsLikeSuppressWarning() {
        final val exception = MethodArgumentNotValidExceptionMock
                .withFieldErrorCausedBy(javaLangSuppressWarning());

        assertThatExceptionOfType(ValidationErrorsExtractionException.class)
                .isThrownBy(() -> errorsExtractor.extractAnnotationBasedErrorsFrom(exception)).withMessage(
                        "Extraction of javax.validation error due to violation caused by annotation 'java.lang.SuppressWarnings' is not supported");
    }

    @Test
    public void shouldExtractMultipleFieldErrors() {
        final val exception = MethodArgumentNotValidExceptionMock.withMultipleFields("path1",
                "message1", javaxValidationNotNull(), "path2", "message2", javaxValidationSize());

        final val validationErrors = errorsExtractor.extractAnnotationBasedErrorsFrom(exception);

        assertThat(validationErrors).hasSize(2);
        final val validationError1 = validationErrors.get(0);
        final val validationError2 = validationErrors.get(1);
        assertThat(validationError1.getJsonPath()).isEqualTo("$.path1");
        assertThat(validationError1.getMessage()).isNull();
        assertThat(validationError1.getErrorName()).isEqualTo("fieldIsMissing");
        assertThat(validationError2.getJsonPath()).isEqualTo("$.path2");
        assertThat(validationError2.getMessage()).isEqualTo("message2");
        assertThat(validationError2.getErrorName()).isEqualTo("valueIsInvalid");
    }
}