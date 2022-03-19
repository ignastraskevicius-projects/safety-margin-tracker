package org.ignast.stockinvesting.testutil.api;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import lombok.val;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

public final class NonExtensibleContentMatchers {

    private static final String LINKS = "_links";

    private NonExtensibleContentMatchers() {}

    public static ResultMatcher bodyMatchesJson(final String expectedJson) {
        return result -> {
            final val actualJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
            JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.NON_EXTENSIBLE);
        };
    }

    public static ResultMatcher resourceContentMatchesJson(final String expectedJson) {
        return result -> {
            final val expected = new JSONObject(expectedJson);
            final val actualJson = new JSONObject(
                result.getResponse().getContentAsString(StandardCharsets.UTF_8)
            );
            actualJson.remove(LINKS);
            JSONAssert.assertEquals(expected, actualJson, JSONCompareMode.NON_EXTENSIBLE);
        };
    }

    @SuppressWarnings("checkstyle:lambdabodylength")
    public static ResultMatcher resourceLinksMatchesJson(final String expectedLinksOnly) {
        return result -> {
            final val expectedJsonLinksOnly = new JSONObject(expectedLinksOnly);
            final val actualJsonLinksOnly = extractLinksOnly(result);
            JSONAssert.assertEquals(
                expectedJsonLinksOnly,
                actualJsonLinksOnly,
                JSONCompareMode.NON_EXTENSIBLE
            );
        };
    }

    private static JSONObject extractLinksOnly(final MvcResult result)
        throws JSONException, UnsupportedEncodingException {
        final val actualJson = new JSONObject(
            result.getResponse().getContentAsString(StandardCharsets.UTF_8)
        );
        final val actualJsonLinksOnly = new JSONObject();
        actualJsonLinksOnly.put(LINKS, actualJson.get(LINKS));
        return actualJsonLinksOnly;
    }
}
