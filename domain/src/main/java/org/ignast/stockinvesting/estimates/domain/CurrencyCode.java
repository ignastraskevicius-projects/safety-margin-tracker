package org.ignast.stockinvesting.estimates.domain;

import lombok.NonNull;

import java.util.Currency;

public class CurrencyCode {

    private String code;
    public CurrencyCode(@NonNull String code) {
        if (code.length() != 3) {
            throw new IllegalArgumentException("Currency must have 3 letters");
        }
        if (!code.matches("^[A-Z]*$")) {
            throw new IllegalArgumentException(
                    "Currency must contain only uppercase latin characters");
        }
        try {
            Currency.getInstance(code);
        } catch(IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Currency must be a valid ISO 4217 code");
        }
        this.code = code;
    }

    public String get() {
        return code;
    }
}
