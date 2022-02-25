package org.ignast.stockinvesting.domain;

import lombok.NonNull;

public class MarketIdentifierCode {
    private String code;

    public MarketIdentifierCode(@NonNull String code) {
        if (code.length() != 4) {
            throw new IllegalArgumentException();
        }
        if (!code.matches("^[A-Z]*$")) {
            throw new IllegalArgumentException();
        }
        this.code = code;
    }

    public String get() {
        return code;
    }
}
