package org.ignast.stockinvesting.util.errorhandling.api.integrationtest.wiring.annotationscan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forIntegerRequiredAt;
import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forMissingFieldAt;
import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forStringRequiredAt;
import static org.ignast.stockinvesting.util.errorhandling.api.integrationtest.wiring.annotationscan.AnnotationScanWiringIT.TestController.url;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.springframework.http.HttpEntity.EMPTY;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.val;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
final class AnnotationScanWiringIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldWireInCustomErrorSerialization() {
        final val response = restTemplate.exchange(url(port), PUT, EMPTY, String.class);

        assertThat(response.getStatusCode()).isEqualTo(METHOD_NOT_ALLOWED);
        assertThat(response.getBody()).isEqualTo("{\"errorName\":\"methodNotAllowed\"}");
    }

    @Test
    public void shouldWireInCustomParsingErrorHandling() {
        final val response = restTemplate.exchange(
            url(port) + "/string",
            PUT,
            asJson("\"not-a-json-object\""),
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(response.getBody()).contains("valueMustBeObject");
    }

    @Test
    public void shouldWireInStrictStringParsing() throws JSONException {
        final val response = restTemplate.exchange(
            url(port) + "/string",
            PUT,
            asJson("{\"stringField\":2}"),
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertEquals(
            forStringRequiredAt("$.stringField"),
            response.getBody(),
            JSONCompareMode.NON_EXTENSIBLE
        );
    }

    @Test
    public void shouldWireInStrictIntegerParsing() throws JSONException {
        final val response = restTemplate.exchange(
            url(port) + "/int",
            PUT,
            asJson("{\"intField\":\"2\"}"),
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertEquals(forIntegerRequiredAt("$.intField"), response.getBody(), JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void shouldWireInDtoJavaxValidation() throws JSONException {
        final val response = restTemplate.exchange(url(port) + "/int", PUT, asJson("{}"), String.class);

        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertEquals(forMissingFieldAt("$.intField"), response.getBody(), JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void shouldNotSerializeNullErrorNamesForSelfExplanatoryErrorCodes() {
        final val response = restTemplate.getForEntity(url(port) + "/notexistent/path", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("{}");
    }

    @Test
    @SuppressWarnings("checkstyle:linelength")
    public void shouldWireInInterceptorEnsuringGETRequestsHaveAcceptHeaderInOrderToRequireExplicitApiVersionAndNeverBreakClientOnVersionBumps()
        throws Exception {
        final val response = getWithoutAcceptHeader(url(port));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
    }

    private HttpResponse<String> getWithoutAcceptHeader(final String url)
        throws IOException, InterruptedException {
        final val request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();
        final val client = HttpClient.newBuilder().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpEntity<String> asJson(final String json) {
        final val h = new HttpHeaders();
        h.setContentType(APPLICATION_JSON);

        return new HttpEntity<>(json, h);
    }

    @TestConfiguration
    static class TestControllerConfig {

        @Bean
        public TestController testController() {
            return new TestController();
        }
    }

    @RestController
    static class TestController {

        static String url(final int port) {
            return "http://localhost:" + port;
        }

        @GetMapping("/")
        public HttpEntity<String> updateTest() {
            return new HttpEntity<>("");
        }

        @PutMapping("/string")
        public HttpEntity<String> updateTest(@Valid @RequestBody final TestStringDTO b) {
            return new HttpEntity<>("");
        }

        @PutMapping("/int")
        public HttpEntity<String> updateTest(@Valid @RequestBody final TestIntDTO b) {
            return new HttpEntity<>("");
        }
    }

    @Getter
    static class TestStringDTO {

        @NotNull
        private String stringField;
    }

    @Getter
    static class TestIntDTO {

        @NotNull
        private Integer intField;
    }
}
