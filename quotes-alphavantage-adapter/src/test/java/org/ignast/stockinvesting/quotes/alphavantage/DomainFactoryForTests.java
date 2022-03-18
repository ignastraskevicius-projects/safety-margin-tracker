package org.ignast.stockinvesting.quotes.alphavantage;

import org.ignast.stockinvesting.quotes.domain.MarketIdentifierCode;
import org.ignast.stockinvesting.quotes.domain.StockSymbol;

public final class DomainFactoryForTests {

    private DomainFactoryForTests() {}

    public static StockSymbol anySymbol() {
        return new StockSymbol("ANY");
    }

    public static MarketIdentifierCode anyMIC() {
        return new MarketIdentifierCode("AANY");
    }
}
