package org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.postparsed;

import org.ignast.stockinvesting.util.errorhandling.api.dto.ValidationErrorDTO;

public final class ValidationErrorDTOs {

    private ValidationErrorDTOs() {}

    public static ValidationErrorDTO anyValidationErrorDTO() {
        return new ValidationErrorDTO() {
            @Override
            public String getJsonPath() {
                return "$.anyPath";
            }

            @Override
            public String getMessage() {
                return "anyMessage";
            }

            @Override
            public String getErrorName() {
                return "fieldIsMissing";
            }
        };
    }
}
