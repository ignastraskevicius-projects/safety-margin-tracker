package org.ignast.stockinvesting.estimates.domain;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.Currency;
import java.util.Locale;

@EqualsAndHashCode
public final class Company {

    private final String countryCode;
    private final Currency functionalCurrency;
    private final String name;

    public Company(@NonNull String name, @NonNull String countryCode, @NonNull Currency functionalCurrency) {
        if (name.isEmpty() || name.length() > 255) {
            throw new IllegalArgumentException();
        }
        this.name = name;
        if (!Locale.getISOCountries(Locale.IsoCountryCode.PART1_ALPHA2).contains(countryCode)) {
            throw new IllegalArgumentException();
        }
        this.countryCode = countryCode;
        this.functionalCurrency = functionalCurrency;
    }

    public Currency getFunctionalCurrency() {
        return functionalCurrency;
    }

    public String getHomeCountry() {
        return countryCode;
    }

    public String getName() {
        return name;
    }
}
