package org.ignast.stockinvesting.api.controller.errorhandler.annotations;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CountryCodeValidatorTest {
    private final CountryCodeValidator validator = new CountryCodeValidator();

    @Test
    public void shouldBeValidForNullCountries() {
        assertThat(validator.isValid(null, null)).isTrue();
    }

    @Test
    public void shouldBeInvalidForNotExistingCountryCode() {
        assertThat(validator.isValid("AB", null)).isFalse();
    }

    @Test
    public void shouldBeValidForExistingCountryCode() {
        assertThat(validator.isValid("US", null)).isTrue();
    }
}