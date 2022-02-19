package org.ignast.stockinvesting.api.controller.errorhandler;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.api.controller.errorhandler.ViolationType.FIELD_IS_MISSING;

public class ValidationErrorTest {

    @ParameterizedTest
    @ValueSource(strings = { "somePath", "otherPath" })
    public void shouldPreservePath(String path) {
        ValidationError error = new ValidationError(path, "anyMessage", FIELD_IS_MISSING);

        assertThat(error.getPath()).isEqualTo(path);
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
}
