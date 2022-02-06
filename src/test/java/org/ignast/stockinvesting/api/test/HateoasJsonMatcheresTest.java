package org.ignast.stockinvesting.api.test;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.api.test.HateoasJsonMatchers.hasRel;

public class HateoasJsonMatcheresTest {
    private Matcher<String> matcher = hasRel("link:link").withHrefContaining("contextPath");

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
