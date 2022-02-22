package org.ignast.stockinvesting.api.fluentjsonassert;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class JsonAssert {
    private String actualJson;

    public JsonAssert(String actualJson) {
        this.actualJson = actualJson;
    }

    public static JsonAssert assertThatJson(String actualJson) {
        return new JsonAssert(actualJson);
    }

    public void isEqualTo(String expectedJson) throws JSONException {
        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.STRICT);
    }
}

class JsonAssertTest {
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