package org.ignast.stockinvesting.testutil.api;

import lombok.val;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.test.web.servlet.ResultMatcher;

import java.nio.charset.StandardCharsets;

public final class NonExtensibleContentMatchers {
    public static ResultMatcher bodyMatchesJson(final String expectedJson) {
        return (result) -> {
            final val actualJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
            JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.NON_EXTENSIBLE);
        };
    }

    public static ResultMatcher resourceContentMatchesJson(final String expectedJson) {
        return (result) -> {
            final val expected = new JSONObject(expectedJson);
            final val actualJson = new JSONObject(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
            actualJson.remove("_links");
            JSONAssert.assertEquals(expected, actualJson, JSONCompareMode.NON_EXTENSIBLE);
        };
    }

    public static ResultMatcher resourceLinksMatchesJson(final String expectedJson) {
        return (result) -> {
            final val expected = new JSONObject(expectedJson);
            final val actualJson = new JSONObject(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
            final val actualJsonLinksOnly = new JSONObject();
            actualJsonLinksOnly.put("_links", actualJson.get("_links"));
            JSONAssert.assertEquals(expected, actualJsonLinksOnly, JSONCompareMode.NON_EXTENSIBLE);
        };
    }
}
