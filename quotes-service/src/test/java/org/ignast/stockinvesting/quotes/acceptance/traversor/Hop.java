package org.ignast.stockinvesting.quotes.acceptance.traversor;

import lombok.NonNull;
import lombok.val;
import org.ignast.stockinvesting.quotes.acceptance.traversor.Hop.TraversableHop;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.quotes.acceptance.traversor.HateoasLink.anyLink;
import static org.ignast.stockinvesting.quotes.acceptance.traversor.HateoasLink.link;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.status;

public interface Hop {

    abstract class TraversableHop implements Hop {
        static final MediaType APP_MEDIA_TYPE = MediaType.parseMediaType("application/vnd.stockinvesting.quotes-v1.hal+json");

        abstract ResponseEntity<String> traverse(ResponseEntity<String> response);
    }

    class Factory {
        private RestTemplate restTemplate;

        private HrefExtractor hrefExtractor;

        Factory(@NonNull RestTemplate restTemplate, @NonNull HrefExtractor hrefExtractor) {
            this.hrefExtractor = hrefExtractor;
            this.restTemplate = restTemplate;
        }

        public TraversableHop put(String rel, String body) {
            return new PutHop(restTemplate, hrefExtractor, rel, body);
        }

        public TraversableHop get(String rel) {
            return new GetHop(restTemplate, hrefExtractor, rel);
        }

        private static class PutHop extends TraversableHop {
            private RestTemplate restTemplate;
            private HrefExtractor hrefExtractor;
            private String rel;
            private String body;

            private PutHop(RestTemplate restTemplate, HrefExtractor hrefExtractor, @NonNull String rel, @NonNull String body) {
                this.restTemplate = restTemplate;
                this.hrefExtractor = hrefExtractor;
                this.rel = rel;
                this.body = body;
            }

            @Override
            public ResponseEntity<String> traverse(@NonNull ResponseEntity<String> response) {
                return restTemplate.exchange(hrefExtractor.extractHref(response, rel), HttpMethod.PUT, contentTypeV1(body), String.class);
            }

            private HttpEntity<String> contentTypeV1(String content) {
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Type", APP_MEDIA_TYPE.toString());
                return new HttpEntity<>(content, headers);
            }
        }

        private static class GetHop extends TraversableHop {
            private RestTemplate restTemplate;
            private HrefExtractor hrefExtractor;
            private String rel;

            private GetHop(RestTemplate restTemplate, HrefExtractor hrefExtractor, @NonNull String rel) {
                this.restTemplate = restTemplate;
                this.hrefExtractor = hrefExtractor;
                this.rel = rel;
            }

            @Override
            ResponseEntity<String> traverse(@NonNull ResponseEntity<String> previousResponse) {
                return restTemplate.exchange(hrefExtractor.extractHref(previousResponse, rel), GET, acceptV1(), String.class);
            }

            private HttpEntity<String> acceptV1() {
                HttpHeaders headers = new HttpHeaders();
                headers.add("Accept", APP_MEDIA_TYPE.toString());
                return new HttpEntity<>(headers);
            }
        }
    }
}

class HopTest {

    private RestTemplate restTemplate = mock(RestTemplate.class);

    private HrefExtractor hrefExtractor = mock(HrefExtractor.class);

    private Hop.Factory hopFactory = new Hop.Factory(restTemplate, hrefExtractor);

    private ArgumentCaptor<HttpEntity<String>> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

    private static final MediaType QUOTES_V1 = MediaType.parseMediaType("application/vnd.stockinvesting.quotes-v1.hal+json");

    @Test
    public void factoryShouldNotBeCreatedWithNullRestTemplate() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new Hop.Factory(null, mock(HrefExtractor.class)));
    }

    @Test
    public void factoryShouldNotBeCreatedWithNullHrefExtractor() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new Hop.Factory(mock(RestTemplate.class), null));
    }

    @Test
    public void hopsTraversalShouldNotAcceptNullEntities() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> hopFactory.get("any").traverse(null));
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> hopFactory.put("any", "request").traverse(null));
    }

    @Test
    public void getHopShouldNotAcceptNullRels() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> hopFactory.get(null));
    }

    @Test
    public void shouldTraverseGetHop() {
        when(restTemplate.exchange(any(String.class), any(), any(), any(Class.class))).thenReturn(status(OK).body("hopResponse"));
        val responseWithLinkToCompany = status(OK).contentType(QUOTES_V1).body(link("company", "companyUri"));
        when(hrefExtractor.extractHref(responseWithLinkToCompany, "company")).thenReturn("companyUri");

        val companyResponse = hopFactory.get("company").traverse(responseWithLinkToCompany);

        verify(restTemplate).exchange(eq("companyUri"), eq(GET), entityCaptor.capture(), eq(String.class));
        assertThat(entityCaptor.getValue().getHeaders().get("Accept")).isEqualTo(asList(QUOTES_V1.toString()));
        assertThat(companyResponse.getBody()).isEqualTo("hopResponse");
    }

    @Test
    public void getHopTraversalShouldFailWhenHrefCannotBeExtracted() {
        val response = mock(ResponseEntity.class);
        when(hrefExtractor.extractHref(response, "any")).thenThrow(TestException.class);

        assertThatExceptionOfType(TestException.class)
                .isThrownBy(() -> hopFactory.get("any").traverse(response));
    }

    @Test
    public void putHopShouldNotAcceptNullRels() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> hopFactory.put(null, "request"));
    }

    @Test
    public void putHopShouldNotAcceptNullRequests() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> hopFactory.put("company", null));
    }

    @Test
    public void shouldTraversePutHop() {
        when(restTemplate.exchange(any(String.class), any(), any(), any(Class.class))).thenReturn(status(OK).body("hopResponse"));
        val responseWithLinkToCompany = status(OK).contentType(QUOTES_V1).body("{\"_links\":{\"client\":{\"href\":\"clientUri\"}}}");
        when(hrefExtractor.extractHref(responseWithLinkToCompany, "client")).thenReturn("clientUri");

        val companyResponse = hopFactory.put("client", "hopRequest").traverse(responseWithLinkToCompany);

        verify(restTemplate).exchange(eq("clientUri"), eq(PUT), entityCaptor.capture(), eq(String.class));
        assertThat(entityCaptor.getValue().getHeaders().get("Content-Type")).isEqualTo(asList(QUOTES_V1.toString()));
        assertThat(entityCaptor.getValue().getBody()).isEqualTo("hopRequest");
        assertThat(companyResponse.getBody()).isEqualTo("hopResponse");
    }

    @Test
    public void putHopTraversalShouldFailWhenHrefCannotBeExtracted() {
        val response = mock(ResponseEntity.class);
        when(hrefExtractor.extractHref(response, "any")).thenThrow(TestException.class);

        assertThatExceptionOfType(TestException.class)
                .isThrownBy(() -> hopFactory.put("any", "anyRequest").traverse(response));
    }

    private class TestException extends RuntimeException {

    }
}

