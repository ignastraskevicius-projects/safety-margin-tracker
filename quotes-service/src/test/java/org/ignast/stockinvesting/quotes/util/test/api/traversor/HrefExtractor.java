package org.ignast.stockinvesting.quotes.util.test.api.traversor;

import lombok.val;
import org.hamcrest.MatcherAssert;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.quotes.util.test.api.HateoasJsonMatchers.hasRel;
import static org.ignast.stockinvesting.quotes.util.test.api.traversor.HateoasLink.anyLink;
import static org.ignast.stockinvesting.quotes.util.test.api.traversor.HateoasLink.link;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.status;

public class HrefExtractor {
    protected String extractHref(ResponseEntity<String> previousResponse, String rel) {
        return new Extractor(previousResponse, rel).extract();
    }

    private class Extractor {
        private ResponseEntity<String> previousResponse;
        private String rel;

        private Extractor(ResponseEntity<String> previousResponse, String rel) {
            this.previousResponse = previousResponse;
            this.rel = rel;
        }

        private String extract() {
            expectSuccessfulResponse();
            expectAppResponse();
            return extractHref();
        }

        private String extractHref() {
            val jsonBody = toJson(previousResponse.getBody());
            try {
                MatcherAssert.assertThat(previousResponse.getBody(), hasRel(rel).withHref());
                return jsonBody.getJSONObject("_links").getJSONObject(rel).getString("href");
            } catch (AssertionError e) {
                throw new IllegalArgumentException(format("Hop to '%s' failed: previous response does not contain rel to '%s'", rel, rel));
            } catch (JSONException e) {
                throw new IllegalArgumentException(format("Hop to '%s' failed: previous response does not contain rel to '%s'", rel, rel));
            }
        }

        private JSONObject toJson(String previousResponse) {
            try {
                return new JSONObject(previousResponse);
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
            if (!Hop.TraversableHop.APP_MEDIA_TYPE.equals(previousResponse.getHeaders().getContentType())) {
                throw new IllegalArgumentException(format("Hop to '%s' failed: previous response has unsupported content-type specified", rel));
            }
        }
    }
}

class HrefExtractorTest {

    private HrefExtractor extractor = new HrefExtractor();

    private static final MediaType QUOTES_V1 = MediaType.parseMediaType("application/vnd.stockinvesting.quotes-v1.hal+json");

    @Test
    public void shouldExtractHref() {
        val response = ResponseEntity.status(OK).contentType(QUOTES_V1).body(link("company", "companyUri"));

        assertThat(extractor.extractHref(response, "company")).isEqualTo("companyUri");
    }

    @Test
    public void shouldFailToExtractFromInvalidJsonResponses() {
        val response = ResponseEntity.status(OK).contentType(QUOTES_V1).body("not-a-json");

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> extractor.extractHref(response, "company"))
                .withMessage("Hop to 'company' failed: previous response is not a valid json");
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"_lilnks\":{}}", "{\"_lilnks\":{\"company\":{}}}", "{\"_lilnks\":{\"client\":{\"href\":\"companyUri\"}}}"})
    public void shouldFailToExtractNonexistentRel(String notContainingCompanyRel) {
        val response = ResponseEntity.status(OK).contentType(QUOTES_V1).body(notContainingCompanyRel);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> extractor.extractHref(response, "company"))
                .withMessage("Hop to 'company' failed: previous response does not contain rel to 'company'");
    }

    @Test
    public void shouldFailToExtractHrefFromResponseWithoutContentType() {
        val response = ResponseEntity.status(OK).body(anyLink());

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> extractor.extractHref(response, "any"))
                .withMessage("Hop to 'any' failed: previous response has no content-type specified");
    }

    @ParameterizedTest
    @ValueSource(strings = {"text/xml", "application/json", "application/hal+jsosn", "application/vnd.stockinvesting.quotes.hal+json"})
    public void shouldNotExtractHrefFromResponsesWithoutVersionedAppContentTypeSet(String type) {
        val mediaType = MediaType.parseMediaType(type);
        val response = ResponseEntity.status(OK).contentType(mediaType).body(anyLink());

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> extractor.extractHref(response, "any"))
                .withMessage("Hop to 'any' failed: previous response has unsupported content-type specified");
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 500})
    public void shouldNotExtractHrefFromResponsesWithNon2xxStatusCodes(int status) {
        val response = ResponseEntity.status(HttpStatus.valueOf(status)).body(anyLink());

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> extractor.extractHref(response, "any"))
                .withMessage("Hop to 'any' failed: previous interaction has failed");
    }

    @ParameterizedTest
    @ValueSource(ints = {200, 201})
    public void shouldExtractHrefForResponsesWith2xxStatusCodes(int status) {
        val response = ResponseEntity.status(HttpStatus.valueOf(status))
                .contentType(QUOTES_V1).body(link("company", "companyUri"));

        assertThat(extractor.extractHref(response, "company")).isEqualTo("companyUri");
    }
}