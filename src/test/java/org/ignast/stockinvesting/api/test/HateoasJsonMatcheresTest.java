package org.ignast.stockinvesting.api.test;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.hamcrest.TypeSafeMatcher;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.api.test.HateoasJsonMatchers.hasRel;

public class HateoasJsonMatcheresTest {
    @Test
    public void shouldNotMatchJsonWithoutHateoasLinks() {
        String testJson = "{}";
        StringDescription desc = new StringDescription();
        Matcher<String> matcher = hasRel("link:link").withHrefContaining("contextPath");

        assertThat(matcher.matches(testJson)).isFalse();
        matcher.describeTo(desc);
        matcher.describeMismatch(testJson, desc);

        assertThat(desc.toString()).contains("HATEOAS json should contain a 'link:link' rel with a href containing substring 'contextPath'").contains(testJson);
    }

    @Test
    public void shouldNotMatchJsonWithoutSpecifiedLinks() {
        TypeSafeMatcher<String> matcher = HateoasJsonMatchers.hasRel("link:link").withHrefContaining("contextPath");

        assertThat(matcher.matches("{\"_links\":{}}")).isFalse();
    }

    @Test
    public void shouldNotMatchJsonHavingRelWithoutHref() {
        TypeSafeMatcher<String> matcher = HateoasJsonMatchers.hasRel("link:link").withHrefContaining("contextPath");

        assertThat(matcher.matches("{\"_links\":{\"link:link\":{}}}")).isFalse();
    }

    @Test
    public void shouldNotMatchHrefsNotContainingRequiredSubstring() {
        TypeSafeMatcher<String> matcher = HateoasJsonMatchers.hasRel("link:link").withHrefContaining("contextPath");

        assertThat(matcher.matches("{\"_links\":{\"link:link\":{\"href\":\"http://example.uri.com\"}}}")).isFalse();
    }

    @Test
    public void shouldMatchJsonWhereRelIsPresentAndHrefContainsRequiredString() {
        TypeSafeMatcher<String> matcher = HateoasJsonMatchers.hasRel("link:link").withHrefContaining("contextPath");

        assertThat(matcher.matches("{\"_links\":{\"link:link\":{\"href\":\"http://example.uri.com/contextPath\"}}}")).isTrue();
    }
}
