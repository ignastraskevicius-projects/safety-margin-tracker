package org.ignast.stockinvesting.util.errorhandling.api;

import static java.util.Objects.requireNonNullElse;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;

import java.util.Collections;
import java.util.List;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

public class StandardErrorDTO {

    private final HttpStatus httpStatus;

    private final String errorName;

    private StandardErrorDTO(final String errorName, @NonNull final HttpStatus status) {
        this.errorName = errorName;
        this.httpStatus = status;
    }

    public static StandardErrorDTO createNameless(final HttpStatus status) {
        return new StandardErrorDTO(null, status);
    }

    public static BodyDoesNotMatchSchemaErrorDTO createForBodyDoesNotMatchSchema(
        final List<ValidationErrorDTO> errors
    ) {
        return new BodyDoesNotMatchSchemaErrorDTO(errors);
    }

    public static StandardErrorDTO createBodyNotParsable() {
        return new StandardErrorDTO("bodyNotParsable", BAD_REQUEST);
    }

    public static StandardErrorDTO createForMethodNotAllowed() {
        return new StandardErrorDTO("methodNotAllowed", METHOD_NOT_ALLOWED);
    }

    public static StandardErrorDTO createForMediaTypeNotAcceptable() {
        return new StandardErrorDTO("mediaTypeNotAcceptable", NOT_ACCEPTABLE);
    }

    public static StandardErrorDTO createForUnsupportedContentType() {
        return new StandardErrorDTO("unsupportedContentType", UNSUPPORTED_MEDIA_TYPE);
    }

    public static StandardErrorDTO createForBusinessError(final BusinessErrorDTO error) {
        return new StandardErrorDTO(error.getErrorName(), error.getHttpStatus());
    }

    @SuppressWarnings("checkstyle:designforextension")
    public String getErrorName() {
        return errorName;
    }

    public int getHttpStatus() {
        return httpStatus.value();
    }

    public static final class BodyDoesNotMatchSchemaErrorDTO extends StandardErrorDTO {

        private final List<ValidationErrorDTO> validationErrors;

        private BodyDoesNotMatchSchemaErrorDTO(final List<ValidationErrorDTO> validationErrors) {
            super("bodyDoesNotMatchSchema", BAD_REQUEST);
            this.validationErrors = requireNonNullElse(validationErrors, Collections.emptyList());
        }

        public List<ValidationErrorDTO> getValidationErrors() {
            return validationErrors;
        }
    }
}
