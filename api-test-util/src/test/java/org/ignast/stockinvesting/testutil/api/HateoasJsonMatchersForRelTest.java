package org.ignast.stockinvesting.testutil.api;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.val;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.jupiter.api.Test;

final class HateoasJsonMatchersForRelTest {

    private final Matcher<String> matcher = HateoasJsonMatchers.hasRel("link:link").withHref();

    @Test
    public void messageShouldIndicateExpectationAndActualOutcome() {
        final val testJson = "{}";
        final val desc = new StringDescription();

        matcher.describeTo(desc);
        matcher.describeMismatch(testJson, desc);

        assertThat(desc.toString())
            .contains("HATEOAS json should contain a 'link:link' rel with a href")
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

final class HateoasJsonMatchersForCuriesTest {

    private final Matcher<String> matcher = HateoasJsonMatchers.hasCuries().withHref();

    @Test
    public void messageShouldIndicateExpectationAndActualOutcome() {
        final val testJson = "{}";
        final val desc = new StringDescription();

        matcher.describeTo(desc);
        matcher.describeMismatch(testJson, desc);

        assertThat(desc.toString())
            .contains("HATEOAS json should contain a 'curies' rel with a href")
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
    public void shouldNotMatchJsonHavingZeroCuries() {
        assertThat(matcher.matches("{\"_links\":{\"curies\":[]]}}")).isFalse();
    }

    @Test
    public void shouldNotMatchJsonHavingCuriesWithoutHrefCuries() {
        assertThat(matcher.matches("{\"_links\":{\"curies\":[{}]]}}")).isFalse();
    }

    @Test
    public void shouldMatchAnyHrefs() {
        assertThat(matcher.matches("{\"_links\":{\"curies\":[{\"href\":\"any\"}]}}")).isTrue();
    }
}

final class HateoasJsonMatchersForRelAndHrefTest {

    private final Matcher<String> matcher = HateoasJsonMatchers
        .hasRel("link:link")
        .withHrefContaining("contextPath");

    @Test
    public void messageShouldIndicateExpectationAndActualOutcome() {
        final val testJson = "{}";
        final val desc = new StringDescription();

        matcher.describeTo(desc);
        matcher.describeMismatch(testJson, desc);

        assertThat(desc.toString())
            .contains(
                "HATEOAS json should contain a 'link:link' rel with a href containing substring 'contextPath'"
            )
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
        assertThat(matcher.matches("{\"_links\":{\"link:link\":{\"href\":\"http://example.uri.com\"}}}"))
            .isFalse();
    }

    @Test
    public void shouldMatchJsonWhereRelIsPresentAndHrefContainsRequiredString() {
        assertThat(
            matcher.matches("{\"_links\":{\"link:link\":{\"href\":\"http://example.uri.com/contextPath\"}}}")
        )
            .isTrue();
    }
}
