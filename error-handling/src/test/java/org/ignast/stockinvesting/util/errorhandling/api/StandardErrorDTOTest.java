package org.ignast.stockinvesting.util.errorhandling.api;

import org.ignast.stockinvesting.util.errorhandling.api.StandardErrorDTO;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class StandardErrorDTOTest {

    @Test
    public void shouldCreateNamelessError() {
        StandardErrorDTO error = StandardErrorDTO.createNameless();

        assertThat(error.getErrorName()).isNull();
    }

    @Test
    public void shouldCreateResourceNotFound() {
        StandardErrorDTO error = StandardErrorDTO.createForResourceNotFound();

        assertThat(error.getErrorName()).isEqualTo("resourceNotFound");
    }

    @Test
    public void shouldCreateMethodNotAllowed() {
        StandardErrorDTO error = StandardErrorDTO.createForMethodNotAllowed();

        assertThat(error.getErrorName()).isEqualTo("methodNotAllowed");
    }

    @Test
    public void shouldCreateBusinessError() {
        StandardErrorDTO error = StandardErrorDTO.createForBusinessError(() -> "someBusinessError");

        assertThat(error.getErrorName()).isEqualTo("someBusinessError");
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