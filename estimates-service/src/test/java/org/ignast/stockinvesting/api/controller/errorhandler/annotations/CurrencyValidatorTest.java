package org.ignast.stockinvesting.api.controller.errorhandler.annotations;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CurrencyValidatorTest {
    @Test
    public void shouldBeValidForNullCurrencyCode() {
        assertThat(new CurrencyValidator().isValid(null, null)).isTrue();
    }

    @Test
    public void shouldNotBeValidForNonexistentCurrencyCode() {
        assertThat(new CurrencyValidator().isValid("ABC", null)).isFalse();
    }

    @Test
    public void shouldBeValidForExistingCurrencyCode() {
        assertThat(new CurrencyValidator().isValid("USD", null)).isTrue();
    }
}