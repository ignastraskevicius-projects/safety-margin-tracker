package org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.parsing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.parsing.ViolationType.VALUE_MUST_BE_STRING;

import lombok.val;
import org.junit.jupiter.api.Test;

public final class JsPathParsingValidationErrorDTOTest {

    @Test
    public void shouldPreserveJsonPath() {
        final val error = new JsPathParsingValidationErrorDTO(
            JsonPath.fromJsonPath("$.path"),
            VALUE_MUST_BE_STRING
        );

        assertThat(error.getJsonPath()).isEqualTo("$.path");
    }

    @Test
    public void shouldPreserveMessageForSelfExplanatoryErrors() {
        final val fieldIsMissing = VALUE_MUST_BE_STRING;
        assertThat(fieldIsMissing.isErrorSelfExplanatory()).isEqualTo(true);
        final val error = new JsPathParsingValidationErrorDTO(
            JsonPath.fromJsonPath("$.anyPath"),
            fieldIsMissing
        );

        assertThat(error.getMessage()).isNull();
    }

    @Test
    public void shouldConvertTypeToErrorName() {
        final val error = new JsPathParsingValidationErrorDTO(
            JsonPath.fromJsonPath("$.anyPath"),
            VALUE_MUST_BE_STRING
        );

        assertThat(error.getErrorName()).isEqualTo("valueMustBeString");
    }

    @Test
    public void shouldNotBeOfNullType() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> new JsPathParsingValidationErrorDTO(JsonPath.fromJsonPath("$.anyPath"), null));
    }
}

final class JsonPathFromJsonPathTest {

    @Test
    public void shouldRejectToCreateFromNull() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> JsonPath.fromJsonPath(null))
            .withMessage("JsonPath required to be non-null");
    }

    @Test
    public void shouldPreserveUnderlyingJsonPath() {
        assertThat(JsonPath.fromJsonPath("$.path").getJsonPath()).isEqualTo("$.path");
    }

    @Test
    public void shouldRejectToCreateFromPathNotStartingWithDollarSign() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> JsonPath.fromJsonPath(".path"))
            .withMessage(
                "Invalid JsonPath provided. " +
                "It should start with '$.' for property or '$[' for index or be root '$'"
            );
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
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> JsonPath.fromJsonPath("$abc"));
    }
}
