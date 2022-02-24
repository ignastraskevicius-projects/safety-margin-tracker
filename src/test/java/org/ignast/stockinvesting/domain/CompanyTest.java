package org.ignast.stockinvesting.domain;

import lombok.val;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class CompanyTest {
    @Test
    public void shouldBeEqual() {
        EqualsVerifier.forClass(Company.class).verify();
    }

    @ParameterizedTest
    @ValueSource(strings = { "EUR", "USD" })
    public void shouldHaveFunctionalCurrency(String currencyCode) {
        val currency = Currency.getInstance(currencyCode);
        assertThat(new Company("US", currency).getFunctionalCurrency()).isEqualTo(currency);
    }

    @Test
    public void shouldNotHaveNullFunctionalCurrency() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new Company("US", null));
    }

    @ParameterizedTest
    @ValueSource(strings = { "US", "ES" })
    public void shouldHaveHomeCountry(String countryCode) {
        assertThat(new Company(countryCode, usd()).getHomeCountry()).isEqualTo(countryCode);
    }

    @Test
    public void companyShouldHaveNullCountry() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new Company(null, usd()));
    }

    @Test
    public void companyShouldHaveInvalidCountryCode() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new Company("AB", usd()));
    }

    private Currency usd() {
        return Currency.getInstance("USD");
    }
}