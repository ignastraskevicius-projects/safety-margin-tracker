package org.ignast.stockinvesting.api.acceptance;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.api.acceptance.Uris.rootResourceOn;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
public class CompanyResourceTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Container
    private static final GenericContainer alphavantange = new GenericContainer(DockerImageName.parse("estimates/alphavantage-simulator:1.0-SNAPSHOT")).withExposedPorts(8080);

    @DynamicPropertySource
    private static void registerWiremockPort(DynamicPropertyRegistry registry) {
        registry.add("alphavantage.url", () -> format("http://localhost:%d", alphavantange.getMappedPort(8080)));
    }

    @Test
    public void shouldDefineCompany() throws JSONException {
        ResponseEntity<String> rootResponse = restTemplate.exchange(rootResourceOn(port), HttpMethod.GET, acceptV1(),
                String.class);
        assertThat(rootResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        JSONObject root = new JSONObject(rootResponse.getBody());

        String companiesHref = root.getJSONObject("_links").getJSONObject("stocks:company").getString("href");
        ResponseEntity<String> companyDefinition = restTemplate.exchange(companiesHref, HttpMethod.PUT, contentTypeV1(
                "{\"id\":\"19c56404-73c6-4cd1-96a4-aae7962b6435\",\"name\":\"Amazon\",\"homeCountry\":\"US\",\"functionalCurrency\":\"USD\",\"listings\":[{\"marketIdentifier\":\"XNYS\",\"stockSymbol\":\"AMZN\"}]}"),
                String.class);
        assertThat(companyDefinition.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    private HttpEntity<String> acceptV1() {
        MediaType v1MediaType = MediaType.valueOf("application/vnd.stockinvesting.estimates-v1.hal+json");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", v1MediaType.toString());
        return new HttpEntity<>(headers);
    }

    private HttpEntity<String> contentTypeV1(String content) {
        MediaType v1MediaType = MediaType.valueOf("application/vnd.stockinvesting.estimates-v1.hal+json");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", v1MediaType.toString());
        return new HttpEntity<>(content, headers);
    }
}