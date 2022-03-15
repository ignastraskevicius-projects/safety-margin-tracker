package org.ignast.stockinvesting.testutil.api;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.hamcrest.TypeSafeMatcher;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HateoasJsonMatchersForRelAndHrefTest {
    private Matcher<String> matcher = HateoasJsonMatchers.hasRel("link:link").withHrefContaining("contextPath");

    @Test
    public void messageShouldIndicateExpectationAndActualOutcome() {
        String testJson = "{}";
        StringDescription desc = new StringDescription();

        matcher.describeTo(desc);
        matcher.describeMismatch(testJson, desc);

        assertThat(desc.toString())
                .contains(
                        "HATEOAS json should contain a 'link:link' rel with a href containing substring 'contextPath'")
                .contains(testJson);
    }

    @Test
    public void shouldNotMatchJsonWithoutHateoasLinks() {
        assertThat(matcher.matches("{}")).isFalse();
    }

    @Test
    public void shouldNotMatchJsonWithoutSpecifiedLinks() {
        assertThat(matcher.matches("{\"_links\":{}}")).isFalse();
    }

    @Test
    public void shouldNotMatchJsonHavingRelWithoutHref() {
        assertThat(matcher.matches("{\"_links\":{\"link:link\":{}}}")).isFalse();
    }

    @Test
    public void shouldNotMatchHrefsNotContainingRequiredSubstring() {
        assertThat(matcher.matches("{\"_links\":{\"link:link\":{\"href\":\"http://example.uri.com\"}}}")).isFalse();
    }

    @Test
    public void shouldMatchJsonWhereRelIsPresentAndHrefContainsRequiredString() {
        assertThat(matcher.matches("{\"_links\":{\"link:link\":{\"href\":\"http://example.uri.com/contextPath\"}}}"))
                .isTrue();
    }
}

class HateoasJsonMatchersForRelTest {
    private Matcher<String> matcher = HateoasJsonMatchers.hasRel("link:link").withHref();

    @Test
    public void messageShouldIndicateExpectationAndActualOutcome() {
        String testJson = "{}";
        StringDescription desc = new StringDescription();

        matcher.describeTo(desc);
        matcher.describeMismatch(testJson, desc);

        assertThat(desc.toString())
                .contains(
                        "HATEOAS json should contain a 'link:link' rel with a href")
                .contains(testJson);
    }

    @Test
    public void shouldNotMatchJsonWithoutHateoasLinks() {
        assertThat(matcher.matches("{}")).isFalse();
    }

    @Test
    public void shouldNotMatchJsonWithoutSpecifiedLinks() {
        assertThat(matcher.matches("{\"_links\":{}}")).isFalse();
    }

    @Test
    public void shouldNotMatchJsonHavingRelWithoutHref() {
        assertThat(matcher.matches("{\"_links\":{\"link:link\":{}}}")).isFalse();
    }

    @Test
    public void shouldMatchAnyHrefs() {
        assertThat(matcher.matches("{\"_links\":{\"link:link\":{\"href\":\"any\"}}}")).isTrue();
    }

}
