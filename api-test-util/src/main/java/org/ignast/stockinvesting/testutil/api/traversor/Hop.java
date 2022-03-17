package org.ignast.stockinvesting.testutil.api.traversor;

import lombok.NonNull;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpMethod.GET;

public interface Hop {

    abstract class TraversableHop implements Hop {
        abstract ResponseEntity<String> traverse(ResponseEntity<String> response);
    }

    class Factory {
        private RestTemplate restTemplate;

        private MediaType appMediaType;

        private HrefExtractor hrefExtractor;

        Factory(@NonNull MediaType appMediaType, @NonNull RestTemplate restTemplate, @NonNull HrefExtractor hrefExtractor) {
            this.appMediaType = appMediaType;
            this.hrefExtractor = hrefExtractor;
            this.restTemplate = restTemplate;
        }

        public TraversableHop put(String rel, String body) {
            return new PutHop(appMediaType, restTemplate, hrefExtractor, rel, body);
        }

        public TraversableHop get(String rel) {
            return new GetHop(appMediaType, restTemplate, hrefExtractor, rel);
        }

        private static class PutHop extends TraversableHop {
            private final MediaType appMediaType;
            private final RestTemplate restTemplate;
            private final HrefExtractor hrefExtractor;
            private final String rel;
            private final String body;

            private PutHop(MediaType appMediaType, RestTemplate restTemplate, HrefExtractor hrefExtractor, @NonNull String rel, @NonNull String body) {
                this.appMediaType = appMediaType;
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
                headers.add("Content-Type", appMediaType.toString());
                return new HttpEntity<>(content, headers);
            }
        }

        private static class GetHop extends TraversableHop {
            private final MediaType appMediaType;
            private final RestTemplate restTemplate;
            private final HrefExtractor hrefExtractor;
            private final String rel;

            private GetHop(MediaType appMediaType, RestTemplate restTemplate, HrefExtractor hrefExtractor, @NonNull String rel) {
                this.appMediaType = appMediaType;
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
                headers.add("Accept", appMediaType.toString());
                return new HttpEntity<>(headers);
            }
        }
    }
}
