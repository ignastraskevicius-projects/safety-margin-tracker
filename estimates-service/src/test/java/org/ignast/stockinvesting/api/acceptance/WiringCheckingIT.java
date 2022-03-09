package org.ignast.stockinvesting.api.acceptance;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.api.acceptance.Uris.rootResourceOn;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WiringCheckingIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldWireInInterceptorEnsuringGETRequestsHaveAcceptHeaderInOrderToRequireExplicitApiVersionAndNeverBreakClientOnVersionBumps()
            throws Exception {
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(rootResourceOn(port))).build();

        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<String> rootResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(rootResponse.statusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
    }

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
    public void jacksonShouldNotSerializeNullErrorNamesWhereErrorCodeIsSelfExplanatory() {
        val response = restTemplate.getForEntity(String.format("http://localhost:%d/notexistent/path", port),
                String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("{}");
    }
}

