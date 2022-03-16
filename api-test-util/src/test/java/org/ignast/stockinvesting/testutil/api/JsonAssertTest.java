package org.ignast.stockinvesting.testutil.api;

import org.json.JSONException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class StrictJsonAssertTest {
    @Test
    public void shouldAssertEqualsIfAttributeOrderIsTheSame() throws JSONException {
        JsonAssert.assertThatJson("{\"a\":\"valueA\",\"b\":\"valueB\"}")
                .isEqualTo("{\"a\":\"valueA\",\"b\":\"valueB\"}");
    }

    @Test
    public void shouldNotAssertEqualsIfAttributeIsMissing() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(
                () -> JsonAssert.assertThatJson("{\"a\":\"valueA\"}").isEqualTo("{\"a\":\"valueA\",\"b\":\"valueB\"}"));
    }

    @Test
    public void shouldAssertEqualsIfAttributeOrderIsNotTheSame() throws JSONException {
        JsonAssert.assertThatJson("{\"a\":\"valueA\",\"b\":\"valueB\"}")
                .isEqualTo("{\"b\":\"valueB\",\"a\":\"valueA\"}");
    }

    @Test
    public void shouldNotAssertEqualsIfElementOrderIsNotTheSame() {
        assertThatExceptionOfType(AssertionError.class)
                .isThrownBy(() -> JsonAssert.assertThatJson("[\"a\",\"b\"]").isEqualTo("[\"b\",\"a\"]"));
    }

    @Test
    public void shouldNotAssertEqualsIfExtraAttributesAreFound() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(
                () -> JsonAssert.assertThatJson("{\"a\":\"valueA\",\"b\":\"valueB\"}").isEqualTo("{\"a\":\"valueA\"}"));
    }
}