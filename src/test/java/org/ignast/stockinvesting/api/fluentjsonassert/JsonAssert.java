package org.ignast.stockinvesting.api.fluentjsonassert;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.skyscreamer.jsonassert.JSONCompareMode.LENIENT;
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT;

public class JsonAssert {
    private String actualJson;
    private JSONCompareMode comparisonMode;

    public JsonAssert(String actualJson, JSONCompareMode strict) {
        this.actualJson = actualJson;
        comparisonMode = strict;
    }

    public static JsonAssert assertThatJson(String actualJson) {
        return new JsonAssert(actualJson, STRICT);
    }

    public static JsonAssert lenientAssertThatJson(String actualJson) {
        return new JsonAssert(actualJson, LENIENT);
    }

    public void isEqualTo(String expectedJson) throws JSONException {
        JSONAssert.assertEquals(expectedJson, actualJson, comparisonMode);
    }
}

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

class LenientJsonAssertTest {
    @Test
    public void shouldAssertEqualsIfAttributeOrderIsTheSame() throws JSONException {
        JsonAssert.lenientAssertThatJson("{\"a\":\"valueA\",\"b\":\"valueB\"}")
                .isEqualTo("{\"a\":\"valueA\",\"b\":\"valueB\"}");
    }

    @Test
    public void shouldNotAssertEqualsIfAttributeIsMissing() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> JsonAssert
                .lenientAssertThatJson("{\"a\":\"valueA\"}").isEqualTo("{\"a\":\"valueA\",\"b\":\"valueB\"}"));
    }

    @Test
    public void shouldAssertEqualsIfAttributeOrderIsNotTheSame() throws JSONException {
        JsonAssert.lenientAssertThatJson("{\"a\":\"valueA\",\"b\":\"valueB\"}")
                .isEqualTo("{\"b\":\"valueB\",\"a\":\"valueA\"}");
    }

    @Test
    public void shouldAssertEqualsIfElementOrderIsNotTheSame() throws JSONException {
        JsonAssert.lenientAssertThatJson("[\"a\",\"b\"]").isEqualTo("[\"b\",\"a\"]");
    }

    @Test
    public void shouldAssertEqualsIfExtraAttributesAreFound() throws JSONException {
        JsonAssert.lenientAssertThatJson("{\"a\":\"valueA\",\"b\":\"valueB\"}").isEqualTo("{\"a\":\"valueA\"}");
    }
}