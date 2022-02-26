package org.ignast.stockinvesting.quotes;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import lombok.val;
import org.ignast.stockinvesting.domain.MarketIdentifierCode;
import org.ignast.stockinvesting.domain.Ticker;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@WireMockTest
class AlphaVantageQuotesTest {

    @Test
    public void shouldGetStockQuotes(WireMockRuntimeInfo wireMock) {
        AlphaVantageStub.returningPrice();
        val quotes = new AlphaVantageQuotes();
        assertThat(quotes.getQuotedPriceOf(new Ticker("A"), new MarketIdentifierCode("XNYS"))).isNotNull();
    }

}