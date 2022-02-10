package org.ignast.stockinvesting.strictjackson;

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
class StrictStringDeserializerTest {

    private ObjectMapper mapper;

    @BeforeEach
    public void setup() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(String.class, new StrictStringDeserializer());
        mapper = new ObjectMapper().registerModule(module);
    }

    @Test
    public void shouldReadJsonString() throws JsonProcessingException {
        assertThat(mapper.readValue("\"someStringValue\"", String.class)).isEqualTo("someStringValue");
    }

    @Test
    public void shouldReadJsonKey() throws JsonProcessingException {
        IntWrapper intWrapper = new IntWrapper();
        intWrapper.intValue = 6;
        assertThat(mapper.readValue("{\"intValue\":6}", IntWrapper.class).intValue).isEqualTo(6);
    }

    @Test
    public void shouldReadJsonStringJsonPair() throws JsonProcessingException {
        StringWrapper wrapper = new StringWrapper();
        wrapper.stringValue = "someValue";
        assertThat(mapper.readValue("{\"stringValue\":\"someValue\"}", StringWrapper.class).stringValue)
                .isEqualTo("someValue");
    }

    @Test
    public void failureShouldPreserveLocation() throws JsonProcessingException {
        StrictStringDeserializingException exception = catchThrowableOfType(() -> {
            mapper.readValue("{\"stringValue\":6}", StringWrapper.class);
        }, StrictStringDeserializingException.class);

        assertThat(exception.getLocation().getColumnNr()).isEqualTo(16);
    }

    @Test
    public void failureShouldBeDueToStrictDeserializing() {
        Throwable throwable = catchThrowable(() -> {
            mapper.readValue("3", String.class);
        });

        assertThat(throwable).isExactlyInstanceOf(StrictStringDeserializingException.class)
                .isInstanceOf(StrictDeserializingException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = { "{}", "[]" })
    public void shouldFailForOtherJsonCompoundTypes(String compoundType) throws JsonProcessingException {
        assertThatExceptionOfType(StrictStringDeserializingException.class).isThrownBy(() -> {
            mapper.readValue(compoundType, String.class);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = { "3", "3.3", "true", "false", "null" })
    public void shouldFailForOtherJsonScalars(String scalar) throws JsonProcessingException {
        assertThatExceptionOfType(StrictStringDeserializingException.class).isThrownBy(() -> {
            mapper.readValue(scalar, String.class);
        });
    }

    static class IntWrapper {
        public int intValue;
    }

    static class StringWrapper {
        public String stringValue;
    }
}