package org.ignast.stockinvesting.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class MarketIdentifierCodeTest {

    @Test
    public void shouldNotBeNull() {
        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new MarketIdentifierCode(null));
    }

    @ParameterizedTest
    @ValueSource(strings = { "XNYS", "XLON" })
    public void shouldPreserveCode(String mic) {
        assertThat(new MarketIdentifierCode(mic).get()).isEqualTo(mic);
    }

    @Test
    public void shouldRejectNon4CharacterMics() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new MarketIdentifierCode("ABC"))
                .withMessage("Market Identifier is not 4 characters long (ISO 10383 standard)");
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new MarketIdentifierCode("ABCDE"))
                .withMessage("Market Identifier is not 4 characters long (ISO 10383 standard)");
    }

    @ParameterizedTest
    @ValueSource(strings = { "AAAa", "1AAA", "AÑAA" })
    public void shouldRejectNonLatinUppercaseCharacters(String mic) {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new MarketIdentifierCode(mic)).withMessage(
                        "Market Identifier must contain only latin uppercase alphanumeric characters (ISO 10383 standard)");
    }
}