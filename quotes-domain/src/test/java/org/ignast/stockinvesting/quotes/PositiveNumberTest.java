package org.ignast.stockinvesting.quotes;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class PositiveNumberTest {

    @Test
    public void shouldNotBeNull() {
        Integer id = null;
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new PositiveNumber(id));
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 2000000 })
    public void shouldPreserveCode(Integer id) {
        assertThat(new PositiveNumber(id).get()).isEqualTo(id);
    }

    @Test
    public void shouldHaveStringRepresentation() {
        assertThat(new PositiveNumber(4).toString()).contains("4");
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, -1, -2000000} )
    public void shouldNotBeNegativeOrZero(Integer id) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new PositiveNumber(id))
                .withMessage("Must be positive");
    }

    @Test
    public void shouldEqualToTheSameNumericId() {
        EqualsVerifier.forClass(PositiveNumber.class).verify();
    }
}