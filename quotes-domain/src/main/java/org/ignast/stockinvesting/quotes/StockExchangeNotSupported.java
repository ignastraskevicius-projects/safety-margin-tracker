package org.ignast.stockinvesting.quotes;

import static java.lang.String.format;

public class StockExchangeNotSupported extends ApplicationException {
    public StockExchangeNotSupported(MarketIdentifierCode marketIdentifierCode) {
        super(format("Market Identifier Code '%s' is not supported", marketIdentifierCode.get()));
    }
}
