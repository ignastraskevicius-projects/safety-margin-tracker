package org.ignast.stockinvesting.quotes;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

@EqualsAndHashCode
public final class StockSymbol {
    private final String code;

    public StockSymbol(@NonNull String code) {
        if (code.isEmpty() || code.length() > 6) {
            throw new IllegalArgumentException("Stock Symbol must contain between 1-6 characters");
        }
        if (!code.matches("^[A-Z0-9]*$")) {
            throw new IllegalArgumentException("Stock Symbol must contain only uppercase alphanumeric characters");
        }
        this.code = code;
    }

    public String get() {
        return code;
    }
}
