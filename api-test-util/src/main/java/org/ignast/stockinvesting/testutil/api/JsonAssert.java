package org.ignast.stockinvesting.testutil.api;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

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

    public void isEqualTo(String expectedJson) throws JSONException {
        JSONAssert.assertEquals(expectedJson, actualJson, comparisonMode);
    }
}

