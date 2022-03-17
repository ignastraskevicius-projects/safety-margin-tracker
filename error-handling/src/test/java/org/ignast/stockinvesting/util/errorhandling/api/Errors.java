package org.ignast.stockinvesting.util.errorhandling.api;

public final class Errors {
    public static ValidationErrorDTO anyValidationErrorDTO() {
        return new ValidationErrorDTO(JsonPath.fromJsonPath("$.anyPath"), "anyMessage", ViolationType.FIELD_IS_MISSING);
    }
}
