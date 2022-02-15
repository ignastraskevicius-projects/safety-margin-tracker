package org.ignast.stockinvesting.strictjackson;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.*;

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
    public void shouldDeserializeBeanValue() throws JsonProcessingException {
        StringWrapper wrapper = new StringWrapper("someValue");
        assertThat(mapper.readValue("{\"stringValue\":\"someValue\"}", StringWrapper.class).stringValue)
                .isEqualTo("someValue");
    }

    @Test
    public void shouldFailToDeserializeBeanValueWithNull() throws JsonProcessingException {
        assertThat(mapper.readValue("{\"stringValue\":null}", StringWrapper.class).stringValue).isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = { "3", "3.3", "true", "false", "{}", "[]" })
    public void failureShouldPreserveParserAndLocation(String jsonValue) throws JsonProcessingException {
        StrictStringDeserializingException exception = catchThrowableOfType(() -> {
            mapper.readValue(format("{\"stringValue\":%s}", jsonValue), StringWrapper.class);
        }, StrictStringDeserializingException.class);

        assertThat(exception.getMessage()).startsWith("java.String can only be deserialized only from json String");
        assertThat(exception.getLocation().getColumnNr()).isEqualTo(16);
        assertThat(exception.getProcessor()).isInstanceOf(JsonParser.class);
        assertThat(((JsonParser) exception.getProcessor()).getTokenLocation().getColumnNr()).isEqualTo(16);
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
    @ValueSource(strings = { "3", "3.3", "true", "false", "{}", "[]" })
    public void shouldFailFromOtherJsonTypes(String jsonValue) throws JsonProcessingException {
        assertThatExceptionOfType(StrictStringDeserializingException.class).isThrownBy(() -> {
            mapper.readValue(jsonValue, String.class);
        });
    }

    @Test
    public void shouldConvertJsonNullToJavaNull() throws JsonProcessingException {
        assertThat(mapper.readValue("null", String.class)).isEqualTo(null);
    }

    static class IntWrapper {
        public int intValue;
    }

    static class StringWrapper {
        private String stringValue;

        public StringWrapper(@JsonProperty(value = "stringValue", required = true) String stringValue) {
            this.stringValue = stringValue;
        }
    }
}