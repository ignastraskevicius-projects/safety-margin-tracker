package org.ignast.stockinvesting.testutil.api;

public class BodySchemaMismatchJsonErrors {

    public static String forMissingFieldAt(String jsonPath) {
        return String.format(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"%s\"}]}",
                jsonPath);
    }

    public static String forTwoMissingFieldsAt(String jsonPath1, String jsonPath2) {
        return String.format(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"%s\"}, {\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"%s\"}]}",
                jsonPath1, jsonPath2);
    }

    public static String forStringRequiredAt(String jsonPath) {
        return String.format(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"valueMustBeString\",\"jsonPath\":\"%s\"}]}",
                jsonPath);
    }

    public static String forIntegerRequiredAt(String jsonPath) {
        return String.format(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"valueMustBeInteger\",\"jsonPath\":\"%s\"}]}",
                jsonPath);
    }

    public static String forObjectRequiredAt(String jsonPath) {
        return String.format(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"valueMustBeObject\",\"jsonPath\":\"%s\"}]}",
                jsonPath);
    }

    public static String forArrayRequiredAt(String jsonPath) {
        return String.format(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"valueMustBeArray\",\"jsonPath\":\"%s\"}]}",
                jsonPath);
    }

    public static String forInvalidValueAt(String jsonPath, String message) {
        return String.format(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"valueIsInvalid\",\"jsonPath\":\"%s\",\"message\":\"%s\"}]}",
                jsonPath, message);
    }

    public static String forInvalidValuesAt(String jsonPath1, String message1, String jsonPath2, String message2) {
        return String.format(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"valueIsInvalid\",\"jsonPath\":\"%s\",\"message\":\"%s\"},{\"errorName\":\"valueIsInvalid\",\"jsonPath\":\"%s\",\"message\":\"%s\"}]}",
                jsonPath1, message1, jsonPath2, message2);
    }
}
