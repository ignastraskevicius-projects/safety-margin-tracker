package org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.postparsed;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.postparsed.ValidationErrorDTOs.anyValidationErrorDTO;
import static org.ignast.stockinvesting.utiltest.ExceptionAssert.assertThatNullPointerExceptionIsThrownBy;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import lombok.val;
import org.ignast.stockinvesting.util.errorhandling.api.dto.StandardErrorDTO;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.MethodArgumentNotValidException;

public final class ControllerAdviceForPostParsedValidationTest {

    @Test
    public void shouldNotCreateWithNullArguments() {
        assertThatNullPointerExceptionIsThrownBy(() -> new ControllerAdviceForPostParsedValidation(null));
        new ControllerAdviceForPostParsedValidation(mock(AnnotationBasedValidationErrorsExtractor.class));
    }
}

final class ControllerAdviceForInvalidArgumentsTest {

    private final AnnotationBasedValidationErrorsExtractor javaxErrorExtractor = mock(
        AnnotationBasedValidationErrorsExtractor.class
    );

    private final ControllerAdviceForPostParsedValidation handler = new ControllerAdviceForPostParsedValidation(
        javaxErrorExtractor
    );

    @Test
    public void shouldExtractJavaxValidationErrors() {
        final val exception = mock(MethodArgumentNotValidException.class);
        when(javaxErrorExtractor.extractAnnotationBasedErrorsFrom(notNull()))
            .thenReturn(of(anyValidationErrorDTO()));

        final val error = handler.handleMethodArgumentNotValidException(exception);

        assertThat(error).isInstanceOf(StandardErrorDTO.BodyDoesNotMatchSchemaErrorDTO.class);
        final val rootValidationError = (StandardErrorDTO.BodyDoesNotMatchSchemaErrorDTO) error;
        assertThat(rootValidationError.getValidationErrors()).isNotEmpty();
        assertThat(rootValidationError.getHttpStatus()).isEqualTo(BAD_REQUEST.value());
    }

    @Test
    public void validationExtractorFailingToExtractExpectedErrorsShouldResultInNamelessError() {
        final val exception = mock(MethodArgumentNotValidException.class);
        when(javaxErrorExtractor.extractAnnotationBasedErrorsFrom(notNull()))
            .thenThrow(AnnotationBasedValidationErrorsExtractor.ExtractionException.class);

        final val error = handler.handleMethodArgumentNotValidException(exception);

        assertThat(error.getErrorName()).isNull();
        assertThat(error.getHttpStatus()).isEqualTo(BAD_REQUEST.value());
    }
}
