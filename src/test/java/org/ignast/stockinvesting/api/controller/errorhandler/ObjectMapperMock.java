package org.ignast.stockinvesting.api.controller.errorhandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ObjectMapperMock {

    public static ObjectMapper writingObjectsToString() throws JsonProcessingException {
        ObjectMapper mapper = mock(ObjectMapper.class);
        when(mapper.writeValueAsString(notNull())).thenReturn("any");
        return mapper;
    }

    public static ObjectMapper failingToWriteObjectToString() throws JsonProcessingException {
        ObjectMapper mapper = mock(ObjectMapper.class);
        when(mapper.writeValueAsString(notNull())).thenThrow(JsonProcessingException.class);
        return mapper;
    }
}

class ObjectMapperMockTest {
    @Test
    public void shouldWriteObjectToString() throws JsonProcessingException {
        ObjectMapper mapper = ObjectMapperMock.writingObjectsToString();

        assertThat(mapper.writeValueAsString(new Object())).isEqualTo("any");
    }

    @Test
    public void shouldNotActivateWritingBehaviourIfNullIsPassed() throws JsonProcessingException {
        ObjectMapper mapper = ObjectMapperMock.writingObjectsToString();

        assertThat(mapper.writeValueAsString(null)).isNull();
    }

    @Test
    public void shouldFailToWriteObjectToString() throws JsonProcessingException {
        ObjectMapper mapper = ObjectMapperMock.failingToWriteObjectToString();

        assertThatExceptionOfType(JsonProcessingException.class)
                .isThrownBy(() -> mapper.writeValueAsString(new Object()));
    }
}
