package org.ignast.stockinvesting.testutil;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

public final class MockitoUtils {

    public static <T> T mock(final Class<? extends T> classToMock, final Consumer<T> customizer) {
        final T mock = Mockito.mock(classToMock);
        customizer.accept(mock);
        return mock;
    }
}

final class MockitoUtilsTest {
    @Test
    public void shouldCreateEmptyMock() {
        final val machine = MockitoUtils.mock(TicketMachine.class, m -> {
        });

        assertThat(machine.getTicket()).isNull();
    }

    @Test
    public void shouldCustomizeMock() {
        final val machine = MockitoUtils.mock(TicketMachine.class, m -> Mockito.when(m.getTicket()).thenReturn("bbb"));

        assertThat(machine.getTicket()).isEqualTo("bbb");
    }

    private static final class TicketMachine {
        public String getTicket() {
            return "aaa";
        }
    }
}