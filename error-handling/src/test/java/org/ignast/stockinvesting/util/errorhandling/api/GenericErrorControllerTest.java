package org.ignast.stockinvesting.util.errorhandling.api;

import lombok.val;
import org.ignast.stockinvesting.util.mockito.MockitoUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;

import static javax.servlet.RequestDispatcher.ERROR_STATUS_CODE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public final class GenericErrorControllerTest {

    private final GenericErrorController controller = new GenericErrorController();

    @ParameterizedTest
    @ValueSource(ints = { 404, 503 })
    public void shouldPreserveHttpStatus(final int statusCode) {
        final val request = MockitoUtils.mock(HttpServletRequest.class,
                r -> when(r.getAttribute(ERROR_STATUS_CODE)).thenReturn(statusCode));

        final val response = controller.handleError(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.valueOf(statusCode));
    }

    @Test
    public void shouldIndicateServerErrorIfRequestIsNull() {
        final val response = controller.handleError(null);

        assertThat(response.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void shouldIndicateServerErrorIfStatusCodeRetrievedIsNull() {
        final val request = mock(HttpServletRequest.class);

        final val response = controller.handleError(request);

        assertThat(response.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void shouldIndicateServerErrorIfStatusCodeRetrievedIsNotOfTypeInt() {
        final val request = MockitoUtils.mock(HttpServletRequest.class,
                r -> when(r.getAttribute(ERROR_STATUS_CODE)).thenReturn("nonInteger"));

        final val response = controller.handleError(request);

        assertThat(response.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void shouldNotExposeDetailsAboutClientErrors() {
        final val request = MockitoUtils.mock(HttpServletRequest.class, r -> when(r.getAttribute(ERROR_STATUS_CODE)).thenReturn(400));

        final val response = controller.handleError(request);

        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(response.getBody().getErrorName()).isNull();
    }
}
