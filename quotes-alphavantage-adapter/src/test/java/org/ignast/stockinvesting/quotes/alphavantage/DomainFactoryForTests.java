package org.ignast.stockinvesting.quotes.alphavantage;

import org.ignast.stockinvesting.quotes.domain.StockSymbol;
import org.ignast.stockinvesting.quotes.domain.MarketIdentifierCode;

public class DomainFactoryForTests {
    public static StockSymbol anySymbol() {
        return new StockSymbol("ANY");
    }

    public static MarketIdentifierCode anyMIC() {
        return new MarketIdentifierCode("AANY");
    }
}
