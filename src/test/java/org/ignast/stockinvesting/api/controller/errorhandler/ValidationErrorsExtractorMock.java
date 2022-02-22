package org.ignast.stockinvesting.api.controller.errorhandler;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ValidationErrorsExtractorMock {
    public static AnnotationBasedValidationErrorsExtractor returningErrors() {
        AnnotationBasedValidationErrorsExtractor extractor = mock(AnnotationBasedValidationErrorsExtractor.class);
        when(extractor.extractAnnotationBasedErrorsFrom(notNull())).thenReturn(Arrays.asList(anyValidationError()));
        return extractor;
    }

    private static ValidationError anyValidationError() {
        return new ValidationError("anyPath", "anyMessage", ViolationType.FIELD_IS_MISSING);
    }

    public static AnnotationBasedValidationErrorsExtractor failingToExtract() {
        AnnotationBasedValidationErrorsExtractor extractor = mock(AnnotationBasedValidationErrorsExtractor.class);
        when(extractor.extractAnnotationBasedErrorsFrom(notNull()))
                .thenThrow(ValidationErrorsExtractionException.class);
        return extractor;
    }
}

class ValidationErrorExtractorMockTest {

    private static final MethodArgumentNotValidException ANY_EXCEPTION = mock(MethodArgumentNotValidException.class);

    @Test
    public void shouldExtractAnError() {
        AnnotationBasedValidationErrorsExtractor extractor = ValidationErrorsExtractorMock.returningErrors();

        assertThat(extractor.extractAnnotationBasedErrorsFrom(ANY_EXCEPTION)).hasSize(1);
    }

    @Test
    public void shouldNotActivateMockedExtractionBehaviourIfNullExceptionArePassedIn() {
        AnnotationBasedValidationErrorsExtractor extractor = ValidationErrorsExtractorMock.returningErrors();

        assertThat(extractor.extractAnnotationBasedErrorsFrom(null)).isEmpty();
    }

    @Test
    public void shouldFailToExtract() {
        AnnotationBasedValidationErrorsExtractor extractor = ValidationErrorsExtractorMock.failingToExtract();

        assertThatExceptionOfType(ValidationErrorsExtractionException.class)
                .isThrownBy(() -> extractor.extractAnnotationBasedErrorsFrom(ANY_EXCEPTION));
    }

    @Test
    public void shouldNotActivateMockedFailureBehaviourIfNullExceptionArePassedIn() {
        AnnotationBasedValidationErrorsExtractor extractor = ValidationErrorsExtractorMock.failingToExtract();

        assertThat(extractor.extractAnnotationBasedErrorsFrom(null)).isEmpty();
    }
}
