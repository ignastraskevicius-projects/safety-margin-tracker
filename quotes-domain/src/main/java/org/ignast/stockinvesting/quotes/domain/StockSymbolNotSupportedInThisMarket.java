package org.ignast.stockinvesting.quotes.domain;

public class StockSymbolNotSupportedInThisMarket extends ApplicationException {
    public StockSymbolNotSupportedInThisMarket(String message) {
        super(message);
    }
}
