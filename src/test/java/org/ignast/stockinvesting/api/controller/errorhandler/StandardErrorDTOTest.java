package org.ignast.stockinvesting.api.controller.errorhandler;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class StandardErrorDTOTest {

    @Test
    public void shouldCreateUnknownError() {
        StandardErrorDTO error = StandardErrorDTO.createUnknownError();

        assertThat(error.getErrorName()).isEqualTo("unknownError");
    }

    @Test
    public void shouldCreateMethodNotAllowed() {
        StandardErrorDTO error = StandardErrorDTO.createForMethodNotAllowed();

        assertThat(error.getErrorName()).isEqualTo("methodNotAllowed");
    }

    @Test
    public void shouldCreateMediaTypeNotAcceptable() {
        StandardErrorDTO error = StandardErrorDTO.createForMediaTypeNotAcceptable();

        assertThat(error.getErrorName()).isEqualTo("mediaTypeNotAcceptable");
    }

    @Test
    public void shouldCreateForContentTypeNotSupported() {
        StandardErrorDTO error = StandardErrorDTO.createForUnsupportedContentType();

        assertThat(error.getErrorName()).isEqualTo("unsupportedContentType");
    }

    @Test
    public void shouldCreateBodyNotParsable() {
        StandardErrorDTO error = StandardErrorDTO.createBodyNotParsable();

        assertThat(error.getErrorName()).isEqualTo("bodyNotParsable");
    }
}

class BodyDoesNotMatchSchemaErrorDTOTest {
    @Test
    public void shouldHaveErrorNameSetAutomatically() {
        StandardErrorDTO.BodyDoesNotMatchSchemaErrorDTO bodyDoesNotMatchSchema = StandardErrorDTO
                .createForBodyDoesNotMatchSchema(Collections.emptyList());

        assertThat(bodyDoesNotMatchSchema.getErrorName()).isEqualTo("bodyDoesNotMatchSchema");
    }

    @Test
    public void shouldTransformNullListToEmptyList() {
        StandardErrorDTO.BodyDoesNotMatchSchemaErrorDTO bodyDoesNotMatchSchema = StandardErrorDTO
                .createForBodyDoesNotMatchSchema(null);

        assertThat(bodyDoesNotMatchSchema.getValidationErrors()).isEqualTo(Collections.emptyList());
    }

    @Test
    public void shouldPreserveValidationErrors() {
        List<ValidationErrorDTO> originalErrors = asList(
                new ValidationErrorDTO(JsonPath.fromJsonPath("$.path"), "message", ViolationType.FIELD_IS_MISSING));

        StandardErrorDTO.BodyDoesNotMatchSchemaErrorDTO bodyDoesNotMatchSchema = StandardErrorDTO
                .createForBodyDoesNotMatchSchema(originalErrors);

        List<ValidationErrorDTO> preservedErrors = bodyDoesNotMatchSchema.getValidationErrors();
        assertThat(preservedErrors).hasSize(1);
        assertThat(preservedErrors.get(0).getErrorName()).isEqualTo("fieldIsMissing");
    }
}