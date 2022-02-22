package org.ignast.stockinvesting.api.controller.errorhandler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.api.controller.errorhandler.ViolationType.FIELD_IS_MISSING;

public class ValidationErrorTest {

    @ParameterizedTest
    @ValueSource(strings = { "somePath", "otherPath" })
    public void shouldConvertPathToJsonPath(String path) {
        ValidationError error = new ValidationError(path, "anyMessage", FIELD_IS_MISSING);

        assertThat(error.getJsonPath()).isEqualTo(String.format("$.%s", path));
    }

    @Test
    public void shouldConvertNullPathToRootJsonPath() {
        ValidationError error = new ValidationError(null, "anyMessage", FIELD_IS_MISSING);

        assertThat(error.getJsonPath()).isEqualTo("$");
    }

    @Test
    public void shouldConvertEmptyPathToRootJsonPath() {
        ValidationError error = new ValidationError("", "anyMessage", FIELD_IS_MISSING);

        assertThat(error.getJsonPath()).isEqualTo("$");
    }

    @ParameterizedTest
    @ValueSource(strings = { "someMessage", "otherMessage" })
    public void shouldPreserveMessage(String message) {
        ValidationError error = new ValidationError("anyPath", message, FIELD_IS_MISSING);

        assertThat(error.getMessage()).isEqualTo(message);
    }

    @ParameterizedTest
    @ValueSource(strings = { "FIELD_IS_MISSING", "VALUE_INVALID" })
    public void shouldPreserveType(String violationType) {
        ValidationError error = new ValidationError("anyPath", "anyMessage", ViolationType.valueOf(violationType));

        assertThat(error.getType()).isEqualTo(ViolationType.valueOf(violationType));
    }

    @Test
    public void shouldNotBeOfNullType() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new ValidationError("anyPath", "anyMessage", null));
    }
}
