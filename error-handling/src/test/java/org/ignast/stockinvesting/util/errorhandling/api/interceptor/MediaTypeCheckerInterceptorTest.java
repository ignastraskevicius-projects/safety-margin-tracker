package org.ignast.stockinvesting.util.errorhandling.api.interceptor;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

final class MediaTypeCheckerInterceptorTest {

    private final HandlerInterceptor interceptor = new MediaTypeCheckerInterceptor();

    @ParameterizedTest
    @ValueSource(strings = { "Accept", "accept", "Accept" })
    public void GETMethodRequestsShouldComeWithAcceptHeader(final String accept) throws Exception {
        final val request = new MockHttpServletRequest("GET", "any");
        request.addHeader(accept, "any");
        final val response = new MockHttpServletResponse();

        assertThat(interceptor.preHandle(request, response, null)).isTrue();
        assertThat(response.getStatus()).isNotEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
    }

    @Test
    public void AcceptHeaderShouldBeRequiredForGetMethodRequests() throws Exception {
        final val request = new MockHttpServletRequest("GET", "any");
        final val response = new MockHttpServletResponse();

        assertThat(interceptor.preHandle(request, response, null)).isFalse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
    }

    @Test
    public void AcceptHeaderShouldNotBeRequiredForPostMethodRequests() throws Exception {
        final val request = new MockHttpServletRequest("POST", "any");
        final val response = new MockHttpServletResponse();

        assertThat(interceptor.preHandle(request, response, null)).isTrue();
        assertThat(response.getStatus()).isNotEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
    }
}
