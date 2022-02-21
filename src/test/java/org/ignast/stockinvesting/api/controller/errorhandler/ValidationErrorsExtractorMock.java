package org.ignast.stockinvesting.api.controller.errorhandler;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ValidationErrorsExtractorMock {
    public static ValidationErrorsExtractor returningErrors() {
        ValidationErrorsExtractor extractor = mock(ValidationErrorsExtractor.class);
        when(extractor.extractAnnotationBasedErrorsFrom(notNull())).thenReturn(Arrays.asList(anyValidationError()));
        return extractor;
    }

    private static ValidationError anyValidationError() {
        return new ValidationError("anyPath", "anyMessage", ViolationType.FIELD_IS_MISSING);
    }
}

class ValidationErrorExtractorMockTest {

    @Test
    public void shouldExtractAnError() {
        ValidationErrorsExtractor extractor = ValidationErrorsExtractorMock.returningErrors();

        assertThat(extractor.extractAnnotationBasedErrorsFrom(mock(MethodArgumentNotValidException.class))).hasSize(1);
    }

    @Test
    public void shouldNotActivateMockBehaviourIfNullErrorsArePassedIn() {
        ValidationErrorsExtractor extractor = ValidationErrorsExtractorMock.returningErrors();

        assertThat(extractor.extractAnnotationBasedErrorsFrom(null)).isEmpty();
    }
}
