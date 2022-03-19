package org.ignast.stockinvesting.quotes.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.testutil.ExceptionAssert.assertThatNullPointerExceptionIsThrownBy;

import java.math.BigDecimal;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PriceDTOTest {

    private static final BigDecimal TWO = new BigDecimal("2");

    private static final BigDecimal SIX = new BigDecimal("6");

    @Test
    public void shouldNotBeCreatedWithNullArguments() {
        assertThatNullPointerExceptionIsThrownBy(
            () -> new PriceDTO(TWO, null),
            () -> new PriceDTO(null, "USD")
        );
        new PriceDTO(TWO, "USD");
    }

    @ParameterizedTest
    @ValueSource(strings = { "3", "4" })
    public void shouldPreserveAmount(final String amount) {
        final val price = new PriceDTO(new BigDecimal(amount), "any");

        assertThat(price.getAmount()).isEqualTo(amount);
    }

    @ParameterizedTest
    @ValueSource(strings = { "USD", "EUR" })
    public void shouldPreserveCurrency(final String currency) {
        final val price = new PriceDTO(any(), currency);

        assertThat(price.getCurrency()).isEqualTo(currency);
    }

    private BigDecimal any() {
        return SIX;
    }
}
