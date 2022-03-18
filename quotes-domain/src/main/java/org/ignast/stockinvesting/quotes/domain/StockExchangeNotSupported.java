package org.ignast.stockinvesting.quotes.domain;

import static java.lang.String.format;

public class StockExchangeNotSupported extends ApplicationException {
    public StockExchangeNotSupported(final MarketIdentifierCode marketIdentifierCode) {
        super(format("Market Identifier Code '%s' is not supported", marketIdentifierCode.get()));
    }
}
