package org.ignast.stockinvesting.testutil.api;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT;

public final class JsonAssert {
    private final String actualJson;

    private final JSONCompareMode comparisonMode;

    public JsonAssert(final String actualJson, final JSONCompareMode strict) {
        this.actualJson = actualJson;
        comparisonMode = strict;
    }

    public static JsonAssert assertThatJson(final String actualJson) {
        return new JsonAssert(actualJson, STRICT);
    }

    public void isEqualTo(final String expectedJson) throws JSONException {
        JSONAssert.assertEquals(expectedJson, actualJson, comparisonMode);
    }
}

