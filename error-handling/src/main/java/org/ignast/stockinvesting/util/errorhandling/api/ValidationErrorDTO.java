package org.ignast.stockinvesting.util.errorhandling.api;

import static java.util.Objects.requireNonNull;

class JsonPath {
    private String jsonPath;

    private JsonPath(String jsonPath) {
        this.jsonPath = jsonPath;
    }

    public static JsonPath fromJsonPath(String jsonPath) {
        requireNonNull(jsonPath, "JsonPath required to be non-null");
        if (jsonPath.startsWith("$.") || jsonPath.startsWith("$[") || jsonPath.equals("$")) {
            return new JsonPath(jsonPath);
        } else {
            throw new IllegalArgumentException(
                    "Invalid JsonPath provided. It should start with '$.' for property or '$[' for index or be root '$'");
        }
    }

    public static JsonPath adaptFromJavaxValidationPath(String javaxValidationPath) {
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

public class ValidationErrorDTO {
    private String jsonPath;
    private String message;
    private ViolationType type;

    public ValidationErrorDTO(JsonPath jsonPath, String message, ViolationType type) {
        this.jsonPath = jsonPath.getJsonPath();
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
