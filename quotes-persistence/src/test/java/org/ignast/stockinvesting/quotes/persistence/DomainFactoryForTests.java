package org.ignast.stockinvesting.quotes.persistence;

import org.ignast.stockinvesting.quotes.*;

import java.math.BigDecimal;
import java.util.UUID;

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
