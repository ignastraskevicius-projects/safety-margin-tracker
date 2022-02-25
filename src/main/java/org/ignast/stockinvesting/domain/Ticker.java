package org.ignast.stockinvesting.domain;

import lombok.NonNull;

public class Ticker {
    private String code;

    public Ticker(@NonNull String code) {
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
