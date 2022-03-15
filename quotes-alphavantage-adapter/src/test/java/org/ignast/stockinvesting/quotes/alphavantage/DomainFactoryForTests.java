package org.ignast.stockinvesting.quotes.alphavantage;

import org.ignast.stockinvesting.quotes.StockSymbol;
import org.ignast.stockinvesting.quotes.MarketIdentifierCode;

public class DomainFactoryForTests {
    public static StockSymbol anySymbol() {
        return new StockSymbol("ANY");
    }

    public static MarketIdentifierCode anyMIC() {
        return new MarketIdentifierCode("AANY");
    }
}
