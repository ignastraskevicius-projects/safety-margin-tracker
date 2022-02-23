package org.ignast.stockinvesting.api.controller.errorhandler;

public class Errors {
    public static ValidationErrorDTO anyValidationErrorDTO() {
        return new ValidationErrorDTO(JsonPath.fromJsonPath("$.anyPath"), "anyMessage", ViolationType.FIELD_IS_MISSING);
    }
}
