package org.ignast.stockinvesting.estimates.domain;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Currency;
import java.util.Locale;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public final class Company {
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