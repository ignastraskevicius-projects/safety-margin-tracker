package org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.parsing;

import static java.util.Objects.requireNonNull;

final class JsonPath {

    private final String jsonPath;

    private JsonPath(final String jsonPath) {
        this.jsonPath = jsonPath;
    }

    @SuppressWarnings("checkstyle:multiplestringliterals")
    public static JsonPath fromJsonPath(final String jsonPath) {
        requireNonNull(jsonPath, "JsonPath required to be non-null");
        if (jsonPath.startsWith("$.") || jsonPath.startsWith("$[") || "$".equals(jsonPath)) {
            return new JsonPath(jsonPath);
        } else {
            throw new IllegalArgumentException(
                "Invalid JsonPath provided. It should start with '$.' for property or '$[' for index or be root '$'"
            );
        }
    }

    public String getJsonPath() {
        return jsonPath;
    }
}
