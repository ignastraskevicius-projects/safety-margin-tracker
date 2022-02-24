package org.ignast.stockinvesting.domain;

import lombok.EqualsAndHashCode;

import java.util.Currency;

@EqualsAndHashCode
public final class Company {

    private final Currency functionalCurrency;

    public Company(Currency functionalCurrency) {
        this.functionalCurrency = functionalCurrency;
    }

    public Currency getFunctionalCurrency() {
        return functionalCurrency;
    }
}
