package org.ignast.stockinvesting.estimates.domain;

public class DomainFactoryForTests {
    public static StockSymbol anySymbol() {
        return new StockSymbol("ANY");
    }

    public static MarketIdentifierCode anyMIC() {
        return new MarketIdentifierCode("AANY");
    }
}
