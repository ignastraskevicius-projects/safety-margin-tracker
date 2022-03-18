package org.ignast.stockinvesting.estimates.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class CountryCodeTest {
    @Test
    public void shouldNotBeNull() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new CountryCode(null));
    }

    @ParameterizedTest
    @ValueSource(strings = { "US", "FR" })
    public void shouldPreserveCode(final String countryCode) {
        assertThat(new CountryCode(countryCode).get()).isEqualTo(countryCode);
    }

    @Test
    public void shouldRejectNon2Character() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new CountryCode("ABC"))
                .withMessage("Must consist of 2 characters");
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new CountryCode("A"))
                .withMessage("Must consist of 2 characters");
    }

    @ParameterizedTest
    @ValueSource(strings = { "Aa", "1A", "AÑ" })
    public void shouldRejectNonLatinUppercaseCharacters(final String countryCode) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new CountryCode(countryCode)).withMessage(
                        "Must contain only uppercase latin characters");
    }

    @Test
    public void shouldRejectNonISO3166alpha2Codes() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new CountryCode("AB").get()).withMessage("Must be a valid ISO 3166 alpha-2 code");
    }
}