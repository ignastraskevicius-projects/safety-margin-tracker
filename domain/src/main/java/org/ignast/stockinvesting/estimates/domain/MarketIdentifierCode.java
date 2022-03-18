package org.ignast.stockinvesting.estimates.domain;

import lombok.NonNull;

public class MarketIdentifierCode {
    private final String code;

    public MarketIdentifierCode(@NonNull final String code) {
        if (code.length() != 4) {
            throw new IllegalArgumentException("Market Identifier is not 4 characters long (ISO 10383 standard)");
        }
        if (!code.matches("^[A-Z]*$")) {
            throw new IllegalArgumentException(
                    "Market Identifier must contain only latin uppercase alphanumeric characters (ISO 10383 standard)");
        }
        this.code = code;
    }

    public String get() {
        return code;
    }
}
