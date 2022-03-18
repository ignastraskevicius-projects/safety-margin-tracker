package org.ignast.stockinvesting.estimates.domain;

import lombok.val;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class CompanyNameTest {

    @Test
    public void shouldNotBeNull() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new CompanyName(null));
    }

    @ParameterizedTest
    @ValueSource(strings = { "Amazon", "Microsoft" })
    public void shouldPreserveName(final String name) {
        assertThat(new CompanyName(name).get()).isEqualTo(name);
    }

    @Test
    public void shouldRejectEmptyName() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new CompanyName(""))
                .withMessage("Company name must be between 1-255 characters");
        new CompanyName("a");
    }

    @Test
    public void shouldRejectTooLongName() {
        final val notTooLongName = "c".repeat(255);
        final val tooLongName = "c".repeat(256);
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new CompanyName(tooLongName))
                .withMessage("Company name must be between 1-255 characters");
        new CompanyName(notTooLongName);
    }

    @Test
    public void shouldEqualToTheSameCurrency() {
        EqualsVerifier.forClass(CompanyName.class).verify();
    }
}