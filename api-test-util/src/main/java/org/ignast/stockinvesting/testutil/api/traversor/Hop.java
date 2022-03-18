package org.ignast.stockinvesting.testutil.api.traversor;

import lombok.NonNull;
import lombok.val;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpMethod.GET;

public interface Hop {

    public abstract static class TraversableHop implements Hop {
        abstract ResponseEntity<String> traverse(ResponseEntity<String> response);
    }

    public final static class Factory {
        private final RestTemplate restTemplate;

        private final MediaType appMediaType;

        private final HrefExtractor hrefExtractor;

        Factory(@NonNull final MediaType appMediaType, @NonNull final RestTemplate restTemplate, @NonNull final HrefExtractor hrefExtractor) {
            this.appMediaType = appMediaType;
            this.hrefExtractor = hrefExtractor;
            this.restTemplate = restTemplate;
        }

        public TraversableHop put(final String rel, final String body) {
            return new PutHop(appMediaType, restTemplate, hrefExtractor, rel, body);
        }

        public TraversableHop get(final String rel) {
            return new GetHop(appMediaType, restTemplate, hrefExtractor, rel);
        }

        private static final class PutHop extends TraversableHop {
            private final MediaType appMediaType;

            private final RestTemplate restTemplate;

            private final HrefExtractor hrefExtractor;

            private final String rel;

            private final String body;

            private PutHop(final MediaType appMediaType, final RestTemplate restTemplate, final HrefExtractor hrefExtractor, @NonNull final String rel, @NonNull final String body) {
                this.appMediaType = appMediaType;
                this.restTemplate = restTemplate;
                this.hrefExtractor = hrefExtractor;
                this.rel = rel;
                this.body = body;
            }

            @Override
            public ResponseEntity<String> traverse(@NonNull final ResponseEntity<String> response) {
                return restTemplate.exchange(hrefExtractor.extractHref(response, rel), HttpMethod.PUT, contentTypeV1(body), String.class);
            }

            private HttpEntity<String> contentTypeV1(final String content) {
                final val headers = new HttpHeaders();
                headers.add("Content-Type", appMediaType.toString());
                return new HttpEntity<>(content, headers);
            }
        }

        private static final class GetHop extends TraversableHop {
            private final MediaType appMediaType;

            private final RestTemplate restTemplate;

            private final HrefExtractor hrefExtractor;

            private final String rel;

            private GetHop(final MediaType appMediaType, final RestTemplate restTemplate, final HrefExtractor hrefExtractor, @NonNull final String rel) {
                this.appMediaType = appMediaType;
                this.restTemplate = restTemplate;
                this.hrefExtractor = hrefExtractor;
                this.rel = rel;
            }

            @Override
            ResponseEntity<String> traverse(@NonNull final ResponseEntity<String> previousResponse) {
                return restTemplate.exchange(hrefExtractor.extractHref(previousResponse, rel), GET, acceptV1(), String.class);
            }

            private HttpEntity<String> acceptV1() {
                final val headers = new HttpHeaders();
                headers.add("Accept", appMediaType.toString());
                return new HttpEntity<>(headers);
            }
        }
    }
}
