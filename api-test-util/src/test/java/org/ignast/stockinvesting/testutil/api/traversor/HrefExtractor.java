package org.ignast.stockinvesting.testutil.api.traversor;

import lombok.val;
import org.hamcrest.MatcherAssert;
import org.ignast.stockinvesting.testutil.api.HateoasJsonMatchers;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.status;

class HrefExtractorTest {

    private HrefExtractor extractor = new HrefExtractor();

    private static final MediaType QUOTES_V1 = MediaType.parseMediaType("application/vnd.stockinvesting.quotes-v1.hal+json");

    @Test
    public void shouldExtractHref() {
        val response = ResponseEntity.status(OK).contentType(QUOTES_V1).body(HateoasLink.link("company", "companyUri"));

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
        val response = ResponseEntity.status(OK).body(HateoasLink.anyLink());

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> extractor.extractHref(response, "any"))
                .withMessage("Hop to 'any' failed: previous response has no content-type specified");
    }

    @ParameterizedTest
    @ValueSource(strings = {"text/xml", "application/json", "application/hal+jsosn", "application/vnd.stockinvesting.quotes.hal+json"})
    public void shouldNotExtractHrefFromResponsesWithoutVersionedAppContentTypeSet(String type) {
        val mediaType = MediaType.parseMediaType(type);
        val response = ResponseEntity.status(OK).contentType(mediaType).body(HateoasLink.anyLink());

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> extractor.extractHref(response, "any"))
                .withMessage("Hop to 'any' failed: previous response has unsupported content-type specified");
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 500})
    public void shouldNotExtractHrefFromResponsesWithNon2xxStatusCodes(int status) {
        val response = ResponseEntity.status(HttpStatus.valueOf(status)).body(HateoasLink.anyLink());

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> extractor.extractHref(response, "any"))
                .withMessage("Hop to 'any' failed: previous interaction has failed");
    }

    @ParameterizedTest
    @ValueSource(ints = {200, 201})
    public void shouldExtractHrefForResponsesWith2xxStatusCodes(int status) {
        val response = ResponseEntity.status(HttpStatus.valueOf(status))
                .contentType(QUOTES_V1).body(HateoasLink.link("company", "companyUri"));

        assertThat(extractor.extractHref(response, "company")).isEqualTo("companyUri");
    }
}