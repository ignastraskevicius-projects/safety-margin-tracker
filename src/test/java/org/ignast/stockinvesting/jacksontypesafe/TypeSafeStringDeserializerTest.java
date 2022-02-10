package org.ignast.stockinvesting.jacksontypesafe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.ignast.stockinvesting.api.controller.CompanyDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

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
    public void shouldFailToReadJsonInteger() throws JsonProcessingException {
        assertThatExceptionOfType(MismatchedInputException.class).isThrownBy(() -> {
            mapper.readValue("3", String.class);
        });
    }

    @Test
    public void shouldFailToReadJsonFloat() throws JsonProcessingException {
        assertThatExceptionOfType(MismatchedInputException.class).isThrownBy(() -> {
            mapper.readValue("3.3", String.class);
        });
    }
}