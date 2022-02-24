package org.ignast.stockinvesting.domain;

import lombok.val;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;

class CompanyTest {
    @Test
    public void shouldBeEqual() {
        EqualsVerifier.forClass(Company.class).verify();
    }

    @ParameterizedTest
    @ValueSource(strings = { "EUR", "USD" })
    public void shouldHaveFunctionalCurrency(String currencyCode) {
        val currency = Currency.getInstance(currencyCode);
        assertThat(new Company(currency).getFunctionalCurrency()).isEqualTo(currency);
    }
}