package org.ignast.stockinvesting.util.errorhandling.api;

import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNullElse;

public class StandardErrorDTO {

    private final String errorName;

    public StandardErrorDTO(final String errorName) {
        this.errorName = errorName;
    }

    public static StandardErrorDTO createNameless() {
        return new StandardErrorDTO(null);
    }

    public static BodyDoesNotMatchSchemaErrorDTO createForBodyDoesNotMatchSchema(final List<ValidationErrorDTO> errors) {
        return new BodyDoesNotMatchSchemaErrorDTO(errors);
    }

    public static StandardErrorDTO createBodyNotParsable() {
        return new StandardErrorDTO("bodyNotParsable");
    }

    public static StandardErrorDTO createForMethodNotAllowed() {
        return new StandardErrorDTO("methodNotAllowed");
    }

    public static StandardErrorDTO createForMediaTypeNotAcceptable() {
        return new StandardErrorDTO("mediaTypeNotAcceptable");
    }

    public static StandardErrorDTO createForUnsupportedContentType() {
        return new StandardErrorDTO("unsupportedContentType");
    }

    public static StandardErrorDTO createForResourceNotFound() {
        return new StandardErrorDTO("resourceNotFound");
    }

    public static StandardErrorDTO createForBusinessError(final BusinessErrorDTO error) {
        return new StandardErrorDTO(error.getErrorName());
    }

    @SuppressWarnings("checkstyle:designforextension")
    public String getErrorName() {
        return errorName;
    }

    public static final class BodyDoesNotMatchSchemaErrorDTO extends StandardErrorDTO {
        private final List<ValidationErrorDTO> validationErrors;

        private BodyDoesNotMatchSchemaErrorDTO(final List<ValidationErrorDTO> validationErrors) {
            super("bodyDoesNotMatchSchema");
            this.validationErrors = requireNonNullElse(validationErrors, Collections.emptyList());
        }

        public List<ValidationErrorDTO> getValidationErrors() {
            return validationErrors;
        }
    }
}
