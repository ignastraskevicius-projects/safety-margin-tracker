package org.ignast.stockinvesting.domain;

import lombok.NonNull;

public class StockSymbol {
    private String code;

    public StockSymbol(@NonNull String code) {
        if (code.isEmpty() || code.length() > 5) {
            throw new IllegalArgumentException();
        }
        if (!code.matches("^[A-Z0-9]*$")) {
            throw new IllegalArgumentException();
        }
        this.code = code;
    }

    public String get() {
        return code;
    }
}
