package org.ignast.stockinvesting.quotes;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.ignast.stockinvesting.domain.*;
import org.ignast.stockinvesting.domain.StockQuotes.QuoteRetrievalFailedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

import java.math.BigDecimal;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.hamcrest.Matchers.anything;
import static org.ignast.stockinvesting.domain.DomainFactoryForTests.anyMIC;
import static org.ignast.stockinvesting.domain.DomainFactoryForTests.anyTicker;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest({ AlphaVantageQuotes.class })
@TestPropertySource(properties = { "alphavantage.url=https://test.uri.com", "alphavantage.apikey=testApiKey" })
class AlphaVantageQuotesTest {

    @Autowired
    private StockQuotes alphaVantageQuotes;

    @Autowired
    private MockRestServiceServer mockServer;

    @Test
    public void shouldRetrievePrice() {
        mockServer.expect(requestTo(anything()))
                .andRespond(withSuccess("{\"Global Quote\":{\"05. price\":\"128.5000\"}}", MediaType.APPLICATION_JSON));

        val price = alphaVantageQuotes.getQuotedPriceOf(anyTicker(), anyMIC());

        assertThat(price).isEqualTo(new BigDecimal("128.5000"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "AMZN", "MSFT" })
    public void shouldGetStockQuotesForCompanyRetrievingUrlAndApiKeyFromProperties(String ticker) {
        val uri = format("%s/query?function=GLOBAL_QUOTE&symbol=%s&apikey=%s", "https://test.uri.com", ticker,
                "testApiKey");
        mockServer.expect(requestTo(uri))
                .andRespond(withSuccess("{\"Global Quote\":{\"05. price\":\"128.5000\"}}", MediaType.APPLICATION_JSON));

        alphaVantageQuotes.getQuotedPriceOf(new Ticker(ticker), new MarketIdentifierCode("XNYS"));
    }

    @Test
    public void shouldNotFindTicker() {
        Assertions.setMaxStackTraceElementsDisplayed(200);
        mockServer.expect(requestTo(anything()))
                .andRespond(withSuccess(format("{\"Global Quote\":{}}"), MediaType.APPLICATION_JSON));

        assertThatExceptionOfType(TickerNotSupported.class)
                .isThrownBy(
                        () -> alphaVantageQuotes.getQuotedPriceOf(new Ticker("A"), new MarketIdentifierCode("XNYS")))
                .withMessage("Ticker 'A' in market 'XNYS' is not supported by this service")
                .isInstanceOf(ApplicationException.class);
    }

    @Test
    public void shouldThrowOnRemoteServerConstrainsViolations() {
        val underlyingMessage = "underlying exception from server";
        mockServer.expect(requestTo(anything())).andRespond(
                withSuccess(format("{\"Error Message\":\"%s\"}", underlyingMessage), MediaType.APPLICATION_JSON));

        assertThatExceptionOfType(QuoteRetrievalFailedException.class)
                .isThrownBy(() -> alphaVantageQuotes.getQuotedPriceOf(anyTicker(), anyMIC()))
                .withMessage("Message from remote server: " + underlyingMessage);
    }

    @Test
    public void shouldThrowIfReceivedJsonIsOfNotExpectedStructure() {
        mockServer.expect(requestTo(anything()))
                .andRespond(withSuccess(format("{\"unexpected\":\"json\"}"), MediaType.APPLICATION_JSON));

        assertThatExceptionOfType(QuoteRetrievalFailedException.class)
                .isThrownBy(() -> alphaVantageQuotes.getQuotedPriceOf(anyTicker(), anyMIC()))
                .withMessage("Communication with server failed");
    }

    @Test
    public void shouldThrowIfReceivedBodyIsNotJson() {
        mockServer.expect(requestTo(anything())).andRespond(withSuccess("not-valid-json", MediaType.APPLICATION_JSON));

        assertThatExceptionOfType(QuoteRetrievalFailedException.class)
                .isThrownBy(() -> alphaVantageQuotes.getQuotedPriceOf(anyTicker(), anyMIC()))
                .withMessage("Communication with server failed");
    }
}