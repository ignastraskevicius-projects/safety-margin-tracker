package org.ignast.stockinvesting.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.api.Uris.rootResourceOn;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class RootResourceTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldRejectUnversionedRequests() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/hal+json");
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> rootResponse = restTemplate.exchange(rootResourceOn(port), HttpMethod.GET, request, String.class);
        assertThat(rootResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
    }

    @Test
    public void shouldRejectNonHalRequests() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> rootResponse = restTemplate.exchange(rootResourceOn(port), HttpMethod.GET, request, String.class);
        assertThat(rootResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
    }

    @Test
    public void shouldRejectWithNoAcceptHeader() throws Exception {
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(rootResourceOn(port))).build();

        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<String> rootResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(rootResponse.statusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
    }

    private HttpEntity<String> v1() {
        MediaType v1MediaType = MediaType.valueOf("application/vnd.stockinvesting.estimates-v1.hal+json");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", v1MediaType.toString());
        return new HttpEntity<>(headers);
    }
}
