package org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.postparsed;

import static java.util.Objects.requireNonNull;

import org.ignast.stockinvesting.util.errorhandling.api.dto.ValidationErrorDTO;

final class JsPathParsingValidationErrorDTO implements ValidationErrorDTO {

    private final String jsonPath;

    private final String message;

    private final ViolationType type;

    public JsPathParsingValidationErrorDTO(
        final JsonPath jsonPath,
        final String message,
        final ViolationType type
    ) {
        this.jsonPath = jsonPath.getJsonPath();
        requireNonNull(type);
        if (!type.isErrorSelfExplanatory()) {
            this.message = message;
        } else {
            this.message = null;
        }
        this.type = type;
    }

    @Override
    public String getJsonPath() {
        return jsonPath;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getErrorName() {
        return type.getCorrespondingErrorName();
    }
}
