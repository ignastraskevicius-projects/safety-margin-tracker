package org.ignast.stockinvesting.quotes.api.testutil;

import org.ignast.stockinvesting.quotes.domain.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.quotes.api.testutil.DomainFactoryForTests.amazon;
import static org.ignast.stockinvesting.quotes.api.testutil.DomainFactoryForTests.exchangeNotSupportingAnySymbol;
import static org.ignast.stockinvesting.testutil.MockitoUtils.mock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class DomainFactoryForTests {
    public static Company amazon() {
        return new Company(new CompanyExternalId(6), new CompanyName("Amazon"), new StockSymbol("AMZN"),
                new StockExchanges(new StubQuotesRepository()).getFor(new MarketIdentifierCode("XNAS")));
    }

    private static class StubQuotesRepository implements QuotesRepository {

        @Override
        public BigDecimal getQuotedPriceOf(StockSymbol stockSymbol, MarketIdentifierCode mic) {
            return null;
        }
    }

    public static StockExchange exchangeNotSupportingAnySymbol() {
        return mock(StockExchange.class, e -> when(e.getQuotedPrice(any())).thenThrow(StockSymbolNotSupportedInThisMarket.class));
    }
}

class DomainFactoryForTestsTest {

    @Test
    public void shouldCreateAmazon() {
        assertThat(amazon().getExternalId()).isEqualTo(new CompanyExternalId(6));
        assertThat(amazon().getName()).isEqualTo(new CompanyName("Amazon"));
        assertThat(amazon().getStockSymbol()).isEqualTo(new StockSymbol("AMZN"));
        assertThat(amazon().getStockExchange().getMarketIdentifierCode()).isEqualTo(new MarketIdentifierCode("XNAS"));
    }

    @Test
    public void shouldCreateStockExchangeNotSupportinSymbol() {
        assertThatExceptionOfType(StockSymbolNotSupportedInThisMarket.class).isThrownBy(() ->
                exchangeNotSupportingAnySymbol().getQuotedPrice(new StockSymbol("AAAA")));
    }
}
