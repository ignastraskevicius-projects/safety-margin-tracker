package org.ignast.stockinvesting.testutil.api.traversor;

import lombok.NonNull;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(QuotesTraversor.Factory.class)
class QuotesTraversorTest {

    @Autowired
    private QuotesTraversor.Factory traversors;

    @Autowired
    private MockRestServiceServer server;

    private static final MediaType QUOTES_V1 = MediaType.parseMediaType("application/vnd.stockinvesting.quotes-v1.hal+json");

    @Test
    public void shouldNotBeCreatedWithNullRestTemplateBuilder() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new QuotesTraversor.Factory(null));
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
        server.expect(requestTo("http://root")).andExpect(method(GET)).andRespond(MockRestResponseCreators.withSuccess(HateoasLink.link("company", "http://root/company"), QUOTES_V1));
        server.expect(requestTo("http://root/company")).andExpect(method(GET)).andRespond(MockRestResponseCreators.withSuccess(HateoasLink.link("company", "http://any"), QUOTES_V1));

        val response = traversors.startAt("http://root").hop(f -> f.get("company")).perform();

        assertThat(response.getBody()).contains("http://any");
    }

    @Test
    public void traversePutHop() {
        server.expect(requestTo("http://root")).andExpect(method(GET)).andRespond(MockRestResponseCreators.withSuccess(HateoasLink.link("company", "http://root/company"), QUOTES_V1));
        server.expect(requestTo("http://root/company")).andExpect(method(PUT)).andRespond(MockRestResponseCreators.withSuccess(HateoasLink.link("any", "http://any"), QUOTES_V1));

        val response = traversors.startAt("http://root").hop(f -> f.put("company", "someRequest")).perform();

        assertThat(response.getBody()).contains("http://any");
    }

    @Test
    public void traverseMultipleHops() {
        server.expect(requestTo("http://root")).andExpect(method(GET)).andRespond(MockRestResponseCreators.withSuccess(HateoasLink.link("company", "http://root/company"), QUOTES_V1));
        server.expect(requestTo("http://root/company")).andExpect(method(GET)).andRespond(MockRestResponseCreators.withSuccess(HateoasLink.link("president", "http://root/president"), QUOTES_V1));
        server.expect(requestTo("http://root/president")).andExpect(method(GET)).andRespond(MockRestResponseCreators.withSuccess(HateoasLink.link("any", "http://any"), QUOTES_V1));

        val response = traversors
                .startAt("http://root")
                .hop(f -> f.get("company"))
                .hop(f -> f.get("president"))
                .perform();

        assertThat(response.getBody()).contains("http://any");
    }
}



