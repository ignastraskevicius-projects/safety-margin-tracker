package org.ignast.stockinvesting.api.controller.errorhandler;

import java.util.Collections;
import java.util.List;

public class BodyDoesNotMatchSchemaErrorDTO {
    private List<ValidationErrorDTO> validationErrors;

    public BodyDoesNotMatchSchemaErrorDTO(List<ValidationErrorDTO> validationErrors) {
        if (validationErrors == null) {
            this.validationErrors = Collections.emptyList();
        } else {
            this.validationErrors = validationErrors;
        }
    }

    public String getErrorName() {
        return "bodyDoesNotMatchSchema";
    }

    public List<ValidationErrorDTO> getValidationErrors() {
        return validationErrors;
    }
}
