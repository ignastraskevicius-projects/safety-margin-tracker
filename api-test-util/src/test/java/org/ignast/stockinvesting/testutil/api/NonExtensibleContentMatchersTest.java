package org.ignast.stockinvesting.testutil.api;

import lombok.NonNull;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.testutil.MockitoUtils.mock;
import static org.ignast.stockinvesting.testutil.api.MatcherWrapper.actualMatchingExpected;
import static org.ignast.stockinvesting.testutil.api.MvcResultStubs.stubbedBody;
import static org.ignast.stockinvesting.testutil.api.NonExtensibleContentMatchers.bodyMatchesJson;
import static org.ignast.stockinvesting.testutil.api.NonExtensibleContentMatchers.resourceContentMatchesJson;
import static org.ignast.stockinvesting.testutil.api.NonExtensibleContentMatchers.resourceLinksMatchesJson;
import static org.mockito.Mockito.when;

final class NonExtensibleContentMatchersTest {

    private final MatcherWrapper matcher = MatcherWrapper.actualMatchingExpected(this::actualMatchingExpected);

    private void actualMatchingExpected(final String actual, final String expected) {
        try {
            bodyMatchesJson(expected).match(stubbedBody(actual));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            new RuntimeException(e);
        }
    }

    @Test
    public void shouldVerifySameJson() {
        matcher.assertThat("{\"list\":[1,2,3]}").matches("{\"list\":[1,2,3]}");
    }

    @Test
    public void shouldFailToVerifysonWithExtraFields() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() ->
                matcher.assertThat("{\"A\":\"a\",\"B\":\"b\"}").matches("{\"A\":\"a\"}"));
    }

    @Test
    public void shouldVerifyJsonWithUnorderedFields() {
        matcher.assertThat("{\"list\":[1,2,3]}").matches("{\"list\":[1,3,2]}");
    }
}

final class NonExtensibleEntityContentMatchersTest {
    private final MatcherWrapper matcher = MatcherWrapper.actualMatchingExpected(this::actualMatchingExpected);

    private void actualMatchingExpected(final String actual, final String expected) {
        try {
            resourceContentMatchesJson(expected).match(stubbedBody(actual));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void shouldFailIfNotJsonObject() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> matcher.assertThat("not-json").matches("{\"list\":[1,2,3]}"))
                .withRootCauseInstanceOf(JSONException.class).havingRootCause().withMessageContaining("not-json");
    }

    @Test
    public void shouldVerifyJsonDiscardingLinksField() {
        matcher.assertThat("{\"list\":[1,2,3],\"_links\":{}}").matches("{\"list\":[1,2,3]}");
    }

    @Test
    public void shouldVerifySameJson() {
        matcher.assertThat("{\"list\":[1,2,3]}").matches("{\"list\":[1,2,3]}");
    }

    @Test
    public void shouldFailToVerifyJsonWithExtraFields() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() ->
                matcher.assertThat("{\"A\":\"a\",\"B\":\"b\"}").matches("{\"A\":\"a\"}"));
    }

    @Test
    public void shouldVerifyJsonWithUnorderedFields() {
        matcher.assertThat("{\"list\":[1,2,3]}").matches("{\"list\":[1,3,2]}");
    }
}

final class NonExtensibleEntityLinksMatchersTest {
    private final MatcherWrapper matcher = MatcherWrapper.actualMatchingExpected(this::actualMatchingExpected);

    private void actualMatchingExpected(final String actual, final String expected) {
        try {
            resourceLinksMatchesJson(expected).match(stubbedBody(actual));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void shouldFailIfNotJsonObject() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> matcher.assertThat("not-json").matches("{\"list\":[1,2,3]}"))
                .withRootCauseInstanceOf(JSONException.class).havingRootCause().withMessageContaining("not-json");
    }

    @Test
    public void shouldVerifyJsonComparingOnlyLinksField() {
        matcher.assertThat("{\"list\":[1,2,3],\"_links\":{\"company\":{\"href\":\"someValue\"}}}").matches("{\"_links\":{\"company\":{\"href\":\"someValue\"}}}");
    }

    @Test
    public void shouldVerifySameJson() {
        matcher.assertThat("{\"_links\":{\"company\":{\"href\":\"someValue\"}}}").matches("{\"_links\":{\"company\":{\"href\":\"someValue\"}}}");
    }

    @Test
    public void shouldFailToVerifyJsonWithExtraFields() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() ->
                matcher.assertThat("{\"_links\":{\"company\":\"A\",\"extra\":\"field\"}}").matches("{\"_links\":{\"company\":\"A\"}}"));
    }

    @Test
    public void shouldVerifyJsonWithUnorderedFields() {
        matcher.assertThat("{\"_links\":[1,2,3]}").matches("{\"_links\":[1,3,2]}");
    }
}

final class MvcResultStubs {
    static MvcResult stubbedBody(final String content) {
        final val response = mock(MockHttpServletResponse.class, re -> when(getUtf8Content(re)).thenReturn(content));
        return mock(MvcResult.class, r -> when(r.getResponse()).thenReturn(response));
    }

    private static String getUtf8Content(final MockHttpServletResponse m) {
        try {
            return m.getContentAsString(StandardCharsets.UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}

final class MvcResultStubsTest {
    @Test
    public void shouldReturnUtf8Content() throws UnsupportedEncodingException {
        Assertions.assertThat(stubbedBody("abc").getResponse().getContentAsString(StandardCharsets.UTF_8)).isEqualTo("abc");
    }
}

final class MatcherWrapper {
    private final BiConsumer<String, String> underlyingMatcherActualExpected;

    private MatcherWrapper(@NonNull final BiConsumer<String, String> underlyingMatcherActualExpected) {
        this.underlyingMatcherActualExpected = underlyingMatcherActualExpected;
    }

    public static MatcherWrapper actualMatchingExpected(final BiConsumer<String, String> underlyingMatcherActualExpected) {
        return new MatcherWrapper(underlyingMatcherActualExpected);
    }

    public MatcherPerformer assertThat(final String actual) {
        return new MatcherPerformer(underlyingMatcherActualExpected, actual);
    }

    final static class MatcherPerformer {
        private final BiConsumer<String, String> underlyingMatcherActualExpected;

        private final String actual;

        private MatcherPerformer(final BiConsumer<String, String> underlyingMatcherActualExpected, final String actual) {
            this.underlyingMatcherActualExpected = underlyingMatcherActualExpected;
            this.actual = actual;
        }

        public void matches(final String expected) {
            underlyingMatcherActualExpected.accept(actual, expected);
        }
    }
}

final class MatcherWrapperTest {

    @Test
    public void shouldNotInitiateWithNullMatcher() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> actualMatchingExpected(null));
    }

    @Test
    public void shouldAssertWhenUnderlyingMatcherAsserts() {
        actualMatchingExpected((a, b) -> noException()).assertThat("A").matches("A");
    }

    private void noException() {
    }

    @Test
    public void shouldFailWhenUnderlyingConditionFails() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> actualMatchingExpected((a, b) -> {
            throw new AssertionError();
        }).assertThat("A").matches("A"));
    }
}