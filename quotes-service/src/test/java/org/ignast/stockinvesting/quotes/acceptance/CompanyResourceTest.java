package org.ignast.stockinvesting.quotes.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.quotes.acceptance.Uris.rootResourceOn;
import static org.ignast.stockinvesting.testutil.api.JsonAssert.assertThatJson;
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
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = { "server.port=8080", "documentation.url=http://localhost:8080" })
public final class CompanyResourceTest extends AcceptanceTestEnvironment {

    @Autowired
    private HateoasTraversor.Factory quotesTraversors;

    @LocalServerPort
    private int port;

    @Test
    public void shouldCreateCompany() throws JSONException {
        final val company = quotesTraversors
            .startAt(rootResourceOn(port))
            .hop(f -> f.put("quotes:createCompany", getMicrosoft()))
            .perform();
        assertThat(company.getStatusCode()).isEqualTo(CREATED);
        assertEquals("should create company", getMicrosoft(), company.getBody(), false);
    }

    @Test
    public void docsShouldBeAccessible() throws JSONException {
        final val docs = quotesTraversors
            .startAt(rootResourceOn(port))
            .hop(f -> f.getDocsFor("createCompany"))
            .perform();
        assertThat(docs.getStatusCode()).isEqualTo(OK);
        assertThatJson(docs.getBody())
            .isEqualTo(
                """
                {
                    "mediaType":"application/vnd.stockinvesting.quotes-v1.hal+json",
                    "methods":[{"method":"PUT"}]
                }"""
            );
    }

    @Test
    public void shouldNotCreateDuplicateCompanies() throws JSONException {
        quotesTraversors
            .startAt(rootResourceOn(port))
            .hop(f -> f.put("quotes:createCompany", getMicrosoft()))
            .perform();
        final val duplicacte = quotesTraversors
            .startAt(rootResourceOn(port))
            .hop(f -> f.put("quotes:createCompany", getMicrosoft()))
            .perform();
        assertThat(duplicacte.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThatJson(duplicacte.getBody())
            .isEqualTo("""
                {"httpStatus":400,"errorName":"companyAlreadyExists"}""");
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
            .hop(f -> f.put("quotes:createCompany", unsupportedCompany))
            .perform();
        assertThat(company.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThatJson(company.getBody())
            .isEqualTo(
                """
                        {\"httpStatus\":400,"errorName":"stockSymbolNotSupportedInThisMarket"}"""
            );
    }

    @Test
    public void shouldNotCreateCompaniesListedInUnsupportedMarkets() throws JSONException {
        final val unsupportedCompany =
            """
                                {
                                    "id":5,
                                    "name":"Microsoft",
                                    "listings":[{
                                        "marketIdentifier":"XNOP",
                                        "stockSymbol":"AMZN"
                                    }]
                                }""";
        final val company = quotesTraversors
            .startAt(rootResourceOn(port))
            .hop(f -> f.put("quotes:createCompany", unsupportedCompany))
            .perform();
        assertThat(company.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThatJson(company.getBody())
            .isEqualTo(
                """
                            {\"httpStatus\":400,"errorName":"marketNotSupported"}"""
            );
    }

    @Test
    public void shouldRetrieveCreatedCompany() throws JSONException {
        final val company = quotesTraversors
            .startAt(rootResourceOn(port))
            .hop(f -> f.put("quotes:createCompany", getMicrosoft()))
            .hop(f -> f.get("self"))
            .perform();
        assertThat(company.getStatusCode()).isEqualTo(OK);
        assertEquals("should create company", getMicrosoft(), company.getBody(), false);
    }

    @Test
    public void shouldRetrieveQuotedPrice() throws JSONException {
        final val quotedPrice = quotesTraversors
            .startAt(rootResourceOn(port))
            .hop(f -> f.put("quotes:createCompany", getMicrosoft()))
            .hop(f -> f.get("quotes:queryQuotedPrice"))
            .perform();
        assertThat(quotedPrice.getStatusCode()).isEqualTo(OK);
        assertThatJson(quotedPrice.getBody())
            .isEqualTo(
                """
                            {
                                "amount":"128.5",
                                "currency":"USD"
                            }"""
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
}
