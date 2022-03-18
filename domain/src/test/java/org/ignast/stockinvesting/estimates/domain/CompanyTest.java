package org.ignast.stockinvesting.estimates.domain;

import lombok.val;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class CompanyTest {
    @Test
    public void shouldBeEqual() {
        EqualsVerifier.forClass(Company.class).suppress(Warning.SURROGATE_KEY).verify();
    }

    @ParameterizedTest
    @ValueSource(strings = { "EUR", "USD" })
    public void shouldHaveFunctionalCurrency(final String currencyCode) {
        final val currency = Currency.getInstance(currencyCode);
        assertThat(new Company("Amazon", "US", currency).getFunctionalCurrency()).isEqualTo(currency);
    }

    @Test
    public void shouldNotHaveNullFunctionalCurrency() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new Company("Amazon", "US", null));
    }

    @ParameterizedTest
    @ValueSource(strings = { "US", "ES" })
    public void shouldHaveHomeCountry(final String countryCode) {
        assertThat(new Company("Amazon", countryCode, usd()).getHomeCountry()).isEqualTo(countryCode);
    }

    @Test
    public void companyShouldHaveNullCountry() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new Company("Amazon", null, usd()));
    }

    @Test
    public void companyShouldHaveInvalidCountryCode() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new Company("Amazon", "AB", usd()));
    }

    @Test
    public void shouldHaveNeitherTooLongNorEmptyName() {
        assertThat(new Company("Amazon", "US", usd()).getName()).isEqualTo("Amazon");
    }

    @Test
    public void companyShouldHaveNullName() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new Company(null, "US", usd()));
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 255 })
    public void shouldHaveNeitherTooLongNorEmptyName(final int boundaryValidLength) {
        final val name = "c".repeat(boundaryValidLength);
        assertThat(new Company(name, "US", usd()).getName()).isEqualTo(name);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 256 })
    public void companyNameShouldNotBeEmpty(final int boundaryInvalidLength) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Company("c".repeat(boundaryInvalidLength), "US", usd()));
    }

    private Currency usd() {
        return Currency.getInstance("USD");
    }
}