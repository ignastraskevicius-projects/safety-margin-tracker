package org.ignast.stockinvesting.quotes.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.quotes.acceptance.Uris.rootResourceOn;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import lombok.val;
import org.ignast.stockinvesting.testutil.api.traversor.HateoasTraversor;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public final class CompanyResourceTest extends AcceptanceTestEnvironment {

    @Autowired
    private HateoasTraversor.Factory quotesTraversors;

    @LocalServerPort
    private int port;

    @Test
    public void shouldCreateCompany() throws JSONException {
        final val company = quotesTraversors
            .startAt(rootResourceOn(port))
            .hop(f -> f.put("quotes:companies", getMicrosoft()))
            .perform();
        assertThat(company.getStatusCode()).isEqualTo(CREATED);
        assertEquals("should create company", getMicrosoft(), company.getBody(), false);
    }

    @Test
    public void shouldNotCreateDuplicateCompanies() throws JSONException {
        quotesTraversors
            .startAt(rootResourceOn(port))
            .hop(f -> f.put("quotes:companies", getMicrosoft()))
            .perform();
        final val duplicacte = quotesTraversors
            .startAt(rootResourceOn(port))
            .hop(f -> f.put("quotes:companies", getMicrosoft()))
            .perform();
        assertThat(duplicacte.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertEquals(
            "fails to create duplicate",
            """
                {"errorName":"companyAlreadyExists"}""",
            duplicacte.getBody(),
            true
        );
    }

    @Test
    public void shouldNotCreateCompaniesForUnsupportedSymbols() throws JSONException {
        final val unsupportedCompany =
            """
                            {
                                "id":5,
                                "name":"Microsoft",
                                "listings":[{
                                    "marketIdentifier":"XNAS",
                                    "stockSymbol":"AAAA"
                                }]
                            }""";
        final val company = quotesTraversors
            .startAt(rootResourceOn(port))
            .hop(f -> f.put("quotes:companies", unsupportedCompany))
            .perform();
        assertThat(company.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertEquals(
            "fails to create company",
            """
                        {"errorName":"stockSymbolNotSupportedInThisMarket"}""",
            company.getBody(),
            true
        );
    }

    @Test
    public void shouldRetrieveCreatedCompany() throws JSONException {
        final val company = quotesTraversors
            .startAt(rootResourceOn(port))
            .hop(f -> f.put("quotes:companies", getMicrosoft()))
            .hop(f -> f.get("self"))
            .perform();
        assertThat(company.getStatusCode()).isEqualTo(OK);
        assertEquals("should create company", getMicrosoft(), company.getBody(), false);
    }

    @Test
    public void shouldRetrieveQuotedPrice() throws JSONException {
        final val quotedPrice = quotesTraversors
            .startAt(rootResourceOn(port))
            .hop(f -> f.put("quotes:companies", getMicrosoft()))
            .hop(f -> f.get("quotes:quotedPrice"))
            .perform();
        assertThat(quotedPrice.getStatusCode()).isEqualTo(OK);
        assertEquals(
            "should retrieve price",
            """
                            {
                                "amount":"128.5",
                                "currency":"USD"
                            }""",
            quotedPrice.getBody(),
            false
        );
    }

    private String getMicrosoft() {
        return """
                {
                    "id":5,
                    "name":"Microsoft",
                    "listings":[{
                        "marketIdentifier":"XNAS",
                        "stockSymbol":"MSFT"
                    }]
                }""";
    }

    @TestConfiguration
    static class AppMediaTypeConfig {

        @Bean
        public MediaType appMediaType() {
            return MediaType.parseMediaType("application/vnd.stockinvesting.quotes-v1.hal+json");
        }
    }
}
