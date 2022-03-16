package org.ignast.stockinvesting.quotes.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class CurrencyCodeTest {
    @Test
    public void shouldNotBeNull() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new CurrencyCode(null));
    }

    @ParameterizedTest
    @ValueSource(strings = { "USD", "EUR" })
    public void shouldPreserveCode(String currencyCode) {
        assertThat(new CurrencyCode(currencyCode).get()).isEqualTo(currencyCode);
    }

    @Test
    public void shouldRejectNon3Character() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new CurrencyCode("ABCD"))
                .withMessage("Currency must have 3 letters");
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new CurrencyCode("AB"))
                .withMessage("Currency must have 3 letters");
    }

    @ParameterizedTest
    @ValueSource(strings = { "AAa", "1AA", "AÑA" })
    public void shouldRejectNonLatinUppercaseCharacters(String currencyCode) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new CurrencyCode(currencyCode)).withMessage(
                        "Currency must contain only uppercase latin characters");
    }

    @Test
    public void shouldRejectNonISO4217Codes() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new CurrencyCode("AAA").get()).withMessage("Currency must be a valid ISO 4217 code");
    }

    @Test
    public void shouldEqualToTheSameCurrency() {
        EqualsVerifier.forClass(CurrencyCode.class).verify();
    }
}
