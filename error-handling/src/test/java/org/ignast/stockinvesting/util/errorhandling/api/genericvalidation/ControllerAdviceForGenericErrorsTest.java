package org.ignast.stockinvesting.util.errorhandling.api.genericvalidation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

public final class ControllerAdviceForGenericErrorsTest {

    private final MediaType appMediaType = MediaType.parseMediaType("application/specific.hal+json");

    private final ControllerAdviceForGenericErrors handler = new ControllerAdviceForGenericErrors(
        appMediaType
    );

    @Test
    public void shouldNotCreateWithNullArguments() {
        assertThatNullPointerException().isThrownBy(() -> new ControllerAdviceForGenericErrors(null));
        new ControllerAdviceForGenericErrors(mock(MediaType.class));
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
