package org.ignast.stockinvesting.api.controller.errorhandler;

import java.util.Collections;
import java.util.List;

public class StandardErrorDTO {

    private String errorName;

    public StandardErrorDTO(String errorName) {
        this.errorName = errorName;
    }

    public static StandardErrorDTO createUnknownError() {
        return new StandardErrorDTO("unknownError");
    }

    public static BodyDoesNotMatchSchemaErrorDTO createForBodyDoesNotMatchSchema(List<ValidationErrorDTO> errors) {
        return new BodyDoesNotMatchSchemaErrorDTO(errors);
    }

    public String getErrorName() {
        return errorName;
    }

    public static class BodyDoesNotMatchSchemaErrorDTO extends StandardErrorDTO {
        private List<ValidationErrorDTO> validationErrors;

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
