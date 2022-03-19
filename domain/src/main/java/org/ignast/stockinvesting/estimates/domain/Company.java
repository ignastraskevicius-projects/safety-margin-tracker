package org.ignast.stockinvesting.estimates.domain;

import java.util.Currency;
import java.util.Locale;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public final class Company {

    private static final int MAX_NAME_LENGTH_FOUND_IN_UK = 160;

    @Id
    @EqualsAndHashCode.Include
    private String id = "AAA";

    private String countryCode;

    @Transient
    private Currency functionalCurrency;

    private String name;

    protected Company() {
        //constructor for JPA
    }

    public Company(
        @NonNull final String name,
        @NonNull final String countryCode,
        @NonNull final Currency functionalCurrency
    ) {
        if (name.isEmpty() || name.length() > MAX_NAME_LENGTH_FOUND_IN_UK) {
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
