package org.ignast.stockinvesting.quotes.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public final class StockSymbolTest {

    @Test
    public void shouldNotBeNull() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new StockSymbol(null));
    }

    @ParameterizedTest
    @ValueSource(strings = { "AMZN", "MSFT" })
    public void shouldPreserveCode(final String symbol) {
        assertThat(new StockSymbol(symbol).get()).isEqualTo(symbol);
    }

    @Test
    public void shouldNotBeEmpty() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new StockSymbol(""))
            .withMessage("Stock Symbol must contain between 1-6 characters");
        assertThat(new StockSymbol("A").get()).isEqualTo("A");
    }

    @Test
    public void shouldRejectLongerThan6() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new StockSymbol("ABCDEFG"))
            .withMessage("Stock Symbol must contain between 1-6 characters");
        assertThat(new StockSymbol("ABCDEF").get()).isEqualTo("ABCDEF");
    }

    @ParameterizedTest
    @ValueSource(strings = { "1Aa", "Ñ1A" })
    public void shouldRejectNonAlphanumericUppercaseCharacters(final String symbol) {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new StockSymbol(symbol))
            .withMessage("Stock Symbol must contain only uppercase alphanumeric characters");
    }

    @ParameterizedTest
    @ValueSource(strings = { "11", "AA", "2B", "C3" })
    public void shouldAcceptUppercaseAlphanumeric(final String symbol) {
        assertThat(new StockSymbol(symbol).get()).isEqualTo(symbol);
    }

    @Test
    public void shouldBeEqualToSameStockSymbol() {
        EqualsVerifier.forClass(StockSymbol.class).verify();
    }
}
