package org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.parsing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.parsing.HttpMessageNotReadableExceptionMock.jacksonFieldLevelError;
import static org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.parsing.HttpMessageNotReadableExceptionMock.unknownCause;
import static org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.parsing.HttpMessageNotReadableExceptionMock.withoutCause;
import static org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.postparsed.ValidationErrorDTOs.anyValidationErrorDTO;
import static org.ignast.stockinvesting.utiltest.ExceptionAssert.assertThatNullPointerExceptionIsThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import lombok.val;
import org.ignast.stockinvesting.util.errorhandling.api.dto.StandardErrorDTO;
import org.ignast.stockinvesting.utiltest.MockitoUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.HttpMessageNotReadableException;

public final class ControllerAdviceForParsingValidationTest {

    @Test
    public void shouldNotCreateWithNullArguments() {
        assertThatNullPointerExceptionIsThrownBy(() -> new ControllerAdviceForParsingValidation(null));
        new ControllerAdviceForParsingValidation(mock(JacksonParsingErrorsExtractor.class));
    }
}

final class ControllerAdviceForJacksonParsingTest {

    private final JacksonParsingErrorsExtractor jacksonErrorExtractor = mock(
        JacksonParsingErrorsExtractor.class
    );

    private final ControllerAdviceForParsingValidation handler = new ControllerAdviceForParsingValidation(
        jacksonErrorExtractor
    );

    @Test
    public void shouldExtractJacksonParsingError() {
        when(jacksonErrorExtractor.extractError(any())).thenReturn(anyValidationErrorDTO());

        final val error = handler.handleUnparsableJson(jacksonFieldLevelError());

        assertThat(error).isInstanceOf(StandardErrorDTO.BodyDoesNotMatchSchemaErrorDTO.class);
        final val rootValidationError = (StandardErrorDTO.BodyDoesNotMatchSchemaErrorDTO) error;
        assertThat(rootValidationError.getValidationErrors()).isNotEmpty();
        assertThat(rootValidationError.getHttpStatus()).isEqualTo(BAD_REQUEST.value());
    }

    @Test
    public void shouldIndicateBodyIsUnparsableWhenExceptionHasNoCause() {
        when(jacksonErrorExtractor.extractError(any())).thenReturn(anyValidationErrorDTO());

        final val error = handler.handleUnparsableJson(withoutCause());

        assertThat(error.getErrorName()).isEqualTo("bodyNotParsable");
        assertThat(error.getHttpStatus()).isEqualTo(BAD_REQUEST.value());
    }

    @Test
    public void shouldIndicateBodyIsUnparsableWhenExceptionHasUnrecognisedCause() {
        when(jacksonErrorExtractor.extractError(any())).thenReturn(anyValidationErrorDTO());

        final val error = handler.handleUnparsableJson(unknownCause());

        assertThat(error.getErrorName()).isEqualTo("bodyNotParsable");
        assertThat(error.getHttpStatus()).isEqualTo(BAD_REQUEST.value());
    }

    @Test
    public void failingToExtractJacksonShouldResultNamelessError() {
        when(jacksonErrorExtractor.extractError(any()))
            .thenThrow(JacksonParsingErrorsExtractor.ExtractionException.class);

        final val error = handler.handleUnparsableJson(jacksonFieldLevelError());

        assertThat(error.getErrorName()).isNull();
        assertThat(error.getHttpStatus()).isEqualTo(BAD_REQUEST.value());
    }
}

final class HttpMessageNotReadableExceptionMock {

    private HttpMessageNotReadableExceptionMock() {}

    public static HttpMessageNotReadableException jacksonFieldLevelError() {
        return MockitoUtils.mock(
            HttpMessageNotReadableException.class,
            e -> when(e.getCause()).thenReturn(mock(MismatchedInputException.class))
        );
    }

    public static HttpMessageNotReadableException unknownCause() {
        return MockitoUtils.mock(
            HttpMessageNotReadableException.class,
            e -> when(e.getCause()).thenReturn(mock(IllegalStateException.class))
        );
    }

    public static HttpMessageNotReadableException withoutCause() {
        return mock(HttpMessageNotReadableException.class);
    }
}

final class HttpMessageNotReadableExceptionMockTest {

    @Test
    public void shouldReturnWithCauseIndicatingFieldLevelError() {
        assertThat(jacksonFieldLevelError()).hasCauseInstanceOf(MismatchedInputException.class);
    }

    @Test
    public void shouldReturnWithCauseIndicatingNotFieldLevelError() {
        assertThat(unknownCause()).hasCauseInstanceOf(IllegalStateException.class);
    }

    @Test
    public void shouldReturnWithNoCause() {
        assertThat(withoutCause()).hasNoCause();
    }
}
