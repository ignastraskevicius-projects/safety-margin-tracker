package org.ignast.stockinvesting.quotes.util.test.api.traversor;

import lombok.NonNull;
import lombok.val;
import org.ignast.stockinvesting.quotes.util.test.api.traversor.Hop.TraversableHop;
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
import static org.ignast.stockinvesting.quotes.util.test.api.traversor.HateoasLink.link;
import static org.ignast.stockinvesting.quotes.util.test.api.traversor.Hop.TraversableHop.APP_MEDIA_TYPE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class QuotesTraversor {
    private final Hop.Factory hopFactory;
    private final List<TraversableHop> hops;
    private final String rootUri;

    private QuotesTraversor(Hop.Factory hopFactory, String rootUri, List<TraversableHop> hops) {
        this.hopFactory = hopFactory;
        this.rootUri = rootUri;
        this.hops = hops;
    }

    public QuotesTraversor hop(Function<Hop.Factory, TraversableHop> constructHop) {
        TraversableHop hop = constructHop.apply(hopFactory);
        return new QuotesTraversor(hopFactory, rootUri, concat(hops.stream(), of(hop)).collect(toUnmodifiableList()));
    }

    public ResponseEntity<String> perform() {
        val fakeLinkToRoot = ResponseEntity.status(HttpStatus.OK).contentType(APP_MEDIA_TYPE).body(link("root", rootUri));
        val rootHop = hopFactory.get("root");
        return concat(of(rootHop), hops.stream()).reduce(fakeLinkToRoot, (r, h) -> h.traverse(r), combinerUnsupported());
    }

    private BinaryOperator<ResponseEntity<String>> combinerUnsupported() {
        return (a, b) -> {
            throw new IllegalArgumentException("combinations are not supported");
        };
    }

    @Service
    public static class Factory {
        private Hop.Factory hopFactory;

        public Factory(RestTemplateBuilder builder) {
            hopFactory = new Hop.Factory(builder.build(), new HrefExtractor());
        }

        public QuotesTraversor startAt(@NonNull String rootUri) {
            return new QuotesTraversor(hopFactory, rootUri, emptyList());
        }
    }
}

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
        server.expect(requestTo("http://root")).andExpect(method(GET)).andRespond(withSuccess(link("company", "http://root/company"), QUOTES_V1));
        server.expect(requestTo("http://root/company")).andExpect(method(GET)).andRespond(withSuccess(link("company", "http://any"), QUOTES_V1));

        val response = traversors.startAt("http://root").hop(f -> f.get("company")).perform();

        assertThat(response.getBody()).contains("http://any");
    }

    @Test
    public void traversePutHop() {
        server.expect(requestTo("http://root")).andExpect(method(GET)).andRespond(withSuccess(link("company", "http://root/company"), QUOTES_V1));
        server.expect(requestTo("http://root/company")).andExpect(method(PUT)).andRespond(withSuccess(link("any", "http://any"), QUOTES_V1));

        val response = traversors.startAt("http://root").hop(f -> f.put("company", "someRequest")).perform();

        assertThat(response.getBody()).contains("http://any");
    }

    @Test
    public void traverseMultipleHops() {
        server.expect(requestTo("http://root")).andExpect(method(GET)).andRespond(withSuccess(link("company", "http://root/company"), QUOTES_V1));
        server.expect(requestTo("http://root/company")).andExpect(method(GET)).andRespond(withSuccess(link("president", "http://root/president"), QUOTES_V1));
        server.expect(requestTo("http://root/president")).andExpect(method(GET)).andRespond(withSuccess(link("any", "http://any"), QUOTES_V1));

        val response = traversors
                .startAt("http://root")
                .hop(f -> f.get("company"))
                .hop(f -> f.get("president"))
                .perform();

        assertThat(response.getBody()).contains("http://any");
    }
}



