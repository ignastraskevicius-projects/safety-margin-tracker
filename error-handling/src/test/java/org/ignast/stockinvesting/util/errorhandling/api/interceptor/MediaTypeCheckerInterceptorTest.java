package org.ignast.stockinvesting.util.errorhandling.api.interceptor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.*;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import static org.assertj.core.api.Assertions.assertThat;

class MediaTypeCheckerInterceptorTest {
    private final HandlerInterceptor interceptor = new MediaTypeCheckerInterceptor();

    @ParameterizedTest
    @ValueSource(strings = { "Accept", "accept", "Accept" })
    public void GETMethodRequestsShouldComeWithAcceptHeader(String accept) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "any");
        request.addHeader(accept, "any");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThat(interceptor.preHandle(request, response, null)).isTrue();
        assertThat(response.getStatus()).isNotEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
    }

    @Test
    public void AcceptHeaderShouldBeRequiredForGETMethodRequests() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "any");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThat(interceptor.preHandle(request, response, null)).isFalse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
    }

    @Test
    public void AcceptHeaderShouldNotBeRequiredForPOSTMethodRequests() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "any");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThat(interceptor.preHandle(request, response, null)).isTrue();
        assertThat(response.getStatus()).isNotEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
    }
}