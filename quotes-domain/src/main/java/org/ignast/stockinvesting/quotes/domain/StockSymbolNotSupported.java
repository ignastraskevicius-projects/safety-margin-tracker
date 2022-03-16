package org.ignast.stockinvesting.quotes.domain;

public class StockSymbolNotSupported extends ApplicationException {
    public StockSymbolNotSupported(String message) {
        super(message);
    }
}
