package org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.postparsed;

final class JsonPath {

    private final String jsonPath;

    private JsonPath(final String jsonPath) {
        this.jsonPath = jsonPath;
    }

    @SuppressWarnings("checkstyle:multiplestringliterals")
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
