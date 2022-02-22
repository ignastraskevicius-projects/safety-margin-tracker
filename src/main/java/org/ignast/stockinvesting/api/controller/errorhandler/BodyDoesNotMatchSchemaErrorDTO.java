package org.ignast.stockinvesting.api.controller.errorhandler;

import java.util.Collections;
import java.util.List;

public class BodyDoesNotMatchSchemaErrorDTO extends StandardErrorDTO {
    private List<ValidationErrorDTO> validationErrors;

    public BodyDoesNotMatchSchemaErrorDTO(List<ValidationErrorDTO> validationErrors) {
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
