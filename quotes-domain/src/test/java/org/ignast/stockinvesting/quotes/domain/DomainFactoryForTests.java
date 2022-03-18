package org.ignast.stockinvesting.quotes.domain;

import java.math.BigDecimal;

public final class DomainFactoryForTests {

    private DomainFactoryForTests() {}

    public static CompanyExternalId anyId() {
        return new CompanyExternalId(15);
    }

    public static StockSymbol anySymbol() {
        return new StockSymbol("ANY");
    }

    public static MarketIdentifierCode anyMIC() {
        return new MarketIdentifierCode("XNYS");
    }

    public static CompanyName anyCompanyName() {
        return new CompanyName("any");
    }

    public static CurrencyCode anyCurrencyCode() {
        return new CurrencyCode("EUR");
    }

    public static StockExchange anyStockExchange() {
        return StockExchange.create(anyMIC(), anyCurrencyCode(), anyQuotes());
    }

    public static QuotesRepository anyQuotes() {
        return (stockSymbol, mic) -> BigDecimal.ONE;
    }
}
