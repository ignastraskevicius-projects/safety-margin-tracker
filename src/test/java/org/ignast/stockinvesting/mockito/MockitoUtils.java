package org.ignast.stockinvesting.mockito;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class MockitoUtils {

    public static <T> T mock(Class<? extends T> classToMock, Consumer<T> customizer) {
        T mock = Mockito.mock(classToMock);
        customizer.accept(mock);
        return mock;
    }
}

class MockitoUtilsTest {
    @Test
    public void shouldCreateEmptyMock() {
        TicketMachine machine = MockitoUtils.mock(TicketMachine.class, m -> {
        });

        assertThat(machine.getTicked()).isNull();
    }

    @Test
    public void shouldCustomizeMock() {
        TicketMachine machine = MockitoUtils.mock(TicketMachine.class, m -> when(m.getTicked()).thenReturn("bbb"));

        assertThat(machine.getTicked()).isEqualTo("bbb");
    }

    class TicketMachine {
        public String getTicked() {
            return "aaa";
        }
    }
}