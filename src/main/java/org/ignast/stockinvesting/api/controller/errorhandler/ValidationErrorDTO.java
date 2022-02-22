package org.ignast.stockinvesting.api.controller.errorhandler;

import static java.util.Objects.requireNonNull;

public class ValidationErrorDTO {
    private String jsonPath;
    private String message;
    private ViolationType type;

    public ValidationErrorDTO(String path, String message, ViolationType type) {
        if (path == null || path.isEmpty()) {
            this.jsonPath = "$";
        } else {
            this.jsonPath = String.format("$.%s", path);
        }
        requireNonNull(type);
        if (!type.isErrorSelfExplanatory()) {
            this.message = message;
        }
        this.type = type;
    }

    public String getJsonPath() {
        return jsonPath;
    }

    public String getMessage() {
        return message;
    }

    public String getErrorName() {
        return type.getCorrespondigErrorName();
    }
}
