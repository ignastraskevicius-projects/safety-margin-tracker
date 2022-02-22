package org.ignast.stockinvesting.api.controller.errorhandler;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class BodyDoesNotMatchSchemaErrorDTOTest {
    @Test
    public void shouldHaveErrorNameSetAutomatically() {
        assertThat(new BodyDoesNotMatchSchemaErrorDTO(Collections.emptyList()).getErrorName())
                .isEqualTo("bodyDoesNotMatchSchema");
    }

    @Test
    public void shouldTransformNullListToEmptyList() {
        assertThat(new BodyDoesNotMatchSchemaErrorDTO(null).getValidationErrors()).isEqualTo(Collections.emptyList());
    }

    @Test
    public void shouldPreserveValidationErrors() {
        List<ValidationErrorDTO> originalErrors = asList(
                new ValidationErrorDTO("path", "message", ViolationType.FIELD_IS_MISSING));

        List<ValidationErrorDTO> preservedErrors = new BodyDoesNotMatchSchemaErrorDTO(originalErrors)
                .getValidationErrors();

        assertThat(preservedErrors).hasSize(1);
        assertThat(preservedErrors.get(0).getErrorName()).isEqualTo("fieldIsMissing");
    }
}