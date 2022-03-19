package org.ignast.stockinvesting.estimates.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import lombok.val;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CompanyNameTest {

    private static final int MAX_LENGTH_FOUND_IN_UK = 160;

    @Test
    public void shouldNotBeNull() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new CompanyName(null));
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
            .withMessage("Company name must be between 1-160 characters");
        new CompanyName("a");
    }

    @Test
    public void shouldRejectTooLongName() {
        final val notTooLongName = "c".repeat(MAX_LENGTH_FOUND_IN_UK);
        final val tooLongName = "c".repeat(MAX_LENGTH_FOUND_IN_UK + 1);
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new CompanyName(tooLongName))
            .withMessage("Company name must be between 1-160 characters");
        new CompanyName(notTooLongName);
    }

    @Test
    public void shouldEqualToTheSameCurrency() {
        EqualsVerifier.forClass(CompanyName.class).verify();
    }
}
