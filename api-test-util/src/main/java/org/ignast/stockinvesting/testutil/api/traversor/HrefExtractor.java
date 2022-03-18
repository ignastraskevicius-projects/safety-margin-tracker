package org.ignast.stockinvesting.testutil.api.traversor;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import lombok.NonNull;
import lombok.val;
import org.hamcrest.MatcherAssert;
import org.ignast.stockinvesting.testutil.api.HateoasJsonMatchers;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

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
                MatcherAssert.assertThat(
                    previousResponse.getBody(),
                    HateoasJsonMatchers.hasRel(rel).withHref()
                );
                return jsonBody.getJSONObject("_links").getJSONObject(rel).getString("href");
            } catch (AssertionError | JSONException e) {
                final val message = format("previous response does not contain rel to '%s'", rel);
                throw new IllegalArgumentException(formatError(message));
            }
        }

        private JSONObject toJson(final String previousResponseBody) {
            try {
                return new JSONObject(previousResponseBody);
            } catch (JSONException e) {
                throw new IllegalArgumentException(formatError("previous response is not a valid json"));
            }
        }

        private void expectSuccessfulResponse() {
            if (!asList(OK, CREATED).contains(previousResponse.getStatusCode())) {
                throw new IllegalArgumentException(formatError("previous interaction has failed"));
            }
        }

        private void expectAppResponse() {
            if (isNull(previousResponse.getHeaders().getContentType())) {
                final val message = "previous response has no content-type specified";
                throw new IllegalArgumentException(formatError(message));
            }
            if (!appMediaType.equals(previousResponse.getHeaders().getContentType())) {
                final val message = "previous response has unsupported content-type specified";
                throw new IllegalArgumentException(formatError(message));
            }
        }

        private String formatError(final String message) {
            return format("Hop to '%s' failed: ", rel) + message;
        }
    }
}
