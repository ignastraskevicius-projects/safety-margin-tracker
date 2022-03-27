package org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.parsing;

import static java.util.Objects.requireNonNull;

import org.ignast.stockinvesting.util.errorhandling.api.dto.ValidationErrorDTO;

final class JsPathParsingValidationErrorDTO implements ValidationErrorDTO {

    private final String jsonPath;

    private final ViolationType type;

    public JsPathParsingValidationErrorDTO(final JsonPath jsonPath, final ViolationType type) {
        this.jsonPath = jsonPath.getJsonPath();
        requireNonNull(type);
        this.type = type;
    }

    @Override
    public String getJsonPath() {
        return jsonPath;
    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public String getErrorName() {
        return type.getCorrespondingErrorName();
    }
}
