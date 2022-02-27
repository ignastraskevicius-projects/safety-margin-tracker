package org.ignast.stockinvesting.domain;

public class DomainFactoryForTests {
    public static Ticker anyTicker() {
        return new Ticker("ANY");
    }

    public static MarketIdentifierCode anyMIC() {
        return new MarketIdentifierCode("AANY");
    }
}
