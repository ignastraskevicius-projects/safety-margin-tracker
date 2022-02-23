package org.ignast.stockinvesting.api.controller.errorhandler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.api.controller.errorhandler.ViolationType.FIELD_IS_MISSING;
import static org.ignast.stockinvesting.api.controller.errorhandler.ViolationType.VALUE_INVALID;

public class ValidationErrorDTOTest {

    @Test
    public void shouldPreserveJsonPath() {
        ValidationErrorDTO error = new ValidationErrorDTO(JsonPath.fromJsonPath("$.path"), "anyMessage",
                FIELD_IS_MISSING);

        assertThat(error.getJsonPath()).isEqualTo("$.path");
    }

    @ParameterizedTest
    @ValueSource(strings = { "someMessage", "otherMessage" })
    public void shouldPreserveMessageForNotSelfExplanatoryErrors(String message) {
        ViolationType valueInvalid = VALUE_INVALID;
        assertThat(valueInvalid.isErrorSelfExplanatory()).isEqualTo(false);
        ValidationErrorDTO error = new ValidationErrorDTO(JsonPath.fromJsonPath("$.anyPath"), message, valueInvalid);

        assertThat(error.getMessage()).isEqualTo(message);
    }

    @Test
    public void shouldPreserveMessageForSelfExplanatoryErrors() {
        ViolationType fieldIsMissing = FIELD_IS_MISSING;
        assertThat(fieldIsMissing.isErrorSelfExplanatory()).isEqualTo(true);
        ValidationErrorDTO error = new ValidationErrorDTO(JsonPath.fromJsonPath("$.anyPath"), "anyMessage",
                fieldIsMissing);

        assertThat(error.getMessage()).isNull();
    }

    @Test
    public void shouldConvertTypeToErrorName() {
        ValidationErrorDTO error = new ValidationErrorDTO(JsonPath.fromJsonPath("$.anyPath"), "anyMessage",
                FIELD_IS_MISSING);

        assertThat(error.getErrorName()).isEqualTo("fieldIsMissing");
    }

    @Test
    public void shouldNotBeOfNullType() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new ValidationErrorDTO(JsonPath.fromJsonPath("$.anyPath"), "anyMessage", null));
    }
}

class JsonPathFromJsonPathTest {

    @Test
    public void shouldRejectToCreateFromNull() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> JsonPath.fromJsonPath(null))
                .withMessage("JsonPath required to be non-null");
    }

    @Test
    public void shouldPreserveUnderlyingJsonPath() {
        assertThat(JsonPath.fromJsonPath("$.path").getJsonPath()).isEqualTo("$.path");
    }

    @Test
    public void shouldRejectToCreateFromPathNotStartingWithDollarSign() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> JsonPath.fromJsonPath(".path"))
                .withMessage(
                        "Invalid JsonPath provided. It should start with '$.' for property or '$[' for index or be root '$'");
    }

    @Test
    public void shouldCreateFromRootPath() {
        assertThat(JsonPath.fromJsonPath("$").getJsonPath()).isEqualTo("$");
    }

    @Test
    public void shouldCreateFromPathStartingWithProperty() {
        assertThat(JsonPath.fromJsonPath("$.property").getJsonPath()).isEqualTo("$.property");
    }

    @Test
    public void shouldCreateFromPathStartingWithIndex() {
        assertThat(JsonPath.fromJsonPath("$[4]").getJsonPath()).isEqualTo("$[4]");
    }

    @Test
    public void shouldRejectToCreateFromPathNotStartingWithNeitherPropertyNorIndex() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> JsonPath.fromJsonPath("$abc"));
    }
}

class JsonPathAdaptionTest {
    @Test
    public void shouldAdaptNullAsRootJsonPath() {
        assertThat(JsonPath.adaptFromJavaxValidationPath(null).getJsonPath()).isEqualTo("$");
    }

    @Test
    public void shouldAdaptEmptyStringAsRootJsonPath() {
        assertThat(JsonPath.adaptFromJavaxValidationPath("").getJsonPath()).isEqualTo("$");
    }

    @Test
    public void shouldAdaptPathStartingWithIndex() {
        assertThat(JsonPath.adaptFromJavaxValidationPath("[4].element").getJsonPath()).isEqualTo("$[4].element");
    }

    @Test
    public void shouldAdaptPathStartingWithProperty() {
        assertThat(JsonPath.adaptFromJavaxValidationPath("element").getJsonPath()).isEqualTo("$.element");
    }
}