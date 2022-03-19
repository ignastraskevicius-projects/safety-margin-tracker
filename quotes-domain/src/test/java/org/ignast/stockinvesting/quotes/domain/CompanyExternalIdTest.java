package org.ignast.stockinvesting.quotes.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public final class CompanyExternalIdTest {

    private static final int SOME_BIG_NUMBER = 2000000;

    @Test
    public void shouldNotBeNull() {
        final Integer id = null;
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new CompanyExternalId(id));
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, SOME_BIG_NUMBER })
    public void shouldPreserveCode(final Integer id) {
        assertThat(new CompanyExternalId(id).get()).isEqualTo(id);
    }

    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void shouldHaveStringRepresentation() {
        assertThat(new CompanyExternalId(4).toString()).contains("4");
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, -1, -SOME_BIG_NUMBER })
    public void shouldNotBeNegativeOrZero(final Integer id) {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new CompanyExternalId(id))
            .withMessage("Must be positive");
    }

    @Test
    public void shouldEqualToTheSameExternalId() {
        EqualsVerifier.forClass(CompanyExternalId.class).verify();
    }
}
