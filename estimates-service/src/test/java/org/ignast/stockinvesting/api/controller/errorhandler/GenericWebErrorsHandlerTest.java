package org.ignast.stockinvesting.api.controller.errorhandler;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.ignast.stockinvesting.api.controller.errorhandler.StandardErrorDTO.BodyDoesNotMatchSchemaErrorDTO;
import org.ignast.stockinvesting.mockito.MockitoUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.api.controller.errorhandler.Errors.anyValidationErrorDTO;
import static org.ignast.stockinvesting.api.controller.errorhandler.HttpMessageNotReadableExceptionMock.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ControllerAdviceForGenericErrorsForInvalidArgumentsTest {

    private AnnotationBasedValidationErrorsExtractor javaxErrorExtractor = mock(
            AnnotationBasedValidationErrorsExtractor.class);

    private ControllerAdviceForGenericErrors handler = new ControllerAdviceForGenericErrors(javaxErrorExtractor,
            mock(JacksonParsingErrorsExtractor.class));

    @Test
    public void shouldExtractJavaxValidationErrors() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(javaxErrorExtractor.extractAnnotationBasedErrorsFrom(notNull()))
                .thenReturn(Arrays.asList(anyValidationErrorDTO()));

        StandardErrorDTO error = handler.handleMethodArgumentNotValidException(exception);

        assertThat(error).isInstanceOf(BodyDoesNotMatchSchemaErrorDTO.class);
        assertThat(((BodyDoesNotMatchSchemaErrorDTO) error).getValidationErrors()).isNotEmpty();
    }

    @Test
    public void validationExtractorFailingToExtractExpectedErrorsShouldResultInNamelessError() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(javaxErrorExtractor.extractAnnotationBasedErrorsFrom(notNull()))
                .thenThrow(ValidationErrorsExtractionException.class);

        StandardErrorDTO error = handler.handleMethodArgumentNotValidException(exception);

        assertThat(error.getErrorName()).isNull();
    }

}

class ControllerAdviceForGenericErrorsForJacksonParsingTest {
    private JacksonParsingErrorsExtractor jacksonErrorExtractor = mock(JacksonParsingErrorsExtractor.class);

    private ControllerAdviceForGenericErrors handler = new ControllerAdviceForGenericErrors(
            mock(AnnotationBasedValidationErrorsExtractor.class), jacksonErrorExtractor);

    @Test
    public void shouldExtractJacksonParsingError() {
        when(jacksonErrorExtractor.extractError(any())).thenReturn(anyValidationErrorDTO());

        StandardErrorDTO error = handler.handleUnparsableJson(jacksonFieldLevelError());

        assertThat(error).isInstanceOf(BodyDoesNotMatchSchemaErrorDTO.class);
        assertThat(((BodyDoesNotMatchSchemaErrorDTO) error).getValidationErrors()).isNotEmpty();
    }

    @Test
    public void shouldIndicateBodyIsUnparsableWhenExceptionHasNoCause() {
        when(jacksonErrorExtractor.extractError(any())).thenReturn(anyValidationErrorDTO());

        StandardErrorDTO error = handler.handleUnparsableJson(withoutCause());

        assertThat(error.getErrorName()).isEqualTo("bodyNotParsable");
    }

    @Test
    public void shouldIndicateBodyIsUnparsableWhenExceptionHasUnrecognisedCause() {
        when(jacksonErrorExtractor.extractError(any())).thenReturn(anyValidationErrorDTO());

        StandardErrorDTO error = handler.handleUnparsableJson(unknownCause());

        assertThat(error.getErrorName()).isEqualTo("bodyNotParsable");
    }

    @Test
    public void failingToExtractJacksonShouldResultNamelessError() {
        when(jacksonErrorExtractor.extractError(any())).thenThrow(JacksonParsingErrorExtractionException.class);

        StandardErrorDTO error = handler.handleUnparsableJson(jacksonFieldLevelError());

        assertThat(error.getErrorName()).isNull();
    }
}

class ControllerAdviceForGenericErrorsHandlerForOtherErrorsTest {
    private ControllerAdviceForGenericErrors handler = new ControllerAdviceForGenericErrors(
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

class HttpMessageNotReadableExceptionMock {

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

class HttpMessageNotReadableExceptionMockTest {
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