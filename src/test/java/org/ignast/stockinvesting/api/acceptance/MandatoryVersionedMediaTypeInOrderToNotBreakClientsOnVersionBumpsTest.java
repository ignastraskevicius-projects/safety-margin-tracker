package org.ignast.stockinvesting.api.acceptance;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.api.acceptance.Uris.rootResourceOn;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MandatoryVersionedMediaTypeInOrderToNotBreakClientsOnVersionBumpsTest {

    @LocalServerPort
    private int port;

    @Test
    public void GETRequestsMustHaveAcceptHeader() throws Exception {
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(rootResourceOn(port))).build();

        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<String> rootResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(rootResponse.statusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
    }
}
