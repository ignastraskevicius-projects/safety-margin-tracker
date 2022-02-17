package org.ignast.stockinvesting.api.controller;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.test.web.servlet.ResultMatcher;

import java.nio.charset.StandardCharsets;

public class NonExtensibleContentMatchers {
    public static ResultMatcher contentMatchesJson(String expectedJson) {
        return (result) -> {
            String actualJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
            JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.NON_EXTENSIBLE);
        };
    }
}
