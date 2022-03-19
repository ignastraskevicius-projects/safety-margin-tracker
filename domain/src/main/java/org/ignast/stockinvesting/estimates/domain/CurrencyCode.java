package org.ignast.stockinvesting.estimates.domain;

import java.util.Currency;
import lombok.NonNull;

public class CurrencyCode {

    private static final int ISO_4217_LENGTH = 3;

    private final String code;

    public CurrencyCode(@NonNull final String code) {
        if (code.length() != ISO_4217_LENGTH) {
            throw new IllegalArgumentException("Currency must have 3 letters");
        }
        if (!code.matches("^[A-Z]*$")) {
            throw new IllegalArgumentException("Currency must contain only uppercase latin characters");
        }
        try {
            Currency.getInstance(code);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Currency must be a valid ISO 4217 code");
        }
        this.code = code;
    }

    public String get() {
        return code;
    }
}
