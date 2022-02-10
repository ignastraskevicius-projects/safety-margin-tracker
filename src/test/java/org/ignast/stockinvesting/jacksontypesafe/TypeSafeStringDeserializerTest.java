package org.ignast.stockinvesting.jacksontypesafe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import static org.assertj.core.api.Assertions.*;

@JsonTest
class TypeSafeStringDeserializerTest {

    private ObjectMapper mapper;

    @BeforeEach
    public void setup() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(String.class, new TypeSafeStringDeserializer());
        mapper = new ObjectMapper().registerModule(module);
    }

    @Test
    public void shouldReadJsonString() throws JsonProcessingException {
        assertThat(mapper.readValue("\"someStringValue\"", String.class)).isEqualTo("someStringValue");
    }

    @Test
    public void failureToReadShouldBeDueToStrictParsing() {
        Throwable throwable = catchThrowable(() -> {
            mapper.readValue("3", String.class);
        });

        assertThat(throwable).isExactlyInstanceOf(StrictStringParsingException.class)
                .isInstanceOf(StrictParsingException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = { "3", "3.3", "true", "false", "null" })
    public void shouldFailToReadOtherJsonScalars(String scalar) throws JsonProcessingException {
        assertThatExceptionOfType(StrictStringParsingException.class).isThrownBy(() -> {
            mapper.readValue(scalar, String.class);
        });
    }

    @Test
    public void shouldFailToReadJsonFloat() throws JsonProcessingException {
        assertThatExceptionOfType(StrictStringParsingException.class).isThrownBy(() -> {
            mapper.readValue("3.3", String.class);
        });
    }
}