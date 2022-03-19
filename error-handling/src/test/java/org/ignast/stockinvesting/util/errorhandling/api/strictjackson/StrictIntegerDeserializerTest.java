package org.ignast.stockinvesting.util.errorhandling.api.strictjackson;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

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

final class StrictIntegerDeserializerTest {

    private ObjectMapper mapper;

    @BeforeEach
    public void setup() {
        final val module = new SimpleModule();
        module.addDeserializer(Integer.class, new StrictIntegerDeserializer());
        mapper = new ObjectMapper().registerModule(module);
    }

    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void shouldReadJsonInteger() throws JsonProcessingException {
        assertThat(mapper.readValue("5", Integer.class)).isEqualTo(5);
    }

    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void shouldDeserializeBeanValue() throws JsonProcessingException {
        assertThat(mapper.readValue("{\"integerValue\":7}", IntegerWrapper.class).integerValue).isEqualTo(7);
    }

    @Test
    public void shouldDeserializeNullBeanValue() throws JsonProcessingException {
        assertThat(mapper.readValue("{\"integerValue\":null}", IntegerWrapper.class).integerValue).isNull();
    }

    @SuppressWarnings("checkstyle:magicnumber")
    @ParameterizedTest
    @ValueSource(strings = { "\"string\"", "3.3", "true", "false", "{}", "[]" })
    public void failureShouldPreserveParserAndLocation(final String jsonValue) {
        final val exception = catchThrowableOfType(
            () -> {
                mapper.readValue(format("{\"integerValue\":%s}", jsonValue), IntegerWrapper.class);
            },
            StrictIntegerDeserializingException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getMessage())
            .startsWith("java.util.Integer can be deserialized only from json Integer type");
        assertThat(exception.getLocation().getColumnNr()).isEqualTo(17);
        assertThat(exception.getProcessor()).isInstanceOf(JsonParser.class);
        assertThat(((JsonParser) exception.getProcessor()).getTokenLocation().getColumnNr()).isEqualTo(17);
    }

    @Test
    public void failureShouldBeDueToStrictDeserializing() {
        final val throwable = catchThrowable(() -> {
            mapper.readValue("\"notInteger\"", Integer.class);
        });

        assertThat(throwable)
            .isExactlyInstanceOf(StrictIntegerDeserializingException.class)
            .isInstanceOf(StrictDeserializingException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = { "\"someString\"", "3.3", "true", "false", "{}", "[]" })
    public void shouldFailFromOtherJsonTypes(final String jsonValue) {
        assertThatExceptionOfType(StrictIntegerDeserializingException.class)
            .isThrownBy(() -> {
                mapper.readValue(jsonValue, Integer.class);
            });
    }

    @Test
    public void shouldConvertJsonNullToJavaNull() throws JsonProcessingException {
        assertThat(mapper.readValue("null", Integer.class)).isEqualTo(null);
    }

    static class IntegerWrapper {

        private Integer integerValue;

        public IntegerWrapper(
            @JsonProperty(value = "integerValue", required = true) final Integer integerValue
        ) {
            this.integerValue = integerValue;
        }
    }
}
