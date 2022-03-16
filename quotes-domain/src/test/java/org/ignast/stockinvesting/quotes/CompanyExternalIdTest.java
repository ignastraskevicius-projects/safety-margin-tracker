package org.ignast.stockinvesting.quotes;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class CompanyExternalIdTest {

    @Test
    public void shouldNotBeNull() {
        Integer id = null;
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new CompanyExternalId(id));
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 2000000 })
    public void shouldPreserveCode(Integer id) {
        assertThat(new CompanyExternalId(id).get()).isEqualTo(id);
    }

    @Test
    public void shouldHaveStringRepresentation() {
        assertThat(new CompanyExternalId(4).toString()).contains("4");
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, -1, -2000000} )
    public void shouldNotBeNegativeOrZero(Integer id) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new CompanyExternalId(id))
                .withMessage("Must be positive");
    }

    @Test
    public void shouldEqualToTheSameNumericId() {
        EqualsVerifier.forClass(CompanyExternalId.class).verify();
    }
}