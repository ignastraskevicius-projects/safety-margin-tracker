package org.ignast.stockinvesting.domain;

public class TickerNotSupported extends ApplicationException {
    public TickerNotSupported(String message) {
        super(message);
    }

}
