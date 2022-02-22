package org.ignast.stockinvesting.api.controller.errorhandler;

import org.junit.jupiter.api.Test;
import org.springframework.validation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.api.controller.errorhandler.AnnotationStubs.*;
import static org.ignast.stockinvesting.api.controller.errorhandler.MethodArgumentNotValidExceptionMock.withErrorFieldViolation;

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
        assertThat(validationError1.getJsonPath()).isEqualTo("$.path1");
        assertThat(validationError1.getMessage()).isEqualTo("message1");
        assertThat(validationError1.getType()).isEqualTo(ViolationType.FIELD_IS_MISSING);
        assertThat(validationError2.getJsonPath()).isEqualTo("$.path2");
        assertThat(validationError2.getMessage()).isEqualTo("message2");
        assertThat(validationError2.getType()).isEqualTo(ViolationType.VALUE_INVALID);
    }
}