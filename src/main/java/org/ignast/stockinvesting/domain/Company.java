package org.ignast.stockinvesting.domain;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.Currency;
import java.util.Locale;

@EqualsAndHashCode
public final class Company {

    private final String countryCode;
    private final Currency functionalCurrency;

    public Company(@NonNull String countryCode, @NonNull Currency functionalCurrency) {
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
}
