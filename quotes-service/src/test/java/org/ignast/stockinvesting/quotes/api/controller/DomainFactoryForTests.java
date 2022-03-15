package org.ignast.stockinvesting.quotes.api.controller;

import org.ignast.stockinvesting.quotes.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.quotes.api.controller.DomainFactoryForTests.amazon;

public class DomainFactoryForTests {
    public static Company amazon() {
        return new Company(new PositiveNumber(6), new CompanyName("Amazon"), new StockSymbol("AMZN"),
                new StockExchanges(new StubQuotesRepository()).getFor(new MarketIdentifierCode("XNAS")));
    }

    private static class StubQuotesRepository implements QuotesRepository {

        @Override
        public BigDecimal getQuotedPriceOf(StockSymbol stockSymbol, MarketIdentifierCode mic) {
            return null;
        }
    }
}

class DomainFactoryForTestsTest {

    @Test
    public void shouldCreateAmazon() {
        assertThat(amazon().getExternalId()).isEqualTo(new PositiveNumber(6));
        assertThat(amazon().getName()).isEqualTo(new CompanyName("Amazon"));
        assertThat(amazon().getStockSymbol()).isEqualTo(new StockSymbol("AMZN"));
        assertThat(amazon().getStockExchange().getMarketIdentifierCode()).isEqualTo(new MarketIdentifierCode("XNAS"));
    }
}
