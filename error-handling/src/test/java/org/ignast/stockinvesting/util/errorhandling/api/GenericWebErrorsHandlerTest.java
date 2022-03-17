package org.ignast.stockinvesting.util.errorhandling.api;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import lombok.val;
import org.ignast.stockinvesting.util.errorhandling.api.StandardErrorDTO.BodyDoesNotMatchSchemaErrorDTO;
import org.ignast.stockinvesting.util.mockito.MockitoUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.util.errorhandling.api.Errors.anyValidationErrorDTO;
import static org.ignast.stockinvesting.util.errorhandling.api.HttpMessageNotReadableExceptionMock.jacksonFieldLevelError;
import static org.ignast.stockinvesting.util.errorhandling.api.HttpMessageNotReadableExceptionMock.unknownCause;
import static org.ignast.stockinvesting.util.errorhandling.api.HttpMessageNotReadableExceptionMock.withoutCause;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class ControllerAdviceForGenericErrorsForInvalidArgumentsTest {

    private final AnnotationBasedValidationErrorsExtractor javaxErrorExtractor = mock(
            AnnotationBasedValidationErrorsExtractor.class);

    private final ControllerAdviceForGenericErrors handler = new ControllerAdviceForGenericErrors(javaxErrorExtractor,
            mock(JacksonParsingErrorsExtractor.class));

    @Test
    public void shouldExtractJavaxValidationErrors() {
        final val exception = mock(MethodArgumentNotValidException.class);
        when(javaxErrorExtractor.extractAnnotationBasedErrorsFrom(notNull()))
                .thenReturn(of(anyValidationErrorDTO()));

        final val error = handler.handleMethodArgumentNotValidException(exception);

        assertThat(error).isInstanceOf(BodyDoesNotMatchSchemaErrorDTO.class);
        assertThat(((BodyDoesNotMatchSchemaErrorDTO) error).getValidationErrors()).isNotEmpty();
    }

    @Test
    public void validationExtractorFailingToExtractExpectedErrorsShouldResultInNamelessError() {
        final val exception = mock(MethodArgumentNotValidException.class);
        when(javaxErrorExtractor.extractAnnotationBasedErrorsFrom(notNull()))
                .thenThrow(ValidationErrorsExtractionException.class);

        final val error = handler.handleMethodArgumentNotValidException(exception);

        assertThat(error.getErrorName()).isNull();
    }

}

final class ControllerAdviceForGenericErrorsForJacksonParsingTest {
    private final JacksonParsingErrorsExtractor jacksonErrorExtractor = mock(JacksonParsingErrorsExtractor.class);

    private final ControllerAdviceForGenericErrors handler = new ControllerAdviceForGenericErrors(
            mock(AnnotationBasedValidationErrorsExtractor.class), jacksonErrorExtractor);

    @Test
    public void shouldExtractJacksonParsingError() {
        when(jacksonErrorExtractor.extractError(any())).thenReturn(anyValidationErrorDTO());

        final val error = handler.handleUnparsableJson(jacksonFieldLevelError());

        assertThat(error).isInstanceOf(BodyDoesNotMatchSchemaErrorDTO.class);
        assertThat(((BodyDoesNotMatchSchemaErrorDTO) error).getValidationErrors()).isNotEmpty();
    }

    @Test
    public void shouldIndicateBodyIsUnparsableWhenExceptionHasNoCause() {
        when(jacksonErrorExtractor.extractError(any())).thenReturn(anyValidationErrorDTO());

        final val error = handler.handleUnparsableJson(withoutCause());

        assertThat(error.getErrorName()).isEqualTo("bodyNotParsable");
    }

    @Test
    public void shouldIndicateBodyIsUnparsableWhenExceptionHasUnrecognisedCause() {
        when(jacksonErrorExtractor.extractError(any())).thenReturn(anyValidationErrorDTO());

        final val error = handler.handleUnparsableJson(unknownCause());

        assertThat(error.getErrorName()).isEqualTo("bodyNotParsable");
    }

    @Test
    public void failingToExtractJacksonShouldResultNamelessError() {
        when(jacksonErrorExtractor.extractError(any())).thenThrow(JacksonParsingErrorExtractionException.class);

        final val error = handler.handleUnparsableJson(jacksonFieldLevelError());

        assertThat(error.getErrorName()).isNull();
    }
}

final class ControllerAdviceForGenericErrorsHandlerForOtherErrorsTest {
    private final ControllerAdviceForGenericErrors handler = new ControllerAdviceForGenericErrors(
            mock(AnnotationBasedValidationErrorsExtractor.class), mock(JacksonParsingErrorsExtractor.class));

    @Test
    public void shouldHandleMethodNotAllowed() {
        assertThat(handler.handleMethodNotAllowed(mock(HttpRequestMethodNotSupportedException.class)).getErrorName())
                .isEqualTo("methodNotAllowed");
    }

    @Test
    public void shouldHandleMediaTypeNotAcceptable() {
        assertThat(handler.handleMediaTypeNotAcceptable(mock(HttpMediaTypeNotAcceptableException.class)).getErrorName())
                .isEqualTo("mediaTypeNotAcceptable");
    }

    @Test
    public void shouldHandleContentTypeNotSupported() {
        assertThat(handler.handleUnsupportedContentType(mock(HttpMediaTypeNotSupportedException.class)).getErrorName())
                .isEqualTo("unsupportedContentType");
    }
}

final class HttpMessageNotReadableExceptionMock {

    public static HttpMessageNotReadableException jacksonFieldLevelError() {
        return MockitoUtils.mock(HttpMessageNotReadableException.class,
                e -> when(e.getCause()).thenReturn(mock(MismatchedInputException.class)));
    }

    public static HttpMessageNotReadableException unknownCause() {
        return MockitoUtils.mock(HttpMessageNotReadableException.class,
                e -> when(e.getCause()).thenReturn(mock(IllegalStateException.class)));
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