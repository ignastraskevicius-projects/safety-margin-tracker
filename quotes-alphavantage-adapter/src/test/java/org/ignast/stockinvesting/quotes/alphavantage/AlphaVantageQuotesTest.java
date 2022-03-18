package org.ignast.stockinvesting.quotes.alphavantage;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.hamcrest.Matchers.anything;
import static org.ignast.stockinvesting.quotes.alphavantage.DomainFactoryForTests.anyMIC;
import static org.ignast.stockinvesting.quotes.alphavantage.DomainFactoryForTests.anySymbol;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import java.math.BigDecimal;
import lombok.val;
import org.ignast.stockinvesting.quotes.domain.ApplicationException;
import org.ignast.stockinvesting.quotes.domain.MarketIdentifierCode;
import org.ignast.stockinvesting.quotes.domain.QuotesRepository;
import org.ignast.stockinvesting.quotes.domain.QuotesRepository.QuoteRetrievalFailedException;
import org.ignast.stockinvesting.quotes.domain.StockSymbol;
import org.ignast.stockinvesting.quotes.domain.StockSymbolNotSupportedInThisMarket;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

@RestClientTest(AlphaVantageQuotes.class)
@TestPropertySource(
    properties = { "alphavantage.url=https://test.uri.com", "alphavantage.apikey=testApiKey" }
)
public final class AlphaVantageQuotesTest {

    @Autowired
    private QuotesRepository alphaVantageQuotes;

    @Autowired
    private MockRestServiceServer mockServer;

    @Test
    public void shouldRetrievePrice() {
        mockServer
            .expect(requestTo(anything()))
            .andRespond(
                withSuccess("{\"Global Quote\":{\"05. price\":\"128.5000\"}}", MediaType.APPLICATION_JSON)
            );

        final val price = alphaVantageQuotes.getQuotedPriceOf(anySymbol(), anyMIC());

        assertThat(price).isEqualTo(new BigDecimal("128.5000"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "AMZN", "MSFT" })
    public void shouldGetStockQuotesForCompanyRetrievingUrlAndApiKeyFromProperties(final String stockSymbol) {
        final val uri = format(
            "%s/query?function=GLOBAL_QUOTE&symbol=%s&apikey=%s",
            "https://test.uri.com",
            stockSymbol,
            "testApiKey"
        );
        mockServer
            .expect(requestTo(uri))
            .andRespond(
                withSuccess("{\"Global Quote\":{\"05. price\":\"128.5000\"}}", MediaType.APPLICATION_JSON)
            );

        alphaVantageQuotes.getQuotedPriceOf(new StockSymbol(stockSymbol), new MarketIdentifierCode("XNYS"));
    }

    @Test
    public void shouldNotFindSymbol() {
        mockServer
            .expect(requestTo(anything()))
            .andRespond(withSuccess("{\"Global Quote\":{}}", MediaType.APPLICATION_JSON));

        assertThatExceptionOfType(StockSymbolNotSupportedInThisMarket.class)
            .isThrownBy(() ->
                alphaVantageQuotes.getQuotedPriceOf(new StockSymbol("A"), new MarketIdentifierCode("XNYS"))
            )
            .withMessage("Stock symbol 'A' in market 'XNYS' is not supported by this service")
            .isInstanceOf(ApplicationException.class);
    }

    @Test
    public void shouldThrowOnRemoteServerConstrainsViolations() {
        final val underlyingMessage = "underlying exception from server";
        mockServer
            .expect(requestTo(anything()))
            .andRespond(
                withSuccess(
                    format("{\"Error Message\":\"%s\"}", underlyingMessage),
                    MediaType.APPLICATION_JSON
                )
            );

        assertThatExceptionOfType(QuoteRetrievalFailedException.class)
            .isThrownBy(() -> alphaVantageQuotes.getQuotedPriceOf(anySymbol(), anyMIC()))
            .withMessage("Message from remote server: " + underlyingMessage);
    }

    @Test
    public void shouldThrowIfReceivedJsonIsOfNotExpectedStructure() {
        mockServer
            .expect(requestTo(anything()))
            .andRespond(withSuccess("{\"unexpected\":\"json\"}", MediaType.APPLICATION_JSON));

        assertThatExceptionOfType(QuoteRetrievalFailedException.class)
            .isThrownBy(() -> alphaVantageQuotes.getQuotedPriceOf(anySymbol(), anyMIC()))
            .withMessage("Communication with server failed");
    }

    @Test
    public void shouldThrowIfReceivedBodyIsNotJson() {
        mockServer
            .expect(requestTo(anything()))
            .andRespond(withSuccess("not-valid-json", MediaType.APPLICATION_JSON));

        assertThatExceptionOfType(QuoteRetrievalFailedException.class)
            .isThrownBy(() -> alphaVantageQuotes.getQuotedPriceOf(anySymbol(), anyMIC()))
            .withMessage("Communication with server failed");
    }

    @Test
    public void shouldThrowOnHttpServerError() {
        final val underlyingMessage = "underlying exception from server";
        mockServer.expect(requestTo(anything())).andRespond(withServerError());

        assertThatExceptionOfType(QuoteRetrievalFailedException.class)
            .isThrownBy(() -> alphaVantageQuotes.getQuotedPriceOf(anySymbol(), anyMIC()))
            .withMessage("Communication with server failed");
    }
}

final class AlphaVantageCasesUnableToCoverWithSpringTest {

    @RegisterExtension
    private static final WireMockExtension WIREMOCK = WireMockExtension.newInstance().build();

    @Test
    public void shouldThrowIfResponseContentTypeUnexpected() {
        WIREMOCK.stubFor(
            get(urlPathEqualTo("/query"))
                .willReturn(
                    WireMock
                        .ok("{\"Global Quote\":{\"05. price\":\"128.5000\"}}")
                        .withHeader("Content-Type", "application/octet-stream")
                )
        );
        final val wireMockUrl = "http://localhost:" + WIREMOCK.getPort();
        final val alphaVantageQuotes = new AlphaVantageQuotes(
            new RestTemplateBuilder(),
            wireMockUrl,
            "anyApiKey"
        );

        assertThatExceptionOfType(QuoteRetrievalFailedException.class)
            .isThrownBy(() -> alphaVantageQuotes.getQuotedPriceOf(anySymbol(), anyMIC()))
            .withMessage("Communication with server failed");
    }
}
