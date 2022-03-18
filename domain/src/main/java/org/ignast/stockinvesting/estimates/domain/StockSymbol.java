package org.ignast.stockinvesting.estimates.domain;

import lombok.NonNull;

public class StockSymbol {

    private final String code;

    public StockSymbol(@NonNull final String code) {
        if (code.isEmpty() || code.length() > 5) {
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
