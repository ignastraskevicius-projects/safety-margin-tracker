package org.ignast.stockinvesting.api.controller.errorhandler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.api.controller.errorhandler.ViolationType.FIELD_IS_MISSING;
import static org.ignast.stockinvesting.api.controller.errorhandler.ViolationType.VALUE_INVALID;

public class ValidationErrorDTOTest {

    @ParameterizedTest
    @ValueSource(strings = { "somePath", "otherPath" })
    public void shouldConvertPathToJsonPath(String path) {
        ValidationErrorDTO error = new ValidationErrorDTO(path, "anyMessage", FIELD_IS_MISSING);

        assertThat(error.getJsonPath()).isEqualTo(String.format("$.%s", path));
    }

    @Test
    public void shouldConvertNullPathToRootJsonPath() {
        ValidationErrorDTO error = new ValidationErrorDTO(null, "anyMessage", FIELD_IS_MISSING);

        assertThat(error.getJsonPath()).isEqualTo("$");
    }

    @Test
    public void shouldConvertEmptyPathToRootJsonPath() {
        ValidationErrorDTO error = new ValidationErrorDTO("", "anyMessage", FIELD_IS_MISSING);

        assertThat(error.getJsonPath()).isEqualTo("$");
    }

    @ParameterizedTest
    @ValueSource(strings = { "someMessage", "otherMessage" })
    public void shouldPreserveMessageForNotSelfExplanatoryErrors(String message) {
        ViolationType valueInvalid = VALUE_INVALID;
        assertThat(valueInvalid.isErrorSelfExplanatory()).isEqualTo(false);
        ValidationErrorDTO error = new ValidationErrorDTO("anyPath", message, valueInvalid);

        assertThat(error.getMessage()).isEqualTo(message);
    }

    @Test
    public void shouldPreserveMessageForSelfExplanatoryErrors() {
        ViolationType fieldIsMissing = FIELD_IS_MISSING;
        assertThat(fieldIsMissing.isErrorSelfExplanatory()).isEqualTo(true);
        ValidationErrorDTO error = new ValidationErrorDTO("anyPath", "anyMessage", fieldIsMissing);

        assertThat(error.getMessage()).isNull();
    }

    @Test
    public void shouldConvertTypeToErrorName() {
        ValidationErrorDTO error = new ValidationErrorDTO("anyPath", "anyMessage", FIELD_IS_MISSING);

        assertThat(error.getErrorName()).isEqualTo("fieldIsMissing");
    }

    @Test
    public void shouldNotBeOfNullType() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new ValidationErrorDTO("anyPath", "anyMessage", null));
    }
}
