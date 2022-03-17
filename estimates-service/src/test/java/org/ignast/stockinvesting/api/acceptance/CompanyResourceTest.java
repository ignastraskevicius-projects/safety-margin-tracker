package org.ignast.stockinvesting.api.acceptance;

import lombok.val;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.api.acceptance.Uris.rootResourceOn;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
public final class CompanyResourceTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldDefineCompany() throws JSONException {
        final val rootResponse = restTemplate.exchange(rootResourceOn(port), HttpMethod.GET, acceptV1(),
                String.class);
        assertThat(rootResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        final val root = new JSONObject(rootResponse.getBody());

        final val companiesHref = root.getJSONObject("_links").getJSONObject("stocks:company").getString("href");
        final val companyDefinition = restTemplate.exchange(companiesHref, HttpMethod.PUT, contentTypeV1(
                "{\"id\":\"19c56404-73c6-4cd1-96a4-aae7962b6435\",\"name\":\"Amazon\",\"homeCountry\":\"US\",\"functionalCurrency\":\"USD\",\"listings\":[{\"marketIdentifier\":\"XNYS\",\"stockSymbol\":\"AMZN\"}]}"),
                String.class);
        assertThat(companyDefinition.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    private HttpEntity<String> acceptV1() {
        final val v1MediaType = MediaType.valueOf("application/vnd.stockinvesting.estimates-v1.hal+json");
        final val headers = new HttpHeaders();
        headers.add("Accept", v1MediaType.toString());
        return new HttpEntity<>(headers);
    }

    private HttpEntity<String> contentTypeV1(final String content) {
        final val v1MediaType = MediaType.valueOf("application/vnd.stockinvesting.estimates-v1.hal+json");
        final val headers = new HttpHeaders();
        headers.add("Content-Type", v1MediaType.toString());
        return new HttpEntity<>(content, headers);
    }


    @TestConfiguration
    static class AppMediaTypeConfig {
        @Bean
        public MediaType appMediaType() {
            return MediaType.parseMediaType("application/vnd.stockinvesting.estimates-v1.hal+json");
        }
    }
}