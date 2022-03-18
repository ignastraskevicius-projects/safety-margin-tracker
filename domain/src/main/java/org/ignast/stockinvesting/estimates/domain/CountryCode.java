package org.ignast.stockinvesting.estimates.domain;

import java.util.Locale;
import lombok.NonNull;

public class CountryCode {

    private final String code;

    public CountryCode(@NonNull final String code) {
        if (code.length() != 2) {
            throw new IllegalArgumentException("Must consist of 2 characters");
        }
        if (!code.matches("^[A-Z]*$")) {
            throw new IllegalArgumentException("Must contain only uppercase latin characters");
        }
        if (!Locale.getISOCountries(Locale.IsoCountryCode.PART1_ALPHA2).contains(code)) {
            throw new IllegalArgumentException("Must be a valid ISO 3166 alpha-2 code");
        }
        this.code = code;
    }

    public String get() {
        return code;
    }
}
