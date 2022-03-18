package org.ignast.stockinvesting.testutil;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Consumer;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public final class MockitoUtils {

    private MockitoUtils() {}

    public static <T> T mock(final Class<? extends T> classToMock, final Consumer<T> customizer) {
        final T mock = Mockito.mock(classToMock);
        customizer.accept(mock);
        return mock;
    }
}

final class MockitoUtilsTest {

    @Test
    public void shouldCreateEmptyMock() {
        final val machine = MockitoUtils.mock(TicketMachine.class, m -> {});

        assertThat(machine.getTicked()).isNull();
    }

    @Test
    public void shouldCustomizeMock() {
        final val machine = MockitoUtils.mock(
            TicketMachine.class,
            m -> Mockito.when(m.getTicked()).thenReturn("bbb")
        );

        assertThat(machine.getTicked()).isEqualTo("bbb");
    }

    private static final class TicketMachine {

        public String getTicked() {
            return "aaa";
        }
    }
}
