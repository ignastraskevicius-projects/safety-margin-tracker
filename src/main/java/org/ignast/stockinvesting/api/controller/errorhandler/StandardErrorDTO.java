package org.ignast.stockinvesting.api.controller.errorhandler;

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
}
