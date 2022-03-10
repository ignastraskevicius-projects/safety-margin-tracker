package org.ignast.stockinvesting.quotes;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.mock;

public class DomainFactoryForTests {
    public static StockSymbol anySymbol() {
        return new StockSymbol("ANY");
    }

    public static MarketIdentifierCode anyMIC() {
        return new MarketIdentifierCode("XNYS");
    }

    public static UUID anyId() {
        return CompanyId.toUUID("55f20234-0f0f-4d61-ae84-44e5428e17c1");
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
