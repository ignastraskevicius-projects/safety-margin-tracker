package org.ignast.stockinvesting.quotes;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;

public class DomainFactoryForTests {
    public static PositiveNumber anyId() {
        return new PositiveNumber(15);
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
        return new QuotesRepository() {

            @Override
            public BigDecimal getQuotedPriceOf(StockSymbol stockSymbol, MarketIdentifierCode mic) {
                return BigDecimal.ONE;
            }
        };
    }
}
