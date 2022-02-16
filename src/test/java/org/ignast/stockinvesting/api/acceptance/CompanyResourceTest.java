package org.ignast.stockinvesting.api.acceptance;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.api.acceptance.Uris.rootResourceOn;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CompanyResourceTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldDefineCompany() throws JSONException {
        ResponseEntity<String> rootResponse = restTemplate.exchange(rootResourceOn(port), HttpMethod.GET, acceptV1(),
                String.class);
        assertThat(rootResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        JSONObject root = new JSONObject(rootResponse.getBody());

        String companiesHref = root.getJSONObject("_links").getJSONObject("stocks:company").getString("href");
        ResponseEntity<String> companyDefinition = restTemplate.exchange(companiesHref, HttpMethod.POST,
                contentTypeV1("{\"name\":\"Amazon\",\"address\":{\"country\":\"Romania\"},\"listings\":[3]}"),
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