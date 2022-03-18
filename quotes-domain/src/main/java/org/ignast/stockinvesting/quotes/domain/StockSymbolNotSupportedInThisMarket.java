package org.ignast.stockinvesting.quotes.domain;

public class StockSymbolNotSupportedInThisMarket extends ApplicationException {
    public StockSymbolNotSupportedInThisMarket(final String message) {
        super(message);
    }
}
