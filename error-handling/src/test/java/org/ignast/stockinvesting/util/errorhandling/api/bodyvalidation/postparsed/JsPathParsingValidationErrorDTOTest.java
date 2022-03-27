package org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.postparsed;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.postparsed.ViolationType.FIELD_IS_MISSING;
import static org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.postparsed.ViolationType.VALUE_INVALID;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public final class JsPathParsingValidationErrorDTOTest {

    @Test
    public void shouldPreserveJsonPath() {
        final val error = new JsPathParsingValidationErrorDTO(
            JsonPath.adaptFromJavaxValidationPath("path"),
            "anyMessage",
            FIELD_IS_MISSING
        );

        assertThat(error.getJsonPath()).isEqualTo("$.path");
    }

    @ParameterizedTest
    @ValueSource(strings = { "someMessage", "otherMessage" })
    public void shouldPreserveMessageForNotSelfExplanatoryErrors(final String message) {
        final val valueInvalid = VALUE_INVALID;
        assertThat(valueInvalid.isErrorSelfExplanatory()).isEqualTo(false);
        final val error = new JsPathParsingValidationErrorDTO(
            JsonPath.adaptFromJavaxValidationPath("anyPath"),
            message,
            valueInvalid
        );

        assertThat(error.getMessage()).isEqualTo(message);
    }

    @Test
    public void shouldPreserveMessageForSelfExplanatoryErrors() {
        final val fieldIsMissing = FIELD_IS_MISSING;
        assertThat(fieldIsMissing.isErrorSelfExplanatory()).isEqualTo(true);
        final val error = new JsPathParsingValidationErrorDTO(
            JsonPath.adaptFromJavaxValidationPath("anyPath"),
            "anyMessage",
            fieldIsMissing
        );

        assertThat(error.getMessage()).isNull();
    }

    @Test
    public void shouldConvertTypeToErrorName() {
        final val error = new JsPathParsingValidationErrorDTO(
            JsonPath.adaptFromJavaxValidationPath("anyPath"),
            "anyMessage",
            FIELD_IS_MISSING
        );

        assertThat(error.getErrorName()).isEqualTo("fieldIsMissing");
    }

    @Test
    public void shouldNotBeOfNullType() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() ->
                new JsPathParsingValidationErrorDTO(
                    JsonPath.adaptFromJavaxValidationPath("anyPath"),
                    "anyMessage",
                    null
                )
            );
    }
}

final class JsonPathAdaptionTest {

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
        assertThat(JsonPath.adaptFromJavaxValidationPath("[4].element").getJsonPath())
            .isEqualTo("$[4].element");
    }

    @Test
    public void shouldAdaptPathStartingWithProperty() {
        assertThat(JsonPath.adaptFromJavaxValidationPath("element").getJsonPath()).isEqualTo("$.element");
    }
}
