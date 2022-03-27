package org.ignast.stockinvesting.util.errorhandling.api.dto;

import static java.lang.String.format;
import static java.util.Objects.requireNonNullElse;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;

import java.util.Collections;
import java.util.List;
import lombok.NonNull;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class StandardErrorDTO {

    private static final String MEDIA_TYPE_NOT_ACCEPTABLE = "mediaTypeNotAcceptable";

    private final HttpStatus httpStatus;

    private String message;

    private final String errorName;

    private StandardErrorDTO(final String errorName, @NonNull final HttpStatus status) {
        this(errorName, status, null);
    }

    private StandardErrorDTO(final String errorName, final HttpStatus status, final String message) {
        this.errorName = errorName;
        this.httpStatus = status;
        this.message = message;
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

    public static StandardErrorDTO createForNotAcceptableRequiresInstead(final MediaType appMediaType) {
        final val message = format("This version of service supports only '%s'", appMediaType);
        return new StandardErrorDTO(MEDIA_TYPE_NOT_ACCEPTABLE, NOT_ACCEPTABLE, message);
    }

    public static StandardErrorDTO createForNotAcceptableNoHeader() {
        return new StandardErrorDTO(MEDIA_TYPE_NOT_ACCEPTABLE, NOT_ACCEPTABLE, "Missing Accept header");
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

    public String getMessage() {
        return message;
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
