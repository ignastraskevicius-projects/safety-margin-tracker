package org.ignast.stockinvesting.util.errorhandling.api;

import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StandardErrorDTOTest {

    @Test
    public void shouldCreateNamelessError() {
        final val error = StandardErrorDTO.createNameless();

        assertThat(error.getErrorName()).isNull();
    }

    @Test
    public void shouldCreateResourceNotFound() {
        final val error = StandardErrorDTO.createForResourceNotFound();

        assertThat(error.getErrorName()).isEqualTo("resourceNotFound");
    }

    @Test
    public void shouldCreateMethodNotAllowed() {
        final val error = StandardErrorDTO.createForMethodNotAllowed();

        assertThat(error.getErrorName()).isEqualTo("methodNotAllowed");
    }

    @Test
    public void shouldCreateBusinessError() {
        final val error = StandardErrorDTO.createForBusinessError(() -> "someBusinessError");

        assertThat(error.getErrorName()).isEqualTo("someBusinessError");
    }

    @Test
    public void shouldCreateMediaTypeNotAcceptable() {
        final val error = StandardErrorDTO.createForMediaTypeNotAcceptable();

        assertThat(error.getErrorName()).isEqualTo("mediaTypeNotAcceptable");
    }

    @Test
    public void shouldCreateForContentTypeNotSupported() {
        final val error = StandardErrorDTO.createForUnsupportedContentType();

        assertThat(error.getErrorName()).isEqualTo("unsupportedContentType");
    }

    @Test
    public void shouldCreateBodyNotParsable() {
        final val error = StandardErrorDTO.createBodyNotParsable();

        assertThat(error.getErrorName()).isEqualTo("bodyNotParsable");
    }
}

final class BodyDoesNotMatchSchemaErrorDTOTest {
    @Test
    public void shouldHaveErrorNameSetAutomatically() {
        final val bodyDoesNotMatchSchema = StandardErrorDTO
                .createForBodyDoesNotMatchSchema(Collections.emptyList());

        assertThat(bodyDoesNotMatchSchema.getErrorName()).isEqualTo("bodyDoesNotMatchSchema");
    }

    @Test
    public void shouldTransformNullListToEmptyList() {
        final val bodyDoesNotMatchSchema = StandardErrorDTO
                .createForBodyDoesNotMatchSchema(null);

        assertThat(bodyDoesNotMatchSchema.getValidationErrors()).isEqualTo(Collections.emptyList());
    }

    @Test
    public void shouldPreserveValidationErrors() {
        final val originalErrors = List.of(
                new ValidationErrorDTO(JsonPath.fromJsonPath("$.path"), "message", ViolationType.FIELD_IS_MISSING));

        final val bodyDoesNotMatchSchema = StandardErrorDTO
                .createForBodyDoesNotMatchSchema(originalErrors);

        final val preservedErrors = bodyDoesNotMatchSchema.getValidationErrors();
        assertThat(preservedErrors).hasSize(1);
        assertThat(preservedErrors.get(0).getErrorName()).isEqualTo("fieldIsMissing");
    }
}