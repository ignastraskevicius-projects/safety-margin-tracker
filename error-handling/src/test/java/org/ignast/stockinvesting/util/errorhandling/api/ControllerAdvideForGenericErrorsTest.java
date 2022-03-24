package org.ignast.stockinvesting.util.errorhandling.api;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.util.errorhandling.api.HttpMessageNotReadableExceptionMock.jacksonFieldLevelError;
import static org.ignast.stockinvesting.util.errorhandling.api.HttpMessageNotReadableExceptionMock.unknownCause;
import static org.ignast.stockinvesting.util.errorhandling.api.HttpMessageNotReadableExceptionMock.withoutCause;
import static org.ignast.stockinvesting.util.errorhandling.api.ValidationErrorDTOs.anyValidationErrorDTO;
import static org.ignast.stockinvesting.utiltest.ExceptionAssert.assertThatNullPointerExceptionIsThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import lombok.val;
import org.ignast.stockinvesting.util.errorhandling.api.StandardErrorDTO.BodyDoesNotMatchSchemaErrorDTO;
import org.ignast.stockinvesting.utiltest.MockitoUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

@SuppressWarnings("checkstyle:outertypefilename")
final class ControllerAdviceForGenericErrorsForInvalidArgumentsTest {

    private final AnnotationBasedValidationErrorsExtractor javaxErrorExtractor = mock(
        AnnotationBasedValidationErrorsExtractor.class
    );

    private final ControllerAdviceForGenericErrors handler = new ControllerAdviceForGenericErrors(
        javaxErrorExtractor,
        mock(JacksonParsingErrorsExtractor.class),
        mock(MediaType.class)
    );

    @Test
    public void shouldExtractJavaxValidationErrors() {
        final val exception = mock(MethodArgumentNotValidException.class);
        when(javaxErrorExtractor.extractAnnotationBasedErrorsFrom(notNull()))
            .thenReturn(of(anyValidationErrorDTO()));

        final val error = handler.handleMethodArgumentNotValidException(exception);

        assertThat(error).isInstanceOf(BodyDoesNotMatchSchemaErrorDTO.class);
        final val rootValidationError = (BodyDoesNotMatchSchemaErrorDTO) error;
        assertThat(rootValidationError.getValidationErrors()).isNotEmpty();
        assertThat(rootValidationError.getHttpStatus()).isEqualTo(BAD_REQUEST.value());
    }

    @Test
    public void validationExtractorFailingToExtractExpectedErrorsShouldResultInNamelessError() {
        final val exception = mock(MethodArgumentNotValidException.class);
        when(javaxErrorExtractor.extractAnnotationBasedErrorsFrom(notNull()))
            .thenThrow(ValidationErrorsExtractionException.class);

        final val error = handler.handleMethodArgumentNotValidException(exception);

        assertThat(error.getErrorName()).isNull();
        assertThat(error.getHttpStatus()).isEqualTo(BAD_REQUEST.value());
    }
}

final class ControllerAdviceForGenericErrorsForJacksonParsingTest {

    private final JacksonParsingErrorsExtractor jacksonErrorExtractor = mock(
        JacksonParsingErrorsExtractor.class
    );

    private final ControllerAdviceForGenericErrors handler = new ControllerAdviceForGenericErrors(
        mock(AnnotationBasedValidationErrorsExtractor.class),
        jacksonErrorExtractor,
        mock(MediaType.class)
    );

    @Test
    public void shouldExtractJacksonParsingError() {
        when(jacksonErrorExtractor.extractError(any())).thenReturn(anyValidationErrorDTO());

        final val error = handler.handleUnparsableJson(jacksonFieldLevelError());

        assertThat(error).isInstanceOf(BodyDoesNotMatchSchemaErrorDTO.class);
        final val rootValidationError = (BodyDoesNotMatchSchemaErrorDTO) error;
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
            .thenThrow(JacksonParsingErrorExtractionException.class);

        final val error = handler.handleUnparsableJson(jacksonFieldLevelError());

        assertThat(error.getErrorName()).isNull();
        assertThat(error.getHttpStatus()).isEqualTo(BAD_REQUEST.value());
    }
}

final class ControllerAdviceForGenericErrorsHandlerForOtherErrorsTest {

    private final MediaType appMediaType = MediaType.parseMediaType("application/specific.hal+json");

    private final ControllerAdviceForGenericErrors handler = new ControllerAdviceForGenericErrors(
        mock(AnnotationBasedValidationErrorsExtractor.class),
        mock(JacksonParsingErrorsExtractor.class),
        appMediaType
    );

    @Test
    public void shouldNotCreateWithNullArguments() {
        assertThatNullPointerExceptionIsThrownBy(
            () ->
                new ControllerAdviceForGenericErrors(
                    mock(AnnotationBasedValidationErrorsExtractor.class),
                    mock(JacksonParsingErrorsExtractor.class),
                    null
                ),
            () ->
                new ControllerAdviceForGenericErrors(
                    mock(AnnotationBasedValidationErrorsExtractor.class),
                    null,
                    mock(MediaType.class)
                ),
            () ->
                new ControllerAdviceForGenericErrors(
                    null,
                    mock(JacksonParsingErrorsExtractor.class),
                    mock(MediaType.class)
                )
        );
        new ControllerAdviceForGenericErrors(
            mock(AnnotationBasedValidationErrorsExtractor.class),
            mock(JacksonParsingErrorsExtractor.class),
            mock(MediaType.class)
        );
    }

    @Test
    public void shouldHandleMethodNotAllowed() {
        final val error = handler.handleMethodNotAllowed(mock(HttpRequestMethodNotSupportedException.class));
        assertThat(error.getErrorName()).isEqualTo("methodNotAllowed");
        assertThat(error.getHttpStatus()).isEqualTo(METHOD_NOT_ALLOWED.value());
    }

    @Test
    public void shouldHandleMediaTypeNotAcceptable() {
        final val error = handler.handleMediaTypeNotAcceptable(
            mock(HttpMediaTypeNotAcceptableException.class)
        );

        assertThat(error.getErrorName()).isEqualTo("mediaTypeNotAcceptable");
        assertThat(error.getHttpStatus()).isEqualTo(NOT_ACCEPTABLE.value());
        final val message = "This version of service supports only 'application/specific.hal+json'";
        assertThat(error.getMessage()).isEqualTo(message);
    }

    @Test
    public void shouldHandleContentTypeNotSupported() {
        final val error = handler.handleUnsupportedContentType(
            mock(HttpMediaTypeNotSupportedException.class)
        );
        assertThat(error.getErrorName()).isEqualTo("unsupportedContentType");
        assertThat(error.getHttpStatus()).isEqualTo(UNSUPPORTED_MEDIA_TYPE.value());
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
