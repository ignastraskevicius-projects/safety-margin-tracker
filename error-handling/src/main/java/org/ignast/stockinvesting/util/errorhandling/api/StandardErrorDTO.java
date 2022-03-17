package org.ignast.stockinvesting.util.errorhandling.api;

import java.util.Collections;
import java.util.List;

public class StandardErrorDTO {

    private final String errorName;

    public StandardErrorDTO(String errorName) {
        this.errorName = errorName;
    }

    public static StandardErrorDTO createNameless() {
        return new StandardErrorDTO(null);
    }

    public static BodyDoesNotMatchSchemaErrorDTO createForBodyDoesNotMatchSchema(List<ValidationErrorDTO> errors) {
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

    public static StandardErrorDTO createForBusinessError(BusinessErrorDTO error) {
        return new StandardErrorDTO(error.getErrorName());
    }

    @SuppressWarnings("checkstyle:designforextension")
    public String getErrorName() {
        return errorName;
    }

    public static final class BodyDoesNotMatchSchemaErrorDTO extends StandardErrorDTO {
        private final List<ValidationErrorDTO> validationErrors;

        private BodyDoesNotMatchSchemaErrorDTO(List<ValidationErrorDTO> validationErrors) {
            super("bodyDoesNotMatchSchema");
            if (validationErrors == null) {
                this.validationErrors = Collections.emptyList();
            } else {
                this.validationErrors = validationErrors;
            }
        }

        public List<ValidationErrorDTO> getValidationErrors() {
            return validationErrors;
        }
    }
}
