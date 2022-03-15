package org.ignast.stockinvesting.testutil.api.traversor;

import lombok.NonNull;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpMethod.GET;

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
