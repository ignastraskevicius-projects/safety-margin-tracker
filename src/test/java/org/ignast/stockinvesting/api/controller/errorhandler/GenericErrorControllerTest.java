package org.ignast.stockinvesting.api.controller.errorhandler;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;

import static javax.servlet.RequestDispatcher.ERROR_STATUS_CODE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.mockito.MockitoUtils.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public class GenericErrorControllerTest {

    private final GenericErrorController controller = new GenericErrorController();

    @ParameterizedTest
    @ValueSource(ints = { 404, 503 })
    public void shouldPreserveHttpStatus(int statusCode) {
        val request = mock(HttpServletRequest.class,
                r -> when(r.getAttribute(ERROR_STATUS_CODE)).thenReturn(statusCode));

        val response = controller.handleError(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.valueOf(statusCode));
    }

    @Test
    public void shouldIndicateServerErrorIfRequestIsNull() {
        val response = controller.handleError(null);

        assertThat(response.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void shouldIndicateServerErrorIfStatusCodeRetrievedIsNull() {
        val request = Mockito.mock(HttpServletRequest.class);

        val response = controller.handleError(request);

        assertThat(response.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void shouldIndicateServerErrorIfStatusCodeRetrievedIsNotOfTypeInt() {
        val request = mock(HttpServletRequest.class,
                r -> when(r.getAttribute(ERROR_STATUS_CODE)).thenReturn("nonInteger"));

        val response = controller.handleError(request);

        assertThat(response.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void shouldNotExposeDetailsAboutClientErrors() {
        val request = mock(HttpServletRequest.class, r -> when(r.getAttribute(ERROR_STATUS_CODE)).thenReturn(400));

        val response = controller.handleError(request);

        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(response.getBody().getErrorName()).isEqualTo("resourceNotFound");
    }
}
