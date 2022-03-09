package org.ignast.stockinvesting.quotes.util.test.api;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.hamcrest.TypeSafeMatcher;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HateoasJsonMatchers {
    public static HasRel hasRel(String relName) {
        return new HasRel(relName);
    }

    public static class HasRel {
        private final String relName;

        public HasRel(String relName) {
            this.relName = relName;
        }

        public TypeSafeMatcher<String> withHrefContaining(String hrefSubstring) {
            return new ExistsRelWithHrefContainingString(relName, hrefSubstring);
        }

        static class ExistsRelWithHrefContainingString extends TypeSafeMatcher<String> {
            private String relName;
            private String hrefSubstring;

            public ExistsRelWithHrefContainingString(String relName, String hrefSubstring) {
                this.relName = relName;
                this.hrefSubstring = hrefSubstring;
            }

            @Override
            protected boolean matchesSafely(String hateoasJson) {
                try {
                    return new JSONObject(hateoasJson).getJSONObject("_links").getJSONObject(relName).getString("href")
                            .contains(hrefSubstring);
                } catch (JSONException e) {
                    return false;
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(
                        String.format("HATEOAS json should contain a '%s' rel with a href containing substring '%s'",
                                relName, hrefSubstring));
            }
        }
    }
}

class HateoasJsonMatcheresTest {
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
