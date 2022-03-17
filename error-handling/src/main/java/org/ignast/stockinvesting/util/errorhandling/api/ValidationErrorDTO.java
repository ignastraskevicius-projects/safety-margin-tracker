package org.ignast.stockinvesting.util.errorhandling.api;

import static java.util.Objects.requireNonNull;

final class JsonPath {
    private final String jsonPath;

    private JsonPath(final String jsonPath) {
        this.jsonPath = jsonPath;
    }

    public static JsonPath fromJsonPath(final String jsonPath) {
        requireNonNull(jsonPath, "JsonPath required to be non-null");
        if (jsonPath.startsWith("$.") || jsonPath.startsWith("$[") || "$".equals(jsonPath)) {
            return new JsonPath(jsonPath);
        } else {
            throw new IllegalArgumentException(
                    "Invalid JsonPath provided. It should start with '$.' for property or '$[' for index or be root '$'");
        }
    }

    public static JsonPath adaptFromJavaxValidationPath(final String javaxValidationPath) {
        if (javaxValidationPath == null || javaxValidationPath.isEmpty()) {
            return new JsonPath("$");
        } else if (javaxValidationPath.startsWith("[")) {
            return new JsonPath("$" + javaxValidationPath);
        } else {
            return new JsonPath("$." + javaxValidationPath);
        }
    }

    public String getJsonPath() {
        return jsonPath;
    }
}

public final class ValidationErrorDTO {
    private final String jsonPath;

    private final String message;

    private final ViolationType type;

    public ValidationErrorDTO(final JsonPath jsonPath, final String message, final ViolationType type) {
        this.jsonPath = jsonPath.getJsonPath();
        requireNonNull(type);
        if (!type.isErrorSelfExplanatory()) {
            this.message = message;
        } else {
            this.message = null;
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
        return type.getCorrespondingErrorName();
    }
}
