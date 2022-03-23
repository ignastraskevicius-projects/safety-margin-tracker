package org.ignast.stockinvesting.util.errorhandling.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.util.Collections;
import java.util.List;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class StandardErrorDTOTest {

    @Test
    public void shouldNotCreateNullError() {
        assertThatNullPointerException().isThrownBy(() -> StandardErrorDTO.createNameless(null));
        StandardErrorDTO.createNameless(HttpStatus.BAD_GATEWAY);
    }

    @Test
    public void shouldCreateNamelessError() {
        final val error = StandardErrorDTO.createNameless(HttpStatus.BAD_GATEWAY);

        final val badGateway = 502;
        assertThat(error.getErrorName()).isNull();
        assertThat(error.getHttpStatus()).isEqualTo(badGateway);
    }

    @Test
    public void shouldCreateMethodNotAllowed() {
        final val error = StandardErrorDTO.createForMethodNotAllowed();

        assertThat(error.getErrorName()).isEqualTo("methodNotAllowed");
        assertThat(error.getHttpStatus()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED.value());
    }

    @Test
    public void shouldCreateBusinessError() {
        final val businessError = mock(BusinessErrorDTO.class);
        when(businessError.getErrorName()).thenReturn("businessRuleViolation");
        when(businessError.getHttpStatus()).thenReturn(BAD_REQUEST);

        final val error = StandardErrorDTO.createForBusinessError(businessError);

        assertThat(error.getErrorName()).isEqualTo("businessRuleViolation");
        assertThat(error.getHttpStatus()).isEqualTo(BAD_REQUEST.value());
    }

    @Test
    public void shouldCreateMediaTypeNotAcceptable() {
        final val error = StandardErrorDTO.createForMediaTypeNotAcceptable();

        assertThat(error.getErrorName()).isEqualTo("mediaTypeNotAcceptable");
        assertThat(error.getHttpStatus()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
    }

    @Test
    public void shouldCreateForContentTypeNotSupported() {
        final val error = StandardErrorDTO.createForUnsupportedContentType();

        assertThat(error.getErrorName()).isEqualTo("unsupportedContentType");
        assertThat(error.getHttpStatus()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
    }

    @Test
    public void shouldCreateBodyNotParsable() {
        final val error = StandardErrorDTO.createBodyNotParsable();

        assertThat(error.getErrorName()).isEqualTo("bodyNotParsable");
        assertThat(error.getHttpStatus()).isEqualTo(BAD_REQUEST.value());
    }
}

final class BodyDoesNotMatchSchemaErrorDTOTest {

    @Test
    public void shouldHaveErrorNameSetAutomatically() {
        final val bodyDoesNotMatchSchema = StandardErrorDTO.createForBodyDoesNotMatchSchema(
            Collections.emptyList()
        );

        assertThat(bodyDoesNotMatchSchema.getErrorName()).isEqualTo("bodyDoesNotMatchSchema");
    }

    @Test
    public void shouldTransformNullListToEmptyList() {
        final val bodyDoesNotMatchSchema = StandardErrorDTO.createForBodyDoesNotMatchSchema(null);

        assertThat(bodyDoesNotMatchSchema.getValidationErrors()).isEqualTo(Collections.emptyList());
    }

    @Test
    public void shouldPreserveValidationErrors() {
        final val originalErrors = List.of(
            new ValidationErrorDTO(JsonPath.fromJsonPath("$.path"), "message", ViolationType.FIELD_IS_MISSING)
        );

        final val bodyDoesNotMatchSchema = StandardErrorDTO.createForBodyDoesNotMatchSchema(originalErrors);

        final val preservedErrors = bodyDoesNotMatchSchema.getValidationErrors();
        assertThat(preservedErrors).hasSize(1);
        assertThat(preservedErrors.get(0).getErrorName()).isEqualTo("fieldIsMissing");
    }
}
