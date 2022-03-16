package org.ignast.stockinvesting.quotes.persistence.repositories;

import org.ignast.stockinvesting.quotes.domain.CompanyName;
import org.ignast.stockinvesting.quotes.domain.MarketIdentifierCode;
import org.ignast.stockinvesting.quotes.domain.StockSymbol;

public class DomainFactoryForTests {
    public static StockSymbol anySymbol() {
        return new StockSymbol("ANY");
    }

    public static MarketIdentifierCode anyMIC() {
        return new MarketIdentifierCode("XNYS");
    }

    public static CompanyName anyCompanyName() {
        return new CompanyName("any");
    }
}
