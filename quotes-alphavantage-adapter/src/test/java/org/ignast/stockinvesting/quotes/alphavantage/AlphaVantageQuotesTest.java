package org.ignast.stockinvesting.quotes.alphavantage;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import lombok.val;
import org.ignast.stockinvesting.quotes.QuotesRepository;
import org.ignast.stockinvesting.quotes.QuotesRepository.QuoteRetrievalFailedException;
import org.ignast.stockinvesting.quotes.StockSymbolNotSupported;
import org.ignast.stockinvesting.quotes.StockSymbol;
import org.ignast.stockinvesting.quotes.MarketIdentifierCode;
import org.ignast.stockinvesting.quotes.ApplicationException;
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

import java.math.BigDecimal;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.hamcrest.Matchers.anything;
import static org.ignast.stockinvesting.quotes.alphavantage.DomainFactoryForTests.anyMIC;
import static org.ignast.stockinvesting.quotes.alphavantage.DomainFactoryForTests.anySymbol;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest({ AlphaVantageQuotes.class })
@TestPropertySource(properties = { "alphavantage.url=https://test.uri.com", "alphavantage.apikey=testApiKey" })
class AlphaVantageQuotesTest {

    @Autowired
    private QuotesRepository alphaVantageQuotes;

    @Autowired
    private MockRestServiceServer mockServer;

    @Test
    public void shouldRetrievePrice() {
        mockServer.expect(requestTo(anything()))
                .andRespond(withSuccess("{\"Global Quote\":{\"05. price\":\"128.5000\"}}", MediaType.APPLICATION_JSON));

        val price = alphaVantageQuotes.getQuotedPriceOf(anySymbol(), anyMIC());

        assertThat(price).isEqualTo(new BigDecimal("128.5000"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "AMZN", "MSFT" })
    public void shouldGetStockQuotesForCompanyRetrievingUrlAndApiKeyFromProperties(String stockSymbol) {
        val uri = format("%s/query?function=GLOBAL_QUOTE&symbol=%s&apikey=%s", "https://test.uri.com", stockSymbol,
                "testApiKey");
        mockServer.expect(requestTo(uri))
                .andRespond(withSuccess("{\"Global Quote\":{\"05. price\":\"128.5000\"}}", MediaType.APPLICATION_JSON));

        alphaVantageQuotes.getQuotedPriceOf(new StockSymbol(stockSymbol), new MarketIdentifierCode("XNYS"));
    }

    @Test
    public void shouldNotFindSymbol() {
        mockServer.expect(requestTo(anything()))
                .andRespond(withSuccess(format("{\"Global Quote\":{}}"), MediaType.APPLICATION_JSON));

        assertThatExceptionOfType(StockSymbolNotSupported.class).isThrownBy(
                () -> alphaVantageQuotes.getQuotedPriceOf(new StockSymbol("A"), new MarketIdentifierCode("XNYS")))
                .withMessage("Stock symbol 'A' in market 'XNYS' is not supported by this service")
                .isInstanceOf(ApplicationException.class);
    }

    @Test
    public void shouldThrowOnRemoteServerConstrainsViolations() {
        val underlyingMessage = "underlying exception from server";
        mockServer.expect(requestTo(anything())).andRespond(
                withSuccess(format("{\"Error Message\":\"%s\"}", underlyingMessage), MediaType.APPLICATION_JSON));

        assertThatExceptionOfType(QuoteRetrievalFailedException.class)
                .isThrownBy(() -> alphaVantageQuotes.getQuotedPriceOf(anySymbol(), anyMIC()))
                .withMessage("Message from remote server: " + underlyingMessage);
    }

    @Test
    public void shouldThrowIfReceivedJsonIsOfNotExpectedStructure() {
        mockServer.expect(requestTo(anything()))
                .andRespond(withSuccess(format("{\"unexpected\":\"json\"}"), MediaType.APPLICATION_JSON));

        assertThatExceptionOfType(QuoteRetrievalFailedException.class)
                .isThrownBy(() -> alphaVantageQuotes.getQuotedPriceOf(anySymbol(), anyMIC()))
                .withMessage("Communication with server failed");
    }

    @Test
    public void shouldThrowIfReceivedBodyIsNotJson() {
        mockServer.expect(requestTo(anything())).andRespond(withSuccess("not-valid-json", MediaType.APPLICATION_JSON));

        assertThatExceptionOfType(QuoteRetrievalFailedException.class)
                .isThrownBy(() -> alphaVantageQuotes.getQuotedPriceOf(anySymbol(), anyMIC()))
                .withMessage("Communication with server failed");
    }
}

class AlphaVantageCasesUnableToCoverWithSpringTest {

    @RegisterExtension
    private static WireMockExtension wireMock = WireMockExtension.newInstance().build();

    @Test
    public void shouldThrowIfResponseContentTypeUnexpected() {
        wireMock.stubFor(get(urlPathEqualTo("/query")).willReturn(WireMock.ok("{\"Global Quote\":{\"05. price\":\"128.5000\"}}")
                .withHeader("Content-Type", "application/octet-stream")));
        val wireMockUrl = "http://localhost:" + wireMock.getPort();
        val alphaVantageQuotes = new AlphaVantageQuotes(new RestTemplateBuilder(), wireMockUrl, "anyApiKey");

        assertThatExceptionOfType(QuoteRetrievalFailedException.class)
                .isThrownBy(() -> alphaVantageQuotes.getQuotedPriceOf(anySymbol(), anyMIC()))
                .withMessage("Communication with server failed");
    }
}