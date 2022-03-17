package org.ignast.stockinvesting.testutil.api.traversor;

import lombok.NonNull;
import lombok.val;
import org.hamcrest.MatcherAssert;
import org.ignast.stockinvesting.testutil.api.HateoasJsonMatchers;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

public class HrefExtractor {
    private final MediaType appMediaType;

    public HrefExtractor(@NonNull final MediaType appMediaType) {
        this.appMediaType = appMediaType;
    }

    @SuppressWarnings("checkstyle:designforextension")
    protected String extractHref(final ResponseEntity<String> previousResponse, final String rel) {
        return new Extractor(previousResponse, rel).extract();
    }

    private final class Extractor {
        private final ResponseEntity<String> previousResponse;

        private final String rel;

        private Extractor(final ResponseEntity<String> previousResponse, final String rel) {
            this.previousResponse = previousResponse;
            this.rel = rel;
        }

        private String extract() {
            expectSuccessfulResponse();
            expectAppResponse();
            return extractHref();
        }

        private String extractHref() {
            final val jsonBody = toJson(previousResponse.getBody());
            try {
                MatcherAssert.assertThat(previousResponse.getBody(), HateoasJsonMatchers.hasRel(rel).withHref());
                return jsonBody.getJSONObject("_links").getJSONObject(rel).getString("href");
            } catch (AssertionError | JSONException e) {
                throw new IllegalArgumentException(format("Hop to '%s' failed: previous response does not contain rel to '%s'", rel, rel));
            }
        }

        private JSONObject toJson(final String previousResponseBody) {
            try {
                return new JSONObject(previousResponseBody);
            } catch (JSONException e) {
                throw new IllegalArgumentException(format("Hop to '%s' failed: previous response is not a valid json", rel));
            }
        }

        private void expectSuccessfulResponse() {
            if (!asList(OK, CREATED).contains(previousResponse.getStatusCode())) {
                throw new IllegalArgumentException(format("Hop to '%s' failed: previous interaction has failed", rel));
            }
        }

        private void expectAppResponse() {
            if (isNull(previousResponse.getHeaders().getContentType())) {
                throw new IllegalArgumentException(format("Hop to '%s' failed: previous response has no content-type specified", rel));
            }
            if (!appMediaType.equals(previousResponse.getHeaders().getContentType())) {
                throw new IllegalArgumentException(format("Hop to '%s' failed: previous response has unsupported content-type specified", rel));
            }
        }
    }
}
