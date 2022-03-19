package org.ignast.stockinvesting.estimates.domain;

import lombok.NonNull;

public class StockSymbol {

    private static final int MAX_LENGTH_FOUND_IN_SHANGHAI_STOCK_EXCHANGE = 5;

    private final String code;

    public StockSymbol(@NonNull final String code) {
        if (code.isEmpty() || code.length() > MAX_LENGTH_FOUND_IN_SHANGHAI_STOCK_EXCHANGE) {
            throw new IllegalArgumentException("Stock Symbol must contain between 1-5 characters");
        }
        if (!code.matches("^[A-Z0-9]*$")) {
            throw new IllegalArgumentException(
                "Stock Symbol must contain only uppercase alphanumeric characters"
            );
        }
        this.code = code;
    }

    public String get() {
        return code;
    }
}
