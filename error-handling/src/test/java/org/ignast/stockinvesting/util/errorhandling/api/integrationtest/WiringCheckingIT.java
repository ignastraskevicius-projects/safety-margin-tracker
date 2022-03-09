package org.ignast.stockinvesting.util.errorhandling.api.integrationtest;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.util.errorhandling.api.integrationtest.WiringCheckingIT.TestController.*;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WiringCheckingIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldWireInCustomErrorSerialization() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(""))
                .uri(URI.create(rootResourceOn(port))).build();

        HttpResponse<String> response = HttpClient.newBuilder().build().send(request,
                HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(METHOD_NOT_ALLOWED.value());
        assertThat(response.body()).isEqualTo("{\"errorName\":\"methodNotAllowed\"}");
    }

    @Test
    public void shouldNotSerializeNullErrorNamesForSelfExplanatoryErrorCodes() {
        val response = restTemplate.getForEntity(rootResourceOn(port) + "/notexistent/path",
                String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("{}");
    }

    @Test
    public void shouldWireInInterceptorEnsuringGETRequestsHaveAcceptHeaderInOrderToRequireExplicitApiVersionAndNeverBreakClientOnVersionBumps()
            throws Exception {
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(rootResourceOn(port))).build();

        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<String> rootResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(rootResponse.statusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
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
        static String rootResourceOn(int port) {
            return "http://localhost:" + port + "/";
        }

        @GetMapping("/")
        public HttpEntity<String> updateTest() {
            return new HttpEntity<>("");
        }
    }
}
