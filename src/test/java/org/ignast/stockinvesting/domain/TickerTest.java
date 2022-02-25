package org.ignast.stockinvesting.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class TickerTest {

    @Test
    public void shouldNotBeNull() {
        Assertions.assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new Ticker(null));
    }

    @ParameterizedTest
    @ValueSource(strings = { "AMZN", "MSFT" })
    public void shouldPreserveCode(String ticker) {
        assertThat(new Ticker(ticker).get()).isEqualTo(ticker);
    }

    @Test
    public void shouldNotBeEmpty() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new Ticker(""));
        assertThat(new Ticker("A").get()).isEqualTo("A");
    }

    @Test
    public void shouldRejectLongerThan5() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new Ticker("ABCDEF"));
        assertThat(new Ticker("ABCDE").get()).isEqualTo("ABCDE");
    }

    @ParameterizedTest
    @ValueSource(strings = { "1Aa", "Ñ1A" })
    public void shouldRejectNonAlphanumericUppercaseCharacters(String ticker) {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new Ticker(ticker));
    }

    @ParameterizedTest
    @ValueSource(strings = { "11", "AA", "2B", "C3" })
    public void shouldAcceptUppercaseAlphanumeric(String ticker) {
        assertThat(new Ticker(ticker).get()).isEqualTo(ticker);
    }
}