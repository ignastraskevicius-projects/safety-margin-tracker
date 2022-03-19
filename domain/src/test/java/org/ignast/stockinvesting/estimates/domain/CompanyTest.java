package org.ignast.stockinvesting.estimates.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.Currency;
import lombok.val;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> new Company("Amazon", "US", null));
    }

    @ParameterizedTest
    @ValueSource(strings = { "US", "ES" })
    public void shouldHaveHomeCountry(final String countryCode) {
        assertThat(new Company("Amazon", countryCode, usd()).getHomeCountry()).isEqualTo(countryCode);
    }

    @Test
    public void companyShouldNotHaveNullCountry() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> new Company("Amazon", null, usd()));
    }

    @Test
    public void companyShouldHaveNullName() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> new Company(null, "US", usd()));
    }

    private Currency usd() {
        return Currency.getInstance("USD");
    }
}
