package org.ignast.stockinvesting.util.errorhandling.api.strictjackson;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

final class StrictStringDeserializerTest {

    private ObjectMapper mapper;

    @BeforeEach
    public void setup() {
        final val module = new SimpleModule();
        module.addDeserializer(String.class, new StrictStringDeserializer());
        mapper = new ObjectMapper().registerModule(module);
    }

    @Test
    public void shouldReadJsonString() throws JsonProcessingException {
        assertThat(mapper.readValue("\"someStringValue\"", String.class)).isEqualTo("someStringValue");
    }

    @Test
    public void shouldReadJsonKey() throws JsonProcessingException {
        assertThat(mapper.readValue("{\"intValue\":6}", IntWrapper.class).intValue).isEqualTo(6);
    }

    @Test
    public void shouldDeserializeBeanValue() throws JsonProcessingException {
        assertThat(mapper.readValue("{\"stringValue\":\"someValue\"}", StringWrapper.class).stringValue)
                .isEqualTo("someValue");
    }

    @Test
    public void shouldFailToDeserializeBeanValueWithNull() throws JsonProcessingException {
        assertThat(mapper.readValue("{\"stringValue\":null}", StringWrapper.class).stringValue).isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = { "3", "3.3", "true", "false", "{}", "[]" })
    public void failureShouldPreserveParserAndLocation(final String jsonValue) {
        final val exception = catchThrowableOfType(() ->
                mapper.readValue(format("{\"stringValue\":%s}", jsonValue), StringWrapper.class), StrictStringDeserializingException.class);

        assertThat(exception.getMessage()).startsWith("java.String can only be deserialized only from json String");
        assertThat(exception.getLocation().getColumnNr()).isEqualTo(16);
        assertThat(exception.getProcessor()).isInstanceOf(JsonParser.class);
        assertThat(((JsonParser) exception.getProcessor()).getTokenLocation().getColumnNr()).isEqualTo(16);
    }

    @Test
    public void failureShouldBeDueToStrictDeserializing() {
        final val throwable = catchThrowable(() -> mapper.readValue("3", String.class));

        assertThat(throwable).isNotNull();
        assertThat(throwable).isExactlyInstanceOf(StrictStringDeserializingException.class)
                .isInstanceOf(StrictDeserializingException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = { "3", "3.3", "true", "false", "{}", "[]" })
    public void shouldFailFromOtherJsonTypes(final String jsonValue) {
        assertThatExceptionOfType(StrictStringDeserializingException.class).isThrownBy(() -> mapper.readValue(jsonValue, String.class));
    }

    @Test
    public void shouldConvertJsonNullToJavaNull() throws JsonProcessingException {
        assertThat(mapper.readValue("null", String.class)).isEqualTo(null);
    }

    private static final class IntWrapper {
        public int intValue;
    }

    private static final class StringWrapper {
        private final String stringValue;

        public StringWrapper(@JsonProperty(value = "stringValue", required = true) final String stringValue) {
            this.stringValue = stringValue;
        }
    }
}