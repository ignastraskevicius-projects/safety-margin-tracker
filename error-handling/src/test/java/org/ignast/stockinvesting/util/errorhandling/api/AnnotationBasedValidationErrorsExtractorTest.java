package org.ignast.stockinvesting.util.errorhandling.api;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.util.errorhandling.api.AnnotationStubs.javaLangOverride;
import static org.ignast.stockinvesting.util.errorhandling.api.AnnotationStubs.javaLangSuppressWarning;
import static org.ignast.stockinvesting.util.errorhandling.api.AnnotationStubs.javaxValidationDomainClassConstraint;
import static org.ignast.stockinvesting.util.errorhandling.api.AnnotationStubs.javaxValidationNotNull;
import static org.ignast.stockinvesting.util.errorhandling.api.AnnotationStubs.javaxValidationPattern;
import static org.ignast.stockinvesting.util.errorhandling.api.AnnotationStubs.javaxValidationSize;
import static org.ignast.stockinvesting.util.errorhandling.api.MethodArgumentNotValidExceptionMock.withErrorFieldViolation;

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
                .withFieldErrorSourceNotBeingConstraintViolation();

        assertThatExceptionOfType(ValidationErrorsExtractionException.class)
                .isThrownBy(() -> errorsExtractor.extractAnnotationBasedErrorsFrom(exception))
                .withMessageContaining(
                        "Expected javax.validation ConstraintViolation but validation failed due to a different cause")
                .withCauseInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void shouldThrowIfAnyViolationIsNotCausedByJavaxAnnotation() {
        MethodArgumentNotValidException withoutDescriptor = withErrorFieldViolation(b -> {
        });
        MethodArgumentNotValidException withoutAnnotation = withErrorFieldViolation(b -> b.withDescriptor());
        MethodArgumentNotValidException withoutAnnotationType = withErrorFieldViolation(
                b -> b.withDescriptor().withAnnotation());
        asList(withoutDescriptor, withoutAnnotation, withoutAnnotationType).stream().forEach(exception -> {

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

        List<ValidationErrorDTO> validationErrors = errorsExtractor.extractAnnotationBasedErrorsFrom(exception);

        assertThat(validationErrors).hasSize(1);
        ValidationErrorDTO validationError = validationErrors.get(0);
        assertThat(validationError.getErrorName()).isEqualTo("fieldIsMissing");
    }

    @Test
    public void shouldExtractFieldErrorRelatedToSizeRestrictions() {
        MethodArgumentNotValidException exception = MethodArgumentNotValidExceptionMock
                .withFieldErrorCausedBy(javaxValidationSize());

        List<ValidationErrorDTO> validationErrors = errorsExtractor.extractAnnotationBasedErrorsFrom(exception);

        assertThat(validationErrors).hasSize(1);
        ValidationErrorDTO validationError = validationErrors.get(0);
        assertThat(validationError.getErrorName()).isEqualTo("valueIsInvalid");
    }

    @Test
    public void shouldExtractFieldErrorRelatedToPatternRestrictions() {
        MethodArgumentNotValidException exception = MethodArgumentNotValidExceptionMock
                .withFieldErrorCausedBy(javaxValidationPattern());

        List<ValidationErrorDTO> validationErrors = errorsExtractor.extractAnnotationBasedErrorsFrom(exception);

        assertThat(validationErrors).hasSize(1);
        ValidationErrorDTO validationError = validationErrors.get(0);
        assertThat(validationError.getErrorName()).isEqualTo("valueIsInvalid");
    }

    @Test
    public void shouldExtractFieldErrorRelatedToDomainClassConstraint() {
        val exception = MethodArgumentNotValidExceptionMock
                .withFieldErrorCausedBy(javaxValidationDomainClassConstraint());

        val validationErrors = errorsExtractor.extractAnnotationBasedErrorsFrom(exception);

        assertThat(validationErrors).hasSize(1);
        ValidationErrorDTO validationError = validationErrors.get(0);
        assertThat(validationError.getErrorName()).isEqualTo("valueIsInvalid");
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

        List<ValidationErrorDTO> validationErrors = errorsExtractor.extractAnnotationBasedErrorsFrom(exception);

        assertThat(validationErrors).hasSize(2);
        ValidationErrorDTO validationError1 = validationErrors.get(0);
        ValidationErrorDTO validationError2 = validationErrors.get(1);
        assertThat(validationError1.getJsonPath()).isEqualTo("$.path1");
        assertThat(validationError1.getMessage()).isNull();
        assertThat(validationError1.getErrorName()).isEqualTo("fieldIsMissing");
        assertThat(validationError2.getJsonPath()).isEqualTo("$.path2");
        assertThat(validationError2.getMessage()).isEqualTo("message2");
        assertThat(validationError2.getErrorName()).isEqualTo("valueIsInvalid");
    }
}