package org.ignast.stockinvesting.quotes.persistence.testutil;

import static java.math.BigDecimal.TEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.quotes.persistence.testutil.DomainFactoryForTests.amazon;
import static org.ignast.stockinvesting.quotes.persistence.testutil.DomainFactoryForTests.constantPriceExchange;
import static org.ignast.stockinvesting.quotes.persistence.testutil.DomainFactoryForTests.constantPriceExchanges;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import lombok.val;
import org.ignast.stockinvesting.quotes.domain.Company;
import org.ignast.stockinvesting.quotes.domain.CompanyExternalId;
import org.ignast.stockinvesting.quotes.domain.CompanyName;
import org.ignast.stockinvesting.quotes.domain.MarketIdentifierCode;
import org.ignast.stockinvesting.quotes.domain.QuotesRepository;
import org.ignast.stockinvesting.quotes.domain.StockExchange;
import org.ignast.stockinvesting.quotes.domain.StockExchanges;
import org.ignast.stockinvesting.quotes.domain.StockSymbol;
import org.ignast.stockinvesting.testutil.MockitoUtils;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

public final class DomainFactoryForTests {

    private DomainFactoryForTests() {}

    @SuppressWarnings("checkstyle:magicnumber")
    public static Company amazon() {
        final val nasdaq = mock(StockExchange.class);
        when(nasdaq.getMarketIdentifierCode()).thenReturn(new MarketIdentifierCode("XNAS"));

        return Company.create(
            new CompanyExternalId(6),
            new CompanyName("Amazon"),
            new StockSymbol("AMZN"),
            nasdaq
        );
    }

    public static QuotesRepository anyQuotes() {
        return MockitoUtils.mock(
            QuotesRepository.class,
            r -> when(r.getQuotedPriceOf(any(), any())).thenReturn(TEN)
        );
    }

    public static StockExchanges constantPriceExchanges(final Money price) {
        final val exchange = constantPriceExchange(price);
        final val exchanges = mock(StockExchanges.class);
        when(exchanges.getFor(any())).thenReturn(exchange);
        return exchanges;
    }

    public static StockExchange constantPriceExchange(final Money price) {
        final val exchange = mock(StockExchange.class);
        when(exchange.getQuotedPrice(any())).thenReturn(price);
        when(exchange.getMarketIdentifierCode()).thenReturn(new MarketIdentifierCode("XANY"));
        return exchange;
    }
}

final class DomainFactoryForTestsTest {

    @Test
    public void shouldCreateAmazon() {
        final val externalAmazonIdForTests = 6;
        assertThat(amazon().getExternalId()).isEqualTo(new CompanyExternalId(externalAmazonIdForTests));
        assertThat(amazon().getName()).isEqualTo(new CompanyName("Amazon"));
        assertThat(amazon().getStockSymbol()).isEqualTo(new StockSymbol("AMZN"));
        assertThat(amazon().getStockExchange().getMarketIdentifierCode())
            .isEqualTo(new MarketIdentifierCode("XNAS"));
        assertThat(amazon().getStockExchange().getMarketIdentifierCode())
            .isEqualTo(new MarketIdentifierCode("XNAS"));
    }

    @Test
    public void shouldCreateConstantPriceExchanges() {
        final val price = Money.of(TEN, "USD");

        final val exchanges = constantPriceExchanges(price);

        final val exchange = exchanges.getFor(new MarketIdentifierCode("XANY"));
        assertThat(exchange.getQuotedPrice(new StockSymbol("ANY"))).isEqualTo(price);
        assertThat(exchange.getMarketIdentifierCode()).isNotNull();
        assertThat(exchange.getMarketIdentifierCode().get()).isNotNull();
    }

    @Test
    public void shouldCreateConstantPriceExchange() {
        final val price = Money.of(TEN, "USD");

        final val exchange = constantPriceExchange(price);

        assertThat(exchange.getQuotedPrice(new StockSymbol("ANY"))).isEqualTo(price);
        assertThat(exchange.getMarketIdentifierCode()).isNotNull();
        assertThat(exchange.getMarketIdentifierCode().get()).isNotNull();
    }
}
