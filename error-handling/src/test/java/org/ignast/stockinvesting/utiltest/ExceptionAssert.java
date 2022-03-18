package org.ignast.stockinvesting.utiltest;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.utiltest.ExceptionAssert.assertThatNullPointerExceptionIsThrownBy;

import java.util.stream.Stream;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

public final class ExceptionAssert {

    private ExceptionAssert() {}

    public static void assertThatNullPointerExceptionIsThrownBy(
        final ThrowableAssert.ThrowingCallable... callables
    ) {
        Stream
            .of(callables)
            .forEach(c -> assertThatExceptionOfType(NullPointerException.class).isThrownBy(c));
    }
}

final class ExceptionAssertTest {

    @Test
    public void shouldAssertIfNoCallablesGiven() {
        assertThatNullPointerExceptionIsThrownBy();
    }

    @Test
    public void shouldAssertNullPointerExceptionIsThrown() {
        assertThatNullPointerExceptionIsThrownBy(() -> {
            throw new NullPointerException();
        });
    }

    @Test
    public void shouldAssertIfAllCallablesThrowTheException() {
        assertThatNullPointerExceptionIsThrownBy(
            () -> {
                throw new NullPointerException();
            },
            () -> {
                throw new NullPointerException();
            }
        );
    }

    @Test
    public void shouldFailToAssertIfSomeCallablesDoNotThrowTheException() {
        shouldFailToAssertForGivenCallables(
            () -> {
                throw new NullPointerException();
            },
            () -> {}
        );
    }

    @Test
    public void shouldFailToAssertIfSomeCallablesDoThrowDifferentException() {
        shouldFailToAssertForGivenCallables(
            () -> {
                throw new IllegalStateException();
            },
            () -> {
                throw new NullPointerException();
            }
        );
    }

    @Test
    public void shouldFailTotAssertIfOtherTypeOfExceptionIsThrown() {
        shouldFailToAssertForGivenCallables(() -> {
            throw new IllegalArgumentException();
        });
        shouldFailToAssertForGivenCallables(() -> {
            throw new IllegalStateException();
        });
    }

    private void shouldFailToAssertForGivenCallables(final ThrowableAssert.ThrowingCallable... callables) {
        assertThatExceptionOfType(AssertionError.class)
            .isThrownBy(() -> assertThatNullPointerExceptionIsThrownBy(callables));
    }
}
