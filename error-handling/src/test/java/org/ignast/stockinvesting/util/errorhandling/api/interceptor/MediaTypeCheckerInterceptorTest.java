package org.ignast.stockinvesting.util.errorhandling.api.interceptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.ignast.stockinvesting.util.errorhandling.api.StandardErrorDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

final class MediaTypeCheckerInterceptorTest {

    private final ObjectMapper mapper = mock(ObjectMapper.class);

    private final HandlerInterceptor interceptor = new MediaTypeCheckerInterceptor(mapper);

    @ParameterizedTest
    @ValueSource(strings = { "Accept", "accept", "Accept" })
    public void httpGetMethodRequestsShouldComeWithAcceptHeader(final String accept) throws Exception {
        final val request = new MockHttpServletRequest("GET", "any");
        request.addHeader(accept, "any");
        final val response = new MockHttpServletResponse();

        assertThat(interceptor.preHandle(request, response, null)).isTrue();
        assertThat(response.getStatus()).isNotEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
    }

    @Test
    public void acceptHeaderShouldBeRequiredForGetMethodRequests() throws Exception {
        final val request = new MockHttpServletRequest("GET", "any");
        final val response = new MockHttpServletResponse();
        final val error = "serializedError";
        when(mapper.writeValueAsString(any(StandardErrorDTO.class))).thenReturn(error);

        assertThat(interceptor.preHandle(request, response, null)).isFalse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
        assertThat(response.getContentAsString()).isEqualTo(error);
    }

    @Test
    public void acceptHeaderShouldNotBeRequiredForPostMethodRequests() throws Exception {
        final val request = new MockHttpServletRequest("POST", "any");
        final val response = new MockHttpServletResponse();

        assertThat(interceptor.preHandle(request, response, null)).isTrue();
        assertThat(response.getStatus()).isNotEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
    }
}
