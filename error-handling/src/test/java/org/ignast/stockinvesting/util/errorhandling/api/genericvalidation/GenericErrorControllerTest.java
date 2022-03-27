package org.ignast.stockinvesting.util.errorhandling.api.genericvalidation;

import static javax.servlet.RequestDispatcher.ERROR_STATUS_CODE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import javax.servlet.http.HttpServletRequest;
import lombok.val;
import org.ignast.stockinvesting.utiltest.MockitoUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public final class GenericErrorControllerTest {

    private static final MediaType APP_MEDIA_TYPE = MediaType.parseMediaType("application/specific+json");

    private static final int NOT_FOUND = 404;

    private static final int SERVICE_UNAVAILABLE = 503;

    private static final int BAD_REQUEST = 400;

    private final GenericErrorController controller = new GenericErrorController(APP_MEDIA_TYPE);

    @Test
    public void shouldNotCreateWithNullArguments() {
        assertThatNullPointerException().isThrownBy(() -> new GenericErrorController(null));
        new GenericErrorController(MediaType.APPLICATION_JSON);
    }

    @ParameterizedTest
    @ValueSource(ints = { NOT_FOUND, SERVICE_UNAVAILABLE })
    public void shouldPreserveHttpStatus(final int statusCode) {
        final val request = MockitoUtils.mock(
            HttpServletRequest.class,
            r -> when(r.getAttribute(ERROR_STATUS_CODE)).thenReturn(statusCode)
        );

        final val response = controller.handleError(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.valueOf(statusCode));
        assertThat(response.getHeaders().getContentType()).isEqualTo(APP_MEDIA_TYPE);
        assertThat(response.getBody().getHttpStatus()).isEqualTo(statusCode);
    }

    @Test
    public void shouldIndicateServerErrorIfRequestIsNull() {
        final val response = controller.handleError(null);

        assertThat(response.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
        assertThat(response.getHeaders().getContentType()).isEqualTo(APP_MEDIA_TYPE);
        assertThat(response.getBody().getHttpStatus()).isEqualTo(INTERNAL_SERVER_ERROR.value());
    }

    @Test
    public void shouldIndicateServerErrorIfStatusCodeRetrievedIsNull() {
        final val request = mock(HttpServletRequest.class);

        final val response = controller.handleError(request);

        assertThat(response.getHeaders().getContentType()).isEqualTo(APP_MEDIA_TYPE);
        assertThat(response.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void shouldIndicateServerErrorIfStatusCodeRetrievedIsNotOfTypeInt() {
        final val request = MockitoUtils.mock(
            HttpServletRequest.class,
            r -> when(r.getAttribute(ERROR_STATUS_CODE)).thenReturn("nonInteger")
        );

        final val response = controller.handleError(request);

        assertThat(response.getHeaders().getContentType()).isEqualTo(APP_MEDIA_TYPE);
        assertThat(response.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void shouldNotExposeDetailsAboutClientErrors() {
        final val request = mock(HttpServletRequest.class);
        when(request.getAttribute(ERROR_STATUS_CODE)).thenReturn(BAD_REQUEST);

        final val response = controller.handleError(request);

        assertThat(response.getHeaders().getContentType()).isEqualTo(APP_MEDIA_TYPE);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getErrorName()).isNull();
    }
}
