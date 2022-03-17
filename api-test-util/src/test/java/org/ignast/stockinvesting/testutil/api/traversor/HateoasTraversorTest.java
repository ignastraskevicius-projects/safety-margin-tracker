package org.ignast.stockinvesting.testutil.api.traversor;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.testutil.api.traversor.RestTemplateBuilderStubs.stub;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(HateoasTraversor.Factory.class)
public class HateoasTraversorTest {

    @Autowired
    private HateoasTraversor.Factory traversors;

    @Autowired
    private MockRestServiceServer server;

    private static final MediaType APP_V1 = MediaType.parseMediaType("application/app.specific.media.type-v1.hal+json");

    @Test
    public void shouldNotBeCreatedWithNullRestTemplateBuilder() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new HateoasTraversor.Factory(null, APPLICATION_JSON));
    }

    @Test
    public void shouldNotBeCreatedWithNullAppMediaType() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new HateoasTraversor.Factory(stub(), null));
    }

    @Test
    public void shouldCreatedWithNonNullArguments() {
        new HateoasTraversor.Factory(stub(), APPLICATION_JSON);
    }

    @Test
    public void shouldNotCreateTraversorsForNullRootUris() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> traversors.startAt(null));
    }

    @Test
    public void traverseRootOnly() {
        server.expect(requestTo("http://root")).andExpect(method(GET)).andRespond(withSuccess("someResponse", APPLICATION_JSON));

        val response = traversors.startAt("http://root").perform();

        assertThat(response.getBody()).isEqualTo("someResponse");
    }

    @Test
    public void traverseGetHop() {
        server.expect(requestTo("http://root")).andExpect(method(GET)).andRespond(withSuccess(HateoasLink.link("company", "http://root/company"), APP_V1));
        server.expect(requestTo("http://root/company")).andExpect(method(GET)).andRespond(withSuccess(HateoasLink.link("company", "http://any"), APP_V1));

        val response = traversors.startAt("http://root").hop(f -> f.get("company")).perform();

        assertThat(response.getBody()).contains("http://any");
    }

    @Test
    public void traversePutHop() {
        server.expect(requestTo("http://root")).andExpect(method(GET)).andRespond(withSuccess(HateoasLink.link("company", "http://root/company"), APP_V1));
        server.expect(requestTo("http://root/company")).andExpect(method(PUT)).andRespond(withSuccess(HateoasLink.link("any", "http://any"), APP_V1));

        val response = traversors.startAt("http://root").hop(f -> f.put("company", "someRequest")).perform();

        assertThat(response.getBody()).contains("http://any");
    }

    @Test
    public void traverseMultipleHops() {
        server.expect(requestTo("http://root")).andExpect(method(GET)).andRespond(withSuccess(HateoasLink.link("company", "http://root/company"), APP_V1));
        server.expect(requestTo("http://root/company")).andExpect(method(GET)).andRespond(withSuccess(HateoasLink.link("president", "http://root/president"), APP_V1));
        server.expect(requestTo("http://root/president")).andExpect(method(GET)).andRespond(withSuccess(HateoasLink.link("any", "http://any"), APP_V1));

        val response = traversors
                .startAt("http://root")
                .hop(f -> f.get("company"))
                .hop(f -> f.get("president"))
                .perform();

        assertThat(response.getBody()).contains("http://any");
    }

    @Test
    public void shouldHandleClientErrors() {
        server.expect(requestTo("http://root")).andExpect(method(GET)).andRespond(withBadRequest().contentType(APPLICATION_JSON).body("someResponse"));

        val response = traversors.startAt("http://root").perform();

        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("someResponse");
    }


    @Test
    public void shouldHandleServerErrors() {
        server.expect(requestTo("http://root")).andExpect(method(GET)).andRespond(withServerError().contentType(APPLICATION_JSON).body("someResponse"));

        val response = traversors.startAt("http://root").perform();

        assertThat(response.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo("someResponse");
    }

    @TestConfiguration
    static class AppMediaTypeConfiguration {
        @Bean
        public MediaType appMediaType() {
            return APP_V1;
        }
    }
}

class RestTemplateBuilderStubs {
    static RestTemplateBuilder stub() {
        val builder = mock(RestTemplateBuilder.class);
        when(builder.errorHandler(any())).thenReturn(builder);
        when(builder.build()).thenReturn(mock(RestTemplate.class));
        return builder;
    }
}

class RestTemplateBuilderStubsTest {

    @Test
    public void shouldBuildRestTemplate() {
        assertThat(stub().build()).isInstanceOf(RestTemplate.class);
    }
}
